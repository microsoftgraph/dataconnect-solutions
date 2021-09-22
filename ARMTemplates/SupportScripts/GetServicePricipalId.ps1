param(
    [string] [Parameter(Mandatory=$true)] $AppId,
    [string] [Parameter(Mandatory=$true)] $TenantId,
    [string] [Parameter(Mandatory=$true)] $AppToGet
)

Install-Module Microsoft.Graph.Authentication -Force
Import-Module Microsoft.Graph.Authentication -Force
Install-Module Microsoft.Graph.Applications -Force
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

$application = Get-MgServicePrincipal -All:$true -Filter "AppID eq '$AppToGet'"
$DeploymentScriptOutputs = @{}
$DeploymentScriptOutputs['PrincipalId'] = $application.Id