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
- a 6-section optimizer update captured in ERICK-139, with a separate 36-position optimizer script now checked in under `docs/documentation/Research/vatsal/erick_v5_6section.py`
- a reproduced 6-section legacy-baseline run captured in `docs/documentation/Research/vatsal/results_and_logs/optimization_results_6section_baseline_2026-04-26.md`
- shipped English efficiency layouts in shared keyboard logic
- supporting visuals and raw logs in `docs/documentation/Research/`

### Current Known Limits

- objective weights were chosen heuristically and have not been sensitivity-tested recently
- the optimizer is largely letter-frequency driven and only lightly reflects symbols, digits, or utility-mode switching cost
- the research corpus is not clearly segmented by use case such as messaging, accessibility, or controller-heavy usage
- historical optimizer generations use different corpus mixes and reporting formats, so many older score comparisons are not directly apples-to-apples
- the cost model does not explicitly include error-proneness, neighbor confusion, or mode-switch recovery time
- learned prediction and next-word acceptance now reduce effort in practice, but the optimizer does not account for that interaction yet

### Verified Branch 0 Snapshot - 2026-04-26

| Area | Verified Current State |
|---|---|
| 8-section optimizer | `docs/documentation/Research/vatsal/erick_v5_vectorized.py` remains the clearest baseline for the shipped efficiency research: 8 chains, 500,000 steps per chain, temperatures `[0.012, 0.008, 0.005, 0.003, 0.0018, 0.001, 0.0005, 0.0002]`, and unigram/bigram/trigram weights `1.0 / 0.6 / 0.3`. |
| 8-section checked-in metrics | `docs/documentation/Research/vatsal/v5_output.txt` reports final score `0.86204`, baseline `1.55694 ± 0.12366`, `44.6%` improvement over random baseline, and predicted `73.2` WPM. |
| 8-section shipped-map drift | `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` still matches the v5 letter-placement family for the main alphabetic core, but several punctuation, digit, and filler-slot assignments differ from `v5_output.txt`. Branch 0 should treat the logged v5 layout and the shipped 8-section map as related but not byte-identical baselines. |
| 8-section historical context | Git history shows the shipped 8-section efficiency map was introduced in commit `599dbb0b` (`Add Efficiency layout and integrate UI/state (#24)`) as part of the original product integration work. Branch 0 should therefore treat the shipping map as a stable product baseline that predates the currently checked-in `v5_output.txt` artifact, not assume the two were ever literally identical. |
| 6-section optimizer track | `docs/documentation/Research/vatsal/erick_v5_6section.py` is a separate 36-position optimizer with the same `1.0 / 0.6 / 0.3` weight mix, but its utility model still reflects an older 5-action wheel: `SHIFT`, `.`, `SPACE`, `ENTER`, and `BACKSPACE` without the shipped `TOGGLE_SYMBOLS` action. |
| 6-section reproduced metrics | The 2026-04-26 reproduction run reports final score `0.94132`, baseline `1.34279 ± 0.06476`, `29.9%` improvement over random baseline, and predicted `72.4` WPM. |
| 6-section reproduced-map drift | The reproduced optimizer output matches only `4 / 36` placeholder slots in `KeyboardLogic.kt`: `N[2]=g`, `N[3]=7`, `NE[1]=t`, and `SE[1]=h`. The remaining 32 slots differ. |
| 6-section exact-shipped gap | The current repo now has a checked-in reproduction summary for the legacy 5-action script, but it still does not have an exact shipped-wheel optimizer baseline. In shared code, `KeyboardLogic.kt` labels `efficiencyNormalMap6` as a placeholder that still needs an optimizer re-run, and the shipped rotated utility wheel no longer matches the directions modeled in the script. |
| 6-section historical context | Git history shows ERICK-139 introduced `docs/documentation/Research/vatsal/erick_v5_6section.py` and the placeholder `efficiencyNormalMap6` in the same commit (`a5dd219f` / tag `v1.0`), with no checked-in 6-section result artifact added alongside them. Branch 0 should therefore treat the missing output as an original gap, not as a later repo cleanup loss. |
| Prediction surface that now matters | `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` now includes learned word frequencies, learned bigrams, and next-word suggestions from ERICK-148. The current optimizers do not model those saved-keystroke effects yet, so Branch 7 is a real scoring gap rather than a speculative follow-up. |

