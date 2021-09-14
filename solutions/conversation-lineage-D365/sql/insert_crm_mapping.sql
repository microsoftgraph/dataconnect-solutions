
-------------------------------------------Populate dbo.crm_mapping table---------------------------------------------
INSERT INTO dbo.crm_mapping (ProcessId, SourceColumn, SinkColumn, Type, PhysicalType)
-- Insert CRM Account Mappings
    SELECT 45, 'name', 'account_name', 'String', 'nvarchar' 
    UNION 
    SELECT 45, 'primarycontactid', 'primary_contact', 'String', 'nvarchar'
    UNION 
    SELECT 45, 'address1_city', 'city', 'String', 'nvarchar'
    UNION 
    SELECT 45, 'address1_stateorprovince', 'state', 'String', 'nvarchar'
    UNION 
    SELECT 45, 'websiteurl', 'website_url', 'String', 'nvarchar'
    UNION 
    SELECT 45, 'numberofemployees', 'number_employees', 'Int64', 'bigint'
    UNION 
    SELECT 45, 'revenue', 'revenue', 'Decimal', 'decimal'


-- Insert CRM Contact Mappings
    UNION 
    SELECT 105, 'emailaddress1', 'client_email', 'String', 'nvarchar'
    UNION 
    SELECT 105, 'fullname', 'full_name', 'String', 'nvarchar'
    UNION 
    SELECT 105, 'jobtitle', 'job_title', 'String', 'nvarchar'
    UNION 
    SELECT 105, 'department', 'department', 'String', 'nvarchar'
    UNION 
    SELECT 105, 'address1_city', 'client_city', 'String', 'nvarchar'
    UNION 
    SELECT 105, 'address1_stateorprovince', 'client_state', 'String', 'nvarchar'
    UNION 
    SELECT 105, 'ownerid', 'owner_id', 'String', 'nvarchar'
    UNION 
    SELECT 105, 'contactid', 'client_id', 'String', 'nvarchar'

-- Insert CRM User Mappings
    UNION 
    SELECT 165, 'systemuserid', 'user_id', 'String', 'nvarchar'
    UNION 
    SELECT 165, 'azureactivedirectoryobjectid', 'aad_oid', 'String', 'nvarchar'
    UNION 
    SELECT 165, 'fullname', 'full_name', 'String', 'nvarchar'
    UNION 
    SELECT 165, 'internalemailaddress', 'primary_email', 'String', 'nvarchar'
    UNION 
    SELECT 165, 'title', 'title', 'String', 'nvarchar'
    UNION 
    SELECT 165, 'address1_city', 'location_city', 'String', 'nvarchar'
    UNION 
    SELECT 165, 'address1_stateorprovince', 'location_state', 'String', 'nvarchar'
