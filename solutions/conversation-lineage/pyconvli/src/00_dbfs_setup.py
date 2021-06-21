#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import json
from azure.storage.blob import BlobClient

from azure.storage.blob import BlobServiceClient


def deploy_scripts_to_folder(input_container="convlineage"):
    CONNECTION_STRING = json.load(open("config_test.json"))["datasource_connection_string"]
    blob_service_client = BlobServiceClient.from_connection_string(CONNECTION_STRING)

    print("Uploading 000_cleanup")
    blob_client = BlobClient.from_connection_string(CONNECTION_STRING,
                                                    container_name=input_container,
                                                    blob_name="scripts/000_cleanup.py")
    with open("000_cleanup.py", "rb") as data:
        blob_client.upload_blob(data, overwrite=True)

    print("Uploading 01_mails_sentiment_retriever.py")
    blob_client = BlobClient.from_connection_string(CONNECTION_STRING,
                                                    container_name=input_container,
                                                    blob_name="scripts/01_mails_sentiment_retriever.py")
    with open("01_mails_sentiment_retriever.py", "rb") as data:
        blob_client.upload_blob(data, overwrite=True)

    print("Uploading 02_export_to_sql.py")
    blob_client = BlobClient.from_connection_string(CONNECTION_STRING,
                                                    container_name=input_container,
                                                    blob_name="scripts/02_export_to_sql.py")
    with open("02_export_to_sql.py", "rb") as data:
        blob_client.upload_blob(data, overwrite=True)


    print("Uploading config_test.json")
    blob_client = BlobClient.from_connection_string(CONNECTION_STRING,
                                                    container_name=input_container,
                                                    blob_name="scripts/config_test.json")
    with open("config_test.json", "rb") as data:
        blob_client.upload_blob(data, overwrite=True)

    print("Uploading config_test_azure.json")
    blob_client = BlobClient.from_connection_string(CONNECTION_STRING,
                                                    container_name=input_container,
                                                    blob_name="scripts/config_test_azure.json")
    with open("config_test_azure.json", "rb") as data:
        blob_client.upload_blob(data, overwrite=True)


deploy_scripts_to_folder()
