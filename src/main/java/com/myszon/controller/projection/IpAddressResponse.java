package com.myszon.controller.projection;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Getter;

@Introspected @Getter @Builder
public class IpAddressResponse {

    private final String ip;
    private final String path;
}
