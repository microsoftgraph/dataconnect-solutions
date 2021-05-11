/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.admin

object HRDataIngestionPhase extends Enumeration {

  type HRDataIngestionPhase = Value

  val Error: HRDataIngestionPhase.Value = Value(-1, "error")
  val StartingFileUpload: HRDataIngestionPhase.Value = Value(0, "started_file_upload")
  val StartingEmployeePipelineRun: HRDataIngestionPhase.Value = Value(1, "started_employee_pipeline_run")
  val EmployeePipelineRunFinished: HRDataIngestionPhase.Value = Value(2, "employee_pipeline_run_finished")

  def withCaseInsensitiveName(name: String): Option[Value] = values.find(_.toString.toLowerCase == name.toLowerCase())

}
