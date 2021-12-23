param(
    [Parameter(Mandatory = $true)]
    [System.String]
    $ApplicationId,

    [Parameter(Mandatory = $true)]
    [System.String]
    $TenantId,

    [Parameter(Mandatory = $true)]
    [System.String]
    $ApplicationSecret,

    [Parameter()]
    [System.Management.Automation.PSCredential]
    $Credential,

    [Parameter(Mandatory = $true)]
    [System.String]
    $DataSetPath,

    [Parameter()]
    [System.Uint32]
    $NumberOfItemsPerRun = 1000,

    [Parameter()]
    [ValidateSet("Contacts", "Emails", "Meetings", "TeamsChats")]
    [System.String[]]
    $SignalTypes
)

if ($SignalTypes.Contains("TeamsChats") -and -not $Credential)
{
    $Credential = Get-Credential
}

Write-Host "Initiating seeding engine..."
$Script:SeedUsers = @()

if ($ApplicationId)
{
    $Script:ApplicationId = $ApplicationId
}
else
{
    $Script:ApplicationId = Read-Host "Application Id"
}

if ($TenantId)
{
    $Script:TenantId = $TenantId
}
else
{
    $Script:TenantId = Read-Host "Tenant Id (e.g. M365x2.onmicrosoft.com)"
}

if ($ApplicationSecret)
{
    $Script:ApplicationSecret = $ApplicationSecret
}
else
{
    $Script:ApplicationSecret = Read-Host "Application Secret"
}

if ($DataSetPath)
{
    $Script:DataSetPath = $DataSetPath
}
else
{
    $Script:DataSetPath = Read-Host "Path to content"
}

# Meeting Variales
$attendeeType = @("required", "optional", "resource")
$attendeeStatus = @("none", "tentativelyAccepted", "accepted", "declined", "notResponded")
$recurrenceType = @("daily", "weekly", "monthly")
$dayOfWeek = @("sunday", "Monday", "tuesday" ,"wednesday" ,"thursday" ,"friday" ,"saturday")

$Script:AllItems = $null

function Get-SeedUsers
{
    # TODO - Support specifying only a specific group.
    [CmdletBinding()]
    [OutputType([System.Array])]
    param()

    $users = Get-MgUser -All | Where-Object -FilterScript {$_.UserPrincipalName -notlike "*#EXT#@*"}
    return $users
}

function Get-RandomContent
{
    [CmdletBinding()]
    [OutputType([System.Collections.Hashtable])]
    param(
        [parameter(Mandatory = $true)]
        [System.String]
        $Path
    )

    if (-not $Script:allItems)
    {
        $Script:allItems = Get-ChildItem $path -Recurse | where { !$_.PSisContainer }
    }
    $randomizer = [System.Random]::new()
    $randomFile = $allItems[$randomizer.Next(0, $allItems.Length - 1)]
    $content = Get-Content $randomFile.FullName -Raw

    $start = $content.IndexOf("Subject: ") + 9
    $end = $content.IndexOf("`n", $start)
    $subject = $content.Substring($start, $end-$start)

    $start = $content.IndexOf("X-FileName:")
    $start = $content.IndexOf("`n", $start) + 1

    $start = $content.IndexOf("X-FileName:")
    $start = $content.IndexOf("`n", $start) + 1
    $content = $content.Substring($start, $content.Length-$start)

    $result = @{
        Subject = $subject
        Content = $content
    }
    return $result
}

