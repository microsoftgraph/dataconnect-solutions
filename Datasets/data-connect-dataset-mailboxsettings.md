---
title: "Microsoft Graph Data Connect MailboxSettings_v0 dataset"
description: "Use the MailboxSettings_v0 dataset to provide details of all users' mailbox settings."
author: "rimisra2"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# Microsoft Graph Data Connect MailboxSettings_v0 dataset

The MailboxSettings_v0 dataset provides details of all users' mailbox settings.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze the different mailbox settings configured by all the users.
- Analyze the mailbox settings created by different users of the tenant.

## Questions

The following are examples of questions that you can answer with this dataset:

- What are the different unique working hours set by users from a particular time zone in an organization?
- Which archive folder IDs are associated with which users?

## Joining with other datasets

The MailboxSettings_v0 dataset can be joined with the User dataset, and other relevant Mail datasets.

## Schema

| Name  | Type  |  Description  |  FilterOptions  |  FilterType  | 
| ----------- | ----------- | ----------- | ----------- | ----------- |
| archiveFolder |	string |	Folder id of an archive folder for the user. |	No |	None |
| timeZone |	string |	The default time zone for the user's mailbox. |	No |	None |
| automaticRepliesSetting |	object |	Configuration settings to automatically notify the sender of an incoming email with a message from the signed-in user. |	No |	None |
| language |	object |	The locale information for the user, including the preferred language and country/region. |	No |	None |
| workingHours |	object |	The days of the week and hours in a specific time zone that the user works. |	No |	None |
| delegateMeetingMessageDeliveryOptions |	string |	If the user has a calendar delegate, this specifies whether the delegate, mailbox owner, or both receive meeting messages and meeting responses. *Allowed values:* sendToDelegateAndInformationToPrincipal, sendToDelegateAndPrincipal, sendToDelegateOnly. |	No |	None |
| dateFormat |	string |	The date format for the user's mailbox. |	No |	None |
| timeFormat |	string |  	The time format for the user's mailbox. |	No |	None |
| ODataType |	string |	Data type of the current folder. |	No |	None |
| puser |	string |	User id. |	No |  	None |
| ptenant |	string |  	Tenant id. |	No |  	None |

## JSON representation

```json
{
  "archiveFolder": "string",
  "timeZone": "string",
  "automaticRepliesSetting": {"@odata.type": "microsoft.graph.automaticRepliesSetting"},  
  "language": {"@odata.type": "microsoft.graph.localeInfo"},
  "workingHours": {"@odata.type": "microsoft.graph.workingHours"}
  "delegateMeetingMessageDeliveryOptions": "String",
  "dateFormat": "string",
  "timeFormat": "string",
  "ODataType": "#microsoft.graph.mailboxSettings",  
  "puser": "String (identifier)", 
  "ptenant": "String (identifier)"
}
```

## Sample 


