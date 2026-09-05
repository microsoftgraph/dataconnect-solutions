/*
------------------------------------------------------------------------------
PARAMETERS FOR DATAOPS RESOURCES
------------------------------------------------------------------------------
*/


param isSQLResourceExists bool


@description('Name of the Resource Group')
param resourceGroupName string = resourceGroup().name
//param resourceGroupName string

@description('Location for all resources.')
param location string = resourceGroup().location

@description('region for all resources.')
param region string

@description('The administrator username of the SQL logical server')
param sqlAdministratorLogin string

@description('The administrator password of the SQL logical server.')
@secure()
param sqlAdministratorLoginPassword string

@description('Tags to add to the resources')
param tag1 string 

@description('Tags to add to the resources')
param tag2 string 

@description('project name')
param project string 

@description('deployment environment for the resources')
param env string

@description('Value of the Subscription Id')
param subscriptionId string = subscription().subscriptionId

// @description('Name of the Virtual Network')
// param vnetName string

// @description('Name of the Subnet')
// param subnetName string

@description('Name of the sql login administrator')
param administratorLogin string

@description('Object Id of the service principle for sql login')
param administratorSid string

@description('Tenant Id')
param tenantId string = tenant().tenantId


/*
------------------------------------------------------------------------------
VARIABLES FOR DATA LANDING ZONE RESOURCES
------------------------------------------------------------------------------
*/
var name = concat('${project}-${region}-${env}')

/*
------------------------------------------------------------------------------
MODULE FOR CREATING AZURE KEY VAULT
------------------------------------------------------------------------------
*/

module keyvault './modules/keyvault/keyvault.bicep' = {
  name: 'kv-${name}-deployment'
  params: {
    location: location
    project: project
    region: region
    //env: env
    tag1: tag1
    tag2: tag2
  }
}

/*


/*
------------------------------------------------------------------------------
MODULE FOR CREATING DATA LAKE STORAGE
------------------------------------------------------------------------------
*/

module DataLakeStorageModule 'modules/datalake/datalake.bicep' = {
  name: 'adl-${name}-deployment'
  scope: resourceGroup(resourceGroupName)
  // dependsOn: [
  //   SynapseModule
  // ]
  params: {
    location: location
    tag1: tag1
    tag2: tag2
    region: region
    project: project
    env: env
    subscriptionId: subscriptionId
    resourceGroupName: resourceGroupName
    managed_identity_name: UserIdentityDeploy.outputs.managed_identity_name
    // vnetName: vnetName
    // subnetName: subnetName

  }
}


/*
------------------------------------------------------------------------------
MODULE FOR CREATING INITIAL SQL SERVER AND DATABASE
------------------------------------------------------------------------------
*/

module sqlServerModule 'modules/sqlserver/sqlserver.bicep' = if(!isSQLResourceExists) {
  name: 'sql-${name}-deployment'
  params: {
    sqlAdministratorLogin: sqlAdministratorLogin
    sqlAdministratorLoginPassword: sqlAdministratorLoginPassword
    location: location
    region: region
    tag1: tag1
    tag2: tag2
    project: project
    env: env
    subscriptionId: subscriptionId
    resourceGroupName: resourceGroupName
    managed_identity_name: UserIdentityDeploy.outputs.managed_identity_name
    // administratorLogin: administratorLogin
    // administratorSid: administratorSid
    // tenantId: tenantId
    // vnetName: vnetName
    // subnetName: subnetName
  }
}

/*
------------------------------------------------------------------------------
MODULE FOR CREATING INCREMENTAL SQL SERVER AND DATABASE
------------------------------------------------------------------------------
*/

module sqlServerModuleinc 'modules/sqlserver/sqlserverinc.bicep' = if(isSQLResourceExists) {
  name: 'sql-${name}-deployment'
  params: {
    sqlAdministratorLogin: sqlAdministratorLogin
    sqlAdministratorLoginPassword: sqlAdministratorLoginPassword
    location: location
    tag1: tag1
    tag2: tag2
    region: region
    project: project
    env: env
    subscriptionId: subscriptionId
    resourceGroupName: resourceGroupName
    managed_identity_name: UserIdentityDeploy.outputs.managed_identity_name
    administratorLogin: administratorLogin
    administratorSid: administratorSid
    tenantId: tenantId
    // vnetName: vnetName
    // subnetName: subnetName
  }
}

/*
------------------------------------------------------------------------------
MODULE FOR CREATING SYNAPSE WORKSPACE
------------------------------------------------------------------------------
*/

module synapseModule 'modules/synapse/synapse.bicep' = {
  name: 'syn-${name}-deployment'
  params: {
    sqlAdministratorLogin: sqlAdministratorLogin
    sqlAdministratorLoginPassword: sqlAdministratorLoginPassword
    location: location
    tag1: tag1
    tag2: tag2
    region: region
    project: project
    env: env
    subscriptionId: subscriptionId
    resourceGroupName: resourceGroupName
    managed_identity_name: UserIdentityDeploy.outputs.managed_identity_name
    adls_name: DataLakeStorageModule.outputs.adls_name
    // vnetName: vnetName
    // subnetName: subnetName
    // resourceId: subscriptionResourceId('Microsoft.Storage/storageAccounts@2022-05-01', DataLakeStorageModule.outputs.adls_resource_id)

  }
  dependsOn: [
    DataLakeStorageModule
  ]
}


/*
--------------------------------------------------------------------------------------------------------
CREATION OF USER ASSIGNED MANAGED IDENTITY 
--------------------------------------------------------------------------------------------------------
*/

module UserIdentityDeploy 'modules/managedidentity/managedidentity.bicep' = {
  name: 'id-${name}-deployment'
  scope: resourceGroup(resourceGroupName)
  params:{
    project : project
    env : env
    location:location
    tag1: tag1
    tag2: tag2
    region: region    
  }
}

/*
------------------------------------------------------------------------------
END OF MODULES
------------------------------------------------------------------------------
*/
