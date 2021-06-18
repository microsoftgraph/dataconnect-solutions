#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import getpass
import json
import os
import uuid
from datetime import date
from time import sleep

from retry import retry
from watercooler_utils.az import az_cli
from watercooler_utils.common import is_valid_uuid
from watercooler_utils.common import make_strong_password
from watercooler_utils.common import yes_no


def prompt_or_create_ad_group(msg: str, tenant_id='default', provided_ad_group_id: str = None, create_if_not_exists=False,
                              no_input: bool = False, add_signed_user: bool = False):
    """
     Prompt for existing or create a new AD group
    :param msg:
    :param tenant_id:
    :param create_if_not_exists:
    :param add_signed_user:
    :return:
    """
    group_accepted = False
    ad_group = provided_ad_group_id
    while not group_accepted:
        if not ad_group:
            ad_group = input(msg)
            print('\n')

        if is_valid_uuid(ad_group):
            rsp = az_cli("ad group list ", "--filter", "objectId eq '%s' " % ad_group)
        else:
            rsp = az_cli("ad group list ", "--filter", "displayname eq '%s' " % ad_group)
        if len(rsp) > 1:
            print("There are multiple groups found in your %s tenant " % tenant_id)
            if create_if_not_exists:
                print("Please select unique name/objectId or enter new one to create: ")
            else:
                print("Please select unique name/objectId : ")
            print("ObjectId\tName ")
            for group in rsp:
                print("%s\t%s" % (group['objectId'], group['displayName']))
        elif len(rsp) == 1:
            group = rsp[0]
            if not no_input:
                group_accepted = yes_no("Selected group: %s, objectId: %s, confirm (y/n)" % (group['displayName'],
                                                                                             group['objectId']))
                # reset selection if not accepted to prompt again
                ad_group = ad_group if group_accepted else None
            else:
                # no confirmation needed for non-interactive mode
                group_accepted = True
        else:
            if create_if_not_exists:
                desc = "AD group for Watercooler app administrators. It manages full access to App resources and SQL database"
                az_cli("ad group create --display-name %s --mail-nickname %s " % (
                    ad_group, ad_group.lower()), "--description", desc
                       )
                group_accepted = True

    group = az_cli("ad group show ", "--group", ad_group)
    if add_signed_user:
        _add_signed_user_to_group(existing_group_name=ad_group)

    return {
        "ad_group_name": group['displayName'],
        "objectId": group['objectId']
    }


def add_wc_deployer_identity(resource_group: str, wc_admin_ad_group: str, identity_name: str = "wc-deployer"):
    group_id = az_cli("group show", "--name", resource_group, "--query", "id")
    id_principals = az_cli("identity list", "--resource-group", resource_group,  "--query", "[?name=='%s'].principalId" % identity_name)
    identity = dict()
    if len(id_principals) == 0:
        print("Creating user-assigned identity %s in resource group %s " % (identity_name, resource_group))
        identity = az_cli("identity create", "--resource-group",  resource_group, "--name", identity_name)
        sleep(5)
        rsp = az_cli("identity list", "--resource-group", resource_group,  "--query", "[?principalId=='%s']" % identity['principalId'])
        if not rsp:
            print("Warning: couldn't find wc-deployer identity ")
            # azure is slow.. let's wait more
            sleep(5)
    else:
        print("Identity %s exists already in group %s " % (identity_name, resource_group))
        identity = az_cli("identity show", "--resource-group", resource_group,  "--name", identity_name)

    _add_member_to_group(existing_group_name=wc_admin_ad_group, user_or_identity_obj_id=identity['principalId'])
    az_cli("role assignment create", "--role", "Contributor", "--assignee-object-id", identity['principalId'],
           "--scope", group_id)
    return identity


def _add_signed_user_to_group(existing_group_name: str):
    signed_user = az_cli("ad signed-in-user show", "--query", "objectId")
    _add_member_to_group(existing_group_name, signed_user)


@retry(tries=5, delay=1, backoff=2)
def _add_member_to_group(existing_group_name: str, user_or_identity_obj_id: str):
    is_member = az_cli("ad group member check", "--member-id", user_or_identity_obj_id, "--group",
                       existing_group_name, "--query", "value")
    if not is_member:
        az_cli("ad group member add", "--member-id", user_or_identity_obj_id, "--group",
               existing_group_name)


@retry(tries=5, delay=1, backoff=2)
def _update_logout_url(logout_url: str, app_id: str):
    az_cli("ad app update", "--id", app_id, "--set", "logoutUrl=\"%s\"" % logout_url)


