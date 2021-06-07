package com.microsoft.graphdataconnect.selenium.api.delete;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;

public class DeleteMethod extends RESTMethod {
    public DeleteMethod(String employee_id) {
        super(null, null);
        replaceUrlPlaceholder("employeeId", employee_id);
    }

}
