---
title: "Contact_v0"
description: "Contains the available information from each user’s address book."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the available information from each user’s address book.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| AssistantName | string | The name of the contact's assistant. |
| Birthday | datetime | contact's birthday. |
| BusinessAddress | string | The contact's business address. Format: STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING> |
| BusinessHomePage | string | The business home page of the contact. |
| BusinessPhones | string | The contact's business phone numbers. Format: ARRAY<STRING> |
| Categories | string | The categories associated with the contact. Format: ARRAY<STRING> |
| ChangeKey | string | Identifies the version of the contact. Every time the contact is changed, ChangeKey changes as well. This allows Exchange to apply changes to the correct version of the object. |
| Children | string | The names of the contact's children. Format: ARRAY<STRING> |
| CompanyName | string | The name of the contact's company. |
| Department | string | The contact's department. |
| CreatedDateTime | datetime | The time the contact was created. |
| LastModifiedDateTime | datetime | The time the contact was modified. |
| DisplayName | string | The contact's display name. |
| EmailAddresses | string | The contact's email addresses. Format: ARRAY<STRUCT<Name : STRING, Address : STRING>> |
| FileAs | string | The name the contact is filed under. |
| Generation | string | The contact's generation. |
| GivenName | string | The contact's given name. |
| HomeAddress | string | The contact's home address. Format: STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING> |
| HomePhones | string | The contact's home phone numbers. Format: ARRAY<STRING> |
| Id | string | The contact's unique identifier. |
| ImAddresses | string | The contact's instant messaging (IM) addresses. Format: ARRAY<STRING> |
| Initials | string | The contact's initials. |
| JobTitle | string | The contact's job title. |
| Manager | string | The name of the contact's manager. |
| MiddleName | string | The contact's middle name. |
| MobilePhone1 | string | The contact's mobile phone number. |
| NickName | string | The contact's nickname. |
| OfficeLocation | string | The location of the contact's office. |
| OtherAddress | string | Other addresses for the contact. Format: STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING> |
| ParentFolderId | string | The ID of the contact's parent folder. |
| PersonalNotes | string | The user's notes about the contact. |
| Profession | string | The contact's profession. |
| SpouseName | string | The name of the contact's spouse. |
| Surname | string | The contact's surname. |
| Title | string | The contact's title. |
| YomiCompanyName | string | The phonetic Japanese company name of the contact. |
| YomiGivenName | string | The phonetic Japanese given name (first name) of the contact. |
| YomiSurname | string | The phonetic Japanese surname (last name) of the contact. |
