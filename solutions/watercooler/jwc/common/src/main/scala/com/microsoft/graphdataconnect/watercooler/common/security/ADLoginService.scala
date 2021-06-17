/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.security

import java.util.concurrent.Executors
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.microsoft.aad.adal4j.{AuthenticationContext, ClientCredential}
import org.slf4j.{Logger, LoggerFactory}


class ADLoginService(tenantId: String) {

  val log: Logger = LoggerFactory.getLogger(classOf[ADLoginService])
  val AUTHORITY: String = s"https://login.microsoftonline.com/$tenantId/"
  val context = new AuthenticationContext(AUTHORITY, false, Executors.newFixedThreadPool(1))

  def loginForAccessToken(clientId: String,
                          secretScope: String,
                          applicationSecretKeyName: String,
                          scope: String): ADLAccessToken = {
    log.info(s"Logging in as App: $clientId on $AUTHORITY")

    val clientSecret = dbutils.secrets.get(scope = secretScope, key = applicationSecretKeyName)
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
