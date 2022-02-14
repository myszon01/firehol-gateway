package com.myszon.controller.projection;

public class IpAddressResponse extends BaseResponse{

    private String ip;

    public IpAddressResponse(String ip) {
        super("Success", "");
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
