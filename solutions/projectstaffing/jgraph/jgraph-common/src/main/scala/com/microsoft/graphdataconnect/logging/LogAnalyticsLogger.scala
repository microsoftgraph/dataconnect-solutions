/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.logging

import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.{Base64, Calendar, TimeZone}

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kong.unirest.{HttpStatus, Unirest}
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class LogAnalyticsLogger(workspaceId: String = "",
                         //Use either the primary or the secondary client authentication key
                         sharedKey: String = "",
                         clazz: Class[_],
                         logType: String = "GDC_Pipelines",
                         logServerTime: Boolean = true) extends GdcLogger {

  //The log type is the name of the event that is being submitted
  val RFC_1123_DATE = "EEE, dd MMM yyyy HH:mm:ss z"
  val LOG_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss"
  val INFO_LEVEL = "Informational"
  val ERROR_LEVEL = "ERROR"
  val DEBUG_LEVEL = "DEBUG"
  val log: Logger = LoggerFactory.getLogger(clazz)

  def debug(message: String): Unit = {
    log.debug(message)
    sendMessageToLogAnalytics(message, DEBUG_LEVEL)
  }

  def info(message: String): Unit = {
    log.info(message)
    sendMessageToLogAnalytics(message, INFO_LEVEL)
  }

  def error(message: String): Unit = {
    log.error(message)
    sendMessageToLogAnalytics(message, ERROR_LEVEL)
  }

  def error(message: String, e: Throwable): Unit = {
    log.error(message, e)
    // Currently LogAnalytics logs containing multiple lines into separate entries. As such, we replace new lines with tabs
    val completeMessage = s"$message  ${ExceptionUtils.getStackTrace(e).replace("\n", "\t")}"
    sendMessageToLogAnalytics(completeMessage, ERROR_LEVEL)
  }

  private def sendMessageToLogAnalytics(message: String, level: String): Unit = {
    val logDateTime = getServerTime(LOG_TIME_FORMAT)
    var completeMessage = s"""[${clazz.getSimpleName}]  $message"""
    if (logServerTime) {
      completeMessage = logDateTime + " " + completeMessage
    }
    val dateString = getServerTime(RFC_1123_DATE)
    val httpMethod = "POST"
    val contentType = "application/json"
    val xmsDate = "x-ms-date:" + dateString
    val resource = "/api/logs"
    val json = s"""{"Message": "$completeMessage", "Level": "$level"}"""
    val stringToHash = String.join("\n", httpMethod, String.valueOf(json.getBytes(StandardCharsets.UTF_8).length), contentType,
      xmsDate, resource)
    val hashedString = getHMAC254(stringToHash, sharedKey)
    val signature = "SharedKey " + workspaceId + ":" + hashedString
    postData(signature, dateString, json)
  }

  private def getServerTime(format: String): String = {
    val calendar: Calendar = Calendar.getInstance()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat(format)
    //Time zone must be GMT otherwise a 403 status will be received from Log Analytics
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
    dateFormat.format(calendar.getTime())
  }

  private def postData(signature: String, dateString: String, json: String): Unit = {
    val url = "https://" + workspaceId + ".ods.opinsights.azure.com/api/logs?api-version=2016-04-01"
    val headers = Map("Content-Type" -> "application/json",
      "Authorization" -> signature,
      "Log-Type" -> logType,
      "x-ms-date" -> dateString,
      // If the time field is not specified Azure Monitor assumes the time is the message ingestion time
      "time-generated-field" -> "")
    Try {
      Unirest.post(url).headers(headers.asJava).body(json).asJson()
    } match {
      case Success(response) =>
        if (response.getStatus == HttpStatus.OK) {
          log.trace("Log sent to Log Analytics")
        } else {
          log.error("Failed to send log to Log Analytics. Status: " + response.getStatus + " Body: " + response.getBody)
        }
      case Failure(exception) =>
        log.error("Failed to send log to Log Analytics. Exception: " + exception)
    }
  }

  private def getHMAC254(input: String, key: String): String = {
    val sha254HMAC = Mac.getInstance("HmacSHA256")
    val decoder = Base64.getDecoder()
    val secretKey = new SecretKeySpec(decoder.decode(key.getBytes(StandardCharsets.UTF_8)), "HmacSHA256");
    sha254HMAC.init(secretKey)
    val encoder = Base64.getEncoder
    val hash = new String(encoder.encode(sha254HMAC.doFinal(input.getBytes(StandardCharsets.UTF_8))))
    hash
  }

}

