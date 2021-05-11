#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import json
import os
from distutils.util import strtobool
from os.path import join, dirname

import sys
from skills_finder_utils import ad_ops
from skills_finder_utils import arm_ops
from skills_finder_utils import az
from skills_finder_utils import blob_ops
from skills_finder_utils import secrets_ops
from skills_finder_utils import adf_ops
from config import InstallConfiguration
from monitoring import DeploymentState, Stages


def initialize_secrets(install_config: InstallConfiguration, resource_group_name: str):
    print("Initializing keyvault secrets... ")
    app_keyvault_name = install_config.app_keyvault_name
    if not app_keyvault_name:
        app_keyvault_name = input("Enter Preferable Azure KeyVault for application properties: ")
    backend_keyvault_name = install_config.backend_keyvault_name
    if not backend_keyvault_name:
        backend_keyvault_name = input("Enter Preferable Azure KeyVault for offline pipeline: ")
    search_name = install_config.azure_search_name
    query_key = arm_ops.get_search_query_token(resource_group=resource_group_name, azure_search_name=search_name,
                                               key_name="jgraph-query-key")
    if not query_key:
        query_key = arm_ops.create_search_query_key(resource_group=resource_group_name, azure_search_name=search_name,
                                                    key_name="jgraph-query-key")
    secrets_ops.set_secret(keyvault_name=app_keyvault_name, secret_name="gdc-search-service-key", value=query_key)
    admin_key = arm_ops.get_search_admin_token(resource_group=resource_group_name, azure_search_name=search_name)
    gdc_sp_secret_value = install_config.gdc_service_principal['password']
    secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="gdc-m365-reader-secret",
                           value=install_config.m365_reader_service_principal['password'])
    secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="gdc-service-principal-secret",
                           value=gdc_sp_secret_value)
    secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="gdc-search-service-admin-key",
                           value=admin_key)
    secrets_ops.set_secret(keyvault_name=app_keyvault_name, secret_name="gdc-jgraph-service-principal-secret",
                           value=install_config.jgraph_aad_app['password'])
    if install_config.airtable_base_id:
        secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="airtable-base-key",
                               value=install_config.airtable_base_id)
    else:
        print("No Airtable Base Id provided 'airtable-base-key' wasn't set")
    if install_config.airtable_api_key:
        secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="airtable-api-key",
                               value=install_config.airtable_api_key)
    else:
        print("No Airtable API Key provided 'airtable-api-key' wasn't set")
    if install_config.log_analytics_workspace_name:
        log_analytics_workspace_key = arm_ops.get_log_workspace_key(resource_group=resource_group_name,
                                                                    workspace_name=install_config.log_analytics_workspace_name)
        secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="log-analytics-api-key",
                               value=log_analytics_workspace_key)
    else:
        print("Log analytics workspace settings not found, 'log-analytics-api-key' secret is not set")

    if install_config.sql_auth:
        if install_config.jgraph_db_user_password:
            secrets_ops.set_secret(keyvault_name=app_keyvault_name, secret_name="azure-sql-user",
                                   value=install_config.appservice_name)
            secrets_ops.set_secret(keyvault_name=app_keyvault_name, secret_name="azure-sql-password",
                                   value=install_config.jgraph_db_user_password)
        else:
            print("WARNING: SQL Sever Authentication mode is selected but no user/password for JGraph defined")

        if install_config.gdc_service_db_user_password:
            secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="azure-sql-backend-user",
                                   value="gdc-service")
            secrets_ops.set_secret(keyvault_name=backend_keyvault_name, secret_name="azure-sql-backend-password",
                                   value=install_config.gdc_service_db_user_password)
        else:
            print("WARNING: SQL Sever Authentication mode is selected but no user/password for gdc-service defined")


if __name__ == '__main__':
    args = sys.argv
    # Create the parser
    arg_parser = argparse.ArgumentParser(description='Install GDC service')

    arg_parser.add_argument("--resource-group",
                            metavar='resource-group',
                            type=str,
                            help='Azure resource group of deployment', required=True)
    arg_parser.add_argument('--debug', default=False, required=False, type=lambda x: bool(strtobool(str(x))))

    parsed_args = arg_parser.parse_args()
    resource_group = parsed_args.resource_group
    debug_enabled = parsed_args.debug
    config: InstallConfiguration = InstallConfiguration.load()
    install_state = DeploymentState.load()
    if debug_enabled:
        az.DEBUG_ENABLED = True

    initialize_secrets(install_config=config, resource_group_name=resource_group)
    install_state.complete_stage(Stages.KEY_VAULT_SECRETS_SET)
    print("GDC secrets has been initialized successfully")
