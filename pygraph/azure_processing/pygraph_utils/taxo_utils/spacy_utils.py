"""NLP-related utilities that are used in pygraph processing modules
"""
from datetime import datetime as dt
import gzip
from pathlib import Path
import re
from types import SimpleNamespace
dict_to_sns = lambda d: SimpleNamespace(**d)
import dill

# from taxo_utils.linkedin import retrieve_all_linkedin_skills
from taxo_utils.utils import open_stopwords, open_profanity
from taxo_utils.data.test_texts import extract_sample

import spacy
nlp = spacy.load('en_core_web_lg')

print(f"Loaded spacy corpus [{nlp.meta['name']}].")

stop_words = open_stopwords('data/stopwords.txt', flag_hardcoded=True)
profanity_set = open_profanity('data/stopwords_profanity.txt', flag_hardcoded=True)

nlp.Defaults.stop_words |= stop_words
# TODO: should this be present even when the module is imported ?
print('Loaded stopwords.')


def clean_tags(html_body):
    """Text cleaning function.

    TODO: Describe steps in more detail.
    :param html_body: text as html/pseudo-html body
    :return: string
    """
    # textpipe alternative
    # html_body = doc.Doc(html_body).clean

    # TODO: <<, >>, {{ =>
    # TODO: make it faster
    html_body = html_body.strip()
    html_body = re.sub('<[^<]+?>', '', html_body).strip()
    html_body = re.sub('<[^>]*(>|$)|&nbsp;|&zwnj;|&amp;|&raquo;|&laquo;|&gt', ' ', html_body)
    html_body = re.sub('\s+', ' ', html_body).strip()  # TODO: check in Pythex
    html_tokens = [p for p in html_body.split(" ") if len(p) <= 46]
    html_body = " ".join(html_tokens)
    html_body = str(html_body.encode('ascii', errors='ignore'), encoding="utf-8")
    html_body = re.sub('\s+', ' ', html_body).strip()
    html_body = re.sub('\\\\r', ' ', html_body).strip()
    html_body = re.sub('\\\\n', ' ', html_body).strip()
    html_body = html_body.replace(" \ ", " ").strip()
    html_body = html_body.replace('"', " ").strip()
    html_body = re.sub('\s+', ' ', html_body).strip()
    html_body = re.sub('\\*r', '', html_body).strip()
    html_body = re.sub('\\*n', '', html_body).strip()
    html_body = re.sub('\\*t', '', html_body).strip()
    html_body = re.sub('\\*', '', html_body).strip()
    if html_body.startswith("b'") and html_body.endswith("'"):
        html_body = html_body[2:-3]

    return str(html_body)


def lemmatization(text, allowed_postags=('NOUN', 'PROPN')):
    """Lemmatizes given text -> breaks text into words, for each word it extracts the root / lemma

    :param text: given text
    :param allowed_postags: allowed part-of-speech (POS) tags, to be returned in the final result
    :return: texts_out
    """
    doc = nlp(text)
    texts_out = [token.lemma_.lower() for token in doc if token.pos_ in allowed_postags]
    return texts_out


def extract_tokens(clean_entry, skills_list, allowed_postags=['NOUN', 'PROPN', 'ADJ']):
    """Token extraction function

    Alternative token extraction function to be placed in `method` dictionary in `keyword_suite()` function
    under make_word2vec.py module.

    Notes:
    + Stopwords removal is interchangeable: `not token._.is_excluded_stopword` (provided by `get_exclude_stopwords()`
    loaded as a spacy extension) vs `not token.is_stop` (provided by piping stopwords into `nlp.Defaults.stop_words`).
    + Check `check_if_stopwords_pos()` for additional criteria to add to the keyword validation conditions.
    """
    doc = nlp(clean_entry)

    tokenized_array = []
    for token in doc:
        if token.pos_ in allowed_postags and \
                not token.is_digit and \
                not token.is_punct and \
                not token.is_space and \
                not token.is_stop:
            tokenized_array.append(str(token).lower())

    # if skills list is dropped, take into account consecutive keywords as they might be related
    final_array = []
    for i in range(0, len(tokenized_array) - 1):
        keyword = tokenized_array[i]
        final_array.append(keyword)

        # this should be done ideally based on the initial document, such that tokens are actually
        # consecutive and not just assumed as so
        # consecutive_1gram = " ".join([tokenized_array[i], tokenized_array[i + 1]])
        # final_array.append(consecutive_1gram)

    return final_array


def load_taxonomy(dill_name):
    if Path(dill_name).exists():
        return dill.load(gzip.open(dill_name))
    else:
        print(f'Taxonomy {dill_name} does not exist !')
        return -1


if __name__ == '__main__':
    time_start = dt.now()

    DATA_ROOT = '../../../../../gdc_data/'
    # entry = extract_sample(random=False)[0]

    # all_linkedin_skills = retrieve_all_linkedin_skills('../data/raw/linkedin_skill.txt')
    # tokens = extract_tokens(entry, all_linkedin_skills)
    # print(len(tokens))

    time_stop = dt.now()
    print('> Time elapsed: %s' % (time_stop - time_start))
