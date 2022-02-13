package com.myszon.repository.impl;

import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.repository.IIndexIngest;
import jakarta.inject.Singleton;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.action.DocWriteRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.xcontent.XContentBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Singleton
public class IndexIngest implements IIndexIngest {


    private final RestHighLevelClient openSearchClient;

    public IndexIngest(RestHighLevelClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }


    @Override
    public void insertIPAddressesToIndex(List<IpAddress> ipAddress, Index index){



        BulkRequest request = new BulkRequest();
//        XContentBuilder xContentBuilder = XContentBuilder.
//        this.openSearchClient.bulk()
//        BulkOperation createIndex = new BulkOperation.Builder().index(new IndexOperation.Builder<IpAddress>()
//                        .index("ip_addresses")
//                        .document(ipAddress.get(0))
//                .build()).build();
//        List<BulkOperation> bulkOperations = ipAddress.stream().map(ia -> new BulkOperation.Builder()
//                .create(
//                        new CreateOperation.Builder<IpAddress>()
//                                .id(ia.getIpAddress())
//                                .document(ia)
//                                .build())
//                .build())
//                .collect(Collectors.toList());
//
////        List<BulkOperation> bulkOperations = new ArrayList<>();
////        bulkOperations.add(createIndex);
//        BulkRequest request = BulkRequest.of(builder -> builder.index("ip_addresses")
//                .timeout(new Time.Builder().time("2").build())
//                .operations(bulkOperations)
//        );
//        BulkResponse response = elasticClient.bulk(request).get();
//        System.out.println(response.toString());
    }

    private void test() throws IOException {


        //Adding data to the index.
        IndexRequest request = new IndexRequest("custom-index"); //Add a document to the custom-index we created.
        request.id("1"); //Assign an ID to the document.

        HashMap<String, String> stringMapping = new HashMap<String, String>();
        stringMapping.put("message:", "Testing Java REST client");
        request.source(stringMapping); //Place your content into the index's source.
        IndexResponse indexResponse = this.openSearchClient.index(request, RequestOptions.DEFAULT);

        //Getting back the document
        GetRequest getRequest = new GetRequest("custom-index", "1");
        GetResponse response = this.openSearchClient.get(getRequest, RequestOptions.DEFAULT);

        System.out.println(response.getSourceAsString());

//        //Delete the document
//        DeleteRequest deleteDocumentRequest = new DeleteRequest("custom-index", "1"); //Index name followed by the ID.
//        DeleteResponse deleteResponse = client.delete(deleteDocumentRequest, RequestOptions.DEFAULT);
//
//        //Delete the index
//        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("custom-index"); //Index name.
//        AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

//        client.close();
    }
}
