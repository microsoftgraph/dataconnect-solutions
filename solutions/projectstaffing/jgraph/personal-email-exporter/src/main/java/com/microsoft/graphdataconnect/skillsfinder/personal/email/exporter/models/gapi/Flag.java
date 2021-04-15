package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Flag {
    @JsonProperty("flagStatus")
    private String flagStatus;

    public String getFlagStatus() {
        return flagStatus;
    }

    public void setFlagStatus(String flagStatus) {
        this.flagStatus = flagStatus;
    }
}
