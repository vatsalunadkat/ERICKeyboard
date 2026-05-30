# ERICK-163 - Auto-Capitalization

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
| **Dependencies** | None. Builds on the existing `KeyboardStateMachine` shift/caps logic and the platform `textBeforeCursor` access |

---

## Objective

Add smart auto-capitalization that automatically shifts the keyboard after sentence-ending punctuation and at the start of new input fields, with a user toggle in settings.

---

## Why This Matters

- Every mainstream keyboard auto-capitalizes after sentence boundaries. Users expect it.
- ERICK currently requires a manual shift swipe after every period, exclamation mark, or question mark to start the next sentence with a capital letter.
- For accessibility users who already find chord entry effortful, removing the extra shift step after every sentence meaningfully reduces fatigue.
- Auto-capitalization should be optional because some users (coders, casual texters) prefer all-lowercase.

---

## Current Surfaces To Build On

### Shared
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` — `fireChord()`, mode transitions, `handleTouch()`
- `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` — `KeyboardMode.SHIFTED`, `KeyboardActionDelegate`

### Android
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` — `getCurrentInputConnection().getTextBeforeCursor()`
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt`

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` — `textDocumentProxy.documentContextBeforeInput`
- `ios/ERICK/ErickKeyBoard/SettingsView.swift`

---

## Proposed Scope

### 1. Add Auto-Capitalize Logic to Shared State Machine

After a chord commits text or a suggestion is accepted, check whether the next character should be auto-capitalized:

- **Trigger conditions:**
  - After `.` `!` `?` followed by a space (or at the very end if the next input starts)
  - At the beginning of a new empty text field
  - After a newline/enter
- **Action:** Transition `KeyboardMode` to `SHIFTED` automatically
- **Reset:** Auto-shift clears after the first character is typed (same as manual shift)

### 2. Add Platform Context Queries

Both platforms already expose text-before-cursor. Wire the shared logic to query this context:

- Android: `InputConnection.getTextBeforeCursor()`
- iOS: `textDocumentProxy.documentContextBeforeInput`

### 3. Add Settings Toggle

- New toggle: **"Auto-Capitalize"** — default ON
- Place in the existing Input/Typing section of settings on both platforms
- When OFF, no automatic shift transitions occur

### 4. Respect Existing Manual Overrides

- If the user manually toggled shift OFF after an auto-capitalize trigger, do not re-shift
- If the user is in Caps Lock mode, auto-capitalize should not interfere
- In Symbols mode, auto-capitalize should not trigger

---

## Acceptance Criteria

- [ ] Keyboard auto-shifts to uppercase after `.` `!` `?` + space
- [ ] Keyboard auto-shifts at the start of an empty text field
- [ ] Keyboard auto-shifts after Enter/newline
- [ ] Auto-shift resets after typing one character (same as manual shift)
- [ ] Auto-capitalize can be toggled off in settings
- [ ] Auto-capitalize defaults to ON for new installs
- [ ] Does not interfere with Caps Lock or Symbols mode
- [ ] Works identically on Android and iOS
- [ ] Works in both 8-section and 6-section dial modes

---

## Out of Scope

- Auto-capitalize after proper noun detection (too complex for first pass)
- Language-specific capitalization rules (e.g., German noun capitalization)
- Auto-capitalize in custom layout mode (follow-up if needed)
