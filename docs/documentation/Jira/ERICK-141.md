# ERICK-141 - AI-First Repository Hardening

| Field | Value |
|---|---|
| **Type** | Tech Debt / Developer Experience |
| **Priority** | High |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | ai-first, docs, maintainability, android, ios, shared |
| **Dependencies** | Merge `main` first and preserve the shipped rotated 6-section behavior |

---

## Objective

Make the repository safer for AI models and coding agents with mixed reasoning quality. Optimize for correctness, small edit surfaces, clear source-of-truth files, and explicit validation paths. Favor accuracy over speed.

---

## Audit Summary

| Finding | Evidence | Risk for AI Agents |
|---|---|---|
| AI-facing docs drifted from shipped 6-section behavior | `PROJECT_PROMPT.md`, `User_Guide.md`, and ticket docs described the earlier unrotated mapping | Agents implement or test the wrong gestures |
| Large multi-concern UI files are hard to edit locally | `SettingsScreen.kt` 1595 lines, `SettingsView.swift` 1156, `KeyboardViewController.swift` 1045, `JoystickView.kt` 1012 | Lower-context agents make broad or ambiguous edits |
| Shared tests were thin and had stale 6-section expectations | `KeyboardLogicTest.kt` still asserted `N = Shift` and `NW = Symbols` before this branch update | Behavior regressions can pass casual review |
| An Android-local `KeyboardLogic.kt` shadow existed before cleanup | The file was unreferenced, diverged from current shared behavior, and has now been removed on this branch | Agents could patch the wrong logic surface without this cleanup |
| Architecture context is duplicated | `APP_CONTEXT.md` exists at repo root and under `docs/documentation/` | Documentation drift and double-update failures |
| Generated artifacts create noisy diffs | `android/app/release/baselineProfiles/*.dm` showed up as local changes during the merge | Agents may waste time or accidentally stage generated files |

---

## Completed In This Branch

- Merged `origin/main` into `ERICK-141` and resolved the 6-section conflicts in favor of the shipped rotated geometry.
- Added `AGENTS.md` as a short, AI-first repo entrypoint.
- Updated `.github/copilot-instructions.md`, `README.md`, and `PROJECT_PROMPT.md` to point agents to the new quick-start path.
- Corrected the shared 6-section test expectations in `KeyboardLogicTest.kt`.
- Corrected the main end-user and AI-facing 6-section mapping docs.
- Removed the unused Android-local `KeyboardLogic.kt` shadow after cross-checking that Android app files import `com.vatoo.erick.shared.Direction` and runtime behavior flows through the shared module.
- Split Android settings into a small navigation wrapper plus extracted screen files, with `MainSettingsContent.kt` holding the main settings UI.
- Split Android host-app home UI out of `MainActivity.kt` into `MainScreenContent.kt` and removed two unused setup-helper composables.
- Corrected the documented Android shared test task to `:shared:testAndroidHostTest`.

---

## Phase 0: Immediate Hardening

- [x] Add a short canonical AI guide (`AGENTS.md`)
- [x] Encode the shipped rotated 6-section mapping in the main AI-facing docs
- [x] Correct stale shared test expectations
- [x] Expand Copilot instructions with high-risk files and generated-artifact rules
- [x] Replace the narrow file-splitting ticket with a broader AI-first plan

---

## Phase 1: Canonical Context Cleanup

### Goal

Reduce the number of places an agent has to read before it can identify the correct edit surface.

### Tasks

1. Choose one authoritative architecture document.
2. Make the duplicate `docs/documentation/APP_CONTEXT.md` explicitly mirrored or generated from the root copy.
3. Keep `AGENTS.md` short and operational; keep `PROJECT_PROMPT.md` and `APP_CONTEXT.md` deeper and more descriptive.
4. Add a lightweight docs-sync checklist whenever behavior, layout mappings, or architecture changes.

### Deliverables

- A clearly documented source of truth for architecture
- A mirrored-doc update rule or generation step
- Fewer contradictory descriptions of the same feature

---

## Phase 2: Reduce Edit Surface Area

### Goal

Split only the files whose size and mixed responsibilities create the highest risk for incorrect AI edits.

### Priority Targets

#### Android `SettingsScreen.kt` (1595 lines)

