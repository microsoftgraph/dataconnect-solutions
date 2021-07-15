/*
=========================
    CREATING VIEWS
=========================

Usage of views - ideally, views are the only ones imported to PowerBI,
- to add another level of decoupling to the work (changes can occur in the database, but as long as the views aren't impacted, reports won't be impacted),
- to document which columns or tables **are** or **aren't** used.

Views, in order of usage:
- one_on_one_interactions
        Tracks all 1-on-1 interactions (eg: for an email, records separately for each sender-receiver pair).

    based on:
    - augmented_flattened_emails_with_managers
    - augmented_flattened_team_chats_with_managers
    - augmented_flattened_events_with_managers

- one_to_many_interactions
        Tracks all 1-to-many interactions (eg: for an email, records 1 time per mail, for one sender and multiple receivers).

    based on:
    - augmented_emails
    - augmented_team_chats
    - augmented_events

- for separate analysis page (eg: mails being read):
    - aug_flat_emails
    - aug_emails
    - idem pentru teams, events

*/

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- VIEWS DROPS

-- drop generalized interaction views
DROP VIEW IF EXISTS [dbo].[vInteractions_one_to_one];
GO

DROP VIEW IF EXISTS [dbo].[vInteractions_one_to_many];
GO

-- drop source-specific views
DROP VIEW IF EXISTS [dbo].[vEmails];
GO

DROP VIEW IF EXISTS [dbo].[vEmails_Flattened];
GO

DROP VIEW IF EXISTS [dbo].[vEvents];
GO

DROP VIEW IF EXISTS [dbo].[vEvents_Flattened];
GO

DROP VIEW IF EXISTS [dbo].[vTeamChats];
GO

DROP VIEW IF EXISTS [dbo].[vTeamChats_Flattened];
GO

-- drop sentiment analysis views
DROP VIEW IF EXISTS [dbo].[vEmailsSentiment];
GO

DROP VIEW IF EXISTS [dbo].[vEmailsEntities];
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= INTERACTIONS - ONE ON ONE ========================================================================================================================

