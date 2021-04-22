# The SkillsFinder (JGraph) App Service

## Running the Application in Azure
The SkillsFinder application is meant to be run as an Azure App Service, and is deployed as such by the deployment script.  
The URL of the AppService can be set during the initial deployment process.  
You can also find it by going to the deployment resource group, opening the jgraph App Service and going to the Overview page.  
Preconditions:
- An environment variable with the Azure Key Vault uri has to be set in Azure App Service Settings -> Configurations -> Application Settings e.g. `AZURE_KEY_VAULT_URI=https://gdckeyvault.vault.azure.net/`
- A secret with the name `azure-search-api-key` has to be set in an Azure Key Vault instance. 
- The managed identity of the application needs to have Reader role (defined in "Access Control (IAM)" tab of the Azure Key Vault instance page) 
  and Get & List access policies (defined in Settings -> "Access Policies" tab) for that particular Azure Key Vault instance.


## Running the Application locally
Although a completely standalone local deployment is not possible (the app still needs to rely on certain services,
such as Azure Search, from an existing development deployment Azure), it is useful to run the application locally
as this allows both fast development and testing of new features, as well as debugging in case of problems.  

In order for the application authentication mechanism to work locally, there is a manual step that has to be done:  
From the JGraph application running in Azure, the `AppServiceAuthSession` cookie will have to be copied and set to the JGraph application running on localhost.  
This can be done using the browser developer tools.

### Azure Search connection configuration
When running the application locally, we currently rely on the Azure Search service from an existing development application deployment in Azure.  
This approach is safe, as the local application only reads the data from Azure Search, so there is no risk of interfering with
the development application running in Azure. The only exception is performing an ingestion mode switch (described below). 
This approach works as long as the same employee data is used to populate the local database, as for the database (Azure Sql) in that development environment.  
This way, the data in the local DB and in Azure Search is in sync.  
When running the application locally, we bypass the azure key vault library and instead we directly provide the api key to Azure Search as an environment variable.  
**Make sure NOT to commit the key, and only use keys of non-critical development environments that only contain simulated data**
```
AZURE_SEARCH_APIKEY=<key_value>
```

Also, the Azure Search URL and emails index name have to be set:
```
AZURE_SEARCH_BASE_URL=<url>
AZURE_SEARCH_EMAILS_INDEX=gdc-emails
```

This requires us to disable Azure KeyVault to prevent a connection error. 
```
AZURE_KEYVAULT_ENABLED=false
```

### Mssql local database configuration
- In order to set up a local database run the deployment/local/docker-compose.yml file. Two docker containers will start each running a 
mssql server. One will be used for running the application locally the other for running tests locally.
After the containers have started run the following commands in order to create the `gdc_database` in each mssql server:
```
docker exec -i $(docker ps -aqf "name=gdc_database$") /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'password_!23' -d master <./create-database.sql
docker exec -i $(docker ps -aqf "name=gdc_database-test$") /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'password_!23' -d master <./create-database.sql
```

- The application will have to connect to a database that contains the data related to the same employees that are stored in Azure Search. 
In order to achieve this, the employee data will have to be exported from Azure Sql and imported into a local mssql docker container.
The following tables are necessary in order for the application to be fully functional: 
`configurations`, `employee_profile`, `employee_responsibilities`, `employee_skills`, `hr_data_employee_profile`, `inferred_roles`.

- The following env variable has to be set in order for the app to connect to the local mssql container:
```
SQLCONNSTR_GDC_SERVER=jdbc:sqlserver://localhost:1433;databaseName=gdc_database;user=sa;password=BDE_great!23;
```

### Testing the "Ingestion Mode Switch" and "HR Data Upload" functionalities locally
The `Ingestion Mode` and `Upload HR Data` menus from the `Settings` panel are only accessible to administrators of the app.  
The application relies on Azure authentication headers to determine if the current application user is an admin
(member of the GDC Admins group), in order to allow or restrict access to certain REST API endpoints. 
The headers are also used to determine if the user has consented to the API permissions required by the application.  

