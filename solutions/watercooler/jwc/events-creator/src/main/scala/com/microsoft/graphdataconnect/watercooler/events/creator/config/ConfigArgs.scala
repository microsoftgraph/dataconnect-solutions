/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.events.creator.config

import scopt.{DefaultOParserSetup, OParser, OParserSetup}

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
        opt[String]("application-key-secret-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(applicationSecretKeyName = string))
          .text("JGraph application key secret name"),
        opt[String]("directory-id")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(directoryId = string))
          .text("directory id"),
        opt[String]("meeting-organizer-email")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(meetingOrganizerEmail = string))
          .text("meeting organizer email"),
        opt[String]("adb-secret-scope-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSecretScopeName = string))
          .text("ADB secret scope name"),

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
          .action((string, currentConfig) => currentConfig.copy(jdbcUsernameKeyName = string))
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
        opt[String]("log-analytics-workspace-key-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(logAnalyticsWorkspaceKeyName = string))
          .text("Log Analytics workspace key secret name"),
        opt[String]("key-vault-url")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(keyVaultUrl = string))
          .text("Azure Key Vault instance url"),
        opt[String]("start-date")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(startDate = string))
          .text("Start date"),
        opt[String]("end-date")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(endDate = string))
          .text("End date"),
        opt[String]("meeting-duration")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(meetingDuration = string))
          .text("Meeting Duration (in minutes)")
      )
    }

    //this setup is needed in order to make OParser ignore spring related parameters that are not defined in the parser
    //e.g. --spring.datasource.username=sa --spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=jwc;
    val setup: OParserSetup = new DefaultOParserSetup {
      override def errorOnUnknownArgument: Boolean = false
    }

    val optConfigArgs: Option[ConfigArgs] = OParser.parse(argsParser, programArgs, ConfigArgs(), setup)
    optConfigArgs.map(configArgs => configArgs.validate())
    optConfigArgs
  }



}

case class ConfigArgs(applicationId: String = "",
                      directoryId: String = "",
                      meetingOrganizerEmail: String = "",
                      adbSecretScopeName: String = "",
                      applicationSecretKeyName: String = "",
                      maxDbConnections: String = "",
                      jdbcHostname: String = "",
                      jdbcPort: String = "",
                      jdbcDatabase: String = "",
                      jdbcUsernameKeyName: String = "",
                      jdbcPasswordKeyName: String = "",
                      useMsiAzureSqlAuth: String = "",
                      logAnalyticsWorkspaceId: String = "",
                      logAnalyticsWorkspaceKeyName: String = "",
                      keyVaultUrl: String = "",
                      startDate: String = "", // yyyy-MM-dd format
                      endDate: String = "", // yyyy-MM-dd format
                      meetingDuration: String = ""
                     ) {

  private def validate(): Unit = {

  }

}
