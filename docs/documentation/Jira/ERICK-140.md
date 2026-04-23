# ERICK-140 - Multi-Language Support (Spanish Foundation)

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 13 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, i18n, shared, android, ios, prediction, planning |
| **Dependencies** | Must build on shipped dual-mode support from ERICK-139 and the learned prediction persistence added in ERICK-148 |

---

## Objective

Add multi-language support to ERICK, starting with Spanish, without duplicating behavior across Android and iOS. The language layer must work in both 8-section and 6-section modes, preserve current utility-wheel invariants, and keep learned prediction history isolated per language.

---

## Why This Ticket Needed A Refresh

- The original draft assumed an English-only static predictor. That is no longer true after ERICK-148 shipped learned words, learned bigrams, punctuation-aware suggestion acceptance, and persisted prediction profiles.
- ERICK-139 already shipped the optional 6-section mode, dedicated Symbols layer, and the corrected rotated utility mapping. Multi-language support now has to work across both dial modes.
- ERICK-147 added quickstart and practice lessons on both platforms. If language support lands, these learning surfaces also need language-aware copy and drills.
- The shared module is now clearly the authoritative behavior surface, so the ticket should route changes through shared profiles rather than platform-specific forks.

---

## Current Architecture

### Shared Keyboard Behavior
- `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` is the authoritative layout surface for logical, efficiency, custom, 8-section, 6-section, and 6-section symbols behavior.
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` owns word buffers, suggestion updates, next-word mode, and punctuation-aware suggestion acceptance.
- `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` now combines a base dictionary with learned words, learned bigrams, autocorrect, and import/export of the learned profile.
- `KeyboardActionDelegate.loadPredictionProfile()` / `savePredictionProfile()` already persist learned prediction state through the platform layer.

### Platform Settings And Storage
- Android uses `PreferencesManager.kt` with DataStore for layout, input mode, six-section mode, controller tuning, onboarding, and practice progress.
- iOS host app and extension share state through App Group `UserDefaults`.
- Any language preference must be readable by both the host app and the active keyboard implementation on each platform.

### Product Invariants To Preserve
- 8-section and 6-section modes must continue to coexist.
- The shipped 6-section utility wheel stays fixed: `N = Symbols`, `NE = Shift`, `SE = Space`, `S = Period`, `SW = Enter`, `NW = Backspace`.
- Existing English behavior must not regress.
- Learned prediction history must not be lost when the user switches languages.
- Custom layouts remain disabled in 6-section mode today and should not be re-enabled accidentally while refactoring.

---

## Proposed Scope

### 1. Introduce Shared Language Profiles

Create a shared language-profile layer that owns everything that varies by language:

- display name and language code
- base alphabet and special characters
- 8-section logical maps
- 6-section logical maps
- efficiency maps for both dial modes when available
- base dictionary tiers
- base bigram data
- localized default suggestions
- any language-specific accent or modifier metadata

This should live in shared code and be consumed by both Android and iOS rather than copied into platform UI files.

### 2. Make Prediction Profiles Language-Aware

Extend the current learned-profile architecture so English and Spanish do not overwrite each other.

Required behavior:

- base dictionary and base bigrams come from the active language profile
- learned words and learned bigrams stay isolated per language
- switching from English to Spanish preserves English learning for when the user comes back
- default suggestions change with the active language

Implementation note:

- the current `loadPredictionProfile()` / `savePredictionProfile()` contract likely needs either a language key or a versioned serialized bundle that can hold multiple per-language learned profiles

### 3. Support Spanish In Both Dial Modes

The original ticket treated 6-section as optional future work. That is now outdated. Spanish support must define and validate:

- 8-section Spanish logical layout
- 6-section Spanish logical layout
- Spanish efficiency layout for 8-section
- Spanish efficiency layout for 6-section, or a clearly documented fallback if phase 1 ships logical-only Spanish first

Design constraints:

- `ñ` must be enterable without awkward workarounds
- accented vowels must be available without regressing the shipped utility-wheel mappings
- numbers, punctuation, and symbols must remain reachable in both dial modes

### 4. Finalize The Accent Entry Strategy

The old draft assumed an accent modifier could simply be added to the utility wheel. That is no longer safe because the 6-section utility wheel is already shipped and documented.

This ticket should lock one supported strategy that works across both dial modes, such as:

- a shared accent state entered from the symbols layer
- long-press or hold behavior on a vowel preview
- a language-aware accent overlay that does not displace existing utility actions

The final choice must be validated against the current 6-section utility mapping and should minimize extra mode confusion.

### 5. Add A Language Setting On Both Platforms

Add a language selector in host-app settings and make the active keyboard reload language-specific resources from shared state.

Android:

- `PreferencesManager.kt`
- `SettingsScreen.kt` / `MainSettingsContent.kt`
- `MyInputMethodService.kt`

iOS:

- `SettingsView.swift` in the host app
- extension-side settings/state readers in `KeyboardViewController.swift`

### 6. Update Preview, Help, And Learning Surfaces

Language support should not stop at raw typing behavior. The following surfaces should use the active language profile or localized content where applicable:

- left-dial previews and label rendering
- quickstart copy where language-specific examples appear
- practice-lesson sample targets
- help screens and user guide documentation

### 7. Validation Strategy

Add shared tests for:

- language-profile selection
- English and Spanish chord routing in both 8-section and 6-section modes
- per-language learned-profile preservation
- language switching mid-session
- accent entry behavior
- Spanish dictionary, corrections, and bigram suggestions

Platform validation should include Android build coverage and iOS editor diagnostics / XCFramework refresh as available.

---

## Acceptance Criteria

- [ ] A language selector is available in settings on both Android and iOS
- [ ] English remains the default language
- [ ] Spanish is available as the first non-English profile
- [ ] The active keyboard reads the selected language from shared platform storage on both Android and iOS
- [ ] Spanish logical typing works in both 8-section and 6-section modes
- [ ] Spanish prediction uses Spanish base dictionary data and Spanish default suggestions
- [ ] Learned prediction history is isolated per language and survives language switching
- [ ] Spanish accented characters and `ñ` are enterable without regressing the shipped 6-section utility wheel mapping
- [ ] Switching languages reloads suggestions and layout data without restarting the app
- [ ] Existing English behavior does not regress
- [ ] Quickstart, practice, help, and user-guide content are updated anywhere language-specific examples are exposed
- [ ] Adding a future language only requires a new shared profile and its resources, not a platform rewrite

---

## Files Expected To Change

| File | Purpose |
|---|---|
| `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` | Language-aware map lookup for both 8-section and 6-section layouts |
| `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` | Active language routing and per-language prediction profile handling |
| `android/shared/src/commonMain/kotlin/WordPredictionEngine.kt` | Base-language resources plus learned-profile isolation |
| `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` | Any delegate/profile contract updates needed for language-aware persistence |
| `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt` | Persist selected language on Android |
| `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` | Read/apply language preference on Android |
| `ios/ERICK/ERICK/SettingsView.swift` | Host-app language selector |
| `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` | Extension-side language preference consumption |
| `docs/documentation/User_Guide.md` | User-facing multi-language documentation |
| `APP_CONTEXT.md` | Shared architecture update for language profiles |

---

## Estimated Sub-Tasks

| Sub-Task | Points | Description |
|---|---|---|
| Shared: Language profile architecture | 2 | Introduce shared profile models, registry, and active-language routing |
| Shared: Per-language prediction persistence | 2 | Preserve learned words/bigrams separately for English and Spanish |
| Shared: Spanish layout resources | 2 | Define 8-section and 6-section Spanish maps and preview data |
| Shared: Accent-entry design + implementation | 2 | Ship a Spanish-safe diacritic entry path without breaking current utility swipes |
| Shared: Spanish dictionary + bigrams | 2 | Add Spanish base dictionary tiers and prediction resources |
| Android: Settings + IME wiring | 1 | Add language preference and live reload on Android |
| iOS: Settings + extension wiring | 1 | Add language preference and live reload on iOS |
| Learning/docs/tests | 1 | Update quickstart, practice, help, user guide, and shared tests |

**Total**: ~13 story points