# Branch 7 - Prediction-Aware Benchmark Pass

Date: 2026-04-28

## Run Metadata

| Field | Value |
|---|---|
| Experiment ID | `branch7-prediction-aware-benchmark-2026-04-28` |
| Branch | `Branch 7` |
| Benchmark packs | `accessibility-supportive`, `controller-tv-query`, `messaging-shortform`, `punctuation-mixed` |
| Predictor source | `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` |
| Layout candidates | Branch 2 `default`, `bigram_up`, and `trigram_up` winners for both 6-section and 8-section |
| Evaluation mode | post-hoc benchmark only; no optimizer-integrated prediction objective |
| Suggestion policy modeled | top-3 prefix completions while a word is in progress, top-3 next-word suggestions when the buffer is empty |
| Suggestion tap cost | one easiest-chord equivalent tap in the active dial mode |
| Prefix acceptance guard | only treated as safe when the target word is followed by whitespace |

## Core Result

Prediction produced measurable savings on the benchmark pack, but it did not change the ranking of the candidate layouts in either dial mode.

That means the first Branch 7 benchmark pass is a `No-Go` for optimizer-coupled prediction scoring right now. The evidence supports keeping prediction-aware evaluation as a post-hoc reporting layer until later corpora or learned-state assumptions show a real ranking change.

## 6-Section Results

| Layout | Raw cost | Prediction-adjusted cost | Savings | Prefix hit rate | Prefix-safe share | Mean useful prefix depth | Next-word hit rate | Winner order changed? |
|---|---|---|---|---|---|---|---|---|
| `6_bigram_up` | `213.5658` | `201.0226` | `5.87%` | `37.72%` | `68.60%` | `1.81` | `0.00%` | no |
| `6_default` | `215.4132` | `202.1265` | `6.17%` | `37.72%` | `68.60%` | `1.81` | `0.00%` | no |
| `6_trigram_up` | `215.3849` | `202.0982` | `6.17%` | `37.72%` | `68.60%` | `1.81` | `0.00%` | no |

Raw ranking stayed `6_bigram_up`, `6_trigram_up`, `6_default` after prediction adjustment.

## 8-Section Results

| Layout | Raw cost | Prediction-adjusted cost | Savings | Prefix hit rate | Prefix-safe share | Mean useful prefix depth | Next-word hit rate | Winner order changed? |
|---|---|---|---|---|---|---|---|---|
| `8_default` | `211.2581` | `199.0753` | `5.77%` | `37.72%` | `68.60%` | `1.81` | `0.45%` | no |
| `8_bigram_up` | `212.4037` | `199.6661` | `6.00%` | `37.72%` | `68.60%` | `1.81` | `0.45%` | no |
| `8_trigram_up` | `213.2215` | `200.9125` | `5.77%` | `37.72%` | `68.60%` | `1.81` | `0.45%` | no |

Raw ranking stayed `8_default`, `8_bigram_up`, `8_trigram_up` after prediction adjustment.

## Interpretation

- Prefix completion is the only practically relevant source of savings in this first pass. Across the Branch 8 benchmark pack, `37.72%` of words were reachable through a top-3 prefix suggestion, and `68.60%` of those opportunities were followed by whitespace and therefore safe under the current conservative acceptance rule.
- The average useful prefix depth was only `1.81` characters, which confirms the earlier Branch 7 hypothesis that prediction mostly compresses later-letter cost rather than early-prefix effort.
- Next-word suggestions barely appeared in this benchmark family and never beat raw entry under the modeled tap cost. The current built-in bigram dictionary helps very little on these shortform packs unless later learned-state or domain-specific corpora shift the hit rate materially.
- Because the ranking did not change in either mode, Branch 7 should currently remain a post-hoc evaluation layer, not a coupled optimizer objective.

## Recommendation

- Keep the new prediction-aware harness as a reporting tool for future branch reruns.
- Do not retune the optimizer objective around prediction yet.
- Re-open coupled prediction scoring only if later benchmark packs, learned-state replay, or domain-specific next-word corpora materially change layout ranking.

## Artifacts

| Artifact | Path |
|---|---|
| Raw benchmark output | `results_and_logs/branch7_prediction_aware_benchmark_2026-04-28.txt` |
| Benchmark harness | `../prediction_aware_benchmark.py` |
| Earlier metric proposal | `results_and_logs/branch7_prediction_aware_metric_proposal_2026-04-26.md` |