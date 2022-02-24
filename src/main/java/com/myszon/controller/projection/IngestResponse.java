package com.myszon.controller.projection;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Getter;

@Introspected @Getter @Builder
public class IngestResponse {

    private int processedFiles;
    private int failedFiles;
    private long insertedIpAddressesAndRange;
    private int totalFiles;

}
