/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.helpers

import java.util.Base64

object IoUtil {

  def encodePhoto(photoBytes: Option[Array[Byte]]): String = {
    photoBytes.map(bytes => Base64.getEncoder.encodeToString(bytes)).orNull
  }

}
