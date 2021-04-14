package com.microsoft.graphdataconnect.skillsfinder.exceptions

class PipelineRunCancellationFailedException(pipelineName: String, runId: String, exception: Throwable = null)
  extends Exception(s"Failed to cancel pipeline $pipelineName with run id $runId", exception) {

}
