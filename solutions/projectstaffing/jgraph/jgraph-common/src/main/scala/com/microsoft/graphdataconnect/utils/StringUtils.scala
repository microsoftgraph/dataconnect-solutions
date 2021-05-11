/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.utils

object StringUtils {

  val specialCharacters = "[$&+,:;=\\?@#|/'<>.^*()%!-]"

  implicit class StringImplicits(val input: String) {

    def withoutSpecialCharacters(): String = {
      input.replaceAll(specialCharacters, "")
    }

    def isNullOrEmpty: Boolean = {
      input == null || input.trim.isEmpty
    }

    def trimIfNotNull(): String = {
      if (input != null) {
        input.trim
      } else {
        input
      }
    }
  }

}
