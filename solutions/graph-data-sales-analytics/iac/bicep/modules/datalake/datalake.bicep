/*
------------------------------------------------------------------------------
PARAMETERS FOR AZURE DATA LAKE STORAGE
------------------------------------------------------------------------------
*/
targetScope = 'resourceGroup'
@description('Region for deployment of resource')
param location string = resourceGroup().location

@description('region for all resources.')
param region string

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

@description('Name of the resource group')
param resourceGroupName string = resourceGroup().name

@description('Name of the Managed Identity')
param managed_identity_name string

/*
------------------------------------------------------------------------------
VARIABLES FOR AZURE DATA LAKE STORAGE
------------------------------------------------------------------------------
*/

// Create a short, unique suffix, that will be unique to each resource group
var uniqueSuffix = substring(uniqueString(resourceGroup().id), 0, 2)

@description('Name of the data lake storage resource')
var storageAccounts_datalake_name = concat('adls-${project}-${region}-${env}-${uniqueSuffix}')

@description('Cleaned Name of the data lake storage resource')
var storageNameCleaned = replace(storageAccounts_datalake_name, '-', '')



/*
------------------------------------------------------------------------------
CREATION OF AZURE DATA LAKE STORAGE
------------------------------------------------------------------------------
*/

resource storage 'Microsoft.Storage/storageAccounts@2022-05-01' = {
  name: storageNameCleaned
  location: location
  tags: {
    environment: tag1
    location: tag2
  }
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '/subscriptions/${subscriptionId}/resourceGroups/${resourceGroupName}/providers/Microsoft.ManagedIdentity/userAssignedIdentities/${managed_identity_name}': {}
    }
  }
  sku: {
    name: 'Standard_LRS'
  }
  kind: 'StorageV2'
  properties: {
    dnsEndpointType: 'Standard'
    defaultToOAuthAuthentication: false
    allowCrossTenantReplication: false
    isSftpEnabled: false
    minimumTlsVersion: 'TLS1_2'
    allowBlobPublicAccess: true
    allowSharedKeyAccess: true
    isHnsEnabled: true
    networkAcls: {
      bypass: 'AzureServices'
      virtualNetworkRules: []
      ipRules: []
      defaultAction: 'Deny'
    }
    supportsHttpsTrafficOnly: true
    encryption: {
      requireInfrastructureEncryption: false
      services: {
        file: {
          keyType: 'Account'
          enabled: true
        }
        blob: {
          keyType: 'Account'
          enabled: true
        }
      }
      keySource: 'Microsoft.Storage'
    }
    accessTier: 'Hot'
    publicNetworkAccess:'Disabled'
  }
}

/*
------------------------------------------------------------------------------
CREATION OF AZURE DATA LAKE STORAGE BLOB SERVICES
------------------------------------------------------------------------------
*/

resource storageAccounts_datalake_name_default 'Microsoft.Storage/storageAccounts/blobServices@2021-08-01' = {
  parent: storage
  name: 'default'
  sku: {
    name: 'Standard_LRS'
    tier: 'Standard'
  }
  properties: {
    containerDeleteRetentionPolicy: {
      enabled: true
      days: 7
    }
    cors: {
      corsRules: []
    }
    deleteRetentionPolicy: {
      enabled: true
      days: 7
    }
  }
resource storageAccounts_container_raw_name 'containers@2021-08-01' = {
  name:'raw'
  properties:{
    publicAccess:'None'
  }
}
resource storageAccounts_container_synapse_name 'containers@2021-08-01' = {
  name:'synapse'
  properties:{
    publicAccess:'None'
  }
}
}

/*
------------------------------------------------------------------------------
CREATION OF AZURE DATA LAKE STORAGE FILE SERVICES
------------------------------------------------------------------------------
*/

resource Microsoft_Storage_storageAccounts_fileServices_storageAccounts_datalake_name_default 'Microsoft.Storage/storageAccounts/fileServices@2021-08-01' = {
  parent: storage
  name: 'default'
  sku: {
    name: 'Standard_LRS'
    tier: 'Standard'
  }
  properties: {
    cors: {
      corsRules: []
    }
    shareDeleteRetentionPolicy: {
      enabled: true
      days: 7
    }
  }
}

/*
------------------------------------------------------------------------------
CREATION OF AZURE DATA LAKE STORAGE QUEUE SERVICES
------------------------------------------------------------------------------
*/

resource Microsoft_Storage_storageAccounts_queueServices_storageAccounts_datalake_name_default 'Microsoft.Storage/storageAccounts/queueServices@2021-08-01' = {
  parent: storage
  name: 'default'
  properties: {
    cors: {
      corsRules: []
    }
  }
}

/*
------------------------------------------------------------------------------
CREATION OF AZURE DATA LAKE STORAGE TABLE SERVICES
------------------------------------------------------------------------------
*/

resource Microsoft_Storage_storageAccounts_tableServices_storageAccounts_datalake_name_default 'Microsoft.Storage/storageAccounts/tableServices@2021-08-01' = {
  parent: storage
  name: 'default'
  properties: {
    cors: {
      corsRules: []
    }
  }
}


/*
------------------------------------------------------------------------------
OUTPUTS
------------------------------------------------------------------------------
*/
output adls_resource_id string = storage.id
output adls_name string = storage.name

