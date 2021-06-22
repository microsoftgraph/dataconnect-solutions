/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.exceptions

class PipelineRunCreationFailedException(pipelineName: String, exception: Throwable = null)
  extends Exception(s"Failed to create pipeline run for pipeline $pipelineName", exception) {

}
