#!/usr/bin/env bash
###### utils #########
function lex_hash() {
    # should be consistent with python skills_finder_utils/common.py#lex_hash
    echo "$1" | openssl md5 | sed 's/^.* //' | tr '[:upper:]' '[:lower:]' | cut -c1-7
}
set -e
WORKDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

if ! command -v az &> /dev/null
then
  echo "Install Azure cli   https://docs.microsoft.com/en-us/cli/azure/install-azure-cli-apt?view=azure-cli-latest "
  exit 1
fi

python_version=$(python3 --version  2>&1 | sed 's/.* \([0-9]\).\([0-9]\).*/\1\2/' )
if [[ $python_version < 37 ]]; then
    echo "This script requires Python 3.7+ "
    echo "Consider to use https://github.com/pyenv/pyenv#installation to install python without sudo access "
    exit 3
fi

unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)  ;;
    Darwin*)  ;;
    *) echo "Unsupported platform"; exit 4 ;;
esac
echo "Installing Azure CLI extension for Databricks and Data Factory..."

az extension add --only-show-errors --name  databricks
az extension add --only-show-errors --name  datafactory
# create virtual env
pip3 install virtualenv -q
echo "Installing python virtual environment for deployment scripts..."
virtualenv -p python3  ~/.gdc-env -q
source ~/.gdc-env/bin/activate
~/.gdc-env/bin/pip install -r $WORKDIR/scripts/requirements.txt -q

mkdir -p ~/.gdc

DEPLOYMENT_NAME=
LOCATION=
DEBUG=
DOCKER_PASSWORD=

while [[ "$#" -gt 0 ]]; do
    case $1 in
      -n | --deployment-name ) DEPLOYMENT_NAME="$2"; shift ;;
      -l | --location ) LOCATION="$2"; shift ;;
      -p | --docker-password ) DOCKER_PASSWORD="$2"; shift ;;
      -d | --debug ) DEBUG="--debug true"; ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done
if [[ -z "$DEPLOYMENT_NAME" ]]; then
  read -p "Enter deployment name: " DEPLOYMENT_NAME
fi

if [[ -z "$LOCATION" ]]; then
  read -p "Enter Azure location: " LOCATION
fi

if [[ -z "$DOCKER_PASSWORD" ]]; then
  read -p "Enter docker repository password: " DOCKER_PASSWORD
fi


RESOURCE_GROUP="${DEPLOYMENT_NAME}-resources"

echo -e "\n\n###################Authenticating to Azure #####################\n\n "
az login

