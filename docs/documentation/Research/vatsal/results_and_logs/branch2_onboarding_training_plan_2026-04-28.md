# Branch 2 - Onboarding And Training-Aid Effectiveness

## Evidence Reviewed

- android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt
- android/app/src/main/java/com/vatoo/erick/MainScreenContent.kt
- android/app/src/main/java/com/vatoo/erick/PracticeHubActivity.kt
- android/app/src/main/java/com/vatoo/erick/HelpActivity.kt
- android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt
- docs/documentation/User_Guide.md

## Shipped Baseline Inventory

| Surface | What Ships Now | Research Implication |
|---|---|---|
| First-launch Quickstart | Four steps: dials and preview, utility swipes, input modes, controllers. It auto-opens once and persists only `onboardingStep`, `onboardingCompleted`, and `onboardingDismissed`. | The baseline already teaches the core mental model, but it cannot answer time-to-complete or per-step struggle without extra instrumentation. |
| Practice Lessons hub | Six lessons: 8-section basics, 6-section basics, utility swipes, assisted one-handed, controller drill, quote practice. Each lesson auto-applies its preset. | ERICK already has a real scaffolded curriculum instead of a blank practice field, which makes A/B tutorial testing feasible. |
| Compact versus expanded help | Practice cards stay compact by default and move longer explanation behind the help button. HelpActivity also offers Replay Quickstart and a compact rule summary. | The product already supports concise versus expanded teaching, so this can be compared directly instead of invented from scratch. |
| Controller guidance | Quickstart step 4 introduces controllers, Controller Diagnostics is on the home screen, and the controller drill exists in Practice Lessons. | Controller support is present but late in the first-run path and not yet branched into a dedicated onboarding flow. |
| Progress persistence | Practice tracks only attempted versus completed lesson IDs. | Current storage is too coarse for first-session metrics like abandon point, time-to-first-correct-word, or help usage. |

## Current Friction Findings

1. The first recommended lesson is inconsistent across surfaces. Quickstart step 1 tells the user to start with 6-section basics, while the Practice Hub itself lists 8-section basics first.
2. The shipped curriculum is ordered and preset-driven, but it has no event-level instrumentation. Branch 2 cannot be closed with measurement claims until the app records timings, help taps, and first-error stage.
3. The current teaching path explains row-first and letter-second conceptually, but it does not offer a stronger guided sequential cue or first-error coaching inside the lessons themselves.
4. Prediction is documented and shipped, but it is not part of the Quickstart or lesson flow. That makes it safe to hold out of the initial tutorial until the core chord model is stable.

## Onboarding Aid Comparison Matrix

| Candidate aid | Current baseline | Branch 2 hypothesis | First metrics to watch | Priority |
|---|---|---|---|---|
| Stronger row-then-letter cueing in lesson 1 | Quickstart text explains the model, but the drills still expect full chord timing immediately. | A short sequential cue in the first lesson should reduce time-to-first-correct-word and first-lesson abandonment. | Time to first correct word, first-error stage, lesson 1 completion rate | High |
| Persistent preview highlighting during the first drills | Preview already updates live, but no dedicated training highlight distinguishes row selection from character selection. | Highlighting the active row and active target should reduce early orientation errors without changing typing rules. | Wrong-row versus wrong-letter errors, help taps, first symbol success | Medium |
| Ordered phrase sets instead of general text | Lessons already progress letters to numbers to punctuation in small targets. | Short ordered targets should outperform freeform text during the first session because they isolate one new demand at a time. | Stage completion time, utility success rate, abandon point | High |
| Stronger first-error coaching | Help exists as a separate dialog, not as inline feedback after a failed attempt. | Lightweight inline coaching after the first failed target should reduce repeated error loops. | Repeated failures on same exercise, help taps, retry count | High |
| Controller-specific tutorial variant | Controller guidance exists, but it comes after the general dial explanation and is not the default branch. | Controller-first users likely need calibration plus timing cues before general text drills. | Controller calibration changes, controller lesson completion, confusion drill summary | High |
| Early prediction teaching | Suggestions are visible at rest but not taught in Quickstart. | Teaching prediction too early will likely blur the core chord model and add unnecessary scanning load. | Suggestion accept rate, lesson 1 completion time, subjective confusion | Low |

## Lesson-Order And Phrase-Set Experiment Plan

### Baseline To Preserve

- Keep `Logical` as the initial layout across all novice cohorts.
- Keep preset auto-application inside lessons so the user is never asked to configure the keyboard before the drill starts.
- Keep compact cards as the default and expanded explanation as optional help.

### Comparisons To Run First

1. Current baseline versus sequential-cue lesson 1.
   - Baseline: current Quickstart plus current Practice Hub.
   - Variant: add explicit row-first and letter-second inline cues during the first letters exercise only.
2. Single lesson order versus routed first lesson.
   - Baseline: current shared curriculum ordering.
   - Variant: touch-first users start with 6-section basics, controller-first users start with diagnostics plus controller drill or 8-section basics.
3. Fixed ordered phrase sets versus more general practice text.
   - Baseline: current short ordered targets.
   - Variant: a mixed phrase set that introduces letters, utilities, and symbols earlier.

### Working Hypotheses

- `Logical` should remain the first layout for both 6-section and 8-section novices.
- The first dial mode should not stay universal. The current product already hints at two different starts: 6-section for touch accessibility and 8-section for controller/default flow.
- Prediction should stay outside the mandatory first-session path and move into a later optional assistive lesson once the user can already type a correct word and a correct utility action.

## Recommended First-Session Metrics

| Metric | Why it matters | Current availability |
|---|---|---|
| Time from Quickstart open to first correct word | Core novice friction measure | Not currently captured |
| Time to first correct utility action | Shows whether the utility wheel explanation works | Not currently captured |
| Lesson abandonment point | Identifies where the curriculum loses users | Not currently captured |
| Help-button usage by surface | Measures when compact explanations stop being enough | Not currently captured |
| First-error stage | Distinguishes row-selection failure from letter-selection failure | Not currently captured |
| Exercise retry count | Shows whether inline coaching reduces loops | Not currently captured |
| Controller calibration changes before controller drill | Detects controller-specific setup friction | Partially available through current settings, not yet session-scoped |
| Attempted versus completed lesson count | Coarse retention proxy | Already captured |

## Branch 2 Recommendation

Branch 2 should be treated as complete at the research-definition level.

- Keep `Logical` as the novice layout baseline.
- Do not teach prediction in the first mandatory path.
- Split the next product work into a dedicated onboarding instrumentation and tutorial-routing ticket.
- Start that follow-up with two comparisons only: sequential cueing in lesson 1, and routed first lessons for touch-first versus controller-first cohorts.

## Ready-To-Split Follow-Up

Recommended follow-up scope:

- session instrumentation for Quickstart and lesson-level timings
- first-error classification in Practice Lessons
- help-tap event capture
- routed first-lesson experiment for touch-first versus controller-first entry paths