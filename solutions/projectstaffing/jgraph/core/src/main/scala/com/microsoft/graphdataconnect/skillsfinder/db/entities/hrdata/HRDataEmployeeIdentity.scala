package com.microsoft.graphdataconnect.skillsfinder.db.entities.hrdata

import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import javax.persistence.{Column, Embeddable, Id}

@Embeddable
class HRDataEmployeeIdentity(@Id
                             @JsonProperty("mail")
                             @Column(name = "mail", nullable = false)
                             var mail: String,

                             @JsonIgnore
                             @Column(name = "version", columnDefinition = "TIMESTAMP")
                             var version: LocalDateTime) extends Serializable {


  def this() {
    this(null, null)
  }

}

object HRDataEmployeeIdentity {
  def apply(mail: String, version: LocalDateTime): HRDataEmployeeIdentity = new HRDataEmployeeIdentity(mail, version)
}

