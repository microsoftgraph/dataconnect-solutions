# Description

Deploys an Azure Storage Account and an Azure Data Factory with a Pipeline that uses Microsoft Graph data connect to extract Microsoft 365 data from your tenant.

# Deployment

## Create a New App Registration in Azure Active Directory

1. Navigate to you Azure Active Directory Portal.
2.	In the left navigation, click on App registrations.
3.	In the top menu bar, click on the New registration button:
 
4.	You will be brought to the Register and application form. 
5.	In the Name text box, provide your app a name.
6.	Leave the other settings to their default values and click on the Register button at the bottom of the form.
 
7.	On the App Registration overview page, take note of the Application (client) ID value of your app registration. You will need it later.
 
8.	In the left navigation, click on Certificates & secrets.
9.	In the middle section of the page, click on the New client secret button.
 
10.	In the Add a client secret blade, leave the default settings in, and simply click on the Add button at the bottom.
 
11.	This will generate a new App secret. In the middle of the screen, make sure to take note of the Value field of the newly generated secret. You will need it for later.
 

## Deploy the components using ARM

1.	Click on the Deploy to Azure button. This will bring you to the Azure portal.

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2FARMTemplates%2FgenericPipeline%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>

2.	Provide your Azure account credentials if prompted.
3.	 Once authenticated, you will be presented with the Custom deployment form:
  a.	Select a subscription from the Subscription drop down list.
  b.	Select an existing resource group from the drop down list or create a new one using the Create new link.
  c.	Select the region in which you wish to deploy your Azure components from the Region drop down list.
  d.	Provide the Application Id of the App registration created at from the steps above in the App Id textbox.
  e.	Provide the Application Secret of the App registration generated from the steps above in the App Secret textbox.
  f.	Select the data set you wish to extract from your pipeline from the Data Set drop down list.
  g.	Create on the Review + create button at the bottom of the form.	 
4.	Confirm that the information you’ve entered is correct, then click on the Create button at the bottom of the form to initiate the deployment process.
5.	You will be brought to the Deployment screen where you will be able to see the deployments progress:
 
6.	After a few minutes, the deployment process will complete and you will be able to click on the Go to resource group button to access components of the resource group:
