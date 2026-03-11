# ADVANCED ERICK OPTIMIZER
# Trigram + Asymmetric Biomechanics + Right-only Dynamic Cost

import requests, json, random, math
import numpy as np
import pandas as pd
from collections import defaultdict
from tqdm import tqdm

# ---------------------------
# PARAMETERS
# ---------------------------

SA_ITERATIONS = 200000
BIGRAM_WEIGHT = 0.6
TRIGRAM_WEIGHT = 0.25

DIRECTIONS = ['N','NE','E','SE','S','SW','W','NW']
INDEX = {d:i for i,d in enumerate(DIRECTIONS)}

RIGHT_ONLY_DIR = {
    'SHIFT':    'N',
    'SPACE':    'E',
    'BACKSPACE':'W',
    'ENTER':    'S',
    'CAPSLOCK': 'NE',
    'TAB':      'SE',
    '.':        'SW',
    ',':        'NW'
}
RIGHT_ONLY_SYMBOLS = set(RIGHT_ONLY_DIR.keys())

# ---------------------------
# BIOMECHANICS
# ---------------------------

DIR_EFFORT = {
    'N':0.85, 'NE':0.9, 'E':1.0, 'SE':1.1,
    'S':1.2,  'SW':1.25,'W':1.05,'NW':0.95
}
DIFF = {0:0.5, 1:0.8, 2:1.2, 3:1.7, 4:2.4}

def circular_steps(a, b):
    diff = abs(INDEX[a] - INDEX[b])
    return min(diff, 8 - diff)

def chord_difficulty(pos):
    l, r = pos
    if l is None:
        return 0.2 * DIR_EFFORT[r]
    ang = circular_steps(l, r)
    return DIFF[ang] * (DIR_EFFORT[l] + DIR_EFFORT[r]) / 2

def transition(a, b, mapping):
    pa, pb = mapping[a], mapping[b]
    # right-dial directions
    ra = pa[1]
    rb = pb[1]
    # left-dial directions (None when right-only)
    la = pa[0]
    lb = pb[0]
    step_r = circular_steps(ra, rb)
    if la is None or lb is None:
        return step_r / 8
    step_l = circular_steps(la, lb)
    return (step_l + step_r) / 8

# ---------------------------
# CORPUS LOADING
# ---------------------------

print("Downloading corpora...")

WF_URL = "https://raw.githubusercontent.com/aparrish/wordfreq-en-25000/main/wordfreq-en-25000-log.json"
wf_raw = requests.get(WF_URL).json()
wf_words = {w: math.exp(f) for w, f in wf_raw[:25000]}

GB1_URL = "https://raw.githubusercontent.com/orgtre/google-books-ngram-frequency/main/ngrams/1grams_english.csv"
gb1 = pd.read_csv(GB1_URL).head(10000)
gb1_words = dict(zip(gb1['ngram'].astype(str), gb1['freq'].astype(float)))

GB2_URL = "https://raw.githubusercontent.com/orgtre/google-books-ngram-frequency/main/ngrams/2grams_english.csv"
gb2 = pd.read_csv(GB2_URL).head(5000)

def normalize(d):
    s = sum(d.values())
    if s == 0: return d
    return {k: float(v)/s for k, v in d.items()}

wf_words   = normalize(wf_words)
gb1_words  = normalize(gb1_words)

# Merged word unigrams: 60% web, 40% books
word_freq = defaultdict(float)
for k, v in wf_words.items():  word_freq[str(k).lower()] += 0.6 * v
for k, v in gb1_words.items(): word_freq[str(k).lower()] += 0.4 * v
word_freq = normalize(word_freq)

# ---- Build CHARACTER-level unigrams, bigrams, trigrams ----
print("Building character n-grams...")

char_uni  = defaultdict(float)
char_bi   = defaultdict(float)

# intra-word characters
for w, p in word_freq.items():
    for c in w:
        char_uni[c] += p
    for i in range(len(w) - 1):
        char_bi[(w[i], w[i+1])] += p
    char_uni['SPACE'] += p

# inter-word boundary bigrams from GB2
for _, row in gb2.iterrows():
    parts = str(row['ngram']).split()
    if len(parts) == 2:
        w1, w2 = parts[0].lower(), parts[1].lower()
        if w1 and w2:
            char_bi[(w1[-1], 'SPACE')] += float(row['freq'])
            char_bi[('SPACE', w2[0])] += float(row['freq'])