### Branch 0 Scorecard Template

Use this table for every baseline or branch comparison so later experiments stay comparable.

| Dial Mode | Optimizer Script | Corpus Source | Positions | Utility Set | Weights (Uni/Bi/Tri) | Search Settings | Best Score | Random Baseline | Improvement | Predicted WPM | Shipped Map Match | Evidence Status |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 8-section | `docs/documentation/Research/vatsal/erick_v5_vectorized.py` | `wordfreq` top 50k English words | 64 | `SHIFT`, `SPACE`, `BACKSPACE`, `ENTER`, `CAPSLOCK`, `TAB`, `.`, `,` | `1.0 / 0.6 / 0.3` | 8 chains, 500k steps, PT temps `0.012 -> 0.0002` | `0.86204` | `1.55694 ± 0.12366` | `44.6%` | `73.2` | Partial drift in punctuation/filler assignments | Checked-in output exists |
| 6-section | `docs/documentation/Research/vatsal/erick_v5_6section.py` | `wordfreq` top 50k English words | 36 | Older 5-action model: `SHIFT`, `.`, `SPACE`, `ENTER`, `BACKSPACE` | `1.0 / 0.6 / 0.3` | 8 chains, 500k steps, PT temps `0.012 -> 0.0002` | `0.94132` | `1.34279 ± 0.06476` | `29.9%` | `72.4` | `4 / 36` exact slot matches plus utility-direction drift from shipped wheel | Reproduced 2026-04-26 under legacy utility assumptions |

### Verified Branch 8 Snapshot - 2026-04-26

| Artifact | Corpus / Dataset | Search Context | Comparison Risk |
|---|---|---|---|
| `docs/documentation/Research/vatsal/results_and_logs/optimization_results.md` | merged Common Crawl, Wikipedia, and Google Books N-grams | earlier combined Simulated Annealing + Genetic Algorithm run | not directly comparable to v5 because both corpus mix and search method changed |
| `docs/documentation/Research/vatsal/results_and_logs/optimization_results_2.md` | `wordfreq` 25k (60% web) + Google Books 1-grams 10k (40% books) + Google Books 2-grams 5k | advanced SA run with different trigram weighting and older objective mix | partially comparable for direction-of-travel, not for score ranking against v5 |
| `docs/documentation/Research/vatsal/v5_output.txt` | `wordfreq` top 50k English words | current 8-section PT baseline | primary current 8-section comparison reference |
| `docs/documentation/Research/vatsal/results_and_logs/optimization_results_6section_baseline_2026-04-26.md` | `wordfreq` top 50k English words | reproduced 6-section PT run under legacy 5-action utility model | only comparable to other `wordfreq` v5-style runs, not to the shipped 6-section wheel |
| `docs/documentation/Research/vatsal/scripts/corpus.txt` and `docs/documentation/Research/vatsal/scripts/corpus_data_values.py` | paste-ready unigram, bigram, and trigram snapshots derived from `wordfreq` top 50k | corpus extraction support files | useful for freezing a baseline corpus snapshot, but not yet a domain-segmented benchmark pack |

Current Branch 8 gap: the repo has corpus history, but it does not yet have one mandatory benchmark suite covering messaging, accessibility phrases, controller-heavy input, punctuation-heavy text, and shared reporting columns across all future runs.

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
- a branch-by-branch plan that lists goals, tasks, outputs, open questions, and decision gates in one place
- a shortlist of the highest-value experiments to run next
- a recommendation for which experiments belong in the shipping optimizer versus which should stay exploratory longer
- updated research docs summarizing findings, tradeoffs, and any proposed new metrics
- if later splits are justified, an explicit split note inside ERICK-150 describing the proposed split and why it is needed

