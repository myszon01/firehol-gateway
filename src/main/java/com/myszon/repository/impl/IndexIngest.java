package com.myszon.repository.impl;

import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.repository.IIndexIngest;
import jakarta.inject.Singleton;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Singleton
public class IndexIngest implements IIndexIngest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexIngest.class);

    private final RestHighLevelClient openSearchClient;

    public IndexIngest(RestHighLevelClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }


    @Override
    public boolean insertIPAddressesToIndex(List<IpAddress> ipAddresses, Index index) throws IOException {




        BulkRequest bulkRequest = new BulkRequest(index.toString());
        for (IpAddress ipAddress : ipAddresses) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.id(ipAddress.getIpAddress());
            HashMap<String, String> stringMapping = new HashMap<String, String>();
            stringMapping.put("ipAddress:", ipAddress.getIpAddress());
            indexRequest.source(stringMapping);
            bulkRequest.add(indexRequest);
        }
        try {
            LOGGER.info(String.format("Start inserting %s ipAddresses to %s", ipAddresses.size(), index));
            BulkResponse response = this.openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            LOGGER.info("Index insertion successful");
            return response.status().getStatus() == 200;
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }
}