---
title: "Microsoft Graph Data Connect TeamsConversationActivity_v0 dataset"
description: "Use this dataset to understand an employee’s activity with their contacts in Microsoft Outlooks."
author: "kumarrachit"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# TeamsConversationActivity_v0 dataset

The TeamsConversationActivity_v0 dataset provides a list of Signals which are relevant for understanding an employee’s activity with their teams and chats in Microsoft Teams. It can help get insights on how and how much the employees in the organization interact through Microsoft Teams. Patterns of communication often change when on-site and in hybrid settings, leading to spikes or drops in activity It includes the following signals: 'AddToGroupChat', 'AddToTeam', 'CreateGroupChat', 'CreateTeam', 'InstantMessage', 'JoinMeeting', 'JoinTeam', 'LeaveGroupChat', 'LeaveTeam', 'ReactedWithEmoji', 'ReceiveCall', 'RemoveFromGroupChat', 'RemoveFromTeam', 'RenameGroupChat', 'RenameTeam', 'ReplyChannelMessage', and 'StartCall'. 

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze patterns of communication of employees 
- Analyze collaboration hours for employees on Microsoft teams 
- There can be teams chat/meeting effectiveness dashboard based on these signals 

## Questions

The following are examples of questions that you can answer with this dataset:

- How many conversations were started by users during a time range? 
- What is the average number of people leaving or joining a team or a group chat? 
- How often are the same conversations used for different purposes (i.e., the conversation is renamed)? 
- How often does a group call happen, and who starts the most calls, or who joins how many calls in a group? 

## Joining with other datasets