---

## Acceptance Criteria

- [ ] The ticket documents the current efficiency optimizer inputs, weights, and known blind spots
- [ ] The ticket produces a prioritized list of follow-up experiments rather than a vague brainstorm
- [ ] At least one proposed experiment addresses biomechanical weighting
- [ ] At least one proposed experiment addresses n-gram weighting
- [ ] At least one proposed experiment addresses symbols, digits, or utility-mode cost
- [ ] At least one proposed experiment addresses error/confusion modeling
- [ ] At least one proposed experiment addresses different user segments or input devices
- [ ] The ticket includes a branch map with goals, tasks, outputs, open questions, and decision gates for each branch
- [ ] The ticket documents the recommended execution order and the research-first questions that may affect any later split
- [ ] No new child tickets are created during the planning phase; any future split remains documented inside ERICK-150 first
- [ ] No shipped layout changes are made until the research output is reviewed

---

## Candidate Files And Artifacts

| File | Purpose |
|---|---|
| `docs/documentation/Research/README.md` | Current optimizer documentation and baseline assumptions |
| `docs/documentation/Research/vatsal/erick_v5_vectorized.py` | Main Parallel Tempering optimizer |
| `docs/documentation/Research/vatsal/results_and_logs/` | Historical output and experiment logs |
| `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` | Shipped 8-section and 6-section Efficiency layout maps |
| `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` | Current learned prediction behavior that may affect layout scoring |
| `android/shared/src/commonMain/kotlin/ControllerInputProcessor.kt` | Shared controller normalization relevant to touch-vs-controller research |
| `android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt` | Guided-lesson content that can expose learnability pressure points |
| `ios/ERICK/ERICK/LearningContent.swift` | iOS lesson mirror for learnability and practice-flow review |
| `docs/documentation/Jira/ERICK-139.md` | 6-section optimizer update history |
| `docs/documentation/Jira/ERICK-148.md` | Prediction improvements that may change the optimization target |
| `APP_CONTEXT.md` | Shared architecture reference for layout and prediction ownership |

---

## Potential Later Split Points

Do not split these out yet. Keep them here until the relevant branch produces enough evidence.

- Branch 1 could later justify a separate calibration effort if touch and controller need different effort matrices.
- Branch 2 could later justify an optimizer-tuning implementation if weight sensitivity produces a clearly dominant objective mix.
- Branch 3 could later justify a symbols-and-utility modeling effort if non-letter cost proves large enough to change ranking.
- Branch 4 could later justify a privacy-safe trace or instrumentation study if confusion modeling cannot be estimated from static analysis alone.
- Branch 6 could later justify a hybrid learnability layout effort if the research shows a strong speed-versus-teachability tradeoff.
- Branch 7 could later justify a prediction-aware evaluation harness if ERICK-148 style prediction changes the best candidate layouts.

---

## Planning Constraint

Keep all research branches inside ERICK-150 until the baseline is reproduced and the first round of findings is written up. Do not create implementation tickets yet. If a branch later proves large enough to split, use this ticket as the umbrella index and keep the branch summary, decision, and links here.

---

## Branch Map Overview

