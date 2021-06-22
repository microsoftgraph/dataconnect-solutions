/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.models;

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
