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
    ,SourceSystem
	)
AS (
	SELECT full_name 
		,client_email
		,job_title
		,department
		,client_city
		,client_state
		,owner_id
        ,source_system
	FROM dbo.crm_contact
	WHERE source_system = 'Dynamics 365'
	),

-- Users who are contact owners
Users (
	Name
    ,UserId
	,Email
	,City
	,State
    ,SourceSystem
	)
AS (
	SELECT full_name
        ,user_id
		,primary_email
		,location_city
		,location_state
        ,source_system
	FROM dbo.crm_user u
	WHERE EXISTS (
			SELECT ContactOwner
			FROM Contacts C
			WHERE u.user_id = C.ContactOwner
			) AND
			u.source_system = 'Dynamics 365'
	),


-- Collect individual interactions w/ sentiment
Interactions (
	Id
	,PersonA
	,PersonB
	,OWNER
	,Contact
	,Sentiment
    ,SentimentScore
    ,PositiveScore
    ,NeutralScore
    ,NegativeScore
	,Type
    ,SourceSystem
	)
AS (
	SELECT O.InteractionId
		,O.Sender
		,O.Recipient
		,Users.Email
		,Contacts.ContactEmail
		,general_sentiment
        ,(S.pos_score + S.neutral_score*0.5) sentiment_score
        ,S.pos_score
        ,S.neutral_score
        ,S.negative_score
		,O.SourceType
        ,Contacts.SourceSystem
	FROM [dbo].[vInteractions_one_to_one] O, Users, Contacts, [dbo].[vConversationSentiment] S
	WHERE S.interaction_id = O.InteractionId AND (Users.Email = O.Sender AND Contacts.ContactEmail = O.Recipient) 
    OR (Users.Email = O.Recipient AND Contacts.ContactEmail = O.Sender)
	
),

Counts (
	Owner
	,Contact
    ,OverallSentiment
    ,SentimentScore
    ,PositiveScore
    ,NeutralScore
    ,NegativeScore
    ,SentimentCount
    ,SourceSystem
	)
AS (
	SELECT Owner
		,Contact
        ,Sentiment
        ,AVG(SentimentScore)
        ,AVG(PositiveScore)
        ,AVG(NeutralScore)
        ,AVG(NegativeScore)
		,COUNT(*)
        ,SourceSystem
	FROM Interactions
	GROUP BY
		Owner
		,Contact
        ,Sentiment
        ,SentimentScore
        ,SourceSystem
	),

-- Rank sentiment scores
Rankings (
    Owner
    ,Contact
    ,OverallSentiment
    ,SentimentScore
    ,PositiveScore
    ,NeutralScore
    ,NegativeScore
    ,SentimentCount
    ,SourceSystem
    ,SentimentRank
    )
AS (
    SELECT *,
    DENSE_RANK() OVER(
        PARTITION BY Owner, Contact, SourceSystem ORDER BY SentimentCount DESC
        ) AS SentimentRank
    FROM Counts
),

Top_Ranks AS (
    SELECT * FROM Rankings
    WHERE SentimentRank = 1
)

-- Fully joined view
SELECT U.full_name Owner
	,U.primary_email ownerEmail
    ,U.location_city city
	,U.location_state state
	,A.account_name client
    ,A.website_url website
    ,A.number_employees numberEmployees
    ,A.revenue revenue
	,C.full_name clientContact
    ,C.client_email clientEmail
	,C.client_city clientCity
	,C.client_state clientState
	,C.department clientDept
	,C.job_title clientTitle
	,R.OverallSentiment
    ,R.SentimentScore
    ,R.PositiveScore
    ,R.NeutralScore
    ,R.NegativeScore
	,R.SentimentCount InteractionCount
	,C.source_system
FROM Top_Ranks R
LEFT JOIN dbo.crm_contact C ON C.client_email = R.Contact AND C.source_system = R.SourceSystem
LEFT JOIN dbo.crm_user U ON U.primary_email = R.Owner AND U.source_system = R.SourceSystem
LEFT JOIN dbo.crm_account A On A.primary_contact = C.client_id;

GO
