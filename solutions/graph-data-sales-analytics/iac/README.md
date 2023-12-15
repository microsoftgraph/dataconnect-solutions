# IAC Directory Structure

## Directories and Files

- **[arm](arm)**: Contains Bicep and JSON scripts for various Azure resources.

- **[iac](../Graph-Data-Connect-Solution/iac/)**: Holds deployment templates and pipeline files for the resources.

    - **[bicep](../iac/bicep/)**: Contains templates for each resource, including the main deployment file "main.bicep".

        - **[modules](../iac/bicep/modules/)**: Contains individual Bicep files for each resource, organized in separate subfolders.

        - **[main.bicep](../iac/bicep/main.bicep)**: Encompasses deployment code for all resources present in the "modules" directory.

        - **[main.parameters.json](../iac/bicep/main.parameters.json)**: Holds necessary parameters to execute "main.bicep". These parameters are retrieved from variable groups to avoid hardcoding.

    - **[pipelines](../iac/pipelines/)**: Inside the "pipelines" directory, you'll find YAML files for deployment pipelines.

        - **[azure_deploy.yml](.../iac/pipelines/azure_deploy.yml)**: Outlines steps to execute "main.bicep". It specifies branch triggers, the variable group supplying values for "main.parameters.json", and deployment stages for different environments.

# Azure Resources Deployment

The resources in this folder can be used to deploy the required cloud services into your Azure Subscription. You have two deployment options:

## Option 1: Deploy from Azure Portal

To deploy directly to Azure, click the following button:

   <a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fmicrosoftgraph%2Fdataconnect-solutions%2Fmain%2Fsolutions%2Fgraph-data-sales-analytics%2Fiac%2Farm%2Fazure_deploy.json"><img src="https://camo.githubusercontent.com/bad3d579584bd4996af60a96735a0fdcb9f402933c139cc6c4c4a4577576411f/68747470733a2f2f616b612e6d732f6465706c6f79746f617a757265627574746f6e" alt="Deploy Environment in Azure" /></a>

## Option 2: Use Azure DevOps Pipeline

### Prerequisites
To deploy Azure resources using Azure DevOps, ensure the following prerequisites are met:

1. **Azure DevOps Project Setup**: Set up an Azure DevOps Project and grant Basic User Access to relevant team members. Detailed instructions can be found in the [Create a project in Azure DevOps](https://learn.microsoft.com/en-us/azure/devops/organizations/projects/create-project?view=azure-devops&tabs=browser) documentation.

2. **Azure Service Principal Configuration**: Create an Azure Service Principal with Contributor permissions for the target Azure Resource Group. Follow the steps outlined in the [Creating a service principal](https://learn.microsoft.com/en-us/azure/active-directory/develop/howto-create-service-principal-portal) guide.

3. **Azure DevOps Service Connection**: Establish an Azure DevOps Service Connection for seamless interaction. Learn how to set up service connections in [Service Connections in Azure Pipeline](https://learn.microsoft.com/en-us/azure/devops/pipelines/library/service-endpoints?view=azure-devops&tabs=yaml).

4. **Bicep Files**: Ensure your Bicep files are ready in your repository. The templates can be found in the provided repository ([link](../iac/bicep/)).

5. **Pipeline Configuration**: Configure Azure DevOps pipelines using the provided YAML files.

6. **Azure Subscription**: Verify that you have an active Azure subscription where you intend to deploy your resources. If not, you can create a new [Azure Subscription](https://azure.com/free).

7. **Azure Resource Group**: Create an Azure Resource Group in your subscription.

8. **Azure DevOps Variables**: Define Azure DevOps variables for your deployment parameters. More information can be found in the [Azure DevOps Variables](https://learn.microsoft.com/en-us/azure/devops/pipelines/process/variables?view=azure-devops&tabs=yaml%2Cbatch) documentation.

### Deployment Process

To deploy resources to your Azure Resource Group or Subscription using Azure DevOps, follow these steps:

#### Step 1: Clone the Repository

Clone the repository to your Azure DevOps workspace.

#### Step 2: Review Bicep File

1. Navigate to the `iac/bicep` directory in your cloned repository.
2. Open the `main.bicep` file using a text editor or an IDE.
3. Review the [Bicep](../iac/bicep/) code to understand the Azure resources you are going to deploy. Make any necessary modifications or customizations.

#### Step 3: Create an Azure DevOps Pipeline

1. Log in to your Azure DevOps account.
2. Create a new project or use an existing one.
3. Go to the "Pipelines" section and click on "New Pipeline."
4. Choose the source repository (the one you cloned earlier) and configure your pipeline settings.

#### Step 4: Configure Pipeline Variables

1. Access the Azure Pipelines Library.
2. Modify Variable Values in the variable group based on deployment requirements.

#### Step 5: Save and Run the Pipeline

1. Save your pipeline configuration.
2. Trigger the pipeline execution to deploy Azure Resources.

#### Step 6: Verify Deployment

1. Once the pipeline completes successfully, log in to the Azure portal.
2. Navigate to the appropriate resource group and verify that the resources defined in the Bicep file have been deployed.

By following these steps, you can efficiently deploy Azure resources using Azure DevOps, ensuring a smooth provisioning process and clear monitoring of deployments.

