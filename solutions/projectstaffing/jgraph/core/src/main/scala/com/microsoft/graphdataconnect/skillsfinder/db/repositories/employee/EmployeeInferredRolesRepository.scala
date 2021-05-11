/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee

import java.time.LocalDateTime
import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.{EmployeeInferredRoles, EmployeeInferredRolesIdentity}
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait EmployeeInferredRolesRepository extends JpaRepository[EmployeeInferredRoles, EmployeeInferredRolesIdentity] {

  /**
   * Retrieves an entity by its email.
   *
   * @param composedId must not be { @literal null or empty}.
   * @return the entity with the given id or { @literal Optional#empty()} if none found
   * @throws IllegalArgumentException if { @code email} is { @literal null or empty string, or EmployeeInferredRoles not found}.
   */
  def findByComposedId(@Param("composedId") composedId: EmployeeInferredRolesIdentity): Optional[EmployeeInferredRoles]

  /**
   * Retrieves an entity by its email.
   *
   * @param email must not be { @literal null or empty}.
   * @return the entity with the given id or { @literal Optional#empty()} if none found
   * @throws IllegalArgumentException if { @code email} is { @literal null or empty string, or EmployeeInferredRoles not found}.
   */
  def findByEmailAndComposedIdVersion(@Param("email") email: String, @Param("version") version: LocalDateTime): Optional[EmployeeInferredRoles]

}
