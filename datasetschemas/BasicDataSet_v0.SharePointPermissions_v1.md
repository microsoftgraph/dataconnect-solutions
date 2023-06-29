---
title: "BasicDataSet_v0.SharePointPermissions_v1"
description: "SharePoint sharing information, showing what is being shared and who are the sharing recipients (including internal users, external users, security groups and SharePoint groups)."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.SharePointPermissions_v1 dataset contains SharePoint permissions information, showing what is being shared and who are the sharing recipients (including internal users, external users, security groups and SharePoint groups).

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
| ShareCreatedBy | String | The user or group that created the sharing link. Format: <Struct <Type: String, Name: String, Email: String>> |
| ShareCreatedTime | Date | The date and time when the share link was created |
| ShareLastModifiedBy | String | The user or group that last modified the sharing link. Format: <Struct <Type: String, Name: String, Email: String>> |
| ShareLastModifiedTime | Date | The date and time when the share was last modified |
| ShareExpirationTime | Date | The date and time when the share link will expire |
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted')|

## Notes

- SharedWith fields refer to a person or group with whom a resource is being shared with.
- In a later release, we will offer a “Sites” data set which can be joined to this “Sharing” data set by Site Id. In the meantime, ItemURL is offered to help understand where the item is being shared.  
- The “Shared With Count” property is useful if you decide to exclude the “Shared With” column from the data set for privacy reasons.
- The "Operation" property is related to the Full or Delta Mode. Please refer to the examples below.   
    1. To get a full snapshot, please ensure that start and end date are the SAME date       
        - Start date: 1/1/2023       
        - End date: 1/1/2023       
        - User receives one full snapshot of data for that day (1/1/2023).       
        - For all objects, the Operation will be “Full”    

    2. To get a Delta snapshot, with only the objects that were created/updated/delete. Please ensure the start date and end date are different and the start date is before the end date.       
        - Start date: 1/1/2023       
        - End date: 1/3/2023       
        - User receives objects that were created, updated or deleted in this time period. If no rows are returned, there were no changes during this time period.        
        - For each object, the Operation will be “Created”, “Updated” or “Deleted”.
  