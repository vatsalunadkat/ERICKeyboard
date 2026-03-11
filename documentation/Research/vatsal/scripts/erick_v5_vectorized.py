#!/usr/bin/env python3
"""
ERICK KEYBOARD OPTIMIZER v5 (FINAL)
════════════════════════════════════════════════════════════════════
Parallel Tempering + Vectorised NumPy + Real wordfreq Corpus

FIXED vs broken original:
  1. BI/TRI not normalised → frozen chains, WPM=0, Unigram=0%
     Fix: norm(bi) and norm(tri) after corpus build

  2. Temperatures wrong scale for normalised costs
     Fix: TEMPS recalibrated to 0.0002–0.012

  3. KeyError: None in print_char_table
     Fix: '.' removed from PUNCT (it lives in UTILITY as SW)
          Defensive guard added in print_char_table

  4. Speed: 1k it/s (~2 hours for 500k steps)
     Fix: delta_cost precomputes per-symbol INTEGER row index arrays
          (not boolean masks). OR-ing two ~80-element int arrays
          is ~50× faster than OR-ing two 9,928-element bool arrays.
          Expected: ~8–12k it/s → 500k steps in ~10 minutes
"""

import math
import time
import numpy as np

try:
    from wordfreq import zipf_frequency, top_n_list
    WORDFREQ_OK = True
except ImportError:
    WORDFREQ_OK = False

try:
    from tqdm import tqdm
except ImportError:
    class tqdm:
        def __init__(self, iterable, **kw): self._it = iter(iterable)
        def __iter__(self): return self._it
        def set_postfix(self, **kw): pass
        @staticmethod
        def write(s): print(s)

# ════════════════════════════════════════════════════════════════════
# TUNABLE PARAMETERS
# ════════════════════════════════════════════════════════════════════

CHAINS          = 8
STEPS_PER_CHAIN = 500_000
SWAP_INTERVAL   = 200

# Calibrated for normalised costs (range ~0.05–0.5).
# Highest T accepts ~40% uphill moves; lowest T <0.1%.
# Typical delta ≈ 0.001–0.01 on normalised scale.
TEMPS = [0.012, 0.008, 0.005, 0.003, 0.0018, 0.001, 0.0005, 0.0002]

BIGRAM_WEIGHT   = 0.6
TRIGRAM_WEIGHT  = 0.3

DUAL_THUMB_PENALTY   = 1.0
SINGLE_THUMB_PENALTY = 0.25
ALT_THUMB_BONUS      = 0.90   # 10% off when dominant thumb alternates L↔R
ROLLING_BONUS        = 0.92   # 8% off on 2nd transition in a same-thumb roll

N_HEURISTIC_CHAINS = 2

BASE_KEY_TIME         = 0.08
EFFORT_TIME_SCALE     = 0.12
TRANSITION_TIME_SCALE = 0.04

# ════════════════════════════════════════════════════════════════════
# CORPUS
# ════════════════════════════════════════════════════════════════════

print("Building corpus…")

def build_corpus():
    from collections import defaultdict

    if not WORDFREQ_OK:
        raise RuntimeError("wordfreq not installed — run: pip install wordfreq")

    print("  Fetching wordfreq corpus (top 50k words)…")
    words = top_n_list("en", 50_000)

    uni = defaultdict(float)
    bi  = defaultdict(float)
    tri = defaultdict(float)

    for w in words:
        freq = 10 ** (zipf_frequency(w, "en") - 5)
        if freq <= 0:
            continue
        for i, c in enumerate(w):
            uni[c] += freq
            if i < len(w) - 1:
                bi[(w[i], w[i+1])]          += freq
            if i < len(w) - 2:
                tri[(w[i], w[i+1], w[i+2])] += freq

        uni["SPACE"] += freq
        bi[(w[-1], "SPACE")] += freq
        bi[("SPACE",  w[0])] += freq
        if len(w) >= 2:
            tri[(w[-2], w[-1], "SPACE")]  += freq
            tri[(w[-1], "SPACE",  w[0])]  += freq
        if len(w) >= 3:
            tri[("SPACE", w[0], w[1])]    += freq

    # FIX 1: normalise ALL THREE dicts to sum = 1
    # Without this: BI/TRI sums ~500k → costs ~258k → temps 0.08
    # are useless (acceptance = exp(-Δ/0.08) ≈ exp(-12500) ≈ 0)
    def norm(d):
        s = sum(d.values())
        return {k: v / s for k, v in d.items()}

    uni = norm(uni)
    bi  = norm(bi)   # ← was missing
    tri = norm(tri)  # ← was missing

    print(f"  Corpus scale — UNI: {sum(uni.values()):.4f}  "
          f"BI: {sum(bi.values()):.4f}  "
          f"TRI: {sum(tri.values()):.4f}")
    print(f"  (all must be ~1.0)")
    return uni, bi, tri