| Branch | Theme | Main Goal | Depends On | Output To Capture In This Ticket |
|---|---|---|---|---|
| **Branch 0** | Baseline Reproducibility | Reproduce the current shipped optimizer assumptions and scores exactly | None | Baseline config snapshot, reproduced scores, drift list |
| **Branch 1** | Effort Matrix Calibration | Re-check physical effort assumptions for touch, controller, and 6-section usage | Branch 0 | Updated effort-matrix recommendations and confidence notes |
| **Branch 2** | Weight Sensitivity + Search Tuning | Test unigram, bigram, trigram, and search-parameter sensitivity | Branch 0 | Recommended weighting ranges and optimizer settings |
| **Branch 3** | Symbols, Digits, And Utility Cost | Decide how non-letter typing cost should affect optimization | Branch 0 | Proposed expanded objective terms and experimental results |
| **Branch 4** | Error And Confusion Modeling | Add a realistic penalty for likely misses and neighbor confusion | Branch 0 | Candidate confusion model and validation notes |
| **Branch 5** | User-Segment Layout Variants | Test whether one layout should serve touch, controller, assisted, novice, and expert users equally | Branches 1-4 as needed | Recommendation on shared vs segment-specific efficiency layouts |
| **Branch 6** | Learnability And Hybrid Objectives | Quantify the speed-vs-learnability tradeoff | Branch 0 | Hybrid-objective candidates and teaching-cost metrics |
| **Branch 7** | Prediction-Aware Evaluation | Decide how prediction gains should change the optimization target | Branch 0 and ERICK-148 behavior review | Combined layout + prediction evaluation proposal |
| **Branch 8** | Corpora And Evaluation Expansion | Improve datasets, benchmarks, and reporting across all branches | Branch 0 | Corpus plan, benchmark suite, and result template |

---

## Recommended Research Order

### Phase 1 - Lock The Baseline

- Complete **Branch 0** first.
- Start **Branch 8** in parallel only where it helps document current corpora and benchmark gaps.
- Do not treat any later branch result as actionable until Branch 0 confirms the shipped baseline can be reproduced or the drift is explained.
- Before using any 6-section result in Branches 1-4, either align the optimizer to the shipped rotated utility wheel plus Symbols toggle or explicitly label the run as a legacy 5-action baseline.

### Phase 2 - Re-Score The Core Objective

- Run **Branch 1** and **Branch 2** once the baseline harness is trustworthy.
- Begin **Branch 3** after the core weighting and effort assumptions are at least stable enough to compare against.

### Phase 3 - Add Real-World Penalties

- Run **Branch 4** after the team agrees on how to capture local debug traces or practice-session evidence without violating privacy.
- Run **Branch 6** once there is a scorecard that can compare pure efficiency against hybrid or mnemonic-friendly layouts.

### Phase 4 - Decide Product Direction

- Run **Branch 5** to decide whether there should be one efficiency layout or more than one.
- Run **Branch 7** once prediction-aware evaluation can use the shipped ERICK-148 behavior instead of hypothetical prediction gains.

---

## Detailed Branch Plans

### Branch 0 - Baseline Reproducibility

**Goal**

Create one trusted baseline for the current shipped Efficiency research before changing any assumptions.

**Work To Do**

1. Identify the exact optimizer script, corpus files, coefficient values, temperature schedule, and effort matrix used for the shipped 8-section and 6-section layouts.
2. Re-run the documented optimizer paths and capture current outputs, runtime, and top candidate layouts.
3. Compare reproduced layouts and scores against:
	- the layouts documented in research outputs
	- the layouts actually shipped in shared keyboard logic
	- a logical baseline and a random baseline
4. Record any drift between docs, research outputs, and shipped code.
5. Create one baseline scorecard format that later branches will reuse.

**Artifacts To Produce**

- baseline configuration snapshot
- reproduced metrics table
- drift log between docs, optimizer outputs, and shipped maps
- agreed comparison template for later experiments

### Branch Status

- Status: `Needs More Data`
- Latest finding summary: the current baseline is split, and the split is now reproduced rather than inferred. The 8-section v5 optimizer still has a checked-in script and output snapshot, but the shipped 8-section map is a stable product baseline from the original Efficiency integration commit rather than a byte-identical mirror of that log. The 6-section optimizer also exists as a separate script and now has a reproduced result (`0.94132`, `29.9%`, `72.4` WPM), but it still models a legacy 5-action utility wheel and its generated layout matches only `4 / 36` slots in the shipped placeholder map.
- Evidence reviewed: `docs/documentation/Research/README.md`, `docs/documentation/Research/vatsal/erick_v5_vectorized.py`, `docs/documentation/Research/vatsal/v5_output.txt`, `docs/documentation/Research/vatsal/erick_v5_6section.py`, `docs/documentation/Research/vatsal/results_and_logs/optimization_results_6section_baseline_2026-04-26.md`, `android/shared/src/commonMain/kotlin/KeyboardLogic.kt`, `docs/documentation/Jira/ERICK-139.md`, and `docs/documentation/Jira/ERICK-148.md`.
- Open blocker: Branch 0 still lacks a checked-in optimizer path that matches the shipped rotated 6-section utility wheel and Symbols toggle, so the exact shipped 6-section objective cannot yet be reproduced from research artifacts alone.
- Next action: decide whether Branch 0 should add a shipped-aligned 6-section optimizer pass before any cross-mode tuning work, or freeze the current 6-section reproduction as a legacy baseline and explicitly scope later branches around that limitation.
- Whether the branch still belongs inside ERICK-150 or is finally ready to split: stays inside ERICK-150.

