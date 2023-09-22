---
title: "BasicDataSet_v0.TeamsChannelDetails_v0"
description: "Contains details about Channels in a team."
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.TeamsChannelDetails_v0 dataset contains details about Channels in a team.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| id | string |Unique directory ID for the Teams channel.| `19:09fc54a3141a45d0bc769cf506d2e079@thread.skype` |0|false|
| createdDateTime | datetime |Timestamp in UTC when channel was created.| `2018-11-07T14:37:21Z` |1|true|
| displayName | string |The display name for the channel.| Onboarding |0|false|
| description | string |User specified description for the channel.| Welcome to HR Taskforce team. |0|false|
| isFavoriteByDefault | boolean |Boolean that specifies if the Channel is favorite by default for all members of the team.| false|0|false|
| email | string |SMTP address of the channel for messaging.| 9496d2eb.microsoft.com@amer.teams.ms|0|false|
| webUrl | string |Web URL for the Teams channel.|https://teams.microsoft.com/l/channel/19%3askypespaces_7376b5607fb94afcb5d630778145cfaf%40thread.skype/General?groupId=c5d5908e-7c3c-43ff-9079-160a647b54e4&tenantId=72f988bf-86f1-41af-91ab-2d7cd011db47|1|false|
| membershipType | string |Membership or Type of the channel, i.e. standard channel, private channel, etc.|standard|0|false|
