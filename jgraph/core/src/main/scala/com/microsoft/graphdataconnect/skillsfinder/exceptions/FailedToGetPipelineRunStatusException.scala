package com.microsoft.graphdataconnect.skillsfinder.exceptions

class FailedToGetPipelineRunStatusException(pipelineRunId: String, exception: Throwable = null)
  extends Exception(s"Failed to get pipeline run status for pipeline run with id $pipelineRunId", exception) {

}
