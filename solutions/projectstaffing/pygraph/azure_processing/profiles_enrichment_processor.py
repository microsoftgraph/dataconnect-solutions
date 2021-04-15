"""Enrich source M365 profiles on Spark / locally

...
"""
import argparse
# import logging, sys
import base64
import json
import os
import sys
import traceback
from adal import AuthenticationContext
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient
from azure.storage.blob import BlobServiceClient
from collections import OrderedDict
from datetime import datetime
from log_analytics_client.logger import LogAnalyticsLogger
from nltk.stem.porter import *
from pathlib import Path
from pyspark import TaskContext
# TODO: remove this lines when configured from production
from pyspark.sql import Row
from pyspark.sql import SparkSession
# from pygraph_utils.taxonomies_pipeline.modules.utils import open_stopwords
from taxo_utils.data.stopwords import get_stopwords_hardcoded

# from nltk.stem.porter import *
# ---
stop_words = get_stopwords_hardcoded()
from taxo_utils.nlp_utils import remove_stop_words

from taxo_utils.nlp_utils import NLPUtils

from taxo_utils.utils import str2bool
from taxo_utils.nlp_utils import GensimMagic, TaxonomyWrapper
from taxo_utils.nlp_utils import clean_email_body
from taxo_utils.nlp_utils import domain_to_field_name

# from taxo_utils.nlp_utils import create_bigrams, create_trigrams, lemmatization
from taxo_utils.nlp_utils import extract_lemma_as_text, extract_bigrams_as_text, extract_trigrams_as_text


def currate(tokens):
    """Ensure that each token found is found in some form in the domain expert vocabulary

    For each token in token list, keep only if these terms match domain expert vocabulary:
    + term
    + term with dashes instead of spaces
    + term without spaces

    :param tokens: tokens found
    :type tokens: list
    :return: curated token list
    :rtype: list
    """
    model = GensimMagic.get(args, SERVICE_PRINCIPAL_SECRET, logger)
    de_vocab = model["vocab"]

    allread_processed_set = set()
    final_list = []

    for term in tokens:
        term = term.strip().lower()
        if not len(term):
            continue
        if term in de_vocab and term not in allread_processed_set:
            allread_processed_set.add(term)
            final_list.append(term)

        new_term1 = term.replace(" ", "-")
        new_term2 = term.replace(" ", "")
        if new_term1 in de_vocab and \
                term not in allread_processed_set and \
                new_term1 not in allread_processed_set:
            allread_processed_set.add(new_term1)
            allread_processed_set.add(term)
            final_list.append(term)

        if new_term2 in de_vocab and \
                term not in allread_processed_set and \
                new_term2 not in allread_processed_set:
            allread_processed_set.add(new_term2)
            allread_processed_set.add(term)
            final_list.append(term)

    return final_list


def extract_tokens(input_text):
    """Classic token extraction by regex/strip, drops stopwords from token list

    :param input_text: source text
    :type input_text: str
    :return: space-separated token string
    :rtype: str
    """
    # TODO: this should be changed
    lemma_tokens = [p for p in re.split('[^a-zA-Z]', input_text) if len(p.strip())]
    clean_tokens = remove_stop_words(lemma_tokens)
    return " ".join(clean_tokens)


