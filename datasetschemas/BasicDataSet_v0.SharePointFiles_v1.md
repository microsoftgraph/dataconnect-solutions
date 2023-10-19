---
title: "BasicDataSet_v0.SharePointFiles_v1"
description: "The BasicDataSet_v0.SharePointFiles_v1 dataset contains information on actions performed on SharePoint Files including details about actor, performed action, time of action, etc."
author: "lmadhala-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.SharePointFiles_v1 dataset contains information on actions performed on SharePoint Files including details about actor, performed action, time of action, etc.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter |
|---|---|---|---|---|---|
| ptenant | string | GUID of the SharePoint Tenant to which the file belongs (OMS Tenant Id)  | 72f988bf-86f1-41af-91ab-2d7cd011db47 | 0 | false |
| SiteId | string | The GUID of the Site containing this file | 355f5fec-e502-4fa0-9218-c0e9ec019491 | 0 | false |
| Author | string | User that created the file. Format: ```STRUCT<`Email`:STRING, `Name`:STRING>``` | ```{\"Email\": \"jsmith@contoso.com\",\"Name\": \"John Smith\"}``` | 0 | false |
| DirName | string | The Directory's web-relative URL. Includes the list and nested directories, excluding the site or web, and denotes the document’s folder path | Project Details/Project1 | 0 | false |
| Extension | string | The file extension (excludes leading dot), derived from the file name | docx | 0 | false |
| FileName | string | The name of the file, including the extension | Overview.docx | 0 | false |
| IsLabelEncrypted | boolean | True if the file has a sensitivity label with encryption enabled. False otherwise | false | 0 | false |
| ItemId | string | GUID of the item/file Combine with SiteId to get a globally unique id (SiteId + ItemId)| 5b1b92b7-4c07-40f2-aa24-c35224e3bc4b | 0 | false |
| ListId | string | GUID of the List containing this file | 5d37eaec-92da-45e3-8d09-8a5b3c129a12 | 0 | false |
| ListServerTemplate | string | The name of the server template used for the List containing this file | DocumentLibrary | 0 | false |
| MajorVersion | int32 | The major version number of the file | 2 | 0 | false |
| MinorVersion | int32 | The minor version number of the file | 0 | 0 | false |
| ModifiedBy | string | User that last modified the file. Format: ```STRUCT<`Email`:STRING, `Name`:STRING>``` | ```{\"Email\": \"jsmith@contoso.com\",\"Name\": \"John Smith\"}``` | 0 | false |
| Operation | string | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted') | Full | 0 | false |
| ScopeId | string | GUID for the Security Scope that controls the permissions for the file | cd2308be-78e4-42d4-b23d-d1cf3d7353e3 | 0 | false |
| SensitivityLabelInfo | string | Sensitivity Label for the site. Format: ```STRUCT<`DisplayName`:STRING, `Id`:STRING>``` | ```{\"DisplayName\": \"Contoso Confidential\",\"Id\": \"12345673-8d20-48a3-8ea2-0f9612345673\"}``` | 0 | false |
| SiteUrl | string | The URL of the site containing this file | https://contoso.sharepoint.com/sites/odsp | 0 | false |
| SizeInBytes | int64 | Size of the file in bytes (last version only) | 1234 | 0 | false |
| SizeInBytesWithVersions | int64 | Size of the file including all versions and metadata  | 2468 | 0 | false |
| TimeCreated | datetime | The date and time when the file was created, in UTC | 2022-11-30T05:39:15Z | 0 | false |
| TimeLastModified | datetime | The date and time when the file was last modified, in UTC | 2022-11-30T05:39:15Z | 0 | false |
| WebId | string | GUID of the Web containing this file | ab56a32c-491a-4f43-8982-07cafe5d9814 | 0 | false |
| WebTemplateId | int32 | GUID of the template used for the Web containing this file | 6221 | 0 | false |
| SnapshotDate | datetime | The date this dataset was generated | 2023-03-01T00:00:00Z | 1 | true |
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted')|Created|0|false|


## Notes

The "Operation" property is related to the Full or Delta Mode. Please refer to the examples below.
1. To get a full snapshot, please ensure that start and end date are the SAME date       
    - Start date: 1/1/2023       
    - End date: 1/1/2023       
    - User receives one full snapshot of data for that day (1/1/2023).       
    - For all objects, the Operation will be “Full”    

2. To get a Delta snapshot, with only the objects that were created/updated/delete. Please ensure the start date and end date are different and the start date is before the end date.
        - Start date: 1/1/2023       
        - End date: 1/3/2023       
        -  Delta snapshot serves rows that have a difference between the start and end date. Delta also serves rows which had a change in the requested columns (if items were updated but there were no changes in the requested columns, no rows will be served)
        -  Since Delta is the difference between the snapshot, any updates that is later reverted during the Start Date and the End Date will not be served
        - For each object, the Operation will be “Created”, “Updated” or “Deleted”.