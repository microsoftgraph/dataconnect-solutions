#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import time

from azure.common import AzureMissingResourceHttpError
from azure.multiapi.storage.v2018_11_09.blob import BlockBlobService
from azure.storage.blob import ContainerClient
from skills_finder_utils import az
from retry import retry


def list_blobs_in(container_url: str, subfolder: str):
    container = ContainerClient.from_container_url(container_url=container_url)
    print("List files from %s" % container_url)
    blob_list = []
    try:
        for blob in container.list_blobs(name_starts_with=subfolder):
            blob_list.append(blob.name)
    except AzureMissingResourceHttpError as ce:
        print("No blobs found at %s/%s" % (container_url, subfolder))
    finally:
        container.close()
    return blob_list


def copy_domain_expert_data(resource_group: str, runtime_storage: str):
    _copy_data(resource_group=resource_group, runtime_storage=runtime_storage,
               source_subfolder="domain-experts", dest_container_name="domain-experts", destination_path_prefix="domain_experts/")


def copy_simulated_data(resource_group: str, testdata_storage: str):
    _copy_data(resource_group=resource_group, runtime_storage=testdata_storage,
               source_subfolder="simulated-data", dest_container_name="simulated-data")


def copy_search_index_definitions(resource_group: str, runtime_storage: str):
    storage_connection_str = az.az_cli("storage account show-connection-string", "--resource-group",
                                       resource_group, "--name", runtime_storage, "--query", "connectionString")

    files = ['schema/indexes/azure_employees_index_definition.json',
             'schema/indexes/azure_mails_index_definition.json']
    _upload_data_from_local(runtime_storage=runtime_storage,
                            dest_container_name="deployment-artifacts",
                            dest_storage_connection_str=storage_connection_str,
                            files=files)


@retry(tries=5, delay=1, backoff=2)
def _upload_data_from_local(runtime_storage: str, dest_container_name: str,
                            dest_storage_connection_str: str, files: list):
    dest_service = BlockBlobService(account_name=runtime_storage, connection_string=dest_storage_connection_str)
    for local_path in files:
        dest_service.create_blob_from_path(container_name=dest_container_name, blob_name=local_path,
                                           file_path=local_path)


@retry(tries=5, delay=1, backoff=2)
def _copy_data(resource_group: str, runtime_storage: str, source_subfolder: str, dest_container_name,
               destination_path_prefix: str = None, source_storage_account_name: str = "bpartifactstorage",
               source_container_name: str = "gdc-artifacts"):
    source_container_url: str = f"https://{source_storage_account_name}.blob.core.windows.net/{source_container_name}"
    files = list_blobs_in(container_url=source_container_url, subfolder=source_subfolder)

    storage_connection_str = az.az_cli("storage account show-connection-string", "--resource-group",
                                       resource_group, "--name", runtime_storage, "--query", "connectionString")

    source_block_blob_service = BlockBlobService(account_name=source_storage_account_name)
    dest_service = BlockBlobService(account_name=runtime_storage, connection_string=storage_connection_str)
    for blob_path in files:
        source_url = source_container_url + "/" + blob_path
        target_file_path = blob_path[len(source_subfolder) + 1:] if blob_path.startswith(
            source_subfolder + "/") else blob_path
        if destination_path_prefix:
            # make sure there is no leading slash otherwise blob path is going to be broken with double slashes
            target_file_path = destination_path_prefix.lstrip("/") + target_file_path

        print("Copying %s into %s/%s " % (blob_path, dest_container_name, target_file_path))
        # We must wait until copying completely finished so that the next deployment steps have all the required data,
        # however, setting requires_sync=True does not support blobs larger than 256MB.
        # Therefore, we need to use async copy together with copied blob properties polling
        dest_service.copy_blob(
            container_name=dest_container_name,
            blob_name=target_file_path,
            copy_source=source_url
        )

        copy_finished = False
        while not copy_finished:

            time.sleep(10)

            dest_blob_properties = dest_service.get_blob_properties(container_name=dest_container_name,
                                                                    blob_name=target_file_path).properties

            source_blob_properties = source_block_blob_service.get_blob_properties(container_name=source_container_name,
                                                                                   blob_name=blob_path).properties

            copy_finished = dest_blob_properties.content_settings.content_md5 == source_blob_properties.content_settings.content_md5 \
                            and dest_blob_properties.content_length == source_blob_properties.content_length

            if not copy_finished:
                print("Waiting for copy operation to finish. Source content_md5=",
                      source_blob_properties.content_settings.content_md5,
                      " destination content_md5=", dest_blob_properties.content_settings.content_md5,
                      " source content_length=", source_blob_properties.content_length,
                      " destination content_length=", dest_blob_properties.content_length)
            else:
                print("Copy operation finished successfully. Source content_md5=",
                      source_blob_properties.content_settings.content_md5,
                      " destination content_md5=", dest_blob_properties.content_settings.content_md5,
                      " source content_length=", source_blob_properties.content_length,
                      " destination content_length=", dest_blob_properties.content_length)


def copy_file(source_path: str, resource_group: str, runtime_storage: str, dest_container_name: str, dest_path):
    storage_connection_str = az.az_cli("storage account show-connection-string", "--resource-group",
                                       resource_group, "--name", runtime_storage, "--query", "connectionString")

    dest_service = BlockBlobService(account_name=runtime_storage, connection_string=storage_connection_str)
    print("Copying %s into %s : %s/%s " % (source_path, runtime_storage, dest_container_name, dest_path))
    dest_service.create_blob_from_path(container_name=dest_container_name, blob_name=dest_path, file_path=source_path)
