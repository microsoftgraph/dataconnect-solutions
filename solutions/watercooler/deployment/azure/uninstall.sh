#!/usr/bin/env bash

set -e

DEPLOYMENT_NAME=
SUBSCRIPTION_ID=
while [[ "$#" -gt 0 ]]; do
    case $1 in
      -n | --deployment-name ) DEPLOYMENT_NAME="$2"; shift ;;
      -s | --subscription ) SUBSCRIPTION_ID="$2"; shift ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done
if [[ -z "$DEPLOYMENT_NAME" ]]; then
  read -p "Enter deployment name: " DEPLOYMENT_NAME
fi



if [[ -z "$SUBSCRIPTION_ID" ]]; then
  SUBSCRIPTION_ID=$(az account show --query id -o tsv)
  if [[ $(az account list --output tsv | wc -l )  -gt  "1" ]]; then
      echo "Multiple subscriptions found"
      az account list --output table
      echo "--------------------------------------------------"
      echo "Current subscription: "
      az account list --output table | grep "${SUBSCRIPTION_ID}"
      read -p "Would you like to uninstall the deploy on this subscription? (Y/n) " -n 1 -r
      echo    # move to a new line
      if [[ ! $REPLY =~ ^[Yy]$ ]]
      then
          read -p "Please provide the desired SubscriptionId, from the list displayed above. " -r SUBSCRIPTION_ID
      fi
  fi
fi

SUBSCRIPTION_NAME=$(az account subscription show --id "$SUBSCRIPTION_ID" --query displayName  -o tsv)
TENANT_ID=$(az account show --query tenantId -o tsv)

WC_SERVICE_SP_OBJ_ID=$(az ad sp list  --all --display-name wc-service --query "[].{objectId: objectId}" -o tsv)
WC_M365_SERVICE_SP_OBJ_ID=$(az ad sp list  --all --display-name wc-m365-reader --query "[].{objectId: objectId}" -o tsv)
WC_WEB_APP_OBJ_ID=$(az ad app list --all --filter " displayName eq '${DEPLOYMENT_NAME}-jwc-aad-web-app' " --query "[].{objectId:objectId}"   -o tsv)
found_apps_count=$(az ad app list --all --filter " displayName eq '${DEPLOYMENT_NAME}-jwc-aad-web-app' " --query "[].{objectId:objectId}" -o tsv | wc -l)
RESOURCE_GROUP=$(az group list --subscription "${SUBSCRIPTION_ID}" --query "[?name=='${DEPLOYMENT_NAME}-resources'].{name:name}" -o tsv )

echo "You are about to uninstall the watercooler project from current Azure Subscription: ${SUBSCRIPTION_NAME}, ID: ${SUBSCRIPTION_ID} , resource group: ${RESOURCE_GROUP} "
  read -p "Would you like continue (Y/n) " -n 1 -r
  echo -e "\n"
  if [[ ! $REPLY =~ ^[Yy]$ ]]
      then
          echo -e "\n Aborting operations..."
          exit 1
  fi

if [[ -n "${WC_SERVICE_SP_OBJ_ID}" && -n "${WC_M365_SERVICE_SP_OBJ_ID}" ]]; then
  read -p "Would you like delete the 'wc-service' and 'wc-m365-reader' service principals? This is only recommended if you want to redeploy from scratch and these principals are not used elsewhere! (Y/n) " -n 1 -r
  echo -e "\n"
  if [[ $REPLY =~ ^[Yy]$ ]]; then
      echo "Deleting service principals."
      az ad sp delete --id "${WC_SERVICE_SP_OBJ_ID}"
      az ad sp delete --id "${WC_M365_SERVICE_SP_OBJ_ID}"
  fi
fi

if [[ -n "${RESOURCE_GROUP}" ]]; then
   echo "Deleting resource group ${RESOURCE_GROUP} "
   az group delete --name "${RESOURCE_GROUP}" --subscription "${SUBSCRIPTION_ID}"
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


read -p "Would you like delete local files related to previous deployments (if any)? Recommended if you want to redeploy from scratch (Y/n) " -n 1 -r
echo -e "\n"
if [[ $REPLY =~ ^[Yy]$ ]]; then
  rm -rf ~/.wc
  rm -rf ~/.wc-env
  echo "Deleted local files."
fi



