#!/usr/bin/env bash

if [ "a$1" == "a" ] && [ "a$2" == "a" ]; then
    echo "Incorrect number of command line arguments!"
    echo "Usage: $0 <env access token> <url>"
    exit 1
fi

mvn clean test -DAppServiceAuthSession=$1 -DAppServiceUrl=$2 -Dretry
mvn surefire-report:report-only