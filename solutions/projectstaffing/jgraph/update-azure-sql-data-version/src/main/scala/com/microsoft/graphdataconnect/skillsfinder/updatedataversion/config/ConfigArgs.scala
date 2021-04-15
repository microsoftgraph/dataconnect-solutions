package com.microsoft.graphdataconnect.skillsfinder.updatedataversion.config

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
        opt[String]("new-data-version")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(newAzureSqlDataVersion = Some(string)))
          .text("new Azure Sql data version"),
        opt[String]("configuration-type")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(configurationType = Some(string)))
          .text("new Azure Sql data version"),
        opt[String]("table-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(tableName = Some(string)))
          .text("Azure Sql table name"),
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
          .action((value, currentConfig) => currentConfig.copy(keyVaultUrl = Some(value)))
          .text("key vault url"),
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
          .text("Use Managed Service Identity (MSI) to authenticate into AzureSql or use user and password read from KeyVault instead")
      )
    }

    OParser.parse(argsParser, arg, ConfigArgs())
  }

}

case class ConfigArgs(newAzureSqlDataVersion: Option[String] = None,
                      configurationType: Option[String] = None,
                      tableName: Option[String] = None,
                      applicationId: Option[String] = None,
                      directoryId: Option[String] = None,
                      adbSecretScopeName: Option[String] = None,
                      adbSPClientKeySecretName: Option[String] = None,
                      jdbcHostname: Option[String] = None,
                      jdbcPort: Option[String] = None,
                      jdbcDatabase: Option[String] = None,
                      keyVaultUrl: Option[String] = None,
                      logAnalyticsWorkspaceId: Option[String] = None,
                      logAnalyticsWorkspaceKeyName: Option[String] = None,
                      jdbcUsernameKeyName: Option[String] = None,
                      jdbcPasswordKeyName: Option[String] = None,
                      useMsiAzureSqlAuth: Option[Boolean] = None)
