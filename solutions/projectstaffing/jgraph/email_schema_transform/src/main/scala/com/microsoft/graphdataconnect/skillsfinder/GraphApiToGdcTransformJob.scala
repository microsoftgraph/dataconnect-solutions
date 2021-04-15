package com.microsoft.graphdataconnect.skillsfinder

import com.microsoft.graphdataconnect.model.email.gdc
import com.microsoft.graphdataconnect.model.email.gdc._
import com.microsoft.graphdataconnect.model.email.graph_api.{EmailAddress, GraphApiEmail}
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

object GraphApiToGdcTransformJob {

  def emailAddressesGraphApiToEmailAddressesGDC(emailAddressGraphApi: Seq[EmailAddress]): Seq[gdc.EmailAddress] =
    emailAddressGraphApi.map(emailAddressGraphApi => emailAddressGraphApi.emailAddress)
      .map(emailAddressFieldsGraphApi => gdc.EmailAddress(GdcEmailAddressFields(emailAddressFieldsGraphApi.name, emailAddressFieldsGraphApi.address)))


  def emailAddressesGraphApiToEmailAddressesGDC(graphApiEmailAddresses: Option[Seq[EmailAddress]]): Seq[gdc.EmailAddress] = {
    graphApiEmailAddresses.map(graphApiEmailAddresses => emailAddressesGraphApiToEmailAddressesGDC(graphApiEmailAddresses)).getOrElse(List())
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("GaToGdcTransform")
      .config("spark.master", "local")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN") // Greatly reduce spark logs to boost readability

    // Build a Spark compatible schema from the Email case class via reflection
    val reflection_schema: StructType = ScalaReflection.schemaFor[GraphApiEmail].dataType.asInstanceOf[StructType]

    // Import the implicits of the current spark session to allow using implicit encoders
    import spark.implicits._

    val path_to_partial_json: String = GraphApiToGdcTransformJob.getClass.getResource("/email.json").getPath
    val df: DataFrame = spark.read.schema(reflection_schema).option("multiLine", true).json(path_to_partial_json)
    //  df.show(false)

    // This Spark dataset has the proper format and content to allow experimenting for transformation into GDC mail format
    val graphApiDS: Dataset[GraphApiEmail] = df.as[GraphApiEmail]
    //    graphApiDS.show(false)

    val GdcDS: Dataset[GdcEmail] = graphApiDS.map(graphApiEmail => GdcEmail(Id = graphApiEmail.id,
      CreatedDateTime = graphApiEmail.createdDateTime,
      LastModifiedDateTime = graphApiEmail.lastModifiedDateTime,
      ChangeKey = graphApiEmail.changeKey,
      Categories = graphApiEmail.categories,
      ReceivedDateTime = graphApiEmail.receivedDateTime,
      SentDateTime = graphApiEmail.sentDateTime,
      HasAttachments = graphApiEmail.hasAttachments,
      InternetMessageId = graphApiEmail.internetMessageId,
      Subject = graphApiEmail.subject,
      Importance = graphApiEmail.importance,
      ParentFolderId = graphApiEmail.parentFolderId,
      ConversationId = graphApiEmail.conversationId,
      IsDeliveryReceiptRequested = graphApiEmail.isDeliveryReceiptRequested,
      IsReadReceiptRequested = graphApiEmail.isDeliveryReceiptRequested,
      IsRead = graphApiEmail.isRead,
      IsDraft = graphApiEmail.isDraft,
      WebLink = graphApiEmail.webLink,
      UniqueBody = if (graphApiEmail.body != null) GdcBody(graphApiEmail.body.contentType, graphApiEmail.body.content) else GdcBody("", ""),
      Sender = GdcSender(GdcEmailAddressFields(graphApiEmail.sender.emailAddress.name, graphApiEmail.sender.emailAddress.address)),
      From = GdcFrom(GdcEmailAddressFields(graphApiEmail.from.emailAddress.name, graphApiEmail.from.emailAddress.address)),
      ToRecipients = emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.toRecipients),
      CcRecipients = emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.ccRecipients),
      BccRecipients = emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.bccRecipients),
      ReplyTo = emailAddressesGraphApiToEmailAddressesGDC(graphApiEmail.replyTo),
      ODataType = graphApiEmail.odata
    ))

    GdcDS.write.mode(SaveMode.Overwrite).json("/tmp/email_gdc.json")
  }

}
