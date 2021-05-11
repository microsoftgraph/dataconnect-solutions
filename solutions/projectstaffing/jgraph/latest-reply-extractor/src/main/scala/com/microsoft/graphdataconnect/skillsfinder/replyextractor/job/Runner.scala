/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.config.{ConfigArgs, GDCConfiguration}
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.fs.{FileSystemInitializer, SparkInitializer}
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.jobs.LatestReplyExtractorJob
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object Runner {

  var log: GdcLogger = _

  def main(arg: Array[String]): Unit = {
    val config: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg) //Some(ConfigArgs(numberOfPartitions = 4 ))

    if (config.isDefined) {
      val configParams: ConfigArgs = config.get
      implicit val configuration: GDCConfiguration = GDCConfiguration(configParams)
      log = configuration.getLogger(Runner.getClass)

      implicit val sparkSession: SparkSession = SparkInitializer().session
      implicit val sparkContext: SparkContext = sparkSession.sparkContext
      configuration.setSparkSettings(sparkContext)

      implicit val fileSystem: FileSystem = FileSystemInitializer(configuration.getBlobConnectionString).fileSystem
      val latestReplyExtractorJob: LatestReplyExtractorJob = new LatestReplyExtractorJob()

      if (configParams.runDeduplication || configParams.runReplyRemoval) {
        latestReplyExtractorJob.start(runDeduplication = configParams.runDeduplication,
          runReplyRemoval = configParams.runReplyRemoval,
          parseEnronDataset = configParams.parseEnronDataset,
          numOutputPartitions = configParams.numberOfPartitions)
      } else {
        log.error("At least one of the 'run-deduplication' or 'run-reply-removal' options must be enabled")
        throw new IllegalArgumentException("At least one of the 'run-deduplication' or 'run-reply-removal' options must be enabled")
      }
    } else {
      log.error("Invalid command line arguments!")
      throw new IllegalArgumentException("Invalid command line arguments")
    }

  }

}
