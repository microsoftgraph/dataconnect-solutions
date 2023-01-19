---
title: "Message_v0"
description: "Contains the email messages from a user’s mailbox."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the email messages from each user’s mailbox.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| ReceivedDateTime | datetime | The date and time the message was received. |
| SentDateTime | datetime | The date and time the message was sent. |
| HasAttachments | boolean | Indicates whether the message has attachments. |
| InternetMessageId | string | The internet message id |
| Subject | string | The subject of the message. |
| Importance | string | The importance of the message: Low = 0, Normal = 1, High = 2. |
| ParentFolderId | string | The unique identifier for the message's parent folder. |
| Sender | string | The account that is actually used to generate the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| From | string | The mailbox owner and sender of the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| ToRecipients | string | The To recipients for the message. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| CcRecipients | string | The Cc recipients for the message. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| BccRecipients | string | The Bcc recipients for the message. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| ReplyTo | string | The email addresses to use when replying. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| ConversationId | string | The ID of the conversation the email belongs to. |
| UniqueBody | string | The body of the message that is unique to the conversation.Format: STRUCT<ContentType: INT32, Content: STRING> |
| IsDeliveryReceiptRequested | boolean | Indicates whether a read receipt is requested for the message. |
| IsReadReceiptRequested | boolean | Indicates whether a read receipt is requested for the message. |
| IsRead | boolean | Indicates whether the message has been read. |
| IsDraft | boolean | Indicates whether the message is a draft. A message is a draft if it hasn't been sent yet. |
| WebLink | string | The URL to open the message in Outlook Web App. |
| CreatedDateTime | datetime | The date and time the message was created. |
| LastModifiedDateTime | datetime | The date and time the message was last changed. |
| ChangeKey | string | The version of the message. |
| Categories | string | The categories associated with the message. Format: ARRAY<STRING> |
| Id | string | The unique identifier of the message. |
| Attachments | string | The FileAttachment and ItemAttachment attachments for the message. Navigation property. Format: ARRAY<STRUCT<LastModifiedDateTime: STRING, Name: STRING, ContentType: STRING, Size: INT, IsInline: BOOLEAN, Id: STRING>> |
