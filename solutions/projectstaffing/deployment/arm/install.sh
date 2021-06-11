#!/usr/bin/env bash

#
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT license. See LICENSE file in the project root for full license information.
#

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

# create virtual env
pip3 install  --disable-pip-version-check virtualenv -q
echo "Installing python virtual environment for deployment scripts..."
virtualenv -p python3  ~/.gdc-env -q
source ~/.gdc-env/bin/activate
~/.gdc-env/bin/python -m pip install --upgrade pip
~/.gdc-env/bin/pip install -r $WORKDIR/scripts/requirements.txt -q

echo "Installing Azure CLI extension for Databricks and Data Factory..."
az extension add --only-show-errors --upgrade --name databricks
az extension add --only-show-errors --upgrade --name datafactory

mkdir -p ~/.gdc

DEPLOYMENT_NAME=
LOCATION=
DEBUG=
DOCKER_PASSWORD=
SUBSCRIPTION_ID=

while [[ "$#" -gt 0 ]]; do
    case $1 in
      -n | --deployment-name ) DEPLOYMENT_NAME="$2"; shift ;;
      -l | --location ) LOCATION="$2"; shift ;;
      -p | --docker-password ) DOCKER_PASSWORD="$2"; shift ;;
      -s | --subscription ) SUBSCRIPTION_ID="$2"; shift ;;
      -d | --debug ) DEBUG="--debug true"; ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done
if [[ -z "$DEPLOYMENT_NAME" ]]; then
  read -p "Enter deployment name: " -r DEPLOYMENT_NAME
fi

if [[ -z "$LOCATION" ]]; then
  read -p "Enter Azure location: " -r LOCATION
fi

if [[ -z "$DOCKER_PASSWORD" ]]; then
  #  read -p "Enter docker repository password: " -r -s DOCKER_PASSWORD
  # For now, using hardcoded password to public ProjectStaffing docker images repository
  DOCKER_PASSWORD="ymCNmmEJ1Zv=BieuVmkM90BYC36UemAg"
fi


RESOURCE_GROUP="${DEPLOYMENT_NAME}-resources"

echo -e "\n\n###################Authenticating to Azure #####################\n\n "
az login

#
# -------- Deployment ------------------------------
#
#

if [[ -z "$SUBSCRIPTION_ID" ]]; then
  SUBSCRIPTION_ID=$(az account show --query id -o tsv)
  if [[ $(az account list --output tsv | wc -l )  -gt  "1" ]]; then
      echo "Multiple subscriptions found"
      az account list --output table
      echo "--------------------------------------------------"
      echo "Current subscription: "
      az account list --output table | grep "${SUBSCRIPTION_ID}"
      read -p "Would you like to deploy into this subscription? (Y/n) " -n 1 -r
      echo    # move to a new line
      if [[ ! $REPLY =~ ^[Yy]$ ]]
      then
          read -p "Please provide the desired SubscriptionId, from the list displayed above. " -r SUBSCRIPTION_ID
      fi
  fi
fi

TENANT_ID=$(az account show --subscription "$SUBSCRIPTION_ID" --query tenantId -o tsv)

echo "Deploying in subscription $SUBSCRIPTION_ID from tenant $TENANT_ID"

REQUIRED_ROLE="Owner"
LOGGED_USER_ID=$(az ad signed-in-user show --query objectId  --output tsv )
ASSIGNMENTS_LIST=$(az role assignment list --scope /subscriptions/${SUBSCRIPTION_ID} --assignee ${LOGGED_USER_ID} --include-classic-administrators true --include-groups --include-inherited --role "${REQUIRED_ROLE}" --query [].{id:id} --output tsv)

if [[ -z "${ASSIGNMENTS_LIST}" ]]; then
    echo "You don't have enough permissions within selected subscription ${SUBSCRIPTION_ID}. Required role: ${REQUIRED_ROLE}"
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
    read -p "There are not enough vCPUs available at ${LOCATION} region. You're using ${vCPU_USED} out of ${vCPU_LIMIT}, but ${MINIMAL_vCPU} are required. Would you still like to try to install ?(Y/n) " -n 1 -r
    echo    # move to a new line
    if [[ ! $REPLY =~ ^[Yy]$ ]]
    then
        echo "Canceling deployment due to lack of available vCPUs "
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
          read -p "Would you like to activate $service and continue installation (Y/n) " -r enable_yn
          case $enable_yn in
              [Yy]* )
                echo "Registering $service for  subscription $SUBSCRIPTION_ID ..."
                az provider register  --subscription "$SUBSCRIPTION_ID" --namespace "$service" --wait
                REGISTER_RESULT=$?
                if [[ $REGISTER_RESULT != 0 ]]; then
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
  echo "Access to logs will be limited for this deployment  "
  while true; do
      read -p "Do you want to continue without Log Analytics Workspace (Y/n) " -r log_yn
      case $log_yn in
          [Yy]* ) LOG_INSIGHTS_PARAM="--log-analytic-enabled false";  break;;
          [Nn]* ) echo "Installation has been terminated";  exit;;
          * ) echo "Please answer yes or no.";;
      esac
  done
