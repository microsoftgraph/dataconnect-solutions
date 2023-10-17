---
title: "Microsoft Graph Data Connect Contact_v0 dataset"
description: "Use the Contact_v0 dataset to provide contact details available from each user’s address book."
author: "rimisra2"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# Microsoft Graph Data Connect Contact_v0 dataset

The Contact_v0 dataset provides contact details available from users' address book.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze all the contacts available from an external organization across all users of a tenant.
- Analyze all the contacts across users having a particular job title or office location.
- Analyze all the contacts across all users as per the different age groups and job titles.

## Questions

The following are examples of questions that you can answer with this dataset:

- Which contacts work for a particular company and from a particular city?
- What are the birthdates, addresses and phone numbers for all contact across all the users?
- When was a particular contact last modified?


## Joining with other datasets

The Contact_v0 dataset can be joined with the Users dataset, and other relevant datasets.


## Schema

| Name  | Type  |  Description  |  FilterOptions  |  FilterType  |
| ----------- | ----------- | ----------- | ----------- | ----------- |
| AssistantName	| string |	The name of the contact's assistant. |	No |	None |
| Birthday	| datetime |	Contact's birthday. |	No |	None |
| BusinessAddress |	object |	The contact's business address. *Format:* `STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING>.` |	No |	None |
| BusinessHomePage |	string |	The business home page of the contact. |	No |	None |
| BusinessPhones |	array |	The contact's business phone numbers. *Format:* `ARRAY<STRING>.` | No |	None |
| Categories |	array |	The categories associated with the contact.  *Format:* `ARRAY<STRING>.` | No | None |
| ChangeKey |	string |	Identifies the version of the contact. Every time the contact is changed, 'ChangeKey' changes as well. This allows Microsoft Exchange to apply changes to the correct version of the object. |	No |	None |
| Children |	array |	The names of the contact's children. *Format:* `ARRAY<STRING>.` |	No |	None |
| CompanyName |	string |	The name of the contact's company. |	No |	None |
| Department |	string |	The contact's department. |	No |	None |
| CreatedDateTime |	datetime |	The time the contact was created. |	No |	None |
| LastModifiedDateTime |	datetime |	The time the contact was modified. |	No |	None |
| DisplayName |	string |	The contact's display name. |	No |	None |
| EmailAddresses |	array |	The contact's email addresses. *Format:* `ARRAY<STRUCT<Name : STRING, Address : STRING>>. ` |	No	 | None |
| FileAs |	string |	The name the contact is filed under. |	No |	None |
| Generation |	string |	The contact's generation. |	No |	None |
| GivenName	 | string |	The contact's given name. |	No |	None |
| HomeAddress |	object |	The contact's home address. *Format:* `STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING>.` | 	No |	None |
| HomePhones |	array |	The contact's home phone number(s). *Format:* `ARRAY<STRING>.` | 	No |	None |
| Id |	string |	The contact's unique identifier. |	No |	None |
| ImAddresses |	array |	The contact's instant messaging (IM) addresses. *Format:*  `ARRAY<STRING>.` |	No |	None |
| Initials |	string |	The contact's initials. |	No |	None |
| JobTitle |	string |	The contact's job title. |	No |	None |
| Manager |	string |	The name of the contact's manager. |	No |	None |
| MiddleName |	string |	The contact's middle name. |	No |	None |
| MobilePhone1 |	string |	The contact's mobile phone number. |	No |	None |
| NickName |	string |	The contact's nickname. |	No |	None |
| OfficeLocation |	string |	The location of the contact's office. |	No |	None |
| OtherAddress |	object |	Other addresses for the contact. *Format:* `STRUCT<Street : STRING, City : STRING, State : STRING, CountryOrRegion : STRING, PostalCode : STRING>.` | 	No |	None |
| ParentFolderId |	string |	The id of the contact's parent folder. |	No |	None |
| PersonalNotes |	string |	The user's notes about the contact. |	No |	None |
| Profession |	string |	The contact's profession. |	No |	None |
| SpouseName |	string |	The name of the contact's spouse. |	No |	None |
| Surname |	string	| The contact's surname. |	No |	None |
| Title |	string |	The contact's title. |	No |	None |
| YomiCompanyName |	string |	The phonetic Japanese company name of the contact. |	No	| None |
| YomiGivenName |	string |	The phonetic Japanese given name (first name) of the contact. |	No |	None |
| YomiSurname |	string |	The phonetic Japanese surname (last name) of the contact. |	No |	None |
| ODataType |	string |	Data type of the current folder. |	No |	None |
| puser	 | string |	User id. | 	No |  	None |
| ptenant |	string  | 	Tenant id. |	No |  	None |


