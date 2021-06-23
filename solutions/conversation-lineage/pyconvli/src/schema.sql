-- this is placeholder, real SQL will be generated runtime depending on chosen deployment params ( authentication mode, etc.)
USE [conversation_lineage_demo]
GO
--drop index  conversation_sentiment_info_convid_ndx ON dbo.conversation_sentiment_info ;
--drop index  conversation_sentiment_info_sendermail_ndx ON dbo.conversation_sentiment_info ;
drop table if exists dbo.conversation_sentiment_info;

CREATE TABLE dbo.conversation_sentiment_info
(
    id                                                      VARCHAR(1024) NOT NULL,
    sent_date  datetime2(6)  NOT NULL,
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


--drop index  conversation_entities_info_convid_ndx ON dbo.conversation_entities_info ;
--drop index  conversation_entities_info_sendermail_ndx ON dbo.conversation_entities_info ;
drop table if exists dbo.conversation_entities_info;

CREATE TABLE dbo.conversation_entities_info
(
    id                                                      VARCHAR(1024) NOT NULL,
    sent_date  datetime2(6)  NOT NULL,
    conversation_id                                         VARCHAR(1024) NOT NULL,
    sender_mail                                         VARCHAR(1024) NOT NULL,
    sender_name                                         VARCHAR(1024) NOT NULL,
    sender_domain                                         VARCHAR(1024) NOT NULL,
    entity_text                                        VARCHAR(1024) NOT NULL,
    category                                         VARCHAR(1024) NOT NULL,
    score                                         float,
    CONSTRAINT PK_conversation_entities_info                 PRIMARY KEY (id)
);
CREATE NONCLUSTERED INDEX conversation_entities_info_convid_ndx ON dbo.conversation_entities_info (conversation_id ASC);
CREATE NONCLUSTERED INDEX conversation_entities_info_sendermail_ndx ON dbo.conversation_entities_info (sender_mail ASC);


--drop index  conversation_to_receiver_sentiment_info_convid_ndx ON dbo.conversation_to_receiver_sentiment_info ;
--drop index  conversation_to_receiver_sentiment_info_sendermail_ndx ON dbo.conversation_to_receiver_sentiment_info ;
drop table if exists dbo.conversation_to_receiver_sentiment_info;

CREATE TABLE dbo.conversation_to_receiver_sentiment_info
(
    id                                                      VARCHAR(1024) NOT NULL,
    sent_date  datetime2(6)  NOT NULL,
    conversation_id                                         VARCHAR(1024) NOT NULL,
    sender_mail                                         VARCHAR(1024) NOT NULL,
    sender_name                                         VARCHAR(1024) NOT NULL,
    sender_domain                                         VARCHAR(1024) NOT NULL,
    general_sentiment                                         VARCHAR(1024) NOT NULL,
    pos_score                                         float,
    neutral_score                                         float,
    negative_score                                         float,
    recipient_name VARCHAR(1024) NOT NULL,
    recipient_address VARCHAR(1024) NOT NULL,
    recipient_domain VARCHAR(1024) NOT NULL,
    CONSTRAINT PK_conversation_to_receiver_sentiment_info                            PRIMARY KEY (id)
);
CREATE NONCLUSTERED INDEX conversation_to_receiver_sentiment_info_convid_ndx ON dbo.conversation_to_receiver_sentiment_info (conversation_id ASC);
CREATE NONCLUSTERED INDEX conversation_to_receiver_sentiment_info_sendermail_ndx ON dbo.conversation_to_receiver_sentiment_info (sender_mail ASC);


GO