#
# -------- Deployment ------------------------------
#
#
TENANT_ID=$(az account show --query tenantId -o tsv)
SUBSCRIPTION_ID=$(az account show --query id -o tsv)
if [[ $(az account list --output tsv | wc -l )  -gt  "1" ]]; then
    echo "Multiple subscription found"
    az account list --output table
    echo "--------------------------------------------------"
    echo "Current subscription: "
    az account list --output table | grep ${SUBSCRIPTION_ID}

    read -p "Would you like to deploy into this subscription ?(Y/n) " -n 1 -r
    echo    # move to a new line
    if [[ ! $REPLY =~ ^[Yy]$ ]]
    then
        echo "Use the following command to switch subscription:"
        echo "    az account set --subscription your_subscription_id "
        [[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
    fi
fi

REQUIRED_ROLE="Owner"
LOGGED_USER_ID=$(az ad signed-in-user show --query objectId  --output tsv )
ASSIGNMENTS_LIST=$(az role assignment list --scope /subscriptions/${SUBSCRIPTION_ID} --assignee ${LOGGED_USER_ID} --include-classic-administrators true --include-groups --include-inherited --role "${REQUIRED_ROLE}" --query [].{id:id} --output tsv)

if [[ -z "${ASSIGNMENTS_LIST}" ]]; then
    echo "You don't have enough permissions within selected subscription ${SUBSCRIPTION_ID}, required role: ${REQUIRED_ROLE}"
    read -p "Would you still like to try to deploy into this subscription ?(Y/n) " -n 1 -r
    echo    # move to a new line
    if [[ ! $REPLY =~ ^[Yy]$ ]]
    then
        [[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
    fi
fi

DEFAULT_VM_TYPE="standardDSv2Family"
MINIMAL_vCPU=12
vCPU_USED=$(az vm   list-usage   --location $LOCATION --subscription  ${SUBSCRIPTION_ID} -o tsv --query "[].{Name:name, currentValue:currentValue}[?contains(Name.value, '${DEFAULT_VM_TYPE}')]" | awk '{ print $1 }')
vCPU_LIMIT=$(az vm   list-usage   --location $LOCATION --subscription  ${SUBSCRIPTION_ID}  -o tsv --query "[].{Name:name, limit:limit}[?contains(Name.value, '${DEFAULT_VM_TYPE}' )]" | awk '{ print $1 }')
if (( ${vCPU_USED} + ${MINIMAL_vCPU} > ${vCPU_LIMIT} ));  then
    read -p "It's not enough vCPU available at ${LOCATION} region. You use ${vCPU_USED} out of ${vCPU_LIMIT} but ${MINIMAL_vCPU} is required. Would you like to still try to install ?(Y/n) " -n 1 -r
    echo    # move to a new line
    if [[ ! $REPLY =~ ^[Yy]$ ]]
    then
        echo "Canceling deployment due to lack of vCPU available"
        exit 5
    fi
fi

declare -a services=("Microsoft.Network" "Microsoft.OperationalInsights" "Microsoft.Databricks" "Microsoft.Sql" "Microsoft.Authorization" "Microsoft.Compute" )

## now loop through the service namespaces to make sure all are enabled
for service in "${services[@]}"
do
   SERVICE_STATE=$(az provider show --namespace $service --query registrationState -o tsv)
   if [[ "${SERVICE_STATE}" -ne "Registered" ]]; then
      echo "The subscription  ${SUBSCRIPTION_ID} is not registered to use $service. "
      while true; do
          read -p "Would you like to activate $service and continue installation (Y/n) " $enable_yn
          case $enable_yn in
              [Yy]* )
                echo "Registering $service for  subscription $SUBSCRIPTION_ID ..."
                az provider register  --subscription $SUBSCRIPTION_ID --namespace $service --wait
                REGISTER_RESULT = $?
                if [[ $REGISTER_RESULT != 0]]; then
                    echo "Failed to register $service, bailing out..."
                    exit $REGISTER_RESULT
                fi
                break;;
              [Nn]* ) echo "Installation has been terminated";  exit;;
              * ) echo "Please answer yes or no.";;
          esac
      done
  fi
done

LOG_INSIGHTS_REGISTRATION_STATE=$(az provider show --namespace microsoft.insights --query registrationState -o tsv)
LOG_INSIGHTS_PARAM="--log-analytic-enabled true"
if [[ "${LOG_INSIGHTS_REGISTRATION_STATE}" -ne "Registered" ]]; then
  echo "The subscription  ${SUBSCRIPTION_ID} is not registered to use microsoft.insights. "
  echo "Access to log will be limited for this deployment  "
  while true; do
      read -p "Do you to continue without Log Analytic Workspace  (Y/n) " log_yn
      case $log_yn in
          [Yy]* ) LOG_INSIGHTS_PARAM="--log-analytic-enabled false";  break;;
          [Nn]* ) echo "Installation has been terminated";  exit;;
          * ) echo "Please answer yes or no.";;
      esac
  done
fi

echo "Creating resource group  $RESOURCE_GROUP in $LOCATION"

TMP_AZURE_STORAGE_ACCOUNT="gdcdeploy$( lex_hash $DEPLOYMENT_NAME )"
CONTAINER="gdc-artifacts"
az group create --name ${RESOURCE_GROUP} --location "$LOCATION" --output none

echo "Creating temporal storage account for deployment  $TMP_AZURE_STORAGE_ACCOUNT in $LOCATION  "

az storage account create --location "$LOCATION"  --name ${TMP_AZURE_STORAGE_ACCOUNT} --resource-group ${RESOURCE_GROUP} --output none

export AZURE_STORAGE_KEY=$(az storage account keys list --account-name ${TMP_AZURE_STORAGE_ACCOUNT} --query "[0].value" | tr -d '"')

echo "Adding storage account container "
az storage container create --name ${CONTAINER} --account-name ${TMP_AZURE_STORAGE_ACCOUNT} --output none

echo "Uploading azure deployment template files "
az storage blob upload-batch -d ${CONTAINER} --account-name ${TMP_AZURE_STORAGE_ACCOUNT}   --account-key "${AZURE_STORAGE_KEY}" -s "${WORKDIR}" --pattern "*.*" --only-show-errors
unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)  expiretime=$(date -u -d '1 day' +%Y-%m-%dT%H:%MZ) ;;
    Darwin*) expiretime=$(date -v+1d +%Y-%m-%dT%H:%MZ) ;;
