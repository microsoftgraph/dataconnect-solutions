/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.graphdataconnect.skillsfinder.exceptions.{FailedToGetAzureServiceManagerTokenException, PermissionConsentMissingException, UnauthorizedException}
import com.microsoft.graphdataconnect.skillsfinder.models.TokenScope
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.{JwtTokenHeaders, UserInfo, UserToken}
import com.microsoft.graphdataconnect.skillsfinder.utils.JwtTokenUtils
import kong.unirest.json.JSONObject
import kong.unirest.{HttpResponse, HttpStatus, JsonNode, Unirest}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.cache.CacheManager
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

@Service
class UserService {

  @Autowired
  var objectMapper: ObjectMapper = _

  @Value("${jgraph.appservice.url}")
  var jgraphUrl: String = _

  @Value("${gdcAdmins.groupId}")
  var gdcAdminsGroupId: String = _

  @Value("${gdc-jgraph-service-principal-secret:#{environment.JGRAPH_SERVICE_PRINCIPAL_SECRET}}")
  var servicePrincipalSecret: String = _

  @Value("${service.principal.tenantId}")
  var tenantId: String = _

  @Value("${service.principal.clientId}")
  var clientId: String = _

  @Autowired
  var cacheManager: CacheManager = _

  @Value("${anonymous.authentication.enabled}")
  var isAnonymousAuthEnabled: Boolean = _

  private val anonymousUserDefaultInfo = UserInfo("", "", "", "test@anonymous.com")

  private val logger: Logger = LoggerFactory.getLogger(classOf[UserService])

  def getUserId(cookieValue: String): String = {
    getUserInfo(cookieValue).userId
  }

  def getUserInfo(cookieValue: String): UserInfo = {
    if (isAnonymousAuthEnabled) {
      anonymousUserDefaultInfo
    } else {
      Try {
        Unirest.get(jgraphUrl + "/.auth/me").headers(Map("Cookie" -> ("AppServiceAuthSession=" + cookieValue)).asJava).asJson()
      } match {
        case Success(response: HttpResponse[JsonNode]) =>
          if (response.getStatus == HttpStatus.OK) {
            val jsonResponse = response.getBody.toPrettyString

            val arrayUserInfo: Array[UserInfo] = objectMapper.readValue(jsonResponse, classOf[Array[UserInfo]])
            if (arrayUserInfo.nonEmpty) {
              arrayUserInfo(0)
            } else {
              throw new UnauthorizedException("There was no information about the user that could be received from calling /.auth/me endpoint.")
            }
          } else {
            throw new UnauthorizedException(s"Failed to get user info from /.auth/me endpoint. Status: ${response.getStatusText} Body: ${response.getBody}")
          }

        case Failure(e: Throwable) =>
          logger.error("Failed to get user info from /.auth/me endpoint!", e)
          throw new UnauthorizedException("Failed to get user info from /.auth/me endpoint!")
      }
    }

  }


  def isUserPartOfAdminsGroup(clientPrincipalToken: String): Boolean = {
    if (isAnonymousAuthEnabled) {
      false
    } else {
      val jwtTokenHeaders = objectMapper.readValue(JwtTokenUtils.extractHeader(clientPrincipalToken), classOf[JwtTokenHeaders])
      val userGroups = jwtTokenHeaders.claims.filter(_.typ.equals("groups"))
      userGroups.exists(_.value.equals(gdcAdminsGroupId))
    }

  }

  def getUserToken(xMsAadIdToken: String = "", refreshToken: String = "", authSessionCookie: String = "", scope: TokenScope): UserToken = {
    if (xMsAadIdToken.nonEmpty && refreshToken.nonEmpty) {
      // If we have the "x-ms-aad-id-token" & a refresh token
      // then we can request a access token on behalf of the user the tokens belong to
      // See: https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow
      getTokenOnBehalfOf(xMsAadIdToken, refreshToken, scope)
    } else if (refreshToken.nonEmpty) {
      //If we have only a refresh token we can request an access token refresh
      refreshOnBehalfOfToken(refreshToken, scope)
    } else if (authSessionCookie.nonEmpty) {
      val userInfo: UserInfo = getUserInfo(cookieValue = authSessionCookie)
      getTokenOnBehalfOf(userInfo.idToken, userInfo.refreshToken, scope)
    } else {
      throw new UnauthorizedException("There is insufficient information in the request headers for generating a Azure Service Management token!")
    }
  }

