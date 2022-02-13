package com.myszon.repository.impl;

import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.repository.IIndexManager;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.common.settings.Settings;

import java.io.IOException;

public class IndexManager implements IIndexManager {


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
    public String deleteIndex(Index index) {
        return null;
    }

    @Override
    public String swapIndexAlias(Index fromIndex, Index toIndex, Alias alias) {
        return null;
    }

    @Override
    public boolean createAliasForIndex(Alias alias, Index index) throws IOException {

        IndicesAliasesRequest.AliasActions aliasActions =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD);
        aliasActions.alias(alias.toString());
        aliasActions.index(index.toString());
        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();
        aliasesRequest.addAliasAction(aliasActions);

        AcknowledgedResponse response =
                this.openSearchClient.indices().updateAliases(aliasesRequest, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }
}
