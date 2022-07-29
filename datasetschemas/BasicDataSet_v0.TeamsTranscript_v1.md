---
title: "BasicDataSet_v0.TeamsTranscript_v1"
description: "Contains transcripts from calls and meetings in Teams when the transcript is enabled for a meeting or a call."
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.TeamsTranscript_v1 dataset contains transcripts from calls and meetings in Teams when the transcript is enabled for a meeting or a call.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| CreatedDateTime | datetime |Date time in UTC when the transcripts was finally saved after a meeting or a call.| `2022-04-05T22:02:49Z` |1|true|
| ScopeId | string |Unique identifier for the transcript across all transcripts in a tenant.| 3e0e8531-7611-428c-b74a-804b762beec0 |0|false|
| ICalUId | string |Unique identifier for the meeting for which this transcript was generated.| 040000008200e00074c5b7101a82e00800000000c571d705024ad801000000000000000010000000282c82729a9fe74fac16ccd67a56eb81 |0|false|
| Entries | string |Time ordered sequence of transcribed utterances in a meeting or a call. Format: ```STRUCT<`editorId`:STRING, `editId`:STRING, `entryId`:STRING, `text`:STRING, `speakerId`:STRING, `displayName`:STRING, `confidence`:DOUBLE, `startTime`:STRING, `endTime`:STRING>```| `[{ \"entryId\": \"ed734acc-e84f-444b-b73a-f6f05f1b9304/38\", \"text\": \"This is a sample transcription text.\", \"speakerId\": \"e530bf91-e834-4369-a808-e0d12b1008cd@8e55195d-f07c-44f0-8108-40e4352e3e74\", \"displayName\": \"FirstName LastName\", \"confidence\": 0.91822494, \"startTime\": \"2022-04-05T22:01:49.783167Z\", \"endTime\": \"2022-04-05T22:01:58.823167Z\" }]` |0|false|
| Events | string |Time ordered sequence of actions in a meeting like meeting join, transcript started, stopped, and others. Format: ```STRUCT<`eventType`:STRING, `eventId`:STRING, `chatMessageId`:STRING, `callId`:STRING, `callParticipants`:ARRAY<STRING>, `userId`:STRING, `displayName`:STRING, `timestamp`:STRING, `reason`:STRING>```| `[{ \"eventType\": \"TranscriptStarted\", \"eventId\": \"ef739acc-e84e-444b-b73a-f6f05f1b9304/3\", \"callId\": \"3cf15961-1b39-492c-8516-437d4992c609\", \"callParticipants\": [ \"e530bf91-e844-4368-a808-e0d12b1008cd@8e56185d-f07c-44f0-8108-40e4352e3e74\" ], \"userId\": \"e530bf91-e844-4368-a808-e0d12b1008cd@8e56185d-f07c-44f0-8108-40e4352e3e74\", \"displayName\": \"FirstName LastName\", \"timestamp\": \"2022-04-05T22:01:47.5488427Z\" }]`|0|false|
| LastModifiedTime | datetime |DateTime in UTC when the transcript was last modified or edited.| `2022-04-06T22:03:31.3526502Z` | 0 |false |