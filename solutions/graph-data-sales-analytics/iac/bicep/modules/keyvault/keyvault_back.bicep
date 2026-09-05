//param resource_prefix string
//param sequence_no string
param region_short string

param tenantId string = subscription().tenantId
param location string = resourceGroup().location
param project_name string
//param vaults_BIKeyVault_name string = 'kv-${project_name}${env}${region_short}${sequence_no}-${resource_prefix}'

//param servers_admin_name string
@description('Deployment environment')
param env string

@description('Name of the resource')
param sqldb_metadata_name string = 'sqldb${project_name}${env}${region_short}'
param servers_metadata_name string = 'sqldbserver${project_name}${env}${region_short}'

param sql_admin_user string 
@secure()
param sql_admin_password string 
//param servers_admin_sid string 
param adls_resource_id string
// param sqldw_admin_user string
// @secure()
// param sqldw_admin_password string
// param sqldw_server_name string
// param sqldw_name string
@secure()
param sf_Sales_Cloud_Password string
@secure()
param sf_Sales_Cloud_SecurityToken string
// @secure()
// param storage_Account_Key string
param client_ID string
@secure()
param client_Secret_Key string
@secure()
param db_Security_Token string
param db_URL string
//param dataFactories_adf_principalId string

var uniqueSuffix = substring(uniqueString(resourceGroup().id), 0, 2)
var vaults_BIKeyVault_name  = 'kv${project_name}${env}${region_short}${uniqueSuffix}'

// resource servers_metadata_name_resource 'Microsoft.Sql/servers@2019-06-01-preview' = {
//   name: servers_metadata_name
//   location: location
//   tags: {}
//   identity: {
//     type: 'SystemAssigned'
//   }
//   properties: {
//     administratorLogin: sql_admin_user
//     administratorLoginPassword: sql_admin_password
//     version: '12.0'
//     minimalTlsVersion: '1.2'
//     publicNetworkAccess: 'Enabled'
//   }
// }

// resource servers_metadata_name_ActiveDirectory 'Microsoft.Sql/servers/administrators@2019-06-01-preview' = {
//   parent: servers_metadata_name_resource
//   name: 'ActiveDirectory'
//   properties: {
//     administratorType: 'ActiveDirectory'
//     login: servers_admin_name
//     sid: servers_admin_sid
//     tenantId: tenantId
//   }
// }



// resource servers_metadata_name_sqldb_metadata_name 'Microsoft.Sql/servers/databases@2020-08-01-preview' = {
//   parent: servers_metadata_name_resource
//   name: sqldb_metadata_name
//   location: location
//   tags: {}
//   sku: {
//     name: 'GP_S_Gen5'
//     tier: 'GeneralPurpose'
//     capacity: 4
//     family:'Gen5'
//   }
//   properties: {
//     collation: 'SQL_Latin1_General_CP1_CI_AS'
//     maxSizeBytes: 2147483648
//     catalogCollation: 'SQL_Latin1_General_CP1_CI_AS'
//     zoneRedundant: false
//     readScale: 'Disabled'
//     storageAccountType: 'LRS'
//   }
// }


resource vaults_kvsynmetadatadev_name_resource 'Microsoft.KeyVault/vaults@2021-11-01-preview' = {
  name: vaults_BIKeyVault_name
  location: location
  properties: {
    sku: {
      family: 'A'
      name: 'standard'
    }
    tenantId: tenantId
    networkAcls: {
      bypass: 'AzureServices'
      defaultAction: 'Deny'
      ipRules: []
      virtualNetworkRules: [
            ]
    }
    accessPolicies: [
          
      // {
      //   objectId: servers_admin_sid
      //   permissions: {
      //     certificates: [
      //       'Get'
      //       'List'
      //       'Update'
      //       'Create'
      //       'Import'
      //       'Delete'
      //       'Recover'
      //       'Backup'
      //       'Restore'
      //       'ManageContacts'
      //       'ManageIssuers'
      //       'GetIssuers'
      //       'ListIssuers'
      //       'SetIssuers'
      //       'DeleteIssuers'
      //   ]
      //   keys: [
      //       'Get'
      //       'List'
      //       'Update'
      //       'Create'
      //       'Import'
      //       'Delete'
      //       'Recover'
      //       'Backup'
      //       'Restore'
      //       'GetRotationPolicy'
      //       'SetRotationPolicy'
      //       'Rotate'
      //   ]
      //   secrets: [
      //       'Get'
      //       'List'
      //       'Set'
      //       'Delete'
      //       'Recover'
      //       'Backup'
      //       'Restore'
      //   ]
      //   }
      //   tenantId: tenantId
      // }
      // {
      //   objectId:dataFactories_adf_principalId
      //   tenantId:tenantId
      //   permissions:{
      //     secrets:[
      //       'Get'
      //       'List'
      //     ]
      //   }
      // }
    ]
    enabledForDeployment: false
    enabledForDiskEncryption: false
    enabledForTemplateDeployment: true
    // enableSoftDelete: false
    enableRbacAuthorization: false
    vaultUri: 'https://${vaults_BIKeyVault_name}.vault.azure.net/'
    provisioningState: 'Succeeded'
    publicNetworkAccess: 'Enabled'
    softDeleteRetentionInDays:7
  }
}

