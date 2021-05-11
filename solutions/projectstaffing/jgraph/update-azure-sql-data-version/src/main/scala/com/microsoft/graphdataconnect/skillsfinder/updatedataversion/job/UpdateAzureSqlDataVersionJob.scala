/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.updatedataversion.job

import java.time.LocalDateTime
import java.util.Properties

import com.microsoft.graphdataconnect.jdbc.{DatabaseConnectionPropertiesFactory, JdbcClient}
import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.skillsfinder.updatedataversion.config.ConfigArgs
import com.microsoft.graphdataconnect.utils.{LogAnalyticsUtils, TimeUtils}

object UpdateAzureSqlDataVersionJob {

  var log: GdcLogger = _

  def main(arg: Array[String]): Unit = {

    val config: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg)

    if (config.isDefined) {

      val configParams: ConfigArgs = config.get

      configParams.newAzureSqlDataVersion.getOrElse(throw new IllegalArgumentException("'new-data-version' argument expected"))
      configParams.configurationType.getOrElse(throw new IllegalArgumentException("'configuration-type' argument expected"))
      configParams.directoryId.getOrElse(throw new IllegalArgumentException("'directory-id' argument expected"))
      configParams.applicationId.getOrElse(throw new IllegalArgumentException("'application-id' argument expected"))
      configParams.adbSecretScopeName.getOrElse(throw new IllegalArgumentException("'adb-secret-scope-name' argument expected"))
      configParams.adbSPClientKeySecretName.getOrElse(throw new IllegalArgumentException("'adb-sp-client-key-secret-name' argument expected"))
      configParams.jdbcDatabase.getOrElse(throw new IllegalArgumentException("'jdbc-database' argument expected"))
      configParams.jdbcHostname.getOrElse(throw new IllegalArgumentException("'jdbc-hostname' argument expected"))
      configParams.jdbcPort.getOrElse(throw new IllegalArgumentException("'jdbc-port' argument expected"))
      configParams.keyVaultUrl.getOrElse(throw new IllegalArgumentException("'key-vault-url' argument expected"))
      configParams.logAnalyticsWorkspaceId.getOrElse(throw new IllegalArgumentException("'log-analytics-workspace-id' argument expected"))
      configParams.logAnalyticsWorkspaceKeyName.getOrElse(throw new IllegalArgumentException("'log-analytics-workspace-key-name' argument expected"))
      configParams.useMsiAzureSqlAuth.getOrElse(throw new IllegalArgumentException("'use-msi-azure-sql-auth' argument expected"))

      val (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey) = LogAnalyticsUtils.buildLogAnalyticsWorkspaceIdAndWorkspaceKey(
        workspaceId = configParams.logAnalyticsWorkspaceId,
        workspaceKey = configParams.logAnalyticsWorkspaceKeyName,
        applicationId = configParams.applicationId.get,
        directoryId = configParams.directoryId.get,
        adbSecretScopeName = configParams.adbSecretScopeName.get,
        adbSPClientKeySecretName = configParams.adbSPClientKeySecretName.get,
        keyVaultUrl = configParams.keyVaultUrl.get
      )

      log = GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = logAnalyticsWorkspaceId,
        logAnalyticsSharedKey = logAnalyticsWorkspaceKey,
        clazz = UpdateAzureSqlDataVersionJob.getClass,
        logType = "UpdateAzureSqlDataVersionJob"
      )

      val newAzureSqlDataVersion: LocalDateTime = TimeUtils.timestampToLocalDateTime(configParams.newAzureSqlDataVersion.get)
      val configurationType = configParams.configurationType.get
      val tableName = configParams.tableName.get
      val applicationId = configParams.applicationId.get
      val directoryId = configParams.directoryId.get
      val adbSecretScopeName = configParams.adbSecretScopeName.get
      val adbSPClientKeySecretName = configParams.adbSPClientKeySecretName.get
      val jdbcHostname = configParams.jdbcHostname.get
      val jdbcPort = configParams.jdbcPort.get
      val jdbcDatabase = configParams.jdbcDatabase.get
      val jdbcUsernameKeyName: Option[String] = configParams.jdbcUsernameKeyName
      val jdbcPasswordKeyName: Option[String] = configParams.jdbcPasswordKeyName
      val useMsiAzureSqlAuth = configParams.useMsiAzureSqlAuth.get
      val keyVaultUrl = configParams.keyVaultUrl.get

      val connectionProperties: Properties = DatabaseConnectionPropertiesFactory.getProperties(
        applicationId, directoryId, adbSecretScopeName, adbSPClientKeySecretName,
        jdbcUsernameKeyName, jdbcPasswordKeyName, useMsiAzureSqlAuth, keyVaultUrl)
      val jdbcUrl = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
      val jdbcClient = JdbcClient(connectionProperties, jdbcUrl)(log)

      val versionUpdatedSuccessfully = jdbcClient.updateEmployeeDataVersion(configurationType, newVersion = newAzureSqlDataVersion)
      if (versionUpdatedSuccessfully) {
        log.info("Successfully stored users profile data")
        log.info("Waiting for Core to refresh the data...")
        Thread.sleep(60000)
        log.info("Deleting old records...")
        jdbcClient.deleteOldVersions(tableName, olderThan = newAzureSqlDataVersion)
        log.info("Done")
      } else {
        // throw exception and let ADF to handle it
        throw new Exception(s"Could not set new version [${configParams.newAzureSqlDataVersion.get}] on Employee profile data")
      }

    } else {
      log.error("Invalid command line arguments!")
      throw new IllegalArgumentException("Invalid command line arguments")
    }


  }

}