def get_tokens_for_text(input_text):
    """
    TODO:

    :param input_text: source text
    :type input_text: str
    :return: final_words, final_words_lemma
    :rtype: list, list
    """
    tokenized_array = [p.lower() for p in re.split('[^a-zA-Z]', input_text) if len(p)]
    tokenized_array = [p for p in tokenized_array if
                       p.lower() not in stop_words and len(p) > 1 and (p[0].isnumeric() or p[0].isalpha())]

    reconstructed_text = " ".join(tokenized_array)
    bigrams = extract_bigrams_as_text(reconstructed_text)
    trigrams = extract_trigrams_as_text(reconstructed_text)

    input_text_lemma = extract_lemma_as_text(input_text, logger=logger)
    bigrams_lemma = extract_bigrams_as_text(input_text_lemma)
    trigrams_lemma = extract_trigrams_as_text(input_text_lemma)

    final_bigrams_lemma = [p for p in bigrams_lemma.split(",") if len(p.strip())]
    final_bigrams = [p for p in bigrams.split(",") if len(p.strip())]
    final_bigrams.extend(final_bigrams_lemma)

    final_trigrams_lemma = [p for p in trigrams_lemma.split(",") if len(p.strip())]
    final_trigrams = [p for p in trigrams.split(",") if len(p.strip())]
    final_trigrams.extend(final_trigrams_lemma)

    final_words = []
    final_words_set = set()
    for token in tokenized_array:
        if token not in final_words_set:
            final_words.append(token)
            final_words_set.add(token)

    for token in final_bigrams:
        if not len(token.strip()):
            continue
        if token not in final_words_set:
            final_words.append(token)
            final_words_set.add(token)

    for token in final_trigrams:
        if not len(token.strip()):
            continue
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

    for token in final_bigrams_lemma:
        if not len(token.strip()):
            continue
        if token not in final_words_set_lemma:
            final_words_lemma.append(token)
            final_words_set_lemma.add(token)

    for token in final_trigrams_lemma:
        if not len(token.strip()):
            continue
        if token not in final_words_set_lemma:
            final_words_lemma.append(token)
            final_words_set_lemma.add(token)

    return final_words, final_words_lemma


def role_to_domain(title):
    """
    TODO:

    :param title:
    :type title:
    :return:
    :rtype:
    """
    title_tokens = [p for p in title.lower().split() if len(p) > 1]
    processed_target = title.replace("Senior", "").replace("Junior", "").replace("Principal", "").strip()
    processed_target = processed_target.replace("Product Manager", "").replace("Program Manager", "").replace("Project Manager", "").strip()
    processed_target = processed_target.replace("Manager", "").replace("Director", "").replace("Executive", "").strip()
    # collapse part:
    if processed_target in ["Data Scientist"]:
        # processed_target = "Data Science"
        processed_target = "Software"

    if processed_target in ["Data Engineer"]:
        # processed_target = "Data Science"
        processed_target = "Software"

    if processed_target in ["Research Scientist"]:
        # processed_target = "Data Science"
        processed_target = "Software"

    if processed_target in ["Web Developer"]:
        processed_target = "Software"

    if "software" in title_tokens or "developer" in title_tokens:
        processed_target = "Software"

    return processed_target


def enrich(record_dict):
    """
    TODO:

    :param record_dict:
    :type record_dict:
    :return:
    :rtype:
    """
    utils = NLPUtils(args, SERVICE_PRINCIPAL_SECRET, logger)

    big_taxo = TaxonomyWrapper.get(args, SERVICE_PRINCIPAL_SECRET, logger)
    gensim_model = GensimMagic.get(args, SERVICE_PRINCIPAL_SECRET, logger)

    all_tokens = []
    about_me_text = record_dict["clean_about_me"]
    about_me_tokens, about_me_lemma_tokens = get_tokens_for_text(about_me_text)

    all_tokens.extend(about_me_tokens)
    all_tokens.extend(about_me_lemma_tokens)

    skill_text = record_dict["skills"]
    skill_tokens, skill_tokens_lemma = get_tokens_for_text(skill_text)

    all_tokens.extend(skill_tokens)
    all_tokens.extend(skill_tokens_lemma)

    responsibilities_text = record_dict["responsibilities"]
    responsibilities_tokens, responsibilities_tokens_lemma = get_tokens_for_text(responsibilities_text)

    all_tokens.extend(responsibilities_tokens)
    all_tokens.extend(responsibilities_tokens_lemma)

    all_tokens_set = set(all_tokens)
    final_token_set = set()
    final_tokens = []
    for token in all_tokens:
        if token not in final_token_set:
            final_token_set.add(token)
            final_tokens.append(token)

    domain_to_role_to_terms = utils.extract_role_to_terms(all_tokens, big_taxo)

    for domain, role_to_terms in domain_to_role_to_terms.items():
        de_field_name, de_field_name_mapping = domain_to_field_name(domain)
        str_mapping_representation = ";".join([p[0] + ":" + ",".join(p[1]) for p in list(role_to_terms.items())])
        record_dict[de_field_name_mapping] = str_mapping_representation

        all_associated_terms = set()
        for associate_term_list in role_to_terms.values():
            all_associated_terms = all_associated_terms.union(set(associate_term_list))

        all_associated_terms = [p for p in list(all_associated_terms) if p not in all_tokens_set]
        record_dict[de_field_name] = ",".join(list(all_associated_terms))

    del record_dict["clean_about_me"]

    record_dict["about_me_lemma"] = extract_lemma_as_text(record_dict["about_me"], logger=logger)
    record_dict["skills_lemma"] = extract_lemma_as_text(record_dict["skills"], logger=logger)
    record_dict["responsibilities_lemma"] = extract_lemma_as_text(record_dict["responsibilities"], logger=logger)

    # record_dict["profile_lemma"] = " ".join(
    #   skill_tokens_lemma + about_me_lemma_tokens + responsibilities_tokens_lemma
    # )

    # record_dict["profile"] = " ".join(final_tokens)
    # record_dict["profile_v2"] = " ".join(final_tokens)

    # this is necessary for the profile skills currated
    # record_dict["currated_all"] = ",".join(curate_list(skill_tokens))
    record_dict["currated_all"] = []
    record_dict["currated_all"].extend(currate(skill_tokens))
    record_dict["currated_all"].extend(currate(about_me_tokens))
    record_dict["currated_all"].extend(currate(responsibilities_tokens))
    record_dict["currated_all"] = ",".join(list(OrderedDict.fromkeys(record_dict["currated_all"])))

    return record_dict


