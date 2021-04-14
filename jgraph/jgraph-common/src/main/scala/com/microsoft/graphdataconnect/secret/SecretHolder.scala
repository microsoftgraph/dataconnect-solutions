package com.microsoft.graphdataconnect.secret

protected trait SecretHolder {

  def get(keyName: String): String

}
