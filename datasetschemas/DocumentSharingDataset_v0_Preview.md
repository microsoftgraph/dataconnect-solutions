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

## Notes

- Sharing recipient is person or group with whom a resource is being shared.
- In a later release, we will offer a “Sites” data set which can be joined to this “Sharing” data set by Site Id. In the meantime, ItemURL is offered to help understand where the item is being shared.  
- The “Shared With Count” property is useful if you decide to exclude the “Shared With” column from the data set for privacy reasons.
