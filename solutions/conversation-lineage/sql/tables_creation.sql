
---------------------------- Create augmented_emails ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[augmented_emails]
(
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[IsReadReceiptRequested] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[WebLink] [varchar](1000)  NULL,
	[Sender] [varchar](100)  NULL,
	[Sender_Name] [varchar](100)  NULL,
	[From] [varchar](100)  NULL,
	[From_Name] [varchar](100)  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[ODataType] [varchar](100)  NULL,
	[emai_puser] [varchar](100)  NULL,
	[email_ptenant] [varchar](100)  NULL,
	[email_datarow] [bigint]  NULL,
	[email_userrow] [bigint]  NULL,
	[email_pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](100)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](200)  NULL,
	[SenderPreferredName] [varchar](200)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](200)  NULL,
	[SenderCompanyName] [varchar](200)  NULL,
	[SenderCountry] [varchar](100)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](200)  NOT NULL DEFAULT ('N\A'),
	[SenderDisplayName] [varchar](200)  NULL,
	[SenderGivenName] [varchar](200)  NULL,
	[SenderId] [varchar](500)  NULL,
	[SenderJobTitle] [varchar](200)  NULL,
	[SenderMail] [varchar](100)  NULL,
	[SenderOfficeLocation] [varchar](200)  NULL,
	[SenderOnPremisesImmutableId] [varchar](200)  NULL,
	[SenderPostalCode] [varchar](200)  NULL,
	[SenderPreferredLanguage] [varchar](100)  NULL,
	[SenderState] [varchar](200)  NULL,
	[SenderStreetAddress] [varchar](200)  NULL,
	[SenderSurname] [varchar](200)  NULL,
	[SenderUsageLocation] [varchar](200)  NULL,
	[SenderUserPrincipalName] [varchar](200)  NULL,
	[SenderUserType] [varchar](200)  NULL,
	[SenderPuser] [varchar](500)  NULL,
	[SenderPtenant] [varchar](500)  NULL,
	[SenderPAdditionalInfo] [varchar](500)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[SenderReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](100)  NULL,
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
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[iCalUId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[Sensitivity] [varchar](max)  NULL,
	[IsAllDay] [bit]  NULL,
	[IsCancelled] [bit]  NULL,
	[IsOrganizer] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[ResponseRequested] [bit]  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[StartDateTime] [datetime2](6)  NULL,
	[StartTimeZone] [varchar](200)  NULL,
	[EndDateTime] [datetime2](6)  NULL,
	[EndTimeZone] [varchar](200)  NULL,
	[LocationDisplayName] [varchar](max)  NULL,
	[Organizer] [varchar](100)  NULL,
	[OrganizerName] [varchar](100)  NULL,
	[ODataType] [varchar](100)  NULL,
	[puser] [varchar](100)  NULL,
	[ptenant] [varchar](100)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[OrganizerAboutMe] [varchar](max)  NULL,
	[OrganizerBirthday] [varchar](100)  NULL,
	[OrganizerHireDate] [datetime2](6)  NULL,
	[OrganizerMySite] [varchar](200)  NULL,
	[OrganizerPreferredName] [varchar](200)  NULL,
	[OrganizerAccountEnabled] [bit]  NULL,
	[OrganizerCity] [varchar](200)  NULL,
	[OrganizerCompanyName] [varchar](200)  NULL,
	[OrganizerCountry] [varchar](100)  NULL,
	[OrganizerCreatedDateTime] [datetime2](6)  NULL,
	[OrganizerDepartment] [varchar](200)  NOT NULL DEFAULT ('N/A'),
	[OrganizerDisplayName] [varchar](200)  NULL,
	[OrganizerGivenName] [varchar](200)  NULL,
	[OrganizerId] [varchar](500)  NULL,
	[OrganizerJobTitle] [varchar](200)  NULL,
	[OrganizerMail] [varchar](100)  NULL,
	[OrganizerOfficeLocation] [varchar](200)  NULL,
	[OrganizerOnPremisesImmutableId] [varchar](200)  NULL,
	[OrganizerPostalCode] [varchar](200)  NULL,
	[OrganizerPreferredLanguage] [varchar](100)  NULL,
	[OrganizerState] [varchar](200)  NULL,
	[OrganizerStreetAddress] [varchar](200)  NULL,
	[OrganizerSurname] [varchar](200)  NULL,
	[OrganizerUsageLocation] [varchar](200)  NULL,
	[OrganizerUserPrincipalName] [varchar](200)  NULL,
	[OrganizerUserType] [varchar](200)  NULL,
	[OrganizerPuser] [varchar](500)  NULL,
	[OrganizerPtenant] [varchar](500)  NULL,
	[OrganizerPAdditionalInfo] [varchar](500)  NULL,
	[OrganizerDatarow] [bigint]  NULL,
	[OrganizerUserrow] [bigint]  NULL,
	[OrganizerPagerow] [bigint]  NULL,
	[IsExternalEvent] [bit]  NULL,
	[EventWithManager] [bit]  NULL,
	[EventWithSubordinate] [bit]  NULL,
	[OrganizerReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[OrganizerManagerEmail] [varchar](100)  NULL,
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
	[Id] [varchar](200)  NULL,
	[InternetMessageId] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[Subject] [varchar](max)  NULL,
	[BodyPreview] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[ConversationId] [varchar](200)  NULL,
	[ConversationIndex] [varchar](200)  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[Sender] [varchar](100)  NULL,
	[Sender_Name] [varchar](100)  NULL,
	[From] [varchar](100)  NULL,
	[From_Name] [varchar](100)  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](100)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[FlagStatus] [varchar](200)  NULL,
	[ODataType] [varchar](100)  NULL,
	[puser] [varchar](100)  NULL,
	[ptenant] [varchar](100)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](100)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](200)  NULL,
	[SenderPreferredName] [varchar](200)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](200)  NULL,
	[SenderCompanyName] [varchar](200)  NULL,
	[SenderCountry] [varchar](100)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](200)  NOT NULL DEFAULT ('N/A'),
	[SenderDisplayName] [varchar](200)  NULL,
	[SenderGivenName] [varchar](200)  NULL,
	[SenderId] [varchar](500)  NULL,
	[SenderJobTitle] [varchar](200)  NULL,
	[SenderMail] [varchar](100)  NULL,
	[SenderOfficeLocation] [varchar](200)  NULL,
	[SenderOnPremisesImmutableId] [varchar](200)  NULL,
	[SenderPostalCode] [varchar](200)  NULL,
	[SenderPreferredLanguage] [varchar](100)  NULL,
	[SenderState] [varchar](200)  NULL,
	[SenderStreetAddress] [varchar](200)  NULL,
	[SenderSurname] [varchar](200)  NULL,
	[SenderUsageLocation] [varchar](200)  NULL,
	[SenderUserPrincipalName] [varchar](200)  NULL,
	[SenderUserType] [varchar](200)  NULL,
	[SenderPuser] [varchar](500)  NULL,
	[SenderPtenant] [varchar](500)  NULL,
	[SenderPAdditionalInfo] [varchar](500)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[IsExternalChat] [bit]  NULL,
	[ChatWithManager] [bit]  NULL,
	[ChatWithSubordinate] [bit]  NULL,
	[SenderReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](100)  NULL,
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
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[IsReadReceiptRequested] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[WebLink] [varchar](1000)  NULL,
	[Sender] [varchar](100)  NULL,
	[Sender_Name] [varchar](100)  NULL,
	[From] [varchar](100)  NULL,
	[From_Name] [varchar](100)  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](100)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[RecipientType] [varchar](max)  NULL,
	[ODataType] [varchar](100)  NULL,
	[emai_puser] [varchar](100)  NULL,
	[email_ptenant] [varchar](100)  NULL,
	[email_datarow] [bigint]  NULL,
	[email_userrow] [bigint]  NULL,
	[email_pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](100)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](200)  NULL,
	[SenderPreferredName] [varchar](200)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](200)  NULL,
	[SenderCompanyName] [varchar](200)  NULL,
	[SenderCountry] [varchar](100)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](200)  NOT NULL DEFAULT ('N/A'),
	[SenderDisplayName] [varchar](200)  NULL,
	[SenderGivenName] [varchar](200)  NULL,
	[SenderId] [varchar](500)  NULL,
	[SenderJobTitle] [varchar](200)  NULL,
	[SenderMail] [varchar](100)  NULL,
	[SenderOfficeLocation] [varchar](200)  NULL,
	[SenderOnPremisesImmutableId] [varchar](200)  NULL,
	[SenderPostalCode] [varchar](200)  NULL,
	[SenderPreferredLanguage] [varchar](100)  NULL,
	[SenderState] [varchar](200)  NULL,
	[SenderStreetAddress] [varchar](200)  NULL,
	[SenderSurname] [varchar](200)  NULL,
	[SenderUsageLocation] [varchar](200)  NULL,
	[SenderUserPrincipalName] [varchar](200)  NULL,
	[SenderUserType] [varchar](200)  NULL,
	[SenderPuser] [varchar](500)  NULL,
	[SenderPtenant] [varchar](500)  NULL,
	[SenderPAdditionalInfo] [varchar](500)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[IsExternalEmail] [bit]  NULL,
	[MailToManager] [bit]  NULL,
	[MailToSubordinate] [bit]  NULL,
	[SenderReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](100)  NULL,
	[RecipientManagerEmail] [varchar](100)  NULL,
	[RecipientReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[Version] [varchar](100)  NOT NULL
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
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[iCalUId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[Sensitivity] [varchar](max)  NULL,
	[IsAllDay] [bit]  NULL,
	[IsCancelled] [bit]  NULL,
	[IsOrganizer] [bit]  NULL,
	[ResponseRequested] [bit]  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[StartDateTime] [datetime2](6)  NULL,
	[StartTimeZone] [varchar](200)  NULL,
	[EndDateTime] [datetime2](6)  NULL,
	[EndTimeZone] [varchar](200)  NULL,
	[LocationDisplayName] [varchar](max)  NULL,
	[AttendeeStatusResponse] [varchar](max)  NULL,
	[IsRead] [bit]  NULL,
	[AttendeeName] [varchar](100)  NULL,
	[AttendeeAddress] [varchar](100)  NULL,
	[Organizer] [varchar](100)  NULL,
	[OrganizerName] [varchar](100)  NULL,
	[ODataType] [varchar](100)  NULL,
	[puser] [varchar](100)  NULL,
	[ptenant] [varchar](100)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[OrganizerAboutMe] [varchar](max)  NULL,
	[OrganizerBirthday] [varchar](100)  NULL,
	[OrganizerHireDate] [datetime2](6)  NULL,
	[OrganizerMySite] [varchar](200)  NULL,
	[OrganizerPreferredName] [varchar](200)  NULL,
	[OrganizerAccountEnabled] [bit]  NULL,
	[OrganizerCity] [varchar](200)  NULL,
	[OrganizerCompanyName] [varchar](200)  NULL,
	[OrganizerCountry] [varchar](100)  NULL,
	[OrganizerCreatedDateTime] [datetime2](6)  NULL,
	[OrganizerDepartment] [varchar](200)  NOT NULL DEFAULT ('N/A'),
	[OrganizerDisplayName] [varchar](200)  NULL,
	[OrganizerGivenName] [varchar](200)  NULL,
	[OrganizerId] [varchar](500)  NULL,
	[OrganizerJobTitle] [varchar](200)  NULL,
	[OrganizerMail] [varchar](100)  NULL,
	[OrganizerOfficeLocation] [varchar](200)  NULL,
	[OrganizerOnPremisesImmutableId] [varchar](200)  NULL,
	[OrganizerPostalCode] [varchar](200)  NULL,
	[OrganizerPreferredLanguage] [varchar](100)  NULL,
	[OrganizerState] [varchar](200)  NULL,
	[OrganizerStreetAddress] [varchar](200)  NULL,
	[OrganizerSurname] [varchar](200)  NULL,
	[OrganizerUsageLocation] [varchar](200)  NULL,
	[OrganizerUserPrincipalName] [varchar](200)  NULL,
	[OrganizerUserType] [varchar](200)  NULL,
	[OrganizerPuser] [varchar](500)  NULL,
	[OrganizerPtenant] [varchar](500)  NULL,
	[OrganizerPAdditionalInfo] [varchar](500)  NULL,
	[OrganizerDatarow] [bigint]  NULL,
	[OrganizerUserrow] [bigint]  NULL,
	[OrganizerPagerow] [bigint]  NULL,
	[IsExternalEvent] [bit]  NULL,
	[EventWithManager] [bit]  NULL,
	[EventWithSubordinate] [bit]  NULL,
	[OrganizerReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[OrganizerManagerEmail] [varchar](100)  NULL,
	[AttendeeManagerEmail] [varchar](100)  NULL,
	[AttendeeReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[Version] [varchar](100)  NOT NULL
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
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[BodyPreview] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[ConversationId] [varchar](200)  NULL,
	[ConversationIndex] [varchar](200)  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[Sender] [varchar](100)  NULL,
	[Sender_Name] [varchar](100)  NULL,
	[From] [varchar](100)  NULL,
	[From_Name] [varchar](100)  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](100)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[FlagStatus] [varchar](200)  NULL,
	[ODataType] [varchar](100)  NULL,
	[puser] [varchar](100)  NULL,
	[ptenant] [varchar](100)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[SenderAboutMe] [varchar](max)  NULL,
	[SenderBirthday] [varchar](100)  NULL,
	[SenderHireDate] [datetime2](6)  NULL,
	[SenderMySite] [varchar](200)  NULL,
	[SenderPreferredName] [varchar](200)  NULL,
	[SenderAccountEnabled] [bit]  NULL,
	[SenderCity] [varchar](200)  NULL,
	[SenderCompanyName] [varchar](200)  NULL,
	[SenderCountry] [varchar](100)  NULL,
	[SenderCreatedDateTime] [datetime2](6)  NULL,
	[SenderDepartment] [varchar](200)  NOT NULL DEFAULT ('N/A'),
	[SenderDisplayName] [varchar](200)  NULL,
	[SenderGivenName] [varchar](200)  NULL,
	[SenderId] [varchar](500)  NULL,
	[SenderJobTitle] [varchar](200)  NULL,
	[SenderMail] [varchar](100)  NULL,
	[SenderOfficeLocation] [varchar](200)  NULL,
	[SenderOnPremisesImmutableId] [varchar](200)  NULL,
	[SenderPostalCode] [varchar](200)  NULL,
	[SenderPreferredLanguage] [varchar](100)  NULL,
	[SenderState] [varchar](200)  NULL,
	[SenderStreetAddress] [varchar](200)  NULL,
	[SenderSurname] [varchar](200)  NULL,
	[SenderUsageLocation] [varchar](200)  NULL,
	[SenderUserPrincipalName] [varchar](200)  NULL,
	[SenderUserType] [varchar](200)  NULL,
	[SenderPuser] [varchar](500)  NULL,
	[SenderPtenant] [varchar](500)  NULL,
	[SenderPAdditionalInfo] [varchar](500)  NULL,
	[SenderDatarow] [bigint]  NULL,
	[SenderUserrow] [bigint]  NULL,
	[SenderPagerow] [bigint]  NULL,
	[IsExternalChat] [bit]  NULL,
	[ChatWithManager] [bit]  NULL,
	[ChatWithSubordinate] [bit]  NULL,
	[SenderReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[SenderManagerEmail] [varchar](100)  NULL,
	[RecipientManagerEmail] [varchar](100)  NULL,
	[RecipientReportsTo] [varchar](100)  NOT NULL DEFAULT ('-'),
	[Version] [varchar](100)  NOT NULL
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
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[IsReadReceiptRequested] [bit]  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[WebLink] [varchar](1000)  NULL,
	[Sender] [varchar](100)  NULL,
	[Sender_Name] [varchar](100)  NULL,
	[From] [varchar](100)  NULL,
	[From_Name] [varchar](100)  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](100)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[RecipientType] [varchar](max)  NULL,
	[ODataType] [varchar](100)  NULL,
	[puser] [varchar](100)  NULL,
	[ptenant] [varchar](100)  NULL,
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
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[iCalUId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[Sensitivity] [varchar](max)  NULL,
	[IsAllDay] [bit]  NULL,
	[IsCancelled] [bit]  NULL,
	[IsOrganizer] [bit]  NULL,
	[ResponseRequested] [bit]  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[StartDateTime] [datetime2](6)  NULL,
	[StartTimeZone] [varchar](200)  NULL,
	[EndDateTime] [datetime2](6)  NULL,
	[EndTimeZone] [varchar](200)  NULL,
	[LocationDisplayName] [varchar](max)  NULL,
	[AttendeeStatusResponse] [varchar](max)  NULL,
	[AttendeeName] [varchar](100)  NULL,
	[AttendeeAddress] [varchar](100)  NULL,
	[Organizer] [varchar](100)  NULL,
	[OrganizerName] [varchar](100)  NULL,
	[ODataType] [varchar](100)  NULL,
	[puser] [varchar](100)  NULL,
	[ptenant] [varchar](100)  NULL,
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
	[Id] [varchar](200)  NULL,
	[CreatedDateTime] [datetime2](6)  NULL,
	[LastModifiedDateTime] [datetime2](6)  NULL,
	[InternetMessageId] [varchar](200)  NULL,
	[Subject] [varchar](max)  NULL,
	[BodyPreview] [varchar](max)  NULL,
	[Importance] [varchar](max)  NULL,
	[ConversationId] [varchar](200)  NULL,
	[ConversationIndex] [varchar](200)  NULL,
	[IsRead] [bit]  NULL,
	[IsDraft] [bit]  NULL,
	[Sender] [varchar](100)  NULL,
	[Sender_Name] [varchar](100)  NULL,
	[From] [varchar](100)  NULL,
	[From_Name] [varchar](100)  NULL,
	[ContentType] [varchar](200)  NULL,
	[Content] [varchar](max)  NULL,
	[Recipient] [varchar](100)  NULL,
	[RecipientName] [varchar](max)  NULL,
	[FlagStatus] [varchar](200)  NULL,
	[ODataType] [varchar](100)  NULL,
	[puser] [varchar](100)  NULL,
	[ptenant] [varchar](100)  NULL,
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
	[birthday] [varchar](100)  NULL,
	[hireDate] [datetime2](6)  NULL,
	[mySite] [varchar](200)  NULL,
	[preferredName] [varchar](200)  NULL,
	[accountEnabled] [bit]  NULL,
	[city] [varchar](200)  NULL,
	[companyName] [varchar](200)  NULL,
	[country] [varchar](100)  NULL,
	[createdDateTime] [datetime2](6)  NULL,
	[department] [varchar](200)  NULL,
	[displayName] [varchar](200)  NULL,
	[givenName] [varchar](200)  NULL,
	[id] [varchar](500)  NULL,
	[jobTitle] [varchar](200)  NULL,
	[mail] [varchar](100)  NULL,
	[officeLocation] [varchar](200)  NULL,
	[onPremisesImmutableId] [varchar](200)  NULL,
	[postalCode] [varchar](200)  NULL,
	[preferredLanguage] [varchar](100)  NULL,
	[state] [varchar](200)  NULL,
	[streetAddress] [varchar](200)  NULL,
	[surname] [varchar](200)  NULL,
	[usageLocation] [varchar](200)  NULL,
	[userPrincipalName] [varchar](200)  NULL,
	[userType] [varchar](200)  NULL,
	[puser] [varchar](500)  NULL,
	[ptenant] [varchar](500)  NULL,
	[pAdditionalInfo] [varchar](500)  NULL,
	[datarow] [bigint]  NULL,
	[userrow] [bigint]  NULL,
	[pagerow] [bigint]  NULL,
	[reportsTo] [varchar](max)  NULL,
	[managerEmail] [varchar](100)  NULL
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
	[Domain] [varchar](250)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	HEAP
)
GO




---------------------------- Create conversation_entities_info ----------------------------




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







































