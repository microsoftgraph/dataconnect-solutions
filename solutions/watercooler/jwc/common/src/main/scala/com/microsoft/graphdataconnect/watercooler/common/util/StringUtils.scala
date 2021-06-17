/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.util

object StringUtils {

  def stringUrlParamToList(input: String): List[String] = {
    if (null == input || input.trim.isEmpty) {
      List.empty
    } else {
      input.split(",").map(_.trim).toList
    }
  }

}
