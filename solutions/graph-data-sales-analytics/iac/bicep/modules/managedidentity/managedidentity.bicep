/*
------------------------------------------------------------------------------
PARAMETERS FOR AZURE MANAGED IDENTITY
------------------------------------------------------------------------------
*/

@description('Location for all resources.')
param location string = resourceGroup().location

@description('region for all resources.')
param region string

@description('deployment environment for the resources')
param env string

@description('project name')
param project string

@description('Tags to add to the resources')
param tag1 string 

@description('Tags to add to the resources')
param tag2 string 

@description('Name of the Managed Identity')
var managedIdentityName = concat('id-${project}-${region}-${env}01')

/*
------------------------------------------------------------------------------
CREATION OF AZURE MANAGED IDENTITY
------------------------------------------------------------------------------
*/

resource managedidentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: managedIdentityName
  location: location
  tags: {
    environment: tag1
    location: tag2
  }
}

/*
------------------------------------------------------------------------------
OUTPUTS
------------------------------------------------------------------------------
*/

output managed_identity_resource_id string = managedidentity.id
output managed_identity_name string = managedidentity.name
