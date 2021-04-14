package com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.jobs

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}

import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.model.email.gdc.{GdcBody, GdcEmail}
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.LatestReplyExtractor
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.config.GDCConfiguration
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.fs.{FilesReader, FilesWriter}
import com.microsoft.graphdataconnect.skillsfinder.replyextractor.job.helpers.{BlobFileHelper, EmailJsonConverter, TimeHelper}
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}

object LatestReplyExtractorJob {

  val latestReplyExtractor = new LatestReplyExtractor()

  def parseEmailContent(gdcEmail: GdcEmail, parseEnronDataset: Boolean, log: GdcLogger): String = {
    try {
      val content = gdcEmail.UniqueBody.Content
      val isContentTypeHtml = gdcEmail.UniqueBody.ContentType.toLowerCase.contains("html")
      latestReplyExtractor.getLatestMessageFromEmailThread(content, parseEnronDataset, isContentTypeHtml)
    } catch {
      case _: Exception => {
        log.error("Failed to parse content for email with InternetMessageId: " + gdcEmail.InternetMessageId)
        gdcEmail.UniqueBody.Content
      }
    }
  }

}

class LatestReplyExtractorJob()
                             (implicit configuration: GDCConfiguration,
                              implicit val sparkContext: SparkContext,
                              implicit val sparkSession: SparkSession,
                              implicit val fileSystem: FileSystem)

  extends BlobFileHelper with FilesReader
    with EmailJsonConverter with FilesWriter with TimeHelper {

  var log: GdcLogger = _

  def start(runDeduplication: Boolean, runReplyRemoval: Boolean, parseEnronDataset: Boolean, numOutputPartitions: Int): Unit = {
    log = configuration.getLogger(classOf[LatestReplyExtractorJob])
    val filesWithPath: Seq[String] = listDirectory(configuration.getRawEmailsFullPath)

    val reflection_schema: StructType = ScalaReflection.schemaFor[GdcEmail].dataType.asInstanceOf[StructType]

    import sparkSession.implicits._
    log.info("Reading emails from input directory")

    val emailDf: DataFrame = sparkSession.sqlContext.read.schema(reflection_schema).json(filesWithPath: _*)
    val emailDs = emailDf.as[GdcEmail]


    var uniqueEmails = emailDs

    if (runDeduplication) {
      log.info("Running email deduplication")
      uniqueEmails = uniqueEmails.dropDuplicates("InternetMessageId")
    }
    if (runReplyRemoval) {
      val loggerWorkspaceId = configuration.getLoggerWorkspaceId()
      val logType = configuration.getLogType()
      val sharedKey = configuration.getLoggerWorkspaceKey()
      log.info("Running email latest reply extraction")
      uniqueEmails = uniqueEmails.mapPartitions { partition =>
        val logger = GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = loggerWorkspaceId,
          logAnalyticsSharedKey = sharedKey,
          clazz = classOf[LatestReplyExtractorJob],
          logType = logType
        )
        partition.map { gdcEmail =>
          gdcEmail.copy(UniqueBody = GdcBody(ContentType = gdcEmail.UniqueBody.ContentType,
            Content = LatestReplyExtractorJob.parseEmailContent(gdcEmail, parseEnronDataset, logger)))
        }
      }
    }

    log.info("Writing result to output directory")
    val destinationFilePath = formatOutputDirectoryName(configuration.getProcessedEmailsFullPath)

    uniqueEmails.repartition(numOutputPartitions).write.json(destinationFilePath)
  }

  def formatOutputDirectoryName(basePath: String): String = {
    val datetime = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
    val formattedDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(datetime)
    val formattedTime = DateTimeFormatter.ofPattern("hhmmss").format(datetime)
    basePath + "/" + formattedDate + "T" + formattedTime
  }

}
