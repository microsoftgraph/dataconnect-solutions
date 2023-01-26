---
title: "SharePointSitesDataset_v0_Preview"
description: "Contains SharePoint site information, including details about name, size, owner, and type."
author: "David1997sb"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The SharePointSitesDataset_v0_Preview dataset contains SharePoint site information, including details about name, size, owner, and type.   

## Properties
| Name | Type | Description | SampleData | FilterOptions | IsDateFilter | 
|--|--|--| -- | -- |--|
| ptenant | string |Id of the tenant| 72f988bf-86f1-41af-91ab-2d7cd011db47 |0|false|
| Id | string |GUID of the site|355f5fec-e502-4fa0-9218-c0e9ec019491|0|false|
| Url | string |URL for the site|https://contoso.sharepoint.com/teams/odsp|0|false|
| RootWeb | string |Root web information for the site. Format: ```STRUCT<`Id`:STRING, `Title`:STRING, `WebTemplate`:STRING, `WebTemplateId`:INTEGER, `LastItemModifiedDate`:DATETIME>```|```{\"Id\": \"12345675-db94-40aa-ab7a-6efa12345675\",\"Title\": \"ODSP Team\",\"WebTemplate\": \"Team Site\",\"WebTemplateId\": 1,\"LastItemModifiedDate\":\"2020-11-18T19:51:38Z\"}```|0|false|
| WebCount | int64 |Number of webs (subsites) in the site|1|0|false|
| StorageQuota | int64 |Total storage in bytes allowed for this site |27487790694400|0|false|
| StorageUsed | int64 |Total storage in bytes used by this site (includes main file stream, file metadata, versions and recycle bin)|1593238|0|false|
| StorageMetrics | string |Storage metrics for the site. Format: ```STRUCT<`MetadataSize`:INT64, `TotalFileCount`:INT64, `TotalFileStreamSize`:INT64, `TotalSize`:INT64>```|`{\"MetadataSize\": 55887,\"TotalFileCount\": 3,\"TotalFileStreamSize\": 35469,\"TotalSize\": 1492456}`|0|false|
| GroupId | string |Id of the group associated with this site|12345672-db94-40aa-ab7a-6efa12345672|0|false|
| GeoLocation | string |Geographic region where the data is stored|NAM|0|false|
| IsInRecycleBin | boolean |Indicates that the site has been deleted and is in the recycle bin|true|0|false|
| IsTeamsConnectedSite | boolean |Indicates that the site is connected to Teams|true|0|false|
| IsTeamsChannelSite | boolean |Indicates that the site is a channel site|true|0|false|
| TeamsChannelType | string |Type of channel, if isTeamsChannelSite is true||0|false|
| IsHubSite | boolean |Indicates that the site is associated with a hub site|true|0|false|
| HubSiteId | string |Id of the hub site for this site, if IsHubSite is true|00000000-0000-0000-0000-000000000000|0|false|
| BlockAccessFromUnmanagedDevices | boolean |Site is configured to block access from unmanaged devices|true|0|false|
| BlockDownloadOfAllFilesOnUnmanagedDevices | boolean |Site is configured to block download of all files from unmanaged devices|true|0|false|
| BlockDownloadOfViewableFilesOnUnmanagedDevices | boolean |Site is configured to block download of viewable files from unmanaged devices|true|0|false|
| ShareByEmailEnabled | boolean |Site is configured to enable share by e-mail|true|0|false|
| ShareByLinkEnabled | boolean |Site is configured to enable share by link|true|0|false|
| SensitivityLabelInfo | string |Sensitivity Label for the site. Format: ```STRUCT<`DisplayName`:STRING, `Id`:STRING>```|`{\"DisplayName\": \"Contoso Confidential\",\"Id\": \"12345673-8d20-48a3-8ea2-0f9612345673\"}`|0|false|
| Classification | string |Classification of the site|HBI|0|false|
| IBSegments | string |List of organization segments if IB mode is Explicit|Sales|0|false|
| Owner | string |Owner of the site. Format: ```STRUCT<`AadObjectId`:STRING,`Email`:STRING,`Name`:STRING>```|```{\"AadObjectId\": \"12345676-6e0e-46ab-855d-2c8912345676\",\"Email\": \"jsmith@contoso.com\",\"Name\": \"John Smith\"}```|0|false|
| SecondaryContact | string |Secondary contact for the site. Format: ```STRUCT<`AadObjectId`:STRING,`Email`:STRING,`Name`:STRING>```|```{\"AadObjectId\": \"12345674-6e0e-46ab-855d-2c8912345674\",\"Email\": \"jwilliams@contoso.com\", \"Name\": \"John Williams\"}```|0|false|
| ReadLocked | boolean |Whether the site is locked for read access. If true, no users or administrators will be able to access the site|false|0|false|
| ReadOnly | boolean |Whether the site is in read-only mode|false|0|false|
| CreatedTime | datetime |When the site was created (in UTC)|`2020-11-18T19:51:38Z`|0|false|
| LastSecurityModifiedDate | datetime |When security on the site was last changed (in UTC)|`2020-11-18T19:51:38Z`|0|false|
| SnapshotDate | datetime When this site information was captured (in UTC)|`2020-11-18T19:51:38Z`|1|true|
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted'|

## Notes

- The "Operation" property with the Full or Delta Mode can be utilized in the examples below
    1. To get a full snapshot, please ensure that start and end date are the SAME date
        - start: 1/1/2023
        - end: 1/1/2023
        - user recieves: One full snapshot of data from requested time period of 1/1/2023
    2. To get rows of a snapshot with only additions or deletions (delta mode), ensure start and data ranges are different in chronological order
        - start: 1/1/2023
        - end: 1/3/2023
        - user recieves: either rows added or deleted from this time period. If no rows are returned, there were no changes in data during this time period.