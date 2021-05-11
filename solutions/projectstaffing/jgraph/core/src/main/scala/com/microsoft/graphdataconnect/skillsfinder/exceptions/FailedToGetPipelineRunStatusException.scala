/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.exceptions

class FailedToGetPipelineRunStatusException(pipelineRunId: String, exception: Throwable = null)
  extends Exception(s"Failed to get pipeline run status for pipeline run with id $pipelineRunId", exception) {

}
