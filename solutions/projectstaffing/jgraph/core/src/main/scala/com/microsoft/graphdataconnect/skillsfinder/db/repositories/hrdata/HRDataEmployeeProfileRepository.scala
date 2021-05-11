/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.hrdata

import java.time.LocalDateTime
import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata.{HRDataEmployeeIdentity, HRDataEmployeeProfile}
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait HRDataEmployeeProfileRepository extends JpaRepository[HRDataEmployeeProfile, HRDataEmployeeIdentity] {

  /**
   * Retrieves an HR Data employee profile by its email and version.
   *
   * @param mail    mail identifying an employee. Must not be { @literal null or empty}.
   * @param version contains the timestamp of when the data was written. Must not be { @literal null or empty}.
   * @return the entity with the given id or { @literal Optional#empty()} if none found
   * @throws IllegalArgumentException if { @code mail} is { @literal null or empty string, or HRDataEmployeeProfile not found}.
   */
  def findByComposedIdMailAndComposedIdVersion(@Param("mail") mail: String, @Param("version") version: LocalDateTime): Optional[HRDataEmployeeProfile]

}

