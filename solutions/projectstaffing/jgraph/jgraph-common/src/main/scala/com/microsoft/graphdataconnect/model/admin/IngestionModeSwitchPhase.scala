package com.microsoft.graphdataconnect.model.admin

object IngestionModeSwitchPhase extends Enumeration {
  type IngestionModeSwitchPhase = Value

  // The order or the enum ids impacts the mode switch logic. Change with caution!
  val Error: IngestionModeSwitchPhase.Value = Value(-1, "error")
  val StartingModeSwitch: IngestionModeSwitchPhase.Value = Value(0, "starting_mode_switch")
  val TriggersStopped: IngestionModeSwitchPhase.Value = Value(1, "triggers_stopped")
  val TriggersDeleted: IngestionModeSwitchPhase.Value = Value(2, "triggers_deleted")
  val RunningTriggerRunsStopped: IngestionModeSwitchPhase.Value = Value(3, "running_trigger_runs_stopped")
  val RunningPipelinesStopped: IngestionModeSwitchPhase.Value = Value(4, "running_pipelines_stopped")
  val CleanupPipelineStarted: IngestionModeSwitchPhase.Value = Value(5, "cleanup_pipeline_started")
  val CleanupPipelineCompleted: IngestionModeSwitchPhase.Value = Value(6, "cleanup_pipeline_completed")
  val IngestionModeUpdatedInAdf: IngestionModeSwitchPhase.Value = Value(7, "ingestion_mode_updated_in_adf")
  val TriggersRecreated: IngestionModeSwitchPhase.Value = Value(8, "triggers_recreated")
  val RecentDataBackfillPipelinesCompleted: IngestionModeSwitchPhase.Value = Value(9, "recent_data_backfill_pipelines_completed")
  val Completed: IngestionModeSwitchPhase.Value = Value(10, "completed")

  def withCaseInsensitiveName(name: String): Option[Value] =
    values.find(_.toString.toLowerCase == name.toLowerCase())

}
