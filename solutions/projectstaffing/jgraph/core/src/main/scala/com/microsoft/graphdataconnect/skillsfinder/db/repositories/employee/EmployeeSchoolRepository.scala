package com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.School
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait EmployeeSchoolRepository extends JpaRepository[School, Long] {

}