When running the application locally, the azure authentication headers are not arriving in the application requests. A workaround is needed.  
In order to be able to access the admin functionalities locally, we will have to perform some temporary local changes to the application code.  
Manually set the following authentication headers in the methods corresponding to REST API endpoints of the following REST controllers:  
`AdminUserController`, `IngestionModeController` and `HRDataUploadController`  
The following lines of code will have to be the first lines in each method receiving an `@RequestHeader httpHeaders: HttpHeaders` parameter:
```
httpHeaders.set("x-ms-client-principal", "<x-ms-client-principal-value>")
httpHeaders.set("x-ms-token-aad-id-token", "<x-ms-token-aad-id-token-value>")
httpHeaders.set("x-ms-token-aad-refresh-token", "<x-ms-token-aad-refresh-token-value>")
```

The value of the `x-ms-client-principal` , `x-ms-token-aad-id-token`, `x-ms-token-aad-refresh-token` headers can be determined from the JGraph App Service logs
when `JGRAPH_LOG_LEVEL` env variable value is `debug`. This has to be set from JGraph App Service `Configurations` section.  
When `JGRAPH_LOG_LEVEL` is configured as described above, every request will contain the authentication headers.

In order to use the "Ingestion Mode Switch" and "HR Data Upload" functionalities locally, the following env variables will have to be set:
- the url of the JGraph App Service running in Azure
```
JGRAPH_APPSERVICE_URL=<jgraph_url> 
```
- the tenant id of the service principal (app registration) used by the JGraph App Service
```
SERVICE_PRINCIPAL_TENANT_ID=<tenant_id>  
```
- the client id of the service principal (app registration) used by the JGraph App Service
```
SERVICE_PRINCIPAL_CLIENT_ID=<client_id> 
```
- the secret of the service principal (app registration) used by the JGraph App Service;
the secret is stored in the App Key Vault, the name of the secret is gdc-jgraph-service-principal-secret
```
JGRAPH_SERVICE_PRINCIPAL_SECRET=<sp_secret> 
```
- the subscription id of the Azure Data Factory service 
```
ADF_SUBSCRIPTION_ID=<subscription_id> 
```
- the name of the resource group where the application is deployed
```
ADF_RESOURCE_GROUP_NAME=<resource_group_name>
```
- the name of the Azure Data Factory service name
```
ADF_NAME=<azure_data_factory_service_name>
```
- the name of the Security Group that contains the users that are considered admins of the application
```
GDC_ADMINS_GROUP_ID=<admins_group_object_id>
```
- the name of the storage account that contains the simulated and sample data
```
DEMO_DATA_STORAGE_ACCOUNT_NAME=<demo_data_storage_account>
```

### Running the tests locally
The following environment variable has to be set:
```
AZURE_SEARCH_APIKEY=<key_value>
```
(although, at the moment, there are no tests that interact with Azure Search, so AZURE_SEARCH_APIKEY isn't used)  
The gdc_database-test docker container needs to be running for the tests to work.  
The tests can then be run either using maven or the IDE.  

---

### Update database schema using flyway
Run these commands (for app & tests) in `core` module base directory to manually apply DB schema change:  
```
mvn flyway:info flyway:clean flyway:migrate -Dflyway.user=sa -Dflyway.password='password_!23' -Dflyway.url=jdbc:sqlserver://localhost:1433;databaseName=gdc

mvn flyway:info flyway:clean flyway:migrate -Dflyway.user=sa -Dflyway.password='password_!23' -Dflyway.url=jdbc:sqlserver://localhost:11433;databaseName=gdc  
```
Normally, if flyway is enabled, the schema changes are applied automatically when the application starts, so these steps are rarely necessary.

---

### Handling schema conflicts when flyway is enabled
If flyway is enabled, and you attempt to run the application using a schema version which is not "historically derived"
from the current schema in the database, then flyway will throw an error, and the application will fail to start.  
This usually happens when an application version is deployed, then another version is deployed which started from
an older point in the git history, and both application versions contain divergent changes to the DB schema, compared to
their common point in the git history.
In this case, one solution is to go to the database and manually fix the flyway_schema_history table, by deleting the 
rows corresponding to the divergent changes, and then building and starting the application again.
