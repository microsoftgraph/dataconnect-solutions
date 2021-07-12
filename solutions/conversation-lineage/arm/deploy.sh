#!/bin/bash

DEDICATED_SQL_POOL_NAME=""
SPARK_POOL_NAME=""
WORKSPACE_NAME=""

while [[ "$#" -gt 0 ]]; do
    case $1 in
      -p | --dedicate-sql-pool-name ) DEDICATED_SQL_POOL_NAME="$2"; shift ;;
      -k | --spark-pool-name ) SPARK_POOL_NAME="$2"; shift ;;
      -w | --workspace-name ) WORKSPACE_NAME="$2"; shift ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

echo "DEDICATED_SQL_POOL_NAME=$DEDICATED_SQL_POOL_NAME"
echo "SPARK_POOL_NAME=$SPARK_POOL_NAME"
echo "WORKSPACE_NAME=$WORKSPACE_NAME"

echo "Deploying datasets ... "

for file in ./End2EndMgdc101WithConvLineage_support_live/dataset/*
do

 file_path="$file"
 prefix="./End2EndMgdc101WithConvLineage_support_live/dataset/"
 suffix=".json"

 dataset_name=${file_path/#$prefix}


 dataset_name=${dataset_name/%$suffix}


 json_definition=`cat $file_path`

 json_definition="${json_definition/synapsededicatesqlpool/$DEDICATED_SQL_POOL_NAME}"

 az synapse dataset create  --file "$json_definition" --name "$dataset_name" --workspace-name "$WORKSPACE_NAME"
done

echo "Deploying notebooks ... "

az synapse notebook create --file @./End2EndMgdc101WithConvLineage_support_live/notebook/ExplodeEmailsByRecipients.json --name 'ExplodeEmailsByRecipients' --workspace-name "$WORKSPACE_NAME" --spark-pool-name "$SPARK_POOL_NAME"

az synapse notebook create --file @./End2EndMgdc101WithConvLineage_support_live/notebook/ConversationLineage.json --name 'ConversationLineage' --workspace-name "$WORKSPACE_NAME" --spark-pool-name "$SPARK_POOL_NAME"

az synapse notebook create --file @./End2EndMgdc101WithConvLineage_support_live/notebook/JoinUsersAndManagers.json --name 'JoinUsersAndManagers' --workspace-name "$WORKSPACE_NAME" --spark-pool-name "$SPARK_POOL_NAME"

echo "Deploying pipelines ... "


az synapse pipeline create --file @./End2EndMgdc101WithConvLineage_support_live/pipeline/ConversationLineage.json --name ConversationLineage --workspace-name "$WORKSPACE_NAME"

az synapse pipeline create --file @./End2EndMgdc101WithConvLineage_support_live/pipeline/PrepareUsersData.json --name PrepareUsersData --workspace-name "$WORKSPACE_NAME"

az synapse pipeline create --file @./End2EndMgdc101WithConvLineage_support_live/pipeline/PrepareEmailsData.json --name PrepareEmailsData --workspace-name "$WORKSPACE_NAME"

az synapse pipeline create --file @./End2EndMgdc101WithConvLineage_support_live/pipeline/PrepareEventsData.json --name PrepareEventsData --workspace-name "$WORKSPACE_NAME"

az synapse pipeline create --file @./End2EndMgdc101WithConvLineage_support_live/pipeline/PrepareTeamChatsData.json --name PrepareTeamChatsData --workspace-name "$WORKSPACE_NAME"

az synapse pipeline create --file @./End2EndMgdc101WithConvLineage_support_live/pipeline/End2EndMgdc101WithConvLineage.json --name End2EndMgdc101WithConvLineage --workspace-name "$WORKSPACE_NAME"











