/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.configs

//TODO this class is currently duplicated in admin package
object IngestionMode extends Enumeration {
  type IngestionMode = Value

  val Production: IngestionMode.Value = Value("production_mode")
  val Sample: IngestionMode.Value = Value("sample_mode")
  val Simulated: IngestionMode.Value = Value("simulated_mode")

  def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(Production)

  implicit val IngestionModeScoptReader: scopt.Read[IngestionMode.Value] = scopt.Read.reads {
    case "production_mode" => IngestionMode.Production
    case "sample_mode" => IngestionMode.Sample
    case "simulated_mode" => IngestionMode.Simulated
  }

}
