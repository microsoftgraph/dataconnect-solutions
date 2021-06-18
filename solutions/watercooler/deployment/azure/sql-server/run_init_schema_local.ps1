#! /usr/bin/pwsh
param (
    [Parameter(Mandatory=$true)][string]$sqlServerName,
    [Parameter(Mandatory=$true)][string]$ResourceGroup,
    [Parameter(Mandatory=$False)][String]$subscriptionId,
    [Parameter(Mandatory=$false)][string]$sqlDBName="wc_database",
    [Parameter(Mandatory=$false)][string]$sqlAdminLogin="",
    [Parameter(Mandatory=$false)][string]$sqlAdminPasword=""
)
if ($sqlAdminPasword -and $sqlAdminLogin) {
    Write-Output "SQL admin credentials was provided via commandline args "
    $strPass = ConvertTo-SecureString -String $sqlAdminPasword -AsPlainText -Force
    $sqlServerCred = New-Object -TypeName System.Management.Automation.PSCredential -ArgumentList ($sqlAdminLogin, $strPass)
}
else {
    $sqlServerCred = Get-Credential -Message "Enter your SQL Admin credential to initialize schema"
}

./init_schema.ps1 -useSqlAuth $true -sqlServerCred $sqlServerCred -sqlServerName $sqlServerName -ResourceGroup $ResourceGroup -sqlDBName $sqlDBName -subscriptionId $subscriptionId