The TeamsConversationActivity_v0 dataset can be joined with other activities datasets, to get insights from how employees in the organization interact through outlook and teams. 

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
| ThreadType | string | The type of thread where the conversation is taking place. Among the properties defined by the signal producer. | No |  None |   
| User_TenantRole | string | Type of user in the tenant (Regular/Admin). Among the properties defined by the signal producer. | No |  None |
| ThreadName | string | The Channel’s Name. Among the properties defined by the signal producer. | No | None |                    
| Team_Id | string | Team id, can be chat(for teams chat) or a skype thread id. Among the properties defined by the signal producer. | No |  None |            
| TeamGroupId | string | Unique GroupId of the teams channel. Among the properties defined by the signal producer. | No | None |                
| TeamName | string | The team name owning the Channel. Among the properties defined by the signal producer. | No | None |                
| Emotion | string | Emotion in case the signal is ReactedWithEmoji. Among the properties defined by the signal producer. | No |  None |
| MembersAdded | string | A list of members added (in case the signal was AddToChannel or Join Channel). Among the properties defined by the signal producer. | No | None |               
| MembersRemoved | string | A list of members removed (in case the signal was RemoveFromChannel or LeaveChannel). Among the properties defined by the signal producer. | No | None |        
| ThreadMembers | string | A list of members in the channel (in case the signal was RenameChannel). Among the properties defined by the signal producer. | No | None |        
| Mentions | string | Mentions in a conversation. Among the properties defined by the signal producer. | No | None |             
| InteractionMetadata | string | The interaction metadata in a teams conversation. Among the properties defined by the signal producer. | No | None |


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
"ThreadType": "string", 
"User_TenantRole": "string", 
"ThreadName": "string", 
"Team_Id": "string", 
"TeamGroupId": "string", 
"TeamName": "string", 
"Emotion": "string", 
"MembersAdded": "string", 
"MembersRemoved": "string", 
"ThreadMembers": "string", 
"Mentions": "string", 
"InteractionMetadata": "string", 
"puser": "String (identifier)", 
"ptenant": "String (identifier)"
}
```

## Sample

```json
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-07-12T17:29:25Z","InteractionMetadata":{"InteractionInitiatorEmail":"eucliduser21@euclidtest21.onmicrosoft.com","InteractionInitiatorName":"FirstName LastName","InteractionSubType":"OneToOneChat","InteractionTargetEmails":["Beth.Byers@euclidtest21.onmicrosoft.com"],"InteractionTargetNames":["Beth Byers"],"InteractionType":"InstantMessage","IsTruncated":false},"TeamGroupId":"","ThreadName":"","ThreadType":"","User_TenantRole":"","EndTime":"2023-07-12T17:29:23Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARU9cqNAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"InstantMessage","StartTime":"2023-07-12T17:29:23Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-07-12T17:31:12Z","InteractionMetadata":{"InteractionInitiatorEmail":"eucliduser21@euclidtest21.onmicrosoft.com","InteractionInitiatorName":"FirstName LastName","InteractionSubType":"GroupChat","InteractionTargetEmails":["Beth.Byers@euclidtest21.onmicrosoft.com","Winnie.Davidson@euclidtest21.onmicrosoft.com"],"InteractionTargetNames":["Beth Byers","Winnie Davidson"],"InteractionType":"InstantMessage","IsTruncated":false},"TeamGroupId":"","ThreadName":"","ThreadType":"","User_TenantRole":"","EndTime":"2023-07-12T17:31:08Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARU9cqUAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"InstantMessage","StartTime":"2023-07-12T17:31:08Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-07-12T17:35:35Z","InteractionMetadata":{"InteractionInitiatorEmail":"eucliduser21@euclidtest21.onmicrosoft.com","InteractionInitiatorName":"FirstName LastName","InteractionSubType":"OneToOneChat","InteractionTargetEmails":["Beth.Byers@euclidtest21.onmicrosoft.com"],"InteractionTargetNames":["Beth Byers"],"InteractionType":"InstantMessage","IsTruncated":false},"TeamGroupId":"","ThreadName":"","ThreadType":"","User_TenantRole":"","EndTime":"2023-07-12T17:35:33Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARU9cqaAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"InstantMessage","StartTime":"2023-07-12T17:35:33Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-07-12T17:37:16Z","InteractionMetadata":{"InteractionInitiatorEmail":"eucliduser21@euclidtest21.onmicrosoft.com","InteractionInitiatorName":"FirstName LastName","InteractionSubType":"OneToOneChat","InteractionTargetEmails":["Winnie.Davidson@euclidtest21.onmicrosoft.com"],"InteractionTargetNames":["Winnie Davidson"],"InteractionType":"InstantMessage","IsTruncated":false},"TeamGroupId":"","ThreadName":"","ThreadType":"","User_TenantRole":"","EndTime":"2023-07-12T17:37:14Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARU9cqgAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"InstantMessage","StartTime":"2023-07-12T17:37:14Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-07-12T17:30:24Z","TeamGroupId":"","ThreadName":"","ThreadType":"OneOnOneChat","User_TenantRole":"Admin","EndTime":"2023-07-12T17:29:39Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARU9cqPAAA=","ItemType":"","SignalType":"StartCall","StartTime":"2023-07-12T17:29:39Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-05-04T12:20:47Z","TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","ThreadName":"Test Channel IDC","ThreadType":"","User_TenantRole":"","EndTime":"2023-05-04T12:20:46Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAAQm6lxcAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"ReplyChannelMessage","StartTime":"2023-05-04T12:20:46Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-07-12T17:29:21Z","TeamGroupId":"","ThreadMembers":[{"MemberId":"e530bf91-e844-4369-a808-e0d12b1008cd","Name":"FirstName LastName"},{"MemberId":"fdfabbfe-daa3-466d-995d-71e7d0931a49","Name":"Beth Byers"}],"ThreadName":"","ThreadType":"","User_TenantRole":"","EndTime":"2023-07-12T17:29:21Z","Id":"AAMkADJhOTdkOGViLWZjYzUtNDY0NS1hNGFkLWQxNjM0OTBiMmVkYgBGAAAAAABfknVfDfJURqxOuHBzEhFGBwCa8HKSWYdiSZsHkjRYM1qIAAAAAAEwAACa8HKSWYdiSZsHkjRYM1qIAARU9cqMAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'InstantMessageConversation'","SignalType":"CreateGroupChat","StartTime":"2023-07-12T17:29:21Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"e530bf91-e844-4369-a808-e0d12b1008cd"} 
{"AadTenantId":"8e56195d-f07c-44f0-8108-40e4352e3e74","AppName":"Teams","CreationTime":"2023-05-04T12:25:24Z","Mentions":[{"DisplayName":"FirstName","ObjectId":"e530bf91-e844-4369-a808-e0d12b1008cd"}],"TeamGroupId":"afe7d584-21bf-4ba0-8c7f-b775777e8b05","ThreadName":"Test Channel IDC","ThreadType":"","User_TenantRole":"","EndTime":"2023-05-04T12:25:23Z","Id":"AAMkADFjYzNiNzcyLWIxNWYtNDQ4YS05MjAzLTU3NjBjN2ZlMWZiYQBGAAAAAADcOaVTrgWQTL7XPjfiGreRBwCHF_h-35jESpfQ5Ec6ha4pAAAAAAEuAACHF_h-35jESpfQ5Ec6ha4pAAAbcTlxAAA=","ItemType":"Microsoft.OutlookServices.SignalItemType'Message'","SignalType":"ReplyChannelMessage","StartTime":"2023-05-04T12:25:23Z","ptenant":"8e56195d-f07c-44f0-8108-40e4352e3e74","puser":"84f6a146-aa67-48d6-b015-785762c6d7c5"} 
```
