
---------------------------- Create augmented_emails ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[augmented_emails]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[IsReadReceiptRequested] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[WebLink] [varchar](1024)  NULL,
	[Sender] [varchar](1024)  NULL,
	[Sender_Name] [varchar](1024)  NULL,
	[From] [varchar](1024)  NULL,
	[From_Name] [varchar](1024)  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[emai_puser] [varchar](1024)  NULL,
	[email_ptenant] [varchar](1024)  NULL,
	[email_datarow] [bigint]  NULL,
	[email_userrow] [bigint]  NULL,
	[email_pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](1024)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](1024)  NULL,
	[SenderPreferredName] [varchar](1024)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](1024)  NULL,
	[SenderCompanyName] [varchar](1024)  NULL,
	[SenderCountry] [varchar](1024)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](1024)  NOT NULL DEFAULT ('N\A'),
	[SenderDisplayName] [varchar](1024)  NULL,
	[SenderGivenName] [varchar](1024)  NULL,
	[SenderId] [varchar](1024)  NULL,
	[SenderJobTitle] [varchar](1024)  NULL,
	[SenderMail] [varchar](1024)  NULL,
	[SenderOfficeLocation] [varchar](1024)  NULL,
	[SenderOnPremisesImmutableId] [varchar](1024)  NULL,
	[SenderPostalCode] [varchar](1024)  NULL,
	[SenderPreferredLanguage] [varchar](1024)  NULL,
	[SenderState] [varchar](1024)  NULL,
	[SenderStreetAddress] [varchar](1024)  NULL,
	[SenderSurname] [varchar](1024)  NULL,
	[SenderUsageLocation] [varchar](1024)  NULL,
	[SenderUserPrincipalName] [varchar](1024)  NULL,
	[SenderUserType] [varchar](1024)  NULL,
	[SenderPuser] [varchar](1024)  NULL,
	[SenderPtenant] [varchar](1024)  NULL,
	[SenderPAdditionalInfo] [varchar](1024)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[SenderReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](1024)  NULL,
	[ToAddresses] [varchar](max)  NULL,
	[CCAddresses] [varchar](max)  NULL,
	[BCCAddresses] [varchar](max)  NULL,
	[ToNames] [varchar](max)  NULL,
	[CCNames] [varchar](max)  NULL,
	[BCCNames] [varchar](max)  NULL,
	[IsExternalEmail] [bit]  NOT NULL DEFAULT ((0)),
	[MailToManager] [bit]  NOT NULL DEFAULT ((0)),
	[MailToSubordinate] [bit]  NOT NULL DEFAULT ((0))
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)