function Start-SeedingM365Meetings
{
    [CmdletBinding()]
    param()
    if ($Script:SeedUsers.Length -lt 1)
    {
        $Script:SeedUsers = Get-SeedUsers
    }
    #region Meetings
    $randomizer = [System.Random]::new()
    $timeZone = 'America/New_York'
    do
    {
        $randomUser = $Script:SeedUsers[$randomizer.Next(0, $Script:SeedUsers.Length - 1)]
        $calendar = Get-MgUserCalendar -UserId $randomUser.Id -ErrorAction SilentlyContinue
    } while (-not $calendar)

    $attendeesObject = @(
        @{
            emailAddress = @{
                address = $randomUser.UserPrincipalName
                name    = $randomUser.DisplayName
            }
            type        = 'required'
            status      = 'organizer'
        }
    )

    $randomNumberAttendees = $randomizer.Next(0,3)
    for ($j = 0; $j -lt $randomNumberAttendees; $j++)
    {
        $randomNumber = $randomizer.Next(0, $Script:SeedUsers.Length - 1)
        $randomAttendeeObject = $Script:SeedUsers[$randomNumber]
        $randomAttendeeType    = $attendeeType[$randomizer.Next(0, $attendeeType.Length -1)]
        $randomAttendeeStatus  = $attendeeStatus[$randomizer.Next(0, $attendeeStatus.Length -1)]

        $attendeesObject += @{
            emailAddress = @{
                address = $Script:SeedUsers[$randomNumber].UserPrincipalName
                name    = $Script:SeedUsers[$randomNumber].DisplayName
            }
            type     = $randomAttendeeType
            status   = $randomAttendeeStatus
        }
        $randomDaysDifference  = $randomizer.Next(-180,180) # Number of days in which meeting will occur (past or future)
        $randomHourOfDay       = $randomizer.Next(7,19) # Meeting hour, keeping it between 7AM and 7PM
        $randomLength          = $randomizer.Next(15,120) # Meeting's length between 15 minutes and 2 hours;

        $today = [System.DateTime]::Today
        $startTime = $today.AddDays($randomDaysDifference)
        $startTime = $startTime.AddHours($randomHourOfDay)
        $endTime = $startTime.AddMinutes($randomLength)

        $startObject = @{
            dateTime = $startTime.ToString("yyyy-MM-ddThh:mm:ss")
            timeZone = $timeZone
        }

        $endObject = @{
            dateTime = $endTime.ToString("yyyy-MM-ddThh:mm:ss")
            timeZone = $timeZone
        }

        $randomMeetingType        = $randomizer.Next(0,1) # 1 = Event, 2 = Online Meeting
        [Boolean]$IsOnlineMeeting = ([System.Convert]::ToBoolean($randomMeetingType))

        $randomRecurrenceOdds = $randomizer.Next(1,100) # Only if > 95 does it become a recurring meeting (1 chance out of 20)

        $recurrenceObject = $null
        if ($randomRecurrenceOdds -gt 95)
        {
            $randomNumber = $randomizer.Next(0, $recurrenceType.Length -1)

            $recurrenceObject = @{
                pattern = @{
                    type     = $recurrenceType[$randomNumber]
                    interval = $randomizer.Next(1,3)
                }
                range = @{
                    type      = "endDate"
                    startDate = $startTime.ToString("yyyy-MM-dd")
                    endDate   = $startTime.AddMonths(6).ToString("yyyy-MM-dd")
                }
            }

            # If weekly, then we need to specify day of Week.
            if ($recurrenceType[$randomNumber] -eq 'Weekly')
            {
                $randomDayOfWeek = $dayOfWeek[$randomizer.Next(0, $dayOfWeek.Length - 1)]
                $recurrenceObject.pattern.Add("daysOfWeek", $randomDayOfWeek)
            }
        }
        try
        {
            $content = Get-RandomContent -Path $Script:DataSetPath
            New-MgUserCalendarEvent -CalendarId $calendar.Id `
                -UserId $randomUser.Id `
                -Body @{content = $content.Content; contentType = "text";} `
                -Attendees $attendeesObject `
                -Start $startObject `
                -End $endObject `
                -Subject $content.Subject `
                -IsOnlineMeeting:$IsOnlineMeeting `
                -Recurrence $recurrenceObject -ErrorAction SilentlyContinue | Out-Null
        }
        catch
        {
            Write-Host $_ -ForegroundColor Red
        }
    }
    #endregion
}

