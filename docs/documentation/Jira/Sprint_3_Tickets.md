# ERICK — Sprint 3 Tickets

**Sprint**: SCRUM Sprint 3  
**Start Date**: March 9, 2026 (Monday)  
**End Date**: March 13, 2026 (Friday)  
**Project**: ERICK  

---

## ERICK-59 — Merge iOS Branch & Polish Keyboard Extension

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Highest |
| **Story Points** | 2 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, infrastructure |
| **Dependencies** | None (blocking all other iOS tickets) |

### Description

The iOS Xcode project, keyboard extension, and SharedKeyboard framework integration were completed in Sprint 2 on the `Implement_iOS_chord_keyboard` branch. The chord keyboard with the full Logical layout is functional. This ticket covers merging that branch into `main` and adding required polish items that are currently missing.

**What already exists on the `Implement_iOS_chord_keyboard` branch:**
- Xcode project at `ios/ERICK/` with app target (`ERICK`) and keyboard extension target (`ErickKeyBoard`)
- `SharedKeyboard.framework` integrated — `import SharedKeyboard` works
- `KeyboardViewController.swift` — full IME integration with `KeyboardStateMachine`, `KeyboardActionDelegate`, touch dispatch, text commit via `textDocumentProxy`
- `JoystickView.swift` — SwiftUI joystick with drag gesture, thumb clamping, spring return-to-center, preview text display
- `KeyboardContainerView` — horizontal layout of left + right joysticks with touch callback plumbing
- `Info.plist` configured for `com.apple.keyboard-service` extension
- Basic `ContentView.swift` and `ERICKApp.swift` (placeholder only — "Hello, world!")

**What is missing and needs to be added in this ticket:**
1. "Next Keyboard" (globe) button — **Apple requires this** for all third-party keyboards
2. Settings button (gear icon) on the keyboard surface
3. Set up an **App Group** (e.g., `group.com.vatoo.erick`) on both the app and extension targets so preferences can be shared
4. Update `Info.plist` to set `IsASCIICapable` to `true` (currently `false`)
5. Clean up `.gitignore` — the branch currently commits `xcuserstate` files which should be ignored
6. Merge the branch into `main`

### How to Get Started

1. **Merge the branch**:
   ```bash
   git checkout main
   git merge origin/Implement_iOS_chord_keyboard
   ```
   Resolve any conflicts (the branch diverged from main — documentation and android files may conflict).

2. **Add the "Next Keyboard" button** in `KeyboardViewController.swift`:
   - Apple requires a button that calls `advanceToNextInputMode()` to let users switch keyboards
   - Add a small globe icon button in the bottom-left corner of the keyboard:
   ```swift
   // In KeyboardContainerView, add a globe button:
   Button(action: { /* will be connected via callback */ }) {
       Image(systemName: "globe")
           .font(.system(size: 20))
           .foregroundColor(.gray)
   }
   ```
   - In `KeyboardViewController`, pass `advanceToNextInputMode` as the button's action

3. **Add a settings button** (gear icon) in the top-right corner of the keyboard:
   - This will eventually open the containing app's settings
   - For now, use `UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)` or open the containing app via URL scheme

4. **Set up App Group**:
   - In Xcode → ERICK target → Signing & Capabilities → + Capability → App Groups → add `group.com.vatoo.erick`
   - Repeat for the `ErickKeyBoard` extension target
   - This enables `UserDefaults(suiteName: "group.com.vatoo.erick")` for shared preferences

5. **Fix Info.plist**: Change `IsASCIICapable` from `false` to `true` (the keyboard supports ASCII input)

6. **Update `.gitignore`**: Add `*.xcuserstate` and `xcuserdata/` if not already covered

### Acceptance Criteria

- [ ] `Implement_iOS_chord_keyboard` branch merged into `main` without breaking Android or iOS builds
- [ ] "Next Keyboard" (globe) button present and calls `advanceToNextInputMode()` correctly
- [ ] Settings button (gear icon) visible on the keyboard surface
- [ ] App Group `group.com.vatoo.erick` configured on both app and extension targets
- [ ] `IsASCIICapable` set to `true` in keyboard extension Info.plist
- [ ] `.gitignore` updated to exclude `xcuserstate` and `xcuserdata/`
- [ ] App builds and runs on iOS Simulator and physical iPhone
- [ ] Chord keyboard still works correctly after merge (all characters typeable)

---

## ERICK-68 — iOS Keyboard Extension — Bug Fixes & Input Action Completeness

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 2 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, keyboard |
| **Dependencies** | ERICK-67 |

### Description

The iOS keyboard extension was implemented in Sprint 2 on the `Implement_iOS_chord_keyboard` branch. The core chord system works — two SwiftUI joysticks, `KeyboardStateMachine` integration via `SharedKeyboard` framework, and text injection via `textDocumentProxy`. This ticket covers testing, fixing any bugs found, and completing the `sendInputAction()` handler for all actions.

**What already works:**
- `KeyboardViewController` with `KeyboardActionDelegate` implementation
- `commitText()` → `textDocumentProxy.insertText()`
- `JoystickView` with drag gesture, thumb clamping, spring animation
- `KeyboardContainerView` with left/right joystick layout
- Actions handled: `.space`, `.enter`, `.backspace`, `.deleteForward`, `.tab`, `.dpadLeft`, `.dpadRight`

**What needs attention:**
1. Incomplete `sendInputAction()` — several actions fall through to `default: break`:
   - `.moveHome` — should move cursor to beginning of document (`adjustTextPosition` to start)
   - `.moveEnd` — should move cursor to end of document
   - `.dpadUp` / `.dpadDown` — limited on iOS but should have best-effort implementation
   - `.pageUp` / `.pageDown` — same limitation, document for future reference
2. Test all single-swipe actions thoroughly on a physical device
3. Test that Shift auto-releases after 1 character and Caps Lock persists
4. Verify `.deleteForward` behavior — current implementation uses `deleteBackward()` which is the same as backspace, not forward delete. Correct approach: `adjustTextPosition(byCharacterOffset: 1)` then `deleteBackward()`
5. Run a 5-minute continuous use stability test

### How to Get Started

1. **Open `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift`**

2. **Fix the `sendInputAction()` method**:
   ```swift
   func sendInputAction(action: InputAction) {
       switch action {
       case .space:
           self.textDocumentProxy.insertText(" ")
       case .enter:
           self.textDocumentProxy.insertText("\n")
       case .backspace:
           self.textDocumentProxy.deleteBackward()
       case .deleteForward:
           // Move cursor right, then delete backward = forward delete
           self.textDocumentProxy.adjustTextPosition(byCharacterOffset: 1)
           self.textDocumentProxy.deleteBackward()
       case .tab:
           self.textDocumentProxy.insertText("\t")
       case .dpadLeft:
           self.textDocumentProxy.adjustTextPosition(byCharacterOffset: -1)
       case .dpadRight:
           self.textDocumentProxy.adjustTextPosition(byCharacterOffset: 1)
       case .moveHome:
           // Move to beginning — move left by a large amount
           if let before = self.textDocumentProxy.documentContextBeforeInput {
               self.textDocumentProxy.adjustTextPosition(byCharacterOffset: -before.count)
           }
       case .moveEnd:
           // Move to end — move right by a large amount
           if let after = self.textDocumentProxy.documentContextAfterInput {
               self.textDocumentProxy.adjustTextPosition(byCharacterOffset: after.count)
           }
       case .toggleShift, .toggleCaps:
           // Handled internally by state machine — no iOS action needed
           break
       default:
           // .dpadUp, .dpadDown, .pageUp, .pageDown — limited iOS support
           break
       }
   }
   ```

3. **Test matrix** — verify each of these works in Notes app and Safari:
   - All 26 lowercase letters via chord
   - All 26 uppercase letters via Shift + chord
   - All numbers (0-9) and symbols
   - Single-swipe: Space, Enter, Backspace, Comma, Period, Home, End
   - Toggle Shift (auto-release after 1 char)
   - Toggle Caps Lock (persists)

### Acceptance Criteria

- [ ] All `InputAction` cases handled in `sendInputAction()` (no silent `default: break` for supported actions)
- [ ] Forward delete works correctly (moves cursor right, then deletes backward)
- [ ] Home/End move cursor to start/end of text
- [ ] All characters from the Logical layout typeable and inject correctly
- [ ] Single-swipe actions all work: Space, Enter, Backspace, Home, End, Comma, Period, Toggle Shift, Toggle Caps
- [ ] Shift auto-releases after 1 character; Caps Lock persists until toggled
- [ ] No crashes during 5-minute continuous use
- [ ] Tested on iOS Simulator and physical iPhone

