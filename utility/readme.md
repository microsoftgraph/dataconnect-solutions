The MGDCUtility PowerShell module provides a set of utilities to help you get started with your Microsoft Graph Data Connect projects. It includes several PowerShell cmdlets which can be use to deploy & configure or generate reports about your environments. These cmdlets currently include:

## Get-MGdcEstimatedNumberOfItems

This cmdlet will connect to your Microsoft 365 tenant and estimate the number of items you could potentially be looking at exporting. It currently only supports estimating the Messages dataset. The parameters accepted by this cmdlet are:

| Name| Mandatory | Description |
| :-: |  :-: | :-- |
| AppId |	**True**	| Id of an app registration in the user’s tenant with the following App only permissions: <br /> *	Mail.ReadBasic.All (to count emails) <br /> *	GroupMember.Read.All (if specific groups are specified)<br />*	User.Read.All (to get the id of the users to call the reporting API) |
| TenantId |	**True** |	Id of the tenant to extract the information from.|
|Secret	| **True**	| The App Registration’s secret |
| Entity	| **True**	| Currently only accepts Messages as a value. |
| NumberOfDays	| False	| Specify how many days to go back to get the data. 7 is the default value. |
| GroupsID	| False	| List of ids from groups to extract the data from. Omitting this parameter will extract information from all users in the tenant by default. |
