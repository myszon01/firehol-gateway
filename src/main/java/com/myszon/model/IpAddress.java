package com.myszon.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class IpAddress{

    private String ipAddress;
    private String path;
    private String sha;

    public IpAddress() {}

    public IpAddress(String ipAddress, String path, String sha) {
        this.ipAddress = ipAddress;
        this.path = path;
        this.sha = sha;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPath() {
        return path;
    }

    public String getSha() {
        return sha;
    }
}
