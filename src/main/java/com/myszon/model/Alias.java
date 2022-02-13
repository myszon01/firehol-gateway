package com.myszon.model;

public enum Alias {

    IP_ADDRESS("ip_address");

    private final String text;

    Alias(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
