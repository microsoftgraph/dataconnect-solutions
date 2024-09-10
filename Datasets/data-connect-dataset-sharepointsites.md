---
title: "BasicDataSet_v0.SharePointSites_v1 dataset"
description: "The SharePoint Sites dataset includes information about every site in the tenant"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.SharePointSites_v1 dataset

### Description: 

The SharePoint Sites dataset includes information about every site in the tenant, including details about name, size, owner, and type. 

### Scenarios:

 - Analytics for sites, particularly count and size for each type (OneDrive, Group Sites, Team Sites, Communication Sites, Channel).
 - Sites configured with options like sharing by link, sharing by e-mail, access by unmanaged devices, sensitivity labels, classification, or information barriers.
 - Calculations around quota and utilization by type or age, like sites hitting a certain limit or percentage of quota.
 - Sites that are missing primary or secondary owners.
 - Understanding of site lifecycle, including creation, growth, and abandonment.

### Questions:

 - What SharePoint sites are the largest?
 - What type of site uses the most storage?
 - What's the current storage for sensitive sites?
 - How much is used by previous versions?
 - Which sites were updated in the last few months?
 - Which sites have just one owner?
 - How many sites were created over 2 years ago?
 - How many sites haven't changed in 1 year?
 - How many sites have over 1TB of files?

### Joining with other datasets:

 - This dataset can be joined with the SharePoint Permissions dataset to provide the name or size of each site being shared. This will allow analytics of sharing by site type or any other site property (like sensitivity labels).
 - This dataset can be joined with the SharePoint Groups or Active Directory Groups dataset to expand the groups assigned as primary or secondary owners.

### Definitions:

 - Site = SharePoint site, also known as site collection or SPSite. They may contain multiple webs, lists, document libraries, and documents.
 - Web = SharePoint web, also known as subsites or SPWebs. Webs are inside a SharePoint Site and may contain multiple lists, document libraries and documents.
 - Root Webs = The main web for a site, created when you first created the site. The type of the root web defines the type of the site.
 - OneDrive = A special type of site created for each user. Also known as personal sites or OneDrive for business.

### Notes:

- This dataset is available after 48 hours. For instance, you can query data for 01/01 starting in 01/03.
- This data is available for 21 days. For instance, the data for 01/01 is available from 01/03 to 01/22.
- To get a full dataset (all the objects), use a date filter with the same date for the start and end date.
- To get a delta dataset (objects that changed between two dates), use different dates for start and end in the date filter.

