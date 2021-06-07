package utils;

import com.microsoft.graphdataconnect.selenium.api.delete.DeleteMethod;
import com.microsoft.graphdataconnect.selenium.api.get.*;
import com.microsoft.graphdataconnect.selenium.api.post.PostAddEmployeeToTeam;
import com.microsoft.graphdataconnect.selenium.api.post.PostEmployeeSearch;
import com.microsoft.graphdataconnect.selenium.api.post.PostSearchSettings;
import com.microsoft.graphdataconnect.selenium.api.put.PutEditTeamTitleAndDescription;
import com.microsoft.graphdataconnect.selenium.api.support.FilePaths;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestUtils {

    public static String addMember(String searchCriteria) {
        PostAddEmployeeToTeam postAddMemberMethod = new PostAddEmployeeToTeam();
        postAddMemberMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        List<Map<String, Object>> results = null;
        String taxonomies = "";
        switch (searchCriteria) {
            case "NAME":
                results = searchAndReturnProfiles("", searchCriteria, taxonomies);
                break;
            case "SKILLS":
                results = searchAndReturnProfiles("", searchCriteria, getProperty("taxonomies"));
        }
        Object employeeId = results.get(0).get("id");
        Object email = results.get(0).get("mail");
        Object employeeName = results.get(0).get("name");
        Object skillsList = null;
        String skills = "";
        if (getIngestionModeStatusResponse().equals("production_mode")) {
            skillsList = results.get(0).get("relevantSkills");
            skills = getStringFromList((ArrayList<String>) skillsList, skills);
        } else {
            skillsList = results.get(0).get("declaredSkills");
            skills = getStringFromList((ArrayList<String>) skillsList, skills);
        }
        postAddMemberMethod.addProperty("employeeId", employeeId);
        postAddMemberMethod.addProperty("email", email);
        postAddMemberMethod.addProperty("name", employeeName);
        postAddMemberMethod.addProperty("skills", skills);
        return postAddMemberMethod.callAPI().jsonPath().get("message");

    }

    private static String getStringFromList(ArrayList<String> skillsList, String skills) {
        for (int i = 0; i < skillsList.size() - 1; i++) {
            skills += skillsList.get(i) + "\"" + "," + "\"";
        }
        skills += skillsList.get(skillsList.size() - 1);
        return skills;
    }

    public static String deleteTeamMember(String employeeId) {
        addMember("NAME");
        DeleteMethod deleteMethod = new DeleteMethod(employeeId);
        deleteMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        return deleteMethod.callAPI().prettyPrint();
    }

    public static ArrayList<Integer> getTeamMembersId() {
        GetTeamMembersMethod getTeamMembersMethod = new GetTeamMembersMethod();
        getTeamMembersMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        ArrayList<Integer> list = getTeamMembersMethod.callAPI().jsonPath().get("teamMemberId");
        return list;
    }

    public static List<ArrayList> search(String searchTerms, String searchCriteria, String taxonomies) {
        PostEmployeeSearch postEmployeeSearch = new PostEmployeeSearch("50", "0");
        postEmployeeSearch.expectResponseStatus(HttpResponseStatusType.OK_200);
        postEmployeeSearch.addProperty("searchCriteria", searchCriteria);
        postEmployeeSearch.addProperty("searchTerms", searchTerms);
        postEmployeeSearch.addProperty("taxonomiesList", taxonomies);
        List<Map<String, Object>> results = postEmployeeSearch.callAPI().jsonPath().get("employees");
        List<ArrayList> highlightedTerms = new ArrayList<>();
        for (Map<String, Object> details : results) {
            highlightedTerms.add((ArrayList) details.get("highlightedTerms"));
        }
        return highlightedTerms;
    }

    public static List<Map<String, Object>> searchAndReturnProfiles(String searchTerms, String searchCriteria, String taxonomies) {
        PostEmployeeSearch postEmployeeSearch = new PostEmployeeSearch("50", "0");
        postEmployeeSearch.expectResponseStatus(HttpResponseStatusType.OK_200);
        postEmployeeSearch.addProperty("searchCriteria", searchCriteria);
        postEmployeeSearch.addProperty("searchTerms", searchTerms);
        postEmployeeSearch.addProperty("taxonomiesList", taxonomies);
        List<Map<String, Object>> results = postEmployeeSearch.callAPI().jsonPath().get("employees");
        return results;
    }

    public static String getSuggestedSkills(String searchSuggestion, String taxonomies) {
        GetSearchSuggestionsMethod getSearchSuggestionsMethod = new GetSearchSuggestionsMethod(searchSuggestion, taxonomies);
        getSearchSuggestionsMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        return getSearchSuggestionsMethod.callAPI().jsonPath().prettyPrint();
    }

    public static String postSearchSettings(String key, Object value) {
        PostSearchSettings postSearchSettings = new PostSearchSettings();
        switch (key) {
            case "isHRDataMandatory":
            case "PROFILE_SKILLS_active":
            case "PROFILE_ABOUT_ME_active":
            case "PROFILE_TOPICS_active":
            case "EMAIL_CONTENT_active":
            case "country_active":
            case "state_active":
            case "city_active":
            case "role_active":
            case "dataSource":
            case "freshness":
            case "volume":
            case "relevanceScore":
            case "includedEmailDomains":
            case "excludedEmailDomains":
            case "department_city":
            case "data_role_active":
            case "data_location_active":
            case "freshnessEnabled":
            case "volumeEnabled":
            case "relevanceScoreEnabled":
            case "freshnessBeginDateEnabled":
            case "includedEmailDomainsEnabled":
            case "excludedEmailDomainsEnabled":
            case "useReceivedEmailsContent":
                postSearchSettings.expectResponseStatus(HttpResponseStatusType.OK_200);
                postSearchSettings.addProperty(key, value);
                return postSearchSettings.callAPI().jsonPath().get("message");
            default:
                throw new IllegalArgumentException("Please insert the correct argument!");
        }
    }
    public static String postSearchSettings(){
        PostSearchSettings postSearchSettings = new PostSearchSettings();
        postSearchSettings.expectResponseStatus(HttpResponseStatusType.OK_200);
        return postSearchSettings.callAPI().jsonPath().get("message");
    }

    public static String getIngestionModeStatusResponse() {
        GetIngestionModeStateMethod getIngestionModeStateMethod = new GetIngestionModeStateMethod();
        getIngestionModeStateMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        return getIngestionModeStateMethod.callAPI().jsonPath().get("ingestionMode");
    }

    public static String editTeamTitleAndDescription(String title, String description) {
        PutEditTeamTitleAndDescription putEditTeamTitleAndDescription = new PutEditTeamTitleAndDescription();
        putEditTeamTitleAndDescription.addProperty("teamName", title);
        putEditTeamTitleAndDescription.addProperty("teamDescription", description);
        putEditTeamTitleAndDescription.expectResponseStatus(HttpResponseStatusType.OK_200);
        return putEditTeamTitleAndDescription.callAPI().jsonPath().get("message");
    }

    public static String getDownloadTeamName(){
        GetDownloadTeamMethod getDownloadTeamMethod = new GetDownloadTeamMethod();
        getDownloadTeamMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        return getDownloadTeamMethod.callAPI().jsonPath().get("name");
    }

    public static String getSearchFilters(){
        GetSearchFilters getSearchFilters = new GetSearchFilters();
        getSearchFilters.expectResponseStatus(HttpResponseStatusType.OK_200);
        return getSearchFilters.callAPI().jsonPath().prettyPrint();
    }
    
    public static Map<String, Object>getSearchSettings(){
        GetSearchSettingsMethod getSearchSettingsMethod = new GetSearchSettingsMethod();
        getSearchSettingsMethod.expectResponseStatus(HttpResponseStatusType.OK_200);
        return getSearchSettingsMethod.callAPI().jsonPath().get();
    }
    public static String getProperty(String propertyName) {
        Properties prop = new Properties();
        try {
            FileInputStream config = new FileInputStream(FilePaths.apiFixturesConstsAbsolutPath);
            prop.load(config);
            return prop.getProperty(propertyName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
