/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.repositories.employee

import java.time.LocalDateTime

import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.EmployeeIdentity
import com.microsoft.graphdataconnect.watercooler.common.db.entities.employee.{EmployeeIdentity, EmployeeProfile}
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait EmployeeProfileRepository extends JpaRepository[EmployeeProfile, EmployeeIdentity]{

  def findByMailAndComposedIdVersion(@Param("mail") mail: String,
                                     @Param("version") version: LocalDateTime): java.util.Optional[EmployeeProfile]

}
