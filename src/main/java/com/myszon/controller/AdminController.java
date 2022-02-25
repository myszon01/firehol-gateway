package com.myszon.controller;

import com.myszon.controller.projection.BaseResponse;
import com.myszon.controller.projection.IngestResponse;
import com.myszon.model.Alias;
import com.myszon.service.IngestService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

@Controller("/admin/v1")
public class AdminController {
    private final IngestService ingestService;

    public AdminController(final IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @Post("/ingest/{alias}")
    public HttpResponse<BaseResponse<?>> ingest(String alias) {
        Alias aliasFromParam = null;
        try {
            aliasFromParam = Alias.fromString(alias);
        } catch (IllegalArgumentException ex) {

            return HttpResponse.notFound().body(
                    BaseResponse.builder()
                            .status(HttpStatus.NOT_FOUND.getCode())
                            .error("Alias does not exists")
                            .build());
        }

        try {
            IngestResponse response = this.ingestService.startIngestion(aliasFromParam);
            return HttpResponse.ok(
                    BaseResponse.builder()
                            .status(HttpStatus.OK.getCode())
                            .entity(response)
                            .build());
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResponse.serverError().body(
                    BaseResponse.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                            .error(ex.getMessage())
                            .build());
        }
    }
}