def process_line_spark(json_dict_rec):
    """
    TODO:

    :param json_dict_rec:
    :type json_dict_rec:
    :return:
    :rtype:
    """
    all_records = []

    logger.info(f"Processing: {json_dict_rec}")
    try:

        if "about_me" not in json_dict_rec:
            return []

        if "skills" not in json_dict_rec:
            return []

        json_dict = dict()
        for key in ["about_me", "job_title", "display_name", "mail", "company_name", "department", "office_location",
                    "city", "country", "state", "skills", "responsibilities"]:
            param_val = json_dict_rec[key]
            if param_val is None:
                param_val = ""
            json_dict[key] = param_val

        rec_dict = {}

        rec_dict["about_me"] = json_dict["about_me"] + " . " + json_dict["job_title"]
        rec_dict["about_me_v2"] = json_dict["about_me"] + " . " + json_dict["job_title"]
        rec_dict["display_name"] = json_dict["display_name"]
        rec_dict["mail"] = str(json_dict["mail"]).lower().strip()
        rec_dict["id"] = base64.urlsafe_b64encode(rec_dict["mail"].encode("ascii")).decode("ascii").rstrip("=")
        rec_dict["job_title"] = json_dict["job_title"]
        rec_dict["job_title_v2"] = json_dict["job_title"]
        rec_dict["company_name"] = json_dict["company_name"]
        rec_dict["department"] = json_dict["department"]
        rec_dict["office_location"] = json_dict["office_location"]
        rec_dict["city"] = json_dict["city"]
        rec_dict["country"] = json_dict["country"]
        rec_dict["state"] = json_dict["state"]
        rec_dict["skills"] = json_dict["skills"]
        rec_dict["skills_v2"] = json_dict["skills"]
        rec_dict["responsibilities"] = json_dict["responsibilities"]
        rec_dict["responsibilities_v2"] = json_dict["responsibilities"]

        rec_dict["clean_about_me"] = clean_email_body(rec_dict["about_me"])

        rec_dict = enrich(rec_dict)
        logger.info(f"enriched: {rec_dict}")
        all_records.append(rec_dict)
    except Exception as ex:
        # TODO: re-add this
        trace = traceback.format_exc()
        logger.exception(f"Exception encountered on json", ex, trace)
        print(ex)
        print(trace)
        return []

    return all_records


