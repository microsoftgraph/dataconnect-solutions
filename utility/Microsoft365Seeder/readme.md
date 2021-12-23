## Context:
The MGDCSeeder script was developed by the Microsoft Graph Customer & Partner Experience team (CPx) and can be used to generate fictive Microsoft 365 signals (emails, meeting invites, team chats, etc.) across a Microsoft 365 demo environment. The script can be executed locally on a machine or can be executed in the cloud via any automation tool (e.g. Azure Automation Runbook). By specifying a path to a location that contains various files, the script is able to read the content of these files and generate the randomize the content of the signals to be created. 

Important to note that the script is not able to generate custom timestamps for the signals. This means that in order to generate signals over a period of, say one week, the script will need to be executed in loop over that period of time. For example, when sending an email, the generated email message will have the Sent Time stamp of when the script was executed.

## Prerequisites:
In order for the script to be able to execute properly in your demo environments there are a few prerequisites that need to be in place first. This section will provide instructions on how to setup the various required components to ensure proper artifacts, permissions and configurations are in place.

### Azure Active Directory Application
The script will use a Service Principal to authenticate to the Microsoft 365 demo tenant. To authenticate properly, you will need to create a new Azure AD Application in your environment. In your Azure portal, navigate to Azure Active Directory. In the left navigation, click on **App registrations**.
 ![image](https://user-images.githubusercontent.com/2547149/146807749-6f8aa3e6-2beb-43b9-b5f7-8cecdf0022b0.png)

From the App registration page, click on the **New registration** button in the menu bar:
 ![image](https://user-images.githubusercontent.com/2547149/146807769-7ce8650b-8255-48b4-8b64-5502898d7dce.png)

Provide any value in the **Name** text box and click on the **Register** button at the bottom:
 ![image](https://user-images.githubusercontent.com/2547149/146807781-bf5e1831-fb5f-4c0e-a84c-efee87cc30c1.png)

Once the application is created, take note of the **Application (client) ID** value from the main page:
 ![image](https://user-images.githubusercontent.com/2547149/146807787-5d93dc12-fbbd-4628-bdf4-41c938eeb2ad.png)

From the left navigation, click on **Certificates & secrets**
 ![image](https://user-images.githubusercontent.com/2547149/146807792-a2be4592-962f-4862-9e9b-ed3c12cb0280.png)

On the Certificates & secrets screen, click on the **New client secret** button in the menu.
 ![image](https://user-images.githubusercontent.com/2547149/146807799-b8e85743-6218-4388-a4f9-50dac7af3b69.png)

In the **Add a client secret** blade, keep the default settings and click on the **Add** button at the bottom:
 ![image](https://user-images.githubusercontent.com/2547149/146807820-531a8a0f-4ba2-4f25-bb2e-70e001e3afe7.png)

A new secret was generated. Make sure to take note of the secret's value, not the Secret ID.
 ![image](https://user-images.githubusercontent.com/2547149/146807833-90e69db3-b74f-479c-9992-01bd145ce33c.png)

From the left menu, navigate to **API permissions**:
 ![image](https://user-images.githubusercontent.com/2547149/146807841-e3f53b6e-f1f4-4000-abfa-c0622d595aa6.png)

On the API permissions page, click on the **Add a permission** button in the top menu:
 ![image](https://user-images.githubusercontent.com/2547149/146807846-30ee97f6-99e3-4cd0-9230-2ce5bc9a5d50.png)

On the Request API permissions blade, click on **Microsoft Graph**:
 ![image](https://user-images.githubusercontent.com/2547149/146807857-5e7d6fb7-3fb9-4a13-84ae-4c3e220426b5.png)

On the next screen, click on **Application permissions**:
 ![image](https://user-images.githubusercontent.com/2547149/146807863-c0598479-5127-4e08-8066-03112af14bac.png)

From the list of permissions, make sure you check the following:
•	Calendar.ReadWrite
•	Mail.Send
•	User.Read.All
Then click on the **Add permissions** button at the bottom.
 ![image](https://user-images.githubusercontent.com/2547149/146807877-d2c9deef-4283-4544-859d-12acada2c956.png)

Back on the API permissions screen, click on the **Grant admin consent for <Tenant>** button in the top menu.
 ![image](https://user-images.githubusercontent.com/2547149/146807887-781dc09c-dc53-482d-aa2f-900f388acfa4.png)

When prompted to confirm, click on the **Yes** button.
 ![image](https://user-images.githubusercontent.com/2547149/146807898-fcbc9315-cf75-41b4-a934-a67f05898d93.png)

### Defining Data Set
As mentioned in the Context section above, the script will need to read content of files to generate random content in emails, meeting invites and Teams chat messages. You can browse the internet to find any data set that would meet your needs or generate your own. One option is to leverage the following data set which is about 1.7Gb in size and contains emails that were collected from senior management at Enron during an investigation. For more information on the data set, please visit https://www.cs.cmu.edu/~./enron/ . The data set can be download as a .tar.gz file from here:
https://www.cs.cmu.edu/~./enron/enron_mail_20150507.tar.gz 

Once you’ve downloaded the data set, extract it to a local folder on the machine where the script will be executed from and take note of the root folder. The script will be able to navigate all the folder hierarchy of this folder and get to all the files no matter where they reside under the provided root path.

### Install the Microsoft Graph PowerShell SDK
In a new PowerShell console on the machine where you will be executing the script, run the following commands to install the required Microsoft Graph PowerShell SDK modules:
-	Install-Module Microsoft.Graph.Authentication
-	Install-Module Microsoft.Graph.Calendar
- Install-Module Microsoft.Graph.Teams
- Install-Module Microsoft.Graph.Users
-	Install-Module Microsoft.Graph.Users.Actions
-	Install-Module MSCloudLoginAssistant

### Grant Permissions to the Microsoft Graph PowerShell SDK
In order to generate the Teams chats signals, the script will need to authenticate as a given user and will leverage the Microsoft Graph PowerShell SDK Azure Active Directory app to authenticate. In order to grant proper permissions to the app, you will need to run the following command in a new PowerShell console:
Connect-MgGraph -Scopes ("Chat.Create", "ChatMessage.Send", "User.Read.All") 
When prompted, provide the credentials of one of your test accounts (e.g. Global Admin account) which is assigned a valid Teams license. The selected account will also need to have permission to grant an application consent of behalf of the organization. All teams chat generated will be sent from this account. Since Teams messages can only be sent in a delegated scope, we can’t generate chat messages from random accounts unfortunately. When prompted, provide the credentials of the selected user.
 
The first time you execute the script, you will be prompted to grant permission to the Microsoft Graph PowerShell SDK to create chats and send messages. Make sure you check the box **Consent of behalf of your organization** and then click on the **Accept** button.
 
Note that if you are using the **Credential** parameter when invoking the script that you will only need to select the account and grant proper permissions to the Microsoft Graph PowerShell SDK the first time you ever execute the script.

  Connect-MgGraph -Scopes ("Chat.Create", "ChatMessage.Send") | Out-Null 

## Executing the Script
The script accepts various parameters as input, the following table lists all the accepted parameters, whether they are mandatory or not and what value should be provided for each one.
  
| Parameter Name | Mandatory	| Description |
|---|---|---|
| ApplicationId |	True | Id of the Azure Active Directory application to use to authenticate to send emails and meeting invites. |
| TenantId | True |	Name of the tenant where we will be generating the signals onto (e.g. M365xXXX.onmicrosoft.com). Need to include the .onmicrosoft.com extension. |
| ApplicationSecret	| True	| Secret for the application used to authenticate to send emails and meeting invites. |
| Credential	| True	| Credential of a user to impersonate when sending Teams Chat Messages. |
| DatasetPath	| True	| Path to the root folder that contains files with the content to use to generate the content of the random signals. |
| NumberofItemsPerRun	| False	| Represents the number of signal to create for each type. The default value is 1,000. This means every time the script is executed it will generate 1,000 emails, 1,000 meeting invites and 1,000 Teams Chats. |
| SignalTypes	| False	| Array of the signal types you want to generate. If this property is not provided then all signals will be generated. Value can be one or more of the following:	Emails, Meetings or	TeamsChats |
 
To execute the script, open a new PowerShell console as an administrator, navigate to the folder where the script  (M365Seeder.ps1) is located and execute the following command, replacing the parameters by your own:
  
$AppId = "Your App ID"
$TenantId = "Your Tenant ID including .onmicrosoft.com"
$AppSecret = "Your App's secret"
$DataSetPath = "Path to the root folder (e.g. C:\Datasets)"

$cred = Get-Credential
& 'M365Seeder.ps1' -ApplicationId $AppId `
    -TenantId $tenantId `
    -ApplicationSecret $AppSecret `
    -Credential $cred `
    -DataSetPath $DataSetPath

## Troubleshooting
This section contains information about the most frequent issues that could be encountered when running the script. This section will be updated on a regular basis as the scripts evolves.

### Tenant is Not Authorized to Send Messaged from IP
**Issue:** When trying to send emails or meeting invites from a given tenant, messages are never sent. Instead when you navigate to the various users’ mailbox, you see several **Delivery has failed to the recipients or groups**: messages such as the following one:
 ![image](https://user-images.githubusercontent.com/2547149/146809055-929c22d9-b625-457c-9f39-30907429b4d2.png)

**Cause:** The reputation of the IP and tenant is not yet established. By default, Microsoft prevents demo tenants to send emails from unauthorized IP to prevent frauds and scams.
  
**Solution:** Open a support ticket in your tenant and request that the tenant be allowed to send emails. Also explain the motive behind you trying to seed random signals in your environment for demo purposes.