char_uni, char_bi, char_tri = build_corpus()

# ════════════════════════════════════════════════════════════════════
# KEYBOARD GEOMETRY
# ════════════════════════════════════════════════════════════════════

DIRS = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
IDX  = {d: i for i, d in enumerate(DIRS)}
ND   = 8

UTILITY = {
    "SHIFT":     "N",   # FIXED
    "SPACE":     "E",   # FIXED
    "BACKSPACE": "W",   # FIXED
    "ENTER":     "S",   # FIXED
    "CAPSLOCK":  "NE",  # flexible
    "TAB":       "SE",  # flexible
    ".":         "SW",  # flexible — period
    ",":         "NW",  # flexible — comma
}
UTIL_KEYS  = list(UTILITY.keys())
UTIL_RIGHT = {k: IDX[d] for k, d in UTILITY.items()}
N_UTIL     = len(UTILITY)

L_EFF = np.array([0.95, 0.98, 1.00, 1.08, 1.18, 1.30, 1.15, 1.03])
R_EFF = np.array([0.88, 0.92, 0.95, 1.02, 1.12, 1.20, 1.05, 0.98])
SEP   = np.array([0.5,  0.8,  1.2,  1.7,  2.4])
ANG   = np.array([[min(abs(i-j), ND-abs(i-j)) for j in range(ND)]
                  for i in range(ND)], dtype=int)

# ════════════════════════════════════════════════════════════════════
# SYMBOL SET
# ════════════════════════════════════════════════════════════════════

LETTERS = list("abcdefghijklmnopqrstuvwxyz")
DIGITS  = list("0123456789")

# FIX 3: '.' and ',' must NOT appear here — they live in UTILITY.
# If a char is in both SYMBOLS and UTILITY then layout_to_map returns
# (None, dir) for it, but print_char_table does IDX[None] → KeyError.
PUNCT   = list("'-!?:;\"()/@#")   # no '.' or ','

SYMBOLS  = LETTERS + DIGITS + PUNCT   # 48 chord-assigned chars
N_SYM    = len(SYMBOLS)
ALL_POS  = [(l, r) for l in range(ND) for r in range(ND)]  # 64 positions
N_POS    = len(ALL_POS)

print(f"  Chord symbols: {N_SYM}  |  Utility: {N_UTIL}")

# ════════════════════════════════════════════════════════════════════
# PRECOMPUTED COST TABLES
# ════════════════════════════════════════════════════════════════════

CHORD_DIFF = np.array([
    DUAL_THUMB_PENALTY * SEP[ANG[l, r]] * (L_EFF[l] + R_EFF[r]) / 2
    for l, r in ALL_POS
], dtype=np.float64)

def _trans(pi, pj):
    li, ri = ALL_POS[pi]; lj, rj = ALL_POS[pj]
    dl, dr = ANG[li, lj], ANG[ri, rj]
    return (dl + dr) / 8.0 * (ALT_THUMB_BONUS if dl != dr else 1.0)

TRANS = np.array([[_trans(i, j) for j in range(N_POS)]
                  for i in range(N_POS)], dtype=np.float64)

R_POS = np.array([r for l, r in ALL_POS], dtype=int)
L_POS = np.array([l for l, r in ALL_POS], dtype=int)

UTIL_R_IDX    = np.array([UTIL_RIGHT[k] for k in UTIL_KEYS], dtype=int)
UTIL_TO_CHORD = ANG[UTIL_R_IDX[:, None], R_POS[None, :]] / 8.0  # (N_UTIL, N_POS)
CHORD_TO_UTIL = UTIL_TO_CHORD.T                                   # (N_POS, N_UTIL)

