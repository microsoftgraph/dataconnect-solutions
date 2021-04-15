package com.microsoft.graphdataconnect.skillsfinder.models

object SearchWeightingOrder extends Enumeration {
  val RELEVANCE: SearchWeightingOrder.Value = Value("Relevance")
  val VOLUME: SearchWeightingOrder.Value = Value("Volume")
  val FRESHNESS: SearchWeightingOrder.Value = Value("Freshness")
}
