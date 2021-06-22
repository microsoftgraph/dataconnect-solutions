#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import os
from distutils.util import strtobool
from os.path import join, dirname, basename

import sys
from watercooler_utils import ad_ops
from watercooler_utils import arm_ops
from watercooler_utils import az
from watercooler_utils import blob_ops
from watercooler_utils import secrets_ops
from watercooler_utils import adf_ops
from config import InstallConfiguration
from monitoring import DeploymentState, Stages
import pathlib

import base64
import json
import os
import requests
from databricks_api import DatabricksAPI
from datetime import datetime, timedelta
from os import listdir
from os.path import isfile, isdir, join, basename
from requests import HTTPError
from retry import retry
from time import sleep
from urllib.parse import urljoin
import time


def upload_mount_storage_file(databricks_host: str,
                              token: str):
    adb_client = DatabricksAPI(host=databricks_host,
                               token=token)

    adb_client.dbfs.mkdirs(path="/mnt/provision")
    res = adb_client.dbfs.list("/mnt/provision")
    print("Successfully created the provision folder: /mnt/provision")

    try:
        res = adb_client.dbfs.delete("/mnt/provision/mount_dbfs.py")
    except Exception as e:
        pass

    print("Preparing to upload mount_dbfs.py to: /mnt/provision/mount_dbfs.py")
    handle = adb_client.dbfs.create(path="/mnt/provision/mount_dbfs.py")['handle']
    # TODO: get the current path
    config_dir = pathlib.Path(__file__).parent.absolute()
    local_file = os.path.join(config_dir, "mount_dbfs.py")
    print("Path for mount_dbfs.py is: ", local_file)

    with open(local_file, "rb") as f:
        while True:
            # A block can be at most 1MB
            block = f.read(1 << 20)
            if not block:
                break
            data = base64.standard_b64encode(block)
            adb_client.dbfs.add_block(handle=handle, data=data.decode("utf-8"))
    # close the handle to finish uploading
    adb_client.dbfs.close(handle=handle)
    print("Upload succeeded: ", local_file)


def execute_script_mount_storage_script(databricks_host: str,
                                        token: str,
                                        cluster_id: str,
                                        storage_account_name: str,
                                        container_name: str,
                                        secret_key: str):

    adb_client = DatabricksAPI(host=databricks_host, token=token)
    res = adb_client.dbfs.list("/mnt/")
    print("Waiting 30 seconds before proceeding with the deployment")
    time.sleep(30)

    deployment_succesfull = False
    for i in range(0, 3):
        submit_run_res = adb_client.jobs.submit_run(run_name="mount_storage",
                                                    existing_cluster_id=cluster_id,
                                                    spark_python_task={
                                                        "python_file": "dbfs:/mnt/provision/mount_dbfs.py",
                                                        "parameters": [
                                                            f"--account_name####" + storage_account_name,
                                                            f"--container_name####" + container_name,
                                                            f"--secret_key_name####" + secret_key,
                                                            f"--mount_point####/mnt/watercooler"
                                                        ]
                                                    },
                                                    timeout_seconds=3600)
        run_id = submit_run_res["run_id"]

        while True:
            res = adb_client.jobs.get_run(run_id=run_id)
            if "state" in res:
                print("Cluster mount job status is: " + str(res["state"]))
            if res is None:
                print("Cluster mount job completed")
                deployment_succesfull = True
                break
            if "state" in res and "life_cycle_state" in res["state"] and res["state"]["life_cycle_state"] in [
                "PENDING", "RUNNING"]:
                time.sleep(5)
                continue
            if "state" in res and "life_cycle_state" in res["state"] and res["state"]["life_cycle_state"] in [
                "INTERNAL_ERROR", "FAILED", "TIMED_OUT"]:
                deployment_succesfull = False
                break
            if "state" in res and "life_cycle_state" in res["state"] and res["state"]["life_cycle_state"] in [
                "SUCCESSFUL", "TERMINATED"]:
                deployment_succesfull = True
                break
            time.sleep(5)
        if deployment_succesfull:
            break
        else:
            print("Retrying: ", str(i), " time. Waiting 10 seconds")
            time.sleep(10)

        time.sleep(30)
    print("Cluster mount job successfully completed:", str(deployment_succesfull))


