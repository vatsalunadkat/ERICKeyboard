# ERICK-145 - Finish Android JoystickView Decomposition

| Field | Value |
|---|---|
| **Type** | Tech Debt |
| **Priority** | High |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | ai-first, android, maintainability, joystick |
| **Dependencies** | Preserve shipped 8-section and rotated 6-section rendering and interaction behavior |

---

## Objective

Reduce the remaining edit risk in `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` by splitting rendering-heavy helpers and geometry code out of the `View` shell.

Status: Completed on `ERICK-141`. `JoystickDrawingUtils.kt`, `JoystickCharacterRenderer.kt`, `JoystickSectionRenderer.kt`, and `JoystickRightDialRenderer.kt` reduced `JoystickView.kt` from 1012 lines to 490 lines while keeping `assembleDebug` green. The view now keeps state, sizing, thumb handling, direction detection, and mode-specific map selection while the rendering-heavy logic lives in dedicated files.

---

## Evidence

- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` started at 1012 lines and is now 490 lines after helper and renderer extraction.
- `android/app/src/main/java/com/vatoo/erick/JoystickCharacterRenderer.kt` now owns the left-dial character placement and text fitting flow for both 8-section and 6-section modes.
- `android/app/src/main/java/com/vatoo/erick/JoystickSectionRenderer.kt` now owns the left-dial section fills, separator lines, and border geometry for both 8-section and 6-section modes.
- `android/app/src/main/java/com/vatoo/erick/JoystickRightDialRenderer.kt` now owns the right-dial segment fill, label layout, and icon rendering flow for both 8-section and 6-section modes.
- `cd android && .\gradlew.bat assembleDebug` stayed green after each extraction slice and after the final right-dial extraction.
- The file now mainly holds view state, direction detection, mode-specific map selection, and thumb orchestration.
- `ERICK-141` identifies `JoystickView.kt` as one of the last Android edit surfaces that is still risky for low-context AI edits.

---

## Scope

1. Keep `JoystickView.kt` focused on view state, sizing, thumb position, and invalidation.
2. Move pure rendering helpers or geometry calculations into dedicated Android-side files.
3. Preserve both 8-section and rotated 6-section drawing behavior exactly.
4. Validate with `cd android && .\gradlew.bat assembleDebug` after each slice.

Result: Completed on `ERICK-141`.

---

## Candidate Split Boundaries

1. `JoystickDrawingUtils.kt` for text sizing, icons, and color helpers
2. `JoystickCharacterRenderer.kt` for 8-section and 6-section character placement
3. `JoystickSectionRenderer.kt` for section/ring arc drawing and separator geometry
4. `JoystickRightDialRenderer.kt` for the right-dial segment, icon, and label loop

---

## Acceptance Criteria

1. `JoystickView.kt` becomes materially smaller and easier to reason about locally.
2. Android `assembleDebug` still passes after the split.
3. No visual or interaction regressions are introduced in 8-section or 6-section mode.

Status: Met on `ERICK-141`.