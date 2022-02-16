package com.myszon.processor;

import com.myszon.api.GithubApiClient;
import com.myszon.api.responses.Blob;
import com.myszon.api.responses.Commit;
import com.myszon.api.responses.Tree;
import com.myszon.api.responses.TreeType;
import com.myszon.config.ElasticsearchProperties;
import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.repository.IIndexIngest;
import com.myszon.repository.IIndexManager;
import com.myszon.util.IpAddressProcessorHelper;
import io.micronaut.context.annotation.Requires;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.myszon.util.IpAddressProcessorHelper.ipToLong;
import static com.myszon.util.IpAddressProcessorHelper.longToIP;

@Requires(beans = ElasticsearchProperties.class)
@Singleton
public class IpAddressProcessor implements IndexProcessor{
    private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressProcessor.class);
    private final IIndexIngest indexIngest;
    private final IIndexManager indexManager;
    private final GithubApiClient githubApiClient;
    private final int maxBulkSize;

    public IpAddressProcessor(IIndexIngest indexIngest,
                              IIndexManager indexManager,
                              GithubApiClient githubApiClient,
                              ElasticsearchProperties elasticsearchProperties) {
        this.indexIngest = indexIngest;
        this.indexManager = indexManager;
        this.githubApiClient = githubApiClient;
        this.maxBulkSize = elasticsearchProperties.getMaxBulkSize();
    }

    @PostConstruct
    public void setup() throws IOException {
        this.ipAddressesInitialSetup();
    }

    @Override
    public Alias getAlias() {
        return Alias.IP_ADDRESS;
    }


    @Override
    public int refreshIndex() throws Exception {
        Index toIndex = getIndexForWriting();
        LOGGER.info(String.format("Start Refreshing Index %s for Alias %s ", toIndex, getAlias()));


        Commit commit = githubApiClient.getCommits().get(0);
        Tree rootTree = githubApiClient.getTreeBySha(commit.getCommit().getTree().getSha());
        rootTree.setType(TreeType.TREE.toString());

        LOGGER.info(String.format("Recreating index %s ", toIndex));
        this.indexManager.recreateIndex(toIndex);

        Queue<Tree> pageQueue = new LinkedList<>();
        pageQueue.add(rootTree);

        int docCount = 0;

        while (!pageQueue.isEmpty()) {
            Tree node = pageQueue.poll();
            if (node.getType() == TreeType.BLOB) {
                Blob blob = this.githubApiClient.getBlobBySha(node.getSha());
                docCount += processBlobContent(blob, toIndex);
            } else {
                node = githubApiClient.getTreeBySha(node.getSha());
                pageQueue.addAll(node.getTree().stream()
                        .filter(IpAddressProcessorHelper::shouldIgnoreBlobOrTree)
                        .collect(Collectors.toList()));
            }
        }

        Index fromIndex = toIndex == Index.IP_ADDRESS_V1 ? Index.IP_ADDRESS_V2 : Index.IP_ADDRESS_V1;
        this.indexManager.swapIndexAlias(fromIndex, toIndex, Alias.IP_ADDRESS);

        LOGGER.info(String.format("Index %s is refreshed and switched to read index", toIndex));
        return docCount;
    }


    private int processBlobContent(Blob blob, Index index) throws Exception {
        int ipCount = 0;
        byte[] bytes = Base64.getMimeDecoder().decode(blob.getContent());
        String page = new String(bytes, StandardCharsets.UTF_8);
        String[] allLines = page.trim().split("\n");

        List<IpAddress> ipAddresses = new ArrayList<>();
        for (String line : allLines) {
            if (line.charAt(0) == '#') continue;

            String[] ipAddressAndMaskPair = IpAddressProcessorHelper.getIpAddressAndOrMask(line);
            String ipAddress = IpAddressProcessorHelper.getIpAddressAndOrMask(line)[0];
            String mask = ipAddressAndMaskPair[1];

            if (IpAddressProcessorHelper.isValidIPAddress(ipAddressAndMaskPair[1])) {
                String errMsg = String.format("'%s' is not an IP string literal.", line);
                LOGGER.error(errMsg);
                throw new Exception(errMsg);
            }

            if (IpAddressProcessorHelper.isValidMask(mask)) {
                ipCount += insertIpAddressesToIndex(ipAddresses, index);
                ipAddresses = new ArrayList<>();

                ipCount += this.saveIpRange(ipAddress, Integer.parseInt(mask), index);
            } else {
                ipAddresses.add(new IpAddress(line));
            }
        }

        return insertIpAddressesToIndex(ipAddresses, index) + ipCount;
    }

    private int saveIpRange(String ipAddress, int mask, Index index) throws IOException {
        int ipCount = 0;
        LOGGER.debug(String.format("Processing range for ip %s and mask %s", ipAddress, mask));

        int totalIpAddresses = (int) Math.pow(2, 32 - mask);
        List<IpAddress> ipAddresses = new ArrayList<>();
        ipAddresses.add(new IpAddress(ipAddress));

        String LAST_IP_ADDRESS = "255.255.255.255";
        long start = ipToLong(ipAddress);
        long end = ipToLong(LAST_IP_ADDRESS);

        for (int i = 0; i < totalIpAddresses && start <= end; i++) {
            if (ipAddresses.size() + 1 > this.maxBulkSize) {
                LOGGER.debug("MAX_BULK_SIZE reached in saveIpRange function. Saving to db");
                ipCount += insertIpAddressesToIndex(ipAddresses, index);
                ipAddresses = new ArrayList<>();
            }
            start += 1;
            ipAddresses.add(new IpAddress(longToIP(start)));
        }

        return insertIpAddressesToIndex(ipAddresses, index) + ipCount;
    }

    private Index getIndexForWriting() throws Exception {
        Set<Index> indices = this.indexManager.getIndexByAlias(Alias.IP_ADDRESS);
        if (indices.size() > 1) {
            throw new Exception(String.format("Alias %s is pointing to moe then one index", Alias.IP_ADDRESS));
        }
        return indices.contains(Index.IP_ADDRESS_V1) ? Index.IP_ADDRESS_V2 : Index.IP_ADDRESS_V1;
    }

    private int insertIpAddressesToIndex(List<IpAddress> ipAddresses, Index index) throws IOException {
        int ipCount = ipAddresses.size();
        this.indexIngest.insertIPAddressesToIndex(ipAddresses, index);
        return ipCount;
    }

    private void ipAddressesInitialSetup() throws IOException {
        if (!this.indexManager.doesIndexExists(Index.IP_ADDRESS_V1)) {
            this.indexManager.createIndex(Index.IP_ADDRESS_V1);
        }

        if (!this.indexManager.doesIndexExists(Index.IP_ADDRESS_V2)) {
            this.indexManager.createIndex(Index.IP_ADDRESS_V2);
        }

        if (!this.indexManager.doesAliasExists(Alias.IP_ADDRESS)) {
            this.indexManager.createAliasForIndex(Alias.IP_ADDRESS, Index.IP_ADDRESS_V1);
        }
    }
}