---

## ERICK-69 — iOS Main App — Onboarding & Keyboard Setup Flow

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, onboarding |
| **Dependencies** | ERICK-67 |

### Description

Build the main app screen for the iOS ERICK app that guides users through enabling the custom keyboard. This replicates the Android `MainActivity.kt` onboarding flow for iOS.

On iOS, users must enable third-party keyboards manually via Settings → General → Keyboard → Keyboards → Add New Keyboard. The app should guide them through this process.

**Note:** A `ContentView.swift` file already exists on the `Implement_iOS_chord_keyboard` branch but it only contains a placeholder "Hello, world!" — you need to replace its contents entirely.

### How to Get Started

1. **Read the Android reference**: Open `android/app/src/main/java/com/vatoo/erick/MainActivity.kt` to understand the onboarding flow. The iOS version should have the same sections and content, adapted for iOS.

2. **Replace the contents of `ContentView.swift`** (in `ios/ERICK/ERICK/ContentView.swift`) with these sections:

   **Header**: ERICK logo + app name + tagline "A radial chorded keyboard for everyone"

   **Step 1 Card — Enable ERICK Keyboard**:
   - Instruction: "Go to Settings → General → Keyboard → Keyboards → Add New Keyboard → ERICKeyboard"
   - Button: "Open Settings" → opens `UIApplication.openSettingsURL`
   - Status indicator: green checkmark if enabled, red X if not
   - To detect if keyboard is enabled, check if the bundle ID appears in the list of enabled keyboards (there is no perfect API for this on iOS — use a best-effort approach or simply use a manual "I've done this" toggle)

   **Step 2 Card — Switch to ERICK**:
   - Instruction: "When typing, tap the globe 🌐 icon on the keyboard to switch to ERICK"
   - This cannot be automated on iOS — just show instructions

   **Privacy & Security Card**:
   - Same content as Android:
     - "We never collect or store your typed text"
     - "No passwords or personal data are saved"
     - "No data is transmitted — ever"
     - "Settings are stored locally on your device only"
     - "No internet permissions requested"
     - "100% open-source: inspect every line of code"

   **Tips Section**:
   - "Use the left joystick to select a character group"
   - "Use the right joystick to select a character within the group"
   - "Swipe the right joystick alone for utility functions (space, enter, backspace)"

   **Settings Button**: NavigationLink to the Settings screen

   **Test Field**: A `TextField` at the bottom where users can test typing with ERICK

3. **Use the ERICK color scheme**: Match the Android Material Design 3 purple theme as closely as possible in SwiftUI.

### Acceptance Criteria

- [ ] Main app screen shows onboarding steps when the app is opened
- [ ] "Open Settings" button opens iOS Settings app
- [ ] Privacy & Security section displays all 6 privacy commitments
- [ ] Tips section explains joystick usage
- [ ] Settings button navigates to the settings screen
- [ ] Test TextField present for user verification
- [ ] ERICK logo displayed in the header
- [ ] Layout looks good on iPhone SE, iPhone 15, and iPhone 15 Pro Max (different screen sizes)
- [ ] No crashes on any navigation path

---

## ERICK-70 — iOS Settings Screen with Layout Switcher & Preferences

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, settings |
| **Dependencies** | ERICK-67 |

### Description

Build the iOS Settings screen mirroring the Android `SettingsScreen.kt`. Use SwiftUI with `UserDefaults` (or `@AppStorage`) for preference persistence. Settings must be identical to the Android version.

**Note:** The iOS project already exists at `ios/ERICK/`. You will add new Swift files to the `ios/ERICK/ERICK/` folder (the main app target). The App Group should already be set up from ERICK-67 — use `UserDefaults(suiteName: "group.com.vatoo.erick")` for persistence.

### How to Get Started

1. **Read the Android reference**: Open `android/app/src/main/java/com/vatoo/erick/SettingsScreen.kt` and `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt`.

2. **Create `SettingsView.swift`** (a new file in `ios/ERICK/ERICK/`) with these sections:

   **Layout Section** ("Keyboard Layout"):
   - Radio button group (Picker with `.radioGroup` style or custom Toggle list):
     - "Logical (A–Z)" — selected by default
     - "Efficiency (Coming in Sprint 3)" — visible but disabled, with a gray label "Coming soon"
   - Store selection in `@AppStorage("keyboard_layout")` with default "LOGICAL"

   **Appearance Section** ("Appearance"):
   - Toggle: "Dark Theme" — `@AppStorage("dark_theme")` default false

   **Accessibility Section** ("Accessibility"):
   - Toggle: "Colorblind Mode" — `@AppStorage("colorblind_mode")` default false
   - Toggle: "Left-Handed Mode" — `@AppStorage("left_handed_mode")` default false

   **Privacy & Security Card**: Same privacy statements as the main screen

3. **Use `@AppStorage`** for persistence — this uses `UserDefaults` under the hood and is the simplest approach in SwiftUI.

4. **Important**: For the keyboard extension to read these preferences, you need to use an **App Group**. Set up an App Group (e.g., `group.com.vatoo.erick`) on both the app target and the keyboard extension target. Use `UserDefaults(suiteName: "group.com.vatoo.erick")` instead of standard `UserDefaults.standard` so both the app and extension can share settings.

5. **Navigation**: Add a "Back" or dismissal mechanism (NavigationStack with back button, or `.dismiss()` for modal presentation).

### Acceptance Criteria

- [ ] Settings screen accessible from the main app
- [ ] Layout toggle shows "Logical (A–Z)" selected by default
- [ ] "Efficiency" option visible but disabled with "Coming soon" label
- [ ] Dark Theme, Colorblind Mode, Left-Handed Mode toggles present
- [ ] All preferences persist across app restarts (via App Group UserDefaults)
- [ ] Keyboard extension can read the preferences using the shared App Group
- [ ] Privacy & Security section visible
- [ ] Screen dismisses cleanly
- [ ] No crashes on open/close/rotate

---

## ERICK-71 — iOS App Logo, Branding & Asset Integration

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Low |
| **Story Points** | 1 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | iOS, branding |
| **Dependencies** | ERICK-67 |

### Description

Add the ERICK logo and branding assets to the iOS app, matching the Android app's visual identity.

### How to Get Started

1. **Locate existing assets**:
   - Logo source files: `documentation/logo/`
   - Android logo: `android/app/src/main/res/drawable/erick_logo.png`
   - Android launcher icons: `android/app/src/main/res/mipmap-*/ic_launcher.webp`

2. **Add the app icon**:
   - Open `Assets.xcassets` in Xcode
   - Open the `AppIcon` asset
   - Generate required icon sizes from the logo (1024×1024 source):
     - iPhone: 60pt @2x (120px), @3x (180px)
     - iPad: 76pt @2x (152px), 83.5pt @2x (167px)
     - App Store: 1024×1024px
   - You can use an online tool like appicon.co to generate all sizes from a single PNG

3. **Add the ERICK logo** to `Assets.xcassets`:
   - Create a new Image Set called `erick_logo`
   - Add @1x, @2x, @3x versions of the logo PNG
   - Reference it in SwiftUI: `Image("erick_logo")`

4. **Set the display name**:
   - In the app target → General → Display Name: "ERICKeyboard"
   - In the keyboard extension target → General → Display Name: "ERICKeyboard"

5. **Match the color scheme**: Define ERICK brand colors in `Assets.xcassets` as Color Sets:
   - Primary Purple: `#6650a4` (light) / `#D0BCFF` (dark)
   - Secondary: `#625b71` (light) / `#CCC2DC` (dark)

### Acceptance Criteria

- [ ] App icon appears correctly on iOS home screen
- [ ] ERICK logo displayed on the onboarding screen
- [ ] App display name shows "ERICKeyboard" on home screen and in Settings
- [ ] Keyboard name shows "ERICKeyboard" in the keyboard switcher
- [ ] Brand colors consistent with Android app
- [ ] Assets added to `Assets.xcassets` with proper resolution variants

---

## ERICK-72 — Remove Double Swipe Right Dial Binds from Shared Logic

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 2 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, cleanup |
| **Dependencies** | None |

### Description

Remove all double-swipe functionality from the shared keyboard logic. The double-swipe detection (250ms window) and all double-swipe key bindings (arrow keys, Page Up/Down, Delete, Tab) should be removed entirely. The keyboard will only support chord input (both dials) and single-swipe (right dial only).