function Start-SeedingM365Emails
{
    [CmdletBinding()]
    param()

    if ($Script:SeedUsers.Length -lt 1)
    {
        $Script:SeedUsers = Get-SeedUsers
    }
    $randomizer = [System.Random]::new()

    #region FROM
    $from = $Script:SeedUsers[$randomizer.Next(0, $Script:SeedUsers.Length -1)]
    #endregion

    #region TO
    $to = @()

    # Get a random number of that represents the number of recipients on the To line of the email;
    # -2 is because to handle the case where email is being to all users in the renant, we don't want to From
    # user to send himself an email.
    $numberOfToRecipients = $randomizer.Next(1, $Script:SeedUsers.Length - 2)

    # Randomly loop through the list of users in the tenant and add the proper of unique number of To Recipients
    # based on the generated random number;
    for ($i = 1; $i -le $numberOfToRecipients; $i++)
    {
        # Select a random user, and loop until we find a user that is not already added to our To array;
        do
        {
            $currentToUser = $Script:SeedUsers[$randomizer.Next(0, $Script:SeedUsers.Length -1)]
        } while ($to -contains $currentToUser.UserPrincipalName -and `
                 $from -ne $currentToUser.UserPrincipalName)
        $to += $currentToUser.UserPrincipalName
    }
    #endregion

    #region CC
    $cc = @()

    # Get a random number of that represents the number of recipients on the CC line of the email.
    # The number of cc recipients canot be greater than the number of users remaining that were not
    # on the To line.

    # Increase the chances of having no CC which is the most typical case for emails;
    $chancesOfNoCCPercent = 80
    $randomNumber = $randomizer.Next(1,100)
    if ($randomNumber -lt $chancesOfNoCCPercent)
    {
        $numberOfToRecipients = 0
    }
    else
    {
        $numberOfCCRecipients = $randomizer.Next(1, ($Script:SeedUsers.Length - $numberOfToRecipients))
    }

    # Randomly loop through the list of users in the tenant and add the proper of unique number of To Recipients
    # based on the generated random number;
    for ($i = 1; $i -le $numberOfCCRecipients; $i++)
    {
        # Select a random user, and loop until we find a user that is not already added to our CC array;
        do
        {
            $currentCCUser = $Script:SeedUsers[$randomizer.Next(0, $Script:SeedUsers.Length -1)]
        } while ($to -contains $currentCCUser.UserPrincipalName -and `
                 $from -ne $currentCCUser.UserPrincipalName)
        $cc += $currentCCUser.UserPrincipalName
    }
    #endregion

    $content = Get-RandomContent -Path $Script:DataSetPath
    #region SUBJECT
    $subject = $content.Subject
    #endregion

    #region BODY
    $body = $content.Content
    #endregion

    #region Sending Message
    $toRecipients = @()
    $toMessage = ""
    foreach ($recipient in $to)
    {
        $toRecipients += @{
            emailAddress = @{
                address = $recipient
            }
        }
        $toMessage += "    --> $recipient`r`n"
    }
    $message = @{
        subject = $subject
        toRecipients = $toRecipients
        #ccRecipients = $cc
        body = @{
            contentType = 'html'
            content = $body
        }
    }
    #endregion

    try
    {
        Send-MgUserMail -UserId $from.Id `
            -Message $message `
            -ErrorAction Stop
    }
    catch
    {
        if ($_.Exception -like "*The mailbox is either inactive*")
        {
            Write-Host "    User {$($from.UserPrincipalName)} is not assigned a license to send emails"
        }
        elseif ($_.Exception -like "*Resource could not be discovered.*" -or
                $_.Exception -like "*At least one recipient is not valid*")
        {
            Write-Host "    One of the following users is invalid:" -NoNewline
            Write-Host ($to -join ',')
        }
        else
        {
            Write-Error $_
        }
    }
}

