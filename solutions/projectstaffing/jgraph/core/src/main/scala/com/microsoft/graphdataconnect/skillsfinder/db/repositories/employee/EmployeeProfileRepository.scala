/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee

import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeIdentity, EmployeeProfile, RecommendedEmployee}
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
trait EmployeeProfileRepository extends JpaRepository[EmployeeProfile, EmployeeIdentity] {

  /**
   * Retrieves an entity by its email.
   *
   * @param composedId must not be { @literal null or empty}.
   * @return the entity with the given id or { @literal Optional#empty()} if none found
   * @throws IllegalArgumentException if { @code email} is { @literal null or empty string, or EmployeeProfile not found}.
   */
  def findByComposedId(@Param("composedId") composedId: EmployeeIdentity): Optional[EmployeeProfile]

  //  @param availableSince is expected as 'yyyy-MM-dd' form
  //  If the availableSince format should be changed, update the stored procedure according to
  //  the new format based on https://www.mssqltips.com/sqlservertip/1145/date-and-time-conversions-using-sql-server/
  //  Currently we use the '23' format.
  @Query(name = "EmployeeProfile.findRecommendedEmployees", nativeQuery = true)
  def findRecommendedEmployees(@Param("size_param") size: Int,
                               @Param("offset_param") offset: Int,
                               @Param("order_by_availability_and_name") orderByAvailabilityAndName: Boolean,
                               @Param("available_since") availableSince: String,
                               @Param("team_owner_email") teamOwnerEmail: String,
                               @Param("recommended_candidates_emails") recommendedCandidatesEmails: String,
                               @Param("latest_profile_version") latestEmployeeProfileVersion: String,
                               @Param("latest_hr_data_version") latestHRDataEmployeeProfileVersion: String,
                               @Param("latest_inferred_roles_version") latestInferredRolesVersion: String,
                               @Param("is_hr_data_mandatory") isHRDataMandatory: Boolean,
                               @Param("json_filters") jsonFilters: String): java.util.List[RecommendedEmployee]

  def findFirstByComposedIdIdOrderByComposedIdVersionDesc(@Param("id") id: String): Optional[EmployeeProfile]

}
