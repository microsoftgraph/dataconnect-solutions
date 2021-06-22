#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import datetime
import json
import os
import random
import sys
import time
import traceback
import uuid
from pathlib import Path

import pandas as pd
from azure.ai.textanalytics import TextAnalyticsClient
from azure.core.credentials import AzureKeyCredential


class Conversation(object):
    def __init__(self, sender_name, sender_mail, sender_domain, content, recipients):
        self.sender_name = sender_name
        self.sender_mail = sender_mail
        self.sender_domain = sender_domain
        if recipients is None or len(recipients) == 0:
            recipients = []
        self.recipients = recipients
        self.content = content
        self.id = str(uuid.uuid4())
        self.conversation = None


def retrieve_conversations(mails_input_path):
    all_conversations = []
    with open(mails_input_path) as f:
        for line in f.readlines():
            try:
                mail = json.loads(line)
                content = mail["UniqueBody"]["Content"]
                sender_mail = mail["Sender"]["EmailAddress"]["Address"]
                sender_name = mail["Sender"]["EmailAddress"]["Name"]
                sender_domain = sender_mail.split("@")[1].strip()
                recipients = mail["ToRecipients"]
                target_recipients = []
                for recipient in recipients:
                    recipient_address = recipient["EmailAddress"]["Address"]
                    recipient_domain = recipient_address.split("@")[1].strip()
                    recipient_name = recipient["EmailAddress"]["Name"]
                    target_recipients.append((recipient_name, recipient_address, recipient_domain))

                conversation = Conversation(sender_name, sender_mail, sender_domain,
                                            content, target_recipients)
                all_conversations.append(conversation)
            except Exception as e:
                print("Exception in creating the conversation")
                print("-" * 60)
                traceback.print_exc(file=sys.stdout)
                print("-" * 60)
    return all_conversations


def batch_list(iterable, n=1):
    l = len(iterable)
    for ndx in range(0, l, n):
        yield iterable[ndx:min(ndx + n, l)]


def analyze_conversations(all_conversations, batch_size=5, endpoint="", key=""):
    """

    :param all_conversations: list of Conversation objects
    :param batch_size: batch size to be used with text analytics
    :param endpoint: azure text analytics endpoint
    :param key: azure text analytics endpoint key
    :return:
    """

    analyzed_conversations = []
    for batch in batch_list(all_conversations, batch_size):
        content = [conv.content for conv in batch]

        text_analytics_client = TextAnalyticsClient(endpoint=endpoint, credential=AzureKeyCredential(key))
        conversations_sentiment_dict = dict()
        conversation_entities_dict = dict()
        try:

            with text_analytics_client:
                entities_result = text_analytics_client.recognize_entities(content)
                sentiment_results = text_analytics_client.analyze_sentiment(content)
                for entity_result in entities_result:
                    index = entity_result["id"]
                    for recognized_ent in entity_result["entities"]:
                        category = recognized_ent["category"]
                        score = recognized_ent["confidence_score"]
                        text = recognized_ent["text"]
                        if score is not None and score > 0.5:
                            ent_dict = dict(category=category, text=text, score=score)
                            conversation_entities_dict.setdefault(index, []).append(ent_dict)

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
                if idx in conversations_sentiment_dict and idx in conversation_entities_dict and len(
                        conversations_sentiment_dict[idx]):
                    conversation_sentiment_info = conversations_sentiment_dict[idx]
                    conversation_entities_info = conversation_entities_dict[idx]

                    conversation.conversation_sentiment_info = conversation_sentiment_info
                    conversation.entities_info = conversation_entities_info
                    analyzed_conversations.append(conversation)

        except Exception as e:
            print("Exception in retrieving the sentiment")
            print("-" * 60)
            traceback.print_exc(file=sys.stdout)
            print("-" * 60)
            continue

        time.sleep(0.1)

    return analyzed_conversations


