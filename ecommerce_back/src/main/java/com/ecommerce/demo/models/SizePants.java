package com.ecommerce.demo.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SizePants {
    SIZE_36("36"),
    SIZE_38("38"),
    SIZE_40("40"),
    SIZE_42("42"),
    SIZE_44("44"),
    SIZE_46("46");

    private final String label;

    SizePants(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
    public static SizePants valueOfLabel(String label){
        for(SizePants s : values()){
            if(s.getLabel().equals(label)) return s;
        }
        throw new IllegalArgumentException("Unknown SizePants: " + label);
    }

}