esac


connection=$(az storage account show-connection-string --resource-group ${RESOURCE_GROUP} --name ${TMP_AZURE_STORAGE_ACCOUNT}  --query connectionString)
SAS_TOKEN=$( az storage container  generate-sas --name $CONTAINER --account-name ${TMP_AZURE_STORAGE_ACCOUNT} --expiry $expiretime  --https-only --permissions dlr --output tsv --connection-string $connection )
TEMPLATE_URL=$(az storage blob url --container-name $CONTAINER --name mainTemplate.json --output tsv --connection-string $connection )
TEMPLATE_BASE_URI=https://${TMP_AZURE_STORAGE_ACCOUNT}.blob.core.windows.net/${CONTAINER}/
echo -e "\n\n\n #################################################################################################################"
echo "###############################     IMPORTANT!  DO NOT IGNORE! ##################################################"
echo -e "#################################################################################################################\n\n\n"

echo "GDC Service supports both Windows (via managed identity and service principal in your AD) and SQL Server (user/password) authentication modes."
echo "For Windows authentication, the Directory Readers role must be assigned to managed instance identity of SQL server before you can set up an Azure AD admin for the managed instance. If the role isn't assigned to the SQL logical server identity, creating Azure AD users in Azure SQL will fail. For more information, see Azure Active Directory service principal with Azure SQL https://docs.microsoft.com/en-us/azure/azure-sql/database/authentication-aad-service-principal "
echo "Windows authentication is considered to be more secure approach. 'Directory Readers' AD role assigment is a manual process and requires *Global Administrator* AD permission"

while true; do
    read -p "Would you like to use SQL Server (user/password) authentication mode ? Select N to use Windows authentication  (Y/n) " use_sql_pass_yn
    case ${use_sql_pass_yn} in
        [Yy]* ) USE_SQL_PASS_MODE_PARAM="true"; echo "SQL Server (user/password) authentication mode is selected";  break;;
        [Nn]* ) USE_SQL_PASS_MODE_PARAM="false"; echo "Windows authentication mode is selected";  break;;
        * ) echo "Please answer yes or no.";;
    esac
done
command -v pwsh --version &> /dev/null && which sqlcmd &> /dev/null
SQL_SCHEMA_GENERATION_LOCAL=$?
SQL_PASS_MODE_PARAM="--sql-auth ${USE_SQL_PASS_MODE_PARAM}"
SCHEMA_GENERATION_MODE=$([ "$SQL_SCHEMA_GENERATION_LOCAL" == 0 ] && echo "auto" || echo "manual")

pushd $WORKDIR/scripts
  echo "Starting deployment script.... "
  # install dependencies
  ~/.gdc-env/bin/python ./install.py --deployment-name $DEPLOYMENT_NAME --resource-group $RESOURCE_GROUP \
                              --template-base-uri ${TEMPLATE_BASE_URI} --sas-token $SAS_TOKEN \
                              --docker-login "gdc-readonly-token" --docker-password $DOCKER_PASSWORD ${LOG_INSIGHTS_PARAM} ${DEBUG} ${SQL_PASS_MODE_PARAM}
popd

