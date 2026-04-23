# ERICK-142 - Remove Stale Android KeyboardLogic Shadow

| Field | Value |
|---|---|
| **Type** | Tech Debt |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Completed on `ERICK-141` |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | ai-first, android, maintainability, cleanup |
| **Dependencies** | Preserve shared `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` as the only behavior source of truth |

---

## Objective

Remove or quarantine the stale Android-local `android/app/src/main/java/com/vatoo/erick/KeyboardLogic.kt` so AI agents stop patching the wrong file.

Status: Completed on `ERICK-141` by deleting the file after two cross-checks: a workspace search for imports/references and an Android app import audit confirming app files use `com.vatoo.erick.shared.Direction`.

Latest validation: rerun on 2026-04-22 with `cd android && .\gradlew.bat :shared:testAndroidHostTest` and `cd android && .\gradlew.bat assembleDebug`; both remained green after later branch fixes.

---

## Evidence

- The app-local file defines its own `Direction` enum and `KeyboardLogic` class in `com.vatoo.erick`.
- Workspace searches show runtime and test call sites use `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` instead.
- The app-local file still describes the older 8-direction-only action model and does not match the shipped 6-section behavior.

---

## Scope

1. Confirm there are no remaining app-module references to the stale file.
2. Delete the file or move it into an explicitly archived path that cannot be mistaken for live logic.
3. Update any docs or comments that still mention the Android-local file as an active implementation.
4. Keep build behavior unchanged.

## Validation

- `cd android && .\gradlew.bat assembleDebug`
- `cd android && .\gradlew.bat :shared:testAndroidHostTest`

---

## Acceptance Criteria

1. `android/app/src/main/java/com/vatoo/erick/KeyboardLogic.kt` no longer looks like a live implementation surface.
2. Shared logic remains the sole source of truth for direction mapping and chord behavior.
3. `cd android && .\gradlew.bat assembleDebug` still passes after the cleanup.