# ════════════════════════════════════════════════════════════════════
# CORPUS → NUMERIC ARRAYS
# ════════════════════════════════════════════════════════════════════

def _si(ch): return SYMBOLS.index(ch)    if ch in SYMBOLS   else None
def _ui(ch): return UTIL_KEYS.index(ch)  if ch in UTIL_KEYS else None

UNI_SI, UNI_F = [], []
for ch in SYMBOLS:
    f = char_uni.get(ch, 0.0)
    if f > 0:
        UNI_SI.append(SYMBOLS.index(ch)); UNI_F.append(f)
UNI_SI = np.array(UNI_SI, int);  UNI_F = np.array(UNI_F)

CC_SI1, CC_SI2, CC_F = [], [], []
UC_UI,  UC_SI,  UC_F = [], [], []
CU_SI,  CU_UI,  CU_F = [], [], []

for (a, b), f in char_bi.items():
    if f <= 0: continue
    sa, ua = _si(a), _ui(a)
    sb, ub = _si(b), _ui(b)
    fw = f * BIGRAM_WEIGHT
    if   sa is not None and sb is not None:
        CC_SI1.append(sa); CC_SI2.append(sb); CC_F.append(fw)
    elif ua is not None and sb is not None:
        UC_UI.append(ua);  UC_SI.append(sb);  UC_F.append(fw)
    elif sa is not None and ub is not None:
        CU_SI.append(sa);  CU_UI.append(ub);  CU_F.append(fw)

CC_SI1 = np.array(CC_SI1, int); CC_SI2 = np.array(CC_SI2, int); CC_F = np.array(CC_F)
UC_UI  = np.array(UC_UI,  int); UC_SI  = np.array(UC_SI,  int); UC_F = np.array(UC_F)
CU_SI  = np.array(CU_SI,  int); CU_UI  = np.array(CU_UI,  int); CU_F = np.array(CU_F)

print(f"  Bigrams: {len(CC_F):,} CC + {len(UC_F):,} UC + {len(CU_F):,} CU")

TRI_TYPES = {"CCC": ([], [], [], []),
             "UCC": ([], [], [], []),
             "CCU": ([], [], [], []),
             "CUC": ([], [], [], [])}

for (a, b, c), f in char_tri.items():
    if f <= 0: continue
    sa, ua = _si(a), _ui(a)
    sb, ub = _si(b), _ui(b)
    sc, uc = _si(c), _ui(c)
    fw = f * TRIGRAM_WEIGHT
    if   sa is not None and sb is not None and sc is not None:
        t = TRI_TYPES["CCC"]; t[0].append(sa); t[1].append(sb); t[2].append(sc); t[3].append(fw)
    elif ua is not None and sb is not None and sc is not None:
        t = TRI_TYPES["UCC"]; t[0].append(ua); t[1].append(sb); t[2].append(sc); t[3].append(fw)
    elif sa is not None and sb is not None and uc is not None:
        t = TRI_TYPES["CCU"]; t[0].append(sa); t[1].append(sb); t[2].append(uc); t[3].append(fw)
    elif sa is not None and ub is not None and sc is not None:
        t = TRI_TYPES["CUC"]; t[0].append(sa); t[1].append(ub); t[2].append(sc); t[3].append(fw)

TRI = {}
for ttype, (i0, i1, i2, wts) in TRI_TYPES.items():
    if i0:
        TRI[ttype] = (np.array(i0, int), np.array(i1, int),
                      np.array(i2, int), np.array(wts))

total_tri = sum(len(v[0]) for v in TRI.values())
print(f"  Trigrams: {total_tri:,} "
      f"({'+ '.join(f'{len(TRI[t][0])}{t}' for t in TRI)})")

# ════════════════════════════════════════════════════════════════════
# VECTORISED COST FUNCTION
# ════════════════════════════════════════════════════════════════════

