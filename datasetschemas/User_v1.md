---
title: "User_v1"
description: "Contains user information."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains user information.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| aboutMe | string | A freeform text entry field for the user to describe themselves. |
| accountEnabled | boolean | true if the account is enabled; otherwise, false. |
| ageGroup | string | Sets the age group of the user. Allowed values: null, minor, notAdult and adult. |
| assignedLicenses | string | The licenses that are assigned to the user. Format: ARRAY<STRUCT<\`disabledPlans\`:ARRAY<STRING>, \`skuId\`:STRING>> |
| assignedPlans | string | The plans that are assigned to the user. Format: ARRAY<STRUCT<\`assignedDateTime\`:STRING, \`capabilityStatus\`:STRING, \`service\`:STRING, \`servicePlanId\`:STRING>> |
| birthday | string | The birthday of the user. The Timestamp type represents date and time information using ISO 8601 format and is always in UTC time. For example, midnight UTC on Jan 1, 2014 would look like this: '2014-01-01T00:00:00Z' |
| businessPhones | string | The telephone numbers for the user. NOTE: Although this is a string collection, only one number can be set for this property. Format: ARRAY<STRING> |
| city | string | The city in which the user is located. |
| companyName | string | The company name which the user is associated. |
| consentProvidedForMinor | string | Sets whether consent has been obtained for minors. Allowed values: null, granted, denied and notRequired. |
| country | string | The country or region in which the user is located; for example, "US" or "UK". |
| createdDateTime | string | The created date of the user object. |
| department | string | The name for the department in which the user works. |
| displayName | string | The name displayed in the address book for the user. This is usually the combination of the user's first name, middle initial and last name. |
| givenName | string | The given name (first name) of the user. |
| hireDate | string | The hire date of the user. The Timestamp type represents date and time information using ISO 8601 format and is always in UTC time. For example, midnight UTC on Jan 1, 2014 would look like this: '2014-01-01T00:00:00Z' |
| id | string | The unique identifier for the user. Inherited from directoryObject. |
| imAddresses | string | The instant message voice over IP (VOIP) session initiation protocol (SIP) addresses for the user. Format: ARRAY<STRING> |
| interests | string | A list for the user to describe their interests. Format: ARRAY<STRING> |
| jobTitle | string | The userâ€™s job title. |
| legalAgeGroupClassification | string | Used by enterprise applications to determine the legal age group of the user. This property is read-only and calculated based on ageGroup and consentProvidedForMinor properties. Allowed values: null, minorWithOutParentalConsent, minorWithParentalConsent, minorNoParentalConsentRequired, notAdult and adult. |
| mail | string | The SMTP address for the user. |
| mailNickname | string | The mail alias for the user. |
| mobilePhone | string | The primary cellular telephone number for the user. |
| mySite | string | The URL for the user's personal site. |
| officeLocation | string | The office location in the user's place of business. |
| onPremisesImmutableId | string | This property is used to associate an on-premises Active Directory user account to their Azure AD user object. |
| onPremisesLastSyncDateTime | string | Indicates the last time at which the object was synced with the on-premises directory. The Timestamp type represents date and time information using ISO 8601 format and is always in UTC time. For example, midnight UTC on Jan 1, 2014 would look like this: '2014-01-01T00:00:00Z'. |
| onPremisesSecurityIdentifier | string | Contains the on-premises security identifier (SID) for the user that was synchronized from on-premises to the cloud. |
| onPremisesSyncEnabled | boolean | true if this object is synced from an on-premises directory; false if this object was originally synced from an on-premises directory but is no longer synced; null if this object has never been synced from an on-premises directory (default). |
| passwordPolicies | string | Specifies password policies for the user. This value is an enumeration with one possible value being "DisableStrongPassword", which allows weaker passwords than the default policy to be specified. "DisablePasswordExpiration" can also be specified. The two may be specified together. |
| pastProjects | string | A list for the user to enumerate their past projects. Format: ARRAY<STRING> |
| postalCode | string | The postal code for the user's postal address. The postal code is specific to the user's country/region. In the United States of America, this attribute contains the ZIP code. |
| preferredLanguage | string | The preferred language for the user. Should follow ISO 639-1 Code; for example "en-US". |
| preferredName | string | The preferred name for the user. |
| provisionedPlans | string | The plans that are provisioned for the user. Read-only. Not nullable. Format: ARRAY<STRUCT<\`capabilityStatus\`:STRING, \`provisioningStatus\`:STRING, \`service\`:STRING>> |
| proxyAddresses | string | Format: ARRAY<STRING> |
| responsibilities | string | A list for the user to enumerate their responsibilities. Format: ARRAY<STRING> |
| schools | string | A list for the user to enumerate the schools they have attended. Format: ARRAY<STRING> |
| skills | string | A list for the user to enumerate their skills. Format: ARRAY<STRING> |
| state | string | The state or province in the user's address. |
| streetAddress | string | The street address of the user's place of business. |
| surname | string | The user's surname (family name or last name). |
| usageLocation | string | A two letter country code (ISO standard 3166). Required for users that will be assigned licenses due to legal requirement to check for availability of services in countries. Examples include: "US", "JP", and "GB". Not nullable. |
| userPrincipalName | string | The user principal name (UPN) of the user. The UPN is an Internet-style login name for the user based on the Internet standard RFC 822. By convention, this should map to the user's email name. The general format is alias@domain, where domain must be present in the tenant's collection of verified domains. The verified domains for the tenant can be accessed from the verifiedDomains property of organization. |
| userType | string | A string value that can be used to classify user types in your directory, such as "Member" and "Guest". |
