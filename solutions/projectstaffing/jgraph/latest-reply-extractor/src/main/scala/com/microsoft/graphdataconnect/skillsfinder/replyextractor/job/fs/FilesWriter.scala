package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.fs

import org.apache.spark.SparkContext
import org.apache.spark.sql.DataFrame

trait FilesWriter {

  implicit val sparkContext: SparkContext

  def writeDF(emails: DataFrame, destinationPath: String): Unit = {
    emails.write.json(destinationPath)
  }
}
