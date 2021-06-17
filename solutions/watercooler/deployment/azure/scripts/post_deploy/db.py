#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import datetime
import os
import re
from os.path import join, dirname

from watercooler_utils import ad_ops, blob_ops
from watercooler_utils import arm_ops
from config import InstallConfiguration


def create_wc_deployer_identity(install_config: InstallConfiguration, resource_group: str):
    admin_ad_group_name = install_config.wc_admin_ad_group['ad_group_name']
    wc_deployer = ad_ops.add_wc_deployer_identity(resource_group=resource_group,
                                                    wc_admin_ad_group=admin_ad_group_name,
                                                    identity_name="wc-deployer")
    install_config.wc_deployer_identity = {
        "principalId": wc_deployer["principalId"],
        "id": wc_deployer["id"],
        "name": wc_deployer["name"]
    }


def initialize_db_schema_via_arm(install_config: InstallConfiguration, template_base_uri: str, storage_sas: str,
                                 rs_group_name: str, custom_init_file: str):
    if not install_config.wc_deployer_identity:
        create_wc_deployer_identity(install_config=install_config, resource_group=rs_group_name)
    if custom_init_file:
        matcher = re.search("https:\/\/(\w+)\..*", template_base_uri, re.IGNORECASE)
        deployment_storage = matcher.group(1)
        blob_ops.copy_file(source_path=custom_init_file, resource_group=rs_group_name,
                           runtime_storage=deployment_storage,
                           dest_container_name="wc-artifacts", dest_path="sql-server/custom-init.sql")
    else:
        print("No custom-init.sql provided")
    template_uri = template_base_uri + "sql-server/init_sql_schema.json" + "?" + storage_sas
    json_params = arm_ops.create_sql_init_arm_parameters(install_config=install_config, base_uri=template_base_uri,
                                                         sas_token=storage_sas)
    arm_params_json_file = os.path.join(install_config.get_wc_dir(), "wc_sql_arm_params.json")
    with open(arm_params_json_file, "w") as param_file:
        param_file.write(json_params)

    print("Validating SQL schema ARM configuration ...")
    arm_ops.validate_templates(template_uri=template_uri, param_file_path=arm_params_json_file,
                               resource_group=rs_group_name)
    print("Deploying SQL schema using managed identity %s " % str(install_config.wc_deployer_identity.get('id')))
    arm_ops.deploy_arm_template(template_uri=template_uri, param_file_path=arm_params_json_file,
                                resource_group=rs_group_name)


def initialize_db_schema_manually(install_config: InstallConfiguration):
    return True


def resolve_custom_sql_runtime_path():
    project_root = dirname(dirname(dirname(__file__)))
    sql_server_tmpl_dir = join(project_root, "sql-server")
    custom_init_file = os.path.join(sql_server_tmpl_dir, "custom-init.sql")
    return custom_init_file


def generate_custom_init_sql(install_config):
    # In AzureSql the DB user names can contain any symbols, including ' and " and [ and ]
    # In AzureSql identifiers can be quoted with [] or ""
    # In identifiers quoted with [], the [ character does not require escaping, however, ] must be escaped by doubling it
    # Only users that are mapped to Windows principals can contain the backslash character (\).
    # In AzureSql strings are quoted with ', can contain any special character and escaping is only needed for ' via ''
    # Therefore, below we shall handle such special characters in user names and passwords, depending on quotation

    wc_service_user = "wc-service"
    jwc_user = install_config.appservice_name
    if install_config.sql_auth:
        wc_service_password = install_config.wc_service_db_user_password
        jwc_user_password = install_config.jwc_db_user_password
        return """ 
            USE [wc_database]
            GO
            
            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{wc_service_user_string_literal}') BEGIN
                CREATE USER [{wc_service_user_identifier}] WITH PASSWORD = '{wc_service_password_string_literal}';
                ALTER ROLE db_datareader ADD MEMBER [{wc_service_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{wc_service_user_identifier}];
                ALTER ROLE db_ddladmin ADD MEMBER [{wc_service_user_identifier}];
            END
            GO

            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{jwc_user_string_literal}') BEGIN
                CREATE USER [{jwc_user_identifier}] WITH PASSWORD = '{jwc_user_password_escaped}';
                ALTER ROLE db_datareader ADD MEMBER [{jwc_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{jwc_user_identifier}];
            END
            GO


            """.format(wc_service_user_identifier=wc_service_user.replace("]", "]]"),
                       wc_service_user_string_literal=wc_service_user.replace("'", "''"),
                       wc_service_password_string_literal=wc_service_password.replace("'", "''"),
                       jwc_user_identifier=jwc_user.replace("]", "]]"),
                       jwc_user_string_literal=jwc_user.replace("'", "''"),
                       jwc_user_password_escaped=jwc_user_password.replace("'", "''"))
    else:
        return """ 
            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{wc_service_user_string_literal}') BEGIN
                CREATE USER [{wc_service_user_identifier}] FROM EXTERNAL PROVIDER;
                ALTER ROLE db_datareader ADD MEMBER [{wc_service_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{wc_service_user_identifier}];
                ALTER ROLE db_ddladmin ADD MEMBER [{wc_service_user_identifier}];
            END
            GO

            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{jwc_user_string_literal}') BEGIN
                CREATE USER [{jwc_user_identifier}] FROM EXTERNAL PROVIDER;
                ALTER ROLE db_datareader ADD MEMBER [{jwc_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{jwc_user_identifier}];
            END
            GO

            """.format(wc_service_user_identifier=wc_service_user.replace("]", "]]"),
                       wc_service_user_string_literal=wc_service_user.replace("'", "''"),
                       jwc_user_identifier=jwc_user.replace("]", "]]"),
                       jwc_user_string_literal=jwc_user.replace("'", "''"))
