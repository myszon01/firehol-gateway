package com.myszon.api.responses;

import io.micronaut.core.annotation.Introspected;

@Introspected
public enum TreeType {

    BLOB("BLOB"),
    TREE("TREE");

    private final String text;

    TreeType(final String text) {
        this.text = text;
    }

    public static TreeType fromString(String text) {
        for (TreeType type : TreeType.values()) {
            if (type.text.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    @Override
    public String toString() {
        return text;
    }
}
