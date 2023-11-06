---
title: "Microsoft Graph Data Connect PlannerTasks_v0 dataset"
description: "Use this dataset to identify Planner Tasks in Microsoft Outlook that track user-level work items."
author: "kumarrachit"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# PlannerTasks_v0 dataset

The PlannerTasks_v0 dataset provides a list of Microsoft Outlook tasks available on Planner App. You can also connect your Planner App to ToDo App to get all Tasks at one place. Tasks in Microsoft Outlook tracks user-level work items. Users can note a task's start, due, and actual completion dates, its progress or status, and whether it's recurring or requires a reminder. All Planner Tasks are a part of a Plan, under which they're organized in buckets. By default, there is no plan or task. The user have to create a Plan, inside which they can add tasks and assign them to a predefined or custom bucket.

The PlannerTasks_v0 dataset enables you to identify all the Planner tasks assigned to any of the users of a tenant that are secured by Azure Active Directory (Azure AD) in Microsoft 365. User accounts can be on Microsoft 365 or a Microsoft account (Hotmail.com, Live.com, MSN.com, Outlook.com, and Passport.com).

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze the number of tasks that were created by all users. You can also analyze how many of these tasks got completed before the assigned due dates, and how many got completed after the assigned due dates.
- Identify stale tasks for notifying respective users to complete their tasks before the due date.
- Identify users having incomplete tasks beyond a particular threshold.
- Identify tasks assigned to users where the status remains unchanged beyond a particular threshold.

## Questions

The following are examples of questions that you can answer with this dataset:

- How many tasks were created by all users of a tenant?
- How many tasks were completed before the assigned due date by all users of a tenant?
- How many tasks were completed after the assigned due date by all users of a tenant?

## Joining with other datasets

The PlannerTasks_v0 dataset can be joined with relevant users, and other category datasets.

## Definitions

- **Task:** Represents a Planner task in Microsoft 365. A Planner task is contained in a plan and can be assigned to a bucket in a plan. 
- **Plan:** Plans are the containers of tasks. A plan can be owned by a group and contains a collection of plannerTasks.


## Schema

| Name  | Type  |  Description  |  FilterOptions  |  FilterType  |
| ----------- | ----------- | ----------- | ----------- | ----------- |
| planId | String | Plan ID to which the task belongs. | No | None |
| bucketId | String | Bucket ID to which the task belongs. The bucket needs to be in the plan that the task is in. It is 28 characters long and case-sensitive. | No | None |
| title | String | Title of the task. | No | None |
| orderHint | String | Hint used to order items of this type in a list view. | No | None |
| assigneePriority | String | Hint used to order items of this type in a list view. | No | None |
| percentComplete | Int32 | Percentage of task completion. When set to 100, the task is considered completed. | No | None |
| startDateTime | Datetime | Date and time at which the task starts. It is in UTC format. | No | None
| createdDateTime | Datetime | Date and time at which the task is created. It is in UTC format. | 1 | Date |
| dueDateTime | Datetime | Date and time at which the task is due. It is in UTC format. | No | None |
| hasDescription | Boolean | Value is?true?if the details object of the task has a non-empty description and?None?otherwise. | No | None |
| previewType | String | "This sets the type of preview that shows up on the task. The possible values are: automatic, noPreview, checklist, description, reference."	No | None |
| completedDateTime | Datetime | Date and time at which the 'percentComplete' of the task is set to '100'. It is in UTC format. | No | None |
| completedBy | String | Identity of the user that completed the task. | No | None |
| referenceCount | Int32 | Number of external references that exist on the task. | No | None |
| checklistItemCount | Int32 | Number of checklist items that are present on the task. | No | None |
| activeChecklistItemCount | Int32 | Number of checklist items with value set to False, representing incomplete items. | No | None |
| conversationThreadId | String | Thread ID of the conversation on the task. This is the ID of the conversation thread object created in the group. | No | None |
| priority | Int32 | Priority of the task. The valid range of values is between 0 and 10, with the increasing value being lower priority (0 has the highest priority and 10 has the lowest priority). |	No | None |
| id | String | ID of the task. It is 28 characters long and case-sensitive. | No | None |
| createdBy | Object | Identity of the user that created the task. *Format:*` STRUCT<application: STRUCT<displayName: STRING, id: STRING>, user: STRUCT<displayName: STRING, id: STRING>>. ` | No | None |
| appliedCategories | Object | The categories to which the task has been applied. *Format:*` STRUCT<category1: BOOL, category2: BOOL, category3: BOOL, category4: BOOL, category5: BOOL, category6: BOOL, category7: BOOL, category8: BOOL, category9: BOOL, category10: BOOL, category11: BOOL, category12: BOOL, category13: BOOL, category14: BOOL, category15: BOOL, category16: BOOL, category17: BOOL, category18: BOOL, category19: BOOL, category20: BOOL, category21: BOOL, category22: BOOL, category23: BOOL, category24: BOOL>. ` | No | None |
| assignments | String | The set of assignees the task is assigned to. | No | None |
| puser | String | User id. | No  | None |
| ptenant | String |  Tenant id. | No | None |

