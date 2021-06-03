package com.microsoft.graphdataconnect.selenium.api.post;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;
import com.microsoft.graphdataconnect.selenium.api.support.FilePaths;

public class PostEmployeeSearch extends RESTMethod {
    public PostEmployeeSearch(String searchSize, String offset) {
        super(FilePaths.postSearchRequest, null);
        replaceUrlPlaceholder("searchSize",searchSize);
        replaceUrlPlaceholder("offset",offset);
    }
}
