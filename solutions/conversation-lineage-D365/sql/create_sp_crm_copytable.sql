-- Stored Procedure for handling flow of data from source CRM tables to landing tables

CREATE PROCEDURE dbo.sp_crm_copytable  @ProcessId INT, @ControlSchemaName NVARCHAR(36), @ControlTableName NVARCHAR(100), 
                @MappingSchemaName NVARCHAR(36), @MappingTableName NVARCHAR(100), @PipelineRunId NVARCHAR(36)
AS 

DECLARE @SourceSystem NVARCHAR(128);
DECLARE @SourceSchemaName NVARCHAR(36);
DECLARE @SourceTableName NVARCHAR(100);
DECLARE @SourceLastModifiedColumn NVARCHAR(36);
DECLARE @SourceMergeKey NVARCHAR(36);
DECLARE @SinkSchemaName NVARCHAR(36) ;
DECLARE @SinkTableName NVARCHAR(100);
DECLARE @SinkMergeKey NVARCHAR(36);
DECLARE @SourceColumnList NVARCHAR(MAX);
DECLARE @SinkColumnList NVARCHAR(MAX);
DECLARE @UpdateClause NVARCHAR(MAX);
DECLARE @LastUpdate DATETIME2(3);

DECLARE @NewWatermark DATETIME2(3) = SYSDATETIME();

-- Query control and mapping tables with ProcessId to obtain table/column mappings.
-- Also, store result as temp table so that the above declared variables can be set by querying the resulting temp table.
DECLARE @MappingSQL NVARCHAR(MAX);
SET @MappingSQL = '
WITH InitialMappings AS (
    SELECT c.ProcessId
        ,c.SourceSystem
        ,c.SourceSchemaName
        ,c.SourceTableName
		    ,c.SourceLastModifiedColumn
		    ,c.SourceMergeKey
        ,c.SinkSchemaName
        ,c.SinkTableName
		    ,c.SinkMergeKey
        ,c.LastUpdate
        ,m.SourceColumn
        ,m.SinkColumn
    FROM ' + @ControlSchemaName + '.' + @ControlTableName + ' c
    INNER JOIN ' + @MappingSchemaName + '.' + @MappingTableName + ' m ON c.ProcessId = m.ProcessId 
    WHERE c.IsActive = 1
    AND c.ProcessId = ' + convert(varchar(10), @ProcessId) + '
),  UpdateMappings AS (
    SELECT ProcessId
        ,SourceSystem
        ,SourceSchemaName
        ,SourceTableName
		,SourceLastModifiedColumn
		,SourceMergeKey
        ,SinkSchemaName
        ,SinkTableName
		,SinkMergeKey
        ,LastUpdate
        ,concat(' + char(39) + 'src.' + char(39) + ',SourceColumn) AS SourceColumn
        ,SinkColumn
        ,CONCAT(' + char(39) + 'tgt.' + char(39) + ',SinkColumn, ' + char(39) + ' = ' + char(39) + ', ' + char(39) + 'src.' + char(39) + ', SourceColumn) AS ColumnMappings        
    FROM InitialMappings
)
SELECT ProcessId
        ,SourceSystem
        ,SourceSchemaName
        ,SourceTableName
        ,SourceLastModifiedColumn
        ,SourceMergeKey
        ,SinkSchemaName
        ,SinkTableName
        ,SinkMergeKey
        ,LastUpdate
    ,STRING_AGG(SourceColumn, ' + char(39) + ', ' + char(39) + ') AS [SourceColumnList]
    ,STRING_AGG(SinkColumn, ' + char(39) + ', ' + char(39) + ') AS [SinkColumnList]
    ,STRING_AGG(ColumnMappings, ' + char(39) + ', ' + char(39) + ') AS [UpdateClause]
INTO #MappingsTemp
FROM UpdateMappings
GROUP BY ProcessId, SourceSystem, SourceSchemaName, SourceTableName, SourceLastModifiedColumn, SourceMergeKey, SinkSchemaName, SinkTableName, SinkMergeKey, LastUpdate
'

-- Execute above SQL
EXEC sp_executesql @MappingSQL

