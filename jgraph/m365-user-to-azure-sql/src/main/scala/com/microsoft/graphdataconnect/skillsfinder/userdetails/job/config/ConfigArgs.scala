package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.config

import com.microsoft.graphdataconnect.model.configs.IngestionMode
import com.microsoft.graphdataconnect.model.configs.IngestionMode.{IngestionMode, IngestionModeScoptReader}
import scopt.OParser

object ConfigArgs {

  def parseCommandLineArguments(arg: Array[String]): Option[ConfigArgs] = {
    val builder = OParser.builder[ConfigArgs]
    val argsParser = {
      import builder._
      OParser.sequence(
        opt[String]("application-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(applicationId = Some(string)))
          .text("application id"),
        opt[String]("directory-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(directoryId = Some(string)))
          .text("directory id"),
        opt[String]("adb-sp-client-key-secret-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSPClientKeySecretName = Some(string)))
          .text("Azure Databricks Service Principal client key secret name"),
        opt[String]("adb-secret-scope-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSecretScopeName = Some(string)))
          .text("ADB secret scope name"),
        opt[String]("jdbc-hostname")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcHostname = Some(value)))
          .text("jdbc hostname"),
        opt[String]("jdbc-port")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcPort = Some(value)))
          .text("jdbc port"),
        opt[String]("jdbc-database")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcDatabase = Some(value)))
          .text("jdbc database"),
        opt[String]("jdbc-username")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcUsername = Some(value)))
          .text("jdbc username"),
        opt[String]("jdbc-password")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcPassword = Some(value)))
          .text("jdbc password"),
        opt[String]("jdbc-username-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcUsernameKeyName = Some(value)))
          .text("jdbc username KeyVault key name"),
        opt[String]("jdbc-password-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcPasswordKeyName = Some(value)))
          .text("jdbc password KeyVault key name"),
        opt[String]("storage-account-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(storageAccountName = Some(value)))
          .text("storage account name"),
        opt[String]("input-container")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(inputContainer = Some(value)))
          .text("input container"),
        opt[String]("users-input-folder-path")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(usersInputFolderPath = Some(value)))
          .text("input folder path for users data"),
        opt[String]("managers-input-folder-path")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(managersInputFolderPath = Some(value)))
          .text("input folder path for managers data"),
        opt[Int]("max-db-connections")
          .valueName("<int>")
          .action((value, currentConfig) => currentConfig.copy(maxDbConnections = Some(value)))
          .text("max DB connections"),
        opt[String]("graph-api-tenant-id")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(graphApiTenantId = Some(value)))
          .text("Graph api application tenant id"),
        opt[String]("graph-api-client-id")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(graphApiClientId = Some(value)))
          .text("Graph api application client id"),
        opt[String]("graph-api-secret-scope")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(graphApiSecretScope = Some(value)))
          .text("Graph api secret scope name"),
        opt[String]("graph-api-sp-client-key-secret-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(graphApiSPClientKeySecretName = Some(value)))
          .text("Graph Api Service Principal client key secret name"),
        opt[String]("log-analytics-workspace-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceId = Some(string)))
          .text("Log Analytics workspace id"),
        opt[String]("log-analytics-workspace-key-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceKeyName = Some(string)))
          .text("Log Analytics workspace key secret name"),
        opt[String]("key-vault-url")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(keyVaultUrl = Some(string)))
          .text("Azure Key Vault instance url"),
        opt[IngestionMode]("ingestion-mode")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(ingestionMode = value))
          .text("Ingestion mode [production_mode, sample_mode or simulated_mode] "),
        opt[Boolean]("use-msi-azure-sql-auth")
          .valueName("<bool>")
          .action((value, currentConfig) => currentConfig.copy(useMsiAzureSqlAuth = Some(value)))
          .text("Use Managed Service Identity (MSI) to authenticate into AzureSql or use user and password read from KeyVault instead")
      )
    }

    OParser.parse(argsParser, arg, ConfigArgs())
  }

}

case class ConfigArgs(applicationId: Option[String] = None,
                      directoryId: Option[String] = None,
                      adbSecretScopeName: Option[String] = None,
                      adbSPClientKeySecretName: Option[String] = None,
                      jdbcHostname: Option[String] = None,
                      jdbcPort: Option[String] = None,
                      jdbcDatabase: Option[String] = None,
                      jdbcUsername: Option[String] = None,
                      jdbcPassword: Option[String] = None,
                      jdbcUsernameKeyName: Option[String] = None,
                      jdbcPasswordKeyName: Option[String] = None,
                      useMsiAzureSqlAuth: Option[Boolean] = None,
                      storageAccountName: Option[String] = None,
                      inputContainer: Option[String] = None,
                      usersInputFolderPath: Option[String] = None,
                      managersInputFolderPath: Option[String] = None,
                      outputContainer: Option[String] = None,
                      outputFolderPath: Option[String] = None,
                      maxDbConnections: Option[Int] = None,
                      graphApiTenantId: Option[String] = None,
                      graphApiClientId: Option[String] = None,
                      graphApiSecretScope: Option[String] = None,
                      graphApiSPClientKeySecretName: Option[String] = None,
                      logAnalyticsWorkspaceId: Option[String] = None,
                      logAnalyticsWorkspaceKeyName: Option[String] = None,
                      ingestionMode: IngestionMode.Value = IngestionMode.Production,
                      keyVaultUrl: Option[String] = None)

