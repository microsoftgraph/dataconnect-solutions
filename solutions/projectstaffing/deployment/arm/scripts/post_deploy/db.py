#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import datetime
import os
import re
from os.path import join, dirname

from skills_finder_utils import ad_ops, blob_ops
from skills_finder_utils import arm_ops
from config import InstallConfiguration


def create_gdc_deployer_identity(install_config: InstallConfiguration, resource_group: str):
    admin_ad_group_name = install_config.gdc_admin_ad_group['ad_group_name']
    gdc_deployer = ad_ops.add_gdc_deployer_identity(resource_group=resource_group,
                                                    gdc_admin_ad_group=admin_ad_group_name,
                                                    identity_name="gdc-deployer")
    install_config.gdc_deployer_identity = {
        "principalId": gdc_deployer["principalId"],
        "id": gdc_deployer["id"],
        "name": gdc_deployer["name"]
    }


def initialize_db_schema_via_arm(install_config: InstallConfiguration, template_base_uri: str, storage_sas: str,
                                 rs_group_name: str, custom_init_file: str):
    if not install_config.gdc_deployer_identity:
        create_gdc_deployer_identity(install_config=install_config, resource_group=rs_group_name)
    if custom_init_file:
        matcher = re.search("https:\/\/(\w+)\..*", template_base_uri, re.IGNORECASE)
        deployment_storage = matcher.group(1)
        blob_ops.copy_file(source_path=custom_init_file, resource_group=rs_group_name,
                           runtime_storage=deployment_storage,
                           dest_container_name="gdc-artifacts", dest_path="sql-server/custom-init.sql")
    else:
        print("No custom-init.sql provided")
    template_uri = template_base_uri + "sql-server/init_sql_schema.json" + "?" + storage_sas
    json_params = arm_ops.create_sql_init_arm_parameters(install_config=install_config, base_uri=template_base_uri,
                                                         sas_token=storage_sas)
    arm_params_json_file = os.path.join(install_config.get_gdc_dir(), "gdc_sql_arm_params.json")
    with open(arm_params_json_file, "w") as param_file:
        param_file.write(json_params)

    print("Validating SQL schema ARM configuration ...")
    arm_ops.validate_templates(template_uri=template_uri, param_file_path=arm_params_json_file,
                               resource_group=rs_group_name)
    print("Deploying SQL schema using managed identity %s " % str(install_config.gdc_deployer_identity.get('id')))
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

    gdc_service_user = "gdc-service"
    jgraph_user = install_config.appservice_name
    gdc_data_ingestion_mode = install_config.gdc_data_ingestion_mode
    if install_config.sql_auth:
        gdc_service_password = install_config.gdc_service_db_user_password
        jgraph_user_password = install_config.jgraph_db_user_password
        return """ 
            USE [gdc_database]
            GO
            
            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{gdc_service_user_string_literal}') BEGIN
                CREATE USER [{gdc_service_user_identifier}] WITH PASSWORD = '{gdc_service_password_string_literal}';
                ALTER ROLE db_datareader ADD MEMBER [{gdc_service_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{gdc_service_user_identifier}];
                ALTER ROLE db_ddladmin ADD MEMBER [{gdc_service_user_identifier}];
            END
            GO

            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{jgraph_user_string_literal}') BEGIN
                CREATE USER [{jgraph_user_identifier}] WITH PASSWORD = '{jgraph_user_password_escaped}';
                ALTER ROLE db_datareader ADD MEMBER [{jgraph_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{jgraph_user_identifier}];
            END
            GO

            GRANT EXECUTE ON OBJECT::[dbo].[find_recommended_employees] TO [{jgraph_user_identifier}];
            GO
            
            INSERT INTO ingestion_mode_switch_state(ingestion_mode, phase, paused)
                VALUES ('{gdc_data_ingestion_mode_string_literal}', 'completed', 0);
            GO

            """.format(gdc_service_user_identifier=gdc_service_user.replace("]", "]]"),
                       gdc_service_user_string_literal=gdc_service_user.replace("'", "''"),
                       gdc_service_password_string_literal=gdc_service_password.replace("'", "''"),
                       jgraph_user_identifier=jgraph_user.replace("]", "]]"),
                       jgraph_user_string_literal=jgraph_user.replace("'", "''"),
                       jgraph_user_password_escaped=jgraph_user_password.replace("'", "''"),
                       gdc_data_ingestion_mode_string_literal=gdc_data_ingestion_mode)
    else:
        return """ 
            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{gdc_service_user_string_literal}') BEGIN
                CREATE USER [{gdc_service_user_identifier}] FROM EXTERNAL PROVIDER;
                ALTER ROLE db_datareader ADD MEMBER [{gdc_service_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{gdc_service_user_identifier}];
                ALTER ROLE db_ddladmin ADD MEMBER [{gdc_service_user_identifier}];
            END
            GO

            IF NOT EXISTS(SELECT uid FROM DBO.SYSUSERS Where Name = '{jgraph_user_string_literal}') BEGIN
                CREATE USER [{jgraph_user_identifier}] FROM EXTERNAL PROVIDER;
                ALTER ROLE db_datareader ADD MEMBER [{jgraph_user_identifier}];
                ALTER ROLE db_datawriter ADD MEMBER [{jgraph_user_identifier}];
            END
            GO

            GRANT EXECUTE ON OBJECT::[dbo].[find_recommended_employees] TO [{jgraph_user_identifier}];
            GO
            
            INSERT INTO ingestion_mode_switch_state(ingestion_mode, phase, paused)
                VALUES ('{gdc_data_ingestion_mode_string_literal}', 'completed', 0);
            GO
            
            """.format(gdc_service_user_identifier=gdc_service_user.replace("]", "]]"),
                       gdc_service_user_string_literal=gdc_service_user.replace("'", "''"),
                       jgraph_user_identifier=jgraph_user.replace("]", "]]"),
                       jgraph_user_string_literal=jgraph_user.replace("'", "''"),
                       gdc_data_ingestion_mode_string_literal=gdc_data_ingestion_mode)
