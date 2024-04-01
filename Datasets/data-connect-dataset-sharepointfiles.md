---
title: "BasicDataSet_v0.SharePointFiles_v1 dataset"
description: "The SharePoint Files dataset includes information about files in SharePoint and OneDrive"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.SharePointFiles_v1 dataset

### Description: 

The SharePoint Files dataset includes information about files in SharePoint and OneDrive, including details like name, size, and owner. This includes one object for every file in the tenant stored in a SharePoint document library, including OneDrives.  

**IMPORTANT NOTE:** This dataset is not currently available publicly. It is coming soon. The details provided here are for informational purposes only.

### Scenarios:

- Report on file count and bytes used, with grouping and filtering by properties like file extension,   date created, date modified or sensitivity label. 
- Number of sites, webs, document libraries that contain files with a specific extension, type or sensitivity.
- Better understanding of site lifecycle, including when files were added, last modified or removed.  

### Questions:

- What file extensions use the most storage?
- What file extensions are the most numerous?
- What is the number of files marked with sensitivity labels?
- What is the amount of previous version storage for files of a specific type?
- Which user authored the most PowerPoint presentations?
- What is the distribution of number of files by their age in months?
- How many files were modified in the last 6 months?
- How many files were last modified by someone other than the author?

### Joining with other datasets:

- This dataset can be joined with the Sites dataset (by SiteId) to provide information about files for a specific site or a specific subset of sites (files in OneDrive for business, files in sites created in the last 6 months)
- This dataset can be joined with the Sharing Permissions   dataset (by SiteId and ScopeId) to provide information on the files within a scope.  

### Definitions:

- SharePoint Files are also known as SPFiles or documents.
- Files are stored in Lists (also known as SPLists)
- Document Libraries are a special type of List used for storing documents
- Lists and Document libraries are stored in Webs (also known as SPWebs, subsites or simply sites)
- Webs are stored in Sites (also known as SPSites or site collections)

### Notes:

- This dataset is available after 48 hours. For instance, you can query data for 01/01 starting in 01/03.
- This data is available for 21 days. For instance, the data for 01/01 is available from 01/03 to 01/22.

### Schema:

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| ptenant | string | GUID of the SharePoint Tenant to which the file belongs (OMS Tenant Id) | No | False |
| SiteId | string | GUID of the Site for the file | No | False |
| WebId | string | GUID of the Web for the file | No | False |
| ListId | string | GUID of the List for the file | No | False |
| ItemId | string | GUID of the item/file (unique Id is SiteId + ItemId)   | No | False |
| ScopeId | string | GUID for the Security Scope that controls the permissions for the file | No | False |
| SiteUrl | string | The URL of the site containing this file | No | False |
| DirName | string | Folder path of the document | No | False |
| FileName | string | The name of the file, including the extension | No | False |
| Extension | string | The file extension (excludes leading period), derived from the file name | No | False |
| WebTemplateId | int | Id of the template used for the Web for this file | No | False |
| ListServerTemplate | string | Document library template for this file. It can be DocumentLibrary or MySiteDocumentLibrary (ODB) | No | False |
| IsLabelEncrypted | boolean | True if file has a sensitivity label and has encryption enabled | No | False |
| MajorVersion | int | The major version number of the file | No | False |
| MinorVersion | int | The minor version number of the file | No | False |
| SensitivityLabelInfo | object | Sensitivity Label for the file. *Format:* STRUCT<`Id`:STRING, `DisplayName`:STRING> | No | False |
| SensitivityLabelInfo, Id | string | GUID for the Sensitivity Label | No | False |
| SensitivityLabelInfo, DisplayName | string | Sensitivity Label Display Name | No | False |
| SizeInBytes | long | Size of the file in bytes (last version only) | No | False |
| SizeInBytesWithVersions | long | Size of the file including all versions and metadata | No | False |
| TimeCreated | datetime | The date and time when the file was created, in UTC | No | False |
| TimeLastModified | datetime | The date and time when the file was last modified, in UTC | No | False |
| Author | object | User that created the file. *Format:* STRUCT<`Email`:STRING,`Name`:STRING> | No | False |
| Author, Email | string | Email of the user that created the file | No | False |
| Author, Name | string | Name of the user that created the file | No | False |
| ModifiedBy | object | User that last modified the file. *Format:* STRUCT<`Email`:STRING,`Name`:STRING> | No | False |
| ModifiedBy, Email | string | Email of the user that last modified the file | No | False |
| ModifiedBy, Name | string | Name of the user that last modified the file | No | False |
| SnapshotDate | datetime | The date this dataset was generated (in UTC) | Yes | True |
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted') | No | False |

