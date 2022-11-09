# PREVIEW Organizational Network Analysis template

- [Overview](#Overview)
- [Installing Pre-reqs](#Installing-Pre-reqs)
- [Synapse Pipeline Packages](#Synapse-Pipeline-Packages)
- [Synapse Pipeline Template](#Synapse-Pipeline-Template)
- [PBI Report Template](#PBI-Report-Template)
- [Preview Considerations](#Preview-Considerations)


## Overview

**Disclaimer: This template is in PREVIEW stage at this time. See [Preview Considerations](#Preview-Considerations) for more information. Feedback is welcome [here](https://aka.ms/ona-m365-feedback)**

The purpose of ONA is to harness information flows and team connectivity to unlock productivity, innovation, employee engagement and organizational change. This solution template enables customers to leverage ONA metrics from M365 data and analyze the networks within.


**After you follow these steps, you will have a Power BI dashboard related to Organizational Network Analysis, like the one shown below.**

![](Images/0.png) 

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

## Synapse Pipeline Packages

1.  After the pre-reqs are complete, navigate to the Synapse workspace just created

![](Images/2.1.png)

2.  Open the Synapse Studio

![](Images/2.2.png)

3.  In the Synapse Studio, select the sixth icon on the left to go to the Manage page. Click on Workspace packages and then upload the following two required packages:
- [Graphframes.whl](https://github.com/microsoft/adb2spark/blob/main/graphframes-0.8.2-py3-none-any.whl)
- [GraphframesSpark.jar](https://spark-packages.org/package/graphframes/graphframes)
Confirm the Scala version of the pool matches to the packages

![](Images/2.3.png)
![](Images/2.4.png)

4.  Click on the Apache Spark pools and then in the three dots for more options -> Packages

![](Images/2.5.png)

5.  Select the workspace packages to enable use and click Apply. It will take some minutes to complete

![](Images/2.6.png)

## Synapse Pipeline Template

1.  Download the ONA pipeline template .zip from [here](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fona%2FSynapsePipelineTemplate)

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
Use the Storage Account created in the resource group (to get the URL, navigate to the resource group -> storage account -> Endpoints -> Data Lake Storage -> Primary endpoint)
If required, change the flags if only certain datasets should run.

![](Images/3.10.png)
![](Images/3.11.1.png)
![](Images/3.11.2.png)

12. Congratulations! You just triggered the MGDC pipeline! Once the admin consents to the request the data will be processed and delivered to your storage account.

![](Images/3.12.png)

13. You will see the data in the storage account.

![](Images/3.13.png)

## **PBI Report Template**
Below steps will help to link datasets that are generated using Synapse pipeline above to link to PowerBI 
Template. 
1. Download and install Microsoft Power BI Desktop if you don’t have it installed already on your machine. 
    - Link to download Download Microsoft Power BI Desktop from Official Microsoft Download Center. [here](https://www.microsoft.com/en-us/download/details.aspx?id=58494)
2. Download the pre-created PowerBI security report that can generate insights from data that is produced using Synapse pipeline in azure storage locations. Link to download PowerBI Report. [here](http://aka.ms/ona-m365-pbi)

3. Open the PowerBI file and click on Transform data → Data source settings

![](Images/43.png)

4. You will see data sources in the Data source settings page

![](Images/44.png)

5. Select one of the data source settings and click on Change Source.
    - Change the Storage account path in URL with right storage account that data is generated from synapse pipeline in the steps above. You can get the storage account that is used in Synapse template pipeline Step 11 above
    - Repeat changing storage account names to all Data sources in current file.

![](Images/45.png)

6. Two data sources you need to update the path with right Date and correct location values for the data generated for Sharing and Sites datasets.
    - Click on data sources
    - Change path with right date and GUID Values Example: (https://**StorageAccountName**.dfs.core.windows.net/output/users.csv **YYYY-MM-DD**) 
    
    After changing the paths your new path should be like below Example: (https://xyzabcpqr1234.dfs.core.windows.net/output/users.csv)

    - You can get location values by navigating to storage account 

![](Images/46.png)

7. Now we need to give the right storage account key / credentials for these data sources.
    - Click on Edit Permissions

        ![](Images/47-a.png)        

    - Click on Edit under credentials

        ![](Images/47-b.png)        

    - Enter the storage account key value

        ![](Images/47-c.png)        

    If you don’t know have storage key get the storage account key by navigating to storage account in azure portal (storage account → access keys)

        ![](Images/47-d.png)        

8. Congratulations, you are all set and will see that the report will be refreshed with the latest data

    ![](Images/0.png) 

**Overview Page**
From the Overview page, you can start analyzing the network graph created from the interactions of the M365 communication data, understand the flow of interactions between departments, have a glance at the raw data populating the graphs, and evaluate the amount of connections and interactions of the nodes. There are date and flags filters available for comparison.

Each node is a person. If a node interacts with another node, then they become a connection.
An interaction can be either of the following:
-  One email
-  One meeting of 5 or less participants
-  Eight chat messages

This blend of interactions is informed by investigations from Microsoft Research (MSR) using statistical insights from US-based Microsoft employees.
The two flags available to classify the nodes are the following and can be configured using parameters:
-  Bridge flag: Top 15 percentile betweenness index
-  Centrality flag: Top 15 percentile degree index 

**Node Analysis Page**
The Node Analysis page provides additional drill-down information of the interactions in the organization and insights on how people prefer to communicate. 

**Influence Analysis Page**
Influence – Explore influential connections: Measures the influence of nodes as being well-connected to others. A high score identifies that the node’s perspective will cascade to others efficiently. How to engage:
    -  Identify influencers
    -  Explore the profile of the influencers: Title, Department, Country
    -  Compare period vs period to analyze consistency

**Influence Analysis Page**
Network Size and Breadth – Empower inclusive networks: Rank the nodes based on their number of connections to identify isolated groups that may be lacking information or left apart.
    -  Identify siloes and communities that may not be interacting the most
    -  Compare period vs period to analyze consistency

**Betweenness Analysis Page**
Betweenness – Evaluate information flow: Capture the approximate betweenness of nodes as their probability to be on the information flow between two people. Helps to identify potential gatekeepers, change agents, or controllers, and to remove bottlenecks.
    -  Identify key bridges and their departments
    -  Analyze the correlation of betweenness and influence: 
        -  High betweenness and high influence usually imply a leader
        -  High betweenness and low influence may imply a bottleneck 

## **Preview Considerations**
This template is in PREVIEW stage at this time. The following considerations apply:
- The network graph visualizations in the Power BI template are limited to 1500 nodes
- The template has been tested in tenant sizes of <500 users
- The Betweenness index provides an approximation via sampling to avoid excesive compute effort and calculation time. This index might be replaced in the upcoming months