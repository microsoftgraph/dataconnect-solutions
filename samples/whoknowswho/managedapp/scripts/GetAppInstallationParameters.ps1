# PS Script to create managed application definition

<#

.SYNOPSIS
Generates the sample MS Graph Data Connect app parameters

.DESCRIPTION
Creates the destination and source azure active directory application

.EXAMPLE
.\GetAppInstallationParameters.ps1
.\GetAppInstallationParameters.ps1 -SubscriptionId "c99f5700-3e11-4295-bf34-20562e5fa5ba" -ApplicationDisplayName "destsampleapp"

#>

Param (
 # Use to set subscription. If no value is provided, default subscription is used. 
 [String] $SubscriptionId,
 [String] $ApplicationDisplayName
)

$creds = Get-Credential

if ([string]::IsNullOrEmpty($(Get-AzureRmContext).Account)) { 
    $login = Login-AzureRmAccount -Credential $creds
}

$adConnection = Connect-AzureAD -Credential $creds
$user = Get-AzureADUser -SearchString $adConnection.Account

Import-Module AzureRM.Resources

if ($SubscriptionId -eq "") 
{
   $SubscriptionId = (Get-AzureRmContext).Subscription.Id
}
else
{
   Set-AzureRmContext -SubscriptionId $SubscriptionId
}

Write-Host "Creating a new destination AAD Application with AAD Read and Sign-in Permission" -foregroundcolor "Yellow"

# Create a .NET Generics List of RequiredResourceAccess
$requiredResources = [System.Collections.Generic.List[Microsoft.Open.AzureAD.Model.RequiredResourceAccess]]::New()

# Retrive the AAD SPN and AAD Read and Sign-in Permissions info
$aad_spn = Get-AzureADServicePrincipal -Filter "DisplayName eq 'Windows Azure Active Directory'"
$aad_Oauth2_readAndSignInPerm = $aad_spn | select -expand Oauth2Permissions | ? {$_.value -eq "User.Read"}

# Create ResourceAccess object representing Read and Sign-in Permission
$aad_readAndSignInPerm = [Microsoft.Open.AzureAD.Model.ResourceAccess]::New()
$aad_readAndSignInPerm.Id = $aad_Oauth2_readAndSignInPerm.Id
$aad_readAndSignInPerm.Type = "Scope"

# Create RequiredResourceAccess object representing Read and Sign-In Permission on AAD
$aad_ResourceAccess = [Microsoft.Open.AzureAD.Model.RequiredResourceAccess]::New()
$aad_ResourceAccess.ResourceAppId = $aad_spn.AppId
$aad_ResourceAccess.ResourceAccess = $aad_readAndSignInPerm

$requiredResources.Add($aad_ResourceAccess)

if ($ApplicationDisplayName -eq "") 
{
   $ApplicationDisplayName = "dest" + [string]([System.Guid]::NewGuid().ToString().Substring(0,8))
}

$ReplyUrls = "https://" + $ApplicationDisplayName + ".azurewebsites.net"

$guidtemp = [guid]::NewGuid()
$tenant = Get-AzureRmTenant

$IndentifierUri = "https://" + $tenant.Directory + $guidtemp.Guid

$AadApplication = New-AzureADApplication -DisplayName $ApplicationDisplayName -HomePage $ReplyUrls -ReplyUrls $ReplyUrls -IdentifierUris $IndentifierUri -RequiredResourceAccess $requiredResources

$ServicePrincipal = New-AzureADServicePrincipal -AppId $AadApplication.AppId

$now = Get-Date
$DestPassword = New-AzureADApplicationPasswordCredential -ObjectId $AadApplication.ObjectId -StartDate $now

$owners = Get-AzureADApplicationOwner -ObjectId $AadApplication.ObjectId
if ($owners.Count -eq 0)
{
   # set the user as the owner of the application
   $addOwnerReturn = Add-AzureADApplicationOwner -ObjectId $AadApplication.ObjectId -RefObjectId $user.ObjectId
}

Write-Host $ApplicationDisplayName " has been created successfully. " -foregroundcolor "Green"

Write-Host "`n App installation parameter details:"

Write-Host "`t Website name : " $ApplicationDisplayName

Write-Host "`t Destination ADLS service principal AAD Id : " $ServicePrincipal.ObjectId

Write-Host "`t Destination ADLS service principal Id : " $ServicePrincipal.AppId

Write-Host "`t Destination ADLS service principal key : " $DestPassword.Value

$date = [System.DateTime]::UtcNow.Date.ToString("yyyy-MM-ddTHH:mm:ssZ")
Write-Host "`t Trigger start time: " $date