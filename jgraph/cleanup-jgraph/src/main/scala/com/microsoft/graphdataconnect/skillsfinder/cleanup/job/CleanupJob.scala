package com.microsoft.graphdataconnect.skillsfinder.cleanup.job

import java.util.Properties

import com.mashape.unirest.http.Unirest
import com.microsoft.graphdataconnect.jdbc.{DatabaseConnectionPropertiesFactory, JdbcClient}
import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.model.configs.ConfigurationTypes
import com.microsoft.graphdataconnect.secret.Vault
import com.microsoft.graphdataconnect.skillsfinder.cleanup.config.ConfigArgs
import com.microsoft.graphdataconnect.utils.{LogAnalyticsUtils, TimeUtils}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object CleanupJob {

  var log: GdcLogger = _
  val EMPLOYEE_PROFILE_TABLE_NAME = "employee_profile"
  val EMPLOYEE_INTERESTS_TABLE_NAME = "employee_interests"
  val EMPLOYEE_PAST_PROJECTS_TABLE_NAME = "employee_past_projects"
  val EMPLOYEE_RESPONSIBILITIES_TABLE_NAME = "employee_responsibilities"
  val EMPLOYEE_SCHOOLS_TABLE_NAME = "employee_schools"
  val EMPLOYEE_SKILLS_TABLE_NAME = "employee_skills"
  val HR_DATA_EMPLOYEE_PROFILE_TABLE_NAME = "hr_data_employee_profile"
  val TEAM_MEMBERS_TABLE_NAME = "team_members"
  val TEAM_MEMBER_SKILLS = "team_member_skills"
  val TEAM_INFO_TABLE_NAME = "team_info"
  val INFERRED_ROLES_TABLE_NAME = "inferred_roles"

  val AZURE_SEARCH_API_VERSION = "?api-version=2020-06-30"

  def main(arg: Array[String]): Unit = {
    val config: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg)

    if (config.isDefined) {

      val configParams: ConfigArgs = config.get

      configParams.directoryId.getOrElse(throw new IllegalArgumentException("'directory-id' argument expected"))
      configParams.applicationId.getOrElse(throw new IllegalArgumentException("'application-id' argument expected"))
      configParams.adbSecretScopeName.getOrElse(throw new IllegalArgumentException("'adb-secret-scope-name' argument expected"))
      configParams.adbSPClientKeySecretName.getOrElse(throw new IllegalArgumentException("'adb-sp-client-key-secret-name' argument expected"))
      configParams.jdbcDatabase.getOrElse(throw new IllegalArgumentException("'jdbc-database' argument expected"))
      configParams.jdbcHostname.getOrElse(throw new IllegalArgumentException("'jdbc-hostname' argument expected"))
      configParams.jdbcPort.getOrElse(throw new IllegalArgumentException("'jdbc-port' argument expected"))
      configParams.keyVaultUrl.getOrElse(throw new IllegalArgumentException("'key-vault-url' argument expected"))
      configParams.logAnalyticsWorkspaceId.getOrElse(throw new IllegalArgumentException("'log-analytics-workspace-id' argument expected"))
      configParams.logAnalyticsWorkspaceKeyName.getOrElse(throw new IllegalArgumentException("'log-analytics-workspace-key-name' argument expected"))
      configParams.emailsIndexName.getOrElse(throw new IllegalArgumentException("'emails-index-name' argument expected"))
      configParams.useMsiAzureSqlAuth.getOrElse(throw new IllegalArgumentException("'use-msi-azure-sql-auth' argument expected"))

      val (logAnalyticsWorkspaceId, logAnalyticsWorkspaceKey) = LogAnalyticsUtils.buildLogAnalyticsWorkspaceIdAndWorkspaceKey(
        workspaceId = configParams.logAnalyticsWorkspaceId,
        workspaceKey = configParams.logAnalyticsWorkspaceKeyName,
        applicationId = configParams.applicationId.get,
        directoryId = configParams.directoryId.get,
        adbSecretScopeName = configParams.adbSecretScopeName.get,
        adbSPClientKeySecretName = configParams.adbSPClientKeySecretName.get,
        keyVaultUrl = configParams.keyVaultUrl.get
      )

      log = GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = logAnalyticsWorkspaceId,
        logAnalyticsSharedKey = logAnalyticsWorkspaceKey,
        clazz = CleanupJob.getClass,
        logType = "CleanupJob"
      )

      log.info(s"Running cleanup job with arguments: $configParams")

      val applicationId = configParams.applicationId.get
      val directoryId = configParams.directoryId.get
      val adbSecretScopeName = configParams.adbSecretScopeName.get
      val adbSPClientKeySecretName = configParams.adbSPClientKeySecretName.get
      val jdbcHostname = configParams.jdbcHostname.get
      val jdbcPort = configParams.jdbcPort.get
      val jdbcDatabase = configParams.jdbcDatabase.get
      val jdbcUsernameKeyName: Option[String] = configParams.jdbcUsernameKeyName
      val jdbcPasswordKeyName: Option[String] = configParams.jdbcPasswordKeyName
      val useMsiAzureSqlAuth = configParams.useMsiAzureSqlAuth.get
      val keyVaultUrl = configParams.keyVaultUrl.get
      val azureSearchUrl = configParams.azureSearchUrl.get
      val azureSearchAdminKeyName = configParams.azureSearchAdminKeyName.get
      val emailsIndexName = configParams.emailsIndexName.get

      val jdbcClientLogger = GdcLoggerFactory.getLogger(logAnalyticsWorkspaceId = logAnalyticsWorkspaceId,
        logAnalyticsSharedKey = logAnalyticsWorkspaceKey,
        clazz = JdbcClient.getClass,
        logType = "JdbcClient"
      )

      val connectionProperties: Properties = DatabaseConnectionPropertiesFactory.getProperties(
        applicationId, directoryId, adbSecretScopeName, adbSPClientKeySecretName,
        jdbcUsernameKeyName, jdbcPasswordKeyName, useMsiAzureSqlAuth, keyVaultUrl)
      val jdbcUrl = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
      val jdbcClient = JdbcClient(connectionProperties, jdbcUrl)(jdbcClientLogger)

      cleanUpAzureSqlData(jdbcClient)

      cleanUpAzureSearchIndexes(jdbcClient, applicationId, directoryId, adbSecretScopeName, adbSPClientKeySecretName, keyVaultUrl, azureSearchUrl, azureSearchAdminKeyName, emailsIndexName)
    } else {
      log.error("Invalid command line arguments!")
      throw new IllegalArgumentException("Invalid command line arguments")
    }
  }

  private def cleanUpAzureSearchIndexes(jdbcClient: JdbcClient, applicationId: String, directoryId: String, adbSecretScopeName: String, adbSPClientKeySecretName: String,
                                        keyVaultUrl: String, azureSearchUrl: String, azureSearchAdminKeyName: String, emailsIndexName: String): Unit = {
    val employeeProfileIndexName = jdbcClient.getIndexNameForConfigurationType(ConfigurationTypes.AzureSearchEmployeesIndex).get

    val vault = Vault(
      keyVaultUrl = keyVaultUrl,
      clientId = applicationId,
      tenantId = directoryId,
      adbSecretScopeName = adbSecretScopeName,
      adbServicePrincipalKeySecretName = adbSPClientKeySecretName)

    val azureSearchAdminKey = vault.getSecretValue(azureSearchAdminKeyName)

    log.info("Cleaning up Azure Search indexes")
    deleteAzureSearchIndex(azureSearchUrl, employeeProfileIndexName, azureSearchAdminKey)
    deleteAzureSearchIndex(azureSearchUrl, emailsIndexName, azureSearchAdminKey)
  }

  private def deleteAzureSearchIndex(azureSearchUrl: String, azureSearchIndexName: String, azureSearchAdminKey: String): Unit = {
    val headers = Map("Content-Type" -> "application/json", "api-key" -> azureSearchAdminKey)
    Try {
      Unirest.delete(azureSearchUrl + "/indexes/" + azureSearchIndexName + AZURE_SEARCH_API_VERSION).headers(headers.asJava).asString()
    } match {
      case Success(response) => log.info(s"Response from deleting index $azureSearchIndexName: ${response.getStatus}, ${response.getBody}")
      case Failure(exception) => log.error(s"Failed to delete index $azureSearchIndexName", exception)
    }
  }

  private def cleanUpAzureSqlData(jdbcClient: JdbcClient): Unit = {
    log.info("Change configurations data versions in order to stop showing data in the UI")
    jdbcClient.updateEmployeeDataVersion(ConfigurationTypes.LatestVersionOfEmployeeProfile, TimeUtils.oldestSqlDate)
    jdbcClient.updateEmployeeDataVersion(ConfigurationTypes.LatestVersionOfHRDataEmployeeProfile, TimeUtils.oldestSqlDate)

    log.info("Cleaning up AzureSql data")
    jdbcClient.deleteAllDataFromTable(HR_DATA_EMPLOYEE_PROFILE_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(EMPLOYEE_SKILLS_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(EMPLOYEE_INTERESTS_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(EMPLOYEE_PAST_PROJECTS_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(EMPLOYEE_RESPONSIBILITIES_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(EMPLOYEE_SCHOOLS_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(EMPLOYEE_PROFILE_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(INFERRED_ROLES_TABLE_NAME)

    jdbcClient.deleteAllDataFromTable(TEAM_MEMBERS_TABLE_NAME)
    jdbcClient.deleteAllDataFromTable(TEAM_MEMBER_SKILLS)
    log.info("AzureSql successfully cleaned")
  }

}
