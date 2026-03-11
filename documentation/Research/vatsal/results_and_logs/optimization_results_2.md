# ERICK Advanced Keyboard Optimization Results

**Algorithm**: Simulated Annealing — 200k iterations, asymmetric biomechanics, trigram cost  
**Corpus**: Wordfreq 25k (60% web) + Google Books 1-grams 10k (40% books) + Google Books 2-grams 5k  
**Character n-grams**: 40 unigrams, 842 bigrams, 9,880 trigrams  

---

## 1. Best 8×8 Chord Table

| Left \ Right | N | NE | E | SE | S | SW | W | NW |
|---|---|---|---|---|---|---|---|---|
| **N**  | `e` | `n` | `w` | `:` | `5` | `?` | `-` | `g` |
| **NE** | `l` | `-` | `o` | `-` | `-` | `/` | `4` | `;` |
| **E**  | `j` | `h` | `t` | `-` | `1` | `-` | `@` | `-` |
| **SE** | `6` | `-` | `s` | `a` | `y` | `3` | `8` | `2` |
| **S**  | `(` | `7` | `f` | `d` | `i` | `x` | `"` | `!` |
| **SW** | `-` | `-` | `-` | `'` | `k` | `u` | `0` | `-` |
| **W**  | `z` | `q` | `-` | `-` | `9` | `-` | `c` | `p` |
| **NW** | `m` | `b` | `-` | `#` | `-` | `)` | `v` | `r` |

*`-` = unused slot in this run (48 of the 64 used; 16 slots reserved for right-only).*

---

## 2. Right-Dial Single Swipe Mapping

| Direction | Function |
|---|---|
| **N** | SHIFT |
| **E** | SPACE |
| **W** | BACKSPACE |
| **S** | ENTER |
| **NE** | CAPSLOCK |
| **SE** | TAB |
| **SW** | `.` |
| **NW** | `,` |

---

## 3. Objective Scores

| Metric | Value |
|---|---|
| **Total Score** | **0.7337** |
| Unigram cost | 0.5886 |
| Bigram cost (×0.6) | 0.0726 |
| Trigram cost (×0.25) | 0.0725 |

---

## 4. Improvement vs Random Baseline

| | Score |
|---|---|
| Random Baseline (avg 50 runs) | 1.4101 |
| Optimized | 0.7337 |
| **Improvement** | **47.97%** |

---

## 5. Top 20 Letters by Difficulty

| Rank | Letter | Chord Difficulty | Char Frequency |
|---|---|---|---|
| 1 | q | 1.658 | 0.07% |
| 2 | f | 1.320 | 1.88% |
| 3 | z | 1.140 | 0.05% |
| 4 | b | 1.110 | 1.19% |
| 5 | j | 1.110 | 0.14% |
| 6 | w | 1.110 | 1.58% |
| 7 | k | 0.980 | 0.60% |
| 8 | x | 0.980 | 0.15% |
| 9 | d | 0.920 | 3.07% |
| 10 | y | 0.920 | 1.56% |
| 11 | s | 0.840 | 5.08% |
| 12 | p | 0.800 | 1.59% |
| 13 | v | 0.800 | 0.83% |
| 14 | h | 0.760 | 4.30% |
| 15 | o | 0.760 | 6.44% |
| 16 | g | 0.720 | 1.61% |
| 17 | m | 0.720 | 2.00% |
| 18 | l | 0.700 | 3.22% |
| 19 | n | 0.700 | 5.74% |
| 20 | u | 0.625 | 2.27% |

> High-frequency letters (e, t, a, i) are placed on the easiest chord positions, as expected.

---

## 6. Transition Heatmap — Top 10 Bigram Costs

| Bigram | Weighted Cost |
|---|---|
| e → SPACE | 0.016539 |
| r → SPACE | 0.004844 |
| SPACE → i | 0.004673 |
| SPACE → a | 0.004103 |
| SPACE → c | 0.003857 |
| n → SPACE | 0.003586 |
| d → SPACE | 0.003568 |
| y → SPACE | 0.002518 |
| SPACE → m | 0.002513 |
| SPACE → h | 0.002266 |

> Word-boundary transitions (letter ↔ SPACE) dominate, which is correct given their high frequency.
