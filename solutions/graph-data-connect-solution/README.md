# Microsoft Data Graph Connect Solution Accelerator

## Contents

1. [Solution Overview](#solution-overview)
2. [Solution Architecture](#solution-architecture)
   - [Azure Services Used](#azure-services-used)
3. [Directory Structure](#directory-structure)
4. [Getting Started](#getting-started)
   - [Pre-requisites](#pre-requisites)
   - [Infrastructure Deployment](#infrastructure-deployment)
   - [Synapse Pipeline](#synapse-pipeline)
   - [PBI Report Report](#pbi-report-report)
5. [Feedback & Considerations](#feedback-considerations)

## Solution Overview
This repository houses the Microsoft Data Graph Connect Solution Accelerator, which delivers a range of advantages, notably heightened insights encompassing email analytics, account sentiment analysis, and more. These insights are derived from the synergistic amalgamation of Salesforce Sales data (Opportunity) and Microsoft 365 data (Email).

This solution encompasses essential components, including Azure resource deployment, Syanapse data pipelines, and a Power BI dashboard. These elements collectively empower organizations to seamlessly integrate and construct within their individual tenant environments.

## Solution Architecture

Upon completion of all steps, you will have a comprehensive end-to-end solution with the following architecture:

![Architecture](docs/media/Architecture.PNG)

### Azure Services Used

The solution leverages the following core Azure components:

- **Azure Synapse**: This analytics service caters to data warehouses and big data systems, centralizing data in the cloud for easy access. It offers a range of pipelines and activities, such as Data Flow and Custom activities, to connect to source data and copy it into Data Lake Storage.
- **Azure Datalake Storage**: A scalable data lake designed for high-performance analytics workloads. It stores input data and contextualized data in Delta tables within this solution.
- **Azure SQL Server and Database**: This component stores metadata used to extract data from Microsoft 365 to an ADLS (Azure Data Lake Storage) for additional processing.
- **Managed Identity**: Enables Azure resources to authenticate with cloud services.
- **Service Principal**: An Azure Active Directory application serving as the security principal for executing the data extraction process. It is responsible for creating, running, and approving data pipelines in Synapse for data extraction from Microsoft 365 to an ADLS.
- **Azure DevOps**: This component houses the source code, Infrastructure templates, and deployment pipeline files. Azure resources are deployed to an Azure resource group using deployment pipeline files.
- **Azure Key Vault**: A secure repository for storing secrets and keys crucial to the solution's operation.

## Directory Structure

Explore the following directories to gain insights into solution components within the solution framework:

- **[iac](iac)**: This directory contains Bicep files required for deploying the infrastructure. 

- **[powerbi](powerbi)**: Here, you will find scripts pertinent to the solution. 

- **[synapse](synapse)**: This directory encompasses Synapse files essential for the solution. 

## Getting Started

To begin, clone or download this repository onto your local machine and meticulously follow the instructions detailed in each of the README files.

### Pre-requisites

Before embarking on the setup process, ensure the following pre-requisites are met:

- Microsoft 365 Data Connection: Establish a connection to your Microsoft 365 data for seamless integration. For detailed instructions, refer to [Microsoft 365 Data Connection Setup](https://learn.microsoft.com/en-us/viva/solutions/data-lakes/microsoft-graph-data-connect).

- Salesforce Data Connection: Establish a connection to your Salesforce Sales data (Opportunity) for integration. Refer to [Salesforce Data Connection Setup](https://learn.microsoft.com/en-us/azure/data-factory/connector-salesforce?tabs=data-factory).

- Azure Subscription: Ensure you have an active Azure subscription that is in the same tenant as your Microsoft 365 tenant. Both should be part of the same Azure AD tenancy.

- Service Principal: Create an Azure Active Directory (Azure AD) Service Principal to securely access Microsoft 365 and Salesforce Data. For guidance, please refer to [Service Principal Setup Guide](https://learn.microsoft.com/en-us/azure/active-directory/develop/howto-create-service-principal-portal#register-an-application-with-azure-ad-and-create-a-service-principal).

### Infrastructure Deployment

For comprehensive instructions on infrastructure setup and usage, please consult the documentation [here](iac/README.md).

### Synapse Pipeline

For setup instructions and usage guidelines, please refer to the documentation [here](synapse/README.md).

### Power BI Report

Discover setup instructions and utilization guidance in the documentation [here](powerbi/README.md). Also, download the pre-created Power BI security report, designed to generate insights from data produced by the Synapse pipeline in Azure storage locations.

Link to download Power BI template: [SalesSentimentDashboard.pbit](powerbi/SalesSentimentDashboard.pbit)

## Feedback & Considerations

We wholeheartedly welcome your feedback as it contributes to the refinement of our solution.

Kindly note the following considerations:

- Regular updates may be performed to accommodate adjustments and fixes.
- Network graph visualizations in the Power BI template are limited to 1500 nodes.

For any inquiries or assistance, please feel free to contact us at [example@example.com](mailto:example@example.com).
