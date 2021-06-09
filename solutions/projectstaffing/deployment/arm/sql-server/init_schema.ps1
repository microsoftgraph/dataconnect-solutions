#! /usr/bin/pwsh
param (
    [Parameter(Mandatory=$true)][string]$sqlServerName,
    [Parameter(Mandatory=$true)][string]$sqlDBName,
    [Parameter(Mandatory=$true)][string]$ResourceGroup,
    [Parameter(Mandatory=$true)][string]$useSqlAuth,
    [Parameter(Mandatory=$False)][PSCredential]$sqlServerCred,
    [Parameter(Mandatory=$False)][String]$subscriptionId
)

if (-not (Get-Command Invoke-Sqlcmd -ErrorAction SilentlyContinue)) {
    Write-Warning "Unabled to find Invoke-SqlCmd cmdlet"
    Write-Output "Installling SqlServer module..."
    Install-Module -Name SqlServer -Confirm:$False -Force
}
$ErrorActionPreference = "Stop"

Import-Module -Name SqlServer -ErrorAction Stop
$agentIP = (Invoke-WebRequest -Uri "http://checkip.dyndns.com" -Method GET).Content -replace "[^\d\.]"
Write-Output " Adding agentIp ${agentIP} to SQL server firewall "

if ( $subscriptionId -and !$useSqlAuth ) {
    if (-not (Get-Command Select-AzSubscription -ErrorAction SilentlyContinue)) {
        Write-Warning "Unabled to find Select-AzSubscription cmdlet"
        Write-Output "Installling Az module..."
        Install-module Az -AllowClobber -Confirm:$False -Force
    }
    Import-Module -Name Az -ErrorAction Stop
    Write-Output " Switching to subscription $subscriptionId "
    Select-AzSubscription -Subscription $subscriptionId
}
New-AzSqlServerFirewallRule -ResourceGroupName $ResourceGroup -ServerName $sqlServerName -FirewallRuleName "GdcDeployerIP" -StartIPAddress $agentIp -EndIPAddress $agentIp
try
{
    $access_token = $null
    if (!$useSqlAuth)
    {
        $response = Invoke-WebRequest `
        -Uri 'http://169.254.169.254/metadata/identity/oauth2/token?api-version=2018-02-01&resource=https%3A%2F%2Fdatabase.windows.net' `
        -Method GET `
        -Headers @{ Metadata = "true" }
        $access_token = ($response.Content | ConvertFrom-Json).access_token
    }
    if ( $useSqlAuth -and ( !$sqlServerCred) ) {
        $sqlServerCred = Get-Credential -Message "Enter your SQL Admin credential"
    }

    $sql_files = @("schema.sql", "stored_procedures.sql", "data.sql", "custom-init.sql" )

    foreach ($sql_file in $sql_files)
    {
        if (Test-Path $sql_file)
        {
            $absScriptPath = (Resolve-Path  $sql_file).Path
            if ($useSqlAuth)
            {
                Write-Output "Inializing SQL Schema from $absScriptPath using SQL Server Authentication "
                Invoke-Sqlcmd -AbortOnError -OutputSqlErrors $true -ServerInstance "${sqlServerName}.database.windows.net" -Database $sqlDBName  -Credential $sqlServerCred -InputFile $absScriptPath
            }
            else
            {
                Write-Output "Inializing SQL Schema from $absScriptPath using Windows Authentication "
                Invoke-Sqlcmd -AbortOnError -OutputSqlErrors $true -ServerInstance "${sqlServerName}.database.windows.net" -Database $sqlDBName -AccessToken $access_token  -InputFile $absScriptPath
            }
        }
    }
} Finally {
    Remove-AzSqlServerFirewallRule -ResourceGroupName $ResourceGroup -ServerName $sqlServerName -FirewallRuleName "GdcDeployerIP"
    Write-Output "agentIp ${agentIP} has been removed from SQL server firewall "
}
Write-Output "SqlServer ${sqlServerName} has been provisioned."
