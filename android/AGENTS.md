# ERICK Android Agent Guide

Use this file for work under `android/`. It supplements the repo-root `AGENTS.md` with Android-specific UI, IME, and validation rules.

## Scope

- `app/` contains the Android IME, host app, settings, onboarding, practice, and controller diagnostics surfaces.
- `shared/` contains the authoritative Kotlin Multiplatform behavior used by Android and iOS.
- If a change affects typing behavior, gestures, preview ordering, controller logic, or layout maps, start in `shared/` unless the task is purely Android UI.

## Android UI And Layout Rules

- Android UI changes must continue to work on small phones and larger/taller phone screens.
- Avoid hard-coded sizes that clip text, suggestions, or dial labels on denser or narrower screens.
- For Compose screens, prefer layouts that adapt to available width rather than assuming one phone size.
- For IME and `JoystickView` changes, make sure preview bars, suggestion bars, and dial content still fit without overlap.
- If a layout or sizing change affects controller diagnostics, onboarding, settings, or practice screens, check for scrolling, truncation, and touch-target regressions.
- If landscape, split-screen, or resized-window behavior is relevant to the touched screen, validate that the layout still degrades safely or explicitly call out the gap.

## Routing Hints

- Shared behavior: `shared/src/commonMain/kotlin/KeyboardLogic.kt`, `KeyboardStateMachine.kt`, `ControllerInputProcessor.kt`, `WordPredictionEngine.kt`
- IME bridge: `app/src/main/java/com/vatoo/erick/MyInputMethodService.kt`
- Android dial state and top-level sizing/orchestration: `app/src/main/java/com/vatoo/erick/JoystickView.kt`
- Settings flow: `MainSettingsContent.kt`, `CustomPaletteEditorScreen.kt`, `CustomLayoutListScreen.kt`, `CustomLayoutEditorScreen.kt`
- Host learning flows: `MainScreenContent.kt`, `PracticeHubActivity.kt`, `HelpActivity.kt`
- Controller diagnostics: `ControllerDiagnosticsActivity.kt`

## Documentation Triggers

- User-visible Android behavior or settings changes: update `docs/documentation/User_Guide.md`.
- Architecture or ownership changes: update `APP_CONTEXT.md` and sync `docs/documentation/APP_CONTEXT.md`.
- Release-note-worthy Android changes: update `CHANGELOG.md`.

## Validation

- `cd android && .\gradlew.bat :shared:testAndroidHostTest`
- `cd android && .\gradlew.bat assembleDebug`
- For UI-only work, also do a layout sanity check on at least one smaller phone-sized surface and one larger/taller surface when feasible.
- If device or emulator validation is unavailable, say so explicitly instead of implying layout validation happened.