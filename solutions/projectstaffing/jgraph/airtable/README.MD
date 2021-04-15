Params: 

--application-id <service principal application/client id>
--directory-id <service principal directory/tenant id>
--secret-scope-name <databricks secret scope>
--secret-key-name <databricks secret key name of service principal client secret>
--jdbc-hostname <host of jdbc database>
--jdbc-port <databse port>
--jdbc-database <database name>
--airtable-base-key-name <value>
--airtable-api-key-name <value>
--max-db-connections <value> 


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
GDC_AIRTABLE_BASE_KEY=
GDC_AIRTABLE_API_KEY=
```