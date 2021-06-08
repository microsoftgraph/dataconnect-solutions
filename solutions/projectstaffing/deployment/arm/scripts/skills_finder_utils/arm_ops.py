#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import base64
import json
import os
import requests
from databricks_api import DatabricksAPI
from datetime import datetime, timedelta
from os import listdir
from os.path import isfile, isdir, join, basename
from requests import HTTPError
from retry import retry
from time import sleep
from urllib.parse import urljoin

from skills_finder_utils.ad_ops import get_service_principal_object_id
from skills_finder_utils.az import az_cli
from config import InstallConfiguration


def get_search_admin_token(resource_group: str, azure_search_name: str):
    return az_cli("search admin-key show", "--resource-group", resource_group, "--service-name", azure_search_name,
                  "--query", "primaryKey")


def get_search_query_token(resource_group: str, azure_search_name: str, key_name: str):
    rsp = az_cli("search query-key list", "--resource-group", resource_group, "--service-name", azure_search_name,
                 "--query", "[?name == '%s']" % key_name)
    if len(rsp) == 1:
        return rsp[0].get('key')

    return None


def create_search_query_key(resource_group: str, azure_search_name: str, key_name: str):
    rsp = az_cli("search query-key create", "--resource-group", resource_group, "--service-name", azure_search_name,
                 "--name", key_name)
    return rsp.get('key')


def create_index(endpoint: str, admin_key: str, index_definition_json: dict):
    index_name = index_definition_json["name"]
    url = urljoin(endpoint, "indexes/" + index_name + "?api-version=2020-06-30")
    headers = {
                'Content-Type': 'application/json',
                'api-key': admin_key
               }
    response = requests.put(url, headers=headers, json=index_definition_json)
    status_code = response.status_code
    if 400 <= status_code < 500:
        msg = response.text
        raise RuntimeError("HTTPError: %s , msg: %s" % (status_code, msg))
    return response


def get_databricks_workspace_url(resource_group: str, ws_name: str):
    return az_cli("databricks workspace show", "--name", ws_name, "--resource-group", resource_group,
                  "--query", "workspaceUrl")


def get_databricks_workspace_id(resource_group: str, ws_name: str):
    return az_cli("databricks workspace show", "--name", ws_name, "--resource-group", resource_group,
                  "--query", "id")


def validate_templates(template_uri: str, param_file_path: str, resource_group: str):
    return az_cli("deployment group validate", "--resource-group", resource_group, "--template-uri",
                  template_uri, "--parameters", param_file_path)


def deploy_arm_template(template_uri: str, param_file_path: str, resource_group: str):
    return az_cli("deployment group create", "--resource-group", resource_group,
                  "--template-uri", template_uri, "--parameters", param_file_path)


