/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.admin

object ModeSwitchRequestType extends Enumeration {
  type ModeSwitchRequestType = Value

  val Switch: ModeSwitchRequestType.Value = Value("SWITCH")
  val Resume: ModeSwitchRequestType.Value = Value("RESUME")
  val Retry: ModeSwitchRequestType.Value = Value("RETRY")
}