  private def getTokenOnBehalfOf(xMsAadIdToken: String, xMsAadRefreshToken: String, scope: TokenScope): UserToken = {
    val urlEncodedServicePrincipalSecret = URLEncoder.encode(servicePrincipalSecret, StandardCharsets.UTF_8.displayName())
    val tokenUrl = s"https://login.microsoftonline.com/$tenantId/oauth2/v2.0/token"
    //TODO add link from docs for on_behalf_of
    val requestBody = s"grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer\n&client_id=$clientId\n&client_secret=$urlEncodedServicePrincipalSecret\n" +
      s"&assertion=$xMsAadIdToken\n&scope=${scope.getValue}\n&requested_token_use=on_behalf_of"

    Try {
      Unirest.post(tokenUrl).body(requestBody).headers(Map("Content-Type" -> "application/x-www-form-urlencoded").asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        val responseJSONObject: JSONObject = response.getBody.getObject
        if (response.getStatus == HttpStatus.OK) {
          val accessToken = responseJSONObject.getString("access_token")
          UserToken(accessToken = accessToken, refreshToken = xMsAadRefreshToken, scope)
        } else if (response.getStatus == HttpStatus.BAD_REQUEST && responseJSONObject.getString("error").equals("invalid_grant") && responseJSONObject.getString("error_codes").contains("500133")) {
          //In case the request fails because the xMsAadIdToken already expired then try getting a new token using the refresh token
          refreshOnBehalfOfToken(xMsAadRefreshToken, scope)
        } else if (response.getStatus == HttpStatus.BAD_REQUEST && responseJSONObject.getString("error").equals("invalid_grant") && responseJSONObject.getString("error_codes").contains("65001")) {
          throw PermissionConsentMissingException(s"The user has not consented to the permission with the scope: $scope", consentRequestUrl = generateUserPermissionConsentUrl())
        } else {
          throw new FailedToGetAzureServiceManagerTokenException(s"Request to get Azure Service Manager token on behalf of user failed.")
        }
      case Failure(exception: Throwable) =>
        throw new FailedToGetAzureServiceManagerTokenException(s"Request to get Azure Service Manager token on behalf of user failed.", exception)
    }

  }

  private def refreshOnBehalfOfToken(refreshToken: String, scope: TokenScope): UserToken = {
    val urlEncodedServicePrincipalSecret = URLEncoder.encode(servicePrincipalSecret, StandardCharsets.UTF_8.displayName())
    val tokenUrl = s"https://login.microsoftonline.com/$tenantId/oauth2/v2.0/token"
    val requestBody = s"client_id=$clientId\n&scope=${scope.getValue}" +
      s"&refresh_token=$refreshToken\n&grant_type=refresh_token\n&client_secret=$urlEncodedServicePrincipalSecret"

    Try {
      Unirest.post(tokenUrl).body(requestBody).headers(Map("Content-Type" -> "application/x-www-form-urlencoded").asJava).asJson()
    } match {
      case Success(response: HttpResponse[JsonNode]) =>
        val responseJSONObject: JSONObject = response.getBody.getObject
        if (response.getStatus == HttpStatus.OK) {
          val accessToken = responseJSONObject.getString("access_token")
          val refreshToken = responseJSONObject.getString("refresh_token")
          UserToken(accessToken = accessToken, refreshToken = refreshToken, scope)
        } else if (response.getStatus == HttpStatus.BAD_REQUEST && responseJSONObject.getString("error").equals("invalid_grant") && responseJSONObject.getString("error_codes").contains("65001")) {
          throw PermissionConsentMissingException(s"The user has not consented to the permission with the scope: $scope", consentRequestUrl = generateUserPermissionConsentUrl())
        } else {
          throw new FailedToGetAzureServiceManagerTokenException(s"Request to refresh Azure Service Manager token on behalf of user failed. Status:${response.getStatusText}, body: ${response.getBody}")
        }
      case Failure(exception: Throwable) =>
        throw new FailedToGetAzureServiceManagerTokenException(s"Request to refresh Azure Service Manager token on behalf of user failed.", exception)
    }
  }

  private def generateUserPermissionConsentUrl(): String = {
    //TODO keep the redirectUri in sync with the reply_url from install.py init_active_directory_entities method
    val redirectUri = URLEncoder.encode(jgraphUrl + "/.auth/login/aad/callback", StandardCharsets.UTF_8.displayName())
    val scope = URLEncoder.encode(TokenScope.values().map(_.getValue).mkString(" "), StandardCharsets.UTF_8.displayName())
    val consentUrl = s"https://login.microsoftonline.com/$tenantId/oauth2/v2.0/authorize?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&response_mode=form_post&scope=$scope"
    consentUrl
  }

  def isCurrentUserAnAdmin(httpHeaders: HttpHeaders): Boolean = {
    if (isAnonymousAuthEnabled) {
      false
    } else {
      httpHeaders.toSingleValueMap.asScala.get("x-ms-client-principal").exists(isUserPartOfAdminsGroup)
    }

  }

  def getUserToken(httpHeaders: HttpHeaders, authSessionCookie: String, scope: TokenScope): UserToken = {
    val httpHeadersMap = httpHeaders.toSingleValueMap.asScala
    if (httpHeadersMap.contains("x-ms-token-aad-id-token") && httpHeadersMap.contains("x-ms-token-aad-refresh-token")) {
      getUserToken(xMsAadIdToken = httpHeadersMap("x-ms-token-aad-id-token"),
        refreshToken = httpHeadersMap("x-ms-token-aad-refresh-token"), scope = scope)
    } else if (httpHeadersMap.contains("x-ms-token-aad-refresh-token")) {
      getUserToken(httpHeadersMap("x-ms-token-aad-refresh-token"), scope = scope)
    } else {
      getUserToken(authSessionCookie = authSessionCookie, scope = scope)
    }
  }

}

