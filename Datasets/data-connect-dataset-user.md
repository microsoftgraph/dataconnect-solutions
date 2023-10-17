---
title: "Microsoft Graph Data Connect User_v0 dataset"
description: "Use the User_v0 dataset to provide user details stored for all the Azure Active Directory (Azure AD) user accounts that are created for a particular tenant."
author: "rimisra2"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# Microsoft Graph Data Connect User_v0 dataset

The User_v0 dataset provides user details stored for all the Azure Active Directory (Azure AD) user accounts that are created for a particular tenant.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyze users' information based on country, state, city, department, job title, skills, experience, age group.
- Know the last time each user was synced with the on-premises directory.
- Analyze users’ information based their office location, schools they attended.
- Derive users’ having specific skills, projects, responsibilities.
- Get list of users with their names, mail, business phone, job title, work location, birthday, address, etc.

## Questions

The following are examples of questions that you can answer with this dataset:

- Which users work from a particular office location?
- How many users have a particular language as their preferred language?
- How many users have been hired in the current financial year?
- Who are the users with a particular job title?

## Joining with other datasets

The User_v0 dataset can be joined with other datasets that can be mapped to the user id. This allows for analyzing user demographics and behavior across different Microsoft 365 applications, such as Outlook, Teams, Office, and more.

## Definitions

- **Azure Active Directory (Azure AD):** Azure AD is a cloud-based identity and access management service. Azure AD enables tenant's employees to access external resources, such as Microsoft 365, the Azure portal, and many other SaaS applications.
- **User:** Employee of any tenant for whom an Azure AD user account has been created by the tenant.

## Schema

| Name  | Type  |  Description  |  FilterOptions  |  FilterType  |
| ----------- | ----------- | ----------- | ----------- | ----------- |
| ExternalEmailAddress | string | The user's external email address. | No | None |
| Languages | array | The languages the user understands. *Format:* `ARRAY<STRING>.` | No | None |
| Name | string | The user's name.  | No | None |
| EmailAddresses | array | The user's email addresses. *Format:* `ARRAY<STRING>.` | No | None |
| PrimarySmtpAddress | string | The user's primary email address. | No | None |
| DisplayName | string | The user's name, as it will be displayed. | No | None |
| City | string | The city where the user works. | No | None |
| Company | string | The company where the user works. | No | None |
| Department | string | The department where the user works. | No | None |
| Fax | string | The user's fax number. | No | None |
| FirstName | string | The user's first name. | No | None |
| HomePhone | string | The user's home phone number. | No | None |
| Initials | string | The user's initials. | No | None |
| LastName | string | The user's last name. | No | None |
| MobilePhone | string | The user's mobile phone number. | No | None |
| Notes | string | Notes about the user. | No | None |
| Office | string | The office where the user works. | No | None |
| OtherFax | string | The user's other fax number. |  No | None |
| OtherHomePhone | string  | The user's other home phone number. | No | None |
| OtherTelephone | string | The user's other phone number. | No | None |
| Pager | string | The user's pager number. | No | None |
| Phone | string | The user's phone number. | No | None |
| PostalCode | string | The user's postal code. | No | None |
| PostOfficeBox | string | The user's post office box. | No | None |
| SimpleDisplayName | string | The user's simplified name, as it will be displayed. | No | None |
| StateOrProvince | string |  The state or province where the user works. | No | None |
| StreetAddress | string | The street address where the user works. | No | None |
| Title | string | The user's professional title. | No | None |
| UserPrincipalName | string | The user's principal name. | No | None |
| puser | string |  User id. | No | None |
| ptenant | string |  Tenant id. | No | None |

## JSON representation

```json
{
  "ExternalEmailAddress": "String",
  "Languages": ["String"],
  "Name": "String",
  "EmailAddresses": ["String"],
  "PrimarySmtpAddress": "String",
  "DisplayName": "String",
  "City": "String",
  "Company": "String",
  "Department": "String",
  "Fax": "String",
  "FirstName": "String",
  "HomePhone": "String",
  "Initials": "String",
  "LastName": "String",
  "MobilePhone": "String",
  "Notes": "String",
  "Office": "String",
  "OtherFax": "String",  
  "OtherHomePhone": "String",
  "OtherTelephone": "String",
  "Pager": "String",
  "Phone": "String",
  "PostalCode": "String",
  "PostOfficeBox": "String",
  "SimpleDisplayName": "String",
  "StateOrProvince": "String",
  "StreetAddress": "String",
  "Title": "String",
  "UserPrincipalName": "String",
  "puser": "String (identifier)",
  "ptenant": "String (identifier)"
}
```

## Sample


