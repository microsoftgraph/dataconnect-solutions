---
title: "Microsoft Graph Data Connect OutlookMeetingActivity_v0 dataset"
description: "Use this dataset to understand an employee’s activity with their meetings in Microsoft Outlooks."
author: "kumarrachit"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# OutlookMeetingActivity_v0 dataset

The OutlookMeetingActivity_v0 dataset provides a list of Signals which are relevant for understanding an employee’s activities with their meetings in Microsoft Outlook. It can help get insights on how and how much the employees in the organization interact through Outlook meetings.  Patterns of communication often change when on-site and in hybrid settings, leading to spikes or drops in activity. Insights from scheduling of Outlook meetings before and after shifts in remoteness along with acceptance and cancellation rates inform if the communication patterns have changed and what the mobility profiles of employees are. It includes the following signals: 'AcceptCalendarEvent', 'AcceptedMeetingRequest', 'CancelCalendarEvent', 'CancelMeeting', 'CreateCalendarItem', 'DeclineCalendarItem', 'DeclinedMeetingRequest', 'DeleteCalendarItem', 'DeleteCalendarEvent', 'ForwardCalendarItem', 'ForwardCalendarEvent', 'Reply', 'ReplyAll', 'ReplyWithMeeting', 'SendMeetingRequest', 'Flag', 'FlagCleared', 'FlagComplete', 'Forward', 'ReceiveCalendarItem', 'TentativeAcceptedCalendarItem', 'TentativelyAcceptCalendarEvent' and 'TentativeMeetingRequest'. 

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze patterns of communication of employees 
- There can be an Outlook meeting effectiveness dashboard based on these signals.

## Questions

The following are examples of questions that you can answer with this dataset:

- How many meetings were interacted with by users during a time range? 
- What is the relative number of meetings compared to last month?  
- How many meeting invites were accepted/declined etc?  
- How often do employees attend meetings? 

## Joining with other datasets

The OutlookMeetingActivity_v0 dataset can be joined with other activities datasets, to get insights from how employees in the organization interact through outlook and teams. 

## Definitions

- **Signal:**  A Substrate signal is a high-value event that captures a user's behavior. A signal can be either active (like joining a meeting) or passive (like a location change).


## Schema

| Name  | Type  |  Description  |  FilterOptions  |  FilterType  |
| ----------- | ----------- | ----------- | ----------- | ----------- |
| SignalType | string | Name of the signal. | No | None |
| Id | string | The unique identifier of the Signal. | No | None |
| StartTime | datetime | The date and time when action happens. | No | None |
| CreationTime | datetime | The date and time when the signal was created. | Yes | Date |
| EndTime | datetime | The date and time when action ends. If a signal is a point-in-time action, it is same as StartTime. If the action lasts a time period, this is the action end time captured on client. | No | None|
| ItemType | string | The type of item being acted on. | No | None |
| AppName | string | The end user client name being used to generate the signal, E.g. "Unknown", "OutlookMobile", "OWA", "OutlookDesktop", etc. | No | None |
| AadTenantId | string | AadTenantId of the actor (signal generator). | No | None |
| ClientSessionId | string | Unique session id for the user, among the properties defined by the signal producer. | No | None |
| MailboxGuid | string | Unique mailbox id for the user, among the|properties defined by the signal producer. | No | None |
| TenantName | string | The tenant name, among the properties defined by the signal producer. | No | None|
| OId | string | UserId, among the properties defined by the signal producer. | No | None |
| puser | String | User id. | No  | None |
| ptenant | String |  Tenant id. | No | None |

## JSON representation

```json
{
"SignalType": "string", 
"Id": "string", 
"StartTime": "datetime", 
"CreationTime": "datetime", 
"EndTime": "datetime", 
"ItemType": "string", 
"AppName": "string", 
"AadTenantId": "string", 
"ClientSessionId": "string",  
"MailboxGuid": "string",  
"TenantName": "string",  
"OId": "string", 
"puser": "String (identifier)", 
"ptenant": "String (identifier)"
}
```

## Sample

```json
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Exchange","CreationTime":"2023-07-05T09:26:48Z","ClientSessionId":"5361c1fa-5a77-4ec0-9498-8f572b60fcae","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-07-05T09:26:48Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARQA2tZAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"AcceptedMeetingRequest","StartTime":"2023-07-05T09:26:48Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-06-26T06:59:19Z","ClientSessionId":"0deb8e0a-8751-474f-83d1-56b0a1aaff1e","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-26T06:59:19Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARJYjF4AAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-26T06:59:19Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-06-26T06:59:58Z","ClientSessionId":"32927f8d-877d-4748-9bad-6db02946bb9e","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-26T06:59:58Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARJYjF7AAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-26T06:59:58Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-06-26T07:00:40Z","ClientSessionId":"32927f8d-877d-4748-9bad-6db02946bb9e","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-26T07:00:39Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARJYjF_AAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-26T07:00:39Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-06-26T07:01:26Z","ClientSessionId":"92ca96fe-2325-4ffe-9f6e-e5acfd7a60a6","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-26T07:01:26Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARJYjGBAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-26T07:01:26Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-06-26T09:48:21Z","ClientSessionId":"43477fe7-9700-459e-8323-e0809b48e62d","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-26T09:48:21Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARJYjGEAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-26T09:48:21Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-06-26T09:48:23Z","ClientSessionId":"43477fe7-9700-459e-8323-e0809b48e62d","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-26T09:48:23Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARJYjGHAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-26T09:48:23Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-06-27T05:48:10Z","ClientSessionId":"e1e72e9d-269b-4533-8988-999c20b0ee2d","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-27T05:48:10Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARKXHv7AAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-27T05:48:10Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-06-27T05:48:35Z","ClientSessionId":"c63534d8-02ea-4735-8898-f23dbd575f54","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-27T05:48:35Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARKXHv_AAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-27T05:48:35Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-06-27T06:53:06Z","ClientSessionId":"c24cb9a9-6bae-4eaa-bdd6-63edbd604bf9","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-06-27T06:53:06Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARKXHwBAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Calendar'","SignalType":"SendMeetingRequest","StartTime":"2023-06-27T06:53:06Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
```
