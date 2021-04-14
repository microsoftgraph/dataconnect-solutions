"""Pygraph library sanity check
"""
from taxo_utils.utils import open_stopwords


def test_open_stopwords():
    """Checks if open_stopwords returns a non-empty set

    :return: True if set is non-empty
    :rtype: bool
    """
    return bool(open_stopwords('data/stopwords.txt', flag_hardcoded=True))


if __name__ == '__main__':
    print(test_open_stopwords())
