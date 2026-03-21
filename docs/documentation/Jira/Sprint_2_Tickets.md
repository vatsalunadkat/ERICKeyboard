# ERICK — Sprint 2 Tickets

**Sprint**: SCRUM Sprint 2  
**Start Date**: March 2, 2026 (Monday)  
**End Date**: March 6, 2026 (Friday)  
**Project**: ERICK

---

## ERICK-40 — Design App Logo and Finalize Project Typography

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Low |
| **Story Points** | 2 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | — |
| **Dependencies** | None |

### Description

Create a logo for the ERICK keyboard app and select the typography (fonts) to be used consistently across the app and any supporting materials.

### Acceptance Criteria

- [ ] Logo designed with light and dark versions
- [ ] Typography (font family and sizes) defined for headings, body, and key labels
- [ ] All source files and exports (PNG, SVG) added to the GitHub repository
- [ ] Logo and font choices documented in the UI/UX Specification template

---

## ERICK-47 — Implement Android Chord Keyboard — Full Logical Layout

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 7 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | — |
| **Related** | ERICK-48, ERICK-57 |

### Description

Integrate the Sprint 1 joystick library PoC with the existing 2-button IME prototype to deliver a fully functional chord keyboard.

**HOW THE INPUT SYSTEM WORKS:**

- User swipes the **LEFT dial** to select a character group (direction = group)
- User swipes the **RIGHT dial** to select a color (color = position within group)
- Chord fires on simultaneous release: left-direction + right-color → character injected

**LEFT DIAL — LOGICAL LAYOUT (Normal mode):**
```
N: a b c d e '
NE: f g h i j /
E: k l m n o ;
SE: p q r s t -
S: u v w x y =
SW: z \ [ ] `
W: 1 2 3 4 5
NW: 6 7 8 9 0
```

**LEFT DIAL — SHIFT/CAPS LOCK mode:**
```
N: A B C D E "
NE: F G H I J ?
E: K L M N O :
SE: P Q R S T _
S: U V W X Y +
SW: Z | { } ~
W: ! @ # $ %
NW: ^ & * ( )
```

**RIGHT DIAL — COLOR → CHARACTER POSITION:**
```
Red=1st, Orange=2nd, Yellow=3rd, Green=4th, Blue=5th, Black=6th/symbol
(Indigo and Violet are unused for now — reserved for future use)
```

**RIGHT DIAL ONLY — SINGLE SWIPE (utility):**
```
N: Home (Shift → End)
NE: Comma (Shift → <)
E: Spacebar
SE: Full stop / Period (Shift → >)
S: Enter / New line
SW: Toggle Shift
W: Backspace
NW: Toggle Caps Lock
```

**RIGHT DIAL ONLY — DOUBLE SWIPE (navigation):**
```
N: Up arrow     NE: Page Up
E: Right arrow  SE: Page Down
S: Down arrow   SW: Delete key
W: Left arrow   NW: Tab
```

**SHIFT BEHAVIOUR:** Shift auto-releases after exactly one character is typed. Caps Lock stays on until explicitly toggled off.

### Acceptance Criteria

- [ ] All 8 left-dial directions correctly detected (≥90% accuracy in 20-chord manual test)
- [ ] All 8 active right-dial colors correctly identified (Red, Orange, Yellow, Green, Blue, Indigo, Violet, Black)
- [ ] Every character in the logical layout is typeable via chord and injects correctly system-wide
- [ ] Shift auto-releases after 1 character; Caps Lock persists until toggled
- [ ] All 8 single-swipe right-only utility functions work in any app
- [ ] Optional — All 8 double-swipe right-only navigation functions work in any app
- [ ] No ANR or crash during 4-minute continuous use
- [ ] Tested on physical Android device (API 33+) and emulator

---

## ERICK-48 — Android Settings Menu — Basic Shell with Layout Switcher

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Low |
| **Story Points** | 2 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | — |
| **Dependencies** | ERICK-47 |

### Description

Build the settings screen for the Android IME, accessible from a settings icon on the keyboard surface.

Sprint 2 scope: shell + layout switcher UI only. The Efficiency layout does not exist yet — wire the toggle so it's ready to activate in Sprint 3.

### Acceptance Criteria

- [ ] Settings screen accessible from keyboard surface (long-press or dedicated icon)
- [ ] Two-option layout toggle: "Logical (A–Z)" and "Efficiency (coming soon)"
- [ ] "Efficiency" option is visible but disabled with a "Coming in Sprint 3" label
- [ ] "Logical" selected by default
- [ ] Selection persisted via DataStore across reboots
- [ ] Screen dismisses cleanly and keyboard resumes
- [ ] No crashes on open/close/rotate

---

## ERICK-49 — Implement iOS Chord Keyboard — Full Logical Layout

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 7 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | — |
| **Related** | ERICK-50 |

### Description

Build the iOS Custom Keyboard Extension (Swift/SwiftUI) with the same full chord input system as the Android version. All character injection is done via `UITextDocumentProxy`.

**LEFT DIAL — LOGICAL LAYOUT (Normal mode):**
```
N: a b c d e '
NE: f g h i j /
E: k l m n o ;
SE: p q r s t -
S: u v w x y =
SW: z \ [ ] `
W: 1 2 3 4 5
NW: 6 7 8 9 0
```

