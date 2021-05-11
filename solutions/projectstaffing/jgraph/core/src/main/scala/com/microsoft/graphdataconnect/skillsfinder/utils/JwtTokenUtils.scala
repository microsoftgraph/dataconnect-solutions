/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.utils

import org.apache.commons.codec.binary.Base64

object JwtTokenUtils {

  def extractHeader(jwtToken: String): String = {
    val splitToken = jwtToken.split("\\.")
    val encodedHeader = splitToken(0)
    val base64Url = new Base64(true)
    val header = new String(base64Url.decode(encodedHeader))
    header
  }

}
