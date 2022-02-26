package com.myszon.processor;

import com.myszon.api.GithubApiClient;
import com.myszon.api.responses.*;
import com.myszon.controller.projection.IngestResponse;
import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.repository.IIndexIngest;
import com.myszon.repository.IIndexManager;
import com.myszon.util.QueueWrapper;
import io.micronaut.http.client.exceptions.ReadTimeoutException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MicronautTest
class IpAddressProcessorTest {

    @Inject
    IIndexManager indexManager;

    @Inject
    IIndexIngest iIndexIngest;

    @Inject
    GithubApiClient githubApiClient;

    @Inject
    QueueWrapper queueWrapper;

    @Inject
    IpAddressProcessor processor;

    private Tree rootNode, node, leaf1, leaf2;
    private Commit commit;
    private Blob blob;

    @BeforeEach
    public void reset() throws IOException {
        rootNode = new Tree();
        rootNode.setSha("rootSha");
        rootNode.setType(TreeType.TREE.toString());

        leaf1 = new Tree();
        leaf1.setType(TreeType.BLOB.toString());
        leaf1.setPath("leaf1_path.ipset");
        leaf1.setSha("leaf1_sha");

        leaf2 = new Tree();
        leaf2.setType(TreeType.BLOB.toString());
        leaf2.setPath("leaf2_path.netset");
        leaf2.setSha("leaf2_sha");

        node = new Tree();
        node.setType(TreeType.TREE.toString());
        node.setPath("node_path");
        node.setSha("node_sha");
        node.setTree(List.of(leaf2));

        rootNode.setTree(List.of(node, leaf1));
        CommitHeader commitHeader = new CommitHeader();
        commitHeader.setTree(rootNode);
        commit = new Commit();
        commit.setCommit(commitHeader);

        blob = new Blob();
        /*
         * # Processed with FireHOL's iprange
         * #
         * 198.54.117.197
         * 198.54.117.199/12
         */
        blob.setContent("IyBQcm9jZXNzZWQgd2l0aCBGaXJlSE9MJ3MgaXByYW5nZSAKIyAKMTk4LjU0LjExNy4xOTcgCiAxOTguNTQuMTE3LjE5OS8xMg==");

    }

    @Test
    public void getAliases_shouldReturn_IP_ADDRESS_and_IP_ADDRESS_RANGE() {
        Set<Alias> expected = Set.of(Alias.IP_ADDRESS, Alias.IP_ADDRESS_RANGE);

        Set<Alias> actual = this.processor.getAliases();

        assertEquals(expected, actual);
    }

