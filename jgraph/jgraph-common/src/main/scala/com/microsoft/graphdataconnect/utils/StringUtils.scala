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
