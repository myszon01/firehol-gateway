package com.myszon.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import org.opensearch.client.RestHighLevelClient;

@ConfigurationProperties(ElasticsearchProperties.PREFIX)
@Requires(classes = RestHighLevelClient.class)
public class ElasticsearchProperties {

    public static final String PREFIX = "elasticsearch";

    private String username;
    private String password;
    private String baseUrl;
    private Integer maxBulkSize;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integer getMaxBulkSize() {
        return maxBulkSize;
    }

    public void setMaxBulkSize(Integer maxBulkSize) {
        this.maxBulkSize = maxBulkSize;
    }
}
