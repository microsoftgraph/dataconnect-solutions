---
title: "BasicDataSet_v0.TodoTasks_v0"
description: "Contains all the tasks in the signed-in user's mailbox."
author: "pushkarprotik"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.TodoTasks_v0 dataset contains all the tasks in the signed-in user's mailbox.

## Properties

| Name | Type | Description |
|--|--|--|
| ptenant | string | The unique identifier of the tenant. |
| puser | string | The unique identifier of the user. |
| AssignedTo | string | The name of the person who has been assigned the task. |
| Attachments | string | The FileAttachment and ItemAttachment attachments for the message. Format: ARRAY<STRUCT<LastModifiedDateTime: STRING, Name: STRING, ContentType: STRING, Size: INT, IsInline: BOOLEAN, Id: STRING, ContentId: STRING, ContentLocation: STRING, ContentBytes: STRING >> |
| Body | string | The task body that typically contains information about the task. Note that only HTML type is supported. Format: STRUCT<ContentType: INT32, Content: STRING> |
| Categories | string | The categories associated with the task. Format: ARRAY<STRING> |
| ChangeKey | string | The version of the task. |
| CompletedDateTime | datetime | The date in the specified time zone that the task was finished. |
| CreatedDateTime | datetime | The date and time when the task was created. By default, it is in UTC. This is a dateFilter property. |
| DueDateTime | datetime | The date in the specified time zone that the task is to be finished. |
| HasAttachments | boolean | Set to true if the task has attachments. |
| Id | string | The unique identifier of the task. |
| Importance | string | The importance of the event: Low, Normal, High. |
| IsReminderOn | boolean | Set to true if an alert is set to remind the user of the task. |
| LastModifiedDateTime | datetime | The date and time when the task was last modified. By default, it is in UTC. This is a dateFilter property. |
| Owner | string | The name of the person who created the task. |
| ParentFolderId | string | The unique identifier for the task's parent folder. |
| Recurrence | string | The recurrence pattern for the task. Format: STRUCT<Pattern: STRUCT<Type: STRING, \`Interval\`: INT, Month: INT, DayOfMonth: INT, DaysOfWeek: ARRAY<STRING>, FirstDayOfWeek: STRING, Index: STRING>, \`Range\`: STRUCT<Type: STRING, StartDate: STRING, EndDate: STRING, RecurrenceTimeZone: STRING, NumberOfOccurrences: INT>> |
| ReminderDateTime | datetime | The date and time for a reminder alert of the task to occur. |
| Sensitivity | string | Indicates the level of privacy for the event: Normal, Personal, Private, Confidential. |
| StartDateTime | datetime | The date in the specified time zone when the task is to begin. |
| Status | string | Indicates state or progress of the task: NotStarted, InProgress, Completed, WaitingOnOthers, Deferred. |
| Subject | string | A brief description or title of the task. |