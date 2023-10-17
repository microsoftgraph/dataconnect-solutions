---
title: "Microsoft Graph Data Connect Contact_v1 dataset"
description: "Use the Contact_v1 dataset to provide the contact details available from each user’s address book."
author: "rimisra2"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# Microsoft Graph Data Connect Contact_v1 dataset

The Contact_v1 dataset provides the contact details available from each user’s address book.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze all the contacts available from an external organization across all users of a tenant. 
- Analyze all the contacts across users having a particular job title or office location.
- Analyze all the contacts across all users as per the different age groups and job title.

## Questions

The following are examples of questions that you can answer with this dataset:

- Which contacts work for a particular company and from a particular city?
- What are the birthdates, addresses and phone numbers for all contact across all the users?
- When was a particular contact last modified?

## Joining with other datasets

The Contact_v1 dataset can be joined with the Users dataset, and other relevant datasets.

## Schema

| Name  | Type  |  Description  |  FilterOptions  |  FilterType  |
| ----------- | ----------- | ----------- | ----------- | ----------- |
| assistantName |	string |	The name of the contact's assistant. |	No |	None |
| birthday |	datetime |	The contact's birthday. |	No |	None |
| businessAddress |	object |	The contact's business address. *Format:* `STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING>.` |	No |	None |
| businessHomePage |	string |	The business home page of the contact. |	No |	None |
| businessPhones |	array |	The contact's business phone numbers. *Format:* `ARRAY<STRING>.` | 	No |	None |
| categories |	array	| The categories associated with the contact. *Format:* `ARRAY<STRING>.` |	No |	None |
| changeKey |	string |	Identifies the version of the contact. Every time the contact is changed, 'ChangeKey' changes as well. This allows Microsoft Exchange to apply changes to the correct version of the object. |	No |	None |
| children |	array |	The names of the contact's children. *Format:* `ARRAY<STRING>.` |	No |	None |
| companyName |	string |	The name of the contact's company. |	No |	None |
| createdDateTime |	datetime |	The time the contact was created. |	No |	None |
| department |	string |	The contact's department. |	No |	None |
| displayName |	string |	The contact's display name. |	No |	None |
| emailAddresses |	array |	The contact's email addresses. *Format:* `ARRAY<STRUCT<Name : STRING, Address : STRING>>.` |	No |	None |
| fileAs |	string |	The name the contact is filed under. |	No |	None |
| generation |	string |	The contact's generation. |	No |	None |
| givenName |	string |	The contact's given name. |	No |	None |
| homeAddress | object |	The contact's home address. *Format:* `STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING>.` |	No	| None |
| homePhones |	array |	The contact's home phone number(s). *Format:* `ARRAY<STRING>.` |	No |	None |
| id |	string |	The contact's unique identifier. |	No |	None |
| imAddresses |	array	| The contact's instant messaging (IM) addresses. *Format:* `ARRAY<STRING>.` |	No |	None |
| initials |	string |	The contact's initials. |	No |	None |
| jobTitle |	string |	The contact's job title. |	No |	None |
| lastModifiedDateTime |	datetime |	The time the contact was modified. |	No |	None |
| manager |	string |	The name of the contact's manager. |	No |	None |
| middleName |	string |	The contact's middle name. |	No |	None |
| mobilePhone |	string |	The contact's mobile phone number. |	No |	None |
| nickName |	string |	The contact's nickname. |	No |	None |
| officeLocation |	string |	The location of the contact's office. |	No |	None |
| otherAddress |	object |	Other addresses for the contact. *Format:* `STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING>.` |	No |	None |
| parentFolderId |	string |	The id of the contact's parent folder. |	No |	None |
| personalNotes |	string	| The user's notes about the contact. |	No |	None |
| profession |	string |	The contact's profession. |	No |	None |
| spouseName |	string |	The name of the contact's spouse. |	No |	None |
| surname |	string |	The contact's surname. |	No |	None |
| title |	string |	The contact's title. |	No |	None |
| yomiCompanyName |	string |	The phonetic Japanese company name of the contact. |	No |	None |
| yomiGivenName |	string |	The phonetic Japanese given name (first name) of the contact. |	No |	None |
| yomiSurname |	string |	The phonetic Japanese surname (last name) of the contact. |	No |	None |
| ODataType |	string	| Data type of the current item. |	No |	None |
| puser	| string |	User id. |	No |  None |
| ptenant |	string |  Tenant id. |	No |  None |


## JSON representation

```json
{
  "assistantName": "string",
  "birthday": "String (timestamp)",
  "businessAddress": { "@odata.type": "microsoft.graph.physicalAddress" },
  "businessHomePage": "string",
  "businessPhones": ["string"],
  "categories": ["string"],
  "changeKey": "string",
  "children": ["string"],
  "companyName": "string",
  "department": "string",
  "createdDateTime": "String (timestamp)",
  "lastModifiedDateTime": "String (timestamp)",
  "displayName": "string",
  "emailAddresses": [{ "@odata.type": "microsoft.graph.emailAddress" }],
  "fileAs": "string",
  "generation": "string",
  "givenName": "string",
  "homeAddress": { "@odata.type": "microsoft.graph.physicalAddress" },
  "homePhones": ["string"],
  "id": "string (identifier)",
  "imAddresses": ["string"],
  "initials": "string",
  "jobTitle": "string",
  "manager": "string",
  "middleName": "string",
  "mobilePhone": "string",
  "nickName": "string",
  "officeLocation": "string",
  "otherAddress": { "@odata.type": "microsoft.graph.physicalAddress" },
  "parentFolderId": "string",
  "personalNotes": "string",
  "profession": "string",
  "spouseName": "string",
  "surname": "string",
  "title": "string",
  "yomiCompanyName": "string",
  "yomiGivenName": "string",
  "yomiSurname": "string",
  "ODataType": "#microsoft.graph.contact",
  "puser": "string(identifier)",
  "ptenant": "string(identifier)"
}
```

