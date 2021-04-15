package com.microsoft.graphdataconnect.skillsfinder.hrdata.config

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
        opt[String]("adb-secret-scope-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSecretScopeName = string))
          .text("adb secret scope name"),
        opt[String]("adb-sp-client-key-secret-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSPClientKeySecretName = string))
          .text("Azure Databricks secret name for the ADB Service Principal client key"),
        opt[String]("key-vault-url")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(keyVaultUrl = Some(string)))
          .text("Azure Key Vault instance url"),
        opt[String]("input-storage-account-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(inputStorageAccountName = Some(value)))
          .text("input AZBS storage account name containing HR Data"),
        opt[String]("input-container")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(inputContainer = Some(value)))
          .text("input container with HR Data"),
        opt[String]("input-folder-path")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(inputFolderPath = value))
          .text("input folder path containing the HR Data"),
        opt[String]("jdbc-hostname")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcHostname = value))
          .text("jdbc hostname"),
        opt[Int]("jdbc-port")
          .valueName("<int>")
          .action((value, currentConfig) => currentConfig.copy(jdbcPort = value))
          .text("jdbc port"),
        opt[String]("jdbc-database")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcDatabase = value))
          .text("jdbc database"),
        opt[String]("jdbc-username-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcUsernameKeyName = Some(value)))
          .text("jdbc username KeyVault key name"),
        opt[String]("jdbc-password-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcPasswordKeyName = Some(value)))
          .text("jdbc password KeyVault key name"),
        opt[Boolean]("use-msi-azure-sql-auth")
          .valueName("<bool>")
          .action((value, currentConfig) => currentConfig.copy(useMsiAzureSqlAuth = Some(value)))
          .text("Use Managed Service Identity (MSI) to authenticate into AzureSql or use user and password read from KeyVault instead"),
        opt[Int]("max-db-connections")
          .valueName("<int>")
          .action((value, currentConfig) => currentConfig.copy(maxDbConnections = value))
          .text("maximum number connections to use while writing the data into the database"),
        opt[String]("log-analytics-workspace-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceId = Some(string)))
          .text("Log Analytics workspace id"),
        opt[String]("log-analytics-workspace-key-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceKeyName = Some(string)))
          .text("Log Analytics workspace key secret name"),
        opt[Boolean]("dev")
          .valueName("<bool>")
          .action((value, currentConfig) => currentConfig.copy(isDevMode = value))
          .text("Start application in development mode"),
        opt[IngestionMode]("ingestion-mode")
          .valueName("<bool>")
          .action((value, currentConfig) => currentConfig.copy(ingestionMode = value))
          .text("Ingestion mode [production_mode, sample_mode or simulated_mode] "),
        opt[Boolean]("fail-fast-on-corrupt-data")
          .valueName("<bool>")
          .action((value, currentConfig) => currentConfig.copy(failFastOnCorruptData = value))
          .text("Fail fast when encountering invalid data or while encountering errors reading non-critical fields")

      )
    }

    OParser.parse(argsParser, arg, ConfigArgs())
  }

}

case class ConfigArgs(applicationId: Option[String] = None,
                      directoryId: Option[String] = None,
                      adbSecretScopeName: String = "gdc",
                      adbSPClientKeySecretName: String = "gdc-service-principal-secret",
                      keyVaultUrl: Option[String] = None,
                      inputStorageAccountName: Option[String] = None,
                      inputContainer: Option[String] = None,
                      inputFolderPath: String = "hr_data",
                      jdbcHostname: String = "localhost",
                      jdbcPort: Int = 1433,
                      jdbcDatabase: String = "gdc_database",
                      jdbcUsernameKeyName: Option[String] = None,
                      jdbcPasswordKeyName: Option[String] = None,
                      useMsiAzureSqlAuth: Option[Boolean] = None,
                      maxDbConnections: Int = 8,
                      logAnalyticsWorkspaceId: Option[String] = None,
                      logAnalyticsWorkspaceKeyName: Option[String] = None,
                      isDevMode: Boolean = false,
                      ingestionMode: IngestionMode.Value = IngestionMode.Production,
                      failFastOnCorruptData: Boolean = false)
