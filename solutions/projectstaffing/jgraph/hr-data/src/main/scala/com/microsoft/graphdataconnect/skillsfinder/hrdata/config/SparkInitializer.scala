/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.hrdata.config

import org.apache.spark.sql.SparkSession

class SparkInitializer {

  def session: SparkSession = {
    SparkSession
      .builder()
      .appName("HR Data processing Job")
      .master("local")
      .getOrCreate()
  }

}

object SparkInitializer {
  def apply(): SparkInitializer = new SparkInitializer()
}
