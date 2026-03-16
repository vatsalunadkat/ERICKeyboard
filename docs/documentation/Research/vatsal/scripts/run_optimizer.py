import requests
import json
import random
import math
import numpy as np
import pandas as pd
from collections import defaultdict, Counter
from tqdm import tqdm
import itertools

# CONFIG
SA_ITERATIONS = 150000
GA_POP = 80
GA_GEN = 300
BIGRAM_WEIGHT = 0.5

DIRECTIONS = ['N','NE','E','SE','S','SW','W','NW']
INDEX = {d:i for i,d in enumerate(DIRECTIONS)}

def fetch_json(url):
    r = requests.get(url)
    r.raise_for_status()
    return json.loads(r.text)

def fetch_csv(url):
    return pd.read_csv(url)

print("Downloading corpora...")
WF_URL = "https://raw.githubusercontent.com/aparrish/wordfreq-en-25000/main/wordfreq-en-25000-log.json"
wf_data = fetch_json(WF_URL)
wf_probs = {w: math.exp(f) for w,f in wf_data[:25000]}

GB1_URL = "https://raw.githubusercontent.com/orgtre/google-books-ngram-frequency/main/ngrams/1grams_english.csv"
gb1 = fetch_csv(GB1_URL).head(10000)
gb1_probs = dict(zip(gb1['ngram'].astype(str), gb1['freq']))

GB2_URL = "https://raw.githubusercontent.com/orgtre/google-books-ngram-frequency/main/ngrams/2grams_english.csv"
gb2 = fetch_csv(GB2_URL).head(5000)

def normalize(d):
    s = sum(d.values())
    if s == 0: return d
    return {k:v/s for k,v in d.items()}

wf_probs = normalize(wf_probs)
gb1_probs = normalize(gb1_probs)

word_unigrams = defaultdict(float)
for k,v in wf_probs.items(): word_unigrams[str(k).lower()] += 0.6*v
for k,v in gb1_probs.items(): word_unigrams[str(k).lower()] += 0.4*v
word_unigrams = normalize(word_unigrams)

char_unigrams = defaultdict(float)
char_bigrams = defaultdict(float)

# Intra-word chars and bigrams
for w, p in word_unigrams.items():
    if not isinstance(w, str): continue
    for c in w:
        char_unigrams[c] += p
    for i in range(len(w)-1):
        char_bigrams[(w[i], w[i+1])] += p
    char_unigrams['SPACE'] += p

# Inter-word bigrams from gb2
for _,row in gb2.iterrows():
    parts = str(row['ngram']).split()
    if len(parts)==2:
        w1, w2 = parts[0].lower(), parts[1].lower()
        if w1 and w2:
            char_bigrams[(w1[-1], 'SPACE')] += row['freq']
            char_bigrams[('SPACE', w2[0])] += row['freq']

char_unigrams = normalize(char_unigrams)
char_bigrams = normalize(char_bigrams)

unigrams = char_unigrams
bigrams = char_bigrams

def circular_steps(a,b):
    if a is None and b is None: return 0
    if a is None or b is None: return 1
    diff = abs(INDEX[a]-INDEX[b])
    return min(diff,8-diff)

DIFF = {0:0.6, 1:0.9, 2:1.2, 3:1.6, 4:2.0}

def chord_difficulty(pos):
    l,r = pos
    if l is None: return 0.2
    return DIFF[circular_steps(l,r)]

def transition_distance(a,b):
    return (circular_steps(a[0],b[0]) + circular_steps(a[1],b[1])) / 8

letters = [chr(i) for i in range(ord('a'),ord('z')+1)]
digits = [str(i) for i in range(10)]
punct = ["'", ";", ":", "?", "!", "-", "(", ")", '"', "/", "@", "#"]
symbols = letters + digits + punct

POSITIONS = [(l,r) for l in DIRECTIONS for r in DIRECTIONS]

right_fixed = {'SHIFT':'N', 'SPACE':'E', 'BACKSPACE':'W', 'ENTER':'S'}
right_fixed_pos = {k: (None, v) for k,v in right_fixed.items()}
right_pool = ['.',',','CAPSLOCK','TAB']
right_pool_dirs = ['NE','SE','SW','NW']

# Build initial random baseline mapping
def build_random_mapping():
    m = {}
    free_pos = POSITIONS.copy()
    random.shuffle(free_pos)
    for s,p in zip(symbols, free_pos):
        m[s] = p
    rp_dirs = right_pool_dirs.copy()
    random.shuffle(rp_dirs)
    for s, d in zip(right_pool, rp_dirs):
        m[s] = (None, d)
    for k, v in right_fixed_pos.items():
        m[k] = v
    return m

def total_cost(mapping, return_breakdown=False):
    uni_cost = 0
    for w,p in unigrams.items():
        if w in mapping:
            uni_cost += p * chord_difficulty(mapping[w])
    bi_cost = 0
    for (a,b),p in bigrams.items():
        if a in mapping and b in mapping:
            bi_cost += BIGRAM_WEIGHT * p * transition_distance(mapping[a],mapping[b])
    
    total = uni_cost + bi_cost
    if return_breakdown:
        return total, uni_cost, bi_cost
    return total

