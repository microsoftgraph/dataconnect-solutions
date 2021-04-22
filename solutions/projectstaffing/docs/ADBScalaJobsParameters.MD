### Scala code ADB jobs that are orchestrated by Azure Data Factory 


#### Cleanup Job

The CleanupJob parameters are the following:
- `--application-id`
    - Represents the client id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_directory_id` ADF global parameter.                                                                                                                   
- `--adb-sp-client-key-secret-name`
    - The client secret of the `gdc-service` service principal is stored in Databricks secrets. This parameter is used to pass the name of the secret.
    - The parameter value is `gdc-service-principal-secret`.
- `--adb-secret-scope-name`
    - The client secret of the `gdc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `gdc_adb_secret_scope` ADF global parameter.
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `gdc_adb_secret_scope` 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the gdc database.
    - The value is taken from ADF global parameter `gdc_azure_sql_database`
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF.
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF. 
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `gdc_key_vault_url`.
- `--azure-search-url`
    - Represents the url of the Azure Search Service where the employee and email indexes are stored.
    - The value is taken from ADF global parameter `gdc_azure_search_url`.
- `--azure-search-admin-key-name`
    - Represents the name of the key from Azure Key Vault, that contains the Azure Search service admin key.
    - The value is set to `gdc-search-service-admin-key` in ADF.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `gdc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--emails-index-name`
    - Represents the name of the Azure Search index where all the employee emails are stored.
    - The value is made of the prefix, which is stored in the `gdc_azure_search_index_prefix` ADF global variable, and the suffix, which is `-emails`.
- `--use-msi-azure-sql-auth`
    - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity. 
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
    - The value is taken from `gdc_azure_sql_msi_auth_enabled` ADF global parameter.
  
#### CopyHRDataToDatabase Job

The CopyHRDataToDatabase parameters are the following:
- `--application-id`
    - Represents the client id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_directory_id` ADF global parameter.                                                                                                                   
- `--adb-secret-scope-name`
    - The client secret of the `gdc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `gdc_adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `gdc-service` service principal is stored in Databricks secrets. This parameter is used to pass the name of the secret.
    - The parameter value is `gdc-service-principal-secret`.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `gdc_key_vault_url`.
- `--input-storage-account-name`
    - Represents the input Azure Blob Storage account name containing HR Data.
    - The value is set by the ADF global parameter `inputStorageAccountName`
- `--input-container`
    - Represents the container name from the Azure Blob Storage account containing HR Data.
    - The value is set by the ADF global parameter `inputContainer`
- `--input-folder-path`
    - Represents the folder path from the container containing HR Data.
    - The value is set by `inputFolderPath` ADF global parameter 
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `gdc_adb_secret_scope`. 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the gdc database.
    - The value is taken from ADF global parameter `gdc_azure_sql_database`.
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF.
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF.
- `--use-msi-azure-sql-auth`
    - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity. 
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
    - The value is taken from `gdc_azure_sql_msi_auth_enabled` ADF global parameter.
- `--max-db-connections`
    - Represents the maximum number connections to use while writing the data into the database. This parameter is optional.
    - The value has to be an integer.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `gdc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--dev`
    - This parameters is optional and is used for starting application in development mode.
    - The default value is false.
- `--ingestion-mode`
    - Represents the data ingestion mode, determines if the input data is production, sample or simulated data. 
    - The allowed values are `production_mode`, `sample_mode` or `simulated_mode`. The value is set by the ADF global parameter `gdc_data_ingestion_mode`.
- `--fail-fast-on-corrupt-data`
    - Determines if the job should fail fast when encountering invalid data or while encountering errors reading non-critical fields.
    - The value is a boolean and is set to `false`.
    
#### LatestReplyExtractorJob

The LatestReplyExtractorJob parameters are the following:
- `--application-id`
    - Represents the client id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `gdc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `gdc_adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `gdc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `gdc-service-principal-secret`.
- `--storage-account-name`
    - Represents the input Azure Blob Storage account name containing the email data from Office 365.
    - The value is set by the ADF global parameter `gdc_azbs_storage_account`
- `--input-container`
    - Represents the container name from the Azure Blob Storage account containing the email data from Office 365.
    - The value is set by the ADF global parameter `gdc_emails_azbs_container`
