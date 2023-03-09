param(
    [string] [Parameter()] $AppId,
    [string] [Parameter()] $TenantId,
    [string] [Parameter()] $AppToGet,    
    [string] [Parameter()] $ServicePrincipalName
)

Install-Module Microsoft.Graph.Authentication -Force -RequiredVersion 1.10.0
Import-Module Microsoft.Graph.Authentication -Force
Install-Module Microsoft.Graph.Applications -Force -RequiredVersion 1.10.0
Import-Module Microsoft.Graph.Applications -Force

$url = "https://login.microsoftonline.com/$TenantId/oauth2/v2.0/token"
$body = @{
    scope = "https://graph.microsoft.com/.default"
    grant_type = "client_credentials"
    client_secret = ${Env:AppSecret}
    client_info = 1
    client_id = $AppId
}

$OAuthReq = Invoke-RestMethod -Uri $url -Method Post -Body $body
$AccessToken = $OAuthReq.access_token
Connect-MgGraph -AccessToken $AccessToken | Out-Null

$DeploymentScriptOutputs = @{}
if (-not [String]::IsNullOrEmpty($AppToGet))
{
    $application = Get-MgServicePrincipal -All:$true -Filter "AppID eq '$AppToGet'"
}
elseif (-not [String]::IsNullOrEmpty($ServicePrincipalName))
{
    $application = Get-MgServicePrincipal -All:$true -Filter "DisplayName eq '$ServicePrincipalName'"
}
$DeploymentScriptOutputs['PrincipalId'] = $application.Id
