package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.config

import java.util.Properties

import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.microsoft.graphdataconnect.jdbc.DatabaseConnectionPropertiesFactory
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

  def getM365UserDataFullPath: String

  def getM365UserManagerFullPath: String

  def getDatabaseConnectionProperties(): Properties

  def getLogger(clazz: Class[_]): GdcLogger

  def getLoggerWorkspaceId(): String

  def getLogType(): String

  def getLoggerWorkspaceKey(): String

}

class LocalConfiguration(jdbcUsername: String,
                         jdbcPassword: String) extends GDCConfiguration {

  override def setSparkSettings(sparkContext: SparkContext): Unit = {
  }

  override def getBlobConnectionString: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/users/users.jsonl").getPath, "target") + "src/main/resources/users";
  }

  override def getM365UserDataFullPath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/users/users.jsonl").getPath, "target") + "src/main/resources/users";
  }

  override def getM365UserManagerFullPath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/managers/managers.jsonl").getPath, "target") + "src/main/resources/managers";
  }

  override def getDatabaseConnectionProperties(): Properties = {
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    connectionProperties
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
                         jdbcUsernameKeyName: Option[String] = None,
                         jdbcPasswordKeyName: Option[String] = None,
                         useMsiAzureSqlAuth: Boolean,
                         keyVaultUrl: String,
                         storageAccountName: String,
                         inputContainer: String,
                         usersInputFolderPath: String,
                         managersInputFolderPath: String,
                         workspaceKey: String,
                         workspaceId: String) extends GDCConfiguration {

  val logAnalyticsLogType = "M365UserToAzureSqlJob"

  def getBlobConnectionString: String = {
    s"abfss://$inputContainer@$storageAccountName.dfs.core.windows.net"
  }

  def getM365UserDataFullPath: String = {
    s"abfss://$inputContainer@$storageAccountName.dfs.core.windows.net/$usersInputFolderPath"
  }

  def getM365UserManagerFullPath: String = {
    s"abfss://$inputContainer@$storageAccountName.dfs.core.windows.net/$managersInputFolderPath"
  }

  // TODO retrieve the account key from Azure KeyVault
  override def setSparkSettings(sparkContext: SparkContext): Unit = {
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.auth.type.$storageAccountName.dfs.core.windows.net", "OAuth")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth.provider.type.$storageAccountName.dfs.core.windows.net", "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.id.$storageAccountName.dfs.core.windows.net", applicationId)
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.secret.$storageAccountName.dfs.core.windows.net", dbutils.secrets.get(scope = adbSecretScopeName, key = adbSPClientKeySecretName))
    sparkContext.hadoopConfiguration.set(s"fs.azure.account.oauth2.client.endpoint.$storageAccountName.dfs.core.windows.net", s"https://login.microsoftonline.com/$directoryId/oauth2/token")
    sparkContext.hadoopConfiguration.set("spark.sql.broadcastTimeout", "3600")
  }

  override def getDatabaseConnectionProperties(): Properties = {
    DatabaseConnectionPropertiesFactory.getProperties(
      applicationId, directoryId, adbSecretScopeName, adbSPClientKeySecretName,
      jdbcUsernameKeyName, jdbcPasswordKeyName, useMsiAzureSqlAuth, keyVaultUrl)
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
  def apply(configArgs: ConfigArgs): GDCConfiguration = {
    if (configArgs.storageAccountName.isDefined && configArgs.inputContainer.isDefined && configArgs.usersInputFolderPath.isDefined
      && configArgs.applicationId.isDefined && configArgs.directoryId.isDefined && configArgs.adbSecretScopeName.isDefined
      && configArgs.adbSPClientKeySecretName.isDefined && configArgs.keyVaultUrl.isDefined
      && configArgs.useMsiAzureSqlAuth.isDefined) {

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
        configArgs.jdbcUsernameKeyName,
        configArgs.jdbcPasswordKeyName,
        configArgs.useMsiAzureSqlAuth.get,
        configArgs.keyVaultUrl.get,
        configArgs.storageAccountName.get,
        configArgs.inputContainer.get,
        configArgs.usersInputFolderPath.getOrElse(""),
        configArgs.managersInputFolderPath.getOrElse(""),
        logAnalyticsWorkspaceKey,
        logAnalyticsWorkspaceId)
    } else {
      new LocalConfiguration(jdbcUsername = configArgs.jdbcUsername.getOrElse("sa"), //local db user
        jdbcPassword = configArgs.jdbcPassword.getOrElse("password_!23"))
    }
  }

}
