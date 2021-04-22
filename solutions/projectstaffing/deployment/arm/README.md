### Pre requirements

    * log into your Azure portal and start the Cloud Shell terminal in Bash mode
    * verify your python version by running the `python --version` command
    * ensure your python version is at least 3.7+ or higher
    * ensure your Azure CLI is already installed and the version is at least `2.16.0` (run `az version` command) 
    * Azure portal user must have an Owner role in the Azure Subscription where deployment is performed 
    
    * optionally, if Windows Authentication is to be enabled for application services to connect to the deployed Azure SQL instance, 
       Global Administrator role and multiple manual steps have to be performed during the deployment. We recommend SQL Authentication instead.
     

### Install 

1. Download prebuilt package

        mkdir gdc
        cd gdc
        wget https://bpartifactstorage.blob.core.windows.net/gdc-artifacts/builds/gdc-1.4.1.zip
        unzip gdc-1.4.1.zip
        rm gdc-1.4.1.zip
           
2. Log into your account using Azure CLI 

    Note: make sure you've logged OUT from all other Azure accounts (if any) before login. 
        
        az login
  
3. Run deployment script from the created gdc folder

      > Note: you may change the deployment name and the region in the command below. 
      The deployment name is used for naming the Azure resource group.
      The name of the App Service will be prompted for during the installation.
      Ensure you have at least 12 Azure Databricks cores in the location/subscription you deploy.
   
    
      ./install.sh --deployment-name gdctest --location westus --docker-password <docker-registry-password> --debug


> Note: Azure CloudShell has default idle timeout of 20 minutes, 
      so please monitor the installation progress to avoid the script being interrupted by Cloud Shell.
      Otherwise, you'll have to delete the created resource group and start all over!
    
    
After the installation completes, a simulated dataset is provisioned in the installed application.  
This process takes 30-45 minutes on the default installed configuration, and can be monitored in the Azure DataFactory UI.  
The application UI can be accessed at `https://<appServiceName>.azurewebsites.net`

