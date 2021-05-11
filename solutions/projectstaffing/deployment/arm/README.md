### Pre requirements

- log into your Azure portal and start the Cloud Shell terminal in Bash mode
- verify your python version by running the `python --version` command
- ensure your python version is at least 3.7+ or higher
- ensure your Azure CLI is already installed and the version is at least `2.16.0` (run `az version` command) 
- Azure portal user must have an Owner role in the Azure Subscription where deployment is performed 

- optionally, if Windows Authentication is to be enabled for application services to connect to the deployed Azure SQL instance, 
   Global Administrator role and multiple manual steps have to be performed during the deployment. We recommend SQL Authentication instead.
     

### Install 

1. Build artifacts  (required for deployment), by following [these steps]( ../README.MD#Building-the-artifacts-zip )
   
2. Upload prebuilt package (e.g. gdc-x.y.z.zip ) onto Azure CloudShell storage and unzip into your working dir.
   It will contain install.sh which is an entrypoint of deployment script. 

3. Log into your account using Azure CLI 

    Note: make sure you've logged OUT from all other Azure accounts (if any) before login. 

            az login
  
4. Run deployment script from the working dir 
    > You may change the deployment name and the region in the command below. 
    The deployment name is used for naming the Azure resource group.
    The name of the App Service will be prompted for during the installation.
    Ensure you have **at least 12 Azure DSv2 Family vCPU** in the location/subscription you deploy.
    
    > Deployment script is interactive and will prompt all necessary parameters.
    Most of them have default value which is good enough to go with. Please read prompted instructions thoughtfully. 
    
            ./install.sh --deployment-name gdctest --location westus --docker-password <docker-registry-password> --debug
    
    > --debug - option will instruct to output all AzureCLI command the script executes during deployment process. 
    > **Note:** Azure CloudShell has default idle timeout of 20 minutes, 
          so please monitor the installation progress to avoid the script being interrupted by Cloud Shell.
          Otherwise, you'll have to delete a resource group which matches a chosen deployment name, 
          delete internal state of deployment at **~/.gdc**  and **start all over**!
         
  Deployment script stores its internal state (finished stages, prompted values, auto-generated secrets, etc.) in ~/.gdc folder.
  It helps restart script and try to fast-forward subsequent installation attempts or automated upgrades.
 
    
    
After the installation completes, a simulated dataset is provisioned in the installed application.  
This process takes 30-45 minutes on the default installed configuration, and can be monitored in the Azure DataFactory UI.  
The application UI can be accessed at `https://<appServiceName>.azurewebsites.net`


