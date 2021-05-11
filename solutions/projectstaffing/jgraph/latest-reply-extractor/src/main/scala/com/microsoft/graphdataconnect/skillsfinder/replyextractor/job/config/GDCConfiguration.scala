/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.config

import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.utils.LogAnalyticsUtils
import org.apache.commons.lang3.StringUtils
import org.apache.spark.SparkContext

/*
 * hardcoded values
 */
abstract class GDCConfiguration {

  def setSparkSettings(sparkContext: SparkContext): Unit

  def getBlobConnectionString: String

  def getRawEmailsFullPath: String

  def getProcessedEmailsFullPath: String

  val emailDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  def getLogger(clazz: Class[_]): GdcLogger

  def getLoggerWorkspaceId(): String

  def getLogType(): String

  def getLoggerWorkspaceKey(): String
}

class LocalConfiguration extends GDCConfiguration {

  override def setSparkSettings(sparkContext: SparkContext): Unit = {
  }

  override def getBlobConnectionString: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/emails/emails.json").getPath, "target") + "src/main/resources/emails";
  }

  override def getRawEmailsFullPath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/emails/emails.json").getPath, "target") + "src/main/resources/emails";
  }

  override def getProcessedEmailsFullPath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/emails/emails.json").getPath, "target") + "src/main/resources/emails/emails_processed";
  }

  override def getLogger(clazz: Class[_]): GdcLogger = GdcLoggerFactory.getLogger(clazz = clazz)

  override def getLoggerWorkspaceId(): String = ""

  override def getLogType(): String = ""

  override def getLoggerWorkspaceKey(): String = ""
}

class AzureConfiguration(applicationId: String,
                         directoryId: String,
                         adbSecretScopeName: String,
                         adbSPClientKeySecretName: String,
                         storageAccountName: String,
                         inputContainer: String,
                         inputFolderPath: String,
                         outputContainer: String,
                         outputFolderPath: String,
                         workspaceKey: String,
                         workspaceId: String) extends GDCConfiguration {

  val logAnalyticsLogType = "LatestReplyExtractor"

  def getBlobConnectionString: String = {
    s"abfss://$inputContainer@$storageAccountName.dfs.core.windows.net"
  }

  def getRawEmailsFullPath: String = {
    s"abfss://$inputContainer@$storageAccountName.dfs.core.windows.net/$inputFolderPath"
  }

  def getProcessedEmailsFullPath: String = {
    s"abfss://$outputContainer@$storageAccountName.dfs.core.windows.net/$outputFolderPath"
  }

  override def setSparkSettings(sparkContext: SparkContext): Unit = {
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.auth.type.$storageAccountName.dfs.core.windows.net", "OAuth")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth.provider.type.$storageAccountName.dfs.core.windows.net", "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.id.$storageAccountName.dfs.core.windows.net", applicationId)
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.secret.$storageAccountName.dfs.core.windows.net", dbutils.secrets.get(scope = adbSecretScopeName, key = adbSPClientKeySecretName))
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.endpoint.$storageAccountName.dfs.core.windows.net", s"https://login.microsoftonline.com/$directoryId/oauth2/token")
  }

  override def getLogger(clazz: Class[_]): GdcLogger = GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = workspaceId,
    logAnalyticsSharedKey = workspaceKey,
    clazz = clazz,
    logType = logAnalyticsLogType
  )

  override def getLoggerWorkspaceId(): String = workspaceKey

  override def getLogType(): String = logAnalyticsLogType

  override def getLoggerWorkspaceKey(): String = workspaceKey
}

object GDCConfiguration {

  def apply(configArgs: ConfigArgs): GDCConfiguration =
    if (configArgs.storageAccountName.isDefined && configArgs.inputContainer.isDefined && configArgs.outputContainer.isDefined &&
      configArgs.applicationId.isDefined && configArgs.directoryId.isDefined && configArgs.adbSecretScopeName.isDefined && configArgs.adbSPClientKeySecretName.isDefined) {

      val (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey) = LogAnalyticsUtils.buildLogAnalyticsWorkspaceIdAndWorkspaceKey(
        workspaceId = configArgs.logAnalyticsWorkspaceId,
        workspaceKey = configArgs.logAnalyticsWorkspaceKeyName,
        applicationId = configArgs.applicationId.get,
        directoryId = configArgs.directoryId.get,
        adbSecretScopeName = configArgs.adbSecretScopeName.get,
        adbSPClientKeySecretName = configArgs.adbSPClientKeySecretName.get,
        keyVaultUrl = configArgs.keyVaultUrl.get
      )

      new AzureConfiguration(
        configArgs.applicationId.get,
        configArgs.directoryId.get,
        configArgs.adbSecretScopeName.get,
        configArgs.adbSPClientKeySecretName.get,
        configArgs.storageAccountName.get,
        configArgs.inputContainer.get,
        configArgs.inputFolderPath.getOrElse(""),
        configArgs.outputContainer.get,
        configArgs.outputFolderPath.getOrElse(""),
        logAnalyticsWorkspaceKey,
        logAnalyticsWorkspaceId)
    } else {
      new LocalConfiguration()
    }

}
