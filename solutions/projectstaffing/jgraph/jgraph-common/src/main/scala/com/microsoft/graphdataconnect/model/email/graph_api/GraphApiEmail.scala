/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.graphdataconnect.model.email.graph_api

case class GraphApiEmail(odata: String = "",
                         id: String = "",
                         createdDateTime: String = "",
                         lastModifiedDateTime: String = "",
                         changeKey: String = "",
                         categories: Seq[String] = List(),
                         receivedDateTime: String = "",
                         sentDateTime: String = "",
                         hasAttachments: Option[Boolean] = None, //using Optional[Boolean] to allow for nullable values in source data
                         internetMessageId: String = "",
                         subject: String = "",
                         bodyPreview: String = "",
                         importance: String = "",
                         parentFolderId: String = "",
                         conversationId: String = "",
                         conversationIndex: String = "",
                         isDeliveryReceiptRequested: Option[Boolean] = None,
                         isReadReceiptRequested: Option[Boolean] = None,
                         isRead: Option[Boolean] = None,
                         isDraft: Option[Boolean] = None,
                         webLink: String = "",
                         inferenceClassification: String = "",
                         body: Body = Body("", ""),
                         sender: Sender = Sender(EmailAddressFields("", "")),
                         from: From = From(EmailAddressFields("", "")),
                         toRecipients: Option[Seq[EmailAddress]] = Some(List()),
                         ccRecipients: Option[Seq[EmailAddress]] = Some(List()),
                         bccRecipients: Option[Seq[EmailAddress]] = Some(List()),
                         replyTo: Option[Seq[EmailAddress]] = Some(List()),
                         flag: Flag = Flag("")
                        )
