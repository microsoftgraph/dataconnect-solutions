package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.HRDataIngestionState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait HRDataIngestionStateRepository extends JpaRepository[HRDataIngestionState, Long] {

  def findFirstByOrderByIdDesc(): Optional[HRDataIngestionState]

}

