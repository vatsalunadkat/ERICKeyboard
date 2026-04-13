# ERICK-139 - Six-Section Dial Mode

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 22 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Sprint 5 (Completed) |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, core, android, ios, shared, accessibility |
| **Dependencies** | None |

---

## Description

Add an optional 6-section (60 degrees per segment) dial mode as an alternative to the current 8-section (45 degrees per segment) dial layout. The 6-section mode is **off by default** in settings; when disabled, the keyboard uses the existing 8-section dial. When enabled, both the left and right dials switch to 6 segments, giving users more room to swipe, improving accuracy for users with motor limitations and reducing mistaps for all users.

A 6x6 grid provides 36 chord positions per mode. The default (normal) mode maps 26 letters + 10 digits = 36 characters, which fits exactly. Shifted mode maps the corresponding uppercase letters and shifted symbols.

Symbols that no longer fit in the chord grid are handled by a **Symbols mode**, toggled via the **NW single-swipe** on the right dial. Swiping NW once switches the entire keyboard to symbols mode; swiping NW again reverts to the normal keyboard. No settings toggle is needed for symbols mode -- it is always available as part of the 6-section dial.

All existing features must work identically in 6-section mode: **left-hand mode**, all **3 input methods** (Instant, Confirm, Assisted), **color palettes** (including custom and colorblind-safe palettes), **custom layouts**, **word prediction**, **haptic feedback**, **typing sounds**, **controller support**, and the **typing practice mini-game**.

The right-dial single-swipe utility wheel is reduced from 8 to 6 directions, with updated action assignments.

---

## Current Architecture (8-Section)

### Direction Enum
`KeyboardContracts.kt` defines `Direction { NONE, N, NE, E, SE, S, SW, W, NW }` (8 cardinal + intercardinal directions).

### Direction Detection
`KeyboardLogic.kt` uses `atan2` to convert X/Y touch coordinates to one of 8 directions, each spanning 45 degrees.

### Chord Mapping
`KeyboardLogic.kt` stores chord maps as `Map<Direction, List<String>>` where each left direction maps to a list of 8 characters (one per right direction). The Logical layout uses 5 letters per row + 3 empty/symbol slots; the Efficiency layout fills all 8 slots with frequency-optimized placements.

### Joystick UI
- **Android** (`JoystickView.kt`): Canvas-based drawing with `for (i in 0 until 8)` loops, 45-degree arcs, concentric rings on the left dial (outer 3 blocks, middle 3 blocks, inner 2 blocks per direction).
- **iOS** (`JoystickView.swift`): SwiftUI drawing with `WheelDirection.orderedDirections` (8 items), `LeftWheelSection` containing `outer[0..2]`, `middle[3..5]`, `inner[6..7]`.

### Color Palettes
`ColorPalettes.kt` defines 7 palettes x 8 colors each, one color per direction.

### Custom Layouts
`CustomLayout.kt` stores user layouts as `Map<Direction, List<String>>` with 8 directions x 8 characters.

### Single-Swipe Actions
8 right-dial-only actions: Home/End (N), comma (NE), Space (E), period (SE), Enter (S), Shift (SW), Backspace (W), Caps Lock (NW).

---

## Proposed Architecture (6-Section)

### 1. Direction Enum Update

**File**: `KeyboardContracts.kt`

Reduce to 6 directions. The simplest option is to keep 6 evenly-spaced directions:

```
Direction { NONE, N, NE, SE, S, SW, NW }
```

This removes E and W, keeping directions at 60-degree intervals: N (270 degrees), NE (330 degrees), SE (30 degrees), S (90 degrees), SW (150 degrees), NW (210 degrees).

Alternatively, use new names like D1-D6 or keep compass names at 60-degree spacing. The exact naming should be finalized during implementation, but the key constraint is **6 evenly-spaced directions at 60-degree intervals**.

### 2. Direction Detection Update

**File**: `KeyboardLogic.kt`

Change `getDirectionFromXY()` from 8 x 45-degree segments to 6 x 60-degree segments:

```
Each segment spans +/- 30 degrees from its center angle.
N:  240-300 degrees
NE: 300-360 degrees
SE: 0-60 degrees
S:  60-120 degrees
SW: 120-180 degrees
NW: 180-240 degrees
```

### 3. Chord Map Update (6x6 = 36 slots)

**File**: `KeyboardLogic.kt`

#### Logical Layout (Normal Mode)
26 letters + 10 digits = 36 characters, one per slot:

