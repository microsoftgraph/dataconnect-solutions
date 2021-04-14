package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.validators;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.arguments.DateField;

public class DateFieldEnumConverter implements IStringConverter<DateField> {

    @Override
    public DateField convert(String value) {
        DateField convertedValue = DateField.fromString(value);

        if (convertedValue == null) {
            throw new ParameterException("Value " + value + "can not be used as -timeFilterField parameter. " +
                    "Available values are: " + DateField.mkString(", ") + ".");
        }
        return convertedValue;
    }

}
