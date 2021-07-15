-- ---------------------------------------------
-- 0. augmented_emails
-- ---------------------------------------------
SELECT TOP (10)
    [InternetMessageId]
    --,[Sender], [Sender_Name], --sender may be impersonated for mocking data
    ,[From], [From_Name]
    ,[CreatedDateTime], [LastModifiedDateTime]
    ,[Subject], [Content]
    ,[IsReadReceiptRequested], [IsRead], [IsDraft]
    --[ContentType]
    --,[WebLink] -- might be of use for interactivity
    -- sender user data ; essential data is duplicated in these tables for speed
    ,[SenderId]
    ,[SenderCity], [SenderCountry], [SenderDepartment], [SenderJobTitle]
    -- recipients
    ,[ToAddresses], [CCAddresses], [BCCAddresses]
    ,CONCAT_WS(', ', [ToAddresses], [CCAddresses], [BCCAddresses]) as Recipients
    ,[ToNames], [CCNames], [BCCNames]
    ,CONCAT_WS(', ', [ToNames], [CCNames], [BCCNames]) as RecipientNames
    -- augmented fields
    ,[MailToManager], [MailToSubordinate], [SenderReportsTo], [SenderManagerEmail]
    ,[IsExternalEmail]
 FROM [dbo].[augmented_emails]
-- WHERE IsDraft = 1 --Check if drafts are included
-- WHERE Sender <> [From] --Check if mails are impersonated as per demo requirements
-- WHERE SenderUserType <> 'Member' --Check if only user type is 'Member'
;
 GO

-- ---------------------------------------------
-- 1. augmented_flattened_emails_with_managers
-- ---------------------------------------------

SELECT TOP (10)
    [InternetMessageId]
    --,[Sender], [Sender_Name], --sender may be impersonated for mocking data
    ,[From], [From_Name]
    ,[CreatedDateTime], [LastModifiedDateTime]
    ,[Subject], [Content]
    ,[IsReadReceiptRequested], [IsRead], [IsDraft]
    --[ContentType]
    --,[WebLink] -- might be of use for interactivity
    -- sender user data ; essential data is duplicated in these tables for speed
    ,[SenderId]
    ,[SenderCity], [SenderCountry], [SenderDepartment], [SenderJobTitle]
    -- recipient
    ,[Recipient], [RecipientName], [RecipientType], [RecipientReportsTo], [RecipientManagerEmail]
    -- augmented fields
    ,[MailToManager], [MailToSubordinate], [SenderReportsTo], [SenderManagerEmail]
    ,[IsExternalEmail]
 FROM [dbo].[augmented_flattened_emails_with_managers];
 GO

-- ---------------------------------------------
-- 2. augmented_events
-- ---------------------------------------------

SELECT TOP (10)
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
 FROM [dbo].[augmented_events];
 GO

-- ---------------------------------------------
-- 3. augmented_flattened_events_with_managers
-- ---------------------------------------------

SELECT TOP (10)
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
 FROM [dbo].[augmented_flattened_events_with_managers];
 GO

-- ---------------------------------------------
-- 4. augmented_team_chats
-- ---------------------------------------------

SELECT TOP (10)
    [InternetMessageId]
    ,[From], [From_Name]
    ,[CreatedDateTime], [LastModifiedDateTime]
    ,[Subject], [BodyPreview], [Content]
    ,[Importance], [IsRead], [IsDraft]
    --,[ConversationId], [ConversationIndex]
    --,[ContentType]
    --,[FlagStatus]
    -- sender user data ; essential data is duplicated in these tables for speed
    ,[SenderId]
    ,[SenderCity], [SenderCountry], [SenderDepartment], [SenderJobTitle]
    -- ,[SenderCompanyName]
    ,[SenderPostalCode], [SenderPreferredLanguage], [SenderState], [SenderStreetAddress], [SenderUsageLocation]
    -- recipients -- anomaly
    ,[ToNames], [ToAddresses], [Recipient], [RecipientName]
    -- augmented fields
    ,[ChatWithManager], [ChatWithSubordinate], [SenderReportsTo], [SenderManagerEmail]
    ,[IsExternalChat]
 FROM [dbo].[augmented_team_chats];
 GO

-- ---------------------------------------------
-- 5. augmented_flattened_team_chats_with_managers
-- ---------------------------------------------

SELECT TOP (10)
    [InternetMessageId]
    ,[From], [From_Name]
    ,[CreatedDateTime], [LastModifiedDateTime]
    ,[Subject], [BodyPreview], [Content]
    ,[Importance], [IsRead], [IsDraft]
    --,[ConversationId]
    --,[ConversationIndex]
    --,[ContentType]
    --,[FlagStatus]
    -- sender user data ; essential data is duplicated in these tables for speed
    ,[SenderId]
    ,[SenderCity], [SenderCountry], [SenderDepartment], [SenderJobTitle]
    --[SenderCompanyName]
    ,[SenderPostalCode], [SenderPreferredLanguage], [SenderState], [SenderStreetAddress], [SenderUsageLocation]
    -- recipients
    ,[Recipient], [RecipientName]
    -- augmented fields
    ,[ChatWithManager], [ChatWithSubordinate], [SenderReportsTo], [SenderManagerEmail]
    ,[RecipientManagerEmail], [RecipientReportsTo]
    ,[IsExternalChat]
 FROM [dbo].[augmented_flattened_team_chats_with_managers];
 GO

-- ---------------------------------------------
-- 6. conversation_entities_info
-- ---------------------------------------------

SELECT TOP (10)
    [interaction_id], [source_type]
    ,[sender_mail], [sender_name], [sender_domain]
    ,[text], [category], [score]
 FROM [dbo].[conversation_entities_info];
 GO

-- ---------------------------------------------
-- 7. conversation_to_recipient_sentiment_info
-- ---------------------------------------------

SELECT TOP (10)
    [interaction_id], [source_type]
    ,[sender_mail], [sender_name], [sender_domain]
    ,[recipient_name], [recipient_address], [recipient_domain]
    ,[general_sentiment]
    ,[pos_score], [neutral_score], [negative_score]
 FROM [dbo].[conversation_to_recipient_sentiment_info];
 GO

-- ---------------------------------------------
-- 8. users
-- ---------------------------------------------

SELECT TOP (10)
    [id], [displayName], [givenName], [mail]
    ,[reportsTo], [managerEmail]
    ,[aboutMe], [hireDate]
    --,[mySite], [createdDateTime]
    ,[city], [country]
    ,[department], [jobTitle]
    ,[officeLocation], [postalCode], [state], [streetAddress], [usageLocation], [preferredLanguage]
 FROM [dbo].[users];
 GO
