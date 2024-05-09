---
title: "BasicDataSet_v0.OneDriveSyncErrors_v0 dataset"
description: "The OneDrive Sync Errors dataset includes information about errors on devices running OneDrive for Business"
author: "josebda"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

## BasicDataSet_v0.OneDriveSyncErrors_v0 dataset

### Description: 

The OneDrive Sync Errors dataset includes information about errors on devices running OneDrive for Business. This includes one object for every type of error in a specific device. Data includes error codes, error messages and number of times an error was seen on the device. 

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

### Error codes:

SyncErrorId | Primary Text | Secondary Text | Documentation | 
| - | - | - | - |
48 | File Name | The file is opened by another program and can't be synced. Once the file is closed, we can sync it  |  | 
50 | OneDrive can’t transfer files right now  | Click here to view sync problems  | https://go.microsoft.com/fwlink/?linkid=2182832 | 
53 | File Name | There isn't enough space on this computer to sync this file. | https://go.microsoft.com/fwlink/?linkid=2182820 | 
54 | File Name | You can't upload files larger than "\*\*\*\*\*" in "\*\*\*\*\*"  | https://go.microsoft.com/fwlink/?linkid=2182724 | 
56 | File Name | Please change the file name. For example, make sure it doesn't begin or end with a space, end with a period, begin with two periods, or include any of these characters: \\ / : \* ? \" < > |   | https://go.microsoft.com/fwlink/?linkid=2182830 | 
58 | File Name | The file or folder and its contents cannot fit in the remaining quota in your "\*\*\*\*\*" | https://go.microsoft.com/fwlink/?linkid=2182716 | 
60 | Your OneDrive is out of space  | Get more storage to continue syncing new files. | https://go.microsoft.com/fwlink/?linkid=2182727 | 
61 | File Name | There are too many files in "\*\*\*\*\*". Please remove files you don't need.  | https://go.microsoft.com/fwlink/?linkid=2182722 | 
62 | File Name | A file or folder with this name already exists in the same location online in "\*\*\*\*\*". Please rename the file or folder.  | https://go.microsoft.com/fwlink/?linkid=2183014 | 
68 | File Name | This file is read-only in "\*\*\*\*\*"  | https://go.microsoft.com/fwlink/?linkid=2182719 | 
69 | File Name | An item couldn't be uploaded to "\*\*\*\*\*"  |  | 
70 | File Name | An item couldn't be uploaded to "\*\*\*\*\*"  | https://go.microsoft.com/fwlink/?linkid=2182825 | 
72 | File Name | We couldn't merge the changes in an Office file. | https://go.microsoft.com/fwlink/?linkid=2182827 | 
73 | Can't sync an item | An IT policy prevents work files from syncing outside your organization.  | https://go.microsoft.com/fwlink/?linkid=2182909 | 
83 | File Name | You may be experiencing a temporary sync delay due to high service activity. We'll try updating the file again soon. | https://go.microsoft.com/fwlink/?linkid=2182821 | 
84 | This item has the same name as another item in this folder.  | Please rename or remove this item.  | https://go.microsoft.com/fwlink/?linkid=2182721 | 
85 | We detected a virus in this file and stopped syncing it.  | If you have a copy on your computer, move it out of your OneDrive folder and use antivirus software to scan your computer. Deleted the infected file from your online storage, and upload the copy you scanned.  |  | 
86 | File Name | We weren't able to sync this protected file. Please contact the SharePoint administrator of this protected file to request access.  | https://go.microsoft.com/fwlink/?linkid=2182826 | 
87 | File Name | You've reached the maximum number of minor versions you can save for this file. Publish the file on your SharePoint site: Right-click the file, and select More > Publish. Then you can start saving minor versions again.  | https://go.microsoft.com/fwlink/?linkid=2182723 | 
94 | File Name | Your organization doesn't allow syncing of this folder. For more info, ask the site owner.  | https://go.microsoft.com/fwlink/?linkid=2182725 | 
98 | File Name | Items must be approved to be added to the folder. To sync this folder, ask the site owner to turn off "Content Approval." | https://go.microsoft.com/fwlink/?linkid=2182831 | 
103 | File Name | This file can't be synced because IRM library settings are configured to block users from uploading documents that do not support IRM.  | https://go.microsoft.com/fwlink/?linkid=2182717 | 
104 | An item can't be deleted. | To stop syncing "\*\*\*\*\*" to your computer, click here to go to Preferences and unselect this item.  |  | 
106 | An item can't be uploaded. | To stop syncing "\*\*\*\*\*" to your computer, click here to go to Preferences and unselect this item.  |  | 
108 | File Name | It appears that you do not have access permissions to the item. Ensure that you have the right permissions to allow the item to sync.  | https://go.microsoft.com/fwlink/?linkid=2182824 | 
109 | File Name | This library was removed. Any files that were saved to this device will still be there. Click or tap to stop syncing this library, or contact the library owner.  |  | 
112 | File Name | You no longer have permissions to sync "\*\*\*\*\*"  | https://go.microsoft.com/fwlink/?linkid=2182828 | 
114 | Remove files from all locations? | You recently deleted \*\*\*\*\* files or moved those files out of your OneDrive folder on this device. Do you want to remove them from OneDrive and all shared libraries? | https://go.microsoft.com/fwlink/?linkid=2182726 | 
120 | File Name | The name contains characters that aren't allowed. Please rename the item, so we can sync it.  | https://go.microsoft.com/fwlink/?linkid=2182720 | 
121 | File Name | The name or type isn't allowed. Please rename the item or remove it from your "\*\*\*\*\*".  | https://go.microsoft.com/fwlink/?linkid=2182823 | 
122 | File Name | The item can't be synced because another item has an issue. When the other item is fixed, we can sync this one too.  | https://go.microsoft.com/fwlink/?linkid=2182718 | 
127 | A Shortcut No Longer Works  | Your IT admin no longer allows you to sync "\*\*\*\*\*"  | https://go.microsoft.com/fwlink/?linkid=2182829 | 
128 | A Shortcut No Longer Works | "\*\*\*\*\*" has been deleted or you no longer have permission to view it.  |  | 
130 | Unable to Sync Shortcut  | A shortcut can't be added since its content is already syncing.  |  | 
131 | File is corrupted or OneDrive can't sync it. | Click to download the latest version or recover a previous version.  |  | 
132 | File Name | afpAccessDenied: Insufficient access privileges for operation  |  | 
133 | An item can't be deleted.  | A SharePoint retention policy prevented deletion of "\*\*\*\*\*" so it has been restored. Use 'Free up space' or stop syncing the library if you no longer want this item on your device. |  | 
134 | An item can’t be moved  | An error occured when trying to move "\*\*\*\*\*". Please try again later.  |  | 
135 | An item can’t be moved  | "\*\*\*\*\*" is currently in use and can’t be moved. Please try again later.  |  | 
136 | An item can’t be synced  | A problem occurred when you moved "\*\*\*\*\*". Please move it back to its previous location so it can continue syncing.  |  | 
139 | Unable to Sync Shortcut | We can't sync your shortcut to "\*\*\*\*\*" because it conflicts with the "\*\*\*\*\*" folder that's added by your admin and already syncing to your device.  |  | 

