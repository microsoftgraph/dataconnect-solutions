/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.common.db.repositories.meta

import com.microsoft.graphdataconnect.watercooler.common.db.entities.meta.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
trait ConfigurationRepository extends JpaRepository[Configuration, String] {

}
