CREATE VIEW dbo.v_ConversationSentiment_D365
AS 

-- Contacts data
WITH Contacts (
	ContactName
	,ContactEmail
	,ContactTitle
	,ContactCity
	,ContactState
	,ContactOwner
	)
AS (
	SELECT fullname 
		,emailaddress1
		,jobtitle
		,address1_city
		,address1_stateorprovince
		,ownerid
	FROM Dataverse.contact
	),

-- Users who are contact owners
Users (
	Name
    ,UserId
	,Email
	,Company
	,City
	,STATE
	)
AS (
	SELECT fullname
        ,systemuserid
		,internalemailaddress
		,businessunitid
		,address1_city
		,address1_stateorprovince
	FROM Dataverse.systemuser u
	WHERE EXISTS (
			SELECT ContactOwner
			FROM Contacts C
			WHERE u.systemuserid = C.ContactOwner
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
	FROM [dbo].[vInteractions_one_to_one] O
	LEFT JOIN [dbo].[vConversationSentiment] S ON S.interaction_id = O.InteractionId
    LEFT JOIN Users ON (users.Email = O.Sender OR users.Email = O.Recipient)
    LEFT JOIN Contacts ON (Contacts.ContactEmail = O.Recipient OR Contacts.ContactEmail = O.Sender)
	
),

-- Get Counts
Counts (
	PersonA
	,PersonB
	,OverallSentiment
	,OWNER
	,Contact
    ,SentimentCount    
	)
AS (
	SELECT PersonA
		,PersonB
		,Sentiment
		,OWNER
		,Contact
		,COUNT(*) Count   
	FROM Interactions
	GROUP BY PersonA
		,PersonB
		,Sentiment
		,OWNER
		,Contact
	),

-- Calculate Dense Rank
DenseRank (
	PersonA
	,PersonB
	,OverallSentiment
	,OWNER
	,Contact
    ,Rankity
	)
AS (
	SELECT PersonA
		,PersonB
		,OverallSentiment
		,OWNER
		,Contact
		,DENSE_RANK() OVER (
			PARTITION BY SentimentCount ORDER BY SentimentCount DESC
			)        
	FROM Counts
),

-- Rank sentiment scores
Rankings (
	PersonA
	,PersonB
	,OverallSentiment
	,OWNER
	,Contact
    ,Rankity
	)
AS (
	SELECT PersonA
		,PersonB
		,OverallSentiment
		,OWNER
		,Contact
        ,Rankity
	FROM DenseRank
	WHERE Rankity = 1
	)
SELECT U.fullname OWNER
	,U.businessunitid company
	,U.address1_city city
	,U.address1_stateorprovince STATE
	,C.fullname client
	,C.address1_city clientCity
	,C.address1_stateorprovince clientState
	,C.department clientDept
	,C.jobtitle clientTitle
	,R.OverallSentiment
FROM Rankings R
LEFT JOIN Dataverse.contact C ON C.emailaddress1 = R.Contact
LEFT JOIN Dataverse.systemuser U ON U.internalemailaddress = R.OWNER