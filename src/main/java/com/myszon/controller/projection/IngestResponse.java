package com.myszon.controller.projection;

public class IngestResponse extends BaseResponse {

    private int docCount;

    public IngestResponse(int docCount, String msg) {
        super("Success", msg);
        this.docCount = docCount;
    }


    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }
}
