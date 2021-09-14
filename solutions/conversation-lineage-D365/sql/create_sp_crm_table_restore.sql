CREATE PROCEDURE dbo.sp_crm_table_restore  @ProcessId INT, @BackupTablePrefix NVARCHAR(36),
                     @ControlSchemaName NVARCHAR(36), @ControlTableName NVARCHAR(100), @LastUpdate DATETIME2(3)
AS 

DECLARE @SinkSchemaName NVARCHAR(36);
DECLARE @SinkTableName NVARCHAR(100);

DECLARE @sql NVARCHAR(MAX) = '
SELECT 
    SinkSchemaName
    ,SinkTableName
INTO #SinkTempTbl
FROM ' + @ControlSchemaName + '.' + @ControlTableName + ' 
WHERE ProcessId = ' + convert(varchar(10), @ProcessId)

EXEC sp_executesql @sql

SELECT @SinkSchemaName=[SinkSchemaName]
        ,@SinkTableName=[SinkTableName]
FROM #SinkTempTbl

-- Check that both the primary table and backup table exist before dropping and renaming.
DECLARE @RenameSQL NVARCHAR(MAX) = '
IF OBJECT_ID (' + char(39) + @SinkSchemaName + '.' + @SinkTableName + char(39) + ') IS NOT NULL 
AND OBJECT_ID (' + char(39) + @SinkSchemaName + '.' + @BackupTablePrefix + @SinkTableName + char(39) + ') IS NOT NULL 
DROP TABLE ' + @SinkSchemaName + '.' + @SinkTableName + ' 
RENAME OBJECT ' + @SinkSchemaName + '.' + @BackupTablePrefix + @SinkTableName + ' TO ' + @SinkTableName

EXEC sp_executesql @RenameSQL;

-- SET LastUpdate back to previous watermark value before rollback
DECLARE @UpdateWatermarkSQL NVARCHAR(MAX);
SET @UpdateWatermarkSQL = 'UPDATE ' + @ControlSchemaName + '.' + @ControlTableName +  ' 
SET LastUpdate = ' + '''' + convert(varchar(25), @LastUpdate, 121) + '''' + 
    ' WHERE ProcessId = ' + convert(varchar(10), @ProcessId);

-- Execute the UPDATE statement from above
EXEC sp_executesql @UpdateWatermarkSQL;