**Open Questions**

- Are the documented 8-section and 6-section optimizer assumptions still exactly what the code ships today?
- Is the same evaluation function being used across both dial modes, or did 6-section introduce hidden differences beyond position count?

**Decision Gate**

Do not split or prioritize later research branches until this branch can either reproduce the current result within an acceptable tolerance or explain why reproduction is impossible.

### Branch 1 - Effort Matrix Calibration

**Goal**

Re-check whether the current physical effort matrix still reflects real use across touch, controller, and 6-section geometry.

**Work To Do**

1. Document the current chord-effort assumptions direction by direction.
2. Separate assumptions that are specific to:
	- touch input
	- controller input
	- 8-section geometry
	- 6-section geometry
3. Review whether same-direction chords should still dominate as the easiest targets in every mode.
4. Test whether cross-body diagonals, repeated outer-angle transitions, or same-hand repetition are under- or over-penalized.
5. Compare a shared matrix against mode-specific or device-specific matrices.

**Evidence To Gather**

- literature references already in the research folder
- controlled manual trial data if available
- controller diagnostics observations
- practice-session notes about hard or easy chord families

**Open Questions**

- Should touch and controller share one effort model?
- Should 8-section and 6-section use different calibrated effort scores?
- Is assisted one-handed mode close enough to normal touch use, or does it need a separate penalty model?

**Decision Gate**

If the current effort matrix changes the ranking of top layouts materially, later branches must use the recalibrated matrix before drawing conclusions.

### Branch 2 - Weight Sensitivity And Search Tuning

**Goal**

Measure how sensitive the current results are to the unigram, bigram, trigram, and optimizer-search settings.

**Work To Do**

1. Run sensitivity sweeps around the current `1.0 / 0.6 / 0.3` coefficients.
2. Test whether the best coefficient ranges differ between 8-section and 6-section layouts.
3. Compare staged optimization approaches such as:
	- letters first, transitions second
	- fixed unigram pass followed by transition refinement
4. Review temperature schedules, replica counts, runtime ceilings, and convergence behavior for Parallel Tempering.
5. Decide whether the current search method is stable enough or if alternative search strategies deserve a later branch.

**Artifacts To Produce**

- coefficient sweep table
- convergence summary
- top-layout stability comparison across weight changes

**Open Questions**

- Are bigrams undervalued now that ERICK-148 made prediction and learned bigrams more important in real typing?
- Are trigrams too weak to represent common English chunks?
- Is the current optimizer producing one strong result or several near-ties that would justify adding learnability constraints?

**Decision Gate**

If the top layouts shift heavily under small coefficient changes, later branches must treat the current shipped Efficiency layout as only one candidate among several viable families.

### Branch 3 - Symbols, Digits, And Utility Cost

**Goal**

Decide how much real-world typing cost is currently missing because the optimizer mostly reasons about alphabetic text.

**Work To Do**

1. Audit how often digits, punctuation, and symbol-layer actions matter in real ERICK usage scenarios.
2. Define candidate cost terms for:
	- digits
	- punctuation
	- 6-section symbols-layer toggling
	- utility-wheel actions such as space, period, shift, enter, and backspace
