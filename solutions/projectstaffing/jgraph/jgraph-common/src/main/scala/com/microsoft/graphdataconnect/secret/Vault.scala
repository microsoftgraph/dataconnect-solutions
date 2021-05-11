/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.secret

import com.azure.identity.ClientSecretCredentialBuilder
import com.azure.security.keyvault.secrets.{SecretClient, SecretClientBuilder}
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

trait Vault {

  val secretHolder: SecretHolder

  def getSecretValue(secretName: String): String = {
    secretHolder.get(secretName)
  }

}

object Vault {

  def apply(keyVaultUrl: String,
            clientId: String,
            tenantId: String,
            adbSecretScopeName: String,
            adbServicePrincipalKeySecretName: String): Vault = {
    val holder: SecretClient = new SecretClientBuilder()
      .vaultUrl(keyVaultUrl)
      .credential(new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .tenantId(tenantId)
        .clientSecret(dbutils.secrets.get(scope = adbSecretScopeName, key = adbServicePrincipalKeySecretName))
        .build())
      .buildClient();

    new AzureKeyVaultBasedVault(holder)
  }

  def apply(holder: Map[String, String]): Vault = {
    new LocalVault(holder)
  }

  def apply(keyVaultUrl: String, clientId: String, tenantId: String, clientSecret: String): Vault = {
    val holder: SecretClient = new SecretClientBuilder()
      .vaultUrl(keyVaultUrl)
      .credential(new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .tenantId(tenantId)
        .clientSecret(clientSecret)
        .build())
      .buildClient();

    new AzureKeyVaultBasedVault(holder)
  }

}