def export_to_csv(full_path, analyzed_conversations):
    tuple_list = []
    file_name = "conversation_sentiment_info.csv"
    for conversation in analyzed_conversations:
        sql_idx = str(uuid.uuid4())
        conversation_id = conversation.id

        tuple_list.append(tuple([sql_idx,
                                 conversation_id,
                                 conversation.sender_mail,
                                 conversation.sender_name,
                                 conversation.sender_domain,
                                 conversation.conversation_sentiment_info["general_sentiment"],
                                 conversation.conversation_sentiment_info["confidence_scores"]["positive"],
                                 conversation.conversation_sentiment_info["confidence_scores"]["neutral"],
                                 conversation.conversation_sentiment_info["confidence_scores"]["negative"],
                                 ]))
    df = pd.DataFrame(tuple_list, columns=[p.strip() for p in
                                           "id,conversation_id,sender_mail,sender_name,sender_domain,general_sentiment,pos_score,neutral_score,negative_score".split(
                                               ",")])
    df.to_csv(os.path.join(full_path, file_name), index=False, doublequote=False, escapechar="\\")

    tuple_list = []
    file_name = "conversation_entities_info.csv"
    for conversation in analyzed_conversations:

        conversation_id = conversation.id

        for entity_info in conversation.entities_info[:5]:  # we limit to 5 entities
            sql_idx = str(uuid.uuid4())
            tuple_list.append(tuple([sql_idx,
                                     conversation_id,
                                     conversation.sender_mail,
                                     conversation.sender_name,
                                     conversation.sender_domain,
                                     entity_info["text"],
                                     entity_info["category"],
                                     entity_info["score"],
                                     ]))
    df = pd.DataFrame(tuple_list, columns=[p.strip() for p in
                                           "id,conversation_id,sender_mail,sender_name,sender_domain,text,category,score".split(
                                               ",")])
    df.to_csv(os.path.join(full_path, file_name), index=False, doublequote=False, escapechar="\\")

    tuple_list = []
    file_name = "conversation_to_recipient_sentiment_info.csv"
    for conversation in analyzed_conversations:

        for receiver in conversation.recipients:
            sql_idx = str(uuid.uuid4())
            conversation_id = conversation.id
            recipient_name, recipient_address, recipient_domain = receiver

            tuple_list.append(tuple([sql_idx,
                                     conversation_id,
                                     conversation.sender_mail,
                                     conversation.sender_name,
                                     conversation.sender_domain,
                                     conversation.conversation_sentiment_info["general_sentiment"],
                                     conversation.conversation_sentiment_info["confidence_scores"]["positive"],
                                     conversation.conversation_sentiment_info["confidence_scores"]["neutral"],
                                     conversation.conversation_sentiment_info["confidence_scores"]["negative"],
                                     recipient_name,
                                     recipient_address,
                                     recipient_domain
                                     ]))
    df = pd.DataFrame(tuple_list, columns=[p.strip() for p in
                                           "id,conversation_id,sender_mail,sender_name,sender_domain,general_sentiment,pos_score,neutral_score,negative_score,recipient_name,recipient_address,recipient_domain".split(
                                               ",")])
    df.to_csv(os.path.join(full_path, file_name), index=False, doublequote=False, escapechar="\\")


if __name__ == '__main__':
    print(sys.argv)

    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Arguments for mail sentiment retriever')
        parser.add_argument('--storage-account-name', type=str,
                            help='storage account name')
        parser.add_argument('--output-folder-path', type=str,
                            help='output folder path')
        parser.add_argument('--azure-text-analytics-endpoint', type=str,
                            help='azure text analytics endpoint id')
        parser.add_argument('--azure-text-analytics-key', type=str,
                            help='azure text analytics endpoint id')
        parser.add_argument('--mails-input-folder', type=str,
                            help='azure text analytics endpoint id')

        args = parser.parse_args()
        # params = json.load(open(Path("/dbfs/mnt/convlineage/scripts/config_test_azure.json")))
        params = json.load(open(Path("/dbfs/mnt/convlineage/scripts/config_test_azure.json")))
        mail_input_file = params["mail_input_folder"]
        key = params["key"]
        endpoint = params["endpoint"]
        output_folder = params["output_folder"]

    else:

        #params = json.load(open(Path("config_test.json")))
        params = json.load(open(Path("/dbfs/mnt/convlineage/scripts/config_test_azure.json")))
        mail_input_file = params["mail_input_folder"]
        key = params["key"]
        endpoint = params["endpoint"]
        output_folder = params["output_folder"]

    subfolder = str(datetime.datetime.now().strftime("%Y%m%d%H%M"))

    if os.path.exists(output_folder) is False:
        os.mkdir(output_folder)

    full_path = os.path.join(output_folder, subfolder)
    if os.path.exists(full_path) is False:
        os.mkdir(full_path)

    flag_time = time.time()
    all_conversations = retrieve_conversations(mail_input_file)
    # sample 100 of them
    all_conversations = random.sample(all_conversations, 400)
    analyzed_conversations = analyze_conversations(all_conversations, batch_size=5, endpoint=endpoint, key=key)

    export_to_csv(full_path, analyzed_conversations)
    print("Total time:", time.time() - flag_time)