def total_cost(layout: np.ndarray) -> float:
    c1  = float(np.dot(UNI_F, CHORD_DIFF[layout[UNI_SI]]))
    c2  = float(np.dot(CC_F, TRANS[layout[CC_SI1], layout[CC_SI2]]))
    c2 += float(np.dot(UC_F, UTIL_TO_CHORD[UC_UI, layout[UC_SI]]))
    c2 += float(np.dot(CU_F, CHORD_TO_UTIL[layout[CU_SI], CU_UI]))
    c3  = 0.0

    if "CCC" in TRI:
        i0, i1, i2, wts = TRI["CCC"]
        t12 = TRANS[layout[i0], layout[i1]]
        t23 = TRANS[layout[i1], layout[i2]]
        rp0=R_POS[layout[i0]]; rp1=R_POS[layout[i1]]; rp2=R_POS[layout[i2]]
        lp0=L_POS[layout[i0]]; lp1=L_POS[layout[i1]]; lp2=L_POS[layout[i2]]
        roll = ((ANG[lp0,lp1]>0)&(ANG[lp1,lp2]>0)) | ((ANG[rp0,rp1]>0)&(ANG[rp1,rp2]>0))
        c3 += float(np.dot(wts, t12 + t23 * np.where(roll, ROLLING_BONUS, 1.0)))
    if "UCC" in TRI:
        i0, i1, i2, wts = TRI["UCC"]
        c3 += float(np.dot(wts, UTIL_TO_CHORD[i0, layout[i1]]
                               + TRANS[layout[i1], layout[i2]]))
    if "CCU" in TRI:
        i0, i1, i2, wts = TRI["CCU"]
        c3 += float(np.dot(wts, TRANS[layout[i0], layout[i1]]
                               + CHORD_TO_UTIL[layout[i1], i2]))
    if "CUC" in TRI:
        i0, i1, i2, wts = TRI["CUC"]
        c3 += float(np.dot(wts, CHORD_TO_UTIL[layout[i0], i1]
                               + UTIL_TO_CHORD[i1, layout[i2]]))
    return c1 + c2 + c3

# ════════════════════════════════════════════════════════════════════
# DELTA-COST — precomputed INTEGER index arrays (the speed fix)
# ════════════════════════════════════════════════════════════════════
#
# FIX 4: Previous version stored per-symbol BOOLEAN masks (length = N_bigrams
# or N_trigrams). The union step `mask[s1] | mask[s2]` allocates and scans
# full 9,928-element arrays on every step → bottleneck.
#
# New approach: store INTEGER row indices for each symbol (typically 50–150
# entries). Union is np.union1d on two small arrays → ~50× less work.

print("\nPrecomputing adjacency masks…")

# Bigram index lists per symbol
_CC_ROWS  = [np.where((CC_SI1 == s) | (CC_SI2 == s))[0] for s in range(N_SYM)]
_UC_ROWS  = [np.where(UC_SI  == s)[0]                    for s in range(N_SYM)]
_CU_ROWS  = [np.where(CU_SI  == s)[0]                    for s in range(N_SYM)]
_UNI_ROWS = [np.where(UNI_SI == s)[0]                    for s in range(N_SYM)]

# Trigram index lists per symbol — only CCC (the large one); others handled inline
if "CCC" in TRI:
    _ccc_i0, _ccc_i1, _ccc_i2, _ccc_wts = TRI["CCC"]
    _CCC_ROWS = [
        np.where((_ccc_i0 == s) | (_ccc_i1 == s) | (_ccc_i2 == s))[0]
        for s in range(N_SYM)
    ]
else:
    _ccc_i0 = _ccc_i1 = _ccc_i2 = _ccc_wts = np.array([])
    _CCC_ROWS = [np.array([], int) for _ in range(N_SYM)]

# Same for UCC/CCU/CUC but they're small so boolean masks are fine there
print("  ✓ Ready\n")


