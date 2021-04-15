package com.microsoft.graphdataconnect.skillsfinder.models.business.employee

case class EmployeeResponseWrapper(reachedEndOfResults: Boolean,
                                   employees: Seq[Employee])
