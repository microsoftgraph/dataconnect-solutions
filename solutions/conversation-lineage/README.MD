# Conversation Lineage

## Table of contents
- [Project Description](#project-description)
- [Prerequisites](#prerequisites)
- [Tutorial for setting up the Conversation Lineage processing pipelines](#tutorial-for-setting-up-the-conversation-lineage-processing-pipelines)
- [Architectural design](#architectural-design)
    - [Data Ingestion](#data-ingestion)
    - [Table Derivation](#table-derivation)  
    - [Sentiment and entities extraction from mails](#sentiment-and-entities-extraction-from-mails)
    - [PowerBI presentation](#powerbi-presentation)



## Project description
This tutorial purpose is to provide an example of using [Graph Data Connect](https://docs.microsoft.com/en-us/graph/data-connect-concept-overview)
(GDC) to extract insights and analytics by analyzing
conversations within an organization.  
Another purpose of this tutorial is to provide the developers with a technical infrastructure for quickly 
setting up a GDC  based application, by providing and example project(*Conversation Lineage*) 
that integrates GDC infrastructure and azure connected services.

More specifically we'll use [Azure Cognitive Services](https://azure.microsoft.com/en-us/services/cognitive-services/text-analytics/)
on top of GDC data in order to: extract sentiment and nlp entities over conversations retrieved from GDC 
Our end goal is to provide insights through a power-bi presentation notebook.

## Prerequisites

All the workflows are dependent on the [Graph-Data-Connect](https://docs.microsoft.com/en-us/graph/data-connect-concept-overview)
and run in [Azure Synapse Analytics](https://docs.microsoft.com/en-us/azure/synapse-analytics/). 

For setting up an *Azure Synapse Workspace* please follow the [official synapse documentation](https://docs.microsoft.com/en-us/azure/synapse-analytics/get-started-create-workspace)    
For setting GDC please use the following tutorial chapter from this Microsoft GDC [tutorial](https://github.com/microsoftgraph/msgraph-training-dataconnect/blob/master/Lab.md#exercise-1-setup-office-365-tenant-and-enable-microsoft-graph-data-connect).    
You will also need an user with [global administration role](https://docs.microsoft.com/en-us/azure/role-based-access-control/elevate-access-global-admin).  


## Tutorial for setting up the Conversation Lineage processing pipelines

In order to start up the deployment log in to portal.azure.com and open an azure cloud shell:   
![azure cloud shell](./docs/azure_cloud_shell.png)

Select the necessary files from the `./arm/mgdc101/` folder and upload them using the upload functionality in azure cloud shell:   
![azure_upload_files](./docs/azure_upload_files.png)

Once the files are uploaded please execute the following steps for deploying the pipelines:

```shell
az command (fill in here)
```

In order to configure the triggers please execute the following commands:

```shell
az command (fill in here)
```


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
- M3665 mails
- M365 calendar events data
- Teams chat data


![Pipeline Overview](./docs/generating_pipeline.png)


The users processing pipeline:
![User information processing pipeline](./docs/pipeline_process_users_data.png)

The mails processing pipeline:
![Mails processing pipeline](./docs/pipeline_process_emails_data.png)

The events processing pipeline:
![Events processing pipeline](./docs/pipeline_process_events_data.png)

The teams chat processing pipeline:
![Teams chat processing pipeline](./docs/pipeline_process_teams_chat_data.png)


### Table derivation

![Flow ](./docs/Conversation%20Lineage%20Table%20Derivation.png)

###  Sentiment and entities extraction from mails 

In order to follow the setup steps for the text analytics pipeline please consult the setup instructions [here](conversations_text_analytics/README.MD)

### PowerBI presentation
The PowerBI presentation is based on the views created from the derived table.
The sql for the views creation can be found [here](./sql/views_creation_sql.sql)
 
