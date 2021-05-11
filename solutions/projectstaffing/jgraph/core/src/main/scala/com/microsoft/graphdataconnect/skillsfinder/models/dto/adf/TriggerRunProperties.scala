/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.dto.adf

import java.time.ZonedDateTime

import com.fasterxml.jackson.annotation.JsonFormat

//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy hh:mm:ss")
case class TriggerRunProperties(@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy h:mm:ss a", timezone = "UTC") windowStartTime: ZonedDateTime,
                                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy h:mm:ss a", timezone = "UTC") windowEndTime: ZonedDateTime)
