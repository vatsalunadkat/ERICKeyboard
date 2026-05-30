# ERICK-164 - Word Blocklist

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Low |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-77 Kotlin Shared Core |
| **Labels** | feature, prediction, shared, android, ios, privacy, settings |
| **Dependencies** | Builds on ERICK-148 learned prediction persistence. Benefits from ERICK-162 User Dictionary Management UI for shared settings patterns |

---

## Objective

Allow users to permanently block specific words from appearing in suggestions and autocorrect results. Blocked words should never surface in completions, corrections, or next-word predictions regardless of how often they appear in the base dictionary or learned profile.

---

## Why This Matters

- Users may accidentally learn embarrassing, offensive, or incorrect words that keep resurfacing in suggestions despite being unwanted.
- Some users want to prevent profanity, slurs, or other sensitive words from ever appearing as suggestions (important for shared devices or accessibility contexts where suggestions are spoken aloud).
- Deleting a learned word (ERICK-162) doesn't prevent the base dictionary from still suggesting it. A blocklist permanently suppresses the word from all suggestion sources.
- Aligns with ERICK's user-control-first philosophy — users should own what their keyboard suggests.

---

## Current Surfaces To Build On

### Shared
- `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` — `getSuggestions()`, `getNextWordSuggestions()`, `getDefaultSuggestions()`
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` — suggestion orchestration

### Android
- `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt` — DataStore persistence
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt`

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift`
- `ios/ERICK/ERICK/SettingsView.swift`

---

## Proposed Scope

### 1. Add Blocklist Storage to Shared Module

- Add a `Set<String>` blocklist field to `WordPredictionEngine` (case-insensitive)
- Filter all suggestion outputs against the blocklist before returning results
- Persist the blocklist through the same platform storage mechanism as learned profiles
- Blocklist is global (applies across all languages) unless per-language isolation proves necessary

### 2. Add Blocklist Filtering to All Suggestion Paths

Ensure blocked words are excluded from:
- `getSuggestions()` — word completions and autocorrect
- `getNextWordSuggestions()` — bigram predictions
- `getDefaultSuggestions()` — sentence starters
- Any future suggestion surface

### 3. Add "Block This Word" Quick Action

- When a user long-presses (or swipes away) a suggestion in the suggestion bar, offer a "Block" action
- The word is immediately added to the blocklist and removed from the current suggestion set
- Provide brief visual confirmation ("Blocked")

### 4. Add Blocklist Management in Settings

- A screen showing all blocked words in a scrollable list
- Ability to unblock (remove from blocklist) individual words
- Ability to manually add words to the blocklist via a text field
- "Clear All Blocked Words" with confirmation

### 5. Platform Parity

Both Android and iOS should support the same blocklist behavior and management UI.

---

## Acceptance Criteria

- [ ] Blocked words never appear in any suggestion type (completion, correction, next-word, default)
- [ ] Users can block a word directly from the suggestion bar via long-press or swipe
- [ ] Users can view and manage their blocklist in settings
- [ ] Users can manually add words to the blocklist
- [ ] Users can unblock previously blocked words
- [ ] Blocklist persists across keyboard restarts and device reboots
- [ ] Blocking is case-insensitive ("Hello" blocks "hello", "HELLO", etc.)
- [ ] Works identically on Android and iOS
- [ ] Blocked words are still typeable via chords — only suggestions are suppressed

---

## Out of Scope

- Pre-built profanity filter or NSFW word list (users manage their own blocklist)
- Blocking partial word patterns or regex
- Blocking words from being committed (only suggestion suppression)
- Syncing blocklist across devices
