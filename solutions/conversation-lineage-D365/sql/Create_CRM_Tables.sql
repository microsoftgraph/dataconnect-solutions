--------------------------- Table to hold system user data ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[crm_user]
(
	[user_id] [nvarchar](36)  NULL,
    [aad_oid] [nvarchar](36)  NULL,
    [full_name] [nvarchar](200)  NULL,
    [primary_email] [varchar](100)  NULL,
    [title] [nvarchar](128)  NULL,
    [location_city] [nvarchar](128)  NULL,
    [location_state] [nvarchar](128)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO


---------------------------- Table to hold client contacts ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[crm_contact]
(
	[client_id] [nvarchar](36)  NULL,
    [client_email] [varchar](100)  NULL,
    [full_name] [nvarchar](160)  NULL,
    [job_title] [nvarchar](100)  NULL,
    [department] [nvarchar](100)  NULL,
    [client_city] [nvarchar](80)  NULL,
    [client_state] [nvarchar](50)  NULL,
    [owner_id] [nvarchar](36)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO


---------------------------- Table to hold client account details ----------------------------


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[crm_account]
(
	[account_name] [nvarchar](160)  NULL,
    [primary_contact] [nvarchar](36)  NULL,
    [city] [nvarchar](80)  NULL,
    [state] [nvarchar](50)  NULL,
    [website_url] [nvarchar](200)  NULL,
    [number_employees] [bigint] NULL,
    [revenue] [decimal](38,2)  NULL
)
WITH
(
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX
)
GO
