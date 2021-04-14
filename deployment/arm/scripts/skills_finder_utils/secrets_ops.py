
from skills_finder_utils.az import az_cli


def set_secret(keyvault_name: str, secret_name: str,  value: str):
    az_cli("keyvault secret set", "--name", secret_name,
                  "--vault-name", keyvault_name, "--value", value)

