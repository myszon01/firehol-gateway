package com.myszon.controller.projection;

public class IngestResponse extends BaseResponse {

    private int docCount;

    public IngestResponse(int docCount) {
        super("Success", "");
        this.docCount = docCount;
    }


    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }
}
