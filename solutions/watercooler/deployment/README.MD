# Build and Deployment Process

## Table of contents
- [Project components](#project-components)
- [Building the libraries from local source](#building-the-libraries-from-local-source)  
- [Building the libraries and the docker image](#building-the-libraries-and-the-docker-image)    
  - [Building the deployment artifacts zip](#building-the-deployment-artifacts-zip)
  - [Running installation for the new created build](#running-installation-for-the-new-created-build)   

- [Building the project using Azure DevOps pipelines](#building-the-project-using-azure-devops-pipelines)
- [Updates and deployment of individual components](#updates-and-deployment-of-individual-components)
      1. [Updating app releases version over existing deployment](#updating-app-release-version-over-existing-deployment)
      2. [Deploying individual components](#deploying-individual-components)
          a. [Deploying Azure DataFactory entities](#deploying-azure-datafactory-entities)
          b. [Deploying the Watercooler docker image](#deploying-the-watercooler-docker-image)
          c. [Deploying jars, python scripts and python scripts to Azure Databricks cluster](#deploying-jars-python-scripts-and-python-scripts-to-azure-databricks-cluster)

- [Test environment](#test-environment)   

## Project components

The watercooler components are:
- the AppService code (mainly scala and javascript code)
   - built into a docker image
- the ADF (Azure DataFactory) components (pipelines, datasets, linked services, triggers etc) which orchestrate the offline processing of data
   - ARM template in a json file named pipelines.json
- the ADB (Azure Databricks) Spark jobs (scala code) processing source data
   - built into jars
- common scala code used by both app service code and ADB jobs
   - maven module which gets shaded into the jars and into the docker image
- the ADB pyspark jobs processing source data
   - python scripts delivered as they are
- the docker image used for the Watercooler AppService
   - needs to be uploaded to a docker repository (Azure Container Registry is recommended) from where it can be accessed by the deployment script, and the App Service
- the project artifacts zip: containing the necessary jars,scripts and ARM templates


## Building the libraries from local source:
1. Install JDK 1.8
    - for example, to install the OpenJDK, follow the appropriate installation steps
      from https://openjdk.java.net/install/
      or https://github.com/AdoptOpenJDK/openjdk8-upstream-binaries/releases/tag/jdk8u292-b10 (take the latest zip build), or search online for a more detailed installation guide, matching your
      OS
        - for Oracle jdk more help can be found here: https://java.com/en/download/help/download_options.html
        - for Open jdk more help can be found here: https://openjdk.java.net/install/
    - required to build, develop and run the project's JVM based components (the AppService code and ADB scala spark
      jobs)
    - set $JAVA_HOME variable with the location of the JDK installation path (Observation: for windows set JAVA_HOME in the System Environment Variables; also add %JAVA_HOME%\bin to PATH)

2. Install latest Maven 3 version
    - download the latest maven 3 binary distribution archive from https://maven.apache.org/download.cgi
    - installation steps: https://maven.apache.org/install.html
    - required to build, develop and run the project's JVM based components (the AppService code and ADB scala spark
      jobs)

## Building the libraries and the docker image:

> NOTE: For developing the application, only macOS/Linux based systems were used.  
> Building the application is possible from Windows systems (using Powershell, not the command-line interpreter), however
> solid knowledge of your development environment and of the differences between Windows/Unix systems is required. Some
> adaptations of the provided instructions might be required as well, as well as some additional steps during deployment.


1. Build the necessary jars using maven:
```commandline
mvn clean install -DskipTests
```

2. Building the docker image 

    *Observation: this step is optional; if you don't have possibility to push to a given registry, please use the default provided version: 0.2.0*
    ```commandline
docker build -t blueprint.azurecr.io/bpcs/watercooler:x.y.z .
docker commit watercooler
docker push blueprint.azurecr.io/bpcs/watercooler:x.y.z
    ```

### Building the deployment artifacts zip
   - build via bin/prepare_artifacts.sh
   - needs to be uploaded to a "project deliverables AZBS storage account", to make it easily accessible to admins performing deployments
   - contains:
      - the jars, python scripts and python wheel files, defining Spark jobs run on ADB by ADF pipelines
      - the pipelines.json file, defining the ADF entities
      - the schema sql file, containing the database schema creation statements
      - all the scripts and ARM templates used to deploy the application on a new environment
    
    - navigate to `./bin` folder and execute: `commandline ./prepare_artifacts.sh --skip-maven`
        
    - the result of running the above command will be a tar.gz file that we recommend renaming it to `wc-x.y.z.tar.gz`:
        ```commandLIne
mv build.tar.gz wc-0.2.0.tar.gz
    ```
    - upload the `wc-x.y.z.tar.gz` build file to a specific storage account from where you can download it via wget

Observation: if you are on windows and you encounter issues running `./prepare_artifacts.sh --skip-maven` 
you can download git-scm from here and use git-bash to run the script.


### Running installation for the new created build:
In order to proceed with the installation in the Azure Cloud, please follow the instructions from here [./deployment/azure/README.MD](./deployment/azure/README.MD)




## Building the project using Azure DevOps pipelines
Please see the [build pipeline documentation](../docs/build_pipeline.md) for details

## Updates and deployment of individual components

### Updating app release version over existing deployment
Depending on what changed form one release to another, some or all of the steps below need to be performed:
- update ADB libraries (jars, python scripts and python utils wheel)
   - detailed deployment steps provided in the [Deploying individual components](#deploying-jars-python-scripts-and-python-scripts-to-azure-databricks-cluster) section
   - the jars current are: jwc-events-creator.jar, jwc-profiles-extractor.jar
   - the python scripts are: 000_cleanup.py,01_calendar_spark_processor,01_1_calendar_events_attendance.py,1_2_update_group_members_invitation_status.py,02_profiles_spark_processor.py, 03_persons_to_events_dill_assembler.py,04_generate_timetable_kmeans.py,05_export_to_csv.py, 06_spark_export_to_sql.py
   - changes to DB schema
- update ADF entities (linked services, datasets, global parameters, pipelines, triggers etc)
   - detailed deployment steps provided in the [Deploying individual components](#deploying-azure-datafactory-entities) section
   - changes to DB schema
- update the App Service
   - detailed deployment steps provided in the [Deploying individual components](#deploying-the-Watercooler-docker-image) section
   - changes to DB schema (and some changes to Azure Search indexes schema) make deploying the application mandatory

### Deploying individual components

#### Deploying Azure DataFactory entities
Perform the next steps if the existing data is to be overwritten (e.g. for deployments using older versions of simulated data)
1. Stop ADF triggers
2. Delete ADF triggers (mainly delete triggers that are window based and which would not run again automatically after deployment)
3. Stop ongoing trigger runs and running pipelines (use "Cancel Recursive" where appropriate)
4. Run cleanup pipeline End2EndCleanup

Updating Azure DataFactory entities:
- update ADF linked services, if required
- update ADF global parameters, if required
- update ADF datasets, if required
   - if there are changes made to the waterooler table schema
      - the update has to be included in the pipelines ARM template update file described in the next step
- update ADF pipelines (two possible approaches: delete&recreate vs manual update)
   - delete&recreate (recommended approach):
      - this approach relies on a creating a json ARM template file containing all the ADF entities which need to be recreated
         - this can be obtained by copying pipelines.json and deleting all irrelevant entities from it (e.g. linked services), as well as all dependencies
         - it should be named "ADF_update_<old_version>_to_<new_version>.json"
      - on environments using production data, preserving the existing data from Watercooler sql database
         - in this case the deployment process might need to be customized so that as little as possible of the ADF entities are recreated and rerun
         - if non-backward-compatible changes are done to the pipelines and sql schema, then deleting the existing data is inevitable
   - manual update: requires the admin to fully understand the changes done between different versions and take the necessary steps to perform the migration directly from the ADF UI
- recreate ADF triggers based on current time
- start triggers that are relevant for the watercooler event creation pipeline
- wait for triggers to start pipelines and process new data

> NOTE: Updating ADF on environments where git configuration is activated:  
> In order to be able to update ADF using `ARM Template -> Import ARM Template`, you have to first disconnect ADF from git. Execute the update and then reconnect ADF to git.  
> Sometimes, importing the template from the ADF UI can fail, so, alternatively, the import can be done from the Azure Bash Cloud Shell using the command  
> `az deployment group create --resource-group <adf_resource_group> --template-file <arm_template_file_containing_relevant_changes>.json`  
> The template file must first be uploaded to the Cloud Shell using the "Upload/Download files" button.

> NOTE: When updating pipelines/datasets/triggers in ADF using `ARM Template -> Import ARM Template`, remove all linked services dependencies ("dependsOn": [...]) from pipelines/datasets/triggers in order to avoid defining them in the update json file.  
> The dependencies are only needed when deploying on a new ADF where the order in which linked service and pipelines/datasets are created counts.

#### Deploying the Watercooler docker image
- stop Watercooler application from the App Service Overview page
- apply required DB migrations (DB schema or stored procedure changes made in the code, but not yet present on the target environment)
   - this only needs to be explicitly done manually if the target env does not have flyway enabled
- deploy latest watercooler docker image into AppServices
   - this can be done in several ways:
      - from the App Service UI -> Deployment Center, using Azure Container Registry
         - for this to work, the App Service and the Container Registry must be in the same subscription
      - from the Azure CLI using the following command  
        `az webapp config container set --name <app-service-name> --resource-group <existing_gdc_resource_group_name> --docker-custom-image-name blueprint.azurecr.io/bpcs/watercooler:<docker_image_tag> --docker-registry-server-url https://blueprint.azurecr.io  -u jwc-readonly-token -p <password>`
- update required AppService env variables (Application Settings, Connection Settings) from the Configuration page
- start the application

> NOTE: The database update scripts don't have to be run manually on environments where flyway is enabled.  
> Flyway does this automatically when the application starts.  
> If the update scripts are run manually, then the application will fail at start-up because the flyway checksums won't match.

#### Deploying jars, python scripts and python scripts to Azure Databricks cluster
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
3. make sure you have the [Databricks CLI](https://docs.microsoft.com/en-us/azure/databricks/dev-tools/cli/) installed and
   configured to point to the desired ADB cluster
   - connecting to several ADB clusters can be achieved by defining [Connection Profiles](https://docs.microsoft.com/en-us/azure/databricks/dev-tools/cli/#--connection-profiles)
   - to check the currently defined profiles, as well as the default one, run `cat ~/.databrickscfg`
4. upload the python scripts using the following command `dbfs cp --overwrite --profile <profile> ./<artifact_to_upload> dbfs:/mnt/watercooler/scripts/`
5. restart the ADB cluster
6. install the new libraries (python utils wheel or jars) on the ADB cluster
   - this step is explicitly required only if you are going to check that the new library works by directly running the ADB spark which it impacts.
     If you are going to simply run the ADF pipeline which depends on the library (more specifically, the pipeline which runs the ADB job
     that makes use of the library), then this step is not required
7. now you can run the Spark job or ADF pipeline which is meant to make use of the new python scripts

**Deploying from the project artifacts zip**  
This approach might be well suited when having a slow internet connection locally or when you are in a different geographical
region than the target environment, and wanting to deploy larger artifacts (e.g. large shaded jars).  
If the resource you want to deploy was already built by the CI pipeline and uploaded to AZBS to the same region as the
target environment, then it might be faster than deploying it from local env.
- open Azure Bash Cloud Shell from Azure portal in your target environment
- download the project artifacts zip from the AZBS location where the CI pipeline uploaded it, using `wget <wc-x.y.z.tar.gz url>`
- unzip it
- `cd wc`
- continue with the instructions from [./deployment/azure/README.MD](./deployment/azure/README.MD)


## Test environment
The `prepare_artifacts.sh` build script was tested on the following configurations:
    - Windows 10 version 20H2 build: 19042.1052
    - MacOS Catalina version 10.15.5 (19F101)
    - MacOS BigSur version 11.4 
   