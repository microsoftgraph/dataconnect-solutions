package com.microsoft.graphdataconnect.secret

protected class LocalSecretClientWrapper(val holder: Map[String, String]) extends SecretHolder {

  def get(keyName: String): String = holder(keyName)

}
