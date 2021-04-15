package com.microsoft.graphdataconnect.skillsfinder.userdetails.job.fs

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

trait FilesReader {

  implicit val sparkContext: SparkContext

  def readFiles(filesPath: Seq[String]): RDD[String] = {
    sparkContext.textFile(filesPath.mkString(","))
  }

}
