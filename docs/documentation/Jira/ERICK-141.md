# ERICK-141 - Split Large Files for Maintainability

| Field | Value |
|---|---|
| **Type** | Tech Debt / Refactor |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | refactor, android, ios, maintainability |
| **Dependencies** | None (should be done on a clean branch) |

---

## Description

Split the largest source files into smaller, single-responsibility files. This improves accuracy when AI-assisted coding (fewer ambiguous edit targets, ability to read entire file context in one pass) and standard maintainability.

**Files over ~600 lines that mix multiple independent concerns are candidates. Files that are large but cohesive (single class, single responsibility) should NOT be split.**

---

## Motivation

| Problem | Impact |
|---|---|
| `SettingsScreen.kt` has 4 independent screens (1574 lines) | Editing one screen requires skipping past 1000+ irrelevant lines |
| `SettingsView.swift` (extension) has 4 independent views (1141 lines) | Same issue on iOS side |
| `KeyboardViewController.swift` has ViewModel + Controller (1043 lines) | Two classes in one file, different responsibilities |
| `JoystickView.kt` has drawing + input + data logic (999 lines) | Drawing helpers are independent of touch handling |

---

## Detailed Plan

### Task 1: Split Android `SettingsScreen.kt` (1574 lines → 4 files)

**Current structure in `android/app/src/main/java/com/vatoo/erick/SettingsScreen.kt`:**
- Lines 1-860: `SettingsScreen()` composable + helper components (`LayoutRadioOption`, `SettingToggle`, `PaletteRadioOption`, `CustomPaletteRadioOption`)
- Lines 861-1121: `CustomPaletteEditorScreen()` composable (standalone screen)
- Lines 1122-1340: `CustomLayoutListScreen()` composable (standalone screen)
- Lines 1341-end: `CustomLayoutEditorScreen()` + `SwipeBindingRow()` + `SwipeBindingEditorScreen()`

**Split into:**

| New File | Contents | ~Lines |
|---|---|---|
| `SettingsScreen.kt` | Main settings screen + `SettingToggle`, `LayoutRadioOption`, `PaletteRadioOption`, `CustomPaletteRadioOption` | ~860 |
| `CustomPaletteEditorScreen.kt` | `CustomPaletteEditorScreen()` | ~260 |
| `CustomLayoutListScreen.kt` | `CustomLayoutListScreen()` | ~220 |
| `CustomLayoutEditorScreen.kt` | `CustomLayoutEditorScreen()` + `SwipeBindingRow` + `SwipeBindingEditorScreen` | ~240 |

**Steps:**
1. Create 3 new files with the extracted composables
2. Move relevant imports to each new file (all in same package `com.vatoo.erick`, no import changes needed for callers)
3. Verify `MainActivity.kt` navigation calls still resolve (they reference function names, not file names)
4. Build and verify no compile errors

---

### Task 2: Split iOS `ErickKeyBoard/SettingsView.swift` (1141 lines → 4 files)

**Current structure in `ios/ERICK/ErickKeyBoard/SettingsView.swift`:**
- Lines 1-467: `SettingsView` struct + collapsible section helper + preview
- Lines 468-742: `ColorPaletteDefinitions`, `ColorPaletteEntry`, palette UI components
- Lines 743-912: `CustomPaletteEditorView` struct + `Color` extension
- Lines 913-1046: `CustomLayoutListView` struct
- Lines 1047-1141: `CustomLayoutEditorView` struct

**Split into:**

| New File | Contents | ~Lines |
|---|---|---|
| `SettingsView.swift` | Main settings view + collapsible section helper | ~467 |
| `ColorPaletteComponents.swift` | `ColorPaletteDefinitions`, `ColorPaletteEntry`, palette option views, `Color` extension | ~445 |
| `CustomPaletteEditorView.swift` | `CustomPaletteEditorView` | ~170 |
| `CustomLayoutViews.swift` | `CustomLayoutListView` + `CustomLayoutEditorView` | ~230 |

