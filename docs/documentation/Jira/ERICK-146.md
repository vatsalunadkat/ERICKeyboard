# ERICK-146 - Controller Calibration & Diagnostics Screen

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, controller, diagnostics, android, ios, accessibility |
| **Dependencies** | Preserve shipped controller typing from ERICK-84/ERICK-85 and the assisted/6-section fixes validated on ERICK-141 |

---

## Objective

Give users a dedicated calibration and diagnostics surface for physical controllers so dead-zone tuning, stick inversion, assisted mode, and left-handed mode can be verified visually instead of by trial and error inside the keyboard.

---

## Why This Matters

- Controller support now exists, but troubleshooting still happens inside the live keyboard where state is hard to reason about.
- Recent user testing surfaced controller-specific regressions in assisted one-handed input, which suggests the project needs a faster way to inspect resolved directions and effective-side routing.
- Dead-zone values, axis inversion, and left-handed mode are already configurable, but there is no place to confirm what the software thinks the controller is doing.

---

## Current Architecture

### Shared Input Logic
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` owns controller normalization, effective-side routing, assisted-mode left-direction locking, and dial-section-aware direction resolution.

### Android IME / Host-App Surface
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` reads controller axes from `onGenericMotionEvent(...)`, mirrors thumb state into `JoystickView`, and applies the configured dead zone.
- `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt` already stores controller-relevant preferences such as dead zone, handedness, and input mode.

### iOS Surface
- Controller behavior lives in the iOS keyboard extension and host app, but there is no dedicated diagnostics UI for verifying raw and resolved controller state.

---

## Proposed Changes

### 1. Add a Controller Diagnostics Screen

Create a host-app diagnostics screen that can be opened from settings or the main screen and shows:

- Connected controller name and connection status
- Raw left/right stick axis values
- Dead-zone-adjusted stick values
- Resolved `Direction` for each stick
- Effective left/right routing after left-handed mode is applied
- Current `InputMode`, `DialSectionMode`, and assisted-mode locked direction

### 2. Add Live Visual Feedback

Render a simple live preview for both sticks:

- Circular dead-zone boundary
- Current thumb position
- Resolved direction label
- Assisted-mode lock indicator
- Left-handed mode side swap indicator

### 3. Support Calibration Actions

Allow users to:

- Adjust dead zone live and immediately see the effect
- Toggle Y-axis inversion and confirm the resulting direction change
- Reset controller-related preferences to defaults

### 4. Preserve IME Behavior

The diagnostics screen must reuse the same normalization and direction-resolution logic as the live keyboard so the screen cannot drift from actual typing behavior.

### 5. Platform Scope

- Android host app diagnostics UI is required in this ticket.
- iOS should either receive a matching host-app diagnostics view or a documented follow-up scope if controller APIs or extension constraints make parity too large for the same slice.

---

## Validation

- Android shared tests for controller normalization and assisted mode still pass.
- Android debug build passes after adding the diagnostics surface.
- Manual smoke test with a physical controller verifies:
  - stick idle inside dead zone shows no direction
  - each active direction resolves correctly in 8-section and 6-section mode
  - left-handed mode swaps effective sides correctly
  - assisted mode shows the locked left direction and clears it after chord commit

---

## Acceptance Criteria

1. A user can open a controller diagnostics screen without activating the IME.
2. The screen shows live raw axes, normalized axes, and resolved directions for both sticks.
3. Dead-zone and inversion changes are visible immediately on the diagnostics screen.
4. Left-handed mode and assisted mode are represented correctly in the diagnostics output.
5. Android `assembleDebug` remains green and shared controller tests remain green.
