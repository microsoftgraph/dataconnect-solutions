---
title: "Microsoft Graph Data Connect OwnedDevices_v0 dataset"
description: "Use the OwnedDevices_v0 dataset to provide detailed information related to all the devices that are owned by each user in the organization."
author: "rimisra2"
ms.localizationpriority: high
ms.prod: "data-connect"
ms.custom: datasets:dataset-name
---

# OwnedDevices_v0 dataset

The OwnedDevices_v0 dataset provides detailed information related to all the devices that are owned by each user in the organization.

NOTE:

- Currently, the MGDC platform ONLY supports extracting valid users. Trying to extract invalid users will result in no data to be returned for such users. Valid users must have the following:
    * A valid mailbox (i.e. users having mailbox that is **not in inactive state**, **not soft-deleted** or **not hosted on-Prem**)
    * User account enabled (i.e. accountEnabled should not be set to false)
    * An exchange license
- The MGDC platform supports extraction of data for all valid users matching with the ADF pipeline's region. Hence, if the users' mailbox are residing in different regions, then multiple pipelines will need to be triggered in the respective ADF regions.

## Scenarios

The following are business scenarios that you can answer with this dataset:

- Analyse the state of the devices owned by the users
- Analyze the type (like Windows/linux/mac) and model of devices being used by the users in the company
- Analyze the actual owners of the devices registered
- Analyze the management channel of different devices in the org 
- Analyze the details of onPrem devices registered for every user that is the owner 

## Questions

The following are examples of questions that you can answer with this dataset:

- How many total devices are owned by the users of the tenant? 
- Who is the owner of a particular device? 
- Are all the device configurations reflected in the status of the device? 
- What are the details of a particular device (like operating system,version) owned by any user? 
- How many devices are of a particular configuration/type? 

## Joining with other datasets

The OwnedDevices_v0 dataset can be joined with relevant users, and other category datasets.

## Definitions

- Devices owned by a user can have certain configurations and specifications that are stored. The configs can be set up in any one of the management channels opted for the particular device. The details of the onPrem devices are also stored and can be retreived by the tenants. 

## Schema

| Name  | Type  |  Description  |  FilterOptions  |  FilterType  |
| ----------- | ----------- | ----------- | ----------- | ----------- |
| accountEnabled | boolean | True if the account is enabled, false otherwise. Default is true. | No | None |
| approximateLastSignInDateTime | datetime | The timestamp of last sign in that represents information in UTC time. | No | None |
| deviceCategory | string | User defined property set by intune to automatically add devices to groups and simplify managing devices. | No | None |
| deviceId | string | Unique identifier set by Azure Device Registration Service at the time of registration. This is an alternate key that can be used to refrence the device object. | No | None |
| deviceOwnership | string | Ownership of the device. This property is set by intune. Possible values are :- unkown, company, personal. | No | None |
| displayName | string | The display name for the device. | No | None |
| domainName | string | The on-prem domain name of hybrid azure AD joined devices. This property is set by intune. | No | None |
| enrollmentProfileName | string | Enrollment profile applied to the device. For example, apple device enrollment profile, Device enrollment - Corporate device identifilers, or Windows autopilot profile name. This property is set by intune. | No | None |
| enrollmentType | string | Enrollment type of the device. This property is set by intune. Possible values are :- unkown, userEnrollment, deviceEnrollmentManager, appleBulkWithUser, appleBulkWithoutUser, windowsAzureADJoin, windowsBulkuserless, windowsAutoEnrollment, windowsBulkAzureDomainJoin, windowsCoManagement. | No | None |
| extensionAttributes | object | Contains extension attributes 1-15 for the device. The individual extension attributes are not selectable. These properties are mastered in cloud and can be set during creation or update of a device object in Azure AD. FORMAT :- STRUCT<"extensionAttribute1":"String", "extensionAttribute2":"String", ... > | No | None |
| id | string | The unique identifier of the device | No | None |
| isCompliant | boolean | True if the device complies with Mobile Device Management(MDM) policies;otherwise, false. This can only be updated by intune for any device OS type or by an approved MDM app for Windows OS devices. | No | None |
| isManaged | boolean  | True if the device complies with Mobile Device Management(MDM) policies; otherwise, false. This can only be update by intune for any device OS type or by an approved MDM app for Windows OS devices. | No | None |
| isRooted | boolean | True if the decive is rooted; false if the device is jail broken. This can only be updated by intune. | No | None |
| managementType | string | Managemenet channel of the device. This property is set by Intune. Possible values are :- eas, mdm, easMdm, intuneClient, easIntuneClient, configurationManagerClient, configurationManagerClientMdm, configurationManagerClientMdmEas, unkown, jamf, googleCloudDevicePolicyController | No | None |
| manufacturer | string | Manufacturer of the device. | No | None |
| mdmAppId | string | Application identifier used to register device into MDM. | No | None |
| model | string | Model of the device. | No | None |
| onPremisesLastSyncDateTime | datetime | The last time at which the object was synced with the on-premises directory. | No | None |
| onPremisesSyncEnabled | boolean | True if this object is synced from an on-premises directory; false if this object was originally synced from an on-premises directory but is no longer synced; null if this object has never been synced from an on-premises directory (default). | No | None |
| operatingSystem | string | The type of operating system on the device. | No | None |
| operatingSystemVersion | string | Operating system version of the device. | No | None |
| profileType | string | The profile type of the device. Possible values: RegisteredDevice(default), SecureVM, Printer, Shared, IoT. | No | None |
| registrationDateTime | datetime | Date and Time of when device was registered. | No | None |
| systemLabels | array | List of labels applied to the device by the system. | No | None |
| trustType | string | Type of trust for the joined device. Possible values: WorkPlace(indicates bring your own personal devices), AzureAd(Cloud only joined devices), ServerAd(on-premises domain joined devices joined to Azure AD). | No | None |
| puser | string | User id. | No |   None |
| ptenant | string |  Tenant id. | No  |  None |



