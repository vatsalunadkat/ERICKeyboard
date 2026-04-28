# 6-Section Shipped Utility + Toggle-Pair Symbol Cost Full Run

Date: 2026-04-26

## Run Metadata

| Field | Value |
|---|---|
| Experiment ID | `6section-shipped-toggle-pair-2026-04-26` |
| Branch | `Branch 3` |
| Dial mode | `6-section` |
| Layout family | `Efficiency` |
| Utility model | `shipped` rotated wheel: `N Symbols`, `NE Shift`, `SE Space`, `S Period`, `SW Enter`, `NW Backspace` |
| Corpus ID | `mixed_shortform` |
| Corpus domain | Branch 8 benchmark-driven messaging, accessibility, controller, and punctuation-heavy shortform text |
| Symbol cost model | `toggle_pair` for non-period symbol clusters |
| Corpus source files | `benchmark_packs/messaging-shortform.txt`, `benchmark_packs/accessibility-supportive.txt`, `benchmark_packs/controller-tv-query.txt`, `benchmark_packs/punctuation-mixed.txt` |
| Search method | Parallel Tempering |
| Search settings | 8 chains, 500,000 steps per chain, swap interval 200, temps `0.012 -> 0.0002` |
| Objective weights | `1.0 / 0.6 / 0.3` |
| Random baseline sample count | `200` |
| Runtime | `916.8s` |

## Scorecard

| Metric | Value |
|---|---|
| Best score | `0.95350` |
| Random baseline mean | `1.39249` |
| Random baseline spread | `0.07509` |
| Improvement percent | `31.5%` |
| Predicted WPM | `71.1` |
| Stability / spread metric | cluster spread `1.937` |
| Layout delta versus `single_toggle` run | `9 / 36` normal-layer slots changed |
| Shipped-map match or drift note | resulting normal-layer map still matches only `2 / 36` slots in the current shared placeholder map |
| Known caveats | symbol-heavy text is still approximated through toggle tokens; the symbol layer itself is not re-optimized here |

## Comparison Against The Earlier Single-Toggle Approximation

| Metric | `single_toggle` | `toggle_pair` | Change |
|---|---|---|---|
| Best score | `0.97299` | `0.95350` | `-0.01949` |
| Random baseline mean | `1.41998` | `1.39249` | `-0.02749` |
| Improvement percent | `31.5%` | `31.5%` | effectively unchanged |
| Predicted WPM | `70.6` | `71.1` | `+0.5` |
| `TOGGLE_SYMBOLS` unigram coverage | `0.0184` | `0.0362` | roughly doubled |
| Placeholder-map matches | `2 / 36` | `2 / 36` | unchanged |

The `toggle_pair` approximation improved the best score and changed the winning normal-layer layout in `9 / 36` slots, which is strong enough to treat symbol entry and exit cost as a first-class Branch 3 modeling choice rather than a tie-breaker.

## Best Normal-Layer Layout

| Left \ Right | N | NE | SE | S | SW | NW |
|---|---|---|---|---|---|---|
| N | c | s | h | 6 | q | u |
| NE | i | t | r | y | 7 | x |
| SE | v | a | e | p | j | 8 |
| S | 1 | f | d | l | b | 9 |
| SW | 5 | 3 | 2 | z | g | m |
| NW | n | k | 4 | 0 | w | o |

## Artifacts

| Artifact | Path |
|---|---|
| Raw output log | `results_and_logs/optimization_results_6section_shipped_toggle_pair_full_2026-04-26.txt` |
| Earlier single-toggle comparison point | `results_and_logs/optimization_results_6section_shipped_mixed_shortform_2026-04-26.md` |
| Updated script | `../erick_v5_6section.py` |

## Interpretation

- The first Branch 3 comparison shows that charging both entry and exit for non-period symbol clusters changes the best mixed-text 6-section layout materially, not just cosmetically.
- `toggle_pair` is the better current approximation for future mixed-text reruns because it improved score and predicted WPM while leaving the stale shared placeholder map just as unsupported as before.
- This is still not exact shipped parity. The optimizer is only scoring toggle actions, not selecting or re-optimizing the symbol-layer placements themselves.