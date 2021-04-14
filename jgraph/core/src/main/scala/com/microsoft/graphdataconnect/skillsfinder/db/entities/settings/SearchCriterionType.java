package com.microsoft.graphdataconnect.skillsfinder.db.entities.settings;


import java.util.Arrays;
import java.util.List;

public enum SearchCriterionType {
    PROFILE_SKILLS,
    PROFILE_ABOUT_ME,
    PROFILE_TOPICS,
    EMAIL_CONTENT,
    EMAIL_CONTENT_LEMMATIZED,
    DE_PROFILE,
    DE_DOMAIN_SPECIFIC_SKILLS,
    DE_EMAIL_CONTENT,
    RECEIVED_EMAIL_CONTENT,
    DE_RECEIVED_EMAIL_CONTENT,
    PROFILE_NAMES,
    PROFILE_FACETS;

    public static List<SearchCriterionType> getPublicTypes() {
        return Arrays.asList(
            PROFILE_SKILLS,
            PROFILE_ABOUT_ME,
            PROFILE_TOPICS,
            EMAIL_CONTENT);
    }
}
