/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator

import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLogger
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.events.creator.config.{JwcConfiguration, SendMailConfig}
import com.microsoft.graphdataconnect.watercooler.events.creator.fs.SparkInitializer
import com.microsoft.graphdataconnect.watercooler.events.creator.management.GroupEventsProcessor
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession


object Runner {

  implicit var log: JwcLogger = _

  def main(args: Array[String]): Unit = {
    implicit val configuration: JwcConfiguration = JwcConfiguration(args)
    log = configuration.getLogger(Runner.getClass)

    implicit val graphApiAppAccessInfo: GraphApiAppAccessInfo = configuration.getGraphApiAppAccessInfo()
    val meetingOrganizerEmail: String = configuration.getMeetingOrganizerEmail()

    implicit val sparkSession: SparkSession = SparkInitializer().session
    implicit val sc: SparkContext = sparkSession.sparkContext

    implicit val sendMailConfig: SendMailConfig = SendMailConfig(configuration.getMeetingDuration())

    val dbProperties = configuration.getDatabaseConnectionProperties()
    val jdbcUrl: String = configuration.getJdbcUrl();

    GroupEventsProcessor(jdbcUrl, dbProperties).start(configuration.getStartDate(), configuration.getEndDate(), meetingOrganizerEmail)

  }
}
