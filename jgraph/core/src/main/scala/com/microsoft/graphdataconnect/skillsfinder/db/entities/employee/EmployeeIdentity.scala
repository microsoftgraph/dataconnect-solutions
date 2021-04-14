package com.microsoft.graphdataconnect.skillsfinder.db.entities.employee

import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import javax.persistence.{Column, Embeddable, Id}

@Embeddable
class EmployeeIdentity(@Id
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

object EmployeeIdentity {
  def apply(id: String, version: LocalDateTime): EmployeeIdentity = new EmployeeIdentity(id, version)
}
