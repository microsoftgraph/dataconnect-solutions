# ui

Written with Vue.js `v.2.6` using Typescript, Vue Class Component, Vuex `v.3.5` and Vue Router `v.3.3`.

### Project setup

Make sure to navigate to the ui module directory and to have `yarn v.1.22.4` package manager and `Node.js v.14.4.0` installed on your local machine.

#### Install Node.js:

If you don't have `Node.js` installed on your machine you can download and run the installer from https://nodejs.org/en/ which will include npm.  
To check that Node.js was installed successfully, run this command in your terminal: `node -v` .

#### Install yarn via npm (windows and macOS):

- The easiest way to install yarn would be with `npm`.  
  If you already have `npm` installed on your local machine, simply run `npm install --global yarn`.

Yarn install alternatives:

- **macOS setup:**
  Yarn can be installed by running `brew install yarn`.

- **Windows setup:**
  Yarn can be installed by downloading the latest installer from: https://classic.yarnpkg.com/latest.msi

You can then check that yarn is installed by running `yarn --version`.

### Running locally

For local development follow the steps below:

#### 1. Install dependencies

Set your current directory to `jgraph/ui` and run `yarn install` command to install all dependencies. This will install all dependencies listed in [package.json](package.json) file without having to install them manually.

##### Some of the most important libraries:

- Vuex
- Vue Class Component
- VueRouter
- Bootstrap
- Moment
- XLSX

#### 2. Compiles and hot-reloads for development

For live compile and hot-reload development run the following command `yarn serve`. This will start the ui module on http://localhost:8080 .  
By default, the port is 8080. This can be changed by adding the `port` parameter as following: `yarn serve --port XXXX`.  
Dev server is configured to proxy pass all requests to http://localhost:7655/ where the `core` module should normally run.  
Target values for all proxies in [vue.config.js](vue.config.js) file should be changed if running `core` module on different port.

> **Note 1:** After accessing http://localhost:8080 you will need to add `AppServiceAuthSession` cookie for ui to 
> authenticate through a deployed and running instance of the jGraph application. The cookie has to be from the same 
> domain set in the env variable `JGRAPH_APPSERVICE_URL`

### Build

#### Build using `yarn`

In order to build the `ui` module, set your current directory to `jgraph/ui` and run the `yarn build` command.

Upon build completion the dist files will be outputted in `target/dist` directory due to `outputDir` setting in [vue.config.js](vue.config.js) file.

#### Build using `mvn`

The `mvn` command will build the `ui` module using the settings described in `pom.xml` file.  
Make sure to navigate to the `ui` directory and then run `mvn clean install`. This will only build the ui module, outputted in `target/dist` directory.

> **Note 2:** After building `ui` module using `mvn` you'll need to start the `core` module and navigate to http://localhost:7655. 
> Please follow the same steps in **Note 1**
