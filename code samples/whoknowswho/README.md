---
page_type: sample
products:
- office-365
- ms-graph
languages:
- csharp
extensions:
  contentType: samples
  technologies:
  - Microsoft Graph
  services:
  - Office 365
  - Microsoft Graph data connect
  createdDate: 2/6/2018 9:50:48 AM
---
# Microsoft Graph data connect
#### Using Office 365 data with Azure analytics to build intelligent applications 

## Introduction 

Microsoft Graph data connect brings Office 365 data and Azure resources to independent software vendors (ISVs). This system enables ISVs to build intelligent applications with Microsoft’s most valuable data and best development tools. Office 365 customers will gain innovative or industry-specific applications that enhance their productivity while keeping full control over their data. For more information, see [Overview](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki)

## Table of Contents
1. [Run ADF pipelines to copy Office 365 data](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Run-Azure-Data-Factory-pipelines-to-copy-Office-365-Data)
    * [Prerequisites](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Run-Azure-Data-Factory-pipelines-to-copy-Office-365-Data#prerequisites)
    * [Linked Service Properties](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Run-Azure-Data-Factory-pipelines-to-copy-Office-365-Data#linked-service-properties)
    * [Data Set Properties](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Run-Azure-Data-Factory-pipelines-to-copy-Office-365-Data#dataset-properties)
    * [Samples](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Run-Azure-Data-Factory-pipelines-to-copy-Office-365-Data#samples)
2. [Publish an Azure Managed Application to copy Office 365 data](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Publish-an-Azure-Managed-Application-to-copy-Office-365-data)
    * [Create a managed application](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Publish-an-Azure-Managed-Application-to-copy-Office-365-data#step-2-create-a-managed-application)
    * [Publish a managed application](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Publish-an-Azure-Managed-Application-to-copy-Office-365-data#step-3-publish-a-managed-application)
3. [Capabilities](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities)
    * [ADF Sinks](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#adf-sinks)
    * [Compliance Policies](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#policies)
    * [Data Regions](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#data-regions)
    * [Datasets](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#datasets)
    * [Filters](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#filters)
    * [User Selection](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Capabilities#user-selection)

## Deployment

| Environment | Description | Deploy |
|---|---|---|
|adlsgen2sink-sample|N/A|<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FOfficeDev%2FMS-Graph-Data-Connect%2Fmaster%2FARMTemplates%2Fadlsgen2sink-sample%2Fazuredeploy.json"><img src="http://azuredeploy.net/deploybutton.png" alt="Deploy Environment in Azure" /></a>|
|basic-sample|N/A|<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FOfficeDev%2FMS-Graph-Data-Connect%2Fmaster%2FARMTemplates%2Fbasic-sample%2Fazuredeploy.json"><img src="http://azuredeploy.net/deploybutton.png" alt="Deploy Environment in Azure" /></a>|
|blobaadauthsink-sample|N/A|<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FOfficeDev%2FMS-Graph-Data-Connect%2Fmaster%2FARMTemplates%2Fblobaadauthsink-sample%2Fazuredeploy.json"><img src="http://azuredeploy.net/deploybutton.png" alt="Deploy Environment in Azure" /></a>|
|incrementaldataload-sqlwatermark-sample|N/A|<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FOfficeDev%2FMS-Graph-Data-Connect%2Fmaster%2FARMTemplates%2Fincrementaldataload-sqlwatermark-sample%2Fazuredeploy.json"><img src="http://azuredeploy.net/deploybutton.png" alt="Deploy Environment in Azure" /></a>|
|incrementaldataload-tumblingwindow-sample|N/A|<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FOfficeDev%2FMS-Graph-Data-Connect%2Fmaster%2FARMTemplates%2Fincrementaldataload-tumblingwindow-sample%2Fazuredeploy.json"><img src="http://azuredeploy.net/deploybutton.png" alt="Deploy Environment in Azure" /></a>|
|sql-sample|N/A|<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FOfficeDev%2FMS-Graph-Data-Connect%2Fmaster%2FARMTemplates%2Fsql-sample%2Fazuredeploy.json"><img src="http://azuredeploy.net/deploybutton.png" alt="Deploy Environment in Azure" /></a>|

## Quick Links
* [Approve a data access request](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Approving-a-data-access-request)
* [Manage ADF piplines](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Azure-Data-Factory-Quick-Links)

## Help
* [FAQ](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/FAQ)  
* [Troubleshooting](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Troubleshooting)
    * [Error Handling](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Troubleshooting#errors)
* [Contact us](https://github.com/OfficeDev/MS-Graph-Data-Connect/wiki/Contact-Us)


## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.microsoft.com.

When you submit a pull request, a CLA-bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., label, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
