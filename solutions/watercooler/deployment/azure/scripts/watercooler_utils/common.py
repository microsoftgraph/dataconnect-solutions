#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import hashlib
import re
import string
import uuid
import random
import json


from random import shuffle, choice

import requests

class AccessToken:
    def __init__(self, value):
        self.value = value

def is_valid_uuid(val):
    try:
        uuid.UUID(str(val))
        return True
    except ValueError:
        return False


def yes_no(answer):
    yes = {'yes', 'y', 'ye', ''}
    no = {'no', 'n'}

    while True:
        choice = input(answer).lower()
        print('\n')
        if choice in yes:
            return True
        elif choice in no:
            return False
        else:
            print("Please respond with 'yes' or 'no'\n")


def lex_hash(base: str, max_ln: int = 7):
    return str(hashlib.md5(base.encode("UTF-8")).hexdigest()[:max_ln])


def get_random_string(max_ln: int = 7):
    return ''.join(random.choice(string.ascii_lowercase + string.digits) for _ in range(max_ln))


def make_strong_password(length: int, special_chars: str = '#%^&-_+{}|\\:\'/`~"'):
    # Password defined so as to be compatible with Azure password definition rules in
    # https://docs.microsoft.com/en-us/sql/relational-databases/security/password-policy?view=sql-server-ver15
    # https://docs.microsoft.com/en-us/sql/relational-databases/security/strong-passwords?view=sql-server-ver15
    min_lowercase = 1
    min_uppercase = 1
    min_digits = 1
    min_special_chars = 1
    min_length = 8
    max_length = 128

    lower_chars = string.ascii_lowercase
    upper_chars = string.ascii_uppercase
    digits = string.digits
    all_chars = lower_chars + upper_chars + digits + special_chars

    collective_min_length = min_lowercase + min_uppercase + min_digits + min_special_chars

    if collective_min_length > min_length:
        min_length = collective_min_length

    if length < min_length or length > max_length:
        raise ValueError(f"Password length should be between {min_length} and {max_length} characters")

    password_char_list = list(choice(lower_chars) for _ in range(min_lowercase))
    password_char_list += list(choice(upper_chars) for _ in range(min_uppercase))
    password_char_list += list(choice(digits) for _ in range(min_digits))
    password_char_list += list(choice(special_chars) for _ in range(min_special_chars))

    current_password_length = len(password_char_list)

    if len(password_char_list) < length:
        password_char_list += list(choice(all_chars) for _ in range(length - current_password_length))

    # passwords generated using this method are usually saved in Azure Key Vault, using az cli
    # in order for the az cli command to work, the password can't start with the character '#' or '-'
    while password_char_list[0] in ['#', '-']:
        shuffle(password_char_list)

    return "".join(password_char_list)


def check_complex_password(password: str, subscription_id: str, token: AccessToken, print_validation_result: bool = True, retrying: bool = False):
    # https://docs.microsoft.com/en-us/sql/relational-databases/security/password-policy?view=sql-server-ver15
    if len(password) < 8:
        return False
    has_upper_case = 1 if re.search("[A-Z]", password) else 0
    has_lower_case = 1 if re.search("[a-z]", password) else 0
    has_numbers = 1 if re.search("[\d]", password) else 0
    has_nonalphas = 1 if re.search("\W", password) else 0
    if has_upper_case + has_lower_case + has_numbers + has_nonalphas < 3:
        return False

    return True


def is_sql_server_name_valid(sql_server_name: str, subscription_id: str, access_token: AccessToken, print_validation_result: bool = True, retrying: bool = False):

    payload = '{"name": "' + sql_server_name + '","type": "Microsoft.Sql/servers"}'
    headers = {"Authorization": f"Bearer {access_token.value}",
               "Content-Type": "application/json"}
    response = requests.post(
        url=f"https://management.azure.com/subscriptions/{subscription_id}/providers/Microsoft.Sql/checkNameAvailability?api-version=2014-04-01",
        data=payload, headers=headers)

    if response.status_code == 200:
        xml_response = response.text
        availability_index_xml = xml_response.index('Boolean">')
        availability = xml_response[availability_index_xml + 9: availability_index_xml + 13]
        if availability == 'true':
            return True
        else:
            return False
    elif response.status_code == 401:
        if print_validation_result:
            print("Request to check if resource name is available failed with Unauthorized status code!")
        from .ad_ops import _get_access_token
        az_access_token = _get_access_token(subscription_id=subscription_id)
        access_token.value = az_access_token
        if not retrying:
            is_azure_app_service_name_valid(sql_server_name, subscription_id, access_token, print_validation_result=True, retrying=True)
        else:
            return True
    else:
        return False


