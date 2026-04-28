# ERICK-151 - Keyboard Research Expansion Part 2

| Field | Value |
|---|---|
| **Status** | In Progress |
| **Type** | Spike |
| **Priority** | High |
| **Story Points** | 13 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | research, onboarding, accessibility, controller, prediction, ergonomics, planning |
| **Dependencies** | Should review ERICK-150, the shipped Quickstart and Practice Lessons, Controller Diagnostics, WordPredictionEngine behavior, current accessibility settings, and the `research_papers/` corpus before proposing product changes |

---

## Objective

Build the next research plan after ERICK-150.

ERICK-150 mostly answered layout-and-objective questions: which optimizer assumptions are still defensible, which layout variants are currently `No-Go`, and which evaluation surfaces now exist. ERICK-151 should widen the research scope from **layout optimization alone** to the **whole keyboard system**:

- learnability and onboarding
- accessibility and assistive use
- controller and portable-device workflows
- error prevention and recovery
- prediction and adaptive assistance inside real typing
- mixed-task keyboard use beyond plain prose
- long-term adoption, habit formation, and trust

This ticket is still a research spike, not an implementation commitment. It should end with ranked follow-up tickets, measured `No-Go` outcomes where appropriate, and a clear split between product-ready experiments and ideas that should stay exploratory.

---

## Why This Matters Now

- ERICK now has more than a layout engine. It ships Quickstart, guided Practice Lessons, controller support, Controller Diagnostics, offline prediction, haptics, sounds, color palettes, accessibility fonts, assisted one-handed typing, and two dial geometries.
- ERICK-150 showed that pure layout research has diminishing returns unless the rest of the typing system is also measured. Branches 5, 6, and 7 all ended in defensible `No-Go` outcomes, which means the next major wins are less likely to come from another layout rerun alone.
- The papers in `research_papers/` repeatedly point to the same risk: alternative keyboards do not fail only because of peak speed. They also fail on learnability, cognitive demand, setup friction, trust, and the gap between expert demonstrations and first-week user behavior.
- ERICK is explicitly accessibility-oriented. That makes it insufficient to ask only “which layout is fastest?” We also need to ask “which experience is teachable, recoverable, and usable across touch, controller, one-handed, low-vision, and motor-variability contexts?”

---

## What ERICK-150 Settled

- the current unigram/bigram/trigram weight mix remains the safest cross-mode default
- the shipped 6-section path is benchmarkable under a modern mixed-shortform evaluation pack
- local privacy-safe confusion evidence collection is feasible through Controller Diagnostics
- segment-specific shipped `Efficiency` variants are not justified yet
- a third hybrid learnability layout family is not justified yet
- prediction-aware post-hoc benchmarking is useful, but optimizer-coupled prediction scoring is currently a `No-Go`

## What ERICK-150 Left Open

- the repo still lacks an exact shipped 6-section optimizer baseline and a benchmark-pack-based 8-section rerun
- the current research still says little about which tutorial aids reduce real onboarding friction
- the current research has only a seed confusion drill, not a broader error-recovery model for live typing
- controller behavior is measurable, but not yet fully benchmarked as its own user path
- the predictor is implemented, but it has not been studied as a teaching tool, trust surface, or adaptive assistance layer
- adoption, effort expectancy, and habit formation have not been translated into ERICK-specific measures
- accessibility research has not yet been organized around concrete ERICK persona/task bundles

---

## Current Implementation Anchors

ERICK-151 should stay grounded in the current product instead of drifting into generic keyboard brainstorming.

### Shipped learning and practice surfaces

- Quickstart teaches the two-dial model, utility swipes, assisted typing, and controller typing.
- Practice Lessons currently cover 8-section basics, 6-section basics, utility swipes, assisted one-handed typing, controller drills, and quote practice.
- Practice content already encodes a lesson order and target phrases, so it can support real learnability experiments rather than only speculative onboarding ideas.

### Shipped input and accessibility surfaces

- 8-section and 6-section dial modes coexist.
- Logical and Efficiency layouts are both shipped, with custom layouts available in the current product path for the existing supported surfaces. 6-section custom layouts should still be treated as outside the shipped path unless a later ticket changes that deliberately.
- Left-Handed Mode, colorblind-safe palettes, dyslexia-friendly fonts, haptic feedback, typing sounds, and assisted one-handed typing are already present.

### Shipped controller and diagnostics surfaces

- ERICK supports controller input on Android and iOS.
- Android now includes Controller Diagnostics plus a local aggregate-only Confusion Drill.
- Controller behavior shares the same normalization path as the typing flow, so diagnostics findings can inform real behavior instead of a dead-end debug screen.

### Shipped adaptive assistance surfaces