### Schema:

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| ptenant | string | Id of the tenant | No | False |
| Id | string | GUID of the site | No | False | 
| Url | string | URL for the site | No | False | 
| ArchiveState | string | The archive state of the site: None, Archiving, Archived, or Reactivating | No | False |
| RootWeb | Object | Root web information for the site. *Format:* STRUCT<`Id`:STRING, `Title`:STRING, `WebTemplate`:STRING, `WebTemplateId`:INTEGER, `Configuration`:INTEGER, `LastItemModifiedDate`:DATETIME> | No | False |
| RootWeb, Id | string | Root web id | No | False |
| RootWeb, Title | string | Root web title | No | False |
| RootWeb, WebTemplate | string | Root web template name | No | False |
| RootWeb, WebTemplateId | int | Root web template id | No | False |
| RootWeb, Configuration | int | Root web template configuration id | No | False |
| RootWeb, LastItemModifiedDate | datetime | Date when an item in the root web was last modified | No | False |
| WebCount | int64 | Number of webs (subsites) in the site | No | False |
| StorageQuota | int64 | Total storage in bytes allowed for this site | No | False |
| StorageUsed | int64 | Total storage in bytes used by this site (includes main file stream, file metadata, versions and recycle bin) | No | False |
| StorageMetrics | object | Storage metrics for the site. *Format:* STRUCT<`MetadataSize`:INT64, `TotalFileCount`:INT64, `TotalFileStreamSize`:INT64, `TotalSize`:INT64> | No | False | 
| StorageMetrics, MetadataSize | int64 | Total metadata size for the site in bytes | No | False | 
| StorageMetrics, TotalFileCount | int64 | Total number of files for the site | No | False | 
| StorageMetrics, TotalFileStreamSize | int64 | Total size of the latest version of the files for the site in bytes | No | False | 
| StorageMetrics, TotalSize | int64 | Total size of all files for the site in bytes, including metadata, all versions and the recycle bin | No | False | 
| GroupId | string | Id of the group associated with this site | No | False | 
| GeoLocation | string | Geographic region where the data is stored | No | False | 
| IsInRecycleBin | boolean | Indicates that the site has been deleted and is in the recycle bin. If the site is in the recycle bin, other properties like TemplateId might be unavailable | No | False | 
| RecycleBinItemCount | int64 | Number of items in the recycle bin | No | False |
| RecycleBinItemSize | int64 | Size of the items in the recycle bin | No | False |
| SecondStageRecycle<br />BinStorageUsage | int64 | Size of the items in the second stage recycle bin | No | False |
| IsTeamsConnectedSite | boolean | Indicates that the site is connected to Teams | No | False | 
| IsTeamsChannelSite | boolean | Indicates that the site is a channel site | No | False | 
| TeamsChannelType | string | Type of channel, if isTeamsChannelSite is true | No | False | 
| IsHubSite | boolean | Indicates that the site is associated with a hub site | No | False | 
| HubSiteId | string | Id of the hub site for this site, if IsHubSite is true | No | False | 
| IsCommunicationSite | boolean | Indicates that the site is a communication site | No | False | 
| IsOneDrive | boolean | Indicates that the site is a OneDrive | No | False | 
| BlockAccessFrom<br />UnmanagedDevices | boolean | Site is configured to block access from unmanaged devices | No | False | 
| BlockDownloadOf<br />AllFilesOn<br />UnmanagedDevices | boolean | Site is configured to block download of all files from unmanaged devices | No | False | 
| BlockDownloadOf<br />ViewableFilesOn<br />UnmanagedDevices | boolean | Site is configured to block download of viewable files from unmanaged devices | No | False | 
| ShareByEmailEnabled | boolean | Site is configured to enable share by e-mail | No | False | 
| ShareByLinkEnabled | boolean | Site is configured to enable share by link | No | False | 
| IsExternalSharing<br />Enabled | boolean | Indicates if the site is configured to enable external sharing | No | False | 
| SiteConnectedTo<br />PrivateGroup | boolean |Indicates if a site is connected to Private Group | No | False | 
| Privacy | string | Privacy of the site: Private or Public. Applies only to team sites | No | False | 
| SensitivityLabelInfo | object | Sensitivity Label for the site. *Format:* STRUCT<`DisplayName`:STRING, `Id`:STRING> | No | False | 
| SensitivityLabelInfo, Id | string | Id of the Sensitivity Label for the site | No | False | 
| SensitivityLabelInfo, DisplayName | string | Display name of the Sensitivity Label for the site | No | False | 
| Classification | string | Classification of the site | No | False | 
| IBMode | string | Information Barriers Mode: Open, Owner Moderated, Implicit, Explicit, Inferred | No | False | 
| IBSegments | string | List of organization segments if IB mode is Explicit | No | False | 
| Owner | object | Owner of the site. *Format:* STRUCT<`AadObjectId`:STRING, `Email`:STRING, `UPN`:STRING, `Name`:STRING> | No | False | 
| Owner, AadObjectId | string | AAD Object Id of the owner of the site | No | False | 
| Owner, Email | string | Email of the owner of the site | No | False | 
| Owner, UPN | string | User Principal Name for the owner of the site | No | False | 
| Owner, Name | string | Name of the owner of the site | No | False | 
| SecondaryContact | object | Secondary contact for the site. *Format:* STRUCT<`AadObjectId`:STRING, `Email`:STRING,`UPN`:STRING,  `Name`:STRING> | No | False | 
| SecondaryContact, AadObjectId | string |  AAD Object Id of the secondary contact for the site | No | False | 
| SecondaryContact, Email | string | Email of the secondary contact for the site | No | False | 
| SecondaryContact, UPN | string | User Principal Name for the secondary contact for the site | No | False | 
| SecondaryContact, Name | string | Name of the secondary contact for the site | No | False | 
| ReadLocked | boolean | Whether the site is locked for read access. If true, no  users or administrators will be able to access the site | No | False | 
| ReadOnly | boolean | Whether the site is in read-only mode | No | False | 
| CreatedTime | datetime | When the site was created (in UTC) | No | False | 
| LastSecurityModifiedDate | datetime | When security on the site was last changed (in UTC) | No | False | 
| LastUserAccessDate | datetime | Last access by a real user for the site (in UTC) | No | False | 
| SnapshotDate | datetime | When this site information was captured (in UTC) | Yes | True |
| Operation | string | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted') | No | False |