def delta_cost(s1: int, s2: int, layout: np.ndarray) -> float:
    # Union of affected row indices — small integer arrays, fast
    cc_r   = np.union1d(_CC_ROWS[s1],  _CC_ROWS[s2])
    uc_r   = np.union1d(_UC_ROWS[s1],  _UC_ROWS[s2])
    cu_r   = np.union1d(_CU_ROWS[s1],  _CU_ROWS[s2])
    uni_r  = np.union1d(_UNI_ROWS[s1], _UNI_ROWS[s2])
    ccc_r  = np.union1d(_CCC_ROWS[s1], _CCC_ROWS[s2])

    def _score(lay):
        d = 0.0
        if uni_r.size:
            d += float(np.dot(UNI_F[uni_r], CHORD_DIFF[lay[UNI_SI[uni_r]]]))
        if cc_r.size:
            d += float(np.dot(CC_F[cc_r], TRANS[lay[CC_SI1[cc_r]], lay[CC_SI2[cc_r]]]))
        if uc_r.size:
            d += float(np.dot(UC_F[uc_r], UTIL_TO_CHORD[UC_UI[uc_r], lay[UC_SI[uc_r]]]))
        if cu_r.size:
            d += float(np.dot(CU_F[cu_r], CHORD_TO_UTIL[lay[CU_SI[cu_r]], CU_UI[cu_r]]))
        if ccc_r.size:
            d += float(np.dot(_ccc_wts[ccc_r],
                               TRANS[lay[_ccc_i0[ccc_r]], lay[_ccc_i1[ccc_r]]]
                             + TRANS[lay[_ccc_i1[ccc_r]], lay[_ccc_i2[ccc_r]]]))
        # UCC/CCU/CUC are small — boolean mask fine here
        if "UCC" in TRI:
            i0, i1, i2, wts = TRI["UCC"]
            m = (i1 == s1) | (i1 == s2) | (i2 == s1) | (i2 == s2)
            if m.any():
                d += float(np.dot(wts[m], UTIL_TO_CHORD[i0[m], lay[i1[m]]]
                                        + TRANS[lay[i1[m]], lay[i2[m]]]))
        if "CCU" in TRI:
            i0, i1, i2, wts = TRI["CCU"]
            m = (i0 == s1) | (i0 == s2) | (i1 == s1) | (i1 == s2)
            if m.any():
                d += float(np.dot(wts[m], TRANS[lay[i0[m]], lay[i1[m]]]
                                        + CHORD_TO_UTIL[lay[i1[m]], i2[m]]))
        if "CUC" in TRI:
            i0, i1, i2, wts = TRI["CUC"]
            m = (i0 == s1) | (i0 == s2) | (i2 == s1) | (i2 == s2)
            if m.any():
                d += float(np.dot(wts[m], CHORD_TO_UTIL[lay[i0[m]], i1[m]]
                                        + UTIL_TO_CHORD[i1[m], lay[i2[m]]]))
        return d

    before = _score(layout)
    layout[s1], layout[s2] = layout[s2], layout[s1]
    after  = _score(layout)
    layout[s1], layout[s2] = layout[s2], layout[s1]   # restore
    return after - before

# ════════════════════════════════════════════════════════════════════
# LAYOUT INITIALISATION
# ════════════════════════════════════════════════════════════════════

EASE_ORDER = np.argsort(CHORD_DIFF).tolist()

def random_layout(rng: np.random.Generator) -> np.ndarray:
    return rng.permutation(N_POS)[:N_SYM].astype(int)

def heuristic_layout(rng: np.random.Generator) -> np.ndarray:
    sym_freq = np.zeros(N_SYM)
    for k, si in enumerate(UNI_SI):
        sym_freq[si] = UNI_F[k]
    freq_order = np.argsort(-sym_freq).tolist()
    layout = np.empty(N_SYM, dtype=int)
    for rank, sym_i in enumerate(freq_order):
        layout[sym_i] = EASE_ORDER[rank]
    # Small perturbation so heuristic chains differ
    for _ in range(300):
        s1, s2 = int(rng.integers(N_SYM)), int(rng.integers(N_SYM))
        layout[s1], layout[s2] = layout[s2], layout[s1]
    return layout

# ════════════════════════════════════════════════════════════════════
# PARALLEL TEMPERING
# ════════════════════════════════════════════════════════════════════

