/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities.employee

import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import javax.persistence.{Column, Embeddable, Id}

@Embeddable
class EmployeeInferredRolesIdentity(@Id
                                    @JsonProperty("id")
                                    @Column(name = "id", nullable = false)
                                    var id: String,

                                    @JsonIgnore
                                    @Column(name = "version", columnDefinition = "TIMESTAMP")
                                    var version: LocalDateTime) extends Serializable {

  def this() {
    this(null, null)
  }

}

object EmployeeInferredRolesIdentity {
  def apply(id: String, version: LocalDateTime): EmployeeInferredRolesIdentity = new EmployeeInferredRolesIdentity(id, version)
}
