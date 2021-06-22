/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.profiles.extractor

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.watercooler.common.db.JdbcClient
import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLogger
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.profiles.extractor.config.JwcConfiguration
import com.microsoft.graphdataconnect.watercooler.profiles.extractor.fs.SparkInitializer
import com.microsoft.graphdataconnect.watercooler.profiles.extractor.jobs.ProfilesExporterWrapper
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.codehaus.jackson.map.ObjectMapper


object Runner {

  implicit var log: JwcLogger = _

  def main(arg: Array[String]): Unit = {
    implicit val configuration: JwcConfiguration = JwcConfiguration(arg)

    log = configuration.getLogger(Runner.getClass)

    implicit val objectMapper: ObjectMapper = new ObjectMapper();

    val graphApiAppAccessInfo: GraphApiAppAccessInfo = configuration.getGraphApiAppAccessInfo()

    implicit val sparkSession: SparkSession = SparkInitializer().session
    implicit val sc: SparkContext = sparkSession.sparkContext

    implicit val newEmployeeProfileVersion: LocalDateTime = LocalDateTime.now()

    val dbProperties = configuration.getDatabaseConnectionProperties()
    val jdbcUrl: String = configuration.getJdbcUrl();
    val jdbcClient: JdbcClient = JdbcClient(jdbcUrl, dbProperties)(log);

    ProfilesExporterWrapper(jdbcClient, jdbcUrl, dbProperties, configuration.getMaxDbConnections()).start(graphApiAppAccessInfo)

  }

}
