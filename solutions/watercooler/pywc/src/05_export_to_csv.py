#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import joblib
import os

import argparse
import datetime
import gzip
import json
import random
import uuid
from pathlib import Path
import dill
import pytz
import sys
import pandas as pd
from types import SimpleNamespace
from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient

group_names = ['diesel', 'song', 'equal', 'hexagon', 'gong', 'harbor', 'cantina', 'korea', 'year', 'saddle', 'regard',
               'jerome', 'gizmo', 'media', 'presto', 'hello', 'prefix', 'papa', 'mars', 'milk', 'remark', 'distant',
               'wedding', 'extend', 'contact', 'miller', 'wheel', 'saturn', 'cowboy', 'rufus', 'garlic', 'spend',
               'logo', 'major', 'pegasus', 'robin', 'flex', 'orca', 'choice', 'history', 'tripod', 'random', 'kevin',
               'school', 'edition', 'pacific', 'apple', 'demo', 'robert', 'camel', 'order', 'gibson', 'giraffe',
               'stock', 'elegant', 'othello', 'moses', 'texas', 'junior', 'virgo', 'nebula', 'cyclone', 'billy', 'andy',
               'ibiza', 'exodus', 'bernard', 'speed', 'russian', 'florida', 'axiom', 'easy', 'educate', 'email',
               'beatles', 'vodka', 'vortex', 'basic', 'shelter', 'appear', 'venus', 'paris', 'dolphin', 'circle',
               'brown', 'textile', 'opera', 'carrot', 'ski', 'miguel', 'group', 'caviar', 'parlor', 'news', 'flute',
               'type', 'event', 'almond', 'toast', 'stone', 'imitate', 'phantom', 'divide', 'capital', 'machine',
               'exit', 'immune', 'detect', 'stop', 'morris', 'pixel', 'labor', 'virus', 'rio', 'oxford', 'canal',
               'earth', 'cement', 'analog', 'jupiter', 'epoxy', 'photo', 'garbo', 'beyond', 'campus', 'novel', 'amber',
               'east', 'time', 'filter', 'complex', 'carmen', 'iris', 'cartel', 'donor', 'edison', 'mexico', 'java',
               'pump', 'radius', 'mayday', 'everest', 'romeo', 'santana', 'pretend', 'tropic', 'front', 'student',
               'field', 'greek', 'teacher', 'postage', 'monster', 'baron', 'echo', 'budget', 'cuba', 'voodoo',
               'alabama', 'bishop', 'puma', 'clarion', 'prodigy', 'shirt', 'jet', 'define', 'cupid', 'amadeus',
               'canary', 'magnet', 'joel', 'diet', 'optic', 'cafe', 'topic', 'boston', 'level', 'bambino', 'silk',
               'chariot', 'capsule', 'pedro', 'poncho', 'thomas', 'laura', 'avatar', 'golf', 'vocal', 'flood', 'comedy',
               'element', 'athlete', 'clark', 'samba', 'scuba', 'grand', 'chess', 'young', 'fast', 'option', 'cola',
               'jeep', 'status', 'soprano', 'pilgrim', 'rocket', 'exotic', 'ambient', 'prague', 'marble', 'harmony',
               'rodent', 'bonanza', 'agenda', 'almanac', 'visa', 'deliver', 'memphis', 'america', 'shadow', 'tibet',
               'lola', 'plastic', 'meaning', 'pelican', 'adrian', 'frozen', 'canyon', 'passive', 'impact', 'clara',
               'extreme', 'trident', 'visual', 'reflex', 'tiger', 'gordon', 'basket', 'next', 'charter', 'father',
               'matrix', 'metal', 'command', 'present', 'gossip', 'bottle', 'north', 'secret', 'tape', 'india', 'neon',
               'spray', 'barcode', 'tonight', 'husband', 'desert', 'helena', 'trade', 'swing', 'silicon', 'bikini',
               'winter', 'nobel', 'laptop', 'cliff', 'twin', 'sphere', 'venice', 'tribal', 'saga', 'adios', 'mega',
               'orchid', 'casino', 'bingo', 'valery', 'repair', 'transit', 'arcade', 'guru', 'alice', 'nitro', 'panic',
               'patent', 'book', 'metro', 'alamo', 'jasmine', 'people', 'crown', 'depend', 'bundle', 'quiz', 'exhibit',
               'mirror', 'love', 'welcome', 'uniform', 'igor', 'tomato', 'colombo', 'monaco', 'salsa', 'mental',
               'friday', 'river', 'gregory', 'dollar', 'safari', 'pogo', 'vendor', 'null', 'jump', 'lobby', 'road',
               'circus', 'trust', 'dolby', 'pupil', 'active', 'protect', 'ginger', 'monitor', 'silence', 'import',
               'bank', 'vienna', 'quota', 'tobacco', 'urban', 'king', 'august', 'david', 'urgent', 'change', 'zoom',
               'bicycle', 'place', 'clone', 'jason', 'beach', 'eddie', 'include', 'holiday', 'adam', 'germany',
               'partner', 'vibrate', 'balance', 'chef', 'job', 'credit', 'project', 'savage', 'dispute', 'beast',
               'script', 'ship', 'invite', 'rabbit', 'slalom', 'dynasty', 'bread', 'button', 'penguin', 'edward',
               'scarlet', 'target', 'goblin', 'sting', 'shrink', 'bogart', 'declare', 'charlie', 'morph', 'noise',
               'sport', 'omega', 'rover', 'armor', 'motel', 'hair', 'dublin', 'decide', 'moment', 'sonic', 'michael',
               'tempo', 'genetic', 'leonid', 'shoe', 'jessica', 'pyramid', 'prelude', 'panda', 'cargo', 'dialog',
               'diana', 'isabel', 'catalog', 'desire', 'club', 'pagoda', 'pigment', 'except', 'biscuit', 'brother',
               'warning', 'absurd', 'final', 'cello', 'vertigo', 'expand', 'hamlet', 'reply', 'snow', 'oval', 'finland',
               'janet', 'weather', 'egypt', 'juice', 'hydro', 'orbit', 'alias', 'natural', 'titanic', 'nato', 'karate',
               'episode', 'soda', 'alarm', 'viva', 'process', 'police', 'lorenzo', 'smart', 'jazz', 'electra', 'loyal',
               'judge', 'sincere', 'multi', 'burma', 'chief', 'product', 'radar', 'bison', 'harvard', 'locate',
               'olympic', 'cannon', 'rapid', 'cave', 'albino', 'obscure', 'trivial', 'austria', 'coral', 'invest',
               'tina', 'famous', 'limbo', 'camera', 'diagram', 'zigzag', 'vampire', 'paradox', 'chemist', 'center',
               'memo', 'under', 'social', 'aurora', 'remote', 'susan', 'example', 'viking', 'conan', 'good', 'thermos',
               'critic', 'magic', 'strong', 'armada', 'economy', 'maxwell', 'sector', 'quarter', 'action', 'martin',
               'brush', 'voyage', 'sierra', 'chance', 'rodeo', 'design', 'dance', 'samuel', 'chicago', 'alert',
               'static', 'watch', 'special', 'scale', 'minute', 'mystic', 'bonus', 'pirate', 'prism', 'charm', 'chrome',
               'byte', 'nice', 'triton', 'strange', 'june', 'velvet', 'olga', 'caramel', 'ladder', 'cosmos', 'austin',
               'balloon', 'theory', 'pilot', 'miranda', 'respect', 'promo', 'premium', 'havana', 'dinner', 'trapeze',
               'break', 'domino', 'denmark', 'table', 'giant', 'spider', 'evident', 'nelson', 'alfred', 'plaza',
               'cipher', 'taboo', 'solo', 'lithium', 'cable', 'calibre', 'bazooka', 'phoenix', 'norway', 'engine',
               'sabrina', 'block', 'admiral', 'lucas', 'eric', 'simon', 'child', 'bravo', 'barbara', 'nickel',
               'organic', 'raymond', 'arizona', 'uranium', 'october', 'corona', 'antonio', 'powder', 'local', 'fractal',
               'stella', 'demand', 'nadia', 'harris', 'galaxy', 'ultra', 'joker', 'size', 'gabriel', 'miami', 'graph',
               'baggage', 'lobster', 'wolf', 'switch', 'battery', 'manager', 'guitar', 'mystery', 'company', 'select',
               'sardine', 'canada', 'before', 'xray', 'ballad', 'gallop', 'sigma', 'develop', 'modular', 'service',
               'orinoco', 'second', 'story', 'equator', 'letter', 'koala', 'solar', 'couple', 'arnold', 'pocket',
               'corner', 'deluxe', 'summer', 'carbon', 'bronze', 'halt', 'virtual', 'pearl', 'archive', 'numeric',
               'lady', 'survive', 'quick', 'aspect', 'lava', 'polaris', 'rebel', 'jester', 'common', 'sensor', 'square',
               'carol', 'trick', 'phrase', 'vital', 'radical', 'jargon', 'zebra', 'mike', 'polygon', 'global',
               'explore', 'sparta', 'tactic', 'sample', 'salt', 'hippie', 'address', 'visible', 'salute', 'extra',
               'blue', 'ego', 'british', 'canvas', 'kermit', 'popcorn', 'kimono', 'round', 'veteran', 'hand', 'vega',
               'lopez', 'fantasy', 'inca', 'side', 'vanilla', 'clever', 'section', 'sinatra', 'dream', 'mobile',
               'derby', 'tango', 'brenda', 'native', 'stretch', 'ringo', 'vista', 'clock', 'stamp', 'alibi', 'contour',
               'eagle', 'colony', 'imagine', 'exact', 'maximum', 'cecilia', 'energy', 'perfect', 'alpha', 'trilogy',
               'simple', 'slow', 'rachel', 'cloud', 'combat', 'segment', 'gold', 'aloha', 'exile', 'axis', 'turbo',
               'polka', 'fragile', 'gondola', 'perfume', 'morning', 'kiwi', 'zodiac', 'london', 'south', 'percent',
               'paint', 'monarch', 'benefit', 'confide', 'inch', 'channel', 'ralph', 'elvis', 'gentle', 'pamela',
               'diego', 'bali', 'sailor', 'morgan', 'gray', 'prime', 'person', 'mineral', 'arthur', 'dexter', 'vision',
               'cactus', 'ramirez', 'finance', 'spain', 'rubber', 'kayak', 'roger', 'beauty', 'today', 'factor',
               'needle', 'rose', 'sleep', 'nirvana', 'storm', 'super', 'henry', 'absent', 'tictac', 'heaven', 'lima',
               'info', 'cover', 'burger', 'marina', 'tractor', 'bagel', 'guide', 'pandora', 'nikita', 'sherman',
               'aspirin', 'solid', 'support', 'tavern', 'minus', 'water', 'client', 'margin', 'mayor', 'fossil',
               'enigma', 'formal', 'cotton', 'money', 'liberal', 'artist', 'owner', 'mentor', 'acrobat', 'fidel',
               'europe', 'cheese', 'raja', 'shine', 'ceramic', 'shannon', 'farmer', 'object', 'truck', 'nectar',
               'modem', 'lunar', 'horizon', 'concert', 'paul', 'mono', 'falcon', 'sheriff', 'roman', 'cairo', 'shampoo',
               'parker', 'control', 'model', 'telecom', 'brazil', 'delete', 'montana', 'genesis', 'source', 'music',
               'calypso', 'general', 'yoga', 'salon', 'invent', 'stage', 'victor', 'jamaica', 'panel', 'private',
               'mile', 'origin', 'herbert', 'lemon', 'actor', 'brave', 'violin', 'animal', 'album', 'tower', 'soviet',
               'pencil', 'version', 'escape', 'limit', 'right', 'mister', 'donald', 'symbol', 'betty', 'cockpit',
               'idiom', 'april', 'anagram', 'plume', 'pluto', 'icon', 'herman', 'ventura', 'gopher', 'caravan',
               'octavia', 'sunset', 'point', 'verona', 'food', 'forum', 'normal', 'century', 'heart', 'pioneer',
               'mailbox', 'swim', 'traffic', 'candid', 'ranger', 'alpine', 'spark', 'bonjour', 'impress', 'crash',
               'door', 'future', 'tokyo', 'freedom', 'cherry', 'medical', 'network', 'coconut', 'ethnic', 'vincent',
               'slang', 'hazard', 'oxygen', 'buffalo', 'honey', 'prepare', 'shallow', 'legend', 'market', 'license',
               'marco', 'berlin', 'mambo', 'alien', 'jaguar', 'combine', 'pizza', 'panther', 'state', 'bermuda',
               'jungle', 'elite', 'eclipse', 'sunday', 'oasis', 'optimal', 'milan', 'press', 'fabric', 'meteor',
               'eternal', 'classic', 'anita', 'salami', 'nominal', 'pablo', 'george', 'night', 'igloo', 'oregano',
               'james', 'orion', 'slogan', 'miracle', 'danube', 'light', 'japan', 'audio', 'mask', 'crater', 'phone',
               'navy', 'tuna', 'conduct', 'similar', 'pardon', 'evita', 'nerve', 'nuclear', 'user', 'violet', 'lexicon',
               'parody', 'ford', 'royal', 'richard', 'scoop', 'regular', 'laser', 'flash', 'zipper', 'resume',
               'collect', 'sharon', 'average', 'connect', 'indigo', 'chaos', 'respond', 'bandit', 'mercury', 'sabine',
               'melon', 'fortune', 'linear', 'tourist', 'plato', 'nina', 'carlo', 'turtle', 'salmon', 'method',
               'hilton', 'wonder', 'iceberg', 'torso', 'polo', 'canoe', 'gloria', 'parent', 'reward', 'reverse',
               'frame', 'armani', 'travel', 'jordan', 'delta', 'garcia', 'pancake', 'alcohol', 'forever', 'tornado',
               'comet', 'package', 'crimson', 'mammal', 'disco', 'aroma', 'poem', 'baby', 'acid', 'alfonso', 'bazaar',
               'sound', 'street', 'kansas', 'open', 'parole', 'avalon', 'sandra', 'little', 'color', 'meter', 'ricardo',
               'race', 'gorilla', 'congo', 'yogurt', 'analyze', 'gyro', 'jacob', 'tribune', 'mama', 'chapter', 'libra',
               'parking', 'baboon', 'absorb', 'relax', 'stereo', 'condor', 'ecology', 'sushi', 'costume', 'blonde',
               'fiber', 'atomic', 'horse', 'sonar', 'jumbo', 'spirit', 'poetic', 'android', 'podium', 'house',
               'natasha', 'video', 'hawaii', 'whiskey', 'manila', 'snake', 'game', 'asia', 'madonna', 'recycle',
               'suzuki', 'cubic', 'hotel', 'world', 'convert', 'julius', 'druid', 'pony', 'stadium', 'camilla', 'data',
               'emotion', 'oscar', 'agent', 'career', 'express', 'hunter', 'saint', 'passage', 'liter', 'chicken',
               'portal', 'think', 'prefer', 'sonata', 'plasma', 'record', 'john', 'idea', 'linda', 'vatican', 'joseph',
               'deposit', 'courage', 'fire', 'trumpet', 'nova', 'deal', 'baker', 'inside', 'rival', 'basil', 'sugar',
               'between', 'cool', 'france', 'patrol', 'empty', 'jackson', 'edgar', 'border', 'anvil', 'driver', 'image',
               'italian', 'current', 'signal', 'union', 'never', 'puzzle', 'tahiti', 'marvin', 'rudolf', 'ninja',
               'patriot', 'academy', 'guest', 'sister', 'geneva', 'style', 'newton', 'arsenal', 'join', 'reptile',
               'pattern', 'octopus', 'riviera', 'sahara', 'peace', 'decade', 'amanda', 'wave', 'fuel', 'shave',
               'society', 'malta', 'citizen', 'vitamin', 'marion', 'botanic', 'motif', 'office', 'gallery', 'china',
               'ivan', 'crack', 'trinity', 'judo', 'citrus', 'pepper', 'mixer', 'forget', 'arena', 'brandy', 'nissan',
               'village', 'ribbon', 'perform', 'detail', 'pretty', 'million', 'pinball', 'ticket', 'volcano', 'seminar',
               'touch', 'decimal', 'grid', 'secure', 'stand', 'supreme', 'clinic', 'folio', 'patient', 'match',
               'genius', 'prosper', 'annex', 'ohio', 'digital', 'direct', 'flipper', 'season', 'gelatin', 'shock',
               'orlando', 'amigo', 'spring', 'palma', 'silver', 'gram', 'aladdin', 'siren', 'claudia', 'diploma',
               'bamboo', 'prize', 'hammer', 'enjoy', 'price', 'floor', 'maze', 'spell', 'film', 'candle', 'jimmy',
               'python', 'fish', 'airport', 'master', 'salad', 'forbid', 'context', 'culture', 'sharp', 'benny',
               'kinetic', 'scholar', 'tennis', 'yellow', 'nepal', 'dynamic', 'telex', 'neuron', 'agatha', 'grace',
               'answer', 'subject', 'window', 'denver', 'permit', 'mozart', 'darwin', 'orange', 'observe', 'finish',
               'senator', 'helium', 'bombay', 'sweden', 'system', 'grille', 'flower', 'uncle', 'chant', 'atlanta',
               'pastel', 'export', 'hostel', 'ivory', 'forward', 'lazarus', 'infant', 'anatomy', 'begin', 'ground',
               'valid', 'totem', 'robot', 'hobby', 'freddie', 'rent', 'fruit', 'voltage', 'nylon', 'serpent', 'erosion',
               'cabinet', 'picasso', 'menu', 'wisdom', 'flag', 'ariel', 'ruby', 'drum', 'amazon', 'popular', 'heavy',
               'maestro', 'paper', 'motor', 'boris', 'blast', 'lecture', 'sultan', 'nothing', 'griffin', 'harlem',
               'torpedo', 'bahama', 'banjo', 'athena', 'talent', 'bucket', 'mango', 'lunch', 'scratch', 'picture',
               'archer', 'value', 'liquid', 'empire', 'opinion', 'alex', 'hope', 'yankee', 'pasta', 'balsa', 'juliet',
               'frank', 'outside', 'promise', 'page', 'senior', 'lake', 'karl', 'planet', 'family', 'toyota', 'copper',
               'plate', 'gustav', 'ballet', 'temple', 'zero', 'duet', 'granite', 'volume', 'copy', 'cycle', 'unique',
               'moral', 'lily', 'tunnel', 'climax', 'fiction', 'unicorn', 'margo', 'friend', 'airline', 'macro',
               'carpet', 'wizard', 'pulse', 'consul', 'olivia', 'ozone', 'subway', 'enrico', 'shake', 'mission',
               'explain', 'crystal', 'scroll', 'clean', 'vacuum', 'jacket', 'cake', 'total', 'glass', 'cobra', 'escort',
               'karma', 'legacy', 'llama', 'blitz', 'mimic', 'region', 'rainbow', 'cinema', 'tommy', 'gemini', 'peru',
               'degree', 'rhino', 'quest', 'visitor', 'binary', 'garden', 'fuji', 'gate', 'modest', 'torch', 'dallas',
               'ferrari', 'frog', 'gilbert', 'lucky', 'harvest', 'cadet', 'star', 'figure', 'ocean', 'life', 'smoke',
               'broken', 'kitchen', 'member', 'buzzer', 'spiral', 'galileo', 'city', 'lotus', 'opus', 'western',
               'profile', 'felix', 'atlas', 'magnum', 'leopard', 'arrow', 'garage', 'ritual', 'rondo', 'mustang',
               'legal', 'craft', 'sofia', 'quiet', 'mercy', 'forest', 'dragon', 'domain', 'paprika', 'single', 'pierre',
               'palace', 'radio', 'risk', 'nixon', 'antenna', 'cigar', 'period', 'formula', 'mimosa', 'spoon',
               'politic', 'report', 'flame', 'genuine', 'brain', 'arctic', 'aztec', 'medusa', 'double', 'logic',
               'salary', 'comrade', 'magenta', 'algebra', 'green', 'nevada', 'mouse', 'kilo', 'castro', 'journal',
               'sweet', 'split', 'index', 'update', 'accent', 'mirage', 'minimum', 'chamber', 'mother', 'result',
               'doctor', 'stuart', 'july', 'piano', 'toga', 'drama', 'fax', 'monkey', 'small', 'cricket', 'caesar',
               'panama', 'mary', 'first', 'list', 'potato', 'store', 'latin', 'sponsor', 'waiter', 'celtic', 'boxer',
               'human', 'number', 'belgium', 'studio', 'biology', 'modern', 'speech', 'micro', 'monica', 'alaska',
               'bruno', 'elastic', 'neutral', 'ammonia', 'museum', 'provide', 'taxi', 'bruce', 'oberon', 'twist',
               'initial', 'learn', 'scorpio', 'evening', 'sulfur', 'lesson', 'albert', 'diamond', 'oliver', 'delphi',
               'daniel', 'insect', 'fame', 'program', 'toronto', 'nurse', 'stick', 'shelf', 'prince', 'angel', 'voice',
               'castle', 'precise', 'fashion', 'printer', 'cobalt', 'proton', 'madam', 'float', 'weekend', 'cabaret',
               'focus', 'andrea', 'madrid', 'philips', 'humor', 'brigade', 'drink', 'compact', 'tulip', 'apollo',
               'banana', 'unit', 'short', 'rider', 'origami', 'civil', 'editor', 'annual', 'fluid', 'tarzan', 'queen',
               'input', 'screen', 'nancy', 'habitat', 'manual', 'dilemma', 'fresh', 'ironic', 'message', 'lion',
               'proxy', 'gravity', 'joshua', 'bridge', 'melody', 'fiesta', 'buenos', 'bless', 'avenue', 'justice',
               'abraham', 'heroic', 'gamma', 'apropos', 'serial', 'africa', 'reform', 'happy', 'ingrid', 'poker',
               'chris', 'neptune', 'isotope', 'fiona', 'ravioli', 'smile', 'orient', 'english', 'picnic', 'emerald',
               'cartoon', 'effect', 'instant', 'disney', 'yoyo', 'lagoon', 'middle']