@retry(tries=5, delay=1, backoff=2)
def _update_group_membership_claims(groupMembershipClaims: str, app_id: str):
    az_cli("ad app update", "--id", app_id, "--set", "groupMembershipClaims=\"%s\"" % groupMembershipClaims)


def _create_web_app_service_principal(display_name, reply_url, logout_url, credentials_valid_years: int = 1):
    graph_permission = find_graph_user_read_permission()
    azure_service_management_permission = find_azure_service_management_user_impersonation_permission()
    azure_storage_permission = find_azure_storage_user_impersonation_permission()
    if not graph_permission:
        raise RuntimeError("Couldn't find 'User.Read' permission reference")
    if not azure_service_management_permission:
        raise RuntimeError("Couldn't find 'user_impersonation' permission reference")
    tmp_file_name = "/tmp/" + str(uuid.uuid4()) + ".json"
    with open(tmp_file_name, mode="w") as fp:
        graphAppId = graph_permission['appId']
        graph_permission_id = graph_permission['id']
        azure_service_management_app_id = azure_service_management_permission['appId']
        azure_service_management_permission_id = azure_service_management_permission['id']
        azure_storage_app_id = azure_storage_permission['appId']
        azure_storage_permission_id = azure_storage_permission['id']
        permissions = [{
            "resourceAppId": graphAppId,
            "resourceAccess": [
                {
                    "id": graph_permission_id,
                    "type": "Scope"
                }
            ]
        },
            {
                "resourceAppId": azure_service_management_app_id,
                "resourceAccess": [
                    {
                        "id": azure_service_management_permission_id,
                        "type": "Scope"
                    }
                ]
            },
            {
                "resourceAppId": azure_storage_app_id,
                "resourceAccess": [
                    {
                        "id": azure_storage_permission_id,
                        "type": "Scope"
                    }
                ]
            }]
        json.dump(permissions, fp)
        fp.flush()
    try:
        password = make_strong_password(42, '/+')
        start_date = date.today()
        end_date = date(start_date.year + credentials_valid_years, start_date.month, start_date.day)
        rsp = az_cli("ad app create", "--available-to-other-tenants", "false", "--password", password,
                     "--display-name", display_name, "--end-date", str(end_date), "--reply-urls", reply_url,
                     "--required-resource-accesses", "@%s" % tmp_file_name)
        _update_logout_url(logout_url, app_id=rsp['appId'])
        _update_group_membership_claims('SecurityGroup', app_id=rsp['appId'])

        service_principal = {
            "appId": rsp['appId'],
            "password": password,
            "name": display_name
        }
        return service_principal
    finally:
        if os.path.exists(tmp_file_name):
            try:
                os.remove(tmp_file_name)
            except BaseException as e:
                pass


def get_watercooler_user():
    while True:
        mail = input("Please enter valid watercooler mail for calendar events mail sender account:")
        print("The following mail was introduced:"+str(mail))
        correct = yes_no("Is this correct?")
        if not correct:
            continue
        existing_users = az_cli("ad user list ", "--filter", "mail eq '%s' " % mail)
        if len(existing_users)==0:
            print("Given user was not found")
            continue
        else:
            user_json_info = existing_users[0]
            return user_json_info


