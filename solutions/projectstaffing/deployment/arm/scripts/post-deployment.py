#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import os
from distutils.util import strtobool
from os.path import join, dirname

import sys
from skills_finder_utils import arm_ops
from skills_finder_utils import az
from skills_finder_utils import blob_ops
from skills_finder_utils import adf_ops
from skills_finder_utils import common
from config import InstallConfiguration
from monitoring import DeploymentState, Stages
from post_deploy import secrets as post_deploy_secrets
from post_deploy import db as post_deploy_db
from post_deploy import cluster as post_deploy_adb


def create_or_update_azure_search_indexes(search_search_endpoint: str, admin_key_value: str):
    with open('schema/indexes/azure_mails_index_definition.json', 'r') as f:
        email_index_def = json.load(f)
    arm_ops.create_index(endpoint=search_search_endpoint, admin_key=admin_key_value,
                         index_definition_json=email_index_def)


if __name__ == '__main__':
    args = sys.argv
    current_account = az.az_cli("account show")
    install_config: InstallConfiguration = InstallConfiguration.load()

    # Create the parser
    arg_parser = argparse.ArgumentParser(description='Install GDC service')

    # Add the arguments
    # arg_parser.add_argument('--azure-search-url',
    #                         metavar='azure-search-url',
    #                         type=str,
    #                         help='Azure search endpoint URL', required=True)
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
    demo_data_storage_account_name = parsed_args.remote_artifacts_storage_name
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

    runtime_storage = install_config.runtime_storage_account_name
    print("Uploading Azure Search schema definitions")
    blob_ops.copy_search_index_definitions(resource_group=resource_group, runtime_storage=runtime_storage)
    print("Creating required Azure Search schema ")
    search_search_endpoint = "https://%s.search.windows.net" % install_config.azure_search_name
    admin_key = arm_ops.get_search_admin_token(resource_group=resource_group, azure_search_name=install_config.azure_search_name)
    create_or_update_azure_search_indexes(search_search_endpoint=search_search_endpoint, admin_key_value=admin_key)
    print("Restart jgraph application")
    arm_ops.restart_web_app(resource_group=resource_group, app_name=install_config.appservice_name)
    print("Activating Jgraph HTTP alert")
    arm_ops.enable_webapp_alert(resource_group=resource_group, alert_name="JGraph HTTP Errors Alert")
    print("Uploading simulated and domain expert data...")
    test_storage_account_name = install_config.test_data_storage_name

    blob_ops.copy_domain_expert_data(resource_group=resource_group, runtime_storage=runtime_storage,
                                     source_storage_account_name=demo_data_storage_account_name, source_container_name="public-artifacts")
    blob_ops.copy_simulated_data(resource_group=resource_group, testdata_storage=test_storage_account_name,
                                 source_storage_account_name=demo_data_storage_account_name, source_container_name="public-artifacts")
    print("Updating DataFactory pipeline configuration ...")
    adb_ws_name = install_config.databricks_workspace_name
    if install_state.is_azure_resources_deployed():
        ws_url = arm_ops.get_databricks_workspace_url(resource_group=resource_group, ws_name=adb_ws_name)
        if not ws_url.startswith("https://"):
            ws_url = "https://" + ws_url
        arm_ops.update_linked_service(resource_group=resource_group, factory_name=install_config.gdc_datafactory_name,
                                      service_name="databricks_linkedservice", prop_name="properties.domain", value=ws_url)
    else:
        print("No Databricks workspace found")

    if "cluster_id" in install_config.adb_cluster_details:
        arm_ops.update_linked_service(resource_group=resource_group, factory_name=install_config.gdc_datafactory_name,
                                      service_name="databricks_linkedservice", prop_name="properties.existingClusterId",
                                      value=install_config.adb_cluster_details['cluster_id'])
    else:
        print("No cluster id has been found in configuration. Please set it manually in Data Factory linked service")

    if install_state.is_sql_schema_initialized() and install_state.is_azure_resources_deployed() and install_state.is_cluster_created():
        print("Activating triggers ")
        adf_ops.activate_datafactory_trigger(resource_group=resource_group, factory_name=install_config.gdc_datafactory_name,
                                             trigger_name="airtable_pipeline_backfill_trigger")
        adf_ops.activate_datafactory_trigger(resource_group=resource_group, factory_name=install_config.gdc_datafactory_name,
                                             trigger_name="inferred_roles_pipeline_backfill_trigger")
        adf_ops.activate_datafactory_trigger(resource_group=resource_group, factory_name=install_config.gdc_datafactory_name,
                                             trigger_name="employee_profiles_pipeline_backfill_trigger")
        adf_ops.activate_datafactory_trigger(resource_group=resource_group, factory_name=install_config.gdc_datafactory_name,
                                             trigger_name="emails_pipeline_backfill_past_week_trigger")
        install_state.complete_stage(Stages.DEPLOYMENT_DONE)
    else:
        if not install_state.is_sql_schema_initialized():
            print("Database schema needs to be initialized")
        if not install_state.is_cluster_created():
            print("Databricks cluster needs to be initialized")

        print("No pipeline triggers have not been activated due to previous stage failures. Please proceed with starting '*_backfill_*' triggers manually in Data Factory")

    print("Project staffing web app is available at %s " % install_config.appservice_url())