def process_spark_partitions(partition):
    """
    TODO:

    :param partition:
    :type partition:
    :return:
    :rtype:
    """
    ctx = TaskContext()
    logger.info("start_processing_partitionpartitionId=" + str(ctx.partitionId()))
    all_records = []
    for entry in partition:
        all_records.extend(process_line_spark(entry))
    logger.info(f"end_processing_partition partitionId={str(ctx.partitionId())}. processed: {len(all_records)} records")
    return all_records


def process_spark_partitions_local(partition):
    """
    TODO:

    :param partition:
    :type partition:
    :return:
    :rtype:
    """
    logger.info("start_processing_partitionpartitionId=0")
    all_records = []
    for entry in partition:
        all_records.extend(process_line_spark(entry))
    logger.info(f"end_processing_partition partitionId=0. processed: {len(all_records)} records")
    return all_records


def generate_dataframe_from_table(spark, args, table):
    application_id = args.application_id
    directory_id = args.directory_id
    adb_secret_scope = args.adb_secret_scope_name
    adb_sp_client_key_secret_name = args.adb_sp_client_key_secret_name
    database = args.jdbc_database
    jdbc_host = args.jdbc_host
    jdbc_port = args.jdbc_port
    jdbc_username_key_name = args.jdbc_username_key_name
    jdbc_password_key_name = args.jdbc_password_key_name
    use_msi_azure_sql_auth = args.use_msi_azure_sql_auth

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
        stsurl = "https://login.microsoftonline.com/" + directory_id
        auth_context = AuthenticationContext(stsurl)
        tokenObj = auth_context.acquire_token_with_client_credentials("https://database.windows.net/",
                                                                      application_id,
                                                                      client_secret)
        accessToken = tokenObj['accessToken']

        df_constructor.option("accessToken", accessToken)
    else:
        service_principal_credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                              client_id=args.application_id,
                                                              client_secret=SERVICE_PRINCIPAL_SECRET)
        client = SecretClient(vault_url=args.key_vault_url, credential=service_principal_credential)

        df_constructor.option("user", client.get_secret(name=jdbc_username_key_name).value)
        df_constructor.option("password", client.get_secret(name=jdbc_password_key_name).value)

    df = df_constructor.load()
    return df


def get_hr_df(spark, args):
    database = args.jdbc_database
    jdbc_host = args.jdbc_host  # "conduit-bde.database.windows.net"  #
    jdbc_port = args.jdbc_port  # 1433
    hr_data_input_container_name = args.hr_data_input_container_name
    hr_data_input_folder_path = args.hr_data_input_folder_path
    hr_data_storage_account_name = args.hr_data_storage_account_name

    hr_df = None

    if database is not None and jdbc_host is not None and jdbc_port is not None:
        # TODO Replace the hardcoded value of the configuration type
        #  with a centralized value in a configuration_constants module in our wheel
        table = f"""
                (select hr.mail, hr.name, hr.location, hr.available_starting_from, hr.current_engagement, hr.manager_name, hr.manager_email, hr.role, hr.linkedin_profile from {database}.dbo.hr_data_employee_profile as hr 
                where hr.version=(select CONVERT(datetime2, JSON_VALUE(configs, '$.date')) from configurations c where c.[type] = 'LatestVersionOfHRDataEmployeeProfile')
                ) foo
                """
        hr_df = generate_dataframe_from_table(spark, args, table)

    elif hr_data_input_container_name is not None and hr_data_input_folder_path is not None and hr_data_storage_account_name is not None:

        hr_data_full_path = f"abfss://{hr_data_input_container_name}@{hr_data_storage_account_name}.dfs.core.windows.net/{hr_data_input_folder_path}"
        hr_df = spark.read.option("header", value=True).csv(hr_data_full_path)

    else:
        raise ValueError(
            "Not enough arguments given in order to read HR data employee profile: jdbc-database & input-container are missing.")

    hr_df = hr_df.withColumnRenamed("name", "hr_data_name") \
        .withColumnRenamed("location", "hr_data_location") \
        .withColumnRenamed("available_starting_from", "hr_data_available_starting_from") \
        .withColumnRenamed("current_engagement", "hr_data_current_engagement") \
        .withColumnRenamed("manager_name", "hr_data_manager_name") \
        .withColumnRenamed("manager_email", "hr_data_manager_email") \
        .withColumnRenamed("role", "hr_data_role") \
        .withColumnRenamed("linkedin_profile", "hr_data_linkedin_profile")

    return hr_df