- `--input-folder-path`
    - Represents the folder path from the container containing the email data from Office 365.
    - The value is set by concatenating the `m365_emails/` string with a local ADF pipeline parameter `batchTimeBasedSubpath`
- `--output-container`
    - Represents the output Azure Blob Storage container where the processed email data is written.
    - The value is set by the ADF global parameter `gdc_emails_azbs_container`
- `--output-folder-path`
    - Represents the folder path in the Azure Blob Storage container where the processed email data is written.
    - The value is set by the ADF global parameter `gdc_emails_azbs_container`
- `--run-deduplication`
    - Determines if the email deduplication functionality is run.
    - The parameter is a bolean and is optional. The default value is `true`.
- `--run-reply-removal`
    - Determines if the email reply removal functionality is run.
    - The parameter is a bolean and is optional. The default value is `true`.
- `--parse-enron-dataset`
    - Determines if the logic for parsing emails from the Enron dataset, is activated.
    - The parameter is a bolean and is optional. The default value is `false`.
- `--number-of-partitions`
    - Determines the number of output files.
    - The parameter is optional and the default value is 1.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `gdc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `gdc_key_vault_url`.
    
    
#### M365UserToAzureSqlJob

The M365UserToAzureSqlJob parameters are the following:
- `--application-id`
    - Represents the client id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_application_id` ADF global parameter.
- `--directory-id`
    - Represents the tenant id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `gdc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `gdc_adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `gdc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `gdc-service-principal-secret`.
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `gdc_adb_secret_scope`. 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the gdc database.
    - The value is taken from ADF global parameter `gdc_azure_sql_database`.
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF. 
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF.
- `--storage-account-name`
    - Represents the input Azure Blob Storage account name containing the user data from Office 365
    - The value is set by the ADF global parameter `gdc_azbs_storage_account`
- `--input-container`
    - Represents the container name from the Azure Blob Storage account containing the user data from Office 365.
    - The value is set by the ADF global parameter `gdc_employee_profiles_azbs_container`
- `--users-input-folder-path`
    - Represents the folder path from the Azure Blob Storage container containing the user data from Office 365.
    - The value is set to `m365_users` in ADF.
- `--managers-input-folder-path`
    - Represents the folder path from the Azure Blob Storage container containing the M365 managers data.
    - The value is set to `m365_managers` in ADF.
- `--max-db-connections`
    - Represents the maximum number connections to use while writing the data into the database. This parameter is optional.
    - The value has to be an integer.
- `--graph-api-tenant-id`
    - Represents the tenant/directory id of `gdc-m365-reader` service principal (app registration) which is used for getting the user profile pictures from 
    Graph Api.
    - The value is taken from ADF global parameter `gdc_graph_api_app_registration_directory_id`.
- `--graph-api-client-id`
    - Represents the client/application id of `gdc-m365-reader` service principal (app registration) which is used for getting the user profile pictures from 
    Graph Api.
    - The value is taken from ADF global parameter `gdc_graph_api_app_registration_application_id`.
- `--graph-api-secret-scope`
    - The client secret of the `gdc-m365-reader` is stored in one of the Databricks secret scopes. This parameter is used to provide the name of the scope.
    - The value is taken from ADF global parameter `gdc_adb_secret_scope`.
- `--graph-api-sp-client-key-secret-name`
    - The client secret of the `gdc-m365-reader` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The value is set to `graph-api-service-principal-secret` in ADF.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `gdc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `gdc_key_vault_url`.
- `--ingestion-mode`
    - Represents the data ingestion mode, determines if the input data is production, sample or simulated data. 
    - The allowed values are `production_mode`, `sample_mode` or `simulated_mode`. The value is set by the ADF global parameter `gdc_data_ingestion_mode`.
- `--use-msi-azure-sql-auth`
    - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity. 
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
    - The value is taken from `gdc_azure_sql_msi_auth_enabled` ADF global parameter.



#### ReplaceCurrentAzureSearchIndexJob

