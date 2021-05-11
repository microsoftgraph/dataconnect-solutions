/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.userdetails

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.security.ADLAccessToken
import com.microsoft.graphdataconnect.servcies.ADLoginService
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.M365UserToAzureSqlJob
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.config.{ConfigArgs, GDCConfiguration}
import com.microsoft.graphdataconnect.skillsfinder.userdetails.job.fs.{FileSystemInitializer, SparkInitializer}
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object Runner {

  var log: GdcLogger = _

  def main(arg: Array[String]): Unit = {
    val config: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg)

    if (config.isDefined) {
      implicit val configParams: ConfigArgs = config.get
      implicit val configuration: GDCConfiguration = GDCConfiguration(configParams)
      log = configuration.getLogger(Runner.getClass)

      try {
        implicit val sparkSession: SparkSession = SparkInitializer().session
        implicit val sparkContext: SparkContext = sparkSession.sparkContext
        configuration.setSparkSettings(sparkContext)

        implicit val fileSystem: FileSystem = FileSystemInitializer(configuration.getBlobConnectionString).fileSystem

        //If there is a specific service principal for graph API, we're using the "graph" specific parameters:
        // graphAppTenantId, graphAppClientId, graphAppSecretScope & graphAppSecretKey to get generate the Graph Api access token.
        val graphApiAccessToken: ADLAccessToken = if (configParams.graphApiTenantId.isDefined && configParams.graphApiClientId.isDefined &&
          configParams.graphApiSecretScope.isDefined && configParams.graphApiSPClientKeySecretName.isDefined) {
          ADLoginService(configParams.graphApiTenantId.get).loginForAccessToken(clientId = configParams.graphApiClientId.get,
            secretScope = configParams.graphApiSecretScope.get,
            secretKey = configParams.graphApiSPClientKeySecretName.get,
            scope = "https://graph.microsoft.com/")
        }
        //  If not, we'll use the same service principal & parameters we use for blob storage and azure sql to generate the access token for Graph Api:
        //  applicationId, directoryId, secretScopeName & secretKeyName.
        else if (configParams.applicationId.isDefined && configParams.directoryId.isDefined && configParams.adbSecretScopeName.isDefined
          && configParams.adbSPClientKeySecretName.isDefined) {
          ADLoginService(configParams.directoryId.get).loginForAccessToken(clientId = configParams.applicationId.get,
            secretScope = configParams.adbSecretScopeName.get,
            secretKey = configParams.adbSPClientKeySecretName.get,
            scope = "https://graph.microsoft.com/")
        } else {
          throw new IllegalArgumentException("Invalid command line arguments. Missing directoryId/tenantId, applicationId/clientId, secretScopeName & secretKeyName. " +
            "Provide the graph app parameter versions or the non graph. They are needed for retrieving employee photos. ")
        }

        val m365UserToAzureSqlJob: M365UserToAzureSqlJob = new M365UserToAzureSqlJob()
        if (configParams.jdbcHostname.isDefined && configParams.jdbcPort.isDefined && configParams.jdbcDatabase.isDefined) {

          m365UserToAzureSqlJob.start(
            jdbcHostname = configParams.jdbcHostname.get,
            jdbcPort = configParams.jdbcPort.get,
            jdbcDatabase = configParams.jdbcDatabase.get,
            maxDbConnections = configParams.maxDbConnections.getOrElse(8),
            graphApiAccessToken = graphApiAccessToken.accessToken,
            ingestionMode = configParams.ingestionMode.toString) // pass as string to avoid serialization issues
        }

      } catch {
        case e: Throwable => log.error("Exception while persisting employee profiles", e)
          throw e
      }

    } else {
      log.error("Invalid command line arguments!")
      throw new IllegalArgumentException("Invalid command line arguments")
    }

  }

}