- `WordPredictionEngine` provides prefix completions, next-word suggestions, learned word frequencies, learned bigrams, and user dictionary additions.
- Suggestions already appear in the live typing UI, but their effect on onboarding, trust, and recovery behavior has not been studied directly.

---

## Research Paper Review Summary

The `research_papers/` folder was reviewed in full. It contains a mix of highly relevant HCI/ergonomics papers, assistive-input surveys, older chord-keyboard studies, product/vendor documents, and a few tangential items that do not currently drive ERICK.

The most relevant signals for ERICK Part 2 are below.

### 1. Learnability is a first-class research problem, not a side note

- `Typing with a two-hand chord keyboard: will the QWERTY become obsolete?` (Gopher and Raij, 1988) argues that a better cognitive and motor structure can improve chord-learning speed, not just expert performance.
- `Analysis of Alternative Keyboards Using Learning Curves` (Anderson et al., 2009) shows that chord keyboards can carry much steeper learning costs than some ergonomic alternatives even when they have physical advantages.
- `New chording text entry methods combining physical and virtual buttons on a mobile phone` (Wu, 2014) reports that better learning efficiency can materially affect willingness to adopt a chord method.
- `Experimental Evaluations of the Twiddler One-Handed Chording Mobile Keyboard`, `Expert chording text entry on the Twiddler one-handed keyboard`, and the novice Twiddler study all point to the same practical lesson: phrase ordering, visual guides, and targeted aids matter during the early curve.
- `Senorita: A Chorded Keyboard for Sighted, Low Vision, and Blind Mobile Users` is especially relevant because it explicitly uses sequential visual cues to support “learning by doing” rather than assuming the user already knows the chord.

### 2. Accessibility research needs persona-specific evaluation, not one generic “accessible” claim

- `Text input for motor-impaired people` (Polacek, 2015) is a strong evaluation roadmap because it surveys text-entry techniques, layout decisions, language-model use, and evaluation methods across a large assistive-input literature base.
- `Input Assistive Keyboards for People with Disabilities: A Survey` reinforces that device suitability depends on the user condition, not on a single universal claim.
- `Performance of Disabled Persons on a Chordic Keyboard` (Kirschenbaum et al., 1986) is directly relevant to ERICK’s accessibility promise because it studies disabled users rather than only able-bodied university participants.
- `Design and Implementation of a Chorded On-Screen Keyboard` is relevant because it frames chorded input around visual guidance, universal design, learning ability, satisfaction, and physical impairment contexts.

### 3. Ergonomic design is broader than key placement

- `Keyboard Design and Operation: A Review of the Major Issues` (Alden, Daniels, and Kanarick, 1972) is still useful because it frames performance as a function of operator, task, equipment, and environment rather than layout alone.
- `Toward a more humane keyboard` (Hargreaves et al., 1992) and `The Development and Evaluation of the Keybowl` (McAlindon, 1994) both push ERICK toward a broader ergonomic frame: biomechanics, workload, wrist motion, coding structure, and mental models all matter.
- `The input efficiency of chord keyboards` (Wu and Shi, 2018) and `An investigation of the performance of novel chorded keyboards in combination with pointing input devices` (Shi, 2015) suggest that compact or multi-character designs can improve efficiency but may also introduce user confusion or interaction tradeoffs.

### 4. Adoption depends on more than raw speed

- `Evaluation of preceding variables affecting behavioral use and acceptance of chord-enabled keyboard among students` (Ong et al., 2024) is especially relevant because it identifies usage behavior, perceived productivity, facilitating conditions, and effort expectancy as adoption drivers.
- `Why Alphabetic Keyboards Are Not Easy to Use: Keyboard Layout Doesn't Much Matter` (Norman and Fisher, 1982) is a caution against believing that rearranging symbols alone will solve novice experience problems.
- Older chord coding comparisons such as `A Comparison of Two Chord Keyboard Coding Systems for Alphanumeric Data Entry` and `Evaluation of Conventional, Serial, and Chord Keyboard Options for Mail Encoding` also reinforce that code structure and training cost can be adoption blockers even when a system is efficient later.

### 5. ERICK should research error tolerance and adaptive assistance, not just ideal-path input

- `Reduced Keyboard Designs Using Disambiguation` (Kreifeldt et al., 1989) suggests that ambiguity can be acceptable when the system is explicit about where disambiguation is helping and when accuracy remains good enough for the task.
- `Chording and Tilting for Rapid, Unambiguous Text Entry to Mobile Phones` (Wigdor, 2004) is relevant because it explores alternative interaction strategies where speed, ambiguity, and error rate are balanced differently.
- The motor-impairment survey by Polacek also reinforces the role of language models, selection methods, and error evaluation in accessible text entry.

