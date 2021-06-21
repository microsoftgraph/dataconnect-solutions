#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.


"""Processes users profiles and retrieve the necessary fields using Spark

...
"""
import argparse
import json
import os
import sys
from pathlib import Path

import jaydebeapi
import pandas as pd


def retrieve_latest_run(parent_folder):
    print(f"*******#####*********Retrieve lateste run from folder {parent_folder}")
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


def export_data_to_azure_sql(full_base_path, database, jdbc_user, jdbc_password):
    export_batch_size = 50

    print(f"*******#####Result {full_base_path}")

    conversation_entities_info_file = os.path.join(full_base_path, "conversation_entities_info.csv")
    conversation_sentiment_info_file = os.path.join(full_base_path, "conversation_sentiment_info.csv")

    connectionProperties = {'databaseName': database,
                            'url': 'conduit-bde.database.windows.net',
                            'hostNameInCertificate': '*.database.windows.net', 'encrypt': 'true',
                            'Driver': 'com.microsoft.sqlserver.jdbc.SQLServerDriver',
                            'ServerCertificate': 'false', 'trustServerCertificate': 'false',
                            'loginTimeout': '30'}
    connectionProperties["user"] = jdbc_user
    connectionProperties["password"] = jdbc_password

    connection = jaydebeapi.connect("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                                    f"jdbc:sqlserver://conduit-bde.database.windows.net:1433;databaseName={database};",
                                    connectionProperties)

    cursor = connection.cursor()

    # print(f"*******#####Processing {employee_profile_file}")
    # write_employee_profile_to_az_sql(employee_profile_file, export_batch_size, cursor)

    print(f"*******#####Processing {conversation_entities_info_file}")
    write_conversation_entities_to_sql(conversation_entities_info_file, export_batch_size, cursor)

    print(f"*******#####Processing {conversation_sentiment_info_file}")
    write_conversation_sentiment_to_sql(conversation_sentiment_info_file, export_batch_size, cursor)

    cursor.close()
    connection.close()


def write_conversation_entities_to_sql(conversation_entities_info_file, batch_size, cursor):
    df = pd.read_csv(conversation_entities_info_file, escapechar="\\")
    number_of_rows = len(df)
    line_count = 0
    insert_command = "INSERT INTO conversation_entities_info " \
                     "(id,conversation_id,sender_mail,sender_name,sender_domain,entity_text,category,score) VALUES "
    batch_insert = insert_command
    for line_count in range(1, number_of_rows + 1):
        row = df.iloc[line_count - 1]
        try:
            batch_insert += f""" ('{row["id"]}',  '{row["conversation_id"]}', '{row["sender_mail"]}', '{row["sender_name"]}', '{row["sender_domain"]}', '{row["text"]}', '{row["category"]}', {row["score"]}),"""
            if line_count % batch_size == 0:
                batch_insert = batch_insert[:-1]  # remove last comma
                batch_insert = batch_insert + ";"
                batch_insert = batch_insert.replace("""'nan'""", "NULL")
                print("===============================")
                print(batch_insert)
                cursor.execute(batch_insert)
                batch_insert = insert_command
        except Exception as e:
            print("===========PROBLEM====================")
            print(batch_insert)
            pass
    if line_count % batch_size != 0:
        batch_insert = batch_insert[:-1]  # remove last comma
        batch_insert = batch_insert + ";"
        print("===============================")
        print(batch_insert)
        try:
            cursor.execute(batch_insert)
        except Exception as e:
            print("===========PROBLEM====================")
            print(batch_insert)


def write_conversation_sentiment_to_sql(conversation_sentiment_info_file, batch_size, cursor):
    df = pd.read_csv(conversation_sentiment_info_file, escapechar="\\")
    number_of_rows = len(df)
    line_count = 0
    insert_command = "INSERT INTO conversation_sentiment_info " \
                     "(id,conversation_id,sender_mail,sender_name,sender_domain,general_sentiment,pos_score,neutral_score,negative_score) VALUES "
    batch_insert = insert_command
    for line_count in range(1, number_of_rows + 1):
        row = df.iloc[line_count - 1]
        batch_insert += f""" ('{row["id"]}',  '{row["conversation_id"]}', '{row["sender_mail"]}', '{row["sender_name"]}', '{row["sender_domain"]}', '{row["general_sentiment"]}', {row["pos_score"]}, {row["neutral_score"]}, {row["negative_score"]}),"""
        try:
            if line_count % batch_size == 0:
                batch_insert = batch_insert[:-1]  # remove last comma
                batch_insert = batch_insert + ";"
                batch_insert = batch_insert.replace("""'nan'""", "NULL")
                cursor.execute(batch_insert)
                batch_insert = insert_command
        except Exception as e:
            print("===========PROBLEM====================")
            print(batch_insert)

    if line_count % batch_size != 0:
        batch_insert = batch_insert[:-1]  # remove last comma
        batch_insert = batch_insert + ";"
        try:
            cursor.execute(batch_insert)
        except Exception as e:
            print("===========PROBLEM====================")
            print(batch_insert)


if __name__ == '__main__':
    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Arguments for exporting informatioj')
        parser.add_argument('--storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--output-folder-path', type=str,
                            help='output folder path')
        parser.add_argument('--azure-text-analytics-endpoint', type=str,
                            help='azure text analytics endpoint id')
        parser.add_argument('--azure-text-analytics-key', type=str,
                            help='azure text analytics endpoint id')
        parser.add_argument('--mails-input-folder', type=str,
                            help='azure text analytics endpoint id')

        args = parser.parse_args()
        params = json.load(open(Path("/dbfs/mnt/convlineage/scripts/config_test_azure.json")))
        output_folder = params["output_folder"]
        database = params["jdbc_db"]
        jdbc_user = params["jdbc_user"]
        jdbc_password = params["jdbc_password"]

    else:

        # params = json.load(open(Path("config_test.json"))) #for debug reasons
        params = json.load(open(Path("/dbfs/mnt/convlineage/scripts/config_test_azure.json")))

        output_folder = params["output_folder"]
        database = params["jdbc_db"]
        jdbc_user = params["jdbc_user"]
        jdbc_password = params["jdbc_password"]

    input_folder = retrieve_latest_run(output_folder)
    export_data_to_azure_sql(input_folder, database, jdbc_user, jdbc_password)
