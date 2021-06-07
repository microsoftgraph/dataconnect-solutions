package com.microsoft.graphdataconnect.selenium.api.post;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;
import com.microsoft.graphdataconnect.selenium.api.support.FilePaths;

public class PostSearchSettings extends RESTMethod {
    public PostSearchSettings() {
        super(FilePaths.postSearchSettingsRequest, null);
       
    }
}