def run_spark_job(args):
    storage_account_name = args.storage_account_name
    input_container = args.input_container_name
    output_container = args.output_container_name
    input_folder = args.input_folder_path
    output_folder = args.output_folder_path
    application_id = args.application_id
    directory_id = args.directory_id
    adbSecretScope = args.adb_secret_scope_name
    adbSPClientKeySecretName = args.adb_sp_client_key_secret_name
    database = args.jdbc_database
    jdbcHost = args.jdbc_host
    jdbcPort = args.jdbc_port
    jdbc_username_key_name = args.jdbc_username_key_name
    jdbc_password_key_name = args.jdbc_password_key_name
    use_msi_azure_sql_auth = args.use_msi_azure_sql_auth

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
    if adbSecretScope is None:
        ValueError("Missing adb_secret_scope_name parameter!")
    if adbSPClientKeySecretName is None:
        ValueError("Missing adb_sp_client_key_secret_name parameter!")

    client_secret = SERVICE_PRINCIPAL_SECRET if SERVICE_PRINCIPAL_SECRET is not None else dbutils.secrets.get(scope=adbSecretScope, key=adbSPClientKeySecretName)

    spark = SparkSession.builder.master("local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    spark.conf.set(f"fs.azure.account.auth.type.{storage_account_name}.dfs.core.windows.net", "OAuth")
    spark.conf.set(f"fs.azure.account.oauth.provider.type.{storage_account_name}.dfs.core.windows.net",
                   "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(f"fs.azure.account.oauth2.client.id.{storage_account_name}.dfs.core.windows.net", application_id)
    spark.conf.set(f"fs.azure.account.oauth2.client.secret.{storage_account_name}.dfs.core.windows.net", client_secret)
    spark.conf.set(f"fs.azure.account.oauth2.client.endpoint.{storage_account_name}.dfs.core.windows.net",
                   f"https://login.microsoftonline.com/{directory_id}/oauth2/token")

    spark.sparkContext.setJobGroup("Running mail processing", f"[employee_profile_processor]")

    logger.info("[employee_profile_processor] Preparing the jobs for data lemmatization and augmentation")
    container_client_credential = ClientSecretCredential(tenant_id=directory_id, client_id=application_id,
                                                         client_secret=client_secret)
    blob_service_client = BlobServiceClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                                            credential=container_client_credential)
    containerClient = blob_service_client.get_container_client(output_container)

    index = 0

    print(f"Database: ", database)
    print(f"Host: ", jdbcHost)
    print(f"Port: ", jdbcPort)

    employees_res_df = None

    if database is not None and jdbcHost is not None and jdbcPort is not None:

        table = f"""
            (select ep.id, ep.mail, ep.display_name , ep.about_me , ep.job_title , ep.company_name , ep.department , ep.office_location , ep.city , ep.state , ep.country, e_skills.skills, e_responsibilities.responsibilities from {database}.dbo.employee_profile ep
            left join (select cs.employee_profile_id, cs.employee_profile_version, string_agg(cs.skill, ',') as skills from {database}.dbo.employee_skills cs group by cs.employee_profile_id, cs.employee_profile_version ) as e_skills on ep.id=e_skills.employee_profile_id and ep.version=e_skills.employee_profile_version 
            left join (select er.employee_profile_id, er.employee_profile_version, string_agg(er.responsibility, ',') as responsibilities from {database}.dbo.employee_responsibilities er group by er.employee_profile_id, er.employee_profile_version) as e_responsibilities on ep.id=e_responsibilities.employee_profile_id and ep.version=e_responsibilities.employee_profile_version 
            where ep.version=(select CONVERT(datetime2, JSON_VALUE(configs, '$.date')) from configurations c where c.[type] = 'LatestVersionOfEmployeeProfile')
            ) foo
            """

        input_df = generate_dataframe_from_table(spark, args, table)

        employees_res_df = enrich_user_profiles(input_df=input_df,
                                                output_folder=output_folder,
                                                containerClient=containerClient)

    elif input_container is not None and input_folder is not None:

        input_folder_name = input_folder
        list_of_json_files_from_folder = []
        if input_folder_name.startswith("/"):
            input_folder_name = input_folder_name[1:]

        for entry in containerClient.list_blobs():
            if entry.name.startswith(input_folder_name + "/") and entry.size and entry.name.lower().endswith(
                    "json") > 0:
                list_of_json_files_from_folder.append(entry.name)

        last_output_full_path = ""

        for index, json_file_to_process in enumerate(list_of_json_files_from_folder):
            logger.info(f"[employee_profile_processor] processing: {json_file_to_process}")
            wasb_file_path = f"abfss://{input_container}@{storage_account_name}.dfs.core.windows.net/{json_file_to_process}"
            logger.info(f"[employee_profile_processor] input wasb_file_path: {wasb_file_path}")

            input_df = spark.read.json(wasb_file_path)

            result_df = enrich_user_profiles(input_df=input_df,
                                             output_folder=output_folder,
                                             containerClient=containerClient)

            if employees_res_df is None:
                employees_res_df = result_df
            else:
                employees_res_df = employees_res_df.union(result_df)

    else:
        raise ValueError(
            "Not enough arguments given in order to read input data: jdbc-database & input-container are missing.")

    hr_df = get_hr_df(spark, args)

    employees_res_df = employees_res_df.join(hr_df, on=['mail'], how='left')

    out_file_name = f"out_{str(index).zfill(4)}" + str(datetime.now().strftime("%Y_%m_%d_%H_%M"))
    out_file_full_path = os.path.join(output_folder, out_file_name)

    wasb_output_file_path = f"abfss://{output_container}@{storage_account_name}.dfs.core.windows.net/{out_file_full_path}"
    logger.info(f"[employee_profile_processor] output wasb_file_path: {wasb_output_file_path}")

    employees_res_df.write.mode("overwrite").json(wasb_output_file_path)

    list_of_files_to_clean = []
    for entry in containerClient.list_blobs(name_starts_with=out_file_full_path + "/"):
        if entry.name.lower().endswith("json") == False or entry.size == 0:
            logger.debug("detected file to delete: " + str(entry.name))
            list_of_files_to_clean.append(entry.name)

    for file_to_del in list_of_files_to_clean:
        blobclient = containerClient.get_blob_client(blob=file_to_del)
        logger.debug(f"Delete {file_to_del}")
        blobclient.delete_blob()

        last_output_full_path = out_file_full_path

def enrich_user_profiles(input_df, output_folder, containerClient):
    spark_res_rdd = input_df.rdd.repartition(8).mapPartitions(process_spark_partitions)

    # clenup all previous runs
    for entry in containerClient.list_blobs(name_starts_with=output_folder + "/"):
        blobclient = containerClient.get_blob_client(blob=entry.name)
        logger.info(f"[employee_profile_processor][cleanup]: deleted {entry.name}")
        blobclient.delete_blob()

    spark_res_df = spark_res_rdd.map(lambda x: Row(**x)).toDF()
    return spark_res_df

def process_local_job(folder_path_to_m365_files):
    """Process m365 files line by line
    TODO

    :param folder_path_to_m365_files: folder path to target m365 files
    :type folder_path_to_m365_files: str
    """
    input_records = []
    with open(folder_path_to_m365_files + 'm365_persons.json') as f:
        for line in f.readlines():
            rec = json.loads(line.strip())
            input_records.append(rec)

    all_rec = process_spark_partitions_local(input_records)
    with open(folder_path_to_m365_files + 'm365_persons_enriched.json', 'w+') as f:
        for rec in all_rec:
            f.write(json.dumps(rec) + '\n')


logger = None
args = None
SERVICE_PRINCIPAL_SECRET = None


if __name__ == '__main__':
    """TODO
    """
    print(sys.argv)

    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process some integers.')
        parser.add_argument('--storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--hr-data-storage-account-name', type=str,
                            help='storage account name', required=False)
        parser.add_argument('--output-container-name', type=str,
                            help='output container name')
        parser.add_argument('--output-folder-path', type=str,
                            help='output folder path')
        parser.add_argument('--input-container-name', type=str,
                            help='input container name', required=False)
        parser.add_argument('--input-folder-path', type=str,
                            help='input folder path', required=False)
        parser.add_argument('--hr-data-input-container-name', type=str,
                            help='hr data input container name', required=False)
        parser.add_argument('--hr-data-input-folder-path', type=str,
                            help='hr data input folder path', required=False)
        parser.add_argument('--domain-expert-container-name', type=str,
                            help='domain expert container name')
        parser.add_argument('--domain-expert-folder-path', type=str,
                            help='domain expert folder path')
        parser.add_argument('--domain-expert-file-name', type=str,
                            help='domain expert file name')
        parser.add_argument('--jdbc-host', type=str,
                            help='jdbc host')
        parser.add_argument('--jdbc-port', type=str,
                            help='jdbc port')
        parser.add_argument('--jdbc-database', type=str,
                            help='database name')
        parser.add_argument('--jdbc-username', type=str,
                            help='database username')
        parser.add_argument('--jdbc-password', type=str, default='',
                            help='database password')
        parser.add_argument('--application-id', type=str,
                            help='application id')
        parser.add_argument('--directory-id', type=str,
                            help='directory id')
        parser.add_argument('--adb-secret-scope-name', type=str,
                            help='ADB secret scope name')
        parser.add_argument('--adb-sp-client-key-secret-name', type=str,
                            help='Azure Databricks Service Principal client key secret name in Databricks Secrets')
        parser.add_argument('--log-analytics-workspace-id', type=str,
                            help='Log Analytics workspace id')
        parser.add_argument('--log-analytics-workspace-key-name', type=str,
                            help='Log Analytics workspace key secret name')
        parser.add_argument('--key-vault-url', type=str,
                            help='Azure Key Vault url')
        parser.add_argument('--jdbc-username-key-name', type=str,
                            help='The name of the Azure Key Vault secret that contains the jdbc username')
        parser.add_argument('--jdbc-password-key-name', type=str,
                            help='The name of the Azure Key Vault secret that contains the jdbc password')
        parser.add_argument('--use-msi-azure-sql-auth', type=str2bool,
                            help='Use Managed Service Identity (MSI) to authenticate into AzureSql or use user and password read from KeyVault instead')

        args = parser.parse_args()
        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)

        if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
            logger = LogAnalyticsLogger(name="[employee_profile_processor]")
        else:
            credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                client_id=args.application_id,
                                                client_secret=SERVICE_PRINCIPAL_SECRET)

            client = SecretClient(vault_url=args.key_vault_url, credential=credential)

            try:
                logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
                logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                            shared_key=logAnalyticsApiKey,
                                            log_type="EmployeeProfilesEnrichmentProcessor",
                                            log_server_time=True,
                                            name="[employee_profile_processor]")
            except Exception as e:
                logger = LogAnalyticsLogger(name="[employee_profile_processor]")
                logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

        run_spark_job(args)
    else:
        from types import SimpleNamespace

        params = json.load(open(Path('./profiles_enrichment_params.json').absolute()))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)

        # TODO: should be in defaults ?
        # args = SimpleNamespace(
        # log_analytics_workspace_id=" ",  # "b61e5e81-9eb2-413e-aaef-624b89af04a0",

        SERVICE_PRINCIPAL_SECRET = json.load(open("config.json"))["SERVICE_PRINCIPAL_SECRET"]
        if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
            logger = LogAnalyticsLogger(name="[employee_profile_processor]")
        else:
            credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                client_id=args.application_id,
                                                client_secret=SERVICE_PRINCIPAL_SECRET)

            client = SecretClient(vault_url=args.key_vault_url, credential=credential)

            try:
                logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
                logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                            shared_key=logAnalyticsApiKey,
                                            log_type="EmployeeProfilesEnrichmentProcessor",
                                            log_server_time=True,
                                            name="[employee_profile_processor]")
            except Exception as e:
                logger = LogAnalyticsLogger(name="[employee_profile_processor]")
                logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))
        run_spark_job(args)