def get_or_create_service_principal(name: str, tenant_id: str, non_interactive_mode: bool, create_if_not_exists: bool = True, credentials_valid_years: int = 1,
                                    is_web_app: bool = False, **kwargs):
    if is_web_app:
        assert 'reply_url' in kwargs
        assert 'logout_url' in kwargs

    if not non_interactive_mode:
        service_principal = dict()
        sp_accepted = False
        while not sp_accepted:
            existing_sps = az_cli("ad sp list ", "--all", "--filter", "displayName eq '%s' " % name, "--query", "[?contains(appOwnerTenantId, '%s')]" % tenant_id)
            if len(existing_sps) > 1:
                print("There are multiple service principal found in your tenant ")
                if create_if_not_exists:
                    print("Please select unique name/objectId or enter new one to create: ")
                else:
                    print("Please select unique name/objectId : ")
                print("appId\tName ")
                for sp in existing_sps:
                    print("%s\t%s" % (sp['appId'], sp['displayName']))
                app_id = input("If you want to use one of the service principals enter one of the app ids, otherwise press enter: ")
                selected_sp = None
                if app_id:
                    for sp in existing_sps:
                        if sp['appId'] == app_id:
                            selected_sp = sp
                            break
                else:
                    name = input("Enter a name for a new service principal: ")

                if selected_sp:
                    password = getpass.getpass(prompt="Service Principal Secret")
                    conf_password = getpass.getpass(prompt="Confirm Service Principal Secret")
                    service_principal = {
                        "appId": sp['appId'],
                        "password": password,
                        "name": name
                    }
                    sp_accepted = password != "" and password == conf_password
            elif len(existing_sps) == 1:
                sp = existing_sps[0]
                print("\nFound an existing service principal : %s, appId: %s. " % (sp['displayName'], sp['appId']))
                print("The service principal with this name is required for the deployment process. You can either reuse this service principal or abort the deployment.")
                print("Reusing it requires you to provide a service principal client secret. ")
                print("If you are an owner of the service principal, you also have the option of creating a new secret and providing that (e.g. if you don't know the current one).")
                sp_accepted = yes_no("Would you like to reuse the existing service principal? (y/n)")
                print("\n")
                if sp_accepted:
                    password = getpass.getpass(prompt="Service Principal Secret")
                    conf_password = getpass.getpass(prompt="Confirm Service Principal Secret")
                    service_principal = {
                        "appId": sp['appId'],
                        "password": password,
                        "name": name
                    }
                    sp_accepted = password != "" and password == conf_password
                else:
                    name = input("Enter a name different from %s, that will be used to create a new service principal: " % name)
            else:
                if create_if_not_exists:
                    if is_web_app:
                        service_principal = _create_web_app_service_principal(display_name=name,
                                                                              reply_url=kwargs['reply_url'],
                                                                              logout_url=kwargs['logout_url'],
                                                                              credentials_valid_years=credentials_valid_years)
                        sp_accepted = True
                    else:
                        sp = az_cli("ad sp create-for-rbac", "--name", name, "--years", str(credentials_valid_years))
                        service_principal = {
                            "appId": sp['appId'],
                            "password": sp['password'],
                            "name": name
                        }
                        sp_accepted = True
                else:
                    return None
    else:
        # if the application is running in non interactive mode even if there is a service principal
        # with the given name we will create a new one
        if is_web_app:
            service_principal = _create_web_app_service_principal(display_name=name,
                                                                  reply_url=kwargs['reply_url'],
                                                                  logout_url=kwargs['logout_url'],
                                                                  credentials_valid_years=credentials_valid_years)
        else:
            sp = az_cli("ad sp create-for-rbac", "--name", name, "--years", str(credentials_valid_years))
            service_principal = {
                "appId": sp['appId'],
                "password": sp['password'],
                "name": name
            }

    return service_principal


@retry(tries=5, delay=1, backoff=2)
def add_role_assigment(role: str, ad_group_id: str, resource_group: str):
    """
    Adds role assigment for ad_group_id on resourec group level
    :param role:
    :param ad_group_id: object ids for users, groups, service principals, and managed identities.
    :return:
    """
    rsp = az_cli("role assignment create", "--role", role, "--assignee-object-id", ad_group_id, "--resource-group",
                 resource_group)
    return rsp

def _find_build_in_role(aad_app_id: str, role_name: str = None, permission_name: str = None):
    if not role_name and not permission_name:
        raise ValueError("role_name or permission_name must be provided")
    if role_name and permission_name:
        raise ValueError("role_name or permission_name are mutual exclusive")

    app = az_cli("ad sp show", "--id", aad_app_id, "--query", "{Name:appDisplayName, Id:appId}")
    if app:
        app_id = app['Id']
        query = "appRoles[?value=='%s'].{Value:value, Id:id}" % role_name
        if permission_name:
            query = "oauth2Permissions[?value=='%s'].{Value:value, Id:id}" % permission_name
        roles_rsp = az_cli("ad sp show", "--id", app_id, "--query", query)
        if len(roles_rsp) == 1:
            role_id = roles_rsp[0]['Id']
            return {
                "appId": app_id,
                "id": role_id
            }
        else:
            print("Cant' find unique result for '%s' in '%s'" % (role_name, aad_app_id))

    return None


def find_graph_user_read_all_role():
    # az ad sp list --all --filter "displayName eq 'Microsoft Graph'" --query "[].appId"
    graph_app_id = "00000003-0000-0000-c000-000000000000"
    return _find_build_in_role(aad_app_id=graph_app_id, role_name='User.Read.All')

def find_online_meetings_read_all():
    graph_app_id = "00000003-0000-0000-c000-000000000000"
    return _find_build_in_role(aad_app_id=graph_app_id, role_name='OnlineMeetings.Read.All')

