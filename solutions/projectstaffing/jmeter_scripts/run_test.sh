#!/usr/bin/env bash

if [ "$#" -eq 0 ]; then
    echo "Specify test to run"
    exit 1
fi

TEST_JMX="$1"
TEST_NAME="${TEST_JMX%.jmx}"
for arg in $@; do
    if [[ "$(echo $arg | cut -d'=' -f1)" == "-Jvideo" ]]; then
        testNameRaw="$(echo $arg | cut -d'=' -f2)"
        TEST_NAME="${testNameRaw%.mp4}"
    fi
done
echo $TEST_NAME
shift

if [[ "$TEST_JMX" == "$TEST_NAME" ]]; then
    echo "Provided test does not have the '.jmx' extension. Try again!"
    exit 1
fi

NAME="jmeter"
IMAGE="jmeter-with-webdriver"

sudo docker build . -f Dockerfile -t ${IMAGE}
rm -rf "${TEST_NAME}_report" || true
rm "${TEST_NAME}_res.jtl" || true
rm "${TEST_NAME}_jmeter.log" || true
sudo docker stop ${TEST_NAME} > /dev/null 2>&1
sudo docker rm ${TEST_NAME} > /dev/null 2>&1
sudo docker run --name ${TEST_NAME} -i -v ${PWD}:${PWD} -w ${PWD} ${IMAGE} -n -t "$TEST_JMX" -l "${TEST_NAME}_res.jtl" -e -o "${TEST_NAME}_report" $@