```json
{"Name":"PattiF","EmailAddresses":["SPO:SPO_a845fc87-df11-4d92-a65e-22e45711e7d5@SPO_027d8585-9664-42ed-ae2a-c9e9fddfda22","SIP:PattiF@M365x723843.OnMicrosoft.com","SMTP:PattiF@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"PattiF@M365x723843.OnMicrosoft.com","DisplayName":"Patti Fernandez","City":"Louisville","Company":"","Department":"Executive Management","Fax": "14085551000","FirstName":"Patti","HomePhone": "14085551000","Initials":"E.M.","LastName":"Fernandez","MobilePhone": "14085551000","Notes": "This is a note.","Office":"15/1102","OtherFax":”14085551000”,"OtherHomePhone": "14085551000","OtherTelephone": "14085551000","Pager","Phone":"+1 502 555 0144","PostalCode":"40223","PostOfficeBox": "PO BOX 42055","SimpleDisplayName": "John Doe","StateOrProvince":"KY","StreetAddress":"9900 Corporate Campus Dr., Suite 3000","Title":"President","UserPrincipalName":"PattiF@M365x723843.OnMicrosoft.com","Languages":["en-US"],"ExternalEmailAddress": "user@outlook.com","puser":"0409a7eb-588d-4871-b629-e33de72b8b0d","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"ChristieC","EmailAddresses":["SPO:SPO_77981e4a-4a56-4539-8806-3b8d58c23e21@SPO_027d8585-9664-42ed-ae2a-c9e9fddfda22","SIP:ChristieC@M365x723843.OnMicrosoft.com","SMTP:ChristieC@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"ChristieC@M365x723843.OnMicrosoft.com","DisplayName":"Christie Cline","City":"San Diego","Company":"Microsoft","Department":"Sales","Fax":"","FirstName":"Christie","HomePhone":"","Initials":"","LastName":"Cline","MobilePhone":"","Notes":"","Office":"131/2105","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"+1 858 555 0111","PostalCode":"92121","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"CA","StreetAddress":"9257 Towne Center Dr., Suite 400","Title":"Buyer","UserPrincipalName":"ChristieC@M365x723843.OnMicrosoft.com","Languages":["en-US"],"ExternalEmailAddress":null,"puser":"1715c984-a1ce-4483-b109-643041ef4469","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"Conf Room Baker","EmailAddresses":["SMTP:Baker@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"Baker@M365x723843.OnMicrosoft.com","DisplayName":"Conf Room Baker","City":"","Company":"","Department":"","Fax":"","FirstName":"","HomePhone":"","Initials":"","LastName":"","MobilePhone":"","Notes":"","Office":"","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"","PostalCode":"","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"","StreetAddress":"","Title":"","UserPrincipalName":"Baker@M365x723843.OnMicrosoft.com","Languages":[],"ExternalEmailAddress":null,"puser":"18dd806f-9613-4865-8664-6523e72a3e68","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"DiegoS","EmailAddresses":["SPO:SPO_8db9eb29-5e00-4196-8841-88290832d30d@SPO_027d8585-9664-42ed-ae2a-c9e9fddfda22","SIP:DiegoS@M365x723843.OnMicrosoft.com","SMTP:DiegoS@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"DiegoS@M365x723843.OnMicrosoft.com","DisplayName":"Diego Siciliani","City":"Birmingham","Company":"","Department":"HR","Fax":"","FirstName":"Diego","HomePhone":"","Initials":"","LastName":"Siciliani","MobilePhone":"","Notes":"","Office":"14/1108","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"+1 205 555 0108","PostalCode":"35243","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"AL","StreetAddress":"3535 Gradview Parkway Suite 335","Title":"HR Manager","UserPrincipalName":"DiegoS@M365x723843.OnMicrosoft.com","Languages":["en-US"],"ExternalEmailAddress":null,"puser":"3853937f-6f46-4fff-a141-1a18be24944e","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"JohannaL","EmailAddresses":["SPO:SPO_22856252-fd58-467a-aa06-1ab2d55ad010@SPO_027d8585-9664-42ed-ae2a-c9e9fddfda22","SIP:JohannaL@M365x723843.OnMicrosoft.com","SMTP:JohannaL@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"JohannaL@M365x723843.OnMicrosoft.com","DisplayName":"Johanna Lorenz","City":"Louisville","Company":"","Department":"Engineering","Fax":"","FirstName":"Johanna","HomePhone":"","Initials":"","LastName":"Lorenz","MobilePhone":"","Notes":"","Office":"23/2102","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"+1 502 555 0102","PostalCode":"40223","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"KY","StreetAddress":"9900 Corporate Campus Dr., Suite 3000","Title":"Senior Engineer","UserPrincipalName":"JohannaL@M365x723843.OnMicrosoft.com","Languages":["en-US"],"ExternalEmailAddress":null,"puser":"3eb5fed9-8c59-4eff-a9ea-ba2b5f1ac27f","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"Conf Room Crystal","EmailAddresses":["SMTP:Crystal@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"Crystal@M365x723843.OnMicrosoft.com","DisplayName":"Conf Room Crystal","City":"","Company":"","Department":"","Fax":"","FirstName":"","HomePhone":"","Initials":"","LastName":"","MobilePhone":"","Notes":"","Office":"","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"","PostalCode":"","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"","StreetAddress":"","Title":"","UserPrincipalName":"Crystal@M365x723843.OnMicrosoft.com","Languages":[],"ExternalEmailAddress":null,"puser":"46807cf7-735f-40d3-bb37-89f5bf2de5ac","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"Conf Room Stevens","EmailAddresses":["SMTP:Stevens@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"Stevens@M365x723843.OnMicrosoft.com","DisplayName":"Conf Room Stevens","City":"","Company":"","Department":"","Fax":"","FirstName":"","HomePhone":"","Initials":"","LastName":"","MobilePhone":"","Notes":"","Office":"","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"","PostalCode":"","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"","StreetAddress":"","Title":"","UserPrincipalName":"Stevens@M365x723843.OnMicrosoft.com","Languages":[],"ExternalEmailAddress":null,"puser":"563b3dd6-e564-45ec-ad2b-37edca5e7154","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"Conf Room Hood","EmailAddresses":["SMTP:Hood@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"Hood@M365x723843.OnMicrosoft.com","DisplayName":"Conf Room Hood","City":"","Company":"","Department":"","Fax":"","FirstName":"","HomePhone":"","Initials":"","LastName":"","MobilePhone":"","Notes":"","Office":"","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"","PostalCode":"","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"","StreetAddress":"","Title":"","UserPrincipalName":"Hood@M365x723843.OnMicrosoft.com","Languages":[],"ExternalEmailAddress":null,"puser":"63dfb181-7e5b-4344-aa70-254bed4470e6","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"LidiaH","EmailAddresses":["EUM:52004;phone-context=Demo_DialPlan.5a520b33-fee4-48cf-859c-2b8bf4f814c7","SPO:SPO_7b0ba7a0-69de-4ba2-87af-b7a0242cc98b@SPO_027d8585-9664-42ed-ae2a-c9e9fddfda22","SIP:LidiaH@M365x723843.OnMicrosoft.com","SMTP:LidiaH@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"LidiaH@M365x723843.OnMicrosoft.com","DisplayName":"Lidia Holloway","City":"Tulsa","Company":"","Department":"Engineering","Fax":"","FirstName":"Lidia","HomePhone":"","Initials":"","LastName":"Holloway","MobilePhone":"","Notes":"","Office":"20/2107","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"+1 918 555 0107","PostalCode":"74133","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"OK","StreetAddress":"7633 E. 63rd Place, Suite 300","Title":"Product Manager","UserPrincipalName":"LidiaH@M365x723843.OnMicrosoft.com","Languages":["en-US"],"ExternalEmailAddress":null,"puser":"6618944e-1fe9-4c03-955e-b1ebbf5737c9","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
{"Name":"AlexW","EmailAddresses":["EUM:52002;phone-context=Demo_DialPlan.5a520b33-fee4-48cf-859c-2b8bf4f814c7","SPO:SPO_3c29e6e4-9c3a-4bb9-8967-84f1a4484224@SPO_027d8585-9664-42ed-ae2a-c9e9fddfda22","SIP:AlexW@M365x723843.OnMicrosoft.com","SMTP:AlexW@M365x723843.OnMicrosoft.com"],"PrimarySmtpAddress":"AlexW@M365x723843.OnMicrosoft.com","DisplayName":"Alex Wilber","City":"San Diego","Company":"","Department":"Marketing","Fax":"","FirstName":"Alex","HomePhone":"","Initials":"","LastName":"Wilber","MobilePhone":"","Notes":"","Office":"131/1104","OtherFax":[],"OtherHomePhone":[],"OtherTelephone":[],"Pager":"","Phone":"+1 858 555 0110","PostalCode":"92121","PostOfficeBox":[],"SimpleDisplayName":"","StateOrProvince":"CA","StreetAddress":"9256 Towne Center Dr., Suite 400","Title":"Marketing Assistant","UserPrincipalName":"AlexW@M365x723843.OnMicrosoft.com","Languages":["en-US"],"ExternalEmailAddress":null,"puser":"84129d5d-1ae2-49a2-ba84-7e9a14901bc2","ptenant":"027d8585-9664-42ed-ae2a-c9e9fddfda22"} 
```