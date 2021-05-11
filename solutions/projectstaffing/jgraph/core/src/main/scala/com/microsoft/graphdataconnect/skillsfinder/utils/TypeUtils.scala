/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.utils

object TypeUtils {

  implicit class OptionUtils[A](value: Option[A]) {
    def getOrFail(t: => Throwable): A =
      if (value.isDefined) value.get else throw t
  }

  implicit class RichBoolean(value: Boolean) {
    def orFail(t: => Throwable): Boolean =
      if (!value) throw t else value

    def thenFail(t: => Throwable): Boolean =
      if (value) throw t else value
  }

}
