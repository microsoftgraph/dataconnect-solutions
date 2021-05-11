/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.models

import java.time.OffsetDateTime

case class EmployeeRankingSearchSettings(freshness: Integer,
                                         freshnessEnabled: Boolean,
                                         volume: Integer,
                                         volumeEnabled: Boolean,
                                         relevanceScore: Integer,
                                         relevanceScoreEnabled: Boolean,
                                         freshnessBeginDate: OffsetDateTime,
                                         freshnessBeginDateEnabled: Boolean,
                                         includedEmailDomains: List[String],
                                         includedEmailDomainsEnabled: Boolean,
                                         excludedEmailDomains: List[String],
                                         excludedEmailDomainsEnabled: Boolean)
