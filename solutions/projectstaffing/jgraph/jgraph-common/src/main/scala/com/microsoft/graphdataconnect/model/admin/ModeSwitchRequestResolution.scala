package com.microsoft.graphdataconnect.model.admin

object ModeSwitchRequestResolution extends Enumeration {
  type ModeSwitchRequestResolution = Value

  val Accepted: ModeSwitchRequestResolution.Value = Value("ACCEPTED")
  val Rejected: ModeSwitchRequestResolution.Value = Value("REJECTED")
}