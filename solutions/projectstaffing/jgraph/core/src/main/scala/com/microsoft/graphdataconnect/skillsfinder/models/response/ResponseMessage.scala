/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.response

import java.time.Instant

import com.fasterxml.jackson.annotation.JsonFormat

case class ResponseMessage(
                            message: String,
                            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
                            serverTime: Instant
                          )

object ResponseMessage {
  def apply(message: String): ResponseMessage = {
    ResponseMessage(message, Instant.now())
  }
}
