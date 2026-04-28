# ERICK v5 6-Section Baseline Reproduction

Date: 2026-04-26

## Purpose

Reproduce the checked-in 6-section optimizer baseline from `docs/documentation/Research/vatsal/erick_v5_6section.py` so ERICK-150 Branch 0 can distinguish between:

- the current reproducible research script
- the shipped 6-section placeholder map in shared keyboard logic
- the exact-shipped 6-section product behavior, which now includes a rotated utility wheel plus Symbols toggle

## Reproduction Setup

- Python environment: repo virtual environment at `d:/vatoo/GitHub/ERICKeyboard/.venv/Scripts/python.exe`
- Added dependency: `wordfreq`
- Command run:

```powershell
& "d:/vatoo/GitHub/ERICKeyboard/.venv/Scripts/python.exe" "d:/vatoo/GitHub/ERICKeyboard/docs/documentation/Research/vatsal/erick_v5_6section.py"
```

## Script Assumptions Reproduced

- 6 directions: `N`, `NE`, `SE`, `S`, `SW`, `NW`
- 36 chord positions
- 36 symbols: 26 letters + 10 digits
- Utility set modeled by the script: `SHIFT`, `.`, `SPACE`, `ENTER`, `BACKSPACE`
- Corpus: `wordfreq` top 50k English words
- Search: 8 Parallel Tempering chains, 500,000 steps per chain
- Weights: unigram `1.0`, bigram `0.6`, trigram `0.3`

This is not the current shipped 6-section product objective because the script does not model the shipped `TOGGLE_SYMBOLS` action and does not mirror the full rotated utility wheel in `KeyboardLogic.kt`.

## Results Summary

| Metric | Value |
|---|---|
| Final score | `0.94132` |
| Random baseline | `1.34279 ± 0.06476` |
| Improvement | `29.9%` (`6.2σ`) |
| Predicted WPM | `72.4` |
| Cluster spread | `1.816` |
| Cost breakdown | Unigram `0.56940`, Bigram `0.18886`, Trigram `0.11305` |

## Reproduced Layout

| Left \ Right | N | NE | SE | S | SW | NW |
|---|---|---|---|---|---|---|
| N | `i` | `n` | `g` | `7` | `q` | `u` |
| NE | `c` | `t` | `s` | `k` | `9` | `z` |
| SE | `v` | `h` | `e` | `r` | `4` | `8` |
| S | `6` | `x` | `d` | `a` | `p` | `3` |
| SW | `5` | `0` | `1` | `y` | `l` | `b` |
| NW | `f` | `w` | `2` | `j` | `m` | `o` |

## Generated Kotlin Map

```kotlin
private val efficiencyNormalMap6 = mapOf(
    Direction.N to listOf("i", "n", "g", "7", "q", "u"),
    Direction.NE to listOf("c", "t", "s", "k", "9", "z"),
    Direction.SE to listOf("v", "h", "e", "r", "4", "8"),
    Direction.S to listOf("6", "x", "d", "a", "p", "3"),
    Direction.SW to listOf("5", "0", "1", "y", "l", "b"),
    Direction.NW to listOf("f", "w", "2", "j", "m", "o"),
)

private val efficiencyShiftedMap6 = mapOf(
    Direction.N to listOf("I", "N", "G", "&", "Q", "U"),
    Direction.NE to listOf("C", "T", "S", "K", "(", "Z"),
    Direction.SE to listOf("V", "H", "E", "R", "$", "*"),
    Direction.S to listOf("^", "X", "D", "A", "P", "#"),
    Direction.SW to listOf("%", ")", "!", "Y", "L", "B"),
    Direction.NW to listOf("F", "W", "@", "J", "M", "O"),
)
```

## Drift Against The Shipped Placeholder Map

The current shared placeholder in `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` does not match this reproduced optimizer output.

- Exact slot matches: `4 / 36`
- Matching positions: `N[2]=g`, `N[3]=7`, `NE[1]=t`, `SE[1]=h`
- All other 32 slots differ

## Branch 0 Conclusion

The checked-in 6-section optimizer script is reproducible, but it currently reproduces a legacy 5-action research objective, not the exact shipped 6-section product behavior. Any future 6-section tuning work in ERICK-150 should either:

1. align the optimizer to the shipped rotated utility wheel plus Symbols toggle before cross-mode comparisons, or
2. explicitly label current 6-section optimizer results as legacy-baseline research only
