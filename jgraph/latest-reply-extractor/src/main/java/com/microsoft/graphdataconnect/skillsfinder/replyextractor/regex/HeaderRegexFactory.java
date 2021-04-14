package com.microsoft.graphdataconnect.skillsfinder.replyextractor.regex;

import java.util.*;

import static com.microsoft.graphdataconnect.skillsfinder.replyextractor.regex.Language.*;

public class HeaderRegexFactory {

    public static List<String> getQuoteHeaderRegexFor(boolean includeEnronRegex, Language... languages) {
        List<String> quoteHeadersRegex = new ArrayList<>();
        Set<Language> languageSet = new HashSet<>(Arrays.asList(languages));

        if (languageSet.contains(ENGLISH)) {
            if (includeEnronRegex) {
                quoteHeadersRegex.addAll(new EnglishQuoteHeaderRegex().getRegexExpressionsWithEnron());
            } else {
                quoteHeadersRegex.addAll(new EnglishQuoteHeaderRegex().getRegexExpressions());
            }
        }

        if (languageSet.contains(SPANISH)) {
            quoteHeadersRegex.addAll(new SpanishQuoteHeaderRegex().getRegexExpressions());
        }

        if (languageSet.contains(ROMANIAN)) {
            quoteHeadersRegex.addAll(new RomanianQuoteHeaderRegex().getRegexExpressions());
        }

        return quoteHeadersRegex;
    }

}
