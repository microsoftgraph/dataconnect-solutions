This section provides steps to populate your test organization with synthetic data. 

# Pre-Requisites
- Install Office 365 modules for powershell. Instructions at https://technet.microsoft.com/en-us/library/dn975125.aspx.
- Install Microsoft Exchange Web Services Managed API 2.2. Instructions at https://www.microsoft.com/en-us/download/details.aspx?id=42951.
- Keep note of the tenant name and the admin's userid and password.

# Step 1: Creating Users
- Start "Microsoft Azure Active Directory Module for Windows Powershell" as admin.
- Run following command from this directory:
`.\Create-Users.ps1 -TenantName "<tenant name here>" -DefaultUserPassword "<default user password here>" -UserInfoTableLocation .\wkw_users.csv`
- You will be required to login as the admin of the tenant.
- The script will then create the users listed in the `UserInfoTableLocation` csv file you provide.
- Password for all users will be same as the `TenantPassword`.

# Step 2: Generating Interactions
- Start "Microsoft Azure Active Directory Module for Windows Powershell" as admin.
- Run following command from this directory:
`.\Create-Interactions.ps1 -TenantName "<tenant name here>" -DefaultUserPassword "<default user password here>" -InteractionTableLocation .\wkw_interactions.csv`
- This script will then create interactions based on the `InteractionTableLocation` csv file you provide.
