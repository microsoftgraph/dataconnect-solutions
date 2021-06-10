/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.selenium.api.get;

import com.microsoft.graphdataconnect.selenium.api.RESTMethod;

public class GetSearchSuggestionsMethod extends RESTMethod {
    public GetSearchSuggestionsMethod(String searchSuggestion,  String taxonomiesList) {
        super(null,null);
//        String taxonomies="";
//        for(int i=0;i< taxonomiesList.size()-1;i++){
//            taxonomies+=taxonomiesList.get(i)+",";
//        }
//        taxonomies+= taxonomiesList.get(taxonomiesList.size()-1);
        replaceUrlPlaceholder("searchSuggestion",searchSuggestion);
        replaceUrlPlaceholder("taxonomiesList",taxonomiesList);
    }
}