```json
{"archiveFolder":"AQMkAGRjNDFjNDY1LTVmNzUtNGViNC1hOTkANi1jOThlMmJmMTU3MmMALgAAA6jVIxIEDQNNtj9CZVt6SRUBAMquub9EVY9Nv31MRSqT3dQAAAIBWQAAAA==","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{},"ODataType":"#microsoft.graph.mailboxSettings","puser":"0409a7eb-588d-4871-b629-e33de72b8b0d","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AQMkAGFlMjczOTQ5LTNjMjAtNDM2ZS04YjNlLTczNmY0OTVhODFlOQAuAAADZ_EAK_Os2kO_qYnMT9a4zQEASqucXnhIlE2b8iXgsvn1qQAAAgFAAAAA","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{},"ODataType":"#microsoft.graph.mailboxSettings","puser":"1715c984-a1ce-4483-b109-643041ef4469","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AQMkAGY4Mjk2NTZjLTUxNjgtNDdlZi1hZjJhLWYwNTQ5YzVhOTkAZTIALgAAA8qoOofxQYpLp-fYvJvsCf0BAKNZARh2HqhDgQvm4HdqNToAAAIBSwAAAA==","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{},"ODataType":"#microsoft.graph.mailboxSettings","puser":"3853937f-6f46-4fff-a141-1a18be24944e","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AAMkADg2NjRmYzE5LTJmZmItNDMxNy1iMGU2LWI4ZTA4ZjJhZWFkYQAuAAAAAAB5Lb3RUjXQTq_4frfZtHdHAQBBChzDntZLTK9_In9X_H7UAAAAAIw_AAA=","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{},"ODataType":"#microsoft.graph.mailboxSettings","puser":"3eb5fed9-8c59-4eff-a9ea-ba2b5f1ac27f","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAGjHAAA=","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{"daysOfWeek":["microsoft.graph.dayOfWeek'monday'","microsoft.graph.dayOfWeek'tuesday'","microsoft.graph.dayOfWeek'wednesday'","microsoft.graph.dayOfWeek'thursday'","microsoft.graph.dayOfWeek'friday'"],"startTime":"1970-01-01T08:00:00Z","endTime":"1970-01-01T17:00:00Z","timeZone":{"name":"Pacific Standard Time"}},"ODataType":"#microsoft.graph.mailboxSettings","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AAMkADI4ZjI2M2M3LTQ2M2UtNDdmZi05N2U2LTQyMTk5NjkyZjhjMQAuAAAAAADK7KnGlO-0QozWnQc1OrNEAQAVIBI9ITL4T4hmBf4E6xqQAAAAAAFWAAA=","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{"daysOfWeek":["microsoft.graph.dayOfWeek'monday'","microsoft.graph.dayOfWeek'tuesday'","microsoft.graph.dayOfWeek'wednesday'","microsoft.graph.dayOfWeek'thursday'","microsoft.graph.dayOfWeek'friday'"],"startTime":"1970-01-01T08:00:00Z","endTime":"1970-01-01T17:00:00Z","timeZone":{"name":"Pacific Standard Time"}},"ODataType":"#microsoft.graph.mailboxSettings","puser":"6acddb90-66a1-4a1f-bbd4-4632aac05f3a","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AQMkAGUzNmIxMzcwLWNiYzAtNDkxMi05NzViLTU2M2VkMGUxNWYwYQAuAAADMfJmQyFaPkWIRgjBBrblMgEAi7RMA7eRU0GB-Rxx-trGSwAAAgFjAAAA","timeZone":"Eastern Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"","timeFormat":"","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{"daysOfWeek":["microsoft.graph.dayOfWeek'monday'","microsoft.graph.dayOfWeek'tuesday'","microsoft.graph.dayOfWeek'wednesday'","microsoft.graph.dayOfWeek'thursday'","microsoft.graph.dayOfWeek'friday'"],"startTime":"1970-01-01T08:00:00Z","endTime":"1970-01-01T17:00:00Z","timeZone":{"name":"Eastern Standard Time"}},"ODataType":"#microsoft.graph.mailboxSettings","puser":"6f995c2b-2dcc-433f-9409-7d847d3935fb","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AAMkAGE0ZWVmYTlhLTI2MDAtNDJmOC05NjRjLTNmMTgyNDM2MDQ3NwAuAAAAAACAHUC988Z9QbixVUMtutstAQDR4t3ZE8_6QpbkP-csYqiqAAAAAAFhAAA=","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{},"ODataType":"#microsoft.graph.mailboxSettings","puser":"820779bc-217e-4370-bb81-4f34a124c072","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AAMkADQ0ODNjYzhhLWQ4ODUtNGRkMy05NGYyLTJmZjQyZTcwMmY3YwAuAAAAAAAOE-T2OkbKR70lxk9KLiM4AQBuhdNMOzPFQKtwhvLEI1MjAAAAAIDSAAA=","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{"daysOfWeek":["microsoft.graph.dayOfWeek'monday'","microsoft.graph.dayOfWeek'tuesday'","microsoft.graph.dayOfWeek'wednesday'","microsoft.graph.dayOfWeek'thursday'","microsoft.graph.dayOfWeek'friday'"],"startTime":"1970-01-01T08:00:00Z","endTime":"1970-01-01T17:00:00Z","timeZone":{"name":"Pacific Standard Time"}},"ODataType":"#microsoft.graph.mailboxSettings","puser":"84129d5d-1ae2-49a2-ba84-7e9a14901bc2","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"archiveFolder":"AQMkADllMGU3NTZhLWU0MjYtNDU5ZC04NzQ1LTUxM2Y0NTI0NzM3MwAuAAADxIFm2RiFrU2EhVfwmeCNMgEA1DBbvNXM6ke6YeAsGY2R0AAAAmxQAAAA","timeZone":"Pacific Standard Time","delegateMeetingMessageDeliveryOptions":"sendToDelegateOnly","dateFormat":"M/d/yyyy","timeFormat":"h:mm tt","automaticRepliesSetting":{"status":"microsoft.graph.automaticRepliesStatus'disabled'","externalAudience":"microsoft.graph.externalAudienceScope'all'","internalReplyMessage":"","externalReplyMessage":"","scheduledStartDateTime":{"dateTime":"2021-03-11T20:00:00.0000000","timeZone":"UTC"},"scheduledEndDateTime":{"dateTime":"2021-03-12T20:00:00.0000000","timeZone":"UTC"}},"language":{"locale":"en-US","displayName":"English (United States)"},"workingHours":{"daysOfWeek":["microsoft.graph.dayOfWeek'monday'","microsoft.graph.dayOfWeek'tuesday'","microsoft.graph.dayOfWeek'wednesday'","microsoft.graph.dayOfWeek'thursday'","microsoft.graph.dayOfWeek'friday'"],"startTime":"1970-01-01T08:00:00Z","endTime":"1970-01-01T17:00:00Z","timeZone":{"name":"Pacific Standard Time"}},"ODataType":"#microsoft.graph.mailboxSettings","puser":"883bfe1c-445d-4848-8db1-b677b16ed4be","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
```