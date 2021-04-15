package com.microsoft.graphdataconnect.skillsfinder.exceptions

class ADFPipelineRunFailed(message: String, runId: String, exception: Throwable = null) extends Exception(message, exception) {

}
