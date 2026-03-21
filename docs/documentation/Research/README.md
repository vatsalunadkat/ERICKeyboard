# ERICK — Research Documentation

This directory contains the research foundation for the ERICK keyboard, including layout optimization experiments, academic references, and design assets.

---

## Overview

The ERICK layout optimization pipeline seeks to minimize typing effort for a two-joystick chorded keyboard with 64 chord positions (8 directions × 8 directions). The optimizer places characters on chords such that high-frequency letters and common letter transitions are assigned to the most ergonomically efficient positions.

Key research goals:
1. Quantify chord effort using a biomechanical model (direction difficulty, dual-thumb penalties, alternation bonuses)
2. Optimize character placement across unigram, bigram, and trigram frequency data
3. Validate that the optimized layout significantly outperforms alphabetical and random baselines

---

## Key Results

| Metric | Value |
|---|---|
| Optimization method | Parallel Tempering (8 chains, 500k steps each) |
| Corpus | 50,000 words from `wordfreq` database |
| Final objective cost | 0.862 |
| Random baseline cost | 1.557 ± 0.124 |
| **Improvement over baseline** | **44.6%** (5.6σ) |
| Predicted WPM | 73.2 |
| Optimizer runtime | ~29 minutes |

### Best Layout (v5)

```
              N     NE      E     SE      S     SW      W     NW
 L \ R    ──────────────────────────────────────────────────────────
 N       |   t      s      g      7      ·      ·      4      k
 NE      |   i      a      n      p      #      ·      @      '
 E       |   v      l      e      r      x      ·      ·      /
 SE      |   "      y      d      o      m      !      ·      ·
 S       |   ·      6      b      f      u      )      ?      ·
 SW      |   ·      ·      ·      5      q      j      ;      8
 W       |   :      (      ·      ·      9      2      3      z
 NW      |   h      w      1      ·      ·      -      0      c
```

Most efficient placements: **e** (E+E), **t** (N+N), **a** (NE+NE) — the three most common English letters on the easiest same-direction chords.

---

## Directory Structure

```
Research/
├── README.md                        ← This file
├── final_layout.png                 ← Visual of the final optimized layout
├── infographic.png / infographic_1.png
├── react_final_layout.png
├── Technical Layout Specification…  ← OrbiTouch-inspired chord keyboard spec
├── Technical Specification…         ← Advanced mapping (Accessibility/Legacy/Pro)
│
└── vatsal/
    ├── erick_v5_vectorized.py       ← Main optimizer (Parallel Tempering, vectorized)
    ├── v5_output.txt                ← v5 results (44.6% improvement, 73.2 WPM)
    │
    ├── layout_design/               ← Visual mockups, wireframes, React visualizer
    │   ├── final_layout.png / .psd
    │   ├── joystick_wireframe.drawio / .svg / .png
    │   ├── left_dial.png
    │   └── layout_react_app/        ← Interactive layout visualizer (React)
    │
    ├── results_and_logs/
    │   ├── erick_keyboard_report.md         ← Literature review + 3 layout variants
    │   ├── optimization_results.md          ← First optimizer run (40% improvement)
    │   ├── optimization_results_2.md        ← SA run (25k corpus + Google N-grams)
    │   └── *.txt, *.log                     ← Raw optimizer logs and error traces
    │
    ├── research_papers/             ← 43 academic papers (see below)
    │
    └── scripts/
        ├── erick_v5_vectorized.py           ← Copy of main optimizer
        ├── erick_advanced_optimizer.py       ← Earlier optimizer version
        ├── erick_advanced_optimizer_2.py     ← Alternative approach
        ├── erick_real_corpus_optimizer.py    ← Real-corpus variant
        ├── run_optimizer.py / run_hybrid.py  ← Runner scripts
        ├── build_report.py                   ← Report generator
        ├── extract.py                        ← Data extraction utilities
        ├── corpus.txt                        ← Training corpus
        └── corpus_data_values.py             ← Corpus statistics
```

