#!/bin/bash

#
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT license. See LICENSE file in the project root for full license information.
#

set -e
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
echo "Staring sshd "

sed -i "s/SSH_PORT/$SSH_PORT/g" /etc/ssh/sshd_config
/usr/sbin/sshd

echo "Running Watercooler server with classpath , config folder ${JWC_CONF_DIR}"

ARGS="-XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/graphdataconnect/jwc ${EXTRA_OPTS}"

if [[ -n "${SERVICE_USER}" ]]; then
  if [[ -n "${JWC_LOG_DIR}" ]]; then
    mkdir -p ${JWC_LOG_DIR} && chown -R ${SERVICE_USER}  ${JWC_LOG_DIR}
  fi
  chown -R ${SERVICE_USER}  ${JWC_WORK_DIR}

  su "${SERVICE_USER}"  -c " java ${AGRS} -jar ${JWC_WORK_DIR}/jwc-core.jar --spring.config.additional-location=${JWC_CONF_DIR} "
else
  java "${AGRS}" -jar ${JWC_WORK_DIR}/jwc-core.jar --spring.config.additional-location="${JWC_CONF_DIR}"
fi
