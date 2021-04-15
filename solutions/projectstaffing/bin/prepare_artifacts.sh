#!/usr/bin/env bash

[[ $@ =~ (^|[[:space:]])"--skip-maven"($|[[:space:]]) ]] && SKIP_MAVEN=1 || SKIP_MAVEN=0

set -e

PROJECT_ROOT="$( cd "$(dirname $(dirname  "$0" ))" >/dev/null 2>&1 ; pwd -P )"

echo "resolved project_root" "$PROJECT_ROOT" "$SCRIPTPATH"

pushd "${PROJECT_ROOT}"

  rm -Rf target/output/gdc \
          && echo "Cleaned up target/output"
  mkdir -p target/output/gdc
  cp -r deployment/arm target/output/gdc

  ARTIFACTS_DIR="${PROJECT_ROOT}"/target/output/gdc/arm/scripts/artifacts/
  mkdir -p target/output/gdc/arm/scripts/artifacts

  pushd "${PROJECT_ROOT}/jgraph"

  [[ $SKIP_MAVEN -eq 1 ]] || mvn -DskipTests clean install

    cp latest-reply-extractor/target/latest-reply-extractor.jar "${ARTIFACTS_DIR}"
    cp m365-user-to-azure-sql/target/m365-user-to-azure-sql.jar "${ARTIFACTS_DIR}"
    cp hr-data/target/hr-data-to-azure-sql.jar                "${ARTIFACTS_DIR}"
    cp airtable/target/airtable-to-hr-data.jar                "${ARTIFACTS_DIR}"
    cp replace-current-search-index/target/replace-current-search-index.jar "${ARTIFACTS_DIR}"
    cp cleanup-jgraph/target/cleanup-jgraph.jar "${ARTIFACTS_DIR}"
    cp update-azure-sql-data-version/target/update-azure-sql-data-version.jar "${ARTIFACTS_DIR}"
    #copy sql files
    ls -1 core/src/main/resources/db/migration/V*.sql | grep -v stored_procedure | sort -V |  xargs -I{} sh -c "cat {}; echo ''" > "${PROJECT_ROOT}"/target/output/gdc/arm/sql-server/schema.sql
    latest_stored_procedure_file=$(ls -1 core/src/main/resources/db/migration/V*.sql | grep stored_procedure | sort -rV | head -1)
    cp  "$latest_stored_procedure_file" "${PROJECT_ROOT}/target/output/gdc/arm/sql-server/stored_procedures.sql"
    cp  core/src/main/resources/db/default-data/data.sql "${PROJECT_ROOT}"/target/output/gdc/arm/sql-server/
  popd

  cp pygraph/azure_processing/mail_enrichment_processor.py     "${ARTIFACTS_DIR}"
  cp pygraph/azure_processing/profiles_enrichment_processor.py "${ARTIFACTS_DIR}"
  cp pygraph/azure_processing/mail_role_detection_taxo.py      "${ARTIFACTS_DIR}"
  cp pygraph/azure_processing/create_azure_search_index.py     "${ARTIFACTS_DIR}"

popd

  pushd "${PROJECT_ROOT}"/pygraph/azure_processing/pygraph_utils
    pip3 install wheel
    pip3 install -U pip setuptools
    python3 setup.py sdist bdist_wheel
    cp dist/pygraph_utils-*.whl "${ARTIFACTS_DIR}"
  popd