def run() -> tuple:
    rngs = [np.random.default_rng(seed=i * 13 + 7) for i in range(CHAINS)]

    layouts = [
        heuristic_layout(rngs[i]) if i < N_HEURISTIC_CHAINS else random_layout(rngs[i])
        for i in range(CHAINS)
    ]

    scores      = [total_cost(l) for l in layouts]
    best_idx    = int(np.argmin(scores))
    best_layout = layouts[best_idx].copy()
    best_score  = scores[best_idx]
    chain_bests = list(scores)

    t0  = time.time()
    LOG = 50_000

    print(f"Running optimizer…\n")
    bar = tqdm(range(STEPS_PER_CHAIN), desc="Optimizing", ncols=100)

    for step in bar:

        for i in range(CHAINS):
            lay = layouts[i]; T = TEMPS[i]; rng = rngs[i]
            s1 = int(rng.integers(N_SYM))
            s2 = int(rng.integers(N_SYM))
            if s1 == s2: continue

            d = delta_cost(s1, s2, lay)

            if d < 0 or rng.random() < math.exp(-d / T):
                lay[s1], lay[s2] = lay[s2], lay[s1]
                scores[i] += d
                if scores[i] < chain_bests[i]: chain_bests[i] = scores[i]
                if scores[i] < best_score:
                    best_score  = scores[i]
                    best_layout = lay.copy()

        if step % SWAP_INTERVAL == 0:
            for i in range(CHAINS - 1):
                d_swap = (scores[i+1]-scores[i]) * (1/TEMPS[i] - 1/TEMPS[i+1])
                if d_swap < 0 or np.random.random() < math.exp(-d_swap):
                    layouts[i], layouts[i+1] = layouts[i+1], layouts[i]
                    scores[i],  scores[i+1]  = scores[i+1],  scores[i]

        bar.set_postfix(best=f"{best_score:.5f}")

        if step % LOG == 0 and step > 0:
            elapsed = time.time() - t0
            rate    = step * CHAINS / elapsed
            cs = "  ".join(f"C{i}:{chain_bests[i]:.5f}" for i in range(CHAINS))
            tqdm.write(f"  step={step:7,}  {cs}  "
                       f"best={best_score:.5f}  {rate/1000:.0f}k it/s  {elapsed:.0f}s")

    elapsed = time.time() - t0
    print(f"\nDone in {elapsed:.1f}s  "
          f"({STEPS_PER_CHAIN * CHAINS / elapsed / 1000:.0f}k it/s effective)")
    return best_layout, best_score

# ════════════════════════════════════════════════════════════════════
# ANALYSIS & OUTPUT
# ════════════════════════════════════════════════════════════════════

def layout_to_map(layout: np.ndarray) -> dict:
    m = {}
    for si in range(N_SYM):
        l, r = ALL_POS[int(layout[si])]
        m[SYMBOLS[si]] = (DIRS[l], DIRS[r])
    for k, d in UTILITY.items():
        m[k] = (None, d)
    return m

def print_grid(layout: np.ndarray, title="ERICK v5 LAYOUT"):
    pos_sym = {int(layout[si]): SYMBOLS[si] for si in range(N_SYM)}
    print(f"\n{'═'*68}")
    print(f"  {title}")
    print(f"{'═'*68}")
    print(f"\n{'':12}" + "".join(f"{d:>7}" for d in DIRS))
    print("  L \\ R     " + "─"*58)
    for li, l in enumerate(DIRS):
        row = f"  {l:<7}  | "
        for ri, r in enumerate(DIRS):
            cell = pos_sym.get(li*ND+ri, "·")
            row += f"{str(cell):>7}"
        print(row)
    print(f"\n  UTILITY (right-dial single-swipe):")
    for k, d in UTILITY.items():
        tag = " [FIXED]" if k in ("SHIFT","SPACE","BACKSPACE","ENTER") else ""
        print(f"    {d} → {k}{tag}")

def cost_breakdown(layout: np.ndarray) -> tuple:
    c1  = float(np.dot(UNI_F, CHORD_DIFF[layout[UNI_SI]]))
    c2  = float(np.dot(CC_F, TRANS[layout[CC_SI1], layout[CC_SI2]]))
    c2 += float(np.dot(UC_F, UTIL_TO_CHORD[UC_UI, layout[UC_SI]]))
    c2 += float(np.dot(CU_F, CHORD_TO_UTIL[layout[CU_SI], CU_UI]))
    c3 = 0.0
    if "CCC" in TRI:
        i0, i1, i2, wts = TRI["CCC"]
        c3 += float(np.dot(wts, TRANS[layout[i0], layout[i1]]
                               + TRANS[layout[i1], layout[i2]]))
    return c1, c2, c3

