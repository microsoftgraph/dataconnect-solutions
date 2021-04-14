import random as rnd

texts = (
    '''interested in machine learning? how about amazon echo, speech and language understanding? 
    we operate amazon scale real-time, machine learning infrastructure in production and we are working hard 
    to improve on our foundations and scale for future growth while having fun along the way. 
    come join us!alexa’s machine learning platform provides ml building blocks to external software and 
    hardware developers to build premier conversational experiences for millions of customers. we believe 
    speech to be a pervasive and superior mode of interacting with automated systems and are committed to 
    maintaining our industry-leading position in this space. our mission is to empower all external parties 
    to extend alexa’s magical experience with innovative applications across domains. for supporting the growth 
    of our rapidly expanding third party eco-system, we are building ml stack to offer state-of-the-art 
    abstractions to developers, so they can deliver immersive voice experiences with ease. our stack leverages 
    innovative ml algorithms and datasets to build and train third party application models to achieve 
    best-in-class accuracy and recognition rates. to accomplish this, we work closely with some of the best ml 
    research and applied scientists in the field.our engineers are self-motivated and customer-focused 
    individuals with a good instinct for developing software services with forward looking apis. these skills 
    help us define the leading edge for elegant, super-scalable ml services for alexa. the ideal candidate 
    will have strong distributed systems and web services design and implementation experience. the person 
    should have a sound understanding of the fundamentals of computer science and practical experience 
    building large-scale distributed systems using c++/java in a linux/unix environment. an ideal candidate 
    should enjoy working on complex system software, be customer-centric, and feel passionate not only about 
    building great software but about making that software achieve its goals as an owner of one of amazon’s 
    flagship services. if you have a flair for innovation and passion for solving some of the most 
    challenging problems in the industry, we need you!''',

)


def extract_sample(k=1, random=True):
    """Extract a sample for NLP tasks

    Function used when job description (or other) records are needed to test various pygraph modules.
    :param k: k as population size (as passed to random.sample())
    :type k: int
    :param random: defaults text sample selection to a random sample, otherwise returns first record
    :type random: bool
    :return: sample of records to use in NLP tasks
    :rtype: list
    """

    if random:
        return rnd.sample(texts, 1)
    else:
        return [texts[0]]


def open_and_sort(filename):
    """Open, clean and sort stopwords txt

    Open, clean and sort stopwords txt in case of further edits, copy the provided string to the module such that
    the stopwords lists are considered.
    :param filename: stopwords filename, relative (eg: "stopwords.txt")
    :type filename: str
    """
    stop_words = open(filename).read().splitlines()
    stop_words = set([string.lower() for string in stop_words if string])

    stop_words = sorted(list(stop_words))
    print(f'\n\nTake this string and save it in {filename[:-4]}.py')
    print('"' + ','.join(stop_words) + '"')
    with open(filename, 'w') as f:
        for word in stop_words:
            f.write(f'{word}\n')


if __name__ == '__main__':
    sample = extract_sample()
    print(sample)

    open_and_sort('stopwords_irrelevant.txt')