def is_sql_server_database_name_valid(database_name: str, subscription_id: str, access_token: AccessToken, print_validation_result: bool = True, retrying: bool = False):

    permitted_characters = string.ascii_lowercase + string.ascii_uppercase + string.digits + '-'
    for char in database_name:
        if char not in permitted_characters:
            if print_validation_result:
                print("Invalid character '", char, "' in database name")
            return False

    # the max limit of characters for Database name is 63
    # in the arm template the database name is constructed
    # by concatenating the server name with the database name parameter
    # officially the database name can have between 1-128 alphanumeric characters
    # (see https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/resource-name-rules#microsoftsql)
    # but because of this construction we will have to limit it to 64 characters
    if len(database_name) > 63 or len(database_name) < 2:
        if print_validation_result:
            print("App database name must be at least 1 characters and fewer than 63 characters")
        return False

    if database_name[0] == '-' or database_name[-1] == '-':
        if print_validation_result:
            print(f"The database name {database_name} is not available")
        return False

    return True


def is_search_service_name_valid(search_service_name: str, subscription_id: str, access_token: AccessToken, print_validation_result: bool = True, retrying: bool = False):

    payload = '{"name": "' + search_service_name + '","type": "searchServices"}'
    headers = {"Authorization": f"Bearer {access_token.value}",
               "Content-Type": "application/json"}
    response = requests.post(
        url=f"https://management.azure.com/subscriptions/{subscription_id}/providers/Microsoft.Search/checkNameAvailability?api-version=2020-08-01",
        data=payload, headers=headers)

    if response.status_code == 200:
        response_dict = json.loads(response.text)
        if 'message' in response_dict and response_dict['message'] and print_validation_result:
            print(response_dict['message'])
        return response_dict['nameAvailable']
    elif response.status_code == 401:
        if print_validation_result:
            print("Request to check if resource name is available failed with Unauthorized status code!")
        from .ad_ops import _get_access_token
        az_access_token = _get_access_token(subscription_id=subscription_id)
        access_token.value = az_access_token
        if not retrying:
            is_azure_app_service_name_valid(search_service_name, subscription_id, access_token, print_validation_result=True, retrying=True)
        else:
            return True
    else:
        return False


def is_azure_app_service_name_valid(app_name: str, subscription_id: str, access_token: AccessToken, print_validation_result: bool = True, retrying: bool = False):

    permitted_characters = string.ascii_lowercase + string.ascii_uppercase + string.digits + '-'
    for char in app_name:
        if char not in permitted_characters:
            if print_validation_result:
                print("Invalid character ", char, " in app service name")
            return False

    # the max limit of characters for App Service name is 60
    # but the App Service name parameter is also used as App Service Plan name, for which the max limit of characters is 40
    # so the maximum character length for this parameter is 40 - len("-app-plan") = 31 , see deployment/arm/app-service/azuredeploy.json line 140
    if len(app_name) > 31 or len(app_name) < 2:
        if print_validation_result:
            print("App service name must be at least 2 characters and fewer than 31 characters")
        return False

    if app_name[0] == '-' or app_name[-1] == '-':
        if print_validation_result:
            print(f"The app name {app_name} is not available")
        return False

    payload = '{"name": "' + app_name + '","type": "Microsoft.Web/sites"}'
    headers = {"Authorization": f"Bearer {access_token.value}",
               "Content-Type": "application/json"}
    response = requests.post(
        url=f"https://management.azure.com/subscriptions/{subscription_id}/providers/Microsoft.Web/checkNameAvailability?api-version=2019-08-01",
        data=payload, headers=headers)

    if response.status_code == 200:
        response_dict = json.loads(response.text)
        if 'message' in response_dict and print_validation_result:
            print(response_dict['message'])
        return response_dict['nameAvailable']
    elif response.status_code == 401:
        if print_validation_result:
            print("Request to check if resource name is available failed with Unauthorized status code!")
        from .ad_ops import _get_access_token
        az_access_token = _get_access_token(subscription_id=subscription_id)
        access_token.value = az_access_token
        if not retrying:
            is_azure_app_service_name_valid(app_name, subscription_id, access_token, print_validation_result=True, retrying=True)
        else:
            return True
    else:
        return False


