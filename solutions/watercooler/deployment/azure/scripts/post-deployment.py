#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import os
from distutils.util import strtobool
from os.path import join, dirname

import sys
from watercooler_utils import arm_ops
from watercooler_utils import az
from watercooler_utils import blob_ops
from watercooler_utils import adf_ops
from watercooler_utils import common
from config import InstallConfiguration
from monitoring import DeploymentState, Stages
from post_deploy import secrets as post_deploy_secrets
from post_deploy import db as post_deploy_db
from post_deploy import cluster as post_deploy_adb


if __name__ == '__main__':
    args = sys.argv
    current_account = az.az_cli("account show")
    tenant_id = current_account['tenantId']
    subscription_id = current_account['id']
    subscription_name = current_account['name']
    install_config: InstallConfiguration = InstallConfiguration.load()

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
                            default=join(dirname(__file__), "artifacts"))
    arg_parser.add_argument("--remote-artifacts-storage-name",
                            metavar="remote-artifacts-storage-name")
    arg_parser.add_argument('--debug', default=False, required=False, type=lambda x: bool(strtobool(str(x))))

    parsed_args = arg_parser.parse_args()
    tenant_id = parsed_args.tenant_id
    subscription_id = parsed_args.subscription_id
    resource_group = parsed_args.resource_group
    artifacts_path = parsed_args.artifacts_path
    debug_enabled = parsed_args.debug
    if debug_enabled:
        az.DEBUG_ENABLED = True

    # ---- init secrets phase -----------------
    install_state = DeploymentState.load()
    post_deploy_secrets.initialize_secrets(install_config=install_config, resource_group_name=resource_group)
    install_state.complete_stage(Stages.KEY_VAULT_SECRETS_SET)

    # ---- Databricks cluster phase -----------------
    should_provision_adb_cluster = True
    if install_state.is_cluster_created():
        should_provision_adb_cluster = install_state.prompt_stage_repeat(msg="Databricks cluster has been provisioned during previous run , would you like to repeat this action ? (Y/n) ")

    if should_provision_adb_cluster:
        try:
            post_deploy_adb.initialize_databricks_cluster(install_config=install_config,
                                                          tenant_id=tenant_id,
                                                          subscription_id=subscription_id,
                                                          resource_group=resource_group,
                                                          artifacts_path=artifacts_path)
            install_state.complete_stage(Stages.DATABRICKS_CLUSTER_INITIALIZED)
        except Exception as adb_err:
            print("Databricks cluster initialization has failed")
            print(adb_err)
            if not common.yes_no(" Would you like to continue ?(Y/n) "):
                raise adb_err

    # ---- post-config phase -----------------
    print("Restartng Watercooler application")
    arm_ops.restart_web_app(resource_group=resource_group, app_name=install_config.appservice_name)
    print("Activating Watercooler HTTP alert")
    arm_ops.enable_webapp_alert(resource_group=resource_group, alert_name="Watercooler HTTP Errors Alert")
    print("Updating DataFactory pipeline configuration ...")
    adb_ws_name = install_config.databricks_workspace_name
    if install_state.is_azure_resources_deployed():
        ws_url = arm_ops.get_databricks_workspace_url(resource_group=resource_group, ws_name=adb_ws_name)
        if not ws_url.startswith("https://"):
            ws_url = "https://" + ws_url
        arm_ops.update_linked_service(resource_group=resource_group, factory_name=install_config.wc_datafactory_name,
                                      service_name="databricks_link_service", prop_name="properties.domain", value=ws_url)
    else:
        print("No Databricks workspace found")

    if "cluster_id" in install_config.adb_cluster_details:
        arm_ops.update_linked_service(resource_group=resource_group, factory_name=install_config.wc_datafactory_name,
                                      service_name="databricks_link_service", prop_name="properties.existingClusterId",
                                      value=install_config.adb_cluster_details['cluster_id'])
    else:
        print("No cluster id has been found in configuration. Please set it manually in Data Factory linked service")

    if install_state.is_sql_schema_initialized() and install_state.is_azure_resources_deployed() and install_state.is_cluster_created():
        print("Activating triggers ... ")
        adf_ops.activate_datafactory_trigger(resource_group=resource_group, factory_name=install_config.wc_datafactory_name,
                                             trigger_name="GenerateEventsEveryTwoWeeks")

        install_state.complete_stage(Stages.DEPLOYMENT_DONE)
    else:
        if not install_state.is_sql_schema_initialized():
            print("Database schema needs to be initialized")
        if not install_state.is_cluster_created():
            print("Databricks cluster needs to be initialized")

        print("No pipeline triggers have not been activated due to previous stage failures. Please proceed with starting '*_backfill_*' triggers manually in Data Factory")

    print("Watercooler web app is available at %s " % install_config.appservice_url())
