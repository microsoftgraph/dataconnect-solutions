---
title: "BasicDataSet_v0.TeamsStandardChannelMessages_v0"
description: "Contains channel posts, and messages from Standard Channels in Teams."
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.TeamsStandardChannelMessages_v0 dataset Contains channel posts, and messages from [Standard Channels](https://docs.microsoft.com/en-us/microsoftteams/teams-channels-overview#channel-feature-comparison) in Teams.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| id | string |Unique ID for the Teams channel message formatted according to RFC2822.| 1614618259349 |0|false|
| CreatedDateTime | datetime |UTC formatted created date time of message.|`2018-11-07T14:37:21Z`|1|true|
| LastModifiedDateTime | datetime |UTC formatted last modified date time of message.|`2018-11-07T14:37:21Z`|1|true|
| SentDateTime | datetime |UTC formatted sent date time of message.|`2018-11-07T14:37:21Z`|1|true|
| Subject | string |Subject of the message or announcement.|Hello World|0|false|
| Importance | string |The importance defined by the sender.|Normal|0|false|
| From | string |Nested property outlining the sender of the message.|`{\"application\": null,\"device\": null,\"conversation\": null, \"user\": {\"id\": \"8ea0e38b-efb3-4757-924a-5f94061cf8c2\",\"displayName\":\"Robin Kline\",\"userIdentityType\": \"aadUser\"}}`|0|false|
| Body | string |The message text body.|`{\"contentType\": \"html\", \"content\": \"<div><div>Test</div></div>\"}`|0|false|
| ParentFolderId | string |The unique identifier for the teams message parent folder.|AQMkAGU4MmU1ZjFlLWM4ZDQtNGRhNi1iNTkyLTYxMGUyZjIwM2RjMAAuAAADY0XXOiFEs0|0|false|
| ConversationId | string |The ID of the conversation the email belongs to.|AAQkAGU4MmU1ZjFlLWM4ZDQtNGRhNi1iNTkyLT|0|false|
| Mentions | string |All the @ mentions in body of the message.|`[{\"id\": 1024, \"mentionText\": \"test mention\", \"mentioned\": {\"@odata.type\": \"user\"}]`|0|false|
| IsRead | boolean |Indicates whether the message has been read.|true|0|false|
| Sender | string |The account that is actually used to generate the teams message. Format: `STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>`|`{ \"EmailAddress\": { \"Name\": \"John Doe\", \"Address\": \"johnd@contoso.com\" } }`|0|false|
| ReplyTo| string |The email addresses to use when replying. Format: `ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>>>`|`[{ \"EmailAddress\": { \"Name\": \"John Doe\", \"Address\": \"johnd@contoso.com\" } }]`|0|false|
| Flag| string |The flag value that indicates the status, start date, due date, or completion date for the teams message.|`{\"FlagStatus\": \"NotFlagged\"}`|0|false|
| Attachments| string |List of attachments on the message.|`[{\"Id\": \"AAMkADFiNTAUhhYuYi0=\",\"Name\": \"How to retrieve item attachment using Outlook REST API\",\"ContentType\": \"application/octet-stream\",\"Size\": 71094,\"IsInline\": false,\"LastModifiedDateTime\": \"2015-09-24T05:57:59Z\",\"SourceUrl\": \"https://microsoft-my.sharepoint-df.com/personal/user_testtenant_com/Documents/MicrosoftTeamsChatFiles/sample.txt\",\"ProviderType\": \"OneDriveBusiness\",\"ThumbnailUrl\": null,\"PreviewUrl\": null,\"Permission\": \"Other\",\"IsFolder\": false}]`|0|false|
