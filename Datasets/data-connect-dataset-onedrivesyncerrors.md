---
title: "BasicDataSet_v0.OneDriveSyncErrors_v1 dataset"
description: "The OneDrive Sync Errors dataset includes information about errors on devices running OneDrive for Business"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.OneDriveSyncErrors_v1 dataset

### Description: 

The OneDrive Sync Errors dataset includes information about errors on devices running OneDrive for Business. This includes one object for every type of error in a specific device. Data includes error codes, error messages and number of times an error was seen on the device. 

NOTE:

- Currently, the MGDC platform ONLY supports extracting valid users. Trying to extract invalid users will result in no data to be returned for such users. Valid users must have the following:
    * A valid mailbox (i.e. users having mailbox that is **not in inactive state**, **not soft-deleted** or **not hosted on-Prem**)
    * User account enabled (i.e. accountEnabled should not be set to false)
    * A valid substrate mailbox (i.e. it should have an exchange license)

**IMPORTANT NOTE**: This dataset is not currently available publicly. It is coming soon.  The details provided here are for informational purposes only.

### Scenarios:

- Users can experience sync errors affecting productivity and collaboration. Administrators want to identify devices that are both experiencing errors and not staying up-to-date and want to drill down into report details to understand the users’ states.
- Administrators want to know which specific errors are occurring on each device in the organization.
- Administrators want to understand which types of error are most common.  

### Questions:

- How many devices are showing errors?
- Which types of errors are making most devices unhealthy?
- Which devices are showing a specific error?
- What are the errors occurring on a specific device?

### Joining with other datasets:

- This dataset can be joined with the Sync Health dataset (by OneDriveDeviceId) to provide device and user details.

### Definitions:

- Device – A computer (PC or Mac) running the OneDrive Sync client to upload and download files to the cloud.

### Notes:

- This dataset covers data from the last 30 days  of activities and does not support Deltas.

### Schema:

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| ptenant | string | Id of the tenant | No | False |
| OneDriveDeviceId | string | Unique OneDrive Sync internal identifier. Can be used to join with the Sync Health dataset | No | False |
| SyncErrorId | int | Error id when this is a Sync error (shown in the system tray icon as a red X). If this is 34, this is not that type of error | No | False |
| TaskDialogId1 | int | Error id when the error is shown in a task dialog type 1 (a native OS dialog on contextual action). If this is 0, this is not that type of error (see note) | No | False |
| TaskDialogId2 | int | Error id when the error is shown in a task dialog type 2 (a native OS dialog on contextual action). If this is 256, this is not that type of error (see note) | No | False |
| ErrorTitle | string | Error title shown to user (localized) | No | False |
| ErrorDescription | string | Error description shown to user (localized) | No | False |
| ErrorCount | int | Number of errors of this type reported by this OneDriveDeviceId | No | False |

NOTE: There is no distinction between the two types of task dialog errors. This will depend on the task dialog shown. 

### JSON Representation:

```json
{
{
    "ptenant":"12345678-4d67-4c21-842a-abcea48840d5",
    "OneDriveDeviceId": "7b805f02-3adc-4e9f-a66a-96a4b30e3654",
    "SyncErrorId": 53,
    "TaskDialogId1": 0,
    "TaskDialogId2": 256,
    "ErrorTitle": "There isn't enough space on this computer to sync this file.",
    "ErrorDescription": "Free up space by removing apps and files you don't need anymore. We'll automatically sync this file when there's enough space.",
    "ErrorCount": 1
}
```

### Sample

```json
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"ee2f3908-efad-407c-b1e1-ee19f0d03870","SyncErrorId":34,"TaskDialogId1":30,"TaskDialogId2":256,"ErrorTitle":"We can't sync this item because the path is too long","ErrorDescription":"Shorten the path and try again.","ErrorCount":1}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"7b805f02-3adc-4e9f-a66a-96a4b30e3654","SyncErrorId":53,"taskDialogId1":0,"taskDialogId2":256,"ErrorTitle":"There isn't enough space on this computer to sync this file.","ErrorDescription":"Free up space by removing apps and files you don't need anymore. We'll automatically sync this file when there's enough space.","ErrorCount":1}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"7b7add73-9075-460f-b7a3-210bafaa8d3b","SyncErrorId":62,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"You already have a file or folder with this name in the same location.","ErrorDescription":"Rename the item on this PC or online to keep both versions. If the items are the same, you can delete the version on this PC to download the online version.","ErrorCount":1}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"43233cce-252c-4ed2-9786-b6d6812dd48b","SyncErrorId":72,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"We couldn't merge the changes in an Office file","ErrorDescription":"Open the OneDrive activity center and click the error to resolve the issue.","ErrorCount":4}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"43233cce-252c-4ed2-9786-b6d6812dd48b","SyncErrorId":85,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"We detected a virus in this file and stopped syncing it.","ErrorDescription":"If you have a copy on your computer, move it out of your OneDrive folder and use antivirus software to scan your computer. Delete the infected file from your online storage, and upload the copy you scanned.","ErrorCount":1}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"2302602d-1020-4d41-9e61-58a188f7e24f","SyncErrorId":112,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"You don't have permission to sync this library.","ErrorDescription":"View this library online to request access, or click or tap to stop syncing this library.","ErrorCount":1}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"4725b0f1-b70c-4f2e-bdc6-9f653d4abe10","SyncErrorId":121,"taskDialogId1":0,"taskDialogId2":256,"ErrorTitle":"This item name or type isn't allowed.","ErrorDescription":"Please rename the item or remove it from your OneDrive.","ErrorCount":1}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"2e7bf84c-8902-402f-bb65-8bfe02b2797c","SyncErrorId":129,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"Unable to sync shortcut","ErrorDescription":"We can't sync your shortcut to \"*****\". However, you can access the folder under \"*****\".","ErrorCount":5}
{"ptenant":"12345678-4d67-4c21-842a-abcea48840d5","OneDriveDeviceId":"ee2f3908-efad-407c-b1e1-ee19f0d03870","SyncErrorId":135,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"An item can't be moved","ErrorDescription":"\"*****\" is currently in use and can't be moved. Please try again later.","ErrorCount":1}
```
