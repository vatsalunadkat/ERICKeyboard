# Branch 5 - Layout Variant Decision Update

Date: 2026-04-28

## Decision

Branch 5 is a `No-Go` for official segment-specific shipped `Efficiency` variants inside ERICK-150.

The evidence now supports keeping one shipped `Efficiency` family and using segment packs only as evaluation slices until a future rerun produces a clearly better segment-specific winner with an acceptable product-story cost.

## Why The Decision Is Stronger Now

| Evidence source | What it says about variants |
|---|---|
| Product surfaces: settings, help, user guide, and current layout model | ERICK already teaches one simple built-in choice: `Logical` for easier learning and `Efficiency` for later speed. Segment-specific official variants would add settings, teaching, and maintenance complexity immediately. |
| Branch 1 effort-profile probe | Touch-strict and controller-relaxed effort matrices changed rankings locally but did not produce a strong cross-mode reason to split the shipped layout family. |
| Branch 4 local confusion spike | The repo now has a privacy-safe local diagnostics surface for confusion buckets, but not yet a calibrated segment-specific confusion matrix that would justify shipping different layouts per audience. |
| Branch 7 prediction-aware benchmark | Prediction reduced cost by about `6%`, but it did not change the ranking of candidate layouts in either dial mode. That weakens the case for a separate prediction-heavy audience variant. |
| Branch 6 learnability probe | The measured tension is between the existing `Logical` and `Efficiency` stories, not between multiple near-tie `Efficiency` families. No current hybrid or segment-specific candidate is close enough to justify another built-in branch. |

## What This Means For ERICK-150

- Treat segment benchmarking as an evaluation lens, not as a shipping roadmap by default.
- Keep the current product story centered on two built-in layouts, with dial mode, assisted mode, controller support, and practice flows already covering the main audience differences.
- Do not propose an official `controller-first`, `assisted`, `novice`, or other segment-specific `Efficiency` variant unless a future rerun beats the global winner by at least `3%` on that segment's pack while losing no more than `1%` on the general pack.

## Recommendation

- Mark Branch 5 as completed inside ERICK-150 with a `No-Go` outcome for official segment-specific variants.
- Keep the existing split rule from the earlier Branch 5 memo:
  1. segment-specific gain of at least `3%`
  2. no more than `1%` loss on the general pack
  3. survival under Branch 3 utility assumptions and later Branch 4-style confusion-aware reruns
  4. a clear UI and lesson story that does not make the default path harder to understand
- Re-open this branch only when measured reruns, not product intuition alone, clear those gates.

## Artifacts

| Artifact | Path |
|---|---|
| Earlier recommendation memo | `results_and_logs/branch5_layout_variant_recommendation_2026-04-26.md` |
| Branch 6 measured proxy pass | `results_and_logs/branch6_hybrid_proxy_measurement_2026-04-28.md` |
| Branch 7 benchmark pass | `results_and_logs/branch7_prediction_aware_benchmark_2026-04-28.md` |
| Branch 4 local diagnostics spike | `results_and_logs/branch4_local_confusion_spike_2026-04-28.md` |