# Branch 1 - Direct 6-Section Implementation And Exact 8-Section Rerun (2026-04-28)

The explicit 8-section shipment decision for this rerun is recorded in `branch1_8section_shipment_decision_2026-04-28.md`.

## Later Product Addendum

The historical shipment recommendation in this note was later superseded on the product path.

- the shared 8-section `Efficiency` map was later updated directly in `KeyboardLogic.kt`
- a post-implementation replay now scores the shipped 8-section map at `0.90671` and `71.7` predicted WPM under the same `mixed_shortform` + `shipped_exact` benchmark settings
- the rerun winner recorded here is therefore no longer only a research artifact; it is now the shipped 8-section preset

## Scope

This note records the second Branch 1 pass after the initial baseline probe.

It covers two concrete outcomes:

1. the shipped 6-section `Efficiency` layout was updated directly in shared code to the measured Branch 3 winner instead of spawning a separate follow-up ticket
2. the 8-section optimizer was rerun on the Branch 8 benchmark packs with an explicit shipped-symbol policy instead of the earlier shared-inventory replay

## 6-Section Direct Implementation

### Change made

- Shared file updated: `android/shared/src/commonMain/kotlin/KeyboardLogic.kt`
- Shipped map replaced with the Branch 3 `toggle_pair` winner from `optimization_results_6section_shipped_toggle_pair_2026-04-26.md`
- Shared regression coverage added in `android/shared/src/commonTest/kotlin/KeyboardLogicTest.kt`

### Validation

- `branch1_baseline_probe.py` rerun after the implementation
- `android\\gradlew.bat :shared:testAndroidHostTest`

### Post-implementation replay result

| Layout | Score | Predicted WPM | Exact slot match |
|---|---:|---:|---|
| current shipped `efficiencyNormalMap6` | `0.95497` | `71.1` | `36 / 36` versus the Branch 3 winner |
| Branch 3 `toggle_pair` winner | `0.95497` | `71.1` | same map |

The 6-section shipped-path baseline question is now closed for the current product path: the shipped 6-section `Efficiency` preset matches the best measured mixed-shortform winner that ERICK has checked in today.

## Exact 8-Section Benchmark-Pack Rerun

### Explicit symbol-policy choice

This rerun used:

- `ERICK8_CORPUS_PROFILE=mixed_shortform`
- `ERICK8_SYMBOL_POLICY=shipped_exact`

`shipped_exact` means the optimizer used the exact then-current shipped 8-section normal-layer chord inventory recorded in `KeyboardLogic.kt` at rerun time rather than the older research punctuation inventory. That kept the benchmark-pack rerun aligned to the product punctuation policy instead of comparing across mismatched symbol sets.

### Run metadata

| Field | Value |
|---|---|
| Experiment ID | `8section-shipped-exact-mixed-shortform-2026-04-28` |
| Dial mode | `8-section` |
| Corpus ID | `mixed_shortform` |
| Symbol policy | `shipped_exact` |
| Chord-assigned symbol count | `45` |
| Utility wheel | current 8-action shared utility model (`SHIFT`, `SPACE`, `BACKSPACE`, `ENTER`, `CAPSLOCK`, `TAB`, `.`, `,`) |
| Search settings | 8 chains, 500,000 steps per chain, swap interval 200, temps `0.012 -> 0.0002` |
| Objective weights | `1.0 / 0.6 / 0.3` |
| Random baseline sample count | `200` |
| Runtime | `806.1s` |
| Raw log | `optimization_results_8section_shipped_exact_mixed_shortform_full_2026-04-28.txt` |

### Full-run scorecard

| Metric | Value |
|---|---|
| Best score | `0.90671` |
| Random baseline mean | `1.66322` |
| Random baseline spread | `0.12980` |
| Improvement percent | `45.5%` |
| Predicted WPM | `71.7` |
| Cluster spread | `1.340` |

### Best layout from the exact rerun

| Left \ Right | N | NE | E | SE | S | SW | W | NW |
|---|---|---|---|---|---|---|---|---|
| N | c | a | h | / |  |  | ] | k |
| NE | i | t | s | u |  |  | \\ | x |
| E | v | r | e | n | q |  |  | [ |
| SE | 4 | f | l | o | w | ` |  |  |
| S |  |  | = | d | g | ; | 3 |  |
| SW |  |  |  | ' | z | m | j | 7 |
| W | 0 |  |  |  | 8 | 6 | b | 2 |
| NW | p | - | 9 |  |  | 1 | 5 | y |

## Then-Current Shipped 8-Section Map Versus The Exact Winner

The then-current shipped 8-section map was rescored under the same `mixed_shortform` + `shipped_exact` settings.

| Layout | Score | Predicted WPM | Exact slot match |
|---|---:|---:|---|
| current shipped `efficiencyNormalMap` | `0.95690` | `70.5` | `5 / 64` versus the exact rerun winner |
| exact rerun winner | `0.90671` | `71.7` | same comparison anchor |

### Main finding

- The exact rerun winner improves score by about `5.2%` over the then-current shipped 8-section map.
- Predicted WPM increases by about `1.2`.
- The two maps share only `5 / 64` exact slots.

That is no longer a “same family with small punctuation drift” result. Under the explicit shipped-symbol policy, the optimizer found a materially different 8-section layout family.

## Branch 1 Conclusion

- Item `1` was implemented directly: the shipped 6-section `Efficiency` map is no longer a placeholder.
- Item `2` now has a true exact benchmark-pack rerun with a declared symbol policy.
- Branch 1 is complete as a baseline-cleanup branch.

## Historical Recommended Next Step

Use the exact winner as a research candidate, not an immediate in-place replacement for the then-current shipped 8-section `Efficiency` preset.

That recommendation is preserved here for historical context only. The product path later accepted the in-place replacement after the current active-user base made the migration risk acceptable.