package com.microsoft.graphdataconnect.skillsfinder.hrdata

import java.time.{LocalDateTime, ZoneOffset}

import com.microsoft.graphdataconnect.model.configs.IngestionMode
import com.microsoft.graphdataconnect.model.userdetails.db.HRDataEmployeeProfile
import com.microsoft.graphdataconnect.skillsfinder.hrdata.config.{ConfigArgs, Constants, GDCConfiguration, SparkInitializer}
import org.apache.spark.SparkException
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.lit
import org.junit.{Assert, Test}


class CopyHRDataToDatabaseTest {

  // Please not that at runtime, the test resources are relative to gdc/jgraph/hr-data/target/test-classes,
  // while the application resources are relative to gdc/jgraph/hr-data/target/classes, so any changes to the
  // resource files requires building the module first for them to be "visible" in the target folder
  protected def runTestFlowBasedOnInputAndOutputFiles(testResourceFilesBaseFolder: String, failFastOnCorruptData: Boolean): Unit = {
    val outputPath = getClass.getResource(testResourceFilesBaseFolder).getPath + "/expected-output"

    val configArgs = ConfigArgs(
      inputStorageAccountName = None,
      inputContainer = None,
      inputFolderPath = getClass.getResource(testResourceFilesBaseFolder).getPath + "/input",
      isDevMode = true,
      ingestionMode = IngestionMode.Sample,
      failFastOnCorruptData = failFastOnCorruptData
    )

    val testStartTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    CopyHRDataToDatabase.run(configArgs)

    val sparkSession = SparkInitializer().session
    val conf = GDCConfiguration(configArgs)

    val jdbcUrl = conf.getJdbcUrl()
    val connectionProperties = conf.getDatabaseConnectionProperties()

    import sparkSession.implicits._
    val actualDataDS: Dataset[HRDataEmployeeProfile] = sparkSession.read
      .jdbc(jdbcUrl, Constants.HR_DATA_EMPLOYEE_TABLE_NAME, connectionProperties)
      .as[HRDataEmployeeProfile]
    println("Actual data:")
    actualDataDS.show(false)

    val actualData: Array[HRDataEmployeeProfile] = actualDataDS.collect()


    val version: String = actualData.head.version
    // Since the actual version can have a variable number of digits for milliseconds between test runs, it can cause parsing
    // problems if a pattern with the wrong number of millis is used such as "yyyy-MM-dd HH:mm:ss.SSS". We therefore convert the
    // string to a standard ISO 8601 format, which can be parsed regardless of how many millis digits are used, without specifying a pattern
    val versionIso8601Format = version.replace(" ", "T")
    val versionTimestamp = LocalDateTime.parse(versionIso8601Format)
    Assert.assertTrue("Data version should depend on job execution time", versionTimestamp.isAfter(testStartTime))
    actualData.foreach(hrDataProfile => Assert.assertTrue("All DB records should have the same version", hrDataProfile.version == version))

    val expectedDataDS: Dataset[HRDataEmployeeProfile] = sparkSession.read.option("header", true).csv(outputPath)
      .withColumn("version", lit(version))
      .as[HRDataEmployeeProfile]
    println("Expected data:")
    expectedDataDS.show(false)
    val expectedData: Array[HRDataEmployeeProfile] = expectedDataDS.collect()

    Assert.assertTrue("Data written to DB should have the expected content", actualData.sameElements(expectedData))

  }


  @Test
  def completeInputDataTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-01", false)
  }

  @Test
  def minimalInputDataTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-02", false)
  }

  @Test
  def ignoreRowWithMissingNameTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-03", false)
  }

  @Test
  def ignoreRowWithMissingEmailTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-04", false)
  }

  @Test
  def ignoreRowsWithDuplicateEmailTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-05", false)
  }

  @Test
  def trimWhitespacesTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-06", false)
  }

  @Test
  def ignoreCorruptRowsTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-07", false)
  }

  @Test(expected = classOf[RuntimeException])
  def failOnMissingNameTest(): Unit = {
    val configArgs = ConfigArgs(
      inputStorageAccountName = None,
      inputContainer = None,
      inputFolderPath = getClass.getResource("/test-data/test-03").getPath + "/input",
      isDevMode = true,
      ingestionMode = IngestionMode.Sample,
      failFastOnCorruptData = true
    )
    CopyHRDataToDatabase.run(configArgs)
  }

  @Test(expected = classOf[RuntimeException])
  def failOnMissingEmailTest(): Unit = {
    val configArgs = ConfigArgs(
      inputStorageAccountName = None,
      inputContainer = None,
      inputFolderPath = getClass.getResource("/test-data/test-04").getPath + "/input",
      isDevMode = true,
      ingestionMode = IngestionMode.Sample,
      failFastOnCorruptData = true
    )

    CopyHRDataToDatabase.run(configArgs)
  }

  @Test(expected = classOf[RuntimeException])
  def failOnDuplicateEmailTest(): Unit = {
    val configArgs = ConfigArgs(
      inputStorageAccountName = None,
      inputContainer = None,
      inputFolderPath = getClass.getResource("/test-data/test-05").getPath + "/input",
      isDevMode = true,
      ingestionMode = IngestionMode.Sample,
      failFastOnCorruptData = true
    )
    CopyHRDataToDatabase.run(configArgs)
  }

  @Test(expected = classOf[SparkException])
  def failOnCorruptRowsTest(): Unit = {
    val configArgs = ConfigArgs(
      inputStorageAccountName = None,
      inputContainer = None,
      inputFolderPath = getClass.getResource("/test-data/test-07").getPath + "/input",
      isDevMode = true,
      ingestionMode = IngestionMode.Sample,
      failFastOnCorruptData = true
    )
    CopyHRDataToDatabase.run(configArgs)
  }

}
