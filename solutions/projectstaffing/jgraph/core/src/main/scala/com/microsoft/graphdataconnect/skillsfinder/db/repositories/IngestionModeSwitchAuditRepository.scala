/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.IngestionModeSwitchLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait IngestionModeSwitchAuditRepository extends JpaRepository[IngestionModeSwitchLog, Long] {

  def findFirstByOrderByIdDesc(): Optional[IngestionModeSwitchLog]

}
