import argparse
from distutils.util import strtobool

import sys

from skills_finder_utils import az
from config import InstallConfiguration
from monitoring import DeploymentState, Stages
from post_deploy.db import initialize_db_schema_via_arm, initialize_db_schema_manually
from post_deploy import db as db_init

if __name__ == '__main__':
    args = sys.argv

    # Create the parser
    arg_parser = argparse.ArgumentParser(description='Initialize GDC SQL schema ')

    arg_parser.add_argument("--resource-group",
                            metavar='resource-group',
                            type=str,
                            help='Azure resource group of deployment', required=False)
    arg_parser.add_argument("--template-base-uri",
                            metavar='template-base-uri',
                            type=str,
                            help='Azure ARM template location', required=False)
    arg_parser.add_argument("--sas-token",
                            metavar='sas-token',
                            type=str,
                            help='SAS token to access Azure ARM templates', required=False)
    arg_parser.add_argument("--mode",
                            metavar='mode',
                            choices=['auto', 'manual'],
                            type=str,
                            help='SQL Server schema initialization mode. auto uses ARM template automation ', required=True)

    arg_parser.add_argument('--debug', default=False, required=False, type=lambda x: bool(strtobool(str(x))))

    arg_parser.add_argument('--sql-auth', required=True, type=lambda x: bool(strtobool(str(x))),
                            help='SQL Server authentication mode for schema init. true for SQL Server mode, false for Windows Auth mode( via Active Directory)')
    arg_parser.add_argument('--force', required=False, default=False, type=lambda x: bool(strtobool(str(x))),
                            help="Perform operation regardless of previous run status")
    arg_parser.add_argument('--only-generate-schema', required=False, default=False, type=lambda x: bool(strtobool(str(x))),
                            help="Do not run schema initialization ")
    parsed_args = arg_parser.parse_args()
    init_mode = parsed_args.mode
    debug_enabled = parsed_args.debug
    config: InstallConfiguration = InstallConfiguration.load()
    config.sql_auth = parsed_args.sql_auth
    force_init = parsed_args.force
    only_generate_schema = parsed_args.only_generate_schema

    install_state = DeploymentState.load()

    if debug_enabled:
        az.DEBUG_ENABLED = True

    custom_init_sql = db_init.generate_custom_init_sql(install_config=config)
    custom_init_file = db_init.resolve_custom_sql_runtime_path()
    with open(custom_init_file, mode="w") as file:
        file.write(custom_init_sql)

    if not only_generate_schema and (not install_state.is_stage_completed(stage_name=Stages.SQL_SCHEMA_INITIALIZED) or force_init):

        # ---- database schema phase -----------------
        if init_mode == "auto":
            arm_base_uri = parsed_args.template_base_uri
            sas_token = parsed_args.sas_token
            resource_group = parsed_args.resource_group
            initialize_db_schema_via_arm(install_config=config, template_base_uri=arm_base_uri, storage_sas=sas_token,
                                         rs_group_name=resource_group)
            install_state.complete_stage(Stages.SQL_SCHEMA_INITIALIZED)
        elif init_mode == "manual":
            if initialize_db_schema_manually(install_config=config):
                install_state.complete_stage(Stages.SQL_SCHEMA_INITIALIZED)
            else:
                print("Database schema wasn't initialized")

