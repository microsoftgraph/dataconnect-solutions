/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.secret

protected class LocalSecretClientWrapper(val holder: Map[String, String]) extends SecretHolder {

  def get(keyName: String): String = holder(keyName)

}
