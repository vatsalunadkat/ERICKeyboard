# ERICK-166 - Double-Tap Shortcuts

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-77 Kotlin Shared Core |
| **Labels** | feature, typing-quality, shared, android, ios, settings |
| **Dependencies** | None. Extends the existing `KeyboardStateMachine` input processing logic |

---

## Objective

Add configurable double-tap shortcuts to ERICK, starting with the universal "double-space inserts period + space + auto-shift" pattern and expanding to other common shortcuts.

---

## Why This Matters

- "Double-space for period" is a near-universal keyboard convention that users expect. Its absence feels like a missing basic feature.
- For accessibility users, reducing multi-step operations (period → space → shift) to a single repeated gesture meaningfully reduces effort.
- Double-tap shortcuts align with ERICK's design philosophy of reducing precise multi-target interactions.

---

## Current Surfaces To Build On

### Shared
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` — `fireChord()`, single-swipe handling, input timing
- `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` — `InputAction`

### Android
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` — `commitText()`, `sendInputAction()`
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt`

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` — input commitment
- `ios/ERICK/ErickKeyBoard/SettingsView.swift`

---

## Proposed Scope

### 1. Add Double-Space → Period Shortcut (Shared Logic)

Implement in `KeyboardStateMachine`:

- Track the timestamp and action of the last committed input
- If a space is committed within 300ms of a previous space commit:
  - Delete the first space (backspace 1)
  - Insert `. ` (period + space)
  - Auto-shift for the next character (if auto-capitalize is enabled, ERICK-163)
- The threshold (300ms) should be a constant that can be adjusted later

### 2. Add Timing Detection Framework

- Add a lightweight "last action" tracker to the state machine that records:
  - What was committed (space, character, action)
  - When it was committed (timestamp)
- This framework enables future double-tap shortcuts beyond just space

### 3. Add Settings Toggle

- New toggle: **"Double-space for period"** — default ON
- Place in the Input/Typing section of settings
- When OFF, double-space simply inserts two spaces as before

### 4. (Future-Ready) Configurable Shortcut Slots

Design the implementation so additional shortcuts can be added later:
- Double-period → ellipsis (…)
- Double-comma → semicolon
- Double-enter → paragraph break

These are NOT in scope for the first pass but the architecture should not preclude them.

### 5. Platform Parity

Both Android and iOS should exhibit identical timing behavior since the logic lives in the shared state machine.

---

## Acceptance Criteria

- [ ] Double-tapping space within 300ms replaces the first space with `. ` (period + space)
- [ ] Auto-shift triggers after double-space period if auto-capitalize is enabled
- [ ] Single space followed by a pause (>300ms) and another space produces two spaces normally
- [ ] Double-space shortcut can be toggled off in settings
- [ ] Default is ON for new installs
- [ ] Works in both 8-section and 6-section modes
- [ ] Works with all three input modes (Quick Type, Steady Type, One-Handed)
- [ ] Works identically on Android and iOS
- [ ] Does not interfere with accelerating backspace or other timing-sensitive features

---

## Out of Scope

- Additional double-tap shortcuts beyond double-space (follow-up ticket)
- Configurable timing threshold in settings (keep as internal constant for now)
- Triple-tap detection
