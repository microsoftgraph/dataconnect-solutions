package com.microsoft.graphdataconnect.secret

import com.azure.security.keyvault.secrets.SecretClient

protected class AzureKeyVaultSecretClientWrapper(val holder: SecretClient) extends SecretHolder {

  def get(keyName: String): String = holder.getSecret(keyName).getValue

}


