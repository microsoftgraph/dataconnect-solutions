---
title: "TeamsStandardChannelMessages_v0"
description: "Contains the details for a Teams standard channel messages."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the details from a Teams standard channel messages.

## Properties

| Name | Type | Description |
|--|--|--|
| id | string | Unique ID for the Teams channel message formatted according to RFC2822. |
| CreatedDateTime | dateTime | UTC formatted created date time of message. |
| LastModifiedDateTime | dateTime | UTC formatted last modified date time of message. |
| SentDateTime | dateTime | UTC formatted sent date time of message. |
| Subject | string | Subject of the message or announcement. |
| Importance | string | The importance defined by the sender. |
| From | string | Nested property specifying the sender of the message. |
| Body | string | The message text body. |
| ParentFolderId | string | The unique identifier for the teams message parent folder. |
| ConversationId | string | The ID of the conversation the email belongs to. |
| Mentions | string | All the @ mentions in body of the message. |
| IsRead | boolean | Indicates whether the message has been read. |
| Sender | string | The account that is actually used to generate the teams message. Format: `STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>` |
| ReplyTo | string | The email addresses to use when replying. |
| Flag | string | The flag value that indicates the status, start date, due date, or completion date for the teams message. |
| Attachments | string | List of attachments on the message. |
