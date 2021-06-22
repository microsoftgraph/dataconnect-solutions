/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */


package com.microsoft.graphdataconnect.watercooler.profiles.extractor.fs

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame

trait FilesWriter {

  implicit val sparkContext: SparkContext

  def writeDFasJson(df: DataFrame, destinationPath: String): Unit = {
    df.write.json(destinationPath)
  }

  def writeRddAsText(rdd: RDD[_], destinationPath: String): Unit = {
    rdd.saveAsTextFile(destinationPath)
  }

}
