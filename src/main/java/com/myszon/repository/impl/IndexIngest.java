package com.myszon.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myszon.config.ElasticsearchProperties;
import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.repository.IIndexIngest;
import jakarta.inject.Singleton;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
public class IndexIngest implements IIndexIngest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexIngest.class);

    private final RestHighLevelClient openSearchClient;
    private final ObjectMapper mapper;
    private final int maxBatchSize;

    public IndexIngest(RestHighLevelClient openSearchClient, ObjectMapper mapper,
                       ElasticsearchProperties elasticsearchProperties) {
        this.openSearchClient = openSearchClient;
        this.mapper = mapper;
        this.maxBatchSize = elasticsearchProperties.getMaxBulkSize();
    }

    @Override
    public boolean insertIPAddressesToIndex(List<IpAddress> ipAddresses, Index index) throws IOException {
        BulkRequest bulkRequest = new BulkRequest(index.toString());

        int bulkRequestSize = 0;
        for (int i = 0; i < ipAddresses.size(); i++) {
            IpAddress ipAddress = ipAddresses.get(i);
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.id(ipAddress.getIpAddress());
            indexRequest.source(mapper.writeValueAsString(ipAddress)
                    .getBytes(StandardCharsets.UTF_8), XContentType.JSON);
            bulkRequest.add(indexRequest);
            bulkRequestSize++;

            if (bulkRequestSize < maxBatchSize && i + 1 < ipAddresses.size()) continue;

            LOGGER.info(String.format("Start inserting %s ipAddresses to %s", bulkRequestSize, index));
            this.executeBulkRequest(bulkRequest);
            LOGGER.info("Index insertion successful");

            bulkRequestSize = 0;
            bulkRequest = new BulkRequest(index.toString());
        }

        return true;
    }

    private void executeBulkRequest(BulkRequest bulkRequest) throws IOException {
        try {
            BulkResponse response = this.openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            response.status();
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }
}
