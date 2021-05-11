/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.hrdata.config

import java.util.Properties

import com.microsoft.graphdataconnect.jdbc.DatabaseConnectionPropertiesFactory
import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.secret.Vault
import com.microsoft.graphdataconnect.skillsfinder.hrdata.CopyHRDataToDatabase
import com.microsoft.graphdataconnect.utils.LogAnalyticsUtils
import org.apache.spark.SparkContext

import scala.util.Try

abstract class GDCConfiguration {
  def getJdbcUrl(): String

  def getDatabaseConnectionProperties(): Properties

  def getMaxDbConnections: Int

  def setSparkSettings(sparkContext: SparkContext): Unit

  def getHRDataFullPath: String

  def getLogger(clazz: Class[_]): GdcLogger

  def failFastOnCorruptData(): Boolean

  def getOldDataCleanupDelaySeconds(): Int

}

class LocalConfiguration(inputFolderPath: String,
                         jdbcUsername: String,
                         jdbcPassword: String,
                         failFastOnCorruptData: Boolean = true) extends GDCConfiguration {

  override def getJdbcUrl(): String = {
    val hostname: String = sys.env.getOrElse(Constants.JDBC_HOSTNAME, "localhost")
    val port: Int = Try(sys.env.getOrElse(Constants.JDBC_PORT, "1433").toInt).getOrElse(1433)
    val database: String = sys.env.getOrElse(Constants.JDBC_DATABASE, "gdc_database")
    s"jdbc:sqlserver://$hostname:$port;database=$database"
  }

  override def getDatabaseConnectionProperties(): Properties = {
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    connectionProperties
  }

  override def getMaxDbConnections: Int = 8

  override def setSparkSettings(sparkContext: SparkContext): Unit = {}

  override def getHRDataFullPath: String = inputFolderPath

  override def getLogger(clazz: Class[_]): GdcLogger = GdcLoggerFactory.getLogger(clazz = clazz)

  override def failFastOnCorruptData(): Boolean = failFastOnCorruptData

  override def getOldDataCleanupDelaySeconds(): Int = 0
}

class AzureConfiguration(applicationId: String,
                         directoryId: String,
                         adbSecretScopeName: String,
                         adbSPClientKeySecretName: String,
                         keyVaultUrl: String,
                         adbSPAccessKey: String,
                         storageAccountName: String,
                         inputContainer: String,
                         inputFolderPath: String,
                         jdbcHost: String,
                         jdbcPort: Int,
                         jdbcDatabase: String,
                         jdbcUsernameKeyName: Option[String] = None,
                         jdbcPasswordKeyName: Option[String] = None,
                         useMsiAzureSqlAuth: Boolean,
                         maxDbConnections: Int,
                         workspaceKey: String,
                         workspaceId: String,
                         failFastOnCorruptData: Boolean) extends GDCConfiguration {

  // Use the name of the main class in the jar in all LogAnalytics logs
  val logAnalyticsLogType = CopyHRDataToDatabase.getClass.getSimpleName.dropRight(1) // Drop the $ character at the end of the object name

  override def getJdbcUrl(): String = {
    s"jdbc:sqlserver://$jdbcHost:$jdbcPort;database=$jdbcDatabase;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
  }

  override def getDatabaseConnectionProperties(): Properties = {
    DatabaseConnectionPropertiesFactory.getProperties(
      applicationId, directoryId, adbSecretScopeName, adbSPClientKeySecretName,
      jdbcUsernameKeyName, jdbcPasswordKeyName, useMsiAzureSqlAuth, keyVaultUrl)
  }

  override def getMaxDbConnections: Int = {
    maxDbConnections
  }

  override def setSparkSettings(sparkContext: SparkContext): Unit = {
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.auth.type.$storageAccountName.dfs.core.windows.net", "OAuth")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth.provider.type.$storageAccountName.dfs.core.windows.net", "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.id.$storageAccountName.dfs.core.windows.net", applicationId)
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.secret.$storageAccountName.dfs.core.windows.net", adbSPAccessKey)
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.endpoint.$storageAccountName.dfs.core.windows.net", s"https://login.microsoftonline.com/$directoryId/oauth2/token")
  }

  def getHRDataFullPath: String = {
    s"abfss://$inputContainer@$storageAccountName.dfs.core.windows.net/$inputFolderPath"
  }

  override def getLogger(clazz: Class[_]): GdcLogger = GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = workspaceId,
    logAnalyticsSharedKey = workspaceKey,
    clazz = clazz,
    logType = logAnalyticsLogType
  )

  override def failFastOnCorruptData(): Boolean = failFastOnCorruptData

  override def getOldDataCleanupDelaySeconds(): Int = 60
}

object GDCConfiguration {

  def apply(configArgs: ConfigArgs): GDCConfiguration = {
    if (configArgs.isDevMode) {
      val username = sys.env.getOrElse(Constants.JDBC_USERNAME, "sa")
      val password = sys.env.getOrElse(Constants.JDBC_PASSWORD, "password_!23")
      new LocalConfiguration(configArgs.inputFolderPath, username, password, configArgs.failFastOnCorruptData)
    } else {

      if (configArgs.applicationId.isDefined && configArgs.directoryId.isDefined && configArgs.keyVaultUrl.isDefined) {

        val (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey) = LogAnalyticsUtils.buildLogAnalyticsWorkspaceIdAndWorkspaceKey(
          workspaceId = configArgs.logAnalyticsWorkspaceId,
          workspaceKey = configArgs.logAnalyticsWorkspaceKeyName,
          applicationId = configArgs.applicationId.get,
          directoryId = configArgs.directoryId.get,
          adbSecretScopeName = configArgs.adbSecretScopeName,
          adbSPClientKeySecretName = configArgs.adbSPClientKeySecretName,
          keyVaultUrl = configArgs.keyVaultUrl.get
        )

        val vault: Vault = Vault(keyVaultUrl = configArgs.keyVaultUrl.get,
          clientId = configArgs.applicationId.get,
          tenantId = configArgs.directoryId.get,
          adbSecretScopeName = configArgs.adbSecretScopeName,
          adbServicePrincipalKeySecretName = configArgs.adbSPClientKeySecretName)

        val accessKey = vault.getSecretValue(configArgs.adbSPClientKeySecretName)

        new AzureConfiguration(
          applicationId = configArgs.applicationId.get,
          directoryId = configArgs.directoryId.get,
          adbSecretScopeName = configArgs.adbSecretScopeName,
          adbSPClientKeySecretName = configArgs.adbSPClientKeySecretName,
          configArgs.keyVaultUrl.get,
          adbSPAccessKey = accessKey,
          storageAccountName = configArgs.inputStorageAccountName.get,
          inputContainer = configArgs.inputContainer.get,
          inputFolderPath = configArgs.inputFolderPath,
          jdbcHost = configArgs.jdbcHostname,
          jdbcPort = configArgs.jdbcPort,
          jdbcDatabase = configArgs.jdbcDatabase,
          jdbcUsernameKeyName = configArgs.jdbcUsernameKeyName,
          jdbcPasswordKeyName = configArgs.jdbcPasswordKeyName,
          useMsiAzureSqlAuth = configArgs.useMsiAzureSqlAuth.get,
          maxDbConnections = configArgs.maxDbConnections,
          workspaceKey = logAnalyticsWorkspaceKey,
          workspaceId = logAnalyticsWorkspaceId,
          failFastOnCorruptData = configArgs.failFastOnCorruptData)

      } else {
        throw new IllegalArgumentException("Not enough information is given in order to establish connections (application-id & directory-id & keyVaultUrl parameters are needed)!")
      }
    }

  }

}
