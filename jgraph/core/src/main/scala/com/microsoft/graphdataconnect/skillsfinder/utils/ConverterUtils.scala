package com.microsoft.graphdataconnect.skillsfinder.utils

object ConverterUtils {

  def caseClassToMap(caseClass: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: caseClass.getClass.getDeclaredFields) {
      (accumulator, field) =>
        field.setAccessible(true)
        accumulator + (field.getName -> field.get(caseClass))
    }

}
