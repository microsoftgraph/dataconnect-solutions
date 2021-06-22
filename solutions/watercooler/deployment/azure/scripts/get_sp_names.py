#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

from config import InstallConfiguration
import sys
import argparse

if __name__ == '__main__':
    args = sys.argv

    install_config: InstallConfiguration = InstallConfiguration.load()

    arg_parser = argparse.ArgumentParser(description='Get service principal names')

    arg_parser.add_argument('--service-principal-type',
                            metavar='service-principal-type',
                            type=str,
                            help='Service principal type: wc-service or wc-m365-reader', required=True)

    parsed_args = arg_parser.parse_args()
    sp_type = parsed_args.service_principal_type

    if sp_type == 'wc-service' and 'name' in install_config.wc_service_principal:
        print("service_principal_name=%s" % install_config.wc_service_principal['name'])
    elif sp_type == 'wc-m365-reader' and 'name' in install_config.m365_reader_service_principal:
        print("service_principal_name=%s" % install_config.m365_reader_service_principal['name'])