    @Test
    public void refreshIndex_shouldRefreshIndexV2_whenIndexV1isInUse() throws Exception {
        // Arrange
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS))).thenReturn(List.of(Index.IP_ADDRESS_V1));
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS_RANGE))).thenReturn(List.of(Index.IP_ADDRESS_RANGE_V1));
        when(githubApiClient.getCommits()).thenReturn(List.of(commit));
        when(githubApiClient.getTreeBySha(rootNode.getSha())).thenReturn(rootNode);
        when(githubApiClient.getTreeBySha(node.getSha())).thenReturn(node);
        when(githubApiClient.getBlobBySha(leaf1.getSha()))
                .thenReturn(Mono.just(blob));
        when(githubApiClient.getBlobBySha(leaf2.getSha()))
                .thenReturn(Mono.just(blob));

        // Act
        IngestResponse actual = this.processor.refreshIndex();

        // Assert
        verify(indexManager).swapIndexAlias(Index.IP_ADDRESS_V1, Index.IP_ADDRESS_V2, Alias.IP_ADDRESS);
        verify(indexManager).swapIndexAlias(Index.IP_ADDRESS_RANGE_V1, Index.IP_ADDRESS_RANGE_V2, Alias.IP_ADDRESS_RANGE);
        assertNotNull(actual);
    }

    @Test
    public void refreshIndex_shouldRefreshIndexV1_whenIndexV2isInUse() throws Exception {
        // Arrange
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS))).thenReturn(List.of(Index.IP_ADDRESS_V2));
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS_RANGE))).thenReturn(List.of(Index.IP_ADDRESS_RANGE_V2));
        when(githubApiClient.getCommits()).thenReturn(List.of(commit));
        when(githubApiClient.getTreeBySha(rootNode.getSha())).thenReturn(rootNode);
        when(githubApiClient.getTreeBySha(node.getSha())).thenReturn(node);
        when(githubApiClient.getBlobBySha(leaf1.getSha()))
                .thenReturn(Mono.just(blob));
        when(githubApiClient.getBlobBySha(leaf2.getSha()))
                .thenReturn(Mono.just(blob));

        // Act
        IngestResponse actual = this.processor.refreshIndex();

        // Assert
        verify(indexManager).swapIndexAlias(Index.IP_ADDRESS_V2, Index.IP_ADDRESS_V1, Alias.IP_ADDRESS);
        verify(indexManager).swapIndexAlias(Index.IP_ADDRESS_RANGE_V2, Index.IP_ADDRESS_RANGE_V1, Alias.IP_ADDRESS_RANGE);
        assertNotNull(actual);
    }

    @Test
    public void refreshIndex_shouldThrowError_whenErrorShouldntBeIgnored() throws Exception {
        // Arrange
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS))).thenReturn(List.of(Index.IP_ADDRESS_V2));
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS_RANGE))).thenReturn(List.of(Index.IP_ADDRESS_RANGE_V2));
        when(githubApiClient.getCommits()).thenReturn(List.of(commit));
        when(githubApiClient.getTreeBySha(rootNode.getSha())).thenReturn(rootNode);
        when(githubApiClient.getTreeBySha(node.getSha())).thenReturn(node);
        when(githubApiClient.getBlobBySha(leaf1.getSha())).thenReturn(Mono.error(new Exception("error")));

        // Act //Assert
        assertThrows(Exception.class, () -> this.processor.refreshIndex());
    }

    @Test
    public void refreshIndex_shouldIgnoreErrorAndAddToNodeToQueue_whenErrorShouldBeIgnored() throws Exception {
        // Arrange
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS))).thenReturn(List.of(Index.IP_ADDRESS_V2));
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS_RANGE))).thenReturn(List.of(Index.IP_ADDRESS_RANGE_V2));
        when(githubApiClient.getCommits()).thenReturn(List.of(commit));
        when(githubApiClient.getTreeBySha(rootNode.getSha())).thenReturn(rootNode);
        when(githubApiClient.getTreeBySha(node.getSha())).thenReturn(node);
        when(githubApiClient.getBlobBySha(leaf1.getSha()))
                .thenReturn(Mono.just(blob));
        when(githubApiClient.getBlobBySha(leaf2.getSha()))
                .thenReturn(Mono.error(ReadTimeoutException.TIMEOUT_EXCEPTION));

        // Act
        assertThrows(Exception.class, () -> this.processor.refreshIndex());

        // Assert
        verify(queueWrapper).put(leaf2);
    }

    @Test
    public void refreshIndex_shouldThrowError_whenFailedFilesAreOverThreshold() throws Exception {
        // Arrange
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS))).thenReturn(List.of(Index.IP_ADDRESS_V2));
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS_RANGE))).thenReturn(List.of(Index.IP_ADDRESS_RANGE_V2));
        when(githubApiClient.getCommits()).thenReturn(List.of(commit));
        when(githubApiClient.getTreeBySha(rootNode.getSha())).thenReturn(rootNode);
        when(githubApiClient.getTreeBySha(node.getSha())).thenReturn(node);
        when(githubApiClient.getBlobBySha(leaf1.getSha()))
                .thenReturn(Mono.just(blob));
        when(githubApiClient.getBlobBySha(leaf2.getSha()))
                .thenReturn(Mono.error(ReadTimeoutException.TIMEOUT_EXCEPTION));

        // Act // Assert
        assertThrows(Exception.class, () -> this.processor.refreshIndex());

    }


    @Test
    public void refreshIndex_shouldAddIpAddressAndRange_whenFoundInBlob() throws Exception {
        // Arrange
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS))).thenReturn(List.of(Index.IP_ADDRESS_V2));
        when(indexManager.getIndexByAlias(eq(Alias.IP_ADDRESS_RANGE))).thenReturn(List.of(Index.IP_ADDRESS_RANGE_V2));
        when(githubApiClient.getCommits()).thenReturn(List.of(commit));
        when(githubApiClient.getTreeBySha(rootNode.getSha())).thenReturn(rootNode);
        when(githubApiClient.getTreeBySha(node.getSha())).thenReturn(node);
        when(githubApiClient.getBlobBySha(leaf1.getSha()))
                .thenReturn(Mono.just(blob));
        when(githubApiClient.getBlobBySha(leaf2.getSha()))
                .thenReturn(Mono.just(blob));
        IngestResponse expected = IngestResponse.builder()
                .totalFiles(2)
                .failedFiles(0)
                .processedFiles(2)
                .insertedIpAddressesAndRange(4)
                .build();
        List<IpAddress> expectedIpRange = List.of(IpAddress.builder()
                        .path("leaf2_path.netset")
                        .ipAddress("198.54.117.199/12")
                .build());
        List<IpAddress> expectedIpAddress = List.of(IpAddress.builder()
                .ipAddress("198.54.117.197")
                .path("leaf2_path.netset")
                .build());

        // Act
        IngestResponse actual = this.processor.refreshIndex();

        // Assert
        verify(iIndexIngest, times(2))
                .insertIPAddressesToIndex(
                        argThat(ipAddresses -> {
                            return  ipAddresses.containsAll(expectedIpAddress) || ipAddresses.containsAll(expectedIpRange);
                        }),
                        argThat(index -> index == Index.IP_ADDRESS_V1 || index == Index.IP_ADDRESS_RANGE_V1));

        assertEquals(expected, actual);

    }



    @MockBean(IIndexManager.class)
    IIndexManager indexManager() {
        return mock(IIndexManager.class);
    }

    @MockBean(GithubApiClient.class)
    GithubApiClient githubApiClient() {
        return mock(GithubApiClient.class);
    }

    @MockBean(IIndexIngest.class)
    IIndexIngest iIndexIngest() {
        return mock(IIndexIngest.class);
    }

    @MockBean(QueueWrapper.class)
    QueueWrapper queueWrapper() {
        return mock(QueueWrapper.class);
    }

}