fi

echo "This deployment script configures ProjectStaffing by default to run in simulated data mode. It requires synthetic input data to be copied from one of our public storages."
PS3="Select preferred location to copy domain expert and synthetic input data from based on your deployment location :"
select opt in westus westeurope southeastasia brazilsouth; do
   case $opt in
      westus)
        DEMO_DATA_STORAGE_ACCOUNT="prjstaffingnortham"
        break;;
      westeurope)
        DEMO_DATA_STORAGE_ACCOUNT="prjstaffingeu"
        break;;
      southeastasia)
         DEMO_DATA_STORAGE_ACCOUNT="prjstaffingsoutheastasia"
         break;;
      brazilsouth)
         DEMO_DATA_STORAGE_ACCOUNT="prjstaffingsoutham"
        break;;
      *)
        echo "Invalid option $opt ";;
  esac
done

echo "Creating resource group  $RESOURCE_GROUP in $LOCATION"

CONTAINER="gdc-artifacts"
az group create --name ${RESOURCE_GROUP} --location "$LOCATION" --output none

TMP_AZURE_STORAGE_ACCOUNT=$(az storage account list --resource-group ${RESOURCE_GROUP} --query "[?starts_with(name, 'gdcdeploy')].name" -o tsv)

if [[ -z "$TMP_AZURE_STORAGE_ACCOUNT" ]]; then
  RANDOM_STRING=$(head /dev/urandom | tr -dc a-z0-9 | head -c10)
  TMP_AZURE_STORAGE_ACCOUNT="gdcdeploy$RANDOM_STRING"
fi

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

command -v pwsh --version &> /dev/null && which sqlcmd &> /dev/null
SQL_SCHEMA_GENERATION_LOCAL=$?
USE_SQL_PASS_MODE_PARAM="true"
SQL_PASS_MODE_PARAM="--sql-auth ${USE_SQL_PASS_MODE_PARAM}"
SCHEMA_GENERATION_MODE=$([ "$SQL_SCHEMA_GENERATION_LOCAL" == 0 ] && echo "auto" || echo "manual")

pushd $WORKDIR/scripts
  echo "Starting deployment script.... "
  # install dependencies
  ~/.gdc-env/bin/python ./install.py --deployment-name "$DEPLOYMENT_NAME" --tenant-id "$TENANT_ID" \
                              --subscription-id "$SUBSCRIPTION_ID" --resource-group "$RESOURCE_GROUP" \
                              --template-base-uri "${TEMPLATE_BASE_URI}" --sas-token "$SAS_TOKEN" \
                              --docker-login "gdc-readonly-token" --docker-password "$DOCKER_PASSWORD" ${LOG_INSIGHTS_PARAM} ${DEBUG} ${SQL_PASS_MODE_PARAM}
popd

dbserver=$(az sql server  list --resource-group ${RESOURCE_GROUP} --query "[].name" -o tsv)
set +e
# address local mode first
AUTO_GENERATION_SUCCESSFUL=$([ -f ~/.gdc/db_stage_successful ] && echo "true" || echo "false" )
if [[ ${AUTO_GENERATION_SUCCESSFUL} == "false" ]]; then
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
    if [[ "${AUTO_GENERATION_SUCCESSFUL}"  == "false" ]]; then
        if [[ "${SCHEMA_GENERATION_MODE}" == "auto" ]]; then
            echo "Automated SQL schema initialization has failed or has been canceled. Falling back to manual mode"
        fi
        AUTH_MODE_MSG=""
        if [[  "${USE_SQL_PASS_MODE_PARAM}" == "true" ]]; then
          AUTH_MODE_MSG="using your SQL administrator credentials"
        else
          AUTH_MODE_MSG="using your Active Directory account"
        fi
        echo -e "We've generated generated SQL schema files and saved them at ${WORKDIR}/sql-server/ }.\nYou need to connect to $dbserver.database.windows.net $AUTH_MODE_MSG and sequentially execute the following scripts: \n $( ls $WORKDIR/sql-server/*.sql ) "
        while true; do
          read -p "Confirm SQL schema has been manually initialized. Select N if you would like to skip this step and proceed with post-deployment script instead (Y/N)" -r manual_schema_completed
          case $manual_schema_completed in
              [Yy]* )
                pushd "$WORKDIR/scripts"
                  python ./run_db_stage.py  ${SQL_PASS_MODE_PARAM} --mode manual
                  touch ~/.gdc/db_stage_successful
                popd
                break;;
              [Nn]* ) echo "Skipping schema initialization step..."; break;;
              * ) echo "Please answer yes or no.";;
          esac
        done
    fi
fi
echo "Running post-deployment script...";
### run post-deployment script
pushd $WORKDIR/scripts
  ~/.gdc-env/bin/python post-deployment.py --tenant-id "$TENANT_ID" --subscription-id "$SUBSCRIPTION_ID" \
                                           --remote-artifacts-storage-name  "$DEMO_DATA_STORAGE_ACCOUNT" \
                                           --resource-group "$RESOURCE_GROUP" ${DEBUG}
  echo " Post deployment script completed successfully at $(date)"
popd

