package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.arguments;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.util.TimeHelper;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.validators.DateFieldEnumConverter;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.validators.TimeArgumentValidator;

import java.time.Instant;

public class AppArguments {

    @Parameter(names = {"-start", "-startDate", "-S", "-s"},
            validateWith = TimeArgumentValidator.class,
            required = true,
            description = "The earliest UTC point in time (inclusive). Date format: 'yyyy-MM-dd'")
    private String startDateStr;

    @Parameter(names = {"-end", "-endDate", "-E", "-e"},
            validateWith = TimeArgumentValidator.class,
            required = true,
            description = "The most recent UTC point in time (inclusive). Date format: 'yyyy-MM-dd'")
    private String endDateStr;

    @Parameter(names = {"-subject"},
            description = "Case insensitive filter on email 'Subject' field")
    private String subjectFilter;

    @Parameter(names = {"-field", "-timeFilterField"},
            description = "Field used for time based filtering\n",
            converter = DateFieldEnumConverter.class)
    private DateField dateField = DateField.RECEIVED_DATE_TIME;

    @Parameter(names = {"--help", "-help", "-h", "-H"},
            help = true,
            description =
                    "Usage example: \n" +
                            "\t\t$>java -jar emails-exporter.jar -start 2020-06-01 -end 2020-07-30\n" +
                            "\t\t\tor\n" +
                            "\t\t$>java -jar emails-exporter.jar -start 2020-06-01 -end 2020-07-30 -subject \"Azure Devops\" -field sentDateTime\n"
    )
    private boolean help;

    public Instant startDate() {
        try {
            return TimeHelper.parseStartDate(startDateStr.trim());
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Instant endDate() {
        try {
            return TimeHelper.parseEndDate(endDateStr.trim());
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public String getSubjectFilter() {
        return subjectFilter;
    }

    public void setSubjectFilter(String subjectFilter) {
        this.subjectFilter = subjectFilter;
    }


    public DateField getDateField() {
        return dateField;
    }

    public void setDateField(DateField dateField) {
        this.dateField = dateField;
    }
}
