/*
------------------------------------------------------------------------------
PARAMETERS FOR AZURE KEY VAULT RESOURCE
------------------------------------------------------------------------------
*/
@description('The Azure Region to deploy the resources into')
param location string = resourceGroup().location

@description('region for all resources.')
param region string

@description('project name')
param project string 

// @description('deployment environment for the resources')
// param env string

@description('Tags to add to the resources')
param tag1 string 

@description('Tags to add to the resources')
param tag2 string

// Create a short, unique suffix, that will be unique to each resource group
var uniqueSuffix = substring(uniqueString(resourceGroup().id), 0, 2)

@description('The name of the Key Vault')
var keyvaultName = concat('kv-${project}-${region}-${uniqueSuffix}')

/*
------------------------------------------------------------------------------
CREATION OF AZURE KEY VAULT
------------------------------------------------------------------------------
*/
resource keyVault 'Microsoft.KeyVault/vaults@2021-10-01' = {
  name: keyvaultName
  location: location
  tags: {
    environment: tag1
    location: tag2
  }
  properties: {
    enabledForDeployment: false
    enabledForDiskEncryption: false
    enabledForTemplateDeployment: true
    enableRbacAuthorization: false
    vaultUri: 'https://${keyvaultName}.vault.azure.net/'
    provisioningState: 'Succeeded'
    publicNetworkAccess: 'Enabled'
    softDeleteRetentionInDays:7
    enableSoftDelete: false
    enablePurgeProtection: true
    networkAcls: {
      bypass: 'AzureServices'
      defaultAction: 'Deny'
      ipRules: []
      virtualNetworkRules: [
            ]
    }
    accessPolicies: []
    sku: {
      family: 'A'
      name: 'standard'
    }
    //softDeleteRetentionInDays: 7
    tenantId: subscription().tenantId
  }
}

/*
------------------------------------------------------------------------------
OUTPUTS
------------------------------------------------------------------------------
*/
output keyvaultId string = keyVault.id
