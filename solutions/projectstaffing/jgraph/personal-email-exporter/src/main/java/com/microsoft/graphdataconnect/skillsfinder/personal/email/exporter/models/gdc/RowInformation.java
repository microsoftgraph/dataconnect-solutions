package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RowInformation {

    @JsonProperty("errorInformation")
    private String errorInformation;
    @JsonProperty("userReturnedNoData")
    private Boolean userReturnedNoData;
    @JsonProperty("isUserSummaryRow")
    private Boolean isUserSummaryRow;
    @JsonProperty("userHasCompleteData")
    private Boolean userHasCompleteData;

    public String getErrorInformation() {
        return errorInformation;
    }

    public void setErrorInformation(String errorInformation) {
        this.errorInformation = errorInformation;
    }

    public Boolean getUserReturnedNoData() {
        return userReturnedNoData;
    }

    public void setUserReturnedNoData(Boolean userReturnedNoData) {
        this.userReturnedNoData = userReturnedNoData;
    }

    public Boolean getUserSummaryRow() {
        return isUserSummaryRow;
    }

    public void setUserSummaryRow(Boolean userSummaryRow) {
        isUserSummaryRow = userSummaryRow;
    }

    public Boolean getUserHasCompleteData() {
        return userHasCompleteData;
    }

    public void setUserHasCompleteData(Boolean userHasCompleteData) {
        this.userHasCompleteData = userHasCompleteData;
    }
}
