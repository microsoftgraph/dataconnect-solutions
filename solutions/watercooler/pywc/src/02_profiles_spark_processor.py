#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import os
import sys
from adal import AuthenticationContext
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from datetime import datetime
from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from pyspark.sql import SparkSession
from pathlib import Path
from types import SimpleNamespace
import json


def generate_dataframe_from_table(spark, spark_args, table):
    application_id = spark_args.application_id
    directory_id = spark_args.directory_id
    adb_secret_scope = spark_args.adb_secret_scope_name
    adb_sp_client_key_secret_name = spark_args.adb_sp_client_key_secret_name
    database = spark_args.jdbc_database
    jdbc_host = spark_args.jdbc_host
    jdbc_port = spark_args.jdbc_port
    jdbc_username_key_name = spark_args.jdbc_username_key_name
    jdbc_password_key_name = spark_args.jdbc_password_key_name
    use_msi_azure_sql_auth = spark_args.use_msi_azure_sql_auth

    client_secret = SERVICE_PRINCIPAL_SECRET if SERVICE_PRINCIPAL_SECRET is not None else dbutils.secrets.get(
        scope=adb_secret_scope, key=adb_sp_client_key_secret_name)

    df_constructor = spark.read.format("jdbc") \
        .option("url", f"jdbc:sqlserver://{jdbc_host}:{jdbc_port};databaseName={database};") \
        .option("dbtable", table) \
        .option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver") \
        .option("hostNameInCertificate", "*.database.windows.net") \
        .option("encrypt", "true") \
        .option("ServerCertificate", "false") \
        .option("trustServerCertificate", "false") \
        .option("loginTimeout", "30")

    if use_msi_azure_sql_auth:
        sts_url = "https://login.microsoftonline.com/" + directory_id
        auth_context = AuthenticationContext(sts_url)
        token_obj = auth_context.acquire_token_with_client_credentials("https://database.windows.net/",
                                                                       application_id,
                                                                       client_secret)
        access_token = token_obj['accessToken']
        df_constructor.option("accessToken", access_token)
    else:
        service_principal_credential = ClientSecretCredential(tenant_id=spark_args.directory_id,
                                                              client_id=spark_args.application_id,
                                                              client_secret=SERVICE_PRINCIPAL_SECRET)
        secret_client = SecretClient(vault_url=spark_args.key_vault_url, credential=service_principal_credential)

        df_constructor.option("user", secret_client.get_secret(name=jdbc_username_key_name).value)
        df_constructor.option("password", secret_client.get_secret(name=jdbc_password_key_name).value)

    df = df_constructor.load()
    return df


def run_spark_job(spark_args):
    """
    :param spark_args:
    :type spark_args:
    :return:
    :rtype:
    """
    storage_account_name = spark_args.storage_account_name
    output_container = spark_args.output_container_name
    output_folder = spark_args.output_folder_path
    application_id = spark_args.application_id
    directory_id = spark_args.directory_id
    adb_secret_scope = spark_args.adb_secret_scope_name

    adb_sp_client_key_secret_name = spark_args.adb_sp_client_key_secret_name

    if storage_account_name is None:
        ValueError("Missing storage_account_name parameter!")
    if output_container is None:
        ValueError("Missing output_container_name parameter!")
    if output_folder is None:
        ValueError("Missing output_folder_path parameter!")
    if application_id is None:
        ValueError("Missing application_id parameter!")
    if directory_id is None:
        ValueError("Missing directory_id parameter!")
    if adb_secret_scope is None:
        ValueError("Missing adb_secret_scope_name parameter!")
    if adb_sp_client_key_secret_name is None:
        ValueError("Missing adb_sp_client_key_secret_name parameter!")

    client_secret = SERVICE_PRINCIPAL_SECRET if SERVICE_PRINCIPAL_SECRET is not None else \
        dbutils.secrets.get(scope=adb_secret_scope, key=adb_sp_client_key_secret_name)

    spark = SparkSession.builder.master("local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    spark.conf.set(f"fs.azure.account.auth.type.{storage_account_name}.dfs.core.windows.net", "OAuth")
    spark.conf.set(f"fs.azure.account.oauth.provider.type.{storage_account_name}.dfs.core.windows.net",
                   "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(f"fs.azure.account.oauth2.client.id.{storage_account_name}.dfs.core.windows.net", application_id)
    spark.conf.set(f"fs.azure.account.oauth2.client.secret.{storage_account_name}.dfs.core.windows.net", client_secret)
    spark.conf.set(f"fs.azure.account.oauth2.client.endpoint.{storage_account_name}.dfs.core.windows.net",
                   f"https://login.microsoftonline.com/{directory_id}/oauth2/token")

    spark.sparkContext.setJobGroup("Running profiles extractor", f"[profiles_spark_processor]")

    logger.info("[profiles_spark_processor] Preparing the jobs for retrieving and processing the profiles")

    database = spark_args.jdbc_database
    jdbc_host = spark_args.jdbc_host
    jdbc_port = spark_args.jdbc_port

    print(f"Database: ", database)
    print(f"Host: ", jdbc_host)
    print(f"Port: ", jdbc_port)

    table = f"""
        (SELECT id, version, mail, display_name, about_me, job_title, 
        company_name, department, country, office_location, 
        city, state, skills, responsibilities, engagement, image
        FROM [{database}].dbo.employee_profile) foo
        """

    input_df = generate_dataframe_from_table(spark, spark_args, table)

    out_file_name = str(datetime.now().strftime("%Y%m%d%H%M"))
    out_file_full_path = os.path.join(output_folder, out_file_name)

    wasb_output_file_path = f"abfss://{output_container}@{storage_account_name}." \
                            f"dfs.core.windows.net/{out_file_full_path}"
    logger.info(f"[profiles_spark_processor] output wasb_file_path: {wasb_output_file_path}")

    input_df.write.mode("overwrite").json(wasb_output_file_path)

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
    print(sys.argv)

    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process Arguments')
        parser.add_argument('--storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--output-container-name', type=str,
                            help='output container name')
        parser.add_argument('--output-folder-path', type=str,
                            help='output folder path')
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

        args = parser.parse_args()
        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)

    else:

        params_path = os.path.expanduser("~/.watercooler/02_profiles_spark_processor_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)

        SERVICE_PRINCIPAL_SECRET = json.load(open("config_test.json"))["SERVICE_PRINCIPAL_SECRET"]

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[profiles_information_extractor]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="ProfilesInformationExtractor",
                                        log_server_time=True,
                                        name="[profiles_information_extractor]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[profiles_information_extractor]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    run_spark_job(args)

