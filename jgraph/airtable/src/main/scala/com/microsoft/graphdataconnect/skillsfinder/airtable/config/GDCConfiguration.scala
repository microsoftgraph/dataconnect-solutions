package com.microsoft.graphdataconnect.skillsfinder.airtable.config

import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.secret.Vault
import com.microsoft.graphdataconnect.skillsfinder.airtable.AirtableToHRData
import com.microsoft.graphdataconnect.utils.LogAnalyticsUtils
import org.apache.spark.SparkContext

abstract class GDCConfiguration {

  def setSparkSettings(sparkContext: SparkContext): Unit

  def getAirtableDataFullPath: String

  def getOutputHRDataFullPath: String

  def getLogger(clazz: Class[_]): GdcLogger

  def failFastOnCorruptData(): Boolean

}

class LocalConfiguration(inputFolderPath: Option[String],
                         outputFolderPath: String,
                         failFastOnCorruptData: Boolean = true) extends GDCConfiguration {

  override def setSparkSettings(sparkContext: SparkContext): Unit = {}

  override def getAirtableDataFullPath: String = inputFolderPath.getOrElse("")

  override def getOutputHRDataFullPath: String = outputFolderPath

  override def getLogger(clazz: Class[_]): GdcLogger = GdcLoggerFactory.getLogger(clazz = clazz)

  override def failFastOnCorruptData(): Boolean = failFastOnCorruptData
}

class AzureConfiguration(applicationId: String,
                         directoryId: String,
                         adbSecretScopeName: String,
                         adbSPClientKeySecretName: String,
                         keyVaultUrl: String,
                         adbSPAccessKey: String,
                         inputStorageAccountName: String,
                         inputContainer: String,
                         inputFolderPath: String,
                         outputStorageAccountName: String,
                         outputContainer: String,
                         outputFolderPath: String,
                         workspaceKey: String,
                         workspaceId: String,
                         failFastOnCorruptData: Boolean) extends GDCConfiguration {
  // Use the name of the main class in the jar in all LogAnalytics logs
  val logAnalyticsLogType = AirtableToHRData.getClass.getSimpleName.dropRight(1) // Drop the $ character at the end of the object name


  override def setSparkSettings(sparkContext: SparkContext): Unit = {
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.auth.type.$inputStorageAccountName.dfs.core.windows.net", "OAuth")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth.provider.type.$inputStorageAccountName.dfs.core.windows.net", "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.id.$inputStorageAccountName.dfs.core.windows.net", applicationId)
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.secret.$inputStorageAccountName.dfs.core.windows.net", adbSPAccessKey)
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.endpoint.$inputStorageAccountName.dfs.core.windows.net", s"https://login.microsoftonline.com/$directoryId/oauth2/token")
  }

  def getAirtableDataFullPath: String = {
    s"abfss://$inputContainer@$inputStorageAccountName.dfs.core.windows.net/$inputFolderPath"
  }

  def getOutputHRDataFullPath: String = {
    s"abfss://$outputContainer@$outputStorageAccountName.dfs.core.windows.net/$outputFolderPath"
  }

  override def getLogger(clazz: Class[_]): GdcLogger = GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = workspaceId,
    logAnalyticsSharedKey = workspaceKey,
    clazz = clazz,
    logType = logAnalyticsLogType
  )

  override def failFastOnCorruptData(): Boolean = failFastOnCorruptData
}

object GDCConfiguration {

  def apply(configArgs: ConfigArgs, vault: Vault): GDCConfiguration = {
    if (configArgs.isDevMode) {
      new LocalConfiguration(configArgs.inputFolderPath, configArgs.outputFolderPath)
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

        val accessKey = vault.getSecretValue(configArgs.adbSPClientKeySecretName)

        new AzureConfiguration(
          applicationId = configArgs.applicationId.get,
          directoryId = configArgs.directoryId.get,
          adbSecretScopeName = configArgs.adbSecretScopeName,
          adbSPClientKeySecretName = configArgs.adbSPClientKeySecretName,
          keyVaultUrl = configArgs.keyVaultUrl.get,
          adbSPAccessKey = accessKey,
          inputStorageAccountName = configArgs.inputStorageAccountName.get,
          inputContainer = configArgs.inputContainer.get,
          inputFolderPath = configArgs.inputFolderPath.get,
          outputStorageAccountName = configArgs.outputStorageAccountName.get,
          outputContainer = configArgs.outputContainer.get,
          outputFolderPath = configArgs.outputFolderPath,
          workspaceKey = logAnalyticsWorkspaceKey,
          workspaceId = logAnalyticsWorkspaceId,
          failFastOnCorruptData = configArgs.failFastOnCorruptData)

      } else {
        throw new IllegalArgumentException("Not enough information is given in order to establish connections (application-id & directory-id & keyVaultUrl parameters are needed)!")
      }
    }

  }

}
