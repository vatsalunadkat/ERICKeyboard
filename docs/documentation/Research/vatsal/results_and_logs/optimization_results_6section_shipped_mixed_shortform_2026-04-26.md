# 6-Section Shipped Utility + Mixed Shortform Full Run

Date: 2026-04-26

## Run Metadata

| Field | Value |
|---|---|
| Experiment ID | `6section-shipped-mixed-shortform-2026-04-26` |
| Branch | `Branch 0` with `Branch 3` utility-cost groundwork |
| Dial mode | `6-section` |
| Layout family | `Efficiency` |
| Utility model | `shipped` rotated wheel: `N Symbols`, `NE Shift`, `SE Space`, `S Period`, `SW Enter`, `NW Backspace` |
| Corpus ID | `mixed_shortform` |
| Corpus domain | Branch 8 benchmark-driven messaging, accessibility, controller, and punctuation-heavy shortform text |
| Corpus source files | `benchmark_packs/messaging-shortform.txt`, `benchmark_packs/accessibility-supportive.txt`, `benchmark_packs/controller-tv-query.txt`, `benchmark_packs/punctuation-mixed.txt` |
| Corpus size / sample count | 4 frozen shortform seed packs |
| N-gram source | generated in-script from benchmark-pack tokenization |
| Search method | Parallel Tempering |
| Search settings | 8 chains, 500,000 steps per chain, swap interval 200, temps `0.012 -> 0.0002` |
| Objective weights | `1.0 / 0.6 / 0.3` |
| Random baseline sample count | `200` |
| Runtime | `352.8s` |

## Scorecard

| Metric | Value |
|---|---|
| Best score | `0.97299` |
| Random baseline mean | `1.41998` |
| Random baseline spread | `0.07649` |
| Improvement percent | `31.5%` |
| Predicted WPM | `70.6` |
| Stability / spread metric | cluster spread `1.937` |
| Shipped-map match or drift note | shipped utility wheel is now modeled directly, but the new normal-layer result matches only `2 / 36` placeholder slots in `KeyboardLogic.kt` (`N[1]=s`, `NE[1]=t`) |
| Known caveats | symbol-heavy text is approximated through `TOGGLE_SYMBOLS` utility tokens; the symbol layer itself is not re-optimized here |

## Corpus And Utility Findings

- Utility unigram coverage was non-zero for `TOGGLE_SYMBOLS=0.0184`, `SPACE=0.1043`, and `.=0.0057`.
- The mixed-text profile generated non-zero utility transitions in `UC`, `CU`, `UCC`, `CCU`, and `CUC` categories.
- This makes the shipped rotated utility wheel materially visible to the score function in a way the earlier legacy wordfreq path did not.

## Best Normal-Layer Layout

| Left \ Right | N | NE | SE | S | SW | NW |
|---|---|---|---|---|---|---|
| N | c | s | h | 0 | q | u |
| NE | i | t | r | f | 1 | x |
| SE | v | a | e | p | 8 | j |
| S | 7 | y | d | l | b | 9 |
| SW | 5 | 6 | 2 | z | g | m |
| NW | n | k | 4 | 3 | w | o |

## Artifacts

| Artifact | Path |
|---|---|
| Raw output log | `results_and_logs/optimization_results_6section_shipped_mixed_shortform_full_2026-04-26.txt` |
| Smoke validation predecessor | `results_and_logs/optimization_results_6section_shipped_mixed_shortform_smoke_2026-04-26.md` |
| Updated script | `../erick_v5_6section.py` |

## Interpretation

- This is the first full 6-section run in the repo that uses the shipped rotated utility wheel rather than the older legacy 5-action utility model.
- Its `31.5%` improvement is directionally strong and lands in the same general performance band as the older legacy 6-section baseline, but it should not be compared as an exact like-for-like replacement because the corpus and utility modeling are materially different.
- The result closes part of the Branch 0 blocker by proving a shipped-path optimizer run now exists. It does not fully close the blocker because symbol-heavy text is still approximated as utility toggles instead of using a re-optimized symbol layer, and the current shared placeholder map still matches only `2 / 36` slots from this run.