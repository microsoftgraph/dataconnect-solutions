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
