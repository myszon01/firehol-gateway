package com.myszon.model;

public enum Index {

    IP_ADDRESS_V1("ip_address_v1"),
    IP_ADDRESS_V2("ip_address_v2");

    private final String text;

    Index(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
