/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.skillsfinder.service

import com.microsoft.graphdataconnect.skillsfinder.db.entities.HRDataIngestionState
import com.microsoft.graphdataconnect.skillsfinder.db.repositories.HRDataIngestionStateRepository
import com.microsoft.graphdataconnect.skillsfinder.models.dto.admin.HRDataIngestionStateModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class HRDataIngestionStateService(@Autowired val hrDataIngestionStateRepository: HRDataIngestionStateRepository) {

  def getHRDataIngestionState(): Option[HRDataIngestionStateModel] = {
    Option(hrDataIngestionStateRepository.findFirstByOrderByIdDesc().orElse(null)).map(HRDataIngestionStateModel(_))
  }

  def saveHRDataIngestionState(hrDataIngestionStateModel: HRDataIngestionStateModel): Unit = {
    hrDataIngestionStateRepository.save(HRDataIngestionState(hrDataIngestionStateModel))
  }

}
