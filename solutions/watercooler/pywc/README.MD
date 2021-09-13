# Azure Processing
The `pywc` module contains pyspark jobs and scripts which are meant to be run on an Azure Databricks (ADB) cluster:


| Job                                             | Description |
| ----------------------------------------------- | ----------- |
| [000_cleanup](./src/000_cleanup.py) | truncates the watercooler database tables. |
| [01_1_calendar_events_attendance](./src/01_1_calendar_events_attendance.py) | Retrieve the user calendar events attendance status. |
| [01_2_update_group_members_invitation_status](./src/01_2_update_group_members_invitation_status.py) | Updates the group member attendance status in db |
| [01_calendar_spark_processor](./src/01_calendar_spark_processor.py) | Retrieves the specific fields from the calendar json. |
| [02_profiles_spark_processor](./src/02_profiles_spark_processor.py) | Retrieves the profiles from m365. |
| [03_persons_to_events_dill_assembler](./src/03_persons_to_events_dill_assembler.py) | processes M365 profiles and joins them with the user calendar information |
| [04_generate_timetable_kmeans](./src/04_generate_timetable_kmeans.py) | generates the clustering based on kmeans |
| [05_export_to_csv](./src/05_export_to_csv.py) | Script that writes watercooler groups as json on azbfs |
| [06_spark_export_to_sql](./src/06_spark_export_to_sql.py) | Script that exports the watercooler jsons to db |

In production, these jobs are orchestrated by Azure DataFactory (ADF) pipelines.


Jobs like [`01_calendar_spark_processor.py`](./parametrized_scripts/01_calendar_spark_processor.py) or [`02_profiles_spark_processor.py`](./parametrized_scripts/02_profiles_spark_processor.py)
will run on the Databricks cluster, and can be monitored in the cluster's SparkUI.

During development, the pyspark jobs can be run either:
+ locally - run using sample data locally:
+ directly on the ADB cluster, or
+ remotely, from the local environment to a target ADB cluster using `databricks-connect`.

---

# Setup Guide

