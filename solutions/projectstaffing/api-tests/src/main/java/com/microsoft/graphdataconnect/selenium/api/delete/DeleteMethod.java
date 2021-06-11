/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.selenium.api.delete;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;

public class DeleteMethod extends RESTMethod {
    public DeleteMethod(String employee_id) {
        super(null, null);
        replaceUrlPlaceholder("employeeId", employee_id);
    }

}