def initialize_databricks_cluster(install_config: InstallConfiguration, resource_group: str, artifacts_path: str,
                                  tenant_id: str = None, subscription_id: str = None):

    runtime_storage = install_config.runtime_storage_account_name
    storage_account_keys_list_res = az.az_cli("storage account keys list --account-name " + runtime_storage)
    storage_account_access_key = storage_account_keys_list_res[0]["value"]

    print("Creating Databricks cluster ... ")
    backend_keyvault_name = install_config.backend_keyvault_name
    wc_sp_secret_value = install_config.wc_service_principal['password']
    adb_ws_name = install_config.databricks_workspace_name
    ws_url = arm_ops.get_databricks_workspace_url(resource_group=resource_group, ws_name=adb_ws_name)
    if not ws_url.startswith("https://"):
        ws_url = "https://" + ws_url
    adb_access_token = ad_ops.get_databricks_access_token(tenant_id, subscription_id)
    managed_libraries = []
    with open("cluster_libraries.json", "r") as libs_file:
        managed_libraries = json.load(libs_file)
    provisioning_rsp = arm_ops.provision_databricks_cluster(install_config=install_config, workspace_url=ws_url,
                                                            oauth_access_token=adb_access_token,
                                                            wc_sp_secret_value=wc_sp_secret_value,
                                                            managed_libraries=managed_libraries)
    install_config.adb_cluster_details = provisioning_rsp

    upload_mount_storage_file(ws_url, adb_access_token)

    execute_script_mount_storage_script(databricks_host=ws_url,
                                        token=adb_access_token,
                                        cluster_id=provisioning_rsp['cluster_id'],
                                        storage_account_name=runtime_storage,
                                        container_name="data",
                                        secret_key=storage_account_access_key)

    secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="wc-databricks-token",
                           value=provisioning_rsp['api_token']['token_value'])

    print("Uploading artifacts onto DBFS ...")
    arm_ops.upload_artifacts(workspace_url=ws_url,
                             oauth_access_token=adb_access_token,
                             local_artifacts_path=artifacts_path,
                             dbfs_dir_path="/mnt/watercooler/scripts")


if __name__ == '__main__':
    DEFAULT_ARTIFACTS_LOCATION = join(dirname(dirname(__file__)), "artifacts")
    args = sys.argv

    current_account = az.az_cli("account show")

    # Create the parser
    arg_parser = argparse.ArgumentParser(description='Install Watercooler service')

    # Add the arguments
    arg_parser.add_argument('--tenant-id',
                            metavar='tenant-id',
                            type=str,
                            help='Id of Azure tenant used for deployment', required=True)
    arg_parser.add_argument('--subscription-id',
                            metavar='subscription-id',
                            type=str,
                            help='Id of Azure subscription used for deployment', required=True)
    arg_parser.add_argument("--resource-group",
                            metavar='resource-group',
                            type=str,
                            help='Azure resource group of deployment', required=True)
    arg_parser.add_argument("--artifacts-path",
                            metavar="artifacts-path",
                            help='Local path to artifacts to be uploaded to DBFS',
                            default=DEFAULT_ARTIFACTS_LOCATION)
    arg_parser.add_argument('--debug', default=False, required=False, type=lambda x: bool(strtobool(str(x))))

    parsed_args = arg_parser.parse_args()
    group = parsed_args.resource_group
    artifacts_local_path = parsed_args.artifacts_path
    debug_enabled = parsed_args.debug
    config: InstallConfiguration = InstallConfiguration.load()
    install_state = DeploymentState.load()
    if debug_enabled:
        az.DEBUG_ENABLED = True

    initialize_databricks_cluster(install_config=config, resource_group=group, artifacts_path=artifacts_local_path,
                                  tenant_id=parsed_args.tenant_id, subscription_id=parsed_args.subscription_id)
    install_state.complete_stage(Stages.DATABRICKS_CLUSTER_INITIALIZED)
    print("Databricks cluster has been initialized")