class Person(object):
    def __init__(self, name="", mail="", skills=None, tzinfo=None):
        self.name = name
        self.mail = mail
        if skills is None:
            skills = []
        self.skills = skills
        self.events = dict()
        self.tzinfo = tzinfo

    def is_available(self, day_ts, hour, timezone):
        if len(self.events) == 0:
            return True
        if day_ts not in self.events:
            return True

        planned_start_time = day_ts + dt.timedelta(hours=hour)
        planned_end_time = planned_start_time + dt.timedelta(minutes=59)
        events_for_day = self.events[day_ts]
        is_available_for_meeting = True
        for event in events_for_day:
            if planned_start_time >= event[0] and planned_end_time <= event[1]:
                is_available_for_meeting = False
                break
            if planned_start_time < event[0] and (event[0] <= planned_end_time <= event[1]):
                is_available_for_meeting = False
                break
            if planned_start_time < event[0] and planned_end_time >= event[1]:
                is_available_for_meeting = False
                break

        if is_available_for_meeting:
            return True
        return False

    def retrieve_free_time_slots(self, working_days):
        time_slots = dict()
        for day in working_days:
            day = day.replace(tzinfo=self.tzinfo)
            for hour in range(9, 18):
                if self.is_available(day, hour, self.tzinfo):
                    planned_start_time = day + dt.timedelta(hours=hour)
                    time_slots.setdefault(day, []).append(planned_start_time)
        return time_slots

