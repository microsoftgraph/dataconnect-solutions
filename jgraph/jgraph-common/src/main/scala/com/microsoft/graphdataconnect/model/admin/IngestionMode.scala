package com.microsoft.graphdataconnect.model.admin

object IngestionMode extends Enumeration {
  type IngestionMode = Value

  val Production: IngestionMode.Value = Value("production_mode")
  val Sample: IngestionMode.Value = Value("sample_mode")
  val Simulated: IngestionMode.Value = Value("simulated_mode")

  def withCaseInsensitiveName(name: String): Option[Value] = values.find(_.toString.toLowerCase == name.toLowerCase())
}


