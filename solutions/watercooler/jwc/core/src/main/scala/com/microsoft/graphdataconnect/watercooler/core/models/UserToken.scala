/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.models

case class UserToken(var accessToken: String,
                     var refreshToken: String,
                     var tokenScope: TokenScope)
