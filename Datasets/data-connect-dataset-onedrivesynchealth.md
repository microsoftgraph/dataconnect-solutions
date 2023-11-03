---
title: "BasicDataSet_v0.OneDriveSyncHealth_v1 dataset"
description: "The OneDrive Sync Health dataset includes information on devices running OneDrive for Business"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.OneDriveSyncHealth_v1 dataset

### Description: 

The OneDrive Sync Health dataset includes information on devices running OneDrive for Business. This includes one object for every report-enabled OneDrive Sync client in the tenant. Reports include Sync app information, user details, error details, and relevant timestamps.

**IMPORTANT NOTE**: This dataset is not currently available publicly. It is coming soon.  The details provided here are for informational purposes only.

### Scenarios:

- Administrators need to know how many devices are online and syncing across the tenant.
- Administrators want to monitor Folder Backup (KFM) rollout across the organization.
- Administrators need to understand if users’ synced content is up-to-date with the cloud.
- Users can experience sync errors affecting productivity and collaboration. Administrators want to identify devices that are both experiencing errors and not staying up-to-date and want to drill down into report details to understand the users’ states.

### Questions:

- How many devices are healthy?
- How many devices have opted in for Folder Backup?
- Which Folders are most selected for Folder Backup?
- What is the breakdown of unhealthy devices by OS version?
- What is the breakdown of unhealthy devices by OneDrive Sync client version?
- Which errors are making the devices unhealthy?
- What errors are only shown for a specific OS version?
- What errors are only shown for a specific OneDrive Sync client version?
- Which devices are showing a specific error?
- Is the device for user X reporting as healthy?

### Joining with other datasets:

 - This dataset can be joined with the Sites dataset (by site owner e-mail, filtering for OneDrive template id 21) to provide information about the OneDrive associated with this user.

### Definitions:

- Device – A computer (PC or Mac) running the OneDrive Sync client to upload and download files to the cloud.
- KFM – Known Folder Move. A feature of the OneDrive Sync client that makes sure certain local folders are backed up to OneDrive. Also known as Folder Backup. Known folders include Documents, Pictures, and Desktop.

### Notes:

- This dataset is available after 48 hours. For instance, you can query data for 01/01 starting in 01/03.
- This data is available for 21 days. For instance, the data for 01/01 is available from 01/03 to 01/22.
- This dataset captures daily actions and does not support Deltas, since the data is unique for each day.

### Schema:

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| ptenant | string | Id of the tenant | No | False |
| OneDriveDeviceId | string | Unique OneDrive Sync internal identifier | No | False |
| User | object | User | No | False |
| User, Name | string | Name of the user | No | False |
| User, Email | string | Email of the user | No | False |
| DeviceName | string | Name of the device | No | False |
| SyncAppVersion | string | OneDrive Sync app version | No | False |
| SyncAppUpdateRing | int | User Ring (4 – Insiders, 5 – Production, 0 – Deferred) | No | False |
| OSName | string | Operating System (Windows or Mac) | No | False |
| OSVersion | string | OS Version number | No | False |
| SyncAppBuildType | string | OS Build Type. For Windows, this is empty. For Mac, this can be Mac App Store or Mac Standalone build. | No | False |
| KFMOptInWithWizardGPOEnabled | bool | GPO-enabled state for showing KFM Wizard to users | No | False |
| KFMSilentOptInGPOEnabled | bool | GPO-enabled state for performing silent opt-in to KFM | No | False |
| KFMEnabledFolders | array | Folders enabled for KFM (0 - Not set, 1 - Documents, 2 - Pictures, 3 - Desktop) | No | False |
| KFMEnabledFolderCount | int | Number of folders enabled for KFM | No | False |
| LastSyncedTimestampUTC | datetime | Date and time when the client last reported up to date (in UTC) | No | False |
| LastStatusReportedTimestampUTC | datetime | Date and time when the tenant report was last generated (in UTC) | No | False |
| TotalErrorCount | int | Total count of all errors the user is experiencing | No | False |
| ErrorDetails | array | Error details for this device | No | False |
| ErrorDetails, OneDriveDeviceId | string | Unique OneDrive Sync internal identifier | No | False |
| ErrorDetails, SyncErrorId | int | Error type, when shown in system tray icon (34 means unused) | No | False |
| ErrorDetails, TaskDialogId1 | int | Error type, when shown in task dialog (0 means unused) | No | False |
| ErrorDetails, TaskDialogId2 | int | Error type, when shown in task dialog, legacy (256 means unused) | No | False |
| ErrorDetails, ErrorTitle | string | Error title shown to user (localized) | No | False |
| ErrorDetails, ErrorDescription | string | Error description shown to user (localized) | No | False |
| ErrorDetails, ErrorCount | int | Number of errors of this type | No | False |
| SnapshotDate | datetime | When this site information was captured (in UTC) | Yes | True |

