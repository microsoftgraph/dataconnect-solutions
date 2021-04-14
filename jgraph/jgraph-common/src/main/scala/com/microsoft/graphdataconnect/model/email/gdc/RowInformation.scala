package com.microsoft.graphdataconnect.model.email.gdc

case class RowInformation(errorInformation: String = "",
                          userReturnedNoData: Option[Boolean] = None,
                          isUserSummaryRow: Option[Boolean] = None,
                          userHasCompleteData: Option[Boolean] = None
                         )
