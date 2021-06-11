{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "sqlserver.admin.login": {
      "value": "testAdmin"
    },
    "sqlserver.admin.password": {
      "value": "9lrxB74_en!6"
    },
    "sqlserver.name": {
      "value": "prjstfdb3ofyrz"
    },
    "keyvault.name": {
      "value": "prjstfapp3ofyrz"
    },
    "m365Adf-keyvault.name": {
      "value": "prjstfbackend3ofyrz"
    },
    "keyvault.enableSoftDelete": {
      "value": false
    },
    "storageAccount.name": {
      "value": "prjstfstorage3ofyrz"
    },
    "testStorageAccount.name": {
      "value": "prjstfdemodata3ofyrz"
    },
    "search-service.name": {
      "value": "prjstf-search-3ofyrz"
    },
    "search-service.sku": {
      "value": "basic"
    },
    "search-service.emails-index": {
      "value": "gdc-emails"
    },
    "sqlsever.database.sku.family": {
      "value": "Gen5"
    },
    "sqlsever.database.sku.name": {
      "value": "GP_S_Gen5"
    },
    "sqlsever.database.sku.edition": {
      "value": "GeneralPurpose"
    },
    "sqlsever.database.sku.capacity": {
      "value": 1
    },
    "sqlsever.sql-auth": {
      "value": true
    },
    "appservice.name": {
      "value": "prjstf-app-3ofyrz"
    },
    "appservice.version": {
      "value": "{{ TAG }}"
    },
    "adf.name": {
      "value": "prjstf-adf-3ofyrz"
    },
    "adb.workspace.name": {
      "value": "prjstf-adb-workspace-3ofyrz"
    },
    "gdc_data_ingestion_mode": {
      "value": "simulated_mode"
    },
    "gdcAdmins.groupId": {
      "value": "326c04c6-d09d-48c6-b650-63a6f9750e36"
    },
    "gdc_employees_ad_group_id": {
      "value": "a269d31b-25d2-44b8-bca0-59b5fc423f31"
    },
    "alert.admin.email": {
      "value": "gdc-test@bpcs.com"
    },
    "alert.admin.fullname": {
      "value": "Test pipeline"
    },
    "airtable.base-id": {
      "value": ""
    },
    "airtable.api-key": {
      "value": ""
    },
    "emails.backfill.time.span": {
      "value": 7
    },
    "gdc-service-sp.name": {
      "value": "prjstf-app-3ofyrz-gdc-service"
    },
    "gdc-m365-reader-sp.name": {
      "value": "prjstf-app-3ofyrz-gdc-m365-reader"
    }
  }
}