# ERICK — Sprint 2 Tickets

**Sprint**: SCRUM Sprint 2  
**Start Date**: March 2, 2026 (Monday)  
**End Date**: March 6, 2026 (Friday)  
**Project**: ERICK - Agile Methods  

**Team**:
- **Android Keyboard** — Khair Muhammad, Nazgul Engvall
- **iOS Keyboard** — Xingxing Yang
- **Game Development (Godot)** — Vilgot Mattsson, Adi, Taimoor Athar Malik, Angeliki Paneri, Irshad
- **Project Infrastructure / Product Owner** — Vatsal Unadkat

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
| **Assignee** | Khair Muhammad |
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
| **Assignee** | Nazgul Engvall |
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
| **Assignee** | Xingxing Yang |
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

## ERICK-52 — Core Typing Engine — Word Display, Input Detection, and Match System

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Highest |
| **Story Points** | 5 |
| **Assignee** | Taimoor Athar Malik |
| **Parent Epic** | ERICK-43 Game Development |
| **Labels** | Group_2 |
| **Dependencies** | None |

### Description

Getting started with Godot: [https://youtu.be/LOhfqjmasi0](https://youtu.be/LOhfqjmasi0) (Watch first 20 min)

YouTube Tutorial: [https://www.youtube.com/playlist?list=PLpwc3ughKbZcJq-Sxew6OippNqlVc924q](https://www.youtube.com/playlist?list=PLpwc3ughKbZcJq-Sxew6OippNqlVc924q)

Code from similar game: [https://github.com/luis-l/SpaceJusticiar](https://github.com/luis-l/SpaceJusticiar)

---

Implement the decoupled typing mechanic that all other game systems build on. Words or letters appear on screen above any target node. The engine detects keyboard input and signals completion — it knows nothing about game state.

**Word prompt display:**
- `WordPrompt.tscn`: a Label (or RichTextLabel) positioned above a given Node2D
- Shows target word with typed portion highlighted (e.g. bold or colour change)

**Input matching:**
- Listens to `_input()` or a connected `InputHandler` singleton
- Correct character: advances match index, updates highlight
- Wrong character: flashes red / brief shake, does not advance
- Word fully matched: emits `word_completed(word_id: String)` signal
- Supports both keyboard (for development) and the custom IME input stream

**Word pool:**
- Words and letters loaded from `res://data/word_list.json`
- JSON format: `{"letters": ["a","b",...], "easy": ["cat","dog",...], "medium": [...], "hard": [...]}`
- At least 26 single letters + 30 words across difficulty tiers

### Acceptance Criteria

- [ ] `WordPrompt` scene displays any given string above a Node2D anchor
- [ ] Correct typing highlighted character by character in real time
- [ ] Wrong character triggers visible error state (does not advance)
- [ ] `word_completed` signal fires with correct `word_id` on full match
- [ ] Word list loadable from JSON; missing file shows a graceful error, not a crash
- [ ] System has zero dependency on game state, customer logic, or scene layout
- [ ] Testable in an isolated test scene (`TestTyping.tscn` committed to repo)

---

## ERICK-53 — Main Menu, Level Select, and In-Game Settings Screens

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Irshad |
| **Parent Epic** | ERICK-43 Game Development |
| **Labels** | — |
| **Dependencies** | None |

### Description

Getting started with Godot: [https://youtu.be/LOhfqjmasi0](https://youtu.be/LOhfqjmasi0) (Watch first 20 min)

How to build a menu screen: [https://youtu.be/zHYkcJyE52g](https://youtu.be/zHYkcJyE52g)

---

Build all navigation screens for the game. Gameplay integration comes later — focus is on correct scene structure and full navigation flow.

**Screens:**

1. **`MainMenu.tscn`** — Title, [Play] button → LevelSelect, [Settings] button → Settings, [Quit] button
2. **`LevelSelect.tscn`** — Shows Level 1 (unlocked), placeholder Level 2+ (locked/greyed). [Level 1] → LaundryStore scene. [Back] → MainMenu
3. **`Settings.tscn`** — Difficulty selector (5 options, radio buttons or OptionButton). Current difficulty read/written via `GameConfig` autoload. [Back] → MainMenu

### Acceptance Criteria

- [ ] All 3 scenes exist and load without errors
- [ ] Full navigation loop works: Main → Level Select → (back) → Settings → (back) → Main
- [ ] Difficulty selection in Settings updates `GameConfig.current_difficulty` immediately
- [ ] Level 1 button launches the LaundryStore scene (or placeholder scene if not ready)
- [ ] Locked levels shown but not clickable
- [ ] No crashes on any navigation path including rapid clicking
- [ ] Scene transitions use a simple fade or instant cut (no black screen hang)

---

## ERICK-54 — Customer and Laundry Workflow State Machine + Money System

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Highest |
| **Story Points** | 7 |
| **Assignee** | Vilgot Mattsson |
| **Parent Epic** | ERICK-43 Game Development |
| **Labels** | Group_2 |
| **Dependencies** | None |

### Description

Getting started with Godot: [https://youtu.be/LOhfqjmasi0](https://youtu.be/LOhfqjmasi0) (Watch first 40 min)

Check other tutorials on YouTube to understand game states and use AI to guide you to code.

---

Implement the core game loop as a GDScript state machine (or Godot StateMachine node). One customer completes the full laundry workflow per cycle. Typing prompts are triggered via the typing engine's signals; use a stub/mock for early development before ERICK-52 is integrated.

**State flow:**
```
IDLE
→ CUSTOMER_ARRIVES (customer walks in, drops laundry — triggered by customer arrival event)
→ AWAITING_PICKUP (word prompt: player types word to pick up laundry basket)
→ AWAITING_MACHINE_DROP (word prompt: player types word to put basket in machine)
→ MACHINE_WASHING (auto-timer: 10s for basic machine; emits wash_complete when done)
→ AWAITING_MACHINE_PICKUP (word prompt: player types word to remove from machine)
→ AWAITING_SHELF_PLACE (word prompt: player types word to put on shelf)
→ AWAITING_CUSTOMER_RETURN (waits for customer return event, triggered by a timer or game clock)
→ AWAITING_SERVE (word prompt: player types word to hand laundry to customer)
→ MONEY_COLLECTED (reward added, money_earned signal emitted)
→ IDLE
```

**Money system:**
- Fixed reward per completed customer (default: 10 coins, configurable)
- Running total stored in a `GameState` autoload
- `money_updated(new_total: int)` signal emitted on every change

### Acceptance Criteria

- [ ] All 9 states implemented with correct transitions
- [ ] Each `AWAITING_` state: emits a `prompt_needed(word: String, state_id: String)` signal; waits for `word_completed`
- [ ] `MACHINE_WASHING`: 10s countdown timer, emits `wash_complete` on finish
- [ ] `MONEY_COLLECTED`: adds reward to `GameState.money`, emits `money_updated`
- [ ] State machine is decoupled from animation — emits events, does not move characters directly
- [ ] Full workflow runs end-to-end in an isolated test scene
- [ ] Current state logged to Godot output at every transition for debugging
- [ ] Money total visible in a HUD Label in the test scene

---

## ERICK-55 — Build Laundry Store Main Game Scene in Godot

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Adi |
| **Parent Epic** | ERICK-43 Game Development |
| **Labels** | — |
| **Dependencies** | None |

### Description

Getting started with Godot: [https://youtu.be/LOhfqjmasi0](https://youtu.be/LOhfqjmasi0)

YouTube Tutorial: [https://www.youtube.com/playlist?list=PLpwc3ughKbZcJq-Sxew6OippNqlVc924q](https://www.youtube.com/playlist?list=PLpwc3ughKbZcJq-Sxew6OippNqlVc924q)

Code from similar game: [https://github.com/luis-l/SpaceJusticiar](https://github.com/luis-l/SpaceJusticiar)

---

Set up the primary in-game scene (`LaundryStore.tscn`) for the laundry store. Use assets or placeholder coloured rectangles. Define all spatial anchor points the backend state machine will reference.

Scene must include named Node2D markers at:
- `CustomerEntrance` — where customer spawns
- `DropOffPoint` — where customer drops laundry
- `MachineSlot_1` — position of washing machine 1
- `ShelfSlot_1` — laundry shelf position
- `PickupCounter` — where customer is served at the end
- `CameraRoot` — camera anchor at correct zoom/position

**Asset Links:**
- [https://github.com/godotengine/awesome-godot](https://github.com/godotengine/awesome-godot)
- [https://www.reddit.com/r/godot/comments/1ixujq0/my_big_list_of_godot_resources_both_free_and_paid/](https://www.reddit.com/r/godot/comments/1ixujq0/my_big_list_of_godot_resources_both_free_and_paid/)
- [https://itch.io/search?type=games&q=laundry&classification=assets](https://itch.io/search?type=games&q=laundry&classification=assets)
- [https://www.kenney.nl/assets](https://www.kenney.nl/assets)
- [https://www.openpixelproject.com/?page_id=227](https://www.openpixelproject.com/?page_id=227)
- [https://jashi-psx.itch.io/public-laundry-psx-asset-pack](https://jashi-psx.itch.io/public-laundry-psx-asset-pack)

### Acceptance Criteria

- [ ] `LaundryStore.tscn` opens and runs without errors in Godot editor
- [ ] All 6 named marker nodes present with correct positions
- [ ] Background and environment filled with assets or clearly labelled placeholders
- [ ] Target resolution decided and documented (suggest 1920×1080 landscape)
- [ ] Camera correctly frames the full scene at target resolution
- [ ] Scene committed to `feature/game-godot` branch
- [ ] A README note added explaining the scene node structure

---

## ERICK-56 — Implement Customer Walk-In, Idle, and Walk-Out Animations with Path Following

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 4 |
| **Assignee** | Angeliki Paneri |
| **Parent Epic** | ERICK-43 Game Development |
| **Labels** | — |
| **Dependencies** | None |

### Description

Getting started with Godot: [https://youtu.be/LOhfqjmasi0](https://youtu.be/LOhfqjmasi0)

YouTube Tutorial: [https://www.youtube.com/playlist?list=PLpwc3ughKbZcJq-Sxew6OippNqlVc924q](https://www.youtube.com/playlist?list=PLpwc3ughKbZcJq-Sxew6OippNqlVc924q)

Animation Tutorial: [https://youtu.be/-f1bHR0iiEY](https://youtu.be/-f1bHR0iiEY)

---

Find CC0/MIT-licensed 2D character sprites suitable for laundry store customers. Must include frames for idle and walking, or at minimum a static sprite usable as a placeholder.

Implement the customer character scene in Godot using `Path2D` + `PathFollow2D` for movement and `AnimatedSprite2D` for animations. Two visit paths are needed: drop-off visit and pickup visit.

**Walk paths:**
- **Drop-off visit**: CustomerEntrance → DropOffPoint (plays walk), idle at DropOffPoint, DropOffPoint → CustomerEntrance (plays walk-out)
- **Pickup visit**: CustomerEntrance → PickupCounter (plays walk), idle at PickupCounter, PickupCounter → CustomerEntrance (plays walk-out)

**Asset Links:**
- [https://github.com/godotengine/awesome-godot](https://github.com/godotengine/awesome-godot)
- [https://www.reddit.com/r/godot/comments/1ixujq0/my_big_list_of_godot_resources_both_free_and_paid/](https://www.reddit.com/r/godot/comments/1ixujq0/my_big_list_of_godot_resources_both_free_and_paid/)
- [https://itch.io/search?type=games&q=laundry&classification=assets](https://itch.io/search?type=games&q=laundry&classification=assets)
- [https://www.kenney.nl/assets](https://www.kenney.nl/assets)
- [https://www.openpixelproject.com/?page_id=227](https://www.openpixelproject.com/?page_id=227)
- [https://jashi-psx.itch.io/public-laundry-psx-asset-pack](https://jashi-psx.itch.io/public-laundry-psx-asset-pack)

### Acceptance Criteria

- [ ] `Customer.tscn` created with `AnimatedSprite2D` using suitable sprites or a placeholder shape
- [ ] `walk_in` and `walk_out` animations play during path movement
- [ ] `idle` animation plays when customer is stopped at a marker point
- [ ] Drop-off and pickup path variants both work correctly
- [ ] At least 1 customer can complete a full drop-off and return-pickup cycle without errors
- [ ] Character z-index correct (appears in front of background, behind UI)
- [ ] Paths defined using `Path2D` nodes in the scene, not hardcoded coordinates

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
