package com.microsoft.graphdataconnect.skillsfinder.db.entities.team

import com.fasterxml.jackson.annotation.JsonProperty
import com.microsoft.graphdataconnect.skillsfinder.models.business.team.TeamMemberRequest
import javax.persistence._

object TeamMember {

  def apply(teamMemberRequest: TeamMemberRequest, userId: String): TeamMember = {
    val teamMember: TeamMember = new TeamMember
    teamMember.ownerEmail = userId
    teamMember.memberId = teamMemberRequest.employeeId
    teamMember.memberEmail = teamMemberRequest.email
    teamMember.memberName = teamMemberRequest.name
    teamMember
  }

}


@Table(name = "team_members")
@Entity
class TeamMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @Column(name = "id", insertable = false, nullable = false)
  var id: Long = _

  @JsonProperty("userId")
  @Column(name = "owner_email")
  var ownerEmail: String = _

  @JsonProperty("employeeId")
  @Column(name = "member_id")
  var memberId: String = _

  @JsonProperty("email")
  @Column(name = "member_email")
  var memberEmail: String = _

  @JsonProperty("name")
  @Column(name = "member_name")
  var memberName: String = _

  @OneToMany(mappedBy = "teamMemberId")
  var skills: java.util.List[TeamMemberSkill] = _

}