function Start-SeedingM365TeamsChats
{
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [System.Management.Automation.PSCredential]
        $Credential
    )

    if ($Script:SeedUsers.Length -lt 1)
    {
        $Script:SeedUsers = Get-SeedUsers
    }
    $randomizer = [System.Random]::new()
    $content = Get-RandomContent -Path $Script:DataSetPath

    #region FROM
    $from = $Script:SeedUsers[$randomizer.Next(0, $Script:SeedUsers.Length -1)]
    #endregion

    $ownerUserId = (Get-MgUser -UserId $Credential.UserName).Id
    $members = @(
        @{
            "@odata.type"     = "#microsoft.graph.aadUserConversationMember"
            roles             = @("owner")
            "user@odata.bind" = "https://graph.microsoft.com/v1.0/users('" + $ownerUserId + "')"
        }
    )

    # Get a random number of that represents the number of members in the chat between 1 and 5
    $numberOfMember = $randomizer.Next(1, 5)

    # Improve chances of getting a 1:1 chat which is the most frequent type of chat;
    $number = $randomizer.Next(1,100)
    if ($number -lt 80)
    {
        $numberOfMember = 1
    }

    $chatType = "oneOnOne"
    if ($numberofMember -gt 1)
    {
        $chatType = "group"
    }

    # Randomly loop through the list of users in the tenant and add the proper of unique number of To Recipients
    # based on the generated random number;
    $memberIds = @()
    for ($i = 1; $i -le $numberOfMember; $i++)
    {
        do
        {
            $currentUser = $Script:SeedUsers[$randomizer.Next(0, $Script:SeedUsers.Length -1)]
        } while ($currentUser.Id -eq $ownerUserId -or $memberIds.Contains($currentUser.Id))

        $memberIds += $currentUser.Id
        $member = @{
            "@odata.type"     = "#microsoft.graph.aadUserConversationMember"
            roles             = @("owner")
            "user@odata.bind" = "https://graph.microsoft.com/v1.0/users('" + $currentUser.Id + "')"
        }
        $members += $member
    }
    #endregion

    $params = @{
        ChatType = $chatType
        Members  = $members
    }

    if ($numberOfMember -gt 1)
    {
        $params.Add("topic", $content.Subject)
    }

    # Create Chat entity
    $chat = New-MgChat @params

    # Send chat message
    try
    {
        New-MgChatMessage -ChatId $chat.Id -Body @{content = $content.Content} -ErrorAction Stop | Out-Null
    }
    catch
    {
        Write-Verbose $_
    }
}

function Get-M365ContactsFromDataset
{
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [System.String]
        $DataSetRootPath
    )

    if ($Script:AllContacts)
    {
        return $Script:AllContacts
    }

    $Script:allContactsFolders = Get-ChildItem -Path $DataSetRootPath -Recurse -Directory -Force -ErrorAction SilentlyContinue | Where-Object -FilterScript {$_.Name -eq 'contacts'}

    $randomizer = [System.Random]::new()
    $Script:AllContacts = @()
    foreach ($folder in $Script:AllContactsFolders)
    {
        $filesInFolder = Get-ChildItem -Path $folder.FullName

        foreach ($file in $filesInFolder)
        {
            $content = Get-Content $file.FullName -Raw

            $start = $content.IndexOf("X-FileName:", 0)
            $start = $content.IndexOf("`r", $start) + 2

            $contactContent = $content.Substring($start, $content.Length - $start)
            $contactEntries = $contactContent.Split("`r")

            foreach ($contactEntry in $contactEntries)
            {
                $parts = $contactEntry.Replace("`n", "").Split(' ')
                $nameInfo = ""
                $phoneInfo = ""
                $emailInfo = ""
                foreach ($part in $parts)
                {
                    try
                    {
                        if ($part.Contains('@'))
                        {
                            $emailInfo = $part
                        }
                        else
                        {
                            $phoneParts = $part.Split('-')

                            if ($phoneParts.Length -gt 0)
                            {
                                $tryParseToNumber = [int]$phoneParts[0]

                                $i = 1
                                foreach ($phonePart in $phoneParts)
                                {
                                    try
                                    {
                                        $tryParseToNumber = [int]$phonePart
                                        $phoneInfo += $phonePart
                                        if ($i -ne $phoneParts.Length)
                                        {
                                            $phoneInfo += "-"
                                        }
                                    }
                                    catch
                                    {
                                        Write-Verbose -Message "$phonePart is not a valid phone number part. Discarding"
                                    }
                                    $i++
                                }
                            }
                            else
                            {
                                $nameInfo += $part
                            }
                        }
                    }
                    catch
                    {
                        $nameInfo += $part + " "
                    }
                }

                if (-not $phoneInfo -or $phoneInfo.Length -lt 10)
                {
                    $firstPart = $randomizer.Next(100,999).ToString()
                    $secondPart = $randomizer.Next(100,999).ToString()
                    $thirdPart = $randomizer.Next(1000, 9999).ToString()

                    $phoneInfo = $firstPart + "-" + $secondPart + "-" + $thirdPart
                }

                if (-not $emailInfo)
                {
                    $chars = "abcdefghijkmnopqrstuvwxyzABCEFGHJKLMNPQRSTUVWXYZ23456789_-".ToCharArray()
                    $randomEmail=""
                    1..15 | ForEach {  $randomEmail += $chars | Get-Random }
                    $emailInfo = $randomEmail + "@" + "contoso.com"
                }

                if (-not $nameInfo)
                {
                    $chars = "abcdefghijkmnopqrstuvwxyzABCEFGHJKLMNPQRSTUVWXYZ".ToCharArray()
                    1..15 | ForEach {  $nameInfo += $chars | Get-Random }
                    $nameInfo += " "
                    1..15 | ForEach {  $nameInfo += $chars | Get-Random }
                }

                $newContact = @{
                    Name = $nameInfo.Trim()
                    Phone = $phoneInfo
                    Email = $emailInfo
                }

                $Script:AllContacts += $newContact
            }
        }
    }
    return $Script:AllContacts
}
function Start-SeedingM365Contacts
{
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [System.String]
        $DataSetPath
    )

    if ($Script:SeedUsers.Length -lt 1)
    {
        $Script:SeedUsers = Get-SeedUsers
    }

    $Script:AllContacts = Get-M365ContactsFromDataset -DataSetRootPath $DataSetPath

    $randomizer = [System.Random]::new()

    $randomContact = $Script:AllContacts[$randomizer.Next(0, $Script:AllContacts.Length -1)]

    $phoneTypes = @("BusinessPhones", "HomePhones", "MobilePhone")
    $randomPhoneType = $phoneTypes[$randomizer.Next(0, $phoneTypes.Length -1)]

    $Emails = @(
        @{
            address = $randomContact.email
            name = $randomContact.Name
        }
    )
    $randomUserId = $Script:SeedUsers[$randomizer.Next(0, $Script:SeedUsers.Length - 1)].Id

    $creationParams = @{
        UserId = $randomUserId
        GivenName = $randomContact.Name
        EmailAddresses = $Emails
        DisplayName = $randomContact.Name
    }
    $creationParams.Add($randomPhoneType, $randomContact.Phone)
    try
    {
        New-MgUserContact @creationParams -ErrorAction Stop | Out-Null
    }
    catch
    {
        Write-Verbose -Message $_
    }
}

