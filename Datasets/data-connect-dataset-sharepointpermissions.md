---
title: "BasicDataSet_v0.SharePointPermissions_v1 dataset"
description: "SharePoint sharing permissions information"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.SharePointPermissions_v1 dataset

### Description: 

SharePoint sharing permissions information, showing what is being shared and who are the sharing recipients (including internal users, external users, security groups and SharePoint groups). This includes sharing links and permissions granted to sites, lists, folders and files.

### Scenarios:

- Analytics for SharePoint permissions, covering all permissions granted by site, list, folder, or file.
- Understanding permissions granted to many users (information oversharing)
- Understanding permissions granted to users outside of the organization (external sharing)
- Understanding permissions granted to sensitive information (sensitivity labels, information barriers)

### Questions:

- How much file sharing is happening?
- How much of the sharing is external?
- Is sensitive information being shared?
- How many items are shared?
- How many items are shared externally?
- How many unique sharing recipients?
- What external domains are most used in sharing?
- Which type of sharing is mostly used internally?
- Top sites by number of shares
- File extensions that are most shared externally
- Graph of relationships between users and groups 

### Joining with other datasets: 

- This dataset can be joined with the Sites dataset by SiteId, to provide site details like the type of site.
- This dataset can be joined with the AAD Groups and SPO Groups dataset by e-mail or name, to provide group details like the number of members in a group.

### Notes:

- This dataset is available after 48 hours. For instance, you can query data for 01/01 starting in 01/03.
- This data is available for 21 days. For instance, the data for 01/01 is available from 01/03 to 01/22.
- To get a full dataset (all the objects), use a date filter with the same date for the start and end date.
- To get a delta dataset (objects that changed between two dates), use different dates for start and end in the date filter.

### Schema: 

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| ptenant | String | GUID that identifies the Office 365 tenant in AAD |  No | False |
| SiteId | String | GUID that identifies the SharePoint site (site collection) |  No | False |
| WebId | String | GUID that identifies the SharePoint web (subsite) |  No | False |
| ListId | String | GUID that identifies the SharePoint list |  No | False |
| ListItemId | int64 | Integer that identifies the SharePoint list item within the list |  No | False |
| UniqueId | String | GUID that identifies the SharePoint list item within the list |  No | False |
| ItemType | String | The type of item being shared (SiteAdmin, Web, List, Folder, File) |  No | False |
| ItemURL | String | URL to the item being shared |  No | False |
| FileExtension | String | File extension of the item being shared (optional, shows only if item type is file) |  No | False |
| RoleDefinition | String | Sharing role (Read, Contribute, Full Control) |  No | False |
| LinkId | String | GUID for the share Link (optional, won’t show if share has no link) |  No | False |
| ScopeId | String | GUID that identifies the SharePoint scope |  No | False |
| LinkScope | String | Scope of sharing link (specific people, anyone)(optional, won’t show if share has no link) |  No | False |
| SharedWithCount | Object[] | Object array with one entry for every type of sharing recipient. <br />Format: ``<ARRAY<STRUCT<`Type`: STRING, `Count`: INT64>>`` |  No | False |
| SharedWithCount, Type | String | Type of sharing recipient (Internal, External, SecurityGroup, SharePoint Group) |  No | False |
| SharedWithCount, Count | Int64 | Number of sharing recipients of this type |  No | False |
| SharedWith | Object[] | Object array with one entry for every sharing recipient. <br />Format: ``<ARRAY<STRUCT<`Type`: STRING, `Name`: STRING, `Email`: STRING, `AadObjectId`: STRING, `UPN`: STRING, `TypeV2`: STRING, `UserCount`: INT64, `UserLoginName`: STRING>>`` |  No | False |
| SharedWith, Type | String | Type of sharing recipient: Internal, External, SecurityGroup, SharePoint Group |  No | False |
| SharedWith, Name | String | Name of sharing recipient |  No | False |
| SharedWith, Email | String | Email of sharing recipient. Blank for SharePoint groups or special security groups |  No | False |
| SharedWith, AadObjectId | string | AAD Object Id of sharing recipient. Blank if this is not an AAD object. | No | False | 
| SharedWith, UPN | string | User Principal Name of sharing recipient | No | False | 
| SharedWith, TypeV2 | string | Type of sharing recipient: InternalUser, ExternalUser, B2BUser, SecurityGroup, SharePointGroup | No | False |
| SharedWith, UserCount | int64 | Unique user count for this sharing recipient. For groups, this is number of users in the group, including nested groups. For users, this is 1. Blank if group is empty or count is unavailable | No | False | 
| SharedWith, UserLoginName | string | Login name for this sharing recipient. If ending with _o, this should be expanded using the owners of the group, not the members | No | False | 
| TotalUserCount | int64 | Unique user count for this entire permission. Blank if count is zero or if count is unavailable | No | False | 
| ShareCreatedBy | String | The user or group that created the sharing link. <br />Format: ``STRUCT<`Type`: STRING, `Name`: STRING, `Email`: STRING, `UPN`: STRING>`` |  No | False |
| ShareCreatedBy, Type | String | Type of principal that created the sharing link: User |  No | False |
| ShareCreatedBy, Name | String | Name of user who created the sharing link |  No | False |
| ShareCreatedBy, Email | String | Email of user who created the sharing link |  No | False |
| ShareCreatedBy, UPN | string | User Principal Name of user who created the sharing link | No | False | 
| ShareCreatedTime | Date | The date and time when the share link was created |  No | False |
| ShareLastModifiedBy | String | The user or group that last modified the sharing link. <br />Format: ``<Struct <`Type`: String, `Name`: String, `Email`: String, `UPN`: STRING>`` |  No | False |
| ShareLastModifiedBy, Type | String | Type of principal that modified the sharing link: User |  No | False |
| ShareLastModifiedBy, Name | String | Name of user who created the modified link |  No | False |
| ShareLastModifiedBy, Email | String | Email of user who created the modified link |  No | False |
| ShareLastModifiedBy, UPN | string | User Principal Name of user who modified the sharing link | No | False | 
| ShareLastModifiedTime | Date | The date and time when the share was last modified |  No | False |
| ShareExpirationTime | Date | The date and time when the share link will expire |  No | False |
| SnapshotDate | Date | Data this data set was collected, in UTC | Yes | true |
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted') |  No | False |