3. Compare a letter-only objective against an expanded mixed-text objective.
4. Identify whether the utility wheel itself should remain fixed, be partially modeled, or later be optimized separately.
5. Evaluate whether coding or heavy punctuation domains require a different weighting profile.

**Evidence To Gather**

- messaging-style corpus samples
- punctuation-heavy samples
- existing user guide and lesson targets
- 6-section symbols behavior from ERICK-139

**Open Questions**

- Should the optimizer treat layer toggles as a hard cost?
- Are digits and punctuation frequent enough to change the best layout family, or only enough to refine tie-breakers?

**Decision Gate**

If expanded mixed-text cost materially changes layout ranking, later implementation planning must treat non-letter modeling as part of the core optimizer, not a nice-to-have extension.

### Branch 4 - Error And Confusion Modeling

**Goal**

Add a realistic penalty for layouts that look efficient on paper but are easy to mis-hit or confuse in practice.

**Work To Do**

1. Define what kinds of mistakes matter most:
	- neighboring direction confusion
	- mirror-direction confusion
	- ring or segment overshoot
	- controller stick noise and snap-back errors
2. Investigate privacy-safe ways to collect evidence, such as local-only debug traces or opt-in practice-session logs that never leave the device.
3. Build a candidate confusion matrix for touch and controller separately if needed.
4. Test whether layouts that separate common letters from frequently confused neighbors perform better under the new score.
5. Compare purely theoretical cost against confusion-aware cost.

**Artifacts To Produce**

- proposed confusion categories
- data-collection plan that stays privacy-safe
- candidate confusion-aware scoring term

**Open Questions**

- Can local practice debugging provide enough evidence without adding product complexity?
- Does confusion risk differ enough between touch and controller to justify separate scoring terms?

**Decision Gate**

If confusion-aware scoring reverses the ranking of top layouts, any later optimizer upgrade should treat this branch as required rather than exploratory.

### Branch 5 - User-Segment Layout Variants

**Goal**

Determine whether ERICK should keep one Efficiency layout for everyone or support more than one research-backed variant.

**Work To Do**

1. Define the user segments worth evaluating:
	- touch-first users
	- controller-first users
	- assisted one-handed users
	- novice users
	- expert users
2. Re-score candidate layouts using branch outputs from effort, confusion, and mixed-text cost.
3. Compare one global winner against segment-specific winners.
4. Document the product cost of shipping multiple efficiency variants:
	- settings complexity
	- teaching burden
	- maintenance burden
5. Recommend whether variant layouts should stay research-only or become a product direction.

**Open Questions**

- Is controller use different enough from touch to justify its own layout?
- Is assisted one-handed mode common enough to justify dedicated optimization?
- Does a novice-friendly efficiency layout make sense, or should novices stay on Logical until they graduate?

**Decision Gate**

Do not propose additional shipped layout modes unless this branch shows a clear measurable gain that outweighs the added product complexity.

### Branch 6 - Learnability And Hybrid Objectives

**Goal**

Measure whether a small sacrifice in raw efficiency could produce a layout that is easier to memorize, teach, and retain.

**Work To Do**

1. Define measurable learnability constraints, such as partial clustering, row coherence, or mnemonic anchors.
2. Build one or more hybrid objectives that combine physical effort with learnability penalties.
3. Compare pure-efficiency winners against hybrid winners using the same baseline scorecard.
4. Review whether lesson difficulty, onboarding friction, or practice error rates can serve as proxies for learnability.
5. Recommend whether hybrid layouts should be considered for a later product experiment.

**Open Questions**

- How much efficiency loss is acceptable for a meaningful memorability gain?
- Can learnability be approximated analytically, or does it require user testing before it becomes credible?

**Decision Gate**

If hybrid objectives remain close to the top efficiency score while improving teaching clarity, this branch becomes a strong candidate for future product testing.

### Branch 7 - Prediction-Aware Evaluation

**Goal**

Decide how the optimizer should change now that prediction, learned words, and learned bigrams reduce real typing effort.

