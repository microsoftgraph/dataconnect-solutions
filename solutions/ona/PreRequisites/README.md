# Installing Pre-reqs

- [Application Registration](#Application-Registration)
- [Azure Resources Deployment](#Azure-Resources-Deployment)
- [Synapse Pipeline Packages](#Synapse-Pipeline-Packages)


## Application Registration

The first step to running this template would be to create an application in the tenant and use that appId and secret to setup the other required resources.

1. Go to Graph Data Connect [Portal](https://portal.azure.com/#view/Microsoft_Azure_GraphDataConnect/GraphApplication.ReactView) and click Add to start a new registration.

![](Images/1.1.png)

2. Enter the subscription id and resource group. If you have an existing storage account created, proceed to the next step. Else, create a new one by clicking on "Create New" and fill out the information as shown below. Click on Review and Create. Once the storage account is created, you will be redirected to the app registration form. Proceed to the next step.

![](Images/1.2.png)

3. Select the Storage Account Uri with .dfs.core.windows.net extension from the dropdown.

![](Images/1.3.png)

4. If you have an existing Application, proceed to the next step. Else, create a new application by clicking on Create New and fill out the information as show below. Click on Register. Once the application has been created, you will be redirected to the app registration form. Proceed to the next step.

![](Images/1.4.png)

5. Enter the application ID (name of your application), description and select the publish type. Click on Next to proceed to the next step.

![](Images/1.5.png)

6. Select the following datasets for your respective template and select "All" columns.
- BasicDataSet_v0.CalendarView_v0
- BasicDataSet_v0.User_v1
- BasicDataSet_v0.Message_v1
- BasicDataSet_v0.TeamChat_v1
(If you are registering the application for Employee Productivity Data Tables, add BasicDataset_v0.TeamsCallRecords_v1 as well).

![](Images/1.6.png)

7. Click on Review + Create and your application should be visible in the landing page. For more information on updating the application information, visit the [wiki](https://learn.microsoft.com/en-us/graph/app-registration). Ensure that the consent has been granted for your application by your global administrator on the [MGDC authorization portal](https://admin.microsoft.com/Adminportal/Home?#/Settings/MGDCAdminCenter).

8. Look for the application on Azure portal.

![](Images/1.8.png)

9. Save the application id (In the screenshot, the one ending in e430). Navigate to API permissions in the Manage menu on the left

![](Images/1.9.png)

10. Select "Microsoft Graph" from the Add permission flyout

![](Images/1.10.png)

11. Select "Application permissions -> Applications -> Application.Read.All"

![](Images/1.11.png)

12. Explicitly Grant consent for the new permissions

![](Images/1.12.png)

13. Verify that that the status shows as granted for the new Application.Read.All permission

![](Images/1.13.png)

14. Navigate to "Certificates and secrets" in the left pane and click on "New client secret"

![](Images/1.14.png)

15. Provide a description and add a secret

![](Images/1.15.png)

16. Copy the value of this new secret and save it securely before navigating away from this page

![](Images/1.16.png)

## Azure Resources Deployment

1. Use this link to initiate the setup of the pre-requisites. Use the appid and secret created in the 
previous steps. Custom deployment - Microsoft Azure [here](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fona%2FARMTemplate%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ)

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fona%2FARMTemplate%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>

The link above sets up the pre-requisites to using the information oversharing template, which are:

- Create a Synapse Workspace
- Create a Spark Pool for the Synapse workspace
- Create a storage account for the extracted data
- Grant permission to the Synapse workspace & the Microsoft Graph Data Connect Service Principal to the storage account as Blob Data Contributor

By clicking on the above button (or navigating to the linked URL), users will be brought to the Azure portal on the Custom deployment page.

2. On that screen, on top of providing information about the resource group and region to deploy the components into, they will need to provide the following information:

- Application Id to be used by Microsoft Graph Data Connect (from step #3, ending in e430)
- Application secret for that app

3. Once all required information has been provided, click on the Review + create button at the bottom of the page. This will validate that the information provided to the template is correct. 

![](Images/1.17.png)

4. Once the information has been validated, click on the Create button at the bottom of the page.

![](Images/1.18.png)

This will initiate the deployment. It should normally take about 5 minutes for the whole deployment to complete.

## Synapse Pipeline Packages

1.  Navigate to the Synapse workspace just created and open the Synapse Studio.

![](Images/2.1.png)

2.  In the Synapse Studio, select the sixth icon on the left to go to the Manage page. Click on Apache Spark pools and right-click the pool to select the Packages.

![](Images/2.2.png)
![](Images/2.3.png)

3. Upload the following file as a Required File and click Apply.

- [env](../SynapsePipelineTemplate/env.yml)

![](Images/2.4.png)
