---
title: "Microsoft Graph Data Connect OutlookContactActivity_v0 dataset"
description: "Use this dataset to understand an employee’s activity with their contacts in Microsoft Outlooks."
author: "kumarrachit"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# OutlookContactActivity_v0 dataset

The OutlookContactActivity_v0 dataset provides a list of Signals which are relevant for understanding an employee’s activities with their contacts in Microsoft Outlook. It can help get insights on how employees in the organization interact through Outlook’s contacts. Patterns of communication often change when on-site and in hybrid settings, leading to spikes or drops in activity. For example, "AddContact” can help understand how often an employee must interact outside their team/circle, leading to insights in new cross-team communications and how they can be reduced to increase efficiency. It includes the following signals: 'AddContact', 'ContactCreated', 'ContactUpdated' and 'RemoveContact'.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- There can be an employee engagement/interaction dashboard based on these signals. 

## Questions

The following are examples of questions that you can answer with this dataset:

- How many contacts were added by all users during a time range? 
- How many contacts were created by all users during a time range? 
- What is the frequency of adding new contacts for different employees? 
- How many contacts were deleted by all users during a time range? 
- How many contacts were modified by all users during a time range? 

## Joining with other datasets

The OutlookContactActivity_v0 dataset can be joined with other activities datasets, to get insights from how employees in the organization interact through outlook and teams. 

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
"puser": "String (identifier)", 
"ptenant": "String (identifier)"
}
```

## Sample

```json
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-07-17T18:18:03Z","ClientSessionId":"6e3e17f0-c800-44aa-a78f-3b0fe89795ff","ItemType":"Message","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-07-17T18:18:03Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTNAAA=","SignalType":"ContactCreated","StartTime":"2023-07-17T18:18:03Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-07-17T18:18:40Z","ClientSessionId":"9bfd424b-1373-42b0-a9cc-a28efca77fc6","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-07-17T18:18:40Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTPAAA=","SignalType":"ContactCreated","StartTime":"2023-07-17T18:18:40Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-07-17T18:18:04Z","ClientSessionId":"6e3e17f0-c800-44aa-a78f-3b0fe89795ff","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-07-17T18:18:04Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTOAAA=","SignalType":"ContactUpdated","StartTime":"2023-07-17T18:18:04Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-07-17T18:18:41Z","ClientSessionId":"6e3e17f0-c800-44aa-a78f-3b0fe89795ff","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-07-17T18:18:41Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVWTQAAA=","SignalType":"ContactUpdated","StartTime":"2023-07-17T18:18:41Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Other","CreationTime":"2023-07-17T18:18:42Z","ClientSessionId":"6e3e17f0-c800-44aa-a78f-3b0fe89795ff","MailboxGuid":"2a97d8eb-fcc5-4645-a4ad-d163490b2edb","OId":"e530bf91-e844-4369-a808-e0d12b1008cd","TenantName":"euclidtest21.onmicrosoft.com","EndTime":"2023-07-17T18:18:41Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARYVYTQAAA=","SignalType":"ContactUpdated","StartTime":"2023-07-17T18:18:42Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
```
