package com.microsoft.graphdataconnect.skillsfinder.models.configs

import java.time.LocalDateTime

case class EmployeeInferredRolesVersionConfiguration(date: LocalDateTime) {
  require(date != null, "Date must be defined")
}
