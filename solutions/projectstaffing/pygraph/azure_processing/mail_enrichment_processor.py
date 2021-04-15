import argparse
import json
# import logging, requests, hashlib, hmac
import os
import sys
import uuid
from pathlib import Path

from azure.identity import ClientSecretCredential
from azure.storage.blob import BlobServiceClient
from datetime import datetime
from nltk.stem.porter import *
# TODO: remove this lines when configured from production
from pyspark.sql import Row
from pyspark.sql import SparkSession
import base64
from types import SimpleNamespace
from azure.keyvault.secrets import SecretClient
from pyspark import TaskContext
import traceback
# --- ?

from log_analytics_client.logger import LogAnalyticsLogger

# from pygraph_utils.taxonomies_pipeline.modules.utils import open_stopwords
from taxo_utils.data.stopwords import get_stopwords_hardcoded
stop_words = get_stopwords_hardcoded()

from taxo_utils.utils import str2bool
from taxo_utils.nlp_utils import SpacyMagic, GensimMagic, TaxonomyWrapper
from taxo_utils.nlp_utils import domain_to_field_name


# from taxo_utils.nlp_utils import create_bigrams, create_trigrams
# from taxo_utils.nlp_utils import remove_stop_words, lemmatization
# from taxo_utils.nlp_utils import extract_lemma_as_list, extract_bigrams_as_list, extract_trigrams_as_list
from taxo_utils.nlp_utils import extract_lemma_as_text, extract_bigrams_as_text, extract_trigrams_as_text, GensimMagic
from taxo_utils.data.stopwords import get_stopwords_hardcoded
stop_words = get_stopwords_hardcoded()
from taxo_utils.nlp_utils import remove_stop_words

from taxo_utils.nlp_utils import NLPUtils
# lemmatization in profiles
# def lemmatization(text, allowed_postags=['NOUN', 'PROPN', 'ADJ'])

# originally
# def lemmatization(text, allowed_postags=['NOUN', 'ADJ', 'VERB', 'ADV', "PROPN", "NUM"]):
# used in last version -> allowed_postags=['NOUN']


from taxo_utils.nlp_utils import clean_email_body


def get_tokens_for_text(input_text):
    """

    :param input_text:
    :type input_text:
    :return:
    :rtype:
    """
    tokenized_array = [p.lower() for p in re.split('[^a-zA-Z]', input_text) if len(p)]
    tokenized_array = [p for p in tokenized_array if p.lower() not in stop_words and len(p) > 1 and (p[0].isnumeric() or p[0].isalpha())]

    reconstructed_text = " ".join(tokenized_array)
    bigrams = extract_bigrams_as_text(reconstructed_text)
    trigrams = extract_trigrams_as_text(reconstructed_text)

    input_text_lemma = extract_lemma_as_text(input_text, allowed_postags=['NOUN'])
    bigrams_lemma = extract_bigrams_as_text(input_text_lemma)
    trigrams_lemma = extract_trigrams_as_text(input_text_lemma)

    final_bigrams = [p for p in bigrams_lemma.split(",") if len(p.strip())]
    final_bigrams.extend([p for p in bigrams.split(",") if len(p.strip())])

    final_trigrams = [p for p in trigrams_lemma.split(",") if len(p.strip())]
    final_trigrams.extend([p for p in trigrams.split(",") if len(p.strip())])

    final_words = []
    final_words_set = set()
    for token in tokenized_array:
        if token not in final_words_set:
            final_words.append(token)
            final_words_set.add(token)

    for token in final_bigrams:
        if token not in final_words_set:
            final_words.append(token)
            final_words_set.add(token)

    for token in final_trigrams:
        if token not in final_words_set:
            final_words.append(token)
            final_words_set.add(token)

    final_words_lemma = []
    final_words_set_lemma = set()

    for token in input_text_lemma.split(" "):
        if not len(token.strip()):
            continue
        if token not in final_words_set_lemma:
            final_words_lemma.append(token)
            final_words_set_lemma.add(token)

    for token in bigrams_lemma.split(","):
        if not len(token.strip()):
            continue
        if token not in final_words_set_lemma:
            final_words_lemma.append(token)
            final_words_set_lemma.add(token)

    for token in trigrams_lemma.split(","):
        if not len(token.strip()):
            continue
        if token not in final_words_set_lemma:
            final_words_lemma.append(token)
            final_words_set_lemma.add(token)

    return final_words, final_words_lemma