all_group_names = group_names
all_group_names = zip(all_group_names, all_group_names[1:])
all_group_names = ["-".join(p) for p in all_group_names]
random.shuffle(all_group_names)

STATUS_INVITATION_UNKNOWN = 2
STATUS_INVITATION_ACCEPTED = 1
STATUS_INVITATION_REJECTED = 0

STATUS_PARTICIPATION_UNKNWON = 2
STATUS_PARTICIPATION_PRESENT = 1
STATUS_PARTICIPATION_ABSENT = 0


def batch(iterable, n=1):
    l = len(iterable)
    for ndx in range(0, l, n):
        yield iterable[ndx:min(ndx + n, l)]


def build_personal_meeting_timetable_for_group(tzinfo, tzname, group_id, group_members, day_param, time_slot,
                                               people_in_timezone):
    record = dict()
    record["group_name"] = group_id
    record["group_busy_slots"] = dict()
    record["watercooler_timeslot"] = time_slot.hour
    record["tzinfo"] = tzinfo
    record["tzname"] = tzname

    group_members_complete_info = []
    day = day_param.replace(tzinfo=None)
    for group_member in group_members:
        person_info = people_in_timezone[group_member]
        group_members_complete_info.append({
            "name": person_info.name,
            "email": group_member,
        })
        personal_events_by_day = person_info.events
        if day not in personal_events_by_day: continue
        personal_events = personal_events_by_day[day]
        for personal_event_timeslot in personal_events:
            record["group_busy_slots"].setdefault(personal_event_timeslot[0].astimezone(pytz.utc).hour, []).append({
                "name": person_info.name,
                "email": group_member,
            })

    personal_events_timeslots = record["group_busy_slots"].keys()
    for personal_event_timeslot in personal_events_timeslots:
        current_member_list = record["group_busy_slots"][personal_event_timeslot]
        record["group_busy_slots"][personal_event_timeslot] = {
            "members": group_members_complete_info,
            "counts": len(current_member_list)
        }

    return record


