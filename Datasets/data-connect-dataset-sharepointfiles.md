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
- To get a full dataset (all the objects), use a date filter with the same date for the start and end date.
- To get a delta dataset (objects that changed between two dates), use different dates for start and end in the date filter.

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
| QuickXorHash | string | A 20-byte non-cryptographic hash of the file. Expected to be null for recently modified files and certain file types such as OneNote and Loop. Algorithm details at https://learn.microsoft.com/en-us/onedrive/developer/code-snippets/quickxorhash | No | False |

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
    "SnapshotDate": "2023-10-22T00:00:00Z",
    "Operation": "Full",
    "QuickXorHash":"39c1fcef5b75b0c54bf9864b3a380bd5a318721e"
}
```

### Sample

```json
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"01322e64-a4f5-456d-87ee-2c7719672d62","Author":{"Name":"System Account"},"DirName":"SiteAssets","Extension":"png","FileName":"__siteIcon__.png","IsLabelEncrypted":false,"ItemId":"a55d8418-670a-4e8b-a4a7-5b45e6df2e47","ListId":"2c85e321-e884-4d64-97c6-3aeef59f8488","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"System Account"},"Operation":"Full","ScopeId":"8e9d904e-b5e6-4f0c-9aea-ba8fb4f73535","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com/sites/SizeTest1","SizeInBytes":680,"SizeInBytesWithVersions":1368,"TimeCreated":"2023-07-29T16:54:35Z","TimeLastModified":"2023-07-29T16:54:35Z","WebId":"e9f81c10-bb5a-4a43-ace8-4034b909e0f8","WebTemplateId":64,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"8557a0b777323ea5dcd93dacbd744f4fa4aad8b8"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"01322e64-a4f5-456d-87ee-2c7719672d62","Author":{"Name":"System Account"},"DirName":"SiteAssets","Extension":"jpg","FileName":"__siteIcon__.jpg","IsLabelEncrypted":false,"ItemId":"bca73de0-7c3e-4704-aeec-366e8c7cf46d","ListId":"2c85e321-e884-4d64-97c6-3aeef59f8488","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"System Account"},"Operation":"Full","ScopeId":"8e9d904e-b5e6-4f0c-9aea-ba8fb4f73535","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com/sites/SizeTest1","SizeInBytes":12237,"SizeInBytesWithVersions":12965,"TimeCreated":"2023-07-29T16:54:35Z","TimeLastModified":"2023-07-29T16:54:35Z","WebId":"e9f81c10-bb5a-4a43-ace8-4034b909e0f8","WebTemplateId":64,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"93f11d21b2f3962381040666d9b087f1678ac379"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"docx","FileName":"HolidayParty_flyer-8baa1cd9-98dd-4d5c-aa7c-98f98582d4bb.docx","IsLabelEncrypted":false,"ItemId":"13e5c7a3-500c-410f-b757-156604e563c6","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"304b0e4e-faac-4a02-ba42-92ba282f00b5","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":721475,"SizeInBytesWithVersions":1445059,"TimeCreated":"2023-05-05T22:16:07Z","TimeLastModified":"2023-05-05T22:16:08Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"370d23704ac6a9706f730629d8e31df73ecab4aa"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"xlsx","FileName":"Monthly Water Report - September 2018-ec2beb6b-573f-4a24-8da9-f4c403eba605-66f1e6dd-c3e2-4689-b130-43717f476c8c.xlsx","IsLabelEncrypted":false,"ItemId":"1b1c3acd-555c-4c52-8dd8-fd5569c000b5","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"304b0e4e-faac-4a02-ba42-92ba282f00b5","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":175860,"SizeInBytesWithVersions":353364,"TimeCreated":"2023-05-05T22:15:45Z","TimeLastModified":"2023-05-05T22:15:45Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"4f99ae0b86136065fc8775cd06718457dafdaf5c"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"pptx","FileName":"ContosoGivingProgram-259fc09b-e26e-4039-8094-105d39edf09c-bb237872-3f08-4c39-b0f8-91d91b3bbc61.pptx","IsLabelEncrypted":false,"ItemId":"35b938cf-0cdd-4495-9ab1-c6adae329fb7","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"304b0e4e-faac-4a02-ba42-92ba282f00b5","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":4595019,"SizeInBytesWithVersions":9192382,"TimeCreated":"2023-05-06T03:20:45Z","TimeLastModified":"2023-05-06T03:20:46Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"7275b44ce727d6e1296b3c14f116e75be6238247"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"docx","FileName":"Contoso Company Goals Q1 - Q4-2a6caff7-6fc6-45c1-acb1-6a3d2ac584ff.docx","IsLabelEncrypted":false,"ItemId":"62ded267-1d27-4e85-93d4-cb12ed564ecb","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"304b0e4e-faac-4a02-ba42-92ba282f00b5","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":227895,"SizeInBytesWithVersions":457522,"TimeCreated":"2023-05-06T03:20:34Z","TimeLastModified":"2023-05-06T03:20:34Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"6978ecc029464366ae9c2289acb0a3cb920db33f"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"txt","FileName":"TestUploadDoc.txt","IsLabelEncrypted":false,"ItemId":"8c43d507-00a2-4830-8a06-e687afca80f0","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":3,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"ccfb2f11-18a0-4717-8be8-398d5bbfcc7a","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":12,"SizeInBytesWithVersions":1304,"TimeCreated":"2022-10-18T20:08:39Z","TimeLastModified":"2023-05-05T22:16:19Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"7428c31ce8408732cc810e6594031d6200000000"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"pdf","FileName":"Contoso-Works-Culture-c62ec7c5-1e0c-4c75-9561-89d16983fa3b-4c62519c-f94c-4260-b0fa-a1f3101a0cb4.pdf","IsLabelEncrypted":false,"ItemId":"96931b35-d75e-453c-8cc1-29505e5c03a0","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"304b0e4e-faac-4a02-ba42-92ba282f00b5","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":2984798,"SizeInBytesWithVersions":5970899,"TimeCreated":"2023-05-05T22:15:54Z","TimeLastModified":"2023-05-05T22:15:54Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"778813c5a43fb15da64f5e4a016620a43566056c"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"docx","FileName":"TestSensLabel.docx","IsLabelEncrypted":false,"ItemId":"bbcd1010-3128-4727-92c7-7148a56ade4c","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":4,"MinorVersion":0,"ModifiedBy":{"Name":"Admin Contoso","Email":"admin@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"304b0e4e-faac-4a02-ba42-92ba282f00b5","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":21981,"SizeInBytesWithVersions":31527,"TimeCreated":"2023-04-11T21:51:16Z","TimeLastModified":"2023-05-06T03:21:07Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"b5e4be36b1324e8e75ade822c978f2464bc7aa92"}
{"ptenant":"3a3630bf-4d67-4c21-842a-abcde48840d5","SiteId":"1796ce4d-abcd-448f-9895-0cafa997be39","Author":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"DirName":"Shared Documents","Extension":"pptx","FileName":"Potential Australia Exp.-60461090-27eb-4990-a8b4-710667311e8b-a0c125ca-4aad-44d0-9529-b508df1e33be.pptx","IsLabelEncrypted":false,"ItemId":"d0264708-ba9e-4d49-8b9d-54fc021e1b5e","ListId":"40dc6c5b-5aae-48b6-a420-9f4cd4987196","ListServerTemplate":"DocumentLibrary","MajorVersion":2,"MinorVersion":0,"ModifiedBy":{"Name":"Contoso User3","Email":"ContosoUser3@contoso.onmicrosoft.com"},"Operation":"Full","ScopeId":"304b0e4e-faac-4a02-ba42-92ba282f00b5","SensitivityLabelInfo":{},"SiteUrl":"https://odspContoso.sharepoint.com","SizeInBytes":161154,"SizeInBytesWithVersions":324812,"TimeCreated":"2023-05-06T03:20:39Z","TimeLastModified":"2023-05-06T03:20:39Z","WebId":"8d908b0c-4518-44bb-a11d-3e7a99a03003","WebTemplateId":68,"SnapshotDate":"2024-07-31T00:00:00Z","QuickXorHash":"20adf6afd969989918f32185ca01e958b032b0da"}
```
