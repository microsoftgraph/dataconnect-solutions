create TABLE dbo.hr_data_employee_profile (
    mail varchar(1024),
    version datetime2(6) DEFAULT '1753-01-01 00:00:00.000000' NOT NULL,
    name varchar(1024),
    available_starting_from varchar(16),
    [role] varchar(1024),
    employee_type varchar(1024),
    current_engagement varchar(1024),
    department varchar(1024),
    company_name varchar(1024),
    manager_name varchar(1024),
    manager_email varchar(1024),
    country varchar(1024),
    state varchar(1024),
    city varchar(1024),
    location varchar(1024),
    office_location varchar(1024),
    linkedin_profile varchar(1024),
    CONSTRAINT PK_hr_data_employee_profile PRIMARY KEY (mail,version)
) ;
create NONCLUSTERED INDEX hr_data_employee_profile_mail_ndx ON dbo.hr_data_employee_profile (mail ASC);

DROP TABLE IF EXISTS dbo.airtable_employee_profile;