## JSON representation

```json
{  
"accountEnabled": "Boolean",  
"approximateLastSignInDateTime": "String (timestamp)",  
"deviceCategory": "String",  
"deviceId": "String",  
"deviceOwnership": "String",  
"displayName": "String", 
"domainName": "String",  
"enrollmentProfileName": "String",  
"enrollmentType": "String", 
"extensionAttributes":{"@odata.type":"microsoft.graph.onPremisesExtensionAttributes"},  
"id": "String (identifier)",  
"isCompliant": "Boolean",  
"isManaged": "Boolean", 
"isRooted": "Boolean", 
"managementType": "Boolean",  
"manufacturer": "String",  
"mdmAppId": "String",  
"model": "String",  
"onPremisesLastSyncDateTime": "String (timestamp)", 
"onPremisesSyncEnabled": "Boolean",  
"operatingSystem": "String",  
"operatingSystemVersion": "String",  
"profileType": "String",  
"registrationDateTime": "String (timestamp)",  
"systemLabels": ["String"],  
"trustType": "String", 
"puser": "String (identifier)",  
"ptenant": "String (identifier)"
} 
```

## Sample

```json
{"accountEnabled":true,"approximateLastSignInDateTime":"2022-05-17T18:54:41Z","deviceCategory":null,"deviceId":"159be45a-02d9-4a4e-92ff-94f9dc02f9d6","deviceOwnership":null,"displayName":"KEVIN-DESKTOP","domainName":null,"enrollmentProfileName":null,"enrollmentType":null,"id":"de217b7e-0c67-4ca2-b5b7-868f506090ed","isCompliant":null,"isManaged":null,"isRooted":null,"managementType":null,"manufacturer":null,"mdmAppId":null,"model":null,"onPremisesLastSyncDateTime":null,"onPremisesSyncEnabled":null,"operatingSystem":"Windows","operatingSystemVersion":"10.0.22581.200","profileType":"RegisteredDevice","registrationDateTime":"2022-04-06T22:00:54Z","systemLabels":[],"trustType":"Workplace","extensionAttributes":{"extensionAttribute1":null,"extensionAttribute2":null,"extensionAttribute3":null,"extensionAttribute4":null,"extensionAttribute5":null,"extensionAttribute6":null,"extensionAttribute7":null,"extensionAttribute8":null,"extensionAttribute9":null,"extensionAttribute10":null,"extensionAttribute11":null,"extensionAttribute12":null,"extensionAttribute13":null,"extensionAttribute14":null,"extensionAttribute15":null},"puser":"X530bf91-e844-4369-a808-e0d12b1008cd","ptenant":"Xe56195d-f07c-44f0-8108-40e4352e3e74","pAdditionalInfo":null,"datarow":0,"userrow":0,"pagerow":0,"rowinformation":{"errorInformation":null,"userReturnedNoData":false,"isUserSummaryRow":false,"userHasCompleteData":false}}
{"accountEnabled":true,"approximateLastSignInDateTime":"2023-07-13T10:43:45Z","deviceCategory":null,"deviceId":"466093f4-0fcc-4a29-aacc-c8802d6938f0","deviceOwnership":"Personal","displayName":"AndroidForWork_8/1/2022_7:41 PM","domainName":null,"enrollmentProfileName":null,"enrollmentType":"UserEnrollment","id":"2046c453-e1ea-411d-b487-0a559226906f","isCompliant":false,"isManaged":false,"isRooted":false,"managementType":"MDM","manufacturer":"OnePlus","mdmAppId":null,"model":"ONEPLUS A6010","onPremisesLastSyncDateTime":null,"onPremisesSyncEnabled":null,"operatingSystem":"AndroidForWork","operatingSystemVersion":"11.0","profileType":"RegisteredDevice","registrationDateTime":"2022-08-01T19:41:33Z","systemLabels":[],"trustType":"Workplace","extensionAttributes":{"extensionAttribute1":null,"extensionAttribute2":null,"extensionAttribute3":null,"extensionAttribute4":null,"extensionAttribute5":null,"extensionAttribute6":null,"extensionAttribute7":null,"extensionAttribute8":null,"extensionAttribute9":null,"extensionAttribute10":null,"extensionAttribute11":null,"extensionAttribute12":null,"extensionAttribute13":null,"extensionAttribute14":null,"extensionAttribute15":null},"puser":"X530bf91-e844-4369-a808-e0d12b1008cd","ptenant":"Xe56195d-f07c-44f0-8108-40e4352e3e74","pAdditionalInfo":null,"datarow":0,"userrow":0,"pagerow":0,"rowinformation":{"errorInformation":null,"userReturnedNoData":false,"isUserSummaryRow":false,"userHasCompleteData":false}}
{"accountEnabled":true,"approximateLastSignInDateTime":"2023-09-23T03:30:35Z","deviceCategory":null,"deviceId":"a811a5be-cbb4-4f3c-9835-452e5cf317b6","deviceOwnership":"Company","displayName":"MININT-KQBUP7L","domainName":null,"enrollmentProfileName":null,"enrollmentType":"OnPremiseCoManaged","id":"f0c70379-25c6-4fa7-b9b8-8ccdc8aedf52","isCompliant":true,"isManaged":true,"isRooted":false,"managementType":"MDM","manufacturer":"LENOVO","mdmAppId":"54b943f8-d761-4f8d-151e-9cea1846db5a","model":"11EVS09B00","onPremisesLastSyncDateTime":"2022-08-03T17:29:45Z","onPremisesSyncEnabled":true,"operatingSystem":"Windows","operatingSystemVersion":"10.0.22621.2283","profileType":"RegisteredDevice","registrationDateTime":"2022-08-03T17:12:59Z","systemLabels":[],"trustType":"ServerAd","extensionAttributes":{"extensionAttribute1":null,"extensionAttribute2":null,"extensionAttribute3":null,"extensionAttribute4":null,"extensionAttribute5":null,"extensionAttribute6":null,"extensionAttribute7":null,"extensionAttribute8":null,"extensionAttribute9":null,"extensionAttribute10":null,"extensionAttribute11":null,"extensionAttribute12":null,"extensionAttribute13":null,"extensionAttribute14":null,"extensionAttribute15":null},"puser":"X530bf91-e844-4369-a808-e0d12b1008cd","ptenant":"Xe56195d-f07c-44f0-8108-40e4352e3e74","pAdditionalInfo":null,"datarow":0,"userrow":0,"pagerow":0,"rowinformation":{"errorInformation":null,"userReturnedNoData":false,"isUserSummaryRow":false,"userHasCompleteData":false}}
{"accountEnabled":true,"approximateLastSignInDateTime":"2023-09-18T08:08:13Z","deviceCategory":null,"deviceId":"c63edf8d-cad6-4666-80d3-7f4c958926c8","deviceOwnership":"Company","displayName":"DESKTOP-N6Q31CA","domainName":null,"enrollmentProfileName":null,"enrollmentType":"AzureDomainJoined","id":"f2ac0c58-a44d-4ee0-ae01-223a79abb433","isCompliant":true,"isManaged":true,"isRooted":false,"managementType":"MDM","manufacturer":"LENOVO","mdmAppId":"54b943f8-d761-4f8d-151e-9cea1846db5a","model":"20Y0S0282Y","onPremisesLastSyncDateTime":null,"onPremisesSyncEnabled":null,"operatingSystem":"Windows","operatingSystemVersion":"10.0.22621.2283","profileType":"RegisteredDevice","registrationDateTime":"2023-02-23T21:49:08Z","systemLabels":[],"trustType":"AzureAd","extensionAttributes":{"extensionAttribute1":null,"extensionAttribute2":null,"extensionAttribute3":null,"extensionAttribute4":null,"extensionAttribute5":null,"extensionAttribute6":null,"extensionAttribute7":null,"extensionAttribute8":null,"extensionAttribute9":null,"extensionAttribute10":null,"extensionAttribute11":null,"extensionAttribute12":null,"extensionAttribute13":null,"extensionAttribute14":null,"extensionAttribute15":null},"puser":"X530bf91-e844-4369-a808-e0d12b1008cd","ptenant":"Xe56195d-f07c-44f0-8108-40e4352e3e74","pAdditionalInfo":null,"datarow":0,"userrow":0,"pagerow":0,"rowinformation":{"errorInformation":null,"userReturnedNoData":false,"isUserSummaryRow":false,"userHasCompleteData":false}}
{"accountEnabled":true,"approximateLastSignInDateTime":"2023-09-28T12:00:00Z","deviceCategory":null,"deviceId":"c8ca294b-e9b7-4eb9-94c9-415b1edc2ef2","deviceOwnership":"Personal","displayName":"DESKTOP-PC","domainName":null,"enrollmentProfileName":null,"enrollmentType":"UserEnrollment","id":"d84c43ef-bf95-4341-85f6-e0d6d84f7c3f","isCompliant":false,"isManaged":false,"isRooted":false,"managementType":"MDM","manufacturer":"HP","mdmAppId":null,"model":"HP Pavilion Desktop TP01-1xxx","onPremisesLastSyncDateTime":null,"onPremisesSyncEnabled":null,"operatingSystem":"Windows","operatingSystemVersion":"10.0.22000.588","profileType":"RegisteredDevice","registrationDateTime":"2023-09-28T11:50:24Z","systemLabels":[],"trustType":"Workplace","extensionAttributes":{"extensionAttribute1":null,"extensionAttribute2":null,"extensionAttribute3":null,"extensionAttribute4":null,"extensionAttribute5":null,"extensionAttribute6":null,"extensionAttribute7":null,"extensionAttribute8":null,"extensionAttribute9":null,"extensionAttribute10":null,"extensionAttribute11":null,"extensionAttribute12":null,"extensionAttribute13":null,"extensionAttribute14":null,"extensionAttribute15":null},"puser":"X530bf91-e844-4369-a808-e0d12b1008cd","ptenant":"Xe56195d-f07c-44f0-8108-40e4352e3e74","pAdditionalInfo":null,"datarow":0,"userrow":0,"pagerow":0,"rowinformation":{"errorInformation":null,"userReturnedNoData":false,"isUserSummaryRow":false,"userHasCompleteData":false}} 
```