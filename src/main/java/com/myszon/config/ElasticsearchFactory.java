package com.myszon.config;


import io.micronaut.context.annotation.*;
import jakarta.inject.Singleton;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Requires(beans = ElasticsearchProperties.class)
@Factory
public class ElasticsearchFactory {

    @Bean(preDestroy = "close")
    RestHighLevelClient getRestHighLevelClient(ElasticsearchProperties elasticsearchProperties) throws URISyntaxException {


        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        SSLContext finalSslContext = sslContext;

        URI uri = new URI(elasticsearchProperties.getBaseUrl());
        RestClientBuilder builder = RestClient.builder(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(elasticsearchProperties.getUsername(),
                                    elasticsearchProperties.getPassword()));

                    return httpClientBuilder
                            .setSSLContext(finalSslContext)
                            .setDefaultCredentialsProvider(credentialsProvider);
                });

        return new RestHighLevelClient(builder);
    }

}
