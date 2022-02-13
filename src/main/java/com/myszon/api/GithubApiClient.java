package com.myszon.api;


import com.myszon.api.responses.Commit;
import com.myszon.api.responses.Tree;
import com.myszon.config.GithubProperties;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client(GithubProperties.GITHUB_API_URL)
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = "application/vnd.github.v3+json, application/json")
public interface GithubApiClient {

    @Get("/repos/${github.organization}/${github.repo}/commits")
    Mono<List<Commit>> getCommits();

    @Get("/repos/${github.organization}/${github.repo}/git/commits/{sha}")
    Mono<Commit> getCommitBySha(String sha);

    @Get("/repos/${github.organization}/${github.repo}/git/trees/{sha}")
    Mono<Tree> getTreeBySha(String sha);

    @Get("/repos/${github.organization}/${github.repo}/git/blobs/{sha}")
    Mono<Tree> getBlobBySha(String sha);
}
