package com.microsoft.graphdataconnect.skillsfinder.db.repositories.employee

import com.microsoft.graphdataconnect.skillsfinder.db.entities.employee.PastProject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait EmployeePastProjectRepository extends JpaRepository[PastProject, Long] {

}
