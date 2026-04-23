# ERICK-150 - Efficiency Mode Research Expansion

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Spike |
| **Priority** | High |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | research, efficiency, optimizer, shared, planning |
| **Dependencies** | Should review the shipped efficiency layouts, ERICK-139 6-section optimizer changes, and ERICK-148 prediction improvements before proposing product changes |

---

## Objective

Audit the current Efficiency layout research, document what the optimizer already models, and identify the next experiments that could produce a meaningfully better typing-efficiency layout for ERICK.

This ticket is a research spike, not an implementation commitment. It should end with a ranked proposal for which follow-up experiments are worth building into the shipped optimizer and which ideas should stay exploratory.

---

## Why This Matters Now

- ERICK already ships Efficiency layouts for both 8-section and 6-section modes, so future changes need to be evidence-based rather than aesthetic.
- The current optimizer is strong, but it was calibrated around a specific English corpus and a limited cost model.
- ERICK-148 made prediction more personalized. That creates new questions about how much physical layout optimization should assume prediction and next-word suggestions will reduce effort.
- Controller support, one-handed assisted mode, and 6-section symbols mode all add real usage patterns that the original optimizer did not fully model.

---

## Current Research Baseline

The current research pipeline already considers several important factors.

### Current Objective Inputs

- chord-position effort scores from a biomechanical model
- same-direction chords as the easiest targets
- cross-body diagonals as the hardest targets
- alternating-thumb bonuses for favorable transitions
- rolling bonuses for adjacent-direction transitions
- unigram weights at `1.0x`
- bigram weights at `0.6x`
- trigram weights at `0.3x`
- English corpus frequency data from the `wordfreq`-based pipeline
- Parallel Tempering search across multiple temperature chains

### Current Documented Outputs

- 8-section efficiency research with a documented 44.6% improvement over random baseline
- a 6-section optimizer update and re-run captured in ERICK-139
- shipped English efficiency layouts in shared keyboard logic
- supporting visuals and raw logs in `docs/documentation/Research/`

### Current Known Limits

- objective weights were chosen heuristically and have not been sensitivity-tested recently
- the optimizer is largely letter-frequency driven and only lightly reflects symbols, digits, or utility-mode switching cost
- the research corpus is not clearly segmented by use case such as messaging, accessibility, or controller-heavy usage
- the cost model does not explicitly include error-proneness, neighbor confusion, or mode-switch recovery time
- learned prediction and next-word acceptance now reduce effort in practice, but the optimizer does not account for that interaction yet

---

## Research Questions To Answer

### 1. Are The Current Effort Weights Still Correct?

Re-test the assumptions behind the chord effort matrix.

Questions:

- Is `N + N` still the right gold-standard easiest chord after the 6-section and controller work?
- Are some diagonals only hard on touch, but acceptable on controller?
- Should same-hand repetition or repeated outer-angle transitions carry a stronger fatigue penalty?
- Should the 8-section and 6-section modes use separately tuned effort matrices rather than just different position counts?

### 2. Are Unigram, Bigram, And Trigram Weights Balanced Well?

Current weights are `1.0 / 0.6 / 0.3`. This ticket should test whether those values are still the best fit.

Questions:

- Do bigrams deserve more weight now that next-word prediction and learned bigrams exist?
- Are trigrams underweighted for common English chunks?
- Would a staged optimization process produce better results, such as letters first and transitions second?
- Does the best weighting differ between 8-section and 6-section layouts?

### 3. What Should The Optimizer Do About Numbers, Symbols, And Utility Actions?

Current efficiency thinking is strongest around alphabetic characters. ERICK now has more mode-specific behavior that affects real typing effort.

Questions:

- Should symbols and digits be part of the same objective instead of mostly living as follow-on mapping work?
- How much should the symbols-layer toggle cost count in 6-section mode?
- Should frequent punctuation sequences such as `.`, `,`, `?`, `!`, `'`, and `-` influence layout ranking more directly?
- Should the utility wheel itself be partially optimized or at least measured alongside the character layout?

