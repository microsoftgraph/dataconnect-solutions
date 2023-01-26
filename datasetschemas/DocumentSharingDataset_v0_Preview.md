---
title: "DocumentSharingDataset_v0_Preview "
description: "SharePoint sharing information, showing what is being shared and who are the sharing recipients (including internal users, external users, security groups and SharePoint groups)."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

> [!NOTE]
> The SharePoint Sharing dataset is currently in Preview. To enable this dataset, you need to join the Graph TAP program using the signup form at [https://aka.ms/GraphTAPForm](https://aka.ms/GraphTAPForm) and request to join the Preview.

# Overview

SharePoint sharing information, showing what is being shared and who are the sharing recipients (including internal users, external users, security groups and SharePoint groups).

## Properties

| Name | Type | Description |
|--|--|--|
| ptenant | String | GUID that identifies the Office 365 tenant in AAD |
| SiteId | String | GUID that identifies the SharePoint site (site collection) |
| WebId | String | GUID that identifies the SharePoint web (subsite) |
| ItemType | String | The type of item being shared (Site, Folder, File) |
| ItemURL | String | URL to the item being shared |
| FileExtension | String | File extension of the item being shared (optional, shows only if item type is file) |
| RoleDefinition | String | Sharing role (Read, Contribute, Full Control) |
| LinkId | String | GUID for the share Link (optional, won’t show if share has no link) |
| ScopeId | String | GUID that identifies the SharePoint scope |
| LinkScope | String | Scope of sharing link (specific people, anyone)(optional, won’t show if share has no link) |
| SharedWithCount | Object\[\] | Object array with one entry for every type of sharing recipient |
| SharedWithCount, Type | String | Type of sharing recipient (Internal, External, SecurityGroup, SharePoint Group) |
| SharedWithCount, Count | Number | Number of sharing recipients of this type |
| SharedWith | Object\[\] | Object array with one entry for every sharing recipient |
| SharedWith, Type | String | Type of sharing recipient (Internal, External, SecurityGroup, SharePoint Group) |
| SharedWith, Name | String | Name of sharing recipient |
| SharedWith, EmailAddress | String | Email of sharing recipient (optional, won’t show for SharePoint groups or special security groups) |
| SnapshotDate | Date | Data this data set was collected, in UTC |
| ShareCreatedBy | String | Gives information about the user/group that created the share. Format: <STRUCT<`Type`:STRING, `Name`:STRING, `Email`:STRING>|
| ShareCreatedTime | Date | The date and time when the share link was created |
| ShareLastModifiedBy | String | Gives information about the user/group that last modified the share. Format: <STRUCT<`Type`:STRING, `Name`:STRING, `Email`:STRING> |
| ShareLastModifiedTime | Date | The date and time when the share was last modified |
| ShareExpirationTime | Date | The date and time when the share link could expires |
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted'|

## Notes

- Sharing recipient is person or group with whom a resource is being shared.
- In a later release, we will offer a “Sites” data set which can be joined to this “Sharing” data set by Site Id. In the meantime, ItemURL is offered to help understand where the item is being shared.  
- The “Shared With Count” property is useful if you decide to exclude the “Shared With” column from the data set for privacy reasons.
- The "Operation" property with the Full or Delta Mode can be utilized in the examples below
    1. To get a full snapshot, please ensure that start and end date are the SAME date
        - start: 1/1/2023
        - end: 1/1/2023
        - user recieves: One full snapshot of data from requested time period of 1/1/2023
    2. To get rows of a snapshot with only additions or deletions (delta mode), ensure start and data ranges are different in chronological order
        - start: 1/1/2023
        - end: 1/3/2023
        - user recieves: either rows added or deleted from this time period. If no rows are returned, there were no changes in data during this time period.