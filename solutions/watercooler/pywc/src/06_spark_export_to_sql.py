#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.


"""Processes users profiles and retrieve the necessary fields using Spark

...
"""
import argparse
import json
import os
import sys
from datetime import datetime
from pathlib import Path

from adal import AuthenticationContext
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from pyspark.sql import SparkSession
import jaydebeapi
import pandas as pd


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


def retrieve_latest_run(parent_folder):
    logger.info(f"*******#####*********Retrieve lateste run from folder {parent_folder}")
    list_of_ouput_folder_runs = []
    for entry in os.listdir(parent_folder):
        if os.path.isdir(os.path.join(parent_folder, entry)):
            try:
                int_repr = int(entry)
                list_of_ouput_folder_runs.append(int_repr)
            except Exception:
                pass

    list_of_ouput_folder_runs = sorted(list_of_ouput_folder_runs, reverse=True)
    full_path = os.path.join(parent_folder, str(list_of_ouput_folder_runs[0]))
    return full_path


def export_data_to_azure_sql(spark_args):
    application_id = spark_args.application_id
    directory_id = spark_args.directory_id
    adb_secret_scope = spark_args.adb_secret_scope_name
    adb_sp_client_key_secret_name = spark_args.adb_sp_client_key_secret_name

    employee_profile = spark_args.employee_profile_file_name
    groups_per_day_file = spark_args.groups_per_day_file_name
    groups_per_week = spark_args.groups_per_weeks_file_name
    members_group_personal_meetings = spark_args.members_group_personal_meetings_file_name
    members_to_group_participation = spark_args.members_to_group_participation_file_name

    database = spark_args.jdbc_database
    jdbc_host = spark_args.jdbc_host
    jdbc_port = spark_args.jdbc_port
    use_msi_azure_sql_auth = spark_args.use_msi_azure_sql_auth
    key_vault_url = spark_args.key_vault_url
    jdbc_username_key_name = spark_args.jdbc_username_key_name
    jdbc_password_key_name = spark_args.jdbc_password_key_name
    jdbc_username = spark_args.jdbc_user # this parameter should be used only for running the application locally
    jdbc_password = spark_args.jdbc_password  # this parameter should be used only for running the application locally
    export_batch_size = int(spark_args.export_batch_size)

    base_folder = spark_args.csv_input_data_path

    full_base_path = retrieve_latest_run(base_folder)

    logger.info(f"*******#####Result {full_base_path}")

    employee_profile_file = os.path.join(full_base_path, employee_profile)
    groups_per_day_file = os.path.join(full_base_path, groups_per_day_file)
    groups_per_week_file = os.path.join(full_base_path, groups_per_week)
    members_group_personal_meetings_file = os.path.join(full_base_path, members_group_personal_meetings)
    members_to_group_participation_file = os.path.join(full_base_path, members_to_group_participation)

    client_secret = SERVICE_PRINCIPAL_SECRET if SERVICE_PRINCIPAL_SECRET is not None else \
        dbutils.secrets.get(scope=adb_secret_scope, key=adb_sp_client_key_secret_name)

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
    if jdbc_host is None:
        ValueError("Missing jdbc_host parameter!")
    if jdbc_port is None:
        ValueError("Missing jdbc_port parameter!")
    if use_msi_azure_sql_auth is None:
        ValueError("Missing use_msi_azure_sql_auth parameter!")
    if key_vault_url is None:
        ValueError("Missing key_vault_url parameter!")
    if jdbc_username_key_name is None:
        ValueError("Missing jdbc_username_key_name parameter!")
    if jdbc_password_key_name is None:
        ValueError("Missing jdbc_password_key_name parameter!")
    if jdbc_username is None:
        ValueError("Missing jdbc_username parameter!")
    if jdbc_password is None:
        ValueError("Missing jdbc_password parameter!")
    if export_batch_size is None:
        ValueError("Missing export_batch_size parameter!")

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

        if jdbc_username_key_name and jdbc_password_key_name:
            connectionProperties["user"] = secret_client.get_secret(name=jdbc_username_key_name).value
            connectionProperties["password"] = secret_client.get_secret(name=jdbc_password_key_name).value
        else:
            connectionProperties["user"] = jdbc_username
            connectionProperties["password"] = jdbc_password
            connectionProperties["encrypt"] = "false"

    connection = jaydebeapi.connect("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                                    f"jdbc:sqlserver://{jdbc_host}:{jdbc_port};databaseName={database};",
                                    connectionProperties)

    cursor = connection.cursor()

    # logger.info(f"*******#####Processing {employee_profile_file}")
    # write_employee_profile_to_az_sql(employee_profile_file, export_batch_size, cursor)

    logger.info("*******#####Processing {groups_per_day_file}")
    write_groups_per_day_to_az_sql(groups_per_day_file, export_batch_size, cursor)

    logger.info(f"*******#####Processing {groups_per_week_file}")
    write_groups_per_week_to_az_sql(groups_per_week_file, export_batch_size, cursor)

    logger.info(f"*******#####Processing {members_group_personal_meetings_file}")
    write_members_group_personal_meetings_to_az_sql(members_group_personal_meetings_file, export_batch_size, cursor)

    logger.info(f"*******#####Processing {members_to_group_participation_file}")
    write_members_to_group_participation_to_az_sql(members_to_group_participation_file, export_batch_size, cursor)

    cursor.close()
    connection.close()

