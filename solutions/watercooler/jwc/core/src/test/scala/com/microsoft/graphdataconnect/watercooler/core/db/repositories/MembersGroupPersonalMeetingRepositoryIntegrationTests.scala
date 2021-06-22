/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.db.repositories

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.MembersGroupPersonalMeeting
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.MembersGroupPersonalMeetingIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.repositories.group.MembersGroupPersonalMeetingRepository
import com.microsoft.graphdataconnect.watercooler.common.util.TimeUtils
import com.microsoft.graphdataconnect.watercooler.core.setup.repository.AbstractIntegrationTestBase
import org.junit.{Assert, Test}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired

class MembersGroupPersonalMeetingRepositoryIntegrationTests extends AbstractIntegrationTestBase {

  private val logger: Logger = LoggerFactory.getLogger(classOf[MembersToGroupParticipationRepositoryIntegrationTests])

  @Autowired
  var membersGroupPersonalMeetingRepository: MembersGroupPersonalMeetingRepository = _


  @Test
  def testFindFirstByGroupNameAndComposedIdVersion(): Unit = {
    // arrange
    val version = TimeUtils.oldestSqlDate
    val membersGroupPersonalMeeting: MembersGroupPersonalMeeting = new MembersGroupPersonalMeeting()
    membersGroupPersonalMeeting.composedId = new MembersGroupPersonalMeetingIdentity("1", version)
    membersGroupPersonalMeeting.groupName = "g1"
    membersGroupPersonalMeeting.hour = 1
    membersGroupPersonalMeeting.groupBusySlots = ""
    membersGroupPersonalMeeting.timezoneNr = "00"
    membersGroupPersonalMeeting.timezoneStr = "UTC"

    membersGroupPersonalMeetingRepository.save(membersGroupPersonalMeeting)

    // act
    val group: Option[MembersGroupPersonalMeeting] = Option(membersGroupPersonalMeetingRepository.findFirstByGroupNameAndComposedIdVersion("g1", version).orElse(null))

    // assert
    Assert.assertTrue("group should not be empty", group.nonEmpty)
    Assert.assertTrue("group id should match", membersGroupPersonalMeeting.composedId == group.get.composedId)
  }

}
