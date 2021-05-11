#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import logging
import base64
import hmac
import sys
import hashlib
from datetime import datetime
import requests
import json


class LogAnalyticsLogger:
    local_logger = None

    def __init__(self, name, workspace_id="", shared_key="", log_type="GDC_Pipeline", log_server_time=False):
        self.workspace_id = workspace_id.strip()
        self.shared_key = shared_key.strip()
        self.log_type = log_type
        self.log_server_time = log_server_time
        self.name = name
        logging.getLogger().setLevel(logging.DEBUG)
        console_handler = logging.StreamHandler(stream=sys.stdout)
        logging.getLogger().addHandler(console_handler)
        logging.getLogger("py4j").setLevel(logging.ERROR)
        self.local_logger = logging.getLogger(name)
        self.uri = 'https://' + self.workspace_id + '.ods.opinsights.azure.com/api/logs?api-version=2016-04-01'
        self.requestsSession = requests.session()

    def build_signature(self, workspace_id, shared_key, date, content_length, method, content_type, resource):
        x_headers = 'x-ms-date:' + date
        string_to_hash = method + "\n" + str(content_length) + "\n" + content_type + "\n" + x_headers + "\n" + resource
        bytes_to_hash = bytes(string_to_hash, encoding="utf-8")
        decoded_key = base64.b64decode(shared_key)
        encoded_hash = base64.b64encode(
            hmac.new(decoded_key, bytes_to_hash, digestmod=hashlib.sha256).digest()).decode()
        authorization = "SharedKey {}:{}".format(workspace_id, encoded_hash)
        return authorization

    def info(self, message):
        self.post_data(message, "Informational")

    def exception(self, message, ex, traceback=""):
        message = message + " " + str(ex) + " " + traceback
        self.error(message)

    def error(self, message):
        self.post_data(message, "ERROR")

    def debug(self, message):
        self.post_data(message, "DEBUG")

    def post_data(self, message, level):
        complete_message = self.name + " " + message
        if self.log_server_time:
            complete_message = datetime.utcnow().strftime('%d-%m-%Y %H:%M:%S') + " " + complete_message

        if level == "ERROR":
            self.local_logger.error(complete_message)
        elif level == "Informational":
            self.local_logger.info(complete_message)
        elif level == "DEBUG":
            self.local_logger.debug(complete_message)

        if self.workspace_id and self.shared_key:
            json_data = {
                "Message": complete_message,
                "Level": level
            }
            body = json.dumps(json_data)
            method = 'POST'
            content_type = 'application/json'
            resource = '/api/logs'
            rfc1123date = datetime.utcnow().strftime('%a, %d %b %Y %H:%M:%S GMT')
            content_length = len(body)
            signature = self.build_signature(self.workspace_id, self.shared_key, rfc1123date, content_length, method,
                                             content_type,
                                             resource)

            headers = {
                'content-type': content_type,
                'Authorization': signature,
                'Log-Type': self.log_type,
                'x-ms-date': rfc1123date
            }

            response = self.requestsSession.post(self.uri, data=body, headers=headers)
            if (response.status_code >= 200 and response.status_code <= 299):
                print('Accepted')
            else:
                print("Response code: {}".format(response.status_code))