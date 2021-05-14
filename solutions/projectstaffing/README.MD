## ProjectStaffing application

The Project Staffing application is a Microsoft Graph Data Connect based solution which allows engagement managers 
to build the best suited teams for projects, by finding employees that are available and have the best set of skills 
for the project, as well as the most relevant set of potential skills.  
The application ingests data from Microsoft Graph Data Connect and complementary data sources using Azure Data Factory (ADF)
pipelines and uses this to build a model based on which the most relevant employees are recommended.

### Setting up the application

The ProjectStaffing application can be run locally for development purposes, but it needs to make use of some
services/resources from an existing full development deployment from Azure.

Here is an overview of the steps required to build the application locally, perform a full development deployment in
Azure and then run the application locally while using the resources from the Azure development deployment:

1. build the ProjectStaffing App Service jar and all the ADB jars, by following [these steps](./deployment/README.MD#building-all-the-projects-jars-at-once)
2. build the pygraph utils wheel, by following [these steps](./pygraph/azure_processing/pygraph_utils/README.md)
3. prepare the zip with all the resulting artifacts (required for deployment), by following [these steps](./deployment/README.MD#building-the-artifacts-zip)
4. to perform a fresh full deployment in Azure, please follow the steps described in the deployment script's [readme file](./deployment/arm/README.md)
   and in the [deployment overview document](docs/ProjectStaffingDeploymentOverview.MD)
5. to run the application in Azure, please follow [these steps](./jgraph/core/README.MD#running-the-application-in-azure)
6. to run the application locally, please follow [these steps](./jgraph/core/README.MD#running-the-application-locally)

#### Development

Please follow the following links to learn more details about the project [architecture](#architectural-components), 
[components](./deployment/README.MD#project-components) and [deliverables](./deployment/README.MD#project-deliverables).

In case changes are done to individual project components (the ProjectStaffing App Service, ADF pipelines, ADB libraries etc),
these components can can either be:
- built individually, as described in the subsections of the [local build documentation](./deployment/README.MD#building-the-project-locally),
  and [deployed individually](./deployment/README.MD#deploying-individual-components)
- built together with the entire project (either locally as described above or via a [build pipeline](./docs/build_pipeline.md))
  and deployed as part of a [full application update](./deployment/README.MD#updating-app-release-version-over-existing-deployment)
  
After the deployment, changes to the App Service usually only require restarting the service.  
Changes to ADF pipelines or ADB libraries require running the impacted ADF pipelines for the changes to take effect in the ProjectStaffing app service.  
If running the ADF pipelines resulted in changes in Azure SQL database, and you want to see the latest changes in the ProjectStaffing
running locally, then the latest version of the data from Azure SQL database needs to be exported and written to the local database.

### Architectural components

- The [ADF pipelines](docs/AzureDataFactory.MD) process the data from several sources using mainly Azure Databricks (ADB) 
  jobs written either in scala or spark. The pipelines are orchestrated by [ADF triggers](docs/ADF_trigger_creation_policy.md).

- The resulting data is written in Azure SQL database and/or in Azure Cognitive Search. Intermediate data is also written in AZBS.  

- Sensitive data is stored in Azure KeyVault.  

- Application logs and ADB jobs logs are also sent to Azure Log Analytics.  

- The application itself is an Azure App Service.

To [recommend relevant employees](./docs/searching_for_relevant_employees.md), the App Service combines results of queries
sent to Azure Search, and it enhances and filters this information using queries sent to Azure SQL database.  

It also uses the Azure SQL database to store and retrieve information about configurations, user settings, user teams, 
state of long-running operations, etc.  

![Architecture Diagram](docs/imgs/project-staffing-high-level-architecture.png)

### Data sources

The following data sources are used:

1. Microsoft 365 User profiles
    - data about employees retrieved using Microsoft Graph Data Connect
2. Microsoft 365 Managers
    - data about managers retrieved using Microsoft Graph Data Connect
3. Microsoft 365 Emails
    - sent and received emails retrieved using Microsoft Graph Data Connect
4. HR Data employee profiles
    - custom data format which can be derived from the systems used by the HR department to store data about the company's employees
    - it is meant to be complementary to the data obtained from Microsoft 365 (Microsoft Graph Data Connect)
    - more details can be found in the [HR Data documentation](./docs/HR_Data.md)

The data from the above sources is retrieved and processed in batch by the ADF pipelines. Besides these sources, the
profile picture of each individual employee is retrieved using Microsoft Graph API by the employee profiles ADF pipeline.

### Data ingestion modes

Since, due to security and privacy concerns, using an organization's real Microsoft 365 data requires the involvement 
and approval of the IT department (which can take a long time), the application comes with three data ingestion modes, 
out of which two modes rely on example datasets:

- Production mode
  - retrieves real production data from the three data sources mentioned above
  - the data is retrieved only for the members of the "GDC Employees" group, required during deployment
  - requires IT approval
  - all the preconditions are described in detail [here](./docs/AdminPermissions.MD)

- Simulated mode
  - relies on a set of artificially generated employee profiles and emails
  - meant for presentation and demonstration purposes
  - the files in this dataset can be manually downloaded using `wget <link>` from the following links:
    - https://bpartifactstorage.blob.core.windows.net/gdc-artifacts/simulated-data/m365_users/generated_user_profiles.json
    - https://bpartifactstorage.blob.core.windows.net/gdc-artifacts/simulated-data/m365_managers/generated_managers.json
    - https://bpartifactstorage.blob.core.windows.net/gdc-artifacts/simulated-data/m365_emails/generated_emails.json
    - https://bpartifactstorage.blob.core.windows.net/gdc-artifacts/simulated-data/hr_data/generated_hr_data.csv
  - this data automatically gets copied by the deployment script to the `demodata<deploymentHash>` storage account
    of the deployment resource group, in the `simulated-data` container, in the following respective folders:
    `m365_users`, `m365_managers`, `m365_emails`, `hr_data`
    - any manual changes to the simulated data require uploading it to these locations
    - in simulated mode, the ADF pipelines retrieve the data from these 4 locations, instead of the production data sources described above

- Sample mode
  - relies on a subset of real internal data obtained from employees who explicitly provided some of their emails and profile data
  - this approach can be used as a shortcut to validate how the product works with known internal people, until approval is obtained from IT to use full production data
  - the required schema of the sample data and the AZBS location where it needs to be placed is described in detail [here](./docs/InputSampleData.MD)
  - in sample mode, the ADF pipelines retrieve the data from the AZBS sample data locations, instead of the production data sources described above

The initial ingestion mode can be chosen during deployment via the deployment script, 
by typing `production_mode`/`simulated_mode`/`sample_mode` when prompted for the ingestion mode.

The switch from one ingestion mode to another can be performed by application admins from the ProjectStaffing UI via
the `Settings -> Ingestion Mode` menu.

### Documentation index

[ADB python scripts/PySpark jobs](./pygraph/azure_processing/README.md)  
[ADB scala spark jobs](./docs/ADBScalaJobsParameters.MD)  
[ADF pipelines](./docs/AzureDataFactory.MD)  
[ADF triggers](./docs/ADF_trigger_creation_policy.md)  
[Admin permissions](./docs/AdminPermissions.MD)  
[Building the project locally](./deployment/README.MD#building-the-project-locally)  
[Building the project via the build pipeline](./docs/build_pipeline.md)  
[CI track](./docs/build_pipeline.md)  
[Components](./deployment/README.MD#project-components)  
[Data enrichment](./docs/enrichment_pipelines.md)  
[Deliverables](./deployment/README.MD#project-deliverables)  
[Deployment](./deployment/README.MD#deployment)  
[Deployment on fresh environments](docs/ProjectStaffingDeploymentOverview.MD)  
[Deployment script](./deployment/arm/README.md)  
[Employee profile Azure Search schema](./docs/Employee_profile_schema_example.md)  
[Full deployment](docs/ProjectStaffingDeploymentOverview.MD)  
[HR Data](./docs/HR_Data.md)  
[Ingestion modes](#data-ingestion-modes)  
[Production mode preconditions](./docs/AdminPermissions.MD)  
[Python utils wheel](./pygraph/azure_processing/pygraph_utils/README.md)  
[Releasing a new version](./jgraph/README.md)  
[Running the application](./jgraph/core/README.MD)  
[Sample data schema and location](./docs/InputSampleData.MD)  
[Searching for relevant employees](./docs/searching_for_relevant_employees.md)  
[Search results sorting and paging](./docs/SearchResultsSortingAndPaging.MD)  
[Test track](./docs/test_track.md)  
[Testing locally](./jgraph/core/README.MD#running-the-tests-locally)  
[UI](./jgraph/ui/README.md)  