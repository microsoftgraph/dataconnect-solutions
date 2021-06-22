/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.watercooler.core.controllers.responses.statistics

case class SelectedParticipationStatusPerDayResponse(selectedStatusCounts: Seq[SelectedStatusGroupedByDayResultsResponse],
                                                     participationStatusCountsResponse: Seq[ParticipationStatusGroupedByDayResultsResponse],
                                                     averageGroupCountsPerDayInIntervalResponse: Seq[AvgGroupCountsInIntervalResponse],
                                                     avgMinMaxGroupSizeResponse: Seq[AvgMinMaxGroupSizeResponse],
                                                     averageParticipationPerGroupPerDay: Seq[AvgParticipationPerGroupPerDayInIntervalResponse],
                                                     weekGroupLoadResponse: WeekGroupLoadResponse)