### JSON Representation:

```json
{
    "ptenant": "9999990a-1285-4938-83eb-c3d131e0ef79",
    "SiteId": "9999990a-e1b8-4b44-b1a3-e6faa6096960",
    "WebId": "9999990a-aabf-49dc-957b-35a44c3cdf06",
    "ListId": "9999990a-e823-47f3-8350-b9f46be00425",
    "ItemType": "File",
    "ItemURL": "sites/ProjectBlue/Shared Documents/Overview.docx",
    "FileExtension": "docx",
    "RoleDefinition": "Contribute",
    "LinkId": "9999990a-056a-459d-ad66-920071a1ace2",
    "ScopeId": "9999990a-bd0e-4b1f-a67c-c849a08c26ed",
    "LinkScope": "Organization",
    "SharedWithCount": [
        {
            "Type": "Internal",
            "Count": 3
        }
    ],
    "SharedWith": [
        {
            "Type": "Internal",
            "Name": "Admin",
            "Email": "admin@contoso.onmicrosoft.com",
            "TypeV2": "InternalUser",
            "UPN": "admin@contoso.onmicrosoft.com",
            "AADObjectId": "9999990a-16c9-4b04-96de-be6e856e5333",
            "UserCount": 1
        },
        {
            "Type": "Internal",
            "Name": "Demo User1",
            "Email": "demouser1@contoso.onmicrosoft.com",
            "TypeV2": "InternalUser",
            "UPN": "demouser1@contoso.onmicrosoft.com",
            "AADObjectId": "9999990a-6b74-4001-9890-ea0ce977ec7e",
            "UserCount": 1
        },
        {
            "Type": "Internal",
            "Name": "Demo User2",
            "Email": "DemoUser2@contoso.onmicrosoft.com",
            "TypeV2": "InternalUser",
            "UPN": "demouser2@contoso.onmicrosoft.com",
            "AADObjectId": "9999990a-f05a-44ed-bd41-58e18227503c",
            "UserCount": 1
        }
    ],
    "TotalUserCount": 3,
    "ShareCreatedBy": {
        "Type": "User",
        "Name": "Admin",
        "Email": "admin@contoso.onmicrosoft.com",
        "UPN": "admin@contoso.onmicrosoft.com"
    },
    "ShareCreatedTime": "2023-11-09T23:31:14Z",
    "ShareLastModifiedBy": {
        "Type": "User",
        "Name": "Admin",
        "Email": "admin@contoso.onmicrosoft.com",
        "UPN": "admin@contoso.onmicrosoft.com"
    },
    "ShareLastModifiedTime": "2023-11-09T23:31:14Z",
    "Operation": "Full",
    "SnapshotDate": "2024-05-03T00:00:00Z"
}
```

