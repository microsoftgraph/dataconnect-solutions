package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.util;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeHelper {

    public static Instant parseStartDate(String input) throws InvalidFormatException {
        // 2020-07-30 becomes 2020-07-29T23:59:59.999
        // because we are using this output compared with `isAfter` or `isBefore` (exclusive functions)
        Instant startDate = parseInputDate(input);
        return startDate.minus(1, ChronoUnit.NANOS);
    }

    public static Instant parseEndDate(String input) throws InvalidFormatException {
        // 2020-08-03 becomes 2020-08-03T23:59:59.999
        // because we are using this output compared with `isAfter` or `isBefore` (exclusive functions)
        Instant endDate = parseInputDate(input);
        return endDate.plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
    }

    public static Instant parseInputDate(String input) throws InvalidFormatException {
        LocalDate date = LocalDate.parse(input);
        return date.atStartOfDay(ZoneId.of("UTC")).toInstant();
    }

}
