/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.microsoft.graphdataconnect.skillsfinder.models.TokenScope

case class UserToken(var accessToken: String,
                     var refreshToken: String,
                     var tokenScope: TokenScope)
