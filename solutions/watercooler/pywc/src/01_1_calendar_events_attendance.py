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
from pathlib import Path


def process_local_job(folder_path_to_user_calendar_files):
    """Process calendar entry files line by line

    :param folder_path_to_user_calendar_files: folder path to target calendar extracted files files
    :type folder_path_to_user_calendar_files: str
    """
    input_records = []
    with open(folder_path_to_user_calendar_files + 'raw_input_events.json') as f:
        for line in f.readlines():
            rec = json.loads(line.strip())
            input_records.append(rec)

    all_rec = process_spark_partitions_local(input_records)
    with open(folder_path_to_user_calendar_files + 'out.json', 'w+') as f:
        for rec in all_rec:
            f.write(json.dumps(rec) + '\n')


def process_spark_partitions_local(partition):
    """
    :param partition:
    :type partition:
    :return:
    :rtype:
    """
    logger.info("start_processing_partitionId=0")
    all_records = []
    for entry in partition:
        all_records.extend(process_line_spark(entry))
    logger.info(f"end_processing_partition partitionId=0. processed: {len(all_records)} records")
    return all_records


def run_spark_job(spark_args):
    """
    :param spark_args:
    :type spark_args:
    :return:
    :rtype:
    """
    storage_account_name = spark_args.storage_account_name
    input_container = spark_args.input_container_name
    output_container = spark_args.output_container_name
    input_folder = spark_args.input_folder_path
    output_folder = spark_args.output_folder_path
    application_id = spark_args.application_id
    directory_id = spark_args.directory_id
    adb_secret_scope = spark_args.adb_secret_scope_name
    adb_sp_client_key_secret_name = spark_args.adb_sp_client_key_secret_name
    database = args.jdbc_database
    jdbcHost = args.jdbc_host
    jdbcPort = args.jdbc_port

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

    spark.sparkContext.setJobGroup("Running calendar processing", f"[calendar_events_attendance_extractor]")

    logger.info("[calendar_events_attendance_extractor] Preparing the jobs for analyzing the calendar data")
    container_client_credential = ClientSecretCredential(tenant_id=directory_id, client_id=application_id,
                                                         client_secret=client_secret)

    blob_service_client = BlobServiceClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                                            credential=container_client_credential)

    container_client = blob_service_client.get_container_client(input_container)

    if input_container is not None and input_folder is not None:

        input_folder_name = input_folder
        list_of_json_files_from_folder = []
        if input_folder_name.startswith("/"):
            input_folder_name = input_folder_name[1:]

        # next will determine the latest folder to be used
        for entry in container_client.list_blobs(name_starts_with=input_folder_name + "/"):
            list_of_json_files_from_folder.append(entry.name)

        collected_folders = []
        for entry in list_of_json_files_from_folder:
            if "events_" not in entry: continue
            tokens = entry.split("/")
            standard_folder_name = tokens[1]
            try:
                tag = int(standard_folder_name.replace("events_", ""))
                collected_folders.append(tag)
            except Exception as e:
                pass
        if not len(collected_folders):
            raise Exception("Could not retrieve the latest run")
        collected_folders.sort(reverse=True)
        input_folder_name = input_folder_name + "/" + "events_" + str(collected_folders[0])

        wasb_file_path = f"abfss://{input_container}@{storage_account_name}.dfs.core.windows.net/{input_folder_name}"
        logger.info(f"[calendar_events_attendance_extractor] input wasb_file_path: {wasb_file_path}")

        input_df = spark.read.json(wasb_file_path)

        input_df = input_df.filter(input_df.subject.contains("Watercooler Event"))

        spark_res_rdd = input_df.rdd.mapPartitions(process_spark_partitions)

        if not spark_res_rdd.isEmpty():
            spark_res_df = spark_res_rdd.map(lambda x: Row(**x)).toDF()

            from pyspark.sql.functions import max as max_

            grouped_events_df = spark_res_df.groupBy("groupDisplayName","email_address").agg(max_("lastModifiedDateTime").alias("lastModifiedDateTime"))

            grouped_events_df = grouped_events_df.join(spark_res_df, ["groupDisplayName", "email_address", "lastModifiedDateTime"], "inner")

            table = f"""
            (SELECT day, group_name, display_name
            FROM [{database}].dbo.groups_per_day) foo
            """

            groups_per_day_df = generate_dataframe_from_table(spark, spark_args, table)

            events_attendance_df = grouped_events_df.join(groups_per_day_df, grouped_events_df.groupDisplayName == groups_per_day_df.display_name, "inner")

            events_attendance_df = events_attendance_df.drop("groupDisplayName")

            write_attendance_info(input_df=events_attendance_df,
                                  output_folder=output_folder,
                                  output_container=output_container,
                                  storage_account_name=storage_account_name,
                                  container_client=container_client)


    else:
        raise ValueError(
            "Not enough arguments given in order to read input data: jdbc-database & input-container are missing.")


