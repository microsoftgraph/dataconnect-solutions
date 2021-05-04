# Pre requisites
# This script requires installing Office 365 modules for powershell
# Please follow the guide (Connect to Office 365 PowerShell) below
# https://technet.microsoft.com/en-us/library/dn975125.aspx

# This script creates users on a given tenant
# Sample cmd:
# .\Create-Users.ps1 -TenantName wkwtesttenant1 -DefaultUserPassword <default user password here> -UserInfoTableLocation .\wkw_users.csv
Param
(
    # The name of the tenant. The string before onmicrosoft.com in the domain name
    # For e.g. if the domain name is abc.onmicrosoft.com then tenant name is abc
    [Parameter(Mandatory=$true)]
    [string] $TenantName,

    # This is the password we set for all users
    # We currently support only one password for all users
    [Parameter(Mandatory=$true)]
    [string] $DefaultUserPassword,

    # This is the location of list of users in csv format
    # Has FirstName LastName and Location of users we want to create
    # Sample file: wkw_users.csv
    [Parameter(Mandatory=$true)]
    [string] $UserInfoTableLocation,

    # This is the license pack you want to assign to the user
    [Parameter(Mandatory=$false)]
    [string] $LicensePack = "ENTERPRISEPACK"
)

# Import the user list
$users = Import-Csv $UserInfoTableLocation

# Login as admin of the tenant with 2FA
Connect-MsolService

# Create those users
for ($i = 0; $i -lt $users.Length; $i++)
{
    $firstName = $users[$i].FirstName
    $lastName = $users[$i].LastName
    $location = $users[$i].Location
    $displayName = "$firstName $lastName"
    $upn = "$firstName.$lastName@$TenantName.onmicrosoft.com"
    $license = $TenantName + ":" + $LicensePack
    New-MsolUser -DisplayName $displayName -UserPrincipalName $upn -Password "$DefaultUserPassword" -LicenseAssignment $license -UsageLocation $location -ForceChangePassword $false -PasswordNeverExpires $true
}
