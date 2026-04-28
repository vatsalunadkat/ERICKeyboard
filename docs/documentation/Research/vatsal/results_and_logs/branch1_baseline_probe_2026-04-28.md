# Branch 1 - Shipped Baseline Probe (2026-04-28)

This note is the first-pass Branch 1 probe only. The direct 6-section implementation and the exact 8-section shipped-symbol rerun are recorded in `branch1_exact_8section_rerun_2026-04-28.md`.

## Scope

This is the first Branch 1 measurement pass, not the final closure of the branch.

The goal here was to produce the closest useful shipped-baseline evidence with the smallest safe move:

- replay the current shipped `efficiencyNormalMap6` under the shipped 6-section mixed-shortform objective
- compare it against the best current measured 6-section research candidate from the Branch 3 `toggle_pair` run
- replay the current shipped 8-section map and the logged `v5_output.txt` winner against the Branch 8 benchmark packs on a shared inventory, instead of waiting for a full new optimizer rerun before learning anything

## Method

### 6-section replay family

- Script: `branch1_baseline_probe.py`
- Objective family: `shipped-mixed-shortform`
- Utility model: `shipped`
- Symbol treatment: `toggle_pair`
- Maps compared:
  - current shipped `efficiencyNormalMap6` from `KeyboardLogic.kt`
  - Branch 3 `toggle_pair` winner from `optimization_results_6section_shipped_toggle_pair_2026-04-26.md`

### 8-section replay family

- Script: `branch1_baseline_probe.py`
- Task pack: Branch 8 shortform benchmark packs
- Inventory rule: shared 40-symbol inventory only
- Shared inventory contents:
  - 26 letters
  - 10 digits
  - 4 punctuation symbols shared by both maps: apostrophe, hyphen, slash, semicolon
- Maps compared:
  - current shipped `efficiencyNormalMap` from `KeyboardLogic.kt`
  - logged v5 winner from `v5_output.txt`

This means the 8-section result is a benchmark-pack replay, not yet a full exact symbol-layer rerun.

## Results

| Layout | Family | Score | Predicted WPM | Slot drift summary |
|---|---|---:|---:|---|
| current shipped `efficiencyNormalMap6` | 6-section shipped mixed shortform | `1.15437` | `63.9` | matches only `2 / 36` slots against the Branch 3 winner |
| Branch 3 `toggle_pair` winner | 6-section shipped mixed shortform | `0.95497` | `71.1` | same comparison anchor |
| current shipped `efficiencyNormalMap` | 8-section benchmark-pack replay on shared 40-symbol inventory | `0.94816` | `70.9` | `44 / 64` exact slot matches against the logged v5 winner |
| logged `v5_output.txt` winner | 8-section benchmark-pack replay on shared 40-symbol inventory | `0.95189` | `71.0` | same comparison anchor |

Raw output is recorded in `branch1_baseline_probe_2026-04-28.txt`.

## Findings

### 1. The current shipped 6-section `Efficiency` placeholder is not close enough to treat as a solved baseline.

- The shipped placeholder scores `20.9%` worse than the Branch 3 winner on the same objective family.
- Predicted speed is `7.2` WPM lower.
- The normal-layer maps share only `2 / 36` slots.

This is strong evidence that the shipped 6-section `Efficiency` layout is still mostly historical accident, not a near-miss research winner.

### 2. The current shipped 8-section layout no longer looks like the biggest open risk on shortform alphanumeric work.

- On the shared 40-symbol inventory, the shipped map edges the logged v5 winner by about `0.4%` on score.
- Predicted WPM is effectively tied.
- The two maps still share `44 / 64` exact slots, which reinforces that the shipped 8-section layout remains inside the same research family even though its punctuation and filler policy drifted.

This does not prove that the shipped 8-section map is globally better. It does show that Branch 1 should spend more attention on 6-section cleanup than on an urgent 8-section alphanumeric reshuffle.

### 3. The remaining 8-section gap is now mostly a symbol-policy question.

The replay had to use a shared inventory because the current shipped 8-section normal map and the historical v5 research winner do not expose the same punctuation set. That makes the remaining work narrower:

- decide whether ERICK wants an exact benchmark-pack-capable 8-section symbol policy
- decide whether a full 8-section rerun should preserve the shipped punctuation inventory or restore the older research inventory for comparison only

## Branch 1 State After This Probe

- The 6-section exact shipped-path problem is still open, but it is now much better bounded.
- The 8-section benchmark-pack adoption gap is partially closed through a shared-inventory replay.
- A full exact 8-section benchmark-pack rerun is still worth doing only if later branches actually need symbol-layer conclusions, not just alphanumeric continuity.

## Recommended Next Step

Split a focused follow-up ticket for the shipped 6-section `Efficiency` map if later Branch 1 work confirms that the placeholder should no longer remain on the product path.