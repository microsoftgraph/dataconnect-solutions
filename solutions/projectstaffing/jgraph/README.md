## Releasing a new version
Precondition: the code to be released should have been previously properly tested (both app and ADF pipelines)
1. add a new version specific file documenting manual update steps in docs/manual-app-update
2. increment the maven version of our deliverables in the pom.xml files (e.g. from x.y.z-SNAPSHOT to x.y.z)
3. increment the jgraph-common version by updating the property `jgraph.common.version` in the jgraph parent pom.xml (e.g. from x.y.z-1 to x.y.z)
4. update the default value of the `appservice.version` parameter in mainTemplate.json, to match the new release version
5. (optional) if several DB schema changes were performed since the last release, then group them all in a single file containing the version to be released in its name
6. ensure jgraph and all its modules compile locally and that all unit tests run successfully
7. ensure all recent changes to ADF entities were properly merged into `deployment/arm/data-factory/azuredeploy.json` and `deployment/arm/data-factory/pipelines.json`
    1. make sure that if there are changes made to employees or emails indices the changes are reflected in the definition of the exportEmployeeToAzureSearch and exportEmailsToAzureSearch activities
        and EnrichedEmployeesForIndexing and EnrichedMailsForIndexing datasets
8. commit the changes
9. build a new docker image via the [https://dev.azure.com/DataConnectSolutions/MGDC%20Solutions/_build?definitionId=2](https://dev.azure.com/DataConnectSolutions/MGDC%20Solutions/_build?definitionId=2) 
   Azure DevOps pipeline, using the new version (e.g. x.y.z) as `tag_override` argument. Make sure to enable the `pushDockerImages` flag.
10. create a new zip with the new deliverables (alternatively, the steps below are performed automatically by the Azure DevOps pipeline if the `uploadArtifacts` flag is enabled)
    1. from the `gdc` project root run `./bin/prepare_artifacts.sh`
    2. after this is complete, go to target/output and rename the `arm` folder into `gdc-<release_version>` (e.g. gdc-x.y.z)
    3. compress this folder into a zip archive (e.g. gdc-x.y.z.zip)
    4. upload the archive into the Project Staffing build artifacts AZBS container: `prjstfartifacts -> repository -> builds`
11. deploy the new artifacts on the target environment
    1. on `dev` environment, this usually involves jGraph and all ADB jobs (jars and python scripts)
12. ideally, test the new release on a clean test environment
13. if any problems occur, add the required fixes and repeat from step 6
14. if everything goes well:
    1. tag the new release in git: `git tag x.y.z` and `git push origin tag x.y.z`
    2. increment the maven version of our deliverables in the pom.xml files to a new snapshot version (e.g. from x.y.z to x.y.{z+1}-SNAPSHOT).
       1. If you do this via mass replace, be careful not to also replace jgraph.common.version or the version of our maven dependencies.
    3. post the release notes on gdc-core