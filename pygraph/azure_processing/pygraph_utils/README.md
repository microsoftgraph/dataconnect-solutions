# Pygraph Utils

In order to have utility classes like `LogAnalyticsLogger` in one place (only) we have to create a 
package for them (eg: using a `.whl` file).

## How to create and use the package

Note: Be mindful whether you install in root or virtualenv/conda environment.

### During development
Package development mode allows us to make changes in the package and be automatically referenced in the next run
+ Run: `python setup.py develop`
+ Package check oneliner: `python -c "from taxo_utils.test import tester;tester()"` (alternatives in `pygraph_utils/taxo_utils/test.py`)
+ Check `./taxo_utils/test.py`, make some changes in the returned string.
+ Rerun package check oneliner above, observe the changed string.

### Wheel approach
This is the reference approach as far as Databricks is considered.

1. Set your current directory to `pygraph/azure_processing/pygraph_utils`
2. Run the following command to create the `.whl` file:
    + ```pip install wheel``` (if not already installed)  
    + ```python setup.py sdist bdist_wheel ```
3. Three new directories will be generated under `/pygraph_utils`.
    + Set the current directory to `/pygraph_utils/dist`
4. Run the following command to install the package:
    + ```pip install pygraph_utils-0.1.0-py3-none-any.whl```

5. Import `LogAnalyticsLogger` class wherever is needed:
    + ```from log_analytics_client.logger import LogAnalyticsLogger```

# Bibliography
+ https://stackoverflow.com/questions/7522250/how-to-include-package-data-with-setuptools-distutils
    + https://docs.python.org/2/distutils/sourcedist.html#manifest-template
    + https://docs.python.org/3.7/distributing/index.html#distributing-index
+ https://stackoverflow.com/questions/29819035/python-setup-py-develop-why-is-my-data-package-not-available
    + https://github.com/pypa/setuptools/issues?q=is%3Aissue+package_dir
+ https://kiwidamien.github.io/making-a-python-package-vi-including-data-files.html
+ https://stackoverflow.com/questions/51286928/what-is-where-argument-for-in-setuptools-find-packages