## JSON representation

```json
{
  "AssistantName": "string",
  "Birthday": "String (timestamp)",
  "BusinessAddress": { "@odata.type": "Microsoft.OutlookServices.PhysicalAddress" },
  "BusinessHomePage": "string",
  "BusinessPhones": ["string"],
  "Categories": ["string"],
  "ChangeKey": "string",
  "Children": ["string"],
  "CompanyName": "string",
  "Department": "string",
  "CreatedDateTime": "String (timestamp)",
  "LastModifiedDateTime": "String (timestamp)",
  "DisplayName": "string",
  "EmailAddresses": [{ "@odata.type": "Microsoft.OutlookServices.EmailAddress" }],
  "FileAs": "string",
  "Generation": "string",
  "GivenName": "string",
  "HomeAddress": { "@odata.type": "Microsoft.OutlookServices.PhysicalAddress" },
  "HomePhones": ["string"],
  "Id": "string (identifier)",
  "ImAddresses": ["string"],
  "Initials": "string",
  "JobTitle": "string",
  "Manager": "string",
  "MiddleName": "string",
  "MobilePhone1": "string",
  "NickName": "string",
  "OfficeLocation": "string",
  "OtherAddress": { "@odata.type": "Microsoft.OutlookServices.PhysicalAddress" },
  "ParentFolderId": "string",
  "PersonalNotes": "string",
  "Profession": "string",	
  "SpouseName": "string",
  "Surname": "string",
  "Title": "string",
  "YomiCompanyName": "string",
  "YomiGivenName": "string",
  "YomiSurname": "string",
  "ODataType": "#Microsoft.OutlookServices.Contact",
  "puser": "string(identifier)",
  "ptenant": "string(identifier)"
}
```

## Sample 


