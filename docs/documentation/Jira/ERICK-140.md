# ERICK-140 - Multi-Language Support (Spanish Foundation And Expansion Path)

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
| **Dependencies** | Must build on shipped dual-mode support from ERICK-139, learned prediction persistence from ERICK-148, and the current quickstart / practice surfaces that already assume shared keyboard behavior |

---

## Objective

Add multi-language support to ERICK, starting with Spanish, through a shared language-profile foundation that works in both 8-section and 6-section modes, preserves the current utility-wheel invariants, and keeps learned prediction history isolated per language.

Phase 1 is the committed shipping scope: safe Spanish support on top of the current product.

Phase 2 is explicitly gated follow-up work: Spanish efficiency optimization and later languages after the shared foundation is stable.

---

## Why This Ticket Needed A Refresh

- The original draft assumed an English-only static predictor. That is no longer true after ERICK-148 shipped learned words, learned bigrams, punctuation-aware suggestion acceptance, and persisted prediction profiles.
- ERICK-139 already shipped the optional 6-section mode, dedicated Symbols layer, and the corrected rotated utility mapping. Multi-language support now has to work across both dial modes.
- ERICK-147 added quickstart and practice lessons on both platforms. If language support lands, those learning surfaces need language-aware copy and drills instead of staying English-assumption-only.
- The shared module is now clearly the authoritative behavior surface, so the ticket should route changes through shared profiles rather than platform-specific forks.
- The older draft did not cover per-language learned-profile versioning, discoverability of accented input, inverted Spanish punctuation, or a logical-first fallback if Spanish efficiency layouts are not ready yet.

---

## Product Requirements To Preserve

- 8-section and 6-section modes must continue to coexist.
- The shipped 6-section utility wheel stays fixed: `N = Symbols`, `NE = Shift`, `SE = Space`, `S = Period`, `SW = Enter`, `NW = Backspace`.
- Existing English behavior must not regress.
- Learned prediction history must not be lost when the user switches languages.
- Custom layouts remain disabled in 6-section mode today and should not be re-enabled accidentally while refactoring.

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

---

## Phase 1 - Committed Spanish Foundation Scope

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
- the storage format should be versioned now so future languages do not require another breaking migration

### 3. Support Spanish In Both Dial Modes

Spanish support must define and validate:

- 8-section Spanish logical layout
- 6-section Spanish logical layout
- Spanish efficiency layout for 8-section when ready
- Spanish efficiency layout for 6-section when ready

Design constraints:

- `ñ` must be enterable without awkward workarounds
- accented vowels (`á`, `é`, `í`, `ó`, `ú`) and `ü` must be available without regressing the shipped utility-wheel mappings
- opening punctuation (`¿`, `¡`) should be supported in a way that stays compatible with the existing symbol and utility model
- numbers, punctuation, and symbols must remain reachable in both dial modes

Phase 1 may ship Spanish with logical layouts first. Spanish efficiency layouts are not required for the initial shipping path if the shared language foundation is ready and the benchmark work is still incomplete.

### 4. Finalize The Accent Entry Strategy

The old draft assumed an accent modifier could simply be added to the utility wheel. That is no longer safe because the 6-section utility wheel is already shipped and documented.

This ticket should lock one supported strategy that works across both dial modes, such as:

- a shared accent state entered from the symbols layer
- long-press or hold behavior on a vowel preview
- a language-aware accent overlay that does not displace existing utility actions

The final choice must be validated against the current 6-section utility mapping, should minimize extra mode confusion, and must be discoverable from the UI or help flow instead of being a hidden expert gesture.

### 5. Add A Language Setting On Both Platforms

Add a language selector in host-app settings and make the active keyboard reload language-specific resources from shared state.

Android:

- `PreferencesManager.kt`
- `SettingsScreen.kt` / `MainSettingsContent.kt`
- `MyInputMethodService.kt`

iOS:

- `SettingsView.swift` in the host app
- extension-side settings/state readers in `KeyboardViewController.swift`

The keyboard should pick up language changes without requiring a full app reinstall or loss of existing prediction data.

### 6. Update Preview, Help, And Learning Surfaces

Language support should not stop at raw typing behavior. The following surfaces should use the active language profile or localized content where applicable:

