# ERICK iOS Agent Guide

Use this file for work under `ios/`. It supplements the repo-root `AGENTS.md` with iOS-specific host-app, keyboard-extension, and layout rules.

## Scope

- `ERICK/ERICK/` is the host app.
- `ERICK/ErickKeyBoard/` is the custom keyboard extension.
- `ERICK/SharedKeyboard.xcframework/` is generated output. Do not hand-edit it.
- If a change affects shared typing behavior, mappings, controller logic, or predictions, start in `android/shared/` unless the task is purely iOS UI.
- When Android receives a new user-visible feature, settings change, learning flow update, or predictor behavior, mirror the iOS host-app and extension surfaces in the same task unless the user explicitly scopes the work to one platform or a concrete blocker is documented.

## iOS UI And Layout Rules

- iOS UI changes must continue to work on smaller supported iPhone screens and on larger modern phone screens.
- Do not assume tablet-optimized layouts exist. If a touched screen is not validated on larger or unusual form factors, call that out explicitly.
- For keyboard-extension work, keep preview content, suggestion bars, settings overlays, and radial controls from clipping in the available keyboard height.
- Respect safe areas, host-app chrome, and extension height constraints.
- If a layout or spacing change touches onboarding, settings, help, or the typing game, check for truncation, overlap, and inaccessible controls.
- If landscape behavior is relevant to the touched surface, validate it or state that it was not checked.

## Routing Hints

- Extension integration: `ERICK/ErickKeyBoard/KeyboardViewController.swift`
- Extension visual state and container: `ERICK/ErickKeyBoard/KeyboardViewModel.swift`, `KeyboardContainerView.swift`
- Extension settings and UI: `ERICK/ErickKeyBoard/SettingsView.swift`, `CustomPaletteEditorView.swift`, `CustomLayoutViews.swift`, `JoystickView.swift`
- Host app: `ERICK/ERICK/ContentView.swift`, `SettingsView.swift`, `HelpView.swift`, `TypingGameView.swift`

## Documentation Triggers

- User-visible iOS behavior or settings changes: update `docs/documentation/User_Guide.md`.
- Architecture or ownership changes: update `APP_CONTEXT.md` and sync `docs/documentation/APP_CONTEXT.md`.
- Release-note-worthy iOS changes: update `CHANGELOG.md`.

## Validation

- Rebuild shared framework when needed: `cd android && .\gradlew.bat assembleSharedKeyboardXCFramework`
- On Apple hardware, build the `ERICK` Xcode project after refreshing `SharedKeyboard.xcframework`
- For UI-only work, also sanity-check smaller and larger phone-sized layouts when feasible.
- On Windows or other non-macOS environments, use editor diagnostics and explicitly call out the missing runtime validation.