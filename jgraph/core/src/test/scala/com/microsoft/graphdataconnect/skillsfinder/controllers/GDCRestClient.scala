package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.Employee
import com.microsoft.graphdataconnect.skillsfinder.models.business.team.{TeamMemberRequest, TeamMemberResponse}
import com.microsoft.graphdataconnect.skillsfinder.models.configs.DatabricksConfiguration
import com.microsoft.graphdataconnect.skillsfinder.setup.Constants
import com.microsoft.graphdataconnect.skillsfinder.setup.web.RestHelper
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.{HttpEntity, HttpMethod, ResponseEntity}
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

import scala.collection.immutable.HashMap.HashTrieMap

@Component
class GDCRestClient extends RestHelper {

  def createDatabricksConfiguration(input: DatabricksConfiguration): DatabricksConfiguration = {
    val request = new HttpEntity[DatabricksConfiguration](input, getBaseHeaders)
    val response: ResponseEntity[DatabricksConfiguration] = restClient.exchange(
      getRelativeResourceURI(Constants.CONFIGURATION_RESOURCE + "/databricks"),
      HttpMethod.POST,
      request,
      classOf[DatabricksConfiguration])

    assertThat(response.getStatusCode.is2xxSuccessful())
    response.getBody
  }

  def getDatabricksConfiguration(includeSecretOpt: Option[Boolean] = None): DatabricksConfiguration = {
    val request = new HttpEntity[DatabricksConfiguration](getBaseHeaders)
    val url: String = includeSecretOpt match {
      case Some(includeSecret) =>
        UriComponentsBuilder
          .fromUriString(getRelativeResourceURI(Constants.CONFIGURATION_RESOURCE + "/databricks"))
          .queryParam("includeSecret", Boolean.box(includeSecret))
          .toUriString
      case _ => getRelativeResourceURI(Constants.CONFIGURATION_RESOURCE + "/databricks")
    }

    val response: ResponseEntity[DatabricksConfiguration] = restClient.exchange(
      url,
      HttpMethod.GET,
      request,
      classOf[DatabricksConfiguration])

    assertThat(response.getStatusCode.is2xxSuccessful())
    response.getBody
  }

  def getEmployee(id: String): Employee = {
    val request = new HttpEntity[String](getBaseHeaders)
    val url: String = getRelativeResourceURI(Constants.EMPLOYEE_RESOURCE + "/" + id)

    val response: ResponseEntity[Employee] = restClient.exchange(
      url,
      HttpMethod.GET,
      request,
      classOf[Employee])

    assertThat(response.getStatusCode.is2xxSuccessful())
    response.getBody
  }

  def getTeamMembers(): Seq[HashTrieMap[String, String]] = {
    val request = new HttpEntity[String](getBaseHeaders)
    val url: String = getRelativeResourceURI(Constants.TEAM_RESOURCE + "/structure/members")

    val response: ResponseEntity[Seq[HashTrieMap[String, String]]] = restClient.exchange(
      url,
      HttpMethod.GET,
      request,
      classOf[Seq[HashTrieMap[String, String]]])

    assertThat(response.getStatusCode.is2xxSuccessful())
    response.getBody
  }

  def createTeamMember(teamMemberRequest: TeamMemberRequest): ResponseEntity[TeamMemberResponse] = {
    val request = new HttpEntity[TeamMemberRequest](teamMemberRequest, getBaseHeaders)
    val url: String = getRelativeResourceURI(Constants.TEAM_RESOURCE + "/structure/members")

    val response: ResponseEntity[TeamMemberResponse] = restClient.exchange(
      url,
      HttpMethod.POST,
      request,
      classOf[TeamMemberResponse])

    response
  }

}
