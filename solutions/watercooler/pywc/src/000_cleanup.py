#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import os
import sys
from pathlib import Path

from adal import AuthenticationContext
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from types import SimpleNamespace
import jaydebeapi


def cleanup_database(spark_args):
    client_secret = SERVICE_PRINCIPAL_SECRET
    database = args.jdbc_database
    jdbcHost = args.jdbc_host
    jdbcPort = args.jdbc_port
    jdbc_username_key_name = spark_args.jdbc_username_key_name
    jdbc_password_key_name = spark_args.jdbc_password_key_name
    use_msi_azure_sql_auth = spark_args.use_msi_azure_sql_auth
    application_id = spark_args.application_id
    directory_id = spark_args.directory_id

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

    truncate_groups_per_day = "truncate table groups_per_day;"
    truncate_groups_per_week = "truncate table groups_per_week;"
    truncate_members_group_personal_meetings = "truncate table members_group_personal_meetings;"
    members_to_group_participation = "truncate table members_to_group_participation;"


    cursor.execute(truncate_groups_per_day)
    logger.info("Truncated groups_per_day table.")

    cursor.execute(truncate_groups_per_week)
    logger.info("Truncated groups_per_week table.")

    cursor.execute(truncate_members_group_personal_meetings)
    logger.info("Truncated members_group_personal_meetings table.")

    cursor.execute(members_to_group_participation)
    logger.info("Truncated members_to_group_participation table.")

    cursor.close()
    connection.close()

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
    """TODO
    """
    print(sys.argv)

    if len(sys.argv) > 2:  # and false was necessary for debugging in adbfs
        parser = argparse.ArgumentParser(description='Process some integers.')
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

        params = json.load(open(Path('./000_cleanup_params.json').absolute()))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)

        # TODO: should be in defaults ?
        # args = SimpleNamespace(
        # log_analytics_workspace_id=" ",  # "b61e5e81-9eb2-413e-aaef-624b89af04a0",

        SERVICE_PRINCIPAL_SECRET = json.load(open("config_test.json"))["SERVICE_PRINCIPAL_SECRET"]

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[cleanup_azure_sql]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="CleanupAzureSql",
                                        log_server_time=True,
                                        name="[cleanup_azure_sql]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[cleanup_azure_sql]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    cleanup_database(args)



