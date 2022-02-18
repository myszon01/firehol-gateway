package com.myszon.controller.projection;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class IpAddressResponse {

    private final String ip;

    public IpAddressResponse(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
