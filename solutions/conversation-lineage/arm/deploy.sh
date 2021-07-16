#!/bin/bash

SPARK_POOL_NAME=""
WORKSPACE_NAME=""
START_DATE=""
END_DATE=`date -u +"%Y-%m-%dT%H:%M:%SZ"`
KEY_VAULT_ENDPOINT=""
STORAGE_ACCOUNT_ENDPOINT=""
SP_TENANT_ID=""
SP_ID=""
DEDICATED_SQL_POOL_ENDPOINT=""
SQL_POOL_DATABASE_NAME=""
AZURE_AI_ENDPOINT=""
M365_EXTRACTION_GROUP_ID=""

while [[ "$#" -gt 0 ]]; do
    case $1 in
      -k | --spark-pool-name ) SPARK_POOL_NAME="$2"; shift ;;
      -w | --workspace-name ) WORKSPACE_NAME="$2"; shift ;;
      -s | --start-date ) START_DATE="$2"; shift ;; #format 2020-01-01T06:00:00Z
      -v | --key-vault-endpoint ) KEY_VAULT_ENDPOINT="$2"; shift ;; #https://gdcbackend75ab71f.vault.azure.net/
      -v | --storage-account-endpoint ) STORAGE_ACCOUNT_ENDPOINT="$2"; shift ;; #https://gdcbackend75ab71f.vault.azure.net/
      -t | --service-principal-tenant-id ) SP_TENANT_ID="$2"; shift ;; #d26bf63a-a52f-436a-bf3b-531b1e378694
      -i | --service-principal-id ) SP_ID="$2"; shift ;; #481ebbf9-c2b2-4cad-9a8f-9d2fd3ccf56d
      -q | --dedicated-sql-pool-endpoint ) DEDICATED_SQL_POOL_ENDPOINT="$2"; shift ;; #gdc-synapse.sql.azuresynapse.net
      -d | --sql-pool-database-name ) SQL_POOL_DATABASE_NAME="$2"; shift ;; #GDCdedicatedSQLpool
      -z | --azure-ai-endpoint ) AZURE_AI_ENDPOINT="$2"; shift ;; #GDCdedicatedSQLpool
      -m | --m365-extraction-group-id ) M365_EXTRACTION_GROUP_ID="$2"; shift ;; #GDCdedicatedSQLpool
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

if ! [[ $START_DATE =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$ ]]
then 
  echo "Date $START_DATE is in an invalid format. Expected format is YYYY-MM-DDTHH:mm:SS (e.g. 2020-01-01T06:00:00Z)."
  exit 1
fi

if ! [[ "$KEY_VAULT_ENDPOINT" =~ '/'$ ]]; then 
  KEY_VAULT_ENDPOINT="$KEY_VAULT_ENDPOINT/"
fi

if ! [[ "$STORAGE_ACCOUNT_ENDPOINT" =~ '/'$ ]]; then
  STORAGE_ACCOUNT_ENDPOINT="$STORAGE_ACCOUNT_ENDPOINT/"
fi

echo "SPARK_POOL_NAME=$SPARK_POOL_NAME"
echo "WORKSPACE_NAME=$WORKSPACE_NAME"
echo "START_DATE=$START_DATE"
echo "KEY_VAULT_ENDPOINT=$KEY_VAULT_ENDPOINT"
echo "STORAGE_ACCOUNT_ENDPOINT=$STORAGE_ACCOUNT_ENDPOINT"
echo "SP_TENANT_ID=$SP_TENANT_ID"
echo "SP_ID=$SP_ID"
echo "DEDICATED_SQL_POOL_ENDPOINT=$DEDICATED_SQL_POOL_ENDPOINT"
echo "SQL_POOL_DATABASE_NAME=$SQL_POOL_DATABASE_NAME"


