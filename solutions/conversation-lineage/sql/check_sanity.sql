-- ------------------------------------------------------------
-- Check that there is no email duplication
-- ------------------------------------------------------------
DECLARE @CountEmails int;
DECLARE @DistinctCountEmails int;

SELECT @CountEmails=COUNT(InternetMessageId)
FROM [dbo].[augmented_emails]

SELECT DISTINCT @DistinctCountEmails=COUNT(InternetMessageId)
FROM [dbo].[augmented_emails];

PRINT '---------------------------------------------';
PRINT 'These two should be equal: ' + CAST(@CountEmails AS VARCHAR) + ' == ' + CAST(@DistinctCountEmails AS VARCHAR);
PRINT '---------------------------------------------';

-- ------------------------------------------------------------
-- Check that
-- ------------------------------------------------------------

