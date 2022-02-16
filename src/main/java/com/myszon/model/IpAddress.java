package com.myszon.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class IpAddress{

    private String ipAddress;

    public IpAddress() {}

    public IpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
