package com.microsoft.graphdataconnect.skillsfinder.hrdata

import com.microsoft.graphdataconnect.logging.GdcLogger
import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.hrdata.config.{ConfigArgs, GDCConfiguration, SparkInitializer}
import com.microsoft.graphdataconnect.skillsfinder.hrdata.validator.EmployeeValidator
import com.microsoft.graphdataconnect.skillsfinder.hrdata.writer.EmployeeWriter
import org.apache.spark.sql.{Dataset, SparkSession}


object CopyHRDataToDatabase {
  private var log: GdcLogger = _

  def main(arg: Array[String]): Unit = {
    try {
      val config: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg)
      if (config.isDefined) {
        val configArgs: ConfigArgs = config.get

        run(configArgs)
      } else {
        throw new IllegalArgumentException("Invalid command line arguments")
      }
    } catch {
      case e: Throwable =>
        if (log != null) {
          log.error(e.getMessage, e)
        } else {
          e.printStackTrace()
        }
        throw e
    }
  }

  def run(configArgs: ConfigArgs): Unit = {
    implicit val sparkSession: SparkSession = SparkInitializer().session
    implicit val configuration: GDCConfiguration = GDCConfiguration(configArgs)
    configuration.setSparkSettings(sparkSession.sparkContext)

    log = configuration.getLogger(CopyHRDataToDatabase.getClass)

    val employeeReader = reader.EmployeeReader(configArgs)
    val employees: Dataset[HRDataEmployeeDetails] = employeeReader.read()

    val validEmployees = EmployeeValidator.retrieveValidEmployees(employees)

    val employeeWriter: EmployeeWriter = EmployeeWriter()
    employeeWriter.write(validEmployees)
  }
}