def provision_databricks_cluster(install_config: InstallConfiguration,
                                 workspace_url: str, oauth_access_token: str,
                                 gdc_sp_secret_value: str,
                                 managed_libraries: list = None,
                                 gdc_sp_secret_name: str = "gdc-service-principal-secret",
                                 gdc_graph_api_sp_secret_name = "graph-api-service-principal-secret",
                                 secret_scope_name: str = "gdc",
                                 adb_cluster_name: str = "default-gdc-cluster",
                                 max_worker: int = 2,
                                 node_type_id: str = "Standard_DS3_v2",
                                 autotermination_minutes: int = 60):
    """

    :param managed_libraries: list of json object in format https://docs.databricks.com/dev-tools/api/latest/libraries.html#example-request
    :param workspace_url:
    :param oauth_access_token:
    :param gdc_sp_secret_value:
    :param gdc_sp_secret_name:
    :param secret_scope_name:
    :param adb_cluster_name:
    :param max_worker:
    :param node_type_id:
    :param autotermination_minutes:
    :return:  dict {
        "cluster_id": cluster_id,
        "api_token": adb_api_token
    }
    """
    print("Provisioning ADB cluster ...")
    assert oauth_access_token is not None
    adb_client = DatabricksAPI(host=workspace_url, token=oauth_access_token)
    scopes = adb_client.secret.list_scopes().get("scopes", [])
    if not any(x for x in scopes if x.get("name") == secret_scope_name):
        adb_client.secret.create_scope(scope=secret_scope_name,
                                       initial_manage_principal="users")

    adb_client.secret.put_secret(scope=secret_scope_name, key=gdc_sp_secret_name, string_value=gdc_sp_secret_value)
    # both databricks jobs use gdc-service service principal to access Graph API and other component
    # but we've introduce two secrets for flexibility even thought they have same value for now
    adb_client.secret.put_secret(scope=secret_scope_name, key=gdc_graph_api_sp_secret_name,
                                 string_value=gdc_sp_secret_value)

    adb_api_token = adb_client.token.create_token(comment="GDC Pipeline API token")
    cluster_id = None
    clusters = adb_client.cluster.list_clusters().get("clusters", [])
    cluster_rsp = list([x for x in clusters if x.get("cluster_name") == adb_cluster_name])
    if not cluster_rsp:
        print("Creating a new cluster %s" % adb_cluster_name)
        cluster_rsp = adb_client.cluster.create_cluster(cluster_name=adb_cluster_name, autoscale={
            "min_workers": 1,
            "max_workers": max_worker
        }, node_type_id=node_type_id, driver_node_type_id=node_type_id,
                                                        autotermination_minutes=autotermination_minutes,
                                                        enable_elastic_disk=True,
                                                        spark_version="6.6.x-scala2.11")
    else:
        print("Cluster %s exists at %s" % (adb_cluster_name, workspace_url))
        cluster_rsp = cluster_rsp[0]

    # capture cluster details as soon as it's available
    install_config.adb_cluster_details = {
        "cluster_id": cluster_rsp['cluster_id'],
        "api_token": adb_api_token
    }
    cluster_id = cluster_rsp['cluster_id']
    if managed_libraries:
        cluster_info = adb_client.cluster.get_cluster(cluster_id=cluster_id)
        cluster_state = cluster_info['state']
        # possible values PENDING, TERMINATED and RUNNING
        if cluster_state == "TERMINATED":
            print("Starting cluster %s " % cluster_id)
            adb_client.cluster.start_cluster(cluster_id=cluster_id)

        cluster_state = "PENDING"
        while cluster_state == "PENDING" or cluster_state == "RESTARTING" or cluster_state == "RESIZING":
            print("Waiting cluster %s " % cluster_id)
            sleep(5)
            cluster_info = adb_client.cluster.get_cluster(cluster_id=cluster_id)
            cluster_state = cluster_info['state']
            print("Cluster is now in state %s " % cluster_state)

        if cluster_state == "TERMINATING" or cluster_state == "TERMINATED" or cluster_state == "ERROR":
            print("Can't install managed libraries, cluster %s is not running" % cluster_id)
            raise RuntimeError("Can't install managed libraries, cluster %s is not running. Check Databricks Workspace Portal for details and  try again later" % cluster_id)
        else:
            try:
                print("Installing managed libraries on cluster %s " % cluster_id)
                install_managed_libraries(adb_client, cluster_id, managed_libraries)
            except BaseException as e:
                print("Failed to install libraries into cluster %s " % cluster_id)
                print(e)

    return {
        "cluster_id": cluster_id,
        "api_token": adb_api_token
    }


@retry(tries=5, delay=1, backoff=2)
def install_managed_libraries(adb_client, cluster_id, managed_libraries):
    adb_client.managed_library.install_libraries(cluster_id=cluster_id, libraries=managed_libraries)


@retry(tries=5, delay=1, backoff=2)
def _upload_multipart(adb_client: DatabricksAPI, local_file: str, dbfs_dir_path: str):
    assert os.path.exists(local_file)
    dest_file = join(dbfs_dir_path, basename(local_file))
    local_file_size = os.path.getsize(local_file)
    try:
        status_rsp = adb_client.dbfs.get_status(path=dest_file)
        if status_rsp["file_size"] != local_file_size:
            adb_client.dbfs.delete(path=dest_file)
        else:
            print("%s exists at %s . Skipping..." % (local_file, dest_file))
            return
    except HTTPError as er:
        pass

    handle = adb_client.dbfs.create(path=dest_file)['handle']
    with open(local_file, "rb") as f:
        while True:
            # A block can be at most 1MB
            block = f.read(1 << 20)
            if not block:
                break
            data = base64.standard_b64encode(block)
            adb_client.dbfs.add_block(handle=handle, data=data.decode("utf-8"))
    # close the handle to finish uploading
    adb_client.dbfs.close(handle=handle)


