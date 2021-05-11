/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilterType {
    M365_COUNTRY("m365_country"),
    M365_STATE("m365_state"),
    M365_CITY("m365_city"),
    //    M365_OFFICE_LOCATION("m365_office_location"),
//    M365_COMPANY_NAME("m365_company_name"),
    M365_DEPARTMENT("m365_department"),
    M365_ROLE("m365_role"),
    HR_DATA_LOCATION("hr_data_location"),
    HR_DATA_ROLE("hr_data_role");

    private final String value;


    FilterType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
