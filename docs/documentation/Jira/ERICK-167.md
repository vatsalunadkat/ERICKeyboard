# ERICK-167 - Keyboard Height Adjustment Slider

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, accessibility, android, ios, settings, ui |
| **Dependencies** | None. Extends the existing keyboard layout and settings infrastructure |

---

## Objective

Add a user-adjustable slider in settings that controls the overall height of the ERICK keyboard, allowing users with different hand sizes, device sizes, and accessibility needs to optimize comfort.

---

## Why This Matters

- Users have different hand sizes, phone sizes, and reach preferences. A fixed keyboard height is a compromise that fits nobody perfectly.
- Accessibility users with limited range of motion may need a smaller keyboard that requires less reaching.
- Users on tablets may want a larger keyboard with bigger dial targets.
- Larger keyboard height means larger dial targets, which directly improves accuracy for users with motor limitations.
- This is a common feature in mainstream keyboards (Gboard, SwiftKey) that users expect.

---

## Current Surfaces To Build On

### Android
- `android/app/src/main/res/layout/keyboard_simple.xml` — keyboard layout with fixed height
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` — keyboard view creation and sizing
- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` — dial rendering (scales with view size)
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt` — settings UI

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` — keyboard extension height constraint
- `ios/ERICK/ErickKeyBoard/KeyboardContainerView.swift` — SwiftUI layout
- `ios/ERICK/ErickKeyBoard/JoystickView.swift` — dial rendering
- `ios/ERICK/ErickKeyBoard/SettingsView.swift`

---

## Proposed Scope

### 1. Add Height Preference

- New preference: `keyboard_height_scale` — a float value from 0.75 to 1.5 (representing 75% to 150% of default height)
- Default value: 1.0 (current height unchanged for existing users)
- Stored in DataStore (Android) and App Group UserDefaults (iOS)

### 2. Add Slider in Settings

- New slider control in the Appearance section: **"Keyboard Height"**
- Labeled endpoints: "Compact" (left) / "Default" (center) / "Large" (right)
- Show a live numeric percentage indicator (e.g., "110%")
- Discrete steps of 5% (0.75, 0.80, 0.85 … 1.45, 1.50)

### 3. Apply Height Scaling (Android)

- Read the scale preference in `MyInputMethodService` during view creation
- Apply the scale to the overall keyboard container height
- Joystick dials and preview bar should scale proportionally
- Top bar height can remain fixed or scale slightly (test which feels better)

### 4. Apply Height Scaling (iOS)

- Read the scale preference in `KeyboardViewController`
- Adjust the keyboard extension's `heightConstraint` or the SwiftUI frame
- iOS keyboard extensions have constraints on requesting height — respect the system's maximum and minimum bounds
- Test across iPhone SE, standard iPhone, and iPad

### 5. Live Preview (Optional but Recommended)

- If feasible, show a live preview of the keyboard at the selected height within the settings screen
- Otherwise, apply immediately and let users switch back to a text field to verify

---

## Acceptance Criteria

- [ ] Slider in settings allows adjusting keyboard height from 75% to 150% of default
- [ ] Default value is 100% (no change for existing users)
- [ ] Keyboard resizes correctly when preference changes
- [ ] Dial targets scale proportionally with keyboard height
- [ ] Preview bar and top row remain functional at all sizes
- [ ] Emoji panel respects the height adjustment
- [ ] Setting persists across keyboard restarts
- [ ] Works on both Android and iOS
- [ ] iOS respects system-imposed height constraints gracefully
- [ ] No layout clipping or overflow at extreme values

---

## Out of Scope

- Separate width adjustment or horizontal positioning
- Draggable/floating keyboard mode (separate ticket: would be a larger effort)
- Per-app height settings
- Adjustable dial size independent of overall height
