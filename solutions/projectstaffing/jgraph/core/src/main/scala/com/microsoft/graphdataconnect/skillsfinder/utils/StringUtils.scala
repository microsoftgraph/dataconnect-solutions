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
