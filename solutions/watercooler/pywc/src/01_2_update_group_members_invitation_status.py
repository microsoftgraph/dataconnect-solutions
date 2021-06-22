#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import os
import sys
import traceback
from datetime import datetime

from adal import AuthenticationContext
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from azure.storage.blob import BlobServiceClient
from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from pyspark import TaskContext
from pyspark.sql import Row
from pyspark.sql import SparkSession
import jaydebeapi
from pathlib import Path


def run_spark_job(spark_args):
    """
    :param spark_args:
    :type spark_args:
    :return:
    :rtype:
    """
    storage_account_name = spark_args.storage_account_name
    input_container = spark_args.input_container_name
    input_folder = spark_args.input_folder_path
    application_id = spark_args.application_id
    directory_id = spark_args.directory_id
    adb_secret_scope = spark_args.adb_secret_scope_name
    adb_sp_client_key_secret_name = spark_args.adb_sp_client_key_secret_name
    database = args.jdbc_database
    jdbcHost = args.jdbc_host
    jdbcPort = args.jdbc_port
    jdbc_username_key_name = spark_args.jdbc_username_key_name
    jdbc_password_key_name = spark_args.jdbc_password_key_name
    use_msi_azure_sql_auth = spark_args.use_msi_azure_sql_auth
    batch_size = spark_args.update_batch_size

    if storage_account_name is None:
        ValueError("Missing storage_account_name parameter!")
    if input_container is None:
        ValueError("Missing input_container parameter!")
    if input_folder is None:
        ValueError("Missing input_folder parameter!")
    if application_id is None:
        ValueError("Missing application_id parameter!")
    if directory_id is None:
        ValueError("Missing directory_id parameter!")
    if adb_secret_scope is None:
        ValueError("Missing adb_secret_scope_name parameter!")
    if adb_sp_client_key_secret_name is None:
        ValueError("Missing adb_sp_client_key_secret_name parameter!")
    if database is None:
        ValueError("Missing database parameter!")
    if jdbcHost is None:
        ValueError("Missing jdbcHost parameter!")
    if jdbcPort is None:
        ValueError("Missing jdbcPort parameter!")

    client_secret = SERVICE_PRINCIPAL_SECRET if SERVICE_PRINCIPAL_SECRET is not None else \
        dbutils.secrets.get(scope=adb_secret_scope, key=adb_sp_client_key_secret_name)

    # conf  = SparkConf().setAppName("CalendarProcesor").setMaster("local")
    spark = SparkSession.builder.master("local").getOrCreate()
    # spark = SparkSession.builder.config(conf).getOrCreate()
    # spark.conf.set("spark.app.name", "CalendarProcesor")
    spark.sparkContext.setLogLevel("ERROR")

    spark.conf.set(f"fs.azure.account.auth.type.{storage_account_name}.dfs.core.windows.net", "OAuth")
    spark.conf.set(f"fs.azure.account.oauth.provider.type.{storage_account_name}.dfs.core.windows.net",
                   "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(f"fs.azure.account.oauth2.client.id.{storage_account_name}.dfs.core.windows.net", application_id)
    spark.conf.set(f"fs.azure.account.oauth2.client.secret.{storage_account_name}.dfs.core.windows.net", client_secret)
    spark.conf.set(f"fs.azure.account.oauth2.client.endpoint.{storage_account_name}.dfs.core.windows.net",
                   f"https://login.microsoftonline.com/{directory_id}/oauth2/token")

    spark.sparkContext.setJobGroup("Running calendar processing", f"[calendar_information_extractor]")

    logger.info("[calendar_information_extractor] Preparing the jobs for analyzing the calendar data")
    container_client_credential = ClientSecretCredential(tenant_id=directory_id, client_id=application_id,
                                                         client_secret=client_secret)

    blob_service_client = BlobServiceClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                                            credential=container_client_credential)

    container_client = blob_service_client.get_container_client(input_container)

    if input_container is not None and input_folder is not None:

        input_folder_name = input_folder

        wasb_file_path = f"abfss://{input_container}@{storage_account_name}.dfs.core.windows.net/{input_folder_name}"
        logger.info(f"[calendar_information_extractor] input wasb_file_path: {wasb_file_path}")

        input_df = spark.read.json(wasb_file_path)

        connectionProperties = {'databaseName': database,
                                'url': 'watercooler-sql-wt.database.windows.net',
                                'hostNameInCertificate': '*.database.windows.net', 'encrypt': 'true',
                                'Driver': 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
                                'ServerCertificate': 'false', 'trustServerCertificate': 'false',
                                'loginTimeout': '30'}
        if use_msi_azure_sql_auth:
            sts_url = "https://login.microsoftonline.com/" + directory_id
            auth_context = AuthenticationContext(sts_url)
            token_obj = auth_context.acquire_token_with_client_credentials("https://database.windows.net/",
                                                                           application_id,
                                                                           client_secret)
            access_token = token_obj['accessToken']
            connectionProperties['accessToken'] = access_token

        else:
            service_principal_credential = ClientSecretCredential(tenant_id=spark_args.directory_id,
                                                                  client_id=spark_args.application_id,
                                                                  client_secret=SERVICE_PRINCIPAL_SECRET)
            secret_client = SecretClient(vault_url=spark_args.key_vault_url, credential=service_principal_credential)

            connectionProperties["user"] = secret_client.get_secret(name=jdbc_username_key_name).value
            connectionProperties["password"] = secret_client.get_secret(name=jdbc_password_key_name).value

        connection = jaydebeapi.connect("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                                        f"jdbc:sqlserver://{jdbcHost}:{jdbcPort};databaseName={database};",
                                        connectionProperties)
        cursor = connection.cursor()

        attendance_rows = input_df.collect()

        index = 0
        command = ""
        for attendance_row in attendance_rows:
            index += 1
            command += f"update members_to_group_participation set invitation_status={attendance_row.invitation_status} where group_name='{attendance_row.group_name}' and member_email='{attendance_row.email_address}'; "
            if index % batch_size == 0:
                cursor.execute(command)
                command = ""
                index = 0

        if index != 0:
            cursor.execute(command)

        cursor.close()
        connection.close()

    else:
        raise ValueError(
            "Not enough arguments given in order to read input data: jdbc-database & input-container are missing.")


