# PREVIEW Organizational Network Analysis template

- [Overview](#Overview)
- [Installing Pre-reqs](#Installing-Pre-reqs)
- [Synapse Pipeline Template](#Synapse-Pipeline-Template)
- [PBI Report Template](#PBI-Report-Template)
- [Preview Considerations](#Preview-Considerations)


## Overview

**Disclaimer: This template is only available for customers in the Preview program. Also, the template is in Preview stage at this time. See [Preview Considerations](#Preview-Considerations) for more information. Feedback is welcome [here](https://aka.ms/ona-m365-feedback)**

The purpose of ONA is to harness information flows and team connectivity to unlock productivity, innovation, employee engagement and organizational change. This solution template enables customers to leverage ONA metrics from M365 data and analyze the networks within.


**After you follow these steps, you will have a Power BI dashboard related to Organizational Network Analysis, like the one shown below.**

![](Images/0.1.png) 

## Installing Pre-reqs
The first step to running this template would be to create an application in the tenant and use that appId 
and secret to setup the other required resources.

1. Navigate to app registrations in your subscription.

![](Images/1.1.png)

2. Register a new application

![](Images/1.2.png)
![](Images/1.3.png)

3. Save the application id (In the screenshot, the one ending in e430). Navigate to API permissions in the Manage menu on the left

![](Images/1.4.png)

4. Select "Microsoft Graph" from the Add permission flyout

![](Images/1.5.png)

5. Select "Application permissions -> Applications -> Application.Read.All"

![](Images/1.6.png)

6. Explicitly Grant consent for the new permissions

![](Images/1.7.png)

7. Verify that that the status shows as granted for the new Application.Read.All permission

![](Images/1.8.png)

8. Navigate to "Certificates and secrets" in the left pane and click on "New client secret"

![](Images/1.9.png)

9. Provide a description and add a secret

![](Images/1.10.png)

10. Copy the value of this new secret and save it securely before navigating away from this page

![](Images/1.11.png)

11. Use this link to initiate the setup of the pre-requisites. Use the appid and secret created in the 
previous steps. Custom deployment - Microsoft Azure [here](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fona%2FARMTemplate%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ)

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fona%2FARMTemplate%2Fazuredeploy.json?token=AATN3TJ6UQWU7TFMZ2R6ZW3ASL5JQ"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>

The link above sets up the pre-requisites to using the information oversharing template, which are:

- Create a Synapse Workspace
- Create a Spark Pool for the Synapse workspace
- Create a storage account for the extracted data
- Grant permission to the Synapse workspace & the MGDC Service Principal to the storage account as Blob Data Contributor

By clicking on the above button (or navigating to the linked URL), users will be brought to the Azure portal on the Custom deployment page.

On that screen, on top of providing information about the resource group and region to deploy the components into, they will need to provide the following information:

- Application Id to be used by MGDC (from step #3, ending in e430)
- Application secret for that app

Once all required information has been provided, click on the Review + create button at the bottom of the page:

![](Images/1.12.png)

This will validate that the information provided to the template is correct. Once the information has been validated, click on the Create button at the bottom of the page.

![](Images/1.13.png)

This will initiate the deployment. It should normally take about 5 minutes for the whole deployment to complete.

## Synapse Pipeline Template

1.  Download the ONA pipeline template .zip from [here](https://github.com/microsoftgraph/dataconnect-solutions/tree/main/solutions/ona/SynapsePipelineTemplate)

2.  In the Synapse Studio, select the fourth icon on the left to go to the Integrate page. Click on the "+" icon to Add new resource -> Import from pipeline template, and select the downloaded template

![](Images/3.1.png)

3.  Create the new linked services required by this pipeline

![](Images/3.2.png)

4.  Provide the parameters of the Linked Service 
        a. Select Authentication Type = Service Principal 
        b. Use the storage account name (starting with "onastore"), SPN id and secret (SPN key) from the pre-req steps above
        c. Test Connection and then click on Create

![](Images/3.3.png)

5.  Repeat the linked Service creation steps for the source linked service and select "Open Pipeline"

![](Images/3.4.png)

6.  Navigate to the Develop page (third icon on the left) -> ONA and ensure the notebook is attached to the onasynapsepool

![](Images/3.5.png)

7.  Click on "Publish All" to validate and publish the pipeline

![](Images/3.6.png)

8. Review the changes and click Publish

![](Images/3.7.png)

9. Verify that the pipeline has been successfully published

![](Images/3.8.png)

10. Trigger the pipeline

![](Images/3.9.png)

11. Provide the required parameters. Use one month per pipeline run. Use date format 'YYYY-MM-DD'.
Use the Storage Account created in the resource group (simply replace with the storage account name created in the resource group or to get the URL, navigate to the resource group -> storage account -> Endpoints -> Data Lake Storage -> Primary endpoint)
If required, change the flags if only certain datasets should run

![](Images/3.10.png)
![](Images/3.11.1.png)
![](Images/3.11.2.png)

12. Congratulations! You just triggered the MGDC pipeline! Once the admin consents to the request the data will be processed and delivered to your storage account

![](Images/3.12.png)

13. You will see the data in the storage account

![](Images/3.13.png)

## **PBI Report Template**

For setup instructions and usage, please refer to the documentation [here](https://github.com/microsoftgraph/dataconnect-solutions/tree/main/solutions/ona/PBItemplate) 
Download the pre-created PowerBI security report that can generate insights from data that is produced using Synapse pipeline in azure storage locations. Link to download PowerBI template. [here](http://aka.ms/ona-m365-pbi)

## **Preview Considerations**
This template is in PREVIEW stage at this time. The following considerations apply:
- There may be updates performed regularly to fit for adjustments and fixes 
- The network graph visualizations in the Power BI template are limited to 1500 nodes