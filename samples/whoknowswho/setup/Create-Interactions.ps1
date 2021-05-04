# Pre requisites
# This script requires Microsoft Exchange Web Services Managed API 2.2
# Please download and install from here
# https://www.microsoft.com/en-us/download/details.aspx?id=42951

# This script generates emails and meetings exchanges between users of a given tenant
# For every exchange/interaction it chooses sender and recipients based on the iteraction table you provide
# It also has the logic to send more emails during business hours compared to non-business hours
# Sample cmd:
# .\Create-Interactions.ps1 -TenantName wkwtesttenant1 -DefaultUserPassword <default user password here> -InteractionTableLocation .\wkw_interactions.csv
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

    # This is the location of interactions you want to replay
    # For details take a look at sample wkw_interactions.csv
    # We randomly select any interaction from the file using the frequency field as the weight
    [Parameter(Mandatory=$true)]
    [string] $InteractionTableLocation,

    # Time till when to run this script
    [Parameter(Mandatory=$false)]
    [datetime] $RunTillUtc = [DateTime]::MaxValue,

    # Sleep interval between two interactions in seconds
    [Parameter(Mandatory=$false)]
    [int] $SleepDuration = 30
)

Add-Type -Path "C:\Program Files\Microsoft\Exchange\Web Services\2.2\Microsoft.Exchange.WebServices.dll"

Add-Type @'
public class Interaction
{
    public string Sender;
    public string[] Recipients;
    public string subject;
    public string body;
}
'@

# we send email once every $EmailFrequency/100*$SleepDuration seconds during 9am-5pm,
# and every $EmailFrequency/1000*$SleepDuration seconds rest of the day
$EmailFrequency = 20
# we send meeting once every $MeetingFrequency/100*$SleepDuration seconds during 9am-5pm,
# and every $MeetingFrequency/1000*$SleepDuration seconds rest of the day
$MeetingFrequency = 2

function RoundToHour(
    [datetime] $time
)
{
    $toHour = $time.AddMinutes(-$time.Minute % 60)
    $toMinute = $toHour.AddSeconds(-$time.Second % 3600)
    return $toMinute
}

# Explode the interactions by inserting them "Frequency" times in an array
function ParseCsv(
    [string] $InteractionTableLocation
)
{
    $expandedInteractions = New-Object -TypeName System.Collections.ArrayList

    #read the all interactions with their frequency information
    $interactions = Import-Csv $InteractionTableLocation
    for ($i = 0; $i -lt $interactions.Length; $i++)
    {
        $sender = $interactions[$i].Sender + "@$TenantName.onmicrosoft.com"
        $recipientAliases = $interactions[$i].Recipient.Split(';')
        $recipients = New-Object -TypeName System.Collections.ArrayList
        for($k = 0; $k -lt $recipientAliases.Length; $k++)
        {
            [void]$recipients.Add($recipientAliases[$k] + "@$TenantName.onmicrosoft.com")
        }
        $interaction = New-Object Interaction
        $interaction.Sender = $sender
        $interaction.Recipients = $recipients
        $interaction.Subject = $interactions[$i].Subject
        $interaction.Body = $interactions[$i].Body

        # insert the interaction "Frequency" times into $expandedInteractions
        [int]$frequency = $interactions[$i].Frequency
        for($j = 0; $j -lt $frequency; $j++)
        {
            [void]$expandedInteractions.Add($interaction)
        }
    }

    $length = $expandedInteractions.Count
    Write-Host "Parsed $length interactions"
    return $expandedInteractions.ToArray()
}

function ChooseSendEmail()
{
    # slow down to 1/10th in non-business hours
    $factor = if((Get-Date).Hour -gt 9 -and (Get-Date).Hour -lt 17) {1} else {0.1};

    return ((Get-Random -Minimum 0 -Maximum 100) -le ($EmailFrequency * $factor));
}