def export_generated_groups_per_day_to_sql(mail_to_image_dict: dict,
                                           timezone_to_generated_groups_per_day:dict,
                                           timezone_to_people_meetings: dict):
    timezones_keys = list(timezone_to_generated_groups_per_day.keys())

    attendance_acceptance_list = []
    all_groups = []
    all_personal_meetings_occupation = []

    for tzinfo in timezones_keys:
        generated_groups_per_day = timezone_to_generated_groups_per_day[tzinfo]
        people_in_timezone = timezone_to_people_meetings[tzinfo]
        random.shuffle(all_group_names)
        tzname = str(tzinfo)

        days = generated_groups_per_day.keys()
        days = sorted(days)
        groups_per_day_dict = dict()
        group_name_counter = 0
        mapping_group_name_to_fancy_name = dict()
        global_group_to_attendance_information = dict()

        for day in days:
            day_index = day.day
            month_index = day.month
            year_number = day.year
            year_month_day = day.strftime("%Y-%m-%d")
            log.info(year_month_day)
            groups_per_day_dict[year_month_day] = []

            time_slots_to_groups = generated_groups_per_day[day]
            time_slots = sorted(time_slots_to_groups.keys())
            for time_slot in time_slots:
                groups = time_slots_to_groups[time_slot]
                time_slot = time_slot.tz_convert(pytz.utc)
                hour = time_slot.hour
                # log.info(groups,hour)
                counter = 0
                for group_members in groups:
                    if len(group_members) < 3:
                        continue
                    record = dict()
                    record["tzinfo"] = str(int(datetime.datetime.now(tzinfo).utcoffset().total_seconds() / 3600))
                    record["tzname"] = tzname
                    record["day"] = year_month_day
                    record["hour"] = hour
                    record["hour_time_slot"] = time_slot
                    group_id = "group[" + str(counter) + "]" + year_month_day + time_slot.strftime(
                        "%H-%M-%S") + ":" + str(record["tzinfo"])
                    record["group_name"] = group_id

                    # here we build the personal time table for group
                    personal_meeting = build_personal_meeting_timetable_for_group(record["tzinfo"], record["tzname"],
                                                                                  group_id,
                                                                                  group_members, day, time_slot,
                                                                                  people_in_timezone)
                    all_personal_meetings_occupation.append(personal_meeting)

                    if group_id in mapping_group_name_to_fancy_name:
                        fancy_name = mapping_group_name_to_fancy_name[group_id]
                    else:
                        if group_name_counter >= len(all_group_names) - 1:
                            group_name_counter = 0
                        fancy_name = all_group_names[group_name_counter]
                        group_name_counter += 1
                        mapping_group_name_to_fancy_name[group_id] = fancy_name

                    record["display_name"] = fancy_name

                    group_members_to_image = dict()
                    for member in group_members:
                        group_members_to_image[member] = mail_to_image_dict[member]
                    record["group_members"] = json.dumps(group_members_to_image)


                    members_wc_status = "unknown"  # get_members_wc_status(group) #accepted/rejected/unknown

                    members_attendance_status = "unknown"  # attended/not-attended/unknown #get_members_attendance_status(day_index, month_index, members_wc_status)

                    record["attendance_rate"] = 0.0  # get_member_attendance_rate(members_attendance_status)
                    record["members_wc_status"] = members_wc_status
                    record["members_attendance_status"] = members_attendance_status

                    global_group_to_attendance_information[group_id] = (
                        members_attendance_status, record["attendance_rate"])
                    all_groups.append(record)

                    counter += 1
                    groups_per_day_dict[year_month_day].append(record)
                    for group_member in group_members:
                        member_group_record = dict()
                        member_group_record["group_name"] = record["group_name"]
                        member_group_record["member_email"] = group_member
                        member_group_record["tzinfo"] = record["tzinfo"]
                        member_group_record["tzname"] = record["tzname"]
                        # day,day_index,month_index,year_index,hour_time_slot
                        member_group_record["day"] = day
                        member_group_record["day_index"] = day_index
                        member_group_record["month_index"] = month_index
                        member_group_record["year_index"] = year_number
                        member_group_record["hour_time_slot"] = time_slot
                        member_group_record["invitation_status"] = 2  # status unknown
                        member_group_record["participation_status"] = 2  # status unknown

                        attendance_acceptance_list.append(member_group_record)

    # json.dump(groups_per_day_dict, open(os.path.join(outputfolder, "ui_groups_per_day.json"), "w"), indent=4)
    # return mapping_group_name_to_fancy_name, global_group_to_attendance_information
    return all_groups, attendance_acceptance_list, all_personal_meetings_occupation