def estimate_wpm(layout: np.ndarray) -> float:
    m = layout_to_map(layout)
    total_time = 0.0
    for (a, b), prob in char_bi.items():
        pa, pb = m.get(a), m.get(b)
        if pa is None or pb is None: continue
        la, ra = pa;  lb, rb = pb
        if la is None:
            effort = SINGLE_THUMB_PENALTY * R_EFF[IDX[ra]]
        else:
            effort = DUAL_THUMB_PENALTY * SEP[ANG[IDX[la], IDX[ra]]] \
                     * (L_EFF[IDX[la]] + R_EFF[IDX[ra]]) / 2
        if la is None or lb is None:
            tc = ANG[IDX[ra], IDX[rb]] / 8.0
        else:
            dl = ANG[IDX[la], IDX[lb]]; dr = ANG[IDX[ra], IDX[rb]]
            tc = (dl+dr) / 8.0 * (ALT_THUMB_BONUS if dl != dr else 1.0)
        total_time += prob * (BASE_KEY_TIME + EFFORT_TIME_SCALE*effort
                              + TRANSITION_TIME_SCALE*tc)
    return (1.0/total_time)*60/5 if total_time > 0 else 0.0

def cluster_compactness(layout: np.ndarray) -> float:
    sym_freq = {SYMBOLS[si]: float(UNI_F[k]) for k, si in enumerate(UNI_SI)}
    top10 = sorted([(ch,f) for ch,f in sym_freq.items() if ch in LETTERS],
                   key=lambda x: -x[1])[:10]
    coords = []
    for ch, _ in top10:
        si = SYMBOLS.index(ch)
        l, r = ALL_POS[int(layout[si])]
        coords.append((l, r))
    if not coords: return float("nan")
    c = np.mean(coords, axis=0)
    return float(np.mean([np.linalg.norm(np.array(p)-c) for p in coords]))

def print_char_table(layout: np.ndarray, n=35):
    m = layout_to_map(layout)
    sym_freq = {SYMBOLS[si]: float(UNI_F[k]) for k, si in enumerate(UNI_SI)}
    R_SPC_IDX = IDX[UTILITY["SPACE"]]

    print(f"\n{'─'*64}")
    print(f"  TOP {n} CHARACTERS BY FREQUENCY")
    print(f"{'─'*64}")
    print(f"  {'Ch':<5} {'L+R':<14} {'Diff':>7} {'Freq%':>8}  →SPC")
    print(f"  {'─'*52}")

    for ch in sorted(SYMBOLS, key=lambda s: -sym_freq.get(s, 0))[:n]:
        if sym_freq.get(ch, 0) == 0: continue
        la, ra = m[ch]
        # FIX 3 guard: skip if somehow a utility char ended up here
        if la is None: continue
        pos_i  = IDX[la] * ND + IDX[ra]
        diff   = CHORD_DIFF[pos_i]
        freq   = sym_freq[ch] * 100
        steps  = ANG[IDX[ra], R_SPC_IDX]
        bar    = "○"*(4-steps) + "●"*steps
        print(f"  {str(ch):<5} {la+'+'+ra:<14} {diff:>7.3f} {freq:>7.3f}%  {bar}")

def print_bigram_table(layout: np.ndarray, n=25):
    m = layout_to_map(layout)
    rows = []
    for (a, b), f in char_bi.items():
        pa, pb = m.get(a), m.get(b)
        if pa is None or pb is None: continue
        la, ra = pa;  lb, rb = pb
        if la is None or lb is None:
            tc = ANG[IDX[ra], IDX[rb]] / 8.0
        else:
            dl = ANG[IDX[la], IDX[lb]]; dr = ANG[IDX[ra], IDX[rb]]
            tc = (dl+dr)/8.0 * (ALT_THUMB_BONUS if dl!=dr else 1.0)
        fa = f"{la}+{ra}" if la else f"util({ra})"
        fb = f"{lb}+{rb}" if lb else f"util({rb})"
        rows.append((a, b, f, fa, fb, tc))

    print(f"\n{'─'*72}")
    print(f"  TOP {n} BIGRAMS")
    print(f"{'─'*72}")
    print(f"  {'BG':<10} {'From':<14} {'To':<14} {'Trans':>7}  {'Freq/M':>10}")
    print(f"  {'─'*60}")
    for a, b, f, fa, fb, tc in sorted(rows, key=lambda x: -x[2])[:n]:
        print(f"  '{a}{b}'  {fa:<14} {fb:<14} {tc:>7.3f}  {f*1e6:>10.1f}")

