create TABLE hr_data_ingestion_state (
    id bigint IDENTITY(1,1) NOT NULL,
    phase varchar(64),
    requester varchar(255),
    start_time varchar(32),
    error_message nvarchar(MAX),
    error_stack_trace nvarchar(MAX),
    logs_correlation_id varchar(40),
    CONSTRAINT PK_hr_data_ingestion_state PRIMARY KEY (id DESC)
);