### 6. Mobile, one-handed, controller-like, and portable contexts deserve dedicated research

- The Twiddler papers are highly relevant because they explore one-handed mobile chording over time, including novice-to-expert transitions.
- `The effects of chorded keyboards on portable computing devices` is directly aligned with ERICK’s phone, tablet, and controller-adjacent use cases even though its extractable text is limited in the local PDF.
- `The input efficiency of chord keyboards` and the 2015 pointing-device paper both suggest that accessory context and companion-input assumptions can change what “efficient” means.

### Reviewed But Not Current Drivers

- `Development of Virtual Musical Keyboard Layout Interfaces for Invented and Evolved Tuning Systems` (Dyroy, 2022) was reviewed but is not a text-entry source for ERICK.
- `AI and Machine Learning in Language Education` was reviewed but is not a keyboard-design or text-entry study for ERICK’s current scope.
- Several vendor/product references in the folder, including the OrbiTouch documents, are still useful as product-history context, but they should not be treated as equivalent to ERICK-specific empirical evidence.

---

## Current Execution Tracker

Current Part 2 tracker as of 2026-04-28:

- Branch `0` is now frozen through the Part 2 literature map and scorecard note in `results_and_logs/branch0_part2_scorecard_freeze_2026-04-28.md`.
- Branch `1` is now closed through the initial baseline probe plus the direct 6-section implementation and the exact 8-section rerun in `results_and_logs/branch1_exact_8section_rerun_2026-04-28.md`.
- Branch `2` is now closed through the onboarding/training plan in `results_and_logs/branch2_onboarding_training_plan_2026-04-28.md`.
- Branch `3` is now closed through the accessibility persona matrix in `results_and_logs/branch3_accessibility_persona_matrix_2026-04-28.md`.
- Branch `4` is now closed through the controller task bundle in `results_and_logs/branch4_controller_task_bundle_2026-04-28.md`.
- Branch `5` is now closed through the error and recovery plan in `results_and_logs/branch5_error_recovery_plan_2026-04-28.md`.
- Branch `6` is now closed through the predictor interaction scorecard in `results_and_logs/branch6_predictor_interaction_scorecard_2026-04-28.md`.
- Branch `7` is now closed through the mixed-task workflow pack in `results_and_logs/branch7_mixed_task_workflow_pack_2026-04-28.md`.
- Branches `8` and `9` still broaden the work from layout research into whole-keyboard behavior, trust, and accessibility.

| Branch | Tracker Status | Focus | Why It Exists |
|---|---|---|---|
| `Branch 0` | `Completed` | Literature and scorecard freeze | Makes the rest of Part 2 comparable instead of anecdotal. |
| `Branch 1` | `Completed` | Shipped baseline completion | Closes the remaining ERICK-150 benchmark and comparability gaps. |
| `Branch 2` | `Completed` | Onboarding and training aids | Defines the first-session measurement plan, the tutorial comparisons, and the split-ready instrumentation work. |
| `Branch 3` | `Completed` | Accessibility persona evaluation | Converts broad accessibility claims into persona bundles, task bundles, and split-ready setup guidance. |
| `Branch 4` | `Completed` | Controller and alternative hardware research | Defines the controller benchmark bundle, diagnostics expansion plan, and controller-versus-touch split rationale. |
| `Branch 5` | `Completed` | Error prevention and recovery | Defines the recovery taxonomy, records a No-Go on silent correction, and keeps explicit coaching as the split-ready path. |
| `Branch 6` | `Completed` | Prediction, disambiguation, and adaptive assistance | Reframes prediction as a trust, visibility, and domain-assistance problem rather than an optimizer-ranking problem. |
| `Branch 7` | `Completed` | Mixed-task workflow research | Defines the mixed-task workflow pack and points future work at utility, symbols, and navigation bottlenecks before macros. |
| `Branch 8` | `Not Started` | Adoption, habit formation, and trust | Addresses the difference between “can type fast” and “will keep using it.” |
| `Branch 9` | `Not Started` | Personalization, multilingual, and future splits | Holds the speculative but promising directions until the core system is better measured. |

---

## Research Questions To Answer

### Branch 0 - Literature And Scorecard Freeze

**Goal**

Create the shared evaluation language for ERICK Part 2 so later branches compare like with like.

**Work To Do**

1. Distill the reviewed `research_papers/` corpus into an ERICK-specific literature map:
   - onboarding and learning
   - accessibility and disability evaluation
   - controller and portable-device use
   - ergonomics and workload
   - error tolerance and disambiguation
   - adoption and habit formation
2. Expand the Branch 8 benchmark/reporting model from ERICK-150 into a broader system scorecard that can track:
   - task completion time
   - error rate and correction cost
   - lesson completion time
   - confusion bucket counts
   - suggestion acceptance and rejection
   - perceived usability or workload notes
