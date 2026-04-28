# 6-Section Shipped Utility + Mixed Shortform Smoke Run

Date: 2026-04-26

## Purpose

Validate that `erick_v5_6section.py` can now run with:

- the shipped rotated 6-section utility wheel
- a benchmark-driven mixed-text corpus profile derived from the ERICK-150 Branch 8 packs
- non-space utility coverage beyond the legacy `SPACE`-only tokenization path

This is a smoke run, not a comparable full optimizer benchmark.

## Command

```powershell
$env:ERICK6_UTILITY_MODEL='shipped'
$env:ERICK6_CORPUS_PROFILE='mixed_shortform'
$env:ERICK6_STEPS_PER_CHAIN='5'
$env:ERICK6_BASELINE_SAMPLES='5'
d:/vatoo/GitHub/ERICKeyboard/.venv/Scripts/python.exe d:/vatoo/GitHub/ERICKeyboard/docs/documentation/Research/vatsal/erick_v5_6section.py
```

## Smoke Validation Findings

- Utility unigram coverage was no longer `SPACE`-only.
- The script reported non-zero coverage for `TOGGLE_SYMBOLS=0.0184`, `SPACE=0.1043`, and `.=0.0057`.
- The mixed-text profile generated non-zero utility transitions:
  - `28` utility-to-chord bigrams
  - `30` chord-to-utility bigrams
  - trigram coverage in `UCC`, `CCU`, and `CUC` categories
- The printed utility wheel matched the shipped shared-code mapping:
  - `N` -> `TOGGLE_SYMBOLS`
  - `NE` -> `TOGGLE_SHIFT`
  - `SE` -> `SPACE`
  - `S` -> `.`
  - `SW` -> `ENTER`
  - `NW` -> `BACKSPACE`

## Smoke Metrics

| Metric | Value |
|---|---|
| Utility model | `shipped` |
| Corpus profile | `mixed_shortform` |
| Steps per chain | `5` |
| Random baseline samples | `5` |
| Final score | `1.31736` |
| Random baseline | `1.42900 ± 0.10444` |
| Improvement | `7.8%` |
| Predicted WPM | `59.4` |
| Cluster spread | `2.503` |

## Caveats

- This run is only a configuration smoke test. Its score is not comparable to the 500k-step v5 baselines.
- The normal-layer optimizer still places only letters and digits on the 36 chord slots.
- Symbol-heavy text is currently approximated by inserting `TOGGLE_SYMBOLS` utility tokens from the benchmark text; the symbol layer itself is not re-optimized here.
- A full shipped-profile rerun is still needed before Branch 0 can claim an exact shipped-aligned 6-section baseline.