##Azure Search connection
####Runnig the Application in Azure
- An environment variable with the Azure Key Vault uri has to be set in Azure App Service Settings -> Configurations -> Appplication Settings e.g. `AZURE_KEY_VAULT_URI=https://gdckeyvault.vault.azure.net/`
- A secret with the name `azure-search-api-key` has to be set in an Azure Key Vault instance. 
- The managed identity of the application needs to have Reader role (defined in "Access Control (IAM)" tab of the Azure Key Vault instance page) and Get & List access policies (defined in Settings -> "Access Policies" tab) for that particular Azure Key Vault instance.


####Running the Application locally
When running the application locally we bypass the azure key vault library and instead we directly provide the api key to Azure Search as environment variable.
This requires us to disable azure key vault to prevent a connection error. 

The following environment variables have to be set in the intellij app configurations:
```
AZURE_KEYVAULT_ENABLED=false
AZURE_SEARCH_APIKEY=<key_value>
```

####Running the tests locally
The following environment variable has to be set:
```
AZURE_SEARCH_APIKEY=<key_value>
```
(although at the moment, there are no tests that interact with Azure Search, so AZURE_SEARCH_APIKEY isn't used)
##flyway install database:  
run these commands (for app & tests) in `core` module base directory for each DB schema change:
```
mvn flyway:info flyway:clean flyway:migrate -Dflyway.user=sa -Dflyway.password='password_!23' -Dflyway.url=jdbc:sqlserver://localhost:1433;databaseName=gdc
```

```
mvn flyway:info flyway:clean flyway:migrate -Dflyway.user=sa -Dflyway.password='password_!23' -Dflyway.url=jdbc:sqlserver://localhost:11433;databaseName=gdc
```

##Testing Ingestion Mode Switch locally using Azure Data Factory from dev environment
The following environment variables have to be set in intellij app configurations:
```
SERVICE_PRINCIPAL_TENANT_ID=
SERVICE_PRINCIPAL_CLIENT_ID=
ADF_SUBSCRIPTION_ID=a63c1e51=
ADF_RESOURCE_GROUP_NAME=
ADF_NAME=
GDC_ADMINS_GROUP_ID=
```


##Testing
### setup
run docker-compose using files from deployment/local/tests/ directory
