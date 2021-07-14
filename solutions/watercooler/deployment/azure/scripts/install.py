#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

# 1. check/create AAD configuration
#   Pre-requirements:
#   +    * wc-admin AAD group
#   +    * wc-employee-export AAD group
#   +   * wc-service SP with Graph.ReadAll permission
#   +   * wc-m365-reader
#   +   * wc-jwc-aad-app web app registration with Graph.Read permissions
import argparse
import json
import os
from distutils.util import strtobool
from os.path import expanduser
import subprocess

from json import JSONDecodeError

import sys
from watercooler_utils import ad_ops
from watercooler_utils import arm_ops
from watercooler_utils import az
from config import InstallConfiguration
from watercooler_utils.common import lex_hash
from watercooler_utils.common import make_strong_password
from monitoring import DeploymentState, Stages


def init_active_directory_entities(deployment_name: str, install_config: InstallConfiguration,
                                   resource_group: str, tenant_id: str, non_interactive_mode: bool = False):
    print("Watercooler requires several records in your Active Directory. Let's verify them now... ")

    graph_user_read_permission = ad_ops.find_graph_user_read_all_role()
    if not graph_user_read_permission:
        raise RuntimeError("Couldn't find 'User.Read' permission in 'Microsoft Graph' for your tenant ")

    if not install_config.wc_admin_ad_group:
        if not non_interactive_mode:
            print("\nThe Watercooler admins group defines a list of AD users which are going to have Owner role over all Azure resources created by this deployment.")
            print("This Security group is mandatory and needs to be created before continuing. You can pause and create it now")

        provided_admin_group_id = install_config.get_provided_param_value("wcAdmins.groupId")
        admin_ad_group = ad_ops.prompt_or_create_ad_group("Enter the name or id of an existing Active Directory group for Watercooler admins: ",
                                                          add_signed_user=False, provided_ad_group_id=provided_admin_group_id,
                                                          no_input=non_interactive_mode, create_if_not_exists=False)

        install_config.wc_admin_ad_group = admin_ad_group

    if not install_config.wc_service_principal:
        wc_service_sp_name = None
        if non_interactive_mode:
            wc_service_sp_name = install_config.get_provided_param_value("wc-service-sp.name")
        if not wc_service_sp_name:
            wc_service_sp_name = install_config.appservice_name + "-wc-service"
        print("Creating %s service principal " % wc_service_sp_name)

        graph_read_all_role = ad_ops.find_graph_user_read_all_role()
        online_meetings_read_all_role = ad_ops.find_online_meetings_read_all()
        online_meetings_readwrite_all_role = ad_ops.find_online_meetings_readwrite_all()
        calendars_read_role = ad_ops.find_calendar_read()
        calendars_readwrite_role = ad_ops.find_calendar_readwrite()
        if not graph_read_all_role:
            raise RuntimeError("Couldn't find 'User.Read.All' permission in 'Microsoft Graph' for your tenant ")

        if not online_meetings_read_all_role:
            raise RuntimeError("Couldn't find 'OnlineMeetings.Read.All' permission in 'Microsoft Graph' for your tenant ")

        if not online_meetings_readwrite_all_role:
            raise RuntimeError("Couldn't find 'OnlineMeetings.ReadWrite.All' permission in 'Microsoft Graph' for your tenant ")

        if not calendars_read_role:
            raise RuntimeError("Couldn't find 'Calendars.Read' permission in 'Microsoft Graph' for your tenant ")

        if not calendars_readwrite_role:
            raise RuntimeError("Couldn't find 'Calendars.ReadWrite' permission in 'Microsoft Graph' for your tenant ")


        wc_sp = ad_ops.get_or_create_service_principal(wc_service_sp_name, tenant_id=tenant_id, non_interactive_mode=non_interactive_mode)
        install_config.wc_service_principal = wc_sp

        ad_ops.add_service_principal_app_permission(sp_app_id=wc_sp['appId'],
                                                    api_resource_id=graph_read_all_role['appId'],
                                                    permission_id=graph_read_all_role['id'])
        ad_ops.add_service_principal_app_permission(sp_app_id=wc_sp['appId'],
                                                    api_resource_id=online_meetings_read_all_role['appId'],
                                                    permission_id=online_meetings_read_all_role['id'])
        ad_ops.add_service_principal_app_permission(sp_app_id=wc_sp['appId'],
                                                    api_resource_id=online_meetings_readwrite_all_role['appId'],
                                                    permission_id=online_meetings_readwrite_all_role['id'])
        ad_ops.add_service_principal_app_permission(sp_app_id=wc_sp['appId'],
                                                    api_resource_id=calendars_read_role['appId'],
                                                    permission_id=calendars_read_role['id'])
        ad_ops.add_service_principal_app_permission(sp_app_id=wc_sp['appId'],
                                                    api_resource_id=calendars_readwrite_role['appId'],
                                                    permission_id=calendars_readwrite_role['id'])

    if not install_config.m365_reader_service_principal:
        wc_m365_reader_sp_name = None
        if non_interactive_mode:
            wc_m365_reader_sp_name = install_config.get_provided_param_value("wc-m365-reader-sp.name")
        if not wc_m365_reader_sp_name:
            wc_m365_reader_sp_name = install_config.appservice_name + "-wc-m365-reader"
        print("Creating %s service principal " % wc_m365_reader_sp_name)

        graph_user_read_all_role = ad_ops.find_graph_user_read_all_role()
        if not graph_user_read_all_role:
            raise RuntimeError("Couldn't find 'User.Read.All' permission in 'Microsoft Graph' for your tenant ")

        m365_reader_sp = ad_ops.get_or_create_service_principal(wc_m365_reader_sp_name, tenant_id=tenant_id, non_interactive_mode=non_interactive_mode)
        install_config.m365_reader_service_principal = m365_reader_sp
        ad_ops.add_service_principal_app_permission(sp_app_id=m365_reader_sp['appId'],
                                                    api_resource_id=graph_user_read_all_role['appId'],
                                                    permission_id=graph_user_read_all_role['id'])


        try:
            admin_group_members = ad_ops.get_group_members(group_object_id=install_config.wc_admin_ad_group["objectId"])
            for member in admin_group_members:
                ad_ops.make_user_owner_for_app(user_object_id=member['objectId'], app_id=m365_reader_sp['appId'])
        except Exception as azError:
            print("Failed to make members of admin group owners over %s service principal!" % wc_m365_reader_sp_name)
            print(azError)

    if not install_config.jwc_aad_app:
        app_registration_name = deployment_name + "-jwc-aad-web-app"
        print("Creating %s app registration " % app_registration_name)
        appservice_name = install_config.appservice_name
        jwc_aad_app = ad_ops. \
            get_or_create_service_principal(app_registration_name, is_web_app=True, credentials_valid_years=3,
                                            reply_url="https://%s.azurewebsites.net/.auth/login/aad/callback" % appservice_name,
                                            logout_url="https://%s.azurewebsites.net/.auth/logout" % appservice_name,
                                            tenant_id=tenant_id, non_interactive_mode=non_interactive_mode)
        install_config.jwc_aad_app = jwc_aad_app

    if not install_config.watercooler_user:
        print("Updating Watercooler Meetings Organizer user")
        watercooler_user_sp = ad_ops.get_watercooler_user()
        install_config.watercooler_user = watercooler_user_sp

    print('Creating custom application policy for creating online meetings on behalf of Watercooler Meetings Organizer')
    script_path = "../meetings/run_policy_assign.ps1"
    subprocess.call(["pwsh", script_path, str(install_config.watercooler_user['objectId']), str(install_config.wc_service_principal['appId'])])


