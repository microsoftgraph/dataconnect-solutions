#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

from skills_finder_utils.az import az_cli


def activate_datafactory_trigger(resource_group: str, factory_name: str, trigger_name: str):
    az_cli("datafactory trigger start", "--factory-name", factory_name, "--resource-group", resource_group,
           "--name", trigger_name)