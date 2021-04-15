package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.models.gdc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GdcBody {
    public static final String HTML_TYPE = "Microsoft.OutlookServices.BodyType'HTML'";

    @JsonProperty("ContentType")
    private String contentType;
    @JsonProperty("Content")
    private String content;

    public GdcBody(String contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public GdcBody() {
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
