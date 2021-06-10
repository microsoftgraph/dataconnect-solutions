/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.selenium.api.get;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;
import com.microsoft.graphdataconnect.selenium.api.support.FilePaths;

public class GetIngestionModeStateMethod extends RESTMethod {
    public GetIngestionModeStateMethod() {
        super(null, FilePaths.getIngestionModeStateResponse);
    }
}