## Setting up environment for Development
Steps:
+ [I. Python Setup](#python-setup)
+ [II. Development conda environment setup](#development-conda-environment-setup)
+ [III. Configure local python scripts to run with Databricks](#local-development-config)
+ [IV. Java Setup](#java-setup)
+ [V. Spark / Databricks Setup](#spark-setup)
+ [VI. Setup Sanity Checks](#finishing-setup)

Additionally, you can consider the following steps:
+ [Java Multiple Environment Setup (jenv)](#jenv-setup) - if you intend to have separate versions of Java in the same environment (eg: having both jgraph and a local Spark for pygraph development & debugging),
+ [Python Multiple Environment Setup (pyenv)](#pyenv-setup) - in case you need multiple Python versions and/or databricks-connect might require a virtualenv or other version of python for compatibility.


---
# Detailed setup steps
## I. Python Setup
<span id="python-setup"></span>

### 1. Install Python bundled with Anaconda
#### Linux and MacOS
Install the reference Python:
+ `wget https://repo.anaconda.com/archive/Anaconda3-2021.05-Linux-x86_64.sh`
  or
+ `curl -o Anaconda3-2021.05-Linux-x86_64.sh https://repo.anaconda.com/archive/Anaconda3-2021.05-Linux-x86_64.sh`
+ `chmod 700 Anaconda3-2021.05-Linux-x86_64.sh`
+ `./Anaconda3-2021.05-Linux-x86_64.sh`
  + you can do a silent install by adding the `-b` parameter, but PATH will remain unmodified, so you'll need to run `eval "$(~/anaconda3/bin/conda shell.bash hook)"`

#### Windows
Install using the install instruction from here https://docs.anaconda.com/anaconda/install/windows/


### 3. Environment setup
Set up your environment, and activate it both in your shell and in your IDE:
+ `conda create --name jwc python=3.7`
+ `conda activate jwc`



## II. Development conda environment setup
<span id="development-conda-environment-setup"></span>
+ `pip install -r requirements.txt`

If any changes to the setup are needed, remember to document changes in requirements:
+ preferably with conda: `conda list --export > requirements_conda.txt` (rather than `pip freeze > requirements.txt`)

Any subsequent installs have to be made in the:
+ activated conda environment - `conda install package-name=2.3.4`
+ outside conda environment - `conda install package-name=2.3.4 -n jwc`

## III. Configure local python scripts to run with Databricks
<span id="local-development-config"></span>
The environment specific configuration for each job can be provided via a configurations file.

For local debugging and execution of the scripts, the following configuration file templates are provided: `config_default.json`,
`profiles_enrichment_params_default.json`,`mail_enrichment_params_default.json`.
Those should be cloned to : `config.json`, `profiles_enrichment_params.json`, `mail_enrichment_params.json` and filled with the appropriate values.

All this files can be found here: [./dev_config](./dev_config/)

The config file `config.json` has the following structure and is being used by all the spark job python scripts:
```
{
  "SERVICE_PRINCIPAL_SECRET": "J65H1[...]"
}
```

The following fields need to be filled:
- `SERVICE_PRINCIPAL_SECRET` - the secret for the `jwc-service` service principal, that is used by all spark jobs to connect to Azure services (AZBS, AzureSql, KeyVault etc)


> **Make sure not to commit this file** since it will contain sensitive information! Check if the rules from the `.gitignore` file cover your file name.  
> Only store locally information for non-critical environments, which don't contain sensitive data and which can be easily decommissioned!


Be mindful if cluster is active/inactive, in order to:
+ minimize load - if cluster is used for upgrade / demo
+ Total Cost of Ownership (abbrev. TCO) - cluster doesn't have to be spun up needlessly, costs should be limited.

## IV. Java Setup
<span id="java-setup"></span>
Please install a JDK 8 appropriate to your OS, if you haven't done so already. License terms differ from one JDK supplier
to another (e.g. Oracle vs OpenJDK), so please read license conditions carefully when choosing supplier.  
For example, to install the OpenJDK, follow the appropriate installation steps from https://openjdk.java.net/install/
or https://jdk.java.net/java-se-ri/8-MR3, or search online for a more detailed installation guide, matching your OS.


## V. Spark / Databricks Setup 

### MacOS/Linux
<span id="spark-setup"></span>

+ install - `brew install apache-spark`
  + usually located in `/usr/local/Cellar/apache-spark/3.0.1/libexec/`

+ install - `pip3  install databricks-connect==8.1` - this version is needed for our current Databricks configuration (Runtime version: 8.1 (includes Apache Spark 3.1.1, Scala 2.12))
+ sanity check - `python3 -c 'import pyspark'`

+ sanity check - `spark-shell` (use `:quit`)
+ sanity check - `pyspark` (use `quit()`)
+ configure according to the below specs - `databricks-connect configure`
+ check - `databricks-connect test`
  + may error if pyspark wasn't uninstalled / left garbage folders (for some reason) -> `rm -rf /Library/Frameworks/Python.framework/Versions/3.8/lib/python3.8/site-packages/pyspark`


Optional objective:
+ TODO: can use local Spark while `databricks-connect` is installed and configured ?

### Windows

In order to have python/databricks-connect installed on windows please 
follow the steps described in this documentation: 

https://docs.databricks.com/dev-tools/databricks-connect.html

Databricks documentation lists conda as a dependency for having to create the 
python enviroment necessary for databricks-connect and pyspark libraries being installed
on the user windows environment.
In order to install anaconda (conda) on your window machine, please follow the steps
described in here: 

https://docs.anaconda.com/anaconda/install/windows/

The steps for installing java (java sdk is a dependency for having pyspark running correctly) you can
follow the steps described in [../README.MD](../README.MD#prerequisites)

If there are any problems with the databricks-connect setup please consult
the troubleshooting section here:

https://docs.databricks.com/dev-tools/databricks-connect.html#troubleshooting



---

## VI. Setup Sanity Checks
<span id="finishing-setup"></span>
Check your PATH:
+ `echo $PATH | tr ":" "\n"`
+ if there are duplicates, `echo $PATH` and clean in your IDE of preference
  + purge references to system-wide python (such as: `/Library/Frameworks/Python.framework/Versions/3.8/bin`) - these will confuse the system as to which `databricks-connect` to use, among others (eg: `site-packages` location used)
  + ( **VERY IMPORTANT** ) if Spark previously used in the system, `unset SPARK_HOME` - this along with other package confusion can lead to a faulty setup (hard to diagnose errors like "*TypeError: 'Java Package' object is not callable*")


Note: ( **VERY IMPORTANT** ) Perform sanity checks for faulty `PATH` or `SPARK_HOME` set in the following locations:
+ `~/.bash_rc`, `~/.bash_profile`
+ `~/.zsh_rc`, `~/.zprofile`

Note: Also try to perform sanity checks in a new tab and your IDE of choice:
+ for `jenv`, `pyenv`, `databricks-connect` (mentioned below).
+ run `python spark_test.py` and check the Spark UI

Your config file (`~/.bashrc` or `~/.zshrc`) should look at the end like this:
```shell script
# set only if not using databricks-connect
# export SPARK_HOME="/usr/local/Cellar/apache-spark/3.0.1/libexec"
# export PYSPARK_PYTHON="python3"
# export PATH=$PATH:$SPARK_HOME/bin
# export PATH=$PATH:$PYSPARK_PYTHON

export PATH="$HOME/.jenv/bin:$PATH"
eval "$(jenv init -)"

export PATH="$HOME/.pyenv/bin:$PATH"
eval "$(pyenv init -)"
eval "$(pyenv virtualenv-init -)"
```

## VII. Development tips

## If a custom container+folder needs to be mounted to a specific location in adbfs:
+TODO: explain the container and the folders within container and the access key

```
dbutils.fs.mount(
 source = "wasbs://watercooler-test@gdcblueprintsampledata.blob.core.windows.net",
 mount_point = "/mnt/watercooler",
 extra_configs = {"fs.azure.account.key.gdcblueprintsampledata.blob.core.windows.net":"AccessKey" })
```

More information about the databricks file system can be found [here](https://docs.databricks.com/data/databricks-file-system.html)

## Uploading the scripts to the ADB cluster
During development, modified scripts can be upload to the following location on Azure Databricks File System:  `dbfs ls dbfs:/mnt/watercooler/watercooler_scripts/parameterized`
The command for upload the scripts is the following and has to be executed from the [./pywc/parametrized_scripts](./pywc/parametrized_scripts) folder.
```buildoutcfg
dbfs cp --overwrite *.py dbfs:/mnt/watercooler/watercooler_scripts/parameterized
```


---
---
# Additional
In case there's a need for a different environments (example: watercooler uses a newer java version different than java needed for spark)
then follow the steps below:

## Java Multiple Environment Setup (jenv)(linux/mac only)
<span id="jenv-setup"></span>
In order to allow for multiple versions of java on the same system, we use `jenv`:
+ install - `brew install jenv`
+ append the following to config file (`~/.bashrc` or `~/.zshrc`) and run `source` or restart/open new shell:
```shell script
export PATH="$HOME/.jenv/bin:$PATH"
eval "$(jenv init -)"
```
+ check if jenv works - `jenv doctor`

After installing needed JDKs:
+ check installed Java VMs - `/usr/libexec/java_home -V`
+ add installed Java VMs to jenv:
  + `jenv add /Library/Java/JavaVirtualMachines/jdk-15.0.1.jdk/Contents/Home`
  + `jenv add /Library/Java/JavaVirtualMachines/jdk1.8.0_271.jdk/Contents/Home`
+ check - `jenv versions`

Optional, but recommended:
+ `jenv enable-plugin export`
+ `jenv enable-plugin maven`

Set JDK needed for Spark:
+ set java 1.8 as system-level java - `jenv global 1.8`
+ sanity check - `jenv doctor`
+ sanity check - `java -version`


## Python Multiple Environment Setup (pyenv)
<span id="pyenv-setup"></span>
Similar to what we did for Java, in this case for Python.  
Please see the [installation section](https://github.com/pyenv/pyenv#installation) of the project's [github page](https://github.com/pyenv/pyenv)  
For an installation guide covering several systems, please read https://wilsonmar.github.io/pyenv/  
The steps below describe the installation process for mac:
+ install - `brew install pyenv pyenv-virtualenv`
+ append the following to config file (`~/.bashrc` or `~/.zshrc`) and run `source` or restart/open new shell:
```shell script
export PATH="$HOME/.pyenv/bin:$PATH"
eval "$(pyenv init -)"
eval "$(pyenv virtualenv-init -)"
```
+ check - `pyenv versions`
+ install - `pyenv install -v 3.7.8` => why `<3.8` ([source](https://stackoverflow.com/questions/58700384/how-to-fix-typeerror-an-integer-is-required-got-type-bytes-error-when-tryin))
+ set installed version globally - `pyenv global 3.7.8`
+ sanity check - `pyenv versions`


For windows you can consult the documentation for pyenv here: https://github.com/pyenv-win/pyenv-win

---
# Sources

## More links:
+ https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html
+ https://medium.com/@chamikakasun/how-to-manage-multiple-java-version-in-macos-e5421345f6d0
+ https://github.com/jenv/jenv/issues/212
+ https://stackoverflow.com/questions/41129504/pycharm-with-pyenv
+ https://docs.databricks.com/dev-tools/databricks-connect.html



        
