package com.microsoft.graphdataconnect.skillsfinder.models.configs

case class OAuthAzureActiveDirectoryConfiguration(
                                                   clientId: String,
                                                   tenantId: String,
                                                   clientSecret: String
                                                 ) {
  require(clientId != null, "ClientId must be defined")
  require(clientId.nonEmpty, "ClientId must not be empty")

  require(tenantId != null, "TenantId must be defined")
  require(tenantId.nonEmpty, "TenantId must not be empty")

}
