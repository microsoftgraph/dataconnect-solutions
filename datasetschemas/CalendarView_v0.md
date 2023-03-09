---
title: "CalendarView_v0"
description: "Contains the events from the Calendar view."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

The CalendarView_v0 dataset contains the events from the Calendar view.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| id | string | The unique identifier of the event. |
| allowNewTimeProposals | boolean | Indicates if the meeting organizer allows invitees to propose a new time when responding. |
| createdDateTime | datetime | The date and time that the event was created. |
| lastModifiedDateTime | datetime | The date and time that the event was last modified. |
| changeKey | string | Identifies the version of the event object. Every time the event is changed, ChangeKey changes as well. This allows Exchange to apply changes to the correct version of the object. |
| categories | string | The categories associated with the event. Format: ARRAY<STRING> |
| originalStartTimeZone | string | The start time zone that was set when the event was created. See DateTimeTimeZone for a list of valid time zones. |
| originalEndTimeZone | string | The end time zone that was set when the event was created. See DateTimeTimeZone for a list of valid time zones. |
| responseStatus | string | Indicates the type of response sent in response to an event message. Format: STRUCT<Response: STRING, Time: STRING> |
| iCalUId | string | A unique identifier that is shared by all instances of an event across different calendars. |
| isOnlineMeeting | boolean | Indicates whether this event has online meeting information. |
| reminderMinutesBeforeStart | int32 | The number of minutes before the event start time that the reminder alert occurs. |
| isReminderOn | boolean | Set to true if an alert is set to remind the user of the event. |
| hasAttachments | boolean | Set to true if the event has attachments. |
| subject | string | The text of the event's subject line. |
| body | string | The body of the message that is unique to the conversation.Format: STRUCT<ContentType: STRING, Content: STRING> |
| importance | string | The importance of the event: Low, Normal, High. |
| sensitivity | string | Indicates the level of privacy for the event: Normal, Personal, Private, Confidential. |
| start | string | The start time of the event. Format: STRUCT<DateTime: STRING, TimeZone: STRING> |
| end | string | The date and time that the event ends. Format: STRUCT<DateTime: STRING, TimeZone: STRING> |
| location | string | Location information of the event. Format: STRUCT<DisplayName: STRING, Address: STRUCT<Street: STRING, City: STRING, State: STRING, CountryOrRegion: STRING, PostalCode: STRING>, Coordinates: STRUCT<Altitude: DOUBLE, Latitude: DOUBLE, Longitude: DOUBLE, Accuracy: DOUBLE, AltitudeAccuracy: DOUBLE>> |
| isAllDay | boolean | Set to true if the event lasts all day. Adjusting this property requires adjusting the Start and End properties of the event as well. |
| isCancelled | boolean | Set to true if the event has been canceled. |
| isOrganizer | boolean | Set to true if the message sender is also the organizer. |
| onlineMeeting | string | Details for an attendee to join the meeting online. |
| onlineMeetingProvider | string | Represents the online meeting service provider. The possible values are teamsForBusiness, skypeForBusiness, and skypeForConsumer. |
| recurrence | string | The recurrence pattern for the event. Format: STRUCT<Pattern: STRUCT<Type: STRING, \`Interval\`: INT, Month: INT, DayOfMonth: INT, DaysOfWeek: ARRAY<STRING>, FirstDayOfWeek: STRING, Index: STRING>, \`Range\`: STRUCT<Type: STRING, StartDate: STRING, EndDate: STRING, RecurrenceTimeZone: STRING, NumberOfOccurrences: INT>> |
| responseRequested | boolean | Set to true if the sender would like a response when the event is accepted or declined. |
| showAs | string | The status to show: Free, Tentative, Busy, Oof, WorkingElsewhere, Unknown. |
| transactionId | string | A custom identifier specified by a client app for the server to avoid redundant POST operations in case of client retries to create the same event. |
| type | string | The event type: SingleInstance, Occurrence, Exception, SeriesMaster. |
| attendees | string | The collection of attendees for the event. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>, Status: STRUCT<Response: STRING, Time: STRING>, Type: STRING>> |
| organizer | string | The organizer of the event. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| webLink | string | The URL to open the event in Outlook Web App. |
| attachments | string | The FileAttachment and ItemAttachment attachments for the message. Navigation property. Format: ARRAY<STRUCT<LastModifiedDateTime: STRING, Name: STRING, ContentType: STRING, Size: INT, IsInline: BOOLEAN, Id: STRING>> |
| bodyPreview | string | The preview of the message associated with the event. It is in text format. |
| locations | string | The locations where the event is held or attended from. The location and locations properties always correspond with each other. Format:  ARRAY<STRUCT<DisplayName: STRING, Address: STRUCT<Street: STRING, City: STRING, State: STRING, CountryOrRegion: STRING, PostalCode: STRING>, Coordinates: STRUCT<Altitude: DOUBLE, Latitude: DOUBLE, Longitude: DOUBLE, Accuracy: DOUBLE, AltitudeAccuracy: DOUBLE>, LocationEmailAddress: STRING, LocationUri: STRING, LocationType: STRING, UniqueId: STRING, UniqueIdType: STRING>> |
| onlineMeetingUrl | string | A URL for an online meeting. The property is set only when an organizer specifies an event as an online meeting such as a Skype meeting |
| seriesMasterId | string | The ID for the recurring series master item, if this event is part of a recurring series. |
| originalStart | datetime | The start time that was set when the event was created in UTC time. |
