/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package configuration;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.List;
import java.util.Map;

public class SearchSettingsTests {

   @Test
    public void postProfileSkillsTest() {
        List<Map<String,Object>> defaultList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        boolean PROFILE_SKILLS_active = false;
        String key = "PROFILE_SKILLS_active";
        String results = TestUtils.postSearchSettings(key, PROFILE_SKILLS_active);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        List<Map<String,Object>> modifiedList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        Assert.assertNotEquals("The settings were not updated",defaultList,modifiedList);
    }
    @Test
    public void postProfileAboutMeTest() {
        List<Map<String,Object>> defaultList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        boolean PROFILE_ABOUT_ME_active = false;
        String key = "PROFILE_ABOUT_ME_active";
        String results = TestUtils.postSearchSettings(key, PROFILE_ABOUT_ME_active);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        List<Map<String,Object>> modifiedList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        Assert.assertNotEquals("The settings were not updated",defaultList,modifiedList);
    }
    @Test
    public void postProfileTopicsTest() {
        List<Map<String,Object>> defaultList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        boolean PROFILE_TOPICS_active = false;
        String key = "PROFILE_TOPICS_active";
        String results = TestUtils.postSearchSettings(key, PROFILE_TOPICS_active);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        List<Map<String,Object>> modifiedList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        Assert.assertEquals("The settings were not updated",defaultList,modifiedList);
    }
    @Test
    public void postEmailContentTest() {
        List<Map<String,Object>> defaultList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        String key = "EMAIL_CONTENT_active";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        List<Map<String,Object>> modifiedList = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        Assert.assertNotEquals("The settings were not updated",defaultList,modifiedList);
    }
    @Test
    public void postContryTest() {
        String key = "country_active";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertFalse("m365_country is still present",TestUtils.getSearchFilters().contains("m365_country"));
    }
    @Test
    public void postStateTest() {
        String key = "state_active";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertFalse("m365_state is still present",TestUtils.getSearchFilters().contains("m365_state"));
    }
    @Test
    public void postCityTest() {
        String key = "city_active";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertFalse("m365_city is still present",TestUtils.getSearchFilters().contains("m365_city"));
    }
    @Test
    public void postDepartmentTest() {
        String key = "department_city";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertFalse("m365_department is still present",TestUtils.getSearchFilters().contains("m365_department"));
    }
    @Test
    public void postRoleTest() {
        String key = "role_active";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertFalse("m365_role is still present",TestUtils.getSearchFilters().contains("role"));
    }
    @Test
    public void postDataLocationTest() {
        String key = "data_location_active";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
    }
    @Test
    public void postDataRoleTest() {
        String key = "data_role_active";
        String results = TestUtils.postSearchSettings(key, true);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
    }
    @Test
    public void postFreshnessTest() {
        int freshness = 23;
        String key = "freshness";
        String results = TestUtils.postSearchSettings(key, freshness);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", TestUtils.getSearchSettings().get(key), freshness);
    }
    @Test
    public void postFreshnessEnabledTest() {
        String key = "freshnessEnabled";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", false, TestUtils.getSearchSettings().get(key));
    }
    @Test
    public void postVolumeTest() {
        int volume = 27;
        String key = "volume";
        String results = TestUtils.postSearchSettings(key, volume);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", TestUtils.getSearchSettings().get(key), volume);
    }
    @Test
    public void postVolumeEnabledTest() {
        String key = "volumeEnabled";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", false, TestUtils.getSearchSettings().get(key));
    }
    @Test
    public void postRelevanceScoreTest() {
        int relevanceScore = 60;
        String key = "relevanceScore";
        String results = TestUtils.postSearchSettings(key, relevanceScore);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", TestUtils.getSearchSettings().get(key), relevanceScore);
    }
    @Test
    public void postRelevanceScoreEnabledTest() {
        String key = "relevanceScoreEnabled";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", false, TestUtils.getSearchSettings().get(key));
    }
    @Test
    public void postFreshnessBeginDateEnabledTest() {
        String key = "freshnessBeginDateEnabled";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", false, TestUtils.getSearchSettings().get(key));
    }
    @Test
    public void postIncludedEmailDomainsEnabledTest() {
        String key = "includedEmailDomainsEnabled";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", false, TestUtils.getSearchSettings().get(key));
    }
    @Test
    public void postExcludedEmailDomainsEnabledTest() {
        String key = "excludedEmailDomainsEnabled";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", false, TestUtils.getSearchSettings().get(key));
    }
    @Test
    public void postUseReceivedEmailsContentTest() {
        String key = "useReceivedEmailsContent";
        String results = TestUtils.postSearchSettings(key, false);
        Assert.assertEquals("The settings were not updated", results, "Employee search settings updated.");
        Assert.assertEquals("The settings were not updated", false, TestUtils.getSearchSettings().get(key));
    }
}
