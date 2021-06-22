import sys
import time

def mount_azure_to_dbfs(account_name, container_name, secret_key, mount_point):
    print(account_name, container_name, secret_key, mount_point)
    source_name = "wasbs://" + container_name + "@" + account_name + ".blob.core.windows.net"
    full_account_name_config = "fs.azure.account.key." + account_name + ".blob.core.windows.net"
    try:
        dbutils.fs.umount(mount_point)
    except Exception as e:
        pass

    time.sleep(2)
    dbutils.fs.mount(
        source=source_name,
        mount_point=mount_point,
        extra_configs={full_account_name_config:secret_key})


account_name_param = None
container_name_param = None
secret_key_name_param = None
mount_point_param = None


for arg_str in sys.argv[1:]:
    opt, arg = arg_str.split("####")
    if opt == "--account_name":
        account_name_param = arg
    elif opt == "--container_name":
        container_name_param = arg
    elif opt == "--secret_key_name":
        secret_key_name_param = arg
    elif opt == "--mount_point":
        mount_point_param = arg

mount_azure_to_dbfs(account_name=account_name_param,
                    container_name=container_name_param,
                    secret_key=secret_key_name_param,
                    mount_point=mount_point_param)
