# Entity Sentiment Analysis template

- [Overview](#Overview)
- [Installing Pre-reqs](#Installing-Pre-reqs)
- [Synapse Pipeline Template](#Synapse-Pipeline-Template)
- [PBI Report Template](#PBI-Report-Template)
- [Feedback & Considerations](#Feedback-&-Considerations)


## Overview

The purpose of ESA is to track what topics people are discussing in internal communications and how they're discussing them. With this tool, you'll be able to detect potential security conflicts, monitor reception to particular events, and gather company-wide sentiment surrounding important concepts. This solution template enables customers to leverage ONA metrics from M365 data and analyze the entities and sentiments therein.

This template leverages a Contoso emails and teamschat datasets, as well as a completed Entity and Sentiment Analysis dataset based on Enron emails.

**After you follow these steps, you will have a Power BI dashboard related to Enitity Sentiment Analysis, like the one shown below.**

![](https://github.com/v-travhanes/dataconnect-solutions/blob/3c86c07cec44d809553c4c305c7241a03ecb5ae4/solutions/esa/Images/Welcome%20Page.png) 

## Installing Pre-reqs

If you do not have an MGDC app, please proceed to the detailed documentation [here](https://github.com/v-travhanes/dataconnect-solutions/blob/main/solutions/esa/PreRequisites/README.md)  

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

![](Images/storageBlobDataContributorAccessPicture1.png)


## Synapse Pipeline Template

1.  Download the ESA pipeline template .zip from [here](https://github.com/v-travhanes/dataconnect-solutions/tree/main/solutions/esa/SynapsePipelineTemplate)

2.  Follow the [Synapse Pipeline Integration instructions](https://github.com/v-travhanes/dataconnect-solutions/blob/main/solutions/esa/Synapse%20Pipeline%20Integration/README.md) to integrate the pipeline. 

3.  After completing the Pipeline Integration and running the pipeline, you will see the data in the storage account

![](Images/ADLOutputContainer.png)

## **PBI Report Template**

For setup instructions and usage, please refer to the documentation [here](https://github.com/v-travhanes/dataconnect-solutions/tree/main/solutions/esa/PBItemplate) 

Download the pre-created PowerBI security report that can generate insights from data that is produced using Synapse pipeline in azure storage locations. 

Link to download PowerBI template [here](https://github.com/v-travhanes/dataconnect-solutions/tree/main/solutions/esa/PBItemplate)

## **Feedback & Considerations**

Feedback is welcome [here](https://aka.ms/ona-m365-feedback)

The following considerations apply:
- There may be updates performed regularly to fit for adjustments and fixes 
