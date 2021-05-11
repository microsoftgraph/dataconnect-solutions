/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.secret

import com.azure.security.keyvault.secrets.SecretClient


protected class AzureKeyVaultBasedVault(secretClient: SecretClient) extends Vault {
  override val secretHolder: SecretHolder = new AzureKeyVaultSecretClientWrapper(secretClient)
}
