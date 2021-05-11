"""Azure Processing Utils module

"""
#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

# Built-in dependencies
# import functools
import gzip
import os
import re
import sys

# 3rd party dependencies
from azure.identity import ClientSecretCredential
import dill

# logger = None
# args = None
# SERVICE_PRINCIPAL_SECRET = None

# Internal modules dependencies
from taxo_utils.utils import open_stopwords
stop_words = open_stopwords('data/stopwords.txt', flag_hardcoded=True)

# def logged(func):
#     def wrapper(*args, **kwargs):
#         result = func(*args, **kwargs)
#         return result


class SpacyMagic(object):
    """Simple Spacy Magic to minimize loading time.

    Spacy isn't serializable but loading it is semi-expensive
    >>> SpacyMagic.get("en")
    <spacy.en.English ...
    """
    _spacys = {}

    @classmethod
    def get(cls, lang, logger):  # TODO: remove logger
        if lang not in cls._spacys:
            import subprocess
            command = "python -m spacy download en_core_web_sm"
            process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE)
            process.wait()
            if logger is not None:
                logger.debug(str(process.returncode))
            else:
                print("python -m spacy download en_core_web_sm returncode:", str(process.returncode))
            import spacy
            # TODO: OSError: [E050] Can't find model 'en_core_web_sm'.
            #  It doesn't seem to be a shortcut link, a Python package or a valid path to a data directory.
            cls._spacys[lang] = spacy.load('en_core_web_sm', disable=['parser', 'ner'])

        return cls._spacys[lang]


class GensimMagic(object):
    """
    Simple GensimMagic Magic to minimize loading time.
    >>> GensimMagic.get("en")
    """
    gensim_model = {}

    # @logged
    @classmethod
    def get(cls, args, SERVICE_PRINCIPAL_SECRET, logger):

        if "model" not in cls.gensim_model:
            from azure.storage.blob import BlobClient
            # logger.debug("========PREPARING TO SETUP GENSIM")

            storage_account_name = args.storage_account_name
            container_client_credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                                 client_id=args.application_id,
                                                                 client_secret=SERVICE_PRINCIPAL_SECRET)
            blob = BlobClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                              container_name=args.domain_expert_container_name,
                              blob_name=args.domain_expert_folder_path + "/" + args.domain_expert_file_name,
                              credential=container_client_credential)

            file_size = blob.get_blob_properties().size

            if os.path.exists(args.domain_expert_file_name) is False or (
                    os.path.exists(args.domain_expert_file_name) and os.stat(
                        args.domain_expert_file_name).st_size != file_size):
                print("getting domain expert")

                with open(args.domain_expert_file_name, "wb") as my_blob:
                    if logger is not None:
                        logger.debug("downloading domain expert")
                    else:
                        print("downloading domain expert")
                    blob_data = blob.download_blob()
                    blob_data.readinto(my_blob)
                    # logger.debug("end downloading")

            if logger is not None:
                logger.debug("after domain expert download")
            else:
                print("after domain expert download")
            from gensim.models import Word2Vec

            try:
                cls.gensim_model["model"] = Word2Vec.load(args.domain_expert_file_name)
                # logger.debug("========MODEL SET")
                cls.gensim_model["vocab"] = cls.gensim_model["model"].wv.vocab
                # logger.debug("========VOCAB SET")
            except Exception as e:
                if logger is not None:
                    logger.debug(f"******************========== EXCEPTION ENCOUNTERED {e}")
                else:
                    print(f"******************========== EXCEPTION ENCOUNTERED {e}")

        return cls.gensim_model


class TaxonomyWrapper(object):
    big_taxo = None

    @classmethod
    def get(cls, args, SERVICE_PRINCIPAL_SECRET, logger):

        if cls.big_taxo is None:
            from azure.storage.blob import BlobClient

            if logger is not None:
                logger.debug("========PREPARING TO DOWNLOAD TAXNOMY")
            else:
                print("========PREPARING TO DOWNLOAD TAXNOMY")

            storage_account_name = args.storage_account_name
            container_client_credential = ClientSecretCredential(tenant_id=args.directory_id,
                                                                 client_id=args.application_id,
                                                                 client_secret=SERVICE_PRINCIPAL_SECRET)

            blob = BlobClient(account_url=f"https://{storage_account_name}.blob.core.windows.net",
                              container_name=args.domain_expert_container_name,
                              blob_name=args.domain_expert_folder_path + "/taxo/" + "taxo_de.dill.gz",
                              credential=container_client_credential)

            file_size = blob.get_blob_properties().size

            if os.path.exists("taxo_de.dill.gz") is False or (
                    os.path.exists("taxo_de.dill.gz") and os.stat("taxo_de.dill.gz").st_size != file_size
            ):
                with open("taxo_de.dill.gz", "wb") as my_blob:
                    # print("downloading")
                    blob_data = blob.download_blob()
                    blob_data.readinto(my_blob)

            general_taxo_dict = dill.load(gzip.open("taxo_de.dill.gz", "rb"))
            cls.big_taxo = general_taxo_dict

        return cls.big_taxo


