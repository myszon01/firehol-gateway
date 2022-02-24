package com.myszon.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myszon.model.Alias;
import com.myszon.model.IpAddress;
import com.myszon.model.SearchResults;
import com.myszon.repository.IIndexSearch;
import jakarta.inject.Singleton;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class IndexSearch implements IIndexSearch {

    private final RestHighLevelClient openSearchClient;
    private final ObjectMapper mappers;

    public IndexSearch(RestHighLevelClient openSearchClient, ObjectMapper mappers) {
        this.openSearchClient = openSearchClient;
        this.mappers = mappers;
    }


    /*
        {
          "query" : {
            "term" : {
              "ipAddress" : {
                "value": ${ipAddress}
              }
            }
          }
        }
     */
    @Override
    public List<IpAddress> findIpAddressById(String ipAddress) throws IOException {

        QueryBuilder queryBuilder = new TermQueryBuilder("ipAddress", ipAddress);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(builder);
        searchRequest.indices(Alias.IP_ADDRESS.toString(), Alias.IP_ADDRESS_RANGE.toString());

        SearchResponse searchResponse = this.openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

        List<IpAddress> ipAddresses = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> values = hit.getSourceAsMap();
            String ip = (String) values.get("ipAddress");
            String path = (String) values.get("path");
            String sha = (String) values.get("sha");

            ipAddresses.add(new IpAddress(ip, path, sha));
        }

        return ipAddresses;
    }
}
