CREATE TABLE inferred_roles
(
    id                          varchar(1024),
    email                       varchar(1024) NOT NULL,
    doc_count_0                 int,
    doc_count_1                 int,
    doc_count_2                 int,
    frequent_role_0             varchar(256),
    frequent_role_1             varchar(256),
    frequent_role_2             varchar(256),
    highest_score_role_0        varchar(256),
    highest_score_role_1        varchar(256),
    highest_score_role_2        varchar(256),
    highest_score_score_0       float,
    highest_score_score_1       float,
    highest_score_score_2       float,
    role_proposal_0             varchar(256),
    role_proposal_1             varchar(256),
    role_proposal_2             varchar(256),
    score_proposal_0            float,
    score_proposal_1            float,
    score_proposal_2            float,
    total_docs                  int NOT NULL,
    version                     datetime2(6) NOT NULL DEFAULT '1753-01-01 00:00:00.000000',
    CONSTRAINT PK_inferred_roles PRIMARY KEY (id,version)
);

CREATE INDEX inferred_roles_from_ndx ON inferred_roles(email);

EXEC sp_rename 'search_settings.data_sources', 'search_criteria', 'COLUMN';
GO

alter table dbo.search_settings add data_sources varchar(4000);
alter table dbo.search_settings add search_results_filters varchar(4000);