-- Use temp table to set all variables based on record returned from control tables
SELECT  @SourceSystem=[SourceSystem] 
        ,@SourceSchemaName=[SourceSchemaName] 
        ,@SourceTableName=[SourceTableName] 
        ,@SourceLastModifiedColumn=[SourceLastModifiedColumn] 
        ,@SourceMergeKey=[SourceMergeKey] 
        ,@SinkSchemaName=[SinkSchemaName] 
        ,@SinkTableName=[SinkTableName] 
        ,@SinkMergeKey=[SinkMergeKey] 
        ,@LastUpdate=[LastUpdate] 
        ,@SourceColumnList=[SourceColumnList]
        ,@SinkColumnList=[SinkColumnList]
        ,@UpdateClause=[UpdateClause]
FROM #MappingsTemp

-- Creating Source and Sink objects (combine schema and table names) since these will be reused frequently
DECLARE @SourceObject NVARCHAR(100);
SET @SourceObject = @SourceSchemaName + '.' + @SourceTableName;

DECLARE @SinkObject NVARCHAR(100); 
SET @SinkObject = @SinkSchemaName + '.' + @SinkTableName;

-- Adding Where clause for Incremental updates. If the @LastUpdate column is Null, then it will just process all records from the source table.
DECLARE @WhereClause NVARCHAR(MAX);
SET @WhereClause = CASE WHEN @LastUpdate IS NULL OR @LastUpdate = '' 
                        THEN '1 = 1' 
                        ELSE ' src.' + @SourceLastModifiedColumn + ' >= ' + '''' + convert(varchar(25), @LastUpdate, 121) + ''''
                   END

-- Generate UPDATE statement for records that match in Source and Landing CRM tables
DECLARE @UpdateSQL NVARCHAR(MAX);
SET @UpdateSQL = 'UPDATE tgt 
SET ' + @UpdateClause + 
    ', tgt.modified = ' + '''' + convert(varchar(25), getdate(), 121) + '''' + 
    ', tgt.pipeline_run_id = ' + '''' + @PipelineRunId + '''' + 
'FROM ' + @SinkObject + ' tgt 
INNER JOIN ' + @SourceObject + ' src 
    ON tgt.' + @SinkMergeKey + ' = src.' + @SourceMergeKey + ' AND tgt.source_system = ' + '''' + @SourceSystem + '''' + 
'WHERE ' + @WhereClause

-- Execute the UPDATE statement from above
EXEC sp_executesql @UpdateSQL;

-- Generate INSERT statement to copy source CRM data into landing table
DECLARE @InsertSQL NVARCHAR(MAX);
SET @InsertSQL = 'INSERT INTO ' + @SinkObject + ' (' + @SinkColumnList + ', source_system, source_id, pipeline_run_id, created, modified)
SELECT ' + @SourceColumnList + ', ' + '''' + @SourceSystem + '''' + ' AS source_system, ' + 'src.' + @SourceMergeKey + ' AS source_id, ' + '''' + @PipelineRunId + '''' + ' AS pipeline_run_id, ' + '''' + convert(varchar(25), getdate(), 121) + '''' + ' AS created, ' + '''' + convert(varchar(25), getdate(), 121) + '''' + ' AS modified  
FROM ' + @SourceObject + ' src 
LEFT JOIN ' + @SinkObject + ' tgt ON src.' + @SourceMergeKey + ' = tgt.' + @SinkMergeKey + ' AND tgt.source_system = ' + '''' + @SourceSystem + '''' + 
' WHERE tgt.' + @SinkMergeKey + ' IS NULL AND '  + @WhereClause;

-- Execute the INSERT statement from above
EXEC sp_executesql @InsertSQL;

DECLARE @ControlObject NVARCHAR(50); 
SET @ControlObject = @ControlSchemaName + '.' + @ControlTableName;

-- Generate UPDATE statement for setting LastUpdate on Control table
DECLARE @UpdateWatermarkSQL NVARCHAR(MAX);
SET @UpdateWatermarkSQL = 'UPDATE ' + @ControlObject + ' 
SET LastUpdate = ' + '''' + convert(varchar(25), @NewWatermark, 121) + '''' + 
    ' WHERE ProcessId = ' + convert(varchar(10), @ProcessId);


-- Execute the UPDATE statement from above
EXEC sp_executesql @UpdateWatermarkSQL;
GO;
