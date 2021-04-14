package com.microsoft.graphdataconnect.skillsfinder.airtable.config

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
        opt[String]("airtable-base-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(airtableBaseKeyName = value))
          .text("Airtable Base key name"),
        opt[String]("airtable-api-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(airtableApiKeyName = value))
          .text("Airtable API key name"),
        opt[String]("input-storage-account-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(inputStorageAccountName = Some(value)))
          .text("input AZBS storage account name containing simulated/sample Airtable data"),
        opt[String]("input-container")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(inputContainer = Some(value)))
          .text("input AZBS container with simulated/sample Airtable data"),
        opt[String]("input-folder-path")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(inputFolderPath = Some(value)))
          .text("input AZBS folder path with simulated/sample Airtable data"),
        opt[String]("output-storage-account-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(outputStorageAccountName = Some(value)))
          .text("AZBS storage account where resulting HR data is to be written"),
        opt[String]("output-container")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(outputContainer = Some(value)))
          .text("AZBS container where resulting HR data is to be written"),
        opt[String]("output-folder-path")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(outputFolderPath = value))
          .text("AZBS folder path where resulting HR data is to be written"),
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
                      airtableBaseKeyName: String = "airtable-base-key",
                      airtableApiKeyName: String = "airtable-api-key",
                      inputStorageAccountName: Option[String] = None,
                      inputContainer: Option[String] = None,
                      inputFolderPath: Option[String] = None,
                      outputStorageAccountName: Option[String] = None,
                      outputContainer: Option[String] = None,
                      outputFolderPath: String = "hr_data",
                      logAnalyticsWorkspaceId: Option[String] = None,
                      logAnalyticsWorkspaceKeyName: Option[String] = None,
                      isDevMode: Boolean = false,
                      ingestionMode: IngestionMode.Value = IngestionMode.Production,
                      failFastOnCorruptData: Boolean = false)
