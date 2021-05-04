# Overview: Microsoft Graph Data Connect - Developers

*Using Office 365 data with Azure Analytics to build intelligent applications*

## Introduction

Microsoft Graph data connect brings Office 365 data and Azure resources to developers through a core data pipeline. This enables them to easily build intelligent applications with Microsoft's most valuable data and best development tools, all within the Microsoft secure cloud.

Productivity is the one topic that all businesses are interested in improving. With more than 120M active commercial users of Office 365 and $250B spent per year on business applications, there is tremendous opportunity building and selling products to make knowledge workers more productive.

### Value

Office 365 contains the richest data in the world about workers and their workplace. It contains information on how people work, how they communicate, collaborate and manage their time. Bringing this data to Azure means that developers have many of the world's best development and hosting tools to work with this data. This enables customers to gain innovative or industry-specific applications that enhance their productivity while keeping full control over their Office 365 data. Microsoft is bringing along the secured control that customers expect and solving the liability of developers holding on to the data.

### Office 365 data

Microsoft Graph data connect offers curated datasets from Office 365 entities accessed through Outlook, Teams and OneDrive. The initial set of data types include message content, meeting content, contact information and user data. The set of data types will continue to grow as development continues. The developer will be able to discover the variety of data types and datasets that are available during application development. Developers and end-customers' applications will have access to data incrementally and historically (13 months prior).

## Application development experience

### Defining the data pipeline

During construction of the data pipeline in Azure Data Factory (ADF), each application will have to define their own custom pipeline definitions, which allows them to specify each of the following:

- Office 365 Dataset
- Date Range: Filter against the sent time property
- Data Freshness Requirements: Schedule data updates

### Data pipeline construction

There is a simple, easy-to-use model in ADF via both Azure Resource Manager (ARM) templates and the UX, which allows a developer to easily express the Office 365 dataset they're interested in and have that data show up in the Azure Data Store of their choice. Once a connection has been made to the Office 365 data store, developers will be able to select tables and apply simple queries. This pipeline will allow curated data to be stored in the Azure Data Lake Stor Generation 1 or 2 and Azure Storage Blob. Developers can also move data from those stores into a different store (such as Azure SQL DB) using ADF. Once stored, developers will have access to the many capabilities of Azure Services to develop and integrate their application.

### Managed Application

The developer must specify in the package templates what standing access they will have to the application. There are two options: the "governed" application where the data and application are deployed into the customer's subscription and neither the developer nor the customer has standing access to the application, and the "ungoverned" application where the developer has full access to the application. When publishing the application, the developer must indicate whether the application sends any data outside of the customer's subscription. The developer also specifies the rest of the Managed Application package, including the ARM templates, SKU options, and price to charge.

### Sample application: Who Knows Who

Microsoft has built a sample application called Who Knows Who (WKW) which uses the tenant's own social network to find connections to people and businesses, similar to what LinkedIn offers individuals. WKW provides a working example of the pipeline workflow. The application provides a useful set of scripts that partners can run within their tenant to create a set of users and email interaction to populate a Who Knows Who Graph. By enabling this pipeline on an Office 365 test tenant, the partner can get started with our sample data and use variations of our scripts or the actual Office clients to create datasets required for their unique application needs.

## Customer Marketplace Experience

### Marketplace

Microsoft Graph data connect leverages the existing integration of Managed Applications with the [AppSource marketplace](https://appsource.microsoft.com). Microsoft AppSource helps enterprise users find and gauge line-of-business (LoB) and software-as-a-service (SaaS) apps from Microsoft and its partners. AppSource apps are built on top of a variety of Microsoft products and includes all their existing apps add-ins and content packages. All applications leveraging Microsoft Graph data connect resources can be purchased in the AppSource marketplace.

### Application Installation

A customer purchaser that has resource creation rights on an Azure subscription, such as a mid-level manager or IT department, installs applications through the AppSource marketplace. They will see the terms of service, the data that is required, price for each application SKU and approximate cost for resource consumption.

Once the properties are specified by the purchaser the resources are provisioned. This includes the Data Factory pipeline that begins extracting data. The purchaser is given a ReadMe from the developer author about when to expect the installation to be complete, how to use the application, and how to get support.