---

## Optimizer Details

### erick_v5_vectorized.py — Parallel Tempering Optimizer

The main optimization script uses Parallel Tempering with 8 concurrent temperature chains and vectorized NumPy operations for a ~50× speedup over naive implementations.

**Algorithm:**
1. Initialize 8 random layouts across a temperature gradient (0.0002–0.012)
2. At each step, propose a random character swap within one layout
3. Compute delta-cost using pre-indexed adjacency masks (avoids full recalculation)
4. Accept or reject via Metropolis criterion at the chain's temperature
5. Periodically attempt replica exchange between adjacent temperature chains

**Biomechanical model:**
- Each of the 64 chord positions has an assigned effort score based on direction difficulty
- Same-direction chords (e.g., N+N) are easiest; cross-body diagonals are hardest
- Alternating-thumb bonuses reduce bigram cost for transitions between different hands
- Rolling bonuses for adjacent-direction transitions

**Frequency weights:**
- Unigrams: 1.0× (single character frequency)
- Bigrams: 0.6× (character pair frequency)
- Trigrams: 0.3× (three-character sequence frequency)

### Running the Optimizer

```bash
pip install numpy wordfreq
cd docs/documentation/Research/vatsal
python erick_v5_vectorized.py
```

Output is printed to stdout. Redirect to a file if desired:
```bash
python erick_v5_vectorized.py > v5_output.txt 2>&1
```

Expected runtime: ~30 minutes on a modern CPU.

---

## Layout Variants

Three layout modes were designed based on the research findings (documented in `results_and_logs/erick_keyboard_report.md`):

| Variant | Target Users | Design Rationale |
|---|---|---|
| **Logical (A–Z)** | New users, cognitive accessibility | Alphabetical ordering minimizes cognitive load |
| **Physical Accessibility** | Users with motor impairments | Cardinal-only positions (N/E/S/W) for gross motor input |
| **Efficiency** | Experienced typists | Frequency-optimized via Parallel Tempering (this research) |

The Logical layout is the default. The Efficiency layout applies the v5 optimization results.

---

## Research Papers (43 sources)

The `vatsal/research_papers/` directory contains the academic foundation. Key categories:

| Category | Papers | Notable Sources |
|---|---|---|
| **Chorded keyboard design** | 12 | Twiddler evaluations, chord efficiency analysis, two-hand chord typing |
| **Ergonomics & biomechanics** | 8 | Applied Ergonomics, keyboard arrangement optimization (ACO) |
| **Accessibility** | 7 | Disabled user studies (1986), assistive keyboard survey, special needs |
| **Layout optimization** | 5 | Norman & Fisher (1982), learning curves, reduced keyboards |
| **OrbiTouch & inspiration** | 3 | OrbiTouch whitepaper, ERICK technical specifications |
| **Historical/foundational** | 5 | Alden (1972) review, Stenophone, KeyBowl |
| **Other** | 3 | AI/ML in language, PhD theses, SSRN preprints |

### Key References

- **orbiTouch_whitepaper.pdf** — The primary inspiration for ERICK's two-dome chord input model
- **Chord keyboards.pdf** — Comprehensive overview of chorded keyboard history and design principles
- **erick_keyboard_report.md** — ERICK-specific literature review synthesizing findings from 7 core papers
- **norman-fisher-1982** — Classic study on why alphabetical layouts are not inherently worse (supports ERICK's A–Z mode)
- **kirschenbaum-et-al-1986** — Performance of disabled users on chorded keyboards (validates accessibility approach)

---

## Design Assets

Visual mockups and wireframes are in `vatsal/layout_design/`:
- **final_layout.png** — Production-ready layout diagram
- **joystick_wireframe.drawio** — Editable wireframe for the dual-joystick interface
- **layout_react_app/** — Interactive React application for exploring and visualizing chord layouts
