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
                            help='Service principal type: gdc-service or gdc-m365-reader', required=True)

    parsed_args = arg_parser.parse_args()
    sp_type = parsed_args.service_principal_type

    if sp_type == 'gdc-service' and 'name' in install_config.gdc_service_principal:
        print("service_principal_name=%s" % install_config.gdc_service_principal['name'])
    elif sp_type == 'gdc-m365-reader' and 'name' in install_config.m365_reader_service_principal:
        print("service_principal_name=%s" % install_config.m365_reader_service_principal['name'])
