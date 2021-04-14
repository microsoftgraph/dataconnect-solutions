package com.microsoft.graphdataconnect.skillsfinder.exceptions

class PipelineRunCreationFailedException(pipelineName: String, exception: Throwable = null)
  extends Exception(s"Failed to create pipeline run for pipeline $pipelineName", exception) {

}
