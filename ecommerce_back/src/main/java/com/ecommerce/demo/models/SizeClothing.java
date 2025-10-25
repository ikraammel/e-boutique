package com.ecommerce.demo.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SizeClothing {
    XS("XS"), S("S"), M("M"), L("L"), XL("XL"), XXL("XXL"),XXXL("XXXL");

    private final String label;

    SizeClothing(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    public static SizeClothing valueOfLabel(String label) {
        for (SizeClothing s : values()) {
            if (s.getLabel().equals(label)) return s;
        }
        throw new IllegalArgumentException("Unknown SizeClothing: " + label);
    }
}
