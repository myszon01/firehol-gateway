package com.myszon.controller.projection;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class IngestResponse {

    private int docCount;

    public IngestResponse(int docCount) {
        this.docCount = docCount;
    }


    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }
}
