/*
==============================
    CREATING INTERNAL VIEWS
==============================

This file contains the definitions of internal/private views.
These should only be used internally by the Synapse pipelines, unlike the public views which are meant to be exposed and used by the outside world (e.g. by PowerBI).
The internal views are owned by the same team that owns the Synapse pipelines.
All internal view names should be prefixed with "vInternal_"

*/

CREATE VIEW [dbo].[vInternal_Interactions_one_to_many]
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

-- SELECT * from vInternal_Interactions_one_to_many;

-- drop view  [dbo].[vInternal_Interactions_one_to_many];

