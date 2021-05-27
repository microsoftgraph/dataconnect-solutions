# Python components Makefile
#
# Assumptions:
# + Java already installed,
#
all: clean make_env

msg:
	@echo "\nMake supported here only for its individual subcommands. Check the Makefile and run make with arguments for your needs.\n"

clean:
	conda remove --name gdc --all

make_env:
	# python environment setup
	conda create --name gdc --file requirements_conda.txt
	python pygraph_utils/script_install.py

test_local:
	# currently no "--local" flag exists
	python mail_enrichment_processor.py
	python profiles_enrichment_processor.py

trigger_adb:
	python mail_enrichment_processor.py
	python profiles_enrichment_processor.py
	python mail_role_detection_taxo.py
