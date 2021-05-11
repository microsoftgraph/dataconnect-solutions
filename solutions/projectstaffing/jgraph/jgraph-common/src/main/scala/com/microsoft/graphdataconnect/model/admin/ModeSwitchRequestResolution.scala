/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.admin

object ModeSwitchRequestResolution extends Enumeration {
  type ModeSwitchRequestResolution = Value

  val Accepted: ModeSwitchRequestResolution.Value = Value("ACCEPTED")
  val Rejected: ModeSwitchRequestResolution.Value = Value("REJECTED")
}