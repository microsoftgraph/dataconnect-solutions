## SkillsFinder application

The purpose of the application is to allow engagement managers to build the best suited teams for projects, by finding the employees that have the best set of skills for the project, as well as the best set of potential skills.

The application ingests data from several data sources offline using Azure Data Factory (ADF) pipelines and uses this to build a model based on which the most relevant employees are recommended.

The following data sources are used:
1. M365 User profiles
   - data retrieved using Graph Data Connect (GDC)
2. M365 Emails
   - sent and received emails retrieved using Graph Data Connect (GDC)
3. HR Data employee profiles
   - custom data format which can be derived from the systems used by the HR department to store data about the company's employees
   - it is meant to be complementary to the data obtained from M365 (Graph Data Connect)
   - more details can be found in the [HR Data documentation](./docs/HR_Data.md)
   
The ADF pipelines process the data from these sources using mainly Azure Databricks (ADB) jobs written either in scala or spark.  
The resulting data is written in AzureSQL and/or in Azure Cognitive Search. Intermediate data is also written in AZBS.  
Sensitive data is stored in Azure KeyVault.  
Application logs and ADB jobs logs are also sent to Azure Log Analytics.  
The application itself is an Azure App Service. 
To [recommend relevant employees](./docs/searching_for_relevant_employees.md), the App Service combines results of queries
sent to Azure Search, and it enhances and filters this information using queries sent to AzureSql.  
It also uses the AzureSql database to store and retrieve information about configurations, user settings, user teams, 
state of long-running operations, etc.  

Information about the project components, its deliverables, as well as the steps required to build and deploy
the project and its components (as a whole or individually) can be found [here](./deployment/README.MD)  

Information about how to run the application can be found [here](./jgraph/core/README.MD)



