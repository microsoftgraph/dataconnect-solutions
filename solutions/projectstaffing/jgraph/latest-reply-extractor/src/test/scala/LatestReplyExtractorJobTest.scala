import java.io.File

import com.microsoft.graphdataconnect.model.email.gdc.GdcEmail
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.fs.{FileSystemInitializer, SparkInitializer}
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.helpers.BlobFileHelper
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.jobs.LatestReplyExtractorJob
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.{After, Assert, Test}
import org.slf4j.{Logger, LoggerFactory}

/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import scala.reflect.io.Directory

object LatestReplyExtractorJobTest {

  implicit val configuration: TestConfig = new TestConfig()
  implicit val sparkSession: SparkSession = SparkInitializer().session
  implicit val sparkContext: SparkContext = sparkSession.sparkContext
  configuration.setSparkSettings(sparkContext)
  implicit val fileSystem: FileSystem = FileSystemInitializer(configuration.getBlobConnectionString).fileSystem

  val latestReplyExtractorJob: LatestReplyExtractorJob = new LatestReplyExtractorJob()

}

class LatestReplyExtractorJobTest extends BlobFileHelper {

  override implicit val fileSystem: FileSystem = LatestReplyExtractorJobTest.fileSystem
  val log: Logger = LoggerFactory.getLogger(classOf[LatestReplyExtractorJobTest])

  @After
  def cleanUp(): Unit = {
    val directory: Directory = new Directory(new File(LatestReplyExtractorJobTest.configuration.getProcessedEmailsBasePath))
    directory.deleteRecursively()
  }

  @Test
  def testJob(): Unit = {
    LatestReplyExtractorJobTest.latestReplyExtractorJob.start(runDeduplication = true, runReplyRemoval = true, parseEnronDataset = true, numOutputPartitions = 1)

    val reflectionSchema: StructType = ScalaReflection.schemaFor[GdcEmail].dataType.asInstanceOf[StructType]
    log.info("The emails from " + LatestReplyExtractorJobTest.configuration.getProcessedEmailsBasePath + " directory, have to be the same (number and content) with the emails" +
      " from " + LatestReplyExtractorJobTest.configuration.getReplyExtractionExpectedResultFullPath + " directory.")

    import LatestReplyExtractorJobTest.sparkSession.implicits._

    val resultFilesPath: Seq[String] = listDirectory(LatestReplyExtractorJobTest.configuration.getProcessedEmailsBasePath, recursive = true)
    val resultEmailDf: DataFrame = LatestReplyExtractorJobTest.sparkSession.sqlContext.read.schema(reflectionSchema).json(resultFilesPath: _*)
    val resultEmailDs = resultEmailDf.as[GdcEmail];

    val expectedResultFilesPath: Seq[String] = listDirectory(LatestReplyExtractorJobTest.configuration.getReplyExtractionExpectedResultFullPath, recursive = true)
    val expectedResultEmailDf: DataFrame = LatestReplyExtractorJobTest.sparkSession.sqlContext.read.schema(reflectionSchema).json(expectedResultFilesPath: _*)
    val expectedResultEmailDs = expectedResultEmailDf.as[GdcEmail]

    val resultEmailIds: Seq[GdcEmail] = resultEmailDs.collect().toList
    val expectedResultEmailIds: Seq[GdcEmail] = expectedResultEmailDs.collect().toList

    Assert.assertFalse("There are more emails than expected after running the job", resultEmailIds.size > expectedResultEmailIds.size)
    Assert.assertFalse("There are more emails than expected after running the job", resultEmailIds.size < expectedResultEmailIds.size)

    val missingRowsFromResult = resultEmailIds.toSet.diff(expectedResultEmailIds.toSet)

    val incorrectResultRows = expectedResultEmailIds.toSet.diff(resultEmailIds.toSet)

    Assert.assertTrue("The emails with the following ids are missing from the result " + missingRowsFromResult, missingRowsFromResult.isEmpty)
    Assert.assertTrue("The emails with the following ids are incorrect" + incorrectResultRows, incorrectResultRows.isEmpty)

    val expectedResultIdContentMap: Map[String, String] = expectedResultEmailDs.collect().map(gdcEmail => gdcEmail.InternetMessageId -> gdcEmail.UniqueBody.Content).toMap
    val resultIdContentMap: Map[String, String] = resultEmailDs.collect().map(gdcEmail => gdcEmail.InternetMessageId -> gdcEmail.UniqueBody.Content).toMap

    expectedResultIdContentMap.foreach { case (id, content) => Assert.assertEquals(s"For the email with InternetMessageID $id the content is not parsed correctly.", content, resultIdContentMap.getOrElse(id, "")) }
  }


}