def create_bigrams(text):
    work_text = text
    if isinstance(work_text, str):
        work_text = work_text.split(" ")
    if len(work_text) > 2:
        return [" ".join(p).lower() for p in list(zip(work_text, work_text[1:]))]
    return []


def create_trigrams(text):
    work_text = text
    if isinstance(work_text, str):
        work_text = work_text.split(" ")
    if len(work_text) > 3:
        return [" ".join(p).lower() for p in list(zip(work_text, work_text[1:], work_text[2:]))]
    return []


def extract_bigrams_as_list(text):
    """Extract bigrams from text and return as list

    :param text: source text
    :type text: str
    :return: bigram tokens list
    :rtype: list
    """
    tokens = text.split(" ")
    bigram_tokens = create_bigrams(tokens)
    return bigram_tokens


def extract_bigrams_as_text(text):
    """Extract bigrams from text and return as comma-separate string

    :param text: source text
    :type text: str
    :return: comma-separated string
    :rtype: str
    """
    tokens = text.split(" ")
    bigram_tokens = create_bigrams(tokens)
    return ",".join(bigram_tokens)


def extract_trigrams_as_list(text):
    """Extract trigrams from text and return as list

    :param text: source text
    :type text: str
    :return: trigram tokens list
    :rtype: list
    """
    tokens = text.split(" ")
    trigram_tokens = create_trigrams(tokens)
    return trigram_tokens


def extract_trigrams_as_text(text):
    """Extract trigrams from text and return as comma-separate string

    :param text: source text
    :type text: str
    :return: comma-separated string
    :rtype: str
    """
    tokens = text.split(" ")
    trigram_tokens = create_trigrams(tokens)
    return ",".join(trigram_tokens)


def remove_stop_words(text):
    if isinstance(text, str):
        text = text.split()
    tokens = [p for p in text if p.lower() not in stop_words and len(p) > 1 and (p[0].isnumeric() or p[0].isalpha())]
    return tokens


def domain_to_field_name(domain_name):
    """
    TODO:

    :param domain_name:
    :type domain_name:
    :return:
    :rtype:
    """
    field_name = domain_name.lower()
    tokens = [p for p in field_name.split(" ") if len(p) > 1]
    field_name = "_".join(tokens)
    return "de_" + field_name, "de_" + field_name + "_mapping"


# TODO: (Sisu) replace this with the more improved version
def clean_email_body(html_body: str):
    """Clean email body of various unwanted text noise/spam

    Cleans the following:
        + html tags
        + HTML reserved characters / character entities
        + ???
        + non-ascii characters
        + Python bytes literal if stored improperly
    :param html_body: input html text
    :type html_body: str
    :return: cleaned html body
    :rtype: str
    """
    html_body = html_body.strip()
    html_removed = re.sub('<[^<]+?>', '', html_body).strip()
    html_removed = re.sub('<[^>]*(>|$)|&nbsp;|&zwnj;|&amp;|&raquo;|&laquo;|&gt', ' ', html_removed)
    html_removed = re.sub('\s+', ' ', html_removed).strip()
    html_tokens = [p for p in html_removed.split(" ") if len(p) <= 46]
    html_body = " ".join(html_tokens)
    html_body = str(html_body.encode('ascii', errors='ignore'))
    html_body = re.sub('\s+', ' ', html_body).strip()
    if html_body.startswith("b'") and html_body.endswith("'"):
        html_body = html_body[2:-1]
    return str(html_body)


# def lemmatization(text, allowed_postags=['NOUN', 'ADJ', 'VERB', 'ADV', "PROPN", "NUM"]):
def lemmatization(text, allowed_postags=('NOUN', "PROPN", "ADJ"), logger=None):
    """Extract token lemma from text that is an allowed POS

    :param text: text string from which to extract lemmatized tokens, as far as they are allowed
    :type text: str
    :param allowed_postags: allowed POS tags for which to extract lemmas, all others are ignored (part-of-speech tags,
    as defined by [spacy POS tagging](https://spacy.io/api/annotation#pos-tagging))
    :type allowed_postags: list
    :param logger: logger passed from the calling module (`profile_enrichment_processor.py` or
    `mail_enrichment_processor.py`), in order minimize hidden dependencies, global variables accross multiple modules
    etc.
    :type logger: log_analytics_client.logger.LogAnalyticsLogger
    :return: texts_out - list of lemmas based on tokens extracted from input text
    :rtype: list
    """
    nlp_local = SpacyMagic.get('en', logger)

    doc = nlp_local(text)
    texts_out = [token.lemma_.lower() for token in doc if token.pos_ in allowed_postags]
    return texts_out


