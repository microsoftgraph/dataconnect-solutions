#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import base64
import dill
import gzip
import json
import logging
import nltk
import os
import random
import sys
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from azure.storage.blob import BlobServiceClient
from datetime import datetime
from nltk.stem.porter import *
from nltk.tokenize import sent_tokenize
from operator import itemgetter
from pyspark import TaskContext
from pyspark.sql import Row
from pyspark.sql import SparkSession
from types import SimpleNamespace

nltk.download('stopwords')
from nltk.corpus import stopwords
from log_analytics_client.logger import LogAnalyticsLogger
from adal import AuthenticationContext
from pyspark.sql.types import StructType, StructField, ArrayType, StringType, BooleanType, LongType

stop_words = list(stopwords.words('english'))
stop_words.extend(
    ['amp', 'count', 'pls', 'super', 'next', 'find', 'help', 'fax', 'unsubscribe', 'nbsp', 'nothing', 'default',
     'using', 'view', 'review', 'from', 'subject', 're', 'edu', 'use', 'com', 'click', 'contact', 'new', 'message',
     'send', 'none', 'support', "this", 'fw', 'cd', 'pro', 'get'])
stop_words = set(stop_words)


class FseWrapper(object):
    fse = None
    ilist = None
    jobs_lookup = None

    @classmethod
    def get(cls):

        if cls.fse is None:
            from azure.storage.blob import BlobClient
            logger.debug("========PREPARING TO DOWNLOAD FSE MODEL")

            storage_account_name = args.storage_account_name
            container_client_credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                                 client_id=args.application_id,
                                                                 client_secret=SERVICE_PRINCIPAL_SECRET)
            blob = BlobClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                              container_name=args.domain_expert_container_name,
                              blob_name=args.fse_folder + "/" + args.fse_file,
                              credential=container_client_credential)

            file_size = blob.get_blob_properties().size

            if os.path.exists(args.fse_file) == False or (
                    os.path.exists(args.fse_file) and os.stat(
                args.fse_file).st_size != file_size):
                with open(args.fse_file, "wb") as my_blob:
                    blob_data = blob.download_blob()
                    blob_data.readinto(my_blob)

            fse, ilist, jobs_lookup = dill.load(gzip.open(args.fse_file, "rb"))
            cls.fse = fse
            cls.ilist = ilist
            cls.jobs_lookup = jobs_lookup

        return cls.fse, cls.ilist, cls.jobs_lookup


def role_to_domain(title):
    title_tokens = [p for p in title.lower().split() if len(p) > 1]
    processed_target = title.replace("Senior", "").replace("Junior", "").replace("Principal", "").strip()
    processed_target = processed_target.replace("Product Manager", "").replace("Program Manager", "").replace(
        "Project Manager", "").strip()
    processed_target = processed_target.replace("Manager", "").replace("Director", "").replace("Executive", "").strip()
    # collapse part:
    if processed_target in ["Data Scientist"]:
        processed_target = "Data Science"

    if processed_target in ["Data Engineer"]:
        processed_target = "Data Science"

    if processed_target in ["Research Scientist"]:
        processed_target = "Data Science"

    if processed_target in ["Web Developer"]:
        processed_target = "Software"

    if "software" in title_tokens or "developer" in title_tokens:
        processed_target = "Software"

    return processed_target


def clean_email_body(html_body: str):
    html_body = html_body.strip()
    html_removed = re.sub('<[^<]+?>', '', html_body).strip()
    html_removed = re.sub('<[^>]*(>|$)|&nbsp;|&zwnj;|&amp;|&raquo;|&laquo;|&gt', ' ', html_removed)
    html_removed = re.sub('\s+', ' ', html_removed).strip()
    html_tokens = [p for p in html_removed.split(" ") if len(p) <= 46]
    html_body = " ".join(html_tokens)
    html_body = str(html_body.encode('ascii', errors='ignore'))
    html_body = re.sub('\s+', ' ', html_body).strip()
    if html_body.startswith("b'") and html_body.endswith("'"):
        html_body = html_body[2:-1]
    return str(html_body)


