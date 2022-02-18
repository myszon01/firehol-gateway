package com.myszon.processor.manager;

import com.myszon.api.GithubApiClient;
import com.myszon.api.responses.Blob;
import com.myszon.api.responses.Commit;
import com.myszon.api.responses.Tree;
import com.myszon.api.responses.TreeType;
import com.myszon.config.ElasticsearchProperties;
import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.processor.worker.IpAddressWorker;
import com.myszon.repository.IIndexIngest;
import com.myszon.repository.IIndexManager;
import com.myszon.util.IpAddressProcessorHelper;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Requires(beans = ElasticsearchProperties.class)
@Singleton
public class IpAddressManager implements IndexManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressManager.class);
    private final IIndexManager indexManager;
    private final GithubApiClient githubApiClient;
    private final ApplicationEventPublisher<IpAddressWorker.BlobEvent> eventPublisher;

    public IpAddressManager(IIndexManager indexManager,
                            GithubApiClient githubApiClient,
                            ApplicationEventPublisher<IpAddressWorker.BlobEvent> eventPublisher) {
        this.indexManager = indexManager;
        this.githubApiClient = githubApiClient;
        this.eventPublisher = eventPublisher;
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
        Index fromIpAddressIndex;
        Index toNewIpAddressIndex;
        Index fromIpAddressRangeIndex;
        Index toNewIpAddressRangeIndex;
        if (Index.IP_ADDRESS_V1 == getIndexBehindAlias(Alias.IP_ADDRESS) ) {
            fromIpAddressIndex = Index.IP_ADDRESS_V1;
            toNewIpAddressIndex = Index.IP_ADDRESS_V2;
        } else {
            fromIpAddressIndex = Index.IP_ADDRESS_V2;
            toNewIpAddressIndex = Index.IP_ADDRESS_V1;
        }

        if (Index.IP_ADDRESS_RANGE_V1 == getIndexBehindAlias(Alias.IP_ADDRESS_RANGE) ) {
            fromIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V1;
            toNewIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V2;
        } else {
            fromIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V2;
            toNewIpAddressRangeIndex = Index.IP_ADDRESS_RANGE_V1;
        }

        LOGGER.debug(String.format("Start Refreshing Index %s for Alias %s ", toNewIpAddressIndex, Alias.IP_ADDRESS));
        LOGGER.debug(String.format("Start Refreshing Index %s for Alias %s ", toNewIpAddressRangeIndex, Alias.IP_ADDRESS_RANGE));

        Commit commit = githubApiClient.getCommits().get(0);
        Tree rootTree = githubApiClient.getTreeBySha(commit.getCommit().getTree().getSha());
        rootTree.setType(TreeType.TREE.toString());

        LOGGER.debug(String.format("Recreating index %s ", toNewIpAddressIndex));
        this.indexManager.recreateIndex(toNewIpAddressIndex, this.getIPAddressMapping());
        LOGGER.debug(String.format("Recreating index %s ", toNewIpAddressRangeIndex));
        this.indexManager.recreateIndex(toNewIpAddressRangeIndex, this.getIPAddressRangeMapping());

        Queue<Tree> pageQueue = new LinkedList<>();
        pageQueue.add(rootTree);

        int docCount = 0;

//        List< Future<?>> workers = new ArrayList<>();
        List<CompletableFuture<Void>> workers = new ArrayList<>();
        while (!pageQueue.isEmpty()) {
            Tree node = pageQueue.poll();
            if (node == null) continue;

            LOGGER.debug(String.format("pooling from queue. size is %s", pageQueue.size()));
            if (node.getType() == TreeType.BLOB) {
                LOGGER.debug(String.format("File %s is blob. Scheduling tasks", node.getPath()));
                Mono<Blob> blob = this.githubApiClient.getBlobBySha(node.getSha());
                Future<Void> worker = this.eventPublisher.publishEventAsync(
                        new IpAddressWorker.BlobEvent(blob, toNewIpAddressIndex, toNewIpAddressRangeIndex, node.getPath())
                );
                workers.add((CompletableFuture<Void>) worker);
            } else {
                LOGGER.debug(String.format("File %s is tree. calling github", node.getPath()));
                node = this.githubApiClient.getTreeBySha(node.getSha());
                pageQueue.addAll(node.getTree().stream()
                        .filter(IpAddressProcessorHelper::shouldIgnoreBlobOrTree)
                        .collect(Collectors.toList()));
            }
            LOGGER.debug(String.format("done processing element %s", node.getPath()));
        }

        CompletableFuture<?> completed = allOf(workers);

        while (true) if (completed.isDone()) break;

        this.indexManager.swapIndexAlias(fromIpAddressIndex, toNewIpAddressIndex, Alias.IP_ADDRESS);
        this.indexManager.swapIndexAlias(fromIpAddressRangeIndex, toNewIpAddressRangeIndex, Alias.IP_ADDRESS_RANGE);
        LOGGER.debug(String.format("Index %s is refreshed and switched to read index", toNewIpAddressRangeIndex));

        return docCount;
    }

    public <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult =
                CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
        return allFuturesResult.thenApply(v ->
                futuresList.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
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
        return  mapping;
    }

    private Map<String, Object> getIPAddressRangeMapping() {
        Map<String, String> ip_addr = new HashMap<>();
        ip_addr.put("type", "ip_range");

        Map<String, Object> properties = new HashMap<>();
        properties.put("ipAddress", ip_addr);

        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        return  mapping;
    }
}