def is_key_vault_resource_name_valid(key_valut_name: str, subscription_id: str, access_token: AccessToken, print_validation_result: bool = True, retrying: bool = False):
    payload = '{"name": "' + key_valut_name + '","type": "Microsoft.KeyVault/vaults"}'
    headers = {"Authorization": f"Bearer {access_token.value}",
               "Content-Type": "application/json"}
    response = requests.post(
        url=f"https://management.azure.com/subscriptions/{subscription_id}/providers/Microsoft.KeyVault/checkNameAvailability?api-version=2019-09-01",
        data=payload, headers=headers)

    if response.status_code == 200:
        response_dict = json.loads(response.text)
        if 'message' in response_dict and print_validation_result:
            print(response_dict['message'])
        return response_dict['nameAvailable']
    elif response.status_code == 401:
        if print_validation_result:
            print("Request to check if resource name is available failed with Unauthorized status code!")
        from .ad_ops import _get_access_token
        az_access_token = _get_access_token(subscription_id=subscription_id)
        access_token.value = az_access_token
        if not retrying:
            is_azure_app_service_name_valid(key_valut_name, subscription_id, access_token, print_validation_result=True, retrying=True)
        else:
            return True
    else:
        return False


def is_storage_account_name_valid(storage_account_name: str, subscription_id: str, access_token: AccessToken, print_validation_result: bool = True, retrying: bool = False):
    payload = '{"name": "' + storage_account_name + '","type": "Microsoft.Storage/storageAccounts"}'
    headers = {"Authorization": f"Bearer {access_token.value}",
               "Content-Type": "application/json"}
    response = requests.post(
        url=f"https://management.azure.com/subscriptions/{subscription_id}/providers/Microsoft.Storage/checkNameAvailability?api-version=2021-04-01",
        data=payload, headers=headers)

    if response.status_code == 200:
        response_dict = json.loads(response.text)
        if 'message' in response_dict and print_validation_result:
            print(response_dict['message'])
        return response_dict['nameAvailable']
    elif response.status_code == 401:
        if print_validation_result:
            print("Request to check if resource name is available failed with Unauthorized status code!")
        from .ad_ops import _get_access_token
        az_access_token = _get_access_token(subscription_id=subscription_id)
        access_token.value = az_access_token
        if not retrying:
            is_azure_app_service_name_valid(storage_account_name, subscription_id, access_token, print_validation_result=True, retrying=True)
        else:
            return True
    else:
        return False


def is_data_factory_name_valid(data_factory_name: str, subscription_id: str, access_token: AccessToken, print_validation_result: bool = True, retrying: bool = False):
    # the check are made according to the documentation
    # see https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/resource-name-rules#microsoftdatafactory

    permitted_characters = string.ascii_lowercase + string.ascii_uppercase + string.digits + '-'
    for char in data_factory_name:
        if char not in permitted_characters:
            if print_validation_result:
                print("Invalid character ", char, " in data factory name")
            return False

    if len(data_factory_name) > 63 or len(data_factory_name) < 3:
        if print_validation_result:
            print("Data factory name must be at least 3 characters and fewer than 63 characters")
        return False

    if data_factory_name[0] == '-' or data_factory_name[-1] == '-':
        if print_validation_result:
            print(f"The data factory name {data_factory_name} is not available")
        return False

    return True