def curate_list(input_list, words_list):
    """

    :param input_list:
    :type input_list:
    :param words_list:
    :type words_list:
    :return:
    :rtype:
    """
    final_list = []
    for token in input_list:
        if len(token.strip()) == 0:
            continue
        if token.strip() in words_list:
            final_list.append(token.strip())
    return final_list


def enrich(record_dict, big_taxo, de_model, de_vocab, words_list):
    """

    :param record_dict:
    :type record_dict:
    :param big_taxo:
    :type big_taxo:
    :param de_model:
    :type de_model:
    :param de_vocab:
    :type de_vocab:
    :param words_list:
    :type words_list:
    :return:
    :rtype:
    """

    """
    Right now content_text_tokens contain also the lemma parts
    """

    utils = NLPUtils(args, SERVICE_PRINCIPAL_SECRET, logger)

    content_text_tokens, all_lemma_tokens = get_tokens_for_text(record_dict["Content"])

    currated_content = curate_list(content_text_tokens, words_list)
    all_lemma_tokens = curate_list(all_lemma_tokens, words_list)

    currated_content_set = set(currated_content)

    #TODO: rename the method extract_role_to_terms to extract_domain_to_terms
    domain_to_role_to_terms = utils.extract_role_to_terms(currated_content, big_taxo)

    for domain, role_to_terms in domain_to_role_to_terms.items():
        de_field_name, de_field_name_mapping = domain_to_field_name(domain)
        str_mapping_representation = ";".join([p[0] + ":" + ",".join(p[1]) for p in list(role_to_terms.items())])
        record_dict[de_field_name_mapping] = str_mapping_representation

        all_associated_terms = set()
        for associate_term_list in role_to_terms.values():
            all_associated_terms = all_associated_terms.union(set(associate_term_list))

        all_associated_terms = [p for p in list(all_associated_terms) if p not in currated_content_set]
        record_dict[de_field_name] = ",".join(list(all_associated_terms))

    # TODO: drop lemma, since we are also searching for this type of terms, we can automatically drop them
    record_dict["lemma_body"] = ",".join(all_lemma_tokens)

    record_dict["Content"] = " ".join(currated_content)
    record_dict["Content_v2"] = " ".join(currated_content)

    return record_dict


def process_line_spark(line, big_taxo, de_model, de_vocab, words_list):
    """

    :param line:
    :type line:
    :param big_taxo:
    :type big_taxo:
    :param de_model:
    :type de_model:
    :param de_vocab:
    :type de_vocab:
    :param words_list:
    :type words_list:
    :return:
    :rtype:
    """
    all_records = []

    try:
        json_dict = line

        if "From" not in json_dict:
            return []

        if json_dict["From"] is None:
            return []

        if "EmailAddress" not in json_dict["From"]:
            return []

        if json_dict["From"]["EmailAddress"] is None:
            return []

        if "Address" not in json_dict["From"]["EmailAddress"]:
            return []

        if json_dict["From"]["EmailAddress"]["Address"] is None:
            return []

        if "UniqueBody" not in json_dict:
            return []

        if "ToRecipients" not in json_dict:
            return []

        if json_dict["ToRecipients"] is None:
            return []

        if "Subject" not in json_dict:
            return []

        strip_date_time = datetime.strptime(json_dict["SentDateTime"], "%Y-%m-%dT%H:%M:%SZ")
        email_date_azure_formatted = strip_date_time.strftime("%Y-%m-%dT%H:%M:%S.%fZ")

        mail_id = json_dict["Id"]
        internet_message_id = base64.urlsafe_b64encode(json_dict["InternetMessageId"].encode("ascii")).decode(
            "ascii").rstrip(
            "=")
        rec_dict = {
            "From": json_dict["From"]["EmailAddress"]["Address"].lower().strip(),
            "TargetUser": json_dict["From"]["EmailAddress"]["Address"].lower().strip(),
            "ToRecipients": [
                p["EmailAddress"]["Address"].lower().strip() for p in json_dict["ToRecipients"] if
                    "EmailAddress" in p and
                    "Address" in p["EmailAddress"] and
                    p["EmailAddress"]["Address"] is not None
            ],
            "Content": json_dict["UniqueBody"]["Content"],
            "Subject": json_dict["Subject"],
            "Date": email_date_azure_formatted,
            "id": str(uuid.uuid4()),
            "mail_id": mail_id,
            "internet_message_id": internet_message_id
        }

        rec_dict["ToRecipients"] = ",".join(p for p in rec_dict["ToRecipients"] if len(p))
        rec_dict["Content"] = clean_email_body(rec_dict["Content"])
        rec_dict["Content_v2"] = clean_email_body(rec_dict["Content"])

        if len(rec_dict["Content"]) < 2:
            return []

        all_involved_actors = [rec_dict["TargetUser"]]
        all_involved_actors.append(rec_dict["ToRecipients"])
        rec_dict["actors"] = ",".join(all_involved_actors)

        rec_dict = enrich(rec_dict, big_taxo, de_model, de_vocab, words_list)
        all_records.append(rec_dict)

    except Exception as ex:
        # TODO: re-add this
        trace = traceback.format_exc()
        logger.exception(f"Exception encountered on json", ex, trace)
        print("Exception",ex)
        print("Exception", trace)
        # logger.debug(line)
        return []
    # print("done!")
    return all_records