char_uni = normalize(char_uni)
char_bi  = normalize(char_bi)

# Build trigrams from bigrams (fast: keep only top bigrams)
TOP_N = 500
sorted_bi = sorted(char_bi.items(), key=lambda x: x[1], reverse=True)[:TOP_N]
bi_for_tri = dict(sorted_bi)

char_tri = defaultdict(float)
for (a, b), p in bi_for_tri.items():
    for (b2, c), p2 in bi_for_tri.items():
        if b == b2 and (a, b, c) not in char_tri:
            char_tri[(a, b, c)] = min(p, p2) * 0.1

char_tri = normalize(char_tri)

# ---------------------------
# SYMBOL SET
# ---------------------------

letters = [chr(i) for i in range(ord('a'), ord('z') + 1)]
digits  = [str(i) for i in range(10)]
punct   = ["'", ";", ":", "?", "!", "-", "(", ")", '"', "/", "@", "#"]
symbols = letters + digits + punct

all_symbols = symbols + list(RIGHT_ONLY_SYMBOLS)
sym_set     = set(all_symbols)

POSITIONS = [(l, r) for l in DIRECTIONS for r in DIRECTIONS]

# Pre-filter to only character-level entries present in our symbol set
valid_uni  = [(w, float(p))                  for w, p    in char_uni.items()  if w in sym_set]
valid_bi   = [((a, b), float(p)*BIGRAM_WEIGHT)   for (a,b), p in char_bi.items()  if a in sym_set and b in sym_set]
valid_tri  = [((a,b,c), float(p)*TRIGRAM_WEIGHT) for (a,b,c),p in char_tri.items() if a in sym_set and b in sym_set and c in sym_set]

print(f"  unigrams: {len(valid_uni)}, bigrams: {len(valid_bi)}, trigrams: {len(valid_tri)}")

# Build per-symbol index for delta cost
sym_uni_idx  = {s: [] for s in symbols}
sym_bi_idx   = {s: [] for s in symbols}
sym_tri_idx  = {s: [] for s in symbols}

for w, p in valid_uni:
    if w in sym_uni_idx:
        sym_uni_idx[w].append(p)

for (a, b), p in valid_bi:
    for s in (a, b):
        if s in sym_bi_idx:
            sym_bi_idx[s].append(((a, b), p))

for (a, b, c), p in valid_tri:
    for s in (a, b, c):
        if s in sym_tri_idx:
            sym_tri_idx[s].append(((a, b, c), p))

# Pre-build mutual (overlap) cache for pairs
mutual_bi_cache  = {}
mutual_tri_cache = {}
for i in range(len(symbols)):
    for j in range(i + 1, len(symbols)):
        s1, s2 = symbols[i], symbols[j]
        key = (s1, s2)
        mutual_bi_cache[key]  = [((a,b), p) for (a,b), p in sym_bi_idx[s1]   if s2 in (a, b)]
        mutual_tri_cache[key] = [((a,b,c), p) for (a,b,c),p in sym_tri_idx[s1] if s2 in (a,b,c)]

# ---------------------------
# COST FUNCTIONS
# ---------------------------

def total_cost(mapping):
    cost = 0.0
    for w, p in valid_uni:
        cost += p * chord_difficulty(mapping[w])
    for (a, b), p in valid_bi:
        cost += p * transition(a, b, mapping)
    for (a, b, c), p in valid_tri:
        cost += p * (transition(a, b, mapping) + transition(b, c, mapping))
    return cost

def partial_cost1(mapping, s):
    """Cost contribution of symbol s (unigram + all bigrams/trigrams involving s)."""
    c = 0.0
    for p in sym_uni_idx[s]:
        c += p * chord_difficulty(mapping[s])
    for (a, b), p in sym_bi_idx[s]:
        c += p * transition(a, b, mapping)
    for (a, b, c2), p in sym_tri_idx[s]:
        c += p * (transition(a, b, mapping) + transition(b, c2, mapping))
    return c

def mutual_cost(mapping, s1, s2):
    """Shared bigram/trigram cost counted in both partial_cost1(s1) and partial_cost1(s2)."""
    c = 0.0
    # canonical ordering matches how the cache was built (index i < j)
    i1, i2 = symbols.index(s1), symbols.index(s2)
    key = (s1, s2) if i1 < i2 else (s2, s1)
    for (a, b), p in mutual_bi_cache[key]:
        c += p * transition(a, b, mapping)
    for (a, b, c2), p in mutual_tri_cache[key]:
        c += p * (transition(a, b, mapping) + transition(b, c2, mapping))
    return c

