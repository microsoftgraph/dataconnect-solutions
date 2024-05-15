---
title: "BasicDataSet_v0.SharePointGroups_v1 dataset"
description: "SharePoint group information, including details about group members"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.SharePointGroups_v1 dataset

### Description: 

SharePoint group information, including details about group members. Typical examples of SharePoint Groups are Site Owners, Site Members and Site Visitors, which are defined at the site level to manage access to a site. This does not include Azure Active Directory (AAD) Group information is provided as a separate dataset.

### Scenarios:

- Analytics for SharePoint groups and SharePoint group members
- Full list of “Shared with” users with groups expanded (when combined with the AAD Groups data set)
- Graph of users that shared documents with groups expanded (when combined with the AAD Groups data set)

### Joining with other datasets: 

- This dataset can be joined with the Sharing Permissions dataset to provide information of the total number of users a document has been shared with. 
- This dataset can be joined with the AAD Groups dataset to provide a complete view of all groups in SharePoint and also a fully expanded members list.

### Definitions:

- SharePoint groups – Groups created in SharePoint itself (not to be confused with AAD groups). 
- AAD groups – Groups created in Azure Active Directory, which can be used with many applications, including SharePoint. 

### Notes:

- This dataset is available after 48 hours. For instance, you can query data for 01/01 starting in 01/03.
- This data is available for 21 days. For instance, the data for 01/01 is available from 01/03 to 01/22.

### Schema:

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| ptenant | string | Id of the tenant | No | False |
| SiteId | string | Id of the site where the group resides | No | False |
| GroupId | int64 | id of the group, unique within SPSite | No | False |
| GroupLinkId | int64 | Id of the sharing link associated with this group, if it was created for a sharing link. The id is all zeros if the group is not related to a sharing link | No | False |
| GroupType | string | Type: SharePointGroup | No | False |
| DisplayName | string | Name of the group | No | False |
| Owner | Object | Group owner. *Format:* `STRUCT<AadObjectId:STRING,Name:STRING,Email:STRING,TypeV2:STRING>` | No | False |
| Owner, Type | string | Type of group owner: User, SharePointGroup, SecurityGroup | No | False |
| Owner, AadObjectId | string | AAD Object Id of the group owner | No | False |
| Owner, Name | string | Name of the group owner | No | False |
| Owner, Email | string | Email of the group owner | No | False |
| Owner, TypeV2 | string | Type of group owner: InternalUser, ExternalUser, B2BUser, SecurityGroup, SharePointGroup | No | False |
| Members | Object[] | Array of group members. *Format:* `ARRAY<STRUCT<Type:STRING, AadObjectId:STRING, Name:STRING, Email:STRING, TypeV2:STRING>>` | No | False |
| Members, Type | string | Type of group member: User, SharePointGroup, SecurityGroup | No | False |
| Members, AadObjectId | string | AAD Object Id of the group member | No | False |
| Members, Name | string | Name of the group member | No | False |
| Members, Email | string | Email of the group member | No | False |
| Members, TypeV2 | string | Type of group member: InternalUser, ExternalUser, B2BUser, SecurityGroup, SharePointGroup | No | False |
| SnapshotDate | datetime | Date this data set was generated | Yes | True |
| Operation | String | Extraction mode of this row. Gives info about row extracted with full mode ('Full') or delta mode ('Created', 'Updated' or 'Deleted') | No | False |

### JSON Representation:

```json
{
    "ptenant": "3adad419-abdd-493e-a3ea-432bd7748cb3",
    "SiteId": "b1565b3a-94d0-4877-953f-55929d99c372",
    "GroupId": 5,
    "GroupLinkId": "00000000-0000-0000-0000-000000000000",
    "GroupType": "SharePointGroup",
    "DisplayName": "Office 365 Adoption Members",
    "Owner": {
            "Type": "User",
            "AadObjectId": "9999bd2b-16c9-4b04-96de-be6e856e5333",
            "Name": "User 1",
            "Email": "User1@M365x16144201.onmicrosoft.com",
            "TypeV2": "InternalUser"
    },
    "Members": [
        {
            "Type": "SecurityGroup",
            "Name": "Everyone except external users",
            "TypeV2": "SecurityGroup"
        },
        {
            "Type": "SecurityGroup",
            "AadObjectId": "120673cd-5672-4d1c-8573-e390abbfc52d",
            "Name": "Office 365 Adoption Members",
            "Email": "office365adoption@M365x16144201.onmicrosoft.com",
            "Type": "SecurityGroup"
        }
    ],
    "Operation": "Full",
    "SnapshotDate": "2023-08-20T00:00:00Z"
}
```

