# ERICK Benchmark Pack

This document defines the first reusable benchmark pack for ERICK-150 Branch 8.

The goal is not to replace the existing `wordfreq` baseline. The goal is to make later layout experiments comparable across a small set of recurring ERICK usage domains that are currently underrepresented in the optimizer corpus.

## Scope

- Keep `general-wordfreq-50k` anchored to the existing baseline assets in `scripts/corpus.txt` and `scripts/corpus_data_values.py`.
- Add four shortform benchmark packs derived from shipped ERICK copy and lesson content:
  - `messaging-shortform`
  - `accessibility-supportive`
  - `controller-tv-query`
  - `punctuation-mixed`
- Use the result template in `results_and_logs/experiment_result_template.md` for every new Branch 1-8 report.

## Normalization Rules

1. Keep one phrase or sentence per line in the shortform packs.
2. Normalize text to lowercase unless case itself is the point of the probe.
3. Preserve digits and punctuation characters that materially affect typing cost.
4. Deduplicate mirrored or repeated strings across Android, iOS, and docs sources.
5. Prefer shipped learning, help, and accessibility copy over invented examples when a suitable source already exists.
6. Treat these packs as fixed evaluation seeds. Add new lines intentionally instead of mutating old lines in place without a note.

## Benchmark Table

| Benchmark ID | Primary Source Anchors | Frozen Seed File | Intended Use | Known Caveat |
|---|---|---|---|---|
| `general-wordfreq-50k` | `scripts/corpus.txt`, `scripts/corpus_data_values.py`, `erick_v5_vectorized.py`, `erick_v5_6section.py` | existing baseline assets | continuity with historical v5 runs | still reflects general English more than ERICK-specific usage |
| `messaging-shortform` | `android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt`, `android/app/src/main/java/com/vatoo/erick/MainScreenContent.kt`, `docs/documentation/User_Guide.md` | `benchmark_packs/messaging-shortform.txt` | short chat-like and lesson-style phrases | intentionally small and biased toward guided-practice language |
| `accessibility-supportive` | `android/app/src/main/java/com/vatoo/erick/BenefitAudienceContent.kt`, `docs/documentation/User_Guide.md` | `benchmark_packs/accessibility-supportive.txt` | supportive, explanatory, and accessibility-centered text | mixes nouns, short phrases, and brief sentences |
| `controller-tv-query` | `android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt`, `android/app/src/main/java/com/vatoo/erick/ControllerDiagnosticsActivity.kt`, `android/app/src/main/java/com/vatoo/erick/HelpActivity.kt`, `docs/documentation/User_Guide.md` | `benchmark_packs/controller-tv-query.txt` | controller-first phrases, diagnostics copy, and TV-style navigation text | still lighter on true living-room search titles than a dedicated TV corpus |
| `punctuation-mixed` | `android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt`, `android/app/src/main/java/com/vatoo/erick/HelpActivity.kt`, `docs/documentation/User_Guide.md` | `benchmark_packs/punctuation-mixed.txt` | symbol-heavy and utility-heavy probes | synthetic formatting is used sparingly where the repo lacks enough raw punctuation text |

## Extraction Rules By Pack

### messaging-shortform

- Start from lesson targets, quickstart actions, practice-flow buttons, and short helper copy.
- Favor phrases that are plausible in chat, drill prompts, or lightweight search.
- Keep lines short enough that punctuation and space costs remain visible.

### accessibility-supportive

- Start from the benefit-audience groups and the accessibility sections of the user guide.
- Keep phrases that reflect ERICK's physical-access, cognitive-support, one-handed, and privacy language.
- Exclude marketing-only phrasing that does not imply real typing content.

### controller-tv-query

- Start from controller lessons, diagnostics copy, and controller troubleshooting text.
- Keep connection, calibration, and navigation phrases that a controller-first user is likely to type or see.
- Preserve terms such as `dead zone`, `y-axis`, `controller diagnostics`, and `connected` because they surface symbol and hyphen usage.

### punctuation-mixed

- Start from utility-wheel labels, lesson targets that already include punctuation, and troubleshooting/settings phrases.
- Add only minimal formatting transforms where the repo already implies the pattern, such as `dead zone: 25%`, `type "start".`, or `controller typing?`.
- Keep this pack small and diagnostic; it is meant to expose utility and symbol cost sensitivity, not to stand in for a full coding corpus.

## Update Rule

When any future research branch publishes a new result log, it should state which of these benchmark IDs it used and whether it used the frozen seed files unchanged.