### JSON Representation:

```json
{
    "ptenant": "3adad419-abdd-493e-a3ea-432bd7748cb3",
    "Id": "cf82c172-b840-4ecd-b391-6ab872212cc7",
    "Url": "https://m365x16144201-my.sharepoint.com/personal/isaiahl_m365x16144201_onmicrosoft_com",
    "ArchiveState": "None",
    "RootWeb": {
        "Id": "ada06d11-4035-4519-8572-1374254f591f",
        "Title": "Isaiah Langer",
        "WebTemplate": "SPSPERS",
        "WebTemplateId": 21,
        "Configuration": 0,
        "LastItemModifiedDate": "2023-09-05T23:00:02.000Z"
    },
    "WebCount": 1,
    "StorageQuota": 1099511627776,
    "StorageUsed": 18929550,
    "StorageMetrics": {
        "MetadataSize": 238815,
        "TotalFileCount": 25,
        "TotalFileStreamSize": 14050752,
        "TotalSize": 18909122
    },
    "GroupId": "00000000-0000-0000-0000-000000000000",
    "GeoLocation": "NAM",
    "IsInRecycleBin": false,
    "RecycleBinItemCount": 0,
    "RecycleBinItemSize": 0,
    "SecondStageRecycleBinStorageUsage": 0,
    "IsTeamsConnectedSite": false,
    "IsTeamsChannelSite": false,
    "TeamsChannelType": "None",
    "IsHubSite": false,
    "HubSiteId": "00000000-0000-0000-0000-000000000000",
    "IsCommunicationSite": false,
    "IsOneDrive": false,
    "BlockAccessFromUnmanagedDevices": false,
    "BlockDownloadOfAllFilesOnUnmanagedDevices": false,
    "BlockDownloadOfViewableFilesOnUnmanagedDevices": false,
    "ShareByEmailEnabled": true,
    "ShareByLinkEnabled": true,
    "IsExternalSharingEnabled": true,
    "SiteConnectedToPrivateGroup": true,
    "Privacy": "Private",
    "SensitivityLabelInfo": {},
    "IBMode": "Open",
    "Owner": {
        "AadObjectId": "3d7237e5-c802-4e39-a87f-dfac34eb1447",
        "Email": "Isaiah.Langer@M365x16144201.OnMicrosoft.com",
        "UPN": "IsaiahL@M365x16144201.OnMicrosoft.com",
        "Name": "Isaiah Langer"
    },
    "SecondaryContact": {},
    "ReadLocked": false,
    "ReadOnly": true,
    "CreatedTime": "2023-08-10T01:18:32Z",
    "LastSecurityModifiedDate": "2023-08-10T01:45:29Z",
    "LastUserAccessDate": "2024-05-13T010:39:19Z",
    "Operation": "Full",
    "SnapshotDate": "2024-05-16T00:00:00Z"
}
```

### Sample

