-- this is placeholder, real SQL will be generated runtime depending on chosen deployment params ( authentication mode, etc.)
USE [conversation_lineage_demo]
GO

CREATE TABLE dbo.conversation_sentiment_info
(
    id                                                      VARCHAR(1024) NOT NULL,
    conversation_id                                         VARCHAR(1024) NOT NULL,
    sender_mail                                         VARCHAR(1024) NOT NULL,
    sender_name                                         VARCHAR(1024) NOT NULL,
    sender_domain                                         VARCHAR(1024) NOT NULL,
    general_sentiment                                         VARCHAR(1024) NOT NULL,
    pos_score                                         float,
    neutral_score                                         float,
    negative_score                                         float,
    CONSTRAINT PK_conversation_sentiment_info                            PRIMARY KEY (id)
);
CREATE NONCLUSTERED INDEX conversation_sentiment_info_convid_ndx ON dbo.conversation_sentiment_info (conversation_id ASC);
CREATE NONCLUSTERED INDEX conversation_sentiment_info_sendermail_ndx ON dbo.conversation_sentiment_info (sender_mail ASC);

CREATE TABLE dbo.conversation_entities_info
(
    id                                                      VARCHAR(1024) NOT NULL,
    conversation_id                                         VARCHAR(1024) NOT NULL,
    sender_mail                                         VARCHAR(1024) NOT NULL,
    sender_name                                         VARCHAR(1024) NOT NULL,
    sender_domain                                         VARCHAR(1024) NOT NULL,
    text,                                        VARCHAR(1024) NOT NULL,
    category                                         VARCHAR(1024) NOT NULL,
    score                                         float,
    CONSTRAINT PK_conversation_entities_info                 PRIMARY KEY (id)
);
CREATE NONCLUSTERED INDEX conversation_entities_info_convid_ndx ON dbo.conversation_entities_info (conversation_id ASC);
CREATE NONCLUSTERED INDEX conversation_entities_info_sendermail_ndx ON dbo.conversation_entities_info (sender_mail ASC);


GO
