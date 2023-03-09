---
title: "SentItem_v1"
description: "Contains the message sent from each user's mailbox."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the message sent from each user's mailbox.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| id | string | The unique identifier of the message. |
| createdDateTime | datetime | The date and time the message was created. |
| lastModifiedDateTime | datetime | The date and time the message was last changed. |
| changeKey | string | The version of the message. |
| categories | string | The categories associated with the message. Format: ARRAY<STRING> |
| receivedDateTime | datetime | The date and time the message was received. |
| sentDateTime | datetime | The date and time the message was sent. |
| hasAttachments | boolean | Indicates whether the message has attachments. |
| internetMessageId | string | The message ID in the format specified by RFC2822. |
| subject | string | The subject of the message. |
| importance | string | The importance of the message: Low = 0, Normal = 1, High = 2. |
| parentFolderId | string | The unique identifier for the message's parent folder. |
| sender | string | The account that is actually used to generate the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| from | string | The mailbox owner and sender of the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| toRecipients | string | The To recipients for the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| ccRecipients | string | The Cc recipients for the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| bccRecipients | string | The Bcc recipients for the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| replyTo | string | The email addresses to use when replying. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| conversationId | string | The ID of the conversation that the email belongs to. |
| uniqueBody | string | The body of the message that is unique to the conversation.Format: STRUCT<ContentType: INT32, Content: STRING> |
| isDeliveryReceiptRequested | boolean | Indicates whether a read receipt is requested for the message. |
| isReadReceiptRequested | boolean | Indicates whether a read receipt is requested for the message. |
| isRead | boolean | Indicates whether the message has been read. |
| isDraft | boolean | Indicates whether the message is a draft. A message is a draft if it hasn't been sent yet. |
| webLink | string | The URL to open the message in Outlook Web App. |
| attachments | string | The FileAttachment and ItemAttachment attachments for the message. Navigation property. Format: ARRAY<STRUCT<LastModifiedDateTime: STRING, Name: STRING, ContentType: STRING, Size: INT, IsInline: BOOLEAN, Id: STRING>> |
| inferenceClassification | string | The classification of the message for the user, based on inferred relevance or importance, or on an explicit override. The possible values are: Focused or Other. |
| flag | string | The flag value that indicates the status, start date, due date, or completion date for the message. |
| body | string | The body of the message. It can be in HTML or text format. Format: STRUCT<ContentType: INT32, Content: STRING> |
| bodyPreview | string | The first 255 characters of the message body. It is in text format. |
| conversationIndex | string | Indicates the position of the message within the conversation |
