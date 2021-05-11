/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.utils

// TODO move to `common` module
object StringUtils {

  val specialCharacters = "[$&+,:;=\\?@#|/'<>.^*()%!-]"

  implicit class StringImplicits(val input: String) {

    def withoutSpecialCharacters(): String = {
      input.replaceAll(specialCharacters, "")
    }

    def isNullOrBlank: Boolean = {
      input == null || input.trim.isEmpty
    }

    def isNullOrEmpty: Boolean = {
      input == null || input.isEmpty
    }
  }

}