def extract_lemma_as_list(input_text, allowed_postags=('NOUN'), logger=None):
    """Extract lemmatized tokens and drop stopwords from it

    :param input_text: text string from which to extract lemmatized tokens, as far as they are allowed
    :type input_text: str
    :param allowed_postags: allowed POS tags for which to extract lemmas, all others are ignored (part-of-speech tags,
    as defined by [spacy POS tagging](https://spacy.io/api/annotation#pos-tagging))
    :type allowed_postags: list
    :param logger: logger passed from the calling module (`profile_enrichment_processor.py` or
    `mail_enrichment_processor.py`), in order minimize hidden dependencies, global variables accross multiple modules
    etc.
    :type logger: log_analytics_client.logger.LogAnalyticsLogger
    :return: lemmatized tokens list
    :rtype: list
    """
    if logger:
        lemma_tokens = lemmatization(input_text, allowed_postags=allowed_postags, logger=logger)
    else:
        lemma_tokens = lemmatization(input_text, allowed_postags=allowed_postags, logger=None)
    clean_tokens = remove_stop_words(lemma_tokens)
    return clean_tokens


def extract_lemma_as_text(input_text, allowed_postags=('NOUN', "PROPN", "ADJ"), logger=None):
    if logger:
        lemma_tokens = lemmatization(input_text, allowed_postags=allowed_postags, logger=logger)
    else:
        lemma_tokens = lemmatization(input_text, allowed_postags=allowed_postags, logger=None)

    clean_tokens = remove_stop_words(lemma_tokens)
    return " ".join(clean_tokens)


