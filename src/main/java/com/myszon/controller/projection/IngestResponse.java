package com.myszon.controller.projection;

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
