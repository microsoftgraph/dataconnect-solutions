---
title: "BasicDataSet_v0.SharePointGroups_v1"
description: "Contains SharePoint group information, including details about group members."
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.SharePointGroups_v1 dataset contains SharePoint group information, including details about group members.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| ptenant | string |Id of the tenant| 72f988bf-86f1-41af-91ab-2d7cd011db47 |0|false|
| SiteId | string |Id of the site where the group resides| 355f5fec-e502-4fa0-9218-c0e9ec019491 |0|false|
| GroupId | int64 |id of the group, unique within SPSite|3|0|false|
| GroupLinkId | int64 |Id of the sharing link associated with this group, if it was created for a sharing link. The id is all zeros if the group is not related to a sharing link|0a87ef23-a542-42ba-97fd-1e66fd26c1e1|0|false|
| GroupType | string |Type: SharePointGroup| SharePointGroup |0|false|
| DisplayName | string |Name of the group| Viewers |0|false|
| Description | string |Description of the group| Members of this group can view pages, list items, and documents. |0|false|
| Owner | string |Group owner. Format: ```STRUCT<`AadObjectId`:STRING,`Name`:STRING,`Email`:STRING>```|`{\"AadObjectId\": \"12345676-6e0e-46ab-855d-2c8912345676\",\"Name\": \"John Smith\",\"Email\": \"jsmith@contoso.com\"}`|0|false|
| Members | string |Members of the group. Format: ```ARRAY<STRUCT<`Type`:STRING, `AadObjectId`:STRING, `Name`:STRING, `Email`:STRING>>```|`[{\"Type\": \"User\", \"AadObjectId\": \"12345676-6e0e-46ab-855d-2c8912345676\", \"Name\": \"John Smith\", \"Email\": \"jsmith@contoso.com\"}]`|0|false|
| SnapshotDate | datetime |Date this data set was generated|`2022-03-16T00:00:00Z`|1|true|
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted'|

## Notes

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