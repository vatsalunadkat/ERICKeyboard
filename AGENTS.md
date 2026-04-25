# ERICK AI Agent Guide

## Purpose

Use this file as the shortest safe entry point for Codex and other AGENTS-aware assistants working in this repository. Claude Code, Cursor, and GitHub Copilot have tool-native files that mirror the same repo-specific guardrails.

## Tool Surfaces

- `AGENTS.md` — primary shared instructions for Codex; also read by Cursor and GitHub Copilot
- `CLAUDE.md` — Claude Code root instructions; it should import `AGENTS.md` instead of drifting from it
- `.cursor/rules/*.mdc` — Cursor project rules; use for always-on or file-scoped behavior
- `.github/copilot-instructions.md` — GitHub Copilot repository-wide instructions
- `.github/instructions/*.instructions.md` — optional GitHub Copilot path-specific instructions
- `android/AGENTS.md` and `android/CLAUDE.md` — Android-specific instructions
- `ios/AGENTS.md` and `ios/CLAUDE.md` — iOS-specific instructions
- `docs/AGENTS.md` and `docs/CLAUDE.md` — docs and website subtree instructions
- `docs/documentation/Research/AGENTS.md` and `docs/documentation/Research/CLAUDE.md` — research and Python workflow instructions

## Minimal Workflow

1. Read this file and the smallest set of local code, tests, and canonical docs that govern the task.
2. Identify the owning code path before editing; do not start from a wrapper if a nearby owner decides the behavior.
3. Make the smallest correct change.
4. Run the narrowest validation that can falsify the change.
5. Update the tests, docs, and diagrams triggered by the change.
6. If the workflow allows git writes, commit and push small validated checkpoints on the current branch.

## Working Posture

### 1. Think Before Coding

- State assumptions explicitly. If multiple interpretations exist, surface them instead of picking silently.
- Start from the nearest owning abstraction, test, or call site before broad repo exploration.
- Push back when a simpler or safer approach exists.

### 2. Simplicity First

- Prefer the smallest behavior-preserving change that solves the request.
- Avoid speculative abstractions, configuration, or edge-case handling that the task does not require.
- If 200 lines can be 50 without losing clarity, simplify.

### 3. Surgical Changes

- Touch only the files and lines that directly serve the task.
- Match local style; do not refactor, reformat, or delete adjacent code unless the request requires it.
- Clean up only the unused code your own change creates.

### 4. Goal-Driven Execution

- Name one cheap discriminating check before the first edit.
- Validate with the narrowest relevant command, test, or diagnostic before widening scope.
- If behavior changes, update the closest tests and docs in the same pass.

## Read Order

1. `AGENTS.md`
2. Your tool-native instruction surface if applicable (`CLAUDE.md`, `.cursor/rules/erick-ai-first.mdc`, or `.github/copilot-instructions.md`)
3. `PROJECT_PROMPT.md` or the repo-root `APP_CONTEXT.md` only if nearby code and this file are insufficient
4. The active ticket in `docs/documentation/Jira/` when the work references an `ERICK-###` item

## Read-First Checklist

- Behavior, gestures, controller input, or dial geometry: read the owning shared file in `android/shared/src/commonMain/kotlin/`, the nearest shared tests, and the relevant user-facing docs before editing.
- Android UI, IME, Compose, or controller-diagnostics layout work: read `android/AGENTS.md` first.
- iOS host app, keyboard extension, SwiftUI, or controller-layout work: read `ios/AGENTS.md` first.
- Settings, onboarding, practice, or diagnostics flows: read the nearest Android or iOS screen/controller plus the matching ticket or guide.
- Architecture, module ownership, or file-splitting work: read the repo-root `APP_CONTEXT.md` first.
- Docs, website, diagrams, or release notes: read `docs/AGENTS.md` first.
- Research, optimizer, analysis scripts, or corpus-processing work: read `docs/documentation/Research/AGENTS.md` first.
- If code and docs disagree, verify shipped behavior in code and tests, then update docs to match the shipped behavior.

## Authoritative Code Paths

