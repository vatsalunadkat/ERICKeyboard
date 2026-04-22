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

Status: In progress on `ERICK-141`. `JoystickDrawingUtils.kt` and `JoystickCharacterRenderer.kt` have now been extracted, which reduced `JoystickView.kt` from 1012 lines to 752 lines while keeping `assembleDebug` green. Remaining work is to peel off the 8-section and 6-section section geometry and arc drawing flow into narrower units.

---

## Evidence

- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` started at 1012 lines and is now 752 lines after helper and character-renderer extraction.
- `android/app/src/main/java/com/vatoo/erick/JoystickCharacterRenderer.kt` now owns the left-dial character placement and text fitting flow for both 8-section and 6-section modes.
- The file still holds view state, direction detection, palette lookup, 8-section rendering, and 6-section rendering in one class.
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

---

## Acceptance Criteria

1. `JoystickView.kt` becomes materially smaller and easier to reason about locally.
2. Android `assembleDebug` still passes after the split.
3. No visual or interaction regressions are introduced in 8-section or 6-section mode.