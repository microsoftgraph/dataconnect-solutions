---
title: "BasicDataSet_v0.SharePointFileActions_v1"
description: "The BasicDataSet_v0.SharePointFileActions_v1 dataset contains information on actions performed on SharePoint Files including details about actor, performed action, time of action, etc."
author: "lmadhala-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The BasicDataSet_v0.SharePointFileActions_v1 dataset contains information on actions performed on SharePoint Files including details about actor, performed action, time of action, etc.

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter |
|---|---|---|---|---|---|
| ptenant | string | GUID of the SharePoint Tenant to which the file belongs (OMS Tenant Id)  | 72f988bf-86f1-41af-91ab-2d7cd011db47 | 0 | false |
| ActionDate | string | Date and time when the action occurred | 2023-04-20T00:00:00Z | 0 | false |
| ActionName | string | Name of the Action: FileAccessed, FileDeleted, FileDownloaded, FileModified, FileMoved, FileRenamed, FileUploaded  | FileAccessed | 0 | false |
| ActorDisplayName | string | Name of the user | sampleuser | 0 | false |
| ActorEmail | string | Email of the user | sampleuser@microsoft.com | 0 | false |
| ActorIdType | string | Type of Id. Enum: Unknown, Anonymous, AAD, MSA, Self | Anonymous | 0 | false |
| ActorType | string | Type of Actor. Enum: NotSpecified, System, User | System | 0 | false |
| ClientIP | string | IP address of the client performing this action | 255.255.255.255 | 0 | false |
| ItemExtension | string | The file extension (excludes leading dot), derived from the file name | png | 0 | false |
| ItemName | string | Name of the file, including the extension  | samplefilename.png | 0 | false |
| ItemURL | string | Full URL for the item | personal/sampleuser_microsoft_com/Documents/Folder2/SampleFile.docx | 0 | false |
| ListId | string | GUID that identifies the List or Document Library containing the file | 88e740cb-13ed-46dd-aeeb-0293bfed3438 | 0 | false |
| ListItemId | string | GUID that identifies the file (unique Id is SiteId + WebId + ListId + ListItemId) | 88e740cb-13ed-46dd-aeeb-0293bfed3438 | 0 | false |
| SiteId | string | GUID of the Site for the file | 88e740cb-13ed-46dd-aeeb-0293bfed3438 | 0 | false |
| SiteUrl | string | URL for the site | https://microsoft.sharepoint-df.com/sampleuser/ | 0 | false |
| UserAgent | string | User agent string of the client device used to perform this action | Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.57 | 0 | false |
| WebId | string | GUID that identifies the Web inside the Site containing the file | 88e740cb-13ed-46dd-aeeb-0293bfed3438 | 0 | false |
| WebTemplateId | string | ID of the template for the site containing the file.  | 88e740cb-13ed-46dd-aeeb-0293bfed3438 | 0 | false |
| SnapshotDate | datetime | When this site information was captured (in UTC)  | 2022-03-16T00:00:00Z | 1 | true |


## Notes

The "Operation" property is related to the Full or Delta Mode. Please refer to the examples below.
1. To get a full snapshot, please ensure that start and end date are the SAME date       
    - Start date: 1/1/2023       
    - End date: 1/1/2023       
    - User receives one full snapshot of data for that day (1/1/2023).       
    - For all objects, the Operation will be “Full”    

2. This dataset does not support Delta snapshot. If start and end date are **NOT** THE SAME date, all data from start date and end date (inclusive) will be served.