CREATE VIEW [dbo].[vInteractions_one_to_one]
AS
    SELECT
        [InternetMessageId] as Id, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment], SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        [Recipient], [RecipientName], RecipientManagerEmail, RecipientReportsTo as RecipientManagerName,
        [IsExternalEmail] as IsExternalInteraction, [MailToManager] as InteractionWithManager, [MailToSubordinate] as InteractionWithSubordinate,
        [Content],
        'Email' as SourceType
    FROM dbo.augmented_flattened_emails_with_managers

    UNION ALL

    SELECT
        [InternetMessageId] as Id, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment], SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        [Recipient], [RecipientName], RecipientManagerEmail, RecipientReportsTo as RecipientManagerName,
        [IsExternalChat]  as IsExternalInteraction, [ChatWithManager] as InteractionWithManager, [ChatWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'TeamsChat' as SourceType
    FROM dbo.augmented_flattened_team_chats_with_managers

    UNION ALL

    SELECT
        Id, [Subject], CreatedDateTime, [IsRead], [Organizer] as [Sender], [OrganizerName] as [SenderName], [OrganizerDepartment] as [SenderDepartment],
        OrganizerCountry as SenderCountry, OrganizerManagerEmail as SenderManagerEmail, OrganizerReportsTo as SenderManagerName,
        [AttendeeAddress] as [Recipient], [AttendeeName] as [RecipientName], AttendeeManagerEmail as RecipientManagerEmail, AttendeeReportsTo as RecipientManagerName,
        [IsExternalEvent]  as IsExternalInteraction, [EventWithManager] as InteractionWithManager, [EventWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'CalendarEvent' as SourceType
    FROM dbo.augmented_flattened_events_with_managers;
GO

SELECT * FROM [dbo].[vInteractions_one_to_one];
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= INTERACTIONS - ONE TO MANY ========================================================================================================================

CREATE VIEW [dbo].[vInteractions_one_to_many]
AS
    SELECT
        [InternetMessageId] as Id, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment],
        SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        CONCAT_WS(', ', [ToAddresses], [CCAddresses], [BCCAddresses]) as Recipients, CONCAT_WS(', ', [ToNames], [CCNames], [BCCNames]) as RecipientNames,
 [IsExternalEmail] as IsExternalInteraction, [MailToManager] as InteractionWithManager,
        [MailToSubordinate] as InteractionWithSubordinate,
        [Content],
        'Email' as SourceType
    FROM dbo.augmented_emails

    UNION ALL

    SELECT
        [InternetMessageId] as Id, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment],
        SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        [ToAddresses] as Recipients, [ToNames] as RecipientNames, [IsExternalChat]  as IsExternalInteraction, [ChatWithManager] as InteractionWithManager,
        [ChatWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'TeamsChat' as SourceType
    FROM dbo.augmented_team_chats

    UNION ALL

    SELECT
        Id, [Subject], CreatedDateTime, [IsRead], [Organizer] as [Sender], [OrganizerName] as [SenderName], [OrganizerDepartment] as [SenderDepartment],
        OrganizerCountry as SenderCountry, OrganizerManagerEmail as SenderManagerEmail, OrganizerReportsTo as SenderManagerName,
        [AttendeeAddresses] as Recipients, [AttendeeNames] as RecipientNames, [IsExternalEvent]  as IsExternalInteraction, [EventWithManager] as InteractionWithManager,
        [EventWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'CalendarEvent' as SourceType
    FROM dbo.augmented_events;
GO

SELECT * FROM [dbo].[vInteractions_one_to_many];
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= EMAILS ============================================================================================================================================
-- Receiver_Names missing, assuming ToNames is the new proper column (when in fact CC and BCC should also be included in Receiver_Names)

CREATE VIEW [dbo].[vEmails]
AS
    SELECT InternetMessageId, CreatedDateTime, From_Name, IsExternalEmail, IsRead, MailToManager, MailToSubordinate, SenderDepartment, SenderDisplayName, SenderReportsTo, Sender_Name, Subject, CONCAT_WS(', ', [ToAddresses], [CCAddresses], [BCCAddresses]) as Recipients, CONCAT_WS(', ', [ToNames], [CCNames], [BCCNames]) as RecipientNames
    FROM dbo.augmented_emails;
GO

CREATE VIEW [dbo].[vEmails_Flattened]
AS
    SELECT InternetMessageId, IsExternalEmail, Recipient, IsRead, MailToManager, MailToSubordinate, RecipientName, SenderCountry, SenderDepartment, Sender_Name, Subject
    FROM augmented_flattened_emails_with_managers;
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= EVENTS =======================================================================================================================================
-- approximation of column usage

CREATE VIEW [dbo].[vEvents]
AS
    SELECT Id, CreatedDateTime, Subject, Importance, Sensitivity, IsAllDay, IsCancelled, IsOrganizer, IsRead, ResponseRequested, Content, StartDateTime, StartTimeZone, EndDateTime, EndTimeZone, LocationDisplayName, Organizer, OrganizerName, OrganizerDisplayName, OrganizerId, OrganizerMail, OrganizerOfficeLocation, IsExternalEvent, EventWithManager, EventWithSubordinate,
    OrganizerReportsTo, OrganizerManagerEmail, AttendeeNames, AttendeeAddresses
    FROM dbo.augmented_events;
GO

CREATE VIEW [dbo].[vEvents_Flattened]
AS
    SELECT Id, CreatedDateTime, Subject, Importance, Sensitivity, IsAllDay, IsCancelled, IsOrganizer, IsRead, ResponseRequested, Content, StartDateTime, StartTimeZone, EndDateTime, EndTimeZone, LocationDisplayName, Organizer, OrganizerName, OrganizerDisplayName, OrganizerId, OrganizerMail, OrganizerOfficeLocation, IsExternalEvent, EventWithManager, EventWithSubordinate,
    OrganizerReportsTo, OrganizerManagerEmail, AttendeeName, AttendeeAddress
    FROM dbo.augmented_flattened_events_with_managers;
GO

-- ,[AttendeeStatusResponse]

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= TEAM CHATS =======================================================================================================================================
-- approximation of column usage
-- Recipient and RecipientName found in both flattened and unflattened data ... why ???

CREATE VIEW [dbo].[vTeamChats]
AS
    SELECT Id, CreatedDateTime, LastModifiedDateTime, Subject, BodyPreview, Importance, ConversationId, IsRead, Sender, Sender_Name, [From], From_Name, Content, Recipient, RecipientName,
    SenderId, SenderMail, SenderDisplayName, IsExternalChat, ChatWithManager, ChatWithSubordinate, SenderReportsTo, SenderManagerEmail, ToNames, ToAddresses
    FROM dbo.augmented_team_chats;
GO

CREATE VIEW [dbo].[vTeamChats_Flattened]
AS
    SELECT Id, CreatedDateTime, LastModifiedDateTime, Subject, BodyPreview, Importance, ConversationId, IsRead, Sender, Sender_Name, [From], From_Name, Content, Recipient, RecipientName,
    SenderId, SenderMail, SenderDisplayName, IsExternalChat, ChatWithManager, ChatWithSubordinate, SenderReportsTo, SenderManagerEmail
    FROM dbo.augmented_flattened_team_chats_with_managers;
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= SENTIMENT ANALYSIS =======================================================================================================================================

CREATE VIEW [dbo].[vEmailsSentiment]
AS
    SELECT id, conversation_id, general_sentiment, negative_score, neutral_score, pos_score, sender_name
    FROM dbo.conversation_sentiment_info;
GO

CREATE VIEW [dbo].[vEmailsEntities]
AS
    SELECT id, conversation_id, text, category, score, sender_name
    FROM dbo.conversation_entities_info;
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------