/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

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
