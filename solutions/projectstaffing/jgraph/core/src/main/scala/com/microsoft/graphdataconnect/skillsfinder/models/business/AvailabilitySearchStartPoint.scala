/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business

case class AvailabilitySearchStartPoint(availabilityStartLimit: Option[String],
                                        ignoreEmailAddresses: List[String])
