---
title: "Message_v1"
description: "Contains the email message in each user's mailbox."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the email message in each user's mailbox.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| receivedDateTime | datetime | The date and time the message was received. |
| sentDateTime | datetime | The date and time the message was sent. |
| hasAttachments | boolean | Indicates whether the message has attachments. |
| internetMessageId | string | The internet message id |
| subject | string | The subject of the message. |
| importance | string | The importance of the message: Low = 0, Normal = 1, High = 2. |
| parentFolderId | string | The unique identifier for the message's parent folder. |
| sender | string | The account that is actually used to generate the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| from | string | The mailbox owner and sender of the message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| toRecipients | string | The To recipients for the message. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| ccRecipients | string | The Cc recipients for the message. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| bccRecipients | string | The Bcc recipients for the message. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| replyTo | string | The email addresses to use when replying. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>> |
| conversationId | string | The ID of the conversation the email belongs to. |
| uniqueBody | string | The body of the message that is unique to the conversation.Format: STRUCT<ContentType: INT32, Content: STRING> |
| isDeliveryReceiptRequested | boolean | Indicates whether a read receipt is requested for the message. |
| isReadReceiptRequested | boolean | Indicates whether a read receipt is requested for the message. |
| isRead | boolean | Indicates whether the message has been read. |
| isDraft | boolean | Indicates whether the message is a draft. A message is a draft if it hasn't been sent yet. |
| webLink | string | The URL to open the message in Outlook Web App. |
| createdDateTime | datetime | The date and time the message was created. |
| lastModifiedDateTime | datetime | The date and time the message was last changed. |
| changeKey | string | The version of the message. |
| categories | string | The categories associated with the message. Format: ARRAY<STRING> |
| id | string | The unique identifier of the message. |
| attachments | string | The FileAttachment and ItemAttachment attachments for the message. Navigation property. Format: ARRAY<STRUCT<LastModifiedDateTime: STRING, Name: STRING, ContentType: STRING, Size: INT, IsInline: BOOLEAN, Id: STRING>> |
| inferenceClassification | string | The classification of the message for the user, based on inferred relevance or importance, or on an explicit override. The possible values are: Focused or Other. |
| flag | string | The flag value that indicates the status, start date, due date, or completion date for the message. |
| body | string | The body of the message. It can be in HTML or text format. Format: STRUCT<ContentType: INT32, Content: STRING> |
| bodyPreview | string | The first 255 characters of the message body. It is in text format. |
| internetMessageHeaders | string | A collection of message headers defined by RFC5322. The set includes message headers indicating the network path taken by a message from the sender to the recipient. It can also contain custom message headers that hold app data for the message. Format: ARRAY<STRUCT<Name: STRING, Value: STRING>>> |
| conversationIndex | string | Indicates the position of the message within the conversation |
