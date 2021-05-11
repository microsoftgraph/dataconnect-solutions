/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.team

case class TeamDownloadMemberResponse(
                                       name: String,
                                       mail: String,
                                       about: String,
                                       declaredSkills: Seq[String],
                                       reportsTo: String,
                                       availableSince: String,
                                       currentEngagement: String,
                                       role: String,
                                       location: String
                                     )
