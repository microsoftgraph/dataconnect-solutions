package com.microsoft.graphdataconnect.skillsfinder.controllers

import com.microsoft.graphdataconnect.skillsfinder.models.business.team.TeamMemberRequest
import com.microsoft.graphdataconnect.skillsfinder.setup.web.AbstractWebIntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class TeamControllerValidationWebIntegrationTests extends AbstractWebIntegrationTestBase {

  @Autowired
  var gdcRestClient: GDCRestClient = _

  @Test
  @Transactional
  def validateInputBody(): Unit = {
    // Assert
    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest(null, "1", "1", List.empty)).getStatusCode.is4xxClientError())
    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("", "1", "1", List.empty)).getStatusCode.is4xxClientError())
    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest(" ", "1", "1", List.empty)).getStatusCode.is4xxClientError())

    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("1", null, "1", List.empty)).getStatusCode.is4xxClientError())
    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("1", "", "1", List.empty)).getStatusCode.is4xxClientError())
    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("1", " ", "1", List.empty)).getStatusCode.is4xxClientError())

    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("1", "1", null, List.empty)).getStatusCode.is4xxClientError())
    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("1", "1", "", List.empty)).getStatusCode.is4xxClientError())
    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("1", "1", " ", List.empty)).getStatusCode.is4xxClientError())

    assertThat(gdcRestClient.createTeamMember(TeamMemberRequest("1", "1", "1", List.empty)).getStatusCode.is4xxClientError())
  }

}
