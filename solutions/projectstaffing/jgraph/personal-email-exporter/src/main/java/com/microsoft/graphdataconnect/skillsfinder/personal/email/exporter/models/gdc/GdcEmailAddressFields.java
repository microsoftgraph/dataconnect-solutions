package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GdcEmailAddressFields {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Address")
    private String address;

    public GdcEmailAddressFields(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public GdcEmailAddressFields() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
