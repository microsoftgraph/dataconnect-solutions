# Pygraph Utils

In order to have utility classes like `LogAnalyticsLogger` in one single place (which can then be reused in any python
script or ADB pyspark job within the project), we have to create a package for them.  
We are going to use a [python wheel](https://realpython.com/python-wheels/) to achieve this purpose. 

## How to create and use the package

> Note: Be mindful whether you install in root or virtualenv/conda environment.

### Building the wheel
Providing a wheel packaging the common code is the reference approach as far as Databricks is considered.  
Depending on the python setup performed, you might need to activate a python virtualenv or a conda environment before
performing the following steps:
1. Set your current directory to `pygraph/azure_processing/pygraph_utils`
2. Run the following command to create the `.whl` file:
    + ```pip install wheel``` (if not already installed)  
    + ```python setup.py sdist bdist_wheel ```
3. Three new directories will be generated under `/pygraph_utils`:
    + build
    + dist
    + pygraph_utils.egg-info
4. The resulting `.whl` wheel file can be found in the `dist` folder
    - the actual name of the file will vary based on the current package version which has been released (independent 
      of the application version), e.g. `pygraph_utils-0.1.7-py3-none-any.whl`
    
> Note: If `pip` is linked to Python 2 on your system, then use `pip3` instead

### Using the wheel locally for development
1. Set the current directory to `pygraph/azure_processing/pygraph_utils/dist`
2. Run the following command to install the package:
    + ```pip install pygraph_utils-0.1.7-py3-none-any.whl```
3. Import the proper module from the package in any python script requiring it
    + For example, to import `LogAnalyticsLogger` class, use:
        + ```from log_analytics_client.logger import LogAnalyticsLogger```
4. Use the imported module API in your code
    + The following example is derived from one of the existing ADB pyspark jobs
```python
from log_analytics_client.logger import LogAnalyticsLogger

try:
    logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
    logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                shared_key=logAnalyticsApiKey,
                                log_type="CreateAzureSearchIndex",
                                log_server_time=True,
                                name="[create_azure_search_index]")
    logger.info("Logger successfully initialized")
except Exception as e:
    logger = LogAnalyticsLogger(name="[create_azure_search_index]")
    logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))
```

### Uploading the wheel to the ADB cluster
Once the wheel has been built, and the python jobs using it have been finalized, all these files simply need to be
uploaded to the ADB cluster for them to be used by the application's ADF pipelines. This can be done by following
one of the approaches described in the [deployment documentation](../../../deployment/README.MD#deployment).  
If the upload is not done as part of a full deployment, then you can simply upload the components that changed, as
described in the section about [deploying individual python components](../../../deployment/README.MD#deploying-jars-python-scripts-and-python-utils-wheel-to-azure-databricks-cluster).
If the contents of the wheel changed, while its API remained the same, then uploading the python scripts that use it
is not required. Otherwise, the scripts impacted by the API change need to be uploaded as well.

### During development
The package development approach allows us to make changes in the wheel and to automatically have them available to
all python scripts using them during the next run
+ Run: `python setup.py develop`
+ Package check oneliner: `python -c "from taxo_utils.test import tester;tester()"` (alternatives in `pygraph_utils/taxo_utils/test.py`)
+ Check `./taxo_utils/test.py`, make some changes in the returned string.
+ Rerun package check oneliner above, observe the changed string.

# Bibliography
+ https://stackoverflow.com/questions/7522250/how-to-include-package-data-with-setuptools-distutils
    + https://docs.python.org/2/distutils/sourcedist.html#manifest-template
    + https://docs.python.org/3.7/distributing/index.html#distributing-index
+ https://stackoverflow.com/questions/29819035/python-setup-py-develop-why-is-my-data-package-not-available
    + https://github.com/pypa/setuptools/issues?q=is%3Aissue+package_dir
+ https://kiwidamien.github.io/making-a-python-package-vi-including-data-files.html
+ https://stackoverflow.com/questions/51286928/what-is-where-argument-for-in-setuptools-find-packages