```json
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD4AAA=","CreatedDateTime":"2021-02-03T00:10:10Z","LastModifiedDateTime":"2021-02-03T00:10:10Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACWB","Categories":”["Orange Category", "Green Category", "Blue Category"]”,"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":” 1970-01-01T08:00:00Z“,"FileAs":"Cooper, Sheldon B ","DisplayName":"Pradeep Gupta","GivenName":”Sheldon”,"Initials":null,"MiddleName":”Benjamin”,"NickName":”Superman”,"Surname":null,"Title":”Mr.”,"YomiGivenName":”Sheldon”,"YomiSurname":”Cooper”,"YomiCompanyName":”MICROSOFT”,"Generation":”Sr.”,"ImAddresses":["sip:PradeepG@M365x723843.OnMicrosoft.com"],"JobTitle":”VP, OUTLOOK SERVICES”,"CompanyName":”MICROSOFT”,"Department":”Outlook Services”,"OfficeLocation":”Building 7”,"Profession":”Manager”,"BusinessHomePage":”microsoft.com”,"AssistantName":”Pepper Pots”,"Manager":”Nick Fury”,"HomePhones":”["+1 (650) 555-1000"]”,"MobilePhone1":null,"BusinessPhones":”["+1 (465) 776-6852 X66822"]”,"SpouseName":”Lois Cooper”,"PersonalNotes":" This is a note about a contact. This contact is important ","Children":”[“Steve”],"EmailAddresses":[{"Name":"Pradeep Gupta","Address":"PradeepG@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD3AAA=","CreatedDateTime":"2021-02-03T00:10:10Z","LastModifiedDateTime":"2021-02-03T00:10:10Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACV3","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Joni Sherman","GivenName":null,"Initials":”S.B.C”,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":["sip:JoniS@M365x723843.OnMicrosoft.com"],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Joni Sherman","Address":"JoniS@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD2AAA=","CreatedDateTime":"2021-02-03T00:10:09Z","LastModifiedDateTime":"2021-02-03T00:10:09Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVv","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Executives","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":[],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Executives","Address":"Executives@CohoLLC5.onmicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD1AAA=","CreatedDateTime":"2021-02-03T00:10:09Z","LastModifiedDateTime":"2021-02-03T00:10:09Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVo","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Nestor Wilke","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":["sip:NestorW@M365x723843.OnMicrosoft.com"],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Nestor Wilke","Address":"NestorW@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVD0AAA=","CreatedDateTime":"2021-02-03T00:10:09Z","LastModifiedDateTime":"2021-02-03T00:10:09Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVd","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Isaiah Langer","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":["sip:IsaiahL@M365x723843.OnMicrosoft.com"],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Isaiah Langer","Address":"IsaiahL@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDzAAA=","CreatedDateTime":"2021-02-03T00:10:08Z","LastModifiedDateTime":"2021-02-03T00:10:08Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVX","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Adele Vance","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":["sip:AdeleV@M365x723843.OnMicrosoft.com"],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Adele Vance","Address":"AdeleV@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDyAAA=","CreatedDateTime":"2021-02-03T00:10:08Z","LastModifiedDateTime":"2021-02-03T00:10:08Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVR","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Megan Bowen","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":["sip:MeganB@M365x723843.OnMicrosoft.com"],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Megan Bowen","Address":"MeganB@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDxAAA=","CreatedDateTime":"2021-02-03T00:10:07Z","LastModifiedDateTime":"2021-02-03T00:10:08Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVH","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Legal Team","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":[],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Legal Team","Address":"Legal@CohoLLC5.onmicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDwAAA=","CreatedDateTime":"2021-02-03T00:10:07Z","LastModifiedDateTime":"2021-02-03T00:10:07Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACVB","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Lynne Robbins","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":["sip:LynneR@M365x723843.OnMicrosoft.com"],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Lynne Robbins","Address":"LynneR@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
{"Id":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwBGAAAAAACePVwnVQLQQo3igsKUUNIPBwDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAADDb9In4fFjSKy7cc0yk1OaAAAAAVDvAAA=","CreatedDateTime":"2021-02-03T00:10:07Z","LastModifiedDateTime":"2021-02-03T00:10:07Z","ChangeKey":"EQAAABYAAADDb9In4fFjSKy7cc0yk1OaAAAAACU6","Categories":[],"ParentFolderId":"AAMkADY0MTRiMDMyLWYyY2QtNGNmYi05MTZmLWE0MWQ5OTVkMTY3NwAuAAAAAACePVwnVQLQQo3igsKUUNIPAQDDb9In4fFjSKy7cc0yk1OaAAAAAAEOAAA=","Birthday":null,"FileAs":"","DisplayName":"Lidia Holloway","GivenName":null,"Initials":null,"MiddleName":null,"NickName":null,"Surname":null,"Title":null,"YomiGivenName":null,"YomiSurname":null,"YomiCompanyName":null,"Generation":null,"ImAddresses":["sip:LidiaH@M365x723843.OnMicrosoft.com"],"JobTitle":null,"CompanyName":null,"Department":null,"OfficeLocation":null,"Profession":null,"BusinessHomePage":null,"AssistantName":null,"Manager":null,"HomePhones":[],"MobilePhone1":null,"BusinessPhones":[],"SpouseName":null,"PersonalNotes":"","Children":[],"EmailAddresses":[{"Name":"Lidia Holloway","Address":"LidiaH@M365x723843.OnMicrosoft.com"}],"HomeAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Home'"},"BusinessAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Business'"},"OtherAddress":{"Type":"Microsoft.OutlookServices.PhysicalAddressType'Other'"},"ODataType":"#Microsoft.OutlookServices.Contact","puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"}
```