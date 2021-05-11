/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.dto.admin

import com.fasterxml.jackson.annotation.JsonProperty

case class Claim(typ: String, @JsonProperty("val") value: String)
