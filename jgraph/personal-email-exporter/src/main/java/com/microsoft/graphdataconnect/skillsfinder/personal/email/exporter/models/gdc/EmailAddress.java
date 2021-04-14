package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailAddress {
    @JsonProperty("EmailAddress")
    private GdcEmailAddressFields emailAddress;

    public EmailAddress(GdcEmailAddressFields emailAddress) {
        this.emailAddress = emailAddress;
    }

    public EmailAddress() {
    }

    public GdcEmailAddressFields getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(GdcEmailAddressFields emailAddress) {
        this.emailAddress = emailAddress;
    }
}