### How to Get Started

1. **Open `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt`**:
   - Remove the `singleSwipeJob` coroutine timer used for 250ms double-tap detection
   - Remove the `lastRightSwipeTime` / `pendingRightSwipe` tracking variables
   - Simplify `handleRightOnlySwipe()` to immediately execute the single-swipe action (no waiting for potential double-tap)
   - Remove the `isDoubleSwipe` logic path entirely

2. **Open `android/shared/src/commonMain/kotlin/KeyboardLogic.kt`**:
   - Remove `getDoubleSwipeKeyCode()` method entirely
   - Remove the double swipe map (N→UP, NE→PAGE_UP, E→RIGHT, SE→PAGE_DOWN, S→DOWN, SW→DELETE, W→LEFT, NW→TAB)
   - Keep the single-swipe map intact (Home, Comma, Space, Period, Enter, Shift, Backspace, Caps Lock)

3. **Open `android/shared/src/commonMain/kotlin/KeyboardContracts.kt`**:
   - Remove any `InputAction` enum values that were exclusively used by double-swipe if applicable (but keep DPAD_UP/DOWN/LEFT/RIGHT, PAGE_UP/DOWN, TAB, DELETE_FORWARD as they may be needed for other features like controller support later — document that they are retained for future use)

4. **Open `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt`**:
   - Remove any Android-specific double-swipe handling code
   - Verify single-swipe actions still work after the change

5. **Test**: Run the app on Android emulator/device and verify:
   - Single swipe right-only actions all work correctly
   - No 250ms delay before single-swipe actions execute (they should now be instant)
   - Chord input still works as before

### Acceptance Criteria

- [ ] Double-swipe detection code removed from `KeyboardStateMachine.kt`
- [ ] Double-swipe map removed from `KeyboardLogic.kt`
- [ ] Single-swipe actions execute immediately (no 250ms delay)
- [ ] All 8 single-swipe right-only functions still work: Home, Comma, Space, Period, Enter, Toggle Shift, Backspace, Toggle Caps Lock
- [ ] Chord input (both dials) still works correctly for all characters
- [ ] Shared module compiles for both Android and iOS targets
- [ ] No crashes during 3-minute continuous use test

---

## ERICK-57 — Implement Efficiency Layout in Shared Module

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, layout |
| **Dependencies** | None |

### Description

Add the "Efficiency" layout to the shared Kotlin Multiplatform module. This layout optimizes character placement based on English letter frequency — the most common letters are placed on the easiest-to-reach chord positions (cardinal directions + Red/Orange colors).

The Efficiency layout replaces letter placement only. Numbers, symbols, and single-swipe utility functions remain identical to the Logical layout.

### Efficiency Layout Definition

**Letter Frequency Ranking** (English): e, t, a, o, i, n, s, h, r, d, l, c, u, m, w, f, g, y, p, b, v, k, j, x, q, z

**LEFT DIAL — EFFICIENCY LAYOUT (Normal mode)**:

