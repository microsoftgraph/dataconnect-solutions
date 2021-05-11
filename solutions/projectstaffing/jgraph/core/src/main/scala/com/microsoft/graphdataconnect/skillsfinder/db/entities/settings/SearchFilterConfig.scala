/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.db.entities.settings

import com.microsoft.graphdataconnect.skillsfinder.models.FilterType

case class SearchFilterConfig(filterType: FilterType, isActive: Boolean)
