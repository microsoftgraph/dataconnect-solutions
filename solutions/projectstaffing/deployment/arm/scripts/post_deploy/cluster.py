#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import os
from distutils.util import strtobool
from os.path import join, dirname, basename

import sys
from skills_finder_utils import ad_ops
from skills_finder_utils import arm_ops
from skills_finder_utils import az
from skills_finder_utils import blob_ops
from skills_finder_utils import secrets_ops
from skills_finder_utils import adf_ops
from config import InstallConfiguration
from monitoring import DeploymentState, Stages


def initialize_databricks_cluster(install_config: InstallConfiguration, resource_group: str, artifacts_path: str):
    print("Creating Databricks cluster ... ")
    backend_keyvault_name = install_config.backend_keyvault_name
    gdc_sp_secret_value = install_config.gdc_service_principal['password']
    adb_ws_name = install_config.databricks_workspace_name
    ws_url = arm_ops.get_databricks_workspace_url(resource_group=resource_group, ws_name=adb_ws_name)
    if not ws_url.startswith("https://"):
        ws_url = "https://" + ws_url
    adb_access_token = ad_ops.get_databricks_access_token()
    managed_libraries = []
    with open("cluster_libraries.json", "r") as libs_file:
        managed_libraries = json.load(libs_file)
    provisioning_rsp = arm_ops.provision_databricks_cluster(install_config=install_config, workspace_url=ws_url,
                                                            oauth_access_token=adb_access_token,
                                                            gdc_sp_secret_value=gdc_sp_secret_value,
                                                            managed_libraries=managed_libraries)
    install_config.adb_cluster_details = provisioning_rsp
    secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="gdc-databricks-token",
                           value=provisioning_rsp['api_token']['token_value'])
    print("Uploading artifacts onto DBFS ...")
    arm_ops.upload_artifacts(workspace_url=ws_url,
                             oauth_access_token=adb_access_token,
                             local_artifacts_path=artifacts_path,
                             dbfs_dir_path="/mnt/gdc-artifacts")


if __name__ == '__main__':
    DEFAULT_ARTIFACTS_LOCATION = join(dirname(dirname(__file__)), "artifacts")
    args = sys.argv

    current_account = az.az_cli("account show")
    tenant_id = current_account['tenantId']
    subscription_id = current_account['id']
    subscription_name = current_account['name']

    # Create the parser
    arg_parser = argparse.ArgumentParser(description='Install GDC service')

    # Add the arguments
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

    initialize_databricks_cluster(install_config=config, resource_group=group, artifacts_path=artifacts_local_path)
    install_state.complete_stage(Stages.DATABRICKS_CLUSTER_INITIALIZED)
    print("Databricks cluster has been initialized")

