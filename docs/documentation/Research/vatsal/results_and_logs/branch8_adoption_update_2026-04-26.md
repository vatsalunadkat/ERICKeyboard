# Branch 8 Adoption Update

Date: 2026-04-26

## What Changed Since The Original Branch 8 Planning Pass

- Branch 3 now has full benchmark-pack adoption in scored 6-section mixed-shortform runs.
- Branches 4 through 7 now each have a checked-in branch note that depends on the benchmark IDs and comparability rules, even where the branch has not yet produced a scored optimizer run.
- The result template needed more required fields in practice: benchmark IDs, symbol-cost assumptions, comparability family, and prediction assumptions.

## Adoption Status

| Branch | Current adoption state |
|---|---|
| Branch 3 | full scored adoption via `mixed_shortform` shipped-wheel runs and explicit symbol-cost comparison |
| Branch 4 | proposal note references benchmark-driven future instrumentation, but no scored run yet |
| Branch 5 | segment recommendation points future evaluation at the benchmark packs |
| Branch 6 | hybrid-objective proposal depends on later benchmark comparisons, not a scored run yet |
| Branch 7 | prediction-aware proposal depends on a post-hoc benchmark pass, not a scored run yet |

## Updated Comparability Rule

Use a named comparability family in every future Branch 1-8 scored report:

| Family | Directly comparable to |
|---|---|
| `legacy-wordfreq` | historical 6-section legacy runs only |
| `shipped-mixed-shortform` | shipped-wheel mixed-shortform runs using the frozen Branch 8 packs |
| `prediction-posthoc` | prediction-aware post-hoc benchmark passes only |
| `proposal-only` | branch notes and design proposals, not scored runs |

This keeps later branches from presenting exploratory notes and scored optimizer outputs as if they were the same kind of evidence.