def process_spark_partitions(partition):
    """

    :param partition:
    :type partition:
    :return:
    :rtype:
    """
    ctx = TaskContext()
    logger.info("start_processing_partition partitionId=" + str(ctx.partitionId()))

    big_taxo = TaxonomyWrapper.get(args, SERVICE_PRINCIPAL_SECRET, logger)
    gensim_model = GensimMagic.get(args, SERVICE_PRINCIPAL_SECRET, logger)  # move this to process_partitions
    de_vocab = gensim_model["vocab"]  # move this to process_partitions
    de_model = gensim_model["model"]  # move this to process_partitions

    words_list = set(de_vocab.keys())
    for domain, domain_dict in big_taxo.items():
        words_list = words_list.union(set(domain_dict.keys()))

    all_records = []
    for entry in partition:
        all_records.extend(process_line_spark(entry, big_taxo, de_model, de_vocab, words_list))
    logger.info(f"end_processing_partition partitionId={str(ctx.partitionId())}. processed: {len(all_records)} records")
    return all_records


def process_local_partitions(partition):
    """

    :param partition:
    :type partition:
    :return:
    :rtype:
    """
    logger.info("start_processing_partition partitionId=0")
    big_taxo = TaxonomyWrapper.get(args, SERVICE_PRINCIPAL_SECRET, logger)
    gensim_model = GensimMagic.get(args, SERVICE_PRINCIPAL_SECRET, logger)  # move this to process_partitions
    de_vocab = gensim_model["vocab"]  # move this to process_partitions
    de_model = gensim_model["model"]  # move this to process_partitions
    words_list = set(de_vocab.keys())
    for domain, domain_dict in big_taxo.items():
        words_list = words_list.union(set(domain_dict.keys()))

    all_records = []
    for entry in partition:
        all_records.extend(process_line_spark(entry, big_taxo, de_model, de_vocab, words_list))
    logger.info(f"end_processing_partition partitionId=0. processed: {len(all_records)} records")
    return all_records


def process_local(local_file):
    """

    :param local_file: mail dump as json
    :return:
    :rtype:
    """
    with open(local_file) as f:
        for line in f.readlines():
            record = json.loads(line)
            results = process_local_partitions([record])
            print(results)


logger = None
args = None
SERVICE_PRINCIPAL_SECRET = None