---------------------------- Create augmented_events ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[augmented_events]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[iCalUId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[Sensitivity] [varchar](max)  NULL,
	[IsAllDay] [bit]  NULL,
	[IsCancelled] [bit]  NULL,
	[IsOrganizer] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[ResponseRequested] [bit]  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[StartDateTime] [datetime2](6)  NULL,
	[StartTimeZone] [varchar](1024)  NULL,
	[EndDateTime] [datetime2](6)  NULL,
	[EndTimeZone] [varchar](1024)  NULL,
	[LocationDisplayName] [varchar](max)  NULL,
	[Organizer] [varchar](1024)  NULL,
	[OrganizerName] [varchar](1024)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[OrganizerAboutMe] [varchar](max)  NULL,
	[OrganizerBirthday] [varchar](1024)  NULL,
	[OrganizerHireDate] [datetime2](6)  NULL,
	[OrganizerMySite] [varchar](1024)  NULL,
	[OrganizerPreferredName] [varchar](1024)  NULL,
	[OrganizerAccountEnabled] [bit]  NULL,
	[OrganizerCity] [varchar](1024)  NULL,
	[OrganizerCompanyName] [varchar](1024)  NULL,
	[OrganizerCountry] [varchar](1024)  NULL,
	[OrganizerCreatedDateTime] [datetime2](6)  NULL,
	[OrganizerDepartment] [varchar](1024)  NOT NULL DEFAULT ('N/A'),
	[OrganizerDisplayName] [varchar](1024)  NULL,
	[OrganizerGivenName] [varchar](1024)  NULL,
	[OrganizerId] [varchar](1024)  NULL,
	[OrganizerJobTitle] [varchar](1024)  NULL,
	[OrganizerMail] [varchar](1024)  NULL,
	[OrganizerOfficeLocation] [varchar](1024)  NULL,
	[OrganizerOnPremisesImmutableId] [varchar](1024)  NULL,
	[OrganizerPostalCode] [varchar](1024)  NULL,
	[OrganizerPreferredLanguage] [varchar](1024)  NULL,
	[OrganizerState] [varchar](1024)  NULL,
	[OrganizerStreetAddress] [varchar](1024)  NULL,
	[OrganizerSurname] [varchar](1024)  NULL,
	[OrganizerUsageLocation] [varchar](1024)  NULL,
	[OrganizerUserPrincipalName] [varchar](1024)  NULL,
	[OrganizerUserType] [varchar](1024)  NULL,
	[OrganizerPuser] [varchar](1024)  NULL,
	[OrganizerPtenant] [varchar](1024)  NULL,
	[OrganizerPAdditionalInfo] [varchar](1024)  NULL,
	[OrganizerDatarow] [bigint]  NULL,
	[OrganizerUserrow] [bigint]  NULL,
	[OrganizerPagerow] [bigint]  NULL,
	[IsExternalEvent] [bit]  NULL,
	[EventWithManager] [bit]  NULL,
	[EventWithSubordinate] [bit]  NULL,
	[OrganizerReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[OrganizerManagerEmail] [varchar](1024)  NULL,
	[AttendeeNames] [varchar](max)  NULL,
	[AttendeeAddresses] [varchar](max)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)





---------------------------- Create augmented_team_chats ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[augmented_team_chats]
(
	[Id] [varchar](1024)  NULL,
	[InternetMessageId] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[Subject] [varchar](max)  NULL,
	[BodyPreview] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[ConversationId] [varchar](1024)  NULL,
	[ConversationIndex] [varchar](1024)  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[Sender] [varchar](1024)  NULL,
	[Sender_Name] [varchar](1024)  NULL,
	[From] [varchar](1024)  NULL,
	[From_Name] [varchar](1024)  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](1024)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[FlagStatus] [varchar](1024)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](1024)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](1024)  NULL,
	[SenderPreferredName] [varchar](1024)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](1024)  NULL,
	[SenderCompanyName] [varchar](1024)  NULL,
	[SenderCountry] [varchar](1024)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](1024)  NOT NULL DEFAULT ('N/A'),
	[SenderDisplayName] [varchar](1024)  NULL,
	[SenderGivenName] [varchar](1024)  NULL,
	[SenderId] [varchar](1024)  NULL,
	[SenderJobTitle] [varchar](1024)  NULL,
	[SenderMail] [varchar](1024)  NULL,
	[SenderOfficeLocation] [varchar](1024)  NULL,
	[SenderOnPremisesImmutableId] [varchar](1024)  NULL,
	[SenderPostalCode] [varchar](1024)  NULL,
	[SenderPreferredLanguage] [varchar](1024)  NULL,
	[SenderState] [varchar](1024)  NULL,
	[SenderStreetAddress] [varchar](1024)  NULL,
	[SenderSurname] [varchar](1024)  NULL,
	[SenderUsageLocation] [varchar](1024)  NULL,
	[SenderUserPrincipalName] [varchar](1024)  NULL,
	[SenderUserType] [varchar](1024)  NULL,
	[SenderPuser] [varchar](1024)  NULL,
	[SenderPtenant] [varchar](1024)  NULL,
	[SenderPAdditionalInfo] [varchar](1024)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[IsExternalChat] [bit]  NULL,
	[ChatWithManager] [bit]  NULL,
	[ChatWithSubordinate] [bit]  NULL,
	[SenderReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](1024)  NULL,
	[ToNames] [varchar](max)  NULL,
	[ToAddresses] [varchar](max)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)





