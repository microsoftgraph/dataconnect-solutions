package com.microsoft.graphdataconnect.skillsfinder.airtable

import java.io.File

import com.microsoft.graphdataconnect.model.configs.IngestionMode
import com.microsoft.graphdataconnect.skillsfinder.airtable.config.ConfigArgs
import org.apache.commons.io.FileUtils
import org.junit.{Assert, Test}

class AirtableToHRDataTest {

  // Please not that at runtime, the test resources are relative to gdc/jgraph/airtable/target/test-classes,
  // while the application resources are relative to gdc/jgraph/airtable/target/classes, so any changes to the
  // resource files requires building the module first for them to be "visible" in the target folder
  protected def runTestFlowBasedOnInputAndOutputFiles(testResourceFilesBaseFolder: String): Unit = {
    val outputFolderPath = getClass.getResource(testResourceFilesBaseFolder).getPath + "/output"
    val configArgs = ConfigArgs(
      inputStorageAccountName = None,
      inputContainer = None,
      inputFolderPath = Some(getClass.getResource(testResourceFilesBaseFolder).getPath + "/input"),
      outputStorageAccountName = None,
      outputContainer = None,
      outputFolderPath = outputFolderPath,
      isDevMode = true,
      ingestionMode = IngestionMode.Sample)

    AirtableToHRData.convert(configArgs)

    val outputDir = new File(outputFolderPath)
    val actualOutputFile = outputDir.listFiles().filter(_.getName.startsWith("part")).head
    val expectedOutputFile = new File(getClass.getResource(testResourceFilesBaseFolder + "/expected-output/hr-data.csv").getPath)
    Assert.assertTrue(FileUtils.contentEqualsIgnoreEOL(actualOutputFile, expectedOutputFile, null))
  }

  @Test
  def strictlyNecessaryInputColumnsTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-01")
  }

  @Test
  def inputColumnsInDifferentOrder(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-02")
  }

  @Test
  def unnecessaryInputColumnsTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-03")
  }

  @Test
  def strictlyNecessaryInputColumnsWithMinimalDataTest(): Unit = {
    runTestFlowBasedOnInputAndOutputFiles("/test-data/test-04")
  }

}
