/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */


package com.microsoft.graphdataconnect.watercooler.profiles.extractor.config

import scopt.OParser

object ConfigArgs {

  def parseCommandLineArguments(programArgs: Array[String]): Option[ConfigArgs] = {

    val builder = OParser.builder[ConfigArgs]
    import builder._
    val argsParser = { // adbSPClientKeyName
      OParser.sequence(
        opt[String]("application-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(applicationId = string))
          .text("application id"),
        opt[String]("directory-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(directoryId = string))
          .text("directory id"),
        opt[String]("adb-secret-scope-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSecretScopeName = string))
          .text("ADB secret scope name"),
        opt[String]("adb-sp-client-key-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSPClientKeyName = string))
          .text("ADB Service pricinpal secret key name"),

        opt[String]("max-db-connections")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(maxDbConnections = string))
          .text("Max Db connections"),
        opt[String]("jdbc-hostname")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(jdbcHostname = string))
          .text("Max Db connections"),
        opt[String]("jdbc-port")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(jdbcPort = string))
          .text("Max Db connections"),
        opt[String]("jdbc-database")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(jdbcDatabase = string))
          .text("Max Db connections"),
        opt[String]("jdbc-username")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(jdbcUsername = string))
          .text("Max Db connections"),
        opt[String]("jdbc-password")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(jdbcPasswordKeyName = string))
          .text("Max Db connections"),
        opt[String]("use-msi-auth")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(useMsiAzureSqlAuth = string))
          .text("Max Db connections"),


        opt[String]("log-analytics-workspace-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceId = string))
          .text("Log Analytics workspace id"),
        opt[String]("log-analytics-workspace-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceKeyName = string))
          .text("Log Analytics workspace key secret name"),
        opt[String]("key-vault-url")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(keyVaultUrl = string))
          .text("Azure Key Vault instance url")
      )
    }

    val optConfigArgs: Option[ConfigArgs] = OParser.parse(argsParser, programArgs, ConfigArgs())
    optConfigArgs.map(configArgs => configArgs.validate())
    optConfigArgs
  }



}

case class ConfigArgs(applicationId: String = "",
                      directoryId: String = "",
                      adbSecretScopeName: String = "",
                      adbSPClientKeyName: String = "",

                      maxDbConnections: String = "",
                      jdbcHostname: String = "",
                      jdbcPort: String = "",
                      jdbcDatabase: String = "",
                      jdbcUsername: String = "",
                      jdbcPasswordKeyName: String = "",
                      useMsiAzureSqlAuth: String = "",

                      logAnalyticsWorkspaceId: String = "",
                      logAnalyticsWorkspaceKeyName: String = "",
                      keyVaultUrl: String = ""
                      ) {

  private def validate(): Unit = {

  }

}
