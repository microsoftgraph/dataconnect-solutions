if (-not (Get-Command Invoke-Sqlcmd -ErrorAction SilentlyContinue)) {
    Write-Warning "Unabled to find Invoke-SqlCmd cmdlet"
    Write-Output "Installling SqlServer module..."
    Install-Module -Name SqlServer -Confirm:$False -Force
}