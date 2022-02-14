package com.myszon.controller;

import com.myszon.controller.projection.BaseResponse;
import com.myszon.controller.projection.IngestResponse;
import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.repository.impl.IndexManager;
import com.myszon.service.IngestService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseWrapper;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.opensearch.client.GetAliasesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Controller("/admin/v1")
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final IngestService ingestService;

    public AdminController(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @Post("/ingest/{alias}")
    public HttpResponse<BaseResponse> ingest(String alias) {
        Alias aliasFromParam = null;
        try {
            aliasFromParam = Alias.fromString(alias);
        } catch (IllegalArgumentException ex) {

            return HttpResponse.notFound().body(
                    new BaseResponse("Failure", "Alias does not exists")
            );
        }

        try {
            Instant start = Instant.now();
            int docCount = this.ingestService.startIngestion(aliasFromParam);
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            return HttpResponse.ok(new IngestResponse(docCount, Long.toString(timeElapsed)));
        } catch (Exception ex) {
            LOGGER.error(ex.getLocalizedMessage());
            return HttpResponse.serverError().body(
                    new BaseResponse("Failure", ex.getMessage())
            );
        }
    }
}
