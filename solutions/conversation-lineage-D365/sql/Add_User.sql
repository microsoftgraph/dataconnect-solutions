-- Create the Azure AD user in the Synapse Dedicated SQL DB
CREATE USER [user@somedomain.com]
FROM EXTERNAL PROVIDER;

-- Example for how to set up a user with a certain role
EXEC sp_addrolemember 'db_datareader', 'user@somedomain.com';
