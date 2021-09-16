CREATE PROCEDURE dbo.sp_crm_backup_table  @ProcessId INT, @ControlSchemaName NVARCHAR(36), @ControlTableName NVARCHAR(100),
                @BackupTablePrefix NVARCHAR(36)
AS 

DECLARE @SinkSchemaName NVARCHAR(36) ;
DECLARE @SinkTableName NVARCHAR(100);
DECLARE @sql NVARCHAR(MAX) = '
SELECT 
    SinkSchemaName
    ,SinkTableName
INTO #SinkTemp
FROM ' + @ControlSchemaName + '.' + @ControlTableName + ' 
WHERE ProcessId = ' + convert(varchar(10), @ProcessId)

EXEC sp_executesql @sql

SELECT @SinkSchemaName=[SinkSchemaName]
        ,@SinkTableName=[SinkTableName]
FROM #SinkTemp

DECLARE @CreateBackupSQL NVARCHAR(MAX) = '
IF OBJECT_ID (' + char(39) + @SinkSchemaName + '.' + @BackupTablePrefix + @SinkTableName + char(39) + ') IS NOT NULL 
BEGIN 
    DROP TABLE ' + @SinkSchemaName + '.' + @BackupTablePrefix + @SinkTableName + ' 
END 
SELECT * 
INTO '+ @SinkSchemaName + '.' + @BackupTablePrefix + @SinkTableName + ' 
FROM ' + @SinkSchemaName + '.' + @SinkTableName

EXEC sp_executesql @CreateBackupSQL
GO;
