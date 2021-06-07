package com.microsoft.graphdataconnect.selenium.api.get;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;
import com.microsoft.graphdataconnect.selenium.api.support.FilePaths;

public class GetIngestionModeStateMethod extends RESTMethod {
    public GetIngestionModeStateMethod() {
        super(null, FilePaths.getIngestionModeStateResponse);
    }
}
