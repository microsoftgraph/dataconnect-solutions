package com.microsoft.graphdataconnect.skillsfinder

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.microsoft.graphdataconnect.model.email.gdc
import com.microsoft.graphdataconnect.model.email.gdc._
import com.microsoft.graphdataconnect.skillsfinder.model.email.enron.{MimeEmail, RawEnronEmail}
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}

object EnronMimeToGdcTransformJob {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("GaToGdcTransform")
      .config("spark.master", "local")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN") // Greatly reduce spark logs to boost readability

    val inputCsvPath = if (args.length > 0) {
      args(0)
    } else {
      //            getClass.getResource("/enron_email_sample.csv").getPath
      getClass.getResource("/enron_no_space_after_field_name.csv").getPath
    }

    val outputPath = if (args.length > 1) {
      args(1)
    } else {
      "/tmp/enron_emails_GDC_schema"
    }

    val defaultOutputFormat = "json"
    val outputFormat = if (args.length > 2) {
      val proposedFormat = args(2)
      if (proposedFormat.toLowerCase() == "json" || proposedFormat.toLowerCase() == "parquet") {
        proposedFormat.toLowerCase()
      } else {
        defaultOutputFormat
      }
    } else {
      defaultOutputFormat
    }

    val numPartitions: Int = if (args.length > 3) {
      args(3).toInt
    } else {
      if (outputFormat == "json") {
        32
      } else { //Parquet
        8
      }
    }


    // Import the implicits of the current spark session to allow using implicit encoders
    import spark.implicits._

    println("Loading data from " + inputCsvPath)
    val rawEnronEmailsDf = spark.read.options(Map("header" -> "true", "escape" -> "\"", "multiLine" -> "true")).csv(inputCsvPath)
    val rawEnronEmailsDs = rawEnronEmailsDf.as[RawEnronEmail]

    val mimeEmailDS: Dataset[MimeEmail] = rawEnronEmailsDs.flatMap(enronEmail => mimeMultilineTextToMimeCaseClass(enronEmail.message))

    //    mimeEmailDS.show(false)

    val gdcEmailDS = mimeEmailDS.map { (mimeEmail: MimeEmail) =>

      // The initial date format was "EEE, d MMM yyyy HH:mm:ss X (z)" and so, included the day of the week.
      // However, for some records the day of the week did not match the calendar date.
      // To prevent conversion error, we'll ignore the day of the week
      val dateStrWithoutDayOfWeek = mimeEmail.date.substring(5)
      val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss X (z)")
      val zdt = ZonedDateTime.parse(dateStrWithoutDayOfWeek, dtf)
      val date = zdt.toInstant.toString

      GdcEmail(Id = mimeEmail.messageId,
        CreatedDateTime = date,
        LastModifiedDateTime = date,
        //      ChangeKey = graphApiEmail.changeKey,
        //      Categories = mimeEmail.categories,
        ReceivedDateTime = date,
        SentDateTime = date,
        //      HasAttachments = mimeEmail.hasAttachments,
        InternetMessageId = mimeEmail.messageId,
        Subject = mimeEmail.subject,
        //      Importance = mimeEmail.importance,
        ParentFolderId = mimeEmail.xFolder,
        //      ConversationId = mimeEmail.conversationId,
        //      IsDeliveryReceiptRequested = mimeEmail.isDeliveryReceiptRequested,
        //      IsReadReceiptRequested = mimeEmail.isDeliveryReceiptRequested,
        //      IsRead = mimeEmail.isRead,
        //      IsDraft = mimeEmail.isDraft,
        //      WebLink = mimeEmail.webLink,
        UniqueBody = GdcBody(mimeEmail.contentType, mimeEmail.body),
        Sender = GdcSender(GdcEmailAddressFields(mimeEmail.xFrom, mimeEmail.from)),
        From = GdcFrom(GdcEmailAddressFields(mimeEmail.xFrom, mimeEmail.from)),
        ToRecipients = zipAddressesAndNames(mimeEmail.to, mimeEmail.xTo),
        CcRecipients = zipAddressesAndNames(mimeEmail.cc, mimeEmail.xCc),
        BccRecipients = zipAddressesAndNames(mimeEmail.bcc, mimeEmail.xBcc)
        //      ReplyTo = emailAddressesGraphApiToEmailAddressesGDC(mimeEmail.replyTo),
        //      ODataType = mimeEmail.odata
      )
    }

