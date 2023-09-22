---
title: "MailFolder_v0"
description: "Contains the mail folders from each user's mailbox."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the mail folders from each user's mailbox.

## Properties

| Name             | Type   | Description                                                          |
| ---------------- | ------ | -------------------------------------------------------------------- |
| puser                         | string   | The unique identifier of the user.  
| id               | string | The mailFolder's unique identifier.                                  |
| displayName      | string | The mailFolder's display name.                                       |
| parentFolderId   | string | The unique identifier for the mailFolder's parent mailFolder.        |
| childFolderCount | int32  | The number of immediate child mailFolders in the current mailFolder. |
| unreadItemCount  | int32  | The number of items in the mailFolder marked as unread.              |
| totalItemCount   | int32  | The number of items in the mailFolder.                               |
