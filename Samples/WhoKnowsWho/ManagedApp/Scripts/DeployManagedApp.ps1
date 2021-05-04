# PS Script to create managed application definition

<#

.SYNOPSIS
Use this script to create the managed app definition

.DESCRIPTION
Will create a resource group and a storage account 
Uploads the artifacts to the container that was created in the storage account
Creates the Managed Application definition
User needs to specify ArtifactStagingDirectory (local folder path from where app.zip will be uploaded) or PackageFileUri(URI value of the uploaded app.zip)

.EXAMPLE
.\DeployManagedApp.ps1 -ResourceGroupLocation "East US 2" -ArtifactStagingDirectory "E:\managedApp"
.\DeployManagedApp.ps1 -ResourceGroupLocation "West Central US" -PackageFileUri "https://samplestorage.blob.core.windows.net/appcontainer/app.zip"
.\DeployManagedApp.ps1 -ArtifactStagingDirectory "E:\share" -ResourceGroupLocation "West Central US" -StorageAccountName "SampleStorageAccount" -GroupId <group-id> -ResourceGroupName "sampleResourceGroup"

.NOTES
Required params: -ResourceGroupLocation

#>
Param(
    [string] [Parameter(Mandatory=$true)] $ResourceGroupLocation,
    [string] $ArtifactStagingDirectory, #local folder path from where app.zip will be uploaded.
    [string] $ResourceGroupName,
    [string] $GroupId, #user group or application for managing the resources on behalf of the customer.
    [string] $StorageAccountName,
    [string] $PackageFileUri #URI value of the uploaded app.zip.
)

$creds = Get-Credential
$login = Login-AzureRmAccount -Credential $creds
$registration = Register-AzureRmResourceProvider -ProviderNamespace Microsoft.Solutions

if($PackageFileUri -eq "" -And $ArtifactStagingDirectory -ne ""){
    $ArtifactsResourceGroup = $ArtifactStagingDirectory.replace(':\','') #remove .\ if present

    # Create a storage account name if none was provided
    if($StorageAccountName -eq "") {
        $subscriptionId = ((Get-AzureRmContext).Subscription.Id).Replace('-', '').substring(0, 19)
        $StorageAccountName = "stage$subscriptionId"
    }

    $storageAccount = (Get-AzureRmStorageAccount | Where-Object{$_.StorageAccountName -eq $StorageAccountName})

    # Create the storage account if it doesn't already exist
    if ($storageAccount -eq $null) {
        Write-Host "Creating a new resource group..." -foregroundcolor "Yellow"
        New-AzureRmResourceGroup -Name $ArtifactsResourceGroup -Location $ResourceGroupLocation -Verbose -Force -ErrorAction Stop 

        Write-Host "Creating a new storage account for uploading the artifacts..." -foregroundcolor "Yellow"
        $storageAccount = New-AzureRmStorageAccount -ResourceGroupName $ArtifactsResourceGroup `
                                            -Name $StorageAccountName `
                                            -Location $ResourceGroupLocation `
                                            -SkuName Standard_LRS `
                                            -Kind Storage `
    }

    $appStorageContainer = (Get-AzureStorageContainer -Context $storageAccount.Context | Where-Object {$_.Name -eq "appcontainer"})

    if ($appStorageContainer -eq $null) {
        Write-Host "Creating a new container in the storage account for uploading the artifacts..." -foregroundcolor "Yellow"
        New-AzureStorageContainer -Name appcontainer `
                          -Context $storageAccount.Context -Permission blob
    }

    Write-Host "Uploading the artifacts..." -foregroundcolor "Yellow"
    Set-AzureStorageBlobContent -File "$($ArtifactStagingDirectory)\app.zip" `
                            -Container appcontainer `
                            -Blob "app.zip" `
                            -Context $storageAccount.Context `
                            -Force

    $blob = Get-AzureStorageBlob -Container appcontainer `
                             -Blob app.zip `
                             -Context $storageAccount.Context

    Write-Host "Successfully uploaded the artifacts." -foregroundcolor "Green"
    $PackageFileUri = $blob.ICloudBlob.StorageUri.PrimaryUri.AbsoluteUri
}

if($PackageFileUri -eq "") {
    Throw "You must supply a value for -PackageFileUri or for -ArtifactStagingDirectory" 
}

if($GroupId -eq "") {
    $user = Connect-AzureAD -Credential $creds
    $GroupId = (Get-AzureADUser -ObjectId $user.Account).ObjectId
}

$ownerID=(Get-AzureRmRoleDefinition -Name Owner).Id

if($ResourceGroupName -eq "") {
    $ResourceGroupName = "msgraphdataconnectorg"
}

Write-Host "Creating a new resource group for publishing the managed application definition..." -foregroundcolor "Yellow"
$ManagedAppDefRG = New-AzureRmResourceGroup -Name $ResourceGroupName -Location $ResourceGroupLocation -Verbose -Force -ErrorAction Stop 

Write-Host "Publishing the managed application definition..." -foregroundcolor "Yellow"
New-AzureRmManagedApplicationDefinition -Name "MSGraphDataConnectSampleApp" `
                                        -Location $ResourceGroupLocation `
                                        -ResourceGroupName $ResourceGroupName `
                                        -LockLevel None `
                                        -DisplayName "MS Graph Data Connect Who Knows Whom Sample App" `
                                        -Description "Who Knows Whom in your company!" `
                                        -Authorization "$($GroupId):$($ownerID)" `
                                        -PackageFileUri $PackageFileUri

Write-Host "Successfully published the managed application definition" -foregroundcolor "Green"
