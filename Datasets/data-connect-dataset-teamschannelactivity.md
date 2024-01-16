---
title: "Microsoft Graph Data Connect TeamsChannelActivity_v0 dataset"
description: "Use this dataset to understand an employee’s activity with their channels in Microsoft Teams."
author: "kumarrachit"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# TeamsChannelActivity_v0 dataset

The TeamsChannelActivity_v0 dataset provides a list of Signals which are relevant for understanding an employee’s activity with their channels in Microsoft Teams. It can help get insights on how and how much the employees in the organization interact in Microsoft Teams Channels. For example, activities such as PostChannelMessage can help understand how active the channel is, or LeaveChannel can help understand if the discussion on the channel is useful or spam for most. Patterns of communication often change when on-site and in hybrid settings, leading to spikes or drops in activity.  It includes the following signals: 'AddToChannel', 'CreateChannel', 'DeleteChannel', 'JoinChannel', 'LeaveChannel', 'PostChannelMessage', 'RemoveFromChannel', and 'RenameChannel'.  

NOTE:

- Currently, the MGDC platform ONLY supports extracting valid users. Trying to extract invalid users will result in no data to be returned for such users. Valid users must have the following:
    * A valid mailbox (i.e. users having mailbox that is **not in inactive state**, **not soft-deleted** or **not hosted on-Prem**)
    * User account enabled (i.e. accountEnabled should not be set to false)
    * An exchange license
- The MGDC platform supports extraction of data for all valid users matching with the ADF pipeline's region. Hence, if the users' mailbox are residing in different regions, then multiple pipelines will need to be triggered in the respective ADF regions.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze Microsoft Teams Channels created or deleted by users in a particular time range 

## Questions

The following are examples of questions that you can answer with this dataset:

- How many channels were interacted with by users during a time range? 
- What is the average number of people leaving or joining a channel? 
- How often are the same channels used for different purposes (i.e., the channel is renamed)? 

## Joining with other datasets

- The TeamsChannelActivity_v0 dataset can be joined with other activities datasets, to get insights from how employees in the organization interact with and through outlook and teams 
- It can also be used with TeamsChannelDetails_v0 to get more information regarding the channels queried here.

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
| ThreadName | string | The Channel’s Name. Among the properties defined by the signal producer. | No | None |                
| PriorThreadName | string | Older Channel’s Name (in case the signal was for renaming the channel). Among the properties defined by the signal producer. | No |  None |               
| TeamGroupId | string | Unique GroupId of the teams channel. Among the properties defined by the signal producer. | No | None |                
| TeamName | string | The team name owning the Channel. Among the properties defined by the signal producer. | No | None |             
| MembersAdded | array | A list of members added (in case the signal was AddToChannel or Join Channel). Among the properties defined by the signal producer. | No | None |               
| MembersRemoved | array | A list of members removed (in case the signal was RemoveFromChannel or LeaveChannel). Among the properties defined by the signal producer. | No | None |        
| ThreadMembers | array | A list of members in the channel (in case the signal was RenameChannel). Among the properties defined by the signal producer. | No | None |
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
"ThreadName": "string", 
"PriorThreadName": "string", 
"TeamGroupId": "string", 
"TeamName": "string", 
"MembersAdded": ["string"], 
"MembersRemoved": ["string"], 
"ThreadMembers": ["string"], 
"puser": "string (identifier)", 
"ptenant": "string (identifier)"
}
```

## Sample

```json
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-05-04T11:55:05Z","TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","TeamName":"TestTeamMgdcIDC","ThreadName":"Test Channel IDC","EndTime":"2023-05-04T11:54:58Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAAQm6lxUAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"PostChannelMessage","StartTime":"2023-05-04T11:54:58Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-05-04T11:55:05Z","TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","TeamName":"TestTeamMgdcIDC","ThreadName":"Test Channel IDC","EndTime":"2023-05-04T11:54:58Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAAQm6lxUAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","PriorThreadName":"TestChannel","ThreadMembers":[{"MemberId":"7bf2eacd-2037-43fa-8453-1547a8f81a24"}],"SignalType":"RenameChannel","StartTime":"2023-05-04T11:54:58Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-05-04T12:20:38Z","TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","TeamName":"TestTeamMgdcIDC","ThreadName":"Test Channel IDC","EndTime":"2023-05-04T12:20:37Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAAQm6lxbAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"PostChannelMessage","StartTime":"2023-05-04T12:20:37Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-07-05T09:26:40Z","TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","TeamName":"TestTeamMgdcIDC","ThreadName":"General","EndTime":"2023-07-05T09:26:40Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARQA2tYAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"PostChannelMessage","StartTime":"2023-07-05T09:26:40Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-05-04T12:26:01Z","TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","TeamName":"TestTeamMgdcIDC","ThreadName":"Test Channel IDC","EndTime":"2023-05-04T12:26:00Z","Id":"AAMkADFjYzNiNzcyLWIxNWYtNDQ4YS05MjAzLTU3NjBjN2ZlMWZiYQBGAAAAAADcOaVTrgWQTL7XPjfiGreRBwCHF_h-35jESpfQ5Ec6ha4pAAAAAAEuAACHF_h-35jESpfQ5Ec6ha4pAAAbcTlzAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"PostChannelMessage","StartTime":"2023-05-04T12:26:00Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"84f6a146-aa67-48d6-b015-785762c6d7c5"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-06-27T06:57:02Z","MembersAdded":[{"MemberId":"7bf2eacd-2037-43fa-8453-1547a8f81a24"}],"TeamGroupId":"943ecd15-a954-40a7-9d00-3224d21dc470","TeamName":"","ThreadName":"channel4public","EndTime":"2023-06-27T06:57:02Z","Id":"AAMkAGY0NDE1MGEzLWZmN2ItNGY1MC1hMjJlLWM3MjEwODQ3ZjA2MwBGAAAAAABSt04ZAKCLTogyMCRWtPw7BwBm59ZvDm5GRolPAkzaWd0HAAAAAAEqAABm59ZvDm5GRolPAkzaWd0HAARHmVtGAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Channel'","SignalType":"AddToChannel","StartTime":"2023-06-27T06:57:02Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"7bf2eacd-2037-43fa-8453-1547a8f81a24"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-05-04T11:55:05Z","TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","TeamName":"TestTeamMgdcIDC","ThreadName":"Test Channel IDC","EndTime":"2023-05-04T11:54:58Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAAQm6lxUAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","MembersRemoved":[{"MemberId":"7bf2eacd-2037-43fa-8453-1547a8f81a24"}],"SignalType":"RemoveFromChannel","StartTime":"2023-05-04T11:54:58Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
```
