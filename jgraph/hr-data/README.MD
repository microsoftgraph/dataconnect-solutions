Params: 

--application-id <service principal application/client id>
--directory-id <service principal directory/tenant id>
--adb-secret-scope-name <databricks secret scope>
--adb-sp-client-key-secret-name <databricks secret key name of service principal client secret>
--key-vault-url <URL for Azure KeyVault which is to be used>
--input-storage-account-name
--input-container
--input-folder-path
--jdbc-hostname <URL to database server>
--jdbc-port <database port>
--jdbc-database <database name>
--jdbc-username-key-name <KeyVault secret name containing db username>
--jdbc-password-key-name <KeyVault secret name containing db password>
--use-msi-azure-sql-auth <set to true to authenticate to DB using MSI>
--max-db-connections <value>
--log-analytics-workspace-id <value>
--log-analytics-workspace-key-name <Log Analytics workspace key secret name>
--ingestion-mode <value>
--fail-fast-on-corrupt-data <boolean value>

# Local Development

### Params:
```
--dev true --ingestion-mode sample_mode
```

--ingestion-mode possible values:
* sample_mode
* production_mode
* simulated_mode

### Env Var
```
GDC_JDBC_HOSTNAME=localhost
GDC_JDBC_PORT=1433
GDC_JDBC_DATABASE=gdc_database
GDC_JDBC_USERNAME=sa
GDC_JDBC_PASSWORD=password_!23
GDC_MAX_CONNECTIONS=8
```