**Steps:**
1. Create 3 new Swift files in the `ErickKeyBoard` target
2. Ensure they are added to the Xcode project's target membership (keyboard extension, not main app)
3. All types are internal by default in Swift — same module, no access issues
4. Build and verify

---

### Task 3: Split iOS `KeyboardViewController.swift` (1043 lines → 2 files)

**Current structure:**
- Lines 1-15: Imports
- Lines 16-310: `KeyboardViewModel` class (~295 lines) — `ObservableObject` managing all visual state
- Lines 311-1043: `KeyboardViewController` class (~732 lines) — `UIInputViewController` + `KeyboardActionDelegate`

**Split into:**

| New File | Contents | ~Lines |
|---|---|---|
| `KeyboardViewModel.swift` | `KeyboardViewModel` class | ~310 |
| `KeyboardViewController.swift` | `KeyboardViewController` class | ~740 |

**Steps:**
1. Create `KeyboardViewModel.swift` with the class and its imports
2. Remove the class from `KeyboardViewController.swift`
3. Both are in the same module — no access changes needed
4. Build and verify

---

### Task 4: Extract Android `JoystickView.kt` Drawing Helpers (999 lines → 2 files)

**Current structure in `android/app/src/main/java/com/vatoo/erick/JoystickView.kt`:**
- Lines 1-100: Class declaration, properties, initialization
- Lines 101-305: Public API (setLockedDirection, etc.) and utility properties
- Lines 306-850: `onDraw()`, `drawCharText()`, `drawRightDialContent()`, `fittedTextSize()`, `getRightDialLabelLines()`, `drawProgrammaticIcon()` — all drawing
- Lines 850-999: `updateThumb()`, `updateThumbFromController()`, `getInfoForDirection()`, `resetThumb()`, `setPreviewText()`, `darkenColor()`

The drawing methods are private and numerous. Rather than a file split (which would require making them internal or using extension functions), extract the icon-drawing logic and text-fitting utilities into a companion/utility:

| New File | Contents | ~Lines |
|---|---|---|
| `JoystickDrawingUtils.kt` | `drawProgrammaticIcon()`, `fittedTextSize()`, `fittedSingleLineTextSize()`, `darkenColor()`, `getRightDialLabelLines()` as top-level or object functions | ~200 |
| `JoystickView.kt` | Everything else | ~800 |

**Steps:**
1. Extract pure utility functions (no `this` reference to View state) into `JoystickDrawingUtils.kt`
2. Pass required parameters explicitly (Canvas, Paint, etc.)
3. Keep `onDraw()` and `drawCharText()` in JoystickView (they reference class state heavily)
4. Build and verify

---

### Task 5: Verify & Test

- [ ] Android: Full Gradle build (`./gradlew assembleDebug`)
- [ ] iOS: Xcode build for both app and keyboard extension targets
- [ ] Manual smoke test: Open settings, navigate to custom palette editor, custom layout editor
- [ ] Manual smoke test: Type using keyboard on both platforms
- [ ] Verify no regressions in 6-section dial mode
- [ ] Update all docs and md files
- [ ] update copilot-instructions.md

---

## Files NOT Being Split (and why)

| File | Lines | Reason |
|---|---|---|
| `MyInputMethodService.kt` | 728 | Single class, single responsibility (Android IME lifecycle) |
| `KeyboardStateMachine.kt` | 530 | Single class, core state machine — splitting would hurt cohesion |
| `JoystickView.swift` (iOS) | 670 | Manageable size, SwiftUI views are naturally composable |
| `SettingsView.swift` (app) | 606 | Borderline; simpler than the extension version |
| `MainActivity.kt` | 685 | Single Activity with navigation — standard Android pattern |
| `WordPredictionEngine.kt` | 443 | Single class, cohesive algorithm |

---

## Acceptance Criteria

1. No file in the project exceeds ~850 lines (with exceptions for cohesive single-class files)
2. Each new file has a single clear responsibility
3. No functional changes — pure structural refactor
4. Both platforms build and pass smoke tests
5. Navigation between settings screens still works correctly
