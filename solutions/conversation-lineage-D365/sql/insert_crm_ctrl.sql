-------------------------------------------Populate dbo.crm_ctrl table---------------------------------------------

INSERT INTO dbo.crm_ctrl (SourceSystem, SourceSchemaName, SourceTableName, SourceLastModifiedColumn, SourceMergeKey, SinkSchemaName, SinkTableName, SinkMergeKey, IsActive)
    SELECT 'Dynamics 365', 'Dataverse', 'Account', 'modifiedon', 'Id', 'dbo', 'crm_account', 'source_id', 1
    UNION 
    SELECT 'Dynamics 365', 'Dataverse', 'contact', 'modifiedon', 'Id', 'dbo', 'crm_contact', 'source_id', 1
    UNION
    SELECT 'Dynamics 365', 'Dataverse', 'systemuser', 'modifiedon', 'Id', 'dbo', 'crm_user', 'source_id', 1

