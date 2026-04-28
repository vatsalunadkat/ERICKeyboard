# ERICK-152 - Practice Hub Lesson Path Refresh

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, onboarding, practice, android, ios, learning, research-followup |
| **Dependencies** | Build on ERICK-147 learning surfaces and the ERICK-151 Branch 2 and Branch 8 findings about first-session friction, routed-first lessons, and expectation setting |

---

## Objective

Improve the current lesson plans so a new ERICK user gets to a successful first session faster, with a clearer recommended order, shorter drills, and fewer contradictions between quickstart, help, and the practice hub.

---

## Why This Ticket Exists

- The current learning content is usable, but it is still shaped more like a feature checklist than a progressive lesson path.
- `quickstartSteps` currently tells people to start with 6-section basics, while the practice hub still exposes multiple lesson paths without a strong recommended route.
- ERICK-151 Branches 2 and 8 both pointed to first-session routing and expectation setting as a stronger near-term product win than more optimizer work.

---

## Current Surfaces To Build On

### Android
- `android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt`
- `android/app/src/main/java/com/vatoo/erick/PracticeHubActivity.kt`
- `android/app/src/main/java/com/vatoo/erick/MainScreenContent.kt`
- `android/app/src/main/java/com/vatoo/erick/HelpActivity.kt`

### iOS
- `ios/ERICK/ERICK/LearningHubViews.swift`
- `ios/ERICK/ERICK/ContentView.swift`

---

## Proposed Scope

### 1. Introduce A Clear Recommended Lesson Route

The practice hub should stop behaving like a flat menu of equal options.

Add a clear recommended sequence such as:

1. quickstart review
2. first letters
3. utility swipes
4. numbers and punctuation
5. mode-specific follow-up (`6-section`, `8-section`, `assisted`, or `controller`)
6. freeform quote practice

The route should match the current quickstart wording instead of competing with it.

### 2. Split Broad Drills Into Shorter Progressive Lessons

The current lessons often combine several concepts at once. Replace them with shorter drills that each have one main teaching goal:

- first letters
- first utilities
- first numbers
- first symbols
- assisted one-handed basics
- controller timing basics

This should reduce the amount of context switching inside each lesson and make failures easier to interpret.

### 3. Make Setup Assumptions Explicit Before Each Lesson

Every lesson should clearly show:

- which dial mode it will apply
- which layout it will apply
- which input mode it will apply
- why that setup was chosen

The user should not have to infer why a lesson suddenly feels different from their current keyboard state.

### 4. Keep Quickstart, Help, And Practice Copy Aligned

If the recommended first route changes, update the nearby onboarding copy in:

- quickstart cards
- help screen call-to-actions
- practice hub section headings
- any mirrored iOS learning copy

### 5. Preserve An Advanced Practice Path

`Quote Practice` should stay available as the open-ended advanced mode, but it should no longer do the job of a missing intermediate lesson plan.

---

## Out Of Scope

- Remote analytics or account-based progress tracking
- New dial geometry research
- Replacing the existing quickstart with a full interactive overlay again

If local progress counters are helpful, they can be added only if they remain lightweight and do not expand this ticket into an instrumentation project.

---

## Acceptance Criteria

- [ ] The practice hub exposes a clear recommended lesson order instead of a flat undifferentiated list
- [ ] The recommended route matches the guidance shown in quickstart and help instead of contradicting it
- [ ] Current broad drills are split into shorter progressive lessons with one main objective each
- [ ] Each lesson clearly states the dial mode, layout, and input mode it applies
- [ ] Assisted and controller users still have dedicated follow-up paths instead of being forced through a touch-only route
- [ ] `Quote Practice` remains available as the advanced freeform mode
- [ ] Android learning surfaces are updated first and any iOS mirrored learning copy stays aligned
