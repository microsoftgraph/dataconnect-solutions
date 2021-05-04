# Run samples via Azure Powershell to copy Office 365 data
This tutorial should help you get started on how to run the samples provided in this directory. The samples here are for showcasing how to ingest data from Office 365. The instructions below help you experiment with these samples using [Azure ARM template](https://github.com/Azure/azure-quickstart-templates). 

You perform the following steps in this tutorial:

> * Create an application in your tenant
> * Deploy an Azure Resource Group
> * Create a data factory.
> * Create linked services. 
> * Create a pipeline.
> * Run the pipeline. 

## Prerequisites
**Azure PowerShell**
* [Install Azure PowerShell on Windows](https://docs.microsoft.com/en-us/powershell/azure/install-azurerm-ps?view=azurermps-6.8.1)
* [Install Azure Powershell on macOS or Linux](https://docs.microsoft.com/en-us/powershell/azure/install-azurermps-maclinux?view=azurermps-6.8.1)

## Definition

For any sample, you can take a look at the `.\*-sample\azuredeploy.json` file to understand what azure resources we are deploying.

> **NOTE:** The samples here are not full managed app. When a customer decides to install your managed app from Azure marketplace, your app during installation, will perform steps similar to the below steps to provision resources in customer's Azure subscription. For a full managed app take a look at `ManagedApp` at root of this repo.

## Deployment 

### Step 1
Fire below command in shell of your choice and follow the instructions to login to your azure subscription.

```powershell
Login-AzureRmAccount
```

----------

### Step 2
Fill in **all** the parameters in `.\*-sample\azuredeploy.parameters.json`. Refer `.\*-sample\azuredeploy.json` for description of the parameters.

Below parameters are common across all samples and can be filled using the [GetAppInstallationParameters.ps1](https://github.com/OfficeDev/ManagedAccessMSGraph/blob/master/ManagedApp/Scripts/GetAppInstallationParameters.ps1) PowerShell script. 

> **NOTE:** You don't need to run the script for every sample you try or every deployment. Same values can be re-used for every sample and every deployment.

- `destinationServicePrincipalId` is the Application Id of your service principal.
- `destinationServicePrincipalKey` is the Authentication Key you generated for your service principal.
- `destinationServicePrincipalAadId` is the Object Id of your service principal.

> **NOTE:** If you are using Azure Storage as the destination sink, please refer to this documentation on how to properly configure your SPN: https://docs.microsoft.com/en-us/azure/storage/common/storage-auth-aad-rbac?toc=%2fazure%2fstorage%2fblobs%2ftoc.json

### Step 3

Finally, deploy the ARM template and create the ADF pipeline by running the deployment script.

```shell
.\Deploy-AzureResourceGroup.ps1 -ResourceGroupLocation "eastus2" -ArtifactStagingDirectory *-sample
```

> **NOTE:** This will create all the resources in a resource group with the same name as the folder name of the sample (e.g. `ArtifactStagingDirectory`) For example, if you deploy `basic-sample` you'll find all the resource created in a resource group named `basic-sample`. If you make changes to `azuredeploy.json` and re-run the same command without changing the folder name, it will simply update the resources instead of creating new ones.

