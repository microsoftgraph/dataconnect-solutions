/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.cleanup.config

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
        opt[String]("jdbc-username-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcUsernameKeyName = Some(value)))
          .text("jdbc username KeyVault key name"),
        opt[String]("jdbc-password-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(jdbcPasswordKeyName = Some(value)))
          .text("jdbc password KeyVault key name"),
        opt[String]("key-vault-url")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(keyVaultUrl = Some(value)))
          .text("key vault url"),
        opt[String]("azure-search-url")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(azureSearchUrl = Some(value)))
          .text("Azure Search url"),
        opt[String]("azure-search-admin-key-name")
          .valueName("<string>")
          .action((value, currentConfig) => currentConfig.copy(azureSearchAdminKeyName = Some(value)))
          .text("Azure Search admin key name"),
        opt[String]("log-analytics-workspace-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceId = Some(string)))
          .text("Log Analytics workspace id"),
        opt[String]("log-analytics-workspace-key-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceKeyName = Some(string)))
          .text("Log Analytics workspace key secret name"),
        opt[String]("emails-index-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(emailsIndexName = Some(string)))
          .text("Azure Search emails index name"),
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
                      jdbcUsernameKeyName: Option[String] = None,
                      jdbcPasswordKeyName: Option[String] = None,
                      useMsiAzureSqlAuth: Option[Boolean] = None,
                      keyVaultUrl: Option[String] = None,
                      azureSearchUrl: Option[String] = None,
                      azureSearchAdminKeyName: Option[String] = None,
                      logAnalyticsWorkspaceId: Option[String] = None,
                      logAnalyticsWorkspaceKeyName: Option[String] = None,
                      emailsIndexName: Option[String] = None)

