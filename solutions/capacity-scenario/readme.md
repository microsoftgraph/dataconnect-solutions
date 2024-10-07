# Capacity Scenario template setup

- [Capacity Scenario template setup](#capacity-scenario-template-setup)
  - [Overview](#overview)
  - [Installing Pre-reqs](#installing-pre-reqs)
  - [Synapse pipeline template](#synapse-pipeline-template)
  - [PBI report template](#pbi-report-template)


## Overview

Capacity scenario is a storage use case powered by our available Sharepoint datasets. This allows customers to better understand how their storage is being used by providing meaningful insights and analytics offered by the SharePoint Sites and SharePoint Files datasets

**IMPORTANT NOTE:** This scenario is not currently available publicly. It is coming soon. The details provided here are for informational purposes only.

**After you follow these steps, you will have a great set of Power BI dashboards related to SharePoint Capacity Scenario, like the one shown below.**
![](Images/Image_1.png) 

## Installing Pre-reqs
The first step to running this template would be to create an application in the tenant and use that appId and secret to setup the other required resources.

1. Navigate to app registrations in your subscription.

![](Images/1.png)

2. Register a new application

![](Images/2.png)

3. Save the application id (In the screenshot, the one ending in 9826). Navigate to API permissions

![](Images/3.png)

4. Select "Microsoft Graph" from the Add permission flyout

![](Images/4.png)

5. Select "Application permissions -> Applications -> Application.Read.All"

![](Images/5.png)

6. Explicitly Grant consent for the new permissions

![](Images/6.png)

7. Verify that that the status shows as granted for the new Application.Read.All permission

![](Images/7.png)

8. In the App, navigate to **Owners** and add an owner who will be responsible for running the pipeline. This is required for the pipeline to run successfully.

![](Images/6a.PNG)
![](Images/6b.PNG)


9. Navigate to "Certificates and secrets" in the left pane and click on "New client secret"

![](Images/8.png)

10. Provide a description and add a secret

![](Images/9.png)

11. Copy the value of this new secret and save it securely before navigating away from this page

![](Images/10.png)

12. Use this link to initiate the setup of the pre-requisites. Use the appid and secret created in the 
previous steps. Custom deployment - Microsoft Azure [here](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2FARMTemplates%2FMGDC%20Extraction%20Pre%20Reqs%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ)

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2FARMTemplates%2FMGDC%20Extraction%20Pre%20Reqs%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>

The link above sets up the pre-requisites to using the capacity analytics template, which are:

- Create a Synapse Workspace
- Create a Spark Pool for the Synapse workspace
- Create a storage account for the extracted data
- Grant permission to the Synapse workspace & the MGDC Service Principal to the storage account as Blob Data Contributor
- Create an Azure SQL Server
- Create a sample database within the Azure SQL Server.

By clicking on the above button (or navigating to the linked URL), users will be brought to the Azure portal on the Custom deployment page.

On that screen, on top of providing information about the resource group and region to deploy the components into, they will need to provide the following information:

- Application Id to be used by MGDC (from step #3, ending in 9826)
- Application secret for that app
- A new password for the Azure SQL Server

Once all required information has been provided, click on the Review + create button at the bottom of the page:


![](Images/11.png)

This will validate that the information provided to the template is correct. Once the information has been validated, click on the Create button at the bottom of the page.

![](Images/12.png)

This will initiate the deployment. It should normally take about 5 minutes for the whole deployment to 
complete.

## Synapse pipeline template

1.  After the pre-reqs are complete, navigate to the Synapse workspace just created

![](Images/21.png)

2.  Open the Synapse Studio

![](Images/22.png)

3.  Navigate to "Integrate -> Add new resource -> **Browse gallery**"

![](Images/23.png)

4.  Search for "SharePoint" and select the "Unlock Capacity Analytics and Insights for SharePoint and OneDrive using Microsoft 365 Datasets" template and Continue

![](Images/24.PNG)

5.  Create the new **Target** Linked services required by this pipeline

![](Images/25.png)

6.  Provide the parameters of the Linked Service
    - Select Authentication Type = Service Principal 
    - Use the storage account name, SPN id and secret (SPN key) from the pre-req steps above
    - Test Connection and then click on Create

![](Images/25a.png)


7.  Repeat the Linked Service creation steps for the **source** Linked Service

![](Images/25b.png)

8.  Select "Open Pipeline"

![](Images/26.png)


9. Click on "Publish All" to validate and publish the pipeline

![](Images/27.png)


10. Review the changes and click Publish

![](Images/27a.PNG)

11. Verify that the pipeline has been successfully published

![](Images/27b.PNG)

12. Trigger the pipeline. **Note: You should not trigger the pipeline until the Files dataset becomes available**

![](Images/27c.PNG)

13. Provide the required parameters - StartTime, EndTime, StorageAccount, and StorageContainer created by the pre-req steps above (Note: names are case sensitive)

![](Images/27d.PNG)

14. You ran the pipeline created the request for the SharePoint Sites and SharePoint Files datasets which will be used to power the report! The data will be processed and delivered to your storage account.

15.  You will see the data in the storage account. The report will pull the data from the uploaded capacity directory in your storage 

![](Images/28.png)

## PBI report template

Note: In order to draw data to the Power BI report, you first need to run the Synapse pipeline as instructed in the previous section.

The report will always pull the latest data from your Azure Data Lake Storage Gen 2. The report is to been as a template and is to be modified based on your requirements 

Below steps will help to link datasets that are generated using Synapse pipeline above to link to Power BI 
Template. 
1. Download and install Microsoft Power BI Desktop if you don’t have it installed already on your machine. 
    - Link to download Download Microsoft Power BI Desktop from Official Microsoft Download Center. [here](https://www.microsoft.com/en-us/download/details.aspx?id=58494)
2. Download the pre-created Power BI Capacity report that can generate insights from data that is produced using Synapse pipeline in azure storage locations. Link to download PowerBI Report [here](https://go.microsoft.com/fwlink/?linkid=2290317)

![](Images/42.png)

3. Open the Power BI file and click on Transform data → Data source settings

![](Images/43.png)

4. You will see 1 data sources in the Data source settings page. Change the Storage account path in URL with right storage account that data is generated from synapse pipeline in the steps above. You can get the storage account that is used in Synapse template pipeline Step 6 above

![](Images/44.png)

![](Images/45.png)



6. Now we need to give the right storage account key / credentials for these data sources.
    - Click on Edit Permissions

        ![](Images/47-a.png)        

    - Click on Edit under credentials

        ![](Images/47-b.png)        

    - Enter the storage account key value

        ![](Images/47-c.png)        

    - If you don’t know have storage key get the storage account key by navigating to storage account in azure portal (storage account → access keys)

        ![](Images/47-d.png)        

7. Congratulations, you are all set and will see that the report will be refreshed with the latest data

    ![](Images/Image_1.PNG) 

8. If you see any error or data is not being refreshed then please make sure your entered right storage account details, path along with credentials in data source settings

Additional Notes:
- The Power BI report will always pull the **latest data** copied during the Synapse pipeline run.
- The Power BI report in this document is simply a template and is to be modified based on your requirements.