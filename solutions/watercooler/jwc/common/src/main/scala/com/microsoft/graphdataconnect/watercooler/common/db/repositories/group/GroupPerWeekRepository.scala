/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.repositories.group

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.GroupPerWeek
import com.microsoft.graphdataconnect.watercooler.common.db.entities.group.identity.GroupPerWeekIdentity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait GroupPerWeekRepository extends JpaRepository[GroupPerWeek, GroupPerWeekIdentity]{

  def findByDayGreaterThanEqualAndDayLessThanEqualAndComposedIdVersionAndTimezoneNrIn(@Param("startDate") startDate: LocalDateTime,
                                                                                      @Param("endDate") endDate: LocalDateTime,
                                                                                      @Param("version") version: LocalDateTime,
                                                                                      @Param("timezoneNr") timezoneNr: java.util.List[String]): java.util.List[GroupPerWeek]

}
