package com.myszon.service;

import com.myszon.api.GithubApiClient;
import com.myszon.api.responses.Blob;
import com.myszon.api.responses.Commit;
import com.myszon.api.responses.Tree;
import com.myszon.api.responses.TreeType;
import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.repository.IIndexIngest;
import com.myszon.repository.IIndexManager;
import com.myszon.util.GitHubProcessingHelper;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class IngestService {

    private final IIndexIngest indexIngest;
    private final IIndexManager indexManager;
    private final GithubApiClient githubApiClient;

    public IngestService(IIndexIngest indexIngest,
                         IIndexManager indexManager,
                         GithubApiClient githubApiClient) {
        this.indexIngest = indexIngest;
        this.indexManager = indexManager;
        this.githubApiClient = githubApiClient;
    }

    @PostConstruct
    public void setup() throws IOException {
        this.ipAddressesInitialSetup();
    }

    public int startIngestion(Alias alias) throws IOException {
        if (Alias.IP_ADDRESS == alias) return this.ipAddressesIngestFromFirehol();

        throw new IllegalArgumentException("Alias doesn't exists");
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

    private int ipAddressesIngestFromFirehol() throws IOException {
        Index toIndex = getIndexForWriting();

        Commit commit = githubApiClient.getCommits().get(0);
        Tree rootTree = githubApiClient.getTreeBySha(commit.getCommit().getTree().getSha());
        rootTree.setType(TreeType.TREE.toString());

        Queue<Tree> queue = new LinkedList<>();
        queue.add(rootTree);

        int docCount = 0;
        while (!queue.isEmpty()) {
            Tree node = queue.poll();
            if (node.getType() == TreeType.BLOB) {
                Blob blob = this.githubApiClient.getBlobBySha(node.getSha());
                List<IpAddress> ipAddresses = GitHubProcessingHelper.getIpAddressFromBlob(blob);
                if (ipAddresses.isEmpty()) continue;
                indexIngest.insertIPAddressesToIndex(ipAddresses, toIndex);
                docCount += ipAddresses.size();
            } else {
                node = githubApiClient.getTreeBySha(node.getSha());
                queue.addAll(node.getTree().stream()
                        .filter(GitHubProcessingHelper::shouldIgnoreBlobOrTree)
                        .collect(Collectors.toList()));
            }
        }

        Index fromIndex = toIndex == Index.IP_ADDRESS_V1 ? Index.IP_ADDRESS_V2 : Index.IP_ADDRESS_V1;
        this.indexManager.swapIndexAlias(fromIndex, toIndex, Alias.IP_ADDRESS);

        return docCount;
    }

    private Index getIndexForWriting() throws IOException {
        Set<Index> indices = this.indexManager.getIndexByAlias(Alias.IP_ADDRESS);
        if (indices.size() > 1) {
            throw new RuntimeException(String.format("Alias %s is pointing to moe then one index", Alias.IP_ADDRESS));
        }
        return indices.contains(Index.IP_ADDRESS_V1) ? Index.IP_ADDRESS_V2 : Index.IP_ADDRESS_V1;
    }
}
