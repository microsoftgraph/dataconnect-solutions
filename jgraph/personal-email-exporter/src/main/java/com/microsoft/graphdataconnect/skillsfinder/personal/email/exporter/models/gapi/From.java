package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class From {
    @JsonProperty("emailAddress")
    private EmailAddressFields emailAddress;

    public EmailAddressFields getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddressFields emailAddress) {
        this.emailAddress = emailAddress;
    }
}
