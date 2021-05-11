/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.regex;

import java.util.ArrayList;
import java.util.List;

public abstract class QuoteHeaderRegex {

    private List<String> regexExpressions = new ArrayList<>();

    public QuoteHeaderRegex() {
        this.regexExpressions.add("^(" + on() + "\\s(.{1,500})" + wrote() + ":)");
        this.regexExpressions.add(from() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + to() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + subject() + ":[^\\n]+");
        this.regexExpressions.add(to() + ":[^\\n]+\\n?([^\\n]+\\n?){0,2}" + from() + ":[^\\n]+\\n?([^\\n]+\\n?){0,2}" + subject() + ":[^\\n]+");
        this.regexExpressions.add(from() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + sent() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + to() + ":[^\\n]+");
        this.regexExpressions.add(from() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + subject() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + date() + ":[^\\n]+");
        this.regexExpressions.add(from() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + date() + ":[^\\n]+\\n{0,2}([^\\n]+\\n?){0,2}" + to() + ":[^\\n]+");
    }


    public abstract String on();

    public abstract String wrote();

    public abstract String to();

    public abstract String from();

    public abstract String subject();

    public abstract String sent();

    public abstract String date();

    public List<String> getRegexExpressions() {
        return regexExpressions;
    }
}