| Direction | Red(1) | Orange(2) | Yellow(3) | Green(4) | Blue(5) | Black(6) |
|---|---|---|---|---|---|---|
| N | e | t | a | o | i | ' |
| NE | n | s | h | r | d | / |
| E | l | c | u | m | w | ; |
| SE | f | g | y | p | b | - |
| S | v | k | j | x | q | = |
| SW | z | \\ | [ | ] | \` | |
| W | 1 | 2 | 3 | 4 | 5 | |
| NW | 6 | 7 | 8 | 9 | 0 | |

**LEFT DIAL — SHIFT/CAPS LOCK mode**:

| Direction | Red(1) | Orange(2) | Yellow(3) | Green(4) | Blue(5) | Black(6) |
|---|---|---|---|---|---|---|
| N | E | T | A | O | I | " |
| NE | N | S | H | R | D | ? |
| E | L | C | U | M | W | : |
| SE | F | G | Y | P | B | _ |
| S | V | K | J | X | Q | + |
| SW | Z | \| | { | } | ~ | |
| W | ! | @ | # | $ | % | |
| NW | ^ | & | * | ( | ) | |

### How to Get Started

1. **Open `android/shared/src/commonMain/kotlin/KeyboardLogic.kt`**:
   - Add two new maps: `efficiencyNormalMap` and `efficiencyShiftedMap` following the same structure as `normalMap` and `shiftedMap`
   - Each map is `Map<Direction, List<String>>` with 8 directions → list of 5–6 characters

2. **Modify `getChordResult()` method**:
   - Accept a layout parameter (or access a layout configuration) to decide which map to use
   - Add a `LayoutType` enum: `LOGICAL`, `EFFICIENCY` (add to `KeyboardContracts.kt`)
   - When `EFFICIENCY` is selected, use the efficiency maps instead of the logical maps

3. **Update `KeyboardContracts.kt`**:
   - Add `enum class LayoutType { LOGICAL, EFFICIENCY }`
   - Update `KeyboardStateMachine` to accept and store the current layout type
   - Add a method `setLayoutType(type: LayoutType)` to switch at runtime

4. **Update `KeyboardStateMachine.kt`**:
   - Store `currentLayoutType: LayoutType`
   - Pass it to `getChordResult()` calls

5. **Update `getPreviewText()`** to also respect the current layout type.

6. **Test**: Verify both layouts produce correct characters by testing all 48 chord combinations (8 directions × 6 colors) in both NORMAL and SHIFTED modes.

### Acceptance Criteria

- [ ] `LayoutType` enum added to `KeyboardContracts.kt` with `LOGICAL` and `EFFICIENCY` values
- [ ] Efficiency layout maps added to `KeyboardLogic.kt` with all characters correctly mapped
- [ ] `getChordResult()` uses the correct map based on current layout type
- [ ] `getPreviewText()` respects current layout type
- [ ] `KeyboardStateMachine` supports `setLayoutType()` to switch layouts at runtime
- [ ] Both layouts produce correct characters for all chord combinations
- [ ] Numbers and symbols (SW, W, NW directions) remain the same in both layouts
- [ ] Single-swipe utility functions unaffected by layout change
- [ ] Shared module compiles for both Android and iOS targets
- [ ] Unit tests covering chord resolution for both layouts

---

## ERICK-58 — Left-Handed Mode — Swap Dials in Shared Logic

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, accessibility |
| **Dependencies** | None |

### Description

Implement left-handed mode in the shared module. When enabled, the roles of the left and right dials are swapped:
- **Normal mode**: Left dial = letter groups (direction), Right dial = color/position within group
- **Left-handed mode**: Left dial = color/position, Right dial = letter groups (direction)

Single-swipe utility functions (Space, Enter, Backspace, etc.) should move to the LEFT dial only in left-handed mode (since they are currently on the right-only dial).

A toggle for this already exists in the Android settings UI (`PreferencesManager.leftHandedMode`). This ticket implements the actual logic.

### How to Get Started

1. **Open `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt`**:
   - Add a `leftHandedMode: Boolean` property (default `false`)
   - Add a `setLeftHandedMode(enabled: Boolean)` method
   - In `handleTouch()`, when `leftHandedMode` is true, invert the `isLeft` parameter:
     - Treat left joystick touches as right joystick inputs and vice versa
     - The simplest approach: at the very start of `handleTouch()`, if `leftHandedMode`, flip `isLeft` to `!isLeft`
   - This swaps which joystick provides direction vs. color

2. **Update single-swipe handling**:
   - In normal mode, single-swipe actions fire when only the right joystick is used
   - In left-handed mode, single-swipe actions should fire when only the LEFT joystick is used
   - The `isLeft` flip should handle this automatically if the detection is based on which joystick is "right" after flipping

3. **Test all scenarios**:
   - Left-handed mode OFF: Left=direction, Right=color (existing behavior)
   - Left-handed mode ON: Left=color, Right=direction
   - Single-swipe utility functions work on the correct dial in each mode
   - Chord input produces correct characters in both modes

### Acceptance Criteria

- [ ] `setLeftHandedMode(enabled: Boolean)` method added to `KeyboardStateMachine`
- [ ] When enabled, left/right dial roles are swapped
- [ ] Charter groups are on the right dial, color selection on the left dial
- [ ] Single-swipe utility functions fire from the left dial in left-handed mode
- [ ] Chord output is identical in both modes (same chord = same character)
- [ ] Toggle can be changed at runtime without restarting the keyboard
- [ ] Shared module compiles for both Android and iOS targets

---

## ERICK-75 — Colorblind Color Palettes — Shared Module

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | shared-module, accessibility |
| **Dependencies** | None |

### Description

Define the colorblind-accessible color palettes in the shared Kotlin Multiplatform module. The right dial uses 8 colors to represent character positions. We need 5 palette options:

1. **Default** — Standard colors (current: Red, Orange, Yellow, Green, Blue, Indigo, Violet, Black)
2. **Okabe-Ito** — Universal colorblind-safe palette (supports all types of color vision deficiency)
3. **Deuteranopia** — Optimized for green-blind users
4. **Protanopia** — Optimized for red-blind users
5. **Tritanopia** — Optimized for blue-blind users

### Color Palette Definitions

**Default (8 colors)**:
| Position | Color Name | Hex |
|---|---|---|
| 1 | Red | #E60012 |
| 2 | Orange | #F39800 |
| 3 | Yellow | #FFF100 |
| 4 | Green | #009944 |
| 5 | Blue | #0068B7 |
| 6 | Indigo | #1D2088 |
| 7 | Violet | #920783 |
| 8 | Black | #000000 |

**Okabe-Ito Universal (8 colors)**:
| Position | Color Name | Hex |
|---|---|---|
| 1 | Orange | #E69F00 |
| 2 | Sky Blue | #56B4E9 |
| 3 | Bluish Green | #009E73 |
| 4 | Yellow | #F0E442 |
| 5 | Blue | #0072B2 |
| 6 | Vermillion | #D55E00 |
| 7 | Reddish Purple | #CC79A7 |
| 8 | Black | #000000 |

**Deuteranopia-Friendly (8 colors)**:
| Position | Color Name | Hex |
|---|---|---|
| 1 | Blue | #0072B2 |
| 2 | Orange | #E69F00 |
| 3 | Light Blue | #56B4E9 |
| 4 | Yellow | #F0E442 |
| 5 | Dark Red | #CC3311 |
| 6 | Teal | #009988 |
| 7 | Pink | #EE7733 |
| 8 | Black | #000000 |

**Protanopia-Friendly (8 colors)**:
| Position | Color Name | Hex |
|---|---|---|
| 1 | Blue | #0077BB |
| 2 | Cyan | #33BBEE |
| 3 | Teal | #009988 |
| 4 | Yellow | #EE7733 |
| 5 | Orange | #CC3311 |
| 6 | Magenta | #EE3377 |
| 7 | Grey | #BBBBBB |
| 8 | Black | #000000 |

**Tritanopia-Friendly (8 colors)**:
| Position | Color Name | Hex |
|---|---|---|
| 1 | Red | #CC3311 |
| 2 | Blue | #0077BB |
| 3 | Yellow | #EECC66 |
| 4 | Cyan | #33BBEE |
| 5 | Magenta | #EE3377 |
| 6 | Teal | #009988 |
| 7 | Grey | #BBBBBB |
| 8 | Black | #000000 |

### How to Get Started

1. **Create a new file `android/shared/src/commonMain/kotlin/ColorPalettes.kt`**:
   ```kotlin
   enum class ColorPaletteType {
       DEFAULT, OKABE_ITO, DEUTERANOPIA, PROTANOPIA, TRITANOPIA
   }
   
   data class ColorEntry(
       val name: String,
       val hexColor: Long  // e.g. 0xFFE69F00
   )
   
   object ColorPalettes {
       fun getPalette(type: ColorPaletteType): List<ColorEntry> {
           return when (type) {
               ColorPaletteType.DEFAULT -> defaultPalette
               ColorPaletteType.OKABE_ITO -> okabeItoPalette
               // ... etc.
           }
       }
       
       private val defaultPalette = listOf(
           ColorEntry("Red", 0xFFE60012),
           ColorEntry("Orange", 0xFFF39800),
           // ... all 8 colors
       )
       // ... other palettes
   }
   ```

2. **Add `ColorPaletteType` to `KeyboardContracts.kt`** if you prefer to keep all enums together.

3. **Update `KeyboardStateMachine`** to include a `setColorPalette(type: ColorPaletteType)` method that the platform layers can call when the user changes settings.

4. **Expose `getCurrentPalette()`** so platform layers can read the active color list for rendering.

### Acceptance Criteria

- [ ] `ColorPaletteType` enum with 5 options defined
- [ ] `ColorEntry` data class with name and hex color defined
- [ ] All 5 palettes defined with exactly 8 colors each
- [ ] `ColorPalettes.getPalette()` returns the correct palette for each type
- [ ] `KeyboardStateMachine` supports `setColorPalette()` and `getCurrentPalette()`
- [ ] Color hex values are accurate for each palette
- [ ] Shared module compiles for both Android and iOS targets
- [ ] Color names are human-readable (for accessibility labels)

---

## ERICK-76 — Colorblind Mode Settings UI — Android

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, accessibility, UI |
| **Dependencies** | ERICK-75 |

### Description

Update the Android Settings screen to replace the simple "Colorblind Mode" toggle with a full color palette selection UI. When colorblind mode is enabled, users should see 4 palette options with visual previews of all 8 colors in each palette.

### How to Get Started

1. **Open `android/app/src/main/java/com/vatoo/erick/SettingsScreen.kt`**:

2. **Replace the simple "Colorblind Mode" toggle** with a two-part UI:

   **Part 1 — Colorblind Mode Toggle**:
   - Keep the existing toggle switch: "Enable Colorblind Mode"
   - When OFF, hide the palette selection section
   - When ON, show the palette selection section

   **Part 2 — Palette Selection** (visible only when toggle is ON):
   - Radio button list with 4 options:
     1. **"Okabe-Ito (Universal)"** — recommended for all types
     2. **"Deuteranopia (Green-blind)"**
     3. **"Protanopia (Red-blind)"**
     4. **"Tritanopia (Blue-blind)"**
   - Below each radio option, display a row of 8 color squares (small boxes, ~32dp) showing the colors of that palette
   - The color squares should be filled with the actual hex colors from `ColorPalettes` (shared module) so the user can visually compare
   - Add a subtle label under each color square with the color name (e.g., "Orange", "Sky Blue")

3. **Compose UI structure**:
   ```kotlin
   @Composable
   fun ColorPaletteOption(
       name: String,
       description: String,
       colors: List<ColorEntry>,
       isSelected: Boolean,
       onSelect: () -> Unit
   ) {
       Column {
           Row {
               RadioButton(selected = isSelected, onClick = onSelect)
               Column {
                   Text(name, style = MaterialTheme.typography.bodyLarge)
                   Text(description, style = MaterialTheme.typography.bodySmall)
               }
           }
           // Color preview row
           Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
               colors.forEach { color ->
                   Column(horizontalAlignment = CenterHorizontally) {
                       Box(
                           modifier = Modifier
                               .size(32.dp)
                               .background(Color(color.hexColor), RoundedCornerShape(4.dp))
                               .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                       )
                       Text(color.name, fontSize = 8.sp)
                   }
               }
           }
       }
   }
   ```

4. **Update `PreferencesManager.kt`** to store the selected palette type:
   - Add key: `colorblind_palette` with values matching `ColorPaletteType` enum names
   - Add `setColorblindPalette(type: String)` and `colorblindPalette: Flow<String>`

5. **Wire to keyboard**: When the keyboard service reads `colorblind_mode = true`, it should also read `colorblind_palette` and call `stateMachine.setColorPalette(type)`.

### Acceptance Criteria

- [ ] Colorblind Mode toggle enables/disables the palette selection section
- [ ] 4 palette options displayed with radio buttons when enabled
- [ ] Each palette option shows 8 color preview squares with correct colors
- [ ] Color names displayed below each square
- [ ] Selected palette persisted via DataStore
- [ ] Keyboard uses the selected palette for rendering when colorblind mode is ON
- [ ] When colorblind mode is OFF, default palette is used
- [ ] UI scrolls properly if content is taller than screen
- [ ] No crashes on toggle/selection changes

---

## ERICK-77 — Colorblind Mode Settings UI — iOS

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | iOS, accessibility, UI |
| **Dependencies** | ERICK-67, ERICK-75 |

### Description

Build the colorblind palette selection UI for the iOS Settings screen, matching the Android implementation. When colorblind mode is enabled, users see 4 palette options with visual color previews.

### How to Get Started

1. **Open the iOS `SettingsView.swift`** created in ERICK-70.

2. **Replace the simple "Colorblind Mode" toggle** with the same two-part UI as Android:

   **Part 1 — Toggle**: Keep "Enable Colorblind Mode" toggle
   **Part 2 — Palette Picker** (shown when toggle is ON):

   ```swift
   struct ColorPaletteOption: View {
       let name: String
       let description: String
       let colors: [(name: String, hex: String)]
       let isSelected: Bool
       let onSelect: () -> Void
       
       var body: some View {
           VStack(alignment: .leading, spacing: 8) {
               HStack {
                   Image(systemName: isSelected ? "circle.fill" : "circle")
                       .foregroundColor(.accentColor)
                   VStack(alignment: .leading) {
                       Text(name).font(.body)
                       Text(description).font(.caption).foregroundColor(.secondary)
                   }
               }
               .onTapGesture { onSelect() }
               
               HStack(spacing: 4) {
                   ForEach(colors, id: \.name) { color in
                       VStack {
                           RoundedRectangle(cornerRadius: 4)
                               .fill(Color(hex: color.hex))
                               .frame(width: 32, height: 32)
                               .overlay(
                                   RoundedRectangle(cornerRadius: 4)
                                       .stroke(Color.gray, lineWidth: 1)
                               )
                           Text(color.name)
                               .font(.system(size: 8))
                       }
                   }
               }
           }
       }
   }
   ```

3. **Store the palette selection** in the App Group `UserDefaults`:
   - Key: `colorblind_palette`
   - Values: `"OKABE_ITO"`, `"DEUTERANOPIA"`, `"PROTANOPIA"`, `"TRITANOPIA"`

4. **Read palette colors** from the shared `ColorPalettes` class (via `SharedKeyboard` framework) or duplicate the hex values locally if the shared module doesn't expose them conveniently for SwiftUI.

5. **You'll need a `Color(hex:)` extension** for SwiftUI to convert hex strings to colors.

### Acceptance Criteria

- [ ] Colorblind Mode toggle shows/hides palette selection
- [ ] 4 palette radio options with names and descriptions
- [ ] 8 color preview squares per palette with correct colors
- [ ] Color names displayed under each square
- [ ] Selection persisted to App Group UserDefaults
- [ ] Keyboard extension reads and applies the selected palette
- [ ] Visual appearance matches Android version as closely as possible
- [ ] No crashes on toggle/selection changes

---

## ERICK-78 — Android Keyboard UI — Radial Dials with Letters, Colors & Live Preview

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, UI, keyboard |
| **Dependencies** | ERICK-75 (for color palettes) |

### Description

Redesign the Android `JoystickView` to render as a proper radial dial that shows:
- **Left Dial**: 8 direction segments, each labeled with the letter group for that direction (e.g., "a b c d e '" for North)
- **Right Dial**: 8 color segments matching the active color palette (Red, Orange, Yellow, Green, Blue, Indigo, Violet, Black)
- **Live Preview**: When the user holds a direction on the left dial, a preview bar/tooltip appears above the keyboard showing the letters in that group with their corresponding colors. When the user also selects a color on the right dial, the specific character is highlighted.

This replaces the current plain gray circle joystick with an informative, visually rich radial UI.

### How to Get Started

1. **Open `android/app/src/main/java/com/vatoo/erick/JoystickView.kt`**:

2. **Redesign the `onDraw()` method** to render a segmented radial dial:

   **For the LEFT dial (letter groups)**:
   - Draw 8 pie segments (45° each) around the center
   - Each segment labeled with its direction letter group (e.g., N: "abcde'")
   - Use a light background for inactive segments, highlight the active segment when touched
   - Show short labels (first 2-3 chars + "..." or abbreviated) to fit within segments
   - Draw direction indicators (N, NE, E, SE, S, SW, W, NW) at the outer edge

   **For the RIGHT dial (colors)**:
   - Draw 8 pie segments colored with the active palette colors
   - Each segment filled with the corresponding color (Red, Orange, Yellow, etc.)
   - Label each segment with position number (1–8) or color name
   - Highlight the active segment when touched
   - The colors should come from the shared `ColorPalettes` module based on colorblind settings

3. **Implement the Live Preview**:
   - Add a preview bar/tooltip view above the keyboard layout (above both joysticks)
   - When the user touches and holds a direction on the LEFT dial:
     - Query the shared `KeyboardLogic` for the character list at that direction (for the current layout type and mode)
     - Display the characters in a horizontal row, each character shown with its corresponding color from the RIGHT dial palette
     - Example: If left=N (normal mode, logical layout): show "a" in Red, "b" in Orange, "c" in Yellow, "d" in Green, "e" in Blue, "'" in Black
   - When the user ALSO selects a direction on the RIGHT dial:
     - Highlight/enlarge the specific character at that color position
     - Example: left=N + right=NE(Orange) → "b" is highlighted
   - The preview should update in real-time as the user moves their finger

4. **Update `keyboard_simple.xml`** or create the layout programmatically:
   - Add a `TextView` or custom view above the joystick area for the preview
   - Ensure the keyboard height accommodates the preview (expand if needed)

5. **Pull character data from shared module**: Call `KeyboardLogic.getChordMap()` or expose the character lists per direction so the UI can display them. You may need to add a helper method to the shared module.

### Acceptance Criteria

- [ ] Left dial shows 8 labeled segments with letter groups for each direction
- [ ] Right dial shows 8 colored segments using the active color palette
- [ ] Active segment highlights when touched on both dials
- [ ] Live preview appears above the keyboard when user holds a direction on left dial
- [ ] Preview shows all characters in the group with their corresponding colors
- [ ] When right dial direction is also selected, the specific character is highlighted in the preview
- [ ] Preview updates in real-time as the user moves their finger
- [ ] Preview disappears when both dials are released
- [ ] Labels are readable and don't overlap
- [ ] Works correctly with both Logical and Efficiency layouts
- [ ] Works correctly with all colorblind palette options
- [ ] Keyboard remains responsive (no jank during rendering)
- [ ] Tested on emulator and physical device

---

## ERICK-79 — iOS Keyboard UI — Radial Dials with Letters, Colors & Live Preview

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, UI, keyboard |
| **Dependencies** | ERICK-67, ERICK-75 |

### Description

Implement the same radial dial UI with letters, colors, and live preview on the iOS keyboard extension. This should be visually identical to the Android implementation (ERICK-78).

### How to Get Started

1. **Open the iOS `JoystickView.swift`** at `ios/ERICK/ErickKeyBoard/JoystickView.swift` (created in Sprint 2). The current implementation uses a basic SwiftUI `Circle` for the base and thumb. It needs to be redesigned with segmented pie slices.

2. **Redesign the drawing** to render segmented radial dials:

   **For the LEFT dial** (UIKit `draw(_ rect:)` using Core Graphics):
   - Draw 8 pie segments (45° each) using `UIBezierPath` arcs
   - Label each segment with the letter group text using `NSAttributedString` drawn at segment centers
   - Highlight active segment with a brighter fill color

   **For the RIGHT dial**:
   - Draw 8 pie segments filled with active palette colors
   - Read palette from shared `ColorPalettes` class
   - Label with position number or color name

3. **Implement the Live Preview**:
   - Add a `UILabel` or custom `UIView` positioned above the joystick views in the keyboard
   - When user touches left dial → show character group with colored backgrounds
   - When both dials active → highlight specific character
   - Use the shared `KeyboardLogic` to resolve which characters to display

4. **Use Core Graphics for rendering** — keyboard extensions have limited UIKit access. Avoid heavy frameworks. The drawing code should be in `draw(_ rect:)` using `CGContext`:
   ```swift
   override func draw(_ rect: CGRect) {
       guard let context = UIGraphicsGetCurrentContext() else { return }
       let center = CGPoint(x: bounds.midX, y: bounds.midY)
       let radius = min(bounds.width, bounds.height) / 2 - 10
       
       for i in 0..<8 {
           let startAngle = CGFloat(i) * .pi / 4 - .pi / 8
           let endAngle = startAngle + .pi / 4
           
           let path = UIBezierPath()
           path.move(to: center)
           path.addArc(withCenter: center, radius: radius,
                       startAngle: startAngle, endAngle: endAngle, clockwise: true)
           path.close()
           
           // Fill with segment color
           segmentColor(for: i).setFill()
           path.fill()
       }
   }
   ```

### Acceptance Criteria

- [ ] Left dial shows 8 labeled segments with letter groups
- [ ] Right dial shows 8 colored segments using active palette
- [ ] Active segment highlights on touch
- [ ] Live preview shows character group with colors when left dial held
- [ ] Specific character highlighted when both dials selected
- [ ] Preview updates in real-time
- [ ] Visually consistent with Android version
- [ ] Works with all layout types and color palettes
- [ ] No jank or lag during touch interaction
- [ ] Tested on iOS Simulator and physical iPhone

---

## ERICK-74 — Custom Keybind Data Model & Persistence — Shared Module

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, custom-layout |
| **Dependencies** | ERICK-73 (Efficiency layout, for LayoutType enum) |

### Description

Implement the data model and serialization for user-created custom keyboard layouts in the shared module. Users should be able to create their own chord-to-character mappings and save them. This includes:
- Custom chord bindings (left direction + right direction → character)
- Custom single-swipe bindings (right-only or left-only direction → action/character)
- No double-swipe support (removed in ERICK-72)
- Serialization to/from JSON for persistence

### How to Get Started

1. **Create `android/shared/src/commonMain/kotlin/CustomLayout.kt`**:

   ```kotlin
   /**
    * Represents a user-created custom keyboard layout.
    */
   data class CustomLayout(
       val id: String,                        // Unique identifier (UUID)
       val name: String,                      // User-given name
       val createdAt: Long,                   // Timestamp
       val chordMap: Map<Direction, List<String>>,       // Normal mode: direction → chars
       val shiftedChordMap: Map<Direction, List<String>>, // Shifted mode: direction → chars
       val singleSwipeMap: Map<Direction, String>,        // direction → action or char
       val shiftedSingleSwipeMap: Map<Direction, String>  // shifted direction → action or char
   )
   ```

2. **Create a `CustomLayoutManager` class**:
   ```kotlin
   class CustomLayoutManager {
       private val layouts = mutableListOf<CustomLayout>()
       
       fun createLayout(name: String): CustomLayout
       fun updateLayout(id: String, layout: CustomLayout): Boolean
       fun deleteLayout(id: String): Boolean
       fun getLayout(id: String): CustomLayout?
       fun getAllLayouts(): List<CustomLayout>
       fun duplicateFromExisting(sourceType: LayoutType, newName: String): CustomLayout
       fun validateLayout(layout: CustomLayout): List<String>  // Returns validation errors
   }
   ```

3. **Implement JSON serialization**:
   - Use `kotlinx.serialization` or manual JSON building (to avoid adding a new dependency)
   - Create `toJson(): String` and `fromJson(json: String): CustomLayout` methods
   - The platform layer will handle file I/O (DataStore on Android, UserDefaults/Files on iOS)

4. **Add `CUSTOM` to `LayoutType` enum**:
   ```kotlin
   enum class LayoutType { LOGICAL, EFFICIENCY, CUSTOM }
   ```

5. **Update `KeyboardLogic.kt`**:
   - Add a method `setCustomLayout(layout: CustomLayout)` that loads the custom maps
   - When `LayoutType.CUSTOM` is active, `getChordResult()` uses the custom maps

6. **Validation rules** (`validateLayout()`):
   - Each direction must have 1–6 characters
   - No duplicate characters across the entire layout
   - All required single-swipe actions must be mapped (Space, Enter, Backspace at minimum)
   - Return human-readable error messages for each issue

7. **Add ability to "fork" existing layouts**: `duplicateFromExisting()` should clone the Logical or Efficiency layout as a starting point for customization.

### Acceptance Criteria

- [ ] `CustomLayout` data class defined with all required fields
- [ ] `CustomLayoutManager` supports CRUD operations on custom layouts
- [ ] JSON serialization/deserialization works correctly
- [ ] `LayoutType.CUSTOM` added to enum
- [ ] `KeyboardLogic` supports custom layout maps
- [ ] Validation catches invalid layouts and returns clear error messages
- [ ] Can fork/duplicate from existing Logical or Efficiency layouts
- [ ] Multiple custom layouts supported (could have more than one saved)
- [ ] Shared module compiles for both Android and iOS targets

---

## ERICK-81 — Custom Keybind Editor UI — Android

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, custom-layout, UI |
| **Dependencies** | ERICK-80 |

### Description

Build the Android UI for creating, editing, and managing custom keyboard layouts. Accessible from the Settings screen under the Layout section.

### How to Get Started

1. **Create a new `CustomLayoutActivity.kt`** (or a Composable screen within the existing navigation):

2. **Layout List Screen** (entry point from Settings):
   - Show list of saved custom layouts (name + created date)
   - "Create New Layout" button
   - "Duplicate from Logical" and "Duplicate from Efficiency" buttons
   - Swipe-to-delete or long-press delete on each layout
   - Tap a layout to edit it
   - Radio button to select which custom layout is active (if multiple exist)

3. **Layout Editor Screen**:
   - **Layout Name**: Editable text field at the top
   - **Chord Map Editor** (main section):
     - Show a list of 8 directions (N, NE, E, SE, S, SW, W, NW)
     - For each direction, show the current characters (e.g., "a b c d e '")
     - Tap a direction to open an edit dialog
     - Edit dialog: 6 text fields (one per color position: Red, Orange, Yellow, Green, Blue, Black)
     - Each text field accepts a single character
     - Show the color square next to each field
   - **Shifted Map Editor**: Same structure for shifted mode characters. Provide a toggle to switch between Normal and Shifted view.
   - **Single-Swipe Editor**:
     - Show 8 directions with their current single-swipe bindings
     - Each binding can be a character or an action (dropdown: Space, Enter, Backspace, Home, End, Toggle Shift, Toggle Caps Lock, Comma, Period, or custom character)
   - **Save Button**: Validates the layout using `CustomLayoutManager.validateLayout()` and shows errors if any
   - **Preview**: Small visual preview of the layout matching the radial dial UI

4. **Wire to Settings**:
   - In `SettingsScreen.kt`, under the Layout section, add a "Custom" radio option
   - When "Custom" is selected and no custom layout exists, prompt to create one
   - When "Custom" is selected and layouts exist, show which one is active
   - Add a "Manage Custom Layouts" button that navigates to the Layout List Screen

5. **Persistence**: Use DataStore to store serialized JSON of custom layouts.

### Acceptance Criteria

- [ ] Custom layout editor accessible from Settings screen
- [ ] Can create a new blank layout or duplicate from existing
- [ ] All 8 directions editable for both Normal and Shifted modes
- [ ] Single-swipe bindings editable for all 8 directions
- [ ] Validation errors shown clearly when saving invalid layout
- [ ] Custom layouts persisted across app restarts
- [ ] Active custom layout applied to keyboard when selected
- [ ] Can delete custom layouts
- [ ] Can rename custom layouts
- [ ] Preview shows the layout visually before saving

---

## ERICK-82 — Custom Keybind Editor UI — iOS

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, custom-layout, UI |
| **Dependencies** | ERICK-67, ERICK-80 |

### Description

Build the iOS SwiftUI equivalent of the Android custom layout editor (ERICK-81). The UI, functionality, and persistence should mirror the Android version exactly.

### How to Get Started

1. **Create `CustomLayoutView.swift`** and `CustomLayoutEditorView.swift`** in the iOS app.

2. **Follow the same structure as Android** (ERICK-81):
   - Layout List: `List` with `ForEach` and `.onDelete` modifier for swipe-to-delete
   - Layout Editor: `Form` with `Section` groups for chord map, shifted map, single-swipe
   - Direction editor: `NavigationLink` to a detail screen with 6 text fields per direction

3. **SwiftUI-specific patterns**:
   ```swift
   struct CustomLayoutListView: View {
       @State private var layouts: [CustomLayout] = []
       
       var body: some View {
           List {
               ForEach(layouts) { layout in
                   NavigationLink(destination: CustomLayoutEditorView(layout: layout)) {
                       VStack(alignment: .leading) {
                           Text(layout.name).font(.headline)
                           Text("Created: \(layout.createdAt)").font(.caption)
                       }
                   }
               }
               .onDelete(perform: deleteLayout)
           }
           .toolbar {
               Button("New Layout") { createLayout() }
           }
       }
   }
   ```

4. **Persistence**: Use App Group `UserDefaults` to store JSON, or use a JSON file in the App Group shared container:
   ```swift
   let containerURL = FileManager.default.containerURL(
       forSecurityApplicationGroupIdentifier: "group.com.vatoo.erick"
   )
   let fileURL = containerURL?.appendingPathComponent("custom_layouts.json")
   ```

5. **Wire to Settings**: In `SettingsView.swift`, add "Custom" layout option and "Manage Custom Layouts" `NavigationLink`.

### Acceptance Criteria

- [ ] Custom layout editor accessible from iOS Settings screen
- [ ] Can create, edit, duplicate, and delete custom layouts
- [ ] All directions editable for Normal and Shifted modes
- [ ] Single-swipe bindings editable
- [ ] Validation errors shown when saving
- [ ] Persistence via App Group (shared with keyboard extension)
- [ ] Active custom layout applied to keyboard
- [ ] UI matches Android version functionally
- [ ] No crashes during edit/save/delete flows

---

## ERICK-61 — Physical Gaming Controller Input — Shared Module

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, controller |
| **Dependencies** | None |

### Description

Add shared module support for physical gaming controller input. The two analog joysticks of a standard controller map directly to the left and right dials. The shared module needs to:
1. Accept normalized joystick axis values (-1.0 to 1.0 for X and Y)
2. Convert them to directions using the same angle-based logic as touch input
3. Feed them through the same `KeyboardStateMachine`

### How to Get Started

1. **Open `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt`**:
   - Add a new method `handleControllerInput(leftX: Float, leftY: Float, rightX: Float, rightY: Float)`:
     - Normalize the input values (they come as -1.0 to 1.0 from both platforms)
     - Apply a configurable dead zone (default 0.25) — ignore values within the dead zone
     - Convert to the same coordinate system used by `handleTouch()`
     - Call `handleTouch()` internally with the converted values
   - Add `handleControllerButton(button: ControllerButton)` for future button support

2. **Add controller-related types to `KeyboardContracts.kt`**:
   ```kotlin
   enum class ControllerButton {
       A, B, X, Y, 
       LEFT_BUMPER, RIGHT_BUMPER,
       LEFT_TRIGGER, RIGHT_TRIGGER,
       START, SELECT
   }
   
   data class ControllerState(
       val isConnected: Boolean,
       val controllerName: String,
       val leftStickX: Float,   // -1.0 to 1.0
       val leftStickY: Float,   // -1.0 to 1.0
       val rightStickX: Float,  // -1.0 to 1.0
       val rightStickY: Float   // -1.0 to 1.0
   )
   ```

3. **Handle the coordinate difference** between touch and controller:
   - Touch: origin at center, Y increases downward (screen coordinates)
   - Controller: origin at center, Y axis direction varies by platform (Android Y-up, iOS Y-down typically)
   - Normalize in the shared layer so platform code just passes raw values

4. **Dead zone logic**: When joystick values are within the dead zone, treat as NONE direction (center position). When they cross outside, detect direction and trigger input.

### Acceptance Criteria

- [ ] `handleControllerInput()` method added to `KeyboardStateMachine`
- [ ] Dead zone configurable (default 0.25)
- [ ] Controller joystick input produces the same chord results as touch input
- [ ] Joystick return-to-center correctly triggers chord release
- [ ] `ControllerState` and `ControllerButton` types defined
- [ ] Shared module compiles for both Android and iOS targets

---

## ERICK-84 — Physical Gaming Controller Support — Android

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | android, controller |
| **Dependencies** | ERICK-83 |

### Description

Add physical gaming controller support to the Android IME. When a compatible controller is connected (Xbox, PlayStation, generic HID gamepad), the keyboard should accept joystick input from the controller's two analog sticks and visually reflect the joystick positions on the on-screen radial dials.

### How to Get Started

1. **Controller detection** in `MyInputMethodService.kt`:
   - Use `InputManager` to detect connected gamepads:
     ```kotlin
     val inputManager = getSystemService(Context.INPUT_SERVICE) as InputManager
     val deviceIds = InputDevice.getDeviceIds()
     val gamepads = deviceIds.mapNotNull { InputDevice.getDevice(it) }
         .filter { it.sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD }
     ```
   - Register `InputManager.InputDeviceListener` to detect connect/disconnect events

2. **Process joystick input** by overriding `onGenericMotionEvent()` in the IME service:
   ```kotlin
   override fun onGenericMotionEvent(event: MotionEvent): Boolean {
       if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
           val leftX = event.getAxisValue(MotionEvent.AXIS_X)
           val leftY = event.getAxisValue(MotionEvent.AXIS_Y)
           val rightX = event.getAxisValue(MotionEvent.AXIS_Z)
           val rightY = event.getAxisValue(MotionEvent.AXIS_RZ)
           
           stateMachine.handleControllerInput(leftX, leftY, rightX, rightY)
           
           // Update visual joystick positions
           leftJoystick.updateThumbFromController(leftX, leftY)
           rightJoystick.updateThumbFromController(rightX, rightY)
           
           return true
       }
       return super.onGenericMotionEvent(event)
   }
   ```

3. **Update `JoystickView.kt`** to support external position updates:
   - Add `updateThumbFromController(normalizedX: Float, normalizedY: Float)`:
     - Convert -1.0..1.0 to pixel coordinates within the view
     - Move the thumb to that position
     - Trigger visual feedback (highlight active segment)
     - Call `invalidate()` to redraw

4. **Handle key events** for controller buttons (optional but useful):
   - Override `onKeyDown()` and `onKeyUp()` for gamepad buttons
   - Map A button → confirm/submit, B button → backspace, etc.
   - Start button → open settings

5. **Test with**: 
   - Xbox controller connected via Bluetooth
   - PlayStation DualSense via Bluetooth or USB
   - Generic USB gamepad
   - Android emulator gamepad emulation

### Acceptance Criteria

- [ ] Controller analog sticks produce chord input matching touch behavior
- [ ] On-screen joystick thumbs move to reflect physical controller stick positions in real-time
- [ ] Active segments highlight correctly based on controller input
- [ ] Controller dead zone prevents accidental input
- [ ] Chords fire correctly on stick release (return to center)
- [ ] Controller connect/disconnect detected at runtime
- [ ] No input conflicts between touch and controller (both can work simultaneously)
- [ ] Tested with at least one physical controller on Android device
- [ ] No crashes when controller is connected/disconnected mid-typing

---

## ERICK-85 — Physical Gaming Controller Support — iOS

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, controller |
| **Dependencies** | ERICK-67, ERICK-83 |

### Description

Add physical gaming controller support to the iOS keyboard extension. Use Apple's Game Controller framework (`GCController`) to detect connected controllers and read analog stick input.

### How to Get Started

1. **Important limitation**: iOS Keyboard Extensions have restricted framework access. The `GameController` framework may need to be accessed from the main app rather than the extension directly. Test if `import GameController` works in the extension — if not, the main app needs to relay controller input to the extension via App Group shared state or notifications.

2. **Controller detection** in the main app (or extension if allowed):
   ```swift
   import GameController
   
   // In viewDidLoad or similar
   NotificationCenter.default.addObserver(
       self, selector: #selector(controllerConnected),
       name: .GCControllerDidConnect, object: nil
   )
   NotificationCenter.default.addObserver(
       self, selector: #selector(controllerDisconnected),
       name: .GCControllerDidDisconnect, object: nil
   )
   GCController.startWirelessControllerDiscovery()
   ```

3. **Read joystick input**:
   ```swift
   if let controller = GCController.controllers().first,
      let gamepad = controller.extendedGamepad {
       
       gamepad.leftThumbstick.valueChangedHandler = { stick, xValue, yValue in
           // xValue and yValue are -1.0 to 1.0
           self.stateMachine.handleControllerInput(
               leftX: xValue, leftY: -yValue,  // Invert Y for screen coordinates
               rightX: 0, rightY: 0
           )
       }
       
       gamepad.rightThumbstick.valueChangedHandler = { stick, xValue, yValue in
           self.stateMachine.handleControllerInput(
               leftX: self.lastLeftX, leftY: self.lastLeftY,
               rightX: xValue, rightY: -yValue
           )
       }
   }
   ```

4. **Update the joystick view** to reflect controller stick positions visually.

5. **If extension access is blocked**: Use IPC via App Group:
   - Main app writes controller state to shared `UserDefaults` or shared file
   - Extension polls for changes (use a timer or file change notification)
   - This is a fallback approach — try direct extension access first

### Acceptance Criteria

- [ ] Controller detected when connected via Bluetooth
- [ ] Analog stick input produces chord output matching touch behavior
- [ ] On-screen joystick views reflect controller positions
- [ ] Dead zone prevents accidental input
- [ ] Controller works with Xbox and PlayStation controllers
- [ ] Connect/disconnect handled gracefully at runtime
- [ ] Tested on physical iPhone with a controller
- [ ] Fallback approach documented if extension framework access is limited

---

## ERICK-62 — Controller Detection & Status on App Homepage — Both Platforms

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Story Points** | 2 |
| **Assignee** | Vatsal Unadkat |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, controller, UI |
| **Dependencies** | ERICK-84, ERICK-85 |

### Description

Add a "Controller Status" card to the main app homepage (onboarding screen) on both Android and iOS. This card should detect if a compatible gaming controller is connected and display its name and status.

### How to Get Started

**Android** (`MainActivity.kt`):

1. Add a new Card composable below the existing setup cards:
   ```kotlin
   @Composable
   fun ControllerStatusCard() {
       val context = LocalContext.current
       var controllerName by remember { mutableStateOf<String?>(null) }
       
       LaunchedEffect(Unit) {
           val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
           val deviceIds = InputDevice.getDeviceIds()
           val gamepad = deviceIds.mapNotNull { InputDevice.getDevice(it) }
               .firstOrNull { it.sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD }
           controllerName = gamepad?.name
       }
       
       Card {
           Row {
               Icon(/* gamepad icon */)
               Column {
                   Text("Controller Status")
                   if (controllerName != null) {
                       Text("✅ Connected: $controllerName", color = Color.Green)
                   } else {
                       Text("No controller detected", color = Color.Gray)
                   }
               }
           }
       }
   }
   ```

2. Use a gamepad icon (add a vector drawable or use Material Icons `SportsEsports`).

3. Refresh status when the activity resumes (use `onResume` or `LifecycleObserver`).

**iOS** (`ContentView.swift`):

1. Add a similar card:
   ```swift
   struct ControllerStatusCard: View {
       @State private var controllerName: String?
       
       var body: some View {
           GroupBox {
               HStack {
                   Image(systemName: "gamecontroller.fill")
                   VStack(alignment: .leading) {
                       Text("Controller Status").font(.headline)
                       if let name = controllerName {
                           Text("✅ Connected: \(name)").foregroundColor(.green)
                       } else {
                           Text("No controller detected").foregroundColor(.gray)
                       }
                   }
               }
           }
           .onAppear { checkController() }
       }
       
       func checkController() {
           controllerName = GCController.controllers().first?.vendorName
       }
   }
   ```

2. Add `import GameController` and listen for connect/disconnect notifications.

### Acceptance Criteria

- [ ] Controller Status card visible on Android main screen
- [ ] Controller Status card visible on iOS main screen
- [ ] Shows controller name when connected (e.g., "Xbox Wireless Controller")
- [ ] Shows "No controller detected" when no controller is connected
- [ ] Status updates when controller is connected/disconnected without restarting app
- [ ] Gamepad icon displayed next to the status
- [ ] Card styled consistently with other cards on the page

---

## Sprint 3 Summary

| Ticket | Title | Assignee | Priority | SP | Dependencies |
|---|---|---|---|---|---|
| ERICK-67 | Merge iOS Branch & Polish Keyboard Extension | Dev 1 | Highest | 2 | — |
| ERICK-68 | iOS Keyboard — Bug Fixes & Input Completeness | Dev 1 | High | 2 | ERICK-67 |
| ERICK-69 | iOS Main App — Onboarding & Setup | Dev 3 | High | 3 | ERICK-67 |
| ERICK-70 | iOS Settings Screen & Preferences | Dev 3 | High | 3 | ERICK-67 |
| ERICK-71 | iOS Logo, Branding & Assets | Dev 3 | Low | 1 | ERICK-67 |
| ERICK-72 | Remove Double Swipe from Shared Logic | Dev 2 | High | 2 | — |
| ERICK-73 | Efficiency Layout in Shared Module | Dev 2 | High | 3 | — |
| ERICK-74 | Left-Handed Mode — Swap Dials Logic | Dev 2 | Medium | 3 | — |
| ERICK-75 | Colorblind Color Palettes — Shared Module | Dev 2 | Medium | 3 | — |
| ERICK-76 | Colorblind Settings UI — Android | Dev 1 | Medium | 3 | ERICK-75 |
| ERICK-77 | Colorblind Settings UI — iOS | Dev 3 | Medium | 3 | ERICK-67, ERICK-75 |
| ERICK-78 | Android Radial Dial UI + Live Preview | Dev 1 | High | 5 | ERICK-75 |
| ERICK-79 | iOS Radial Dial UI + Live Preview | Dev 1 | High | 5 | ERICK-67, ERICK-75 |
| ERICK-80 | Custom Keybind Data Model — Shared Module | Dev 2 | Medium | 5 | ERICK-73 |
| ERICK-81 | Custom Keybind Editor UI — Android | Dev 1 | Medium | 5 | ERICK-80 |
| ERICK-82 | Custom Keybind Editor UI — iOS | Dev 1 | Medium | 5 | ERICK-67, ERICK-80 |
| ERICK-83 | Controller Input — Shared Module | Dev 2 | Medium | 3 | — |
| ERICK-84 | Controller Support — Android | Dev 1 | Medium | 5 | ERICK-83 |
| ERICK-85 | Controller Support — iOS | Dev 1 | Medium | 5 | ERICK-67, ERICK-83 |
| ERICK-86 | Controller Status on Homepage — Both | Dev 3 | Medium | 2 | ERICK-84, ERICK-85 |

### Story Point Totals

| Developer | Total SP | High/Highest SP |
|---|---|---|
| **Dev 1** | 42 | 14 |
| **Dev 2** | 22 | 5 |
| **Dev 3** | 12 | 6 |
| **Total** | 76 | 25 |

### Recommended Sprint 3 Scope (5-day sprint)

With the iOS project setup and keyboard already done from Sprint 2, Dev 1 has significantly more bandwidth this sprint. Based on Sprint 2 velocity (~15 SP per developer), here is the recommended Sprint 3 scope:

**Sprint 3 Must-Do (Highest/High priority)**:

| Ticket | Assignee | SP |
|---|---|---|
| ERICK-67 — Merge iOS & Polish | Dev 1 | 2 |
| ERICK-68 — iOS Keyboard Bug Fixes | Dev 1 | 2 |
| ERICK-78 — Android Radial Dial UI | Dev 1 | 5 |
| ERICK-76 — Colorblind Settings Android | Dev 1 | 3 |
| ERICK-79 — iOS Radial Dial UI | Dev 1 | 5 |
| ERICK-72 — Remove Double Swipe | Dev 2 | 2 |
| ERICK-73 — Efficiency Layout | Dev 2 | 3 |
| ERICK-75 — Colorblind Palettes | Dev 2 | 3 |
| ERICK-74 — Left-Handed Mode | Dev 2 | 3 |
| ERICK-69 — iOS Onboarding | Dev 3 | 3 |
| ERICK-70 — iOS Settings | Dev 3 | 3 |
| ERICK-71 — iOS Branding | Dev 3 | 1 |
| ERICK-77 — iOS Colorblind Settings UI | Dev 3 | 3 |

**Sprint 3 Total**: ~38 SP (Dev 1: 17, Dev 2: 11, Dev 3: 10)

**Carry to Sprint 4**: ERICK-80, ERICK-81, ERICK-82, ERICK-83, ERICK-84, ERICK-85, ERICK-86

---

### Suggested Execution Order

**Dev 1 (Platform Layer)**:
1. ERICK-67 (Merge iOS branch & polish) — Day 1
2. ERICK-68 (iOS keyboard bug fixes) — Day 1
3. ERICK-78 (Android radial dial UI) — Day 2-3
4. ERICK-76 (Android colorblind settings UI) — Day 3-4
5. ERICK-79 (iOS radial dial UI) — Day 4-5

**Dev 2 (Shared Module)**:
1. ERICK-72 (Remove double swipe) — Day 1
2. ERICK-73 (Efficiency layout) — Day 1-2
3. ERICK-75 (Colorblind palettes) — Day 2-3
4. ERICK-74 (Left-handed mode) — Day 3-4
5. ERICK-83 (Controller shared logic) — Day 4-5 (stretch goal)

**Dev 3 (Flexible)**:
1. ERICK-71 (iOS branding/assets) — Day 1
2. ERICK-69 (iOS onboarding) — Day 2-3
3. ERICK-70 (iOS settings) — Day 3-4
4. ERICK-77 (iOS colorblind settings UI) — Day 4-5
