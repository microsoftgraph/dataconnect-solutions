#!/usr/bin/env bash

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

GDC_SERVICE_SP_OBJ_ID=$(az ad sp list  --all --display-name gdc-service --query "[].{objectId: objectId}" -o tsv)
GDC_M365_SERVICE_SP_OBJ_ID=$(az ad sp list  --all --display-name gdc-m365-reader --query "[].{objectId: objectId}" -o tsv)
GDC_WEB_APP_OBJ_ID=$(az ad app list --all --filter " displayName eq '${DEPLOYMENT_NAME}-jgraph-aad-web-app' " --query "[].{objectId:objectId}"   -o tsv)
found_apps_count=$(az ad app list --all --filter " displayName eq '${DEPLOYMENT_NAME}-jgraph-aad-web-app' " --query "[].{objectId:objectId}" -o tsv | wc -l)
RESOURCE_GROUP=$(az group list --subscription "${SUBSCRIPTION_ID}" --query "[?name=='${DEPLOYMENT_NAME}-resources'].{name:name}" -o tsv )

echo "You are about to uninstall the Project Staffing project from current Azure Subscription: ${SUBSCRIPTION_NAME}, ID: ${SUBSCRIPTION_ID} , resource group: ${RESOURCE_GROUP} "
yes_no_confirmation "Would you like continue (Y/n) " reply_yn true
if [[ "${reply_yn}" == false ]]; then
    echo -e "\n Aborting operations..."
    exit 1
fi

if [[ -n "${GDC_SERVICE_SP_OBJ_ID}" && -n "${GDC_M365_SERVICE_SP_OBJ_ID}" ]]; then

  yes_no_confirmation "Would you like delete the 'gdc-service' and 'gdc-m365-reader' service principals? This is only recommended if you want to redeploy from scratch and these principals are not used elsewhere! (Y/n) " DELETE_SP true
  if [[ "${DELETE_SP}" == true ]]; then
      echo "Deleting service principals."
      az ad sp delete --id "${GDC_SERVICE_SP_OBJ_ID}"
      az ad sp delete --id "${GDC_M365_SERVICE_SP_OBJ_ID}"
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

if [[ "$found_apps_count" -eq "1" && -n "${GDC_WEB_APP_OBJ_ID}" ]]; then
    echo "Deleting the jgraph-aad-web-app app registration from Active Directory."
    az ad app delete --id "${GDC_WEB_APP_OBJ_ID}"
else
  if [[ -z "${GDC_WEB_APP_OBJ_ID}" || "$found_apps_count" -eq "0" ]]; then
    echo "There is no app registration named ${DEPLOYMENT_NAME}-jgraph-aad-web-app to be deleted from Active Directory. Skipping this step."
  else
    echo "Found multiple jgraph-aad-web-app app registrations. These need to be deleted manually from Active Directory."
  fi
fi

yes_no_confirmation "Would you like delete local files related to previous deployments (if any)? Recommended if you want to redeploy from scratch (Y/n) " DELETE_LOCAL_FILES true

if [[ "${DELETE_LOCAL_FILES}" == true ]]; then
  rm -rf ~/.gdc
  rm -rf ~/.gdc-env
  echo "Deleted local files."
fi



