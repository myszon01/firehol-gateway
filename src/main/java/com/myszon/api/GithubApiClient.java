package com.myszon.api;


import com.myszon.api.responses.Blob;
import com.myszon.api.responses.Commit;
import com.myszon.api.responses.Tree;
import com.myszon.config.GithubConfigProperties;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(GithubConfigProperties.GITHUB_API_URL)
@Header(name = USER_AGENT, value = "Micronaut HTTP Client v1")
@Header(name = ACCEPT, value = "application/vnd.github.v3+json, application/json")
public interface GithubApiClient {

    @Get("/repos/${github.organization}/${github.repo}/commits")
    List<Commit> getCommits();

    @Get("/repos/${github.organization}/${github.repo}/git/trees/{sha}")
    Tree getTreeBySha(String sha);

    @Get("/repos/${github.organization}/${github.repo}/git/blobs/{sha}")
    Mono<Blob> getBlobBySha(String sha);
}
