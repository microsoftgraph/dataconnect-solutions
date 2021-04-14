package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import java.util.Optional

import com.microsoft.graphdataconnect.skillsfinder.db.entities.ModeSwitchState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait IngestionModeSwitchStateRepository extends JpaRepository[ModeSwitchState, Long] {

  def findFirstByOrderByIdDesc(): Optional[ModeSwitchState]

}