def write_groups_per_day_to_az_sql(groups_per_day_file, batch_size, cursor):
    df = pd.read_csv(groups_per_day_file, escapechar="\\")
    number_of_rows = len(df)
    line_count = 0
    insert_command = "INSERT INTO groups_per_day " \
              "(id, [day], hour_time_slot, [hour], group_name, display_name, group_members, timezone_str, timezone_nr) VALUES "
    batch_insert = insert_command
    for line_count in range(1, number_of_rows +1):
        row = df.iloc[line_count-1]
        batch_insert += f""" ('{row["id"]}',  '{row["day"]}', '{row["hour_time_slot"]}', {row["hour"]}, '{row["group_name"]}', '{row["display_name"]}', '{row["group_members"]}', '{row["timezone_str"]}', '{row["timezone_nr"]}'),"""
        if line_count % batch_size == 0:
            batch_insert = batch_insert[:-1]  # remove last comma
            batch_insert = batch_insert + ";"
            batch_insert = batch_insert.replace("""'nan'""", "NULL")
            cursor.execute(batch_insert)
            batch_insert = insert_command

    if line_count % batch_size != 0:
        batch_insert = batch_insert[:-1]  # remove last comma
        batch_insert = batch_insert + ";"
        cursor.execute(batch_insert)

def write_employee_profile_to_az_sql(employee_profile_file, batch_size, cursor):
    df = pd.read_csv(employee_profile_file, escapechar="\\")
    number_of_rows = len(df)
    line_count = 0
    insert_command = "INSERT INTO employee_profile " \
                     "(id, mail, display_name, about_me, job_title, company_name, department, country, office_location, city, state, skills, responsibilities, engagement, image) VALUES "
    batch_insert = insert_command
    for line_count in range(1, number_of_rows +1):
        row = df.iloc[line_count-1]
        batch_insert += f""" ('{row["id"]}', '{row["mail"]}', '{row["display_name"]}', '{row["about_me"]}', '{row["job_title"]}', '{row["company_name"]}', '{row["department"]}', '{row["country"]}', '{row["office_location"]}', '{row["city"]}', '{row["state"]}', '{row["skills"]}', '{row["responsibilities"]}', '{row["engagement"]}', '{row["image"]}'),"""
        if line_count % batch_size == 0:
            batch_insert = batch_insert[:-1]  # remove last comma
            batch_insert = batch_insert + ";"
            batch_insert = batch_insert.replace("""'nan'""", "NULL")
            cursor.execute(batch_insert)
            batch_insert = insert_command

    if line_count % batch_size != 0:
        batch_insert = batch_insert[:-1]  # remove last comma
        batch_insert = batch_insert + ";"
        cursor.execute(batch_insert)

def write_groups_per_week_to_az_sql(groups_per_week_file, batch_size, cursor):
    df = pd.read_csv(groups_per_week_file, escapechar="\\")
    number_of_rows = len(df)
    line_count = 0
    insert_command = "INSERT INTO groups_per_week " \
                     "(id, [day], group_members, timezone_str, timezone_nr) VALUES "
    batch_insert = insert_command
    for line_count in range(1, number_of_rows +1):
        row = df.iloc[line_count-1]
        batch_insert += f""" ('{row["id"]}',  '{row["day"]}', '{row["group_members"]}', '{row["timezone_str"]}', '{row["timezone_nr"]}'),"""
        if line_count % batch_size == 0:
            batch_insert = batch_insert[:-1]  # remove last comma
            batch_insert = batch_insert + ";"
            batch_insert = batch_insert.replace("""'nan'""", "NULL")
            cursor.execute(batch_insert)
            batch_insert = insert_command

    if line_count % batch_size != 0:
        batch_insert = batch_insert[:-1]  # remove last comma
        batch_insert = batch_insert + ";"
        cursor.execute(batch_insert)

