DROP VIEW IF EXISTS dbo.v_ConversationSentiment_CRM ;
GO

CREATE VIEW dbo.v_ConversationSentiment_CRM
AS 

-- Contacts data
WITH Contacts (
	ContactName
	,ContactEmail
	,ContactTitle
	,ContactDepartment
	,ContactCity
	,ContactState
	,ContactOwner
	)
AS (
	SELECT full_name 
		,client_email
		,job_title
		,department
		,client_city
		,client_state
		,owner_id
	FROM dbo.crm_contact
	),

-- Users who are contact owners
Users (
	Name
    ,UserId
	,Email
	,City
	,State
	)
AS (
	SELECT full_name
        ,user_id
		,primary_email
		,location_city
		,location_state
	FROM dbo.crm_user u
	WHERE EXISTS (
			SELECT ContactOwner
			FROM Contacts C
			WHERE u.user_id = C.ContactOwner
			)
	),


	-- Collect individual interactions w/ sentiment
Interactions (
	Id
	,PersonA
	,PersonB
	,OWNER
	,Contact
	,Sentiment    
	,Type
	)
AS (
	SELECT O.InteractionId
		,O.Sender
		,O.Recipient
		,Users.Email
		,Contacts.ContactEmail
		,general_sentiment
		,O.SourceType
	FROM [dbo].[vInteractions_one_to_one] O, Users, Contacts, [dbo].[vConversationSentiment] S
	WHERE S.interaction_id = O.InteractionId AND (Users.Email = O.Sender AND Contacts.ContactEmail = O.Recipient) 
    OR (Users.Email = O.Recipient AND Contacts.ContactEmail = O.Sender)
	
),
Counts (
	Owner
	,Contact
    ,OverallSentiment
    ,SentimentCount    
	)
AS (
	SELECT Owner
		,Contact
        ,Sentiment
		,COUNT(*)
	FROM Interactions
	GROUP BY
		Owner
		,Contact
        ,Sentiment
	),

-- Rank sentiment scores
Rankings (
    Owner,
    Contact,
    OverallSentiment,
    SentimentCount,
    SentimentRank
    )
AS (
    SELECT *,
    DENSE_RANK() OVER(
        PARTITION BY Owner, Contact ORDER BY SentimentCount DESC
        ) AS SentimentRank
    FROM Counts
),

Top_Ranks AS (
    SELECT * FROM Rankings
    WHERE SentimentRank = 1
)

-- Fully joined view
SELECT U.full_name Owner
	,U.location_city city
	,U.location_state State
	,A.account_name client
    ,A.website_url website
    ,A.number_employees numberEmployees
    ,A.revenue revenue
	,C.full_name clientContact
	,C.client_city clientCity
	,C.client_state clientState
	,C.department clientDept
	,C.job_title clientTitle
	,R.OverallSentiment
FROM Top_Ranks R
LEFT JOIN dbo.crm_contact C ON C.client_email = R.Contact
LEFT JOIN dbo.crm_user U ON U.primary_email = R.Owner
LEFT JOIN dbo.crm_account A On A.primary_contact = C.client_id;

GO