def export_groups_per_day_to_db(all_groups, file_name, output_folder=""):
    tuple_list = []
    for rec in all_groups:
        id = str(uuid.uuid4())
        tzinfo = rec["tzinfo"]
        tzname = rec["tzname"]
        # dict_keys(['day', 'hour', 'hour_time_slot', 'group_name', 'display_name', 'group_members', 'attendance_rate', 'members_wc_status', 'members_attendance_status'])
        day = datetime.datetime.strptime(rec["day"], '%Y-%m-%d')
        # day = day.replace(tzinfo=pytz.UTC)
        hour = rec["hour"]
        hour_time_slot = rec["hour_time_slot"]
        group_name = rec["group_name"]
        display_name = rec["display_name"]
        group_members = rec["group_members"]
        # statement = "insert into  [dbo].[groups_per_day](id,day,hour_time_slot,hour,group_name,display_name,group_members) values (?,?,?,?,?,?,?)"

        # tuple_list.append(tuple([id, day, hour_time_slot, hour, group_name, display_name, group_members, tzinfo]))
        tuple_list.append(
            tuple([id, rec["day"], hour_time_slot.isoformat(), hour, group_name, display_name, group_members, tzname,
                   tzinfo]))

    df = pd.DataFrame(tuple_list, columns=[p.strip() for p in
                                           "id,day,hour_time_slot,hour,group_name,display_name,group_members,timezone_str,timezone_nr".split(
                                               ",")])
    df.to_csv(os.path.join(output_folder, file_name), index=False, doublequote=False, escapechar="\\")