3. Decide which metrics are mandatory for touch, controller, assisted one-handed, and accessibility-focused evaluations.
4. Define what counts as a comparable result family versus an exploratory prototype result.
5. Freeze a small set of core user tasks that later branches must reuse where possible.

**Artifacts To Produce**

- paper-to-branch literature map
- ERICK Part 2 evaluation scorecard
- comparability rules for behavioral versus optimizer-style runs

**Key Literature Signals**

- Alden et al. (1972)
- Polacek (2015)
- Anderson et al. (2009)
- Ong et al. (2024)

**Open Questions**

- Which measures should be required before any future product recommendation is taken seriously?
- How much subjective data is enough for a research spike without turning ERICK into a formal user-study program?

**Decision Gate**

Do not treat later branch results as product-direction evidence until this branch defines the common scorecard and comparability rules.

### Branch Status

- Status: `Completed`
- Latest finding summary: Part 2 now has a frozen literature map, reusable task IDs, and explicit comparability rules for behavioral versus replay results.
- Evidence reviewed: ERICK-150, current User Guide, current Quickstart and Practice Lessons, `research_papers/` corpus, and `results_and_logs/branch0_part2_scorecard_freeze_2026-04-28.md`.
- Open blocker: none.
- Next action: reuse the frozen scorecard and task IDs in later branch notes instead of redefining them locally.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: belongs inside ERICK-151.

### Branch 1 - Shipped Baseline Completion And Cross-Mode Benchmark Adoption

**Goal**

Close the remaining layout and benchmark gaps left open by ERICK-150 so Part 2 has a cleaner product baseline.

**Work To Do**

1. Reconstruct the closest possible exact shipped 6-section baseline using the current rotated utility wheel and current shared maps.
2. Run the first benchmark-pack-based 8-section rerun so Branch 8 adoption becomes cross-mode instead of 6-section-heavy.
3. Quantify the remaining drift between:
   - shipped 8-section map and `v5_output.txt`
   - shared 6-section placeholder map and current best research candidate
4. Separate symbol-layer, utility-wheel, and normal-layer effects more explicitly than ERICK-150 did.
5. Decide whether the 6-section `Efficiency` placeholder is still acceptable as a shipped stopgap or deserves its own update ticket.

**Artifacts To Produce**

- exact-or-closest shipped 6-section baseline report
- first benchmark-pack-based 8-section scorecard
- shipped-map drift memo

**Key Literature Signals**

- Norman and Fisher (1982)
- Wu and Shi (2018)
- McAlindon (1994)

**Open Questions**

- Is the current shipped 6-section `Efficiency` map close enough to keep, or only historically accidental?
- Does the 8-section layout still look strongest when benchmarked on ERICK’s newer shortform packs instead of only `wordfreq` continuity data?

**Decision Gate**

If the shipped maps remain close enough to the best measured candidates, keep layout changes off the product path. If the gaps are large and repeatable, split a focused follow-up ticket rather than burying it inside broader research.

### Branch Status

- Status: `Completed`
- Latest finding summary: the shipped 6-section `Efficiency` map now directly matches the Branch 3 mixed-shortform winner in shared code, while the first exact 8-section benchmark-pack rerun with `ERICK8_SYMBOL_POLICY=shipped_exact` found a materially better winner than the current shipped 8-section map (`0.90377` vs `0.95690`, `71.7` vs `70.5` WPM) with only `5 / 64` exact slot matches. The shipment decision for 8-section is still `No-Go` for an in-place replacement because the continuity and learnability risk is too high for a silent preset swap.
- Evidence reviewed: ERICK-150 Branches 0, 3, 8, the current research README, `results_and_logs/branch1_baseline_probe_2026-04-28.md`, `results_and_logs/branch1_exact_8section_rerun_2026-04-28.md`, `results_and_logs/branch1_exact_8section_comparison_2026-04-28.txt`, `results_and_logs/branch1_8section_shipment_decision_2026-04-28.md`, and `results_and_logs/optimization_results_8section_shipped_exact_mixed_shortform_full_2026-04-28.txt`.
- Open blocker: none.
- Next action: none inside Branch 1; any future 8-section layout migration should split into its own focused follow-up.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: completed inside ERICK-151; any future 8-section shipping change can split into its own focused ticket if needed.

### Branch 2 - Onboarding And Training-Aid Effectiveness

**Goal**

Measure which teaching aids actually reduce early friction for ERICK’s current dual-dial model.

**Work To Do**

1. Inventory the current Quickstart and Practice Lesson aids:
   - lesson order
   - compact versus expanded explanation
   - preset application
   - controller-first guidance
   - utility drill sequencing
