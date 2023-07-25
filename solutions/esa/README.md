# Entity Sentiment Analysis template

- [Overview](#Overview)
- [Installing Pre-reqs](#Installing-Pre-reqs)
- [Synapse Pipeline Template](#Synapse-Pipeline-Template)
- [PBI Report Template](#PBI-Report-Template)
- [Feedback & Considerations](#Feedback-&-Considerations)


## Overview

The purpose of ESA is to track what topics people are discussing in internal communications and how they're discussing them. With this tool, you'll be able to detect potential security conflicts, monitor reception to particular events, and gather company-wide sentiment surrounding important concepts. This solution template enables customers to leverage ONA metrics from M365 data and analyze the entities and sentiments therein.

This template leverages an Enron emails dataset.

**After you follow these steps, you will have a Power BI dashboard related to Enitity Sentiment Analysis, like the one shown below.**

![](https://github.com/v-travhanes/dataconnect-solutions/blob/3c86c07cec44d809553c4c305c7241a03ecb5ae4/solutions/esa/Images/Welcome%20Page.png) 

## Installing Pre-reqs

If you do not have an MGDC app, please proceed to the detailed documentation [here](https://github.com/microsoftgraph/dataconnect-solutions/tree/main/solutions/ona/PreRequisites)  

If you already have an MGDC app and its secret, the automated deployment to Azure helps setup the required resources in 5 minutes. 

The link below sets up the Azure resource group for the template, which are:

- Create an ACS Language Resource
- Create a Synapse Workspace
- Create a Spark Pool for the Synapse workspace
- Create a storage account for the extracted data
- Grant permission to the Synapse workspace & the MGDC Service Principal to the storage account as Blob Data Contributor

Custom deployment - Microsoft Azure [here](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fv-travhanes%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fesa%2FARMTemplate%2Fazuredeploy.json)

<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fv-travhanes%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fesa%2FARMTemplate%2Fazuredeploy.json"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>

Provide Storage Blob Data Contributor access to the user who is developing the solution. The Synapse workspace should already have access with the automated deployment. 

![](Images/storageBlobDataContributorAccess.png)


## Synapse Pipeline Template

As a pre-requisite, ensure that the required packages are installed by following the [Synapse Pipeline Installation instructions](https://github.com/microsoftgraph/dataconnect-solutions/tree/ona-v2-doc-updates/solutions/ona#Synapse-Pipeline-Template)

1.  Download the ONA pipeline template .zip from [here](https://github.com/microsoftgraph/dataconnect-solutions/tree/main/solutions/ona/SynapsePipelineTemplate)

2.  In the Synapse Studio, select the fourth icon on the left to go to the Integrate page. Click on the "+" icon to Add new resource -> Import from pipeline template, and select the downloaded template

![](Images/3.1.png)

3.  Create the new linked services required by this pipeline

![](Images/3.2.png)

4.  Provide the parameters of the Linked Service 

        a. Select Authentication Type = Service Principal 

        b. Use the storage account name (starting with "onastore"), for Service Principal ID use the Application (client) ID), and for Service Principal key use the value from the secret of the application certificate. See screenshots below

![](Images/1.4.png)
![](Images/1.11.png)
        
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

Configure the Synapse Pipeline Package required as described [here](https://github.com/microsoftgraph/dataconnect-solutions/tree/main/solutions/ona/PreRequisites#Synapse-Pipeline-Packages)

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

Download the pre-created PowerBI security report that can generate insights from data that is produced using Synapse pipeline in azure storage locations. 

Link to download PowerBI template [here](http://aka.ms/ona-m365-pbi)

## **Feedback & Considerations**

Feedback is welcome [here](https://aka.ms/ona-m365-feedback)

The following considerations apply:
- There may be updates performed regularly to fit for adjustments and fixes 
- The network graph visualizations in the Power BI template are limited to 1500 nodes
