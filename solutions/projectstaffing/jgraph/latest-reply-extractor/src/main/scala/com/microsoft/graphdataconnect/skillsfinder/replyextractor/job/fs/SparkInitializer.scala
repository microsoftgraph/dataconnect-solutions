package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.fs

import org.apache.spark.sql.SparkSession

class SparkInitializer {

  def session: SparkSession = {
    SparkSession
      .builder()
      .appName("Emails Final Reply Extraction Job")
      .master("local")
      .getOrCreate()
  }

}

object SparkInitializer {
  def apply(): SparkInitializer = new SparkInitializer()
}
