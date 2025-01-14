package com.myszon.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public enum Index {

    IP_ADDRESS_V1("ip_address_v1"),
    IP_ADDRESS_RANGE_V1("ip_address_range_v1"),

    IP_ADDRESS_V2("ip_address_v2"),
    IP_ADDRESS_RANGE_V2("ip_address_range_v2");

    private final String text;

    Index(final String text) {
        this.text = text;
    }

    public static Index fromString(String text) {
        for (Index type : Index.values()) {
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
