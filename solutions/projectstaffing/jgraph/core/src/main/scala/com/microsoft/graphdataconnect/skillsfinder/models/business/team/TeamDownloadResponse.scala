/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.team

case class TeamDownloadResponse(
                                 ownerMail: String,
                                 name: String,
                                 description: String,
                                 members: Seq[TeamDownloadMemberResponse]
                               )