---------------------------- Create augmented_flattened_emails ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[augmented_flattened_emails]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[IsReadReceiptRequested] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[WebLink] [varchar](1024)  NULL,
	[Sender] [varchar](1024)  NULL,
	[Sender_Name] [varchar](1024)  NULL,
	[From] [varchar](1024)  NULL,
	[From_Name] [varchar](1024)  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](1024)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[RecipientType] [varchar](max)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[emai_puser] [varchar](1024)  NULL,
	[email_ptenant] [varchar](1024)  NULL,
	[email_datarow] [bigint]  NULL,
	[email_userrow] [bigint]  NULL,
	[email_pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](1024)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](1024)  NULL,
	[SenderPreferredName] [varchar](1024)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](1024)  NULL,
	[SenderCompanyName] [varchar](1024)  NULL,
	[SenderCountry] [varchar](1024)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](1024)  NOT NULL DEFAULT ('N/A'),
	[SenderDisplayName] [varchar](1024)  NULL,
	[SenderGivenName] [varchar](1024)  NULL,
	[SenderId] [varchar](1024)  NULL,
	[SenderJobTitle] [varchar](1024)  NULL,
	[SenderMail] [varchar](1024)  NULL,
	[SenderOfficeLocation] [varchar](1024)  NULL,
	[SenderOnPremisesImmutableId] [varchar](1024)  NULL,
	[SenderPostalCode] [varchar](1024)  NULL,
	[SenderPreferredLanguage] [varchar](1024)  NULL,
	[SenderState] [varchar](1024)  NULL,
	[SenderStreetAddress] [varchar](1024)  NULL,
	[SenderSurname] [varchar](1024)  NULL,
	[SenderUsageLocation] [varchar](1024)  NULL,
	[SenderUserPrincipalName] [varchar](1024)  NULL,
	[SenderUserType] [varchar](1024)  NULL,
	[SenderPuser] [varchar](1024)  NULL,
	[SenderPtenant] [varchar](1024)  NULL,
	[SenderPAdditionalInfo] [varchar](1024)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[IsExternalEmail] [bit]  NULL,
	[MailToManager] [bit]  NULL,
	[MailToSubordinate] [bit]  NULL,
	[SenderReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](1024)  NULL,
	[RecipientManagerEmail] [varchar](1024)  NULL,
	[RecipientReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[Version] [varchar](1024)  NOT NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)







