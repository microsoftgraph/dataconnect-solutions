---
title: "GroupDetails_v0"
description: "Contains the details for a Teams group."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the information from a Teams group chat details.

## Properties

| Name | Type | Description |
|--|--|--|
| id | string | Unique directory ID for the group in AAD |
| deletedDateTime | DateTime | Timestamp in UTC when group was deleted. "null" if group is active. |
| classification | string | Defined the group sensitivity classification in Microsoft 365. |
| createdDateTime | DateTime | Timestamp in UTC when group was created. |
| description | string | User specified description for the group. |
| displayName | string | The display name for the group. |
| expirationDateTime | DateTime | Timestamp in UTC when the group is set to expire. |
| groupType | string | Specified type of group. Unified indicated group has an associated Teams team. |
| isAssignableToRole | boolean | Indicated if group can have AAD role assigned. |
| mail | string | SMTP address of the group |
| mailEnabled | boolean | Specifies if Group is mail enabled. |
| mailNickname | string | Mail alias for the group unique to the tenant. |
| membershipRule | string | The rule determines if membership is dynamic. |
| membershipRuleProcessingState | string | Determines if dynamic membership is on or paused. Null is not set. |
| onPremisesDomainName | string | Domain name for on-premise AD groups. |
| onPremisesLastSyncDateTime | DateTime | Timestamp in UTC for last sync of on-premise AD groups. |
| onPremisesSyncEnabled | boolean | True if synced from on-premise AD. Null if not synced from AD. False if used to not anymore. |
| preferredDataLocation | string | Physical geographic location for the group data storage like emails, files, chat, etc. |
| preferredLanguage | string | Preferred language set for the group. |
| proxyAddresses | string | Email addresses that direct to the same group mailboxes as this group. |
| renewedDateTime | DateTime | Timestamp in UTC when group was last renewed. |
| resourceProvisioningOptions | string | Specifies the group resources that are provisioned as part of M365 Group creation. Possible value is \\"Team\\". |
| securityEnabled | boolean | Specifies if the group is a Security group. |
| securityIdentifier | string | Security identifier for the group. |
| theme | string | Specifies a M365 Group color theme. |
| visibility | string | Specifies the group join policy and content visibility for the group. |