```json
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"10b682c7-d272-4bec-a5c6-e071044476d2","Url":"https://contoso.sharepoint.com/sites/TestSite043","RootWeb":{"Id":"03646e42-3b8c-4fcf-a5dd-4302982bdf34","Title":"Test 43","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:01:55.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622187,"StorageMetrics":{"MetadataSize":14767,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609311},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T19:01:07Z","LastSecurityModifiedDate":"2022-05-01T04:01:55Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"692682db-2422-41f5-86ae-2200c45544dc","Url":"https://contoso.sharepoint.com/sites/TestSite096","RootWeb":{"Id":"0c78cec6-688f-43c9-98af-4d789e04a36a","Title":"Test 96","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:02:25.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622188,"StorageMetrics":{"MetadataSize":14757,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609312},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T20:07:32Z","LastSecurityModifiedDate":"2022-05-01T04:02:25Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"9b31a840-4c8c-477e-8e3f-acdfbdbaf6f0","Url":"https://contoso.sharepoint.com/sites/TestSite089","RootWeb":{"Id":"a3049947-253f-494d-bfcc-404865ff4049","Title":"Test 89","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:02:22.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622180,"StorageMetrics":{"MetadataSize":14762,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609304},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T20:01:20Z","LastSecurityModifiedDate":"2022-05-01T04:02:21Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"63ba1199-491e-4d0f-ad13-70a9b5802f19","Url":"https://contoso.sharepoint.com/sites/TestSite094","RootWeb":{"Id":"445fe498-5123-4293-b012-8ce23ca5fdc4","Title":"Test 94","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:02:24.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622179,"StorageMetrics":{"MetadataSize":14761,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609303},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T20:05:02Z","LastSecurityModifiedDate":"2022-05-01T04:02:24Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"26c9fbcf-abb7-48f0-b6dd-6b073e5339b9","Url":"https://contoso.sharepoint.com/sites/TestSite048","RootWeb":{"Id":"a5be1a43-ee08-445d-84c6-12c9805311ef","Title":"Test 48","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:01:58.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622185,"StorageMetrics":{"MetadataSize":14768,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609309},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T19:05:11Z","LastSecurityModifiedDate":"2022-05-01T04:01:58Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"3b25534d-6777-4a39-8f0c-db2fda9d3100","Url":"https://contoso.sharepoint.com/sites/TestSite018","RootWeb":{"Id":"cd254b4c-98f9-4d93-ba48-b0b68e220e11","Title":"Test 18","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:01:41.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":621940,"StorageMetrics":{"MetadataSize":14675,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609064},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T18:00:07Z","LastSecurityModifiedDate":"2022-05-01T04:01:41Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"99ad2d5a-93b7-41c7-98db-0bf0ec2968d7","Url":"https://contoso.sharepoint.com/sites/TestSite019","RootWeb":{"Id":"61f38a3d-ba09-4bd4-9f12-8d135366be84","Title":"Test 19","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:01:42.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622173,"StorageMetrics":{"MetadataSize":14761,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609297},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T18:01:09Z","LastSecurityModifiedDate":"2022-05-01T04:01:41Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"e2b2ab9e-19b7-4909-b5bb-d6f26ad86836","Url":"https://contoso.sharepoint.com/sites/TestSite008","RootWeb":{"Id":"d2575b07-4d06-41a2-ae6b-adc0ecb2e291","Title":"Test 8","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:01:35.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622002,"StorageMetrics":{"MetadataSize":14585,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609126},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T17:51:02Z","LastSecurityModifiedDate":"2022-05-01T04:01:35Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"a9a67893-ced0-4679-965b-f15fcdfd177d","Url":"https://contoso.sharepoint.com/sites/TestSite077","RootWeb":{"Id":"c3b5b3d2-1a2c-4d62-851a-4bf31c515783","Title":"Test 77","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:02:15.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622188,"StorageMetrics":{"MetadataSize":14771,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609312},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T19:36:28Z","LastSecurityModifiedDate":"2022-05-01T04:02:15Z","SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","Id":"ef8f85f3-c557-4fa8-ab4d-fcef353c270b","Url":"https://contoso.sharepoint.com/sites/TestSite069","RootWeb":{"Id":"13628e8e-62e4-4485-ab54-2af3fe62daa0","Title":"Test 69","WebTemplate":"STS","WebTemplateId":1,"LastItemModifiedDate":"2022-05-01T04:02:10.000Z"},"WebCount":1,"StorageQuota":27487790694400,"StorageUsed":622180,"StorageMetrics":{"MetadataSize":14769,"TotalFileCount":0,"TotalFileStreamSize":0,"TotalSize":609304},"GroupId":"00000000-0000-0000-0000-000000000000","GeoLocation":"NAM","IsInRecycleBin":false,"IsTeamsConnectedSite":false,"IsTeamsChannelSite":false,"TeamsChannelType":"None","IsHubSite":false,"HubSiteId":"00000000-0000-0000-0000-000000000000","BlockAccessFromUnmanagedDevices":false,"BlockDownloadOfAllFilesOnUnmanagedDevices":false,"BlockDownloadOfViewableFilesOnUnmanagedDevices":false,"ShareByEmailEnabled":false,"ShareByLinkEnabled":false,"SensitivityLabelInfo":{},"IBMode":"Open","Owner":{"AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Email":"admin@contoso.onmicrosoft.com","Name":"Jane Doe"},"SecondaryContact":{},"ReadLocked":false,"ReadOnly":false,"CreatedTime":"2022-03-08T19:29:08Z","LastSecurityModifiedDate":"2022-05-01T04:02:10Z","SnapshotDate":"2022-06-02T00:00:00Z"}
```
