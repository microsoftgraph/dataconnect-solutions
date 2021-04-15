alter table dbo.employee_profile add manager_email varchar(1024);

alter table dbo.airtable_employee_profile add manager_email varchar(1024);
alter table dbo.airtable_employee_profile add linkedin_profile varchar(1024);

-- keep this entity in sync with the default values inserted in db.py during deployment
create TABLE ingestion_mode_switch_state (
    id bigint IDENTITY(1,1) NOT NULL,
    ingestion_mode varchar(32),
    phase varchar(64),
    paused BIT DEFAULT 0 NOT NULL,
    requester varchar(255),
    start_time varchar(32),
    error_message nvarchar(MAX),
    error_stack_trace nvarchar(MAX),
    logs_correlation_id varchar(40),
    CONSTRAINT PK_application_status PRIMARY KEY (id DESC)
);

create TABLE ingestion_mode_switch_audit (
    id bigint IDENTITY(1,1) NOT NULL,
    request_type varchar(32) NOT NULL, -- switch/resume/retry etc.
    target_ingestion_mode varchar(32),
    requester varchar(255) NOT NULL,
    request_time varchar(32) NOT NULL,
    request_resolution varchar(32) NOT NULL, -- accepted/rejected etc
    logs_correlation_id varchar(40),
    message nvarchar(MAX),
    CONSTRAINT PK_ingestion_mode_switch_audit PRIMARY KEY (id)
);
CREATE NONCLUSTERED INDEX ingestion_mode_switch_audit_target_ingestion_mode_ndx ON dbo.ingestion_mode_switch_audit (target_ingestion_mode ASC);
CREATE NONCLUSTERED INDEX ingestion_mode_switch_audit_request_type_ndx ON dbo.ingestion_mode_switch_audit (request_type ASC);
CREATE NONCLUSTERED INDEX ingestion_mode_switch_audit_request_resolution_ndx ON dbo.ingestion_mode_switch_audit (request_resolution ASC);