```
         N    NE    SE    S    SW    NW
N   [    a     b     c     d     e     f  ]
NE  [    g     h     i     j     k     l  ]
SE  [    m     n     o     p     q     r  ]
S   [    s     t     u     v     w     x  ]
SW  [    y     z     1     2     3     4  ]
NW  [    5     6     7     8     9     0  ]
```

#### Logical Layout (Shifted Mode)
26 uppercase letters + 10 common symbols:

```
         N    NE    SE    S    SW    NW
N   [    A     B     C     D     E     F  ]
NE  [    G     H     I     J     K     L  ]
SE  [    M     N     O     P     Q     R  ]
S   [    S     T     U     V     W     X  ]
SW  [    Y     Z     !     @     #     $  ]
NW  [    %     ^     &     *     (     )  ]
```

#### Efficiency Layout
Must be re-optimized for the 6x6 grid. The optimizer (`erick_v5_vectorized.py`) needs to be updated to work with 36 positions instead of 64. Same-direction chords (N+N, NE+NE, etc.) remain the easiest, so put the highest-frequency letters (e, t, a, o, i, n) on those 6 diagonal slots.

### 4. Single-Swipe Actions (Right Dial Only)

Reduce from 8 to 6 actions:

```
N:   Shift (long-press or double-tap for Caps Lock)
NE:  Period
SE:  Spacebar
S:   Enter
SW:  Backspace
NW:  Symbols (toggle symbols mode on/off)
```

Changes from current 8-section layout:
- **Shift** moves to N; Caps Lock is activated by long-press or double-tap on the same direction
- **Spacebar** moves to SE
- **Backspace** moves to SW
- **Period** moves to NE
- **NW** is now the **Symbols toggle** -- single-swiping NW switches the entire keyboard into symbols mode, and swiping NW again returns to the normal letter keyboard
- **Home/End** and **comma** are no longer on the single-swipe wheel; comma is available in the symbols grid, and Home/End can be accessed via controller or other means

### 5. Symbols Mode (NW Toggle)

Activated by single-swiping **NW** on the right dial. Swiping NW again returns to the normal keyboard. No settings toggle is required -- symbols mode is always available when the 6-section dial is enabled.

When active:

- The left dial shows symbol groups instead of letter groups
- The right dial selects individual symbols within the group
- 6x6 = 36 symbol slots available per mode (normal + shifted = 72 symbols total)
- A visual indicator (e.g. highlighted NW segment, status label, or tinted dial) shows that symbols mode is active

**Normal symbols mode:**

