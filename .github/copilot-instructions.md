# ERICK Keyboard — Copilot Instructions

## Start Here

- Read `AGENTS.md` first; treat this file as a Copilot-specific overlay.
- If the task references an `ERICK-###` ticket, read the matching file in `docs/documentation/Jira/`.
- If you are editing anything under `docs/`, also read `docs/AGENTS.md`.
- Use `PROJECT_PROMPT.md` or the repo-root `APP_CONTEXT.md` only when nearby code and `AGENTS.md` are insufficient.
- Copilot can also use nested `AGENTS.md` files and path-specific files under `.github/instructions/`; prefer shared `AGENTS.md` guidance when a rule should work across tools.

## Working Posture

### 1. Think Before Coding

- State assumptions explicitly instead of picking an interpretation silently.
- Start from the nearest owning abstraction, test, or call site before broad repo exploration.
- Push back if a simpler or safer approach clearly fits the request better.

### 2. Simplicity First

- Prefer the smallest behavior-preserving change that solves the task.
- Avoid speculative abstractions, configuration, or edge-case handling that the request does not require.

### 3. Surgical Changes

- Touch only the files and lines that directly serve the task.
- Match local style and clean up only the unused code your own change creates.

### 4. Goal-Driven Execution

- Name one cheap falsifiable check before the first edit.
- Run the narrowest relevant validation command, test, or diagnostic before widening scope.
- If behavior changes, update the closest tests and docs in the same pass.
- If Android receives a new user-visible feature, settings change, learning flow update, or predictor behavior change, implement the matching iOS behavior in the same pass unless the user explicitly scopes the work to one platform or a concrete blocker is documented.

## Project Overview
ERICK is a dual-joystick keyboard for Android and iOS. Users type by combining left and right directional swipes into character chords. The product supports both touch and physical game controllers.

## Architecture (Kotlin Multiplatform)
- **Shared module** (`android/shared/src/commonMain/kotlin/`): authoritative typing and controller behavior
  - `KeyboardContracts.kt` — Enums (`Direction`, `KeyboardMode`, `DialSectionMode`, `InputAction`, etc.) and `KeyboardActionDelegate`
  - `KeyboardLogic.kt` — Direction detection, chord maps, and single-swipe actions for 8-section and 6-section modes
  - `KeyboardStateMachine.kt` — Chord firing, word buffer, suggestion orchestration, mode transitions, accelerating backspace
  - `ControllerInputProcessor.kt` — Shared controller stick normalization and direction resolution
  - `ColorPalettes.kt`, `CustomLayout.kt`, `CustomLayoutSerializer.kt`, `WordPredictionEngine.kt`
- **Android platform** (`android/app/src/main/java/com/vatoo/erick/`):
  - `MyInputMethodService.kt` — IME service and `KeyboardActionDelegate` bridge
  - `JoystickView.kt` — Dial state/orchestration; drawing lives in the extracted renderer helpers
  - `SettingsScreen.kt`, `SettingsActivity.kt`, `MainSettingsContent.kt`, `CustomPaletteEditorScreen.kt`, `CustomLayoutListScreen.kt`, `CustomLayoutEditorScreen.kt` — settings flow
  - `MainActivity.kt`, `MainScreenContent.kt`, `PracticeHubActivity.kt`, `HelpActivity.kt`, `ControllerDiagnosticsActivity.kt` — host app, learning, and diagnostics
  - `PreferencesManager.kt` — DataStore preferences
- **iOS keyboard extension** (`ios/ERICK/ErickKeyBoard/`):
  - `KeyboardViewController.swift` — UIInputViewController and delegate integration
  - `KeyboardViewModel.swift` and `KeyboardContainerView.swift` — extracted visual state and SwiftUI container
  - `JoystickView.swift`, `SettingsView.swift`, `CustomPaletteEditorView.swift`, `CustomLayoutViews.swift`
- **iOS host app** (`ios/ERICK/ERICK/`):
  - `ContentView.swift`, `TypingGameView.swift`, `SettingsView.swift`

## Dual Dial Mode System
Both 8-section and 6-section modes coexist. `DialSectionMode` defaults to `EIGHT_SECTION`.
- 8-section: 8 directions × 8 = 64 positions with 45° segments
- 6-section: 6 directions × 6 = 36 positions with 60° segments, 2 rings, and a symbols layer

## Key Patterns
- **Shared logic is authoritative**: start behavior changes in `android/shared/src/commonMain/kotlin/` unless the task is purely platform UI.
- **Delegate pattern**: `KeyboardActionDelegate` bridges shared logic to platform UI.
- **State machine**: all input flows through `KeyboardStateMachine`.
- **Preferences**: Android uses DataStore (`PreferencesManager`); iOS uses `@AppStorage` with the App Group.

## Critical Invariants
- The shipped 6-section utility wheel is rotated `-30°`: `NE` Shift, `SE` Space, `S` Period, `SW` Enter, `NW` Backspace, `N` Symbols toggle.
- The 8-section and 6-section modes must both remain correct.
- Custom layouts are currently disabled in 6-section mode. Do not re-enable them accidentally.
- `APP_CONTEXT.md` at the repo root is canonical; `docs/documentation/APP_CONTEXT.md` is a mirrored copy.

## AI-First Safety Notes
- Avoid manual edits to generated artifacts unless the task is explicitly about them: `android/**/build/`, `android/app/release/baselineProfiles/*.dm`, and `ios/ERICK/SharedKeyboard.xcframework/`.
- Route Android settings work into the extracted settings files before editing the wrappers.
- Route controller diagnostics through shared `ControllerInputProcessor.kt` instead of duplicating stick parsing on a platform surface.
- If you change gesture mappings, dial geometry, onboarding flow text, or controller behavior, update the relevant tests and docs in the same pass.
- Keep Android and iOS user-visible behavior aligned. New Android settings, learning flows, or prediction behavior should ship with the corresponding iOS implementation in the same task unless the user explicitly says otherwise or a concrete blocker is called out.
- If you change architecture, module ownership, or major flow descriptions, update `APP_CONTEXT.md`, sync `docs/documentation/APP_CONTEXT.md`, and update the relevant diagram source when it would otherwise become inaccurate.
- If the workflow explicitly allows git writes, prefer frequent small validated commits on the current branch with a clear subject and a detailed body that records the why and validation.
- Keep this file aligned with `AGENTS.md`, `CLAUDE.md`, and `.cursor/rules/erick-ai-first.mdc` when the shared AI workflow rules change.

## Validation Commands
- Android shared tests: `cd android && .\gradlew.bat :shared:testAndroidHostTest`
- Android debug build: `cd android && .\gradlew.bat assembleDebug`
- Rebuild iOS shared framework: `cd android && .\gradlew.bat assembleSharedKeyboardXCFramework`
- iOS build: build the `ERICK` Xcode project after refreshing `SharedKeyboard.xcframework`

For docs-only work, reread the changed markdown or HTML, keep mirrored docs synchronized, and confirm that changed paths, labels, and availability statements stay aligned.
