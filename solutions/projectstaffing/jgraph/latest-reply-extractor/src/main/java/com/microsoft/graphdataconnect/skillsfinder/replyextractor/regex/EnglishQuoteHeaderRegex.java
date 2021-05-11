/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.regex;

import java.util.ArrayList;
import java.util.List;

public class EnglishQuoteHeaderRegex extends QuoteHeaderRegex {
    public EnglishQuoteHeaderRegex() {
        super();
    }

    @Override
    public String on() {
        return "On";
    }

    @Override
    public String wrote() {
        return "wrote";
    }

    @Override
    public String to() {
        return "To";
    }

    @Override
    public String from() {
        return "From";
    }

    @Override
    public String sent() {
        return "Sent";
    }

    @Override
    public String date() {
        return "Date";
    }

    @Override
    public String subject() {
        return "Subject";
    }


    public List<String> getRegexExpressionsWithEnron() {
        List<String> englishHeaderPatterns = new ArrayList<>();
        englishHeaderPatterns.add("^.*---.*Forwarded by.*(\\n)?.*-----");
        englishHeaderPatterns.add("^.*---.*Original Message.*(\\n)?.*---");

        englishHeaderPatterns.addAll(super.getRegexExpressions());

        englishHeaderPatterns.add(".*To:(\\n)?((.*<.*>.*\\n)|(<*.*@.*\\n)|(.*\\/.*\\/.*)){1,2}");
        return englishHeaderPatterns;
    }
}