def upload_artifacts(workspace_url: str, oauth_access_token: str, local_artifacts_path: str, dbfs_dir_path):
    adb_client = DatabricksAPI(host=workspace_url, token=oauth_access_token)
    files_to_upload = []
    if isdir(local_artifacts_path):
        files_to_upload = [join(local_artifacts_path, f) for f in listdir(local_artifacts_path) if isfile(join(local_artifacts_path, f))]
    else:
        files_to_upload = [local_artifacts_path]
    dbfs_folder_exists = False
    try:
        status_rsp = adb_client.dbfs.get_status(path=dbfs_dir_path)
        dbfs_folder_exists = status_rsp["is_dir"]
    except HTTPError as er:
        if er.response.status_code == 404:
            dbfs_folder_exists = False
        else:
            raise er

    if not dbfs_folder_exists:
        print("Creating destination directory on DBFS %s " % dbfs_dir_path)
        adb_client.dbfs.mkdirs(path=dbfs_dir_path)

    for local_file in files_to_upload:
        print("Uploading %s ..." % local_file)
        _upload_multipart(adb_client, local_file, dbfs_dir_path)


@retry(tries=5, delay=1, backoff=2)
def update_linked_service(resource_group: str, factory_name: str, service_name: str, prop_name: str, value: str):
    az_cli("datafactory linked-service update", "--resource-group", resource_group,
           "--factory-name", factory_name, "--linked-service-name", service_name, "--set", "%s=%s" % (prop_name, value))


def restart_web_app(resource_group: str, app_name: str):
    az_cli("webapp restart", "--resource-group", resource_group,
           "--name", app_name)


def enable_webapp_alert(resource_group: str, alert_name: str, enabled: bool = True):
    try:
        # This call will fail if alert does not exist
        az_cli("monitor metrics alert show", "--resource-group", resource_group, "--name", alert_name)
        return az_cli("monitor metrics alert update", "--resource-group", resource_group, "--name", alert_name, "--enabled", str(enabled).lower())
    except BaseException as er:
        return None


def find_log_workspace_id(resource_group: str, workspace_name: str):
    return az_cli("monitor log-analytics workspace show", "--resource-group", resource_group,
                  "--workspace-name", workspace_name, "--query", "customerId")


def get_log_workspace_key(resource_group: str, workspace_name: str):
    return az_cli("monitor log-analytics workspace get-shared-keys", "--resource-group", resource_group,
                  "--workspace-name", workspace_name, "--query", "primarySharedKey")


def _get_earliest_6am_in_past(utc_now):
    pipeline_start_time = utc_now
    if utc_now.hour > 6:
        pipeline_start_time = datetime(utc_now.year, utc_now.month, utc_now.day, 6, 0, 0, 0, tzinfo=utc_now.tzinfo)
    else:
        yesterday = utc_now - timedelta(days=1)
        pipeline_start_time = datetime(yesterday.year, yesterday.month, yesterday.day, 6, 0, 0, 0,
                                       tzinfo=utc_now.tzinfo)
    pipeline_start_time_str = pipeline_start_time.strftime('%Y-%m-%dT%H:%M:%SZ')
    return pipeline_start_time_str


