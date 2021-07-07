#Conversation Lineage

There are 3 major steps in retrieving the conversations.
1) The data ingestion from gdc.
2) Table derivation necessary for obtaining the views that will contain the information for powerbi
3) Sentiment extraction on the conversation content retrieved by the step 1. 


## Data Ingestion
The workflow steps are focused on extracting the following type of information:
- M3665 mails
- M365 calendar events data
- M365 manager information
- M365 user profile data
- Teams chat data

Here are the workflows steps that get executed:
### Mail retrieval workflows

- #### DeleteOldM365EmailsBlobData

- #### AllM365EmailsToAZBS

- #### DeleteOldEmailsParquetData

- #### Emails_JsonToParquet

- #### CopyEmailsToTable

- #### Compute Recipient Email Domains


### Calendar retrieval workflows
- #### DeleteOldM365CalendarEventBlobData

- #### AllM365CalendarEventsToAZBS

- #### DeleteOldEventsParquetData

- #### Events_JsonToParquet

- #### CopyCalendarEventsToSqlTable

### Manager retrieval workflow
- #### DeleteOldM365ManagersBlobData

- #### AllM365ManagersToAZBS

### User profiles retrieval workflow
- #### DeleteOldM365UsersProfilesBlobData

- #### AllM365UserProfilesToAZBS

### Join Users with Managers
- #### TruncateUsersTable

- #### JoinUsersWithManagers

### Teams chat retrieval workflow
- #### DeleteOldM365TeamChatsBlobData

- #### AllM365TeamChatsToAZBS

- #### DeletOldTeamChatsParquetData

- #### TeamChats_JsonToParquet

- #### CopyTeamChatsToTable

### Join all the information
- #### WriteAugmentedFlattenedEmailsWithManagersToTable

- #### WriteAugmentedEmailsToTable

- #### WriteAugmentedFlattenedEventsToTable

- #### WriteAugmentedEvents

- #### WriteAugmentedFlattenedChatsToTable

- #### WriteAugmentedTeamChats

## Table derivation

![Flow ](./docs/Conversation%20Lineage%20Table%20Derivation.png)
 


