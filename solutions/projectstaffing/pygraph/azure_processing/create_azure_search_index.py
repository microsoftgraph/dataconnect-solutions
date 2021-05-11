#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import requests
import sys
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from log_analytics_client.logger import LogAnalyticsLogger
from pyspark.sql import SparkSession
from types import SimpleNamespace

logger = None


def create_index(index_definition):
    url = endpoint + "/indexes" + api_version
    logger.info("Azure Search index creation request url: " + url)
    response = requests.post(url, headers=headers, json=index_definition)
    index = response.json()
    logger.info("Azure Search index creation request response: " + str(index))
    if response.status_code != 201:
        raise Exception("Failed to create search index.")


if __name__ == '__main__':

    if len(sys.argv) > 9:
        parser = argparse.ArgumentParser(description='Process arguments.')
        parser.add_argument('--new-azure-search-index-name', type=str,
                            help='new azure search index name')
        parser.add_argument('--azure-search-endpoint', type=str,
                            help='azure search account url')
        parser.add_argument('--index-definition-storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--index-definition-container-name', type=str,
                            help='storage account name')
        parser.add_argument('--index-definition-file-path', type=str,
                            help='storage account name')
        parser.add_argument('--application-id', type=str,
                            help='application id')
        parser.add_argument('--directory-id', type=str,
                            help='directory id')
        parser.add_argument('--adb-secret-scope-name', type=str,
                            help='secret scope name')
        parser.add_argument('--adb-sp-client-key-secret-name', type=str,
                            help='Azure Databricks Service Principal client key secret name in Databricks Secrets')
        parser.add_argument('--log-analytics-workspace-id', type=str,
                            help='Log Analytics workspace id')
        parser.add_argument('--log-analytics-workspace-key-name', type=str,
                            help='Log Analytics workspace key secret name')
        parser.add_argument('--key-vault-url', type=str,
                            help='Azure Key Vault url')
        parser.add_argument('--azure-search-admin-key-name', type=str,
                            help='Azure Search admin key secret name in Azure Key Vault')

        args = parser.parse_args()
        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)
    else:
        args = SimpleNamespace(
            new_azure_search_index_name="gdc-employees-index-test",
            azure_search_endpoint="",  # Fill this with appropriate value
            index_definition_storage_account_name="",  # Fill this with appropriate value
            index_definition_container_name="",  # Fill this with appropriate value
            index_definition_file_path="schema/azure_employees_index_definition.json",
            application_id="",  # Fill this with appropriate value
            directory_id="",  # Fill this with appropriate value
            adb_secret_scope_name="gdc",
            adb_sp_client_key_secret_name="gdc-service-principal-secret",
            log_analytics_workspace_id=" ",
            log_analytics_workspace_key_name="log-analytics-api-key",
            key_vault_url="https://gdckeyvault.vault.azure.net/",
            azure_search_admin_key_name="gdc-search-service-admin-key")
        SERVICE_PRINCIPAL_SECRET = json.load(open("config.json"))["SERVICE_PRINCIPAL_SECRET"]

    credential = ClientSecretCredential(tenant_id=args.directory_id,
                                        client_id=args.application_id,
                                        client_secret=SERVICE_PRINCIPAL_SECRET)
    client = SecretClient(vault_url=args.key_vault_url, credential=credential)

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[create_azure_search_index]")
    else:
        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="CreateAzureSearchIndex",
                                        log_server_time=True,
                                        name="[create_azure_search_index]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[create_azure_search_index]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    endpoint = args.azure_search_endpoint
    index_definition_storage_account_name = args.index_definition_storage_account_name
    index_definition_container_name = args.index_definition_container_name
    index_definition_file_path = args.index_definition_file_path
    adb_secret_scope_name = args.adb_secret_scope_name
    adb_sp_client_key_secret_name = args.adb_sp_client_key_secret_name
    storage_account_name = args.index_definition_storage_account_name

    # index_definition = json.load(open("azure_user_index_definition.json"))

    spark = SparkSession.builder.master("local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    spark.conf.set(f"fs.azure.account.auth.type.{storage_account_name}.dfs.core.windows.net", "OAuth")
    spark.conf.set(f"fs.azure.account.oauth.provider.type.{storage_account_name}.dfs.core.windows.net",
                   "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(f"fs.azure.account.oauth2.client.id.{storage_account_name}.dfs.core.windows.net", args.application_id)
    spark.conf.set(f"fs.azure.account.oauth2.client.secret.{storage_account_name}.dfs.core.windows.net", SERVICE_PRINCIPAL_SECRET)
    spark.conf.set(f"fs.azure.account.oauth2.client.endpoint.{storage_account_name}.dfs.core.windows.net",
                   f"https://login.microsoftonline.com/{args.directory_id}/oauth2/token")

    schema_df = spark.read.option("multiLine", "true").json(
        f"abfss://{index_definition_container_name}@{index_definition_storage_account_name}.dfs.core.windows.net/{index_definition_file_path}")

    index_definition = json.loads(schema_df.toJSON().first())

    index_definition["name"] = args.new_azure_search_index_name

    api_version = '?api-version=2020-06-30'
    headers = {'Content-Type': 'application/json',
               'api-key': client.get_secret(args.azure_search_admin_key_name).value}

    create_index(index_definition)
