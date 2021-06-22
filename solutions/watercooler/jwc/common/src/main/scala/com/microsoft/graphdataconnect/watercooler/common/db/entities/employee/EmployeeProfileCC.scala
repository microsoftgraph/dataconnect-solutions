/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.entities.employee

//Corresponds to the employee_profile table in the DB
case class EmployeeProfileCC(
                              id: String,
                              version: String,
                              mail: String,
                              display_name: String,
                              about_me: String,
                              job_title: String,
                              company_name: String,
                              department: String,
                              office_location: String,
                              city: String,
                              state: String,
                              country: String,
                              skills: String,
                              responsibilities: String,
                              engagement: String,
                              image: String
                            )
