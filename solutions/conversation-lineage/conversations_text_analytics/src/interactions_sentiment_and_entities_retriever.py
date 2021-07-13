#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import uuid
import html2text

from pyspark.sql.types import *

from azure.ai.textanalytics import TextAnalyticsClient
from azure.core.credentials import AzureKeyCredential

"""
The following section of code will have to be removed when integrated as a notebook in Synapse analytics, because 
the parameters will be passed as notebook parameters
"""
sql_database_name = "synapsededicatesqlpool"
sql_table_name = "dbo.augmented_emails"
sql_username = ""
sql_password  = ""
azure_ai_endpoint = ""
azure_ai_key = ""
sql_server_name = ""


conversation_sentiment_info_sql_table_definition = "uuid VARCHAR(1024), interaction_id VARCHAR(1024), source_type VARCHAR(1024), sender_mail VARCHAR(1024), sender_name VARCHAR(1024), " \
                                                   "sender_domain VARCHAR(1024), general_sentiment VARCHAR(1024), pos_score FLOAT, neutral_score FLOAT, negative_score FLOAT"
conversation_entities_info_sql_table_definition = "uuid VARCHAR(1024), interaction_id VARCHAR(1024), source_type VARCHAR(1024), sender_mail VARCHAR(1024), sender_name VARCHAR(1024), " \
                                                  "sender_domain VARCHAR(1024), text VARCHAR(1024), category VARCHAR(1024), score FLOAT"
conversation_to_recipient_sentiment_info_sql_table_definition = "uuid VARCHAR(1024), interaction_id VARCHAR(1024), source_type VARCHAR(1024), sender_mail VARCHAR(1024), sender_name VARCHAR(1024), " \
                                                                "sender_domain VARCHAR(1024), general_sentiment VARCHAR(1024), pos_score FLOAT, neutral_score FLOAT, negative_score FLOAT, \
                                                                recipient_name VARCHAR(8000), recipient_address VARCHAR(8000), recipient_domain VARCHAR(8000)"


MAX_NUMBER_OF_RECIPIENTS = 50
MAX_ENTITIES_RETRIEVED_FROM_MAIL = 10
TEXT_ANALYTICS_BATCH_SIZE = 5


def retrieve_interactions():
    interactions = spark.read.format("jdbc") \
        .option("url", f"jdbc:sqlserver://{sql_server_name}.sql.azuresynapse.net:1433;database={sql_database_name};user={sql_username}@{sql_server_name};password={sql_password};encrypt=true;trustServerCertificate=true;hostNameInCertificate=*.sql.azuresynapse.net;loginTimeout=30;") \
        .option("user", sql_username) \
        .option("password", sql_password) \
        .option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver") \
        .option("query", f"SELECT InteractionID, Content, Sender, SenderName, Recipients, RecipientNames, SourceType FROM {sql_table_name}") \
        .load()

    schema = StructType([
        StructField("InteractionId", StringType(), False),
        StructField("Content", StringType(), True),
        StructField("Sender", StringType(), True),
        StructField("SenderName", StringType(), True),
        StructField("Recipients", StringType(), True),
        StructField("RecipientNames", StringType(), True),
        StructField("SourceType", StringType(), True)
    ])

    interactions_df = spark.createDataFrame(interactions.rdd, schema)

    def transform_as_sql_data(interaction):
        recipient_addresses_list = interaction["Recipients"].split(",")
        recipient_names_list = interaction["RecipientNames"].split(",")
        interaction_id = interaction['InteractionId']
        content = interaction['Content']
        sender_mail = interaction["Sender"]
        sender_name = interaction["SenderName"]
        source_type = interaction["SourceType"]
        sender_domain = sender_mail.split("@")[1].strip()
        recipient_addresses = recipient_addresses_list[:min(MAX_NUMBER_OF_RECIPIENTS, len(recipient_addresses_list))]
        recipient_names = recipient_names_list[:min(MAX_NUMBER_OF_RECIPIENTS, len(recipient_names_list))]
        recipient_domains = list(map(lambda mail: mail.split("@")[1].strip(), recipient_addresses))

        return {
            "sender_name": sender_name,
            "id": interaction_id,
            "sender_mail": sender_mail,
            "sender_domain": sender_domain,
            "content": content,
            "recipient_addresses": recipient_addresses,
            "recipient_names": recipient_names,
            "recipient_domains": recipient_domains,
            "source_type": source_type
        }

    sql_transformed_interactions = interactions_df.rdd.map(lambda interaction: transform_as_sql_data(interaction))
    return sql_transformed_interactions