@description('Secret - sfsalescloudpassword')
resource sf_sales_cloud_password_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'sfsalescloudpassword'
  properties:{
    contentType:'string'
    value: sf_Sales_Cloud_Password
  }
}

@description('Secret - sfsalescloudsecuritytoken')
resource sfsalescloudsecuritytoken_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'sfsalescloudsecuritytoken'
  properties:{
    contentType:'string'
    value: sf_Sales_Cloud_SecurityToken
  }
}

@description('Secret - storageaccountkey')
resource storageaccountkey_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'storageaccountkey'
  properties:{
    contentType:'string'
    value: listKeys(adls_resource_id, '2019-04-01').keys[0].value
  }
}

@description('Secret - clientID')
resource clientID_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'clientID'
  properties:{
    contentType:'string'
    value: client_ID
  }
}


@description('Secret - ClientSecretKey')
resource ClientSecretKey_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'clientSecretKey'
  properties:{
    contentType:'string'
    value: client_Secret_Key
  }
}

@description('Secret - dbsecuritytoken')
resource dbsecuritytoken_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'dbsecuritytoken'
  properties:{
    contentType:'string'
    value: db_Security_Token
  }
}

@description('Secret - sqladminusername')
resource sqladminusername_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'sqladminusername'
  properties:{
    contentType:'string'
    value: sql_admin_user
  }
}

@description('Secret - sqlpassword')
resource sqlpassword_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'sqlpassword'
  properties:{
    contentType:'string'
    value: sql_admin_password
  }
}

@description('Secret - TenantId')
resource TenantId_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'TenantId'
  properties:{
    contentType:'string'
    value: tenantId
  }
}

@description('Secret - dbURL')
resource dbURL_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'dbURL'
  properties:{
    contentType:'string'
    value: db_URL
  }
}


resource sqldb_connection_secret_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
  parent:vaults_kvsynmetadatadev_name_resource
  name: 'sqlconnectionstring'
  properties:{
    contentType:'string'
    value: 'Server=${servers_metadata_name}.database.windows.net;Database=${sqldb_metadata_name};User Id=${sql_admin_user};Password=${sql_admin_password}'
  }
}


// resource sqldw_connection_secret_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
//   parent:vaults_kvadfmetadatadev_name_resource
//   name: 'SynDBConnection'
//   properties:{
//     contentType:'string'
//     value: 'Server=${sqldw_server_name}.database.windows.net;Database=${sqldw_name};User Id=${sqldw_admin_user};Password=${sqldw_admin_password}'
//   }

// }



// resource adlskey_secret_resource 'Microsoft.KeyVault/vaults/secrets@2021-11-01-preview' ={
//   parent:vaults_kvadfmetadatadev_name_resource
//   name: 'ADLSKey'
//   properties:{
//     contentType:'string'
//     value: listKeys(adls_resource_id, '2019-04-01').keys[0].value
//   }

// }

// output sql_db_name string = servers_metadata_name_resource.name
// output sql_db_resource_id string = servers_metadata_name_resource.id
output sql_server_name string = servers_metadata_name
output vaults_BIKeyVault_name string = vaults_BIKeyVault_name
output sqldb_connection_secret string = sqldb_connection_secret_resource.name
// output sqldw_connection_secret string = sqldw_connection_secret_resource.name
output adlskey_secret string = storageaccountkey_resource.name