## JSON representation

```json
{
"planId": "String",
"bucketId": "String",
"title": "String",
"orderHint": "String",
"assigneePriority": "String",
"percentComplete": "Int32",
"startDateTime": "Datetime (timestamp)",
"createdDateTime": "Datetime (timestamp)",
"dueDateTime": "Datetime (timestamp)",
"hasDescription": "Boolean",
"previewType": "String",
"completedDateTime": "Datetime (timestamp)",
"completedBy": "String",
"referenceCount": "Int32",
"checklistItemCount": "Int32",
"activeChecklistItemCount": "Int32",
"conversationThreadId": "String",
"priority": "Int32",
"id": "String",
"createdBy": ["String"],
"appliedCategories": ["String"],
"assignments": "String",
"puser": "String (identifier)",
"ptenant": "String (identifier)"
}
```

## Sample

```json
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T07:07:08.568918Z","orderHint":"8585039941169399459Pd"}},"bucketId":"dcHNgsP8QUaAIDLhN9F8qWUAMFMo","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T07:07:08.568918Z","datarow":0,"dueDateTime":"2023-10-19T10:00:00Z","hasDescription":false,"id":"-LTdLv17K0GiV-gPgB3OJGUAO98a","orderHint":"8585039941169399459P0","pagerow":0,"percentComplete":0,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"Backlog task 1"}
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T07:07:27.3546495Z","orderHint":"8585039940981698144P8"}},"bucketId":"qUfpFVTgmkysRVVCPhM1OmUAA5_4","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T07:07:27.3546495Z","datarow":1,"dueDateTime":"2023-10-26T10:00:00Z","hasDescription":false,"id":"Q9G9iPuMgky20txATBWu0WUALszq","orderHint":"8585039940981698144P:","pagerow":1,"percentComplete":0,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"Task 1"}
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T08:25:38.7964678Z","orderHint":"8585039894067279892PF"}},"bucketId":"TBBuMqY9w0uHUMx1PKTENmUAPxDw","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T08:25:38.7964678Z","datarow":2,"dueDateTime":"2023-10-24T10:00:00Z","hasDescription":false,"id":"Von_i_RPR0yGjq2or9sIGmUAA2_U","orderHint":"8585039894067279892Pl","pagerow":2,"percentComplete":0,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"In Progress Test Task 3"}
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T08:28:02.9044887Z","orderHint":"8585039892626043338PQ"}},"bucketId":"dcHNgsP8QUaAIDLhN9F8qWUAMFMo","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T08:28:02.9044887Z","datarow":3,"dueDateTime":"2023-10-19T10:00:00Z","hasDescription":true,"id":"XMYvJyenVE2qxHi-jlNmDWUAH3Ox","orderHint":"8585039892626043338Pk","pagerow":3,"percentComplete":0,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"Backlog task 2"}
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T08:28:26.4528508Z","orderHint":"8585039892390559854P4"}},"bucketId":"qUfpFVTgmkysRVVCPhM1OmUAA5_4","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T08:28:26.4528508Z","datarow":4,"dueDateTime":"2023-10-26T10:00:00Z","hasDescription":false,"id":"YKMz0rIWmkej7FjvX7_SJGUAIQ1R","orderHint":"8585039892390559854P*","pagerow":4,"percentComplete":0,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"Task 2"}
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T08:24:41.5604643Z","orderHint":"8585039894639483676P9"}},"bucketId":"TBBuMqY9w0uHUMx1PKTENmUAPxDw","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T08:24:41.5604643Z","datarow":5,"dueDateTime":"2023-10-24T10:00:00Z","hasDescription":true,"id":"YU4QW40Vmk-G0LaiINCUGWUAC51I","orderHint":"8585039894639483676PE","pagerow":5,"percentComplete":50,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"In Progress Test Task 1"}
{"activeChecklistItemCount":0,"appliedCategories":{"category1":true},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T08:26:11.8661593Z","orderHint":"8585039893736426622PA"}},"bucketId":"3wvKzD8sjEuxrD3OMyi7i2UANPXK","checklistItemCount":0,"conversationThreadId":"AAQkADE0NjA4ODU3LWY0MGEtNDA1My1hMjQ4LWI5YmRjYjA3YzliNwMkABAAQu4RZJBnYUqJAsNcCSBhJBAAQu4RZJBnYUqJAsNcCSBhJA==","createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T08:26:11.8661593Z","datarow":6,"dueDateTime":"2023-10-25T10:00:00Z","hasDescription":true,"id":"bI6VC6ylB0i3rRTB6syON2UAF9Nm","orderHint":"8585039893736426622P:","pagerow":6,"percentComplete":50,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"description","priority":9,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"startDateTime":"2023-10-11T10:00:00Z","title":"Blocked Task 1"}
{"activeChecklistItemCount":0,"appliedCategories":{"category1":true,"category9":true},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T08:28:35.4890093Z","orderHint":"8585039892300354487Pq"}},"bucketId":"3wvKzD8sjEuxrD3OMyi7i2UANPXK","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T08:28:35.4890093Z","datarow":7,"dueDateTime":"2023-10-25T10:00:00Z","hasDescription":true,"id":"f71r-F5nnkCLFBNHJeGNXWUAK_hy","orderHint":"8585039892300354487PS","pagerow":7,"percentComplete":50,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"description","priority":9,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"startDateTime":"2023-10-11T10:00:00Z","title":"Blocked Task 2"}
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T08:24:58.7677215Z","orderHint":"8585039894467411086Pk"}},"bucketId":"TBBuMqY9w0uHUMx1PKTENmUAPxDw","checklistItemCount":0,"createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T08:24:58.7677215Z","datarow":9,"dueDateTime":"2023-10-24T10:00:00Z","hasDescription":true,"id":"vIlrSXCQz0m0vYgJiQt-umUAGJv0","orderHint":"8585039894467411086P6","pagerow":9,"percentComplete":50,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"In Progress Test Task 2"}
{"activeChecklistItemCount":0,"appliedCategories":{},"assigneePriority":"","assignments":{"34e2aa1f-3519-4337-a1ee-9d34e4493125":{"assignedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"assignedDateTime":"2023-10-18T07:07:55.8398902Z","orderHint":"8585039940696689285PE"}},"bucketId":"TBBuMqY9w0uHUMx1PKTENmUAPxDw","checklistItemCount":0,"completedBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"completedDateTime":"2023-10-18T08:25:46.3904336Z","createdBy":{"application":{"id":"09abbdfd-ed23-44ee-a2d9-a627aa1c90f3"},"user":{"id":"34e2aa1f-3519-4337-a1ee-9d34e4493125"}},"createdDateTime":"2023-10-18T07:07:55.8398902Z","datarow":10,"hasDescription":true,"id":"dC87CZs33Um2ZXe7kQZ7mGUAFeoN","orderHint":"8585039940696689285PO","pagerow":10,"percentComplete":100,"planId":"uYQ3vB-GVEGVgvUx6-uzsWUAHKiX","previewType":"automatic","priority":5,"ptenant":"537d6c63-efb6-4922-8643-17921eb1b0dd","puser":"34e2aa1f-3519-4337-a1ee-9d34e4493125","referenceCount":0,"rowinformation":{"isUserSummaryRow":false,"userHasCompleteData":false,"userReturnedNoData":false},"title":"Task 3"}
```
