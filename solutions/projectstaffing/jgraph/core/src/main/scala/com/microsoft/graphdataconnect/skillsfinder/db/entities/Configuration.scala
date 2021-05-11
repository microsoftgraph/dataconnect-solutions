/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.model.configs.{AzureSearchEmployeesIndexConfiguration, ConfigurationTypes}
import com.microsoft.graphdataconnect.skillsfinder.exceptions.{ComposedPKVersionNotFoundException, ResourceNotFoundException}
import com.microsoft.graphdataconnect.skillsfinder.models.configs
import com.microsoft.graphdataconnect.skillsfinder.models.configs._
import com.microsoft.graphdataconnect.skillsfinder.utils.ConverterUtils
import com.microsoft.graphdataconnect.skillsfinder.utils.TypeUtils._
import com.microsoft.graphdataconnect.utils.TimeUtils
import com.vladmihalcea.hibernate.`type`.json.JsonStringType
import javax.persistence._
import org.hibernate.annotations.{Type, TypeDef}

@Table(name = "configurations")
@Entity
@TypeDef(name = "json", typeClass = classOf[JsonStringType])
class Configuration {

  @Id
  @Column(name = "type")
  var `type`: String = _

  @Column(name = "configs", columnDefinition = "NVARCHAR")
  @Type(`type` = "json")
  var configs: Map[String, Any] = _
}

object Configuration {

  def apply(hdiConfiguration: HDInsightConfiguration): Configuration = {
    val config = new Configuration()
    config.`type` = ConfigurationTypes.HDInsight
    config.configs = ConverterUtils.caseClassToMap(hdiConfiguration)
    config
  }

  def apply(databricksConfiguration: DatabricksConfiguration): Configuration = {
    val config = new Configuration()
    config.`type` = ConfigurationTypes.Databricks
    config.configs = ConverterUtils.caseClassToMap(databricksConfiguration)
    config
  }

  def apply(azureBlobStorageCredential: AzureBlobStorageCredential): Configuration = {
    val config = new Configuration()
    config.`type` = ConfigurationTypes.AzureBlobStorage
    config.configs = ConverterUtils.caseClassToMap(azureBlobStorageCredential)
    config
  }

  def apply(azureActiveDirectory: OAuthAzureActiveDirectoryConfiguration): Configuration = {
    val config = new Configuration()
    config.`type` = ConfigurationTypes.AzureActiveDirectory
    config.configs = ConverterUtils.caseClassToMap(azureActiveDirectory)
    config
  }

  def apply(employeeProfileVersion: EmployeeProfileVersionConfiguration): Configuration = {
    val config = new Configuration()
    config.`type` = ConfigurationTypes.LatestVersionOfEmployeeProfile
    config.configs = Map("date" -> TimeUtils.localDateTimeToString(employeeProfileVersion.date))
    config
  }

  def apply(hrDataEmployeeProfileVersion: HRDataEmployeeProfileVersionConfiguration): Configuration = {
    val config = new Configuration()
    config.`type` = ConfigurationTypes.LatestVersionOfHRDataEmployeeProfile
    config.configs = Map("date" -> TimeUtils.localDateTimeToString(hrDataEmployeeProfileVersion.date))
    config
  }

  def toHDInsightConfiguration(configuration: Configuration): HDInsightConfiguration = {
    val clusterId = configuration.configs.get("clusterId").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("HDInsight cluster id not found")
    val clusterName = configuration.configs.get("clusterName").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("HDInsight cluster name not found")
    val address = configuration.configs.get("address").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("HDInsight address not found")
    val user = configuration.configs.get("user").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("HDInsight user not found")
    val password = configuration.configs.get("password").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("HDInsight password not found")
    HDInsightConfiguration(clusterId, clusterName, address, user, password)
  }

  def toDatabricksConfiguration(configuration: Configuration): DatabricksConfiguration = {
    val address = configuration.configs.get("address").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Databricks address not found")
    val apiToken = configuration.configs.get("apiToken").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Databricks api token not found")
    val orgId = configuration.configs.get("orgId").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Databricks organization id not found")
    val clusterId = configuration.configs.get("clusterId").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Databricks cluster id not found")
    val port = configuration.configs.get("port").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Databricks port not found")
    DatabricksConfiguration(address, apiToken, orgId, clusterId, port)
  }

  def toAzureBlobStorageConfiguration(configuration: Configuration): AzureBlobStorageCredential = {
    val azureStorageAccount = configuration.configs.get("storageAccount").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Azure storage not found")
    val azureStorageKey = configuration.configs.get("storageKey").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Azure storage key not found")
    AzureBlobStorageCredential(azureStorageAccount, azureStorageKey)
  }

  def toAzureActiveDirectoryConfiguration(configuration: Configuration): OAuthAzureActiveDirectoryConfiguration = {
    val azureADClientId = configuration.configs.get("clientId").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Azure Active Directory Client Id not found")
    val azureADTenantId = configuration.configs.get("tenantId").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Azure Active Directory Tenant Id not found")
    val azureADSecretKey = configuration.configs.get("clientSecret").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Azure Active Directory Client Secret not found")
    OAuthAzureActiveDirectoryConfiguration(clientId = azureADClientId, tenantId = azureADTenantId, clientSecret = azureADSecretKey)
  }

  def toAzureSearchEmployeesIndexConfiguration(configuration: Configuration): AzureSearchEmployeesIndexConfiguration = {
    val employeesIndexName = configuration.configs.get("employeesIndexName").map(_.asInstanceOf[String]) getOrFail new ResourceNotFoundException("Azure search employees index name not found")
    AzureSearchEmployeesIndexConfiguration(employeesIndexName)
  }

  def toLatestVersionOfEmployeeProfile(configuration: Configuration): EmployeeProfileVersionConfiguration = {
    configuration.configs.get("date") match {
      case Some(value) =>
        value match {
          case str: String => configs.EmployeeProfileVersionConfiguration(TimeUtils.timestampToLocalDateTime(str))
          case dt: LocalDateTime => configs.EmployeeProfileVersionConfiguration(dt)
        }
      case _ => throw new ComposedPKVersionNotFoundException("Employee profile latest version not found")
    }
  }

  def toLatestVersionOfHRDataEmployeeProfile(configuration: Configuration): HRDataEmployeeProfileVersionConfiguration = {
    configuration.configs.get("date") match {
      case Some(value) =>
        value match {
          case str: String => configs.HRDataEmployeeProfileVersionConfiguration(TimeUtils.timestampToLocalDateTime(str))
          case dt: LocalDateTime => configs.HRDataEmployeeProfileVersionConfiguration(dt)
        }
      case _ => throw new ComposedPKVersionNotFoundException("HR Data employee profile latest version not found")
    }
  }

  def toLatestVersionOfEmployeeInferredRoles(configuration: Configuration): EmployeeInferredRolesVersionConfiguration = {
    configuration.configs.get("date") match {
      case Some(value) =>
        value match {
          case str: String => configs.EmployeeInferredRolesVersionConfiguration(TimeUtils.timestampToLocalDateTime(str))
          case dt: LocalDateTime => configs.EmployeeInferredRolesVersionConfiguration(dt)
        }
      case _ => throw new ComposedPKVersionNotFoundException("Employee inferred roles latest version not found")
    }
  }

}


