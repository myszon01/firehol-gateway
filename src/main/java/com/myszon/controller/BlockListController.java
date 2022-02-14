package com.myszon.controller;

import com.myszon.controller.projection.BaseResponse;
import com.myszon.controller.projection.IpAddressResponse;
import com.myszon.service.SearchService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@Controller("/v1/blocklist")
public class BlockListController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockListController.class);

    private final SearchService searchService;

    public BlockListController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Get("/ip/{ipAddress}")
    public HttpResponse<BaseResponse> getIpAddress(String ipAddress) {

        if(!isValidIpAddress(ipAddress)) {
            return HttpResponse.badRequest(new BaseResponse("Failure", "Wrong IP address format"));
        }

        try {
            return HttpResponse.ok(new IpAddressResponse(
                    searchService.findIpAddressById(ipAddress).getDocuments().get(0).getIpAddress()));
        } catch (IOException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            return HttpResponse.serverError().body(
                    new BaseResponse("Failure", ex.getMessage())
            );
        }
    }

    private boolean isValidIpAddress(String ipAddress) {
        String[] ipClasses = ipAddress.split("\\.");
        if(ipClasses.length != 4) return false;

        for (String ipClass : ipClasses) {
            int range;

            try {
                range = Integer.parseInt(ipClass);
            } catch (NumberFormatException ex) {
                return false;
            }

            if (range < 0 || range > 255) {
                return false;
            }

        }
        return true;
    }
}
