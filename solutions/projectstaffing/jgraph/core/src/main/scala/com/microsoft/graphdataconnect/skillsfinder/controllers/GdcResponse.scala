package com.microsoft.graphdataconnect.skillsfinder.controllers

case class GdcResponse(timestamp: Long,
                       status: Int,
                       error: String,
                       message: String,
                       path: String,
                       correlationId: String)
