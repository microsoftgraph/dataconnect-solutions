import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.config.GDCConfiguration
import org.apache.commons.lang3.StringUtils
/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import org.apache.spark.SparkContext

class TestConfig extends GDCConfiguration {

  override def setSparkSettings(sparkContext: SparkContext): Unit = {
  }

  override def getBlobConnectionString: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/replyextraction_job/emails/emails1.json").getPath, "target") + "src/test/resources/replyextraction_job/emails";
  }

  override def getRawEmailsFullPath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/replyextraction_job/emails/emails1.json").getPath, "target") + "src/test/resources/replyextraction_job/emails";
  }

  override def getProcessedEmailsFullPath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/replyextraction_job/emails/emails1.json").getPath, "target") + "src/test/resources/replyextraction_job/actual_result/emails_processed";
  }

  def getProcessedEmailsBasePath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/replyextraction_job/emails/emails1.json").getPath, "target") + "src/test/resources/replyextraction_job/actual_result";
  }

  def getReplyExtractionExpectedResultFullPath: String = {
    StringUtils.substringBefore(GDCConfiguration.getClass.getResource("/replyextraction_job/emails/emails1.json").getPath, "target") + "src/test/resources/replyextraction_job/expected_result";
  }

  override def getLogger(clazz: Class[_]): GdcLogger = GdcLoggerFactory.getLogger(clazz = clazz)

  override def getLoggerWorkspaceId(): String = ""

  override def getLogType(): String = ""

  override def getLoggerWorkspaceKey(): String = ""
}
