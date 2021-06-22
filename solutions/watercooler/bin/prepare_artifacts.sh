#!/usr/bin/env bash

[[ $@ =~ (^|[[:space:]])"--skip-maven"($|[[:space:]]) ]] && SKIP_MAVEN=1 || SKIP_MAVEN=0

set -e

PROJECT_ROOT="$( cd "$(dirname $(dirname  "$0" ))" >/dev/null 2>&1 ; pwd -P )/.."

echo "resolved project_root" "$PROJECT_ROOT" "$SCRIPTPATH"

pushd "${PROJECT_ROOT}"

  rm -Rf wc \
          && echo "Cleaned up target/output"
  mkdir -p "${PROJECT_ROOT}"/bin/wc
  cp -r deployment/azure/* "${PROJECT_ROOT}"/bin/wc

  ARTIFACTS_DIR="${PROJECT_ROOT}"/bin/wc/scripts/artifacts
  mkdir -p ${ARTIFACTS_DIR}

  pushd "${PROJECT_ROOT}/jwc"

  [[ $SKIP_MAVEN -eq 1 ]] || mvn -DskipTests clean install
    cp events-creator/target/jwc-events-creator.jar                           "${ARTIFACTS_DIR}"
    cp profiles-extractor/target/jwc-profiles-extractor.jar                   "${ARTIFACTS_DIR}"
  popd

  cp pywc/src/000_cleanup.py                                                   "${ARTIFACTS_DIR}"
  cp pywc/src/01_1_calendar_events_attendance.py                               "${ARTIFACTS_DIR}"
  cp pywc/src/01_2_update_group_members_invitation_status.py                   "${ARTIFACTS_DIR}"
  cp pywc/src/01_calendar_spark_processor.py                                   "${ARTIFACTS_DIR}"
  cp pywc/src/02_profiles_spark_processor.py                                   "${ARTIFACTS_DIR}"
  cp pywc/src/03_persons_to_events_dill_assembler.py                           "${ARTIFACTS_DIR}"
  cp pywc/src/04_generate_timetable_kmeans.py                                  "${ARTIFACTS_DIR}"
  cp pywc/src/05_export_to_csv.py                                              "${ARTIFACTS_DIR}"
  cp pywc/src/06_spark_export_to_sql.py                                        "${ARTIFACTS_DIR}"

popd

rm -rf build.tar.gz
tar -czvf build.tar.gz ./wc
rm -rf ./wc