Split into:

| New File | Contents |
|---|---|
| `SettingsScreen.kt` | Main settings screen and shared row components |
| `CustomPaletteEditorScreen.kt` | Custom palette editor screen |
| `CustomLayoutListScreen.kt` | Custom layout list screen |
| `CustomLayoutEditorScreen.kt` | Custom layout editor, swipe binding row, binding editor |

Status: Completed on `ERICK-141` with `MainSettingsContent.kt`, `CustomPaletteEditorScreen.kt`, `CustomLayoutListScreen.kt`, and `CustomLayoutEditorScreen.kt` extracted.

#### Android `MainActivity.kt` (685 lines before split)

Split into:

| New File | Contents |
|---|---|
| `MainActivity.kt` | Activity lifecycle, theme wiring, and keyboard-enabled status checks |
| `MainScreenContent.kt` | Home screen content, setup cards, and controller status UI |

Status: Completed on `ERICK-141`; the unused `InstructionStep` helpers were removed instead of being carried forward.

#### iOS `ErickKeyBoard/SettingsView.swift` (1156 lines)

Split into:

| New File | Contents |
|---|---|
| `SettingsView.swift` | Main settings view and section helpers |
| `ColorPaletteComponents.swift` | Palette definitions and palette UI components |
| `CustomPaletteEditorView.swift` | Custom palette editor |
| `CustomLayoutViews.swift` | Custom layout list and editor views |

#### iOS `KeyboardViewController.swift` (1045 lines)

Split into:

| New File | Contents |
|---|---|
| `KeyboardViewModel.swift` | ObservableObject visual state holder |
| `KeyboardViewController.swift` | UIInputViewController and delegate integration |

#### Android `JoystickView.kt` (1012 lines)

Extract pure drawing helpers into:

| New File | Contents |
|---|---|
| `JoystickDrawingUtils.kt` | Text fitting, icon drawing, color darkening, label splitting |

### Guardrail

These refactors must remain behavior-preserving. No feature work should be mixed into the split.

---

## Phase 3: Validation Hardening

### Goal

Make it easier for agents to prove they changed the right thing.

### Tasks

1. Expand shared tests around 6-section direction detection and single-swipe actions.
2. Add focused tests for `KeyboardStateMachine` symbols toggling and mode transitions.
3. Document the preferred validation commands in one place.
4. Record smoke-test paths for settings, typing, controller input, and the typing game.

### Minimum Validation Matrix

- Android shared tests: `cd android && .\gradlew.bat :shared:testAndroidHostTest`
- Android debug build: `cd android && .\gradlew.bat assembleDebug`
- Rebuild iOS shared framework: `cd android && .\gradlew.bat assembleSharedKeyboardXCFramework`
- iOS build: build the `ERICK` Xcode project after refreshing `SharedKeyboard.xcframework`

---

## Phase 4: Cleanup Backlog

### Items To Investigate

| Item | Scope | Why It Matters |
|---|---|---|
| Decide how to handle tracked baseline profile `.dm` files | Android release artifacts | They create noisy diffs and merge friction |
| Review whether host-app `SettingsView.swift` needs later splitting | iOS | The host app still has large UI surfaces outside the extension targets |
| Add an explicit “generated artifact” section to contributor docs | Repo-wide | Helps agents and humans avoid editing derived files |

---

## Findings To Preserve For Future Agents

- The shipped 6-section utility mapping is `NE` Shift, `SE` Space, `S` Period, `SW` Enter, `NW` Backspace, `N` Symbols.
- Shared logic is the canonical source for behavior changes.
- The old Android-local `KeyboardLogic.kt` shadow has been removed; behavior changes should start in `android/shared/src/commonMain/kotlin/`.
- Large-file refactors should be isolated from behavior changes.
- Documentation updates must travel with mapping changes.

---

## Acceptance Criteria

1. A new AI agent can start from `AGENTS.md` and identify the correct code path within a few reads.
2. The main AI-facing docs describe the shipped 6-section mapping correctly.
3. `ERICK-141` contains a concrete, phased plan for context cleanup, large-file splitting, validation, and cleanup debt.
4. High-risk stale sources and generated-artifact traps are documented explicitly.
5. Shared test coverage reflects the shipped rotated 6-section behavior.
