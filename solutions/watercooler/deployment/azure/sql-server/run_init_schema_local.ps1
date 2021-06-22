#! /usr/bin/pwsh
param (
    [Parameter(Mandatory=$true)][string]$sqlServerName,
    [Parameter(Mandatory=$true)][string]$ResourceGroup,
    [Parameter(Mandatory=$False)][String]$subscriptionId,
    [Parameter(Mandatory=$false)][string]$sqlDBName="wc_database"
)

$sqlServerCred = Get-Credential -Message "Enter your SQL Admin credential to initialize schema"
./init_schema.ps1 -useSqlAuth $true -sqlServerCred $sqlServerCred -sqlServerName $sqlServerName -ResourceGroup $ResourceGroup -sqlDBName $sqlDBName -subscriptionId $subscriptionId