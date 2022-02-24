package com.myszon.controller;

import com.myszon.controller.projection.BaseResponse;
import com.myszon.controller.projection.IpAddressResponse;
import com.myszon.service.SearchService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;
import java.util.stream.Collectors;

@Controller("/blocklist/v1")
public class BlockListController {

    private final SearchService searchService;

    public BlockListController(final SearchService searchService) {
        this.searchService = searchService;
    }

    @Get("/ip/{ipAddress}")
    public HttpResponse<BaseResponse<?>> getIpAddress(String ipAddress) {

        if(!isValidIpAddress(ipAddress)) {
            return HttpResponse.badRequest(
                    BaseResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.getCode())
                            .message("Wrong IP address format")
                            .build());
        }

        try {
            List<IpAddressResponse> results = searchService.findIpAddressById(ipAddress)
                    .stream().map(ip -> IpAddressResponse.builder()
                            .ip(ip.getIpAddress())
                            .path(ip.getPath())
                            .build())
                    .collect(Collectors.toList());

            return HttpResponse.ok(
                    BaseResponse.builder()
                            .status(HttpStatus.OK.getCode())
                            .message("success")
                            .entity(results)
                            .build());

        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResponse.serverError().body(
                    BaseResponse.builder()
                            .status(HttpStatus.NOT_FOUND.getCode())
                            .message(ex.getMessage())
                            .build());
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
