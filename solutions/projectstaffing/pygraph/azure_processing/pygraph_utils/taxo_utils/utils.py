"""General Utils
"""
#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import argparse
import yaml
import base64

from taxo_utils.data.stopwords import get_stopwords_hardcoded
from taxo_utils.data.stopwords_irrelevant import get_stopwords_irrelevant_hardcoded
from taxo_utils.data.stopwords_profanity import get_stopwords_profanity_hardcoded

def encode_base64(term):
    return base64.b64encode(term.encode("ascii")).decode("ascii")


def decode_base64(b64term):
    return base64.b64decode(b64term.encode("ascii")).decode("ascii")


def open_stopwords(path, flag_hardcoded=False):
    """Open base stopwords and irrelevant stopword files, joins them and returns a set for faster checking

    :param flag_hardcoded: flag to load stopwords from module rather than package data
    :type flag_hardcoded: bool
    :param path: stopwords file location, ideally kept in the same folder with all other stopwords files
    :type path: str
    :return: stopwords set (base + irrelevant)
    :rtype: set

    ..doctest::
        >>> bool(open_stopwords('../data/stopwords.txt'))
        True
    """

    if flag_hardcoded:
        stop_words = set([string.lower() for string in get_stopwords_hardcoded()])
        stop_words_irr = set([string.lower() for string in get_stopwords_irrelevant_hardcoded()])
    else:
        # TODO: strip words for unicode ?
        stop_words = open(path).read().splitlines()
        stop_words = set([string.lower() for string in stop_words if string])

        path_irr = path[:-4] + '_irrelevant.txt'
        stop_words_irr = open(path_irr).read().splitlines()
        stop_words_irr = set([string.lower() for string in stop_words_irr if string])

    return stop_words.union(stop_words_irr)


def open_profanity(path, flag_hardcoded=False):
    """Open profanity stopwords and returns a set for faster checking

    :param flag_hardcoded: flag to load stopwords from module rather than package data
    :type flag_hardcoded: bool
    :param path: stopwords file location
    :type path: str
    :return: stopwords set (profanity)
    :rtype: set
    """
    if flag_hardcoded:
        stop_words_prof = set([string.lower() for string in get_stopwords_profanity_hardcoded()])
    else:
        stop_words_prof = open(path).read().splitlines()
        stop_words_prof = set([string.lower() for string in stop_words_prof if string])

    return stop_words_prof


def open_word2vec_expected_hard_skills(path):
    """Loads expected skills for different roles, to be used as a future sanity check suite for taxonomies
     (check README in ../data folder)

    :param path: yaml location
    :type path: str
    :return: expected hard_skills for various roles
    :rtype: dict
    """
    expected_skills = yaml.load(open(path), Loader=yaml.FullLoader)
    expected_skills = {key: val['hard_skills'] for key, val in expected_skills.items()}
    return expected_skills


def str2bool(v):
    """Transforms string flag into boolean

    :param v: boolean as type or string
    :type v: str
    :return: bool or argparse error (if it's not recognized)
    :rtype: bool
    """
    if isinstance(v, bool):
        return v
    if v.lower() in ('yes', 'true', 't', 'y', '1'):
        return True
    elif v.lower() in ('no', 'false', 'f', 'n', '0'):
        return False
    else:
        raise argparse.ArgumentTypeError('Boolean value expected.')


if __name__ == '__main__':
    stop_words = open_stopwords('../data/stopwords.txt', flag_hardcoded=True)
    # expected_hard_skills = open_word2vec_expected_hard_skills('../data/word2vec_expected_skills.yml')
