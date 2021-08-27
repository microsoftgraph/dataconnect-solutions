#!/bin/bash

DATAVERSE_DATABASE_NAME=""
WORKSPACE_NAME=""


while [[ "$#" -gt 0 ]]; do
    case $1 in
      -d | --dataverse-database-name ) DATAVERSE_DATABASE_NAME="$2"; shift ;;
      -w | --workspace-name ) WORKSPACE_NAME="$2"; shift ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

echo "SPARK_POOL_NAME=$SPARK_POOL_NAME"
echo "WORKSPACE_NAME=$WORKSPACE_NAME"


echo -e "\n\n################### Authenticating to Azure #####################\n\n "

az login

echo "Deploying linkedservices ... "

#on demand slq pool linked service
OnDemandSqlPoolDefinition=`cat ./linkedservices/LS_Dataverse.json`
OnDemandSqlPoolDefinition="${OnDemandSqlPoolDefinition//<synapseworkspacename>/$WORKSPACE_NAME}"
OnDemandSqlPoolDefinition="${OnDemandSqlPoolDefinition//<linkeddataversename>/$DATAVERSE_DATABASE_NAME}"

az synapse linked-service create --file "$OnDemandSqlPoolDefinition" --name LS_Dataverse --workspace-name "$WORKSPACE_NAME"


echo "Deploying datasets ... "

for file in ./datasets/*
do

 file_path="$file"
 prefix="./dataset/"
 suffix=".json"

 dataset_name=${file_path/#$prefix}


 dataset_name=${dataset_name/%$suffix}


 json_definition=`cat $file_path`


 az synapse dataset create  --file "$json_definition" --name "$dataset_name" --workspace-name "$WORKSPACE_NAME"
done

echo "Deploying pipelines ... "

# create Dataverse pipeline

conversation_lineage_pipeline_definition=`cat ./pipelines/PL_Copy_Dataverse_Data.json`

az synapse pipeline create --file "$conversation_lineage_pipeline_definition" --name PL_Copy_Dataverse_Data --workspace-name "$WORKSPACE_NAME"









