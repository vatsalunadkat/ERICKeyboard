# Branch 0 - Part 2 Scorecard Freeze (2026-04-28)

## Purpose

Freeze the common evaluation language for ERICK-151 before later branches start producing their own metrics.

This note turns the Part 2 paper review into an ERICK-specific literature map, defines the minimum reusable task set, and separates truly comparable results from exploratory prototypes.

## Paper-to-Branch Literature Map

| Part 2 Surface | ERICK-151 Branches | Representative papers | Why the papers matter for ERICK |
|---|---|---|---|
| Learnability and onboarding | Branch 2 | Gopher and Raij (1988), Anderson et al. (2009), Wu (2014), Twiddler novice and expert studies, Senorita | Chord systems live or die on early teaching cost, phrase ordering, cueing, and the gap between expert demos and novice behavior. |
| Accessibility and disability evaluation | Branch 3 | Polacek (2015), Input Assistive Keyboards for People with Disabilities, Kirschenbaum et al. (1986), Design and Implementation of a Chorded On-Screen Keyboard, Senorita | ERICK needs persona-task evidence, not one generic accessibility claim. |
| Controller and portable-device use | Branch 4 | Twiddler studies, The effects of chorded keyboards on portable computing devices, Shi (2015), Wigdor (2004) | Controller and portable contexts can change timing needs, calibration expectations, and what counts as efficient enough. |
| Error tolerance and recovery | Branches 5 and 6 | Kreifeldt et al. (1989), Wigdor (2004), Polacek (2015) | Alternative keyboards need explicit research on correction cost, ambiguity tolerance, and trust after a miss. |
| Ergonomics and workload | Branches 0, 1, 3, and 7 | Alden et al. (1972), Hargreaves et al. (1992), McAlindon (1994), Wu and Shi (2018) | ERICK should evaluate whole-task workload and recovery cost, not only abstract symbol placement. |
| Adoption, trust, and habit formation | Branch 8 | Ong et al. (2024), Norman and Fisher (1982), older chord-coding comparisons | Adoption depends on effort expectancy, facilitating conditions, and confidence, not only peak typing speed. |
| Personalization and future splits | Branch 9 | Wu (2014), Senorita, portable-device papers | Personalization stays promising, but should wait until the core learning and baseline questions are measured first. |

## Frozen Core Task Set

| Task ID | Reusable task | Primary branches | Minimum metrics |
|---|---|---|---|
| `T1` | Guided first-session lesson slice: first correct word, first utility use, first correction | Branches 2, 3, 8 | completion time, help usage, abandonment point, correction count |
| `T2` | Shortform messaging reply | Branches 1, 2, 4, 6, 8 | completion time, corrected error count, uncorrected errors, suggestion actions |
| `T3` | Accessibility-supportive phrase entry | Branches 1, 3, 6, 8 | completion time, error rate, settings bundle used, workload note |
| `T4` | Controller or portable-device query entry | Branches 1, 4, 8 | completion time, confusion bucket counts, retry count, calibration notes |
| `T5` | Punctuation and edit-heavy sentence | Branches 1, 5, 7 | completion time, symbol-layer activations, correction cost, recovery path used |
| `T6` | Recovery after a seeded miss or slip | Branches 5, 6, 8 | time to recover, backspace cost, suggestion acceptance or rejection, trust note |

## Part 2 Scorecard

### Required for behavioral task runs

| Metric | Required for | Why it stays mandatory |
|---|---|---|
| Task completion time | touch, controller, one-handed assist, accessibility bundles | Keeps later branches anchored to a real task outcome. |
| Corrected errors | all behavioral runs | Separates speed from how much cleanup the user paid for. |
| Uncorrected errors | all behavioral runs | Prevents misleading speed wins that leave bad output behind. |
| Recovery cost | any run with edits, misses, or symbol work | Makes Branches 5 and 7 comparable to plain typing tasks. |
| Utility and layer actions | controller, 6-section, punctuation-heavy, assisted runs | Needed to understand whether a result depends on hidden mode churn. |
| Suggestion accept or reject count | any run with prediction visible | Keeps Branch 6 tied to observed behavior instead of anecdotes. |
| Confusion bucket notes | controller and onboarding runs, optional elsewhere | Reuses the research language opened by the diagnostics drill. |
| Short workload or usability note | all behavioral runs | Captures adoption and accessibility signals without pretending this is a formal lab study. |

### Required for replay or optimizer-style runs

| Metric | Required for | Why it stays mandatory |
|---|---|---|
| Objective score | all replay or optimizer runs | Primary numeric comparison surface for layout family work. |
| Predicted WPM | all replay or optimizer runs | Keeps the score legible to product decisions. |
| Corpus or task pack ID | all replay or optimizer runs | Prevents silent drift between `wordfreq`, mixed shortform, and future task packs. |
| Utility model and symbol treatment | all replay or optimizer runs | Needed because these assumptions now move results materially. |
| Slot drift versus shipped map | baseline and candidate replays | Keeps research output tied to the product path rather than only abstract winners. |
| Coverage note for omitted symbols or layers | any partial-inventory replay | Prevents partial probes from being misread as exact product baselines. |

## Comparability Rules

| Result family | Counts as comparable when | Does not count as comparable when |
|---|---|---|
| `behavioral-core` | same task ID, same hardware path, same assist level, same training state, same visible suggestion policy | task set, hardware, or training state changed materially |
| `replay-core` | same corpus pack, same utility model, same symbol treatment, same effort profile, same coverage rules | symbol inventory changed silently or one run includes a layer the other ignores |
| `cross-family-context` | behavioral and replay results are discussed together only as context | one family is used as direct proof for the other |
| `proposal-only` | early sketches or probes can inform next steps | proposal-only work cannot justify product changes on its own |

## Core Decisions Frozen For Part 2

1. Later ERICK-151 notes should name a task ID or replay family explicitly.
2. Behavioral notes and replay notes should not be mixed into one winner claim.
3. Any partial-inventory replay must state the omitted symbols or layers in the summary table.
4. Product-direction claims should cite at least one comparable family, not only a promising prototype or anecdote.

## Immediate Effect On Later Branches

- Branch 1 can now record exact shipped-path gaps without pretending that a partial replay is a full rerun.
- Branches 2 through 8 can reuse `T1` through `T6` instead of inventing new task bundles every time.
- Branch 8 adoption work now has a minimum subjective note requirement, so trust and effort expectancy are not left implicit.

## Recommended Next Step

Use this note as the baseline reference for every ERICK-151 branch summary until a later ticket deliberately replaces the scorecard.