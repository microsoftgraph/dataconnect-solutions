# Conversation Lineage

## Table of contents
- [Tutorial Overview](#tutorial-overview)
- [Prerequisites](#prerequisites)
- [Tutorial for setting up the Conversation Lineage processing pipelines](#tutorial-for-setting-up-the-conversation-lineage-processing-pipelines)
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
All the workflows are dependent on [Graph-Data-Connect](https://docs.microsoft.com/en-us/graph/data-connect-concept-overview)
and run in [Azure Synapse Analytics](https://docs.microsoft.com/en-us/azure/synapse-analytics/). 

For setting up an *Azure Synapse Workspace*, please follow the [official synapse documentation](https://docs.microsoft.com/en-us/azure/synapse-analytics/get-started-create-workspace).   
For setting GDC, please use the following tutorial chapter from this Microsoft GDC [tutorial](https://github.com/microsoftgraph/msgraph-training-dataconnect/blob/master/Lab.md#exercise-1-setup-office-365-tenant-and-enable-microsoft-graph-data-connect).      
You will also need a user with [global administration role](https://docs.microsoft.com/en-us/azure/role-based-access-control/elevate-access-global-admin).  

To create and visualize the final reports, you will need a PowerBI deployment with an appropriate [license](https://docs.microsoft.com/en-us/power-bi/fundamentals/service-features-license-type).


## Tutorial for setting up the Conversation Lineage processing pipelines
In order to start up the deployment log in to portal.azure.com and open an azure cloud shell:   
![azure cloud shell](./docs/azure_cloud_shell.png)

Select the necessary ARM template JSON files from the `./arm/End2EndMgdc101WithConvLineage/` [folder](./arm/End2EndMgdc101WithConvLineage) and upload them using
the upload functionality in azure cloud shell:   
![azure_upload_files](./docs/azure_upload_files.png)


### Arm files deployment
Once the files are uploaded, please execute the appropriate command specific for each entity type that needs to be deployed.   
The documentation for Synapse deployment commands can be found [here](https://docs.microsoft.com/en-us/cli/azure/synapse?view=azure-cli-latest)

Example: in order to deploy a pipeline, run the following:
```shell
az synapse pipeline create --file @pipeline.json --name 'ConversationLineage' --workspace-name <Azure Synapse Workspace Name>
```

### Trigger deployment
Synapse triggers are responsible with coordinating the processing of historical data, and the processing of future data on a scheduled basis.  
In order to configure the Synapse triggers, please execute the appropriate trigger az commands.  
Documentation for synapse trigger commands can be found [here](https://docs.microsoft.com/en-us/cli/azure/synapse/trigger?view=azure-cli-latest#az-synapse-trigger-create)

```shell
az synapse trigger create --workspace-name <Azure Synapse Workspace Name>  --name 'ConversationLineageTrigger' --file @trigger.json
  ```

### Sql deployment
In order to create/update the associated sql tables, open a sql editor on the "Tables" folder in Synapse workspace manager:  
![create-tables-menu](./docs/sql_script_create_table_menu.png)

Use the script located here [./sql/tables_creation.sql](./sql/tables_creation.sql) and execute it in Synapse workspace manager:  
![create-all-tables](./docs/sql_script_create_all_tables.png)

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
The statements for the SQL views creation can be found [here](./sql/views_creation_sql.sql)  
The PowerBI report can be found [here](./power_bi_presentation/MGDC%20Conversation%20Lineage.pbix)
The PowerBI report documentation can be found [here](./power_bi_presentation/README.MD)

In order to create/update the views necessary for the PowerBI please open a sql query window:  
![sql-query-window](./docs/sql_script_creation.png)  

Execute the sql script necessary for the PowerBI reports:  
![sql-query-execution](./docs/sql_script_views_creation.png)
 
