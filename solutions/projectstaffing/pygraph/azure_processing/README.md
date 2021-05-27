# Azure Processing
The `pygraph` module contains pyspark jobs which are meant to be run on an Azure Databricks (ADB) cluster:

| Job                                             | Description |
| ----------------------------------------------- | ----------- |
| [mail_enrichment](mail_enrichment_processor.py) | processes mails and extracts relevant data (such as tokens) from it. |
| [profiles_enrichment](profiles_enrichment_processor.py) | processes M365 profiles and extracts specific fields, to be reused within the main portal later. |
| [mail_role_detection](mail_role_detection_taxo.py) | processes mails and leverages FSE (Fast Sentence Embedding) to classify the employee roles. |
| [create_azure_search_index](create_azure_search_index.py) | creates an AzureSearch index (e.g. employees, emails), using a schema file from AZBS provided as argument. |

In production, these jobs are orchestrated by Azure DataFactory (ADF) pipelines.  

Jobs like [`mail_enrichment_processor.py`](./mail_enrichment_processor.py) or [`profiles_enrichment_processor.py`](./profiles_enrichment_processor.py) 
will run on the Databricks cluster, and can be monitored in the cluster's SparkUI.

During development, the pyspark jobs can be run either:
+ locally - run using sample data locally:
  + profiles_enrichment (`process_local_job()` instead of `run_spark_job()`),
  + mail_enrichment (`process_local()` uncommented in `__main__`),
  + mail_role_detection - use `process_local()`.
+ directly on the ADB cluster, or
+ remotely, from the local environment to a target ADB cluster using `databricks-connect`.


---
# Setup Guide

## Setting up environment for building from source
Follow the instructions from the [I. Python Setup](#python-setup) section.

Once this is done, the pygraph utils wheel can be built by following [these steps](./pygraph_utils/README.md#building-the-wheel)

## Setting up environment for Development
Steps:
+ [I. Python Setup](#python-setup)
+ [II. Development conda environment setup](#development-conda-environment-setup)
+ [III. Set up jobs configuration for Databricks](#databricks-config)
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
+ `conda create --name gdc python=3.7`
+ `conda activate gdc`



## II. Development conda environment setup
<span id="development-conda-environment-setup"></span>
+ `pip install -r requirements_conda.txt`

If any changes to the setup are needed, remember to document changes in requirements:
+ preferably with conda: `conda list --export > requirements_conda.txt` (rather than `pip freeze > requirements.txt`)

Any subsequent installs have to be made in the:
+ activated conda environment - `conda install package-name=2.3.4`
+ outside conda environment - `conda install package-name=2.3.4 -n gdc`

> Note: Latest requirements file has the `smart_open` import bug from gensim patched by upgrading smart_open (`conda update smart_open`). 
> You shouldn't encounter this error unless `gensim` reinstalls the faulty `smart_open` version.


## III. Set up jobs configuration for Databricks
<span id="databricks-config"></span>
The environment specific configuration for each job can be provided via a configurations file.  

For local debugging and execution you should have a configuration file named `config.json`, with the following structure:
```
{
  "key": "",
  "sas_token": "",
  "de_key": "",
  "datasource_connection_string": "",
  "azure_search_api_key": "",
  "endpoint": "",
  "SERVICE_PRINCIPAL_SECRET": "[...]"
}
```

The following fields need to be filled:
- `SERVICE_PRINCIPAL_SECRET` - the secret for the `gdc-service` service principal, that is used by all spark jobs to connect to Azure services (AZBS, AzureSql, KeyVault etc) 

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
<span id="spark-setup"></span>

+ install - `brew install apache-spark`
    + usually located in `/usr/local/Cellar/apache-spark/3.0.1/libexec/`

+ install - `pip3  install databricks-connect==6.6.0` - this version is needed for our current Databricks configuration (5.5.* at least, 6.4 should also work)
+ sanity check - `python3 -c 'import pyspark'`

+ sanity check - `spark-shell` (use `:quit`)
+ sanity check - `pyspark` (use `quit()`)
+ configure according to the below specs - `databricks-connect configure`
+ check - `databricks-connect test`
    + may error if pyspark wasn't uninstalled / left garbage folders (for some reason) -> `rm -rf /Library/Frameworks/Python.framework/Versions/3.8/lib/python3.8/site-packages/pyspark`


Optional objective:
+ TODO: can use local Spark while `databricks-connect` is installed and configured ?

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
+ for `jenv`, `pyenv`, `databricks-connect` (mentioned above).
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


---
---
# Additional

## Java Multiple Environment Setup (jenv)
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

---
# Sources

## More links:
+ https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html
+ https://medium.com/@chamikakasun/how-to-manage-multiple-java-version-in-macos-e5421345f6d0
+ https://github.com/jenv/jenv/issues/212
+ https://stackoverflow.com/questions/41129504/pycharm-with-pyenv
+ https://docs.databricks.com/dev-tools/databricks-connect.html