The ReplaceCurrentAzureSearchIndexJob parameters are the following:
- `--application-id`
    - Represents the client id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_application_id` ADF global parameter.
- `--new-azure-search-index-name`
    - Represents the name of the new Azure Search index that will replace the old one.
    - This value is set using a ADF pipeline variable.
- `--directory-id`
    - Represents the tenant id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `gdc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `gdc_adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `gdc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `gdc-service-principal-secret`.
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `gdc_adb_secret_scope`. 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the gdc database.
    - The value is taken from ADF global parameter `gdc_azure_sql_database`.
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF. 
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `gdc_key_vault_url`.
- `--azure-search-url`
    - Represents the url of the Azure Search Service where the employee and email indexes are stored.
    - The value is taken from ADF global parameter `gdc_azure_search_url`.
- `--azure-search-admin-key-name`
    - Represents the name of the key from Azure Key Vault, that contains the Azure Search service admin key.
    - The value is set to `gdc-search-service-admin-key` in ADF.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `gdc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--use-msi-azure-sql-auth`
    - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity. 
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
    - The value is taken from `gdc_azure_sql_msi_auth_enabled` ADF global parameter.


#### UpdateAzureSqlDataVersionJob

The UpdateAzureSqlDataVersionJob parameters are the following:

- `--application-id`
    - Represents the client id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
     (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_application_id` ADF global parameter.
- `new-data-version`
    - Represents the version of the new data in Azure Sql.
    - The value is a date time string that is set by an ADF pipeline variable. The date time string represents the time when the data is 
    inserted in the database. 
- `configuration-type`
    - Represents a string that identifies a Azure Sql table.
    - The allowed values are `LatestVersionOfEmployeeProfile`, `LatestVersionOfHRDataEmployeeProfile`, `LatestVersionOfInferredRoles`.
- `table-name`
    - Represents the name of the table for which the data version will be changed.
    - The allowed values are the tables from Azure Sql `gdc_database` that are using versioning: `employee_profiles`, `hr_data_employee_profile`, `inferred_roles`.
- `--directory-id`
    - Represents the tenant id of `gdc-service` service principal (app registration) used by all the spark jobs in order to connect to other services
    (Azure Sql, Key Vault, Azure Blob Storage).
    - The parameter value is taken from `gdc_app_registration_directory_id` ADF global parameter. 
- `--adb-secret-scope-name`
    - The client secret of the `gdc-service` service principal is stored in one of the Databricks secret scopes. This parameter is used to pass the name of the scope.
    - The parameter value is taken from `gdc_adb_secret_scope` ADF global parameter.
- `--adb-sp-client-key-secret-name`
    - The client secret of the `gdc-service` service principal is stored in Databricks secrets. This parameter is used to provide the name of the secret.
    - The parameter value is `gdc-service-principal-secret`.
- `--jdbc-hostname`
    - Represents the Azure Sql database host.
    - The value is taken from ADF global parameter `gdc_adb_secret_scope`. 
- `--jdbc-port`
    - Represents the Azure Sql database port.
    - The value is set to `1433` in ADF.
- `--jdbc-database`
    - Represents the name of the gdc database.
    - The value is taken from ADF global parameter `gdc_azure_sql_database`.
- `--jdbc-username-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the username for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-user` in ADF. 
- `--jdbc-password-key-name`
    - Represents the name of the key stored in Azure Key Vault that contains the user password for the Azure Sql Database connection.
    - The value is set to `azure-sql-backend-password` in ADF.
- `--log-analytics-workspace-id`
    - Represents the workspace id of the Log Analytics Workspace where the logs of all spark jobs and JGraph application get stored.
    - The value is taken from ADF global parameter `gdc_log_analytics_workspace_id`.
- `--log-analytics-workspace-key-name`
    - Represents the name of the key from Azure Key Vault that contains the value of the Log Analytics Workspace API key.
    - The value is set to `log-analytics-api-key` in ADF.
- `--key-vault-url`
    - Represents the url of Azure Key Vault where secrets (such as database username and password) used by the job are stored.
    - The value is taken from ADF global parameter `gdc_key_vault_url`.
- `--use-msi-azure-sql-auth`
    - This parameter can have two values `true` or `false`. If the value is true then the Azure Sql connection authentication is made using Managed Service Indentity. 
    If the value is false then the Azure Sql connection authentication is made using user and password credentials.
    - The value is taken from `gdc_azure_sql_msi_auth_enabled` ADF global parameter.




