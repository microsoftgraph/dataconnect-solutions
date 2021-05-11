/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.userdetails.source.gdc

case class UserDetails(id: String,
                       mail: String,
                       displayName: String,
                       aboutMe: String,
                       jobTitle: String,
                       companyName: String,
                       department: String,
                       officeLocation: String,
                       city: String,
                       state: String,
                       country: String,
                       skills: Seq[String],
                       responsibilities: Seq[String],
                       pastProjects: Seq[String],
                       schools: Seq[String],
                       interests: Seq[String],
                       picture: String
                      )
