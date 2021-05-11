/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.dto.adf

import java.time.ZonedDateTime

import com.fasterxml.jackson.annotation.JsonFormat

case class TriggerRun(@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.n'Z'", timezone = "UTC") triggerRunTimestamp: ZonedDateTime,
                      triggerName: String,
                      triggerRunId: String,
                      status: String,
                      properties: TriggerRunProperties,
                      triggeredPipelines: Map[String, String])
