#!/usr/bin/env bash

containsElement () {
  local match=$1
  shift
  local output_var=$1
  shift
  elements=("$@")

  for e in "${elements[@]}"
  do
     if [[ "$e" == "$match" ]]; then
         eval "$output_var=true"; return;
     fi
  done

  eval "$output_var=false"
}
ADF_NAME=
RESOURCE_GROUP=

while [[ "$#" -gt 0 ]]; do
    case $1 in
      -n | --factory-name ) ADF_NAME="$2"; shift ;;
      -g | --resource-group ) RESOURCE_GROUP="$2"; shift ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift

done

runtime="60 minute"

endtime=$(date -ud "$runtime" +%s)
ALL_FINISHED=false
while [[ $(date -u +%s) -le $endtime ]]
do
  SUCCESSFUL_PIPELINES_ARRAY=( $(az datafactory pipeline-run query-by-factory --factory-name ${ADF_NAME} \
      --filters operand="Status" operator="Equals" values="Succeeded" --resource-group ${RESOURCE_GROUP} \
       --last-updated-after $(date -u -d '-2 hours' '+%FT%T.000000+00:00') --last-updated-before $(date '+%FT%T.000000+00:00') \
        --query value[].pipelineName  -o tsv --only-show-errors |  sort | uniq) )

  FAILED_PIPELINES_ARRAY=( $(az datafactory pipeline-run query-by-factory --factory-name ${ADF_NAME} \
      --filters operand="Status" operator="Equals" values="Failed" --resource-group ${RESOURCE_GROUP} \
       --last-updated-after $(date -u -d '-2 hours' '+%FT%T.000000+00:00') --last-updated-before $(date '+%FT%T.000000+00:00') \
        --query value[].pipelineName  -o tsv --only-show-errors |  sort | uniq) )

  echo "Successful: ${SUCCESSFUL_PIPELINES_ARRAY[@]}"
  echo "Failed : ${FAILED_PIPELINES_ARRAY[@]} "

  containsElement "End2EndEmailsPipeline" emailPipelineDone  "${SUCCESSFUL_PIPELINES_ARRAY[@]}"
  containsElement "End2EndEmployeeProfilePipeline" employeePipelineDone  "${SUCCESSFUL_PIPELINES_ARRAY[@]}"
  containsElement "End2EndMailsToRolesPipeline" rolesPipelineDone "${SUCCESSFUL_PIPELINES_ARRAY[@]}"

  containsElement "End2EndEmailsPipeline"  emailPipelineFailed "${FAILED_PIPELINES_ARRAY[@]}"
  containsElement "End2EndEmployeeProfilePipeline" employeePipelineFailed "${FAILED_PIPELINES_ARRAY[@]}"
  containsElement "End2EndMailsToRolesPipeline" rolesPipelineFailed "${FAILED_PIPELINES_ARRAY[@]}"

  echo "DEBUG: emailPipelineDone=${emailPipelineDone} , employeePipelineDone=${employeePipelineDone},  rolesPipelineDone=${rolesPipelineDone}, emailPipelineFailed=$emailPipelineFailed , employeePipelineFailed=$employeePipelineFailed ,  rolesPipelineFailed=$rolesPipelineFailed "

  if [[ ${emailPipelineDone} == true && ${employeePipelineDone} == true && ${rolesPipelineDone} == true ]]; then
    ALL_FINISHED=true
    echo "All End2End pipeline have been finished"
    exit 0
  else
      if [[ ${emailPipelineFailed} == true ]]; then
        echo "End2EndEmailsPipeline has failed. Check ${ADF_NAME} datafactory logs for details"
        exit 1
      fi
      if [[ ${employeePipelineFailed} == true ]]; then
        echo "End2EndEmployeeProfilePipeline has failed. Check ${ADF_NAME} datafactory logs for details"
        exit 2
      fi
      if [[ ${rolesPipelineFailed} == true ]]; then
        echo "End2EndMailsToRolesPipeline has failed. Check ${ADF_NAME} datafactory logs for details"
        exit 3
      fi
      if [[ ${#FAILED_PIPELINES_ARRAY[@]} > 2 ]]; then
        echo "Too many pipeline failures found. Consider deployment unsuccessful "
        exit 4
      fi

    echo "Waiting pipeline to be finished"
    sleep 60
  fi
done

if [[ ${ALL_FINISHED} == false ]]; then
  echo "it's taking too long end2end pipelines to finish processing. Consider deployment unsuccessful "
  exit 5
fi
