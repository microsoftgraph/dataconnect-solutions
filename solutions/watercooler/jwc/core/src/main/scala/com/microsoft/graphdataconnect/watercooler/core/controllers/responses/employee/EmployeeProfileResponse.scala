/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.employee

case class EmployeeProfileResponse(id: String,
                                   mail: String,
                                   displayName: String,
                                   aboutMe: String,
                                   jobTitle: String,
                                   companyName: String,
                                   department: String,
                                   city: String,
                                   state: String,
                                   country: String,
                                   skills: String,
                                   responsibilities: String,
                                   engagement: String,
                                   image: String)
