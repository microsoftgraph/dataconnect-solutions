---
title: "Event_v0"
description: "Contains the information from a user’s calendar events."
author: "fercobo-msft"
localization_priority: Priority
ms.prod: "data-connect"
---

# Overview

Contains the information from a user’s calendar events.

## Properties

| Name | Type | Description |
|--|--|--|
| puser | string | The unique identifier of the user. |
| Id | string | The unique identifier of the event. |
| CreatedDateTime | datetime | The date and time that the event was created. |
| LastModifiedDateTime | datetime | The date and time that the event was last modified. |
| ChangeKey | string | Identifies the version of the event object. Every time the event is changed, ChangeKey changes as well. This allows Exchange to apply changes to the correct version of the object. |
| Categories | string | The categories associated with the event. Format: ARRAY<STRING> |
| OriginalStartTimeZone | string | The start time zone that was set when the event was created. See DateTimeTimeZone for a list of valid time zones. |
| OriginalEndTimeZone | string | The end time zone that was set when the event was created. See DateTimeTimeZone for a list of valid time zones. |
| ResponseStatus | string | Indicates the type of response sent in response to an event message. Format: STRUCT<Response: STRING, Time: STRING> |
| iCalUId | string | A unique identifier that is shared by all instances of an event across different calendars. |
| ReminderMinutesBeforeStart | int32 | The number of minutes before the event start time that the reminder alert occurs. |
| IsReminderOn | boolean | Set to true if an alert is set to remind the user of the event. |
| HasAttachments | boolean | Set to true if the event has attachments. |
| Subject | string | The text of the event's subject line. |
| Body | string | The body of the message associated with the event.Format: STRUCT<ContentType: STRING, Content: STRING> |
| Importance | string | The importance of the event: Low, Normal, High. |
| Sensitivity | string | Indicates the level of privacy for the event: Normal, Personal, Private, Confidential. |
| Start | string | The start time of the event. Format: STRUCT<DateTime: STRING, TimeZone: STRING> |
| End | string | The date and time that the event ends. Format: STRUCT<DateTime: STRING, TimeZone: STRING> |
| Location | string | Location information of the event. Format: STRUCT<DisplayName: STRING, Address: STRUCT<Street: STRING, City: STRING, State: STRING, CountryOrRegion: STRING, PostalCode: STRING>, Coordinates: STRUCT<Altitude: DOUBLE, Latitude: DOUBLE, Longitude: DOUBLE, Accuracy: DOUBLE, AltitudeAccuracy: DOUBLE>> |
| IsAllDay | boolean | Set to true if the event lasts all day. Adjusting this property requires adjusting the Start and End properties of the event as well. |
| IsCancelled | boolean | Set to true if the event has been canceled. |
| IsOrganizer | boolean | Set to true if the message sender is also the organizer. |
| Recurrence | string | The recurrence pattern for the event. Format: STRUCT<Pattern: STRUCT<Type: STRING, \`Interval\`: INT, Month: INT, DayOfMonth: INT, DaysOfWeek: ARRAY<STRING>, FirstDayOfWeek: STRING, Index: STRING>, \`Range\`: STRUCT<Type: STRING, StartDate: STRING, EndDate: STRING, RecurrenceTimeZone: STRING, NumberOfOccurrences: INT>> |
| ResponseRequested | boolean | Set to true if the sender would like a response when the event is accepted or declined. |
| ShowAs | string | The status to show: Free, Tentative, Busy, Oof, WorkingElsewhere, Unknown. |
| Type | string | The event type: SingleInstance, Occurrence, Exception, SeriesMaster. |
| Attendees | string | The collection of attendees for the event. Format: ARRAY<STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>, Status: STRUCT<Response: STRING, Time: STRING>, Type: STRING>> |
| Organizer | string | The organizer of the event. Format: STRUCT<EmailAddress: STRUCT<Name: STRING, Address: STRING>> |
| WebLink | string | The URL to open the event in Outlook Web App. |
| Attachments | string | The FileAttachment and ItemAttachment attachments for the message. Navigation property. Format: ARRAY<STRUCT<LastModifiedDateTime: STRING, Name: STRING, ContentType: STRING, Size: INT, IsInline: BOOLEAN, Id: STRING>> |
