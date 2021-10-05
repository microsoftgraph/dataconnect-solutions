---
title: "TeamChat_v1"
description: "Contains Teams chat messages for one-on-one and group chat messages. This dataset excludes chat messages explicitly deleted by users."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains [Teams chat messages](https://support.microsoft.com/office/first-things-to-know-about-chat-in-microsoft-teams-88ed0a06-6b59-43a3-8cf7-40c01f2f92f2) for one-on-one and group chat messages. This dataset excludes chat messages explicitly deleted by users.

## Properties

| Name                 | Type     | Description                                                                                                                                                                                         |
| -------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| puser                         | string   | The unique identifier of the user.  |
| Id                   | string   | The unique identifier of the message.                                                                                                                                                               |
| CreatedDateTime      | datetime | The date and time the message was created.                                                                                                                                                          |
| LastModifiedDateTime | datetime | The date and time the message was last changed.                                                                                                                                                     |
| ChangeKey            | string   | The version of the message.It is same as odata.etag.                                                                                                                                                |
| Categories           | string   | The categories associated with the message. Format: ARRAY<STRING>                                                                                                                                   |
| ReceivedDateTime     | datetime | The date and time the teams message was received.                                                                                                                                                   |
| SentDateTime         | datetime | The date and time the teams message was sent.                                                                                                                                                       |
| HasAttachments       | boolean  | Indicates whether the teams message is an attachment.                                                                                                                                               |
| InternetMessageId    | string   | The internet teams message id                                                                                                                                                                       |
| Subject              | string   | The subject of the teams message.                                                                                                                                                                   |
| BodyPreview          | string   | The first 255 characters of the teams message body. It is in text format.                                                                                                                           |
| Importance           | string   | The importance of the teams message: Low = 0, Normal = 1, High = 2.                                                                                                                                 |
| ParentFolderId       | string   | The unique identifier for the teams message parent folder.                                                                                                                                          |
| ConversationId       | string   | The ID of the conversation the email belongs to.                                                                                                                                                    |
| ConversationIndex    | string   | Indicates the position of the message within the conversation.                                                                                                                                      |
| IsRead               | boolean  | Indicates whether the message has been read.                                                                                                                                                        |
| IsDraft              | boolean  | Indicates whether the message is a draft. A message is a draft if it hasn't been sent yet.                                                                                                          |
| Body                 | string   | The body of the teams message. It can be in HTML or text format. Format: STRUCT<ContentType: INT32, Content: STRING>                                                                                |
| Sender               | string   | The account that is actually used to generate the teams message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>                                                                |
| From                 | string   | The mailbox owner and sender of the teams message. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>                                                                              |
| ToRecipients         | string   | The To recipients for the teams message. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>>                                                                                 |
| ReplyTo              | string   | The email addresses to use when replying. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>>                                                                                |
| Flag                 | string   | The flag value that indicates the status, start date, due date, or completion date for the teams message.                                                                                           |
| Attachments          | string   | The FileAttachment and ItemAttachment attachments for the message. Format: ARRAY<STRUCT<LastModifiedDateTime: STRING, Name: STRING, ContentType: STRING, Size: INT, IsInline: BOOLEAN, Id: STRING>> |
