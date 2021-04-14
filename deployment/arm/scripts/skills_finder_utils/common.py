import hashlib
import re
import string
import uuid

from random import shuffle, choice


def is_valid_uuid(val):
    try:
        uuid.UUID(str(val))
        return True
    except ValueError:
        return False


def yes_no(answer):
    yes = {'yes', 'y', 'ye', ''}
    no = {'no', 'n'}

    while True:
        choice = input(answer).lower()
        print('\n')
        if choice in yes:
            return True
        elif choice in no:
            return False
        else:
            print("Please respond with 'yes' or 'no'\n")


def lex_hash(base: str, max_ln: int = 7):
    return str(hashlib.md5(base.encode("UTF-8")).hexdigest()[:max_ln])


def make_strong_password(length: int, special_chars: str = '#%^&-_+{}|\\:\'/`~"'):
    # Password defined so as to be compatible with Azure password definition rules in
    # https://docs.microsoft.com/en-us/sql/relational-databases/security/password-policy?view=sql-server-ver15
    # https://docs.microsoft.com/en-us/sql/relational-databases/security/strong-passwords?view=sql-server-ver15
    min_lowercase = 1
    min_uppercase = 1
    min_digits = 1
    min_special_chars = 1
    min_length = 8
    max_length = 128

    lower_chars = string.ascii_lowercase
    upper_chars = string.ascii_uppercase
    digits = string.digits
    all_chars = lower_chars + upper_chars + digits + special_chars

    collective_min_length = min_lowercase + min_uppercase + min_digits + min_special_chars

    if collective_min_length > min_length:
        min_length = collective_min_length

    if length < min_length or length > max_length:
        raise ValueError(f"Password length should be between {min_length} and {max_length} characters")

    password_char_list = list(choice(lower_chars) for _ in range(min_lowercase))
    password_char_list += list(choice(upper_chars) for _ in range(min_uppercase))
    password_char_list += list(choice(digits) for _ in range(min_digits))
    password_char_list += list(choice(special_chars) for _ in range(min_special_chars))

    current_password_length = len(password_char_list)

    if len(password_char_list) < length:
        password_char_list += list(choice(all_chars) for _ in range(length - current_password_length))

    shuffle(password_char_list)
    return "".join(password_char_list)


def check_complex_password(password: str):
    # https://docs.microsoft.com/en-us/sql/relational-databases/security/password-policy?view=sql-server-ver15
    if len(password) < 8:
        return False
    has_upper_case = 1 if re.search("[A-Z]", password) else 0
    has_lower_case = 1 if re.search("[a-z]", password) else 0
    has_numbers = 1 if re.search("[\d]", password) else 0
    has_nonalphas = 1 if re.search("\W", password) else 0
    if has_upper_case + has_lower_case + has_numbers + has_nonalphas < 3:
        return False

    return True
