package com.myszon.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Introspected
public class IpAddress{

    private String ipAddress;
    private String path;
    private String sha;
}