    println(s"Writing Enron data with GDC schema in $outputFormat format as $numPartitions partitions in $outputPath")
    if (outputFormat == "json") {
      gdcEmailDS.repartition(numPartitions).write.mode(SaveMode.Overwrite).json(outputPath)
    } else {
      gdcEmailDS.repartition(numPartitions).write.mode(SaveMode.Overwrite).parquet(outputPath)
    }

  }

  def mimeMultilineTextToMimeCaseClass(message: String): Option[MimeEmail] = {
    // Some useful regex concepts that were used:
    //  (?s) "single line mode" modifier - allows the dot to match all characters, including line breaks
    //  (?:pattern) used to group tokens without creating a capturing group. We use this as (?:pattern)? to define optional complex patterns
    val pattern = "(?s)Message-ID:(.*)\nDate:(.*)\nFrom:(.*)\nTo:(.*)\nSubject:([^\n]*)\n(?:Cc:(.*)\n)?Mime-Version:(.*)\nContent-Type:(.*)\nContent-Transfer-Encoding:([^\n]*)\n(?:Bcc:(.*)\n)?X-From:(.*)\nX-To:(.*)\nX-cc:(.*)\nX-bcc:(.*)\nX-Folder:(.*)\nX-Origin:(.*)\nX-FileName:([^\n]*)\n(.*)".r

    message match {
      case pattern(messageId, date, from, to, subject, cc, mimeVersion, contentType, contentTransferEncoding, bcc, xFrom, xTo, xCc, xBcc, xFolder, xOrigin, xFilename, body) =>
        Some(MimeEmail(messageId.trim, date.trim, from.trim,
          processMultiLineList(to), subject.trim, processMultiLineList(cc),
          mimeVersion.trim, contentType.trim, contentTransferEncoding.trim,
          processMultiLineList(bcc),
          xFrom.trim, processNamesAndEmailsMixedListField(xTo),
          processNamesAndEmailsMixedListField(xCc), processNamesAndEmailsMixedListField(xBcc),
          xFolder.trim, xOrigin.trim, xFilename.trim,
          body.trim))
      case _ => None
    }
  }

  def processMultiLineList(str: String): List[String] = {
    if (str == null) {
      List()
    } else {
      str.split(",").map(_.trim).map(_.replaceAll("\t", "")).map(_.replaceAll("\n", "")).toList
    }
  }

  def processNamesAndEmailsMixedListField(field: String): Seq[String] = {
    val commaSeparatedElements: List[String] = processXReceiversField(field)
    commaSeparatedElements
  }

  def processXReceiversField(str: String): List[String] = {
    if (str == null) {
      List()
    } else {
      // Certain recipient names have formats which contain commas. Whe must process these to allow splitting the
      // recipients list by comma without breaking these recipients apart.
      // Since the Enron data contains no "^" characters, we ca temporarily use that instead of the comma during the split
      str.replaceAll("\t", " ")
        .replaceAll("\n", "")
        // handle recipients like "Hogan, Irena D. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Ihogan>"
        .replaceAll("(^\"?|.*?, *)([a-zA-Z .]+),([a-zA-Z .]+ </O=E[^>]*>)", "$1$2^$3")
        // handle recipients like "\"Varley, Peter\" <PVarley@GPC.CA>" etc
        .replaceAll("(^\"?|.*?, *)(\"[a-zA-Z \\-().]+),([a-zA-Z \\-().]+\" <.*?>)", "$1$2^$3")
        .replaceAll("(^\"?|.*?, *)(\"\"[a-zA-Z \\-().]+),([a-zA-Z \\-().]+\"\" <.*?>)", "$1$2^$3")
        .replaceAll("(^\"?|.*?, *)(\'[a-zA-Z \\-().]+),([a-zA-Z \\-().]+\' <.*?>)", "$1$2^$3")
        .replaceAll("(^\"?|.*?, *)(\'\'[a-zA-Z \\-().]+),([a-zA-Z \\-().]+\'\' <.*?>)", "$1$2^$3")
        .replaceAll("(^\"?|.*?, *)(\"\'[a-zA-Z \\-().]+),([a-zA-Z \\-().]+\'\" <.*?>)", "$1$2^$3")
        .split(",")
        .map(_.replaceAll("\\^", ",")) // Replace the temporary "^" character with a comma again
        .map(_.trim).toList
    }
  }

  def zipAddressesAndNames(addresses: Seq[String], names: Seq[String]): Seq[EmailAddress] = {
    val listOfAddressesAndNames: Seq[(String, String)] = addresses.zipAll(names, "", "")
    listOfAddressesAndNames.map(addressAndName => gdc.EmailAddress(GdcEmailAddressFields(addressAndName._2, addressAndName._1)))
  }
}
