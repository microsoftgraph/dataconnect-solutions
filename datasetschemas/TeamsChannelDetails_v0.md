---
title: "TeamsChannelDetails_v0"
description: "Contains the details for a Teams channel."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the details from a Teams channel.

## Properties

| Column Name | Type | Description |
|--|--|--|
| Id | string | Unique directory ID for the Teams channel. |
| createdDateTime | dateTime | Timestamp in UTC when channel was created. |
| displayName | string | The display name for the channel. |
| description | string | User specified description for the channel. |
| isFavoriteByDefault | boolean | Boolean that specifies if the Channel is "favorited" by default for all members of the team. |
| email | string | SMTP address of the channel for messaging. |
| webUrl | string | Web URL for the Teams channel. |
| membershipType | string | Membership or Type of the channel, i.e. standard channel, private channel, etc. |
