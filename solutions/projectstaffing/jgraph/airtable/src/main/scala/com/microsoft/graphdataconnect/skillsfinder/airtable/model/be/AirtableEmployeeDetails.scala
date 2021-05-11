/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.model.be

// Class meant to reflect structure of simulated Airtable data,
// as well as the intermediate internal structure from which the HR Data is built
case class AirtableEmployeeDetails(mail: String,
                                   name: String,
                                   locate: String,
                                   up_for_redeployment_date: String,
                                   current_engagement: String,
                                   reports_to: String,
                                   manager_email: String,
                                   role: String,
                                   consultant_type: String,
                                   linkedin_profile: String)
