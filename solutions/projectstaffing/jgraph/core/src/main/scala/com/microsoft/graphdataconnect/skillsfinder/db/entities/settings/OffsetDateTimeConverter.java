package com.microsoft.graphdataconnect.skillsfinder.db.entities.settings;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, String> {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSSS xxx";

    @Override
    public String convertToDatabaseColumn(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ofPattern(DATE_FORMAT).format(offsetDateTime);
        }
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(String dateAsString) {
        if (dateAsString == null) {
            return null;
        } else {
            return OffsetDateTime.parse(dateAsString, DateTimeFormatter.ofPattern(DATE_FORMAT));
        }
    }
}
