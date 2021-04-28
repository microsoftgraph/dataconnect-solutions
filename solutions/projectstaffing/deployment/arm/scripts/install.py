# 1. check/create AAD configuration
#   Pre-requirements:
#   +    * gdc-admin AAD group
#   +    * gdc-employee-export AAD group
#   +   * gdc-service SP with Graph.ReadAll permission
#   +   * gdc-m365-reader
#   +   * gdc-jgraph-aad-app web app registration with Graph.Read permissions
import argparse
import json
import os
from distutils.util import strtobool
from os.path import expanduser

import sys
from skills_finder_utils import ad_ops
from skills_finder_utils import arm_ops
from skills_finder_utils import az
from config import InstallConfiguration
from skills_finder_utils.common import lex_hash
from skills_finder_utils.common import make_strong_password
from monitoring import DeploymentState, Stages


def init_active_directory_entities(deployment_name: str, install_config: InstallConfiguration, resource_group: str):
    print("GDC requires several records in your Active Directory. Let's verify it now... ")

    graph_user_read_permission = ad_ops.find_graph_user_read_all_role()
    if not graph_user_read_permission:
        raise RuntimeError("Couldn't find 'User.Read' permission in 'Microsoft Graph' for your tenant ")

    if not install_config.gdc_admin_ad_group:
        admin_ad_group = ad_ops.prompt_or_create_ad_group("Enter the name of an existing Active Directory group for GDC admins: ",
                                                          add_signed_user=False, create_if_not_exists=False)
        install_config.gdc_admin_ad_group = admin_ad_group

    if not install_config.gdc_employees_ad_group:
        employees_ad_group = ad_ops.prompt_or_create_ad_group("GDC is able to export employee data which belongs to a particular AD group. Enter the name of an existing Active Directory group for GDC employees: ",
                                                              add_signed_user=False, create_if_not_exists=False)
        install_config.gdc_employees_ad_group = employees_ad_group

    if not install_config.gdc_service_principal:
        print("Creating %s service principal " % "gdc-service")
        graph_read_all_role = ad_ops.find_graph_user_read_all_role()
        if not graph_read_all_role:
            raise RuntimeError("Couldn't find 'User.Read.All' permission in 'Microsoft Graph' for your tenant ")
        gdc_sp = ad_ops.get_or_create_service_principal("gdc-service")
        install_config.gdc_service_principal = gdc_sp
        ad_ops.add_service_principal_app_permission(sp_app_id=gdc_sp['appId'], api_resource_id=graph_read_all_role['appId'],
                                                    permission_id=graph_read_all_role['id'])

    if not install_config.m365_reader_service_principal:
        print("Creating %s service principal " % "gdc-m365-reader")
        graph_user_read_all_role = ad_ops.find_graph_user_read_all_role()
        if not graph_user_read_all_role:
            raise RuntimeError("Couldn't find 'User.Read.All' permission in 'Microsoft Graph' for your tenant ")
        graph_mail_read_role = ad_ops.find_graph_mail_read_role()
        if not graph_mail_read_role:
            raise RuntimeError("Couldn't find 'Mail.Read' permission in 'Microsoft Graph' for your tenant ")
        m365_reader_sp = ad_ops.get_or_create_service_principal("gdc-m365-reader")
        install_config.m365_reader_service_principal = m365_reader_sp
        ad_ops.add_service_principal_app_permission(sp_app_id=m365_reader_sp['appId'],
                                                    api_resource_id=graph_user_read_all_role['appId'],
                                                    permission_id=graph_user_read_all_role['id'])
        ad_ops.add_service_principal_app_permission(sp_app_id=m365_reader_sp['appId'],
                                                    api_resource_id=graph_mail_read_role['appId'],
                                                    permission_id=graph_mail_read_role['id'])

        try:
            admin_group_members = ad_ops.get_group_members(group_object_id=install_config.gdc_admin_ad_group["objectId"])
            for member in admin_group_members:
                ad_ops.make_user_owner_for_app(user_object_id=member['objectId'], app_id=m365_reader_sp['appId'])
        except Exception as azError:
            print("Failed to make members of admin group owners over gdc-m365-reader service principal!")
            print(azError)

    if not install_config.jgraph_aad_app:
        app_registration_name = deployment_name + "-jgraph-aad-web-app"
        print("Creating %s app registration " % app_registration_name)
        appservice_name = install_config.appservice_name
        jgraph_aad_app = ad_ops.\
            get_or_create_service_principal(app_registration_name, is_web_app=True, credentials_valid_years=3,
                                            reply_url="https://%s.azurewebsites.net/.auth/login/aad/callback" % appservice_name,
                                            logout_url="https://%s.azurewebsites.net/.auth/logout" % appservice_name)
        install_config.jgraph_aad_app = jgraph_aad_app


def execute_user_prompts(deployment_name: str, install_config: InstallConfiguration, resource_group: str):
    install_config.prompt_all_required(deployment_name=deployment_name)
    install_config.prompt_airtable_config(deployment_name=deployment_name)
    if install_config.sql_auth:
        # let's generate service password without prompting, it will be saved in key vaults
        install_config.gdc_service_db_user_password = make_strong_password(length=12)
        install_config.jgraph_db_user_password = make_strong_password(length=12)


