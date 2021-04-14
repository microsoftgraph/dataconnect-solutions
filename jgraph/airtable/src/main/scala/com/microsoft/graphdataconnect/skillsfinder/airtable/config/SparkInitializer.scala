package com.microsoft.graphdataconnect.skillsfinder.airtable.config

import org.apache.spark.sql.SparkSession

class SparkInitializer {

  def session: SparkSession = {
    SparkSession
      .builder()
      .appName("Airtable data processing Job")
      .master("local")
      .getOrCreate()
  }

}

object SparkInitializer {
  def apply(): SparkInitializer = new SparkInitializer()
}
