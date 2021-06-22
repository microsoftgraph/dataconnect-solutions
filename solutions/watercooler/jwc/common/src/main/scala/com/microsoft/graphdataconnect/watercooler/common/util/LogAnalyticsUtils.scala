/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.util

import com.microsoft.graphdataconnect.watercooler.common.secret.Vault


object LogAnalyticsUtils {

  def buildLogAnalyticsWorkspaceIdAndWorkspaceKey(workspaceId: String,
                                                  workspaceKey: String,
                                                  applicationId: String,
                                                  directoryId: String,
                                                  adbSecretScopeName: String,
                                                  adbSPClientKeySecretName: String,
                                                  keyVaultUrl: String): (String, String) = {
    var logAnalyticsWorkspaceId = if (workspaceId.trim.nonEmpty) workspaceId else ""

    val logAnalyticsWorkspaceKey = if (!logAnalyticsWorkspaceId.trim.isEmpty && workspaceKey.trim.nonEmpty) {
      try {
        val azureKeyVaultClient = Vault(
          keyVaultUrl = keyVaultUrl,
          clientId = applicationId,
          tenantId = directoryId,
          adbSecretScopeName = adbSecretScopeName,
          adbServicePrincipalKeySecretName = adbSPClientKeySecretName)

        azureKeyVaultClient.getSecretValue(secretName = workspaceKey.trim)
      } catch {
        case e: Exception => println("Failed to get Log Analytics api key from Azure Key Vault. " + e)
          logAnalyticsWorkspaceId = ""
          ""
      }
    } else ""

    (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey)
  }

}
