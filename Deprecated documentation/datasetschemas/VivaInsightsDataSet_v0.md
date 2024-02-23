---
title: "VivaInsightsDataSet_v0 "
description: "Contains Viva Insights metrics."
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.TeamsStandardChannelMessages_v0 dataset contains Viva Insights metrics.  .

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| AfterHoursCollaboration | double | Number of hours the person spent in meetings, emails, IMs, and calls with at least one other person, either internal or external, after deduplication of time due to overlapping activities (for example, calls during a meeting), outside of working hours. |  |0|false|
| AfterHoursCollaborationEmails | double | Number of hours the person spent sending and receiving emails outside of working hours. |  |0|false|
| AfterHoursCollaborationInstantMessages | double | Number of hours a person spent in instant messages through Teams, outside of working hours. |  |0|false|
| AfterHoursCollaborationMeetings | double | Number of hours the person spent in meetings, emails, IMs, and calls with at least one other person, either internal or external, after deduplication of time due to overlapping activities (for example, calls during a meeting), outside of working hours. |  |0|false|
| AfterHoursCollaborationScheduledCalls | double | Number of hours a person spent in scheduled calls through Teams, outside of working hours. For calls that started during working hours, this number only includes the part of the call that occurred outside of that person’s work schedule (as set in Outlook). |  |0|false|
| AfterHoursCollaborationAdhocCalls | double | Number of hours a person spent in scheduled calls through Teams, outside of working hours. For calls that started during working hours, this number only includes the part of the call that occurred outside of that person’s work schedule (as set in Outlook). |  |0|false|
| CollaborationHours | double | "Number of hours the person spent in meetings, emails, IMs, and calls with at least one other person, either internal or external, after deduplication of time due to overlapping activities (for example, calls during a meeting). |  |0|false|
| CollaborationHoursEmails | double | Number of hours the person spent sending and receiving emails.|  |0|false|
| CollaborationHoursExternal | double | Number of hours the person spent in meetings, emails, IMs, and calls with at least one other person outside the company, after deduplication of time due to overlapping activities (for example, calls during a meeting). |  |0|false|
| CollaborationHoursInstantMessages | double | Number of hours the person spent sending and receiving instant messages through Teams with at least one other person. |  |0|false|
| CollaborationHoursMeetings | double | Number of hours the person spent in meetings with at least one other person. |  |0|false|
| CollaborationHoursScheduledCalls | double | Total number of hours a person spent time in scheduled calls with Teams, during working hours. |  |0|false|
| CollaborationHoursAdhocCalls | double | Total number of hours a person spent time in unscheduled calls with Teams, during working hours. |  |0|false|
| ConflictingMeetingHours | double | "Number of meeting hours where the person had overlapping meetings in their calendar. The count includes the entire duration of all overlapping meetings, not just the amount of time that overlaps. (This number includes all non-declined meeting times, which includes accepted, tentative, or no responses to meeting invitations.) |  |0|false|
| InfluenceScore | double | A numeric score that indicates how well connected a person is within the company. A higher score means that the person is better connected and has greater potential to drive change. (A person’s connection score is based on the frequency of collaboration activities, which include emails, meetings, Teams calls, and Teams chats with other people within the company.) |  |0|false|
| OneOnOneMeetingHours | double | Number of meeting hours involving only the person and their manager. |  |0|false|
| SkipLevelMeetingHours | double | Number of meeting hours that the person attends where their manager's manager also attends the meeting. |  |0|false|
| TotalFocusHours | double | Total number of hours made up of two-hour or more blocks of time where the person had no meetings. The new "Uninterrupted focus hours" metric is now available. It measures the total number of hours where a person has more than or equal to a one-hour block of uninterrupted time to focus on work during their set working hours. Uninterrupted time is when the person has no collaboration activity, including attending a meeting or an unscheduled Teams call, sending emails, or replying or sending an instant message or Teams chat. |  |0|false|
