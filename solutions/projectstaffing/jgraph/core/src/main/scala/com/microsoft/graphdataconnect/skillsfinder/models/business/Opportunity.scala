/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business

case class Opportunity(id: Long,
                       name: String,
                       owner: String,
                       value: Long,
                       details: String)
