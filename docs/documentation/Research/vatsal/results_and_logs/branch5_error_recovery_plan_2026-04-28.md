# Branch 5 - Error Prevention And Recovery

## Evidence Reviewed

- android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt
- android/shared/src/commonMain/kotlin/ControllerConfusionMetrics.kt
- android/app/src/main/java/com/vatoo/erick/ControllerDiagnosticsActivity.kt
- docs/documentation/User_Guide.md

## Current Recovery Surfaces

| Recovery surface | What ships now | Research implication |
|---|---|---|
| Immediate retry | In Instant and Assisted flows, a user can retry the same chord immediately after a miss without leaving the typing surface. | This is the lowest-friction explicit recovery path and should stay the baseline. |
| Quick backspace | A backspace single swipe deletes one character. | Best for isolated wrong-letter or wrong-utility corrections. |
| Accelerating backspace hold | Hold starts repeat after 300 ms, escalates to character deletion, then word deletion. | Powerful, but overshoot risk is real, especially after controller slips or utility mistakes. |
| Suggestion acceptance | Accepting a suggestion replaces the current prefix or inserts a next-word suggestion. | This can repair lexical near misses, but it is trust-sensitive because it changes visible text beyond a single character. |
| Mode and dial changes | 6-section, Assisted, and controller calibration live in settings or diagnostics, not inline. | These are mitigation paths for repeated failure, not true in-the-moment recovery tools. |

## Error Taxonomy And Recovery Scorecard

| Error class | Where it comes from | Best current recovery | Trust profile | Notes |
|---|---|---|---|---|
| Adjacent directional slip | Near-neighbor direction error | Immediate retry or quick backspace | High trust | Best candidate for explicit coaching because it is common and legible. |
| Mirror slip | Opposite-direction error | Quick backspace plus retry | High trust | More severe than adjacent slips and a stronger sign of orientation or inversion trouble. |
| Premature release | One dial released before a stable chord resolves | Immediate retry | High trust | Current state machine stays explicit and does not guess intent. |
| Wrong utility swipe | Space, period, backspace, enter, or symbols mistake | Quick backspace or symbol toggle retry | High trust | Utility mistakes are easy to see but can break flow. |
| Symbol-layer confusion | Entering or exiting symbols at the wrong time | Toggle retry and explicit visual check | Medium trust | Harder to recover mentally than a single wrong letter. |
| Dead-zone jitter | Controller movement never leaves the dead zone | Calibration adjustment or retry | High trust | Not a text error so much as a controller setup error. |
| Snap-back reversal | Controller release flips through another direction before rest | Retry plus calibration check | High trust | Should stay visible as a controller-specific failure type, not be silently corrected. |
| Prefix near miss | A few wrong letters still point to the intended word | Suggestion acceptance | Medium trust | Useful only when the suggestion is obviously right to the user. |

## Candidate Adaptive-Assistance Memo

### What should stay explicit

- Do not silently reinterpret adjacent slips as intended letters.
- Do not auto-raise dead zone from local confusion aggregates.
- Do not auto-switch a user into Assisted mode or 6-section mode after a run of mistakes.

Reason: the current state machine is explicit, the confusion drill stores only aggregate buckets, and silent correction would blur whether ERICK typed what the user actually entered.

### What can be adaptive without breaking trust

- Suggest a dead-zone review after repeated dead-zone jitter.
- Suggest Y-axis inversion review after repeated mirror-like controller errors.
- Suggest Utility Swipes or Assisted practice after repeated wrong-utility or premature-release failures.
- Suggest trying 6-section when adjacent slips remain high across a short task bundle.

These are coaching and setup recommendations, not hidden corrections.

## Recommendation On Local Confusion Use Versus Static Defaults

Branch 5 lands with a mixed outcome.

- `No-Go` for silent correction and automatic preference mutation.
- `Yes` for explicit, local, session-scoped coaching derived from confusion buckets.
- Keep static defaults for actual text entry until a user explicitly accepts a recommendation.

## Branch 5 Recommendation

The most promising next step is not ambiguity-tolerant typing. It is explicit recovery coaching.

- Preserve immediate retry and quick backspace as the main recovery path.
- Treat suggestion acceptance as a visible opt-in recovery tool, not an automatic fixer.
- Use local confusion only to surface recommendations after a threshold, never to rewrite the user's input silently.

## Ready-To-Split Follow-Up

Recommended follow-up scope:

- recovery scorecard instrumentation for retry count, backspace overshoot, and suggestion-based repair
- post-session coaching cards driven by local confusion thresholds
- controller-specific recovery hints for dead-zone jitter and snap-back reversals
- an explicit `No-Go` note in product follow-up scope for silent correction or auto-retuning