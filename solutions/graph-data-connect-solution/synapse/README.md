# **Azure Synapse Directory Structure**

## **Directories and Files**

- **[credential](credential)**
    - **[WorkspaceSystemIdentity](credential/WorkspaceSystemIdentity.json):** This JSON file contains connection details for the Azure Synapse workspace.

- **[dataset](dataset)**
    - **[DS_Binary](dataset/DS_Binary.json):** Defines a dataset for loading binary format data into ADLS within an Azure Synapse workspace.
    - **[DS_CSV](dataset/DS_CSV.json):** Specifies a dataset for loading CSV format data into ADLS within an Azure Synapse workspace.
    - **[DS_Microsoft365](dataset/DS_Microsoft365.json):** Describes a dataset for importing data from Microsoft 365 using a linked M365 service within an Azure Synapse workspace.
    - **[DS_Parquet](dataset/DS_Parquet.json):** Contains an Azure Synapse workspace dataset for loading data in Parquet format into ADLS.
    - **[DS_Json](dataset/DS_Json.json):** Provides an Azure Synapse workspace dataset for loading data in JSON format into ADLS.

- **[integrationRuntime](integrationRuntime)**
    - **[AutoResolveIntegrationRuntime](integrationRuntime/AutoResolveIntegrationRuntime.json):** Configuration for managed private endpoint hosted integration runtime.

- **[linkedService](linkedService)**
    - **[LS_AzureSqlDatabase](linkedService/LS_AzureSqlDatabase.json):** Linked service for connecting to the Metadata SQL database.
    - **[LS_AzureDataLakeStorage](linkedService/LS_AzureDataLakeStorage.json):** Linked service to connect to the ADLS account.
    - **[LS_AzureKeyVault](linkedService/LS_AzureKeyVault.json):** Linked service providing a connection to Azure Key Vault.
    - **[LS_Microsoft365](linkedService/LS_Microsoft365.json):** Linked service for establishing a connection with Microsoft 365.
    - **[syngdcscindevil-WorkspaceDefaultSqlServer](linkedService/syngdcscindevil-WorkspaceDefaultSqlServer.json):** Linked service to connect to the default SQL server.
    - **[syngdcscindevil-WorkspaceDefaultStorage](linkedService/syngdcscindevil-WorkspaceDefaultStorage.json):** Linked service for connecting to Azure Key Vault.

- **[managedVirtualNetwork](managedVirtualNetwork)**
    - **[managedVirtualNetwork](managedVirtualNetwork/default/managedPrivateEndpoint/synapse-ws-sql--cdp-foundation-synapse):** Code for creating private endpoints to connect with Synapse notebooks in ADF.

- **[notebook](notebook)**
    - **[variables](notebook/variables.json)** : Notebook containing common variables used across notebooks.
    - **[Saslesforce_SourceToRaw](notebook/Saslesforce_SourceToRaw.json)** : Synapse notebook for copying data from Salesforce to Raw ADLS layer.
    - **[RawToBronze](notebook/RawToBronze.json)** : Synapse notebook for transforming data from Raw to Bronze stage.
    - **[BronzeToSilver](notebook/BronzeToSilver.json):** Synapse notebook for transforming data from Bronze to Silver stage.
    - **[SilverToGold](notebook/SilverToGold.json):** Synapse notebook for transforming data from Silver to Gold stage.
    - **[M365_Silver_To_Gold](notebook/M365_Silver_To_Gold.json):** Synapse notebook for transforming M365 data from Silver to Gold stage.
    - **[Sentiment_code_nltk](notebook/Sentiment_code_nltk.json):** Synapse notebook for sentiment analysis on M365 data.

- **[pipeline](pipeline)**
    - **[pl_source_to_raw](pipeline/pl_source_to_raw.json):** JSON script for deploying the source to raw notebook in Synapse.
    - **[pl_raw_to_bronze](pipeline/pl_raw_to_bronze.json):** JSON script for deploying the raw to bronze notebook in Synapse.
    - **[pl_bronze_to_silver](pipeline/pl_bronze_to_silver.json):** JSON script for deploying the bronze to silver notebook in Synapse.
    - **[pl_silver_to_gold](pipeline/pl_silver_to_gold.json):** JSON script for deploying the silver to gold notebook in Synapse.

- **[sqlscript](sqlscript)**
    - **[ExternalTables](sqlscript/ExternalTables.json):** SQL script to generate external tables that are utilized within Power BI.


<div style="background-color:#f9f2f4; border: 1px solid #f78181; padding: 10px">
    <strong style="color:red">Note:</strong> The provided code/solution has been tested on sample data to ensure its functionality. However, it is highly advised and best practice to thoroughly test the code on actual, real-world data before deploying it in a production environment.
</div>
