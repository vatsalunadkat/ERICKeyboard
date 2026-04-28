# Branch 8 - Adoption, Habit Formation, And Trust

## Evidence Reviewed

- android/app/src/main/java/com/vatoo/erick/MainScreenContent.kt
- android/app/src/main/java/com/vatoo/erick/PracticeHubActivity.kt
- android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt
- android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt
- android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt
- docs/documentation/User_Guide.md

## Adoption Hypothesis Map

| Adoption stage | ERICK-specific hypothesis | Current proxy status | What is still missing |
|---|---|---|---|
| Setup to first usable keyboard | The first major drop-off happens before typing, while enabling and selecting ERICK. | Live setup state exists on the main screen. | No persisted setup-completion history or timing. |
| Quickstart to first lesson | Quickstart completion or dismissal predicts whether the user will attempt a lesson. | `onboardingCompleted`, `onboardingDismissed`, and `onboardingStep` are persisted. | No replay count, no dwell time, no handoff timestamp. |
| Guided lesson traction | Lesson completion is a stronger early-retention signal than raw typing speed. | Attempted and completed lesson ID sets are persisted. | No order history, no retry count, no abandon point. |
| Voluntary deeper engagement | Users who return for quote practice, replay Quickstart, or try controller drills are more likely to stick. | Quote practice, replay, and controller surfaces exist. | Those launches are not persisted as counters today. |
| Comfort progression | Moving from Logical toward Efficiency, custom layouts, or controller use is a habit signal. | Current layout and controller calibration state are persisted. | No transition history, no change count, no reason for the change. |
| Trust in prediction and correction | Suggestion trust and recovery trust affect retention more than another small layout-speed gain. | Learned prediction profile is persisted locally. | No suggestion impression, tap, or correction-trust proxy is recorded. |

## Trust And Retention Proxy Plan

| Proxy | Available now | Privacy posture | Why it matters |
|---|---|---|---|
| Onboarding completed or dismissed | Yes | Local-only, coarse state | Earliest indicator of whether the first-run explanation works. |
| Practice lessons attempted or completed | Yes | Local-only, coarse state | Stronger than WPM as a first-week engagement proxy. |
| Current layout, input mode, dial mode | Yes | Local-only current state | Indicates the user's current comfort profile. |
| Controller dead zone and Y-axis inversion | Yes | Local-only current state | Useful for controller abandonment or persistence hypotheses. |
| Serialized learned prediction profile present or empty | Yes | Local-only content-free proxy if measured only as non-empty or size band | Rough signal that the predictor is actually being used over time. |
| Quickstart replay count | No | Could stay local-only | Best lightweight proxy for lingering confusion. |
| Quote practice launch count | No | Could stay local-only | Strong voluntary-depth signal once drills feel easy. |
| Suggestion acceptance count | No | Could stay local-only and aggregate-only | Needed to study trust in the predictor without logging typed text. |
| Layout transition count | No | Could stay local-only | Helps distinguish curiosity from real migration. |
| Settings churn count | No | Could stay local-only | High churn can indicate confusion rather than healthy exploration. |

## Prioritized Product-Risk Memo

| Candidate product move | Potential upside | Trust risk | Recommendation |
|---|---|---|---|
| Privacy-preserving local proxy counters | Makes Branch 8 measurable without cloud analytics | Low if counters stay aggregate-only | Highest-priority follow-up |
| More aggressive onboarding or mandatory teaching | Could improve early comprehension | Medium if it feels patronizing or hard to dismiss | Use only after Branch 2 routing instrumentation lands |
| Silent correction or automatic mode switching | Could superficially improve completion metrics | High | Keep the Branch 5 `No-Go` |
| Pushing Efficiency or a new layout too early | Could inflate speed in a narrow cohort | High because it risks migration pain and confidence loss | Defer behind onboarding and trust work |
| Rich behavioral analytics or keystroke logging | Would answer more questions faster | Very high for a privacy-first keyboard | Do not pursue |

## Branch 8 Conclusion

The current evidence points to setup, expectation, and trust as the bigger current barrier than raw typing performance.

- ERICK already stores enough local state to begin a privacy-preserving adoption proxy layer.
- ERICK does not yet store the time-based or count-based signals needed to make credible retention claims.
- The first follow-up should therefore be product-experience instrumentation, not another optimizer branch.

## Ready-To-Split Follow-Up

Recommended follow-up scope:

- local-only counters for quickstart replay, lesson flow, suggestion acceptance, quote practice launch, and settings churn
- a single adoption dashboard in the host app that reads aggregate local counts only
- Branch 2 onboarding routing experiments prioritized ahead of new optimizer work