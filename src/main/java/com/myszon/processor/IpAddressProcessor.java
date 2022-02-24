package com.myszon.processor;

import com.myszon.api.GithubApiClient;
import com.myszon.api.responses.Blob;
import com.myszon.api.responses.Commit;
import com.myszon.api.responses.Tree;
import com.myszon.api.responses.TreeType;
import com.myszon.config.ElasticsearchProperties;
import com.myszon.controller.projection.IngestResponse;
import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.processor.queue.DLQ;
import com.myszon.repository.IIndexIngest;
import com.myszon.repository.IIndexManager;
import com.myszon.util.IpAddressProcessorHelper;
import io.micronaut.context.annotation.Requires;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.myszon.util.IpAddressProcessorHelper.isValidMask;

@Requires(beans = ElasticsearchProperties.class)
@Singleton
public class IpAddressProcessor implements IndexProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressProcessor.class);
    private final IIndexManager indexManager;
    private final GithubApiClient githubApiClient;
    private final IIndexIngest indexIngest;

    public IpAddressProcessor(IIndexManager indexManager,
                              IIndexIngest indexIngest,
                              GithubApiClient githubApiClient) {
        this.indexManager = indexManager;
        this.githubApiClient = githubApiClient;
        this.indexIngest = indexIngest;
    }

    @PostConstruct
    public void setup() throws IOException {
        this.ipAddressesInitialSetup();
    }

    @Override
    public Set<Alias> getAliases() {
        // This processor support two aliases to satisfy range query
        return Set.of(Alias.IP_ADDRESS, Alias.IP_ADDRESS_RANGE);
    }


    @Override
    public IngestResponse refreshIndex() throws Exception {
        Index[] fromAndToIpAddressIndex = this.getFromAndToIpAddressIndex();
        Index fromIpAddressIndex = fromAndToIpAddressIndex[0];
        Index toNewIpAddressIndex = fromAndToIpAddressIndex[1];

        Index[] fromAndToIpAddressRangeIndex = this.getFromAndToIpAddressRangeIndex();
        Index fromIpAddressRangeIndex = fromAndToIpAddressRangeIndex[0];
        Index toNewIpAddressRangeIndex = fromAndToIpAddressRangeIndex[1];

        LOGGER.info(String.format("Start Refreshing Index %s for Alias %s ",
                toNewIpAddressIndex, Alias.IP_ADDRESS));
        LOGGER.info(String.format("Start Refreshing Index %s for Alias %s ",
                toNewIpAddressRangeIndex, Alias.IP_ADDRESS_RANGE));

        Commit commit = githubApiClient.getCommits().get(0);
        Tree rootTree = githubApiClient.getTreeBySha(commit.getCommit().getTree().getSha());
        rootTree.setType(TreeType.TREE.toString());

        LOGGER.info(String.format("Recreating index %s ", toNewIpAddressIndex));
        this.indexManager.recreateIndex(toNewIpAddressIndex, this.getIPAddressMapping());

        LOGGER.info(String.format("Recreating index %s ", toNewIpAddressRangeIndex));
        this.indexManager.recreateIndex(toNewIpAddressRangeIndex, this.getIPAddressRangeMapping());

        LOGGER.info("Transform github tree structure to flat list");
        Set<Tree> flatten = new HashSet<>();
        Queue<Tree> pageQueue = new LinkedList<>();
        pageQueue.add(rootTree);
        while (!pageQueue.isEmpty()) {
            Tree node = pageQueue.poll();
            if (node == null) continue;

            LOGGER.debug(String.format("pooling from queue. size is %s", pageQueue.size()));
            if (node.getType() == TreeType.BLOB) {
                LOGGER.debug(String.format("File %s is blob. Scheduling tasks", node.getPath()));

                if (node.getSha() == null) continue;
                flatten.add(node);
            } else {
                LOGGER.debug(String.format("File %s is tree. calling github", node.getPath()));
                node = this.githubApiClient.getTreeBySha(node.getSha());
                pageQueue.addAll(node.getTree().stream()
                        .filter(IpAddressProcessorHelper::shouldIgnoreBlobOrTree)
                        .collect(Collectors.toList()));
            }
        }

        LOGGER.info(String.format("Start processing each blob from github. Current nr of blobs %s", flatten.size()));

        AtomicInteger processedFilesCounter = new AtomicInteger(0);
        AtomicInteger failedFilesCounter = new AtomicInteger(0);
        AtomicLong insertedIpAddressesAndRange = new AtomicLong(0);

        Blob subscribe = Flux.fromIterable(flatten)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(node -> Mono.defer(() -> this.githubApiClient.getBlobBySha(node.getSha()))
                        // TODO refactor to continue only on connection errors. Change loggers to debug
                        .retryWhen(
                                Retry.backoff(10, Duration.ofMillis(10000))
                                        .doAfterRetry(retrySignal -> LOGGER.info("Retried " + retrySignal.totalRetries()))
                                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal)
                                                -> retrySignal.failure()))
                        .onErrorResume(Exception.class, (msg) -> {
                            // TODO refactor to be in shared queue no static
                            DLQ.addTree(new DLQ.DLQ_MESSAGE(node, toNewIpAddressIndex, toNewIpAddressRangeIndex));
                            LOGGER.info(String.format("Error when processing doc. Total failed docs %s ",
                                    failedFilesCounter.incrementAndGet()));
                            msg.printStackTrace();
                            return Mono.empty();
                        })
                        .flatMap(blob -> {
                            try {
                                long ips = processBlob(blob, node.getPath(), toNewIpAddressIndex,
                                        toNewIpAddressRangeIndex);
                                LOGGER.info(String.format("Blob processed. Current processed doc count " +
                                                "is %s and inserted ips %s ", processedFilesCounter.incrementAndGet(),
                                        insertedIpAddressesAndRange.addAndGet(ips)));
                                return Mono.just(blob);
                            } catch (Exception e) {
                                // TODO refactor to be in shared queue no static
                                DLQ.addTree(new DLQ.DLQ_MESSAGE(node, toNewIpAddressIndex, toNewIpAddressRangeIndex));
                                e.printStackTrace();
                                return Mono.error(e);
                            }
                        })).blockLast();


        System.out.println(subscribe);

        this.indexManager.swapIndexAlias(fromIpAddressIndex, toNewIpAddressIndex, Alias.IP_ADDRESS);
        this.indexManager.swapIndexAlias(fromIpAddressRangeIndex, toNewIpAddressRangeIndex, Alias.IP_ADDRESS_RANGE);

        LOGGER.info(String.format("Indices %s %s are fresh and switched to aliases %s %s",
                toNewIpAddressIndex, toNewIpAddressRangeIndex, Alias.IP_ADDRESS, Alias.IP_ADDRESS_RANGE));

        return IngestResponse.builder()
                .processedFiles(processedFilesCounter.get())
                .insertedIpAddressesAndRange(insertedIpAddressesAndRange.get())
                .failedFiles(failedFilesCounter.get())
                .totalFiles(flatten.size())
                .build();
    }

    // TODO refactor all pjos to builder
    private long processBlob(Blob blob, String path, Index ipAddress, Index ipAddressRange) throws Exception {

        LOGGER.debug(String.format("Processing blob with %s ", path));
        InputStream is = new ByteArrayInputStream(Base64.getMimeDecoder().decode(blob.getContent()));
        String page = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String[] allLines = page.trim().split("\n");

        List<IpAddress> ipAddresses = new ArrayList<>();
        List<IpAddress> ipAddressesRange = new ArrayList<>();

        for (String line : allLines) {
            if (line.charAt(0) == '#') continue;

            String[] ipAddressAndMaskPair = IpAddressProcessorHelper.getIpAddressAndOrMask(line);
            String mask = ipAddressAndMaskPair[1];

            if (isValidMask(mask)) {
                ipAddressesRange.add(new IpAddress(line, path, blob.getSha()));
            } else {
                ipAddresses.add(new IpAddress(line, path, blob.getSha()));
            }

        }

        LOGGER.debug("Done executing processIP function. Saving to db");
        this.indexIngest.insertIPAddressesToIndex(ipAddressesRange, ipAddressRange);
        this.indexIngest.insertIPAddressesToIndex(ipAddresses, ipAddress);
        return ipAddresses.size() + ipAddressesRange.size();
    }

    private Index[] getFromAndToIpAddressIndex() throws Exception {
        Index fromIpAddressIndex;
        Index toNewIpAddressIndex;
        if (Index.IP_ADDRESS_V1 == this.getIndexBehindAlias(Alias.IP_ADDRESS)) {
            fromIpAddressIndex = Index.IP_ADDRESS_V1;
            toNewIpAddressIndex = Index.IP_ADDRESS_V2;
        } else {
            fromIpAddressIndex = Index.IP_ADDRESS_V2;
            toNewIpAddressIndex = Index.IP_ADDRESS_V1;
        }
        return new Index[]{fromIpAddressIndex, toNewIpAddressIndex};
    }

    private Index[] getFromAndToIpAddressRangeIndex() throws Exception {
        Index fromIpAddressRangeIndex;
        Index toNewIpAddressRangeIndex;
        if (Index.IP_ADDRESS_RANGE_V1 == this.getIndexBehindAlias(Alias.IP_ADDRESS_RANGE)) {
            fromIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V1;
            toNewIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V2;
        } else {
            fromIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V2;
            toNewIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V1;
        }
        return new Index[]{fromIpAddressRangeIndex, toNewIpAddressRangeIndex};
    }

    private Index getIndexBehindAlias(Alias alias) throws Exception {
        List<Index> indices = this.indexManager.getIndexByAlias(alias);
        if (indices.size() > 1) {
            throw new Exception(String.format("Alias %s is pointing to moe then one index", alias));
        }
        return indices.get(0);
    }

    private void ipAddressesInitialSetup() throws IOException {

        if (!this.indexManager.doesIndexExists(Index.IP_ADDRESS_V1)) {
            this.indexManager.createIndex(Index.IP_ADDRESS_V1, this.getIPAddressMapping());
        }

        if (!this.indexManager.doesIndexExists(Index.IP_ADDRESS_V2)) {
            this.indexManager.createIndex(Index.IP_ADDRESS_V2, this.getIPAddressMapping());
        }

        if (!this.indexManager.doesIndexExists(Index.IP_ADDRESS_RANGE_V1)) {
            this.indexManager.createIndex(Index.IP_ADDRESS_RANGE_V1, this.getIPAddressRangeMapping());
        }

        if (!this.indexManager.doesIndexExists(Index.IP_ADDRESS_RANGE_V2)) {
            this.indexManager.createIndex(Index.IP_ADDRESS_RANGE_V2, this.getIPAddressRangeMapping());
        }

        if (!this.indexManager.doesAliasExists(Alias.IP_ADDRESS)) {
            this.indexManager.createAliasForIndex(Alias.IP_ADDRESS, Index.IP_ADDRESS_V1);
        }

        if (!this.indexManager.doesAliasExists(Alias.IP_ADDRESS_RANGE)) {
            this.indexManager.createAliasForIndex(Alias.IP_ADDRESS_RANGE, Index.IP_ADDRESS_RANGE_V1);
        }
    }

    private Map<String, Object> getIPAddressMapping() {
        Map<String, String> ip_addr = new HashMap<>();
        ip_addr.put("type", "ip");

        Map<String, Object> properties = new HashMap<>();
        properties.put("ipAddress", ip_addr);

        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        return mapping;
    }

    private Map<String, Object> getIPAddressRangeMapping() {
        Map<String, String> ip_addr = new HashMap<>();
        ip_addr.put("type", "ip_range");

        Map<String, Object> properties = new HashMap<>();
        properties.put("ipAddress", ip_addr);

        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        return mapping;
    }
}
