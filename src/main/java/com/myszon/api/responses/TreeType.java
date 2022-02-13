package com.myszon.api.responses;

public enum TreeType {

    BLOB("blob"),
    TREE("tree");

    private final String text;

    TreeType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
