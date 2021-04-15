package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DateField {
    SENT_DATE_TIME("sentDateTime"),
    RECEIVED_DATE_TIME("receivedDateTime"),
    LAST_MODIFIED_DATE_TIME("lastModifiedDateTime"),
    CREATED_DATE_TIME("createdDateTime");

    private String value;

    DateField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DateField fromString(String text) {
        for (DateField e : DateField.values()) {
            if (e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }

    public static String mkString(String delimiter) {
        List<String> values = Arrays.stream(DateField.values()).map(DateField::name).collect(Collectors.toList());
        return String.join(delimiter, values);
    }
}