2. Compare candidate teaching supports such as:
   - persistent preview highlighting
   - guided sequential cues before full simultaneous chords
   - ordered phrase sets versus general practice text
   - stronger first-error coaching
   - controller-specific tutorial variants
3. Define first-session measures such as:
   - time to first correct word
   - time to first symbol or utility action
   - lesson abandonment point
   - help-button usage
   - error rate by lesson stage
4. Test whether the current Logical-first lesson path remains best for both 6-section and 8-section users.
5. Decide whether prediction suggestions should remain outside the learning path at first or be taught early as a legitimate assistive tool.

**Artifacts To Produce**

- onboarding aid comparison matrix
- lesson-order and phrase-set experiment plan
- recommended first-session metrics

**Key Literature Signals**

- Gopher and Raij (1988)
- Anderson et al. (2009)
- Wu (2014)
- Twiddler novice study
- `Experimental Evaluations of the Twiddler One-Handed Chording Mobile Keyboard`
- `Senorita: A Chorded Keyboard for Sighted, Low Vision, and Blind Mobile Users`

**Open Questions**

- Should ERICK explicitly teach a sequential “row first, letter second” mental model for novices before expecting fluid two-dial release timing?
- Which tutorial aids help without making the interface feel slower or more patronizing for repeat users?

**Decision Gate**

If one or two teaching aids materially reduce first-session friction, create a dedicated implementation ticket rather than folding them into a general UX cleanup.

### Branch Status

- Status: `Completed`
- Latest finding summary: ERICK already ships a real learning path, but it also ships an inconsistent first recommended lesson (`Quickstart` points to 6-section basics while the Practice Hub lists 8-section basics first) and lacks the event-level instrumentation needed to measure first-session friction.
- Evidence reviewed: current Quickstart, Practice Lessons, HelpActivity, lesson persistence in PreferencesManager, User Guide, and `results_and_logs/branch2_onboarding_training_plan_2026-04-28.md`.
- Open blocker: none.
- Next action: split a focused onboarding instrumentation and tutorial-routing ticket starting with sequential cueing in lesson 1 and routed first lessons for touch-first versus controller-first users.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: ready to split.

### Branch 3 - Accessibility Persona Evaluation

**Goal**

Turn ERICK’s accessibility promise into concrete persona-and-task research instead of generic accessibility language.

**Work To Do**

1. Define a small set of ERICK persona bundles, such as:
   - motor variability / reduced precision touch
   - low vision / high visual effort
   - one-handed use
   - controller-first use
   - mixed fatigue / long-session typing
2. Map each persona to the current ERICK surfaces most likely to matter:
   - 6-section mode
   - assisted one-handed mode
   - controller input
   - color palettes and fonts
   - haptic feedback and typing sounds
3. Build task bundles for those personas rather than reusing a generic prose benchmark only.
4. Compare which current settings combinations are strong defaults versus merely possible options.
5. Decide whether accessibility guidance should stay one-size-fits-all or branch into persona-specific presets or recommendations.

**Artifacts To Produce**

- persona-and-task matrix
- accessibility evaluation scorecard
- recommended default bundles or setup guidance memo

**Key Literature Signals**

- Polacek (2015)
- `Input Assistive Keyboards for People with Disabilities: A Survey`
- Kirschenbaum et al. (1986)
- `Design and Implementation of a Chorded On-Screen Keyboard`
- `Senorita: A Chorded Keyboard for Sighted, Low Vision, and Blind Mobile Users`

**Open Questions**

- Which ERICK settings matter most for low-vision users versus motor-precision users?
- Is 6-section mode enough as the main “larger target” accessibility answer, or do some personas need other interventions first?

**Decision Gate**

If persona bundles lead to clearly different recommended defaults or task outcomes, split those into focused product tickets instead of continuing to market ERICK with one broad accessibility claim.

### Branch Status

- Status: `Completed`
- Latest finding summary: ERICK's current settings and lesson surfaces already imply distinct bundles for reduced precision touch, low-vision scanning, one-handed use, controller-first use, and fatigue-oriented use, so one-size-fits-all accessibility guidance is no longer the strongest framing.
- Evidence reviewed: BenefitAudienceContent, MainSettingsContent, learning surfaces, User Guide accessibility sections, and `results_and_logs/branch3_accessibility_persona_matrix_2026-04-28.md`.
- Open blocker: none.
- Next action: split persona-specific setup guidance into focused product work for onboarding, settings, and practice entry points.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: ready to split.

### Branch 4 - Controller And Alternative Hardware Research

**Goal**

Treat controller typing as a primary ERICK input path worthy of its own evaluation, not just a supported extra.

**Work To Do**