### JSON Representation:

```json
{
    "ptenant": "3a3630bf-4d67-4c21-842a-abcea48840d5",
    "SiteId": "191f7f71-a950-432b-809f-09043cec1d88",
    "WebId": "0e57e46e-3215-4eb3-ab53-c9cc5a44a683",
    "ListId": "c699f07e-a71f-4df6-aeef-4168e1b4b3c6",
    "ItemId": "0f1802ae-42c5-4ecc-91f9-9834f77ef478",
    "ScopeId": "323dfda8-26a4-48c6-9a01-df374683ce4a",
    "SiteUrl": "https://contoso.sharepoint.com/sites/ProjectA",
    "DirName": "Shared Documents",
    "FileName": "ProjectASpecs.pdf",
    "Extension": "pdf",
    "WebTemplateId": 64,
    "ListServerTemplate": "DocumentLibrary",
    "IsLabelEncrypted": false,
    "MajorVersion": 3,
    "MinorVersion": 0,
    "SensitivityLabelInfo": {},
    "SizeInBytes": 16485996,
    "SizeInBytesWithVersions": 49459733,
    "TimeCreated": "2023-05-06T03:20:33Z",
    "TimeLastModified": "2023-05-06T03:21:03Z",
    "Author": {
        "Name": "Contoso User3",
        "Email": "ContosoUser3@contoso.onmicrosoft.com"
    },
    "ModifiedBy": {
        "Name": "Contoso User3",
        "Email": "ContosoUser3@contoso.onmicrosoft.com"
    },
    "SnapshotDate": "2023-10-22T00:00:00Z"
    "Operation": "Full"
}
```

### Sample

