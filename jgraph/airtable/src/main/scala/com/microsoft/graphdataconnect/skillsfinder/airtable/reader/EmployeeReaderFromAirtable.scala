package com.microsoft.graphdataconnect.skillsfinder.airtable.reader

import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.access.AirtableReader
import com.microsoft.graphdataconnect.skillsfinder.airtable.airtable.models.{AirtableConsultancyType, AirtableCurrentEngagement, AirtableEmployee, AirtableRole}
import com.microsoft.graphdataconnect.skillsfinder.airtable.config.GDCConfiguration
import com.microsoft.graphdataconnect.skillsfinder.airtable.converters.AirtableToBEConverter
import com.microsoft.graphdataconnect.skillsfinder.airtable.model.be.AirtableEmployeeDetails
import com.microsoft.graphdataconnect.utils.StringUtils._
import org.apache.spark.sql.{Dataset, SparkSession}

protected class EmployeeReaderFromAirtable(airtableReader: AirtableReader)
                                          (implicit sparkSession: SparkSession,
                                           configuration: GDCConfiguration) extends EmployeeReader {

  override def readAirtableData(): Dataset[AirtableEmployeeDetails] = {

    log = configuration.getLogger(classOf[EmployeeReaderFromAirtable])

    //  read whole data from Airtable
    val airtableEmployees: Map[String, AirtableEmployee] = airtableReader.readEmployees
    val roles: Map[String, AirtableRole] = airtableReader.readRoles
    val engagements: Map[String, AirtableCurrentEngagement] = airtableReader.readEngagements
    val consultancyTypes: Map[String, AirtableConsultancyType] = airtableReader.readConsultancyType

    log.info("Read Employee information. Starting to assemble data")
    //  filter out unprocessable rows
    val filteredAirtableEmployees: List[AirtableEmployee] = airtableEmployees.values.toList
      .filterNot { employee =>
        val shouldFilterOut = !"Active".equalsIgnoreCase(employee.status)
        if (shouldFilterOut) {
          log.info(s"Unprocessable record because of status: ${employee.status} Id: ${employee.id}, name: ${employee.name}, email: ${employee.emailAddress}")
        }
        shouldFilterOut
      }
      .filterNot { employee =>
        val shouldFilterOut = employee.emailAddress.isNullOrEmpty
        if (shouldFilterOut) {
          log.info(s"Unprocessable record without email. Id: ${employee.id}, name: ${employee.name}, status: ${employee.status}")
        }
        shouldFilterOut
      }
      .filterNot { employee =>
        val shouldFilterOut = employee.name.isNullOrEmpty || "N/A".equalsIgnoreCase(employee.name)
        if (shouldFilterOut) {
          log.info(s"Unprocessable record without name. Id: ${employee.id}, email: ${employee.emailAddress}, status: ${employee.status}")
        }
        shouldFilterOut
      }

    // transform Airtable raw data into airtable business entity
    val airtableEmployeesBE: List[AirtableEmployeeDetails] = filteredAirtableEmployees.map { employee =>
      AirtableToBEConverter.convertEmployee(employee, airtableEmployees, engagements, roles, consultancyTypes)
    }
    log.info(s"From a total of ${airtableEmployees.size} Airtable records, ${airtableEmployeesBE.size} remained after filtering out invalid records")

    import sparkSession.implicits._

    sparkSession.createDataset(airtableEmployeesBE)
  }
}