### Notes:

- This dataset covers data from the last 30 days  of activities and does not support Deltas.

### Schema:

| **Name** | **Type** | **Description** | **FilterOptions** | **IsDateFilter** |
|-|-|-|:-:|:-:|
| Id | string | Id of the item | No | False |
| ptenant | string | Id of the tenant | No | False |
| puser | string | Id of the user | No | False |
| ODataType | string | Data type of the object | No | False |
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
    "Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0LAAAA0",
    "ptenant":"12345678-4d67-4c21-842a-abcea48840d5",
    "puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25",
    "ODataType":"#Microsoft.OutlookServices.ApplicationDataItem",
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
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0LAAAA1","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"ee2f3908-efad-407c-b1e1-ee19f0d03870","SyncErrorId":34,"TaskDialogId1":30,"TaskDialogId2":256,"ErrorTitle":"We can't sync this item because the path is too long","ErrorDescription":"Shorten the path and try again.","ErrorCount":1}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0KAAAA2","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"7b805f02-3adc-4e9f-a66a-96a4b30e3654","SyncErrorId":53,"taskDialogId1":0,"taskDialogId2":256,"ErrorTitle":"There isn't enough space on this computer to sync this file.","ErrorDescription":"Free up space by removing apps and files you don't need anymore. We'll automatically sync this file when there's enough space.","ErrorCount":1}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0JAAAA3","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"7b7add73-9075-460f-b7a3-210bafaa8d3b","SyncErrorId":62,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"You already have a file or folder with this name in the same location.","ErrorDescription":"Rename the item on this PC or online to keep both versions. If the items are the same, you can delete the version on this PC to download the online version.","ErrorCount":1}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0IAAAA4","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"43233cce-252c-4ed2-9786-b6d6812dd48b","SyncErrorId":72,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"We couldn't merge the changes in an Office file","ErrorDescription":"Open the OneDrive activity center and click the error to resolve the issue.","ErrorCount":4}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0HAAAA5","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"43233cce-252c-4ed2-9786-b6d6812dd48b","SyncErrorId":85,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"We detected a virus in this file and stopped syncing it.","ErrorDescription":"If you have a copy on your computer, move it out of your OneDrive folder and use antivirus software to scan your computer. Delete the infected file from your online storage, and upload the copy you scanned.","ErrorCount":1}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0HAAAA6","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"2302602d-1020-4d41-9e61-58a188f7e24f","SyncErrorId":112,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"You don't have permission to sync this library.","ErrorDescription":"View this library online to request access, or click or tap to stop syncing this library.","ErrorCount":1}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0LAAAA7","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"4725b0f1-b70c-4f2e-bdc6-9f653d4abe10","SyncErrorId":121,"taskDialogId1":0,"taskDialogId2":256,"ErrorTitle":"This item name or type isn't allowed.","ErrorDescription":"Please rename the item or remove it from your OneDrive.","ErrorCount":1}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0LAAAA8","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"2e7bf84c-8902-402f-bb65-8bfe02b2797c","SyncErrorId":129,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"Unable to sync shortcut","ErrorDescription":"We can't sync your shortcut to \"*****\". However, you can access the folder under \"*****\".","ErrorCount":5}
{"Id":"RgAAAABqEZVyLE_UTYqVWU7Ky3ewBwA38aviTWJnToDKsbNwCB8BAAAAAAEVAAA38aviTWJnToDKsbNwCB8BAABlpB0LAAAA9","ptenant":"12345678-4d67-4c21-842a-abcea48840d5","puser":"1f11e7e1-c483-403a-b6b4-78f8dedc7f25","ODataType":"#Microsoft.OutlookServices.ApplicationDataItem","OneDriveDeviceId":"ee2f3908-efad-407c-b1e1-ee19f0d03870","SyncErrorId":135,"TaskDialogId1":0,"TaskDialogId2":256,"ErrorTitle":"An item can't be moved","ErrorDescription":"\"*****\" is currently in use and can't be moved. Please try again later.","ErrorCount":1}
```
