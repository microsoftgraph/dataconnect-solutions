package com.microsoft.graphdataconnect.skillsfinder.experiment

import com.microsoft.graphdataconnect.model.email.graph_api.GraphApiEmail
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType


object GraphApiEmailFormatExperiments {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("GaToGdcTransform")
      .config("spark.master", "local")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN") // Greatly reduce spark logs to boost readability

    // Build a Spark compatible schema from the Email case class via reflection
    val reflection_schema = ScalaReflection.schemaFor[GraphApiEmail].dataType.asInstanceOf[StructType]

    // Import the implicits of the current spark session to allow using implicit encoders
    import spark.implicits._

    val path_to_partial_json = GraphApiEmailFormatExperiments.getClass.getResource("/email_empty_cc.json").getPath
    val df = spark.read.schema(reflection_schema).option("multiLine", true).json(path_to_partial_json)
    df.show(false)

    // This Spark dataset has the proper format and content to allow experimenting for transformation into GDC mail format
    val ds = df.as[GraphApiEmail]


    // The following sections are about displaying useful information about the email data frame and dataset
    ds.printSchema()
    println(s"Schemas are identical (df vs ds): ${df.schema == ds.schema}")

    val path_to_full_json = GraphApiEmailFormatExperiments.getClass.getResource("/email.json").getPath
    val df_with_inferred_schema = spark.read.option("multiLine", true).json(path_to_full_json)
    // In the inferred schema all the fields are in alphabetical order, while the reflection schema uses case class field order
    println(s"Schemas are identical (inferred vs reflection): ${df_with_inferred_schema.schema == df.schema}")

    ds.map(email => (email.isDeliveryReceiptRequested.isDefined, email.isDeliveryReceiptRequested == null, email.isDeliveryReceiptRequested)).show
    ds.map(email => (email.isRead.isDefined, email.isRead == null, email.isRead)).show
    ds.map(email => (email.ccRecipients.isEmpty, email.ccRecipients == null, email.ccRecipients)).show
    ds.map(email => (email.bccRecipients == null, email.bccRecipients)).show


    spark.close()
  }

}
