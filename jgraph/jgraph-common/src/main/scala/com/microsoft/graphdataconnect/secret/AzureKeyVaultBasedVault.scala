package com.microsoft.graphdataconnect.secret

import com.azure.security.keyvault.secrets.SecretClient


protected class AzureKeyVaultBasedVault(secretClient: SecretClient) extends Vault {
  override val secretHolder: SecretHolder = new AzureKeyVaultSecretClientWrapper(secretClient)
}
