# Branch 2 - Weight Sensitivity And Search Tuning Probe

Date: 2026-04-26

## Run Metadata

| Field | Value |
|---|---|
| Experiment ID | `branch2-weight-sensitivity-2026-04-26` |
| Branch | `Branch 2` |
| Search method | Parallel Tempering |
| Search settings | 8 chains, 100,000 steps per chain, swap interval `200`, temps `0.012 -> 0.0002` |
| Random baseline sample count | `100` |
| Prediction assumptions | none; this branch measures raw optimizer sensitivity before any Branch 7 saved-keystroke adjustment |
| Weight families tested | default `1.0 / 0.6 / 0.3`, `bigram_up` = `1.0 / 0.7 / 0.2`, `trigram_up` = `1.0 / 0.5 / 0.4` |

## Dial-Mode Inputs

| Dial mode | Corpus ID / benchmark IDs | Utility model | Symbol cost model | Effort profile | Comparability family |
|---|---|---|---|---|---|
| 6-section | `mixed_shortform` using `messaging-shortform`, `accessibility-supportive`, `controller-tv-query`, and `punctuation-mixed` | shipped rotated wheel | `toggle_pair` | `shared_derived` | `shipped-mixed-shortform` |
| 8-section | `wordfreq` top 50k via `general-wordfreq-50k` continuity baseline | shipped 8-section fixed utilities | n/a | v5 default | `legacy-wordfreq` |

## Scorecards

Raw objective scores change scale with the coefficient mix, so the useful comparisons here are the improvement percentage within each weight family, the predicted WPM proxy, and how much the winning map moves.

### 6-Section Results

| Weight family | Best score | Random baseline | Improvement | Predicted WPM | Best score at 50k | Gain after 50k |
|---|---|---|---|---|---|---|
| default `1.0 / 0.6 / 0.3` | `0.95350` | `1.38480 +/- 0.08131` | `31.1%` | `71.1` | `0.95350` | `0.00000` |
| `bigram_up` `1.0 / 0.7 / 0.2` | `0.92634` | `1.34965 +/- 0.08110` | `31.4%` | `71.4` | `0.92634` | `0.00000` |
| `trigram_up` `1.0 / 0.5 / 0.4` | `0.98049` | `1.41995 +/- 0.08155` | `30.9%` | `71.1` | `0.98053` | `-0.00004` |

The fixed-runtime default 6-section rerun reproduced the earlier full `toggle_pair` best score exactly at `0.95350`; only the baseline sample count differs from the earlier 200-sample Branch 3 write-up.

### 8-Section Results

| Weight family | Best score | Random baseline | Improvement | Predicted WPM | Best score at 50k | Gain after 50k |
|---|---|---|---|---|---|---|
| default `1.0 / 0.6 / 0.3` | `0.85512` | `1.55979 +/- 0.11341` | `45.2%` | `73.8` | `0.85512` | `0.00000` |
| `bigram_up` `1.0 / 0.7 / 0.2` | `0.83281` | `1.52292 +/- 0.11269` | `45.3%` | `73.4` | `0.83284` | `-0.00003` |
| `trigram_up` `1.0 / 0.5 / 0.4` | `0.87751` | `1.59666 +/- 0.11416` | `45.0%` | `73.4` | `0.87856` | `-0.00105` |

## Layout Stability

Top-12 movement counts use the current English core `etaoinshrdlc` as the quick stability check.

| Dial mode | Comparison | Normal-layer slot delta | Top-12 letters moved | Reading |
|---|---|---|---|---|
| 6-section | default -> `bigram_up` | `25 / 36` | `6 / 12` | materially different winner family |
| 6-section | default -> `trigram_up` | `11 / 36` | `0 / 12` | mostly lower-frequency and digit reshuffling |
| 8-section | default -> `bigram_up` | `39 / 64` | `11 / 12` | very high drift for a marginal within-family gain |
| 8-section | default -> `trigram_up` | `31 / 64` | `5 / 12` | still a large family shift without a compensating gain |

## Search Tuning Readout

The current Parallel Tempering schedule looks stable enough for screening work.

| Probe set | Largest improvement after 50k | What it means |
|---|---|---|
| 6-section default / `bigram_up` / `trigram_up` | `0.00004` | 50k per chain already captured effectively all remaining gain |
| 8-section default / `bigram_up` / `trigram_up` | `0.00105` | 50k per chain is sufficient for coarse sweeps; 100k is enough for documented sensitivity probes |

This does not prove that 500k-step confirmation runs are unnecessary. It does show that Branch 2 does not need a more aggressive temperature schedule or a heavier runtime budget just to screen nearby coefficient mixes.

## Recommendation

- Keep `1.0 / 0.6 / 0.3` as the shipping-adjacent default for both modes.
- Use 50k steps per chain for quick coefficient screening, 100k for branch-level documented sweeps, and reserve 500k reruns for final candidate confirmation.
- Do not split Branch 2 into a standalone optimizer-retuning effort right now. The coefficient changes can move layouts substantially, but none of the tested mixes produced a strong cross-mode reason to replace the current default.
- Re-open coefficient tuning only if a later Branch 4 or Branch 7 objective adds new evidence, or if a new benchmark family shows a non-default mix winning consistently enough to offset the layout drift.

## Artifacts

| Artifact | Path |
|---|---|
| 6-section default raw log | `results_and_logs/optimization_results_6section_branch2_default_probe_2026-04-26.txt` |
| 6-section `bigram_up` raw log | `results_and_logs/optimization_results_6section_branch2_bigram_up_probe_2026-04-26.txt` |
| 6-section `trigram_up` raw log | `results_and_logs/optimization_results_6section_branch2_trigram_up_probe_2026-04-26.txt` |
| 8-section default raw log | `results_and_logs/optimization_results_8section_branch2_default_probe_2026-04-26.txt` |
| 8-section `bigram_up` raw log | `results_and_logs/optimization_results_8section_branch2_bigram_up_probe_2026-04-26.txt` |
| 8-section `trigram_up` raw log | `results_and_logs/optimization_results_8section_branch2_trigram_up_probe_2026-04-26.txt` |
| Updated 8-section script | `../erick_v5_vectorized.py` |