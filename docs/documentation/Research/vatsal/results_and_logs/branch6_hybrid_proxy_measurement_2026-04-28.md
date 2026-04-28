# Branch 6 - Learnability Proxy Measurement

Date: 2026-04-28

## Run Metadata

| Field | Value |
|---|---|
| Experiment ID | `branch6-learnability-probe-2026-04-28` |
| Branch | `Branch 6` |
| Replay script | `docs/documentation/Research/vatsal/branch6_learnability_probe.py` |
| Objective replay | `ERICK6_UTILITY_MODEL=shipped`, `ERICK6_CORPUS_PROFILE=mixed_shortform`, `ERICK6_SYMBOL_COST_MODEL=toggle_pair`, `ERICK6_EFFORT_PROFILE=shared_derived` |
| Candidate layouts | current `Logical` 6-section map, current shared `efficiencyNormalMap6` placeholder, and the Branch 3 `toggle_pair` mixed-text winner |
| Learnability proxy | `row_dispersion + preview_jump_penalty + lesson_span_penalty` |
| Utility anchor handling | held constant across all candidates because the shipped 6-section utility wheel does not move between layouts |
| Lesson anchors | `face`, `907`, `?`, `go on`, `go.`, `?`, `be`, `12`, `go.` from the first 6-section basics, utility, and assisted-mode drills |

## Core Result

The first measured Branch 6 pass does **not** justify a hybrid layout candidate.

The current `Logical` 6-section layout is much easier under the proposed learnability proxies, but it gives back far too much raw efficiency. The Branch 3 mixed-text winner remains the best pure-efficiency candidate and even beats the current shared placeholder efficiency map on both speed and the proxy score.

That makes Branch 6 a `No-Go` for a new hybrid shipping direction inside ERICK-150. The product already exposes the learnability side of the tradeoff through the existing `Logical` layout, so the measured gap does not support inventing a third built-in family yet.

## Scorecard

| Layout | Replay score | Predicted WPM | Row dispersion | Preview jump penalty | Lesson span penalty | Learnability proxy | Score loss vs best efficiency | Learnability gain vs best efficiency |
|---|---|---|---|---|---|---|---|---|
| `branch3_toggle_pair` | `0.95497` | `71.1` | `5.53` | `6.20` | `11.50` | `23.23` | baseline | baseline |
| current `Logical` 6-section | `1.36054` | `57.1` | `0.37` | `4.20` | `10.50` | `15.07` | `42.5%` worse | `35.2%` better |
| current shared `efficiencyNormalMap6` placeholder | `1.15437` | `63.9` | `7.27` | `7.40` | `11.50` | `26.17` | `20.9%` worse | `12.6%` worse |

## Hybrid Gate Check

Branch 6 proposed that a hybrid candidate should only be treated as interesting when it stays within `3%` of the best pure-efficiency score while improving learnability proxies by at least `15%`.

Measured against that gate:

- current `Logical` 6-section improves the proxy strongly, but misses the efficiency gate by a wide margin.
- current shared `efficiencyNormalMap6` placeholder misses both gates.
- the Branch 3 `toggle_pair` winner remains the best pure-efficiency candidate and therefore the comparison baseline, not the hybrid challenger.

No checked-in candidate cleared the hybrid-interest threshold.

## Interpretation

- Branch 6 now has a real measured separation between `Logical` and `Efficiency` rather than only a conceptual argument. Learnability and pure efficiency are pulling in different directions, but not closely enough to support a blended winner.
- The current shared placeholder efficiency map is dominated by the Branch 3 mixed-text winner. That means future hybrid exploration should compare against the Branch 3 family, not against the placeholder map in `KeyboardLogic.kt`.
- The measured result strengthens the existing product story instead of complicating it: `Logical` stays the learnable built-in path, and `Efficiency` stays the speed-oriented path.

## Caveat

The replay score for the Branch 3 winner in this probe (`0.95497`) is slightly above the original checked-in Branch 3 report (`0.95350`). Treat the 2026-04-26 Branch 3 result note as the authoritative historical run; the replay here is close enough to support Branch 6 comparisons without changing the earlier branch conclusion.

## Recommendation

- Mark Branch 6 as completed inside ERICK-150 with a `No-Go` outcome for new hybrid shipping candidates.
- Keep any future hybrid work exploratory until a candidate is both within `3%` of the best efficiency score and at least `15%` better on the learnability proxy.
- If later work revisits this branch, start from the Branch 3 mixed-text family and measured lesson anchors rather than inventing a new proxy baseline from scratch.

## Artifacts

| Artifact | Path |
|---|---|
| Raw replay log | `results_and_logs/branch6_learnability_probe_2026-04-28.txt` |
| Replay script | `../branch6_learnability_probe.py` |
| Earlier proposal | `results_and_logs/branch6_hybrid_objective_proposal_2026-04-26.md` |