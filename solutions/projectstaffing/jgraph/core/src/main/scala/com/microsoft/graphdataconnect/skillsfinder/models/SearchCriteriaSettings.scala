/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models

import com.microsoft.graphdataconnect.skillsfinder.db.entities.settings.SearchCriterion

case class SearchCriteriaSettings(searchCriteria: List[SearchCriterion],
                                  useReceivedEmailsContent: Boolean)
