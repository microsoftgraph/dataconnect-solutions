package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GdcSender {
    @JsonProperty("EmailAddress")
    private GdcEmailAddressFields emailAddress;

    public GdcSender(GdcEmailAddressFields emailAddress) {
        this.emailAddress = emailAddress;
    }

    public GdcSender() {
    }

    public GdcEmailAddressFields getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(GdcEmailAddressFields emailAddress) {
        this.emailAddress = emailAddress;
    }
}
