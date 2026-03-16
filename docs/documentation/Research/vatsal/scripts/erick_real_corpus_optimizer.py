import requests
import json
import random
import math
import numpy as np
import pandas as pd
from collections import defaultdict, Counter
from tqdm import tqdm
import itertools

# ============================================
# CONFIG
# ============================================

SA_ITERATIONS = 150000
GA_POP = 80
GA_GEN = 300
BIGRAM_WEIGHT = 0.5

DIRECTIONS = ['N','NE','E','SE','S','SW','W','NW']
INDEX = {d:i for i,d in enumerate(DIRECTIONS)}

# ============================================
# DOWNLOAD DATA
# ============================================

def fetch_json(url):
    r = requests.get(url)
    r.raise_for_status()
    return json.loads(r.text)

def fetch_csv(url):
    return pd.read_csv(url)

print("Downloading corpora...")

# wordfreq 25k
WF_URL = "https://raw.githubusercontent.com/aparrish/wordfreq-en-25000/main/wordfreq-en-25000-log.json"
wf_data = fetch_json(WF_URL)
wf_probs = {w: math.exp(f) for w,f in wf_data[:25000]}

# Google Books 1gram
GB1_URL = "https://raw.githubusercontent.com/orgtre/google-books-ngram-frequency/main/ngrams/en/1grams_10k_2010-2019_clean.csv"
gb1 = fetch_csv(GB1_URL)
gb1_probs = dict(zip(gb1['ngram'], gb1['freq']))

# Google Books 2gram
GB2_URL = "https://raw.githubusercontent.com/orgtre/google-books-ngram-frequency/main/ngrams/en/2grams_5k_2010-2019_clean.csv"
gb2 = fetch_csv(GB2_URL)

print("Merging corpora...")

# Normalize
def normalize(d):
    s = sum(d.values())
    return {k:v/s for k,v in d.items()}

wf_probs = normalize(wf_probs)
gb1_probs = normalize(gb1_probs)

# Merge 60% web + 40% books
unigrams = defaultdict(float)
for k,v in wf_probs.items():
    unigrams[k] += 0.6*v
for k,v in gb1_probs.items():
    unigrams[k] += 0.4*v

unigrams = normalize(unigrams)

# Build bigrams
bigram_counts = defaultdict(float)
for _,row in gb2.iterrows():
    parts = row['ngram'].split()
    if len(parts)==2:
        bigram_counts[(parts[0],parts[1])] += row['freq']

bigram_total = sum(bigram_counts.values())
bigrams = {k:(v/bigram_total)*0.15 for k,v in bigram_counts.items()}

# ============================================
# COST FUNCTIONS
# ============================================

def circular_steps(a,b):
    diff = abs(INDEX[a]-INDEX[b])
    return min(diff,8-diff)

DIFF = {0:0.6,1:0.9,2:1.2,3:1.6,4:2.0}

def chord_difficulty(pos):
    l,r = pos
    return DIFF[circular_steps(l,r)]

def transition_distance(a,b):
    return (circular_steps(a[0],b[0]) + circular_steps(a[1],b[1])) / 8

# ============================================
# SYMBOLS
# ============================================

letters = [chr(i) for i in range(ord('a'),ord('z')+1)]
digits = [str(i) for i in range(10)]
punct = ["'", ";", ":", "?", "!", "-", "(", ")", '"', "/", "@", "#"]

symbols = letters + digits + punct

POSITIONS = [(l,r) for l in DIRECTIONS for r in DIRECTIONS]

# ============================================
# FIXED RIGHT-ONLY
# ============================================

right_fixed = {
    'SHIFT':'N',
    'SPACE':'E',
    'BACKSPACE':'W',
    'ENTER':'S'
}

right_pool = ['.',',','CAPSLOCK','TAB']

# ============================================
# OBJECTIVE
# ============================================

def total_cost(mapping):
    cost = 0
    for w,p in unigrams.items():
        if w in mapping:
            cost += p * chord_difficulty(mapping[w])
    for (a,b),p in bigrams.items():
        if a in mapping and b in mapping:
            cost += BIGRAM_WEIGHT * p * transition_distance(mapping[a],mapping[b])
    return cost

# ============================================
# SIMULATED ANNEALING
# ============================================

def simulated_annealing():
    mapping = {}
    free_positions = POSITIONS.copy()
    random.shuffle(free_positions)
    for s,p in zip(symbols, free_positions):
        mapping[s]=p

    best = mapping.copy()
    best_cost = total_cost(mapping)

    for i in tqdm(range(SA_ITERATIONS)):
        s1,s2 = random.sample(symbols,2)
        mapping[s1],mapping[s2] = mapping[s2],mapping[s1]
        c = total_cost(mapping)
        if c < best_cost or random.random() < math.exp((best_cost-c)/0.01):
            best_cost = c
            best = mapping.copy()
        else:
            mapping[s1],mapping[s2] = mapping[s2],mapping[s1]

    return best,best_cost

# ============================================
# RUN
# ============================================

print("Running Simulated Annealing...")
best_map, best_score = simulated_annealing()

print("BEST SCORE:", best_score)

print("\nFINAL 8x8 LAYOUT")
for l in DIRECTIONS:
    row=[]
    for r in DIRECTIONS:
        found = [k for k,v in best_map.items() if v==(l,r)]
        row.append(found[0] if found else "-")
    print(l, row)

print("\nRight-dial single swipe fixed:")
for k,v in right_fixed.items():
    print(v,"→",k)

print("\nRemaining right-only directions:")
print("NE, SE, SW, NW assigned to:", right_pool)