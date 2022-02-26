package com.myszon.controller.projection;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Introspected @Getter @Builder @EqualsAndHashCode
public class IngestResponse {

    private int processedFiles;
    private int failedFiles;
    private long insertedIpAddressesAndRange;
    private int totalFiles;

}
