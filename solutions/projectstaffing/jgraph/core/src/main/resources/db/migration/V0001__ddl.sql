-- dbo.airtable_employee_profile definition
CREATE TABLE dbo.airtable_employee_profile (
    mail varchar(1024),
    name varchar(1024),
    status varchar(1024),
    locate varchar(1024),
    up_for_redeployment_date varchar(1024),
    current_engagement varchar(1024),
    current_engagement_end_date varchar(1024),
    reports_to varchar(1024),
    [role] varchar(1024),
    consultant_type varchar(1024),
    id varchar(128) NOT NULL,
    version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
    CONSTRAINT PK_airtable_employee_profile PRIMARY KEY (id,version)
) ;
CREATE NONCLUSTERED INDEX employee_profile_mail_ndx ON dbo.airtable_employee_profile (mail ASC);
CREATE NONCLUSTERED INDEX employee_profile_mail_version_ndx ON dbo.airtable_employee_profile (mail ASC, version ASC);

-- dbo.configurations definition
CREATE TABLE dbo.configurations (
     [type] varchar(100) NOT NULL,
     configs nvarchar(MAX),
     CONSTRAINT PK_configurations PRIMARY KEY ([type])
) ;

-- dbo.employee_profile definition
CREATE TABLE dbo.employee_profile (
      id varchar(128) NOT NULL,
      mail varchar(1024),
      display_name varchar(1024),
      about_me varchar(MAX),
      job_title varchar(1024),
      company_name varchar(1024),
      department varchar(1024),
      office_location varchar(1024),
      city varchar(1024),
      state varchar(1024),
      country varchar(1024),
      profile_picture varchar(MAX),
      reports_to varchar(1024),
      version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
      CONSTRAINT PK_employee_profile PRIMARY KEY (id,version)
) ;
CREATE NONCLUSTERED INDEX employee_profile_mail_ndx ON dbo.employee_profile (mail ASC);
CREATE NONCLUSTERED INDEX employee_profile_mail_version_ndx ON dbo.employee_profile (mail ASC, version ASC);

-- dbo.employee_interests definition
CREATE TABLE dbo.employee_interests (
     id bigint IDENTITY(1,1) NOT NULL,
     employee_profile_id varchar(128) NOT NULL,
     interest varchar(MAX),
     employee_profile_version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
     CONSTRAINT PK_employee_interests PRIMARY KEY (id,employee_profile_version),
     CONSTRAINT FK_employee_interests_employee_profile FOREIGN KEY (employee_profile_id, employee_profile_version) REFERENCES dbo.employee_profile(id, version) ON DELETE CASCADE
) ;
CREATE NONCLUSTERED INDEX employee_interests_employee_profile_id_ndx ON dbo.employee_interests (employee_profile_id ASC);
CREATE NONCLUSTERED INDEX employee_interests_employee_profile_id_version_ndx ON dbo.employee_interests (employee_profile_id ASC, employee_profile_version ASC);

-- dbo.employee_past_projects definition
CREATE TABLE dbo.employee_past_projects (
     id bigint IDENTITY(1,1) NOT NULL,
     employee_profile_id varchar(128) NOT NULL,
     past_project varchar(MAX) NOT NULL,
     employee_profile_version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
     CONSTRAINT PK_employee_past_projects PRIMARY KEY (id,employee_profile_version),
     CONSTRAINT FK_employee_past_projects_employee_profile FOREIGN KEY (employee_profile_id, employee_profile_version) REFERENCES dbo.employee_profile(id, version) ON DELETE CASCADE
) ;
CREATE NONCLUSTERED INDEX employee_past_projects_employee_profile_id_ndx ON dbo.employee_past_projects (employee_profile_id ASC);
CREATE NONCLUSTERED INDEX employee_past_projects_employee_profile_id_version_ndx ON dbo.employee_past_projects (employee_profile_id ASC, employee_profile_version ASC);

-- dbo.employee_responsibilities definition
CREATE TABLE dbo.employee_responsibilities (
    id bigint IDENTITY(1,1) NOT NULL,
    employee_profile_id varchar(128) NOT NULL,
    responsibility varchar(MAX) NOT NULL,
    employee_profile_version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
    CONSTRAINT PK_employee_responsibilities PRIMARY KEY (id,employee_profile_version),
    CONSTRAINT FK_employee_responsibilities_employee_profile FOREIGN KEY (employee_profile_id, employee_profile_version) REFERENCES dbo.employee_profile(id, version) ON DELETE CASCADE
) ;
CREATE NONCLUSTERED INDEX employee_responsibilities_employee_profile_id_ndx ON dbo.employee_responsibilities (employee_profile_id ASC);
CREATE NONCLUSTERED INDEX employee_responsibilities_employee_profile_id_version_ndx ON dbo.employee_responsibilities (employee_profile_id ASC, employee_profile_version ASC);

