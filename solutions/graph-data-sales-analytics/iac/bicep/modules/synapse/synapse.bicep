/*
------------------------------------------------------------------------------
PARAMETERS FOR AZURE SYNAPSE WORKSPACE
------------------------------------------------------------------------------
*/

@description('Location for all resources.')
param location string = resourceGroup().location

@description('region for all resources.')
param region string

@description('project name')
param project string 

@description('deployment environment for the resources')
param env string

@description('Tags to add to the resources')
param tag1 string 

@description('Tags to add to the resources')
param tag2 string 

@description('The administrator username of the SQL logical server')
param sqlAdministratorLogin string

@description('The administrator password of the SQL logical server.')
@secure()
param sqlAdministratorLoginPassword string

@description('Value of the Subscription Id')
param subscriptionId string = subscription().subscriptionId

@description('Name of the resource group')
param resourceGroupName string = resourceGroup().name

@description('Name of the Managed Identity')
param managed_identity_name string


@description('Name of the Azure Data Lake fetching from data_lake.bicep')
param adls_name string

/*
------------------------------------------------------------------------------
VARIABLES FOR AZURE SYNAPSE WORKSPACE
------------------------------------------------------------------------------
*/
var uniqueSuffix = substring(uniqueString(resourceGroup().id), 0, 2)

@description('Name of the synapse workspace')
var SynapseWorkspace = concat('syn-${project}-${region}-${env}-${uniqueSuffix}')

@description('Cleaned Name of the synapse workspace')
var SynapseWorkspaceCleaned = replace(SynapseWorkspace, '-', '')

@description('Name of the Apache spark pool')
var BigDataPoolName = concat('${SynapseWorkspaceCleaned}/synsp-${project}-${region}-${uniqueSuffix}')

@description('Cleaned Name of the Apache spark pool')
var BigDataPoolNameCleaned = replace(BigDataPoolName, '-', '')

@description('Name of the Apache spark pool')
var sqlPoolName = concat('${SynapseWorkspaceCleaned}/syndp-${project}-${region}-${uniqueSuffix}')

@description('Cleaned Name of the Apache spark pool')
var sqlPoolNameNameCleaned = replace(sqlPoolName, '-', '')

@description('Name of the firewall rules name')
var firewallRulesname = concat('${SynapseWorkspaceCleaned}/allowAll')

@description('Resource ID of the Azure Data Lake Storage')
var adls_resource_id = '/subscriptions/${subscriptionId}/resourceGroups/${resourceGroupName}/providers/Microsoft.Storage/storageAccounts/${adls_name}'

@description('URL of the Azure Data Lake Storage')
var adlsURL = 'https://${adls_name}.dfs.core.windows.net/'

/*
------------------------------------------------------------------------------
CREATION OF AZURE SYNAPSE WORKSPACE
------------------------------------------------------------------------------
*/

resource synapseWorkspace 'Microsoft.Synapse/workspaces@2021-06-01' = {
  name: SynapseWorkspaceCleaned
  location: location
  tags: {
    environment: tag1
    location: tag2
  }
  identity: {
    type: 'SystemAssigned,UserAssigned'
    userAssignedIdentities: {
      '/subscriptions/${subscriptionId}/resourceGroups/${resourceGroupName}/providers/Microsoft.ManagedIdentity/userAssignedIdentities/${managed_identity_name}': {}
    }
  }
  properties: {
    azureADOnlyAuthentication: false
    defaultDataLakeStorage: {
      accountUrl: adlsURL
      createManagedPrivateEndpoint: true
      filesystem: 'synapse'
      resourceId: adls_resource_id
    }
    managedVirtualNetwork: 'default'
    managedVirtualNetworkSettings: {
      allowedAadTenantIdsForLinking: []
      preventDataExfiltration: false
    }
    publicNetworkAccess: 'Enabled'
// workspace admin id of the user group in active directory
    cspWorkspaceAdminProperties: {
      initialWorkspaceAdminObjectId: 'cc5f61c9-5e15-4de0-b2ff-30b21f762e17'
    }
    sqlAdministratorLogin: sqlAdministratorLogin
    sqlAdministratorLoginPassword: sqlAdministratorLoginPassword
    trustedServiceBypassEnabled: false
  }
}


resource firewallRules 'Microsoft.Synapse/workspaces/firewallRules@2021-06-01' = {
  name: firewallRulesname
  dependsOn: [
    synapseWorkspace
  ]
  properties: {
    //startIpAddress: '0.0.0.0'
    //endIpAddress: '255.255.255.255'
  }
}

resource bigDataPool 'Microsoft.Synapse/workspaces/bigDataPools@2021-06-01-preview' = {
  name: BigDataPoolNameCleaned
  location: location
  properties: {
    sparkVersion: '3.3'
    nodeCount: 0
    nodeSize: 'Medium'
    nodeSizeFamily: 'MemoryOptimized'
    autoScale: {
      enabled: true
      minNodeCount: 3
      maxNodeCount: 6
    }
    autoPause: {
      delayInMinutes: 15
      enabled: true
    }
    isComputeIsolationEnabled: false
    sessionLevelPackagesEnabled: true
    cacheSize: 50
    dynamicExecutorAllocation: {
      enabled: false
      // minExecutors: 1
      // maxExecutors: 5
    }
    isAutotuneEnabled: false
    provisioningState: 'Succeeded'
  }
  tags: {
    environment: tag1
    location: tag2
  }
  dependsOn: [
    synapseWorkspace
  ]
}



resource sqlPools 'Microsoft.Synapse/workspaces/sqlPools@2021-06-01' = {
  parent: synapseWorkspace
  name: sqlPoolNameNameCleaned
  location: location // Replace this with the desired region for your SQL pool
  tags: {}
  sku: {
    name: 'DW100c'
    capacity: 0
  }
  properties: {
    status: 'Paused'
    maxSizeBytes: 263882790666240
    collation: 'SQL_Latin1_General_CP1_CI_AS'
    //creationDate: '2023-07-06T10:32:35.047Z'
    storageAccountType: 'GRS'
    provisioningState: 'Succeeded'
  }
}




/*
------------------------------------------------------------------------------
OUTPUTS
------------------------------------------------------------------------------
*/

output sqlserver_resource_id string = synapseWorkspace.id
output sqlserver_name string = synapseWorkspace.name
