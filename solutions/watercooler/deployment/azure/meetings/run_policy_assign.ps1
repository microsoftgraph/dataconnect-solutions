param (
    [Parameter(Mandatory=$true)][string]$userId,
    [Parameter(Mandatory=$true)][string]$spId
)

Install-Module MicrosoftTeams -AllowPrerelease -RequiredVersion "2.3.1" -Force
Update-Module  MicrosoftTeams

Connect-MicrosoftTeams -UseDeviceAuthentication

$WCPolicy = Get-CsApplicationAccessPolicy -Identity Watercooler-Teams-Meetings-policy

if(!$WCPolicy) {
    Write-Output "Creating new Access Policy: Watercooler-Teams-Meetings-policy"
    New-CsApplicationAccessPolicy -Identity Watercooler-Teams-Meetings-policy -AppIds $spId -Description "Watercooler Event Creation"
} else {
    Write-Output "Watercooler-Teams-Meetings-policy already exists"
    Set-CsApplicationAccessPolicy -Identity Watercooler-Teams-Meetings-policy -AppIds @{Add=$spId}
}

Write-Output "Assigning Watercooler-Teams-Meetings-policy to application: $spId"
Grant-CsApplicationAccessPolicy -PolicyName Watercooler-Teams-Meetings-policy -Identity $userId
Write-Output "Watercooler-Teams-Meetings-policy was assigned to application: $spId"