### JSON Representation:

```json
{
    "ptenant": "a60f3940-2ff4-4856-a191-17994f26c581",
    "OneDriveDeviceId": "cd64acbe-bd10-48bd-ae04-4a443f5eaad6",
    "User": {
        "Name": "Kat Larson",
        "Email": "KatL@contoso.com"
    },
    "DeviceName": "KAT-PC",
    "SyncAppVersion": "23.199.0924.0001",
    "SyncAppUpdateRing": 5,
    "OSName": "Windows",
    "OSVersion": "10.0.19042",
    "SyncAppBuildType": "",
    "KFMOptInWithWizardGPOEnabled": false,
    "KFMSilentOptInGPOEnabled": false,
    "KFMEnabledFolders": [
        1,
        2,
        3
    ],
    "KFMEnabledFolderCount": 3,
    "LastSyncedTimestampUTC": "2023-09-27T02:12:50Z",
    "LastStatusReportedTimestampUTC": "2023-10-03T11:00:43Z",
    "TotalErrorCount": 1,
    "ErrorDetails": [
        {
            "OneDriveDeviceId": "cd64acbe-bd10-48bd-ae04-4a443f5eaad6",
            "SyncErrorId": 53,
            "TaskDialogId1": 0,
            "TaskDialogId2": 256,
            "ErrorTitle": "There isn't enough space on this computer to sync this file.",
            "ErrorDescription": "Free up space by removing apps and files you don't need anymore. We'll automatically sync this file when there's enough space.",
            "ErrorCount": 1
        }
    ],
}
```

### Sample

