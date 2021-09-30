# Build and Deployment Process

## Table of contents
- [Project components](#project-components)
- [Project deliverables](#project-deliverables)
- [Building the project](#building-the-project)
    - [Building from source using the local development environment](#building-from-source-using-the-local-development-environment)
        1. [Prerequisites](#prerequisites)
        2. [Building the App Service](#building-the-app-service)
        3. [Building the ADB jars](#building-the-adb-jars)
        4. [Building all the project's jars at once](#building-all-the-projects-jars-at-once)
        5. [Building the python utils wheel](#building-the-python-utils-wheel)
        6. [Building the deployment artifacts zip](#building-the-deployment-artifacts-zip)
    - [Building the project using Azure DevOps pipelines](#building-the-project-using-azure-devops-pipelines)
- [Deployment](#deployment)
    - [Performing deployment on a new environment](#performing-deployment-on-a-new-environment)
    - [Updating app release version over existing deployment](#updating-app-release-version-over-existing-deployment)
    - [Deploying individual components](#deploying-individual-components)
        - [Deploying Azure DataFactory entities](#deploying-azure-datafactory-entities)
        - [Deploying the ProjectStaffing docker image](#deploying-the-projectstaffing-docker-image)
        - [Deploying jars, python scripts and python utils wheel to Azure Databricks cluster](#deploying-jars-python-scripts-and-python-utils-wheel-to-azure-databricks-cluster)

## Project components
The ProjectStaffing application consists of 2 kinds of components:
- the "dynamic" ones which define the behavior of the application
- the "static" ones which define underlying infrastructure upon which the application is built

The dynamic components are the ones that change most often and can be divided in the following categories:
- the AppService code (mainly scala and javascript code, as well as some procedural code written in T-SQL)
    - built into a docker image
- the ADF (Azure DataFactory) components (pipelines, datasets, linked services, triggers etc) which orchestrate the offline processing of data
    - ARM template in a json file named pipelines.json
- the ADB (Azure Databricks) Spark jobs (scala code) processing source data
    - built into jars
- common scala code used by both app service code and ADB jobs
    - maven module which gets shaded into the jars and into the docker image
- the ADB pyspark jobs processing source data
    - python scripts delivered as they are
- common python code used by the python ADB jobs
    - built into a python wheel


## Project deliverables
There are two main project deliverables:
- the docker image used for the Project Staffing AppService
    - needs to be uploaded to a docker repository (Azure Container Registry is recommended) from where it can be accessed by the deployment script and the App Service
- the project artifacts zip
    - build via bin/prepare_artifacts.sh
    - needs to be uploaded to a "project deliverables AZBS storage account", to make it easily accessible to admins performing deployments
    - contains:
        - the jars, python scripts and python wheel files, defining Spark jobs run on ADB by ADF pipelines
        - the pipelines.json file, defining the ADF entities
        - the schema.sql file, containing the database schema creation statements
        - the stored_procedures.sql file, containing the MS SQL stored procedure used for relevant employee retrieval and enhancement
        - the data.sql file, containing the insert statements which populate the database when deploying on a new environment
        - all the scripts and ARM templates used to deploy the application on a new environment

# Building the project
You have two options for building the project:
- building from source using local development environment
- building the project using Azure DevOps pipelines

## Building from source using the local development environment
> NOTE: For developing the application, only macOS/Linux based systems were used.  
> Building the application is possible from Windows 10 systems (using Powershell, not the command-line interpreter), however 
> solid knowledge of your development environment and of the differences between Windows/Unix systems is required. Some
> adaptations of the provided instructions might be required as well, as well as some additional steps during deployment.

### Prerequisites
1. Install JDK 1.8
    - for example, to install the OpenJDK, follow the appropriate installation steps from: https://openjdk.java.net/install/
      for Ubuntu, https://wiki.openjdk.java.net/display/jdk8u for Windows 10 (use 8u292-b10 version, the other versions might 
      not work), https://adoptopenjdk.net/?variant=openjdk8 (macOS), or search online for a more detailed installation guide, 
      matching your OS
    - required to build, develop and run the project's JVM based components (the AppService code and ADB scala spark jobs)
2. Install latest Maven 3 version
    - download the latest maven 3 binary distribution archive from https://maven.apache.org/download.cgi. For Windows 
    use maven 3.8.1 (https://mirror.efect.ro/apache/maven/maven-3/3.8.1/binaries/), as other versions might not work.
    - installation steps: https://maven.apache.org/install.html
    - required to build, develop and run the project's JVM based components (the AppService code and ADB scala spark jobs)
3. Install docker
    - follow the installation steps specific to your OS from https://docs.docker.com/get-docker/
    - required for building and publishing the AppService docker image, and for running tests locally (the tests use database docker containers)
    - on Windows, if you are using Docker with WSL backend, you might receive an error telling you that you need the latest 
      Linux kernel to be installed. In this case, please follow steps 1-5 from https://docs.microsoft.com/en-us/windows/wsl/install-manual
4. Install nodejs 14.x, npm and yarn 1.22
    - required to build and run the application's UI
    - for details please follow the [UI setup steps](../jgraph/ui/README.md#project-setup)
5. Install Python 3 and related environment
    - the python environment setup will differ if you plan to simply build the pygraph utils wheel locally or 
      if you want to do development and run pyspark jobs locally
    - please follow the appropriate [python environment setup steps](../pygraph/azure_processing/README.md#setup-guide)

> NOTE: building certain components will also run the tests for that component.  
> **On Windows**, certain tests relying on Apache Spark will fail with the following exception:  
> `ERROR Shell: Failed to locate the winutils binary in the hadoop binary path
> java.io.IOException: Could not locate executable null\bin\winutils.exe in the Hadoop binaries.`   
> In this scenario the [official databricks documentation](https://docs.databricks.com/dev-tools/databricks-connect.html#cannot-find-winutilsexe-on-windows)
> points you to hadoop installation instructions. However, there might be a shortcut by performing the following steps:
> - clone https://github.com/cdarlint/winutils
> - in environment variables set:
>    - HADOOP_HOME=<path_to_folder_where_winutils_was_cloned>\winutils\hadoop-2.9.2
>    - extend PATH with the following entry %HADOOP_HOME%\bin
> - close the PowerShell terminal, reopen it, and only after that you can re-run the desired build command

### Building the App Service
Building the ProjectStaffing App Service jar requires the following two modules to have been previously built:
- the UI module
    - the steps required to build the UI module are described in the [UI documentation](../jgraph/ui/README.md)
    - when the UI is built, the resulting artifacts are meant to get integrated into the ProjectStaffing App Service jar. This is done in two steps:
        1. building the UI module
            - the relevant resulting artifacts are placed in `ui/target/dist`
        2. building the ProjectStaffing "core" module, as described below
            - this first uses the `maven-resources-plugin` to copy the artifacts in the `core` module's resources folder in `resources/public`
            - a later step of the build process copies the module resources to `target/classes` to get packed into the resulting jar
- the jgraph-common module
    ```shell script
    cd ./jgraph/jgraph-common
    mvn clean install
    ```

Unless you plan to run build the app without running the unit test, you will have to first start the docker containers
used while the tests are run (the test db container).  
This is explained in the "Mssql local database configuration" section of the ProjectStaffing `core` module [README file](../jgraph/core/README.MD).    
To build the ProjectStaffing app (i.e. the `core` module), run the following commands:
```shell script
cd ./jgraph/core
mvn clean install
```
This approach also runs the unit tests. To build the app without running the tests, run `mvn clean install -DskipTests`  
The resulting jar (jgraph-core-<version>.jar) can be found in `jgraph/core/target/`  
The resulting jar can then be [run locally](../jgraph/core/README.MD).  
Normally, the ProjectStaffing App Service jar is not built as a standalone artifact, but instead it is built by  
the [CI build pipeline](../docs/build_pipeline.md) into a docker image, and deployed as such into the App Service.
> Note: if changes were performed to the jgraph-common module, it will have to be rebuilt before building the current module

### Building the ADB jars
We are going to exemplify for one ADB jar, but the same steps apply to any other module defining a scala spark job
```shell script
cd ./jgraph/hr-data
mvn clean install
```
This approach also runs the unit tests. To build the jar without running the tests, run `mvn clean install -DskipTests`  
The resulting jar (hr-data-to-azure-sql.jar) can be found in `jgraph/hr-data/target/`  
The resulting jar can then be [run locally](../jgraph/hr-data/README.MD) or deployed to ADB as described below.
> Note: if changes were performed to the jgraph-common module, it will have to be rebuilt before building the current module

### Building all the project's jars at once
To build the ProjectStaffing App Service jar and all the ADB jars in one go, run the following commands:
```shell script
cd ./jgraph
mvn clean install
```
This approach also runs the unit tests. However, in order to have your tests pass, you'll have to provide connectivity 
to a sql database. To provide access to the database follow the steps described [here](../jgraph/core/README.MD#mssql-local-database-configuration).

To build the jars without running the tests, run `mvn clean install -DskipTests`  
The resulting jars can be found in the `target` folder of the corresponding module

### Building the python utils wheel
The steps required to build the pygraph utils wheel are defined in the pygraph_utils [readme file](../pygraph/azure_processing/pygraph_utils/README.md)

### Building the deployment artifacts zip
After all the project artifacts were build as described above, from the ProjectStaffing root folder run `./bin/prepare_artifacts.sh`.  

> NOTE: if you are on Windows and you encounter issues running `./prepare_artifacts.sh`, then do one of the following:
>   - either download git-scm from [http://git-scm.com/download/win](http://git-scm.com/download/win) and use **git-bash** to run the script that will prepare the artifacts
>   - either run ```sed -i 's/\r$//' prepare_artifacts.sh```; (however `sed` as a command, cannot be executed only from git-bash or cygwin)

Since the script was designed for mac/linux systems, when running it on Windows via Powershell, the last part handling 
the python utils wheel normally fails. This could be avoided if you explicitly configured Powershell and Git Bash so 
that python3 and pip3 (e.g. the ones from the gdc conda environment) are directly accessible, however setting this up is
out of the scope of this documentation. 
As such, there are a few steps you will need to manually perform on Windows, from Powershell:
1. verify the wheel was built. From the project root run `ls solutions/projectstaffing/pygraph/azure_processing/pygraph_utils/dist`
    - check the wheel file exists and that it has the proper timestamp
2. if it does not exist, use Anaconda Powershell Prompt to follow the Windows specific steps to [manually build the wheel](../pygraph/azure_processing/pygraph_utils/README.md#building-the-wheel)
3. copy the wheel file to the build artifacts folder
```shell script
cp solutions/projectstaffing/pygraph/azure_processing/pygraph_utils/dist/pygraph_utils-*.whl solutions/projectstaffing/target/output/gdc/arm/scripts/artifacts
```

After this is complete, go to `target/output/gdc` and rename the `arm` folder into `gdc-<release_version>` (e.g. gdc-x.y.z).  
Compress this folder into a zip archive (e.g. gdc-x.y.z.zip).  
If desired, upload the archive to the project deliverables AZBS storage account, to make it available for download and deployment


## Building the project using Azure DevOps pipelines
Please see the [build pipeline documentation](../docs/build_pipeline.md) for details



## Deployment
### Performing deployment on a new environment
This creates all the required infrastructure ("static") components and deploys the "dynamic" components mentioned above on top of them.   
The deployment steps are described in the deployment script [readme file](../deployment/arm/README.md)
and in the [deployment overview ](../docs/ProjectStaffingDeploymentOverview.MD)

### Updating app release version over existing deployment
Depending on what changed form one release to another, some or all of the steps below need to be performed:
- update ADB libraries (jars, python scripts and python utils wheel)
    - detailed deployment steps provided in the [Deploying individual components](#deploying-jars-python-scripts-and-python-utils-wheel-to-azure-databricks-cluster) section
    - the jars current are: cleanup-jgraph.jar, latest-reply-extractor.jar, airtable-to-hr-data.jar, hr-data-to-azure-sql.jar, m365-user-to-azure-sql.jar, replace-current-search-index.jar, update-azure-sql-data-version.jar
    - the python scripts are: create_azure_search_index.py, mail_enrichment_processor.py, mail_role_detection_taxo.py, profiles_enrichment_processor.py
    - the python utils wheel is currently called pygraph_utils-0.1.7-py3-none-any.whl
    - changes to DB schema, Azure Search indexes schema make deploying impacted libraries mandatory
- update ADF entities (linked services, datasets, global parameters, pipelines, triggers etc)
    - detailed deployment steps provided in the [Deploying individual components](#deploying-azure-datafactory-entities) section
    - changes to DB schema, Azure Search indexes schema or to simulated data can impact the ADF deployment steps
- update the App Service
    - detailed deployment steps provided in the [Deploying individual components](#deploying-the-projectstaffing-docker-image) section
    - changes to DB schema (and some changes to Azure Search indexes schema) make deploying the application mandatory

### Deploying individual components
#### Deploying Azure DataFactory entities
Perform the next steps if the existing data is to be overwritten (e.g. for deployments using older versions of simulated data)
1. Stop ADF triggers
2. Delete ADF triggers (mainly delete triggers that are window based and which would not run again automatically after deployment)
3. Stop ongoing trigger runs and running pipelines (use "Cancel Recursive" where appropriate)
4. Run cleanup pipeline End2EndCleanup

Updating Azure DataFactory entities:
- if a new version of simulated data was released, replace it in the "demodata<deploymentHash>" storage account, in the "simulated-data" container
- update ADF linked services, if required
- update ADF global parameters, if required
- update ADF datasets, if required
    - if there are changes made to the emails or employees Az Search indices then
        - the schemas for EnrichedEmployeesForIndexing and EnrichedMailsForIndexing datasets have to be updated
        - the update has to be included in the pipelines ARM template update file described in the next step
- update ADF pipelines (two possible approaches: delete&recreate vs manual update)
    - delete&recreate (recommended approach):
        - this approach relies on a creating a json ARM template file containing all the ADF entities which need to be recreated
            - this can be obtained by copying pipelines.json and deleting all irrelevant entities from it (e.g. linked services), as well as all dependencies
            - it should be named "ADF_update_<old_version>_to_<new_version>.json"
        - on environments using production data, preserving the existing data from AzureSearch derived from emails might be desirable (e.g. if years worth of data were already processed)
            - in this case the deployment process might need to be customized so that as little as possible of the ADF entities are recreated and rerun
            - if non-backward-compatible changes are done to the pipelines and AzureSearch emails index schema, then deleting the existing data is inevitable
        - depending on the ingestion mode used by the target environment, the migration file will need to contain the following:
            - if data is to be completely rewritten (e.g. when new simulated data version needs to be deployed, or in production mode when non-backward-compatible schema changes need to be deployed)
              => delete and include in migration file both pipelines and triggers (and datasets, if required). The recreated triggers ensure the data is reprocessed
            - if the current data is to be preserved (e.g. in production mode, when schema is not changed or backward compatible schema changes are done, such as new columns being added which do not need to be populated for existing data)
              => delete and include in migration file only pipelines (and datasets, if required), without triggers. If required, relevant pipelines can be run manually after deployment.
            - if there are changes made to the emails or employees Az Search indices then the mapping for exportEmployeeToAzureSearch and exportEmailsToAzureSearch activities will have to be updated accordingly
    - manual update: requires the admin to fully understand the changes done between different versions and take the necessary steps to perform the migration directly from the ADF UI
- if the source data is to be recomputed from scratch, recreate ADF triggers based on current time (the most recent 6AM UTC date that is in the past must be computed and used for start time/end time/window computation for all triggers)
    - since the window computation is more tedious, importing the pipelines.json (properly parameterized) might be the better solution
- start triggers that are relevant for the current ingestion mode (e.g. for simulated mode only start all backfill triggers, except "emails_pipeline_backfill_further_past_trigger")
- wait for triggers to start pipelines and process new data

> NOTE: Updating ADF on environments where git configuration is activated:  
> In order to be able to update ADF using `ARM Template -> Import ARM Template`, you have to first disconnect ADF from git. Execute the update and then reconnect ADF to git.  
> Sometimes, importing the template from the ADF UI can fail, so, alternatively, the import can be done from the Azure Bash Cloud Shell using the command  
> `az deployment group create --resource-group <adf_resource_group> --template-file <arm_template_file_containing_relevant_changes>.json`  
> The template file must first be uploaded to the Cloud Shell using the "Upload/Download files" button.

> NOTE: When updating pipelines/datasets/triggers in ADF using `ARM Template -> Import ARM Template`, remove all linked services dependencies ("dependsOn": [...]) from pipelines/datasets/triggers in order to avoid defining them in the update json file.  
> The dependencies are only needed when deploying on a new ADF where the order in which linked service and pipelines/datasets are created counts.

#### Deploying the ProjectStaffing docker image
- stop jGraph application from the App Service Overview page
- apply required DB migrations (DB schema or stored procedure changes made in the code, but not yet present on the target environment)
    - this only needs to be explicitly done manually if the target env does not have flyway enabled
- deploy latest jGraph docker image into AppServices
    - this can be done in several ways:
        - from the App Service UI -> Deployment Center, using Azure Container Registry
            - for this to work, the App Service and the Container Registry must be in the same subscription
        - from the Azure CLI using the following command  
            `az webapp config container set --name <app-service-name> --resource-group <existing_gdc_resource_group_name> --docker-custom-image-name contosohub.azurecr.io/microsoft-gdc/project-staffing:<docker_image_tag> --docker-registry-server-url https://contosohub.azurecr.io  -u prj-staffing-reader -p <password>`
- update required AppService env variables (Application Settings, Connection Settings) from the Configuration page 
- start the application

> NOTE: The database update scripts don't have to be run manually on environments where flyway is enabled.  
> Flyway does this automatically when the application starts.  
> If the update scripts are run manually, then the application will fail at start-up because the flyway checksums won't match.

#### Deploying jars, python scripts and python utils wheel to Azure Databricks cluster
These libraries need to be deployed on the ADB cluster so that they can be run as spark jobs or used as utility code by such jobs.
In production, these spark jobs in turn, are only run by ADF pipelines.  
Any such library can be deployed separately (e.g. on a development environment) to check that the latest changes work as expected.  
The libraries can be deployed either from a project artifacts zip or from the local development environment.  
Since deploying individual components (as opposed to deploying whole new project build) is done on development environments
to quickly ensure a fix or feature works as expected before creating a new official build of the entire project, most often
individual components will be deployed from the local environment. However, there might be cases when deploying from the
project artifacts zip (downloaded from the project deliverables AZBS storage account) would make sense, therefore we are
also going to briefly describe this scenario.

**Deploying from the local environment**
1. open a terminal
2. change the current directory to the location of the library to upload
3. deploying the python utils wheel or jars (unlike python scripts) requires uninstalling the former artifact(s) from the ADB cluster first
    - option 1: from the ADB UI, go to _Clusters_ -> target cluster -> _Libraries_, select the libraries to update, hit _Uninstall_ and restart the cluster
    - option 2: from ADB CLI, by building the `uninstall` command specific to each type of artifact using the instructions from [Libraries CLI](https://docs.microsoft.com/en-us/azure/databricks/dev-tools/cli/libraries-cli)
4. make sure you have the [Databricks CLI](https://docs.microsoft.com/en-us/azure/databricks/dev-tools/cli/) installed and
   configured to point to the desired ADB cluster
    - connecting to several ADB clusters can be achieved by defining [Connection Profiles](https://docs.microsoft.com/en-us/azure/databricks/dev-tools/cli/#--connection-profiles)
    - to check the currently defined profiles, as well as the default one, run `cat ~/.databrickscfg`
5. upload the desired artifact using the following command `dbfs cp --overwrite --profile <profile> ./<artifact_to_upload> dbfs:/mnt/gdc-artifacts/`
6. restart the ADB cluster
7. install the new libraries (python utils wheel or jars) on the ADB cluster
    - this step is explicitly required only if you are going to check that the new library works by directly running the ADB spark which it impacts.
      If you are going to simply run the ADF pipeline which depends on the library (more specifically, the pipeline which runs the ADB job
      that makes use of the library), then this step is not required, as the Databricks actions of the ADF pipelines will trigger
      the library installation themselves (as long as they have the library mentioned in the "Append libraries" Settings section).
8. now you can run the Spark job or ADF pipeline which is meant to make use of the new library

**Deploying from the project artifacts zip**  
This approach might be well suited when having a slow internet connection locally or when you are in a different geographical
region than the target environment, and wanting to deploy larger artifacts (e.g. large shaded jars).  
If the resource you want to deploy was already built by the CI pipeline and uploaded to AZBS to the same region as the
target environment, then it might be faster than deploying it from local env.
- open Azure Bash Cloud Shell from Azure portal in your target environment
- download the project artifacts zip from the AZBS location where the CI pipeline uploaded it, using `wget <zip_url>`
- unzip it
- `cd ./scripts/artifacts`
- continue the same as from step 3 of the local deployment steps