def analyze_conversations(all_conversations):
    def analyze_per_partition(partition_data):
        all_analyzed_conversations = []

        text_analytics_client = TextAnalyticsClient(endpoint=azure_ai_endpoint, credential=AzureKeyCredential(azure_ai_key))
        html_text_handler = html2text.HTML2Text()
        html_text_handler.ignore_links = True

        def process_batch(batch):
            print("Analyzing batch")
            conversations_sentiment_dict = dict()
            conversation_entities_dict = dict()
            try:
                content = [html_text_handler.handle(conv['content']) for conv in batch]
                #content = [html_text_handler.handle(conv['content'])]

                entities_result = text_analytics_client.recognize_entities(content)
                for entity_result in entities_result:
                    index = entity_result["id"]
                    print("Entity_result keys:",str(list(entity_result.keys())))
                    if "entities" in list(entity_result.keys()):
                        for recognized_ent in entity_result["entities"]:
                            category = recognized_ent["category"]
                            score = recognized_ent["confidence_score"]
                            text = recognized_ent["text"]
                            if score is not None and score > 0.5:
                                ent_dict = dict(category=category, text=text, score=score)
                                conversation_entities_dict.setdefault(index, []).append(ent_dict)
                    elif "error" in entity_result:
                        print("Error encountered",str(entity_result))
                    else:
                        print("Error encountered",str(entity_result))

                sentiment_results = text_analytics_client.analyze_sentiment(content)
                for sentiment_result in sentiment_results:
                    index = sentiment_result["id"]
                    confidence_scores = sentiment_result["confidence_scores"]
                    general_sentiment = sentiment_result["sentiment"]
                    conversations_sentiment_dict.setdefault(index, [])
                    sent_dict = dict(
                        confidence_scores=confidence_scores,
                        general_sentiment=general_sentiment,
                        sentences=sentiment_result["sentences"]
                    )
                    conversations_sentiment_dict[index] = sent_dict


                for idx, conversation in enumerate(batch):
                    idx = str(idx)
                    # we update the conversation only if we have information about it
                    if idx in conversations_sentiment_dict and idx in conversation_entities_dict and len(conversations_sentiment_dict[idx]):
                        conversation_sentiment_info = conversations_sentiment_dict[idx]
                        conversation_entities_info = conversation_entities_dict[idx]

                        conversation['conversation_sentiment_info'] = conversation_sentiment_info
                        conversation['entities_info'] = conversation_entities_info
                        all_analyzed_conversations.append(conversation)

            except Exception as e:
                print("Exception in retrieving analyzed text. Error:", e)


        # split rdd in batches
        batch = []
        batch_index = 0
        for conv in partition_data:
            batch_index += 1
            batch.append(conv)
            if batch_index == TEXT_ANALYTICS_BATCH_SIZE:
                process_batch(batch)
                batch = []
                batch_index = 0

        if len(batch) > 0:
            process_batch(batch)
            batch_index = 0
            batch = []

        return iter(all_analyzed_conversations)

    return all_conversations.mapPartitions(analyze_per_partition)



def create_conversation_sentiment_info(analyzed_conversations):
    def transform(conversation):
        sql_idx = str(uuid.uuid4())
        conversation_id = conversation['id']
        return [sql_idx,
                conversation_id,
                conversation['source_type'],
                conversation['sender_mail'],
                conversation['sender_name'],
                conversation['sender_domain'],
                conversation['conversation_sentiment_info']["general_sentiment"],
                conversation['conversation_sentiment_info']["confidence_scores"]["positive"],
                conversation['conversation_sentiment_info']["confidence_scores"]["neutral"],
                conversation['conversation_sentiment_info']["confidence_scores"]["negative"],
                ]


    return analyzed_conversations.map(transform).toDF("uuid,interaction_id,source_type,sender_mail,sender_name,sender_domain,general_sentiment,pos_score,neutral_score,negative_score".split(","))


