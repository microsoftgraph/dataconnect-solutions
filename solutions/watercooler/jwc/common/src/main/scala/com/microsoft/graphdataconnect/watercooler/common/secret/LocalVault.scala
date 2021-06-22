/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.secret

protected class LocalVault(holder: Map[String, String]) extends Vault {
  override val secretHolder: SecretHolder = new LocalSecretClientWrapper(holder)
}
