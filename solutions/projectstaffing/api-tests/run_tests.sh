#!/usr/bin/env bash

#
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT license. See LICENSE file in the project root for full license information.
#

if [ "a$1" == "a" ]; then
    echo "Incorrect number of command line arguments!"
    echo "Usage: $0 <url>"
    exit 1
fi

mvn clean test -DAppServiceUrl=$1 -Dretry
mvn surefire-report:report-only