#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import sys
from pathlib import Path

import jaydebeapi


def cleanup_database(database, jdbc_user, jdbc_password):
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

    conversation_sentiment_info = "truncate table conversation_sentiment_info;"
    conversation_entities_info = "truncate table conversation_entities_info;"
    conversation_to_receiver_sentiment_info = "truncate table conversation_to_receiver_sentiment_info;"

    cursor.execute(conversation_sentiment_info)
    print("Truncated conversation_sentiment_info table.")

    cursor.execute(conversation_entities_info)
    print("Truncated conversation_entities_info table.")

    cursor.execute(conversation_to_receiver_sentiment_info)
    print("Truncated conversation_to_receiver_sentiment_info table.")

    cursor.close()
    connection.close()


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
        database = params["jdbc_db"]
        jdbc_user = params["jdbc_user"]
        jdbc_password = params["jdbc_password"]

    else:

        #params = json.load(open(Path("config_test.json")))
        params = json.load(open(Path("/dbfs/mnt/convlineage/scripts/config_test_azure.json")))
        database = params["jdbc_db"]
        jdbc_user = params["jdbc_user"]
        jdbc_password = params["jdbc_password"]

    cleanup_database(database, jdbc_user, jdbc_password)