## Sample 


```json
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD4AAA=","createdDateTime":"2021-02-03T00:10:10Z","lastModifiedDateTime":"2021-02-03T00:10:10Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACWB","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"Cooper, Sheldon B ","displayName":"Pradeep Gupta","givenName":”Sheldon”,"initials":”S.B.C”,"middleName":”Benjamin”,"nickName":”Superman”,"surname":”Copper”,"title":”Mr.”,"yomiGivenName":”Sheldon”,"yomiSurname":”Cooper”,"yomiCompanyName":”MICROSOFT”,"generation":”Sr.”,"imAddresses":["sip:PradeepG@M365x723843.OnMicrosoft.com"],"jobTitle":”VP, OUTLOOK SERVICES”,"companyName":”MICROSOFT”,"department":”Outlook Services”,"officeLocation":”Building 7”,"profession":null,"businessHomePage":”microsoft.com”,"assistantName":”Pepper Pots”,"manager":”Nick Fury”,"homePhones":”["+1 (650) 555-1000"]”,"mobilePhone":null,"businessPhones":”["+1 (465) 776-6852 X66822"]“,"spouseName":”Lois Cooper”,"personalNotes":"This is a note about a contact. This contact is important ","children":”["Steve”]”,"emailAddresses":[{"name":"Pradeep Gupta","address":"PradeepG@M365x723843.OnMicrosoft.com"}],"homeAddress":{},"businessAddress":”{"PostalCode": "98052","Street": "1 Microsoft Way","State": "WA","CountryOrRegion": "United States of America","City": "Redmond"}”,"otherAddress":”{"PostalCode": "98052","Street": "1 Microsoft Way","State": "WA","CountryOrRegion": "United States of America","City": "Redmond"}”,"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD3AAA=","createdDateTime":"2021-02-03T00:10:10Z","lastModifiedDateTime":"2021-02-03T00:10:10Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACV3","categories":”["Orange Category", "Green Category", "Blue Category"]”,"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Joni Sherman","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":["sip:JoniS@M365x723843.OnMicrosoft.com"],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":”Manage”,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Joni Sherman","address":"JoniS@M365x723843.OnMicrosoft.com"}],"homeAddress":”{"PostalCode": "98052","Street": "1 Microsoft Way","State": "WA","CountryOrRegion": "United States of America","City": "Redmond"} ”,"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD2AAA=","createdDateTime":"2021-02-03T00:10:09Z","lastModifiedDateTime":"2021-02-03T00:10:09Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVv","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Executives","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":[],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Executives","address":"Executives@CohoLLC5.onmicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD1AAA=","createdDateTime":"2021-02-03T00:10:09Z","lastModifiedDateTime":"2021-02-03T00:10:09Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVo","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Nestor Wilke","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":["sip:NestorW@M365x723843.OnMicrosoft.com"],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Nestor Wilke","address":"NestorW@M365x723843.OnMicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD0AAA=","createdDateTime":"2021-02-03T00:10:09Z","lastModifiedDateTime":"2021-02-03T00:10:09Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVd","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Isaiah Langer","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":["sip:IsaiahL@M365x723843.OnMicrosoft.com"],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Isaiah Langer","address":"IsaiahL@M365x723843.OnMicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDzAAA=","createdDateTime":"2021-02-03T00:10:08Z","lastModifiedDateTime":"2021-02-03T00:10:08Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVX","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Adele Vance","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":["sip:AdeleV@M365x723843.OnMicrosoft.com"],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Adele Vance","address":"AdeleV@M365x723843.OnMicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDyAAA=","createdDateTime":"2021-02-03T00:10:08Z","lastModifiedDateTime":"2021-02-03T00:10:08Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVR","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Megan Bowen","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":["sip:MeganB@M365x723843.OnMicrosoft.com"],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Megan Bowen","address":"MeganB@M365x723843.OnMicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDxAAA=","createdDateTime":"2021-02-03T00:10:07Z","lastModifiedDateTime":"2021-02-03T00:10:08Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVH","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Legal Team","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":[],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Legal Team","address":"Legal@CohoLLC5.onmicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDwAAA=","createdDateTime":"2021-02-03T00:10:07Z","lastModifiedDateTime":"2021-02-03T00:10:07Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVB","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":null,"fileAs":"","displayName":"Lynne Robbins","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":["sip:LynneR@M365x723843.OnMicrosoft.com"],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Lynne Robbins","address":"LynneR@M365x723843.OnMicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDvAAA=","createdDateTime":"2021-02-03T00:10:07Z","lastModifiedDateTime":"2021-02-03T00:10:07Z","changeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACU6","categories":[],"parentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","birthday":”1970-01-01T08:00:00Z“,"fileAs":"","displayName":"Lidia Holloway","givenName":null,"initials":null,"middleName":null,"nickName":null,"surname":null,"title":null,"yomiGivenName":null,"yomiSurname":null,"yomiCompanyName":null,"generation":null,"imAddresses":["sip:LidiaH@M365x723843.OnMicrosoft.com"],"jobTitle":null,"companyName":null,"department":null,"officeLocation":null,"profession":null,"businessHomePage":null,"assistantName":null,"manager":null,"homePhones":[],"mobilePhone":null,"businessPhones":[],"spouseName":null,"personalNotes":"","children":[],"emailAddresses":[{"name":"Lidia Holloway","address":"LidiaH@M365x723843.OnMicrosoft.com"}],"homeAddress":{},"businessAddress":{},"otherAddress":{},"ODataType":"#microsoft.graph.contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
```