--------------------------- Control Table for CRM Copy Process -------------------------

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID ('dbo.crm_ctrl') IS NOT NULL
DROP TABLE [dbo].[crm_ctrl]

CREATE TABLE [dbo].[crm_ctrl]
(
	[ProcessId] INT IDENTITY(1,1),
    [SourceSystem] [NVARCHAR](128)  NOT NULL,
    [SourceSchemaName] [NVARCHAR](36)  NOT NULL,
    [SourceTableName] [NVARCHAR](100)  NOT NULL,
    [SourceLastModifiedColumn] [NVARCHAR](36) NOT NULL,
    [SourceMergeKey] [NVARCHAR](36) NOT NULL,
    [SinkSchemaName] [NVARCHAR](36)  NOT NULL,
    [SinkTableName] [NVARCHAR](100)  NOT NULL,
    [SinkMergeKey] [NVARCHAR](36) NOT NULL,
    [IsActive] BIT NOT NULL,
    [LastUpdate] [DATETIME2](3) NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO

---------------------------- Table for CRM Table Mappings -----------------------------

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID ('dbo.crm_mapping') IS NOT NULL
DROP TABLE [dbo].[crm_mapping]

CREATE TABLE [dbo].[crm_mapping]
(
	[MappingId] INT IDENTITY(1,1) NOT NULL,
    [ProcessId] [INT],
    [SourceColumn] [NVARCHAR](100)  NULL,
    [SinkColumn] [NVARCHAR](100)  NULL,
    [Type] [NVARCHAR](36)  NULL,
    [PhysicalType] [NVARCHAR](36)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO
