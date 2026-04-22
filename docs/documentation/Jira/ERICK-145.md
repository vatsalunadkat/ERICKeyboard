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

Status: In progress on `ERICK-141`. `JoystickDrawingUtils.kt`, `JoystickCharacterRenderer.kt`, and `JoystickSectionRenderer.kt` have now been extracted, which reduced `JoystickView.kt` from 1012 lines to 591 lines while keeping `assembleDebug` green. The highest-risk left-dial rendering paths are now out of the `View`; remaining work is mainly the right-dial rendering loop and any final orchestration cleanup that still feels too broad.

---

## Evidence

- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` started at 1012 lines and is now 591 lines after helper, character-renderer, and section-renderer extraction.
- `android/app/src/main/java/com/vatoo/erick/JoystickCharacterRenderer.kt` now owns the left-dial character placement and text fitting flow for both 8-section and 6-section modes.
- `android/app/src/main/java/com/vatoo/erick/JoystickSectionRenderer.kt` now owns the left-dial section fills, separator lines, and border geometry for both 8-section and 6-section modes.
- The file still holds view state, direction detection, right-dial rendering, and mode-specific map selection in one class.
- `ERICK-141` identifies `JoystickView.kt` as one of the last Android edit surfaces that is still risky for low-context AI edits.

---

## Scope

1. Keep `JoystickView.kt` focused on view state, sizing, thumb position, and invalidation.
2. Move pure rendering helpers or geometry calculations into dedicated Android-side files.
3. Preserve both 8-section and rotated 6-section drawing behavior exactly.
4. Validate with `cd android && .\gradlew.bat assembleDebug` after each slice.

---

## Candidate Split Boundaries

1. `JoystickDrawingUtils.kt` for text sizing, icons, and color helpers
2. `JoystickCharacterRenderer.kt` for 8-section and 6-section character placement
3. `JoystickSectionRenderer.kt` for section/ring arc drawing and separator geometry
4. Optional follow-on: `JoystickRightDialRenderer.kt` for the right-dial segment, icon, and label loop if `JoystickView.kt` still feels too broad

---

## Acceptance Criteria

1. `JoystickView.kt` becomes materially smaller and easier to reason about locally.
2. Android `assembleDebug` still passes after the split.
3. No visual or interaction regressions are introduced in 8-section or 6-section mode.