-- dbo.employee_schools definition
CREATE TABLE dbo.employee_schools (
   id bigint IDENTITY(1,1) NOT NULL,
   employee_profile_id varchar(128) NOT NULL,
   school varchar(1024) NOT NULL,
   employee_profile_version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
   CONSTRAINT PK_employee_schools PRIMARY KEY (id,employee_profile_version),
   CONSTRAINT FK_employee_schools_employee_profile FOREIGN KEY (employee_profile_id, employee_profile_version) REFERENCES dbo.employee_profile(id, version) ON DELETE CASCADE
) ;
CREATE NONCLUSTERED INDEX employee_schools_employee_profile_id_ndx ON dbo.employee_schools (employee_profile_id ASC);
CREATE NONCLUSTERED INDEX employee_schools_employee_profile_id_version_ndx ON dbo.employee_schools (employee_profile_id ASC, employee_profile_version ASC);

-- dbo.employee_skills definition
CREATE TABLE dbo.employee_skills (
      id bigint IDENTITY(1,1) NOT NULL,
      employee_profile_id varchar(128) NOT NULL,
      skill varchar(MAX) NOT NULL,
      employee_profile_version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
      CONSTRAINT PK_employee_skills PRIMARY KEY (id,employee_profile_version),
      CONSTRAINT FK_employee_skills_employee_profile FOREIGN KEY (employee_profile_id, employee_profile_version) REFERENCES dbo.employee_profile(id, version) ON DELETE CASCADE
) ;
CREATE NONCLUSTERED INDEX employee_skills_employee_profile_id_ndx ON dbo.employee_skills (employee_profile_id ASC);
CREATE NONCLUSTERED INDEX employee_skills_employee_profile_version_id_ndx ON dbo.employee_skills (employee_profile_id ASC, employee_profile_version ASC);

-- dbo.search_settings definition
CREATE TABLE dbo.search_settings (
      user_email varchar(255) NOT NULL,
      data_sources varchar(4000),
      freshness int NOT NULL,
      volume int NOT NULL,
      relevance_score int NOT NULL,
      freshness_enabled bit DEFAULT 1 NOT NULL,
      volume_enabled bit DEFAULT 1 NOT NULL,
      relevance_score_enabled bit DEFAULT 1 NOT NULL,
      freshness_begin_date varchar(128),
      freshness_begin_date_enabled bit DEFAULT 1 NOT NULL,
      included_email_domains varchar(MAX),
      included_email_domains_enabled bit DEFAULT 1 NOT NULL,
      excluded_email_domains varchar(MAX),
      excluded_email_domains_enabled bit DEFAULT 1 NOT NULL,
      use_received_emails_content bit DEFAULT 1 NOT NULL,
      CONSTRAINT PK__search_s__B0FBA2138274AB6A PRIMARY KEY (user_email)
) ;

-- dbo.team_info definition
CREATE TABLE dbo.team_info (
    user_email varchar(255) NOT NULL,
    name varchar(1024),
    description varchar(MAX),
    CONSTRAINT PK_team_info PRIMARY KEY (user_email)
) ;

-- dbo.team_members definition
CREATE TABLE dbo.team_members (
   id bigint IDENTITY(1,1) NOT NULL,
   member_id varchar(128),
   member_email varchar(1024),
   member_name varchar(1024),
   owner_email varchar(255),
   CONSTRAINT UK_team_members_owner_email_member_id UNIQUE (owner_email,member_id),
   CONSTRAINT PK_team_members PRIMARY KEY (id)
) ;
CREATE NONCLUSTERED INDEX team_member_owner_email_ndx ON dbo.team_members (owner_email ASC);

-- dbo.team_member_skills definition
CREATE TABLE dbo.team_member_skills (
     id bigint IDENTITY(1,1) NOT NULL,
     team_member_id bigint NOT NULL,
     skill varchar(MAX) NOT NULL,
     CONSTRAINT PK_team_member_skills PRIMARY KEY (id),
     CONSTRAINT FK_team_member_skills_team_member FOREIGN KEY (team_member_id) REFERENCES dbo.team_members(id) ON DELETE CASCADE
) ;
CREATE NONCLUSTERED INDEX team_member_skills_team_member_id_ndx ON dbo.team_member_skills (team_member_id ASC);
