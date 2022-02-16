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
                    new BaseResponse<>(HttpStatus.NOT_FOUND.getCode(), "Alias does not exists")
            );
        }

        try {
            int docCount = this.ingestService.startIngestion(aliasFromParam);
            return HttpResponse.ok(
                    new BaseResponse<>(HttpStatus.OK.getCode(), "",new IngestResponse(docCount)));
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResponse.serverError().body(
                    new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage())
            );
        }
    }
}
