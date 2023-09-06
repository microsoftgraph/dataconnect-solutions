---
title: "BasicDataSet_v0.OutlookGroupConversations_v0"
description: "Contains messages and conversations from Microsoft 365 groups in Outlook. "
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.OutlookGroupConversations_v0 dataset contains messages and conversations from [Microsoft 365](https://support.microsoft.com/en-us/office/create-a-group-in-outlook-04d0c9cf-6864-423c-a380-4fa858f27102) groups in Outlook. This sentence is for demo purposes.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| Attachments | string | All attachments in a message. Contains metadata of attachments, such as LastModifiedDateTime, Name, ContentType, Size, IsInline, SourceUrl, and ProviderType. The actual attachment files are not included.| |0 | false |
| BodyPreview | string | The first 255 characters of the message body content.| |0 | false |
| CcRecipients | string | The Cc: recipients for the message.| |0 | false |
| ConversationId | string | The ID of the conversation that the email belongs to. | |0 | false |
| ConversationIndex | string | Indicates the relative position of the message within the conversation that the message belongs to | |0 | false |
| CreatedDateTime | datetime | The date and time the message was created. | | 1 | true |
| From | string | The mailbox owner and sender of the message. | | 0 | false |
| HasAttachments | string | `true`, `false`, Indicates whether the message has attachments. This property doesn't include inline attachments, so if a message contains only inline attachments, this property is false. | | 0 | false |
| Id | string | 'Unique identifier for the message (note that this value may change if a message is moved or altered). | | 0 | false |
| Importance | string | The importance of the message: `high`, `normal`, `low` | | 0 | false |
| InternetMessageId | string | The message ID in the format specified by RFC2822.| | 0 | false|
| IsRead | string | Indicates whether the message has been read.| | 0 | false|
| LastModifiedDateTime | datetime | The date and time the message was last changed.| | 1 | true|
| LikesPreview | string | Number of Likes of this message.| | 0 | false|
| MentionsPreview | string | Preview of the mentions in the message. | | 0 | false|
| ReplyTo | string | The email addresses to use when replying. | | 0 | false|
| Body | string | The UniqueBody in raw format | | 0 | false|
| ReceivedDateTime | datetime | The date and time the message was received. | | 1 | true|
| Sender | string | The account that is actually used to generate the message. | | 0 | false|
| SentDateTime | datetime | The date and time the message was sent. | | 1 | true|
| Subject | string | The subject of the message. | | 0 | false|
| ToRecipients | string | The To: recipients for the message. | | 0 | false|
| UniqueBody | string | The part of the body of the message that is unique to the current message. It is in text format. | | 0 | false|
| WebLink | string | The URL to open the message in Outlook Web App | | 0 | false|
| Mentions | string | Represents a notification to a person based on the person's email address. This type of notification is also known as @-mentions. | | 0 | false|