def export_person_attendance_to_group_to_db(attendance_acceptance_list, file_name, output_folder=""):
    tuple_list = []
    for rec in attendance_acceptance_list:
        id = str(uuid.uuid4())
        # dict_keys(['day', 'hour', 'hour_time_slot', 'group_name', 'display_name', 'group_members', 'attendance_rate', 'members_wc_status', 'members_attendance_status'])
        group_name = rec["group_name"]
        member_email = rec["member_email"]
        tzinfo = rec["tzinfo"]
        tzname = rec["tzname"]
        invitation_status = rec["invitation_status"]
        participation_status = rec["participation_status"]
        day = rec["day"].strftime('%Y-%m-%d')
        day_index = rec["day_index"]
        month_index = rec["month_index"]
        year_index = rec["year_index"]
        hour_time_slot = rec["hour_time_slot"].to_pydatetime().isoformat()
        # statement = "insert into  [dbo].[groups_per_day](id,day,hour_time_slot,hour,group_name,display_name,group_members) values (?,?,?,?,?,?,?)"

        tuple_list.append(tuple(
            [id, group_name, member_email, day, day_index, month_index, year_index, hour_time_slot, invitation_status,
             participation_status, tzname, tzinfo]))

    df = pd.DataFrame(tuple_list, columns=[p.strip() for p in
                                           "id,  group_name, member_email,day, day_index, month_index, year_index, hour_time_slot,  invitation_status, participation_status,timezone_str,timezone_nr".split(
                                               ",")])
    df.to_csv(os.path.join(output_folder, file_name), index=False, doublequote=False, escapechar="\\")


