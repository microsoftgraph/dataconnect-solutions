package com.microsoft.graphdataconnect.skillsfinder.exceptions

class StopTriggerFailureException(triggerName: String, exception: Throwable = null)
  extends Exception(s"Failed to stop trigger $triggerName", exception) {

}
