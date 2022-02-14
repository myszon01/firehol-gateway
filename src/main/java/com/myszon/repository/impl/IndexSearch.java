package com.myszon.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myszon.model.Alias;
import com.myszon.model.IpAddress;
import com.myszon.model.SearchResults;
import com.myszon.repository.IIndexSearch;
import jakarta.inject.Singleton;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Singleton
public class IndexSearch implements IIndexSearch {

    private final RestHighLevelClient openSearchClient;
    private final ObjectMapper mappers;

    public IndexSearch(RestHighLevelClient openSearchClient,
                       ObjectMapper mappers) {
        this.openSearchClient = openSearchClient;
        this.mappers = mappers;
    }

    @Override
    public SearchResults<IpAddress> findIpAddressById(String ipAddress) throws IOException {
        GetRequest getRequest = new GetRequest(Alias.IP_ADDRESS.toString(), ipAddress);
        GetResponse response = this.openSearchClient.get(getRequest, RequestOptions.DEFAULT);
        if(!response.isExists()) return new SearchResults.Builder<IpAddress>()
                .documents(Collections.emptyList()).found(false).build();
        IpAddress ipAdd = mappers.readValue(response.getSourceAsString(), IpAddress.class);

        return new SearchResults.Builder<IpAddress>().documents(List.of(ipAdd)).found(true).build();
    }
}
