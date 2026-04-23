# ERICK-148 - Prediction Quality Improvements

| Field | Value |
|---|---|
| **Status** | Done |
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 13 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, prediction, shared, android, ios, personalization |
| **Dependencies** | Extend the shared prediction foundation from ERICK-130 and remain compatible with the language-profile work proposed in ERICK-140 |

---

## Objective

Improve prediction quality by adding a user dictionary, learned word frequency, and smarter suggestion acceptance around punctuation and next-word mode.

---

## Why This Matters

- The current predictor works, but it still behaves like a mostly static demo dictionary rather than a personalized typing system.
- Recent validation work around `acceptSuggestion(...)` and next-word mode exposed that prediction behavior is important enough to deserve more than baseline tests.
- Users will notice suggestion quality quickly, especially on a chorded keyboard where good completions and corrections remove a meaningful amount of effort.

---

## Current Architecture

### Shared Prediction Engine
- `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` holds the trie, correction logic, and bigram next-word prediction.

### Shared State Machine
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` owns the word buffer, suggestion updates, `acceptSuggestion(...)`, and next-word mode transitions.

### Current Gaps
- No persistent user dictionary for names, slang, or learned terms
- No frequency learning from accepted suggestions or repeated commits
- Suggestion acceptance is still relatively naive around punctuation and completed-word boundaries
- Next-word mode is functional, but it does not adapt strongly to what the user actually types over time

---

## Proposed Changes

### 1. Add a User Dictionary Layer

Introduce a persistence-backed user dictionary that can:

- store user-added words
- retain learned words from repeated typing
- keep platform-specific storage separate while leaving ranking logic in shared code

### 2. Learn Frequency From Real Usage

Teach the predictor to update ranking signals when users:

- repeatedly type the same word
- accept a suggestion
- manually correct the engine's preferred choice

### 3. Improve Suggestion Acceptance Semantics

Refine `acceptSuggestion(...)` and related state transitions so they behave correctly when:

- the current word is followed by punctuation
- the user is in next-word mode
- the accepted suggestion should preserve or re-apply trailing punctuation and spacing

### 4. Improve Ranking Quality

Adjust ranking so results consider:

- prefix match quality
- learned frequency
- bigram context
- dictionary tier or base frequency
- user dictionary priority for exact learned matches

### 5. Expand Validation Coverage

Add focused shared tests for:

- user dictionary insertion and retrieval
- frequency learning
- punctuation-aware suggestion acceptance
- next-word prediction transitions after suggestion acceptance

---

## Validation

- Shared host tests cover the new ranking and suggestion-acceptance cases.
- Existing prediction behavior does not regress for untouched baseline words.
- Android and iOS continue consuming the shared suggestion outputs without platform-specific divergence.

---

## Acceptance Criteria

1. Users can add or implicitly learn custom words that persist across sessions.
2. Frequently used words rise in rank over time.
3. Accepting a suggestion behaves correctly when punctuation or spacing is involved.
4. Next-word suggestions improve after the engine observes real user history.
5. Shared automated tests cover the new personalization and punctuation behavior.

---

## Implementation Summary

- Added shared learned-word and learned-bigram persistence in `WordPredictionEngine.kt` with platform storage supplied through `KeyboardActionDelegate`.
- Updated `KeyboardStateMachine.kt` to learn from committed words and accepted suggestions, persist the learned profile, and return punctuation-aware suggestion acceptance instructions.
- Updated Android and iOS suggestion tap handling to consume the shared acceptance result instead of maintaining separate spacing heuristics.
- Added focused shared tests for learned prediction ranking, learned bigrams, and punctuation-aware suggestion acceptance.

## Validation Snapshot

- `cd android && .\gradlew.bat :shared:testAndroidHostTest`
- `cd android && .\gradlew.bat assembleDebug`
- `cd android && .\gradlew.bat assembleSharedKeyboardXCFramework` (Apple slices skipped on Windows as expected)
- iOS extension editor diagnostics clean for `KeyboardViewController.swift` on this Windows machine