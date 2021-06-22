### Python and Spark Scripts that are orchestrated by Azure Data Factory 


#### Clean Data Job

The CleanupJob parameters are the following:
- `--application-id`
    - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `directory_id` ADF global parameter.                                                                                                                   
- `--adb-sp-client-key-secret-name`
    - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to pass the name of the secret.
    - The parameter value is `jwc-service-principal-secret`.
- `--adb-secret-scope-name`
    - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `adb_secret_scope` 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the jwc database.
    - The value is taken from ADF global parameter `azure_sql_database`
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF.
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF. 
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `key_vault_url`.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--use-msi-azure-sql-auth`
    - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity. 
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
    - The value is taken from `azure_sql_msi_auth_enabled` ADF global parameter.
  
#### Calendar Events Extractor Job

The Calendar Events Extractor Job parameters are the following:
- `--application-id`
    - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `directory_id` ADF global parameter.                                                                                                                   
- `--adb-secret-scope-name`
    - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to pass the name of the secret.
    - The parameter value is `jwc-service-principal-secret`.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `key_vault_url`.
- `--storage-account-name`
    - Represents the input Azure Blob Storage account name containing Calendar Data.
    - The value is set by the ADF global parameter `inputStorageAccountName`
- `--input-container`
    - Represents the container name from the Azure Blob Storage account containing Calendar Data.
    - The value is set by the ADF global parameter `inputContainer`
- `--input-folder-path`
    - Represents the folder path from the container containing Extracted Calendar Data.
    - The value is set by `inputFolderPath` ADF global parameter
- `--output-folder-path`
    - Represents the folder path from the container containing Filtered Calendar Data.
    - The value is set by `outputFolderPath` ADF global parameter 
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.

#### Profiles Fields Extractor job 02_profiles_spark_processor job

The Profiles field extractor parameters are the following:
- `--application-id`
    - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `jwc-service-principal-secret`.
- `--storage-account-name`
    - Represents the input Azure Blob Storage account name that will contain the extracted and filtered user profile info.
    - The value is set by the ADF global parameter `azbs_storage_account`
- `--output-container`
    - Represents the container name from the Azure Blob Storage account containing the extracted and filtered user profile info
    - The value is set by the ADF global parameter `azbs_container_name`
- `--output-folder-path`
    - Represents the folder path from the container containing the extracted and filtered user profile info
    - The value is set by concatenating the `watercooler_out_profiles/` string with a local ADF pipeline parameter `batchTimeBasedSubpath`
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `key_vault_url`.
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `adb_secret_scope` 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the jwc database.
    - The value is taken from ADF global parameter `azure_sql_database`
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF.
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF.     
    
#### Events and Profiles Assembler

The Events and Profiles Assembler parameters are the following:
- `--application-id`
    - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `jwc-service-principal-secret`.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--user-profiles-input-path`
    - Represents dbfs extracted profiles folder path.
    - The value is set to `/dbfs/mnt/watercooler/watercooler_out_profiles` in ADF.
- `--calendar-events-input-path`
    - Represents dbfs extracted and filtered calendar events path.
    - The value is set to `/dbfs/mnt/watercooler/watercooler_out_events` in ADF.
- `--mailbox-meta-folder-path`
    - Represents extracted mailbox setting information path.
    - The value is set to `/dbfs/mnt/watercooler/mailbox` in ADF.
- `--output-path`
    - Represents the outputpath of the assembled information.
    - The value is set to `/dbfs/mnt/watercooler/assembled_data` in ADF.



#### Clustering Job

The Clustering job parameters are the following:
- `--application-id`
    - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `jwc-service-principal-secret`.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `key_vault_url`.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--use-msi-azure-sql-auth`
    - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity. 
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
    - The value is taken from `azure_sql_msi_auth_enabled` ADF global parameter.
- `--clusters`
    - This parameter sets the number of clusters to be used in the kmeans clustering process
    - Default value is 10
- `--max-group-size`
    - This parameter sets the maximum number of people a watercooler group can have
    - Default value is 10
- `--input-assembled-data-path`
    - Represents the assembled data path where all the timezone and people specific calendar information is being found.
    - The value is set to `/dbfs/mnt/watercooler/assembled_data` in ADF.
- `--kmeans-data-output-path`
    - Represents the outputpath of the assembled information.
    - The value is set to `/dbfs/mnt/watercooler/kmeans_output` in ADF.  


#### Write Csv Files Job

The Write Csv Files Job  parameters are the following:
- `--application-id`
    - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `jwc-service-principal-secret`.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `key_vault_url`.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--user-profiles-input-path`
    - This parameter sets the path where the filtered user profiles can be found
    - Default value is `/dbfs/mnt/watercooler/watercooler_out_profiles`
- `--kmeans-data-input-path`
    - Represents output path of the previous clustering step where all the data can be found.
    - The value is set to `/dbfs/mnt/watercooler/kmeans_output` in ADF.
- `--csv-output-data-path`
    - Represents the outputpath where the csv files that are to be exported to sql can be found
    - The value is set to `/dbfs/mnt/watercooler/csv_output_data` in ADF. 


#### Sql Exporter Job

The Write Csv Files Job  parameters are the following:

- `--application-id`
    - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `jwc-service-principal-secret`.
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `adb_secret_scope` 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the jwc database.
    - The value is taken from ADF global parameter `azure_sql_database`
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF.
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF.       
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `key_vault_url`.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--csv-input-data-path`
    - Represents output path of the `export to csv` step.
    - The value is set to `/dbfs/mnt/watercooler/csv_output_data` in ADF.
- `--export-batch-size`
    - Sets the batch size for the export to sql tables 
    - The default value is set to 10


#### Events Email Sender Job

The Emails Event Sender Job parameters are the following:

- `--application-id`
  - Represents the client id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
  - The parameter value is taken from `service_application_id` ADF global parameter.
- `--directory-id`
  - Represents the tenant id of `jwc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
  - The parameter value is taken from `directory_id` ADF global parameter.
- `--adb-secret-scope-name`
  - The client secret of the `jwc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
  - The parameter value is taken from `adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
  - The client secret of the `jwc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
  - The parameter value is `jwc-service-principal-secret`.
- `--jdbc-hostname`
  - Represents the Azure Sql database host.
  - The value is taken from ADF global parameter `adb_secret_scope`.
- `--jdbc-port`
  - Represents the Azure Sql database port.
  - The value is set to `1433` in ADF.
- `--jdbc-database`
  - Represents the name of the jwc database.
  - The value is taken from ADF global parameter `azure_sql_database`.
- `--jdbc-username-key-name`
  - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
  - The value is set to `azure-sql-backend-user` in ADF.
- `--jdbc-password-key-name`
  - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
  - The value is set to `azure-sql-backend-password` in ADF.
- `--log-analytics-workspace-id`
  - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
  - The value is taken from ADF global parameter `wc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
  - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
  - The value is set to `log-analytics-api-key` in ADF.
- `--key-vault-url`
  - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
  - The value is taken from ADF global parameter `key_vault_url`.
- `--use-msi-azure-sql-auth`
  - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity.
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
  - The value is taken from `azure_sql_msi_auth_enabled` ADF global parameter.
- `--start-date`
  - Represent the start date from which the watercooler event mail invites will be created
- `--end-date`
  - Represent the end date to which the watercooler event mail invites will be created
- `--meeting-duration`
  - Represents the watercooler meeting duration