function ChooseSendMeeting()
{
    # slow down to 1/10th in non-business hours
    $factor = if((Get-Date).Hour -gt 9 -and (Get-Date).Hour -lt 17) {1} else {0.1};

    return ((Get-Random -Minimum 0 -Maximum 100) -le ($MeetingFrequency * $factor));
}

function ChooseInteraction()
{
    return Get-Random -input $AllInteractions
}

function SendMail(
    [string] $subject,
    [string] $body,
    [string] $sender,
    [string[]] $recipients,
    [string] $password)
{
    $user = $sender
    $service = New-Object Microsoft.Exchange.WebServices.Data.ExchangeService -ArgumentList Exchange2010_SP1
    $service.Credentials = New-Object Microsoft.Exchange.WebServices.Data.WebCredentials -ArgumentList $user, $password
    $service.AutodiscoverUrl($user, {$true})

    $message = New-Object Microsoft.Exchange.WebServices.Data.EmailMessage -ArgumentList $service
    $message.Subject = $subject
    $messageBody = New-Object Microsoft.Exchange.WebServices.Data.MessageBody -ArgumentList "Text", $body
    $message.Body = $messageBody
    foreach ($recipient in $recipients)
    {
        $noOutput = $message.ToRecipients.Add($recipient)
    }
    $message.SendAndSaveCopy()
}

function SendMeeting(
    [string] $subject,
    [string] $body,
    [string] $sender,
    [string[]] $recipients,
    [string] $password)
{
    $user = $sender
    $service = New-Object Microsoft.Exchange.WebServices.Data.ExchangeService -ArgumentList Exchange2010_SP1
    $service.Credentials = New-Object Microsoft.Exchange.WebServices.Data.WebCredentials -ArgumentList $user, $password
    $service.AutodiscoverUrl($user, {$true})

    $meetingRequest = New-Object Microsoft.Exchange.WebServices.Data.Appointment -ArgumentList $service
    $meetingRequest.Subject = $subject
    $messageBody = New-Object Microsoft.Exchange.WebServices.Data.MessageBody -ArgumentList "Text", $body
    $meetingRequest.Body = $messageBody
    #meeting time set to 1 week from today, beginning at the current hour
    $startTime = RoundToHour (Get-Date).AddDays(7)
    $meetingRequest.Start = $startTime
    $meetingRequest.End = $startTime.AddHours(1)
    $meetingRequest.Location = "Conference room"
    $requiredAttendees = @()
    foreach ($attendee in $recipients)
    {
        $noOutput = $meetingRequest.RequiredAttendees.Add($attendee)
    }
    $meetingRequest.Save([Microsoft.Exchange.WebServices.Data.SendInvitationsMode]::SendToAllAndSaveCopy)
}

function SendInteractions([string] $password)
{
    #Should we send a message this minute?
    if (ChooseSendEmail)
    {
        $email = ChooseInteraction
        $subject = $email.subject
        $body = $email.body
        $sender = $email.Sender
        $recipients = $email.Recipients
        SendMail -subject $subject -body $body -sender $sender -recipients $recipients -password $password
        $date = Get-Date
        echo "$date SendMail sender=$sender recipients=$recipients"
    }

    #Should we send a meeting this minute?
    if (ChooseSendMeeting)
    {
        $meeting = ChooseInteraction
        $subject = $meeting.subject
        $body = $meeting.body
        $sender = $meeting.Sender
        $recipients = $meeting.Recipients
        SendMeeting -subject $subject -body $body -sender $sender -recipients $recipients -password $password
        $date = Get-Date
        echo "$date SendMeeting sender=$sender recipients=$recipients"
    }

    ##TODO read messages, respond to messages, accept/decline meeting invitations, etc.
}

$AllInteractions = ParseCsv $InteractionTableLocation

while ([DateTime]::UtcNow -lt $RunTillUtc)
{
    SendInteractions $DefaultUserPassword
    Start-Sleep -Seconds $SleepDuration
}