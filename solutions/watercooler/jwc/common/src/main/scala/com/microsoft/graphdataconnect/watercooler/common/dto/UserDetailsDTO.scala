/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.dto

case class UserDetailsDTO(
                           displayName: String,
                           aboutMe: String,
                           jobTitle: String,
                           companyName: String,
                           department: String,
                           country: String,
                           officeLocation: String,
                           city: String,
                           state: String,
                           skills: String,
                           responsibilities: String,
                           engagement: String
                         )
