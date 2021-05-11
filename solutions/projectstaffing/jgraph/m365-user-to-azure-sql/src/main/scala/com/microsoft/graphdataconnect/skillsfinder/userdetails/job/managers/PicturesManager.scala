/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.managers

import com.microsoft.graphdataconnect.model.configs.IngestionMode
import com.microsoft.graphdataconnect.model.userdetails.source.gdc.UserDetails
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.helpers.IoUtil
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.service.{GdcILogger, GraphService}

trait PicturesManager {

  def getProfilePicture(userDetails: UserDetails): String

}

class GraphApiPicturesManager(graphService: GraphService, backupPicturesManager: Option[PicturesManager] = None) extends PicturesManager {

  override def getProfilePicture(userDetails: UserDetails): String = {
    val graphApiProfilePicture = IoUtil.encodePhoto(graphService.readUserProfilePicture(userDetails.mail))
    if ((graphApiProfilePicture == null || graphApiProfilePicture.isEmpty) && backupPicturesManager.isDefined) {
      backupPicturesManager.get.getProfilePicture(userDetails)
    } else {
      graphApiProfilePicture
    }
  }

}

class NoImplementation() extends PicturesManager {
  override def getProfilePicture(userDetails: UserDetails): String = ""
}

class AvatarGenerator() extends PicturesManager {
  override def getProfilePicture(userDetails: UserDetails): String = ""
}

class SimulatedPicturesManager() extends PicturesManager {
  override def getProfilePicture(userDetails: UserDetails): String = userDetails.picture
}

object PicturesManager {

  def apply(graphService: GraphService, logger: GdcILogger, ingestionModeStr: String): PicturesManager = {
    logger.gdcLogger.info(s"Initializing PicturesManager for ingestion mode: ${ingestionModeStr}")

    // passed as string to avoid serialization issues
    val ingestionMode: IngestionMode.Value = IngestionMode.withNameWithDefault(ingestionModeStr)
    ingestionMode match {
      case IngestionMode.Production => new GraphApiPicturesManager(graphService)
      case IngestionMode.Sample => new GraphApiPicturesManager(graphService, Option(new SimulatedPicturesManager()))
      case IngestionMode.Simulated => new SimulatedPicturesManager()
      case _ => new NoImplementation()
    }
  }


}
