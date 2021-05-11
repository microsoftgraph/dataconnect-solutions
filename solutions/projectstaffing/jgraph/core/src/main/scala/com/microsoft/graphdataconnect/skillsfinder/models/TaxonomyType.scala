/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models

object TaxonomyType extends Enumeration {
  val SOFTWARE = Value("software")
  val DATA_SCIENCE = Value("data_science")
  val FACILITIES = Value("facilities")
  val FINANCE = Value("finance")
  val HR = Value("human_relations")
  val LEGAL = Value("legal")
  val SALES_MARKETING = Value("sales_marketing")
  val OILGAS = Value("oilgas")
  val HEALTHCARE = Value("healthcare")

  def from(taxoAsString: String): TaxonomyType.Value = {
    taxoAsString match {
      case "software" => SOFTWARE
      case "data_science" => DATA_SCIENCE
      case "facilities" => FACILITIES
      case "finance" => FINANCE
      case "human_relations" => HR
      case "legal" => LEGAL
      case "sales_marketing" => SALES_MARKETING
      case "oilgas" => OILGAS
      case "healthcare" => HEALTHCARE
      case _ => SOFTWARE //TODO introduce here something like ALL to
    }
  }
}