def estimate_job_from(rec):
    sentence = sent_tokenize(rec["Content"])

    fse, ilist, jobs_lookup = FseWrapper.get()
    matches = fse.sv.similar_by_sentence(sentence, model=fse, indexable=ilist.items,
                                         topn=10)
    role_to_max_score = {}
    role_counters = {}
    roles_to_score = {}
    roles_found = set()
    for item in matches:
        # word_list = item[0]
        index = item[1]
        score = item[2]
        role = jobs_lookup[index]
        role_to_max_score[role] = score  # append to score
        role_counters[role] = 1
        roles_to_score[role] = score
        roles_found.add(role)

    rec["role_to_max_score"] = role_to_max_score
    rec["role_counters"] = role_counters
    rec["roles_to_score"] = roles_to_score
    # print(rec)
    del rec["Content"]
    del rec["Subject"]
    del rec["Date"]
    return rec


def reduceByKeyAndCombine(accumulator: dict = {}, record: dict = {}):
    # logger.debug("===============debug reduce")
    # logger.debug(f"acumulator {acumulator}")
    # logger.debug(f"record {record}")
    for key in record.keys():
        if key not in ["role_to_max_score", "role_counters", "roles_to_score"]:
            accumulator[key] = record[key]

        if key in ["role_to_max_score"]:
            if key in accumulator:
                for role, max_score in record["role_to_max_score"].items():
                    if role in accumulator["role_to_max_score"]:
                        accumulator["role_to_max_score"][role] = max(accumulator["role_to_max_score"][role], max_score)
                    else:
                        accumulator["role_to_max_score"][role] = max_score
            else:
                accumulator[key] = record[key]

        if key in ["role_counters"]:
            if key in accumulator:
                for role, role_score in record["role_counters"].items():
                    if role in accumulator["role_counters"]:
                        accumulator["role_counters"][role] += role_score
                    else:
                        accumulator["role_counters"][role] = role_score
            else:
                accumulator[key] = record[key]

        if key in ["roles_to_score"]:
            if key in accumulator:
                for role, role_score_val in record["roles_to_score"].items():
                    if role in accumulator["roles_to_score"]:
                        accumulator["roles_to_score"][role] += role_score_val
                    else:
                        accumulator["roles_to_score"][role] = role_score_val
            else:
                accumulator[key] = record[key]

    # logger.debug(f"Acumm - AFTER: {acumulator}")

    return dict(accumulator)


def process_line_spark(line):
    all_records = []
    try:
        json_dict = line

        if "From" not in json_dict:
            return []

        if json_dict["From"] is None:
            return []

        if json_dict["From"]["EmailAddress"] is None:
            return []

        if json_dict["From"]["EmailAddress"]["Address"] is None:
            return []

        strip_date_time = datetime.strptime(json_dict["SentDateTime"], "%Y-%m-%dT%H:%M:%SZ")
        email_date_azure_formatted = strip_date_time.strftime("%Y-%m-%dT%H:%M:%S.%fZ")

        internet_message_id = base64.urlsafe_b64encode(json_dict["InternetMessageId"].encode("ascii")).decode(
            "ascii").rstrip("=")
        rec_dict = {
            "From": json_dict["From"]["EmailAddress"]["Address"].lower().strip(),
            "Content": json_dict["UniqueBody"]["Content"],
            "Subject": json_dict["Subject"],
            "Date": email_date_azure_formatted,
            "internet_message_id": internet_message_id
        }

        # TODO parametrize this shuffle param
        rand_shuffle_key = random.randint(0, 7)

        if rec_dict["From"] is None or len(rec_dict["From"].strip()) == 0:
            return []

        rec_dict["Content"] = clean_email_body(rec_dict["Content"])

        if len(rec_dict["Content"]) < 50:
            return []

        rec_dict = estimate_job_from(rec_dict)

        all_records.append((rec_dict["From"], rec_dict))

    except Exception as ex:
        # TODO: re-add this
        logger.exception(f"Exception encountered on json", ex)
        logger.debug(str(ex))
        # logger.debug(line)
        return []

    return all_records