### Sample

```json
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","GroupId":3,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Contoso Team Site Owners","Owner":{"Type":"SharePointGroup","Name":"Contoso Team Site Owners","TypeV2":"SharePointGroup"},"Members":[{"Type":"SecurityGroup","AadObjectId":"19127d09-5399-4045-816b-cd3bc1528043","Name":"Contoso Team Site Owners","Email":"ContosoTeamSite@contoso.onmicrosoft.com","TypeV2":"SecurityGroup"},{"Type":"User","AadObjectId":"12345678-9981-46e7-9ee2-cedccacc0e94","Name":"Jane Doe","Email":"admin@contoso.onmicrosoft.com","TypeV2":"InternalUser"}],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"dcc16dfa-dac2-40f1-a052-18de723245d1","GroupId":28,"GroupLinkId":"d996011d-18d3-4f4e-a57e-91eda36f310e","GroupType":"SharePointGroup","DisplayName":"SharingLinks.61189f32-f808-4468-9bf4-16840ec8c3a8.OrganizationEdit.d996011d-18d3-4f4e-a57e-91eda36f310e","Description":"This group is for OrganizationEdit sharing links on item 'ConfidentialDocs/Presentations/MGDC-Architechture.pptx'","Owner":{"Type":"User","Name":"System Account","TypeV2":"InternalUser"},"Members":[{"Type":"User","AadObjectId":"d182ea07-4729-4982-b8ab-63702862ef59","Name":"John Doe","Email":"johnd@contoso.onmicrosoft.com","TypeV2":"InternalUser"}],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"1fe27988-c5c9-4bcb-9d60-8cce1a8487eb","GroupId":5,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 5 Members","Owner":{"Type":"SharePointGroup","Name":"Test 5 Owners","TypeV2":"SharePointGroup"},"Members":[],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"1fe27988-c5c9-4bcb-9d60-8cce1a8487eb","GroupId":3,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 5 Owners","Owner":{"Type":"SharePointGroup","Name":"Test 5 Owners,"TypeV2":"SharePointGroup""},"Members":[{"Type":"User","Name":"System Account"}],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"1fe27988-c5c9-4bcb-9d60-8cce1a8487eb","GroupId":4,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 5 Visitors","Owner":{"Type":"SharePointGroup","Name":"Test 5 Owners","TypeV2":"SharePointGroup"},"Members":[],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"330a9220-d2f5-4fb0-b098-3a66cd67b6cc","GroupId":3,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 30 Owners","Owner":{"Type":"SharePointGroup","Name":"Test 30 Owners","TypeV2":"SharePointGroup"},"Members":[{"Type":"User","Name":"System Account","TypeV2":"InternalUser"}],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"330a9220-d2f5-4fb0-b098-3a66cd67b6cc","GroupId":5,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 30 Members","Owner":{"Type":"SharePointGroup","Name":"Test 30 Owners","TypeV2":"SharePointGroup"},"Members":[],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"330a9220-d2f5-4fb0-b098-3a66cd67b6cc","GroupId":4,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 30 Visitors","Owner":{"Type":"SharePointGroup","Name":"Test 30 Owners,"TypeV2":"SharePointGroup""},"Members":[],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"faf52046-1c55-453c-b28d-1528ea70e217","GroupId":6,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 61 Members","Owner":{"Type":"SharePointGroup","Name":"Test 61 Owners","TypeV2":"SharePointGroup"},"Members":[],"SnapshotDate":"2022-06-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","SiteId":"faf52046-1c55-453c-b28d-1528ea70e217","GroupId":4,"GroupLinkId":"00000000-0000-0000-0000-000000000000","GroupType":"SharePointGroup","DisplayName":"Test 61 Owners","Owner":{"Type":"SharePointGroup","Name":"Test 61 Owners","TypeV2":"SharePointGroup"},"Members":[{"Type":"User","Name":"System Account","TypeV2":"InternalUser"}],"SnapshotDate":"2022-06-02T00:00:00Z"}
```