def str2bool(v):
    """Transforms string flag into boolean

    :param v: boolean as type or string
    :type v: str
    :return: bool or argparse error (if it's not recognized)
    :rtype: bool
    """
    if isinstance(v, bool):
        return v
    if v.lower() in ('yes', 'true', 't', 'y', '1'):
        return True
    elif v.lower() in ('no', 'false', 'f', 'n', '0'):
        return False
    else:
        raise argparse.ArgumentTypeError('Boolean value expected.')

logger = None
args = None
SERVICE_PRINCIPAL_SECRET = None

if __name__ == '__main__':
    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process Arguments')
        parser.add_argument('--storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--input-container-name', type=str,
                            help='input container name')
        parser.add_argument('--input-folder-path', type=str,
                            help='input folder path')
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
        parser.add_argument('--jdbc-host', type=str,
                            help='jdbc host')
        parser.add_argument('--jdbc-port', type=str,
                            help='jdbc port')
        parser.add_argument('--jdbc-database', type=str,
                            help='database name')
        parser.add_argument('--jdbc-username-key-name', type=str,
                            help='The name of the Azure Key Vault secret that contains the jdbc username')
        parser.add_argument('--jdbc-password-key-name', type=str,
                            help='The name of the Azure Key Vault secret that contains the jdbc password')
        parser.add_argument('--use-msi-azure-sql-auth', type=str2bool,
                            help='Use Managed Service Identity (MSI) to authenticate into AzureSql or use user and password read from KeyVault instead')
        parser.add_argument('--update-batch-size', type=int,
                            help='Batch size for exporting the data to Azure Sql')

        args = parser.parse_args()
        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)
    else:
        from types import SimpleNamespace

        params_path = os.path.expanduser("~/.watercooler/01_2_update_group_members_invitation_status_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)
        SERVICE_PRINCIPAL_SECRET = json.load(open("config.json"))["SERVICE_PRINCIPAL_SECRET"]

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[update_group_members_invitiation_status]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="UpdateGroupMembersInvitationStatus",
                                        log_server_time=True,
                                        name="[update_group_members_invitiation_status]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[update_group_members_invitiation_status]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    run_spark_job(args)
