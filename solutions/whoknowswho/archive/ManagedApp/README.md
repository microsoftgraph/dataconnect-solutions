# Azure Managed Applications

We will work through a sample that covers all three components:

1. We use Azure Data Factory (ADF) with copy activity to move data from Office 365 to your target ADLS instance.
2. We then have an Azure web app that reads the data at the target ADLS instance and outputs intelligent data.

Before we begin exploring the sample application, here are a few resources to get you started with the involved technologies:

- [Azure Data Factory](https://docs.microsoft.com/en-us/azure/data-factory/)
- [Azure Data Lake Analytics](https://docs.microsoft.com/en-us/azure/data-lake-analytics/)
- [Azure ARM Templates](https://azure.microsoft.com/en-us/resources/templates/)
- [Azure ARM Template Samples](https://github.com/Azure/azure-quickstart-templates)
- [Azure Managed App](https://docs.microsoft.com/en-us/azure/managed-applications/)
- [Azure Managed App Samples](https://github.com/Azure/azure-managedapp-samples/tree/master/samples)

## Prerequisites

- Visual Studio 2017
- Office 365 tenant with Azure subscription - The tenant should have users with data in their mailboxes.
- [Azure AD Powershell](https://docs.microsoft.com/en-us/powershell/azure/active-directory/install-adv2?view=azureadps-2.0)
- [Azure Powershell](https://docs.microsoft.com/en-us/powershell/azure/install-azurerm-ps)

## Create and publish an Office 365 powered Azure managed application:

The instructions below will help you create and publish an Azure managed application internally.
For reference: [Publish a managed application for internal consumption](https://docs.microsoft.com/en-us/azure/managed-applications/publish-service-catalog-app).

### Step 1: Package the web application

Open the **./src/WhoKnowsWho.sln** solution in Visual Studio 2017. This solution contains the web application which will consume and process the data in the Azure Data Lake Store created by Project MSGraphDataConnect.

#### Create the package

1. Right-click the **WhoKnowsWho** solution in **Solution Explorer** and choose **Restore NuGet Packages**.
2. Select the **WhoKnowsWho** project in **Solution Explorer**. Select the **Build** menu, then **Publish WhoKnowsWho**.
3. Select the **WebPackage** publishing profile and select **Publish**.

This should generate a **WhoKnowWho.zip** file in the `ManagedApp` directory, unless you specified a different output directory in the publishing profile.

#### Upload the package to blob storage

In this step we'll create a storage account and upload the **WhoKnowsWho.zip** file as a blob. This will allow us to include the web application as part of the Azure managed application we'll create later.

1. Follow the steps in [Create a storage account](https://docs.microsoft.com/en-us/azure/storage/common/storage-quickstart-create-account?tabs=portal) to create a general-purpose storage account.

2. Follow the steps in the [Create a container](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-portal#create-a-container) section and the [Upload a block blob](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-portal#upload-a-block-blob) section of [Quickstart: Upload, download, and list blobs using the Azure portal](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-portal) to upload **WhoKnowWho.zip**.

   > **Note:** Be sure to select **Blob (anonymous read access for blobs only)** in the **Public access level** dropdown when creating the container.

3. Take a note of the **WhoKnowWho.zip** blob **URL** value.

### Step 2: Create an Application in your tenant

For Office 365 LinkedService you need to provide an AAD application in your company's tenant (azure marketplace app publisher tenant). This application is different from the destination service principal. The destination service principal belongs to the customer tenant where the resources are being deployed and it's provided to your app via parameters by the customer during installation. Although, if you are deploying a service catalog app or an ARM template directly (for e.g. sample [ARMTemplates](../ARMTemplates)), your company tenant and installer tenants are same and you can technically use the same service principal for Office365 LinkedService as well as ADLS account & LinkedService.

1. Follow these [instructions](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal#create-an-azure-active-directory-application) to create an app registration in your tenant.

2. Add yourself as the owner of the application.

   ![](../docs/images/managedapp-appowners.png)

3. Take note of [AppId, Secret Key](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal#get-application-id-and-authentication-key) and your [TenantId.](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal#get-tenant-id)

   > **NOTE:** While creating credentials set the expiry to "Never Expire". Otherwise all installed instances of your azure marketplace application will fail once the creds expire.

### Step 3: Create the app template

Create an ARM template that defines the resources to deploy with the managed application (refer to [mainTemplate.json](mainTemplate.json)).

If you look at the **mainTemplate.json**, it consists of three main sections:

#### Parameters

Contains the list of parameters whose values will be provided by the user.

| Parameter name | Description |
|----------------|-------------|
| `WebSiteName` | The website name, used as the prefix in the url of the published web app. For example: `<websitename>.azurewebsites.net` |
| `DestinationServicePrincipalAadId` | The Azure Active Directory ID of the service principal to be granted access to the destination Data Lake store |
| `DestinationServicePrincipalId` | The app ID of the service principal that has access to the destination Data Lake store |
| `DestinationServicePrincipalKey` | The app secret of the service principal that has access to the destination Data Lake store |
| `TriggerStartTime` | UTC date in `YYYY-MM-ddT00:00:00Z` format |


#### Variables 

Contains the list of variables. Please go through all the variables. You should update all the fields marked below unless marked as *(Optional)*.

| Variable name | Description |
|----------------|-------------|
| `sourceLinkedServicePrincipalId` | The App Id for the SPN created in Step 2 |
| `sourceLinkedServicePrincipalKey` | The Secret for the SPN created in Step 2 |
| `sourceLinkedServicePrincipalTenantId` | The TenantId for the SPN created in Step 2 |

#### Resources

Contains the list of resources that will be deployed as a part of the managed app creation.

Below are few of the resources that will be deployed as a part of the **mainTemplate.json** explained briefly.

| Resource name | Description |
|---------------|-------------|
| `AuditStorageAccount` | Storage account to store all audit logs |
| `DestinationAdlsAccount` | Creates the destination Data Lake store in the customer's subscription used in the ADF pipeline for the data output. The account also creates `diagnosticSettings` with `AuditStorageAccount` as the store to collect `audit` and `requests` logs. |
| `DataFactory` | Creates the ADF pipeline that copies data from Office 365 to the newly created destination ADLS (`DestinationAdlsAccount` that was created above) |
| `WebApp` | Creates the web app that uses data stored in the newly created destination ADLS. Sample: [web app](https://github.com/Azure/azure-managedapp-samples/tree/master/samples/201-managed-web-app)|

The data factory has couple of interesting resources of it's own.

| Resource name | Description |
|---------------|-------------|
| `SourceLinkedService` | Creates the link to Office 365 which is used as the source of the data extraction. Using service principal supplied by the source ADLS owner. |
| `DestinationLinkedService` | Creates the link to the newly created destination ADLS, using service principal supplied by the customer deploying this template. |
| `*InputDataset` | You should change the structure in this resource to match the table and columns that you would like to extract. In this template we are trying to extract messages and events. For contacts and users refer [basic-sample](../ARMTemplates/basic-sample)|
| `*OutputDataset` | Corresponds to the `DestinationAdlsAccount` where we wanted the data to be copied to. |
| `Pipeline` | The Copy activity pipeline that copies the data from source Office 365 to the destination ADLS. Sample [copy activity](https://docs.microsoft.com/en-us/azure/data-factory/load-azure-data-lake-store)|
| `PipelineTriggers` | Contains settings to ensure the copy pipeline can be scheduled to run periodically. Sample: [tumbling window trigger](https://docs.microsoft.com/en-us/azure/data-factory/how-to-create-tumbling-window-trigger)|

#### Enable tracking resources for your template

Follow instructions at https://aka.ms/aboutinfluencedrevenuetracking to register unique GUID and update mainTemplate.json to enable tracking resources.
This is a required step to avoid application package validation error during publishing and it is required for applications in the service catalog (internal organization consumption) and to the Azure marketplace (external consumption). 

#### Optional properties

In order to make your pipeline more relevant, see [capabilities](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities)  for [user selection](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#user-selection), [filters](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#filters), other [datasets](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#datasets) and [data regions](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#data-regions).

### Step 4: Create the UI definition

Define the user interface elements for the portal when deploying the managed application (refer to [createUiDefinition.json](createUiDefinition.json)). The Azure portal uses the **createUiDefinition.json** file to generate the user interface for users who create the managed application. You define how users provide input for each parameter. You can use options like a drop-down list, text box, password box, and other input tools. To learn how to create a UI definition file for a managed application, see [Get started with CreateUiDefinition](https://docs.microsoft.com/en-us/azure/managed-applications/create-uidefinition-overview).

The values of the parameters defined in **mainTemplate.json** are supplied through the UI generated by **createUiDefinition.json** when the managed application is being created.

### Step 5: Deploy managed app

1. Open the **./ManagedApp/mainTemplate.json** file.
2. Locate the `webAppRemote` value. Change this value to the URL of the **WhoKnowsWho.zip** blob you created above.
3. Save the file.
4. Create a new ZIP file named **app.zip** that contains **./ManagedApp/mainTemplate.json** and **./ManagedApp/createUiDefinition.json**.

Use `scripts/DeployManagedApp.ps1` to deploy the managed app. Specify a value for **-ArtifactStagingDirectory** or for **-PackageFileUri**. **ArtifactStagingDirectory** is the local folder from where **app.zip** will be uplaoded. **PackageFileUri** is the URL value of the uploaded **app.zip** (if **app.zip** is already uploaded via the script or manually).

```shell
.\Scripts\DeployManagedApp.ps1 -ResourceGroupLocation "eastus2" -ArtifactStagingDirectory "E:\managedApp"
```

**OR**

```shell
.\Scripts\DeployManagedApp.ps1 -ResourceGroupLocation "West Central US" -PackageFileUri "https://samplestorage.blob.core.windows.net/appcontainer/app.zip"
```

The script automates the following steps:

#### Upload the app.zip

More details under the section [Packages the Files](https://docs.microsoft.com/en-us/azure/managed-applications/publish-service-catalog-app#package-the-files) in [Publish a managed application for internal consumption](https://docs.microsoft.com/en-us/azure/managed-applications/publish-service-catalog-app) for packaging the template files and uploading them to a blob storage.

#### Assign a user group or application

Create a user group or application for managing the resources on behalf of a customer by following the steps under the section [Create the managed application definition](https://docs.microsoft.com/en-us/azure/managed-applications/publish-service-catalog-app#create-the-managed-application-definition) in [Publish a managed application for internal consumption](https://docs.microsoft.com/en-us/azure/managed-applications/publish-service-catalog-app)

Get the role definition ID by following the steps in [Get the role definition ID](https://docs.microsoft.com/en-us/azure/managed-applications/publish-service-catalog-app#get-the-role-definition-id).

#### Create the managed application definition

Create the managed application definition using [`New-AzureRmManagedApplicationDefinition`](https://docs.microsoft.com/en-us/powershell/module/azurerm.resources/new-azurermmanagedapplicationdefinition?view=azurermps-6.0.0)

> **NOTE:** Step 5 is going to deploy managed app for internal users. This step will differ when you are [publishing your official app to azure marketplace](https://docs.microsoft.com/en-us/azure/managed-applications/publish-marketplace-app).

### Step 6: Install the managed application

You can create the managed application by following the steps listed below.

1. Run the `Scripts\GetAppInstallationParameters.ps1` script with no parameters. Save these values to use during the installation of the managed app.

   > **NOTE:** The script automates [creation of an Azure Active Directory application](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal#create-an-azure-active-directory-application) and [gets application id and authentication key](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-create-service-principal-portal#get-application-id-and-authentication-key).

1. Go to Azure Portal and choose **Managed Applications** from **All Services**.

1. Click on **Add** and you will see the Managed Application definition that we created above.

1. Select the Managed App definition that you want to create and click on **Create**.

1. On the **Basics** screen, select your subscription and either create a new resource group or use an existing one, then select **OK**.
   > **NOTE:** Please make sure that the location selected is one of the [Azure regions](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#data-regions) we support, since we are currently not available worldwide. For example "East US 2"

1. On the **Web App Settings** screen, enter the **Website name** value generated by the **GetAppInstallationParameters.ps1** script, then select **OK**.

1. On the **Data Factory Settings** screen enter the corresponding values from the output of the **GetAppInstallationParameters.ps1** script, then select **OK**.

1. On the **Summary** screen, wait for the validation to complete and select **OK**.

The deployment of the app starts and once it completes you will be able to see it in the dashboard.

### Step 7: Try it out

Click on the app and in the overview section you will see two resource groups. Click on the managed resource group.

You will notice that all the resources mentioned in the ARM template have been created successfully.

See here if you want to [Run](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Azure-Data-Factory-Quick-Links#running-an-adf-pipeline), [Schedule](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Azure-Data-Factory-Quick-Links#schedule-a-pipeline), [Monitor](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Azure-Data-Factory-Quick-Links#monitor-a-pipeline) a pipeline or [Approve a Data Access Request](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Approving-a-data-access-request)

## Using the sample web app

1. Open your browser and browse to `https://<websitename>.azurewebsites.net`, where `<websitename>` is the value of **Website name** you provided during the installation of the managed application.

2. If prompted to login, use an account from your test tenant.

3. Accept the prompt advising that the app would like to sign you in and read your profile.

4. At the bottom of the page, enter one of your user's email address and click the search button.

### Web App Sample UX

![](../docs/images/web-app-sample-ux.png)