def execute_deploy_mainTemplate(parsed_args):
    rs_group_name = parsed_args.resource_group
    template_base_uri = parsed_args.template_base_uri
    sas_token = parsed_args.sas_token
    docker_login = parsed_args.docker_login
    docker_password = parsed_args.docker_password
    log_analytic_enabled = parsed_args.log_analytic_enabled
    main_template_uri = template_base_uri + "mainTemplate.json" + "?" + sas_token
    log_analytic_ws_name = None
    if log_analytic_enabled:
        log_analytic_ws_name = "gdc-logs-" + lex_hash(deployment_name)
        install_config.log_analytics_workspace_name = log_analytic_ws_name
    current_user = ad_ops.get_loggedin_user(fields=["givenName", "surname", "mail", "userPrincipalName"])
    full_name = "%s %s" % (current_user['givenName'], current_user['surname'])
    admin_email = current_user.get('mail') or current_user.get('userPrincipalName')
    json_params = arm_ops.create_main_arm_parameters(install_config=install_config, base_uri=template_base_uri,
                                                     sas_token=sas_token,
                                                     docker_login=docker_login,
                                                     app_version=install_config.appservice_version,
                                                     docker_password=docker_password,
                                                     log_analytic_ws_name=log_analytic_ws_name,
                                                     admin_full_name=full_name, admin_email=admin_email)
    arm_params_json_file = os.path.join(install_config.get_gdc_dir(), "gdc_arm_params.json")
    with open(arm_params_json_file, "w") as param_file:
        param_file.write(json_params)
    print("Saved deployment parameters at %s " % arm_params_json_file)
    print("Validating ARM template with given parameters")
    arm_ops.validate_templates(template_uri=main_template_uri, param_file_path=arm_params_json_file,
                               resource_group=rs_group_name)
    print("Deploying ARM template mainTemplate.json")
    arm_ops.deploy_arm_template(template_uri=main_template_uri, param_file_path=arm_params_json_file,
                                resource_group=rs_group_name)


if __name__ == '__main__':
    args = sys.argv

    current_account = az.az_cli("account show")
    tenant_id = current_account['tenantId']
    subscription_id = current_account['id']
    subscription_name = current_account['name']
    install_config: InstallConfiguration = InstallConfiguration.load()
    install_state = DeploymentState.load()

    # Create the parser
    arg_parser = argparse.ArgumentParser(description='Install GDC service')

    # Add the arguments
    arg_parser.add_argument('--deployment-name',
                            metavar='deployment-name',
                            type=str,
                            help='Name of deployment', required=True)
    arg_parser.add_argument("--resource-group",
                            metavar='resource-group',
                            type=str,
                            help='Azure resource group of deployment', required=True)
    arg_parser.add_argument("--template-base-uri",
                            metavar='template-base-uri',
                            type=str,
                            help='Azure ARM template location', required=True)
    arg_parser.add_argument("--sas-token",
                            metavar='sas-token',
                            type=str,
                            help='SAS token to access Azure ARM templates', required=True)

    arg_parser.add_argument("--docker-login",
                            metavar='docker-login',
                            type=str,
                            help='Docker registry login', required=True)

    arg_parser.add_argument("--docker-password",
                            metavar='docker-password',
                            type=str,
                            help='Docker registry password', required=True)

    arg_parser.add_argument('--log-analytic-enabled', default=False, required=False, type=lambda x: bool(strtobool(str(x))))
    arg_parser.add_argument('--debug', default=False, required=False, type=lambda x: bool(strtobool(str(x))))
    arg_parser.add_argument('--sql-auth', required=True, type=lambda x: bool(strtobool(str(x))),
                            help='SQL Server authentication mode for schema init. true for SQL Server mode, false for Windows Auth mode( via Active Directory)')

    parsed_args = arg_parser.parse_args()
    deployment_name = parsed_args.deployment_name
    resource_group = parsed_args.resource_group
    debug_enabled = parsed_args.debug
    if debug_enabled:
        az.DEBUG_ENABLED = True

    install_config.sql_auth = parsed_args.sql_auth

    if install_state.is_user_prompts_taken():
        if install_state.prompt_stage_repeat("Previously entered values have been found. Would you like to re-enter deployment parameters? (Y/n) "):
            execute_user_prompts(deployment_name=deployment_name, install_config=install_config, resource_group=resource_group)
            install_state.complete_stage(Stages.USER_PROMPTS_TAKEN)
        else:
            print("Reusing previously entered parameters")
    else:
        execute_user_prompts(deployment_name=deployment_name, install_config=install_config, resource_group=resource_group)
        install_state.complete_stage(Stages.USER_PROMPTS_TAKEN)

    init_active_directory_entities(deployment_name=deployment_name, install_config=install_config, resource_group=resource_group)
    print("Adding role assignment for %s on resource group %s" % (install_config.gdc_admin_ad_group['ad_group_name'],
                                                                  resource_group))
    ad_ops.add_role_assigment(role="Owner", ad_group_id=install_config.gdc_admin_ad_group["objectId"],
                              resource_group=resource_group)
    if not install_state.is_azure_resources_deployed():
        execute_deploy_mainTemplate(parsed_args)
        install_state.complete_stage(Stages.RESOURCES_DEPLOYMENT_DONE)
        print("GDC Azure resources have been created")
    else:
        print("GDC Azure resources had been created already. Skipping this stage")


