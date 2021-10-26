## Azure Data Factory

As part of the ProjectStaffing application, Azure Data Factory (ADF) is used to perform ETL work, namely to ingest raw information
related to employees (M365 user profiles, email, information about managers, HR data), filter out irrelevant information,
join data from several sources, [enrich it](enrichment_pipelines.md) with related information from several domains (taxonomies),
transform its schema and push it to the target systems (AzureSql, Azure Search) from where it's consumed by the AppService.


### Triggers

Azure Data Factory uses triggers for orchestrating which part of the data should be processed at what point in time. 
More information regarding triggers can be found [here](ADF_trigger_creation_policy.md). 

### Pipelines

There are 4 pipelines that are started either by triggers or by the Project Staffing application when an ingestion mode switch 
operation is performed.  
The definition of each one of these pipelines is stored in a separate folder in the Azure Data Factory Pipelines section.  
Some of these pipelines are structured as a main orchestrating pipeline, which in turn starts and coordinates other
child pipelines. These child pipelines can be found in a subfolder named "Components".  
This approach was chosen as it allows the child pipelines to be run independently, making development and testing easier.

#### End2EndCleanup pipeline

This pipeline is run at the beginning of every ingestion mode switch operation in order to delete all the data from the previous ingestion mode. 
End2EndCleanup pipeline is composed of two pipelines that run in parallel:
- `CleanupAzureSearchAndAzureSql` pipeline that is composed of two activities:
    - `CleanupAzureSearchAndAzureSql` - deletes the emails and employee profile indexes from Azure Search, and clears  
      the content of all the tables in Azure Sql except those that retain configuration data and team information.
    - `createAzureSearchEmailIndex` - this activity recreates the Azure Search email index. Unlike the emails index,
      the employee index gets recreated each time the `End2EndEmployeeProfilePipeline` is run, so it doesn't have to get 
      created when cleanup is performed.
- `CleanupBlobStorageData` pipeline that has 6 components:
    - `DeleteM365EmailsBlobData` activity that deletes the raw input email data
    - `DeleteDeduplicatedEmailsBlobData` activity that deletes the deduplicated email data
    - `DeleteEnrichedEmailsBlobData` activity that delete the enriched email data
    - `deleteOldM365ManagerBlobData` pipeline that delete the raw manager input data
    - `deleteOldM365UserBlobData` pipeline that deletes the raw user profile information
    - `DeleteEnrichedEmployeeBlobData` activity that deletes the processed user profile data
    
#### End2EndEmployeeProfilePipeline
The `End2EndEmployeeProfilePipeline` is responsible for 
- deleting old raw M265 users and manager data from Azure Blob Storage
- processing the new users and managers input data
- storing the processed data in Azure Search and Azure Sql

The pipeline is composed of the following components:
- `deleteOldM365ManagerBlobData`
- `deleteOldM365UserBlobData`
- `CopyM365EmployeeProfiles`- this pipeline copies the user profile raw input data into Azure Blob Storage. Depending on 
  the current ingestion mode, the input data might come from 3 sources:
    - Public storage account for simulated data
    - Private storage account for sample data
    - OfficeM365 for real/production data
- `buildEmployeeProfileAndExportToAzureSql`- this pipeline is responsible for:
    - reading the user profile raw data and convert it into a format specific for Azure Sql
    - use Graph Api in order to get the profile pictures for all the users and also store them in Azure Sql
- `ImportHRDataIntoAzureSql`- this pipeline is used to read HR Data about employees, meant to complement the M365 data
    - the data is read from AZBS, from the "demodata" storage account, but the container depends on the current ingestion mode
- `enrichEmployeeProfile` - enhance the employee profiles with information from several domains (taxonomies), so that 
  relevant employees can be recommended not only based on the information appearing in their profiles, but also based
  on the skills which are inferred that they might possess, based on the known relations defined in the taxonomies 
- `exportDataToAzureSearch` - creates a new index with the new enriched data based on the current timestamp, updates the 
  application configurations so that the app uses the new index to serve requests, waits a while to allow ongoing requests
  using the old index to finish, then deletes the old index

#### End2EndEmailsPipeline
The `End2EndEmailsPipeline` is responsible for
- retrieving emails within a given time interval 
    - the interval is defined based on two pipeline parameters: `batchStartTime` and `batchEndTime`
        - these get initialized by an ADF tumbling window trigger, based on the window start and end times
    - retrieves email of the "GDC employees" group members, when running in production mode
        - this is the group which gets prompted for during deployment
        - in sample mode and simulated mode, it simply processes whichever emails get provides in the sample files
- processing the emails to help infer which skills a user has, based on sent or received emails
- storing the processed data in Azure Search

The pipeline is composed of the following components:
- `Set batchDateTime` - extracts the UTC date and time corresponding to the current batch of emails being processed
- `Set batchMonth` - extracts the month corresponding to the current batch of emails being processed
- `Set batchYear` - extracts the year corresponding to the current batch of emails being processed
- `Set batchTimeBasedSubpath` - builds the structure of a folder hierarchy  that is specific to the time window of the 
  current batch. This path component will be reused relative to various parent folders, to allow passing the data 
  specific to a time window between pipeline stages.
- `IngestionModeSwitch`- this pipeline copies the raw emails input data into Azure Blob Storage. Depending on
  the current ingestion mode, the input data might come from 3 sources:
  - Public storage account for simulated data
  - Private storage account for sample data
  - OfficeM365 for real/production data
- `latestReplyExtractionAndDeduplication`- this pipeline is responsible for:
  - only preserving the last email in a reply thread, as it is the piece of information which got written and sent by 
    the current user, and therefore the only part of the thread which can be used to infer information about the sender
  - remove duplicate emails from the dataset 
      - i.e. these are mails having the same InternetMessageId, appearing once for every sender and receiver
- `emailEnrichment` - extract relevant information from emails and enhance it with information from several taxonomies,
  so that relevant employees can be recommended not only based on the relevant information mentioned in their emails, 
  but also on the related and potentially relevant information obtained from the taxonomies
- `exportEmailsToAzureSearch` - appends the new batch of enriched email data to the existing emails index

#### End2EndMailsToRolesPipeline
The `End2EndMailsToRolesPipeline` is responsible for
- inferring which role might be suited for an employee based on the content of received or sent emails 
- for more details, please see the "Role Inference" section in [here](enrichment_pipelines.md)
- currently, the pipeline processes all the emails of each user
- the pipeline follows the next steps:
  - `SetNewInferredRolesVersion` - defines what the new data version is, based on the current timestamp
  - `MailsToRoleDetection` - performs the actual inference
    - reads the deduplicated emails data from AZBS 
    - writes the results to AzureSql using the new version
  - `UpdateAzureSqlDataVersion` - updates the application configurations so that the app uses the new data version to 
    serve requests, waits a while to allow ongoing requests using the old version to finish, then deletes the old data
