/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities.employee

import javax.persistence.{EmbeddedId, _}

@Table(name = "inferred_roles")
@Entity
class EmployeeInferredRoles {

  @EmbeddedId
  var composedId: EmployeeInferredRolesIdentity = _

  @Column(name = "email")
  var email: String = _

  @Column(name = "doc_count_0")
  var docCount0: Int = _

  @Column(name = "doc_count_1")
  var docCount1: Int = _

  @Column(name = "doc_count_2")
  var docCount2: Int = _

  @Column(name = "frequent_role_0")
  var frequentRole0: String = _

  @Column(name = "frequent_role_1")
  var frequentRole1: String = _

  @Column(name = "frequent_role_2")
  var frequentRole2: String = _

  @Column(name = "highest_score_role_0")
  var highestScoreRole0: String = _

  @Column(name = "highest_score_role_1")
  var highestScoreRole1: String = _

  @Column(name = "highest_score_role_2")
  var highestScoreRole2: String = _

  @Column(name = "highest_score_score_0")
  var highestScoreScore0: Double = _

  @Column(name = "highest_score_score_1")
  var highestScoreScore1: Double = _

  @Column(name = "highest_score_score_2")
  var highestScoreScore2: Double = _

  @Column(name = "role_proposal_0")
  var roleProposal0: String = _

  @Column(name = "role_proposal_1")
  var roleProposal1: String = _

  @Column(name = "role_proposal_2")
  var roleProposal2: String = _

  @Column(name = "score_proposal_0")
  var scoreProposal0: Double = _

  @Column(name = "score_proposal_1")
  var scoreProposal1: Double = _

  @Column(name = "score_proposal_2")
  var scoreProposal2: Double = _

  @Column(name = "total_docs")
  var totalDocs: Int = _

  def mergeToSingleRolesList(): List[String] = {
    List(roleProposal0, roleProposal1, roleProposal2,
      frequentRole0, frequentRole1, frequentRole2,
      highestScoreRole0, highestScoreRole1, highestScoreRole2
    ).filter(_.nonEmpty).map(_.trim.toLowerCase).distinct
  }

}