def removeRandKey(rec_with_key):
    composed_key = rec_with_key[0]
    record = rec_with_key[1]
    return composed_key[0], record


def computeFinalRoleScores(rec_with_key):
    rec = rec_with_key[1]

    role_counters = rec["role_counters"]
    role_to_max_score = rec["role_to_max_score"]
    roles_to_score = rec["roles_to_score"]

    new_rec = {}

    final_roles_found = set()

    # here we determine the role that occurs most of the times
    for counter in range(0, 3):
        new_rec["frequent_role_" + str(counter)] = ""
        new_rec["doc_count_" + str(counter)] = 0.0

    new_rec["total_docs"] = sum([p[1] for p in list(role_counters.items())])

    if len(role_counters.items()):
        role_counters_sorted = sorted(list(role_counters.items()), key=lambda x: x[1], reverse=True)
        counter = 0
        for role, doc_count in role_counters_sorted:
            new_rec["frequent_role_" + str(counter)] = role
            final_roles_found.add(role)
            new_rec["doc_count_" + str(counter)] = doc_count
            counter += 1
            if counter >= 3:
                break

    # here we determine the role based on the highest score
    for counter in range(0, 3):
        new_rec["highest_score_role_" + str(counter)] = ""
        new_rec["highest_score_score_" + str(counter)] = 0.0

    if len(role_to_max_score.items()):
        highest_score_role_sorted = sorted(list(role_to_max_score.items()), key=itemgetter(1), reverse=True)
        counter = 0
        for role, highest_score in highest_score_role_sorted:
            new_rec["highest_score_role_" + str(counter)] = role
            new_rec["highest_score_score_" + str(counter)] = highest_score
            final_roles_found.add(role)
            counter += 1
            if counter >= 3:
                break

    new_rec["From"] = rec["From"]
    new_rec["id"] = base64.urlsafe_b64encode(new_rec["From"].encode("ascii")).decode("ascii").rstrip("=")

    final_scores = []
    for role, total_score in roles_to_score.items():
        if role not in role_counters: continue
        counter = role_counters[role]
        if counter == 0:
            counter = 1
        final_score = (total_score * 1.0) / (counter * 1.0)
        final_scores.append((role, final_score))

    final_scores = sorted(final_scores, key=lambda x: x[1], reverse=True)

    for counter in range(0, 3):
        new_rec["role_proposal_" + str(counter)] = ""
        new_rec["score_proposal_" + str(counter)] = 0.0

    counter = 0
    for role, score in final_scores:
        new_rec["role_proposal_" + str(counter)] = role
        new_rec["score_proposal_" + str(counter)] = score
        final_roles_found.add(role)
        counter += 1
        if counter >= 3:
            break

    new_rec["all_roles_found"] = list(final_roles_found)

    return new_rec


def process_spark_partitions(partition):
    ctx = TaskContext()
    nltk.download('punkt')
    logger.info("start_processing_partition partitionId=" + str(ctx.partitionId()))
    all_records = []
    for entry in partition:
        if entry["From"] is not None:
            all_records.extend(process_line_spark(entry))

    final_dict = dict()
    for key, rec in all_records:
        if key not in final_dict:
            final_dict[key] = rec
            continue
        current_rec = final_dict[key]
        final_dict[key] = reduceByKeyAndCombine(current_rec, rec)

    all_final_records = []
    for key, rec in final_dict.items():
        all_final_records.append(((key, 1), rec))
    logger.info(
        f"end_processing_partition partitionId={str(ctx.partitionId())}. processed: {len(all_final_records)} records")
    return all_final_records


