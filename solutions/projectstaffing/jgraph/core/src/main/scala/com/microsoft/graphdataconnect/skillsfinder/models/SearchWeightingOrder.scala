/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models

object SearchWeightingOrder extends Enumeration {
  val RELEVANCE: SearchWeightingOrder.Value = Value("Relevance")
  val VOLUME: SearchWeightingOrder.Value = Value("Volume")
  val FRESHNESS: SearchWeightingOrder.Value = Value("Freshness")
}
