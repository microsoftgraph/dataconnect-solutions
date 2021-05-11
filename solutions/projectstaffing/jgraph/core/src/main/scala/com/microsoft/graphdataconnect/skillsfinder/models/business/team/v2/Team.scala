/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models.business.team.v2

import com.microsoft.graphdataconnect.skillsfinder.models.business.employee.Employee
import com.microsoft.graphdataconnect.skillsfinder.models.business.team.v2.characteristics.{Diversity, Synergy}

case class Team(members: List[Employee],
                synergy: Synergy,
                diversity: Diversity,
                openings: Map[String, Int]) //role -> number of openings
