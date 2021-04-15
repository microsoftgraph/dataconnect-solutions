package com.microsoft.graphdataconnect.skillsfinder.models.configs

import java.time.LocalDateTime

case class EmployeeProfileVersionConfiguration(date: LocalDateTime) {
  require(date != null, "Date must be defined")
}
