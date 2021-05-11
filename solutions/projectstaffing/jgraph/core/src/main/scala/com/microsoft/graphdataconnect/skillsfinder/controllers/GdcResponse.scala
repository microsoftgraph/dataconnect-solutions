/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.controllers

case class GdcResponse(timestamp: Long,
                       status: Int,
                       error: String,
                       message: String,
                       path: String,
                       correlationId: String)