def process_spark_partitions_local(partition):
    nltk.download('punkt')
    all_records = []
    for entry in partition:
        all_records.extend(process_line_spark(entry))
    return all_records


def process_local(local_file):
    """

    :param local_file: mail dump in json format
    """
    all_results = []
    with open(local_file) as f:
        for line in f.readlines():
            record = json.loads(line)
            results = process_spark_partitions_local([record])
            all_results.extend(results)
            print(results)
    print(computeFinalRoleScores((0, reduceByKeyAndCombine(all_results[0][1], all_results[1][1]))))


logger = None
args = None
SERVICE_PRINCIPAL_SECRET = None


def str2bool(v):
    if isinstance(v, bool):
        return v
    if v.lower() in ('yes', 'true', 't', 'y', '1'):
        return True
    elif v.lower() in ('no', 'false', 'f', 'n', '0'):
        return False
    else:
        raise argparse.ArgumentTypeError('Boolean value expected.')


if __name__ == '__main__':
    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process some integers.')
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
        parser.add_argument('--domain-expert-container-name', type=str,  # TODO: change this
                            help='domain expert container name')
        parser.add_argument('--taxonomy-folder', type=str,
                            help='taxonomy folder path')
        parser.add_argument('--taxonomy-file', type=str,
                            help='taxonomy file name')
        parser.add_argument('--fse-folder', type=str,
                            help='fse folder path')
        parser.add_argument('--fse-file', type=str,
                            help='fse file name')
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
        parser.add_argument('--new-data-version', type=str,
                            help='new inferred roles data version ')
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
        args = SimpleNamespace(
            storage_account_name="",  # Fill this with appropriate value
            input_container_name="",  # Fill this with appropriate value
            input_folder_path="simulated_data/m365_emails",
            domain_expert_container_name="domain-experts",
            taxonomy_folder="domain_experts/taxo",
            taxonomy_file="taxo_v3_k50_n123456.dill.gz",
            fse_folder="domain_experts/fse",
            # fse_file="gensim-fse-m1-s50-w5-mn5-i20_v3.dill.gz",
            fse_file="gensim-fse-m1-s300-w3-mn5-i20_v2.dill.gz",
            application_id="",  # Fill this with appropriate value
            directory_id="",  # Fill this with appropriate value
            adb_secret_scope_name="gdc",
            adb_sp_client_key_secret_name="",
            log_analytics_workspace_id=" ",
            log_analytics_workspace_key_name="log-analytics-api-key",
            key_vault_url="",
            jdbc_host="",
            jdbc_port="",
            jdbc_database="gdc_database_test",
            new_data_version="2020-11-17 16:58:53.871898",
            jdbc_username_key_name="azure-sql-backend-user",
            jdbc_password_key_name="azure-sql-backend-password",
            use_msi_azure_sql_auth=False
        )
        SERVICE_PRINCIPAL_SECRET = json.load(open("config.json"))["SERVICE_PRINCIPAL_SECRET"]

    service_principal_credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                          client_id=args.application_id,
                                                          client_secret=SERVICE_PRINCIPAL_SECRET)
    client = SecretClient(vault_url=args.key_vault_url, credential=service_principal_credential)

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[mails_to_role_processor]")
    else:
        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="MailsToRoleProcessor",
                                        log_server_time=True,
                                        name="[mails_to_role_processor]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[mails_to_role_processor]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    storage_account_name = args.storage_account_name
    input_container = args.input_container_name
    input_folder = args.input_folder_path
    application_id = args.application_id
    directory_id = args.directory_id
    adbSecretScope = args.adb_secret_scope_name
    adbSPClientKeySecretName = args.adb_sp_client_key_secret_name
    database = args.jdbc_database
    jdbcHost = args.jdbc_host
    jdbcPort = args.jdbc_port

    client_secret = SERVICE_PRINCIPAL_SECRET



    spark = SparkSession.builder.master("local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    spark.conf.set(f"fs.azure.account.auth.type.{storage_account_name}.dfs.core.windows.net", "OAuth")
    spark.conf.set(f"fs.azure.account.oauth.provider.type.{storage_account_name}.dfs.core.windows.net",
                   "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(f"fs.azure.account.oauth2.client.id.{storage_account_name}.dfs.core.windows.net", application_id)
    spark.conf.set(f"fs.azure.account.oauth2.client.secret.{storage_account_name}.dfs.core.windows.net",
                   SERVICE_PRINCIPAL_SECRET)
    spark.conf.set(f"fs.azure.account.oauth2.client.endpoint.{storage_account_name}.dfs.core.windows.net",
                   f"https://login.microsoftonline.com/{directory_id}/oauth2/token")

    spark.sparkContext.setJobGroup("Running mail processing", f"[mails_to_role_processor]14_21")

    logger = logging.getLogger("[mails_to_role_processor]")
    logger.info("[mails_to_role_processor] Preparing the for role extraction")

    container_client_credential = ClientSecretCredential(tenant_id=directory_id, client_id=application_id,
                                                         client_secret=client_secret)
    blob_service_client = BlobServiceClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                                            credential=service_principal_credential)
    inputContainerClient = blob_service_client.get_container_client(input_container)

    input_folder_name = input_folder
    list_of_json_files_from_folder = []
    if input_folder_name.startswith("/"):
        input_folder_name = input_folder_name[1:]

    for entry in inputContainerClient.list_blobs():
        if entry.name.startswith(input_folder_name + "/") and entry.size and entry.name.lower().endswith("json") > 0:
            list_of_json_files_from_folder.append(entry.name)

    last_output_full_path = ""
    logger.info("inferred_roles_version = " + str(args.new_data_version))

    finalEmployeesDf = None

    email_schema = StructType([StructField("BccRecipients",ArrayType(StructType([StructField("EmailAddress",StructType([StructField("Address",StringType(),True),StructField("Name",StringType(),True)]),True)]),True),True),
                         StructField("Categories",ArrayType(StringType(),True),True),
                         StructField("CcRecipients",ArrayType(StructType([StructField("EmailAddress",StructType([StructField("Address",StringType(),True),StructField("Name",StringType(),True)]),True)]),True),True),
                         StructField("ChangeKey",StringType(),True),
                         StructField("ConversationId",StringType(),True),
                         StructField("CreatedDateTime",StringType(),True),
                         StructField("From",StructType([StructField("EmailAddress",StructType([StructField("Address",StringType(),True),StructField("Name",StringType(),True)]),True)]),True),
                         StructField("HasAttachments",BooleanType(),True),
                         StructField("Id",StringType(),True),
                         StructField("Importance",StringType(),True),
                         StructField("InternetMessageId",StringType(),True),
                         StructField("IsDeliveryReceiptRequested",BooleanType(),True),
                         StructField("IsDraft",BooleanType(),True),
                         StructField("IsRead",BooleanType(),True),
                         StructField("IsReadReceiptRequested",BooleanType(),True),
                         StructField("LastModifiedDateTime",StringType(),True),
                         StructField("ODataType",StringType(),True),
                         StructField("ParentFolderId",StringType(),True),
                         StructField("ReceivedDateTime",StringType(),True),
                         StructField("ReplyTo",ArrayType(StructType([StructField("EmailAddress",StructType([StructField("Address",StringType(),True),StructField("Name",StringType(),True)]),True)]),True),True),
                         StructField("Sender",StructType([StructField("EmailAddress",StructType([StructField("Address",StringType(),True),StructField("Name",StringType(),True)]),True)]),True),
                         StructField("SentDateTime",StringType(),True),
                         StructField("Subject",StringType(),True),
                         StructField("ToRecipients",ArrayType(StructType([StructField("EmailAddress",StructType([StructField("Address",StringType(),True),StructField("Name",StringType(),True)]),True)]),True),True),
                         StructField("UniqueBody",StructType([StructField("Content",StringType(),True),StructField("ContentType",StringType(),True)]),True),
                         StructField("WebLink",StringType(),True),
                         StructField("datarow",LongType(),True),
                         StructField("pagerow",LongType(),True),
                         StructField("ptenant",StringType(),True),
                         StructField("puser",StringType(),True),
                         StructField("pAdditionalInfo",StringType(),True),
                         StructField("rowinformation",StructType([StructField("isUserSummaryRow",BooleanType(),True),StructField("userHasCompleteData",BooleanType(),True),StructField("userReturnedNoData",BooleanType(),True), StructField("errorInformation",StringType(),True)]),True),
                         StructField("userrow",LongType(),True)])

    for index, json_file_to_process in enumerate(list_of_json_files_from_folder):
        logger.info(f"processing: {json_file_to_process}")
        wasb_file_path = f"abfss://{input_container}@{storage_account_name}.dfs.core.windows.net/{json_file_to_process}"
        logger.info(f"input wasb_file_path: {wasb_file_path}")

        input_df = spark.read.json(wasb_file_path, schema=email_schema)

        # spark_res_rdd = input_df.rdd.mapPartitions(self.process_spark_partitions)  # .map(lambda x: str(x))

        if finalEmployeesDf is None:
            finalEmployeesDf = input_df
        else:
            finalEmployeesDf = finalEmployeesDf.union(input_df)

    spark_res_rdd = finalEmployeesDf.rdd. \
        coalesce(8). \
        mapPartitions(process_spark_partitions). \
        reduceByKey(reduceByKeyAndCombine). \
        map(removeRandKey). \
        map(computeFinalRoleScores)

    spark_res_df = spark_res_rdd.map(lambda x: Row(**x)).toDF()

    logger.info(f"Writing inferred roles from {json_file_to_process} to Azure Sql")

    from pyspark.sql.functions import lit

    employeeInferredRolesDf = spark_res_df.select("id", "From", "doc_count_0", "doc_count_1", "doc_count_2",
                                                  "frequent_role_0", "frequent_role_1",
                                                  "frequent_role_2", "highest_score_role_0", "highest_score_role_1",
                                                  "highest_score_role_2",
                                                  "highest_score_score_0", "highest_score_score_1",
                                                  "highest_score_score_2", "role_proposal_0",
                                                  "role_proposal_1", "role_proposal_2", "score_proposal_0",
                                                  "score_proposal_1", "score_proposal_2",
                                                  "total_docs") \
        .withColumnRenamed("From", "email") \
        .withColumn("version", lit(args.new_data_version))

    connectionProperties = {}

    if args.use_msi_azure_sql_auth:
        stsurl = "https://login.microsoftonline.com/" + directory_id
        auth_context = AuthenticationContext(stsurl)
        tokenObj = auth_context.acquire_token_with_client_credentials("https://database.windows.net/",
                                                                      application_id,
                                                                      client_secret)
        accessToken = tokenObj['accessToken']

        connectionProperties = {'accessToken': accessToken, 'databaseName': database,
                                'url': jdbcHost,
                                'hostNameInCertificate': '*.database.windows.net', 'encrypt': 'true',
                                'Driver': 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
                                'ServerCertificate': 'false', 'trustServerCertificate': 'false',
                                'loginTimeout': '30'}
    else:
        service_principal_credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                              client_id=args.application_id,
                                                              client_secret=SERVICE_PRINCIPAL_SECRET)
        client = SecretClient(vault_url=args.key_vault_url, credential=service_principal_credential)

        connectionProperties = {'user': client.get_secret(name=args.jdbc_username_key_name).value,
                                'password': client.get_secret(name=args.jdbc_password_key_name).value}

    jdbcUrl = f"jdbc:sqlserver://{jdbcHost}:{jdbcPort};database={database};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

    employeeInferredRolesDf.write.jdbc(url=jdbcUrl, table="inferred_roles", mode="append",
                                       properties=connectionProperties)
