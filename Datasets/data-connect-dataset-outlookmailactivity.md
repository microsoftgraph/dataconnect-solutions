---
title: "Microsoft Graph Data Connect OutlookMailActivity_v0 dataset"
description: "Use this dataset to understand an employee’s activity with their mails in Microsoft Outlooks."
author: "kumarrachit"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# OutlookMailActivity_v0 dataset

The OutlookMailActivity_v0 dataset provides a list of Signals which are relevant for understanding an employee’s activity with their mails in Microsoft Outlook. It can help get insights on how and how much the employees in the organization interact through Outlook’s emails. Patterns of communication often change when on-site and in hybrid settings, leading to spikes or drops in activity. It includes the following signals: 'Reply', 'ReplyAll', 'MarkAsNotSpam', 'MarkAsOther', 'MarkAsPhishing', 'MarkAsUnread', 'MarkMessageAsRead', 'MessageSent', 'Flag', 'FlagCleared', 'FlagComplete', 'Forward', 'SendMessageToUnifiedGroup', 'ViewMessage', 'MarkAsFocused', 'MarkAsJunk', 'DeleteFolder', 'MoveFolder', 'JoinGroup' and 'LeaveGroup'. 

NOTE:

- Currently, the MGDC platform ONLY supports extracting valid users. Trying to extract invalid users will result in no data to be returned for such users. Valid users must have the following:
    * A valid mailbox (i.e. users having mailbox that is **not in inactive state**, **not soft-deleted** or **not hosted on-Prem**)
    * User account enabled (i.e. accountEnabled should not be set to false)
    * An exchange license
- The MGDC platform supports extraction of data for all valid users matching with the ADF pipeline's region. Hence, if the users' mailbox are residing in different regions, then multiple pipelines will need to be triggered in the respective ADF regions.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze the interaction of employees with Outlook Emails 
- There can be an email effectiveness dashboard based on user engagement 

## Questions

The following are examples of questions that you can answer with this dataset:

- How many emails were interacted with by users during a time range? 
- What is the relative email engagement compared to last month?  
- How many emails were marked as spam/focused etc?  
- How often do employees reply to emails? 

## Joining with other datasets

The OutlookMailActivity_v0 dataset can be joined with other activities datasets, to get insights from how employees in the organization interact with and through outlook and teams. 

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
| MailboxGuid | string | Unique mailbox id for the user, among the properties defined by the signal producer. | No | None |
| TenantName | string | The tenant name, among the properties defined by the signal producer. | No | None|
| OId | string | UserId, among the properties defined by the signal producer. | No | None |
| puser | string | User id. | No  | None |
| ptenant | string |  Tenant id. | No | None |

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
"puser": "string (identifier)", 
"ptenant": "string (identifier)"
}
```

## Sample

```json
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:05:48Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:05:48Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTDAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:05:21Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:06:05Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:06:04Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTEAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:06:04Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:06:10Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:06:09Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTFAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:06:04Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:09:14Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:09:13Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTHAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:06:11Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:11:28Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:11:27Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTIAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:11:22Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:11:30Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:11:30Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTJAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:11:27Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:17:00Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:16:59Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTKAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:16:59Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:17:06Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:17:05Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTLAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:17:05Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"OWA","CreationTime":"2023-07-17T18:17:11Z","ClientSessionId":"","MailboxGuid":"","OId":"","TenantName":"","EndTime":"2023-07-17T18:17:10Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTMAAA=","ItemType":"Message","SignalType":"ViewMessage","StartTime":"2023-07-17T18:17:06Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Web","CreationTime":"2023-07-17T18:05:48Z","ClientSessionId":"dc30791f-caf5-4220-aaee-284f22505c24","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-07-17T18:05:48Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTCAAA=","ItemType":"Message","SignalType":"MarkMessageAsRead","StartTime":"2023-07-17T18:05:48Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
```