- Shared typing and controller behavior lives in `android/shared/src/commonMain/kotlin/`.
- Android UI, host-app, and IME behavior lives in `android/app/src/main/java/com/vatoo/erick/`.
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

## High-Risk Or High-Value Edit Surfaces

- Android settings are split: start in `MainSettingsContent.kt`, `CustomPaletteEditorScreen.kt`, `CustomLayoutListScreen.kt`, or `CustomLayoutEditorScreen.kt` before editing the `SettingsScreen.kt` or `SettingsActivity.kt` wrappers.
- Android host learning flows live in `MainScreenContent.kt`, `PracticeHubActivity.kt`, and `HelpActivity.kt`.
- Android controller diagnostics live in `ControllerDiagnosticsActivity.kt` and must reuse shared `ControllerInputProcessor.kt`.
- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` owns dial state and orchestration, but drawing helpers live in `JoystickDrawingUtils.kt`, `JoystickCharacterRenderer.kt`, `JoystickSectionRenderer.kt`, and `JoystickRightDialRenderer.kt`.
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` still owns controller and delegate integration, while visual state and container UI live in `KeyboardViewModel.swift` and `KeyboardContainerView.swift`.
- `ios/ERICK/ErickKeyBoard/SettingsView.swift` is slimmer after ERICK-141; palette and custom-layout editing now live in `CustomPaletteEditorView.swift` and `CustomLayoutViews.swift`.

## Documentation Drift Risks

- The repo-root `APP_CONTEXT.md` is canonical. `docs/documentation/APP_CONTEXT.md` is a mirrored copy for docs navigation; update the root file first and sync the docs copy intentionally.
- `PROJECT_PROMPT.md`, `docs/documentation/User_Guide.md`, and ticket docs can drift from shipped behavior. If you change gestures, mappings, onboarding flows, or controller behavior, update the relevant docs in the same pass.
- Keep `AGENTS.md`, `CLAUDE.md`, `.cursor/rules/erick-ai-first.mdc`, and `.github/copilot-instructions.md` aligned when the shared AI workflow rules change.

## Documentation And Diagram Update Matrix

- Architecture, ownership, module moves, or major flow changes: update `APP_CONTEXT.md`, then sync `docs/documentation/APP_CONTEXT.md`, and update `ERICK_architecture.drawio` when the diagram would otherwise lie.
- Dial geometry, gesture mappings, preview ordering, or layout rules: update `docs/documentation/User_Guide.md`, the relevant Jira ticket, and `docs/documentation/joystick_wireframe.drawio` when geometry or labeled controls changed.
- Public feature descriptions, install flow, availability, or store messaging: update `README.md` and the affected website pages under `docs/`.
- Release-note-worthy user-visible changes: update `CHANGELOG.md`.
- If a `.drawio` source changes and a checked-in export exists beside it, refresh the export when possible or explicitly note the export gap.
- Do not update legacy or research docs unless the task actually changes them.

## Git Workflow For Autonomous Agents

- Stay on the current branch unless the user explicitly asks for branch creation.
- If commit and push are allowed in the workflow, prefer small validated commits at natural checkpoints instead of one large opaque diff.
- Use commit subjects in the form `scope: outcome` when possible.
- Add a short commit body that states why the change was made and how it was validated.
- Do not force-push, rewrite history, or commit unrelated changes unless explicitly asked.
- If the environment or user does not allow autonomous git writes, leave the work in a commit-ready state and report the recommended commit message.

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

For docs-only work, validate by rereading the changed markdown, HTML, and diagram-related notes for consistency, mirrored-doc sync, and link/path accuracy.

## Current Repo-Level Gaps

- Automated coverage is still thin outside the shared logic, controller processing, and state-machine tests.
- Several UI files are still large enough that lower-context agents should route through the owning extracted surface before editing.
- AI-facing docs previously drifted from the shipped 6-section mapping. Preserve the corrected mapping above.

## Current Planning Ticket

- `docs/documentation/Jira/ERICK-141.md` tracks the AI-first repository hardening plan, including file-splitting work and follow-up multi-agent guidance.
