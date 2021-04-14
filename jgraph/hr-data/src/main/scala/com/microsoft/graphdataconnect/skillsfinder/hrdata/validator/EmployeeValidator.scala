package com.microsoft.graphdataconnect.skillsfinder.hrdata.validator

import com.microsoft.graphdataconnect.model.userdetails.source.hrdata.HRDataEmployeeDetails
import com.microsoft.graphdataconnect.skillsfinder.hrdata.config.GDCConfiguration
import org.apache.spark.sql.{Dataset, SparkSession}

object EmployeeValidator {
  def retrieveValidEmployees(employees: Dataset[HRDataEmployeeDetails])
                            (implicit sparkSession: SparkSession, configuration: GDCConfiguration): Dataset[HRDataEmployeeDetails] = {
    employees.cache()
    val validEmployees = if (configuration.failFastOnCorruptData()) {
      val invalidRecordsCount = employees.filter { employee =>
        employee.mail == null || employee.mail.isEmpty || employee.name == null || employee.name.isEmpty
      }.count()
      if (invalidRecordsCount > 0) {
        throw new RuntimeException("Job failed due to invalid records. Each record must contain at least an employee email address and name")
      }
      import sparkSession.implicits._
      if (employees.map(_.mail).distinct().count() < employees.count()) {
        throw new RuntimeException("Job failed due to invalid records. All records must contain distinct employee email addresses")
      }
      employees
    } else {
      employees.filter { employee =>
        employee.mail != null && !employee.mail.isEmpty && employee.name != null && !employee.name.isEmpty
      }.dropDuplicates("mail")
    }
    employees.unpersist()

    validEmployees
  }
}