def export_person_profile_to_db(all_profiles, file_name, output_folder=""):
    all_tuples = []
    for id, profile in all_profiles.items():
        mail = profile['mail']
        id = profile["id"]
        display_name = profile["display_name"]
        about_me = profile["about_me"]
        job_title = profile["job_title"]
        company_name = profile["company_name"]
        department = profile["department"]
        country = profile["country"]
        office_location = profile["office_location"]
        city = profile["city"]
        state = profile["state"]
        skills = profile["skills"]
        responsibilities = profile["responsibilities"]
        engagement = profile["engagement"]
        image = profile["image"]
        all_tuples.append(tuple(
            [id, mail, display_name, about_me, job_title, company_name, department, country, office_location, city,
             state, skills, responsibilities, engagement, image]))

    df = pd.DataFrame(all_tuples, columns=[p.strip() for p in
                                           "id, mail, display_name, about_me, job_title, company_name, department, country, office_location, city, state, skills, responsibilities, engagement, image".split(
                                               ",")])
    df.to_csv(os.path.join(output_folder, file_name), index=False, doublequote=False, escapechar="\\")


def export_groups_week_view(day_to_group_and_time_slots, file_name, output_folder=""):
    all_days = day_to_group_and_time_slots.keys()
    all_days = sorted(all_days)

    tuple_list = []
    for day in all_days:

        groups = day_to_group_and_time_slots[day]

        groups_organized_by_timezone = dict()
        for group in groups:
            key = (group["tzinfo"], group["tzname"])
            if key in groups_organized_by_timezone:
                groups_organized_by_timezone[key].append(group)
            else:
                groups_organized_by_timezone[key] = [group]

        for key, groups in groups_organized_by_timezone.items():
            id = str(uuid.uuid4())
            tzinfo, tzname = key
            groups_as_text = json.dumps(groups)
            tuple_list.append(tuple([id, day, groups_as_text, tzname, tzinfo]))

    df = pd.DataFrame(tuple_list,
                      columns=[p.strip() for p in "id,day, group_members,timezone_str,timezone_nr".split(",")])
    df.to_csv(os.path.join(output_folder, file_name), index=False, doublequote=False, escapechar="\\")


def export_all_personal_meetings_information(all_personal_meetings, file_name, output_folder=""):
    tuple_list = []
    for rec in all_personal_meetings:
        id = str(uuid.uuid4())
        group_name = rec["group_name"]
        tzinfo = rec["tzinfo"]
        tzname = rec["tzname"]
        watercooler_hour = rec["watercooler_timeslot"]
        group_busy_slots = rec["group_busy_slots"]

        if watercooler_hour in group_busy_slots:
            del group_busy_slots[watercooler_hour]

        group_busy_slots = json.dumps(group_busy_slots)

        tuple_list.append(tuple([id, group_name, watercooler_hour, group_busy_slots, tzname, tzinfo]))

    df = pd.DataFrame(tuple_list,
                      columns=[p.strip() for p in
                               "id, group_name,watercooler_hour,group_busy_slots,timezone_str,timezone_nr".split(",")])
    df.to_csv(os.path.join(output_folder, file_name), index=False, doublequote=False, escapechar="\\")


def retrieve_latest_run(parent_folder):
    list_of_ouput_folder_runs = []
    for entry in os.listdir(parent_folder):
        if os.path.isdir(os.path.join(parent_folder, entry)):
            try:
                int_repr = int(entry)
                list_of_ouput_folder_runs.append(int_repr)
            except Exception:
                pass

    list_of_ouput_folder_runs = sorted(list_of_ouput_folder_runs, reverse=True)
    full_path = os.path.join(parent_folder, str(list_of_ouput_folder_runs[0]))
    return full_path


def build_persons_from_csv(input_profile_folder):
    all_files_to_be_processed = []
    for entry in os.listdir(input_profile_folder):
        if entry.endswith("json"):
            nested_file = os.path.join(input_profile_folder, entry)
            all_files_to_be_processed.append(nested_file)

    persons_dict = dict()
    for entry in all_files_to_be_processed:
        with open(entry) as f:
            for line in f.readlines():
                entry = json.loads(line.strip())
                fields = ["id",
                          'version', 'mail', 'display_name', 'about_me', 'job_title',
                          'company_name', 'department', 'country', 'office_location', 'city',
                          'state', 'skills', 'responsibilities', 'engagement', 'image']
                rec_dict = dict()
                for field in fields:
                    if field not in entry or entry[field] is None:
                        rec_dict[field] = ""
                    elif str(entry[field]) in ["nan"] or str(entry[field]) in [""]:
                        rec_dict[field] = ""
                    else:
                        rec_dict[field] = entry[field]
                print(rec_dict)
                persons_dict[rec_dict["id"]] = rec_dict
    return persons_dict

args = None
log = None
SERVICE_PRINCIPAL_SECRET = None

