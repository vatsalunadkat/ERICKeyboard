# Branch 7 - Mixed-Task Workflow Research

## Evidence Reviewed

- android/shared/src/commonMain/kotlin/KeyboardLogic.kt
- android/shared/src/commonMain/kotlin/KeyboardContracts.kt
- android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt
- docs/documentation/User_Guide.md
- docs/documentation/Research/vatsal/benchmark_pack.md
- docs/documentation/Research/vatsal/results_and_logs/branch7_prediction_aware_benchmark_2026-04-28.md

## Utility And Symbols Audit Memo

| Workflow surface | 8-section shipped path | 6-section shipped path | Mixed-task implication |
|---|---|---|---|
| Space / enter / backspace | Direct single swipes | Direct single swipes | Core text editing remains explicit and comparable across dial modes. |
| Punctuation on the main utility wheel | `,` and `.` live on the right-dial utility wheel | `.` lives on the utility wheel, wider symbol inventory requires Symbols mode | 6-section mixed-task cost is more sensitive to mode switching than 8-section. |
| Symbols access | No dedicated symbols mode; shifted and chord maps carry symbols | Dedicated Symbols mode on `N` single swipe | 6-section likely pays extra workflow cost on symbol-heavy tasks even when prose cost is good. |
| Home / end navigation | `N` single swipe gives home or end depending on shift state | No dedicated home or end single swipe | Editing and navigation asymmetry is real and should be measured directly. |
| Caps behavior | Dedicated caps toggle exists | No dedicated caps toggle single swipe in the shipped 6-section wheel | Uppercase-heavy mixed tasks are not equivalent across dial modes. |
| Prediction support | Suggestions help letter prefixes only | Suggestions help letter prefixes only | Prediction is not a strong assist for number, symbol, or navigation-heavy workflows. |
| Extended action surface | Contracts include `DPAD`, `PAGE`, `TAB`, and delete-forward actions | Same enum surface exists | The shipped default mappings do not currently expose most of these actions, so shortcut research is premature. |

## Mixed-Task Benchmark Pack

This pack is intentionally scenario-based rather than plain prose-only. It should sit beside the existing shortform packs, not replace them.

| Task ID | Task family | Frozen scenario | Source style |
|---|---|---|---|
| `M1` | punctuation-heavy reply | `go? no.` | lesson-target derived |
| `M2` | diagnostics-style numeric punctuation | `dead zone: 25%` | shipped diagnostics and guide copy |
| `M3` | quoted command string | `type "start".` | shipped main-screen helper copy with minimal formatting carryover |
| `M4` | controller-first query | `controller diagnostics` | shipped controller labels |
| `M5` | connection state phrase | `connected: controller` | shipped diagnostics wording |
| `M6` | email-like string | `test@example.com` | minimal synthetic string for a missing real corpus type |
| `M7` | version-like string | `v1.2.0` | repo-derived release formatting |
| `M8` | edit-and-repair scenario | type `go on`, move home, insert `ok `, move end, finish with `.` | action scenario built from shipped home/end and punctuation surfaces |
| `M9` | symbol round-trip | enter 6-section Symbols mode, type `[]`, return to letters, type `go.` | shipped 6-section symbols workflow |
| `M10` | backspace escalation | type `go`, delete once, retype `go.`; repeat with a held delete | shipped backspace behavior |

## Workflow Finding Summary

1. Mixed-task pain is unlikely to be a pure layout problem. The bigger suspects are symbol-mode switching, navigation asymmetry, and repeated utility use.
2. The shared action contracts advertise more actions than the shipped default layouts expose. That means future shortcut or macro work should not leap ahead of a shipped-surface audit.
3. The current benchmark family already covers shortform messaging, controller queries, accessibility-supportive text, and punctuation-heavy strings, but it still under-measures edit-and-repair workflows.
4. Prediction is helpful for prose prefixes and much less relevant for symbols, numbers, navigation, and correction-heavy scenarios.

## Recommendation On Shortcut Research Versus Staying Text-Only

Branch 7 should not jump straight to macros or shortcut features.

- First split the mixed-task execution work around the benchmark pack above.
- Measure utility placement, symbol-mode switching, and navigation asymmetry before inventing new action systems.
- Treat shortcut and macro research as a later branch only if mixed-task evidence shows the current shipped action surface is the main bottleneck.

## Ready-To-Split Follow-Up

Recommended follow-up scope:

- execute the mixed-task benchmark pack as a scored research pass
- add explicit edit-and-repair logging for navigation and backspace-heavy tasks
- compare 6-section symbols-mode cost against 8-section punctuation and navigation cost
- keep shortcut and macro work in the future bucket until the shipped mixed-task bottlenecks are measured