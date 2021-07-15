# Conversation Lineage

## Table of contents
- [Tutorial Overview](#tutorial-overview)
- [Prerequisites](#prerequisites)
- [Set up the Synapse data processing pipelines](#set-up-the-synapse-data-processing-pipelines)
    - [Arm files deployment](#arm-files-deployment)
    - [Trigger deployment](#trigger-deployment)
    - [Sql deployment](#sql-deployment)
- [Architectural design](#architectural-design)
    - [Data Ingestion](#data-ingestion)
    - [Table Derivation](#table-derivation)  
    - [Sentiment and entities extraction from mails](#sentiment-and-entities-extraction-from-mails)
    - [PowerBI presentation](#powerbi-presentation)

  
## Tutorial Overview
This tutorial will provide you with an example of using [Graph Data Connect](https://docs.microsoft.com/en-us/graph/data-connect-concept-overview)
(GDC) to gain insights into an organization's communication patterns by analyzing Microsoft 365 data.  
By doing this, you will learn the key steps and Azure technologies required to build your own GDC based application.  
You will learn how to:
- extract and process Microsoft 365 data and run analytics on top of it using [Azure Synapse Analytics](https://docs.microsoft.com/en-us/azure/synapse-analytics/)
- process both historical data and future data on a daily basis using Azure Synapse triggers
- extract sentiment and NLP entities from conversations using [Azure Cognitive Services](https://azure.microsoft.com/en-us/services/cognitive-services/text-analytics/)
- visualize key insights using [PowerBI](https://docs.microsoft.com/en-us/power-bi/fundamentals/power-bi-overview)

## Prerequisites

To complete this lab, you need the following:

- Microsoft Azure subscription
  - If you do not have one, you can obtain one (for free) here: [https://azure.microsoft.com/free](https://azure.microsoft.com/free/)
  - The account used to perform the set up must have the [**global administrator** role granted to it](https://docs.microsoft.com/en-us/azure/role-based-access-control/elevate-access-global-admin),
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
> A copy of email data is extracted from all users in an Office 365 tenant and copied to an Azure Blob Storage 
> account that you maintain control over. Thus, you control who has access to the data within the Azure Blob Storage.
 
### Set up Graph Data Connect
For setting up Office 365 Tenant and enabling Graph Data Connect, please follow the steps from chapter `Excercise 1` 
from this GDC [tutorial](https://github.com/microsoftgraph/msgraph-training-dataconnect/blob/master/Lab.md#exercise-1-setup-office-365-tenant-and-enable-microsoft-graph-data-connect)

### Create an Azure AD application
Synapse pipelines need to interact with external Azure services in order to extract, process or store data. To be able
to do this, Synapse must authenticate itself while connecting to these services. For certain services the authentication
can be done via Managed Identity, however, for others a service principal is required.  
In this tutorial, Synapse will need a service principal to extract data from M365 and to connect to Azure Blob Storage.   
Therefore, you will need to create an Azure Active Directory app registration that will be used as the security principal:
1. Open a browser and navigate to your Azure Portal at [https://portal.azure.com](https://portal.azure.com)
2. Login using an account with global administrator rights to your Azure and Office 365 tenants.
3. Select **Azure Active Directory** (Azure AD) from the sidebar navigation.
4. Select **App registrations** from the **Manage** section of the sidebar on the left.
5. Select the **New registration** button:
   ![Create AAD app registration](./docs/environment_setup/create-aad-app-registration.png)
6. Use the following values to create a new Azure AD application and select **Register**:
  - **Name**: conversation-lineage-m365-reader
  - **Supported account types**: Accounts in this organizational directory only
  - **Redirect URI**: *Leave the default values*
7. Locate the **Application (client) ID** and copy it as you will need it later in this lab. This will be referred to as the *service principal ID*.
8. Locate the **Directory (tenant) ID** and copy it as you will need it later in this lab. This will be referred to as the *tenant ID*.
9. Select **Certificates & secrets** under **Manage** in the sidebar navigation.
10. Select the **New client secret** button. Leave **Description** empty, set **Expires** to `24 months` and choose **Add**.
   > You can choose different values for **Description** and **Expires** if you like. Ensure you keep a copy of the 
   > hashed key after it is saved, as it is needed later in the tutorial (to be saved in KeyVault). 
   > The hashed value will never be shown again, so if you forget it, you will need to create a new one!

   ![Screenshot of creating a password for an Azure AD application](./docs/environment_setup/create-service-principal-client-secret.png)

   > This will be referenced as the *service principal key*.
11. Using the sidebar navigation for the application, select **Owners**.
12. Ensure your account is listed as an owner for the application. If it isn't listed as an owner, add it.

### Create an Azure AD group
While ingesting data from M365 you have the option of retrieving the data for all users (having an Office 365 license),
or only for a specific set of users. To achieve this, create an Azure AD security group called `MGDC101 Members`.  
The steps required to create a group and add members to it are described [here](https://docs.microsoft.com/en-us/azure/active-directory/fundamentals/active-directory-groups-create-azure-portal).  
Once the group is created, add to it users for which the M365 data (profile, emails, calendar events, team chats) should
be processed. The users must have an Office 365 license, for their data to be actually retrieved.  
In the **Overview** page of the group, look for the **ObjectId** and copy its value as you will need it later.

### Create an Azure resource group
Create a resource group to hold all the Azure resources required for this project.  
In order to create a resources group please follow the steps described [here](https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/manage-resource-groups-portal#create-resource-groups).

### Create an Azure Storage account
Create a Storage account meant to store the raw data ingested from Microsoft 365, as well as intermediate data resulting
from transforming the input data.
In order to set it up please follow the instructions from the official documentation [here](https://docs.microsoft.com/en-us/azure/storage/common/storage-account-create?tabs=azure-portal).  
Now, grant the service principal created above a `Storage Blob Data Contributor` role for the Storage account.

### Create an Azure Cognitive Service
Create an instance of Azure Cognitive Services meant to be used for sentiment analysis of interactions between users.
From *Azure Cognitive Services* we'll use *Text Analytics* service.  
In order to set it up please follow the instructions from the official documentation [here](https://docs.microsoft.com/en-us/azure/cognitive-services/text-analytics/how-tos/text-analytics-how-to-call-api?tabs=synchronous#create-a-text-analytics-resource)
From *Text Analytics* service we'll use 2 provided api functions:
- [sentiment analysis](https://docs.microsoft.com/en-us/azure/cognitive-services/text-analytics/how-tos/text-analytics-how-to-sentiment-analysis?tabs=version-3-1)
- [named entity recognition](https://docs.microsoft.com/en-us/azure/cognitive-services/text-analytics/how-tos/text-analytics-how-to-entity-linking?tabs=version-3-1)

### Create an Azure Synapse workspace
Create the Synapse workspace used to process all the data.
For setting up a Synapse workspace, please follow the [official Synapse documentation](https://docs.microsoft.com/en-us/azure/synapse-analytics/get-started-create-workspace)
and make sure to use your resource group.

### Create a Synapse Spark pool
Certain more complex transformations of data will be performed using Apache Spark. This requires the computation
resources of a Spark cluster. Synapse provides an integrated Spark cluster via Spark pools.  

From Synapse Studio, create a Spark pool named `mgdc101synapse` by adapting the steps in [this tutorial](https://docs.microsoft.com/en-us/azure/synapse-analytics/quickstart-create-apache-spark-pool-studio).
This can also be [done from Azure portal](https://docs.microsoft.com/en-us/azure/synapse-analytics/quickstart-create-apache-spark-pool-portal).  
In order to keep costs low, assuming you will use a rather small input dataset, use small nodes and keep the number of
nodes to a minimum. Also, to enable automatic pausing after the pool is idle, go to `Pause Settings` (on the right of 
the pool name in the pools list), select "Enabled", and set the desired idle duration.

Please follow the next link to learn more about [Apache Spark in Synapse](https://docs.microsoft.com/en-us/azure/synapse-analytics/spark/apache-spark-overview).

### Create a Synapse dedicated SQL pool
In order to keep most data transformations as accessible as possible, SQL queries are used. Also, the data is exposed
to PowerBI for visualization in tabular format (more precisely as views). Therefore, Synapse dedicated SQL pools are 
used.  
The SQL pool is an analytics engine which offers both compute and storage capabilities.

From Synapse Studio, create a dedicated SQL pool named `synapsededicatesqlpool` by adapting the relevant steps from [this guide](https://docs.microsoft.com/en-us/azure/synapse-analytics/quickstart-create-sql-pool-studio).
This can also be [done from Azure portal](https://docs.microsoft.com/en-us/azure/synapse-analytics/quickstart-create-sql-pool-portal).  
In order to keep costs low, assuming you will use a rather small input dataset, set the "Performance level" of the pool
to the smallest available value (`DW100c`). The pool does not have the option to automatically shut down while idle, so
consider pausing it yourself while not in use. However, make sure this does not interfere with scheduled processing
via Synapse triggers (described below).

Please follow the next link to learn more about [Azure Synapse SQL architecture](https://docs.microsoft.com/en-us/azure/synapse-analytics/sql/overview-architecture)
and about [dedicated SQL pools](https://docs.microsoft.com/en-us/azure/synapse-analytics/sql-data-warehouse/sql-data-warehouse-overview-what-is).

### Create an Azure Key Vault
Create a KeyVault meant to store any sensitive pieces of information meant to be used by the Synapse pipelines.  
To create the vault, follow [this guide](https://docs.microsoft.com/en-us/azure/key-vault/general/quick-create-portal).
To add secrets to the vault, follow [this guide](https://docs.microsoft.com/en-us/azure/key-vault/secrets/quick-create-portal).

Here is a list of all the secrets that have to be saved in Azure Key Vault and what their names **must** be:
- The Azure Storage Account Key, using the name `storageAccountKey`
- Synapse dedicated SQL pool credentials:
  - username, using the name `jdbcUsername`
  - secret password, using the name `jdbcPassword`
- The secret of the service principal used to extract data from M365 and to connect to Azure Blob Storage. The name of
  the secret has to be `m365-reader-secret`
  
### Set up PowerBI
To create, edit and visualize the final reports over the processed data, you will need to have [PowerBI Desktop](https://docs.microsoft.com/en-us/power-bi/fundamentals/desktop-what-is-desktop) 
installed (either locally or on a Windows VM accessed via RDP).  
To perform the installation, please follow [these steps](https://docs.microsoft.com/en-us/power-bi/fundamentals/desktop-getting-started#install-and-run-power-bi-desktop).  
To share the reports with others you will also need an appropriate [license](https://docs.microsoft.com/en-us/power-bi/fundamentals/service-features-license-type).


## Set up the Synapse data processing pipelines
To start up the deployment, log in to [https://portal.azure.com](https://portal.azure.com) and open a bash Azure Cloud Shell:
![azure cloud shell](./docs/azure_cloud_shell.png)

Create an archive with the content of the  `./arm/` [folder](./arm) and upload it using the upload functionality 
in Azure Cloud Shell:   
![azure_upload_files](./docs/azure_upload_files.png)

After the zip archive is uploaded, unzip the archive using the following command:
```unzip arm.zip```

This will provide you with the ARM template of each Synapse component that needs to be deployed.
You would normally have to execute the appropriate [az synapse command](https://docs.microsoft.com/en-us/cli/azure/synapse?view=azure-cli-latest)
specific for each entity type that needs to be deployed, and provide the ARM template of that component.
Since this can be quite a tedious process, you are provided with a script that will deploy all the synapse components
(linked services, pipelines, datasets, notebooks & triggers) for you.
To run it, execute the following command from the `arm` directory, after replacing the values of the parameters with the 
values specific to the environment where the deployment will take place:

```
./deploy.sh --workspace-name "<synapse-workspace-name>" --spark-pool-name "<spark-pool-name>" --start-date "YYYY-MM-DDTHH:mm:ssZ" --key-vault-endpoint "https://<keyvault-name>.vault.azure.net/" --storage-account-endpoint "<storage-account-endpoint>"  --service-principal-tenant-id "<tenant-id>"--service-principal-id "<sp-id>" --dedicated-sql-pool-endpoint "<dedicated-sql-pool-endpoint>" --sql-pool-database-name "<database-name>" --azure-ai-endpoint "<azure-ai-endpoint>" --m365-extraction-group-id "<group_id>"
```

The script will perform `az login` for you, so you will be prompted to authenticate into Azure.

### Sql deployment
In order to create/update the associated sql tables, open a sql editor on the "Tables" folder in Synapse workspace manager:  
![create-tables-menu](./docs/sql_script_create_table_menu.png)

Use the script located here [./sql/tables_creation.sql](./sql/tables_creation.sql) and execute it in Synapse workspace manager:  
![create-all-tables](./docs/sql_script_create_all_tables.png)


### Limiting the size of the input data
In order to minimize costs, you should consider keeping  the size of the input data reasonably small. This can be achieved
via 2 mechanisms:

#### Limit the number of users for which data is processed

#### Limit the time span of the processed data


## Architectural design

There are 3 types of entities that represent conversations: mails, chats and calendar events.

There are 3 major pipeline steps in retrieving and processing the conversations:
1) The data ingestion from Graph-Data-Connect.
2) Table derivation necessary for obtaining the views that will contain the information for PowerBI
3) Sentiment extraction on the conversation content retrieved by the step 1. 
![Architecture](./docs/Diagram-Architecture.png)

### Data Ingestion

The workflow steps are focused on extracting the following type of information:
- M365 user profile data
- M365 emails data
- M365 calendar events data
- M365 Teams chat data


![Pipeline Overview](./docs/generating_pipeline.png)


The users processing pipeline:
![User information processing pipeline](./docs/pipeline_process_users_data.png)

The mails processing pipeline:
![Mails processing pipeline](./docs/pipeline_process_emails_data.png)

The calendar events processing pipeline:
![Events processing pipeline](./docs/pipeline_process_events_data.png)

The teams chat processing pipeline:
![Teams chat processing pipeline](./docs/pipeline_process_teams_chat_data.png)


### Table derivation

![Flow ](./docs/Conversation%20Lineage%20Table%20Derivation.png)

###  Sentiment and entities extraction from mails 

In order to follow the setup steps for the text analytics pipeline, please consult the setup instructions [here](conversations_text_analytics/README.MD)

### PowerBI presentation
The PowerBI presentation is based on the views created from the derived table.  
The statements for the SQL views creation can be found [here](./sql/views_creation_sql.sql).  
The PowerBI report can be found [here](./power_bi_presentation/MGDC%20Conversation%20Lineage.pbix).  
The PowerBI report documentation can be found [here](./power_bi_presentation/README.MD).

In order to create/update the views necessary for the PowerBI please open a sql query window:  
![sql-query-window](./docs/sql_script_creation.png)  

Execute the sql script necessary for the PowerBI reports:  
![sql-query-execution](./docs/sql_script_views_creation.png)
 
