package com.myszon.model;

import com.myszon.api.responses.TreeType;

public enum Alias {

    IP_ADDRESS("ip_address");

    private final String text;

    Alias(final String text) {
        this.text = text;
    }

    public static Alias fromString(String text) {
        for (Alias type : Alias.values()) {
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
