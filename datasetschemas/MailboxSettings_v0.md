---
title: "MailboxSettings_v0"
description: "Contains the mailbox settings of each user."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the mailbox settings of each user.

## Properties

| Name                    | Type   | Description                                                                                                            |
| ----------------------- | ------ | ---------------------------------------------------------------------------------------------------------------------- |
| puser                         | string   | The unique identifier of the user.  
| archiveFolder           | string | Folder ID of an archive folder for the user.                                                                           |
| timeZone                | string | The default time zone for the user's mailbox.                                                                          |
| automaticRepliesSetting | string | Configuration settings to automatically notify the sender of an incoming email with a message from the signed-in user. |
| language                | string | The locale information for the user, including the preferred language and country/region.                              |
| workingHours            | string | The days of the week and hours in a specific time zone that the user works.                                            |