def write_members_group_personal_meetings_to_az_sql(members_group_personal_meetings_file, batch_size, cursor):
    df = pd.read_csv(members_group_personal_meetings_file, escapechar="\\")
    number_of_rows = len(df)
    line_count = 0
    insert_command = "INSERT INTO members_group_personal_meetings " \
                     "(id, group_name, watercooler_hour, group_busy_slots, timezone_str, timezone_nr) VALUES "
    batch_insert = insert_command
    for line_count in range(1, number_of_rows +1):
        row = df.iloc[line_count-1]
        batch_insert += f""" ('{row["id"]}',  '{row["group_name"]}', '{row["watercooler_hour"]}', '{row["group_busy_slots"]}', '{row["timezone_str"]}', '{row["timezone_nr"]}'),"""
        if line_count % batch_size == 0:
            batch_insert = batch_insert[:-1]  # remove last comma
            batch_insert = batch_insert + ";"
            batch_insert = batch_insert.replace("""'nan'""", "NULL")
            cursor.execute(batch_insert)
            batch_insert = insert_command

    if line_count % batch_size != 0:
        batch_insert = batch_insert[:-1]  # remove last comma
        batch_insert = batch_insert + ";"
        cursor.execute(batch_insert)

def write_members_to_group_participation_to_az_sql(members_to_group_participation_file, batch_size, cursor):
    df = pd.read_csv(members_to_group_participation_file, escapechar="\\")
    number_of_rows = len(df)
    line_count = 0
    insert_command = "INSERT INTO members_to_group_participation " \
                     "(id, group_name, member_email, [day], day_index, month_index, year_index, hour_time_slot, invitation_status, participation_status, timezone_str, timezone_nr) VALUES "
    batch_insert = insert_command
    for line_count in range(1, number_of_rows +1):
        row = df.iloc[line_count-1]
        batch_insert += f""" ('{row["id"]}',  '{row["group_name"]}', '{row["member_email"]}', '{row["day"]}', '{row["day_index"]}', '{row["month_index"]}', '{row["year_index"]}', '{row["hour_time_slot"]}', '{row["invitation_status"]}', '{row["participation_status"]}', '{row["timezone_str"]}', '{row["timezone_nr"]}'),"""
        if line_count % batch_size == 0:
            batch_insert = batch_insert[:-1]  # remove last comma
            batch_insert = batch_insert + ";"
            batch_insert = batch_insert.replace("""'nan'""", "NULL")
            cursor.execute(batch_insert)
            batch_insert = insert_command

    if line_count % batch_size != 0:
        batch_insert = batch_insert[:-1]  # remove last comma
        batch_insert = batch_insert + ";"
        cursor.execute(batch_insert)

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
        parser = argparse.ArgumentParser(description='Process some integers.')
        parser.add_argument('--storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--application-id', type=str,
                            help='application id')
        parser.add_argument('--directory-id', type=str,
                            help='directory id')
        parser.add_argument('--csv-input-data-path', type=str,
                            help='Input path for csv files')
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
        parser.add_argument('--export-batch-size', type=int,
                            help='Batch size for exporting the data to Azure Sql')
        # for the following parameters, the "required" option is set to False, we assume that the names of the assembled data files
        # and the generated groups output file, will be the default in most of the cases
        parser.add_argument('--members-group-personal-meetings-file-name', type=str, default="members_group_personal_meetings.csv",
                            help='Csv file name for members group personal meetings data', required=False)
        parser.add_argument('--groups-per-weeks-file-name', type=str, default="groups_per_week.csv",
                            help='Csv file name for groups per week data', required=False)
        parser.add_argument('--groups-per-day-file-name', type=str, default="groups_per_day.csv",
                            help='Csv file name for groups per day data', required=False)
        parser.add_argument('--members-to-group-participation-file-name', type=str, default="members_to_group_participation.csv",
                            help='Csv file name for members to group participation data', required=False)
        parser.add_argument('--employee-profile-file-name', type=str, default="employee_profile.csv",
                            help='Csv file name for employee profiles data', required=False)
        parser.add_argument('--jdbc-user', type=str,
                            help='Username for database running on localhost', required=False)
        parser.add_argument('--jdbc-password', type=str,
                            help='Password for database running on localhost', required=False)

        args = parser.parse_args()
        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)

    else:
        from types import SimpleNamespace

        params_path = os.path.expanduser("~/.watercooler/06_spark_export_to_sql_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)

        SERVICE_PRINCIPAL_SECRET = json.load(open("config_test.json"))["SERVICE_PRINCIPAL_SECRET"]


    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[watercooler_groups_exporter]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="WatercoolerGroupsExporter",
                                        log_server_time=True,
                                        name="[watercooler_groups_exporter]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[watercooler_groups_exporter]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    export_data_to_azure_sql(args)
