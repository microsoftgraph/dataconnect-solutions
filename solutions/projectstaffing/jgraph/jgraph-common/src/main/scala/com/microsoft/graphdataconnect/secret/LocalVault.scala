package com.microsoft.graphdataconnect.secret

protected class LocalVault(holder: Map[String, String]) extends Vault {
  override val secretHolder: SecretHolder = new LocalSecretClientWrapper(holder)
}
