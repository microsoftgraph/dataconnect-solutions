#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import os
import pickle
from os import path
from os.path import expanduser


from enum import Enum

from skills_finder_utils.common import yes_no


class Stages(Enum):
    USER_PROMPTS_TAKEN = 10
    RESOURCES_DEPLOYMENT_DONE = 20
    KEY_VAULT_SECRETS_SET = 30
    SQL_SCHEMA_INITIALIZED = 40
    DATABRICKS_CLUSTER_INITIALIZED = 50
    DEPLOYMENT_DONE = 60


class DeploymentState:

    __dump_file_name = expanduser("~") + "/.gdc/deployment.p"

    def __init__(self):
        self._completed_stages = set()

    def get_gdc_dir(self):
        return os.path.dirname(self.__dump_file_name)

    @classmethod
    def load(cls):
        if path.exists(DeploymentState.__dump_file_name):
            with open(DeploymentState.__dump_file_name, "rb") as param_file:
                config = pickle.load(param_file)
                return config
        else:
            return DeploymentState()

    def _save(self):
        if not os.path.exists(self.__dump_file_name):
            if not os.path.exists(os.path.dirname(self.__dump_file_name)):
                os.mkdir(os.path.dirname(self.__dump_file_name), )
        with open(DeploymentState.__dump_file_name, "wb") as param_file:
            pickle.dump(self, param_file)

    def is_stage_completed(self, stage_name: Stages):
        return stage_name in self._completed_stages

    def is_user_prompts_taken(self):
        return self.is_stage_completed(stage_name=Stages.USER_PROMPTS_TAKEN)

    def is_azure_resources_deployed(self):
        return self.is_stage_completed(stage_name=Stages.RESOURCES_DEPLOYMENT_DONE)

    def is_sql_schema_initialized(self):
        return self.is_stage_completed(stage_name=Stages.SQL_SCHEMA_INITIALIZED)

    def is_cluster_created(self):
        return self.is_stage_completed(stage_name=Stages.DATABRICKS_CLUSTER_INITIALIZED)

    def complete_stage(self, value: Stages):
        self._completed_stages.add(value)
        self._save()

    def prompt_stage_repeat(self, msg):
        return yes_no(answer=msg)
