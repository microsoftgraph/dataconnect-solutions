/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.configs

import java.time.LocalDateTime

case class HRDataEmployeeProfileVersionConfiguration(date: LocalDateTime) {
  require(date != null, "Date must be defined")
}
