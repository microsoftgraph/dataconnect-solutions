/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.services.meta

import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.{HttpResponse, Unirest}
import com.microsoft.graphdataconnect.watercooler.common.exceptions.UnauthorizedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

@Service
class UserService {

  private val objectMapper: ObjectMapper = new ObjectMapper()

  @Value("${jwc.appservice.url:}")
  var jgraphUrl: String = _

  def getUserId(cookieValue: String): String = {
    Try {
      Unirest.get(jgraphUrl + "/.auth/me").headers(Map("Cookie" -> ("AppServiceAuthSession=" + cookieValue)).asJava).asString()
    } match {
      case Success(response: HttpResponse[String]) =>
        val jsonResponse = response.getBody
        Try {
          objectMapper.readValue(jsonResponse, classOf[Array[java.util.HashMap[String, Any]]])
        } match {
          case Success(result) =>
            if (result == null || result.isEmpty) {
              throw new UnauthorizedException("Authentication failed!")
            } else {
              val userId: String = result(0).get("user_id").toString
              userId
            }
          case Failure(_) => throw new UnauthorizedException("Authentication failed!")
        }
    }
  }

}