if __name__ == '__main__':

    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process some integers.')
        parser.add_argument('--application-id', type=str,
                            help='application id')
        parser.add_argument('--directory-id', type=str,
                            help='directory id')
        parser.add_argument('--user-profiles-input-path', type=str,
                            help='Full input path in dbfs for user profiles')
        parser.add_argument('--kmeans-data-input-path', type=str,
                            help='Input path for kmeans data')
        parser.add_argument('--csv-output-data-path', type=str,
                            help='Output path for csv files')
        parser.add_argument('--log-analytics-workspace-id', type=str,
                            help='Log Analytics workspace id')
        parser.add_argument('--log-analytics-workspace-key-name', type=str,
                            help='Log Analytics workspace key secret name')
        parser.add_argument('--adb-secret-scope-name', type=str,
                            help='secret scope name')
        parser.add_argument('--adb-sp-client-key-secret-name', type=str,
                            help='Azure Databricks Service Principal client key secret name in Databricks Secrets')
        parser.add_argument('--key-vault-url', type=str,
                            help='Azure Key Vault url')
        # for the following parameters, the "required" option is set to False, we assume that the names of the assembled data files
        # and the generated groups output file, will be the default in most of the cases
        parser.add_argument('--tzinfo-people-with-meetings-file-name', type=str, default="tzinfo_people_with_meetings.dict.gz",
                            help="File name for 'people time zone with meetings info' data file", required=False)
        parser.add_argument('--timezone-generated-groups-file-name', type=str, default="timezone_generated_groups.dict.gz",
                            help='Tmezone generated groups output file name', required=False)
        parser.add_argument('--members-group-personal-meetings-file-name', type=str, default="members_group_personal_meetings.csv",
                            help='Csv file name for members group personal meetings data', required=False)
        parser.add_argument('--groups-per-weeks-file-name', type=str, default="groups_per_week.csv",
                            help='Csv file name for groups per week data', required=False)
        parser.add_argument('--groups-per-day-file-name', type=str, default="groups_per_day.csv",
                            help='Csv file name for groups per day data', required=False)
        parser.add_argument('--members-to-group-participation-file-name', type=str, default="members_to_group_participation.csv",
                            help='Csv file name for members to group participation data', required=False)
        parser.add_argument('--employee-profile-file-name', type=str, default="employee_profile.csv",
                            help='Csv file name for employee profiles data', required=False)
        #members_group_personal_meetings

        args = parser.parse_args()

        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)

    else:

        params_path = os.path.expanduser("~/.watercooler/05_export_to_csv_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}
        args = SimpleNamespace(**default_params)

        SERVICE_PRINCIPAL_SECRET = json.load(open("config_test.json"))["SERVICE_PRINCIPAL_SECRET"]

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        log = LogAnalyticsLogger(name="[export_to_csv]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            log = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                     shared_key=logAnalyticsApiKey,
                                     log_type="ExportGroupsAndEmployeesDataToCsv",
                                     log_server_time=True,
                                     name="[export_to_csv]")
        except Exception as e:
            log = LogAnalyticsLogger(name="[export_to_csv]")
            log.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    user_profiles_input_path = args.user_profiles_input_path
    kmeans_data_input_path = args.kmeans_data_input_path
    tzinfo_people_with_meetings_file_name = args.tzinfo_people_with_meetings_file_name
    timezone_generated_groups_file_name = args.timezone_generated_groups_file_name
    output_folder = args.csv_output_data_path
    members_group_personal_meetings_file_name = args.members_group_personal_meetings_file_name
    groups_per_weeks_file_name = args.groups_per_weeks_file_name
    groups_per_day_file_name = args.groups_per_day_file_name
    members_to_group_participation_file_name = args.members_to_group_participation_file_name
    employee_profile_file_name = args.employee_profile_file_name


    if not os.path.exists(output_folder):
        os.mkdir(output_folder)

    subfolder = str(datetime.datetime.now().strftime("%Y%m%d%H%M"))

    if os.path.exists(os.path.join(output_folder, subfolder)) is False:
        os.mkdir(os.path.join(output_folder, subfolder))

    csv_output_folder = os.path.join(output_folder, subfolder)

    input_data_folder = retrieve_latest_run(kmeans_data_input_path)

    input_profile_folder = retrieve_latest_run(user_profiles_input_path)

    timezone_to_generated_groups_per_day = joblib.load(
        os.path.join(input_data_folder, timezone_generated_groups_file_name))

    timezone_to_people_meetings = joblib.load(
        os.path.join(input_data_folder, tzinfo_people_with_meetings_file_name))

    all_profiles = build_persons_from_csv(input_profile_folder=input_profile_folder)
    all_tuples = []
    mail_to_image_dict = dict()
    for id, profile in all_profiles.items():
        mail = profile["mail"]

        mail_to_image_dict[mail] = {
            "name": profile["display_name"],
            "image": profile["image"]
        }

    all_groups, attendance_acceptance_list, all_personal_meetings = export_generated_groups_per_day_to_sql(
        mail_to_image_dict,
        timezone_to_generated_groups_per_day,
        timezone_to_people_meetings)

    day_to_group_and_time_slots = dict()
    for group in all_groups:
        print(group)
        day = group["day"]
        tzinfo = group["tzinfo"]
        tzname = group["tzname"]
        hour = group["hour"]
        group_name = group["group_name"]
        group_display_name = group["display_name"]
        hour_time_slot = group["hour_time_slot"]
        group_members_as_text = group["group_members"]
        group_members = json.loads(group_members_as_text)
        new_group_record = []
        for key, member_info in group_members.items():
            new_group_record.append({
                "mail": key,
                "name": member_info["name"]
            })
        if day not in day_to_group_and_time_slots:
            day_to_group_and_time_slots[day] = []

        new_record = dict()
        new_record["group_name"] = group_name
        new_record["tzinfo"] = tzinfo
        new_record["tzname"] = tzname
        new_record["display_name"] = group_display_name
        new_record["hour"] = hour
        new_record["hour_time_slot"] = hour_time_slot.strftime('%Y-%m-%d %H:%M:%S')
        new_record["members"] = new_group_record
        day_to_group_and_time_slots[day].append(new_record)

    export_all_personal_meetings_information(all_personal_meetings, file_name=members_group_personal_meetings_file_name, output_folder=csv_output_folder )
    export_groups_week_view(day_to_group_and_time_slots, file_name=groups_per_weeks_file_name, output_folder=csv_output_folder )
    export_groups_per_day_to_db(all_groups, file_name=groups_per_day_file_name, output_folder=csv_output_folder )
    export_person_attendance_to_group_to_db(attendance_acceptance_list, file_name=members_to_group_participation_file_name, output_folder=csv_output_folder )
    export_person_profile_to_db(all_profiles, file_name=employee_profile_file_name, output_folder=csv_output_folder )

    pass
