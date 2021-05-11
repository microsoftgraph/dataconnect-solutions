/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.repositories.settings

import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.SearchSettings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
trait SearchSettingsRepository extends JpaRepository[SearchSettings, String] {

  def findByUserEmail(@Param("user_email") userEmail: String): Optional[SearchSettings]

}
