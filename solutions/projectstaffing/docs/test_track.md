## ProjectStaffing test track

### About
The ProjectStaffing test track uses JMeter to run a suite of API tests against an existing ProjectStaffing deployment.

Files:
- `Dockerfile`

  Used to create the custom JMeter docker image used for running the JMeter test

- `launch.sh`

  The entrypoint command for the docker PluginManagerCMDInstaller

- `run_test.sh`

  Used as a top-level entry for the test track. This script runs the tests. Used in the AzureDevops pipeline defined by the tests-pipeline.yml file

- `smoke-test_v01_upd.jmx`

  The JMeter test used to verify and validate an existing ProjectStaffing deployment.

### Pre-requisites
The JMeter test relies on the Selenium plugin of JMeter. In the `run_test.sh` script the docker image for this is being built.

If you want to run the script in your local JMeter (GUI mode) you'll need to install the Selenium plugin 
(see [jpgc-webdriver](https://JMeter-plugins.org/?search=jpgc-webdriver))


### How to run the tests
In both local and Azure DevOps pipelines, the manner in which the tests can be run is the same. Simply execute the `run_test.sh` script.

The script will build the needed docker image and use it to run the JMeter test.

Run command example:
`run_test.sh smoke-test_v01_upd.jmx`

At the end of the run, a report folder will be created with the name of the test appended with the `_report` suffix. E.g:
```
bash-3.2$ ls
total 364
drwxr-xr-x 10 user staff    320 Jan  1 00:00 .
drwxr-xr-x  3 user staff    928 Jan  1 00:00 ..
-rw-r--r--  1 user staff   2596 Jan  1 00:00 Dockerfile
-rw-r--r--  1 user staff   2495 Jan  1 00:00 JMeter.log
-rw-r--r--  1 user staff    256 Jan  1 00:00 launch.sh
-rwxr-xr-x  1 user staff    912 Jan  1 00:00 run_test.sh
-rw-r--r--  1 user staff 321366 Jan  1 00:00 smoke-test_v01_upd.jmx
drwxr-xr-x  6 user staff    192 Jan  1 00:00 smoke-test_v01_upd_report
-rw-r--r--  1 user staff  14611 Jan  1 00:00 smoke-test_v01_upd_res.jtl
```

Opening `smoke-test_v01_upd_report/index.html` in a browser will render the report for the run.

### How to run the tests locally in your own JMeter
Below we're describing the steps necessary to run the JMeter test in a local JMeter application running in GUI mode 
using Chrome as the browser for Selenium.

In order to run the test in a different browser, besides the adapted steps below, you'll need to entirely change 
the `jp@gc - Chrome Driver Config` element to the config element for your chosen browser.

For using Chrome follow the below steps:

1. Install the plugin mentioned above.

2. Download the [chromedriver](https://chromedriver.chromium.org/downloads)

3. Modify the path for the chromedriver executable inside the `jp@gc - Chrome Driver Config` element found in 
   the `Thread Group`, in the tab labeled `Chrome` -> `Path to Chrome Driver`.

> Note: the `/usr/bin/chromedriver` points to the chromedriver in the docker image.

### Pipeline
The file `tests-pipeline.yml` defines an Azure DevOps pipeline used to run the tests as part of a CI-track.

### Pipeline artifacts
At the end of a pipeline run, the report folder will be available as a *published artifact*.  
You can download that artifact to your local machine and open up the `index.html` file in your favorite browser to access the report.
