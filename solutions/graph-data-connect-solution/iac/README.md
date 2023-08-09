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

---

# Azure Resources Deployment

The resources in this folder can be used to deploy the required cloud services into your Azure Subscription. This can be done either via the [Azure Portal](https://portal.azure.com) or by using the [Bicep](../iac/bicep/) file included in the iac folder.

Resources can also be deployed into your Azure Subscription by using one of these Deploy Azure links:  

[![Deploy to Azure](https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/1-CONTRIBUTION-GUIDE/images/deploytoazure.svg?sanitize=true)]()&emsp;&emsp;&emsp;
[![Deploy To Azure US Gov](https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/1-CONTRIBUTION-GUIDE/images/deploytoazuregov.svg?sanitize=true)]()

## Table of Contents

- [Prerequisites](#prerequisites)
- [Deployment Process](#deployment-process)

## Prerequisites

Before initiating the deployment of Azure resources, ensure the following prerequisites are met:

1. **Azure DevOps Project Setup**: Set up an Azure DevOps Project and grant Basic User Access to relevant team members. Detailed instructions can be found in the [Create a project in Azure DevOps](https://learn.microsoft.com/en-us/azure/devops/organizations/projects/create-project?view=azure-devops&tabs=browser) documentation.

2. **Azure Service Principal Configuration**: Create an Azure Service Principal with Contributor permissions for the target Azure Resource Group. Follow the steps outlined in the [Creating a service principal](https://learn.microsoft.com/en-us/azure/active-directory/develop/howto-create-service-principal-portal) guide.

3. **Azure DevOps Service Connection**: Establish an Azure DevOps Service Connection for seamless interaction. Learn how to set up service connections in [Service Connections in Azure Pipeline](https://learn.microsoft.com/en-us/azure/devops/pipelines/library/service-endpoints?view=azure-devops&tabs=yaml).

4. **Bicep Files**: Ensure your Bicep files are ready in your repository. The templates can be found in the provided repository ([link](../iac/bicep/)).

5. **Pipeline Configuration**: Configure Azure DevOps pipelines using the provided YAML files.

6. **Azure Subscription**: Verify that you have an active Azure subscription where you intend to deploy your resources. If not, you can create a new [Azure Subscription](https://www.googleadservices.com/pagead/aclk?sa=L&ai=DChcSEwi28aqOts-AAxWtT0gAHdSZDewYABAAGgJjZQ&gclid=CjwKCAjw8symBhAqEiwAaTA__GJh33-zB8OXc07tUnmmyqx03HPQLdrypvY6t1CQVqP76nEe1qwVAxoCBccQAvD_BwE&ei=tHHTZIbCNY6z4-EPuPKFmAk&ohost=www.google.com&cid=CAESbeD21vMGT2uzMZE8CCx3iWXNopV3Xb2bKl81YPvDPp7jRTxO-El5AagAN4lHb0GKcBfXBkthpaUSGMZpBl0nqZkgjodcDzBFWmjNHxxEzU4H9lty4W3n5eh0T5NnE_7B2lyj9NZwlTyJqBO3-YI&sig=AOD64_2EyPksexK4iu0h4EntTC1RAP480Q&q&sqi=2&adurl&ved=2ahUKEwiG7JiOts-AAxWO2TgGHTh5AZMQ0Qx6BAgNEAE).

7. **Azure Resource Group**: Create an Azure Resource Group in your subscription.

8. **Azure DevOps Variables**: Define Azure DevOps variables for your deployment parameters. More information can be found in the [Azure DevOps Variables](https://learn.microsoft.com/en-us/azure/devops/pipelines/process/variables?view=azure-devops&tabs=yaml%2Cbatch) documentation.

## Deployment Process Using Bicep

To deploy resources to your Azure Resource Group or Subscription:

### Step 1: Clone the Repository

Clone the repository to your Azure DevOps workspace.

### Step 2: Review Bicep File

1. Navigate to the `iac/bicep` directory in your cloned repository.
2. Open the `main.bicep` file using a text editor or an IDE.
3. Review the [Bicep](../iac/bicep/) code to understand the Azure resources you are going to deploy. Make any necessary modifications or customizations.

### Step 3: Create an Azure DevOps Pipeline

1. Log in to your Azure DevOps account.
2. Create a new project or use an existing one.
3. Go to the "Pipelines" section and click on "New Pipeline."
4. Choose the source repository (the one you cloned earlier) and configure your pipeline settings.

### Step 4: Configure Pipeline Variables

1. Access the Azure Pipelines Library.
2. Modify Variable Values in the variable group based on deployment requirements.

### Step 5: Save and Run the Pipeline

1. Save your pipeline configuration.
2. Trigger the pipeline execution to deploy Azure Resources.

### Step 6: Verify Deployment

1. Once the pipeline completes successfully, log in to the Azure portal.
2. Navigate to the appropriate resource group and verify that the resources defined in the Bicep file have been deployed.

By following these steps, you can efficiently deploy Azure resources using Azure DevOps, ensuring a smooth provisioning process and clear monitoring of deployments.


