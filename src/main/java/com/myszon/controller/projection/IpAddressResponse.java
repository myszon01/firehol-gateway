package com.myszon.controller.projection;

public class IpAddressResponse {

    private final String ip;

    public IpAddressResponse(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