### 4. Can We Model Real Error Risk Instead Of Idealized Effort Alone?

Research should add a confusion/error term, not just theoretical speed.

Questions:

- Which neighboring directions are most often confused on touch versus controller?
- Should visually or physically adjacent chords be pushed farther apart when they represent high-frequency letters?
- Can we use practice-lesson telemetry or local debug traces to build a confusion matrix without violating privacy?

### 5. Should We Optimize For Different User Segments?

ERICK supports more than one physical input style.

Questions:

- Should touch users and controller users share the same efficiency layout?
- Should assisted one-handed mode have its own optimization target?
- Should left-handed usage influence the cost model, or is mirroring enough?
- Do novice and expert users want the same tradeoff between memorability and absolute speed?

### 6. How Much Learnability Should Constrain Efficiency?

Pure speed is not the only product goal.

Questions:

- Should some high-frequency letters remain partly clustered for mnemonic reasons?
- Can we add penalties for layouts that are hard to teach in quickstart and practice lessons?
- Is there a hybrid objective that preserves some logical structure while keeping most of the efficiency gain?

### 7. How Should Prediction Interact With Layout Research?

Prediction now removes some physical effort. The optimizer should revisit what it is trying to minimize.

Questions:

- If prediction handles common completions and next-word suggestions well, should the layout focus more on prefixes and error recovery?
- Should the optimizer use a combined objective that includes physical cost plus expected saved keystrokes from prediction?
- Can learned user bigrams help define better evaluation corpora for future layout experiments?

### 8. What Additional Research Areas Should Be Explored?

Potential extensions worth testing:

- domain-specific corpora such as messaging, accessibility phrases, email, and coding text
- language-specific efficiency layouts as a dependency for ERICK-140
- Bayesian or grid-search tuning of objective weights
- simulated annealing vs Parallel Tempering vs hybrid search comparison
- robustness testing against noisy stick input and imperfect touch traces
- user-study tasks that measure not just WPM, but fatigue, error rate, and time-to-learn
- visualization tooling for heatmaps, confusion maps, and lesson-level failure clusters

---

## Deliverables

- a written inventory of the current optimizer assumptions and weights
- a shortlist of the highest-value experiments to run next
- a recommendation for which experiments belong in the shipping optimizer versus separate research branches
- updated research docs summarizing findings, tradeoffs, and any proposed new metrics
- follow-up implementation tickets for the experiments that survive review

---

## Acceptance Criteria

- [ ] The ticket documents the current efficiency optimizer inputs, weights, and known blind spots
- [ ] The ticket produces a prioritized list of follow-up experiments rather than a vague brainstorm
- [ ] At least one proposed experiment addresses biomechanical weighting
- [ ] At least one proposed experiment addresses n-gram weighting
- [ ] At least one proposed experiment addresses symbols, digits, or utility-mode cost
- [ ] At least one proposed experiment addresses error/confusion modeling
- [ ] At least one proposed experiment addresses different user segments or input devices
- [ ] The output identifies which experiments should become separate implementation tickets
- [ ] No shipped layout changes are made until the research output is reviewed

---

## Candidate Files And Artifacts

| File | Purpose |
|---|---|
| `docs/documentation/Research/README.md` | Current optimizer documentation and baseline assumptions |
| `docs/documentation/Research/vatsal/erick_v5_vectorized.py` | Main Parallel Tempering optimizer |
| `docs/documentation/Research/vatsal/results_and_logs/` | Historical output and experiment logs |
| `docs/documentation/Jira/ERICK-139.md` | 6-section optimizer update history |
| `docs/documentation/Jira/ERICK-148.md` | Prediction improvements that may change the optimization target |
| `APP_CONTEXT.md` | Shared architecture reference for layout and prediction ownership |

---

## Suggested Follow-Up Tickets From This Spike

- weight-sensitivity analysis for unigram/bigram/trigram coefficients
- touch-vs-controller effort-matrix calibration
- error/confusion-aware optimizer objective
- symbols/digits/utility-cost integration for 6-section mode
- prediction-aware evaluation harness for layout scoring