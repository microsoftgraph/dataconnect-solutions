CREATE TABLE dbo.groups_per_day
(
    id                                                      VARCHAR(1024) NOT NULL,
    version                                                 datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
    day                                                     datetime2(6)  NOT NULL,
    hour_time_slot                                          datetime2(6) NOT NULL,
    hour                                                    Int,
    group_name                                              varchar(1024) NOT NULL,
    display_name                                            varchar(1024) NOT NULL,
    group_members                                           varchar(max) NOT NULL,
    timezone_str                                            varchar(128) DEFAULT 'UTC' NOT NULL,
    timezone_nr                                             varchar(128) DEFAULT '00' NOT NULL,
    CONSTRAINT PK_groups_per_day                            PRIMARY KEY (id, version)
);
CREATE NONCLUSTERED INDEX groups_per_day_day_ndx ON dbo.groups_per_day (day ASC);
CREATE NONCLUSTERED INDEX groups_per_day_day_v_ndx ON dbo.groups_per_day (day ASC, version ASC);
CREATE NONCLUSTERED INDEX groups_per_day_day_hour_ndx ON dbo.groups_per_day (day ASC, hour ASC);
CREATE NONCLUSTERED INDEX groups_per_day_day_hour_v_ndx ON dbo.groups_per_day (day ASC, hour ASC, version ASC);
CREATE TABLE dbo.members_to_group_participation
(
    id                                                      VARCHAR(1024) NOT NULL,
    version                                                 datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
    group_name                                              varchar(1024) NOT NULL,
    member_email                                            varchar(1024) NOT NULL,
    day                                                     datetime2(6) NOT NULL,
    day_index                                               Int,
    month_index                                             Int,
    year_index                                              Int,
    hour_time_slot                                          datetime2(6) NOT NULL,
    invitation_status                                       Int,
    participation_status                                    Int,
    timezone_str                                            varchar(128) DEFAULT 'UTC' NOT NULL,
    timezone_nr                                             varchar(128) DEFAULT '00' NOT NULL,
    CONSTRAINT PK_members_to_group_participation            PRIMARY KEY (id, version)
);
CREATE NONCLUSTERED INDEX members_to_group_participation_group_name_ndx ON dbo.members_to_group_participation (group_name ASC);
CREATE NONCLUSTERED INDEX members_to_group_participation_group_name_v_ndx ON dbo.members_to_group_participation (group_name ASC, version ASC);
CREATE NONCLUSTERED INDEX members_to_group_participation_group_name_member_email_ndx ON dbo.members_to_group_participation (group_name ASC, member_email ASC);
CREATE NONCLUSTERED INDEX members_to_group_participation_group_name_member_email_v_ndx ON dbo.members_to_group_participation (group_name ASC, member_email ASC, version ASC);
CREATE TABLE dbo.employee_profile (
      id                                                    varchar(128) NOT NULL,
      version                                               datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
      mail                                                  varchar(1024),
      display_name                                          varchar(1024),
      about_me                                              varchar(MAX),
      job_title                                             varchar(1024),
      company_name                                          varchar(1024),
      department                                            varchar(1024),
      country                                               varchar(1024),
      office_location                                       varchar(1024),
      city                                                  varchar(1024),
      state                                                 varchar(1024),
      skills                                                varchar(1024),
      responsibilities                                      varchar(1024),
      engagement                                            varchar(1024),
      image                                                 varchar(MAX),
      CONSTRAINT PK_employee_profile                        PRIMARY KEY (id, version)
) ;
CREATE NONCLUSTERED INDEX employee_profile_mail_ndx ON dbo.employee_profile (mail ASC);
CREATE NONCLUSTERED INDEX employee_profile_mail_v_ndx ON dbo.employee_profile (mail ASC, version ASC);
CREATE TABLE dbo.groups_per_week
(
    id                                                      VARCHAR(128) NOT NULL,
    version                                                 datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
    day                                                     datetime2(6) NOT NULL,
    group_members                                           varchar(max) NOT NULL,
    timezone_str                                            varchar(128) DEFAULT 'UTC' NOT NULL,
    timezone_nr                                             varchar(128) DEFAULT '00' NOT NULL,
    CONSTRAINT PK_groups_per_week                           PRIMARY KEY (id, version)
);
CREATE NONCLUSTERED INDEX groups_per_week_day_ndx ON dbo.groups_per_week (day ASC);
CREATE NONCLUSTERED INDEX groups_per_week_day_v_ndx ON dbo.groups_per_week (day ASC, version ASC);
CREATE TABLE members_group_personal_meetings
(
    id                                                      VARCHAR(1024) NOT NULL,
    version                                                 datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
    group_name                                              varchar(1024) NOT NULL,
    watercooler_hour                                        Int,
    group_busy_slots                                        varchar(MAX) NOT NULL,
    timezone_str                                            varchar(128) DEFAULT 'UTC' NOT NULL,
    timezone_nr                                             varchar(128) DEFAULT '00' NOT NULL,
    CONSTRAINT PK_members_group_personal_meetings           PRIMARY KEY (id, version)
);
CREATE NONCLUSTERED INDEX members_group_personal_meetings_group_name_ndx ON dbo.members_group_personal_meetings (group_name ASC);
CREATE NONCLUSTERED INDEX members_group_personal_meetings_group_name_v_ndx ON dbo.members_group_personal_meetings (group_name ASC, version ASC);
-- dbo.configurations definition
CREATE TABLE dbo.configurations
(
    [key_name]                                              varchar(100) NOT NULL,
    [value]                                                 nvarchar(MAX),
    CONSTRAINT PK_configurations                            PRIMARY KEY ([key_name])
) ;