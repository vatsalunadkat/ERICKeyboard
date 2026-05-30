# ERICK-162 - User Dictionary Management UI

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-77 Kotlin Shared Core |
| **Labels** | feature, prediction, shared, android, ios, settings, user-dictionary |
| **Dependencies** | Builds on ERICK-148 learned prediction persistence and ERICK-154 predictor trust and domain assistance |

---

## Objective

Add a settings screen on both Android and iOS where users can view, add, edit, and delete their learned words and bigrams. Include the ability to export and import the user dictionary as a file for backup or device migration.

---

## Why This Matters

- The predictor currently learns words silently. Users have no way to see what has been learned, correct mistakes, or remove accidentally learned nonsense.
- Users who switch devices or reset their phone lose all learned words. Export/import solves this without cloud sync.
- Providing visibility into learned data builds trust and aligns with ERICK's privacy-first positioning — users own their data and can see exactly what it contains.

---

## Current Surfaces To Build On

### Shared
- `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` — `exportLearnedProfile()`, `importLearnedProfile()`, `learnWord()`, learned word storage
- `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` — `KeyboardActionDelegate` with `loadPredictionProfile()` / `savePredictionProfile()`

### Android
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt`
- `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt`

### iOS
- `ios/ERICK/ERICK/SettingsView.swift`
- `ios/ERICK/ErickKeyBoard/SettingsView.swift`

---

## Proposed Scope

### 1. Add Learned Words List Screen

A new screen accessible from Settings showing all user-learned words for the active language:

- Scrollable alphabetized list of learned words with their frequency/count
- Search/filter bar to find specific words quickly
- Swipe-to-delete (or long-press → delete) for individual words
- "Clear All" action with confirmation dialog
- Per-language tab or filter (since learned profiles are per-language)

### 2. Add Manual Word Entry

- An "Add Word" button that lets users manually add words to their dictionary
- Optional: assign a priority/frequency weight so the word appears more readily

### 3. Add Bigram Viewer (Optional)

- A secondary section showing learned word pairs (bigrams)
- Users can delete specific bigrams they don't want

### 4. Add Export/Import Functionality

- **Export**: Serialize the current language's learned profile to a shareable JSON file. Use the platform share sheet (Android Intent, iOS UIActivityViewController).
- **Import**: Accept a JSON file via file picker and merge into the current learned profile. Handle conflicts gracefully (keep higher frequency).
- File format should be human-readable and documented so power users can hand-edit it.

### 5. Platform Parity

- Android: Jetpack Compose screen in the settings flow with Material 3 components
- iOS: SwiftUI view accessible from host app settings and optionally from the keyboard extension settings

---

## Acceptance Criteria

- [ ] Users can view all learned words for the active language in a scrollable list
- [ ] Users can delete individual learned words
- [ ] Users can manually add new words to the dictionary
- [ ] Users can export their learned dictionary as a JSON file
- [ ] Users can import a dictionary file and merge it into their existing profile
- [ ] Clear All action requires confirmation before executing
- [ ] Feature works identically on Android and iOS
- [ ] Learned profiles remain per-language isolated (editing English doesn't affect Spanish)
- [ ] No network access required — fully offline

---

## Out of Scope

- Cloud sync or automatic backup
- Shared dictionary marketplace
- Automatic import from other keyboard apps
- Bigram editing in the first pass (nice-to-have follow-up)
