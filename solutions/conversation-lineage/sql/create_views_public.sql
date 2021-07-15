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
    - augmented_flattened_emails
    - augmented_flattened_team_chats
    - augmented_flattened_events

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

DROP VIEW IF EXISTS [dbo].[vUsers];
GO

-- drop sentiment analysis views
DROP VIEW IF EXISTS [dbo].[vConversationEntities];
GO

DROP VIEW IF EXISTS [dbo].[vConversationSentiment];
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= INTERACTIONS - ONE ON ONE ========================================================================================================================


CREATE VIEW [dbo].[vInteractions_one_to_one]
AS
    SELECT
        [InternetMessageId] as InteractionId, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment], SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        [Recipient], [RecipientName], RecipientManagerEmail, RecipientReportsTo as RecipientManagerName,
        [IsExternalEmail] as IsExternalInteraction, [MailToManager] as InteractionWithManager, [MailToSubordinate] as InteractionWithSubordinate,
        [Content],
        'Email' as SourceType
    FROM dbo.augmented_flattened_emails

    UNION ALL

    SELECT
        [InternetMessageId] as InteractionId, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment], SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        [Recipient], [RecipientName], RecipientManagerEmail, RecipientReportsTo as RecipientManagerName,
        [IsExternalChat]  as IsExternalInteraction, [ChatWithManager] as InteractionWithManager, [ChatWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'TeamsChat' as SourceType
    FROM dbo.augmented_flattened_team_chats

    UNION ALL

    SELECT
        [iCalUId] as InteractionId, [Subject], CreatedDateTime, [IsRead], [Organizer] as [Sender], [OrganizerName] as [SenderName], [OrganizerDepartment] as [SenderDepartment],
        OrganizerCountry as SenderCountry, OrganizerManagerEmail as SenderManagerEmail, OrganizerReportsTo as SenderManagerName,
        [AttendeeAddress] as [Recipient], [AttendeeName] as [RecipientName], AttendeeManagerEmail as RecipientManagerEmail, AttendeeReportsTo as RecipientManagerName,
        [IsExternalEvent]  as IsExternalInteraction, [EventWithManager] as InteractionWithManager, [EventWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'CalendarEvent' as SourceType
    FROM dbo.augmented_flattened_events;
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(InteractionId) FROM [dbo].[vInteractions_one_to_one];
PRINT 'vInteractions_one_to_one: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= INTERACTIONS - ONE TO MANY ========================================================================================================================

CREATE VIEW [dbo].[vInteractions_one_to_many]
AS
    SELECT
        [InternetMessageId] as InteractionId, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment],
        SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        CONCAT_WS(', ', [ToAddresses], [CCAddresses], [BCCAddresses]) as Recipients, CONCAT_WS(', ', [ToNames], [CCNames], [BCCNames]) as RecipientNames,
 [IsExternalEmail] as IsExternalInteraction, [MailToManager] as InteractionWithManager,
        [MailToSubordinate] as InteractionWithSubordinate,
        [Content],
        'Email' as SourceType
    FROM dbo.augmented_emails

    UNION ALL

    SELECT
        [InternetMessageId] as InteractionId, [Subject], CreatedDateTime, [IsRead], [Sender], [Sender_Name] as SenderName, [SenderDepartment],
        SenderCountry, SenderManagerEmail, SenderReportsTo as SenderManagerName,
        [ToAddresses] as Recipients, [ToNames] as RecipientNames, [IsExternalChat]  as IsExternalInteraction, [ChatWithManager] as InteractionWithManager,
        [ChatWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'TeamsChat' as SourceType
    FROM dbo.augmented_team_chats

    UNION ALL

    SELECT
        [iCalUId] as InteractionId, [Subject], CreatedDateTime, [IsRead], [Organizer] as [Sender], [OrganizerName] as [SenderName], [OrganizerDepartment] as [SenderDepartment],
        OrganizerCountry as SenderCountry, OrganizerManagerEmail as SenderManagerEmail, OrganizerReportsTo as SenderManagerName,
        [AttendeeAddresses] as Recipients, [AttendeeNames] as RecipientNames, [IsExternalEvent]  as IsExternalInteraction, [EventWithManager] as InteractionWithManager,
        [EventWithSubordinate] as InteractionWithSubordinate,
        [Content],
        'CalendarEvent' as SourceType
    FROM dbo.augmented_events;
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(InteractionId) FROM [dbo].[vInteractions_one_to_many];
PRINT 'vInteractions_one_to_many: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= EMAILS ============================================================================================================================================
-- Receiver_Names missing, assuming ToNames is the new proper column (when in fact CC and BCC should also be included in Receiver_Names)

CREATE VIEW [dbo].[vEmails]
AS
    SELECT
        [InternetMessageId]
        ,[From], [From_Name]
        ,[CreatedDateTime], [LastModifiedDateTime]
        ,[Subject], [Content]
        ,[IsReadReceiptRequested], [IsRead], [IsDraft]
        -- sender user data ; essential data is duplicated in these tables for speed
        ,[SenderId]
        ,[SenderCity], [SenderCountry], [SenderDepartment], [SenderJobTitle]
        -- recipients
        ,CONCAT_WS(', ', [ToAddresses], [CCAddresses], [BCCAddresses]) as Recipients
        ,CONCAT_WS(', ', [ToNames], [CCNames], [BCCNames]) as RecipientNames
        -- augmented fields
        ,[MailToManager], [MailToSubordinate], [SenderReportsTo], [SenderManagerEmail]
        ,[IsExternalEmail]
    FROM dbo.augmented_emails;
GO

CREATE VIEW [dbo].[vEmails_Flattened]
AS
    SELECT
        [InternetMessageId]
        ,[From], [From_Name]
        ,[CreatedDateTime], [LastModifiedDateTime]
        ,[Subject], [Content]
        ,[IsReadReceiptRequested], [IsRead], [IsDraft]
        -- sender user data ; essential data is duplicated in these tables for speed
        ,[SenderId]
        ,[SenderCity], [SenderCountry], [SenderDepartment], [SenderJobTitle]
        -- recipient
        ,[Recipient], [RecipientName], [RecipientType], [RecipientReportsTo], [RecipientManagerEmail]
        -- augmented fields
        ,[MailToManager], [MailToSubordinate], [SenderReportsTo], [SenderManagerEmail]
        ,[IsExternalEmail]
    FROM dbo.augmented_flattened_emails;
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(InternetMessageId) FROM [dbo].[vEmails];
PRINT 'vEmails: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(InternetMessageId) FROM [dbo].[vEmails_Flattened];
PRINT 'vEmails_Flattened: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= EVENTS =======================================================================================================================================
-- approximation of column usage

CREATE VIEW [dbo].[vEvents]
AS
    SELECT
        [iCalUId]
        ,[Organizer], [OrganizerName], [OrganizerDisplayName]
        ,[CreatedDateTime], [LastModifiedDateTime]
        ,[Subject], [Content]
        ,[Importance], [Sensitivity]
        ,[IsAllDay], [IsCancelled], [IsOrganizer], [IsRead]
        ,[ResponseRequested]
        --,[ContentType]
        ,[StartDateTime], [StartTimeZone] ,[EndDateTime], [EndTimeZone]
        ,[LocationDisplayName]
        -- organizer user data ; essential data is duplicated in these tables for speed
        ,[OrganizerId]
        ,[OrganizerCity], [OrganizerCountry], [OrganizerDepartment], [OrganizerJobTitle]
        --,[OrganizerCompanyName]
        ,[OrganizerOfficeLocation], [OrganizerPostalCode], [OrganizerPreferredLanguage], [OrganizerState], [OrganizerStreetAddress], [OrganizerUsageLocation]
        -- recipients
        ,[AttendeeNames], [AttendeeAddresses]
        -- augmented fields
        ,[EventWithManager], [EventWithSubordinate], [OrganizerReportsTo], [OrganizerManagerEmail]
        ,[IsExternalEvent]
    FROM dbo.augmented_events;
GO

CREATE VIEW [dbo].[vEvents_Flattened]
AS
    SELECT
        [iCalUId]
        ,[Organizer], [OrganizerName], [OrganizerDisplayName]
        ,[CreatedDateTime], [LastModifiedDateTime]
        ,[Subject], [Content]
        ,[Importance], [Sensitivity]
        ,[IsAllDay], [IsCancelled], [IsOrganizer], [IsRead], [ResponseRequested]
        --,[ContentType]
        ,[StartDateTime], [StartTimeZone], [EndDateTime], [EndTimeZone]
        ,[LocationDisplayName]
        -- organizer user data ; essential data is duplicated in these tables for speed
        ,[OrganizerId]
        ,[OrganizerCity], [OrganizerCountry], [OrganizerDepartment], [OrganizerJobTitle]
        --,[OrganizerCompanyName]
        ,[OrganizerOfficeLocation], [OrganizerPostalCode], [OrganizerPreferredLanguage], [OrganizerState], [OrganizerStreetAddress], [OrganizerUsageLocation]
        -- recipients
        ,[AttendeeName], [AttendeeAddress]
        --,[AttendeeStatusResponse]
        ,[AttendeeReportsTo], [AttendeeManagerEmail]
        -- augmented fields
        ,[EventWithManager], [EventWithSubordinate], [OrganizerReportsTo], [OrganizerManagerEmail]
        ,[IsExternalEvent]
    FROM dbo.augmented_flattened_events;
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(iCalUId) FROM [dbo].[vEvents];
PRINT 'vEvents: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(iCalUId) FROM [dbo].[vEvents_Flattened];
PRINT 'vEvents_Flattened: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= TEAM CHATS =======================================================================================================================================

CREATE VIEW [dbo].[vTeamChats]
AS
    SELECT
        [InternetMessageId]
        ,[From], [From_Name]
        ,[CreatedDateTime], [LastModifiedDateTime]
        ,[Subject], [BodyPreview], [Content]
        ,[Importance], [IsRead], [IsDraft]
        -- sender user data ; essential data is duplicated in these tables for speed
        ,[SenderId]
        ,[SenderCity], [SenderCountry], [SenderDepartment], [SenderJobTitle]
        ,[SenderPostalCode], [SenderPreferredLanguage], [SenderState], [SenderStreetAddress], [SenderUsageLocation]
        -- recipients
        ,[ToNames], [ToAddresses], [Recipient], [RecipientName]
        -- augmented fields
        ,[ChatWithManager], [ChatWithSubordinate], [SenderReportsTo], [SenderManagerEmail]
        ,[IsExternalChat]
    FROM dbo.augmented_team_chats;
GO

CREATE VIEW [dbo].[vTeamChats_Flattened]
AS
    SELECT
        [InternetMessageId]
        ,[From], [From_Name]
        ,[CreatedDateTime], [LastModifiedDateTime]
        ,[Subject], [BodyPreview], [Content]
        ,[Importance], [IsRead], [IsDraft]
        -- sender user data ; essential data is duplicated in these tables for speed
        ,[SenderId]
        ,[SenderCity], [SenderCompanyName], [SenderCountry], [SenderDepartment], [SenderJobTitle]
        ,[SenderPostalCode], [SenderPreferredLanguage], [SenderState], [SenderStreetAddress], [SenderUsageLocation]
        -- recipient
        ,[Recipient], [RecipientName]
        -- augmented fields
        ,[ChatWithManager], [ChatWithSubordinate], [SenderReportsTo], [SenderManagerEmail]
        ,[RecipientManagerEmail], [RecipientReportsTo]
        ,[IsExternalChat]
    FROM dbo.augmented_flattened_team_chats;
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(InternetMessageId) FROM [dbo].[vTeamChats];
PRINT 'vTeamChats: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(InternetMessageId) FROM [dbo].[vTeamChats_Flattened];
PRINT 'vTeamChats_Flattened: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= SENTIMENT ANALYSIS =======================================================================================================================================

CREATE VIEW [dbo].[vConversationEntities]
AS
    SELECT
        [interaction_id], [source_type]
        ,[sender_mail], [sender_name], [sender_domain]
        ,[text], [category], [score]
    FROM dbo.conversation_entities_info;
GO

CREATE VIEW [dbo].[vConversationSentiment]
AS
    SELECT
        [interaction_id], [source_type]
        ,[sender_mail], [sender_name], [sender_domain]
        ,[recipient_name], [recipient_address], [recipient_domain]
        ,[general_sentiment]
        ,[pos_score], [neutral_score], [negative_score]
    FROM dbo.conversation_to_recipient_sentiment_info;
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(interaction_id) FROM [dbo].[vConversationEntities];
PRINT 'vConversationEntities: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(interaction_id) FROM [dbo].[vConversationSentiment];
PRINT 'vConversationSentiment: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- ========================= USERS =======================================================================================================================================

CREATE VIEW [dbo].[vUsers]
AS
    SELECT
        id, displayName, givenName, mail
        ,reportsTo, managerEmail
        ,aboutMe, hireDate
        ,city, country
        ,department, jobTitle
        ,officeLocation, postalCode, [state], streetAddress, usageLocation, preferredLanguage
    FROM dbo.users;
GO

DECLARE @CountRecords int;
SELECT @CountRecords=COUNT(id) FROM [dbo].[vUsers];
PRINT 'vUsers: ' + CAST(@CountRecords AS VARCHAR) + ' records';
GO

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------