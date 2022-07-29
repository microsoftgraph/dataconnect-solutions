---
title: "BasicDataSet_v0.TeamsCallRecords_v1"
description: "Contains activity records from Teams calls and meetings."
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.TeamsCallRecords_v1 dataset Contains activity records from Teams calls and meetings.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| CreatedDateTime | datetime |Timestamp in UTC when the Call Record was created. It is approximate 9 hours after the meeting has ended.| `2022-02-19T04:39:10Z` |1|true |
| Organizer | string |Represents Caller (P2P) or Organizer (Meeting). Format: STRUCT<`tenantId`:STRING, `userAADObjectId`:STRING, `displayName`:STRING, `recipientType`:STRING>"| ```{ \"tenantId\": \"8e56195d-f07c-44f0-8108-40e4352e3e74\", \"userAADObjectId\": \"e530bf91-e844-4369-a808-e0d12b1008cd\", \"displayName\": \"Sample User\", \"recipientType\": \"User\" }``` |0|false |
| Attendees | string | Represents user(s) that participated in the meeting/call. Format: ARRAY<STRUCT<`tenantId`:STRING, `userAADObjectId`:STRING, `displayName`:STRING, `recipientType`:STRING>> | ```[ { \"tenantId\": \"8e56195d-f07c-44f0-8108-40e4352e3e74\", \"userAADObjectId\": \"e530bf91-e844-4369-a808-e0d12b1008cd\", \"displayName\": \"Sample User\", \"recipientType\": \"User\" } ]```|0|false |
| StartTime | datetime | The start time of the meeting/call | ```2022-03-04T23:28:21.7857748+00:00```|0|false |
| EndTime | datetime | The start time of the meeting/call | ```2022-03-04T23:28:21.7857748+00:00```|0|false |
| CommunicationId | string | The Chain Id/Call Id of the Call/Meeting – Unique per record. |0fe840ea-f623-408d-a316-1cf32ab7d857|0|false |
| ICalUid | string | Exchange calendar item unique id|040000008200e00074c5b7101a82e0080000000061aed77a1f30d8010000000000000000100000008c2fc6681e3c33499a281ca1b670b9a0|0|false |
| CommunicationType | string | Enum: "Call" or "Meeting"|Call|0|false |
| CommunicationSubType | string | Enum: (“MissedCall”, “VoiceMail”|MissedCall|0|false |
| ThreadId | string |Teams meeting thread Id – for correlating with messages/chats|19:meeting_Yjk2MjNmNGUtY2ExMi00MWRjLWE2MTAtNzIwYWM3NmJkMTBk@thread.v2|0|false |
| Content | string |Represents user activity/actions during the meeting. Format: ```ARRAY<STRUCT<`dateTime`:DATETIME, `action`:STRING, `participant`:STRUCT<`tenantId`:STRING, `userAADObjectId`:STRING, `displayName`:STRING, `recipientType`:STRING>>>```|```[ { \"dateTime\": \"2022-03-04T23:28:21.7857748+00:00\", \"action\": \"Leave\", \"participant\": { \"tenantId\": \"8e56195d-f07c-44f0-8108-40e4352e3e74\", \"userAADObjectId\": \"e530bf91-e844-4369-a808-e0d12b1008cd\", \"displayName\": \"Sample User\", \"recipientType\": \"User\" } } ]```|0|false |