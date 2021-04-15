package com.microsoft.graphdataconnect.skillsfinder.db.entities.employee

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence._

@Table(name = "employee_interests")
@Entity
class Interest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @Column(name = "id", insertable = false, nullable = false)
  var id: Long = _

  @ManyToOne
  @JsonProperty("employeeProfileId")
  @JoinColumns(Array(
    new JoinColumn(name = "employee_profile_id", referencedColumnName = "id"),
    new JoinColumn(name = "employee_profile_version", referencedColumnName = "version")
  ))
  var employeeProfile: EmployeeProfile = _

  @JsonProperty("interest")
  @Column(name = "interest", columnDefinition = "text")
  var interest: String = _

}