- left-dial previews and label rendering
- quickstart copy where language-specific examples appear
- practice-lesson sample targets
- help screens and user guide documentation

At minimum, the product must explain how to type `ñ`, accented vowels, and Spanish punctuation before calling the feature complete.

### 7. Validation Strategy

Add shared tests for:

- language-profile selection
- English and Spanish chord routing in both 8-section and 6-section modes
- per-language learned-profile preservation
- language switching mid-session
- accent entry behavior
- Spanish dictionary, corrections, and bigram suggestions

Validation should include a small cross-matrix, not only shared unit tests:

- touch typing in 8-section and 6-section
- controller typing still behaving correctly after language switching
- accessibility-sensitive paths such as left-handed mode and colorblind-safe settings still rendering correctly

---

## Phase 2 - Gated Spanish Optimization And Later Language Expansion

Spanish efficiency layouts for 8-section and 6-section should only move onto the product path after the following exist:

- a Spanish benchmark corpus that is not just translated English frequency data
- explicit punctuation and diacritic coverage rules
- replay and learnability comparisons against the Spanish logical baseline
- a clear judgment on whether the extra complexity actually pays for Spanish users

If that benchmark work is not ready, Phase 1 still ships Spanish safely with logical-first behavior.

Later languages should follow the same shared-profile path instead of inventing one-off platform work.

---

## Out Of Scope For Phase 1

- Automatic translation of every host-app screen and every website page
- Silent mutation of existing English efficiency layouts
- Shipping Spanish efficiency layouts before the corpus and benchmark requirements are ready

---

## Acceptance Criteria

- [ ] A language selector is available in settings on both Android and iOS
- [ ] English remains the default language
- [ ] Spanish is available as the first non-English profile
- [ ] The active keyboard reads the selected language from shared platform storage on both Android and iOS
- [ ] Spanish logical typing works in both 8-section and 6-section modes
- [ ] Spanish prediction uses Spanish base dictionary data and Spanish default suggestions
- [ ] Learned prediction history is isolated per language and survives language switching
- [ ] `ñ`, accented vowels, `ü`, `¿`, and `¡` are enterable without regressing the shipped 6-section utility wheel mapping
- [ ] The accent / diacritic path is documented or shown clearly enough that a user can discover it without guessing
- [ ] Switching languages reloads suggestions and layout data without restarting the app
- [ ] Existing English behavior does not regress
- [ ] Quickstart, practice, help, and user-guide content are updated anywhere language-specific examples are exposed
- [ ] Shared tests cover per-language profile selection and learned-profile isolation, and manual validation covers touch, controller, and accessibility-sensitive settings after language switching
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
| `android/app/src/main/java/com/vatoo/erick/LearningAndPracticeModels.kt` | Language-aware lesson examples and discovery copy |
| `ios/ERICK/ERICK/SettingsView.swift` | Host-app language selector |
| `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` | Extension-side language preference consumption |
| `ios/ERICK/ERICK/LearningHubViews.swift` | Language-aware learning and discovery copy on iOS |
| `docs/documentation/User_Guide.md` | User-facing multi-language documentation |
| `APP_CONTEXT.md` | Shared architecture update for language profiles |

---

## Estimated Sub-Tasks

| Sub-Task | Points | Description |
|---|---|---|
| Shared: Language profile architecture | 2 | Introduce shared profile models, registry, and active-language routing |
| Shared: Per-language prediction persistence | 2 | Preserve learned words/bigrams separately for English and Spanish |
| Shared: Spanish logical layout resources | 2 | Define 8-section and 6-section Spanish logical maps and preview data |
| Shared: Accent-entry design + implementation | 2 | Ship a Spanish-safe diacritic entry path without breaking current utility swipes |
| Shared: Spanish dictionary + bigrams | 2 | Add Spanish base dictionary tiers and prediction resources |
| Android: Settings + IME wiring | 1 | Add language preference and live reload on Android |
| iOS: Settings + extension wiring | 1 | Add language preference and live reload on iOS |
| Learning/docs/tests | 1 | Update quickstart, practice, help, user guide, and shared tests |

Phase 2 follow-up work should be split separately if Spanish efficiency optimization becomes justified.

**Total**: ~13 story points