def create_main_arm_parameters(install_config: InstallConfiguration, base_uri: str = None, sas_token: str = None, docker_login: str = None,
                               docker_password: str = None, app_version: str = "1.0.2",
                               log_analytic_ws_name: str = None, admin_full_name: str = None, admin_email: str = None):
    parameters = dict()
    for key, value in install_config.required_arm_params.items():
        parameters[key] = {
            "value": value
        }
    parameters["appservice.version"] = {
        "value": app_version
    }
    if base_uri:
        parameters["_artifactsLocation"] = {
            "value": base_uri
        }
    if sas_token:
        _artifactsLocationSasToken = sas_token
        if not sas_token.startswith("?"):
            _artifactsLocationSasToken = "?" + sas_token

        parameters["_artifactsLocationSasToken"] = {
            "value": _artifactsLocationSasToken
        }
    if docker_login:
        parameters["docker.login"] = {
            "value": docker_login
        }
    if docker_password:
        parameters["docker.password"] = {
            "value": docker_password
        }
    if install_config.jgraph_aad_app and "appId" in install_config.jgraph_aad_app:
        parameters["appservice.aad.clientId"] = {
            "value": install_config.jgraph_aad_app["appId"]
        }
        parameters["appservice.aad.clientSecret"] = {
            "value": install_config.jgraph_aad_app["password"]
        }
    if install_config.gdc_service_principal and "appId" in install_config.gdc_service_principal:
        parameters["gdc-service.sp.clientId"] = {
            "value": install_config.gdc_service_principal["appId"]
        }
        if "gdc-service.sp.clientSecret" in install_config.arm_params:
            parameters["gdc-service.sp.clientSecret"] = {
                "value": install_config.gdc_service_principal["password"]
            }
        if "gdc-service.sp.objectId" in install_config.arm_params:
            parameters["gdc-service.sp.objectId"] = {
                "value": get_service_principal_object_id(app_id=install_config.gdc_service_principal["appId"])
            }
    if install_config.gdc_admin_ad_group and "objectId" in install_config.gdc_admin_ad_group:
        parameters["sqlsever.database.aad.admin-object-id"] = {
            "value": install_config.gdc_admin_ad_group["objectId"]
        }
        parameters["sqlsever.database.aad.admin-login"] = {
            "value": install_config.gdc_admin_ad_group["ad_group_name"]
        }
    if install_config.m365_reader_service_principal and "appId" in install_config.m365_reader_service_principal:
        parameters['m365Adf-reader.sp.clientId'] = {
            "value": install_config.m365_reader_service_principal['appId']
        }
        if "365Adf-reader.sp.objectId" in install_config.arm_params:
            parameters["365Adf-reader.sp.objectId"] = {
                "value": get_service_principal_object_id(app_id=install_config.m365_reader_service_principal["objectId"])
            }
    if install_config.gdc_employees_ad_group and "objectId" in install_config.gdc_employees_ad_group:
        parameters['gdc_employees_ad_group_id'] = {
            "value": install_config.gdc_employees_ad_group['objectId']
        }
    if log_analytic_ws_name:
        parameters['logs.workspace.name'] = {
            "value": log_analytic_ws_name
        }
    if admin_full_name:
        parameters['alert.admin.fullname'] = {
            "value": admin_full_name
        }
    if admin_email:
        parameters['alert.admin.email'] = {
            "value": admin_email
        }
    parameters['sqlsever.sql-auth'] = {
        "value": install_config.sql_auth
    }
    utc_now = datetime.utcnow()
    pipeline_start_time_str = _get_earliest_6am_in_past(utc_now)

    parameters['pipeline_start_time'] = {
        "value": pipeline_start_time_str
    }

    parameters['pygraph_utils_library.name'] = {
        "value": install_config.get_pygraph_utils_library_name()
    }

    parameters["gdcAdmins.groupId"] = {
        "value": install_config.gdc_admin_ad_group['objectId']
    }

    json_data = {
        "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#",
        "contentVersion": "1.0.0.0",
        "parameters": parameters
    }
    return json.dumps(json_data, indent=4, sort_keys=True)


def create_sql_init_arm_parameters(install_config: InstallConfiguration, base_uri: str, sas_token: str = None):
    parameters = dict()

    if base_uri:
        parameters["_artifactsLocation"] = {
            "value": base_uri
        }
    if sas_token:
        _artifactsLocationSasToken = sas_token
        if not sas_token.startswith("?"):
            _artifactsLocationSasToken = "?" + sas_token

        parameters["_artifactsLocationSasToken"] = {
            "value": _artifactsLocationSasToken
        }

    if "appservice.name" in install_config.required_arm_params:
        parameters["appservice.name"] = {
            "value": install_config.required_arm_params["appservice.name"]
        }
    if "sqlserver.name" in install_config.required_arm_params:
        parameters["sqlserver.name"] = {
            "value": install_config.required_arm_params["sqlserver.name"]
        }
    if install_config.gdc_service_principal and "name" in install_config.gdc_service_principal:
        parameters['gdc-service.sp.name'] = {
            "value": install_config.gdc_service_principal['name']
        }

    if install_config.gdc_deployer_identity:
        parameters['userIdentityName'] = {
            "value": install_config.gdc_deployer_identity['name']
        }

    json_data = {
        "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#",
        "contentVersion": "1.0.0.0",
        "parameters": parameters
    }
    return json.dumps(json_data, indent=4, sort_keys=True)
