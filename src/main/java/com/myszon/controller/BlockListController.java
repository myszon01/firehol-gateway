package com.myszon.controller;

import com.myszon.controller.projection.BaseResponse;
import com.myszon.controller.projection.IpAddressResponse;
import com.myszon.model.IpAddress;
import com.myszon.model.SearchResults;
import com.myszon.service.SearchService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/v1/blocklist")
public class BlockListController {

    private final SearchService searchService;

    public BlockListController(final SearchService searchService) {
        this.searchService = searchService;
    }

    @Get("/ip/{ipAddress}")
    public HttpResponse<BaseResponse<?>> getIpAddress(String ipAddress) {

        if(!isValidIpAddress(ipAddress)) {
            return HttpResponse.badRequest(
                    new BaseResponse<>(HttpStatus.BAD_REQUEST.getCode(), "Wrong IP address format"));
        }

        try {
            SearchResults<IpAddress> results = searchService.findIpAddressById(ipAddress);

            if (!results.isFound()) {
                return HttpResponse.notFound(
                        new BaseResponse<>(HttpStatus.NOT_FOUND.getCode(), "Ip address not found"));
            }
            return HttpResponse.ok(new BaseResponse<>(HttpStatus.OK.getCode(),"",
                    results.getDocuments().stream().map(ipA -> new IpAddressResponse(ipA.getIpAddress()))));
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResponse.serverError().body(
                    new BaseResponse<>(HttpStatus.NOT_FOUND.getCode(), ex.getMessage())
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
