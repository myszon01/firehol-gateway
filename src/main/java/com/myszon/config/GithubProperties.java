package com.myszon.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;

@ConfigurationProperties(GithubProperties.PREFIX)
@Requires(property = GithubProperties.PREFIX)
public class GithubProperties {

    public static final String PREFIX = "github";
    public static final String GITHUB_API_URL = "https://api.github.com";

    private String organization;
    private String repo;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }
}