suffix=".blob.core.windows.net/"
prefix="https://"
storage_account_name=${STORAGE_ACCOUNT_ENDPOINT#"$prefix"}
storage_account_name=${storage_account_name/%$suffix}

echo -e "\n\n################### Authenticating to Azure #####################\n\n "

az login

echo "Deploying linkedservices ... "

#create key vault linked service
key_vault_linked_service_definition=`cat ./End2EndMgdc101WithConvLineage/linkedService/keyvault_linkedservice.json`
key_vault_linked_service_definition="${key_vault_linked_service_definition//<keyVaultEndpoint>/$KEY_VAULT_ENDPOINT}"

az synapse linked-service create --file "$key_vault_linked_service_definition" --name keyvault_linkedservice --workspace-name "$WORKSPACE_NAME"

#create blob storage linked service
blob_storage_linked_service_definition=`cat ./End2EndMgdc101WithConvLineage/linkedService/blobstorage_linkedservice.json`
blob_storage_linked_service_definition="${blob_storage_linked_service_definition//<storageAccountEndpoint>/$STORAGE_ACCOUNT_ENDPOINT}"
blob_storage_linked_service_definition="${blob_storage_linked_service_definition//<servicePrincipalTenantId>/$SP_TENANT_ID}"
blob_storage_linked_service_definition="${blob_storage_linked_service_definition//<servicePrincipalId>/$SP_ID}"


az synapse linked-service create --file "$blob_storage_linked_service_definition" --name blobstorage_linkedservice --workspace-name "$WORKSPACE_NAME"

#office365_linkedservice
office_365_linked_service_definition=`cat ./End2EndMgdc101WithConvLineage/linkedService/office365_linkedservice.json`
office_365_linked_service_definition="${office_365_linked_service_definition//<office365TenantId>/$SP_TENANT_ID}"
office_365_linked_service_definition="${office_365_linked_service_definition//<servicePrincipalTenantId>/$SP_TENANT_ID}"
office_365_linked_service_definition="${office_365_linked_service_definition//<servicePrincipalId>/$SP_ID}"


az synapse linked-service create --file "$office_365_linked_service_definition" --name office365_linkedservice --workspace-name "$WORKSPACE_NAME"

#dedicate slq pool linked service
SynapseDedicatedSqlPoolDefinition=`cat ./End2EndMgdc101WithConvLineage/linkedService/SynapseDedicatedSqlPool.json`
SynapseDedicatedSqlPoolDefinition="${SynapseDedicatedSqlPoolDefinition//<dedicatedSqlPoolEndpoint>/$DEDICATED_SQL_POOL_ENDPOINT}"
SynapseDedicatedSqlPoolDefinition="${SynapseDedicatedSqlPoolDefinition//<sqlPoolDatabaseName>/$SQL_POOL_DATABASE_NAME}"

az synapse linked-service create --file "$SynapseDedicatedSqlPoolDefinition" --name SynapseDedicatedSqlPool --workspace-name "$WORKSPACE_NAME"


echo "Deploying datasets ... "

for file in ./End2EndMgdc101WithConvLineage/dataset/*
do

 file_path="$file"
 prefix="./End2EndMgdc101WithConvLineage/dataset/"
 suffix=".json"

 dataset_name=${file_path/#$prefix}


 dataset_name=${dataset_name/%$suffix}


 json_definition=`cat $file_path`

 json_definition="${json_definition//synapsededicatesqlpool/$SQL_POOL_DATABASE_NAME}"

 az synapse dataset create  --file "$json_definition" --name "$dataset_name" --workspace-name "$WORKSPACE_NAME"
done

echo "Deploying notebooks ... "


az synapse notebook create --file @./End2EndMgdc101WithConvLineage/notebook/ExplodeEmailsByRecipients.json --name 'ExplodeEmailsByRecipients' --workspace-name "$WORKSPACE_NAME" --spark-pool-name "$SPARK_POOL_NAME"

az synapse notebook create --file @./End2EndMgdc101WithConvLineage/notebook/ConversationLineage.json --name 'ConversationLineage' --workspace-name "$WORKSPACE_NAME" --spark-pool-name "$SPARK_POOL_NAME"

az synapse notebook create --file @./End2EndMgdc101WithConvLineage/notebook/JoinUsersAndManagers.json --name 'JoinUsersAndManagers' --workspace-name "$WORKSPACE_NAME" --spark-pool-name "$SPARK_POOL_NAME"

echo "Deploying pipelines ... "

# create ConversationLineage pipeline

conversation_lineage_pipeline_definition=`cat ./End2EndMgdc101WithConvLineage/pipeline/ConversationLineage.json`
conversation_lineage_pipeline_definition="${conversation_lineage_pipeline_definition//<key_vault_endpoint>/$KEY_VAULT_ENDPOINT}"

az synapse pipeline create --file "$conversation_lineage_pipeline_definition" --name ConversationLineage --workspace-name "$WORKSPACE_NAME"

# create PrepareUsersData pipeline

prepare_users_data_pipeline_definition=`cat ./End2EndMgdc101WithConvLineage/pipeline/PrepareUsersData.json`
prepare_users_data_pipeline_definition="${prepare_users_data_pipeline_definition//<key_vault_endpoint>/$KEY_VAULT_ENDPOINT}"
prepare_users_data_pipeline_definition="${prepare_users_data_pipeline_definition//<storageAccountName>/$storage_account_name}"
prepare_users_data_pipeline_definition="${prepare_users_data_pipeline_definition//<dedicatedSqlPoolEndpoint>/$DEDICATED_SQL_POOL_ENDPOINT}"
prepare_users_data_pipeline_definition="${prepare_users_data_pipeline_definition//<sqlPoolDatabaseName>/$SQL_POOL_DATABASE_NAME}"

az synapse pipeline create --file "$prepare_users_data_pipeline_definition" --name PrepareUsersData --workspace-name "$WORKSPACE_NAME"

# create PrepareEmailsData pipeline

prepare_emails_data_pipeline_definition=`cat ./End2EndMgdc101WithConvLineage/pipeline/PrepareEmailsData.json`
prepare_emails_data_pipeline_definition="${prepare_emails_data_pipeline_definition//<key_vault_endpoint>/$KEY_VAULT_ENDPOINT}"
prepare_emails_data_pipeline_definition="${prepare_emails_data_pipeline_definition//<storageAccountName>/$storage_account_name}"

az synapse pipeline create --file "$prepare_emails_data_pipeline_definition" --name PrepareEmailsData --workspace-name "$WORKSPACE_NAME"

# create PrepareEventsData

az synapse pipeline create --file @./End2EndMgdc101WithConvLineage/pipeline/PrepareEventsData.json --name PrepareEventsData --workspace-name "$WORKSPACE_NAME"

# create PrepareTeamChatsData

az synapse pipeline create --file @./End2EndMgdc101WithConvLineage/pipeline/PrepareTeamChatsData.json --name PrepareTeamChatsData --workspace-name "$WORKSPACE_NAME"

# create End2EndMgdc101WithConvLineage pipeline

end2end_pipeline_definition=`cat ./End2EndMgdc101WithConvLineage/pipeline/End2EndMgdc101WithConvLineage.json`

suffix=".sql.azuresynapse.net"
sql_server_name=${DEDICATED_SQL_POOL_ENDPOINT/%$suffix}

end2end_pipeline_definition="${end2end_pipeline_definition//<sql_database_name>/$SQL_POOL_DATABASE_NAME}"
end2end_pipeline_definition="${end2end_pipeline_definition//<azure_ai_endpoint>/$AZURE_AI_ENDPOINT}"
end2end_pipeline_definition="${end2end_pipeline_definition//<sql_server_name>/$sql_server_name}"
end2end_pipeline_definition="${end2end_pipeline_definition//<m365_extraction_group_id>/$M365_EXTRACTION_GROUP_ID}"

az synapse pipeline create --file "$end2end_pipeline_definition" --name End2EndMgdc101WithConvLineage --workspace-name "$WORKSPACE_NAME"



echo "Deploying triggers"


backfill_trigger_definition=`cat ./End2EndMgdc101WithConvLineage/trigger/MGDC101_backfill_trigger.json`

backfill_trigger_definition="${backfill_trigger_definition//<startTimeValue>/$START_DATE}"
backfill_trigger_definition="${backfill_trigger_definition//<endTimeValue>/$END_DATE}"

az synapse trigger create --file "$backfill_trigger_definition" --name MGDC101_backfill_trigger --workspace-name "$WORKSPACE_NAME"


recurring_trigger_definition=`cat ./End2EndMgdc101WithConvLineage/trigger/MGDC101_recurring_trigger.json`

recurring_trigger_definition="${recurring_trigger_definition//<startTimeValue>/$END_DATE}"

az synapse trigger create --file "$recurring_trigger_definition" --name MGDC101_recurring_trigger --workspace-name "$WORKSPACE_NAME"








