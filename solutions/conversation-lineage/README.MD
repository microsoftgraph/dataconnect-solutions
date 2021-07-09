# Conversation Lineage

## Project purpose
This application’s purpose is to extract conversation analytics from a provided mail-flow between members of a company.
More specifically, we aim to extract sentiment and nlp entities in order to identify the conversation’s sentiment over certain topics and then write it to a sql table. 
Our end goal is to provide insights through a power-bi presentation notebook.

## Architectural design

There are 3 major steps in retrieving the conversations.
1) The data ingestion from gdc.
2) Table derivation necessary for obtaining the views that will contain the information for powerbi
3) Sentiment extraction on the conversation content retrieved by the step 1. 
![Architecture](./docs/Diagram-Architecture.png)

## Data Ingestion

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


## Table derivation

![Flow ](./docs/Conversation%20Lineage%20Table%20Derivation.png)

## PowerBI presentation
The powerbi presentation is based on the views created from the derived table.
The sql for the views creation can be found [here](./sql/views_creation_sql.sql)
 
## [Sentiment and entities extraction from mails](text_analytics/README.MD)

## Deployment

All the workflows run in azure-synapse. For setting it up use the [Official documentation](https://docs.microsoft.com/en-us/azure/synapse-analytics/get-started-create-workspace)

