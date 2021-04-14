package com.microsoft.graphdataconnect.skillsfinder.db.repositories

import com.microsoft.graphdataconnect.skillsfinder.db.entities.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait ConfigurationRepository extends JpaRepository[Configuration, String] {

}
