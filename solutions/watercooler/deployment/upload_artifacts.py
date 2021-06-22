import gzip
import json
# import logging, requests, hashlib, hmac
import os
import subprocess

from azure.storage.blob import BlobClient

from azure.storage.blob import BlobServiceClient
from datetime import datetime


def deploy_scripts_to_folder(input_container="watercooler-test", add_jars=True):
    """
    tar -zcvf  build20210528.tar.gz  azure
    """

    cmd = f"rm -rf wc && mkdir wc && cp -r ./azure/* wc/ && mkdir -p wc/scripts/artifacts"

    print(f"executing command: {cmd}")
    # cmdRes = Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL).wait()
    cmdRes = subprocess.Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.PIPE,
                              stdout=subprocess.PIPE).communicate()
    print(cmdRes)

    if add_jars:
        cmd = f"cp ../jwc/profiles-extractor/target/jwc-profiles-extractor.jar wc/scripts/artifacts/"

        print(f"executing command: {cmd}")
        # cmdRes = Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL).wait()
        cmdRes = subprocess.Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.PIPE,
                                  stdout=subprocess.PIPE).communicate()
        print(cmdRes)

        cmd = f"cp ../jwc/events-creator/target/jwc-events-creator.jar wc/scripts/artifacts/"

        print(f"executing command: {cmd}")
        # cmdRes = Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL).wait()
        cmdRes = subprocess.Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.PIPE,
                                  stdout=subprocess.PIPE).communicate()
        print(cmdRes)

    cmd = f"cp ../pywc/src/*.py wc/scripts/artifacts/"

    print(f"executing command: {cmd}")
    # cmdRes = Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL).wait()
    cmdRes = subprocess.Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.PIPE,
                              stdout=subprocess.PIPE).communicate()
    print(cmdRes)

    """
    cmd = f"cp ./lib/pygraph_*.whl wc/scripts/artifacts/"

    print(f"executing command: {cmd}")
    # cmdRes = Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL).wait()
    cmdRes = subprocess.Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.PIPE,
                              stdout=subprocess.PIPE).communicate()
    print(cmdRes)
    """

    tag = str(datetime.now().strftime("%Y%m%d%H%M"))

    filename = f"build{tag}.tar.gz"
    cmd = f"tar -zcvf  {filename} wc"

    print(f"executing command: {cmd}")
    # cmdRes = Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL).wait()
    cmdRes = subprocess.Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.PIPE,
                              stdout=subprocess.PIPE).communicate()
    print(cmdRes)

    cmd = f"rm -rf wc"

    print(f"executing command: {cmd}")
    # cmdRes = Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.DEVNULL, stdout=subprocess.DEVNULL).wait()
    cmdRes = subprocess.Popen(cmd, shell=True, stdin=subprocess.DEVNULL, stderr=subprocess.PIPE,
                              stdout=subprocess.PIPE).communicate()
    print(cmdRes)

    CONNECTION_STRING = json.load(open("config_test.json"))["datasource_connection_string"]
    blob_service_client = BlobServiceClient.from_connection_string(CONNECTION_STRING)
    container_client = blob_service_client.get_container_client(input_container)

    print("Uploading zip file")
    blob_client = BlobClient.from_connection_string(CONNECTION_STRING,
                                                    container_name="watercooler-test",
                                                    blob_name=f"builds/{filename}")
    with open(filename, "rb") as data:
        blob_client.upload_blob(data, overwrite=True)


deploy_scripts_to_folder()
