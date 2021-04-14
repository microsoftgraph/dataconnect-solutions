package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.util.TimeHelper;

public class TimeArgumentValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        try {
            TimeHelper.parseInputDate(value);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            throw new ParameterException("Parameter " + name + " should be in format 'yyyy-MM-dd'");
        }
    }
}