def write_attendance_info(input_df,
                          output_folder,
                          output_container,
                          storage_account_name,
                          container_client):

    wasb_output_file_path = f"abfss://{output_container}@{storage_account_name}." \
                            f"dfs.core.windows.net/{output_folder}"
    logger.info(f"[calendar_events_attendance_extractor] output wasb_file_path: {wasb_output_file_path}")

    input_df.write.mode("overwrite").json(wasb_output_file_path)


def process_spark_partitions(partition):
    """
    :param partition:
    :type partition:
    :return:
    :rtype:
    """
    ctx = TaskContext()
    logger.info("start_processing_partitionId=" + str(ctx.partitionId()))
    all_records = []
    for entry in partition:
        all_records.extend(process_line_spark(entry))
    logger.info(f"end_processing_partition partitionId={str(ctx.partitionId())}. processed: {len(all_records)} records")
    return all_records


def process_line_spark(json_dict_rec):
    """
    :param json_dict_rec:
    :type json_dict_rec:
    :return:
    :rtype:
    """
    all_records = []

    logger.info(f"Processing: {json_dict_rec}")
    try:
        try:
            json_dict_rec = json_dict_rec.asDict()
        except Exception as ex:
            logger.exception("Error encountered processing entry.", ex=ex)
            pass

        for attendee in json_dict_rec['attendees']:
            json_dict = dict()
            json_dict['groupDisplayName'] = json_dict_rec["subject"].replace(" Watercooler Event", "")
            json_dict['email_address'] = attendee["emailAddress"]["address"]
            json_dict['lastModifiedDateTime'] = json_dict_rec["lastModifiedDateTime"]
            if "none" in attendee["status"]["response"]:
                json_dict['invitation_status'] = 2
            elif "notResponded" in attendee["status"]["response"]:
                json_dict['invitation_status'] = 2
            elif "tentativelyAccepted" in attendee["status"]["response"]:
                json_dict['invitation_status'] = 1
            elif "accepted" in attendee["status"]["response"]:
                json_dict['invitation_status'] = 1
            elif "declined" in attendee["status"]["response"]:
                json_dict['invitation_status'] = 0
            else:
                json_dict['invitation_status'] = 2
            all_records.append(json_dict)

        # print(json_dict)
        logger.info(f"processed: {json_dict}")

    except Exception as ex:
        trace = traceback.format_exc()
        logger.exception(f"Exception encountered on json", ex, trace)
        print(ex)
        print(trace)
        return []

    return all_records


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
        from types import SimpleNamespace

        params_path = os.path.expanduser("~/.watercooler/01_1_calendar_events_attendance_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)
        SERVICE_PRINCIPAL_SECRET = json.load(open("config.json"))["SERVICE_PRINCIPAL_SECRET"]

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[calendar_events_attendance_extractor]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="CalendarEventsAttendanceExtractor",
                                        log_server_time=True,
                                        name="[calendar_events_attendance_extractor]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[calendar_events_attendance_extractor]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    # process_local_job('/Users/borleaandrei/projects/watercooler_stuff/m365_events/')
    run_spark_job(args)
