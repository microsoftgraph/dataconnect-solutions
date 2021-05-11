/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.userdetails.source.hrdata

case class HRDataEmployeeDetails(mail: String,
                                 name: String,
                                 availableStartingFrom: String,
                                 role: String,
                                 employeeType: String,
                                 currentEngagement: String,
                                 department: String,
                                 companyName: String,
                                 managerName: String,
                                 managerEmail: String,
                                 country: String,
                                 state: String,
                                 city: String,
                                 location: String,
                                 officeLocation: String,
                                 linkedInProfile: String)
