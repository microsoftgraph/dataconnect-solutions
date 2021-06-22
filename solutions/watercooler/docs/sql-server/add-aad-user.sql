-- https://docs.microsoft.com/en-us/azure/app-service/app-service-web-tutorial-connect-msi

CREATE USER [<jgraph-app-name>] FROM EXTERNAL PROVIDER;
ALTER ROLE db_datareader ADD MEMBER [<jgraph-app-name>];
ALTER ROLE db_datawriter ADD MEMBER [<jgraph-app-name>];
-- Enable this role if you want the app to create a sql schema
-- ALTER ROLE db_ddladmin ADD MEMBER [<jgraph-app-name>];
GO
