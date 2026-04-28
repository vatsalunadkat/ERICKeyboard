# Branch 1 Effort Matrix Probe

Date: 2026-04-26

## Current Matrix Inventory

### 8-section shared matrix from `erick_v5_vectorized.py`

| Surface | Values |
|---|---|
| Left effort `L_EFF` | `N=0.95`, `NE=0.98`, `E=1.00`, `SE=1.08`, `S=1.18`, `SW=1.30`, `W=1.15`, `NW=1.03` |
| Right effort `R_EFF` | `N=0.88`, `NE=0.92`, `E=0.95`, `SE=1.02`, `S=1.12`, `SW=1.20`, `W=1.05`, `NW=0.98` |
| Separation `SEP` | distance `0..4` -> `0.5, 0.8, 1.2, 1.7, 2.4` |

### 6-section matrices from `erick_v5_6section.py`

| Profile | Left effort `L_EFF` | Right effort `R_EFF` | Separation `SEP` |
|---|---|---|---|
| `shared_derived` | `0.95, 0.98, 1.08, 1.18, 1.30, 1.03` | `0.88, 0.92, 1.02, 1.12, 1.20, 0.98` | `0.5, 0.8, 1.2, 1.7` |
| `touch_strict` | `0.93, 0.99, 1.12, 1.24, 1.38, 1.04` | `0.86, 0.93, 1.05, 1.17, 1.30, 1.00` | `0.5, 0.85, 1.3, 1.9` |
| `controller_relaxed` | `0.97, 0.99, 1.04, 1.10, 1.16, 1.01` | `0.92, 0.95, 1.00, 1.07, 1.12, 0.97` | `0.5, 0.74, 1.05, 1.45` |

## Easiest-Chord Check

Same-direction chords remain the easiest family under every candidate profile.

| Profile | Six easiest chord positions |
|---|---|
| `shared_derived` | `N+N`, `NE+NE`, `NW+NW`, `SE+SE`, `S+S`, `SW+SW` |
| `touch_strict` | `N+N`, `NE+NE`, `NW+NW`, `SE+SE`, `S+S`, `SW+SW` |
| `controller_relaxed` | `N+N`, `NE+NE`, `NW+NW`, `SE+SE`, `S+S`, `SW+SW` |

That means Branch 1 did not overturn the basic chord-difficulty ordering. The profile changes mainly affect the finer-grained ranking inside that same family.

## 6-section Probe Results

All Branch 1 probes used the shipped 6-section utility wheel, `mixed_shortform` corpus, `toggle_pair` symbol-cost model, default `1.0 / 0.6 / 0.3` weights, `100,000` steps per chain, and `100` random baseline samples.

| Profile | Best score | Random baseline | Improvement | Predicted WPM | Layout delta vs shared-derived full run |
|---|---|---|---|---|---|
| `shared_derived` reference | `0.95350` | `1.39249 ± 0.07509` | `31.5%` | `71.1` | reference |
| `touch_strict` probe | `0.99074` | `1.48927 ± 0.09739` | `33.5%` | `69.6` | `22 / 36` slots changed |
| `controller_relaxed` probe | `0.91351` | `1.26243 ± 0.06256` | `27.6%` | `72.9` | `14 / 36` slots changed |

## Important Caveat

These raw objective scores are not directly comparable across effort profiles because the scoring surface itself changed. The layout deltas and the stable easiest-chord ordering are the more trustworthy signals here.

## Interpretation

- `controller_relaxed` is the milder split candidate. It changed `14 / 36` slots relative to the current shared-derived toggle-pair winner while keeping the same overall compactness signal.
- `touch_strict` is a much stronger perturbation. It changed `22 / 36` slots and pushed the winner farther away from the current shared-derived family.
- Neither probe provides enough product evidence to replace the current shared-derived model. The repo still lacks device-specific observations that can say whether controller users truly benefit from the relaxed profile or whether touch users truly need the stricter penalties.

## Recommendation

Keep the shared-derived effort matrix frozen as the shipping-adjacent default for now.

Use the `touch_strict` and `controller_relaxed` profiles only as Branch 1 calibration probes until local controller diagnostics or practice evidence can justify a real split.

## Artifacts

| Artifact | Path |
|---|---|
| Touch-strict raw output | `results_and_logs/optimization_results_6section_branch1_touch_strict_probe_2026-04-26.txt` |
| Controller-relaxed raw output | `results_and_logs/optimization_results_6section_branch1_controller_relaxed_probe_2026-04-26.txt` |
| Updated optimizer | `../erick_v5_6section.py` |