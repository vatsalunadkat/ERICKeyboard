# Branch 4 - Controller And Alternative Hardware Research

## Evidence Reviewed

- android/app/src/main/java/com/vatoo/erick/ControllerDiagnosticsActivity.kt
- android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt
- android/app/src/main/java/com/vatoo/erick/HelpActivity.kt
- android/shared/src/commonMain/kotlin/ControllerConfusionMetrics.kt
- android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt
- docs/documentation/User_Guide.md
- docs/documentation/Research/vatsal/results_and_logs/branch4_local_confusion_spike_2026-04-28.md

## Shipped Controller Baseline

| Surface | What ships now | Research implication |
|---|---|---|
| Shared normalization path | Controller Diagnostics and the keyboard both use `ControllerInputProcessor` and the shared `KeyboardStateMachine`. | Controller research can compare diagnostics results to real typing behavior without a separate interpretation layer. |
| Calibration controls | Dead zone and Y-axis inversion are persisted preferences and visible in diagnostics. | Controller performance already has configuration overhead that touch typing does not. |
| Local confusion drill | The diagnostics screen records exact match, adjacent slip, mirror slip, dead-zone jitter, other mismatch, and passive snap-back reversal counts. | ERICK already has a privacy-safe local controller signal surface worth promoting into benchmark work. |
| Controller lesson path | Quickstart step 4 introduces controllers, and the controller practice lesson assumes 8-section Logical Quick Type after calibration. | The current product already encodes a controller baseline, but it is late in the overall learning flow. |
| Help guidance | HelpActivity tells users to start controller drills only after touch feels comfortable. | That is a product assumption, not a measured controller-first conclusion. |

## Controller-Specific Benchmark Pack Or Task Bundle

| Task family | Example task bundle | Why it belongs in controller research | Primary metrics |
|---|---|---|---|
| Calibration warm-up | Run the local confusion drill for one full direction cycle per stick, then adjust dead zone or Y-axis only if needed. | Controller-only setup cost is a first-class part of the experience. | Exact-match rate, adjacent-slip rate, dead-zone jitter, snap-back reversals, calibration changes |
| Short messaging | `go`, `on`, `go on`, `ok.` | Matches the current lesson scale while exercising dual-stick timing. | Time to complete, corrections, confusion drill deltas before and after |
| Query entry | `wifi 6`, `elden ring?`, `usb c hub` | Represents TV and couch-search use where controllers are a real alternative input path. | Completion time, utility errors, punctuation success |
| Punctuation-heavy entry | `go?`, `ok.`, `a/b?` | Controllers need explicit validation on utility and symbol transitions, not only letters. | Wrong utility swipe rate, symbol toggle count, recovery time |
| Recovery drill | Intentionally mistype one character, then recover with retry or backspace. | Tests whether controller mistakes cost more than touch mistakes once drift and snap-back enter the picture. | Recovery time, backspace overshoot, retry count |
| Extended two-stick run | Three to five short phrases in one sitting with no recalibration. | Captures drift, fatigue, and confidence over a longer controller session. | Mid-session recalibration, adjacent-slip drift, completion drop-off |

## Diagnostics Expansion Plan

1. Promote ratios and thresholds, not only raw counts.
   - Show exact-match percentage, adjacent-slip percentage, and dead-zone jitter percentage for the current session.
2. Tag diagnostics results with the active typing context.
   - Include dial mode, input mode, left-handed mode, and haptic-on versus haptic-off in the local session summary.
3. Add a short warm-up flow before freeform recording.
   - The first few samples should separate basic calibration failure from later timing failure.
4. Keep confusion evidence local and aggregate-only.
   - Do not add raw traces or typed text storage.
5. Use diagnostics to recommend explicit actions, not silent preference changes.
   - Example: suggest trying a higher dead zone after repeated dead-zone jitter instead of mutating preferences automatically.

## Controller-Versus-Touch Comparison Memo

### What stays shared

- The same `KeyboardStateMachine` owns the typing path.
- The same dial geometry, input modes, utility behavior, and prediction behavior still apply.

### What materially diverges for controller use

- Controller typing adds calibration cost before text entry starts.
- Controller-only failure modes exist: dead-zone jitter and snap-back reversal.
- Dual-stick timing is physically different from touch, even though the state machine is shared.
- Hardware rumble is variable by device, so haptic value must be treated as a comparison factor rather than a guaranteed baseline.

### Branch 4 conclusion

Controller behavior diverges enough from touch behavior to justify its own product follow-up.

- Keep `8-section Logical Quick Type` as the current controller comparison baseline because that is what the shipped lesson path teaches today.
- Treat `6-section` as a fallback comparison only when controller adjacent slips stay high after calibration.
- Stop assuming controller onboarding must come after touch onboarding. That should be measured directly, not preserved as doctrine.

## Ready-To-Split Follow-Up

Recommended follow-up scope:

- controller benchmark screen or repeatable local task runner
- richer local diagnostics summaries with percentages and threshold-based recommendations
- controller-first onboarding path experiments
- touch-versus-controller comparison study using the task bundle above