if __name__ == '__main__':
    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process some integers.')

        parser.add_argument('--storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--output-container-name', type=str,
                            help='output container name')
        parser.add_argument('--output-folder-path', type=str,
                            help='output folder path')
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
        parser.add_argument('--domain-expert-container-name', type=str,
                            help='domain expert container name')
        parser.add_argument('--domain-expert-folder-path', type=str,
                            help='domain expert folder path')
        parser.add_argument('--domain-expert-file-name', type=str,
                            help='domain expert file name')
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
        params = json.load(open(Path('./mail_enrichment_params.json').absolute()))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)

        # TODO: should be in defaults ?
        # args = SimpleNamespace(
        #    log_analytics_workspace_id=" ",  # "b61e5e81-9eb2-413e-aaef-624b89af04a0",

        SERVICE_PRINCIPAL_SECRET = json.load(open("config.json"))["SERVICE_PRINCIPAL_SECRET"]

    service_principal_credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                          client_id=args.application_id,
                                                          client_secret=SERVICE_PRINCIPAL_SECRET)
    client = SecretClient(vault_url=args.key_vault_url, credential=service_principal_credential)

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[mail_json_processor]")
    else:
        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="MailEnrichmentProcessor",
                                        log_server_time=True,
                                        name="[mail_json_processor]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[mail_json_processor]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    storage_account_name = args.storage_account_name
    output_container = args.output_container_name
    input_container = args.input_container_name
    input_folder = args.input_folder_path  # "deduplicated"
    output_folder = args.output_folder_path  # "to_index_azure3"
    application_id = args.application_id
    directory_id = args.directory_id
    adbSecretScope = args.adb_secret_scope_name
    adbSPClientKeySecretName = args.adb_sp_client_key_secret_name

    # process_local()
    # exit(0)

    spark = SparkSession.builder.master("local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    spark.conf.set(f"fs.azure.account.auth.type.{storage_account_name}.dfs.core.windows.net", "OAuth")
    spark.conf.set(f"fs.azure.account.oauth.provider.type.{storage_account_name}.dfs.core.windows.net",
                   "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(f"fs.azure.account.oauth2.client.id.{storage_account_name}.dfs.core.windows.net", application_id)
    spark.conf.set(f"fs.azure.account.oauth2.client.secret.{storage_account_name}.dfs.core.windows.net", SERVICE_PRINCIPAL_SECRET)
    spark.conf.set(f"fs.azure.account.oauth2.client.endpoint.{storage_account_name}.dfs.core.windows.net",
                   f"https://login.microsoftonline.com/{directory_id}/oauth2/token")

    spark.sparkContext.setJobGroup("Running mail processing", f"[mail_json_processor]")

    logger.info("Preparing the jobs for data lemmatization and augmentation")

    blob_service_client = BlobServiceClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                                            credential=service_principal_credential)
    outputContainerClient = blob_service_client.get_container_client(output_container)
    inputContainerClient = blob_service_client.get_container_client(input_container)

    input_folder_name = input_folder
    list_of_json_files_from_folder = []
    if input_folder_name.startswith("/"):
        input_folder_name = input_folder_name[1:]

    for entry in inputContainerClient.list_blobs():
        if entry.name.startswith(input_folder_name + "/") and entry.size and entry.name.lower().endswith("json") > 0:
            list_of_json_files_from_folder.append(entry.name)

    last_output_full_path = ""
    for index, json_file_to_process in enumerate(list_of_json_files_from_folder):
        logger.info(f"processing: {json_file_to_process}")
        wasb_file_path = f"abfss://{input_container}@{storage_account_name}.dfs.core.windows.net/{json_file_to_process}"
        logger.info(f"input wasb_file_path: {wasb_file_path}")

        input_df = spark.read.json(wasb_file_path)

        # spark_res_rdd = input_df.rdd.mapPartitions(self.process_spark_partitions)  # .map(lambda x: str(x))

        spark_res_rdd = input_df.rdd.coalesce(8).mapPartitions(process_spark_partitions)

        out_file_name = f"out_{str(index).zfill(4)}" + str(datetime.now().strftime("%Y_%m_%d_%H_%M"))
        out_file_full_path = os.path.join(output_folder, out_file_name)

        wasb_output_file_path = f"abfss://{output_container}@{storage_account_name}.dfs.core.windows.net/{out_file_full_path}"
        logger.info(f"output wasb_file_path: {wasb_output_file_path}")
        # spark_res_rdd.saveAsTextFile(wasb_output_file_path)
        spark_res_df = spark_res_rdd.map(lambda x: Row(**x)).toDF()
        spark_res_df.write.mode("overwrite").json(wasb_output_file_path)

        list_of_files_to_clean = []
        for entry in outputContainerClient.list_blobs(name_starts_with=out_file_full_path + "/"):
            if entry.name.lower().endswith("json") is False:
                logger.debug(f"detected file to delete: {entry.name}")
                list_of_files_to_clean.append(entry.name)

        for file_to_del in list_of_files_to_clean:
            blobclient = outputContainerClient.get_blob_client(blob=file_to_del)
            logger.debug(f"Delete {file_to_del}")
            blobclient.delete_blob()

            last_output_full_path = out_file_full_path