def create_conversation_entities_info(analyzed_conversations):
    def transform(conversation):
        sql_idx = str(uuid.uuid4())
        conversation_id = conversation['id']
        entities_info = conversation["entities_info"]
        result = []
        for ei in entities_info[:min(MAX_ENTITIES_RETRIEVED_FROM_MAIL, len(entities_info))]:
            result.append([sql_idx,
                           conversation_id,
                           conversation['source_type'],
                           conversation['sender_mail'],
                           conversation['sender_name'],
                           conversation['sender_domain'],
                           ei["text"],
                           ei["category"],
                           ei["score"]
                           ])
        return result

    return analyzed_conversations.flatMap(transform).toDF("uuid,interaction_id,source_type,sender_mail,sender_name,sender_domain,text,category,score".split(","))


def create_conversation_to_recipient_sentiment_info(analyzed_conversations):
    def transform(conversation):
        sql_idx = str(uuid.uuid4())
        conversation_id = conversation['id']
        recipient_names = ",".join(conversation['recipient_names'])
        recipient_addresses = ",".join(conversation['recipient_addresses'])
        recipient_domain = ",".join(conversation['recipient_domains'])
        return [sql_idx,
                conversation_id,
                conversation['source_type'],
                conversation['sender_mail'],
                conversation['sender_name'],
                conversation['sender_domain'],
                conversation['conversation_sentiment_info']["general_sentiment"],
                conversation['conversation_sentiment_info']["confidence_scores"]["positive"],
                conversation['conversation_sentiment_info']["confidence_scores"]["neutral"],
                conversation['conversation_sentiment_info']["confidence_scores"]["negative"],
                recipient_names,
                recipient_addresses,
                recipient_domain
                ]

    return analyzed_conversations.map(transform).toDF("uuid,interaction_id,source_type,sender_mail,sender_name,sender_domain,general_sentiment,pos_score,neutral_score,negative_score,recipient_name,recipient_address,recipient_domain".split(","))


def write_df_to_sql(df, table_name, table_schema):
    df.write.mode("overwrite") \
        .format("jdbc") \
        .option("url", f"jdbc:sqlserver://{sql_server_name}.sql.azuresynapse.net:1433;database={sql_database_name};user={sql_username}@{sql_server_name};password={sql_password};encrypt=true;trustServerCertificate=true;hostNameInCertificate=*.sql.azuresynapse.net;loginTimeout=30;") \
        .option("dbtable", table_name) \
        .option("user", sql_username) \
        .option("password", sql_password) \
        .option("createTableColumnTypes", table_schema) \
        .option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver") \
        .save()



def main():
    print('Reading interactions from SQL pool...')
    all_interactions = retrieve_interactions()
    print('Analyzing interactions ...')
    analyzed_conversations = analyze_conversations(all_interactions)

    # print(analyzed_conversations.count())
    print('Writing conversation Sentimet into SQL pool ...')
    conversation_sentiment_info = create_conversation_sentiment_info(analyzed_conversations)
    write_df_to_sql(conversation_sentiment_info, "dbo.conversation_sentiment_info", conversation_sentiment_info_sql_table_definition)

    print('Writing conversation Entities into SQL pool ...')
    conversation_entities_info = create_conversation_entities_info(analyzed_conversations)
    write_df_to_sql(conversation_entities_info, "dbo.conversation_entities_info", conversation_entities_info_sql_table_definition)

    print('Writing conversation Recipient Sentiment into SQL pool ...')
    conversation_to_recipient_sentiment_info = create_conversation_to_recipient_sentiment_info(analyzed_conversations)
    write_df_to_sql(conversation_to_recipient_sentiment_info, "dbo.conversation_to_recipient_sentiment_info", conversation_to_recipient_sentiment_info_sql_table_definition)


    print('Completed')

main()


