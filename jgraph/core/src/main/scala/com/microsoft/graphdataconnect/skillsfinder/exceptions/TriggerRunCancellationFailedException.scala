package com.microsoft.graphdataconnect.skillsfinder.exceptions

class TriggerRunCancellationFailedException(triggerName: String, runId: String, exception: Throwable = null)
  extends Exception(s"Failed to cancel trigger run $triggerName with run id $runId", exception) {
}
