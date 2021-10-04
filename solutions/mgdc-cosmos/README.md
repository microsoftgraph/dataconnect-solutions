# MGDC with Azure Cosmos DB

## Table of contents
* [Tutorial Overview](#tutorial-overview)
* [Prerequisites](#prerequisites)
* [Create and Configure Azure Cosmos DB](#create-and-configure-azure-cosmos-db)
* [Update Azure Key Vault](#update-azure-key-vault)
* [Update Synapse Workspace](#update-synapse-workspace)
    * [Add Workspace Package to Spark Pool](#add-workspace-package-to-spark-pool)
    * [Import Python Notebook](#import-python-notebook)
    * [Import Pipeline](#import-pipeline)
    * [Add Trigger](#add-trigger)
* [Execute Pipeline](#execute-pipeline)
* [Query Data in Cosmos DB](#query-data-in-cosmos-db)

## Tutorial Overview

This tutorial will provide an example of how to load [Graph Data Connect](https://docs.microsoft.com/en-us/graph/data-connect-concept-overview)
(GDC) to gain insights into a Cosmos DB Grmelin API Graph database. By doing this, you will learn the key steps and Azure technologies required to build your own GDC based Graph database.  

You will learn how to:
Take GDC data already loaded into Azure Synapse and model and load the data into a CosmosDB Gremlin API

## Prerequisites

To complete this lab, you need the following:

- Microsoft Azure subscription
  - If you do not have one, you can obtain one (for free) here: [https://azure.microsoft.com/free](https://azure.microsoft.com/free/)
  - The account used to perform the set up must have the [**Contributor** role for the subscription granted to it](https://docs.microsoft.com/en-us/azure/role-based-access-control/elevate-access-global-admin),
    in order to be able to create the various infrastructure components described below
  - The Azure subscription must be in the same tenant as the Office 365 tenant as Graph Data Connect will only export 
    data to an Azure subscription in the same tenant, not across tenants.
- Office 365 tenancy
  - If you do not have one, you obtain one (for free) by signing up to the [Office 365 Developer Program](https://developer.microsoft.com/office/dev-program).
  - Multiple Office 365 users with emails sent & received
  - Access to at least two accounts that meet the following requirements:
  - One of the two accounts must be a global tenant administrator & have the **global administrator** role granted (just one account)
- Workplace Analytics licenses
  - Access to the Microsoft Graph data connect toolset is available through [Workplace Analytics](https://products.office.com/en-us/business/workplace-analytics), 
    which is licensed on a per-user, per-month basis.
  - To learn more please see [Microsoft Graph data connect policies and licensing](https://docs.microsoft.com/en-us/graph/data-connect-policies)

> NOTE: The screenshots and examples used in this lab are from an Office 365 test tenant with fake email from test users. 
> You can use your own Office 365 tenant to perform the same steps. No data is written to Office 365. 

The tutorial assume that you already have graph data connect in Azure Synapse. For an example of how to load that data into Azure Synapse you can refer to the [Coversation Lineage Tutorial](https://github.com/microsoftgraph/dataconnect-solutions/tree/main/solutions/conversation-lineage).

## Create and Configure Azure Cosmos DB

1. Open a browser and navigate to your Azure Portal at [https://portal.azure.com](https://portal.azure.com)
2. In the search bar type **Azure Cosmos DB** and then Click on **Azure Cosmos DB** in the Services list.
3. Click on Create then Click on the Create button in the section labled  **Gremlin (Graph)**.
4. Select your prefered Subscription, Resource Group and Location. Type in the name you'd like to use for your Cosmos DB instance. Record this name as you will need it later in the tutorial.
5. Choose your prefered pricing option then Click on Review and Create.
6. From the Overview page of the Azure Cosmos DB instance you just created click on Data Explorer.
7. Click on New Graph.
8. Select Create New and enter a Database id and a Graph id and make a record of the value. You will need them later in the tutorial.
9. Choose data throughput that will meet your needs.
10. For the Partition Key enter in **/pk**
11. Click OK

## Update Azure Key Vault

1. In your Azure Keyvault You will need to add following keys:
2. gremlinEndpoint - Value: wss://CosmosDBInstanceName.gremlin.cosmos.azure.com:443/  (CosmosDBInstanceName is the name of your Cosmos DB instance.)
3. gremlinUsername - Value: /dbs/databaseid/colls/graphid  (Use the database id and graph id you entered in the steps above.)
4. gremlinPassword - Value: CosmosDBPrimaryKey  (You can find this by click on Keys on the Cosmos DB Overview screen in the Portal.)


## Update Synapse Workspace


### Add Workspace Package to Spark Pool
   
1. Download the file: [gremlinpython-3.5.1-py2.py3-none-any.whl](https://github.com/microsoftgraph/dataconnect-solutions/blob/main/solutions/mgdc-cosmos/packages/gremlinpython-3.5.1-py2.py3-none-any.whl)
2. In your Synapse Worksapce click on **Manage** then click on **Workspace Packages**.
3. Click on Upload at the Top.
4. In the dialog box on the right click on the folder icon. Navigate to the **gremlinpython-3.5.1-py2.py3-none-any.whl** file you downloaded and click open.
5. Click on the **Upload** button at the button of the dialog box.
6. In the Azure portal navigate to the OVerview page for your Synapse Workspace.
7. Click **Apache Spark pools** in the left menu bar, then click on the spark pool you've previously created.
8. Click on **Packages** in the left menu bar.
9. Click on **Select from Workspace packages**.
10. Chick the box next to **gremlinpython-3.5.1-py2.py3-none-any.whl** the click **Select**.
11. Click **Save** at the top.
   
   
### Import Python Notebook

1. Download the file: [MGDCToCosmosDB.ipynb](https://github.com/microsoftgraph/dataconnect-solutions/blob/main/solutions/mgdc-cosmos/arm/notebook/MGDCToCosmosDB.ipynb)
2. Inside your Azure SYnapse workspace click on the **Develop** icon.
3. Click on the + symbol then click on Import.
4. Navigate to and select File 1 then click Open

   
### Import Pipeline
   
1. Download the file: [PL_MGDC_CosmosDB.zip](https://github.com/microsoftgraph/dataconnect-solutions/blob/main/solutions/mgdc-cosmos/arm/pipeline/PL_MGDC_CosmosDB.zip)
2. Inside your Azure SYnapse workspace click on the Integrate Icon.
3. Click on the + symbol then click on Import.
4. Navigate to and select PL_MGDC_CosmosDB.zip then click Open
5. Open the pipeline ** MGDCToCosmosDB and Update the following pipeline parameters:
   a. sql_database_name - Set this to the name of your dedicated SQL pool.
   b. sql_server_name - Set this to the name of your Azure Synapse Workspace
   c. keyvault_name - Set this ot the name of your Keyvault.

### Add Trigger

1. In the Synapse workspace Click the **Integrate** icon, then click on **MGDCToCosmosDB** pipeline. 
2. Click on **Trigger** then **New/Edit**.
3. in The Choose trigger... drop down select **New**
4. Fill out the fields in the tigger with your prefered values then Click **OK*

## Execute Pipeline

1. In the Synapse workspace Click the **Integrate** icon, then click on **MGDCToCosmosDB** pipeline. 
2. Click on **Trigger** then **Trigger Now.**

## Query Data in Cosmos DB

1. In the Azure Portal, from the Overview page of the Azure Cosmos DB instance you click on **Data Explorer**.
2. Exand your Database and Graph and click on the line that says **Graph**. 
3. You either click on **Load Graph** to do some basic exploration of the data or execute a gremlin query to get something more specific. Ex. g.V('Office365-User').as('b').bothE().as('e').select ('b', 'e').  Office365-User would be a user that's available on your MGDC data set.

## Setting Up Linkcurious for Cosmos DB

It is highly recommended that you use a robust graph visualization tool such as [Linkcurious](http://linkurio.us/) for navigating the data. You can find instructions for setting up Linkcurious for Cosmos DB [here](https://doc.linkurio.us/admin-manual/latest/configure-cosmos/). You will need the information you recorded earlier to in the tutorial to configure Linkcurious.

There are other graph visualization tools available if you decide not to use Linkcurious.