1. Expand controller evaluation beyond raw live-stick inspection:
   - dead-zone comfort
   - inversion understanding
   - dual-stick timing
   - rumble usefulness
   - recovery from drift or snap-back
2. Compare controller typing against touch for the same task families:
   - short messaging
   - query entry
   - punctuation-heavy tasks
   - practice drills
3. Study whether controller users need different preview behavior, timing cues, or guided diagnostics.
4. Consider accessory or companion-input contexts where ERICK is used with another pointing device or TV-style environment.
5. Decide whether controller calibration and teaching should remain inside diagnostics only or partially move into mainstream onboarding.

**Artifacts To Produce**

- controller-specific benchmark pack or task bundle
- diagnostics expansion plan
- controller-versus-touch comparison memo

**Key Literature Signals**

- Twiddler studies
- Wu and Shi (2018)
- Shi (2015) pointing-device combination study
- `The effects of chorded keyboards on portable computing devices`
- Wigdor (2004)

**Open Questions**

- Do controller users need different recommended defaults than touch users?
- Does controller rumble help timing and confidence enough to justify more deliberate tuning work?

**Decision Gate**

If controller behavior diverges materially from touch behavior, split a controller-focused follow-up rather than burying the result inside general keyboard research.

### Branch Status

- Status: `Completed`
- Latest finding summary: controller typing already shares ERICK's main state machine and normalization path, but it adds calibration cost, dead-zone jitter, and snap-back failure modes that justify a controller-specific benchmark and follow-up instead of treating controller use as only a touch variant.
- Evidence reviewed: Controller Diagnostics, shared controller confusion metrics, current lessons, HelpActivity guidance, current User Guide controller section, `results_and_logs/branch4_local_confusion_spike_2026-04-28.md`, and `results_and_logs/branch4_controller_task_bundle_2026-04-28.md`.
- Open blocker: none.
- Next action: split a controller-focused follow-up covering benchmark execution, richer local diagnostics summaries, and controller-first onboarding experiments.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: ready to split.

### Branch 5 - Error Prevention And Recovery

**Goal**

Extend ERICK’s new confusion-drill evidence into real research about how the product should prevent mistakes and recover from them.

**Work To Do**

1. Define the first live-typing recovery taxonomy:
   - adjacent directional slips
   - mirror slips
   - premature release
   - wrong utility swipe
   - symbol-layer confusion
   - controller snap-back reversal
2. Measure the correction cost of current recovery tools:
   - backspace hold behavior
   - suggestion acceptance after a near miss
   - retrying the chord immediately
   - switching to assisted mode or 6-section mode
3. Decide where ERICK can be forgiving safely and where silent correction would feel untrustworthy.
4. Evaluate whether local confusion aggregates should ever inform adaptive dead-zone or coaching defaults.
5. Compare explicit recovery research against ambiguity-tolerant approaches from the literature.

**Artifacts To Produce**

- error taxonomy and recovery scorecard
- candidate adaptive-assistance memo
- recommendation on local confusion use versus static defaults

**Key Literature Signals**

- Kreifeldt et al. (1989)
- Wigdor (2004)
- Amell and Crawford (1987)
- Richardson et al. (1987)
- Polacek (2015)

**Open Questions**

- When should ERICK attempt to help automatically, and when should it stay explicit and ask the user to correct manually?
- Is the most important research question here prevention, recovery speed, or trust after a mistake?

**Decision Gate**

If a recovery or forgiveness strategy improves completion without surprising users, split a product ticket. If it mostly hides errors or harms trust, record a `No-Go` and keep the current explicit model.

### Branch Status

- Status: `Completed`
- Latest finding summary: ERICK now has enough concrete recovery surfaces to reject silent correction and automatic retuning as the default path, while still treating explicit, local, threshold-based coaching as a promising follow-up.
- Evidence reviewed: current confusion drill, current backspace behavior, current predictor behavior, current controller recovery surfaces, and `results_and_logs/branch5_error_recovery_plan_2026-04-28.md`.
- Open blocker: none.
- Next action: split a focused recovery-coaching ticket and carry forward the explicit `No-Go` on silent correction and automatic preference changes.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: ready to split.

### Branch 6 - Prediction, Disambiguation, And Adaptive Assistance

**Goal**

Study prediction as a real assistance system inside ERICK, not only as a layout-scoring modifier.

**Work To Do**

1. Separate the predictor questions ERICK-150 did not answer:
   - when suggestions help typing
   - when they help learning
   - when they help recovery
   - when they hurt trust or focus
2. Evaluate prefix completions, next-word suggestions, learned words, learned bigrams, and user dictionary additions under real task families instead of only post-hoc cost replay.
3. Decide whether suggestions should be taught explicitly in practice lessons or stay as a later discovery.
4. Evaluate whether domain-specific or persona-specific language packs are more promising than layout personalization in the short term.
5. Explore whether limited disambiguation or abbreviation support belongs in future ERICK research at all.

