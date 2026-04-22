# ERICK Keyboard — Copilot Instructions

## Project Overview
ERICK is a dual-joystick keyboard for Android & iOS. Users type by combining left+right directional swipes into character chords. Supports touch and physical game controllers.

## Architecture (Kotlin Multiplatform)
- **Shared module** (`android/shared/src/commonMain/kotlin/`): Core logic shared between platforms
  - `KeyboardContracts.kt` — Enums (`Direction`, `KeyboardMode`, `DialSectionMode`, `InputAction`, etc.) and `KeyboardActionDelegate` interface
  - `KeyboardLogic.kt` — Direction detection (atan2), chord maps (normal/shifted/efficiency/symbols for both 8-section and 6-section), single-swipe actions
  - `KeyboardStateMachine.kt` — State tracking, chord firing, word buffer, suggestion orchestration, accelerating backspace
  - `ColorPalettes.kt` — 7 palette types with 8-color and 6-color variants
  - `CustomLayout.kt` + `CustomLayoutSerializer.kt` — User-defined chord/swipe mappings
  - `WordPredictionEngine.kt` — Trie + bigram + Levenshtein autocorrect
- **Android platform** (`android/app/src/main/java/com/vatoo/erick/`):
  - `MyInputMethodService.kt` — IME service, implements `KeyboardActionDelegate`
  - `JoystickView.kt` — Custom canvas View for the dial UI
  - `SettingsScreen.kt` — Jetpack Compose settings (multiple screens in one file — see ERICK-141)
  - `PreferencesManager.kt` — DataStore preferences
  - `MainActivity.kt` — Compose navigation host
- **iOS platform** (`ios/ERICK/ErickKeyBoard/`):
  - `KeyboardViewController.swift` — Input view controller + `KeyboardViewModel`
  - `JoystickView.swift` — SwiftUI wheel views
  - `SettingsView.swift` — Settings for keyboard extension
- **iOS app** (`ios/ERICK/ERICK/`):
  - `SettingsView.swift` — App-level settings (mirrors extension settings via App Group)
  - `ContentView.swift` — Main app with typing demo area
  - `TypingGameView.swift` — Practice game

## Dual Dial Mode System
Both 8-section and 6-section modes coexist. Controlled by `DialSectionMode` enum (default: EIGHT_SECTION).
- 8-section: 8 directions × 8 = 64 positions, 45° segments
- 6-section: 6 directions × 6 = 36 positions, 60° segments, 2 rings instead of 3, has SYMBOLS layer

## Key Patterns
- **Delegate pattern**: `KeyboardActionDelegate` interface bridges shared logic → platform UI
- **State machine**: All input flows through `KeyboardStateMachine` which calls delegate methods
- **Preferences**: Android uses DataStore (`PreferencesManager`), iOS uses `@AppStorage` with App Group
- **Duplicate direction code**: `KeyboardLogic.kt` exists in both shared module AND Android platform (`android/app/.../KeyboardLogic.kt`) — must keep in sync

## Conventions
- Package: `com.vatoo.erick` (Android), `com.vatoo.erick.shared` (shared module)
- iOS bundle: `com.vatoo.erick`, App Group: `group.com.vatoo.erick`
- Ticket format: `ERICK-{number}` in `docs/documentation/Jira/`
- Changelog: `CHANGELOG.md` at repo root

## AI-First Safety Notes
- Read `AGENTS.md` first for the shortest safe workflow.
- Shared behavior is authoritative in `android/shared/src/commonMain/kotlin/`.
- The 6-section utility wheel is rotated `-30°`: `NE` Shift, `SE` Space, `S` Period, `SW` Enter, `NW` Backspace, `N` Symbols toggle.
- `android/app/src/main/java/com/vatoo/erick/KeyboardLogic.kt` appears stale and unreferenced. Do not use it as the source of truth for current behavior.
- Avoid manual edits to generated artifacts unless the task is explicitly about them: `android/**/build/`, `android/app/release/baselineProfiles/*.dm`, and `ios/ERICK/SharedKeyboard.xcframework/`.
- `APP_CONTEXT.md` and `docs/documentation/APP_CONTEXT.md` are mirrored docs. Keep them aligned intentionally when architecture changes.
- If you change gesture mappings or dial geometry, update `KeyboardLogicTest.kt` and the relevant docs in the same pass.
