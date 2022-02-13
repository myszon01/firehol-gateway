package com.myszon.controller;

import com.myszon.model.Index;
import com.myszon.service.IngestService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class AdminController {

    private final IngestService ingestService;

    public AdminController(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @Get
    public String dupa() {
        ingestService.startIngestion(Index.IP_ADDRESS_V1);
        return "dupa";
    }
}
