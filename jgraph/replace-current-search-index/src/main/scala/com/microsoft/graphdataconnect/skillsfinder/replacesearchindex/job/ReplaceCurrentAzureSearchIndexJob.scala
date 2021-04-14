package com.microsoft.graphdataconnect.skillsfinder.replacesearchindex.job

import java.sql.ResultSet
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.Unirest
import com.microsoft.graphdataconnect.jdbc.{DatabaseConnectionPropertiesFactory, JdbcClient}
import com.microsoft.graphdataconnect.logging.{GdcLogger, GdcLoggerFactory}
import com.microsoft.graphdataconnect.model.configs.{AzureSearchEmployeesIndexConfiguration, ConfigurationTypes}
import com.microsoft.graphdataconnect.secret.Vault
import com.microsoft.graphdataconnect.skillsfinder.replacesearchindex.config.ConfigArgs
import com.microsoft.graphdataconnect.utils.LogAnalyticsUtils

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object ReplaceCurrentAzureSearchIndexJob {

  var log: GdcLogger = _
  var oldAzSearchIndexName: String = _
  val api_version = "?api-version=2020-06-30"
  val objectMapper = new ObjectMapper()

  def main(arg: Array[String]): Unit = {

    val config: Option[ConfigArgs] = ConfigArgs.parseCommandLineArguments(arg)

    if (config.isDefined) {

      val configParams: ConfigArgs = config.get

      configParams.newAzureSearchIndexName.getOrElse(throw new IllegalArgumentException("'new-azure-search-index-name' argument expected"))
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
        clazz = ReplaceCurrentAzureSearchIndexJob.getClass,
        logType = "ReplaceCurrentAzureSearchIndexJob"
      )

      try {
        val newAzSearchIndexName = configParams.newAzureSearchIndexName.get
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

        val connectionProperties: Properties = DatabaseConnectionPropertiesFactory.getProperties(
          applicationId, directoryId, adbSecretScopeName, adbSPClientKeySecretName,
          jdbcUsernameKeyName, jdbcPasswordKeyName, useMsiAzureSqlAuth, keyVaultUrl)
        val jdbcUrl = s"jdbc:sqlserver://$jdbcHostname:$jdbcPort;database=$jdbcDatabase;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
        val jdbcClient = JdbcClient(connectionProperties, jdbcUrl)(log)
        val connection = jdbcClient.connection

        val (configurationType, indexNameField) =
          if (newAzSearchIndexName.toLowerCase.contains("employee"))
            (ConfigurationTypes.AzureSearchEmployeesIndex, classOf[AzureSearchEmployeesIndexConfiguration].getDeclaredFields()(0).getName)
          else throw new IllegalArgumentException("Invalid new-azure-search-index-name argument! It must be a employee index name.")

        try {
          val stmt = connection.createStatement
          // Replace old Azure Search index name in configurations table
          val configurationsResultSet: ResultSet = stmt.executeQuery(f"""SELECT * FROM dbo.configurations where type = '$configurationType'""")
          if (configurationsResultSet.next()) {
            val jsonConfigs = configurationsResultSet.getString("configs")
            val configMap = objectMapper.readValue(jsonConfigs, classOf[java.util.Map[String, String]])
            oldAzSearchIndexName = configMap.get(indexNameField)
            log.info("Old index name " + oldAzSearchIndexName)

            configMap.put(indexNameField, newAzSearchIndexName)
            val updatedJsonConfigs = objectMapper.writeValueAsString(configMap)

            stmt.execute(f"""UPDATE dbo.configurations SET configs = '$updatedJsonConfigs' WHERE type = '$configurationType' """)
          } else {
            stmt.execute(f"""INSERT INTO dbo.configurations  (type, configs) VALUES ('$configurationType', '{"$indexNameField": "$newAzSearchIndexName"}') """)
          }
        } finally {
          connection.close()
        }

        // Wait for requests that are still using the old index to finish
        Thread.sleep(60000)

        val vault = Vault(
          keyVaultUrl = keyVaultUrl,
          clientId = applicationId,
          tenantId = directoryId,
          adbSecretScopeName = adbSecretScopeName,
          adbServicePrincipalKeySecretName = adbSPClientKeySecretName)

        val headers = Map("Content-Type" -> "application/json", "api-key" -> vault.getSecretValue(azureSearchAdminKeyName))

        Try {
          Unirest.delete(azureSearchUrl + "/indexes/" + oldAzSearchIndexName + api_version).headers(headers.asJava).asString()
        } match {
          case Success(response) => log.info("Response from delete index " + response.getStatus + "  " + response.getBody)
          case Failure(exception) => log.info("Failed to delete index : " + exception)
        }
      } catch {
        case e: Throwable => log.error("Exception while replacing AzureSearch index", e)
          throw e
      }

    } else {
      log.error("Invalid command line arguments!")
      throw new IllegalArgumentException("Invalid command line arguments")
    }

  }


}
