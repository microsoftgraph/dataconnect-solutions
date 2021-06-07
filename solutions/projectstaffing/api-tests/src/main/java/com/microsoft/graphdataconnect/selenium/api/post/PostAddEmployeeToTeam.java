package com.microsoft.graphdataconnect.selenium.api.post;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;
import com.microsoft.graphdataconnect.selenium.api.support.FilePaths;

public class PostAddEmployeeToTeam extends RESTMethod {
    public PostAddEmployeeToTeam() {
        super(FilePaths.postAddMemberRequest, null);
    }
}
