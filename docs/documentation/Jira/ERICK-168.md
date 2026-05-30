# ERICK-168 - Dedicated Number Pad Mode

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, ui, shared, android, ios, accessibility, input-modes |
| **Dependencies** | None. Could optionally integrate with ERICK-170 (input type detection) if that ships first |

---

## Objective

Add a dedicated number pad overlay that replaces the dials with large, accessible numeric buttons for situations that require digit-heavy input (phone numbers, PINs, credit card numbers, calculations).

---

## Why This Matters

- Entering phone numbers, PINs, and numeric data with chord input is slow and unintuitive — numbers are placed in less prominent chord positions.
- A traditional numpad with large touch targets aligns with ERICK's accessibility-first design for digit-heavy contexts.
- Many users switch away from ERICK entirely for numeric input. A built-in numpad keeps them in the ERICK ecosystem.
- Large numpad buttons are especially important for users with motor limitations who find small number keys on standard keyboards difficult.

---

## Current Surfaces To Build On

### Shared
- `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` — `KeyboardMode`, `InputAction`
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` — mode transitions

### Android
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` — view switching (similar to emoji panel swap)
- `android/app/src/main/java/com/vatoo/erick/EmojiPanelView.kt` — reference for panel replacement pattern

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardContainerView.swift` — view swapping between dial and emoji
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift`

---

## Proposed Scope

### 1. Add Number Pad UI (Android)

A custom view that displays in the dial area (same pattern as emoji panel):

```
┌─────────────────────────────┐
│  [1]  [2]  [3]             │
│  [4]  [5]  [6]             │
│  [7]  [8]  [9]             │
│  [.]  [0]  [⌫]            │
└─────────────────────────────┘
```

- Large buttons with generous padding (accessibility target size 48dp minimum)
- Buttons fill the dial area proportionally
- Additional row or side buttons for: `+`, `-`, `*`, `/`, `#`, `(`, `)` accessible via a toggle or scroll
- Backspace button with the same accelerating-backspace behavior as the dial backspace
- Done/ABC button to return to the chord keyboard

### 2. Add Number Pad UI (iOS)

SwiftUI grid matching the same layout, replacing the JoystickView area.

### 3. Add Activation Method

Multiple ways to enter number pad mode:

- **Manual toggle**: A `123` button in the top bar (next to emoji button) when in typing mode
- **Optional auto-detection** (future): When the input field type is `number`, `phone`, or `decimal`, suggest or auto-show the numpad (separate follow-up)

### 4. Add Shared Mode Support

- Add `NUMPAD` to `KeyboardMode` enum (or handle as a UI-only overlay outside the mode system if simpler)
- The number pad should still use `delegate.commitText()` for digit output so the state machine and prediction remain aware

### 5. Top Bar Behavior in Numpad Mode

- Top bar stays visible (consistent with emoji panel behavior)
- Emoji button changes to `ABC` (or numpad shows its own `ABC` return button)
- Settings button remains accessible

### 6. Theming

- Number pad respects the active theme (Light / Dark / AMOLED Black)
- Button colors should be neutral but accessible
- Direction palette colors are NOT applied to numpad buttons (they're not directional)

---

## Acceptance Criteria

- [ ] Numpad mode is accessible via a button in the keyboard top bar
- [ ] Numpad displays digits 0-9 with large accessible touch targets
- [ ] Backspace works with accelerating behavior matching dial backspace
- [ ] Additional symbols (+, -, *, /, #, etc.) are accessible from numpad mode
- [ ] Decimal point (`.`) and comma (`,`) available for numeric entry
- [ ] `ABC` button returns to the chord keyboard
- [ ] Keyboard height does not change when entering numpad mode
- [ ] Numpad respects active theme and color scheme
- [ ] Works identically on Android and iOS
- [ ] Works in both 8-section and 6-section base modes (numpad replaces either)

---

## Out of Scope

- Calculator functionality (evaluating expressions)
- Auto-detection of numeric input fields (follow-up ticket)
- Numpad in controller mode (controller users still use chords for numbers)
- Custom numpad layouts