def delta_swap_cost(mapping, s1, s2):
    """Return the CHANGE in total cost from swapping s1 and s2."""
    old = partial_cost1(mapping, s1) + partial_cost1(mapping, s2) - mutual_cost(mapping, s1, s2)
    mapping[s1], mapping[s2] = mapping[s2], mapping[s1]
    new = partial_cost1(mapping, s1) + partial_cost1(mapping, s2) - mutual_cost(mapping, s1, s2)
    return new - old   # mapping already has swap applied

# ---------------------------
# SIMULATED ANNEALING
# ---------------------------

def anneal():
    mapping = {}
    free = POSITIONS.copy()
    random.shuffle(free)
    for s, p in zip(symbols, free):
        mapping[s] = p
    for s, d in RIGHT_ONLY_DIR.items():
        mapping[s] = (None, d)

    best = mapping.copy()
    current_score = total_cost(mapping)
    best_score = current_score

    T0 = 0.05
    for i in tqdm(range(SA_ITERATIONS), desc="SA"):
        s1, s2 = random.sample(symbols, 2)
        delta = delta_swap_cost(mapping, s1, s2)   # swap already done inside
        new_score = current_score + delta
        T = T0 * (1.0 - i / SA_ITERATIONS)

        accept = (delta < 0) or (T > 1e-10 and random.random() < math.exp(-delta / T))
        if accept:
            current_score = new_score
            if current_score < best_score:
                best_score = current_score
                best = mapping.copy()
        else:
            # revert
            mapping[s1], mapping[s2] = mapping[s2], mapping[s1]

    return best, best_score

# ---------------------------
# RUN
# ---------------------------

print("Running advanced optimization...")
best_map, best_score = anneal()

# Final verified score
_, uni_c, bi_c, tri_c = 0.0, 0.0, 0.0, 0.0
for w, p in valid_uni:  uni_c += p * chord_difficulty(best_map[w])
for (a,b), p in valid_bi: bi_c += p * transition(a, b, best_map)
for (a,b,c), p in valid_tri: tri_c += p * (transition(a,b,best_map) + transition(b,c,best_map))
total_verified = uni_c + bi_c + tri_c

# Random baseline
random_scores = []
for _ in range(50):
    rm = {}
    fp = POSITIONS.copy(); random.shuffle(fp)
    for s, p in zip(symbols, fp): rm[s] = p
    for s, d in RIGHT_ONLY_DIR.items(): rm[s] = (None, d)
    random_scores.append(total_cost(rm))
baseline = sum(random_scores) / len(random_scores)
improvement = (baseline - total_verified) / baseline * 100

print("---RESULTS---")
print(f"Random Baseline: {baseline:.4f}")
print(f"Best Score: {total_verified:.4f}  (Unigram: {uni_c:.4f}, Bigram: {bi_c:.4f}, Trigram: {tri_c:.4f})")
print(f"Improvement over baseline: {improvement:.2f}%")

print("\n8x8 LAYOUT")
for l in DIRECTIONS:
    row = []
    for r in DIRECTIONS:
        found = [k for k, v in best_map.items() if v == (l, r)]
        row.append(found[0] if found else "-")
    print(l, row)

print("\nRight-dial single swipe mapping:")
for k, v in RIGHT_ONLY_DIR.items():
    print(f"  {v} -> {k}")

print("\nTop 20 letters by difficulty (chord difficulty × char frequency):")
letter_diffs = []
for s in letters:
    if s in best_map:
        freq = next((p for w, p in valid_uni if w == s), 0.0)
        letter_diffs.append((s, chord_difficulty(best_map[s]), freq))
letter_diffs.sort(key=lambda x: x[1], reverse=True)
for k, d, f in letter_diffs[:20]:
    print(f"  {k}: difficulty={d:.3f}, char_freq={f:.4f}")

print("\nTop 10 Transition Costs (bigram):")
trans_costs = []
for (a, b), p in valid_bi:
    tc = p * transition(a, b, best_map)
    trans_costs.append((f"{a}->{b}", tc))
trans_costs.sort(key=lambda x: x[1], reverse=True)
for pair, cost in trans_costs[:10]:
    print(f"  {pair}: {cost:.6f}")

print("---END_RESULTS---")