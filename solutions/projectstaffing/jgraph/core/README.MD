# The ProjectStaffing (JGraph) App Service

## Running the Application in Azure
The ProjectStaffing application is meant to be run as an Azure App Service, and is deployed as such by the deployment script.  
The AppService name (which determines the URL of the AppService UI) can be set during the initial deployment process. 
The default value is `project-staffing-app`, so the default URL is `https://project-staffing-app.azurewebsites.net`.  
You can also find it by going to the deployment resource group, opening the jgraph App Service and going to the Overview page.  
The application automatically gets started during deployment, however, you can also stop it, start it and restart it
from the App Service Overview page.

### User Authentication
The ProjectStaffing AppService uses the [built-in authentication mechanism of the AppService](https://docs.microsoft.com/en-us/azure/app-service/overview-authentication-authorization).   
Anonymous access is not permitted.
In order to log in, you need to access the AppService UI URL and use your Azure ActiveDirectory identity to log in.


## Running the Application locally
Although a completely standalone local deployment is not possible (the app still needs to rely on certain services,
such as Azure Search and Active Directory, from an existing development deployment in Azure), it is useful to run the
app locally as this allows both fast development and testing of new features, as well as debugging in case of problems.  

### Preconditions
The following steps need to be taken for the local app to be fully functional. 
Some of these steps need to be performed after the local application was started, for it to be fully functional.

#### Defining a Run Configuration in your favorite IDE
We used IntelliJ IDEA for the development process, so that is what will be used as reference. 
However, the equivalent steps can be done in your favorite IDE.  
- Go to `Run` -> `Edit Configurations` -> Add New Configuration -> `Application` and choose a name for your configuration (e.g. `ProjectStaffing`)
- Set the classpath of the `jgraph-core` module
- Set `com.microsoft.graphdataconnect.skillsfinder.Runner` as the main class
- Set under `Environment variables` all the environment variables mentioned in the sections below

#### Azure Search connection configuration
When running the application locally, we currently rely on the Azure Search service from an existing development application deployment in Azure.  
This approach is safe, as the local application only reads the data from Azure Search, so there is no risk of interfering with
the development application running in Azure. The only exception is performing an ingestion mode switch (described below). 
This approach works as long as the same employee data is used to populate the local database, as for the database (Azure Sql) in that development environment.  
This way, the data in the local DB and in Azure Search is in sync.  

When running the application locally, we bypass the azure key vault library and instead we directly provide the api key to Azure Search as an environment variable.  
To obtain this, go to the "app key vault" (`GdcApp<deployment-hash>`) in your deployment resource group and copy the value of the secret `gdc-search-service-key`.  
**Make sure NOT to commit the key, and only use keys of non-critical development environments that only contain simulated data**
```
AZURE_SEARCH_APIKEY=<key_value>
```

This requires us to disable Azure KeyVault to prevent a connection error.
```
AZURE_KEYVAULT_ENABLED=false
```

Also, the Azure Search URL and emails index name have to be set. The URL can be obtained from the Search Service resource
in the deployment resource group, from the Overview page:
```
AZURE_SEARCH_BASE_URL=<url>
AZURE_SEARCH_EMAILS_INDEX=gdc-emails
```

This enables the anonymous authentication option that is required to run API tests without adding the app service authentication token.
```
ALLOW_ANONYMOUS_USER=true
```
The name of the employee index is computed by the employees ADF pipeline based on the current date (to support versioning).
Therefore, the current name of the index has to be set in the local database (the local database setup is presented in 
[the next step](#mssql-local-database-configuration)).  
This can be done with the following command:
```
INSERT INTO gdc_database.dbo.configurations ([type], configs)
VALUES('AzureSearchEmployeesIndex', '{"employeesIndexName":"<gdc-employees-version>"}');
```
Replace `<gdc-employees-version>` with the name of the employee index. The name can be found in the Azure Portal by 
following the next steps:
- Go to the search service (`gdc-search-service<deployment-hash>`) in your deployment resource group  
- Select the "Overview" section on the left of the page
- Select the "Indexes" tab from the middle of the page
- After "Indexes" is selected, you should see the names of the two search indexes `gdc-emails` (which is always the 
same), and `gdc-employees-<date>` which is the employees index name (the name has a date suffix)

#### Mssql local database configuration
- In order to set up a local database run the deployment/local/docker-compose.yml file
```shell script
cd deployment/local
docker-compose -f ./docker-compose.yml up -d
```
- Two docker containers will start each running a mssql server. One will be used for running the application locally, the other for running tests locally
  
- After the containers have started for the first time, run the following commands in order to create the `gdc_database` in each mssql server:
```shell script
docker exec -i $(docker ps -aqf "name=gdc_database$") /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'password_!23' -d master <./create-database.sql
docker exec -i $(docker ps -aqf "name=gdc_database-test$") /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'password_!23' -d master <./create-database.sql
```

- The application will have to connect to a database that contains the data related to the same employees that are stored in Azure Search. 
In order to achieve this, the employee data will have to be exported from Azure Sql and imported into a local mssql docker container.
The following tables are necessary in order for the application to be fully functional: 
`configurations`, `employee_profile`, `employee_responsibilities`, `employee_skills`, `hr_data_employee_profile`, `inferred_roles`.

- The following env variable has to be set in order for the app to connect to the local mssql container:
```
SQLCONNSTR_GDC_SERVER=jdbc:sqlserver://localhost:1433;databaseName=gdc_database;user=sa;password=password_!23;
```

#### Copy the app service auth cookie locally
Since while running in an AppService the ProjectStaffing app uses the built-in authentication mechanism of the AppService,
when running the application locally (i.e. outside the AppService), there is no authentication functionality to rely on.  
However, most of the functionality of the application is dependent on the identity of the current user, which is 
derived by the application code from the `X-MS-CLIENT-PRINCIPAL-NAME` request header or, alternatively, from the 
authentication cookie (`AppServiceAuthSession`). These get created by the AppService authentication process and are thus 
not available locally.  
Therefore, while running locally you will need to explicitly provide your identity to the application, by manually  
setting the authentication cookie. This must be previously generated by logging into the development App Service

To retrieve the authentication cookie from the App Service:
1. open the ProjectStaffing UI of the development App Service in the browser
    - Please see the section about [Running the Application in Azure](#running-the-application-in-azure) for details 
      about how to obtain the URL of the AppService UI
2. log into the application if required
3. use the browser developer tools to obtain the cookie's value
    - in Chrome:
        - right click in the page -> `Inspect` -> go to the `Application` tab -> `Storage` -> `Cookies`
        - copy the whole value of the `AppServiceAuthSession` cookie
    - in Firefox
        - right click in the page -> `Inspect` -> go to the `Storage` tab -> `Cookies`
        - copy the whole value of the `AppServiceAuthSession` cookie

Set the cookie in the ProjectStaffing application running on localhost:
1. after the local application is started, open the local ProjectStaffing UI in the browser (localhost:7655)
2. use the browser developer tools to set the cookie's value
    - in Chrome:
        - right click in the page -> `Inspect` -> go to the `Application` tab -> `Storage` -> `Cookies`
        - create a cookie named `AppServiceAuthSession` and paste the copied string as its value
    - in Firefox
        - right click in the page -> `Inspect` -> go to the `Storage` tab -> `Cookies`
        - create a cookie named `AppServiceAuthSession` and paste the copied string as its value
        
        
The application will use the cookie to authenticate to a running AppService, the url of the JGraph App Service running 
in Azure has to be set in `JGRAPH_APPSERVICE_URL` env variable as presented below:
```
JGRAPH_APPSERVICE_URL=<jgraph_url> 
```

> Note: The auth cookie from the App Service needs to be updated locally every time it got refreshed in the App Service 
> (e.g. because the session expired).


### Starting the application from your IDE
Now that all the prerequisites have been met, start the previously defined run configuration from our IDE.  
E.g. in IntelliJ IDEA, start the `ProjectStaffing` run configuration defined previously either in Run or Debug mode.


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
To run the tests via maven, simply run the following command from the `jgraph` folder, or the specific module which you want to test
```
mvn test
```
Alternatively, the unit tests automatically get run when you build a module, as long as the `-DskipTests` flag is not used
```
mvn clean install
```

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
