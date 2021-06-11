/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.selenium.api;

import com.microsoft.graphdataconnect.selenium.api.support.FilePaths;
import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RESTMethod extends AbstractApiMethodV2 {
    public RESTMethod(String rqPath, String rsPath) {
        super(rqPath, rsPath, FilePaths.apiFixturesConsts);

        Properties prop = new Properties();
        try {
            FileInputStream config = new FileInputStream(FilePaths.resourcesConstProperties);
            prop.load(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        replaceUrlPlaceholder("base_url", System.getProperty("AppServiceUrl"));
        addCookie("AppServiceAuthSession",System.getProperty("AppServiceAuthSession"));
        replaceUrlPlaceholder("config_url",prop.getProperty("config_url"));
        replaceUrlPlaceholder("team",prop.getProperty("team_url"));
        replaceUrlPlaceholder("employee_search",prop.getProperty("employee_search"));
    }
}