def find_online_meetings_readwrite_all():
    graph_app_id = "00000003-0000-0000-c000-000000000000"
    return _find_build_in_role(aad_app_id=graph_app_id, role_name='OnlineMeetings.ReadWrite.All')

def find_calendar_read():
    graph_app_id = "00000003-0000-0000-c000-000000000000"
    return _find_build_in_role(aad_app_id=graph_app_id, role_name='Calendars.Read')

def find_calendar_readwrite():
    graph_app_id = "00000003-0000-0000-c000-000000000000"
    return _find_build_in_role(aad_app_id=graph_app_id, role_name='Calendars.ReadWrite')

def find_graph_user_read_permission():
    # be aware https://go.microsoft.com/fwlink/?linkid=2132805
    # return _find_build_in_role(aad_app_id='Windows Azure Active Directory', permission_name='User.Read')

    # az ad sp list --all --filter "displayName eq 'Microsoft Graph'" --query "[].appId"
    graph_app_id = "00000003-0000-0000-c000-000000000000"
    return _find_build_in_role(aad_app_id=graph_app_id, permission_name='User.Read')


def find_azure_service_management_user_impersonation_permission():
    # az ad sp list --all --filter "displayName eq 'Windows Azure Service Management API'" --query "[].appId"
    azure_service_management_id = "797f4846-ba00-4fd7-ba43-dac1f8f63013"
    return _find_build_in_role(aad_app_id=azure_service_management_id, permission_name='user_impersonation')

def find_azure_storage_user_impersonation_permission():
    # az ad sp list --all --filter "displayName eq 'Azure Storage'" --query "[].appId"
    azure_storage_id = "e406a681-f3d4-42a8-90b6-c2b029497af1"
    return _find_build_in_role(aad_app_id=azure_storage_id, permission_name='user_impersonation')

@retry(tries=5, delay=1, backoff=2)
def add_service_principal_app_permission(sp_app_id: str, api_resource_id: str, permission_id: str):
    rsp = az_cli("ad app permission add", "--id", sp_app_id,
                 "--api", api_resource_id, "--api-permissions", "%s=Role" % permission_id)
    print(rsp)

@retry(tries=5, delay=1, backoff=2)
def get_group_members(group_object_id: str):
    return az_cli("ad group member list --group", group_object_id)

@retry(tries=5, delay=1, backoff=2)
def make_user_owner_for_app(user_object_id: str, app_id: str):
    return az_cli("ad app owner add --id", app_id, "--owner-object-id", user_object_id)


@retry(tries=5, delay=1, backoff=2)
def add_service_principal_delegated_permission(sp_app_id: str, api_resource_id: str, permission_id: str):
    rsp = az_cli("ad app permission add", "--id", sp_app_id,
                 "--api", api_resource_id, "--api-permissions", "%s=Scope" % permission_id)
    print(rsp)


@retry(tries=5, delay=1, backoff=2)
def _get_access_token(resource_id: str = None, subscription_id: str = None):
    rsp = dict()
    az_cli_args = []
    if resource_id:
        az_cli_args.extend(["--resource", resource_id])
    if subscription_id:
        az_cli_args.extend(["--subscription", subscription_id])
    rsp = az_cli(" account get-access-token ", *az_cli_args)
    print("The access token for resource %s expires at %s" % (resource_id, rsp.get('expiresOn')))
    return rsp.get('accessToken')


def get_mng_access_token():
    """
     Gets Azure Resource Manager access token of currenyly logged used.
      It can be used to manage Azure resources
    :return:
    """
    return _get_access_token()


def get_databricks_access_token(tenant_id: str = None, subscription_id: str = None):
    # az ad sp list --all --filter "displayName eq 'AzureDatabricks'" --query "[].appId"
    # The resource id for Azure Databricks is a constant value in Azure https://docs.microsoft.com/en-us/azure/databricks/dev-tools/api/latest/aad/service-prin-aad-token
    resource_id = "2ff814a6-3304-4ab8-85cb-cd0e6f879c1d"
    return _get_access_token(resource_id=resource_id, subscription_id=subscription_id)


def get_loggedin_user(fields: list):
    query = ""
    if fields:
        query = "{" + ",".join(list(map(lambda s: s + " : " + s, fields))) + " }"
    try:
        rsp = az_cli("ad signed-in-user show", "--query", query)
        return rsp
    except BaseException:
        print("WARN: Can't resolve signed in user, are you running as service principal?")
    return None


def get_service_principal_object_id(app_id: str):
    rsp = az_cli("ad sp show", "--id", app_id, "--query", "objectId")
    return rsp