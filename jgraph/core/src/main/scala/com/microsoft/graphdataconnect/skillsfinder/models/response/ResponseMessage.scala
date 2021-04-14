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
