package com.myszon.api.responses;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Blob {
    private String encoding;
    private String content;
    private String sha;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }
}
