/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.exceptions

class StopTriggerFailureException(triggerName: String, exception: Throwable = null)
  extends Exception(s"Failed to stop trigger $triggerName", exception) {

}
