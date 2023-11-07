---
title: "BasicDataSet_v0.SharePointFileActions_v1 dataset"
description: "The SharePoint File Actions datasets includes details about every file action"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.SharePointFileActions_v1 dataset

### Description: 

The SharePoint File Actions datasets includes details about every time a file was accessed, deleted, downloaded, modified, moved, renamed, or uploaded. Other file actions are not included. This helps you understand how documents are being used across the tenant. The dataset contains one object for each file-related action in SharePoint for the tenant, including the type of action, when it happened, what file was acted upon, what application was used, and who performed the action. 

**IMPORTANT NOTE:** This dataset is not currently available publicly. It is coming soon. The details provided here are for informational purposes only.

### Scenarios:

- Report on file usage by site, site type, file extension, action, or application. 
- Number of sites, webs, lists, document libraries that had usage for files with a specific extension or type.
- Analytics for type of actions performed.
- Understanding the lifecycle of sites, like which sites were most used recently. 
- Understanding people that are active on a specific site. 

### Questions:

- Which site had the most access?
- Which site type had the most uploads?
- What is the distribution of file actions based on the age of the site?
- Which file extensions had the most downloads?
- Which file authors had the most modifications to their files?
- Which files were most modified by users other than the author?
- Which user was the most active in each SharePoint site?
- How many files were deleted by the author as a percentage of deletes?
- Are files hosted in Teams-connected sites more accessed than files that are not Teams-connected?

### Joining with other datasets:

- This dataset can be joined with the SharePoint Files to provide details about the file like creation date, last modified date, sensitivity label, author, or size. This will allow analytics of file actions by author, file age, or any other file attribute.
- This dataset can be joined with the SharePoint Sites dataset to do analytics at the site level, like the number of file actions by the type of site (web template), site owner, site age, or Teams connection.

### Definitions:

- SharePoint Files are also known as SPFiles or documents.
- Files are stored in Lists (also known as SPLists)
- Document Libraries are a special type of List used for storing documents.
- Lists and Document libraries are stored in Webs (also known as SPWebs, subsites or simply sites)
- Webs are stored in Sites (also known as SPSites or site collections)

### Notes:

- This dataset is available after 48 hours. For instance, you can query data for 01/01 starting in 01/03.
- This data is available for 21 days. For instance, the data for 01/01 is available from 01/03 to 01/22.
- This dataset captures daily actions and does not support Deltas, since the data is unique for each day.

### Schema:

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| ptenant | string | Id of the tenant | No | False |
| SiteId | string | GUID of the Site for the file | No | False |
| SiteUrl | string | URL for the site | No | False |
| WebId | string | GUID that identifies the Web inside the Site containing the file | No | False |
| WebTemplateId | int | ID of the template for the site containing the file | No | False |
| ListId | string | GUID that identifies the List or Document Library containing the file | No | False |
| ListItemId | string | GUID that identifies the file | No | False |
| ItemURL | string | Full URL for the item | No | False |
| ItemName | string | Name of the file, including the extension | No | False |
| ItemExtension | string | The file extension (excludes leading period), derived from the file name | No | False |
| ActionDate | datetime | Date and time when the action occurred (in UTC) | No | False |
| ActorType | string | Type of user. Enum: NotSpecified, System, User | No | False |
| ActorIdType | string | Type of user id. Enum: Unknown, Anonymous, AAD, MSA, Self | No | False |
| ActorDisplayName | string | Name of the user | No | False |
| ActorEmail | string | Email of the user | No | False |
| ActionName | string | Name of the action: FileAccessed, FileDeleted, FileDownloaded, FileModified, FileMoved, FileRenamed, FileUploaded | No | False |
| UserAgent | string | User agent string of the client device used to perform this action | No | False |
| ClientIP | string | IP address of the client performing this action | No | False |
| SnapshotDate | datetime | The date this dataset was generated (in UTC) | Yes | True |

### JSON Representation:

```json
{
    "ptenant": "537d6c63-efb6-4922-8643-17921eb1b0dd",
    "SiteId": "abb417b5-8c80-4ccf-8fb2-67a6a548fa06",
    "SiteUrl": "https://contoso.sharepoint.com/teams/Projects/",
    "WebId": "5599f943-e88e-44d2-916b-6039a9898a8a",
    "WebTemplateId": 21,
    "ListId": "725e6dfe-def6-41ca-a0d3-4c36ba6aea68",
    "ListItemId": "94617d46-d947-41fb-9ec7-4055c0b331d4",
    "ItemURL": "https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx",
    "ItemName": "Project plan.docx",
    "ItemExtension": "docx",
    "ActionDate": "2023-09-10T01:45:30.1550648+00:00",
    "ActorType": "User",
    "ActorIdType": "AAD",
    "ActorDisplayName": "John Smith",
    "ActorEmail": "jsmith@contoso.com",
    "ActionName": "FileModified",
    "UserAgent": "Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)",
    "ClientIP": "255.255.255.255",
    "SnapshotDate": "2023-09-10T00:00:00Z"
}
```

### Sample

```json
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T01:45:30.1550648+00:00","ActionName":"FileModified","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T05:28:57.6157508+00:00","ActionName":"FileMoved","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T03:30:09.9039112+00:00","ActionName":"FileDeleted","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T18:40:05.0860076+00:00","ActionName":"FileUploaded","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T21:37:58.8503815+00:00","ActionName":"FileDownloaded","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T02:00:19.1374469+00:00","ActionName":"FileMoved","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T23:50:55.7142137+00:00","ActionName":"FileUploaded","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T05:52:17.4978702+00:00","ActionName":"FileAccessed","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T12:03:18.5336543+00:00","ActionName":"FileDownloaded","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
{"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","ActionDate":"2023-09-10T14:08:19.3283304+00:00","ActionName":"FileDeleted","ActorDisplayName":"John Smith","ActorEmail":"jsmith@contoso.com","ActorIdType":"AAD","ActorType":"User","ItemExtension":"docx","ItemName":"Project plan.docx","ItemURL":"https://contoso.sharepoint.com/teams/Projects/Projects Spec Library/ProjectA/Specs/Project plan.docx","ListId":"725e6dfe-def6-41ca-a0d3-4c36ba6aea68","ListItemId":"94617d46-d947-41fb-9ec7-4055c0b331d4","SiteId":"abb417b5-8c80-4ccf-8fb2-67a6a548fa06","SiteUrl":"https://contoso.sharepoint.com/teams/Projects/","UserAgent":"Microsoft Office Word/16.0.16124.20000 (Windows/10.0; Desktop WOW64; en-US; Desktop app; HP/HP Z240 Tower Workstation)","WebId":"5599f943-e88e-44d2-916b-6039a9898a8a","WebTemplateId":21,"SnapshotDate":"2023-09-10T00:00:00Z"}
```