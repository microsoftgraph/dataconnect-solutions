/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.service

import java.io.BufferedInputStream

import com.microsoft.graph.authentication.IAuthenticationProvider
import com.microsoft.graph.http.IHttpRequest
import com.microsoft.graph.logger.LoggerLevel
import com.microsoft.graph.models.extensions._
import com.microsoft.graph.requests.extensions.GraphServiceClient
import com.microsoft.graphdataconnect.logging.GdcLogger

class GraphService(graphClient: IGraphServiceClient, log: GdcLogger) {
  // According to https://docs.microsoft.com/en-us/graph/api/profilephoto-get?view=graph-rest-1.0
  // "The supported sizes of HD photos on Microsoft 365 are as follows: 48x48, 64x64, 96x96, 120x120, 240x240, 360x360,
  // 432x432, 504x504, and 648x648. Photos can be any dimension if they are stored in Azure Active Directory."
  private val DEFAULT_MAX_PICTURE_SIZE: String = "120x120"

  def readUserProfilePicture(mail: String): Option[Array[Byte]] = {
    val sizedProfilePicture: Option[Array[Byte]] = getSizedProfilePicture(mail)
    if (sizedProfilePicture.isDefined) {
      sizedProfilePicture
    } else {
      getDefaultSizedProfilePicture(mail)
    }
  }

  private def getDefaultSizedProfilePicture(mail: String): Option[Array[Byte]] = {
    try {
      val stream = graphClient.customRequest("/users/" + mail + "/photo/$value", classOf[BufferedInputStream]).buildRequest.get
      Some(Stream.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray)
    } catch {
      case e: Exception =>
        log.error(s"Cannot read default profile picture for user with mail: $mail.", e)
        None
    }
  }

  private def getSizedProfilePicture(mail: String, profilePictureSize: String = DEFAULT_MAX_PICTURE_SIZE): Option[Array[Byte]] = {
    try {
      val stream = graphClient.customRequest(s"/users/$mail/photos/$profilePictureSize/" + "$value", classOf[BufferedInputStream]).buildRequest.get
      Some(Stream.continually(stream.read).takeWhile(_ != -1).map(_.toByte).toArray)
    } catch {
      case e: Exception =>
        log.error(s"Cannot read $profilePictureSize profile picture for user with mail: $mail. Trying to get the default picture", e)
        None
    }
  }


}

object GraphService {

  def apply(accessToken: String, iLogger: GdcILogger): GraphService = new GraphService(initGraphClient(accessToken, iLogger), iLogger.gdcLogger)

  private def initGraphClient(accessToken: String, iLogger: GdcILogger): IGraphServiceClient = {
    val authProvider = new SimpleAuthProvider(accessToken)
    iLogger.setLoggingLevel(LoggerLevel.ERROR)
    // Build a Graph client
    GraphServiceClient.builder.authenticationProvider(authProvider).logger(iLogger).buildClient
  }

  private class SimpleAuthProvider(val accessToken: String) extends IAuthenticationProvider {
    override def authenticateRequest(request: IHttpRequest): Unit = {
      // Add the access token in the Authorization header
      request.addHeader("Authorization", "Bearer " + accessToken)
    }
  }

}