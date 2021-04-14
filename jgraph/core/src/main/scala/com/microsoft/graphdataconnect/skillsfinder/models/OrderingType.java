package com.microsoft.graphdataconnect.skillsfinder.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderingType {

    AVAILABILITY("availability"),
    RELEVANCE("relevance");

    private final String value;


    OrderingType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
