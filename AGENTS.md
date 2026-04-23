# ERICK AI Agent Guide

## Purpose

Use this file as the shortest safe entry point for AI-assisted work in this repository. Read it before diving into the longer architecture docs.

## Read Order

1. `AGENTS.md`
2. `.github/copilot-instructions.md`
3. `PROJECT_PROMPT.md` or the repo-root `APP_CONTEXT.md` only if you need deeper architecture details
4. The active ticket in `docs/documentation/Jira/`

## Authoritative Code Paths

- Shared typing behavior lives in `android/shared/src/commonMain/kotlin/`.
- Android UI and IME behavior live in `android/app/src/main/java/com/vatoo/erick/`.
- iOS keyboard extension behavior lives in `ios/ERICK/ErickKeyBoard/`.
- The iOS host app lives in `ios/ERICK/ERICK/`.
- Website and end-user docs live in `docs/`.

## Critical Invariants

- Start behavior changes in the shared module unless the change is purely platform UI.
- The 6-section utility wheel is rotated `-30°` from the older draft design.
- Current 6-section single-swipe actions are:
  - `NE` = Shift
  - `SE` = Space
  - `S` = Period
  - `SW` = Enter
  - `NW` = Backspace
  - `N` = Symbols toggle
- The 8-section and 6-section modes must continue to coexist. Do not regress 8-section behavior while editing 6-section logic.
- Custom layouts are currently disabled in 6-section mode. Do not re-enable them accidentally.

## High-Risk Files

- `android/app/src/main/java/com/vatoo/erick/SettingsScreen.kt` is very large and contains multiple screens.
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` contains both controller logic and visual state management.
- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` is substantially reduced after ERICK-145, but it still owns Android dial state, direction detection, and mode-specific map selection. Rendering helpers now live in `JoystickDrawingUtils.kt`, `JoystickCharacterRenderer.kt`, `JoystickSectionRenderer.kt`, and `JoystickRightDialRenderer.kt`.

## Documentation Drift Risks

- The repo-root `APP_CONTEXT.md` is canonical. `docs/documentation/APP_CONTEXT.md` is a mirrored copy for docs navigation; update the root file first and sync the docs copy intentionally.
- `PROJECT_PROMPT.md`, `docs/documentation/User_Guide.md`, and ticket docs can drift from the shipped behavior. If you change gestures or mappings, update the relevant docs in the same pass.

## Generated Or Derived Artifacts

- Avoid editing `android/**/build/` outputs.
- Avoid editing `ios/ERICK/SharedKeyboard.xcframework/` by hand.
- Avoid touching `android/app/release/baselineProfiles/*.dm` unless the task is explicitly about regenerating release artifacts.

## Validation Commands

- Android shared tests: `cd android && .\gradlew.bat :shared:testAndroidHostTest`
- Android app build: `cd android && .\gradlew.bat assembleDebug`
- Rebuild the iOS shared framework: `cd android && .\gradlew.bat assembleSharedKeyboardXCFramework`
- iOS app and extension: build the `ERICK` Xcode project after refreshing `SharedKeyboard.xcframework`

If the machine does not have Java, Xcode, or the required SDKs configured, use editor diagnostics and call out the validation gap explicitly.

## Current Repo-Level Gaps

- Automated coverage is still thin outside the shared logic and state-machine tests.
- Several UI files are above the size where lower-context agents edit them safely.
- AI-facing docs were previously inconsistent about the rotated 6-section mapping. Preserve the corrected mapping above.

## Current Planning Ticket

- `docs/documentation/Jira/ERICK-141.md` tracks the AI-first repository hardening plan, including file-splitting work and future cleanup items.