**LEFT DIAL — SHIFT/CAPS LOCK mode:**
```
N: A B C D E "
NE: F G H I J ?
E: K L M N O :
SE: P Q R S T _
S: U V W X Y +
SW: Z | { } ~
W: ! @ # $ %
NW: ^ & * ( )
```

**RIGHT DIAL — COLOR → CHARACTER POSITION:**
```
Red=1st, Orange=2nd, Yellow=3rd, Green=4th, Blue=5th, Black=6th/symbol
(Indigo and Violet are unused for now — reserved for future use)
```

**RIGHT DIAL ONLY — SINGLE SWIPE (utility):**
```
N: Home (Shift → End)
NE: Comma (Shift → <)
E: Spacebar
SE: Full stop / Period (Shift → >)
S: Enter / New line
SW: Toggle Shift
W: Backspace
NW: Toggle Caps Lock
```

**RIGHT DIAL ONLY — DOUBLE SWIPE (navigation):**
```
N: Up arrow     NE: Page Up
E: Right arrow  SE: Page Down
S: Down arrow   SW: Delete key
W: Left arrow   NW: Tab
```

**SHIFT BEHAVIOUR:** Shift auto-releases after exactly one character is typed. Caps Lock stays on until explicitly toggled off.

### Acceptance Criteria

- [ ] Keyboard extension declared in `Info.plist` and selectable in iOS Settings → General → Keyboard → Add New Keyboard
- [ ] All 8 left-dial directions correctly detected (≥90% accuracy in manual test)
- [ ] All 8 active right-dial colors correctly identified
- [ ] Every character in the logical layout injects correctly into any iOS app via `UITextDocumentProxy`
- [ ] Shift auto-releases after 1 character; Caps Lock persists until toggled
- [ ] All 8 single-swipe right-only utility functions work
- [ ] Optional — All 8 double-swipe right-only navigation functions work
- [ ] Tested on physical iPhone and iOS Simulator (iOS 16+)
- [ ] No crashes during 5-minute test

---

## ERICK-66 — Restructure Project for Multi-Platform Support (Android & iOS)

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Highest |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-39 Project Infrastructure |
| **Labels** | — |
| **Dependencies** | None |

### Description

Reorganize the project repository to support both Android and iOS development with separate platform-specific folders and configurations.

**Technical Details:**

- Android build artifacts (`.gradle`, `.kotlin`, `.idea`) relocated to `android/` folder
- Created iOS-specific `.gitignore` for Xcode/Swift projects
- Root directory now contains only shared resources (documentation, LICENSE, README)

### Acceptance Criteria

- [ ] Android project builds successfully from `android/` folder
- [ ] All Android IDE settings preserved in new location
- [ ] iOS folder structure created with proper `.gitignore`
- [ ] Documentation updated to reflect new structure
- [ ] Root directory contains only shared resources
