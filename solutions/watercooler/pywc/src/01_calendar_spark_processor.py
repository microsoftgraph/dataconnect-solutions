#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import traceback

import argparse
# import logging, sys
import json
import os
import sys
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from azure.storage.blob import BlobServiceClient
from datetime import datetime
from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from pyspark import TaskContext
from pyspark.sql import Row
from pyspark.sql import SparkSession
from pathlib import Path
from types import SimpleNamespace

def process_line_spark(json_dict_rec):
    """
    :param json_dict_rec:
    :type json_dict_rec:
    :return:
    :rtype:
    """
    all_records = []

    required_fields = ["puser", "originalStartTimeZone", "originalEndTimeZone", "responseStatus", "start", "end",
                       "attendees", "responseStatus", "organizer"]
    logger.info(f"Processing: {json_dict_rec}")
    try:
        try:
            json_dict_rec = json_dict_rec.asDict()
        except Exception as ex:
            logger.exception("Error encountered processing entry.", ex=ex)
            pass

        json_dict = dict()
        for key in required_fields:
            param_val = json_dict_rec[key]
            if param_val is None:
                param_val = ""
            json_dict[key] = param_val

        json_dict["isOnlineMeeting"] = ""
        if "isOnlineMeeting" in json_dict_rec:
            json_dict["isOnlineMeeting"] = json_dict_rec["isOnlineMeeting"]

        # print(json_dict)
        logger.info(f"processed: {json_dict}")
        all_records.append(json_dict)
    except Exception as ex:
        trace = traceback.format_exc()
        logger.exception(f"Exception encountered on json", ex, trace)
        print(ex)
        print(trace)
        return []

    return all_records


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
        logger.info(f"[calendar_information_extractor] input wasb_file_path: {wasb_file_path}")

        input_df = spark.read.json(wasb_file_path)

        extract_essential_calendar_info_and_write_to_output(input_df=input_df,
                                                            output_folder=output_folder,
                                                            output_container=output_container,
                                                            storage_account_name=storage_account_name,
                                                            container_client=container_client)
    else:
        raise ValueError(
            "Not enough arguments given in order to read input data: jdbc-database & input-container are missing.")


def extract_essential_calendar_info_and_write_to_output(input_df,
                                                        output_folder,
                                                        output_container,
                                                        storage_account_name,
                                                        container_client):

    spark_res_rdd = input_df.rdd.mapPartitions(process_spark_partitions)

    out_file_name = str(datetime.now().strftime("%Y%m%d%H%M"))
    out_file_full_path = os.path.join(output_folder, out_file_name)

    wasb_output_file_path = f"abfss://{output_container}@{storage_account_name}." \
                            f"dfs.core.windows.net/{out_file_full_path}"
    logger.info(f"[calendar_information_extractor] output wasb_file_path: {wasb_output_file_path}")

    spark_res_df = spark_res_rdd.map(lambda x: Row(**x)).toDF()
    spark_res_df.write.mode("overwrite").json(wasb_output_file_path)

    list_of_files_to_clean = []
    for entry in container_client.list_blobs(name_starts_with=out_file_full_path + "/"):
        if entry.name.lower().endswith("json") is False:
            logger.debug("detected file to delete: " + str(entry.name))
            list_of_files_to_clean.append(entry.name)

logger = None
args = None
SERVICE_PRINCIPAL_SECRET = None

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

if __name__ == '__main__':
    print(sys.argv)

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

        args = parser.parse_args()
        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)

    else:
        params_path = os.path.expanduser("~/.watercooler/01_calendar_spark_processor_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)

        SERVICE_PRINCIPAL_SECRET = json.load(open("config_test.json"))["SERVICE_PRINCIPAL_SECRET"]

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[calendar_information_extractor]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="CalendarInformationExtractor",
                                        log_server_time=True,
                                        name="[calendar_information_extractor]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[calendar_information_extractor]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    try:
        run_spark_job(args)
    except Exception as e:
        logger.error("Error processing retrieved calendar events. They might be missing or check for other type of error. " + str(e))

