package com.microsoft.graphdataconnect.skillsfinder.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenScope {

    AZURE_MANAGEMENT_SCOPE("https://management.core.windows.net//user_impersonation"),
    AZURE_STORAGE_SCOPE("https://storage.azure.com//user_impersonation");

    private final String value;


    TokenScope(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
