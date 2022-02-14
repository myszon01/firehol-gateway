package com.myszon.repository.impl;

import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.repository.IIndexManager;
import jakarta.inject.Singleton;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.opensearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.cluster.metadata.AliasMetadata;
import org.opensearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

@Singleton
public class IndexManager implements IIndexManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexManager.class);


    private final RestHighLevelClient openSearchClient;

    public IndexManager(RestHighLevelClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }


    @Override
    public boolean createIndex(Index index) throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index.toString());

        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 2)
        );
        CreateIndexResponse createIndexResponse = this.openSearchClient.indices()
                .create(createIndexRequest, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    @Override
    public boolean swapIndexAlias(Index fromIndex, Index toIndex, Alias alias) throws IOException {
        IndicesAliasesRequest.AliasActions removeIndex =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest
                        .AliasActions.Type.REMOVE);
        removeIndex.alias(alias.toString());
        removeIndex.index(fromIndex.toString());

        IndicesAliasesRequest.AliasActions addIndex =
                new IndicesAliasesRequest
                        .AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD);
        addIndex.alias(alias.toString());
        addIndex.index(toIndex.toString());

        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();
        aliasesRequest.addAliasAction(removeIndex);
        aliasesRequest.addAliasAction(addIndex);

        try {
            AcknowledgedResponse response = this.openSearchClient.indices()
                    .updateAliases(aliasesRequest, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            throw new IOException(ex);
        }
    }

    @Override
    public boolean createAliasForIndex(Alias alias, Index index) throws IOException {

        IndicesAliasesRequest.AliasActions aliasActions =
                new IndicesAliasesRequest.AliasActions(
                        IndicesAliasesRequest.AliasActions.Type.ADD);
        aliasActions.alias(alias.toString());
        aliasActions.index(index.toString());
        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();
        aliasesRequest.addAliasAction(aliasActions);

        try {
            AcknowledgedResponse response = this.openSearchClient.indices()
                    .updateAliases(aliasesRequest, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            throw new IOException(ex);
        }
    }

    @Override
    public boolean doesIndexExists(Index index) throws IOException {
        try {
            GetIndexRequest request = new GetIndexRequest(index.toString());
            return this.openSearchClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            throw new IOException(ex);
        }
    }

    @Override
    public boolean doesAliasExists(Alias alias) throws IOException {
        try {
            GetAliasesRequest request = new GetAliasesRequest(alias.toString());
            return this.openSearchClient.indices().existsAlias(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            throw new IOException(ex);
        }
    }

    @Override
    public Set<Index> getIndexByAlias(Alias alias) throws IOException {
        GetAliasesRequest request = new GetAliasesRequest(alias.toString());
        Map<String, Set<AliasMetadata>> metadataSet;

        try {
            metadataSet = this.openSearchClient.indices()
                    .getAlias(request, RequestOptions.DEFAULT)
                    .getAliases();
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            throw new IOException(ex);
        }

        Set<Index> indices = new HashSet<>();
        for (String key : metadataSet.keySet()) {
            indices.add(Index.fromString(key));
        }
        return indices;
    }
}
