/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.fs

import org.apache.spark.SparkContext
import org.apache.spark.sql.DataFrame

trait FilesWriter {

  implicit val sparkContext: SparkContext

  def writeDF(emails: DataFrame, destinationPath: String): Unit = {
    emails.write.json(destinationPath)
  }
}