class NLPUtils(object):
    """NLP Utilities used in mail/profile enrichment pipelines
    """

    def __init__(self, args, SERVICE_PRINCIPAL_SECRET, logger):
        self.args = args
        self.SERVICE_PRINCIPAL_SECRET = SERVICE_PRINCIPAL_SECRET
        self.logger = logger

    def retrieve_specialized_term(self, term):
        """

        :param term:
        :type term:
        :return:
        :rtype:
        """
        model = GensimMagic.get(self.args, self.SERVICE_PRINCIPAL_SECRET, self.logger)
        de_vocab = model["vocab"]
        de_model = model["model"]

        associated_tokens = set()
        associated_tokens_list = []
        custom_taxo_list = [
            ["data-science", "machine-learning", "statistics", "data-analysis", "data-mining", "ml",
             "artificial-intelligence", "neural-network", "computer-vision", "deep-learning", "prediction", "forecast"],
            ["m365", "o365", "office-365", "microsoft-365"],
            ["ux", "user-experience", "app-design", "web-design"],
            ["sqa", "qa", "regression testing", "functional testing", "integration testing", "software quality",
             "quality assurance"],
        ]
        # mapping_list = []

        # allread_processed_set = set()

        term = term.strip().lower()

        local_list = []
        if term in de_vocab:
            for associated_term in de_model.wv.most_similar(positive=[term], topn=5):
                if associated_term[0] not in associated_tokens:
                    associated_tokens.add(associated_term[0])
                    associated_tokens_list.append(associated_term[0])

                # TODO: optimize this one
                if associated_term[0] not in set(local_list):
                    local_list.append(associated_term[0])

        new_term1 = term.replace(" ", "-")
        new_term2 = term.replace(" ", "")
        if new_term1 in de_vocab:
            for associated_term in de_model.wv.most_similar(positive=[new_term1], topn=5):
                if associated_term[0] not in associated_tokens:
                    associated_tokens.add(associated_term[0])
                    associated_tokens_list.append(associated_term[0])

                # TODO: optimize this one
                if associated_term[0] not in set(local_list):
                    local_list.append(associated_term[0])

        if new_term2 in de_vocab:
            for associated_term in de_model.wv.most_similar(positive=[new_term2], topn=5):
                if associated_term[0] not in associated_tokens:
                    associated_tokens.add(associated_term[0])
                    associated_tokens_list.append(associated_term[0])

                # TODO: optimize this one
                if associated_term[0] not in set(local_list):
                    local_list.append(associated_term[0])

        for custom_list in custom_taxo_list:
            if term in custom_list or new_term1 in custom_list or new_term2 in custom_list:
                for associated_term in custom_list:
                    if associated_term not in associated_tokens:
                        associated_tokens.add(associated_term)
                        associated_tokens_list.insert(0, associated_term)

                    if associated_term not in set(local_list):
                        local_list.insert(0, associated_term)

        # if term not in allread_processed_set and len(term) and len(local_list) > 0:
        #    mapping_list.append(term + ":" + ",".join(local_list))
        #    allread_processed_set.add(term)

        return associated_tokens_list

    def retrieve_specialized_term_ds(self, term, big_taxo):
        """

        :param term:
        :type term:
        :param big_taxo:
        :type big_taxo:
        :return:
        :rtype:
        """
        de_model = big_taxo["Data Science"]
        de_vocab = de_model.keys()

        associated_tokens = set()
        associated_tokens_list = []
        custom_taxo_list = [
            ["data-science", "machine-learning", "statistics", "data-analysis", "data-mining", "ml",
             "artificial-intelligence", "neural-network", "computer-vision", "deep-learning", "prediction", "forecast"],
        ]
        # mapping_list = []
        # allread_processed_set = set()
        term = term.strip().lower()

        local_list = []
        if term in de_vocab:
            for associated_term in de_model[term]:
                if associated_term not in associated_tokens:
                    associated_tokens.add(associated_term)
                    associated_tokens_list.append(associated_term)

                # TODO: optimize this one
                if associated_term not in set(local_list):
                    local_list.append(associated_term)

        new_term1 = term.replace(" ", "-")
        new_term2 = term.replace(" ", "")
        if new_term1 in de_vocab:
            for associated_term in de_model[new_term1]:
                if associated_term not in associated_tokens:
                    associated_tokens.add(associated_term)
                    associated_tokens_list.append(associated_term)

                # TODO: optimize this one
                if associated_term not in set(local_list):
                    local_list.append(associated_term)

        if new_term2 in de_vocab:
            for associated_term in de_model[new_term2]:
                if associated_term not in associated_tokens:
                    associated_tokens.add(associated_term)
                    associated_tokens_list.append(associated_term)

                # TODO: optimize this one
                if associated_term not in set(local_list):
                    local_list.append(associated_term)

        for custom_list in custom_taxo_list:
            if term in custom_list or new_term1 in custom_list or new_term2 in custom_list:
                for associated_term in custom_list:
                    if associated_term not in associated_tokens:
                        associated_tokens.add(associated_term)
                        associated_tokens_list.append(associated_term)

                    if associated_term not in set(local_list):
                        associated_tokens_list.append(associated_term)

        return associated_tokens_list

    def extract_role_to_terms(self, all_document_tokens, big_taxo):
        """

        :param all_document_tokens:
        :type all_document_tokens:
        :param big_taxo:
        :type big_taxo:
        :return:
        :rtype:
        """
        role_to_terms = dict()
        for term in all_document_tokens:
            for domain in big_taxo.keys():
                if domain not in role_to_terms:
                    role_to_terms[domain] = {}

                if domain in ["Software"]:
                    domain = "Software"
                    if domain not in role_to_terms:
                        role_to_terms[domain] = {}
                    specialized_terms = self.retrieve_specialized_term(term)

                    if len(specialized_terms):
                        role_to_terms[domain][term] = specialized_terms

                    #                elif term in big_taxo[domain]:
                    #                    specialized_terms = big_taxo[domain][term][:5]
                    #                    role_to_terms[domain][term] = specialized_terms
                    continue

                if domain in ["Data Science"]:
                    domain = "Data Science"
                    if domain not in role_to_terms:
                        role_to_terms[domain] = {}
                    specialized_terms = self.retrieve_specialized_term_ds(term, big_taxo)

                    if len(specialized_terms):
                        role_to_terms[domain][term] = specialized_terms

                    #                elif term in big_taxo[domain]:
                    #                    specialized_terms = big_taxo[domain][term][:5]
                    #                    role_to_terms[domain][term] = specialized_terms
                    continue
                if term not in big_taxo[domain]:
                    continue

                if term in role_to_terms[domain]:
                    continue

                role_to_terms[domain][term] = big_taxo[domain][term][:5]

        for domain in big_taxo.keys():
            if domain not in role_to_terms:
                role_to_terms[domain] = {}

        return role_to_terms


if __name__ == '__main__':
    print('Do not run as module currently, several critical arguments are passed and not mocked here for testing.')
    sys.exit()