```json
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"191f7f71-a950-432b-809f-09043cec1d88","Author":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"pdf","FileName":"ProjectASpecs.pdf","IsLabelEncrypted":false,"ItemId":"0f1802ae-42c5-4ecc-91f9-9834f77ef478","ListId":"c699f07e-a71f-4df6-aeef-4168e1b4b3c6","ListServerTemplate":"DocumentLibrary","MajorVersion":3,"MinorVersion":0,"ModifiedBy":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"323dfda8-26a4-48c6-9a01-df374683ce4a","SensitivityLabelInfo":{},"SiteUrl":"https://contoso.sharepoint.com/sites/ProjectA","SizeInBytes":16485996,"SizeInBytesWithVersions":49459733,"TimeCreated":"2023-05-06T03:20:33Z","TimeLastModified":"2023-05-06T03:21:03Z","WebId":"0e57e46e-3215-4eb3-ab53-c9cc5a44a683","WebTemplateId":64,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"a0e4b708-b0fe-441e-9623-9acc1210776e","Author":{"Name":"System Account"},"DirName":"Style Library/Images","Extension":"jpg","FileName":"Search_Arrow.jpg","IsLabelEncrypted":false,"ItemId":"05be36a7-be16-4d3b-b0fb-fd78904ed4c5","ListId":"af998ebe-643c-40b6-860b-e14fad11b165","ListServerTemplate":"DocumentLibrary","MajorVersion":1,"MinorVersion":0,"ModifiedBy":{"Name":"System Account"},"Operation":"Full","ScopeId":"22de3179-132c-49c1-97cb-d166a3e70adb","SensitivityLabelInfo":{},"SiteUrl":"https://contoso.sharepoint.com/sites/ContosoWiki-DeltaTest-08Aug2022","SizeInBytes":592,"SizeInBytesWithVersions":744,"TimeCreated":"2022-08-09T02:33:58Z","TimeLastModified":"2022-08-09T02:33:58Z","WebId":"8f43c93e-d157-473f-a8dd-66e81b440178","WebTemplateId":56,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"ec508b4b-6e54-401c-826f-775950f03353","Author":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"DirName":"Documents/Documents","Extension":"xlsx","FileName":"Monthly Water Report - September 2018-ec2beb6b-573f-4a24-8da9-f4c403eba605-1683343430.xlsx","IsLabelEncrypted":false,"ItemId":"363bc9a6-2031-460d-a3da-524960575321","ListId":"8779ac14-b274-4bf2-92ac-7760fdc1ebc7","ListServerTemplate":"MySiteDocumentLibrary","MajorVersion":1,"MinorVersion":0,"ModifiedBy":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"9924d4d2-302a-4680-a71f-be587a82b96f","SensitivityLabelInfo":{},"SiteUrl":"https://contoso-my.sharepoint.com/personal/ContosoUser3_contoso_onmicrosoft_com","SizeInBytes":175819,"SizeInBytesWithVersions":176984,"TimeCreated":"2023-05-06T03:23:51Z","TimeLastModified":"2023-05-06T03:23:52Z","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","WebTemplateId":21,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"f8894107-9135-422f-84f3-f9bf863319e1","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Documents/Documents","Extension":"docx","FileName":"word-template-15dc044b-0934-4bcd-962c-472f8c1632d5-1683326651.docx","IsLabelEncrypted":false,"ItemId":"13946fd3-277e-4495-bf4f-66e00e87032b","ListId":"8779ac14-b274-4bf2-92ac-7760fdc1ebc7","ListServerTemplate":"MySiteDocumentLibrary","MajorVersion":16,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"9924d4d2-302a-4680-a71f-be587a82b96f","SensitivityLabelInfo":{},"SiteUrl":"https://contoso-my.sharepoint.com/personal/admin_contoso_onmicrosoft_com","SizeInBytes":481526,"SizeInBytesWithVersions":7721466,"TimeCreated":"2023-05-05T22:44:12Z","TimeLastModified":"2023-05-06T01:19:48Z","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","WebTemplateId":21,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"f8894107-9135-422f-84f3-f9bf863319e1","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Documents/Documents","Extension":"pdf","FileName":"7 ways to work together in PowerPoint-fbd1dcde-4ba1-4192-a1a6-02b631615e05-1683329474.pdf","IsLabelEncrypted":false,"ItemId":"24d2efc3-e1bd-49f1-b8ba-23b81309c49a","ListId":"8779ac14-b274-4bf2-92ac-7760fdc1ebc7","ListServerTemplate":"MySiteDocumentLibrary","MajorVersion":11,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"9924d4d2-302a-4680-a71f-be587a82b96f","SensitivityLabelInfo":{},"SiteUrl":"https://contoso-my.sharepoint.com/personal/admin_contoso_onmicrosoft_com","SizeInBytes":1294853,"SizeInBytesWithVersions":14254626,"TimeCreated":"2023-05-05T23:31:15Z","TimeLastModified":"2023-05-06T02:53:59Z","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","WebTemplateId":21,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"f8894107-9135-422f-84f3-f9bf863319e1","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Documents/Documents","Extension":"docx","FileName":"Microsoft Search Announcement Poster Editable-1683335395.docx","IsLabelEncrypted":false,"ItemId":"a3c2ed05-f017-47d5-83be-0483d17a3e1c","ListId":"8779ac14-b274-4bf2-92ac-7760fdc1ebc7","ListServerTemplate":"MySiteDocumentLibrary","MajorVersion":5,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"9924d4d2-302a-4680-a71f-be587a82b96f","SensitivityLabelInfo":{},"SiteUrl":"https://contoso-my.sharepoint.com/personal/admin_contoso_onmicrosoft_com","SizeInBytes":1386518,"SizeInBytesWithVersions":6937977,"TimeCreated":"2023-05-06T01:09:55Z","TimeLastModified":"2023-05-06T02:02:19Z","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","WebTemplateId":21,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"f8894107-9135-422f-84f3-f9bf863319e1","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Documents/Documents","Extension":"pdf","FileName":"Microsoft Search in Bing Adoption Kit Overview-1683338102.pdf","IsLabelEncrypted":false,"ItemId":"d4f2e146-f947-4539-9b12-c6af5945a5cd","ListId":"8779ac14-b274-4bf2-92ac-7760fdc1ebc7","ListServerTemplate":"MySiteDocumentLibrary","MajorVersion":3,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"9924d4d2-302a-4680-a71f-be587a82b96f","SensitivityLabelInfo":{},"SiteUrl":"https://contoso-my.sharepoint.com/personal/admin_contoso_onmicrosoft_com","SizeInBytes":855000,"SizeInBytesWithVersions":2568087,"TimeCreated":"2023-05-06T01:55:03Z","TimeLastModified":"2023-05-06T02:45:50Z","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","WebTemplateId":21,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"f8894107-9135-422f-84f3-f9bf863319e1","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Documents/Documents","Extension":"docx","FileName":"Microsoft-Search-Poster-11x17-FindFiles-Editable-1683326555.docx","IsLabelEncrypted":false,"ItemId":"dbb638f6-7f5d-4cfa-8d6a-da775410360e","ListId":"8779ac14-b274-4bf2-92ac-7760fdc1ebc7","ListServerTemplate":"MySiteDocumentLibrary","MajorVersion":17,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"9924d4d2-302a-4680-a71f-be587a82b96f","SensitivityLabelInfo":{},"SiteUrl":"https://contoso-my.sharepoint.com/personal/admin_contoso_onmicrosoft_com","SizeInBytes":459302,"SizeInBytesWithVersions":7826200,"TimeCreated":"2023-05-05T22:42:36Z","TimeLastModified":"2023-05-06T02:39:46Z","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","WebTemplateId":21,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"704b0414-5bdf-4e53-b679-bfcc6b90d2eb","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"bin","FileName":"test3.bin","IsLabelEncrypted":false,"ItemId":"7091772c-91b2-4b0d-b548-72076200e6c3","ListId":"7d4d265d-1b01-4dd0-94a5-384b0150ac8f","ListServerTemplate":"DocumentLibrary","MajorVersion":1,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"8e9d904e-b5e6-4f0c-9aea-ba8fb4f73535","SensitivityLabelInfo":{},"SiteUrl":"https://contoso.sharepoint.com/sites/StorageTest3","SizeInBytes":10000000,"SizeInBytesWithVersions":10000497,"TimeCreated":"2023-07-29T20:18:23Z","TimeLastModified":"2023-07-29T20:18:23Z","WebId":"e9f81c10-bb5a-4a43-ace8-4034b909e0f8","WebTemplateId":64,"SnapshotDate":"2023-10-22T00:00:00Z"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcea48840d5","SiteId":"a0e4b708-b0fe-441e-9623-9acc1210776e","Author":{"Name":"System Account"},"DirName":"Style Library/ko-kr/Themable/Core Styles","Extension":"css","FileName":"pagelayouts15.css","IsLabelEncrypted":false,"ItemId":"07a9018b-4bc7-4679-9ae8-fc9a0d433852","ListId":"af998ebe-643c-40b6-860b-e14fad11b165","ListServerTemplate":"DocumentLibrary","MajorVersion":1,"MinorVersion":0,"ModifiedBy":{"Name":"System Account"},"Operation":"Full","ScopeId":"22de3179-132c-49c1-97cb-d166a3e70adb","SensitivityLabelInfo":{},"SiteUrl":"https://contoso.sharepoint.com/sites/ContosoWiki-DeltaTest-08Aug2022","SizeInBytes":2820,"SizeInBytesWithVersions":2972,"TimeCreated":"2022-08-09T02:41:10Z","TimeLastModified":"2022-08-09T02:41:10Z","WebId":"8f43c93e-d157-473f-a8dd-66e81b440178","WebTemplateId":56,"SnapshotDate":"2023-10-22T00:00:00Z"}
```
