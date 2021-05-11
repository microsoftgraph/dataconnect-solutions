/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.exceptions

class PipelineRunCancellationFailedException(pipelineName: String, runId: String, exception: Throwable = null)
  extends Exception(s"Failed to cancel pipeline $pipelineName with run id $runId", exception) {

}
