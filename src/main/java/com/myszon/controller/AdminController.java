package com.myszon.controller;

import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.service.IngestService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.io.IOException;

@Controller
public class AdminController {

    private final IngestService ingestService;

    public AdminController(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    // TODO Handle Exception
    @Get
    public String dupa() throws IOException {
        ingestService.startIngestion(Alias.IP_ADDRESS);
        return "dupa";
    }
}