### Sample datasets:

```json
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"ec508b4b-6e54-401c-826f-775950f03353","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","ItemURL":"personal/johnd_contoso_onmicrosoft_com/Documents/SampleFolder/raw/tenantid-04f9bbc5-c9b8-48ff-b39e-fea19fb67596_8b49b8db-1e86-4c97-9d8f-c725107e326d_VM104094_2021-08-13-40-45.tsv","RoleDefinition":"Review","LinkId":"9680ffed-cb58-4562-b918-76a3fa05af08","ScopeId":"fc7b0de2-9cb9-4083-b0b0-ee571691e1c5","LinkScope":"Anyone","SharedWithCount":[{"Type":"SecurityGroup","Count":1}],"SharedWith":[{"Type":"SecurityGroup","Name":"ContosoApprovers","Email":"ContosoApprovers@contoso.onmicrosoft.com"}],"FileExtension":"tsv"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Folder","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/ContosoExternalShareOnly","RoleDefinition":"Edit","ScopeId":"48280341-ab11-4e7f-ae01-dba1d9120e95","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingMembers"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder2-OrganizationLinks-ReadOnlyShares/File2-ReadOnly-User1.txt","RoleDefinition":"Edit","ScopeId":"9180b464-ea4c-4b27-92ef-ddb30cfbc43d","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingMembers"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder1-OrganizationLinks-Edit/SharedWithUser2.txt.txt","RoleDefinition":"Read","ScopeId":"420997ed-d028-4f02-a86d-e1101d9420cd","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingVisitors"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/ContosoTeamSite/ConfidentialDocs/Presentations/Architechture.pptx","RoleDefinition":"Contribute","LinkId":"ddec67de-0478-439e-9a67-b628ad4cce39","ScopeId":"2d92bb2c-a743-4693-ba87-aaf0c2410750","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"External","Count":5},{"Type":"SecurityGroup","Count":1},{"Type":"Internal","Count":1}],"SharedWith":[{"Type":"External","Name":"John.Doe","Email":"John.Doe@gmail.com"},{"Type":"External","Name":"HarryDoe","Email":"harry.doe@external.com"},{"Type":"External","Name":"janedoe","Email":"janedoe@external.com"},{"Type":"External","Name":"JohnDoe","Email":"johnd@external.com"},{"Type":"SecurityGroup","Name":"Contoso-Crew-ExternalMembers","Email":"Contoso-Crew-External@contoso.onmicrosoft.com"},{"Type":"External","Name":"JohnDoe","Email":"johnd54@hotmail.com"},{"Type":"Internal","Name":"JaneDoe","Email":"admin@contoso.onmicrosoft.com"}],"FileExtension":"pptx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/ContosoTeamSite/SharedDocuments/ShortContosoDemo.mp4","RoleDefinition":"Contribute","LinkId":"0b0d359a-45b7-488b-84c8-baa3be2a1933","ScopeId":"6cb4884c-5214-4fa2-b3b6-89074eb44206","LinkScope":"SpecificPeople","SharedWithCount":[],"SharedWith":[],"FileExtension":"mp4"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Web","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"1b178690-a914-48fd-b205-e77224ce18f9","WebId":"f5f47732-256b-4d3a-8143-588c1f9d9c56","RoleDefinition":"Contribute","ScopeId":"87cb7759-2069-4a08-a231-50087c0c5237","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"Members"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"ec508b4b-6e54-401c-826f-775950f03353","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","ItemURL":"personal/johnd_contoso_onmicrosoft_com/Documents/SampleFolder/raw/tenantid-04f9bbc5-c9b8-48ff-b39e-fea19fb67596_8b49b8db-1e86-4c97-9d8f-c725107e326d_VM104094_2021-08-13-14-15.tsv","RoleDefinition":"Contribute","LinkId":"a8e2a703-5181-4db1-a5de-ba1b59182de6","ScopeId":"1e68d614-2374-4107-9d60-ae4106565e8a","LinkScope":"Anyone","SharedWithCount":[],"SharedWith":[],"FileExtension":"tsv"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder2-OrganizationLinks-ReadOnlyShares/File2-ReadOnly-User1.txt","RoleDefinition":"Read","LinkId":"0e922d11-0ece-4378-903b-228ef1402337","ScopeId":"9180b464-ea4c-4b27-92ef-ddb30cfbc43d","LinkScope":"Organization","SharedWithCount":[{"Type":"Internal","Count":1}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser1","Email":"ContosoUser1@contoso.onmicrosoft.com"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/SharingFromTeams/Shared-External-Internal-Edit-SpecificPeople.docx","RoleDefinition":"Contribute","LinkId":"d8880dbc-fdf5-42bd-a25e-f963ed65132f","ScopeId":"11b8c535-41f1-45a0-b6d8-b85fa023e9d8","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"External","Count":1}],"SharedWith":[{"Type":"External","Name":"harry.doe@external.com","Email":"harry.doe@external.com"}],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/SharingFromTeams/FirstExternalShareFromTeams.docx","RoleDefinition":"Contribute","LinkId":"1764680e-c9f5-4024-813a-c7019a207df0","ScopeId":"f4f77c7c-dfe2-4080-b477-f76eb97d094f","LinkScope":"Anyone","SharedWithCount":[],"SharedWith":[],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/SharingFromTeams/FirstExternalShareFromTeams.docx","RoleDefinition":"Read","ScopeId":"f4f77c7c-dfe2-4080-b477-f76eb97d094f","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingVisitors"}],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/ContosoTeamSite/SharedDocuments/ContosoDemo4min.mp4","RoleDefinition":"FullControl","ScopeId":"9155285e-5b50-49a0-99f4-e389556f9f04","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTeamSiteOwners"}],"FileExtension":"mp4"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/ContosoTeamSite/ConfidentialDocs/Documents/MicrosoftGraphDataConnectfrequentlyaskedquestions.docx","RoleDefinition":"Read","ScopeId":"6175e30d-fd64-4c68-ba1c-f927657f8caf","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTeamSiteVisitors"}],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/ExternalSharing2/FileOnlyExternalShare.txt","RoleDefinition":"Edit","ScopeId":"f5392963-101a-4527-bef4-44e1d4c05c33","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingMembers"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder1-OrganizationLinks-Edit/SharedWithUser2.txt.txt","RoleDefinition":"Contribute","LinkId":"ebd031b2-dbe8-43a5-9771-3724e331633a","ScopeId":"420997ed-d028-4f02-a86d-e1101d9420cd","LinkScope":"Organization","SharedWithCount":[{"Type":"Internal","Count":1}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser2","Email":"ContosoUser2@contoso.onmicrosoft.com"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder1-OrganizationLinks-Edit/SharedWithUser1AndUser2.txt","RoleDefinition":"Contribute","LinkId":"c288a11c-1435-44b0-ac22-d21aee628628","ScopeId":"e7bfcf87-740e-433a-8af5-f9e70455555f","LinkScope":"Organization","SharedWithCount":[{"Type":"Internal","Count":2}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser1","Email":"ContosoUser1@contoso.onmicrosoft.com"},{"Type":"Internal","Name":"ContosoUser2","Email":"ContosoUser2@contoso.onmicrosoft.com"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/ContosoTeamSite/SharedDocuments/ContosoDemo4min.mp4","RoleDefinition":"Review","LinkId":"3db1083c-2cf3-423c-b455-4fe4e713ff3d","ScopeId":"9155285e-5b50-49a0-99f4-e389556f9f04","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"SecurityGroup","Count":1}],"SharedWith":[{"Type":"SecurityGroup","Name":"ContosoApprovers","Email":"ContosoApprovers@contoso.onmicrosoft.com"}],"FileExtension":"mp4"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/ContosoExternalShareOnly/johnd-External-Edit-SpecificPeople.txt","RoleDefinition":"Contribute","LinkId":"84718f17-3b99-44d9-80de-f9118b877794","ScopeId":"184d995c-b546-482f-9963-777a1991fbfb","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"External","Count":1}],"SharedWith":[{"Type":"External","Name":"harry.doe@external.com","Email":"harry.doe@external.com"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/SharingFromTeams/Shared-External-Internal-Edit-SpecificPeople.docx","RoleDefinition":"Read","ScopeId":"11b8c535-41f1-45a0-b6d8-b85fa023e9d8","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingVisitors"}],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Folder","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder3-AnyoneLinks-ReadOnly","RoleDefinition":"FullControl","ScopeId":"f5633224-7af7-4358-9edc-427661fd86b5","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingOwners"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Folder","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder2-OrganizationLinks-ReadOnlyShares","RoleDefinition":"FullControl","ScopeId":"bbd766bf-53f1-46be-9e4c-1736ef32168e","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingOwners"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Folder","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/ContosoExternalShareOnly","RoleDefinition":"Contribute","LinkId":"8fee16fc-3a0d-4902-ab4d-42c21b580c40","ScopeId":"48280341-ab11-4e7f-ae01-dba1d9120e95","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"External","Count":1}],"SharedWith":[{"Type":"External","Name":"John.Doe@gmail.com","Email":"John.Doe@gmail.com"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"ec508b4b-6e54-401c-826f-775950f03353","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","ItemURL":"personal/johnd_contoso_onmicrosoft_com/Documents/Book.xlsx","RoleDefinition":"Read","LinkId":"bbca4225-9e4f-45ac-b437-0fc1935543cb","ScopeId":"70a9170b-c973-4350-bb9f-ffe7ba34e8d9","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"Internal","Count":1}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser2","Email":"ContosoUser2@contoso.onmicrosoft.com"}],"FileExtension":"xlsx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/ExternalShare2/Share-Edit.docx","RoleDefinition":"Read","ScopeId":"2f119508-8243-4d7e-a306-01043b62182e","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingVisitors"}],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Folder","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder2-OrganizationLinks-ReadOnlyShares","RoleDefinition":"Read","LinkId":"55e95a30-7658-42b3-8aa9-1fba62c6996c","ScopeId":"bbd766bf-53f1-46be-9e4c-1736ef32168e","LinkScope":"Organization","SharedWithCount":[{"Type":"Internal","Count":1}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser2","Email":"ContosoUser2@contoso.onmicrosoft.com"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Web","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"fa74c2e0-a6e5-4869-8116-2b85222a6d20","WebId":"8adaff9f-7768-4586-a62f-3adb88a74f91","ItemURL":"sites/Contoso-CommunicationSite","RoleDefinition":"FullControl","ScopeId":"a6245a8c-9092-4ff1-bd03-deb93cd0198a","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"Contoso-CommunicationSiteOwners"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"ec508b4b-6e54-401c-826f-775950f03353","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","ItemURL":"personal/johnd_contoso_onmicrosoft_com/Documents/SampleFolder/raw/tenantid-04f9bbc5-c9b8-48ff-b39e-fea19fb67596_8b49b8db-1e86-4c97-9d8f-c725107e326d_VM104094_2021-08-13-14-15.tsv","RoleDefinition":"Contribute","LinkId":"7b623b4e-868e-40dc-85ed-f8a097e43b58","ScopeId":"1e68d614-2374-4107-9d60-ae4106565e8a","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"Internal","Count":1}],"SharedWith":[{"Type":"Internal","Name":"JaneDoe","Email":"admin@contoso.onmicrosoft.com"}],"FileExtension":"tsv"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/ContosoTeamSite/ConfidentialDocs/Pricing/ContosoPricing.xlsx","RoleDefinition":"Contribute","LinkId":"c84f75b3-2fb9-45cc-85ca-845a105f6275","ScopeId":"a4d939ef-48b2-47cf-8b05-ad3e828c872f","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"SecurityGroup","Count":1},{"Type":"External","Count":2}],"SharedWith":[{"Type":"External","Name":"John.Doe","Email":"John.Doe@gmail.com"},{"Type":"SecurityGroup","Name":"Contoso-Crew-ExternalMembers","Email":"Contoso-Crew-External@contoso.onmicrosoft.com"},{"Type":"External","Name":"JohnDoe","Email":"johnd54@hotmail.com"}],"FileExtension":"xlsx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/SharingFromTeams/FirstExternalShareFromTeams.docx","RoleDefinition":"Edit","ScopeId":"f4f77c7c-dfe2-4080-b477-f76eb97d094f","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingMembers"}],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Folder","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder2-OrganizationLinks-ReadOnlyShares","RoleDefinition":"Read","ScopeId":"bbd766bf-53f1-46be-9e4c-1736ef32168e","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingVisitors"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder1-OrganizationLinks-Edit/SharedWithUser1AndUser2.txt","RoleDefinition":"Contribute","LinkId":"e2acbe31-487d-460e-886c-366039ede3a8","ScopeId":"e7bfcf87-740e-433a-8af5-f9e70455555f","LinkScope":"Organization","SharedWithCount":[{"Type":"Internal","Count":2}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser1","Email":"ContosoUser1@contoso.onmicrosoft.com"},{"Type":"Internal","Name":"ContosoUser2","Email":"ContosoUser2@contoso.onmicrosoft.com"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/ExternalSharing2/FileOnlyExternalShare.txt","RoleDefinition":"Review","LinkId":"1191dce9-0d19-4a63-84df-6a6b9a5d9eb4","ScopeId":"f5392963-101a-4527-bef4-44e1d4c05c33","LinkScope":"Anyone","SharedWithCount":[],"SharedWith":[],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/ContosoTeamSite/ConfidentialDocs/Documents/MicrosoftGraphDataConnectfrequentlyaskedquestions.docx","RoleDefinition":"Contribute","LinkId":"11cdc173-b5c7-4949-966d-4af4fada7ee3","ScopeId":"6175e30d-fd64-4c68-ba1c-f927657f8caf","LinkScope":"SpecificPeople","SharedWithCount":[{"Type":"External","Count":1},{"Type":"SecurityGroup","Count":1}],"SharedWith":[{"Type":"SecurityGroup","Name":"Contoso-Crew-ExternalMembers","Email":"Contoso-Crew-External@contoso.onmicrosoft.com"},{"Type":"External","Name":"JohnDoe","Email":"johnd54@hotmail.com"}],"FileExtension":"docx"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Folder","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"ec508b4b-6e54-401c-826f-775950f03353","WebId":"efe47d8c-6672-420b-80f9-fb884195bcbe","ItemURL":"personal/johnd_contoso_onmicrosoft_com/Documents/SampleFolder","RoleDefinition":"Contribute","LinkId":"b7eb6706-84f3-45ad-9531-8726ba8c2e29","ScopeId":"f441650c-b8c0-4f0a-87a7-4fe3f088d52d","LinkScope":"Anyone","SharedWithCount":[{"Type":"Internal","Count":2}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser1","Email":"ContosoUser1@contoso.onmicrosoft.com"},{"Type":"Internal","Name":"ContosoUser2","Email":"ContosoUser2@contoso.onmicrosoft.com"}]}
{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"File","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"d6fc2ce9-53c1-45e4-83ff-34ee770285d4","WebId":"e17df665-1a28-4c1d-9b81-f6c7e96df8ac","ItemURL":"sites/contosotesting/contosotesting_doclibrary1/Folder2-OrganizationLinks-ReadOnlyShares/File2-ReadOnly-User1.txt","RoleDefinition":"Read","ScopeId":"9180b464-ea4c-4b27-92ef-ddb30cfbc43d","SharedWithCount":[{"Type":"SharePointGroup","Count":1}],"SharedWith":[{"Type":"SharePointGroup","Name":"ContosoTestingVisitors"}],"FileExtension":"txt"}{"ptenant":"4b4741c0-4d67-4c21-842a-abcea48840d5","ItemType":"Web","SnapshotDate":"2021-10-20T00:00:00Z","SiteId":"47cb73d4-8cd8-4b19-b6a4-7905c2966cb3","WebId":"fc277af4-d28f-4d35-8637-421b64420cb5","ItemURL":"personal/ContosoUser1_contoso_onmicrosoft_com","RoleDefinition":"FullControl","ScopeId":"a2037a73-2bcc-4b34-bee7-9fbdbcab3c41","SharedWithCount":[{"Type":"Internal","Count":1}],"SharedWith":[{"Type":"Internal","Name":"ContosoUser1","Email":"ContosoUser1@contoso.onmicrosoft.com"}]}
```
