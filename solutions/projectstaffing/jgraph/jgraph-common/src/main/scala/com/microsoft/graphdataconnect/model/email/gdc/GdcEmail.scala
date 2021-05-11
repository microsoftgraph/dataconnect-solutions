/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.email.gdc

case class GdcEmail(Id: String = "",
                    CreatedDateTime: String = "",
                    LastModifiedDateTime: String = "",
                    ChangeKey: String = "",
                    Categories: Seq[String] = List(),
                    ReceivedDateTime: String = "",
                    SentDateTime: String = "",
                    HasAttachments: Option[Boolean] = None,
                    InternetMessageId: String = "",
                    Subject: String = "",
                    Importance: String = "",
                    ParentFolderId: String = "",
                    ConversationId: String = "",
                    IsDeliveryReceiptRequested: Option[Boolean] = None,
                    IsReadReceiptRequested: Option[Boolean] = None,
                    IsRead: Option[Boolean] = None,
                    IsDraft: Option[Boolean] = None,
                    WebLink: String = "",
                    UniqueBody: GdcBody = GdcBody("", ""),
                    Sender: GdcSender = GdcSender(GdcEmailAddressFields("", "")),
                    From: GdcFrom = GdcFrom(GdcEmailAddressFields("", "")),
                    ToRecipients: Seq[EmailAddress] = List(),
                    CcRecipients: Seq[EmailAddress] = List(),
                    BccRecipients: Seq[EmailAddress] = List(),
                    ReplyTo: Seq[EmailAddress] = List(),
                    ODataType: String = "#Microsoft.OutlookServices.Message",
                    puser: String = "",
                    ptenant: String = "",
                    pAdditionalInfo: String = "",
                    datarow: Option[Int] = None,
                    userrow: Option[Int] = None,
                    pagerow: Option[Int] = None,
                    rowinformation: RowInformation = RowInformation()
                   )
