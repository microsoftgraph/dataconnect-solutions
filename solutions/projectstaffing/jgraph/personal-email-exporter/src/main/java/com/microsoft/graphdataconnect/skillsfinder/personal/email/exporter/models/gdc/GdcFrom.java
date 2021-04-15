package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GdcFrom {
    @JsonProperty("EmailAddress")
    private GdcEmailAddressFields emailAddress;

    public GdcFrom(GdcEmailAddressFields emailAddress) {
        this.emailAddress = emailAddress;
    }

    public GdcFrom() {
    }

    public GdcEmailAddressFields getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(GdcEmailAddressFields emailAddress) {
        this.emailAddress = emailAddress;
    }
}