def execute_user_prompts(deployment_name: str, install_config: InstallConfiguration, resource_group: str,
                         subscription_id: str, validate_default_params: bool = True):

    install_config.prompt_all_required(deployment_name=deployment_name, subscription_id=subscription_id,
                                       validate_default_params=validate_default_params)
    if install_config.sql_auth:
        # Generating service password without prompting, it will be saved in Az Key Vault.
        # Because the password will be saved in Az Key Vault using az cli,
        # only these special characters are allowed: #%^-_+{}\\:/~
        install_config.wc_service_db_user_password = make_strong_password(length=12, special_chars='#%^-_+{}\\:/~')
        install_config.jwc_db_user_password = make_strong_password(length=12, special_chars='#%^-_+{}\\:/~')


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
        log_analytic_ws_name = "wc-logs-" + lex_hash(deployment_name)
        install_config.log_analytics_workspace_name = log_analytic_ws_name
    current_user = ad_ops.get_loggedin_user(fields=["givenName", "surname", "mail", "userPrincipalName"])
    if not current_user:
        install_config.prompt_admin_contact_info(deployment_name=deployment_name)
        full_name = install_config.required_arm_params.get("alert.admin.fullname")
        alert_admin_email = install_config.required_arm_params.get("alert.admin.email")
    else:
        full_name = "%s %s" % (current_user['givenName'], current_user['surname'])
        alert_admin_email = current_user.get('mail') or current_user.get('userPrincipalName')

    meeting_organizer_email = str(install_config.watercooler_user['userPrincipalName'])
    json_params = arm_ops.create_main_arm_parameters(install_config=install_config, base_uri=template_base_uri,
                                                     sas_token=sas_token,
                                                     docker_login=docker_login,
                                                     app_version=install_config.appservice_version,
                                                     docker_password=docker_password,
                                                     log_analytic_ws_name=log_analytic_ws_name,
                                                     admin_full_name=full_name,
                                                     admin_email=alert_admin_email,
                                                     meeting_organizer_email=meeting_organizer_email)

    arm_params_json_file = os.path.join(install_config.get_wc_dir(), "wc_arm_params.json")
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

    # Create the parser
    arg_parser = argparse.ArgumentParser(description='Install Watercooler service')

    # Add the arguments
    arg_parser.add_argument('--deployment-name',
                            metavar='deployment-name',
                            type=str,
                            help='Name of deployment', required=True)
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
    arg_parser.add_argument("--parameter-file",
                            metavar='parameter-file',
                            type=str,
                            help='Default ARM parameters json file', required=False, default=None)

    arg_parser.add_argument('--log-analytic-enabled', default=False, required=False, type=lambda x: bool(strtobool(str(x))))
    arg_parser.add_argument('--debug', default=False, required=False, type=lambda x: bool(strtobool(str(x))))
    arg_parser.add_argument('--sql-auth', required=True, type=lambda x: bool(strtobool(str(x))),
                            help='SQL Server authentication mode for schema init: true for SQL Server mode, false for Windows Auth mode (via Active Directory)')
    arg_parser.add_argument('--no-input', default=False, required=False, type=lambda x: bool(strtobool(str(x))))

    parsed_args = arg_parser.parse_args()
    deployment_name = parsed_args.deployment_name
    tenant_id = parsed_args.tenant_id
    subscription_id = parsed_args.subscription_id
    resource_group = parsed_args.resource_group
    debug_enabled = parsed_args.debug
    parameter_file = parsed_args.parameter_file
    no_input = parsed_args.no_input

    if debug_enabled:
        az.DEBUG_ENABLED = True

    install_config: InstallConfiguration = InstallConfiguration.load(default_param_file=parameter_file)
    install_state = DeploymentState.load()
    install_config.sql_auth = parsed_args.sql_auth

    if install_state.is_user_prompts_taken():
        prompt_again = False
        if not no_input:
            prompt_again = install_state.prompt_stage_repeat("Previously entered values have been found. Would you like to ignore them and re-enter deployment parameters? (Y/n) ")

        if prompt_again:
            execute_user_prompts(deployment_name=deployment_name, install_config=install_config, resource_group=resource_group,
                                 subscription_id=subscription_id, validate_default_params=(not install_state.is_azure_resources_deployed()))
            install_state.complete_stage(Stages.USER_PROMPTS_TAKEN)
        else:
            print("Reusing previously entered parameters")
    else:
        execute_user_prompts(deployment_name=deployment_name, install_config=install_config, resource_group=resource_group, subscription_id=subscription_id)
        install_state.complete_stage(Stages.USER_PROMPTS_TAKEN)

    init_active_directory_entities(deployment_name=deployment_name, install_config=install_config,
                                   resource_group=resource_group, non_interactive_mode=no_input, tenant_id=tenant_id)
    print("Adding role assignment for %s on resource group %s" % (install_config.wc_admin_ad_group['ad_group_name'],
                                                                  resource_group))
    ad_ops.add_role_assigment(role="Owner", ad_group_id=install_config.wc_admin_ad_group["objectId"],
                              resource_group=resource_group)
    if not install_state.is_azure_resources_deployed():
        execute_deploy_mainTemplate(parsed_args)
        install_state.complete_stage(Stages.RESOURCES_DEPLOYMENT_DONE)
        print("Watercooler Azure resources have been created")
    else:
        print("Watercooler Azure resources had been already created. Skipping this stage")
