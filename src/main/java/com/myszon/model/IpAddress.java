package com.myszon.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Builder
@Getter
@Introspected
@EqualsAndHashCode
public class IpAddress{

    private String ipAddress;
    private String path;
    private String sha;
}
