/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.entities.meta

import javax.persistence.{Column, Entity, Id, Table}

@Table(name = "configurations")
@Entity
class Configuration {

  @Id
  @Column(name = "key_name")
  var key: String = _

  @Column(name = "value", columnDefinition = "NVARCHAR")
  var value: String = _

}

object Configuration {
  def apply(key: String, value: String): Configuration = {
    val configuration = new Configuration()
    configuration.key = key
    configuration.value = value
    configuration
  }
}

