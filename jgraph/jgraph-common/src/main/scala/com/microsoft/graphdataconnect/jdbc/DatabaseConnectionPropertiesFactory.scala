package com.microsoft.graphdataconnect.jdbc

import java.util.Properties

import com.microsoft.graphdataconnect.model.security.ADLAccessToken
import com.microsoft.graphdataconnect.secret.Vault
import com.microsoft.graphdataconnect.servcies.ADLoginService

object DatabaseConnectionPropertiesFactory {
  def getProperties(applicationId: String,
                    directoryId: String,
                    adbSecretScopeName: String,
                    adbSPClientKeySecretName: String,
                    jdbcUsernameKeyName: Option[String] = None,
                    jdbcPasswordKeyName: Option[String] = None,
                    useMsiAzureSqlAuth: Boolean,
                    keyVaultUrl: String): Properties = {
    val scope = "https://database.windows.net/"
    val driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver"

    val connectionProperties = new Properties()
    connectionProperties.put("Driver", driverClass)
    connectionProperties.put("hostNameInCertificate", "*.database.windows.net")
    connectionProperties.put("encrypt", "true")
    connectionProperties.put("ServerCertificate", "false")
    connectionProperties.put("trustServerCertificate", "false")
    connectionProperties.put("loginTimeout", "30")

    if (useMsiAzureSqlAuth) {
      val adlAccessToken: ADLAccessToken = ADLoginService(directoryId).loginForAccessToken(clientId = applicationId,
        secretScope = adbSecretScopeName,
        secretKey = adbSPClientKeySecretName,
        scope = scope)
      connectionProperties.put("accessToken", adlAccessToken.accessToken)
    } else {
      if (jdbcUsernameKeyName.isDefined && jdbcPasswordKeyName.isDefined) {
        val azureKeyVaultClient = Vault(
          keyVaultUrl = keyVaultUrl,
          clientId = applicationId,
          tenantId = directoryId,
          adbSecretScopeName = adbSecretScopeName,
          adbServicePrincipalKeySecretName = adbSPClientKeySecretName)

        val jdbcUsername = try {
          azureKeyVaultClient.getSecretValue(jdbcUsernameKeyName.get)
        } catch {
          case e: Exception => throw new RuntimeException("Failed to get the AzureSql user from Azure KeyVault", e)
        }

        val jdbcPassword = try {
          azureKeyVaultClient.getSecretValue(jdbcPasswordKeyName.get)
        } catch {
          case e: Exception => throw new RuntimeException("Failed to get the AzureSql password from Azure KeyVault", e)
        }

        connectionProperties.put("user", jdbcUsername)
        connectionProperties.put("password", jdbcPassword)
      } else {
        throw new IllegalArgumentException("When MSI AzureSql auth is disabled, providing the names of the secrets containing the AzureSql user and password as arguments is mandatory")
      }
    }

    connectionProperties
  }
}
