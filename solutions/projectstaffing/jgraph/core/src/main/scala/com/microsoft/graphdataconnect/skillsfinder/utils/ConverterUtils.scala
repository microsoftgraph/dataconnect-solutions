/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.utils

object ConverterUtils {

  def caseClassToMap(caseClass: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: caseClass.getClass.getDeclaredFields) {
      (accumulator, field) =>
        field.setAccessible(true)
        accumulator + (field.getName -> field.get(caseClass))
    }

}
