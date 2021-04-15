package com.microsoft.graphdataconnect.servcies

import java.util.concurrent.Executors

import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.microsoft.aad.adal4j.{AuthenticationContext, ClientCredential}
import com.microsoft.graphdataconnect.model.security.ADLAccessToken
import org.slf4j.{Logger, LoggerFactory}

class ADLoginService(tenantId: String) {

  val log: Logger = LoggerFactory.getLogger(classOf[ADLoginService])
  val AUTHORITY: String = s"https://login.microsoftonline.com/$tenantId/"
  val context = new AuthenticationContext(AUTHORITY, false, Executors.newFixedThreadPool(1))

  def loginForAccessToken(clientId: String,
                          secretScope: String,
                          secretKey: String,
                          scope: String): ADLAccessToken = {
    log.info(s"Logging in as App: $clientId on $AUTHORITY")

    val clientSecret = dbutils.secrets.get(scope = secretScope, key = secretKey)
    val cred = new ClientCredential(clientId, clientSecret)
    val future = context.acquireToken(scope, cred, null)
    val accessToken = future.get().getAccessToken
    ADLAccessToken(accessToken, clientId)
  }

  def loginForAccessToken(clientId: String,
                          clientSecret: String,
                          scope: String): ADLAccessToken = {
    log.info(s"Logging in as App: ${clientId} on $AUTHORITY")

    val cred = new ClientCredential(clientId, clientSecret);
    val future = context.acquireToken(scope, cred, null);
    val accessToken = future.get().getAccessToken
    ADLAccessToken(accessToken, clientId)
  }

}

object ADLoginService {

  def apply(tenantId: String) = new ADLoginService(tenantId)

}
