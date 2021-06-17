/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */


package com.microsoft.graphdataconnect.watercooler.profiles.extractor.config

import java.util.Properties

import com.microsoft.graphdataconnect.watercooler.common.logging.JwcLoggerFactory
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.microsoft.graphdataconnect.watercooler.common.db.DatabaseConnectionPropertiesFactory
import com.microsoft.graphdataconnect.watercooler.common.logging.{JwcLogger, JwcLoggerFactory}
import com.microsoft.graphdataconnect.watercooler.common.login.GraphApiAppAccessInfo
import com.microsoft.graphdataconnect.watercooler.common.util.LogAnalyticsUtils
import pureconfig.ConfigSource

import scala.util.Try


/*
 * hardcoded values
 */
abstract class JwcConfiguration {

  def getDatabaseConnectionProperties(): Properties

  def getLogger(clazz: Class[_]): JwcLogger

  def getLoggerWorkspaceId(): String

  def getLogType(): String

  def getLoggerWorkspaceKey(): String

  def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo

  def getJdbcUrl(): String

  def getMaxDbConnections(): Int
}

class LocalConfiguration(jdbcHostname: String,
                         jdbcPort: String,
                         jdbcDatabase: String,
                         jdbcUsername: String,
                         jdbcPassword: String,
                         applicationId: String,
                         applicationSecret: String,
                         directoryId: String,
                         maxDbConnections: String) extends JwcConfiguration {

  override def getDatabaseConnectionProperties(): Properties = {
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    connectionProperties
  }

  override def getLogger(clazz: Class[_]): JwcLogger = JwcLoggerFactory.getLogger(clazz = clazz)

  override def getLoggerWorkspaceId(): String = ""

  override def getLogType(): String = ""

  override def getLoggerWorkspaceKey(): String = ""

  override def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo = GraphApiAppAccessInfo(applicationId, applicationSecret, directoryId)

  override def getJdbcUrl(): String = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase"

  override def getMaxDbConnections(): Int = Try(maxDbConnections.toInt).getOrElse(2)
}

class DatabricksConfiguration(applicationId: String,
                              directoryId: String,
                              secretScope: String,
                              adbSPClientKeySecretName: String, //storage-account-secret
                              jdbcUsername: String,
                              jdbcPassword: String,
                              jdbcHostname: String,
                              jdbcPort: String,
                              jdbcDatabase: String,
                              maxDbConnections: String,
                              useMsiAzureSqlAuth: String,
                              keyVaultUrl: String,
                              workspaceKey: String,
                              workspaceId: String) extends JwcConfiguration {

  val logAnalyticsLogType = "ProfilesExtractor"

  // TODO
  override def getDatabaseConnectionProperties(): Properties = {
    DatabaseConnectionPropertiesFactory.getProperties(
      applicationId, directoryId, secretScope, adbSPClientKeySecretName,
      jdbcUsername, jdbcPassword, "true".equals(useMsiAzureSqlAuth.toLowerCase().trim), keyVaultUrl)
  }

  override def getLogger(clazz: Class[_]): JwcLogger = JwcLoggerFactory.getLogger(logAnalyticsWorkspaceId = workspaceId,
    logAnalyticsSharedKey = workspaceKey,
    clazz = clazz,
    logType = logAnalyticsLogType
  )

  override def getLoggerWorkspaceId(): String = workspaceKey

  override def getLogType(): String = logAnalyticsLogType

  override def getLoggerWorkspaceKey(): String = workspaceKey

  override def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo = GraphApiAppAccessInfo(applicationId, dbutils.secrets.get(scope = secretScope, key = adbSPClientKeySecretName), directoryId)

  override def getJdbcUrl(): String = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

  override def getMaxDbConnections(): Int = Try(maxDbConnections.toInt).getOrElse(2)

}

class LocalAzureConfiguration(applicationId: String,
                              applicationSecret: String,
                              directoryId: String,
                              jdbcHostname: String,
                              jdbcPort: String,
                              jdbcDatabase: String,
                              jdbcUsername: String,
                              jdbcPassword: String,
                              maxDbConnections: String
                            ) extends JwcConfiguration {

  val logAnalyticsLogType = "MailsExtractor"

  override def getLogger(clazz: Class[_]): JwcLogger = JwcLoggerFactory.getLogger(clazz = clazz)

  override def getLoggerWorkspaceId(): String = ""

  override def getLogType(): String = ""

  override def getLoggerWorkspaceKey(): String = ""

  override def getDatabaseConnectionProperties(): Properties = {
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    connectionProperties
  }

  override def getGraphApiAppAccessInfo(): GraphApiAppAccessInfo = GraphApiAppAccessInfo(applicationId, applicationSecret, directoryId)

  override def getJdbcUrl(): String = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

  override def getMaxDbConnections(): Int = Try(maxDbConnections.toInt).getOrElse(2)

}

object JwcConfiguration {

  def apply(arg: Array[String]): JwcConfiguration = {
    if (arg.contains("--dev")) {
      import pureconfig.generic.auto._  // needed for the implicits
      val configs: ConfigFile = ConfigSource.resources("local-dev.conf").loadOrThrow[ConfigFile]

      new LocalAzureConfiguration(
        configs.applicationId,
        configs.applicationSecret,
        configs.directoryId,
        configs.jdbcHostname,
        configs.jdbcPort,
        configs.jdbcDatabase,
        configs.jdbcUsername,
        configs.jdbcPassword,
        configs.maxDbConnections
      )

    } else if(arg.contains("--local")) {
      import pureconfig.generic.auto._  // needed for the implicits
      val configs: ConfigFile = ConfigSource.resources("local-dev.conf").loadOrThrow[ConfigFile]

      new LocalConfiguration(
        configs.jdbcHostname,
        configs.jdbcPort,
        configs.jdbcDatabase,
        configs.jdbcUsername,
        configs.jdbcPassword,
        configs.applicationId,
        configs.applicationSecret,
        configs.directoryId,
        configs.maxDbConnections
      )

    } else {
      // Production
      val configOpt: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg)
      if (configOpt.isDefined) {
        val configs = configOpt.get
        val (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey) = LogAnalyticsUtils.buildLogAnalyticsWorkspaceIdAndWorkspaceKey(
          workspaceId = configs.logAnalyticsWorkspaceId,
          workspaceKey = configs.logAnalyticsWorkspaceKeyName,
          applicationId = configs.applicationId,
          directoryId = configs.directoryId,
          adbSecretScopeName = configs.adbSecretScopeName,
          adbSPClientKeySecretName = configs.adbSPClientKeyName,
          keyVaultUrl = configs.keyVaultUrl
        )

        new DatabricksConfiguration(
          configs.applicationId,
          configs.directoryId,
          configs.adbSecretScopeName,
          configs.adbSPClientKeyName,
          configs.jdbcUsername,
          configs.jdbcPasswordKeyName,
          configs.jdbcHostname,
          configs.jdbcPort,
          configs.jdbcDatabase,
          configs.maxDbConnections,
          configs.useMsiAzureSqlAuth,
          configs.keyVaultUrl,
          logAnalyticsWorkspaceKey,
          logAnalyticsWorkspaceId)

      } else {
        throw new IllegalArgumentException("Invalid command line arguments")
      }
    }

  }

}

