# Microsoft Graph data connect - One-Click Deployment

Deploys an Azure Storage Account and an Azure Data Factory with a Pipeline that uses Microsoft Graph data connect to extract Mailbox Settings from your tenant.

## Parameters

* **App Id:**
  The Application ID of an Azure Active Directory Application Registration. This application doesn't require any API permissions. The solution simply grants its Service Principal access to the location where the exported file will be stored.

* **App Secret:**
  A secret for the Azure Active Directory's Application Registration.

* **Data Set:**
  The name of the dataset you wish to export.

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FNikCharlebois%2FMicrosoftGraphDataConnectLab%2Fmain%2FARM%20Templates%2Fazuredeploy.json"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>
