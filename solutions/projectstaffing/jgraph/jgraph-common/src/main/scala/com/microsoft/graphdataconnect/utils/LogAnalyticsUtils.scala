/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.utils

import com.microsoft.graphdataconnect.secret.Vault

object LogAnalyticsUtils {

  def buildLogAnalyticsWorkspaceIdAndWorkspaceKey(workspaceId: Option[String],
                                                  workspaceKey: Option[String],
                                                  applicationId: String,
                                                  directoryId: String,
                                                  adbSecretScopeName: String,
                                                  adbSPClientKeySecretName: String,
                                                  keyVaultUrl: String): (String, String) = {
    var logAnalyticsWorkspaceId = if (workspaceId.exists(!_.trim.isEmpty)) workspaceId.get else ""

    val logAnalyticsWorkspaceKey = if (!logAnalyticsWorkspaceId.trim.isEmpty && workspaceKey.exists(!_.trim.isEmpty)) {
      try {
        val azureKeyVaultClient = Vault(
          keyVaultUrl = keyVaultUrl,
          clientId = applicationId,
          tenantId = directoryId,
          adbSecretScopeName = adbSecretScopeName,
          adbServicePrincipalKeySecretName = adbSPClientKeySecretName)

        azureKeyVaultClient.getSecretValue(secretName = workspaceKey.get)
      } catch {
        case e: Exception => println("Failed to get Log Analytics api key from Azure Key Vault. " + e)
          logAnalyticsWorkspaceId = ""
          ""
      }
    } else ""

    (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey)
  }

}