```json
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"848784b3-ba8e-425a-9e4e-a8635c061fe5","User":{"Name":"Robel Muris","Email":"RobelMu@contoso.com"},"DeviceName":"ROBE15432","SyncAppVersion":"23.201.0926.0001","SyncAppUpdateRing":4,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[3,1],"KFMFolderCount":2,"LastSyncedTimestampUTC":"2023-09-29T20:21:10Z","LastStatusReportedTimestamp":"2023-10-02T01:02:19Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"12df4e4c-17a6-4046-a6dd-7b226bdbbac1","User":{"Name":"Claudia Bergqvist","Email":"ClaudBe@contoso.com"},"DeviceName":"CLAU136780","SyncAppVersion":"23.199.0924.0001","SyncAppUpdateRing":5,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":24,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[3,1],"KFMFolderCount":2,"LastSyncedTimestampUTC":"2023-09-30T05:08:45Z","LastStatusReportedTimestamp":"2023-10-02T00:26:37Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"2d192a53-7bbd-481c-af39-e28a619a082b","User":{"Name":"Payton Young","Email":"PaytoYo@contoso.com"},"DeviceName":"PAYT299476","SyncAppVersion":"23.201.0926.0001","SyncAppUpdateRing":4,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":8,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[3],"KFMFolderCount":1,"LastSyncedTimestampUTC":"2023-09-28T08:04:14Z","LastStatusReportedTimestamp":"2023-10-02T13:13:40Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"7b805f02-3adc-4e9f-a66a-96a4b30e3654","User":{"Name":"Zachary O'Sullivan","Email":"ZachaO@contoso.com"},"DeviceName":"ZACH338329","SyncAppVersion":"23.199.0924.0001","SyncAppUpdateRing":5,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":0,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[],"KFMFolderCount":0,"LastSyncedTimestampUTC":"2023-09-27T02:12:50Z","LastStatusReportedTimestamp":"2023-10-03T11:00:43Z","TotalErrorCount":1,"ErrorDetails":[{"OneDriveDeviceId":"7b805f02-3adc-4e9f-a66a-96a4b30e3654","SyncErrorId":53,"taskDialogId1":0,"taskDialogId2":256,"ErrorTitle":"There isn't enough space on this computer to sync this file.","ErrorDescription":"Free up space by removing apps and files you don't need anymore. We'll automatically sync this file when there's enough space.","ErrorCount":1}],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"4725b0f1-b70c-4f2e-bdc6-9f653d4abe10","User":{"Name":"Otto Lynge","Email":"OttoLy@contoso.com"},"DeviceName":"OTTO318996","SyncAppVersion":"23.199.0924.0001","SyncAppUpdateRing":5,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":0,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[],"KFMFolderCount":0,"LastSyncedTimestampUTC":"2023-09-26T23:16:42Z","LastStatusReportedTimestamp":"2023-10-03T13:43:36Z","TotalErrorCount":1,"ErrorDetails":[{"OneDriveDeviceId":"4725b0f1-b70c-4f2e-bdc6-9f653d4abe10","SyncErrorId":121,"taskDialogId1":0,"taskDialogId2":256,"ErrorTitle":"This item name or type isn't allowed.","ErrorDescription":"Please rename the item or remove it from your OneDrive.","ErrorCount":1}],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"26cbc593-eb53-4c5b-8ea8-dec7d8b68e67","User":{"Name":"Dominik Schmitt","Email":"DominSc@contoso.com"},"DeviceName":"DOMI143272","SyncAppVersion":"23.201.0926.0001","SyncAppUpdateRing":4,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":16,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[1],"KFMFolderCount":1,"LastSyncedTimestampUTC":"2023-09-29T02:12:42Z","LastStatusReportedTimestamp":"2023-10-02T05:10:28Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"1d9d127a-f8d7-4540-91d9-e3186832ce42","User":{"Name":"Sanna Nykanen","Email":"SannaNy@contoso.com"},"DeviceName":"SANN58934","SyncAppVersion":"23.199.0924.0001","SyncAppUpdateRing":5,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":24,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[3,1],"KFMFolderCount":2,"LastSyncedTimestampUTC":"2023-09-30T05:08:45Z","LastStatusReportedTimestamp":"2023-10-02T00:26:37Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"68e0c452-2ca1-42c5-9773-a43846550af0","User":{"Name":"Jill Barker","Email":"JillBa@contoso.com"},"DeviceName":"JILL450396","SyncAppVersion":"23.201.0926.0001","SyncAppUpdateRing":4,"OSName":"Mac","OSVersion":"11.6.1","SyncAppBuildType":"Standalone","KFMState":1,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[],"KFMFolderCount":0,"LastSyncedTimestampUTC":"2023-10-03T00:01:43Z","LastStatusReportedTimestamp":"2023-10-03T02:02:44Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"d09c1417-8c05-4b1f-9915-418ce84d7cd4","User":{"Name":"Hugh Wallace","Email":"HughWa@contoso.com"},"DeviceName":"HUGH534901","SyncAppVersion":"23.201.0926.0001","SyncAppUpdateRing":4,"OSName":"Mac","OSVersion":"11.6.1","SyncAppBuildType":"Standalone","KFMState":1,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[],"KFMFolderCount":0,"LastSyncedTimestampUTC":"2023-10-01T11:44:46Z","LastStatusReportedTimestamp":"2023-10-02T05:32:56Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"59f4c131-fa4b-45d0-abc2-c82adceba101","User":{"Name":"Laura Harper","Email":"LauraHa@contoso.com"},"DeviceName":"LAUR758444","SyncAppVersion":"23.201.0926.0001","SyncAppUpdateRing":4,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":16,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[1],"KFMFolderCount":1,"LastSyncedTimestampUTC":"2023-09-28T21:05:49Z","LastStatusReportedTimestamp":"2023-10-02T07:02:55Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"fcc28d16-53a9-43a9-93f2-2aa9b98436aa","User":{"Name":"Sylvia Challinor","Email":"SylviCh@contoso.com"}, "DeviceName":"SYLV534421","SyncAppVersion":"23.199.0924.0001","SyncAppUpdateRing":5,"OSName":"Windows","OSVersion":"10.0.19042","SyncAppBuildType":"","KFMState":24,"KFMOptInWithWizardGPOEnabled":false,"KFMSilentOptInGPOEnabled":false,"KFMFolders":[3,1],"KFMFolderCount":2,"LastSyncedTimestampUTC":"2023-09-29T12:18:10Z","LastStatusReportedTimestamp":"2023-10-02T02:23:26Z","TotalErrorCount":0,"ErrorDetails":[],"SnapshotDate":"2023-11-02T00:00:00Z"}
```