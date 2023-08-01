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


| Name | Type | Description  | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| ptenant | String | GUID that identifies the Office 365 tenant in AAD |4b4741c0-4d67-4c21-842a-abcea48840d5|0|false|
| SiteId | String | GUID that identifies the SharePoint site (site collection) |ec508b4b-6e54-401c-826f-775950f03353|0|false|
| WebId | String | GUID that identifies the SharePoint web (subsite) |efe47d8c-6672-420b-80f9-fb884195bcbe|0|false|
| ItemType | String | The type of item being shared (Site, Folder, File) | File|0|false|
| ItemURL | String | URL to the item being shared |personal/johnd_contoso_onmicrosoft_com/Documents/SampleFolder/test.tsv|0|false|
| FileExtension | String | File extension of the item being shared (optional, shows only if item type is file) |tsv|0|false|
| RoleDefinition | String | Sharing role (Read, Contribute, Full Control) |Read|0|false|
| LinkId | String | GUID for the share Link (optional, won’t show if share has no link) |e2acbe31-487d-460e-886c-366039ede3a8|0|false|
| ScopeId | String | GUID that identifies the SharePoint scope |793e2f2d-cdf5-406a-91fc-d32b9ed9c2f1|0|false|
| LinkScope | String | Scope of sharing link (specific people, anyone)(optional, won’t show if share has no link) |Anyone|0|false|
| SharedWithCount | Object\[\] | Object array with one entry for every type of sharing recipient |[{"Type":"SharePointGroup","Count":1}]|0|false|
| SharedWithCount, Type | String | Type of sharing recipient (Internal, External, SecurityGroup, SharePoint Group) |SharePointGroup|0|false|
| SharedWithCount, Count | Number | Number of sharing recipients of this type |1|0|false|
| SharedWith | Object\[\] | Object array with one entry for every sharing recipient |[{"Type":"SharePointGroup","Name":"ContosoTestingMembers"}]|0|false|
| SharedWith, Type | String | Type of sharing recipient (Internal, External, SecurityGroup, SharePoint Group) |SharePointGroup|0|false|
| SharedWith, Name | String | Name of sharing recipient |ContosoTestingMembers|0|false|
| SharedWith, EmailAddress | String | Email of sharing recipient (optional, won’t show for SharePoint groups or special security groups) |ContosoTestingMembers@domain.com|0|false|
| SnapshotDate | Date | Data this data set was collected, in UTC |2022-03-16T00:00:00Z|1|true|
| ShareCreatedBy | String | The user or group that created the sharing link. Format: <Struct <Type: String, Name: String, Email: String>> |{"Type": "User","Name": "John Smith","Email": "jsmith@contoso.com"}|0|false|
| ShareCreatedTime | Date | The date and time when the share link was created |2022-03-16T00:00:00Z|0|false|
| ShareLastModifiedBy | String | The user or group that last modified the sharing link. Format: <Struct <Type: String, Name: String, Email: String>> |{"Type": "User","Name": "John Smith","Email": "jsmith@contoso.com"}|0|false|
| ShareLastModifiedTime | Date | The date and time when the share was last modified |2022-03-16T00:00:00Z|0|false|
| ShareExpirationTime | Date | The date and time when the share link will expire |2022-03-16T00:00:00Z|0|false|
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted')|Created|0|false|

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
  