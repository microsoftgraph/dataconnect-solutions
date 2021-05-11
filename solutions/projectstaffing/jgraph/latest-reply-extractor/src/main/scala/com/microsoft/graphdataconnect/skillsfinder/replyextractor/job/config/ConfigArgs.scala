/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.config

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
          .action((string, currentConfig) => currentConfig.copy(adbSecretScopeName = Some(string)))
          .text("ADB secret scope name"),
        opt[String]("adb-sp-client-key-secret-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(adbSPClientKeySecretName = Some(string)))
          .text("Azure Databricks Service Principal client key secret name"),
        opt[String]("storage-account-name")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(storageAccountName = Some(string)))
          .text("storage account name"),
        opt[String]("input-container")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(inputContainer = Some(string)))
          .text("input container"),
        opt[String]("input-folder-path")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(inputFolderPath = Some(string)))
          .text("input folder path"),
        opt[String]("output-container")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(outputContainer = Some(string)))
          .text("output container"),
        opt[String]("output-folder-path")
          .valueName("<string>")
          .action((string, currentConfig) => currentConfig.copy(outputFolderPath = Some(string)))
          .text("output folder path"),
        opt[String]("run-deduplication")
          .valueName("<boolean>")
          .action((string, currentConfig) => currentConfig.copy(runDeduplication = string.toBoolean))
          .text("remove duplicate emails"),
        opt[String]("run-reply-removal")
          .valueName("<boolean>")
          .action((string, currentConfig) => currentConfig.copy(runReplyRemoval = string.toBoolean))
          .text("extract only the latest message from each email"),
        opt[String]("parse-enron-dataset")
          .valueName("<boolean>")
          .action((string, currentConfig) => currentConfig.copy(parseEnronDataset = string.toBoolean))
          .text("parse emails from enron dataset"),
        opt[String]('n', "number-of-partitions")
          .valueName("<integer>")
          .action((string, currentConfig) => currentConfig.copy(numberOfPartitions = string.toInt))
          .text("number of output files"),
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
          .text("Azure Key Vault instance url")
      )
    }

    OParser.parse(argsParser, arg, ConfigArgs())
  }

}

case class ConfigArgs(applicationId: Option[String] = None,
                      directoryId: Option[String] = None,
                      adbSecretScopeName: Option[String] = None,
                      adbSPClientKeySecretName: Option[String] = None,
                      storageAccountName: Option[String] = None,
                      inputContainer: Option[String] = None,
                      inputFolderPath: Option[String] = None,
                      outputContainer: Option[String] = None,
                      outputFolderPath: Option[String] = None,
                      runDeduplication: Boolean = true,
                      runReplyRemoval: Boolean = true,
                      parseEnronDataset: Boolean = false,
                      numberOfPartitions: Int = 1,
                      logAnalyticsWorkspaceId: Option[String] = None,
                      logAnalyticsWorkspaceKeyName: Option[String] = None,
                      keyVaultUrl: Option[String] = None)
