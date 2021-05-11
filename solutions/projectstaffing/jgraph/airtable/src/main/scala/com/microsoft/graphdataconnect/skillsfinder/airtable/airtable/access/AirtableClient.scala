/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.access

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.api.{AirtableRecord, AirtableRecords, Identity}
import com.microsoft.graphdataconnect.skillsfinder.airtable.config.GDCConfiguration
import kong.unirest.{GenericType, HttpStatus, JsonObjectMapper, Unirest}

import scala.collection.JavaConverters._


class AirtableClient[T <: Identity](baseId: String, apiKey: String, genericType: GenericType[AirtableRecords[T]])
                                   (implicit configuration: GDCConfiguration) {
  private val log: GdcLogger = configuration.getLogger(classOf[AirtableClient[T]])

  def getAirtableRecords(tableName: String, isMandatory: Boolean = true): Seq[T] = {
    var airtableRecords: Seq[T] = Seq()
    val authorizationHeaderKey = "Bearer " + apiKey
    val authorizationHeader = Map("Authorization" -> authorizationHeaderKey)

    log.info(s"Reading data from the Airtable '$tableName' table")
    try {
      var page = getPage(authorizationHeader, Map(), tableName)
      airtableRecords = airtableRecords ++ page._1

      while (page._2 != null) {
        val queryParams = Map("offset" -> page._2)
        page = getPage(authorizationHeader, queryParams, tableName)
        airtableRecords = airtableRecords ++ page._1
      }

      log.info(s"Successfully read data from the Airtable '$tableName' table")
      airtableRecords
    } catch {
      case e: Throwable =>
        if (isMandatory || configuration.failFastOnCorruptData) {
          log.error(s"Failed reading data from the Airtable '$tableName' table")
          throw e
        } else {
          if (airtableRecords.isEmpty)
            log.error(s"Failed reading data from the non-critical Airtable '$tableName' table. Returning empty data and ignoring underlying exception:", e)
          else
            log.error(s"Failed while reading data from the non-critical Airtable '$tableName' table. Returning partial data consisting of ${airtableRecords.size} records and ignoring underlying exception:", e)

          airtableRecords
        }
    }


  }

  private def getPage(headers: Map[String, String], queryParams: Map[String, Object], tableName: String): (Seq[T], String) = {
    val url = "https://api.airtable.com/v0/" + baseId + "/" + tableName
    val response = Unirest.get(url)
      .headers(headers.asJava)
      .queryString(queryParams.asJava)
      .asString()

    if (response.getStatus == HttpStatus.OK) {
      val body = response.getBody

      val objectMapper = new JsonObjectMapper();
      val airtableRecords = objectMapper.readValue(body, genericType)

      if (airtableRecords != null && airtableRecords.records != null) {
        val employees: Seq[T] = airtableRecords.records
          .map((record: AirtableRecord[T]) => {
            record.fields.id = record.id
            record.fields
          })
        (employees, airtableRecords.offset)
      } else
        (Seq[T](), null)
    } else {
      log.error(s"Call to Airtable API using URL $url and query params ${queryParams.asJava} failed with status code ${response.getStatus} and message ${response.getBody}")
      throw new RuntimeException(s"Error response from Airtable REST API: ${response.getBody}")
    }

  }

}

object AirtableClient {
  def apply[T <: Identity](baseId: String, apiKey: String, genericType: GenericType[AirtableRecords[T]])
                          (implicit configuration: GDCConfiguration): AirtableClient[T] = {
    new AirtableClient[T](baseId, apiKey, genericType)
  }

}