dbserver=$(az sql server  list --resource-group ${RESOURCE_GROUP} --query "[].name" -o tsv)
set +e
# address local mode first
AUTO_GENERATION_SUCCESSFUL=$([ -f ~/.gdc/db_stage_successful ] && echo "true" || echo "false" )
if [[ ${AUTO_GENERATION_SUCCESSFUL} == "false" ]]; then
    if [[ "${USE_SQL_PASS_MODE_PARAM}" == "false" ]]; then
      echo -e "\n\n\n#################################################################################################################"
      echo "###############################     IMPORTANT!  DO NOT IGNORE! ##################################################"
      echo -e "#################################################################################################################\n\n\n"
      echo "Deployment has been set on hold. This manual step is mandatory otherwise the deployment won't operate properly. "
      echo "Follow this instruction to add SQL Server '${dbserver}' managed Identity into Directory Readers"
      echo "https://docs.microsoft.com/en-us/azure/azure-sql/database/authentication-aad-directory-readers-role-tutorial#add-azure-sql-managed-identity-to-the-group"
      echo "NOTE: SQL schema initialization will fail if you don't add SQL Server into 'Directory Readers' role and installation procedure have to be started over after deployment resource group is deleted"
      echo "Azure CLI doesn't support operations that allow to verify this permission. You have to confirm it manually"
      while true; do
        read -p "Confirm SQL server identity was added to 'Directory Readers' role in order to proceed with Azure SQL Server schema creation. Select N if you would like to skip this step and retry later by running post-deployment script again (Y/N) " sql_server_yn
        case $sql_server_yn in
            [Yy]* ) SCHEMA_GENERATION_MODE="auto"; break;;
            [Nn]* ) SCHEMA_GENERATION_MODE="manual"; break;;
            * ) echo "Please answer yes or no.";;
        esac
      done
    fi
    pushd "$WORKDIR/scripts"
      python ./run_db_stage.py  ${SQL_PASS_MODE_PARAM} --mode manual --only-generate-schema true
      echo -e "SQL schema has been saved to:\n $( ls $WORKDIR/sql-server/*.sql ) "
    popd
    if [[ "${SCHEMA_GENERATION_MODE}" == "auto" ]]; then
        if [[ "${USE_SQL_PASS_MODE_PARAM}" == "true" ]]; then
          if [[ $SQL_SCHEMA_GENERATION_LOCAL == 0 ]]; then
            pushd $WORKDIR/sql-server
              echo "Initializing database schema using powershell locally "
              # script assumes SQL schema files have been generated and placed in the same folder
              pwsh ./run_init_schema_local.ps1 -sqlServerName $dbserver -ResourceGroup $RESOURCE_GROUP -subscriptionId $SUBSCRIPTION_ID
              AUTO_GENERATION_SUCCESSFUL=$([ "$?" == 0 ] && echo "true" || echo "false")
            popd
            # initiate db_state in manual mode to complete stage state
            if [[ "${AUTO_GENERATION_SUCCESSFUL}" == "true" ]]; then
              touch ~/.gdc/db_stage_successful
              pushd "$WORKDIR/scripts"
                python ./run_db_stage.py  ${SQL_PASS_MODE_PARAM} --mode manual
              popd
            fi
          else
            echo "No Powershell and sqlcmd found on the host to initialize database schema automatically"
          fi
        else
          # Windows authentication mode with auto init enabled, we need
          pushd "$WORKDIR/scripts"
            python ./run_db_stage.py  ${SQL_PASS_MODE_PARAM}  --resource-group $RESOURCE_GROUP \
                                    --template-base-uri ${TEMPLATE_BASE_URI} --sas-token $SAS_TOKEN ${DEBUG} --mode $SCHEMA_GENERATION_MODE
            AUTO_GENERATION_SUCCESSFUL=$([ "$?" == 0 ] && echo "true" || echo "false")
          popd
          if [[ "${AUTO_GENERATION_SUCCESSFUL}" == "true" ]]; then
              touch ~/.gdc/db_stage_successful
          fi
        fi
    fi

    set -e
    if [[ "${AUTO_GENERATION_SUCCESSFUL}"  == "false"  || "${SCHEMA_GENERATION_MODE}" == "manual" ]]; then
        if [[ "${AUTO_GENERATION_SUCCESSFUL}"  == "false" &&  "${SCHEMA_GENERATION_MODE}" == "auto" ]]; then
            echo "Automated SQL schema initialization has failed or cancel. Falling back to manual mode"
            echo -e "We've generated generated SQL schema files and saved at ${WORKDIR}/sql-server/ }. Please connect to $dbserver.database.windows.net using your SQL administrator credentials or AD admin  and sequentially execute the following scripts: \n schema.sql, \n stored_procedures.sql, \n data.sql, \n custom-init.sql  "
            while true; do
              read -p "Confirm SQL schema has been manually initialized. Select N if you would like to skip this step and execute it later by running post-deployment script again (Y/N)" manual_schema_completed
              case $manual_schema_completed in
                  [Yy]* )
                    pushd "$WORKDIR/scripts"
                      python ./run_db_stage.py  ${SQL_PASS_MODE_PARAM} --mode manual
                      touch ~/.gdc/db_stage_successful
                    popd
                    break;;
                  [Nn]* ) echo "SQL schema wasn't initialized "; break;;
                  * ) echo "Please answer yes or no.";;
              esac
            done
        fi
    fi
fi
echo "Running post-deployment script...";
### run post-deployment script
pushd $WORKDIR/scripts
  ~/.gdc-env/bin/python post-deployment.py --resource-group $RESOURCE_GROUP ${DEBUG}
  echo " Post deployment script completed successfully at $(date)"
popd

