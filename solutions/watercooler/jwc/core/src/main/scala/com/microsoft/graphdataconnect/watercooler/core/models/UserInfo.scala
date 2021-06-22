/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.models

import com.fasterxml.jackson.annotation.JsonProperty

case class UserInfo(@JsonProperty("access_token") accessToken: String,
                    @JsonProperty("id_token") idToken: String,
                    @JsonProperty("refresh_token") refreshToken: String,
                    @JsonProperty("user_id") userId: String)
