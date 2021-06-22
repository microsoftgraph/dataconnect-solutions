#!/usr/bin/env bash

#
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT license. See LICENSE file in the project root for full license information.
#

WORKDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

source ${WORKDIR}/functions.sh

set -e

DEPLOYMENT_NAME=
SUBSCRIPTION_ID=
while [[ "$#" -gt 0 ]]; do
    case $1 in
      -n | --deployment-name ) DEPLOYMENT_NAME="$2"; shift ;;
      -s | --subscription ) SUBSCRIPTION_ID="$2"; shift ;;
      -y | --no-input ) NO_INPUT="--no-input true"; ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done
if [[ -z "$DEPLOYMENT_NAME" ]]; then
  prompt_non_empty_str_value "Enter deployment name: " DEPLOYMENT_NAME
fi

echo -e "\n\n################### Authenticating to Azure #####################\n\n "
if [[ -z "${NO_INPUT}" ]]; then
  az login
else
  echo "Non-interactive mode is enabled. Reusing exiting authentication session"
fi

if [[ -z "$SUBSCRIPTION_ID" ]]; then
  SUBSCRIPTION_ID=$(az account show --query id -o tsv)
  select_subscription "Would you like to uninstall the deployment from this subscription? (Y/n) " SUBSCRIPTION_ID $SUBSCRIPTION_ID
fi

SUBSCRIPTION_NAME=$(az account subscription show --id "$SUBSCRIPTION_ID" --query displayName  -o tsv)
TENANT_ID=$(az account show --subscription "$SUBSCRIPTION_ID" --query tenantId -o tsv)

WC_WEB_APP_OBJ_ID=$(az ad app list --all --filter " displayName eq '${DEPLOYMENT_NAME}-jwc-aad-web-app' " --query "[].{objectId:objectId}"   -o tsv)
found_apps_count=$(az ad app list --all --filter " displayName eq '${DEPLOYMENT_NAME}-jwc-aad-web-app' " --query "[].{objectId:objectId}" -o tsv | wc -l)
RESOURCE_GROUP=$(az group list --subscription "${SUBSCRIPTION_ID}" --query "[?name=='${DEPLOYMENT_NAME}-resources'].{name:name}" -o tsv )
APP_SERVICE_NAME=$(az webapp list --resource-group "${RESOURCE_GROUP}" --subscription "${SUBSCRIPTION_ID}" --query "[].name" -o tsv)

echo "You are about to uninstall the Watercooler project from current Azure Subscription: ${SUBSCRIPTION_NAME}, ID: ${SUBSCRIPTION_ID} , resource group: ${RESOURCE_GROUP} "
yes_no_confirmation "Would you like continue (Y/n) " reply_yn true
if [[ "${reply_yn}" == false ]]; then
    echo -e "\n Aborting operations..."
    exit 1
fi

CONFIG_DIR="~/.wc-env/bin"
WC_SERVICE_SP_NAME=
WC_M365_SERVICE_SP_NAME=

###########Deleting watercooler service principal

if [ -d "`eval echo ${CONFIG_DIR//>}`" ]; then
  script_output=$(~/.wc-env/bin/python scripts/get_sp_names.py --service-principal-type wc-service | grep -P 'service_principal_name=.*\n?')
  if [[ -n "${script_output}" ]]; then
    WC_SERVICE_SP_NAME=${script_output#*=}
  fi
fi

if [[ -z "${WC_SERVICE_SP_NAME}" ]]; then
    prompt_str_value "If you want to delete the watercooler service principal, please provide its name. Otherwise just press enter: " WC_SERVICE_SP_NAME "${APP_SERVICE_NAME}-wc-service"
fi

if [[ -n "${WC_SERVICE_SP_NAME}" ]]; then

  WC_SERVICE_SP_OBJ_ID=$(az ad sp list  --all --display-name "$WC_SERVICE_SP_NAME" --query "[?contains(appOwnerTenantId, '$TENANT_ID')].{objectId: objectId}" -o tsv)

  if [[ -n "${WC_SERVICE_SP_OBJ_ID}" ]]; then

    yes_no_confirmation "Deleting a service principal is only recommended if you want to redeploy from scratch and the principal is not used elsewhere! Please confirm you want to delete the $WC_SERVICE_SP_NAME service principal (Y/n)" DELETE_SP true
    if [[ "${DELETE_SP}" == true ]]; then
        echo "Deleting service principal."
        az ad sp delete --id "${WC_SERVICE_SP_OBJ_ID}"
    fi
  fi
fi

###########Deleting watercooler m365 reader service principal

if [ -d "`eval echo ${CONFIG_DIR//>}`" ]; then
  script_output=$(~/.wc-env/bin/python scripts/get_sp_names.py --service-principal-type wc-m365-reader | grep -P 'service_principal_name=.*\n?')
  if [[ -n "${script_output}" ]]; then
    WC_M365_SERVICE_SP_NAME=${script_output#*=}
  fi
fi

if [[ -z "${WC_M365_SERVICE_SP_NAME}" ]]; then
    prompt_str_value "If you want to delete the watercooler m365 reader service principal, please provide its name. Otherwise just press enter: " WC_M365_SERVICE_SP_NAME "${APP_SERVICE_NAME}-wc-m365-reader"
fi

if [[ -n "${WC_M365_SERVICE_SP_NAME}" ]]; then

  WC_M365_SERVICE_SP_OBJ_ID=$(az ad sp list  --all --display-name "$WC_M365_SERVICE_SP_NAME" --query "[?contains(appOwnerTenantId, '$TENANT_ID')].{objectId: objectId}" -o tsv)

  if [[ -n "${WC_M365_SERVICE_SP_OBJ_ID}" ]]; then

    yes_no_confirmation "Deleting a service principal is only recommended if you want to redeploy from scratch and the principal is not used elsewhere! Please confirm you want to delete the $WC_M365_SERVICE_SP_NAME service principal (Y/n)" DELETE_SP true
    if [[ "${DELETE_SP}" == true ]]; then
        echo "Deleting service principal."
        az ad sp delete --id "${WC_M365_SERVICE_SP_OBJ_ID}"
    fi
  fi

fi



if [[ -n "${RESOURCE_GROUP}" ]]; then
   echo "Deleting resource group ${RESOURCE_GROUP} "
   YES_ARGS=""
   if [[ -n "${NO_INPUT}" ]]; then
     YES_ARGS=" --yes "
   fi
   az group delete --name "${RESOURCE_GROUP}" --subscription "${SUBSCRIPTION_ID}" ${YES_ARGS}
fi

if [[ "$found_apps_count" -eq "1" && -n "${WC_WEB_APP_OBJ_ID}" ]]; then
    echo "Deleting the jwc-aad-web-app app registration from Active Directory."
    az ad app delete --id "${WC_WEB_APP_OBJ_ID}"
else
  if [[ -z "${WC_WEB_APP_OBJ_ID}" || "$found_apps_count" -eq "0" ]]; then
    echo "There is no app registration named ${DEPLOYMENT_NAME}-jwc-aad-web-app to be deleted from Active Directory. Skipping this step."
  else
    echo "Found multiple jwc-aad-web-app app registrations. These need to be deleted manually from Active Directory."
  fi
fi

yes_no_confirmation "Would you like delete local files related to previous deployments (if any)? Recommended if you want to redeploy from scratch (Y/n) " DELETE_LOCAL_FILES true

if [[ "${DELETE_LOCAL_FILES}" == true ]]; then
  rm -rf ~/.wc
  rm -rf ~/.wc-env
  echo "Deleted local files."
fi