def print_trigram_table(layout: np.ndarray, n=20):
    m = layout_to_map(layout)
    def _tc(px, py):
        lx, rx = px;  ly, ry = py
        if lx is None or ly is None: return ANG[IDX[rx], IDX[ry]] / 8.0
        dl = ANG[IDX[lx], IDX[ly]]; dr = ANG[IDX[rx], IDX[ry]]
        return (dl+dr)/8.0 * (ALT_THUMB_BONUS if dl!=dr else 1.0)
    rows = []
    for (a, b, c), f in char_tri.items():
        pa, pb, pc = m.get(a), m.get(b), m.get(c)
        if None in (pa, pb, pc): continue
        la,ra=pa; lb,rb=pb; lc,rc=pc
        fa = f"{la}+{ra}" if la else f"u({ra})"
        fb = f"{lb}+{rb}" if lb else f"u({rb})"
        fc = f"{lc}+{rc}" if lc else f"u({rc})"
        rows.append((a,b,c,f,fa,fb,fc,_tc(pa,pb)+_tc(pb,pc)))

    print(f"\n{'─'*76}")
    print(f"  TOP {n} TRIGRAMS")
    print(f"{'─'*76}")
    print(f"  {'TG':<8} {'A → B → C':<42} {'Cost':>7}  {'Freq/M':>9}")
    print(f"  {'─'*64}")
    for a,b,c,f,fa,fb,fc,tc in sorted(rows, key=lambda x: -x[3])[:n]:
        flow = f"{fa} → {fb} → {fc}"
        print(f"  '{a+b+c}'  {flow:<42} {tc:>7.3f}  {f*1e6:>9.1f}")

# ════════════════════════════════════════════════════════════════════
# MAIN
# ════════════════════════════════════════════════════════════════════

if __name__ == "__main__":
    print("╔══════════════════════════════════════════════════════════════╗")
    print("║  ERICK v5 (FINAL) — Parallel Tempering Optimizer            ║")
    print("║  Normalised corpus · Integer index masks · ~8–12k it/s      ║")
    print("╚══════════════════════════════════════════════════════════════╝\n")

    best_layout, best_score = run()

    print("\nComputing baseline (200 random layouts)…")
    rng0     = np.random.default_rng(0)
    baseline = [total_cost(random_layout(rng0)) for _ in range(200)]
    b_mean   = float(np.mean(baseline))
    b_std    = float(np.std(baseline))
    improvement = (b_mean - best_score) / b_mean * 100

    c1, c2, c3 = cost_breakdown(best_layout)
    total       = c1 + c2 + c3
    wpm         = estimate_wpm(best_layout)
    compactness = cluster_compactness(best_layout)

    print(f"\n{'═'*60}")
    print(f"  RESULTS SUMMARY")
    print(f"{'═'*60}")
    print(f"  Final score    : {best_score:.5f}")
    print(f"  Baseline mean  : {b_mean:.5f}  (±{b_std:.5f})")
    print(f"  Improvement    : {improvement:.1f}%  ({(b_mean-best_score)/b_std:.1f}σ)")
    print(f"  Cluster spread : {compactness:.3f}")
    print(f"  Predicted WPM  : {wpm:.1f}")
    print(f"{'─'*60}")
    print(f"  Cost breakdown:")
    print(f"    Unigram  (×1.0): {c1:.5f}  ({c1/total*100:.1f}%)")
    print(f"    Bigram   (×{BIGRAM_WEIGHT}): {c2:.5f}  ({c2/total*100:.1f}%)")
    print(f"    Trigram  (×{TRIGRAM_WEIGHT}): {c3:.5f}  ({c3/total*100:.1f}%)")
    print(f"{'═'*60}")

    print_grid(best_layout)
    print_char_table(best_layout, n=35)
    print_bigram_table(best_layout, n=25)
    print_trigram_table(best_layout, n=20)