**Artifacts To Produce**

- predictor interaction scorecard
- trust-and-acceptance memo for suggestions
- recommendation on domain-specific assistance versus layout personalization

**Key Literature Signals**

- ERICK-150 Branch 7 findings
- Polacek (2015)
- Kreifeldt et al. (1989)
- Wigdor (2004)
- exploratory background: `ssrn-5757903` on personalized layouts and abbreviation-oriented chord systems

**Open Questions**

- Is the next meaningful predictor improvement about ranking quality, suggestion visibility, trust, or domain adaptation?
- Should ERICK invest in adaptive language assistance before adaptive layouts?

**Decision Gate**

If predictor changes look more promising as a trust or workflow problem than a ranking problem, route the follow-up into UX and onboarding tickets rather than back into optimizer research.

### Branch Status

- Status: `Completed`
- Latest finding summary: ERICK's predictor already provides explicit prefix completion, lightweight correction, next-word suggestions, and local learned-state adaptation, but the most promising next work is in visibility, trust, and domain-aware assistance rather than ranking or optimizer coupling.
- Evidence reviewed: `WordPredictionEngine`, `KeyboardStateMachine`, live suggestion-bar behavior in `MyInputMethodService`, User Guide prediction section, `results_and_logs/branch7_prediction_aware_benchmark_2026-04-28.md`, and `results_and_logs/branch6_predictor_interaction_scorecard_2026-04-28.md`.
- Open blocker: none.
- Next action: split a predictor trust-and-visibility follow-up focused on domain-aware assistance and learned-state controls, not layout re-optimization.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: ready to split.

### Branch 7 - Mixed-Task Workflow Research

**Goal**

Measure ERICK as a full keyboard system, not only a prose-entry engine.

**Work To Do**

1. Build task bundles that go beyond word-level text entry:
   - punctuation-heavy phrases
   - controller/TV search queries
   - email-like strings
   - editing and cursor movement
   - repeated utility use
   - shortcut- or command-like flows
2. Audit the current 6-section symbols path, 8-section utility swipes, and editing/navigation behaviors as workflow surfaces.
3. Decide whether shortcut-like behavior or macro support deserves future research.
4. Compare whether mixed-task pain comes more from layout, from utility placement, or from mode switching.
5. Reuse those tasks in controller and accessibility branches where relevant.

**Artifacts To Produce**

- mixed-task benchmark pack
- utility and symbols audit memo
- recommendation on shortcut research versus staying text-only

**Key Literature Signals**

- Wu and Shi (2018)
- Shi (2015)
- `DownChord and UpChord`
- OrbiTouch background documents as product-history context
- `The effects of chorded keyboards on portable computing devices`

**Open Questions**

- Are symbols, editing, and navigation the hidden bottlenecks after basic letter entry becomes easy?
- Should utility and shortcut research be treated as a separate future ticket family?

**Decision Gate**

If mixed-task cost dominates real use, future product work should not keep treating layout-only typing speed as the main KPI.

### Branch Status

- Status: `Completed`
- Latest finding summary: mixed-task bottlenecks now look more likely to come from utility placement, symbol-mode switching, navigation asymmetry, and repeated correction work than from prose-layout ranking alone.
- Evidence reviewed: `KeyboardLogic`, `KeyboardContracts`, `KeyboardStateMachine`, User Guide utility and prediction flows, `benchmark_pack.md`, `results_and_logs/branch7_prediction_aware_benchmark_2026-04-28.md`, and `results_and_logs/branch7_mixed_task_workflow_pack_2026-04-28.md`.
- Open blocker: none.
- Next action: split a mixed-task execution ticket and measure the pack before opening shortcut or macro research.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: ready to split.

### Branch 8 - Adoption, Habit Formation, And Trust

**Goal**

Understand why users would keep using ERICK after the novelty period, not only whether they can eventually type fast.

**Work To Do**

1. Translate adoption research into ERICK-specific questions:
   - performance expectancy
   - effort expectancy
   - facilitating conditions
   - perceived usability
   - habit formation
   - trust in prediction and correction behavior
2. Define lightweight longitudinal proxies appropriate for an internal research spike:
   - return-to-practice behavior
   - voluntary lesson replay
   - settings churn
   - progression from Logical to Efficiency
   - controller retention versus abandonment
3. Compare where the first major drop-off probably happens:
   - setup
   - first lesson
   - first symbol use
   - first controller attempt
   - first prediction mistake
