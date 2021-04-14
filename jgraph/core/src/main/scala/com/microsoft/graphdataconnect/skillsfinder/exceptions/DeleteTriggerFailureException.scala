package com.microsoft.graphdataconnect.skillsfinder.exceptions

class DeleteTriggerFailureException(triggerName: String, exception: Throwable = null)
  extends Exception(s"Failed to delete trigger $triggerName", exception) {

}
