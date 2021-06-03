package com.microsoft.graphdataconnect.selenium.api.support;

public class FilePaths {
    public static String apiFixturesGet = "api/fixtures/_get/";
    public static String apiFixturesPost = "api/fixtures/_post/";
    public static String apiFixturesDelete = "api/fixtures/_delete/";
    public static String apiFixturesPut = "api/fixtures/_put/";
    public static String apiFixturesConsts = "api/fixtures/_consts.properties";

    public static String resourcesConstProperties = "src/main/resources/_config.properties";

    public static String apiFixturesGetAbsolutPath = "src/main/resources/" + apiFixturesGet;
    public static String apiFixturesPostAbsolutPath = "src/main/resources/" + apiFixturesPost;
    public static String apiFixturesDeleteAbsolutPath = "src/main/resources/" + apiFixturesDelete;
    public static String apiFixturesConstsAbsolutPath = "src/main/resources/" + apiFixturesConsts;

    //---------------------------------- POST ----------------------------------
    public static String postAddMemberRequest = apiFixturesPost + "postAddEmployeeToTeamRequest.json";
    public static String postSearchRequest = apiFixturesPost + "postSearchRequest.json";
    public static String postSearchSettingsRequest = apiFixturesPost + "postSearchSettingsRequest.json";
    public static String getPostAddMemberResponse = apiFixturesPost + "getPostAddMemberResponse.json";
    public static String postSearchSettingsRequestAbs = apiFixturesPostAbsolutPath + "postSearchSettingsRequest.json";
    public static String getPostSearchRequestAbs = apiFixturesPostAbsolutPath + "postSearchRequest.json";
    public static String postAddMemberRequestAbs = apiFixturesPostAbsolutPath + "postAddEmployeeToTeamRequest.json";

    //-----------------------------------GET--------------------------------------
    public static String getIngestionModeStateResponse = apiFixturesGetAbsolutPath + "GetIngestionModeStateResponse.json";

    //-----------------------------------PUT--------------------------------------
    public static String putEditTeamTitleAndDescription = apiFixturesPut + "putEditTeamTitleAndDescriptionRequest.json";
}