---------------------------- Create augmented_flattened_events ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[augmented_flattened_events]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[iCalUId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[Sensitivity] [varchar](max)  NULL,
	[IsAllDay] [bit]  NULL,
	[IsCancelled] [bit]  NULL,
	[IsOrganizer] [bit]  NULL,
	[ResponseRequested] [bit]  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[StartDateTime] [datetime2](6)  NULL,
	[StartTimeZone] [varchar](1024)  NULL,
	[EndDateTime] [datetime2](6)  NULL,
	[EndTimeZone] [varchar](1024)  NULL,
	[LocationDisplayName] [varchar](max)  NULL,
	[AttendeeStatusResponse] [varchar](max)  NULL,
	[IsRead] [bit]  NULL,
	[AttendeeName] [varchar](1024)  NULL,
	[AttendeeAddress] [varchar](1024)  NULL,
	[Organizer] [varchar](1024)  NULL,
	[OrganizerName] [varchar](1024)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[OrganizerAboutMe] [varchar](max)  NULL,
	[OrganizerBirthday] [varchar](1024)  NULL,
	[OrganizerHireDate] [datetime2](6)  NULL,
	[OrganizerMySite] [varchar](1024)  NULL,
	[OrganizerPreferredName] [varchar](1024)  NULL,
	[OrganizerAccountEnabled] [bit]  NULL,
	[OrganizerCity] [varchar](1024)  NULL,
	[OrganizerCompanyName] [varchar](1024)  NULL,
	[OrganizerCountry] [varchar](1024)  NULL,
	[OrganizerCreatedDateTime] [datetime2](6)  NULL,
	[OrganizerDepartment] [varchar](1024)  NOT NULL DEFAULT ('N/A'),
	[OrganizerDisplayName] [varchar](1024)  NULL,
	[OrganizerGivenName] [varchar](1024)  NULL,
	[OrganizerId] [varchar](1024)  NULL,
	[OrganizerJobTitle] [varchar](1024)  NULL,
	[OrganizerMail] [varchar](1024)  NULL,
	[OrganizerOfficeLocation] [varchar](1024)  NULL,
	[OrganizerOnPremisesImmutableId] [varchar](1024)  NULL,
	[OrganizerPostalCode] [varchar](1024)  NULL,
	[OrganizerPreferredLanguage] [varchar](1024)  NULL,
	[OrganizerState] [varchar](1024)  NULL,
	[OrganizerStreetAddress] [varchar](1024)  NULL,
	[OrganizerSurname] [varchar](1024)  NULL,
	[OrganizerUsageLocation] [varchar](1024)  NULL,
	[OrganizerUserPrincipalName] [varchar](1024)  NULL,
	[OrganizerUserType] [varchar](1024)  NULL,
	[OrganizerPuser] [varchar](1024)  NULL,
	[OrganizerPtenant] [varchar](1024)  NULL,
	[OrganizerPAdditionalInfo] [varchar](1024)  NULL,
	[OrganizerDatarow] [bigint]  NULL,
	[OrganizerUserrow] [bigint]  NULL,
	[OrganizerPagerow] [bigint]  NULL,
	[IsExternalEvent] [bit]  NULL,
	[EventWithManager] [bit]  NULL,
	[EventWithSubordinate] [bit]  NULL,
	[OrganizerReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[OrganizerManagerEmail] [varchar](1024)  NULL,
	[AttendeeManagerEmail] [varchar](1024)  NULL,
	[AttendeeReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[Version] [varchar](1024)  NOT NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)



---------------------------- Create augmented_flattened_team_chats ----------------------------



SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[augmented_flattened_team_chats]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[BodyPreview] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[ConversationId] [varchar](1024)  NULL,
	[ConversationIndex] [varchar](1024)  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[Sender] [varchar](1024)  NULL,
	[Sender_Name] [varchar](1024)  NULL,
	[From] [varchar](1024)  NULL,
	[From_Name] [varchar](1024)  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](1024)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[FlagStatus] [varchar](1024)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](1024)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](1024)  NULL,
	[SenderPreferredName] [varchar](1024)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](1024)  NULL,
	[SenderCompanyName] [varchar](1024)  NULL,
	[SenderCountry] [varchar](1024)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](1024)  NOT NULL DEFAULT ('N/A'),
	[SenderDisplayName] [varchar](1024)  NULL,
	[SenderGivenName] [varchar](1024)  NULL,
	[SenderId] [varchar](1024)  NULL,
	[SenderJobTitle] [varchar](1024)  NULL,
	[SenderMail] [varchar](1024)  NULL,
	[SenderOfficeLocation] [varchar](1024)  NULL,
	[SenderOnPremisesImmutableId] [varchar](1024)  NULL,
	[SenderPostalCode] [varchar](1024)  NULL,
	[SenderPreferredLanguage] [varchar](1024)  NULL,
	[SenderState] [varchar](1024)  NULL,
	[SenderStreetAddress] [varchar](1024)  NULL,
	[SenderSurname] [varchar](1024)  NULL,
	[SenderUsageLocation] [varchar](1024)  NULL,
	[SenderUserPrincipalName] [varchar](1024)  NULL,
	[SenderUserType] [varchar](1024)  NULL,
	[SenderPuser] [varchar](1024)  NULL,
	[SenderPtenant] [varchar](1024)  NULL,
	[SenderPAdditionalInfo] [varchar](1024)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[IsExternalChat] [bit]  NULL,
	[ChatWithManager] [bit]  NULL,
	[ChatWithSubordinate] [bit]  NULL,
	[SenderReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](1024)  NULL,
	[RecipientManagerEmail] [varchar](1024)  NULL,
	[RecipientReportsTo] [varchar](1024)  NOT NULL DEFAULT ('-'),
	[Version] [varchar](1024)  NOT NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)



---------------------------- Create flattened_emails ----------------------------




SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[flattened_emails]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[IsReadReceiptRequested] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[WebLink] [varchar](1024)  NULL,
	[Sender] [varchar](1024)  NULL,
	[Sender_Name] [varchar](1024)  NULL,
	[From] [varchar](1024)  NULL,
	[From_Name] [varchar](1024)  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](1024)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[RecipientType] [varchar](max)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)
GO




---------------------------- Create flattened_events ----------------------------



SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[flattened_events]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[iCalUId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[Sensitivity] [varchar](max)  NULL,
	[IsAllDay] [bit]  NULL,
	[IsCancelled] [bit]  NULL,
	[IsOrganizer] [bit]  NULL,
	[ResponseRequested] [bit]  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[StartDateTime] [datetime2](6)  NULL,
	[StartTimeZone] [varchar](1024)  NULL,
	[EndDateTime] [datetime2](6)  NULL,
	[EndTimeZone] [varchar](1024)  NULL,
	[LocationDisplayName] [varchar](max)  NULL,
	[AttendeeStatusResponse] [varchar](max)  NULL,
	[AttendeeName] [varchar](1024)  NULL,
	[AttendeeAddress] [varchar](1024)  NULL,
	[Organizer] [varchar](1024)  NULL,
	[OrganizerName] [varchar](1024)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)