try
{
    Disconnect-MGGraph -ErrorAction Stop | Out-Null
}
catch
{
    Write-Verbose $_
}

if (-not $SignalTypes -or $SignalTypes.Contains("Emails"))
{
    $Global:MSCloudLoginConnectionProfile = $null
    Connect-M365Tenant -Workload MicrosoftGraph -ApplicationId $Script:ApplicationId `
        -TenantId $Script:TenantId `
        -ApplicationSecret $Script:ApplicationSecret
    for ($i = 1; $i -le $NumberOfItemsPerRun; $i++)
    {
        Start-SeedingM365Emails
        Write-Host "Email #$i"
    }
}
if (-not $SignalTypes -or $SignalTypes.Contains("Meetings"))
{
    $Global:MSCloudLoginConnectionProfile = $null
    Connect-M365Tenant -Workload MicrosoftGraph -ApplicationId $Script:ApplicationId `
        -TenantId $Script:TenantId `
        -ApplicationSecret $Script:ApplicationSecret
    for ($i = 1; $i -le $NumberOfItemsPerRun; $i++)
    {
        Start-SeedingM365Meetings
        Write-Host "Meeting #$i"
    }
}

if (-not $SignalTypes -or $SignalTypes.Contains("Contacts"))
{
    $Global:MSCloudLoginConnectionProfile = $null
    Connect-M365Tenant -Workload MicrosoftGraph -ApplicationId $Script:ApplicationId `
        -TenantId $Script:TenantId `
        -ApplicationSecret $Script:ApplicationSecret
    for ($i = 1; $i -le $NumberOfItemsPerRun; $i++)
    {
        Start-SeedingM365Contacts -DataSetPath $Script:DataSetPath
        Write-Host "Contact #$i"
    }
}

try
{
    Disconnect-MGGraph -ErrorAction Stop | Out-Null
}
catch
{
    Write-Verbose $_
}

if (-not $SignalTypes -or $SignalTypes.Contains("TeamsChats"))
{
    $Global:MSCloudLoginConnectionProfile = $null
    Connect-M365Tenant -Workload MicrosoftGraph -Credential $Credential
    for ($i = 1; $i -le $NumberOfItemsPerRun; $i++)
    {
        Start-SeedingM365TeamsChats -Credential $Credential
        Write-Host "Teams Chat #$i"
    }
}
