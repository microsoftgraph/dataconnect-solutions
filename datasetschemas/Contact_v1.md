---
title: "Contact_v1"
description: "Contains contact information from each user's address book."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains contact information from each user's address book.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| assistantName | string | The name of the contact's assistant. |
| birthday | datetime | The contact's birthday. |
| businessAddress | string | The contact's business address. Format: STRUCT<Street: STRING, City: STRING, State: STRING, CountryOrRegion: STRING, PostalCode: STRING> |
| businessHomePage | string | The business home page of the contact. |
| businessPhones | string | The contact's business phone numbers. Format: ARRAY<STRING> |
| categories | string | The categories associated with the contact. Format: ARRAY<STRING> |
| changeKey | string | Identifies the version of the contact. Every time the contact is changed, ChangeKey changes as well. This allows Exchange to apply changes to the correct version of the object. |
| children | string | The names of the contact's children. Format: ARRAY<STRING> |
| companyName | string | The name of the contact's company. |
| createdDateTime | datetime | The time the contact was created. |
| department | string | The contact's department. |
| displayName | string | The contact's display name. |
| emailAddresses | string | The contact's email addresses. Format: ARRAY<STRUCT<Name : STRING, Address : STRING>> |
| fileAs | string | The name the contact is filed under. |
| generation | string | The contact's generation. |
| givenName | string | The contact's given name. |
| homeAddress | string | The contact's home address. Format: STRUCT<Street: STRING, City: STRING, State: STRING, CountryOrRegion: STRING, PostalCode: STRING> |
| homePhones | string | The contact's home phone numbers. Format: ARRAY<STRING> |
| id | string | The contact's unique identifier. |
| imAddresses | string | The contact's instant messaging (IM) addresses. Format: ARRAY<STRING> |
| initials | string | The contact's initials. |
| jobTitle | string | The contact's job title. |
| lastModifiedDateTime | datetime | The time the contact was modified. |
| manager | string | The name of the contact's manager. |
| middleName | string | The contact's middle name. |
| mobilePhone | string | The contact's mobile phone number. |
| nickName | string | The contact's nickname. |
| officeLocation | string | The location of the contact's office. |
| otherAddress | string | Other addresses for the contact. Format: STRUCT<Street: STRING, City: STRING, States: STRING, CountryOrRegion: STRING, PostalCode: STRING> |
| parentFolderId | string | The ID of the contact's parent folder. |
| personalNotes | string | The user's notes about the contact. |
| profession | string | The contact's profession. |
| spouseName | string | The name of the contact's spouse/partner. |
| surname | string | The contact's surname. |
| title | string | The contact's title. |
| yomiCompanyName | string | The phonetic Japanese company name of the contact. |
| yomiGivenName | string | The phonetic Japanese given name (first name) of the contact. |
| yomiSurname | string | The phonetic Japanese surname (last name) of the contact. |
