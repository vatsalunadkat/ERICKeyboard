# ============================================================
# ERICK CORPUS EXTRACTOR — run this where wordfreq is installed
# then paste the output back
# ============================================================
from wordfreq import zipf_frequency, top_n_list
from collections import defaultdict
import json

print("Building corpus from wordfreq top 50k words...")
words = top_n_list("en", 50000)

char_uni = defaultdict(float)
char_bi  = defaultdict(float)
char_tri = defaultdict(float)

for w in words:
    freq = 10 ** (zipf_frequency(w, "en") - 5)
    if freq <= 0:
        continue
    for i, c in enumerate(w):
        char_uni[c] += freq
        if i < len(w)-1: char_bi[(w[i], w[i+1])] += freq
        if i < len(w)-2: char_tri[(w[i], w[i+1], w[i+2])] += freq
    char_uni["SPACE"] += freq
    char_bi[(w[-1], "SPACE")] += freq
    char_bi[("SPACE", w[0])]  += freq
    if len(w) >= 2:
        char_tri[(w[-2], w[-1], "SPACE")] += freq
        char_tri[(w[-1], "SPACE", w[0])]  += freq

# Normalize
def norm(d): s = sum(d.values()); return {k: v/s for k,v in d.items()}
char_uni = norm(char_uni)
char_bi  = norm(char_bi)
char_tri = norm(char_tri)

# ── Print in paste-ready format ──────────────────────────────
print("\n# ============ PASTE FROM HERE ============\n")

print("CORPUS_UNI =", json.dumps(
    {k: round(v, 8) for k,v in
     sorted(char_uni.items(), key=lambda x: -x[1])},
    indent=2
))

print("\nCORPUS_BI =", json.dumps(
    [[a, b, round(v, 8)] for (a,b),v in
     sorted(char_bi.items(), key=lambda x: -x[1])[:300]],
    indent=2
))

print("\nCORPUS_TRI =", json.dumps(
    [[a, b, c, round(v, 8)] for (a,b,c),v in
     sorted(char_tri.items(), key=lambda x: -x[1])[:150]],
    indent=2
))

print("\n# ============ END PASTE ============")
print(f"\nStats: {len(char_uni)} unigrams, {len(char_bi)} bigrams, {len(char_tri)} trigrams")