```
         N    NE    SE    S    SW    NW
N   [    !     @     #     $     %     ^  ]
NE  [    &     *     (     )     -     =  ]
SE  [    [     ]     {     }     \     |  ]
S   [    ;     :     '     "     ,     .  ]
SW  [    /     ?     <     >     `     ~  ]
NW  [    +     _              (reserved)  ]
```

The shifted variant can hold additional mathematical or currency symbols.

**Behavior notes:**
- Single-swipe actions (Shift, Period, Spacebar, Enter, Backspace) continue to work normally in symbols mode
- The NW direction on the right dial single-swipe always toggles back to the normal keyboard
- Shift in symbols mode switches to the shifted symbols grid
- Word prediction is paused in symbols mode since the user is typing symbols

### 6. Joystick UI Changes

#### Android (`JoystickView.kt`)
- Change all `for (i in 0 until 8)` loops to `for (i in 0 until 6)`
- Change arc angles from `startAngle = -22.5f + i * 45f` / `sweepAngle = 45f` to `startAngle = -30f + i * 60f` / `sweepAngle = 60f`
- Update the left dial concentric rings: with 6 chars per direction, use outer ring (3 blocks x 20 degrees each) and inner ring (3 blocks x 20 degrees each), or a single ring of 6 blocks
- Update label/icon positioning for the wider segments
- Update direction-to-color index mapping

#### iOS (`JoystickView.swift`)
- Update `WheelDirection` enum to 6 directions
- Update `orderedDirections` array to 6 entries
- Update `centerAngleDegrees` for 60-degree spacing
- Update `LeftWheelSection` from `outer[0..2], middle[3..5], inner[6..7]` to `outer[0..2], inner[3..5]` (two rings of 3)
- Update all SwiftUI `Path` and arc drawing code for 60-degree segments
- Update `sharedDirection()` mapping between iOS `WheelDirection` and KMP `Direction`

### 7. Color Palette Changes

**File**: `ColorPalettes.kt`

Reduce all 7 palettes from 8 colors to 6 colors. Select the 6 most distinguishable colors from each existing palette. The custom palette editor also needs updating to show 6 color slots instead of 8.

Suggested default palette (6 colors):

```
N:  Red (#E60012)
NE: Orange (#F39800)
SE: Green (#009944)
S:  Blue (#0068B7)
SW: Indigo (#1D2088)
NW: Violet (#920783)
```

All colorblind-safe palettes (Okabe-Ito, Deuteranopia, Protanopia, Tritanopia, Pastel) must be re-evaluated for 6-color distinguishability.

### 8. Custom Layout Migration

Existing custom layouts stored on user devices use 8 directions x 8 characters. Grey out layouts that were created for the 8 x 8. When the user creates a new layout they should be asked if it's a 6x6 or 8x8x.

### 9. Shared Module (`KeyboardLogic.kt`) Changes Checklist

- [x] Update `getDirectionFromXY()` to return one of 6 directions
- [x] Update `normalMap` to 6 directions x 6 characters
- [x] Update `shiftedMap` to 6 directions x 6 characters
- [x] Update `efficiencyNormalMap` to 6 directions x 6 characters
- [x] Update `efficiencyShiftedMap` to 6 directions x 6 characters
- [x] Update `getRightIndex()` to return 0-5
- [x] Update `getSingleSwipeResult()` to handle 6 directions
- [x] Update `getCharactersForDirection()` return list size
- [x] Update `getCharactersAtPosition()` direction list
- [x] Add symbols mode chord maps (normal + shifted symbols grids)
- [x] Add NW single-swipe toggle logic for symbols mode

### 10. State Machine Changes

**File**: `KeyboardStateMachine.kt`

- Add a `SYMBOLS` value to `KeyboardMode` enum (or a separate boolean flag)
- When symbols mode is active, route chord lookups to the symbols map instead of the letter map
- Single-swipe NW triggers the toggle: if in normal/shifted/caps, switch to symbols; if in symbols, switch back to the previous mode
- Single-swipe actions (N, NE, SE, S, SW) continue to work normally in symbols mode
- Exiting symbols mode returns to the previous mode (normal/shifted/caps)

### 11. Settings Changes

**Android** (`SettingsScreen.kt`, `PreferencesManager.kt`):
- Add `SIX_SECTION_DIAL_KEY` boolean preference (default `false`)
- Add toggle in the Layout or Input section of settings:
  - Label: "6-section dial mode"
  - Description: "Use 6 larger segments instead of 8. Larger targets improve accuracy but change the chord layout."
- When toggled, the keyboard switches between 8-section and 6-section chord maps, direction detection, and UI rendering
- Persist via DataStore

**iOS** (`SettingsView.swift`):
- Add `@AppStorage("six_section_dial")` boolean (default `false`)
- Add toggle in the settings UI with matching label and description

**Important**: No separate symbols mode toggle in settings. Symbols mode is accessed exclusively via the NW single-swipe on the right dial and is always available when 6-section dial mode is enabled.

### 12. Optimizer Update

**File**: `docs/documentation/Research/vatsal/erick_v5_vectorized.py`

- Update chord position count from 64 to 36
- Update effort matrix for 6-direction biomechanical model
- Remove diagonal directions from the model
- Re-run optimization to produce a new Efficiency layout for the 6x6 grid
- Update `v5_output.txt` with new results

---

## Files to Modify

| File | Changes |
|---|---|
| `android/shared/src/commonMain/kotlin/KeyboardContracts.kt` | Direction enum (8 to 6 variant), add SYMBOLS mode |
| `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` | 6-section direction detection, all 6x6 chord maps, single-swipe actions, symbols maps |
| `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` | Symbols mode toggle via NW swipe, mode routing |
| `android/shared/src/commonMain/kotlin/ColorPalettes.kt` | 6-color variants for all palettes |
| `android/shared/src/commonMain/kotlin/CustomLayout.kt` | 6x6 data model variant, migration logic |
| `android/shared/src/commonMain/kotlin/CustomLayoutSerializer.kt` | Serialization for 6-direction layouts |
| `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` | All drawing code (6-segment arcs, rings, labels, colors) |
| `android/app/src/main/java/com/vatoo/erick/SettingsScreen.kt` | 6-section dial mode toggle (default off) |
| `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt` | 6-section dial mode preference |
| `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` | 6-section dial mode preference collection, conditional direction/map logic |
| `ios/ERICK/ErickKeyBoard/JoystickView.swift` | All drawing code, direction enum, section model (6-section variant) |
| `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` | 6-section mode handling, NW symbols toggle |
| `ios/ERICK/ERICK/SettingsView.swift` | 6-section dial mode toggle (default off) |
| `docs/documentation/Research/vatsal/erick_v5_vectorized.py` | Optimizer for 36 positions |
| `docs/documentation/User_Guide.md` | Updated chord tables, symbols mode docs, 6-section dial instructions |
| `docs/documentation/APP_CONTEXT.md` | Architecture updates for dual-mode support |

---

## Acceptance Criteria

- [x] 6-section dial mode is **off by default** in settings on both Android and iOS
- [x] When 6-section mode is disabled, the keyboard uses the existing 8-section dial with no changes
- [x] When 6-section mode is enabled, direction detection uses 6 x 60-degree segments
- [x] Logical layout maps 26 letters + 10 digits across a 6x6 chord grid (normal mode)
- [x] Shifted mode maps 26 uppercase letters + 10 common symbols
- [x] Efficiency layout re-optimized for the 6x6 grid and placed in `KeyboardLogic.kt`
- [x] Single-swipe right-dial actions: N = Shift (long-press/double-tap for Caps Lock), NE = Period, SE = Spacebar, S = Enter, SW = Backspace, NW = Symbols toggle
- [x] NW single-swipe toggles the entire keyboard into symbols mode; swiping NW again reverts to normal
- [x] Symbols mode provides access to all remaining symbols (brackets, slashes, quotes, math operators, etc.) via a 6x6 symbols grid
- [x] Symbols mode has a clear visual indicator showing it is active
- [x] All single-swipe actions (Shift, Period, Spacebar, Enter, Backspace) work normally in symbols mode
- [x] Android `JoystickView` draws 6 segments per dial with 60-degree arcs
- [x] iOS `JoystickView` draws 6 segments per dial with 60-degree arcs
- [x] Left dial shows character labels correctly in the wider segments (outer + inner ring layout)
- [x] All 7 color palettes updated to 6 colors each; colorblind-safe palettes reviewed for 6-color distinguishability
- [x] Custom palette editor shows 6 color slots instead of 8
- [x] Existing custom layouts are handled gracefully (migration notice or reset)
- [x] Custom layout creator works with 6 directions x 6 characters
- [x] **Left-hand mode** works correctly with 6-section dial (mirrored layout)
- [x] All **3 input methods** (Instant, Confirm, Assisted) work correctly with 6-section dial
- [x] Word prediction and autocorrect continue to work (no changes needed)
- [x] Haptic feedback and typing sounds continue to work for all input types
- [x] Controller support works with 6-direction mapping on analog sticks
- [x] Typing practice mini-game works with updated chord positions
- [x] Onboarding screens updated to show 6-section dial visuals (when mode is active)
- [x] User Guide updated with new chord tables and symbols mode documentation
- [x] All existing unit tests updated and passing
- [x] No regressions in any existing 8-section keyboard functionality

---

## Estimated Sub-Tasks

| Sub-Task | Points | Description |
|---|---|---|
| Shared: Direction enum + detection | 2 | Update `KeyboardContracts.kt` and `getDirectionFromXY()` |
| Shared: Chord maps + single-swipe | 3 | Rewrite all layout maps for 6x6, update single-swipe actions (N=Shift, NE=Period, SE=Space, S=Enter, SW=Backspace, NW=Symbols) |
| Shared: Symbols mode | 3 | Add symbols chord maps, NW toggle logic in state machine |
| Shared: Color palettes | 1 | Add 6-color variants for all palettes |
| Shared: Custom layout model | 2 | Add 6x6 data model variant, serializer, migration |
| Android: JoystickView | 3 | Conditional 6-segment rendering (arcs, rings, labels) |
| Android: Settings + preferences | 1 | Add 6-section dial mode toggle (default off) |
| iOS: JoystickView | 3 | Conditional 6-segment rendering (arcs, rings, labels) |
| iOS: Settings + preferences | 1 | Add 6-section dial mode toggle (default off) |
| Feature parity validation | 1 | Verify left-hand mode, all 3 input methods, color palettes, controller, mini-game work in 6-section mode |
| Optimizer: 36-position update | 2 | Update and re-run optimizer for 6x6 grid |
| Documentation + tests | 2 | Update User Guide, APP_CONTEXT, unit tests |

**Total**: ~22 story points
