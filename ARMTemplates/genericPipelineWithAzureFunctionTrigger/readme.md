# Microsoft Graph data connect - One-Click Deployment

Deploys an Azure Storage Account and an Azure Data Factory with a Pipeline that uses Microsoft Graph data connect to extract Microsoft 365 data from your tenant. Then creates an Azure Function and defines a trigger on the storage account.

## Parameters

* **App Id:**
  The Application ID of an Azure Active Directory Application Registration. This application doesn't require any API permissions. The solution simply grants its Service Principal access to the location where the exported file will be stored.

* **App Secret:**
  A secret for the Azure Active Directory's Application Registration.

* **Data Set:**
  The name of the dataset you wish to export.

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2FVIvaInsightsARM%2FARMTemplates%2FgenericPipelineWithAzureFunctionTrigger%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>
