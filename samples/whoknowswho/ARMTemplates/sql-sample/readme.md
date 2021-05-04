This sample covers:

 1. Creating a SQL Server via ARM template
 2. Importing a sql db from bacpac file that contains the necessary tables with appropriate schema.
 3. Copying data from Office 365 to SQL DB. ADLS is used as intermediate location to achieve the copying of data to sql.


> **NOTE:** The `sampleimportdb.bacpac` file needs to be uploaded to an accessible location and the `dbBackupURI` variable in ARM template (`azuredeploy.json`) needs to be populated with the URL.

Helpful Links:

https://docs.microsoft.com/en-us/azure/sql-database/sql-database-export
