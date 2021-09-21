#!/usr/bin/env bash


#
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT license. See LICENSE file in the project root for full license information.
#

function boolean() {
  case $1 in
    TRUE) echo true ;;
    true) echo true ;;
    FALSE) echo false ;;
    false) echo false ;;
    *) echo "Err: Unknown boolean value \"$1\"" 1>&2; exit 1 ;;
   esac
}

function yes_no_confirmation() {
  local MSG=$1
  local DEFAULT_ANSWER=$3
  if [[ -z "${NO_INPUT}" ]]; then
    while true; do
      read -p "$MSG" -r YN
      case $YN in
          [Yy]* )
            eval "$2=true"; break;;
          [Nn]* )
            eval "$2=false"; break;;
          * ) echo "Please answer yes/y/Y or no/n/N.";;
      esac
    done
  else
    if [[ -z "${DEFAULT_ANSWER}" ]]; then
      echo "ERROR: Enable to determine default value for prompt: $MSG"
      exit 6
    fi
    eval "$2=${DEFAULT_ANSWER}"
  fi
}

function prompt_non_empty_str_value() {
    MSG=$1
    DEFAULT_ANSWER="$3"
    if [[ -z "${NO_INPUT}" ]]; then
      while true; do
        read -p "$MSG" -r PROMPT_VALUE
        if [[ -n "${PROMPT_VALUE}" ]]; then
          eval "$2=\"${PROMPT_VALUE}\""
          break;
        else
          echo "Please enter non-empty "
        fi
      done
    else
      if [[ -z "${DEFAULT_ANSWER}" ]]; then
        echo "ERROR: Enable to determine default value for prompt: $MSG"
        exit 6
      fi
      echo "Non-interactive mode is enabled, setting default value to ${DEFAULT_ANSWER} "
      eval "$2=\"${DEFAULT_ANSWER}\""
    fi
}

function prompt_str_value() {
    MSG=$1
    DEFAULT_ANSWER="$3"
    if [[ -z "${NO_INPUT}" ]]; then
        read -p "$MSG" -r PROMPT_VALUE
        if [[ -n "${PROMPT_VALUE}" ]]; then
          eval "$2=\"${PROMPT_VALUE}\""
        fi
    else
      if [[ -z "${DEFAULT_ANSWER}" ]]; then
        echo "ERROR: Enable to determine default value for prompt: $MSG"
        exit 6
      fi
      echo "Non-interactive mode is enabled, setting default value to ${DEFAULT_ANSWER} "
      eval "$2=\"${DEFAULT_ANSWER}\""
    fi
}

function select_subscription() {
  MSG=$1
  OUTPUT_VAR_NAME=$2
  DEFAULT_ANSWER="$3"

  if [[ $(az account list --output tsv | wc -l) -gt "1" ]]; then
      echo "Multiple subscriptions found"
      az account list --output table
      echo "--------------------------------------------------"
      echo "Current subscription: "
      az account list --output table | grep "${DEFAULT_ANSWER}"
      yes_no_confirmation "$MSG" reply_yn
      if [[ "${reply_yn}" == false ]]
      then
          prompt_non_empty_str_value "Please provide the desired SubscriptionId, from the list displayed above. " "${OUTPUT_VAR_NAME}" "${DEFAULT_ANSWER}"
      fi
    fi

}