4. Decide whether ERICK’s most urgent next product work is about typing performance or about reducing setup and confidence friction.
5. Create a risk register for future research that could improve metrics but damage trust.

**Artifacts To Produce**

- adoption hypothesis map
- trust and retention proxy plan
- prioritized product-risk memo

**Key Literature Signals**

- Ong et al. (2024)
- Anderson et al. (2009)
- Norman and Fisher (1982)
- Twiddler longitudinal findings

**Open Questions**

- Is ERICK’s biggest current barrier productivity, or the cost of getting comfortable enough to keep going?
- Which behaviors count as credible internal retention signals without building analytics into a privacy-first product?

**Decision Gate**

If adoption barriers look dominated by setup, expectation, and trust rather than raw entry speed, prioritize product-experience tickets before new optimizer tickets.

### Branch Status

- Status: `Not Started`
- Latest finding summary: the literature strongly suggests adoption is its own research problem, and ERICK has not yet translated that into product-specific measures.
- Evidence reviewed: Ong et al. (2024), learning-curve papers, User Guide, current lesson and prediction surfaces.
- Open blocker: none.
- Next action: define the first ERICK-specific adoption hypotheses and proxy measures.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: belongs inside ERICK-151.

### Branch 9 - Personalization, Multilingual, And Future Splits

**Goal**

Hold the promising but still speculative directions in one place until the core keyboard experience is better measured.

**Work To Do**

1. Review whether layout personalization, domain-specific packs, and multilingual expansion should all stay together or split later.
2. Compare likely short-term value among:
   - domain-specific language models
   - user dictionary and learned bigram improvements
   - personalized onboarding or presets
   - profession-specific benchmark packs
   - multilingual or cross-language layout research
3. Decide whether future personalization should target prediction before layout, especially in 6-section mode where layout complexity is already sensitive.
4. Map which of these ideas require clean English baselines first and which can start earlier.
5. Recommend which candidate should become ERICK-151’s first real split ticket after the more grounded branches finish.

**Artifacts To Produce**

- future-split ranking memo
- dependency map for personalization and multilingual work
- recommendation on prediction-first versus layout-first personalization

**Key Literature Signals**

- Polacek (2015)
- exploratory background: `ssrn-5757903`
- ERICK-150 findings on branch splitting and benchmark discipline

**Open Questions**

- Is the next best adaptive path domain-aware prediction, user-specific lessons, or eventually personalized layouts?
- When does multilingual work become credible enough to leave the current English-first research track?

**Decision Gate**

Do not split this branch into product work until the core English, touch, controller, and onboarding evidence is less ambiguous.

### Branch Status

- Status: `Not Started`
- Latest finding summary: these directions look promising, but the current product and research baseline are not stable enough to prioritize them yet.
- Evidence reviewed: ERICK-150 outcomes, motor-impaired survey literature, exploratory personalization paper.
- Open blocker: none.
- Next action: keep this branch as a holding area until the more grounded branches produce real priorities.
- Whether the branch still belongs inside ERICK-151 or is finally ready to split: belongs inside ERICK-151.

---

## Recommended First Pass Order

1. `Branch 0` - freeze the scorecard and literature map
2. `Branch 1` - close the remaining shipped baseline and benchmark gaps
3. `Branch 2` - measure onboarding and practice aids
4. `Branch 4` - give controller typing a real benchmark story
5. `Branch 3` - convert accessibility claims into persona-and-task bundles
6. `Branch 5` - move from confusion buckets to recovery research
7. `Branch 6` - study prediction as live assistance
8. `Branch 7` - expand into mixed-task workflows
9. `Branch 8` - formalize adoption and trust measures
10. `Branch 9` - rank future personalization and multilingual splits

---

## Exit Conditions For ERICK-151

This ticket is ready to close when:

- the reviewed paper corpus has been distilled into branch-specific research signals rather than a flat reading list
- each branch has a goal, work list, output target, open questions, and decision gate in one place
- the ERICK Part 2 scorecard is defined clearly enough that later results can be compared across touch, controller, onboarding, and accessibility surfaces
- the remaining ERICK-150 baseline gaps are either closed or clearly labeled as their own follow-up
- at least the first follow-up split ticket candidate is ranked using measured evidence rather than intuition alone
- branches that end in `No-Go` outcomes are documented as useful research conclusions instead of silent dead ends
- no product-direction change is proposed without tying it back to both current implementation surfaces and at least one relevant literature signal

---

## Branch Tracking Template

Use the following structure inside this ticket as each branch progresses:

### Branch Status

- Status: `Not Started` / `Researching` / `Needs More Data` / `Ready For Split` / `No-Go`
- Latest finding summary
- Evidence reviewed
- Open blocker
- Next action
- Whether the branch still belongs inside ERICK-151 or is finally ready to split