random_baseline = sum(total_cost(build_random_mapping()) for _ in range(100))/100

def simulated_annealing():
    mapping = build_random_mapping()
    best = mapping.copy()
    best_cost = total_cost(mapping)
    
    T = 0.01
    for i in tqdm(range(SA_ITERATIONS), desc="SA"):
        # 10% chance to swap in right pool
        if random.random() < 0.1:
            s1, s2 = random.sample(right_pool, 2)
        else:
            s1, s2 = random.sample(symbols, 2)
        
        mapping[s1], mapping[s2] = mapping[s2], mapping[s1]
        c = total_cost(mapping)
        if c < best_cost or random.random() < math.exp((best_cost-c)/T):
            best_cost = c
            best = mapping.copy()
        else:
            mapping[s1], mapping[s2] = mapping[s2], mapping[s1]
            
    return best, best_cost

def mutate(mapping):
    m = mapping.copy()
    if random.random() < 0.1:
        s1, s2 = random.sample(right_pool, 2)
    else:
        s1, s2 = random.sample(symbols, 2)
    m[s1], m[s2] = m[s2], m[s1]
    return m

def crossover(m1, m2):
    m = {}
    used_pos = set()
    # greedy crossover for symbols
    for s in symbols:
        if random.random() < 0.5:
            p = m1[s]
        else:
            p = m2[s]
        if p not in used_pos:
            m[s] = p
            used_pos.add(p)
    # Fill remaining symbols
    free_pos = [p for p in POSITIONS if p not in used_pos]
    random.shuffle(free_pos)
    missing = [s for s in symbols if s not in m]
    for s, p in zip(missing, free_pos):
        m[s] = p
    
    # Same for right pool
    rp_used = set()
    for s in right_pool:
        if random.random() < 0.5: p = m1[s]
        else: p = m2[s]
        if p not in rp_used:
            m[s] = p
            rp_used.add(p)
    free_rp = [(None, d) for d in right_pool_dirs if (None, d) not in rp_used]
    random.shuffle(free_rp)
    missing_rp = [s for s in right_pool if s not in m]
    for s, p in zip(missing_rp, free_rp):
        m[s] = p
        
    for k, v in right_fixed_pos.items():
        m[k] = v
    return m

def genetic_algorithm():
    pop = [build_random_mapping() for _ in range(GA_POP)]
    best_overall = None
    best_cost = float('inf')
    
    for gen in tqdm(range(GA_GEN), desc="GA"):
        scored = [(total_cost(m), m) for m in pop]
        scored.sort(key=lambda x: x[0])
        if scored[0][0] < best_cost:
            best_cost = scored[0][0]
            best_overall = scored[0][1]
            
        elite = [x[1] for x in scored[:10]]
        new_pop = elite.copy()
        while len(new_pop) < GA_POP:
            p1 = random.choice(elite)
            p2 = random.choice(elite)
            child = mutate(crossover(p1, p2))
            new_pop.append(child)
        pop = new_pop
        
    return best_overall, best_cost

best_sa, cost_sa = simulated_annealing()
best_ga, cost_ga = genetic_algorithm()

if cost_sa < cost_ga:
    best_map = best_sa
    best_score = cost_sa
else:
    best_map = best_ga
    best_score = cost_ga

_, uni, bi = total_cost(best_map, True)
print("---RESULTS---")
print(f"Random Baseline: {random_baseline:.4f}")
print(f"Best Score: {best_score:.4f} (Unigram: {uni:.4f}, Bigram: {bi:.4f})")
print(f"Improvement over baseline: {((random_baseline-best_score)/random_baseline)*100:.2f}%")

print("\nFINAL 8x8 LAYOUT")
for l in DIRECTIONS:
    row=[]
    for r in DIRECTIONS:
        found = [k for k,v in best_map.items() if v==(l,r)]
        row.append(found[0] if found else "-")
    print(l, row)

print("\nRight-dial single swipe mapping:")
for k,v in right_fixed.items():
    print(f"{v} -> {k}")
for k in right_pool:
    # best_map[k] is (None, d)
    print(f"{best_map[k][1]} -> {k}")

print("\nTop 20 letters by difficulty:")
letter_diffs = []
for s in letters:
    if s in best_map:
        # User implies frequency * base difficulty, but base difficulty is just chord_difficulty(best_map[s])
        # "Top 20 letters by difficulty" usually means sorting by chord difficulty.
        letter_diffs.append((s, chord_difficulty(best_map[s])))
letter_diffs.sort(key=lambda x: x[1], reverse=True)
for k,v in letter_diffs[:20]:
    print(f"{k}: {v}")

print("\nTransition heatmap summary:")
trans = {}
for (a,b),p in bigrams.items():
    if a in best_map and b in best_map:
        trans[f"{a}->{b}"] = BIGRAM_WEIGHT * p * transition_distance(best_map[a],best_map[b])
top_trans = sorted(trans.items(), key=lambda x: x[1], reverse=True)[:10]
for k,v in top_trans:
    print(f"{k}: {v:.6f}")

print("---END_RESULTS---")