**Work To Do**

1. Review the shipped ERICK-148 behavior and identify where prediction most meaningfully saves physical input.
2. Define candidate combined metrics, such as:
	- physical effort only
	- physical effort plus expected completion savings
	- physical effort plus next-word acceptance savings
3. Test whether layouts optimized for prefix-heavy input differ from layouts optimized for raw character entry.
4. Decide whether prediction-aware evaluation belongs in the optimizer itself or only in post-hoc benchmarking.
5. Document how learned user bigrams or domain-specific predictions could change future corpora.

**Open Questions**

- Should layout optimization assume strong prediction support, or should layout remain robust when prediction is ignored?
- Does prediction reduce the value of optimizing later letters in long words and increase the value of optimizing common prefixes?

**Decision Gate**

If prediction-aware evaluation materially changes the ranking of layouts, later implementation planning should treat layout and prediction as a coupled system rather than two separate optimizations.

### Branch 8 - Corpora And Evaluation Expansion

**Goal**

Strengthen the datasets and benchmark suite used across every other branch.

**Work To Do**

1. Inventory the current corpus sources and identify domain bias.
2. Build a candidate corpus plan covering:
	- general English messaging
	- accessibility/supportive phrases
	- controller-heavy or TV-style input
	- punctuation-heavy text
	- optional future multilingual samples for ERICK-140
3. Define one reusable benchmark pack and reporting format that every branch must use.
4. Decide which metrics are required in every branch result, such as objective score, predicted WPM, symbol overhead, and stability.
5. Document what experimental results are comparable and what results are only exploratory.

**Open Questions**

- Does one general corpus remain enough, or is the current optimizer overfitting to a narrow writing style?
- Which benchmark scenarios should be considered mandatory before any new layout is taken seriously?

**Decision Gate**

If corpus choice changes the best layout family, every later branch conclusion must be annotated by corpus domain instead of presented as universally true.

### Branch Status

- Status: `Researching`
- Latest finding summary: the current corpus story is fragmented across at least three generations: an older merged web/books corpus, an intermediate mixed `wordfreq` plus Google Books corpus, and the current `wordfreq` top 50k baseline used by the v5 scripts. That means many historical score improvements are directional evidence, not directly comparable scoreboard entries.
- Evidence reviewed: `docs/documentation/Research/README.md`, `docs/documentation/Research/vatsal/results_and_logs/optimization_results.md`, `docs/documentation/Research/vatsal/results_and_logs/optimization_results_2.md`, `docs/documentation/Research/vatsal/v5_output.txt`, `docs/documentation/Research/vatsal/results_and_logs/optimization_results_6section_baseline_2026-04-26.md`, `docs/documentation/Research/vatsal/scripts/corpus.txt`, `docs/documentation/Research/vatsal/scripts/corpus_data_values.py`, and `docs/documentation/Research/vatsal/scripts/run_hybrid.py`.
- Open blocker: there is still no single benchmark pack or mandatory result template that every branch can reuse across corpora and dial modes.
- Next action: define the minimum benchmark pack and result columns for future runs, starting with messaging, accessibility/supportive phrases, controller-heavy text, and punctuation-heavy text.
- Whether the branch still belongs inside ERICK-150 or is finally ready to split: stays inside ERICK-150.

---

## Branch Tracking Template

Use the following structure inside this ticket as each branch progresses:

### Branch Status

- Status: `Not Started` / `Researching` / `Needs More Data` / `Ready For Split` / `No-Go`
- Latest finding summary
- Evidence reviewed
- Open blocker
- Next action
- Whether the branch still belongs inside ERICK-150 or is finally ready to split

---

## Exit Conditions For ERICK-150

This ticket is ready to close only when:

- Branch 0 baseline reproduction is complete
- each active branch has a documented result or explicit no-go outcome
- the highest-value next steps are prioritized
- any branch that truly deserves implementation or a dedicated research follow-up has a clear split recommendation documented here
- the team can explain why those branches should or should not become separate tickets