GO



---------------------------- Create flattened_team_chats ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[flattened_team_chats]
(
	[Id] [varchar](1024)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](1024)  NULL,
	[Subject] [varchar](max)  NULL,
	[BodyPreview] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[ConversationId] [varchar](1024)  NULL,
	[ConversationIndex] [varchar](1024)  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[Sender] [varchar](1024)  NULL,
	[Sender_Name] [varchar](1024)  NULL,
	[From] [varchar](1024)  NULL,
	[From_Name] [varchar](1024)  NULL,
	[ContentType] [varchar](1024)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](1024)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[FlagStatus] [varchar](1024)  NULL,
	[ODataType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)
GO



---------------------------- Create users ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[users]
(
	[aboutMe] [varchar](max)  NULL,
	[birthday] [varchar](1024)  NULL,
	[hireDate] [datetime2](6)  NULL,
	[mySite] [varchar](1024)  NULL,
	[preferredName] [varchar](1024)  NULL,
	[accountEnabled] [bit]  NULL,
	[city] [varchar](1024)  NULL,
	[companyName] [varchar](1024)  NULL,
	[country] [varchar](1024)  NULL,
	[createdDateTime] [datetime2](6)  NULL,
	[department] [varchar](1024)  NULL,
	[displayName] [varchar](1024)  NULL,
	[givenName] [varchar](1024)  NULL,
	[id] [varchar](1024)  NULL,
	[jobTitle] [varchar](1024)  NULL,
	[mail] [varchar](1024)  NULL,
	[officeLocation] [varchar](1024)  NULL,
	[onPremisesImmutableId] [varchar](1024)  NULL,
	[postalCode] [varchar](1024)  NULL,
	[preferredLanguage] [varchar](1024)  NULL,
	[state] [varchar](1024)  NULL,
	[streetAddress] [varchar](1024)  NULL,
	[surname] [varchar](1024)  NULL,
	[usageLocation] [varchar](1024)  NULL,
	[userPrincipalName] [varchar](1024)  NULL,
	[userType] [varchar](1024)  NULL,
	[puser] [varchar](1024)  NULL,
	[ptenant] [varchar](1024)  NULL,
	[pAdditionalInfo] [varchar](1024)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[reportsTo] [varchar](max)  NULL,
	[managerEmail] [varchar](1024)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)
GO



---------------------------- Create recipient_email_domains ----------------------------




SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[recipient_email_domains]
(
	[Domain] [varchar](1024)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)
GO




--------------------------- Create conversation_entities_info ----------------------------




SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[conversation_entities_info]
(
	[uuid] [varchar](1024)  NULL,
	[interaction_id] [varchar](1024)  NULL,
    [source_type] [varchar](1024)  NULL,
	[sender_mail] [varchar](1024)  NULL,
	[sender_name] [varchar](1024)  NULL,
	[sender_domain] [varchar](1024)  NULL,
	[text] [varchar](1024)  NULL,
	[category] [varchar](1024)  NULL,
	[score] [float]  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO



---------------------------- Create conversation_entities_info ----------------------------



SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[conversation_sentiment_info]
(
	[uuid] [varchar](1024)  NULL,
	[interaction_id] [varchar](1024)  NULL,
    [source_type] [varchar](1024)  NULL,
	[sender_mail] [varchar](1024)  NULL,
	[sender_name] [varchar](1024)  NULL,
	[sender_domain] [varchar](1024)  NULL,
	[general_sentiment] [varchar](1024)  NULL,
	[pos_score] [float]  NULL,
	[neutral_score] [float]  NULL,
	[negative_score] [float]  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO




---------------------------- Create conversation_to_recipient_sentiment_info ----------------------------



SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[conversation_to_recipient_sentiment_info]
(
	[uuid] [varchar](1024)  NULL,
	[interaction_id] [varchar](1024)  NULL,
    [source_type] [varchar](1024)  NULL,
	[sender_mail] [varchar](1024)  NULL,
	[sender_name] [varchar](1024)  NULL,
	[sender_domain] [varchar](1024)  NULL,
	[general_sentiment] [varchar](1024)  NULL,
	[pos_score] [float]  NULL,
	[neutral_score] [float]  NULL,
	[negative_score] [float]  NULL,
	[recipient_name] [varchar](8000)  NULL,
	[recipient_address] [varchar](8000)  NULL,
	[recipient_domain] [varchar](8000)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO
