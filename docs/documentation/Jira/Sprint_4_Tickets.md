# ERICK — Sprint 4+ Tickets

**Sprint**: SCRUM Sprint 4 (and beyond)  
**Sprint 4 Start Date**: March 16, 2026 (Monday)  
**Sprint 4 End Date**: March 20, 2026 (Friday)  
**Project**: ERICK - Agile Methods  

**Team**:
- **Developer 1** — Platform Layer (UI/OS) for Android & iOS  
- **Developer 2** — Shared Module (Kotlin Multiplatform)  

**Note**: Tickets from Sprint 3 that carried over — ERICK-80 through ERICK-86 (Custom Keybinds, Controller Support) — remain in the backlog and are not re-listed here. This document covers new feature requests only.

**Ticket Numbering**: Continues from ERICK-86 (last Sprint 3 ticket).

---

## ERICK-87 — Unify Android Preview Bar to Match iOS Capsule Design + Right-Dial Preview

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 5 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 4 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, UI, keyboard, preview |
| **Dependencies** | None |

### Description

The iOS keyboard preview bar currently has a much more polished look than Android — it uses **elongated oval capsules** with a white semi-transparent background, drop shadows, and animated highlighting. The Android preview bar is a plain rectangular gray bar with `SpannableString` text.

This ticket covers **two objectives**:

1. **Visual redesign** — Make the Android preview bar look **identical** to iOS: white capsule shape, shadow, animated text highlighting, properly spaced character "blobs"
2. **Right-dial preview** — Currently, holding the **left dial** on Android shows a preview of all 8 characters in that group (e.g., holding N shows `a b c d e '`). However, holding the **right dial alone** does **not** show a preview. We need to add a **right-dial preview**: when the user holds a direction on the right dial (e.g., N = Red), the preview bar should show the character at that color position across ALL left-dial groups. For example, holding right-dial N (Red) should preview: `a f k p u z 1 6` (the first character from every group in logical layout). This should work for both Logical and Efficiency layouts.

### Current State (what exists)

**iOS (reference — target look)**:
- Preview bar uses `Capsule().fill(Color.white.opacity(0.96))` with `shadow(color: .black.opacity(0.08), radius: 6, y: 2)`
- Characters displayed in `HStack` with 8pt spacing
- Normal text: 22pt bold; Highlighted: 27pt heavy with 1.08x scale
- Animation: `.easeInOut(duration: 0.12)` on highlight change
- Color-coded per ring layer (outer → Red/Orange/Yellow, middle → Green/Blue/Purple, inner → Black/Magenta)
- Rendered conditionally: hidden when no preview data

**Android (current — needs redesign)**:
- Preview bar is a `FrameLayout` in `keyboard_simple.xml` with background `#D0D6DC` (gray)
- Characters rendered via `SpannableStringBuilder` with `ForegroundColorSpan` and `RelativeSizeSpan(1.5f)` for highlighting
- No shadow, no capsule shape, no animation
- Only triggered by left-dial direction; right-dial-only hold shows nothing

### How to Get Started

#### Part 1: Visual Redesign

1. **Open `android/app/src/main/res/layout/keyboard_simple.xml`**:
   - Replace the `FrameLayout` preview container with a custom `View` or a Compose-hosted view
   - If staying with XML: use a `CardView` with `cornerRadius` set high (e.g., `24dp`) to create a capsule shape, set elevation for shadow (`4dp`), and background `#F5FFFFFF` (white with ~96% opacity)
   - Alternatively, convert the preview bar to a Jetpack Compose view embedded in the IME

2. **Open `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt`**:
   - In `updateLivePreview()`, update the rendering to match iOS:
     - Use individual `TextView` items in a `LinearLayout` (horizontal) inside the capsule, instead of a single `SpannableString`
     - Each character gets its own small capsule background (optional — check iOS for exact styling)
     - Normal characters: 18sp bold, highlighted: 24sp extra-bold with a slight scale animation
   - OR refactor to use Compose for the preview bar:
     ```kotlin
     // Compose approach (recommended):
     @Composable
     fun PreviewBar(items: List<PreviewItem>, highlightIndex: Int?) {
         Surface(
             shape = RoundedCornerShape(50), // capsule
             color = Color.White.copy(alpha = 0.96f),
             shadowElevation = 6.dp
         ) {
             Row(
                 modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                 horizontalArrangement = Arrangement.spacedBy(8.dp)
             ) {
                 items.forEachIndexed { index, item ->
                     val isHighlighted = index == highlightIndex
                     Text(
                         text = item.text,
                         fontSize = if (isHighlighted) 24.sp else 18.sp,
                         fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Bold,
                         color = item.color,
                         modifier = Modifier.animateContentSize(
                             animationSpec = tween(120, easing = EaseInOut)
                         )
                     )
                 }
             }
         }
     }
     ```

3. **Style checklist** to match iOS exactly:
   - [ ] Capsule (fully rounded) container shape
   - [ ] White background at ~96% opacity
   - [ ] Drop shadow (elevation 4-6dp)
   - [ ] 8-point spacing between characters
   - [ ] Normal: 18-20sp bold
   - [ ] Highlighted: 24-27sp heavy/black weight with scale effect
   - [ ] Smooth animation on highlight change (~120ms ease-in-out)
   - [ ] Color-coded text per direction/ring

#### Part 2: Right-Dial Preview

4. **Add right-dial preview logic** in `MyInputMethodService.kt` or the shared module:
   - When the user holds a direction on the **right dial only** (left dial is at center/NONE), compute a "column" preview:
     - For each of the 8 left-dial directions (N, NE, E, SE, S, SW, W, NW), get the character at the held right-dial position
     - Example (Logical, Normal, Right=N/Red): get char at index 0 from each left group → `a, f, k, p, u, z, 1, 6`
     - Example (Logical, Normal, Right=NE/Orange): get char at index 1 from each left group → `b, g, l, q, v, \, 2, 7`
   - Display these 8 characters in the preview bar, each colored with the **left-dial direction color** or a neutral scheme

5. **Add a helper method** to the shared module `KeyboardStateMachine` or `KeyboardLogic`:
   ```kotlin
   /**
    * Returns the characters across all left-directions at a specific right-direction index.
    * Used for right-dial-only preview (showing what each group offers at that color position).
    */
   fun getCharactersForRightDirection(rightDir: Direction): List<String>
   ```
   This iterates over all 8 left directions, calls the chord map for `(leftDir, rightDir)`, and collects the results.

6. **Update the touch handler** to detect right-dial-only holds:
   - If `leftDir == Direction.NONE && rightDir != Direction.NONE` → show right-dial preview
   - If `leftDir != Direction.NONE` → show left-dial preview (existing behavior)
   - If both are active → show left-dial preview with right-dial highlight (existing behavior)

### Acceptance Criteria

- [ ] Android preview bar uses a capsule (rounded oval) shape matching iOS
- [ ] White semi-transparent background with drop shadow
- [ ] Characters spaced evenly with 8dp gaps
- [ ] Highlighted character is larger and bolder with smooth animation
- [ ] Color coding matches iOS (per ring/direction)
- [ ] **Left-dial hold** shows all characters in that group (existing behavior, now with new styling)
- [ ] **Right-dial hold** shows the character at that color position across all 8 left-dial groups
- [ ] Right-dial preview works for both Logical and Efficiency layouts
- [ ] Right-dial preview works in Normal, Shifted, and Caps Lock modes
- [ ] Preview bar hides when both dials are released
- [ ] No jank or lag during preview rendering
- [ ] Tested on emulator and physical device

---

## ERICK-88 — iOS Right-Dial Preview + Preview Parity Check

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 4 |
| **Parent Epic** | ERICK-44 iOS IME Core |
| **Labels** | iOS, UI, keyboard, preview |
| **Dependencies** | None |

### Description

Verify and implement the right-dial preview on the iOS keyboard extension. Currently, the iOS preview bar shows characters when the left dial is held, and highlights the specific character when the right dial is also active. However, holding the **right dial alone** (without the left dial) does not show any preview.

Add the same right-dial-only preview behavior as described in ERICK-87: when the user holds a direction on the right dial only, show the character at that color/position across all 8 left-dial groups.

Also perform a visual parity check to ensure Android (after ERICK-87) and iOS preview bars are pixel-perfect identical.

### Current State

**iOS preview bar currently supports:**
- ✅ Left-dial hold → shows character group preview
- ✅ Left+right hold → highlights specific character
- ❌ Right-dial-only hold → no preview shown

### How to Get Started

1. **Open `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift`**:
   - In the touch handling logic, detect when only the right dial is active (left direction is `.none`)
   - Call the shared module's `getCharactersForRightDirection(rightDir)` method (from ERICK-87's shared module addition)
   - Update the `KeyboardViewModel` preview items with the "column" characters

2. **Open `ios/ERICK/ErickKeyBoard/JoystickView.swift`**:
   - In `KeyboardContainerView`, update the preview bar rendering to handle the right-dial preview case
   - The preview items should use a distinct color scheme for right-dial preview — consider coloring each character by its **left-dial direction** (N=group1 color, NE=group2 color, etc.) since the right-dial color is the common factor
   - Alternatively, show all items in the right-dial's color (e.g., all Red if holding right N)

3. **Update `KeyboardViewModel`** (or relevant state) to include a `previewMode` enum:
   ```swift
   enum PreviewMode {
       case none
       case leftDial      // Standard: shows characters for left direction
       case rightDial     // New: shows characters across groups for right direction
       case chord         // Both dials: left preview with right highlight
   }
   ```

4. **Parity check** — After ERICK-87 is complete, compare Android and iOS side by side:
   - [ ] Capsule shape and border radius identical
   - [ ] Background opacity identical (96% white)
   - [ ] Shadow depth and spread identical
   - [ ] Text sizes match (normal and highlighted)
   - [ ] Animation timing matches (~120ms)
   - [ ] Color coding matches
   - [ ] Right-dial preview content matches

### Acceptance Criteria

- [ ] Right-dial-only hold shows preview of characters across all left-dial groups at that position
- [ ] Right-dial preview works for both Logical and Efficiency layouts
- [ ] Right-dial preview works in Normal, Shifted, and Caps Lock modes
- [ ] Left-dial preview still works correctly (no regression)
- [ ] Left+right highlight still works correctly (no regression)
- [ ] Visual parity with Android (after ERICK-87) confirmed
- [ ] Tested on iOS Simulator and physical iPhone

---

## ERICK-89 — Custom Layout Creator — Shared Module Data Model & Persistence

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Developer 2 |
| **Sprint** | Sprint 4 |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, custom-layout |
| **Dependencies** | ERICK-73 (Efficiency layout — for LayoutType enum) |

### Description

**Note**: This ticket replaces/supersedes ERICK-80 from Sprint 3 with expanded scope to support user-created custom layouts.

Users should be able to create **multiple custom layouts** of their own choice. Each custom layout is a complete chord mapping where every chord/action (single swipe and chord) is mapped to a specific alphabet (a–z), symbol (!, @, etc.), or utility action (Backspace, HOME, etc.). Custom layouts should integrate seamlessly with the existing layout system.

This ticket covers the **shared module** data model, serialization, validation, and integration with `KeyboardLogic`.

### How to Get Started

1. **Create `android/shared/src/commonMain/kotlin/CustomLayout.kt`**:

   ```kotlin
   import kotlinx.serialization.Serializable
   
   /**
    * A user-created custom keyboard layout.
    * Defines the complete mapping of chord directions to characters/actions.
    */
   @Serializable
   data class CustomLayout(
       val id: String,                              // UUID string
       val name: String,                            // User-given name (e.g., "My Gaming Layout")
       val createdAt: Long,                         // Unix timestamp in millis
       val updatedAt: Long,                         // Unix timestamp in millis
       
       // Chord maps: leftDirection → list of up to 8 characters (one per right direction)
       // Index 0 = N (Red), 1 = NE (Orange), ..., 7 = NW
       val normalChordMap: Map<String, List<String>>,    // Direction name → chars
       val shiftedChordMap: Map<String, List<String>>,   // Direction name → shifted chars
       
       // Single-swipe maps: direction → action string
       // Action strings: "SPACE", "ENTER", "BACKSPACE", "HOME", "END",
       //                 "TOGGLE_SHIFT", "TOGGLE_CAPS", or a literal character like ","
       val normalSingleSwipeMap: Map<String, String>,
       val shiftedSingleSwipeMap: Map<String, String>
   )
   ```

2. **Create `android/shared/src/commonMain/kotlin/CustomLayoutManager.kt`**:

   ```kotlin
   class CustomLayoutManager {
       private val layouts = mutableListOf<CustomLayout>()
       
       // CRUD operations
       fun createLayout(name: String): CustomLayout  // Creates blank layout with unique ID
       fun updateLayout(layout: CustomLayout): Boolean
       fun deleteLayout(id: String): Boolean
       fun getLayout(id: String): CustomLayout?
       fun getAllLayouts(): List<CustomLayout>
       
       // Convenience: duplicate from existing built-in layout as starting point
       fun duplicateFromBuiltIn(sourceType: LayoutType, newName: String): CustomLayout
       
       // Validation: returns list of human-readable error strings (empty = valid)
       fun validateLayout(layout: CustomLayout): List<String>
       
       // Serialization
       fun toJson(layout: CustomLayout): String
       fun fromJson(json: String): CustomLayout?
       fun allToJson(): String          // Serialize all layouts
       fun loadAllFromJson(json: String) // Deserialize and replace all layouts
   }
   ```

3. **Validation rules** for `validateLayout()`:
   - Layout name must not be blank (max 30 characters)
   - Each direction in `normalChordMap` must have between 1-8 characters
   - No duplicate characters across the entire normal chord map
   - No duplicate characters across the entire shifted chord map
   - Single-swipe maps must map all 8 directions
   - At minimum, SPACE, ENTER, and BACKSPACE must exist in single-swipe map
   - Characters must be single Unicode characters (no multi-char strings except action names)
   - Return clear, user-friendly error messages like "Direction N has duplicate character 'a' at positions 1 and 3"

4. **Update `LayoutType` enum** in `KeyboardContracts.kt`:
   ```kotlin
   enum class LayoutType { LOGICAL, EFFICIENCY, CUSTOM }
   ```

5. **Update `KeyboardLogic.kt`**:
   - Add `setCustomLayout(layout: CustomLayout)` method
   - When `LayoutType.CUSTOM`, `getChordResult()` reads from the active `CustomLayout` maps
   - When `LayoutType.CUSTOM`, `getSingleSwipeResult()` reads from the custom single-swipe map

6. **Update `KeyboardStateMachine.kt`**:
   - Store reference to active `CustomLayout` (nullable, only set when CUSTOM type)
   - Wire `setCustomLayout()` to `KeyboardLogic`
   - Ensure `getPreviewText()` and `getCharactersForDirection()` respect custom layouts

7. **Add `kotlinx-serialization` dependency** if not already present:
   - In `android/shared/build.gradle.kts`, add `kotlin("plugin.serialization")` plugin
   - Add `implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")` dependency
   - If avoiding new dependencies, implement manual JSON parsing with `org.json` or string manipulation

### Acceptance Criteria

- [ ] `CustomLayout` data class defined with all fields for chord maps, single-swipe maps, and metadata
- [ ] `CustomLayoutManager` supports create, read, update, delete operations
- [ ] `duplicateFromBuiltIn()` correctly clones Logical or Efficiency layout into a new `CustomLayout`
- [ ] `validateLayout()` catches all invalid configurations with clear error messages
- [ ] JSON serialization/deserialization round-trips correctly (serialize → deserialize = same data)
- [ ] `LayoutType.CUSTOM` added to enum
- [ ] `KeyboardLogic.getChordResult()` resolves characters from custom layout when active
- [ ] `KeyboardLogic.getSingleSwipeResult()` resolves actions from custom layout when active
- [ ] Preview text functions work with custom layouts
- [ ] Multiple custom layouts can be stored simultaneously
- [ ] Shared module compiles for both Android and iOS targets
- [ ] Unit tests cover: CRUD operations, validation rules, serialization round-trip, chord resolution with custom data

---

## ERICK-90 — Custom Layout Creator UI — Android & iOS

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 5 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, custom-layout, UI |
| **Dependencies** | ERICK-89 |

### Description

**Note**: This ticket replaces/supersedes ERICK-81 and ERICK-82 from Sprint 3. Build the UI for creating, editing, and managing custom keyboard layouts on **both** Android and iOS. The UIs should be functionally identical with platform-appropriate styling.

From the Settings screen, users can tap "Manage Custom Layouts" to enter a layout management flow. They can create a new custom layout (from scratch or by duplicating a built-in layout), edit each chord mapping and single-swipe binding, validate, and save.

### How to Get Started

#### Android (Jetpack Compose)

1. **Create `CustomLayoutListScreen.kt`** in `android/app/src/main/java/com/vatoo/erick/`:

   **Layout List Screen** (entry point from Settings):
   - Title: "Custom Layouts"
   - List of saved custom layouts showing name + last modified date
   - "Create New Layout" button → asks for name, then navigates to editor
   - "Duplicate from Logical / Efficiency" buttons → creates a pre-filled layout
   - Swipe-to-delete with confirmation dialog
   - Tap a layout to edit

2. **Create `CustomLayoutEditorScreen.kt`**:

   **Editor Screen Structure**:
   - **Top bar**: Layout name (editable), Save button, Cancel button
   - **Tab selector**: "Normal Mode" / "Shifted Mode" toggle to switch between maps
   - **Chord Map Section** (main content):
     - 8 expandable rows, one per left-dial direction (N, NE, E, SE, S, SW, W, NW)
     - Each row shows the direction name + current characters (e.g., "North: a b c d e '")
     - Tap to expand → shows 8 text fields (one per right direction: Red, Orange, Yellow, Green, Blue, Indigo, Violet, Black)
     - Each text field has the color indicator next to it
     - Text field accepts a single character or can be cleared (empty = no binding)
     - Show a dropdown/picker for utility actions: Space, Enter, Backspace, Home, End, Tab, Delete, etc.
   - **Single-Swipe Section** (below chords):
     - 8 rows for single-swipe bindings
     - Each row has: direction label + dropdown picker (Action: Space/Enter/Backspace/...) OR text field (custom character)
   - **Validation Errors**: Red text at bottom, populated by `CustomLayoutManager.validateLayout()`
   - **Preview Section**: Small radial dial preview at the bottom showing how the layout looks

3. **Wire to Settings**:
   - In `SettingsScreen.kt`, under Layout section, add "Custom" radio option
   - Add "Manage Custom Layouts" button that navigates to `CustomLayoutListScreen`
   - When "Custom" is selected with no custom layouts, prompt to create one
   - When multiple custom layouts exist, show a dropdown to pick the active one

4. **Persistence**:
   - Use `PreferencesManager` to store the JSON from `CustomLayoutManager.allToJson()`
   - Add preference key `custom_layouts_json` (String)
   - Add preference key `active_custom_layout_id` (String)
   - Load on app start and keyboard service start

#### iOS (SwiftUI)

5. **Create `CustomLayoutListView.swift`** and **`CustomLayoutEditorView.swift`** in `ios/ERICK/ERICK/`:
   - Mirror the Android flow using SwiftUI `NavigationStack`, `List`, `Form`, `Section`
   - Use `.onDelete` modifier for swipe-to-delete
   - Use `NavigationLink` for edit navigation
   - Store JSON in App Group `UserDefaults` key `custom_layouts_json`

6. **Wire to iOS Settings**:
   - In `SettingsView.swift`, add "Custom" layout option and "Manage Custom Layouts" NavigationLink

### Acceptance Criteria

- [ ] Custom layout list screen accessible from Settings on both Android and iOS
- [ ] Can create a new blank layout with a custom name
- [ ] Can duplicate from Logical or Efficiency as a starting point
- [ ] Editor shows all 8 directions for chord mapping in both Normal and Shifted modes
- [ ] Each direction's character bindings are editable (up to 8 characters per direction)
- [ ] Single-swipe bindings editable with action picker or custom character input
- [ ] Validation errors displayed clearly before saving
- [ ] Layout saved and persisted across app restarts
- [ ] Active custom layout correctly applied to keyboard when selected
- [ ] Can delete custom layouts with confirmation
- [ ] Can rename layouts
- [ ] Android and iOS UIs functionally identical
- [ ] Custom layout works with left-handed mode
- [ ] No crashes during create/edit/save/delete flows

---

## ERICK-91 — Left-Handed Mode — UI Swap on Android & iOS

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 4 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, accessibility, UI |
| **Dependencies** | ERICK-74 (Left-handed shared logic from Sprint 3) |

### Description

Implement the **UI side** of left-handed mode on both Android and iOS. Sprint 3 ticket ERICK-74 handles the shared module logic (swapping `isLeft` in `handleTouch()`). This ticket handles:

1. **Swapping the visual positions** of the left and right dials in the keyboard layout
2. **Updating labels/characters** so the left dial shows colors and the right dial shows letter groups
3. **Reading the left-handed setting** from preferences and applying it on keyboard load
4. **Ensuring it works** with all layout types: Logical, Efficiency, and Custom

In left-handed mode:
- The **left dial** (physically on the left side of the screen) shows the **color segments** (Red, Orange, Yellow, etc.)
- The **right dial** (physically on the right side) shows the **letter groups** (a-e, f-j, etc.)
- The user uses their left thumb for color selection and right thumb for direction
- Single-swipe utility functions move to the left dial (since the "direction" dial is now on the right)

### How to Get Started

#### Android

1. **Open `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt`**:
   - Read `PreferencesManager.leftHandedMode` flow on service creation
   - When enabled, swap which `JoystickView` is `isRightSide = true`:
     ```kotlin
     if (leftHandedMode) {
         leftJoystickView.isRightSide = true    // Left view renders as color dial
         rightJoystickView.isRightSide = false   // Right view renders as letter groups
     } else {
         leftJoystickView.isRightSide = false
         rightJoystickView.isRightSide = true
     }
     ```
   - Also swap the `isLeft` flag when dispatching touch to the state machine, OR rely on ERICK-74's `setLeftHandedMode()` in the shared module to handle the logic swap

2. **Open `android/app/src/main/java/com/vatoo/erick/JoystickView.kt`**:
   - Ensure `isRightSide` property correctly switches between letter-group rendering and color-segment rendering
   - Verify that character labels update correctly when `isRightSide` is toggled
   - The concentric ring rendering (letters) and solid color segment rendering should already be gated on `isRightSide` — verify this

3. **Test that settings changes take effect immediately** (or after keyboard is re-opened):
   - Toggle left-handed mode in settings
   - Switch back to the keyboard
   - Dials should be swapped

#### iOS

4. **Open `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift`**:
   - Read `left_handed_mode` from App Group `UserDefaults` on `viewWillAppear()`
   - Pass it to `KeyboardContainerView` and to the shared module via `stateMachine.setLeftHandedMode()`

5. **Open `ios/ERICK/ErickKeyBoard/JoystickView.swift`**:
   - In `KeyboardContainerView`, swap the order of left/right joystick views:
     ```swift
     if leftHandedMode {
         // Right view (letters) first, then left view (colors)
         JoystickView(isRight: false, ...) // Now renders letter groups
         JoystickView(isRight: true, ...)  // Now renders colors
     } else {
         JoystickView(isRight: false, ...)
         JoystickView(isRight: true, ...)
     }
     ```
   - Or simply swap the `isRight` property assignment rather than view order

6. **Preview bar behavior**: In left-handed mode, the preview bar should still show characters based on the **letter-group dial** (which is now on the right side). Ensure the preview logic follows the "letter group" dial, not the physical side.

### Acceptance Criteria

- [ ] Left-handed mode visually swaps the dials on Android
- [ ] Left-handed mode visually swaps the dials on iOS
- [ ] Left dial shows color segments, right dial shows letter groups in left-handed mode
- [ ] Chord input produces correct characters (same chord = same character regardless of mode)
- [ ] Single-swipe utility functions move to the correct dial in left-handed mode
- [ ] Preview bar follows the letter-group dial (shows previews for the correct dial)
- [ ] Setting read from DataStore (Android) and UserDefaults (iOS) on keyboard load
- [ ] Setting changes apply without restarting the app (or at minimum, after closing and reopening the keyboard)
- [ ] Works correctly with Logical, Efficiency, and Custom layouts
- [ ] Tested on both Android and iOS

---

## ERICK-92 — Light/Dark Mode Theme Support — Shared Theme Definitions & Platform UI

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 5 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 4 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, UI, theming |
| **Dependencies** | None |

### Description

Implement full light/dark mode support for the ERICK keyboard and app on both platforms. Currently a "Dark Theme" toggle exists in settings on both platforms, but it has **no effect** on the actual keyboard UI or app screens.

The user should be able to choose between:
- **System Default** — follows the device's light/dark mode setting
- **Light Mode** — always light regardless of system setting
- **Dark Mode** — always dark regardless of system setting

This applies to:
1. **Keyboard IME view** — joystick backgrounds, text colors, preview bar, borders
2. **App screens** — Settings, Onboarding (MainActivity/ContentView)
3. **Preference persistence** — user choice saved and applied on every launch

### How to Get Started

#### Shared Theme Definitions

1. **Create `android/shared/src/commonMain/kotlin/ThemeColors.kt`**:
   ```kotlin
   data class KeyboardTheme(
       val keyboardBackground: Long,      // Hex color for keyboard surface
       val dialBackground: Long,          // Joystick circle fill
       val dialBorder: Long,              // Joystick circle border
       val dialSegmentInactive: Long,     // Inactive segment fill
       val dialSegmentActive: Long,       // Active segment fill
       val textPrimary: Long,             // Primary text/labels
       val textSecondary: Long,           // Secondary/dimmed text
       val previewBackground: Long,       // Preview bar background
       val previewText: Long,             // Preview bar text color
       val thumbColor: Long,              // Joystick thumb color
       val dividerColor: Long             // Segment divider lines
   )
   
   object ThemeColors {
       val light = KeyboardTheme(
           keyboardBackground = 0xFFE8E8E8,
           dialBackground = 0xFFF5F5F5,
           dialBorder = 0xFFCCCCCC,
           dialSegmentInactive = 0xFFE0E0E0,
           dialSegmentActive = 0xFFFFFFFF,
           textPrimary = 0xFF1A1A1A,
           textSecondary = 0xFF777777,
           previewBackground = 0xF5FFFFFF,  // white 96%
           previewText = 0xFF333333,
           thumbColor = 0xFFAAAAAA,
           dividerColor = 0xFFDDDDDD
       )
       
       val dark = KeyboardTheme(
           keyboardBackground = 0xFF1E1E1E,
           dialBackground = 0xFF2A2A2A,
           dialBorder = 0xFF444444,
           dialSegmentInactive = 0xFF333333,
           dialSegmentActive = 0xFF444444,
           textPrimary = 0xFFEEEEEE,
           textSecondary = 0xFF999999,
           previewBackground = 0xF5333333,  // dark gray 96%
           previewText = 0xFFEEEEEE,
           thumbColor = 0xFF666666,
           dividerColor = 0xFF444444
       )
   }
   ```

#### Android

2. **Update `PreferencesManager.kt`** — change `dark_theme` from `Boolean` to a `String` with three values:
   - `"system"` (default), `"light"`, `"dark"`
   - Add `Flow<String>` for `themeMode`

3. **Update `SettingsScreen.kt`** — replace the toggle with a 3-option radio group:
   ```kotlin
   // Theme Mode
   RadioGroup(
       options = listOf("System Default", "Light", "Dark"),
       selected = themeMode,
       onSelect = { preferencesManager.setThemeMode(it) }
   )
   ```

4. **Update `SettingsActivity.kt` and `MainActivity.kt`** — apply the theme:
   ```kotlin
   val themeMode by preferencesManager.themeMode.collectAsState(initial = "system")
   val darkTheme = when (themeMode) {
       "dark" -> true
       "light" -> false
       else -> isSystemInDarkTheme()
   }
   ERICKTheme(darkTheme = darkTheme) {
       // ... content
   }
   ```

5. **Update `MyInputMethodService.kt`** — read theme preference and pass to joystick views:
   - Read `themeMode` from `PreferencesManager`
   - Resolve to light/dark based on system setting or user override
   - Pass `KeyboardTheme` colors to `JoystickView` for rendering
   - Apply to preview bar colors

6. **Update `JoystickView.kt`** — accept theme colors:
   - Add a `theme: KeyboardTheme` property
   - Replace all hardcoded color values with theme references
   - Update `onDraw()` to use `theme.dialBackground`, `theme.textPrimary`, etc.

#### iOS

7. **Update `SettingsView.swift`** — change dark theme toggle to 3-option picker:
   ```swift
   Picker("Theme", selection: $themeMode) {
       Text("System Default").tag("system")
       Text("Light").tag("light")
       Text("Dark").tag("dark")
   }
   .pickerStyle(.segmented)
   ```
   - Store as `@AppStorage("theme_mode")` with default `"system"`

8. **Update `ERICKApp.swift`** — apply theme:
   ```swift
   @AppStorage("theme_mode") var themeMode = "system"
   
   var body: some Scene {
       WindowGroup {
           ContentView()
               .preferredColorScheme(colorScheme)
       }
   }
   
   var colorScheme: ColorScheme? {
       switch themeMode {
       case "dark": return .dark
       case "light": return .light
       default: return nil  // follows system
       }
   }
   ```

9. **Update `KeyboardViewController.swift`** — apply theme to keyboard extension:
   - Read `theme_mode` from App Group UserDefaults
   - Resolve to light/dark
   - Pass theme colors to `KeyboardContainerView` and `JoystickView`

10. **Update `JoystickView.swift`** — accept theme colors from `KeyboardTheme`:
    - Replace hardcoded background/text colors with theme references
    - Update preview bar to respect dark mode (dark background + light text)

### Acceptance Criteria

- [ ] Three-option theme selector in Settings: System Default, Light, Dark
- [ ] "System Default" follows device light/dark mode setting
- [ ] "Light" forces light mode regardless of system setting
- [ ] "Dark" forces dark mode regardless of system setting
- [ ] Keyboard IME view updates: background, text, borders, preview bar
- [ ] App screens (Settings, Onboarding) update with theme
- [ ] User preference saved and applied on every launch
- [ ] Settings change takes effect immediately (no app restart)
- [ ] Theme colors are visually pleasant and maintain readability
- [ ] Color direction indicators (Red, Orange, etc.) remain visible in both themes
- [ ] Implemented on both Android and iOS
- [ ] No contrast/readability issues in either mode

---

## ERICK-93 — Accelerating Backspace — Hold to Delete Words

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Developer 2 |
| **Sprint** | Sprint 4 |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, input, UX |
| **Dependencies** | None |

### Description

Implement Google Keyboard–style accelerating backspace behavior. When the user performs the backspace single-swipe and **holds** the right dial in the backspace direction (West), the keyboard should:

1. **First ~300ms**: Delete one character (like a normal backspace tap)
2. **After 300ms hold**: Start repeating character deletion at ~100ms intervals
3. **After ~1.5s of continuous hold**: Switch to **word deletion** — delete one word at a time at ~200ms intervals
4. **After ~3s of continuous hold**: Accelerate word deletion to ~100ms intervals

This mimics the Google Keyboard behavior where holding backspace starts slow (characters), then accelerates to word-level deletion.

### How to Get Started

1. **Open `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt`**:
   - Currently, backspace is triggered once via `getSingleSwipeResult()` → `InputAction.BACKSPACE` when the right-only swipe to West is detected
   - Add a repeating backspace mechanism using coroutines:

   ```kotlin
   private var backspaceJob: Job? = null
   private var backspaceStartTime: Long = 0
   
   private fun startBackspaceRepeat() {
       backspaceJob?.cancel()
       backspaceStartTime = currentTimeMillis()
       
       backspaceJob = scope.launch {
           // Initial single delete
           delegate?.sendInputAction(InputAction.BACKSPACE)
           delay(300) // Initial delay before repeat
           
           // Character-level repeat
           while (isActive) {
               val elapsed = currentTimeMillis() - backspaceStartTime
               when {
                   elapsed > 3000 -> {
                       // Fast word deletion
                       delegate?.sendInputAction(InputAction.DELETE_WORD)
                       delay(100)
                   }
                   elapsed > 1500 -> {
                       // Word deletion
                       delegate?.sendInputAction(InputAction.DELETE_WORD)
                       delay(200)
                   }
                   else -> {
                       // Character deletion
                       delegate?.sendInputAction(InputAction.BACKSPACE)
                       delay(100)
                   }
                }
           }
       }
   }
   
   private fun stopBackspaceRepeat() {
       backspaceJob?.cancel()
       backspaceJob = null
   }
   ```

2. **Add `InputAction.DELETE_WORD`** to `KeyboardContracts.kt`:
   ```kotlin
   enum class InputAction {
       // ... existing values ...
       DELETE_WORD  // Delete one word backward
   }
   ```

3. **Detect hold vs. release** in the state machine:
   - When a right-only swipe is detected at West (Backspace direction):
     - On `ACTION_DOWN` / direction detected → call `startBackspaceRepeat()`
     - On `ACTION_UP` / return to center → call `stopBackspaceRepeat()`
   - The existing touch handler already tracks action down/up — hook into the right-only swipe detection path
   - **Important**: Do NOT trigger `startBackspaceRepeat()` for regular chord inputs where the right dial goes West — only for **right-dial-only** swipes (single-swipe backspace)

4. **Implement `DELETE_WORD` on Android** in `MyInputMethodService.kt`:
   ```kotlin
   InputAction.DELETE_WORD -> {
       // Get text before cursor, find last word boundary
       val beforeCursor = currentInputConnection?.getTextBeforeCursor(50, 0)?.toString() ?: return
       val lastSpace = beforeCursor.trimEnd().lastIndexOf(' ')
       val charsToDelete = if (lastSpace == -1) beforeCursor.length else beforeCursor.length - lastSpace - 1
       if (charsToDelete > 0) {
           currentInputConnection?.deleteSurroundingText(charsToDelete, 0)
       }
   }
   ```

5. **Implement `DELETE_WORD` on iOS** in `KeyboardViewController.swift`:
   ```swift
   case .deleteWord:
       // iOS word deletion: delete backward until whitespace is found
       var deleted = 0
       // First, skip trailing spaces
       while let before = textDocumentProxy.documentContextBeforeInput,
             before.hasSuffix(" ") {
           textDocumentProxy.deleteBackward()
           deleted += 1
           if deleted > 100 { break } // safety limit
       }
       // Then delete until next space or beginning
       while let before = textDocumentProxy.documentContextBeforeInput,
             !before.isEmpty, !before.hasSuffix(" ") {
           textDocumentProxy.deleteBackward()
           deleted += 1
           if deleted > 100 { break }
       }
   ```

6. **Platform time function**: The shared module needs `currentTimeMillis()`. Use `expect/actual`:
   ```kotlin
   // commonMain
   expect fun currentTimeMillis(): Long
   
   // androidMain
   actual fun currentTimeMillis(): Long = System.currentTimeMillis()
   
   // iosMain
   actual fun currentTimeMillis(): Long = 
       (NSDate().timeIntervalSince1970 * 1000).toLong()
   ```

### Acceptance Criteria

- [ ] Single backspace swipe still works normally (one character deleted)
- [ ] Holding backspace direction starts repeating after ~300ms
- [ ] Character deletion repeats at ~100ms intervals (first 1.5s)
- [ ] After ~1.5s of hold, switches to word-level deletion at ~200ms intervals
- [ ] After ~3s of hold, word deletion accelerates to ~100ms intervals
- [ ] Releasing the dial immediately stops all deletion
- [ ] `DELETE_WORD` action implemented on Android (using `InputConnection`)
- [ ] `DELETE_WORD` action implemented on iOS (using `textDocumentProxy`)
- [ ] Does NOT trigger during chord input (only right-dial-only backspace swipe)
- [ ] No race conditions or double-deletion bugs
- [ ] Shared module compiles for both targets
- [ ] Tested thoroughly on both platforms

---

## ERICK-94 — Font Selection — Settings & Keyboard UI

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 5 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, UI, fonts, accessibility |
| **Dependencies** | None |

### Description

Add a font selection option to the Settings screen on both Android and iOS. Users can choose from 4 font options that affect all text displayed **on the keyboard surface** (dial labels, preview bar, etc.):

1. **System Default** — platform default sans-serif font
2. **Verdana** — clean, readable sans-serif (preinstalled on most devices)
3. **Georgia** — elegant serif font (preinstalled on most devices)
4. **OpenDyslexic** — dyslexia-friendly font with weighted bottoms and distinct letter shapes (free and open-source, Apache 2.0 license)

The font setting should be saved with other preferences and applied across the keyboard UI.

### How to Get Started

#### Font Acquisition

1. **OpenDyslexic font**:
   - Download from: https://opendyslexic.org/ (Apache 2.0 license — free for commercial use)
   - Download `OpenDyslexic-Regular.otf` (and Bold variant if desired)
   - **Android**: Place in `android/app/src/main/res/font/opendyslexic_regular.otf`
   - **iOS**: Add to the `ErickKeyBoard` target's "Copy Bundle Resources" build phase
   - Add the font filename to `Info.plist` under `UIAppFonts` (iOS)

2. **Verdana and Georgia**: These are system fonts on both Android and iOS — no bundling needed. Access via:
   - Android: `Typeface.create("verdana", Typeface.NORMAL)` or `ResourcesCompat.getFont()`
   - iOS: `UIFont(name: "Verdana", size: 16)` or `Font.custom("Verdana", size: 16)`

#### Android Implementation

3. **Update `PreferencesManager.kt`**:
   - Add preference key `font_choice` with values: `"system"`, `"verdana"`, `"georgia"`, `"opendyslexic"`
   - Add `Flow<String>` for `fontChoice`

4. **Update `SettingsScreen.kt`**:
   - Add a "Font" section with 4 radio options
   - Show font name + a small preview text ("The quick brown fox") in each font
   - When "OpenDyslexic" is selected, show a subtitle: "Dyslexia-friendly font with weighted letter bottoms"

5. **Create a font utility `FontManager.kt`**:
   ```kotlin
   object FontManager {
       fun getTypeface(context: Context, fontChoice: String): Typeface {
           return when (fontChoice) {
               "verdana" -> Typeface.create("sans-serif", Typeface.NORMAL)  // Closest match
               "georgia" -> Typeface.create("serif", Typeface.NORMAL)
               "opendyslexic" -> ResourcesCompat.getFont(context, R.font.opendyslexic_regular) 
                   ?: Typeface.DEFAULT
               else -> Typeface.DEFAULT
           }
       }
   }
   ```

6. **Update `JoystickView.kt`**:
   - Add a `typeface: Typeface` property
   - Apply `paint.typeface = typeface` in `onDraw()` for all text rendering

7. **Update `MyInputMethodService.kt`**:
   - Read font preference from `PreferencesManager`
   - Apply typeface to `JoystickView` instances and preview bar `TextView`

#### iOS Implementation

8. **Update `SettingsView.swift`**:
   - Add font picker with 4 options
   - Preview text in each font
   - Store as `@AppStorage("font_choice")` in App Group UserDefaults

9. **Update `JoystickView.swift`** and `KeyboardContainerView`:
   - Read font preference from UserDefaults
   - Apply `Font.custom("fontName", size: ...)` to all text elements
   - For OpenDyslexic: `Font.custom("OpenDyslexic", size: ...)`

10. **Register OpenDyslexic** in `Info.plist` for the keyboard extension:
    ```xml
    <key>UIAppFonts</key>
    <array>
        <string>OpenDyslexic-Regular.otf</string>
    </array>
    ```

### Acceptance Criteria

- [ ] Font selection in Settings with 4 options: System, Verdana, Georgia, OpenDyslexic
- [ ] Font preview shown next to each option ("The quick brown fox")
- [ ] OpenDyslexic labeled as "Dyslexia-friendly"
- [ ] Selected font applied to all keyboard surface text (dial labels, preview bar)
- [ ] Font preference saved and applied on every keyboard launch
- [ ] OpenDyslexic font file bundled with the app (Android `res/font/`, iOS bundle)
- [ ] Font changes reflected immediately after changing the setting
- [ ] No text clipping or layout issues with any font selection
- [ ] All 4 fonts render correctly on both Android and iOS
- [ ] OpenDyslexic font license (Apache 2.0) compliant

---

## ERICK-95 — Shift & Caps Lock Visual Indicators

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 5 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, UI, keyboard |
| **Dependencies** | None |

### Description

Currently, when Shift or Caps Lock is toggled via single-swipe, there is **no clear visual indicator** on the keyboard to show the user they are in Shifted or Caps Locked mode. The `onModeChanged()` delegate callback already fires and updates `keyboardMode` which changes the displayed characters (uppercase), but the overall keyboard appearance doesn't change enough to make the mode obvious.

We need **prominent, unmistakable visual indicators** for:
1. **Shift active** (one-shot uppercase): Temporary indicator that auto-clears after one character
2. **Caps Lock active** (persistent uppercase): Persistent indicator until toggled off

### How to Get Started

#### Indicator Design

1. **Shift Active (one-shot)**:
   - Add a small **upward arrow icon (⬆)** in the top corner of the keyboard (near the preview bar)
   - Arrow should be filled/solid with the primary theme color
   - Add a subtle **border glow** or **highlight outline** around the right dial (where shift was triggered)
   - All characters on the left dial should switch to uppercase (this already happens via `keyboardMode`)
   - The indicator should automatically disappear after one character is typed

2. **Caps Lock Active (persistent)**:
   - Add a **double upward arrow icon (⬆⬆)** or a **highlighted "CAPS" badge** near the preview bar
   - Change the keyboard **background color slightly** (e.g., add a subtle blue or yellow tint)
   - Add a **persistent border glow** around the keyboard area (more prominent than shift)
   - Optionally, add a small **"CAPS" label** or **underline** on the mode indicator
   - All characters switch to uppercase (already happens)
   - Indicator persists until Caps Lock is toggled off

#### Android Implementation

3. **Open `android/app/src/main/java/com/vatoo/erick/JoystickView.kt`**:
   - The `keyboardMode` property is already set by `MyInputMethodService.onModeChanged()`
   - Add rendering logic in `onDraw()` for mode indicators:
     - When `SHIFTED`: draw a filled arrow icon at top-right, add a colored border
     - When `CAPS_LOCKED`: draw a double arrow + "CAPS" badge, add persistent border + background tint

4. **Open `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt`**:
   - In `onModeChanged()`, besides updating joystick mode, also update a mode indicator view:
     ```kotlin
     override fun onModeChanged(mode: KeyboardMode) {
         leftJoystick.keyboardMode = mode
         rightJoystick.keyboardMode = mode
         updateModeIndicator(mode)
     }
     
     private fun updateModeIndicator(mode: KeyboardMode) {
         when (mode) {
             KeyboardMode.NORMAL -> {
                 modeIndicatorView.visibility = View.GONE
             }
             KeyboardMode.SHIFTED -> {
                 modeIndicatorView.visibility = View.VISIBLE
                 modeIndicatorView.text = "⬆"
                 modeIndicatorView.setBackgroundColor(shiftColor)
             }
             KeyboardMode.CAPS_LOCKED -> {
                 modeIndicatorView.visibility = View.VISIBLE
                 modeIndicatorView.text = "⬆ CAPS"
                 modeIndicatorView.setBackgroundColor(capsLockColor)
             }
         }
     }
     ```
   - Add a `modeIndicatorView` (small `TextView` or `ImageView`) to the keyboard layout, positioned at the top-right area near the preview bar

5. **Update `keyboard_simple.xml`**:
   - Add a mode indicator view element:
     ```xml
     <TextView
         android:id="@+id/mode_indicator"
         android:layout_width="wrap_content"
         android:layout_height="wrap_content"
         android:visibility="gone"
         android:padding="4dp"
         android:textSize="14sp"
         android:textStyle="bold"
         android:background="@drawable/indicator_background" />
     ```

#### iOS Implementation

6. **Open `ios/ERICK/ErickKeyBoard/JoystickView.swift`**:
   - In `KeyboardContainerView`, add a mode indicator overlay:
     ```swift
     if viewModel.keyboardMode == .shifted {
         HStack(spacing: 2) {
             Image(systemName: "arrow.up")
                 .font(.system(size: 14, weight: .bold))
                 .foregroundColor(.blue)
         }
         .padding(.horizontal, 8)
         .padding(.vertical, 4)
         .background(Capsule().fill(Color.blue.opacity(0.15)))
         .transition(.scale.combined(with: .opacity))
     }
     
     if viewModel.keyboardMode == .capsLocked {
         HStack(spacing: 2) {
             Image(systemName: "arrow.up")
             Image(systemName: "arrow.up")
             Text("CAPS")
                 .font(.system(size: 12, weight: .heavy))
         }
         .foregroundColor(.orange)
         .padding(.horizontal, 8)
         .padding(.vertical, 4)
         .background(Capsule().fill(Color.orange.opacity(0.15)))
         .transition(.scale.combined(with: .opacity))
     }
     ```

7. **Animate transitions**: Use SwiftUI's `withAnimation` for smooth appearance/disappearance of indicators.

### Acceptance Criteria

- [ ] **Shift indicator**: visible arrow icon when Shift is active
- [ ] **Shift indicator**: auto-disappears after one character is typed
- [ ] **Caps Lock indicator**: visible double arrow + "CAPS" badge when Caps Lock is active
- [ ] **Caps Lock indicator**: persists until Caps Lock is toggled off
- [ ] Indicators are easily visible and unmistakable (not subtle)
- [ ] Character labels on dials switch to uppercase (already works — verify no regression)
- [ ] Optional: keyboard background tint changes slightly for Caps Lock
- [ ] Implemented on both Android and iOS
- [ ] Indicators work correctly with all layout types (Logical, Efficiency, Custom)
- [ ] No layout clipping or overlap with preview bar or other elements
- [ ] Tested Normal → Shift → type char → Normal, and Normal → Caps → type multiple → toggle off

---

## ERICK-96 — Symbols Keyboard Shortcut Button

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Developer 2 |
| **Sprint** | Sprint 5 |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, android, iOS, keyboard |
| **Dependencies** | None |

### Description

Add a quick-access button or shortcut for a **symbols keyboard layer**. Currently, symbols are accessible via chords (e.g., left SW + right directions give `z \ [ ] \``), but common symbols like `! @ # $ % ^ & * ( )` require specific chords that users may not remember easily.

The goal is to provide a **symbols toggle button** on the keyboard surface that temporarily overlays or switches the keyboard to a symbols-only mode, similar to how Google Keyboard has a `?123` button.

### How to Get Started

1. **Define a Symbols Layer** in the shared module:
   - Create a `SymbolsLayer` constant or enum in `KeyboardLogic.kt` or a new file
   - The symbols layer replaces all letter chords with commonly used symbols:
     ```
     N:  !  @  #  $  %  ^
     NE: &  *  (  )  -  _
     E:  +  =  {  }  [  ]
     SE: |  \  :  ;  "  '
     S:  <  >  ,  .  ?  /
     SW: ~  `  ©  ®  €  £
     W:  1  2  3  4  5
     NW: 6  7  8  9  0
     ```
   - This layer is NOT a new `LayoutType` — it's a temporary overlay that works with any layout
   - Numbers row (W, NW) stays the same as the regular layout

2. **Add `KeyboardMode.SYMBOLS`** or use a separate flag:
   ```kotlin
   // Option A: Add to KeyboardMode (simpler)
   enum class KeyboardMode { NORMAL, SHIFTED, CAPS_LOCKED, SYMBOLS }
   
   // Option B: Separate toggle (more flexible)
   var symbolsLayerActive: Boolean = false
   ```
   - When symbols mode is active, `getChordResult()` uses the symbols map instead of the regular/efficiency/custom map
   - Single-swipe actions remain unchanged (Space, Enter, Backspace still work normally)

3. **Toggle mechanism in `KeyboardStateMachine`**:
   - Add `toggleSymbolsLayer()` method
   - When active, a chord produces a symbol instead of a letter
   - After one symbol is typed, auto-deactivate (like Shift's one-shot behavior)
   - OR: stay in symbols mode until the user toggles it off again (configurable)
   - Provide `isSymbolsActive(): Boolean` for UI to query state

4. **Add symbols button to keyboard UI (both platforms)**:

   **Android** (`MyInputMethodService.kt` or `keyboard_simple.xml`):
   - Add a small button labeled `#+=` or `SYM` at the bottom-left or bottom-right of the keyboard
   - On tap: call `stateMachine.toggleSymbolsLayer()`
   - Button should visually indicate active state (highlighted background)

   **iOS** (`KeyboardContainerView` in `JoystickView.swift`):
   - Add a `Button` labeled `#+=` near the globe/next keyboard button
   - On tap: call `stateMachine.toggleSymbolsLayer()` via viewModel

5. **Visual feedback**: When symbols mode is active:
   - The dial labels update to show symbol characters instead of letters
   - The `SYM` button appears pressed/highlighted
   - A small indicator (similar to Shift indicator from ERICK-95) shows "SYM" mode is active

### Acceptance Criteria

- [ ] Symbols layer defined with common symbols mapped to chord positions
- [ ] `SYM` or `#+=` button visible on the keyboard surface (both platforms)
- [ ] Tapping the button toggles symbols mode on/off
- [ ] When active, chord input produces symbols instead of letters
- [ ] Dial labels update to show symbols when mode is active
- [ ] Single-swipe actions (Space, Enter, Backspace) still work in symbols mode
- [ ] Numbers row (W, NW directions) unchanged in symbols mode
- [ ] Auto-deactivate after one symbol typed (one-shot behavior, like Shift)
- [ ] Visual indicator shows symbols mode is active
- [ ] Works with all base layouts (Logical, Efficiency, Custom)
- [ ] Shared module compiles for both targets

---

## ERICK-97 — Emoji Keyboard Integration

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 5 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, UI, keyboard, emoji |
| **Dependencies** | None |

### Description

Add an emoji keyboard shortcut/button that launches the system emoji picker or displays a categorized emoji grid within the ERICK keyboard. Similar to how Google Keyboard has a smiley face button that opens an emoji panel.

### How to Get Started

#### Approach Options (Choose One Per Platform)

**Option A — Use System Emoji Picker (Recommended for v1)**:
- **Android**: No built-in system emoji picker for IMEs. Need to build a simple emoji grid.
- **iOS**: No built-in emoji picker API for keyboard extensions either. Need to build a simple emoji grid.

**Option B — Build a Simple Emoji Grid (Both Platforms)**:
This is the recommended approach for consistency.

#### Implementation

1. **Create an emoji data source** in the shared module or as a platform resource:
   - Create `android/shared/src/commonMain/kotlin/EmojiData.kt`:
     ```kotlin
     object EmojiData {
         data class EmojiCategory(val name: String, val icon: String, val emojis: List<String>)
         
         val categories = listOf(
             EmojiCategory("Smileys", "😀", listOf(
                 "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂",
                 "🙂", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗",
                 "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭",
                 "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "😮‍💨",
                 "🤥", "😌", "😔", "😪", "🤤", "😴", "😷", "🤒"
                 // ... more
             )),
             EmojiCategory("Gestures", "👋", listOf(
                 "👋", "🤚", "✋", "🖖", "👌", "🤌", "🤏", "✌️",
                 "🤞", "🤟", "🤘", "🤙", "👈", "👉", "👆", "👇",
                 "☝️", "👍", "👎", "✊", "👊", "🤛", "🤜", "👏"
                 // ... more
             )),
             EmojiCategory("Hearts", "❤️", listOf(
                 "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍",
                 "💔", "💕", "💞", "💓", "💗", "💖", "💘", "💝"
             )),
             EmojiCategory("Animals", "🐶", listOf(
                 "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼",
                 "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔"
             )),
             EmojiCategory("Food", "🍕", listOf(
                 "🍎", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
                 "🍕", "🍔", "🍟", "🌭", "🍿", "🧁", "🍩", "🍪"
             )),
             EmojiCategory("Objects", "💡", listOf(
                 "⌚", "📱", "💻", "⌨️", "🖥️", "📷", "🔑", "💡",
                 "📚", "✏️", "📎", "🔧", "💰", "🎵", "🎮", "🏆"
             )),
             EmojiCategory("Flags", "🏳️", listOf(
                 "🏁", "🚩", "🎌", "🏴", "🏳️", "🏳️‍🌈", "🏳️‍⚧️",
                 "🇺🇸", "🇬🇧", "🇨🇦", "🇦🇺", "🇩🇪", "🇫🇷", "🇯🇵", "🇰🇷"
             ))
         )
     }
     ```

2. **Add emoji button to keyboard surface (both platforms)**:
   - Place a **smiley face icon** (😊 or a simple outlined smiley) button near the existing keyboard controls
   - Position: bottom-left or bottom-right, near the globe/settings button
   - Tapping it replaces the joystick area with the emoji grid
   - Tapping it again (or a "ABC" back button) returns to the normal keyboard

3. **Android emoji grid** (`EmojiPanelView.kt` or Composable):
   ```kotlin
   @Composable
   fun EmojiPanel(onEmojiSelected: (String) -> Unit, onClose: () -> Unit) {
       Column {
           // Category tabs at top
           ScrollableTabRow(selectedTabIndex = selectedCategory) {
               EmojiData.categories.forEachIndexed { index, cat ->
                   Tab(
                       selected = selectedCategory == index,
                       onClick = { selectedCategory = index },
                       text = { Text(cat.icon, fontSize = 20.sp) }
                   )
               }
           }
           // Emoji grid
           LazyVerticalGrid(columns = GridCells.Fixed(8)) {
               items(EmojiData.categories[selectedCategory].emojis) { emoji ->
                   Text(
                       text = emoji,
                       fontSize = 28.sp,
                       modifier = Modifier
                           .clickable { onEmojiSelected(emoji) }
                           .padding(8.dp),
                       textAlign = TextAlign.Center
                   )
               }
           }
       }
   }
   ```

4. **iOS emoji grid** (`EmojiPanelView.swift`):
   ```swift
   struct EmojiPanelView: View {
       let onEmojiSelected: (String) -> Void
       let onClose: () -> Void
       @State private var selectedCategory = 0
       
       var body: some View {
           VStack(spacing: 0) {
               // Category tabs
               ScrollView(.horizontal, showsIndicators: false) {
                   HStack(spacing: 12) {
                       ForEach(Array(EmojiData.shared.categories.enumerated()), id: \.offset) { index, cat in
                           Button(cat.icon) { selectedCategory = index }
                               .opacity(selectedCategory == index ? 1.0 : 0.5)
                       }
                   }
                   .padding(.horizontal)
               }
               // Emoji grid
               LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 8)) {
                   ForEach(currentEmojis, id: \.self) { emoji in
                       Button(emoji) { onEmojiSelected(emoji) }
                           .font(.system(size: 28))
                   }
               }
           }
       }
   }
   ```

5. **Wire the emoji panel to the IME**:
   - When an emoji is tapped → call `commitText(emoji)` to insert it
   - The emoji panel overlays the joystick area (not a separate window)
   - Keep the height consistent with the keyboard height (~280dp)

6. **Recently used emojis** (stretch goal):
   - Track the last 16 emojis the user inserted
   - Show "Recent" as the first category tab
   - Persist in DataStore / UserDefaults

### Acceptance Criteria

- [ ] Emoji button (smiley face icon) visible on the keyboard near other shortcut buttons
- [ ] Tapping emoji button shows categorized emoji grid replacing the joystick area
- [ ] At least 6 emoji categories with ~16-40 emojis each
- [ ] Tapping an emoji inserts it into the active text field
- [ ] "ABC" or back button returns to the normal joystick keyboard
- [ ] Emoji grid scrollable within category
- [ ] Category tabs scrollable horizontally
- [ ] Emoji panel respects light/dark theme
- [ ] Implemented on both Android and iOS
- [ ] No crashes when switching between emoji and keyboard modes
- [ ] Keyboard height remains consistent when switching
- [ ] (Stretch) Recently used emojis tracked and shown first

---

## ERICK-98 — CozyTyper-Style Typing Game

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 5 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, UI, game |
| **Dependencies** | None |

### Description

Add a calm, peaceful typing practice game accessible from the app home screen. When the user types "start" into the test text box on the home page, they are navigated to the game screen. The game is inspired by [CozyTyper](https://store.steampowered.com/app/2063420/CozyTyper/) — a relaxing environment where users type motivational quotes to practice with the ERICK keyboard.

A hint text below the test box on the home screen should say: *"Type 'start' to begin the typing game 🎮"*

### Game Design

**Screens**: Home → (type "start") → Game Screen

**Game Screen Layout**:
- **Background**: Soft gradient or calming solid color (light lavender, soft green, or warm beige — follows light/dark mode)
- **Quote Display**: Large, centered text showing the quote to type
  - Characters not yet typed: default color
  - Correctly typed characters: green
  - Incorrectly typed characters: red with subtle shake animation
  - Current character position: underlined or highlighted cursor
- **Typing Area**: Invisible text input that captures keyboard input (not a visible text box — the quote display IS the visual)
- **Stats Bar** (at top or bottom):
  - **WPM** (Words Per Minute): calculated as (characters typed / 5) / minutes elapsed
  - **Accuracy**: (correct keystrokes / total keystrokes) × 100%
  - **Streak**: Number of consecutively completed quotes without errors (resets on a mistake)
- **Quote Navigation**: After completing a quote correctly, auto-advance to the next quote with a brief celebration animation (confetti or gentle sparkle)
- **Exit**: Back button or "Quit" button returns to the home screen

### Quote Collection

Include at least **50 curated quotes** — relaxing, motivating, humorous, and positive:

```kotlin
val quotes = listOf(
    // Motivational
    "Be yourself; everyone else is already taken.",
    "It's not the load that breaks you down, it's the way you carry it.",
    "For every minute you are angry, you lose sixty seconds of happiness.",
    "The only way to do great work is to love what you do.",
    "In the middle of difficulty lies opportunity.",
    "Believe you can and you're halfway there.",
    "The best time to plant a tree was twenty years ago. The second best time is now.",
    "You are never too old to set another goal or to dream a new dream.",
    "Happiness is not something ready made. It comes from your own actions.",
    "Every moment is a fresh beginning.",
    
    // Relaxing / Calm
    "Breathe in deeply to bring your mind home to your body.",
    "Almost everything will work again if you unplug it for a few minutes, including you.",
    "The quieter you become, the more you can hear.",
    "Nature does not hurry, yet everything is accomplished.",
    "Slow down and everything you are chasing will come around and catch you.",
    "Rest is not idleness, and to lie sometimes on the grass under trees on a summer's day is by no means a waste of time.",
    "The greatest weapon against stress is our ability to choose one thought over another.",
    "Your calm mind is the ultimate weapon against your challenges.",
    "Calm mind brings inner strength and self-confidence.",
    "Life is ten percent what happens to you and ninety percent how you respond to it.",
    
    // Humorous / Light
    "I am not lazy, I am on energy saving mode.",
    "Life is short. Smile while you still have teeth.",
    "I would like to apologize to anyone I have not yet offended. Please be patient. I will get to you shortly.",
    "The road to success is always under construction.",
    "I used to think I was indecisive, but now I'm not so sure.",
    "Age is of no importance unless you are a cheese.",
    "If at first you don't succeed, then skydiving definitely isn't for you.",
    "A clear conscience is usually the sign of a bad memory.",
    "I'm not arguing, I'm just explaining why I'm right.",
    "Behind every great man is a woman rolling her eyes.",
    
    // Positive / Uplifting
    "You are enough just as you are.",
    "Every day may not be good, but there is something good in every day.",
    "Stars can't shine without darkness.",
    "You are braver than you believe, stronger than you seem, and smarter than you think.",
    "Difficult roads often lead to beautiful destinations.",
    "Be the reason someone smiles today.",
    "The sun will rise and we will try again.",
    "You make the world a better place just by being in it.",
    "One small positive thought can change your whole day.",
    "You don't have to be perfect to be amazing.",
    
    // Short & Sweet
    "Keep going.",
    "This too shall pass.",
    "You've got this.",
    "Dream big.",
    "Stay curious.",
    "Be kind.",
    "Just breathe.",
    "Make it happen.",
    "Choose joy.",
    "Progress, not perfection."
)
```

### How to Get Started

#### Android

1. **Add hint text** to `MainActivity.kt`:
   - Below the test `TextField`, add a subtle hint:
     ```kotlin
     Text(
         "Type 'start' to begin the typing game 🎮",
         style = MaterialTheme.typography.bodySmall,
         color = Color.Gray,
         modifier = Modifier.padding(top = 4.dp)
     )
     ```
   - Monitor the `TextField` value. When it equals "start" (case-insensitive), navigate to the game screen:
     ```kotlin
     LaunchedEffect(textFieldValue) {
         if (textFieldValue.text.trim().equals("start", ignoreCase = true)) {
             navController.navigate("typing_game")
         }
     }
     ```

2. **Create `TypingGameScreen.kt`**:
   - Implement the game layout with Jetpack Compose
   - Use `remember` for game state: current quote index, typed characters, WPM, accuracy, streak
   - Calculate WPM in real-time: `(totalCharsTyped / 5.0) / (elapsedMs / 60000.0)`
   - Character-by-character comparison: compare each typed char with the expected char at that position
   - Use `Color.Green` for correct, `Color.Red` for incorrect
   - Add `AnimatedVisibility` for celebration after completing a quote

3. **Input capture**:
   - Use an invisible `TextField` to capture keyboard input
   - Forward each character to the game logic for comparison
   - The visible quote display updates character colors based on correctness

#### iOS

4. **Add hint text** to `ContentView.swift`:
   ```swift
   Text("Type 'start' to begin the typing game 🎮")
       .font(.caption)
       .foregroundColor(.gray)
   ```
   - Monitor text field binding. When value is "start", set a `@State var showGame = true`
   - Use `NavigationLink` or `.fullScreenCover` to present `TypingGameView`

5. **Create `TypingGameView.swift`**:
   - Mirror the Android implementation in SwiftUI
   - Use `@State` variables for game state
   - `RichText`-style display: loop through characters, color each based on typing correctness
   - WPM + accuracy calculations same as Android

### Acceptance Criteria

- [ ] Hint text "Type 'start' to begin the typing game 🎮" visible below test box on home screen
- [ ] Typing "start" navigates to the game screen (case-insensitive)
- [ ] Game displays a quote for the user to type
- [ ] Characters turn green as they are correctly typed
- [ ] Characters turn red when incorrectly typed
- [ ] Current character position clearly indicated (underline or highlight)
- [ ] **WPM counter** displayed and updates in real-time
- [ ] **Accuracy percentage** displayed and updates in real-time
- [ ] **Streak counter** tracks consecutively completed quotes without errors
- [ ] Quote auto-advances after successful completion with brief celebration
- [ ] At least 50 quotes included (motivational, relaxing, humorous, positive)
- [ ] Back/quit button returns to home screen
- [ ] Calming background that follows light/dark mode
- [ ] Implemented on both Android and iOS
- [ ] No crashes during gameplay
- [ ] Game works correctly with both Logical and Efficiency layouts (any keyboard layout)

---

## ERICK-99 — GitHub Pages Website — Features, Accessibility & Privacy Policy

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 5 |
| **Assignee** | Developer 2 |
| **Sprint** | Sprint 6 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | website, documentation, play-store |
| **Dependencies** | None |

### Description

Create a public-facing project website hosted on GitHub Pages within the same repository. The website serves three purposes:

1. **Feature showcase** — Explain ERICK's features, how it works, and who it's for
2. **Accessibility** — Highlight how the app caters to different users (colorblind, left-handed, dyslexia-friendly, motor accessibility)
3. **Privacy Policy** — Required public page for Google Play Store listing

The website should be designed with future expandability in mind since new features will be added over time.

### Website Structure

```
website/
├── index.html          # Landing page
├── privacy-policy.html # Privacy policy (required for Play Store)
├── accessibility.html  # Accessibility features page
├── css/
│   └── style.css       # Stylesheet
├── js/
│   └── main.js         # Minimal interactivity
└── images/
    ├── erick-logo.png
    ├── screenshot-*.png (from play store assets)
    └── feature-*.png
```

### Page Content

#### Landing Page (index.html)
- **Hero Section**: ERICK logo + tagline "A radial chorded keyboard for everyone" + download buttons (Play Store badge, App Store badge — placeholder links for now)
- **What is ERICK?**: Brief explanation of chorded keyboard concept with an animated GIF or diagram showing two-joystick input
- **Key Features**:
  - 🎯 **Dual-Joystick Input** — Two radial dials combine to form character chords
  - 🎨 **Multiple Layouts** — Logical (A-Z), Efficiency (frequency-optimized), or create your own
  - 🌙 **Light & Dark Mode** — Follow system or set your preference
  - 🎮 **Controller Support** — Use physical gaming controllers for text input
  - ♿ **Accessibility First** — Colorblind palettes, left-handed mode, dyslexia-friendly fonts
  - 🔒 **Privacy Focused** — No data collection, no internet, fully open-source
  - 🎯 **Typing Practice** — Built-in relaxing typing game with quotes
  - ✏️ **Custom Layouts** — Design your own keyboard layout
- **How It Works**: Step-by-step illustration:
  1. Left dial selects a character group
  2. Right dial selects the specific character
  3. Both combined produce the output
- **Open Source**: Link to GitHub repository
- **Footer**: Copyright, links to Privacy Policy & Accessibility page

#### Privacy Policy (privacy-policy.html)
- **Title**: "ERICK Keyboard — Privacy Policy"
- **Effective Date**: March 2026
- **Content** (Play Store compliant):
  - **Data Collection**: "ERICK does not collect, store, or transmit any personal data, typed text, passwords, or usage statistics."
  - **Keyboard Input**: "All text processing occurs entirely on your device. Keystrokes are processed locally by the keyboard's chord engine and immediately passed to the active application. No text is logged, stored, or sent externally."
  - **Settings Storage**: "Your preferences (layout choice, theme, font, accessibility options) are stored locally on your device using platform-standard storage (Android DataStore / iOS UserDefaults). These settings never leave your device."
  - **Network Permissions**: "ERICK requests no internet or network permissions. The app operates fully offline."
  - **Third-Party Services**: "ERICK uses no third-party analytics, advertising, crashing reporting, or data processing services."
  - **Children's Privacy**: "ERICK does not knowingly collect any information from children under 13. No information is collected from any user."
  - **Open Source**: "ERICK is 100% open-source. You can inspect, audit, and verify every line of code at [GitHub link]."
  - **Contact**: Project maintainer email or GitHub issues link
  - **Changes**: "Any changes to this privacy policy will be posted on this page with an updated effective date."

#### Accessibility Page (accessibility.html)
- **Title**: "ERICK — Designed for Everyone"
- **Sections**:
  - **Colorblind Support**: 5 color palette options with visual examples
  - **Left-Handed Mode**: Full dial swap for left-hand dominant users
  - **Dyslexia-Friendly Font**: OpenDyslexic font option with examples
  - **Motor Accessibility**: Large joystick targets, adjustable dead zones, controller support
  - **Customizable Layouts**: Create layouts that work best for your needs
  - **Chorded Efficiency**: Fewer movements needed compared to traditional keyboards
- **Testimonials / Use Cases** (hypothetical): Brief personas explaining how different users benefit

### How to Get Started

1. **Create the `website/` directory** at the repository root (same level as `android/`, `ios/`, `documentation/`)

2. **Set up GitHub Pages**:
   - Go to the repository Settings → Pages
   - Source: Deploy from branch → `main` → `/website` folder (or `/docs` if you prefer)
   - Or use `gh-pages` branch
   - **Recommend**: Use `/docs` folder on `main` branch for simplicity:
     ```
     docs/
     ├── index.html
     ├── privacy-policy.html
     ├── accessibility.html
     ├── css/style.css
     ├── js/main.js
     └── images/
     ```

3. **Design approach**:
   - Use clean, minimal HTML/CSS (no framework needed)
   - Mobile-responsive (flexbox/grid layout)
   - ERICK brand colors — purple primary (#6650a4), dark purple (#381E72)
   - System font stack: `-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif`
   - Smooth scrolling: `html { scroll-behavior: smooth; }`

4. **Screenshots**: When available (from ERICK-101), add actual app screenshots to the website

5. **SEO basics**:
   - `<title>ERICK — Ergonomic Radial Inclusive Chorded Keyboard</title>`
   - `<meta name="description" content="A cross-platform chorded keyboard...">`
   - Open Graph tags for social sharing

### Acceptance Criteria

- [ ] Website accessible at `https://[username].github.io/Ergonomic-Radial-Inclusive-Chorded-Keyboard/`
- [ ] Landing page explains ERICK features clearly with visuals
- [ ] Privacy Policy page contains all required content for Play Store
- [ ] Accessibility page highlights all inclusivity features
- [ ] Website is mobile-responsive (works on phone, tablet, desktop)
- [ ] Consistent ERICK branding (logo, colors, typography)
- [ ] All links work (GitHub repo, inter-page navigation)
- [ ] Website loads quickly (no heavy frameworks)
- [ ] Privacy policy URL can be submitted to Play Store
- [ ] Placeholder download buttons for Play Store and App Store

---

## ERICK-100 — Play Store Listing Assets — Screenshots, Graphics & Marketing

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 3 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 6 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, play-store, marketing |
| **Dependencies** | ERICK-92 (Light/Dark Mode), ERICK-87 (Preview Bar) |

### Description

Prepare all **required and recommended assets** for the Google Play Store listing. Google Play Console requires specific assets before an app can be published.

### Required Assets

1. **App Screenshots** (minimum 2, maximum 8, recommended 4-8):
   - **Screenshot 1**: Keyboard in action — typing text with both joysticks active, preview bar showing
   - **Screenshot 2**: Settings screen — showing layout options, theme, accessibility
   - **Screenshot 3**: Onboarding screen — the setup flow
   - **Screenshot 4**: Left-handed mode — showing swapped dials
   - **Screenshot 5**: Dark mode keyboard — full dark theme
   - **Screenshot 6**: Colorblind mode — showing an alternative palette
   - **Screenshot 7**: Custom layout editor (if implemented)
   - **Screenshot 8**: Typing game screen (if implemented)
   - **Specs**: Phone: 16:9 aspect ratio, minimum 320px–3840px per side; Best: 1080×1920 pixels
   - **Format**: JPEG or 24-bit PNG, no alpha

2. **Feature Graphic** (required, 1 image):
   - Dimensions: 1024×500 pixels
   - Content: ERICK logo centered, app name, tagline "A radial chorded keyboard for everyone"
   - Background: ERICK brand gradient (purple to indigo)
   - No device frames — just the graphic
   - **Format**: JPEG or 24-bit PNG

3. **App Icon** (512×512 pixels):
   - High-resolution version of the ERICK app icon
   - Must match the launcher icon
   - **Format**: 32-bit PNG (with alpha)

4. **Short Description** (max 80 characters):
   ```
   A two-joystick chorded keyboard — accessible, customizable, and private.
   ```

5. **Full Description** (max 4000 characters):
   ```
   ERICK (Ergonomic Radial Inclusive Chorded Keyboard) reimagines mobile text input 
   with an innovative dual-joystick chord system. Instead of tapping individual keys, 
   combine two directional inputs to produce characters — reducing finger travel and 
   enabling faster, more ergonomic typing.

   🎯 HOW IT WORKS
   • Left joystick selects a character group (A-E, F-J, K-O, etc.)
   • Right joystick selects the specific character within the group
   • Both combined = one character output
   • Single right swipe for utilities: Space, Enter, Backspace, and more

   📐 MULTIPLE LAYOUTS
   • Logical (A–Z): Alphabetical order, easy to learn
   • Efficiency: Optimized by letter frequency for faster typing
   • Custom: Create your own layouts from scratch

   ♿ ACCESSIBILITY FIRST
   • Colorblind mode with 4 specialized color palettes (Okabe-Ito, Deuteranopia, Protanopia, Tritanopia)
   • Left-handed mode — swap the dials
   • Dyslexia-friendly OpenDyslexic font option
   • Large touch targets with configurable dead zones
   • Physical gaming controller support (Xbox, PlayStation)

   🔒 PRIVACY FOCUSED
   • No data collection — ever
   • No internet permissions
   • All processing is local
   • 100% open-source on GitHub

   🎮 TYPING PRACTICE
   • Built-in relaxing typing game
   • Type motivational quotes at your own pace
   • Track WPM, accuracy, and streak
   • Calm, stress-free environment

   🎨 CUSTOMIZABLE
   • Light and dark themes
   • Multiple font options
   • Create custom keyboard layouts
   • Adjustable color palettes

   ERICK is free, open-source, and built with one goal: making text input 
   accessible and enjoyable for everyone.
   ```

6. **Screen Recordings** (optional but recommended, 30s–2min):
   - Record a short demo of typing with ERICK
   - Show the joystick interaction flow
   - Show switching between layouts

### How to Get Started

1. **Screenshots**: Build the app in release mode, use Android emulator (Pixel 7 or Pixel 8) at 1080×1920 resolution:
   ```bash
   adb shell screencap -p /sdcard/screenshot.png
   adb pull /sdcard/screenshot.png
   ```
   - Or use Android Studio's emulator screenshot button
   - Take screenshots with meaningful text in the text field (e.g., "Hello, World!")
   - Capture both light and dark mode variants

2. **Feature Graphic**: Create using Figma, Canva, or any design tool:
   - ERICK logo centered on a gradient background
   - Text: "ERICKeyboard" + tagline
   - Export as 1024×500 PNG

3. **App Icon**: Scale the existing `ic_launcher.webp` or the source logo to 512×512 PNG

4. **Screen Recording**: Use Android emulator's built-in screen recorder or `adb shell screenrecord`

5. **Store the assets**: Create `documentation/play_store/` folder with all files

### Acceptance Criteria

- [ ] Minimum 4 app screenshots at 1080×1920 pixels
- [ ] Feature graphic at 1024×500 pixels
- [ ] App icon at 512×512 pixels PNG
- [ ] Short description (≤80 chars) drafted
- [ ] Full description (≤4000 chars) drafted
- [ ] All screenshots show real app functionality (not mockups)
- [ ] At least one screenshot shows dark mode
- [ ] At least one screenshot shows an accessibility feature
- [ ] Screen recording captured (30-60 seconds demo)
- [ ] All assets stored in `documentation/play_store/` folder
- [ ] Assets meet Google Play Console format and size requirements

---

## ERICK-101 — Translate All Code Comments to English

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Low |
| **Story Points** | 3 |
| **Assignee** | Developer 2 |
| **Sprint** | Sprint 7 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | documentation, cleanup |
| **Dependencies** | None (do near end of project) |

### Description

Audit the entire codebase and translate all code comments to English. This includes:
- Inline comments (`//` and `/* */`)
- KDoc / Javadoc comments (`/** */`)
- Swift doc comments (`///`)
- TODO comments
- File headers

Some comments in the codebase may be in other languages or use abbreviations that aren't clear. All should be translated to clear, professional English.

### How to Get Started

1. **Search every source file** in these directories:
   - `android/shared/src/` (all Kotlin files)
   - `android/app/src/main/java/` (all Kotlin files)
   - `android/app/src/main/res/` (XML files — check for comments)
   - `ios/ERICK/ERICK/` (all Swift files)
   - `ios/ERICK/ErickKeyBoard/` (all Swift files)

2. **For each file**:
   - Read through all comments
   - If any comment is not in English, translate it to clear English
   - If a comment is ambiguous or unclear, rewrite it to be more descriptive
   - Ensure KDoc/Javadoc follows standard format:
     ```kotlin
     /**
      * Resolves a chord from left and right directions into a character.
      *
      * @param leftDir The direction of the left joystick.
      * @param rightDir The direction of the right joystick.
      * @param mode The current keyboard mode (NORMAL, SHIFTED, CAPS_LOCKED).
      * @param layout The current layout type.
      * @return The resolved character string, or empty string if no mapping exists.
      */
     ```

3. **Do NOT change any code logic** — only modify comments and documentation strings.

4. **Add missing file-level comments** where absent:
   ```kotlin
   /**
    * KeyboardStateMachine.kt
    * 
    * Manages the state transitions of dual joystick inputs to form complete chords.
    * Tracks which joystick(s) are active and their directions, then resolves
    * the final character output via KeyboardLogic.
    */
   ```

### Acceptance Criteria

- [ ] All comments in Kotlin files are in English
- [ ] All comments in Swift files are in English
- [ ] All comments in XML resource files are in English
- [ ] KDoc comments follow standard format with `@param`, `@return` where applicable
- [ ] No code logic changed — comment-only modifications
- [ ] Every source file has a file-level description comment
- [ ] No orphaned TODO comments without context
- [ ] All changes reviewed for accuracy (translations are correct)

---

## ERICK-102 — Final Documentation Update — Architecture Diagrams, Sprint Docs & User Guides

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 5 |
| **Assignee** | Developer 2 |
| **Sprint** | Sprint 7 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | documentation |
| **Dependencies** | All other tickets (do near end of project) |

### Description

Comprehensive documentation update at the end of the project. This is a catch-all ticket covering all documentation deliverables.

### Deliverables

#### 1. Architecture Diagrams (update `documentation/`)

- **System Architecture Diagram**: Update the diagram in `APP_CONTEXT.md` to reflect the final state of the codebase:
  - All shared module classes and their relationships
  - Platform layer components (Android activities/services, iOS ViewControllers)
  - Data flow diagrams (touch → chord → character)
  - Settings flow (preferences → UI → keyboard behavior)
  - New components: custom layouts, themes, emoji panel, typing game
- **Class Diagrams**: Create UML-style class diagrams for:
  - Shared module: `KeyboardStateMachine`, `KeyboardLogic`, `CustomLayout`, `ThemeColors`, etc.
  - Android platform layer: `MyInputMethodService`, `JoystickView`, `SettingsScreen`, etc.
  - iOS platform layer: `KeyboardViewController`, `JoystickView`, `ContentView`, etc.
- **Sequence Diagrams**: User interaction flows:
  - Chord input sequence (touch → state machine → character output)
  - Settings change sequence (UI → persistence → keyboard reload)
  - Typing game sequence (input capture → comparison → scoring)
- **Format**: Use Mermaid diagrams in Markdown (renders on GitHub) or draw.io (already used — `joystick_wireframe.drawio`)

#### 2. Sprint Retrospective Documents

- Create `documentation/Jira/Sprint_4_Retrospective.md` (and for any subsequent sprints):
  - **What went well**: Positive outcomes
  - **What didn't go well**: Challenges and obstacles
  - **Action items**: Improvements for future sprints
  - **Velocity**: Story points planned vs. completed
  - **Carry-over**: Tickets not completed and reasons

#### 3. Burndown / Burnup Charts

- Create charts for each sprint (4 through final):
  - **Burndown Chart**: Ideal vs. actual remaining work over sprint days
  - **Burnup Chart**: Total work completed vs. total scope over time
  - **Format**: Can be simple Markdown tables, ASCII charts, or images generated from data

#### 4. Jira Ticket Archive

- Ensure all sprint tickets are documented in `documentation/Jira/`:
  - `Sprint_1_Tickets.md` (or CSV — already exists as `Sprint 1 Retrospective.csv`)
  - `Sprint_2_Tickets.csv` (already exists)
  - `Sprint_3_Tickets.md` (already exists)
  - `Sprint_4_Tickets.md` (this document)
  - Subsequent sprint ticket documents

#### 5. User Guide

- Create `documentation/User_Guide.md`:
  - **Getting Started**: How to install and enable ERICK
  - **Basic Typing**: How chords work (left + right = character)
  - **Layout Options**: Logical vs. Efficiency vs. Custom
  - **Utility Functions**: Space, Enter, Backspace, Shift, Caps Lock
  - **Settings**: How to change theme, font, layout, accessibility options
  - **Typing Game**: How to access and use the typing practice game
  - **Custom Layouts**: How to create and manage custom layouts
  - **Controller Support**: How to connect and use a gaming controller
  - **Troubleshooting**: Common issues and solutions
  - Include annotated screenshots where helpful

#### 6. Research Documentation Updates

- Update `documentation/Research/` with:
  - Final efficiency layout research results
  - Any user testing findings
  - References to research papers used
  - Update scripts if methodologies changed

#### 7. README Updates

- Update root `README.md` with:
  - Current feature list
  - Build instructions for both platforms
  - Link to website and privacy policy
  - Link to user guide
  - Contribution guidelines
- Update `android/README.md` and `ios/README.md` with platform-specific build instructions

### How to Get Started

1. Start with architecture diagrams — read all source files to understand the final codebase state
2. Use Mermaid syntax for diagrams (renders natively on GitHub):
   ```markdown
   ```mermaid
   graph TD
       A[User Touch] --> B[JoystickView]
       B --> C[MyInputMethodService]
       C --> D[KeyboardStateMachine]
       D --> E[KeyboardLogic]
       E --> F[Character Output]
   ```
   ```
3. Write retrospective documents based on actual sprint outcomes
4. Create burndown data from sprint velocities
5. Write user guide step by step with flow matching the app

### Acceptance Criteria

- [ ] Updated system architecture diagram reflecting final codebase
- [ ] Class diagrams for shared module and both platform layers
- [ ] Sequence diagrams for at least 3 key user flows
- [ ] Sprint retrospective documents for sprints 4+
- [ ] Burndown/burnup chart data for each sprint
- [ ] All sprint ticket documents archived in `documentation/Jira/`
- [ ] User guide covering all features with clear instructions
- [ ] Research documentation updated
- [ ] README files updated with current information
- [ ] All diagrams render correctly on GitHub

---

## ERICK-103 — Onboarding Tutorial & Accessibility Improvements

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Medium |
| **Story Points** | 3 |
| **Assignee** | Developer 1 |
| **Sprint** | Sprint 6 |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | android, iOS, accessibility, UX |
| **Dependencies** | None |

### Description

This ticket covers additional improvements identified during sprint planning that enhance the user experience and accessibility:

1. **Interactive Tutorial Overlay**: Add a first-launch interactive tutorial that overlays on the keyboard and guides the user through their first chord. Shows arrows and labels like "Swipe here first" → "Now swipe here" → "You typed 'a'!"

2. **Haptic Feedback**: Add subtle vibration feedback when:
   - A chord is successfully completed (character typed)
   - Shift or Caps Lock is toggled
   - A single-swipe action is performed
   - Controllable via settings (on/off toggle)

3. **Sound Effects** (optional, off by default):
   - Soft click sound on character commit
   - Distinct sound for Shift toggle, Caps Lock toggle
   - Controllable via settings (on/off toggle)

4. **Accessibility Labels**: Ensure all interactive elements have proper content descriptions:
   - Android: `contentDescription` on all views
   - iOS: `accessibilityLabel` on all views
   - Screen readers (TalkBack / VoiceOver) should be able to describe the keyboard

### How to Get Started

#### Interactive Tutorial

1. **Create a tutorial controller** that tracks whether first-launch tutorial has been shown:
   - Store `tutorial_completed` in DataStore / UserDefaults
   - On first keyboard display, if not completed, show the overlay

2. **Tutorial flow** (3 steps):
   - Step 1: Arrow pointing to left dial → "Swipe in any direction to select a letter group"
   - Step 2: Arrow pointing to right dial → "Now swipe here to pick the specific character"
   - Step 3: "🎉 You typed your first character! Keep practicing!"
   - "Skip Tutorial" button always visible

3. **Overlay implementation**:
   - Android: Semi-transparent overlay view on top of the keyboard
   - iOS: SwiftUI overlay with `.opacity()` and arrow shapes

#### Haptic Feedback

4. **Android** (`MyInputMethodService.kt`):
   ```kotlin
   private fun performHapticFeedback() {
       val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
       }
   }
   ```

5. **iOS** (`KeyboardViewController.swift`):
   ```swift
   let feedback = UIImpactFeedbackGenerator(style: .light)
   feedback.impactOccurred()
   ```

6. **Settings**: Add "Haptic Feedback" and "Sound Effects" toggles in Settings screens (both platforms)

### Acceptance Criteria

- [ ] First-launch interactive tutorial guides user through first chord
- [ ] Tutorial can be skipped
- [ ] Tutorial only shows once (persisted)
- [ ] Haptic feedback on character commit (when enabled in settings)
- [ ] Haptic feedback on Shift/Caps Lock toggle (when enabled in settings)
- [ ] Haptic and sound settings toggles in Settings screen (both platforms)
- [ ] All interactive keyboard elements have accessibility labels
- [ ] Tested with TalkBack (Android) and VoiceOver (iOS)
- [ ] Implemented on both Android and iOS

---

## ERICK-104 — Word Prediction & Autocorrect Foundation

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | Low |
| **Story Points** | 5 |
| **Assignee** | Developer 2 |
| **Sprint** | Sprint 7 (Stretch) |
| **Parent Epic** | ERICK-41 Android IME Core |
| **Labels** | shared-module, AI, keyboard |
| **Dependencies** | None |

### Description

**Note**: This is a stretch goal for a future sprint if time permits.

Implement a basic word prediction and autocorrect system in the shared module. As the user types, the keyboard should:
1. Suggest up to 3 word completions based on the characters typed so far
2. Offer spelling corrections for common typos
3. Display suggestions in a suggestion bar above the keyboard (above the preview bar)

### How to Get Started

1. **Create a dictionary-based predictor** in the shared module:
   - Bundle a list of the **5,000 most common English words** as a resource
   - Use a **Trie data structure** for fast prefix matching
   - On each character typed, query the trie for words starting with the current prefix
   - Return top 3 matches sorted by frequency

2. **Create `android/shared/src/commonMain/kotlin/WordPredictor.kt`**:
   ```kotlin
   class WordPredictor {
       private val trie = Trie()
       
       fun initialize(wordList: List<Pair<String, Int>>) // word, frequency
       fun predict(prefix: String, limit: Int = 3): List<String>
       fun addUserWord(word: String)  // Learn from user input
       fun getSuggestions(currentWord: String): List<String>
   }
   ```

3. **Suggestion bar**: Add a horizontal bar above the preview bar showing 3 tappable word suggestions

4. **Autocorrect**: When the user types Space after a misspelled word, check if common corrections exist (Levenshtein distance ≤ 2) and auto-replace

### Acceptance Criteria

- [ ] Word prediction suggests up to 3 completions based on typed prefix
- [ ] Suggestions update in real-time as user types
- [ ] Tapping a suggestion inserts the full word
- [ ] Basic autocorrect for common misspellings
- [ ] Dictionary of 5,000+ common English words
- [ ] Suggestion bar visible above keyboard
- [ ] No noticeable lag from prediction computation
- [ ] Shared module implementation works on both platforms

---

## Sprint Planning Summary

### All Tickets Overview

| Ticket | Title | Assignee | Priority | SP | Target Sprint | Dependencies |
|---|---|---|---|---|---|---|
| ERICK-87 | Android Preview Bar Redesign + Right-Dial Preview | Dev 1 | High | 5 | Sprint 4 | — |
| ERICK-88 | iOS Right-Dial Preview + Parity Check | Dev 1 | High | 3 | Sprint 4 | — |
| ERICK-89 | Custom Layout Data Model — Shared Module | Dev 2 | Medium | 5 | Sprint 4 | ERICK-73 |
| ERICK-90 | Custom Layout Creator UI — Android & iOS | Dev 1 | Medium | 5 | Sprint 5 | ERICK-89 |
| ERICK-91 | Left-Handed Mode UI Swap — Android & iOS | Dev 1 | High | 3 | Sprint 4 | ERICK-74 |
| ERICK-92 | Light/Dark Mode Theme Support | Dev 1 | High | 5 | Sprint 4 | — |
| ERICK-93 | Accelerating Backspace — Hold to Delete Words | Dev 2 | High | 3 | Sprint 4 | — |
| ERICK-94 | Font Selection — Settings & Keyboard UI | Dev 1 | Medium | 5 | Sprint 5 | — |
| ERICK-95 | Shift & Caps Lock Visual Indicators | Dev 1 | High | 3 | Sprint 5 | — |
| ERICK-96 | Symbols Keyboard Shortcut Button | Dev 2 | Medium | 3 | Sprint 5 | — |
| ERICK-97 | Emoji Keyboard Integration | Dev 1 | Medium | 5 | Sprint 5 | — |
| ERICK-98 | CozyTyper-Style Typing Game | Dev 1 | Medium | 5 | Sprint 5 | — |
| ERICK-99 | GitHub Pages Website | Dev 2 | High | 5 | Sprint 6 | — |
| ERICK-100 | Play Store Listing Assets | Dev 1 | High | 3 | Sprint 6 | ERICK-92, ERICK-87 |
| ERICK-101 | Translate Code Comments to English | Dev 2 | Low | 3 | Sprint 7 | — |
| ERICK-102 | Final Documentation Update | Dev 2 | Medium | 5 | Sprint 7 | All |
| ERICK-103 | Onboarding Tutorial & Accessibility | Dev 1 | Medium | 3 | Sprint 6 | — |
| ERICK-104 | Word Prediction & Autocorrect (Stretch) | Dev 2 | Low | 5 | Sprint 7 | — |

### Story Point Totals by Developer

| Developer | Total SP | Sprint 4 | Sprint 5 | Sprint 6 | Sprint 7 |
|---|---|---|---|---|---|
| **Dev 1** | 45 | 16 | 18 | 6 | 0 |
| **Dev 2** | 34 | 8 | 3 | 5 | 13 |
| **Combined** | **79** | **24** | **21** | **11** | **13** |

### Sprint 4 Plan (March 16–20, 2026)

**Developer 1 (Platform Layer) — 16 SP**:

| Day | Ticket | SP |
|---|---|---|
| Mon–Tue | ERICK-87 — Android Preview Bar Redesign + Right-Dial Preview | 5 |
| Wed | ERICK-88 — iOS Right-Dial Preview + Parity Check | 3 |
| Thu | ERICK-91 — Left-Handed Mode UI Swap | 3 |
| Fri | ERICK-92 — Light/Dark Mode Theme (start) | 5 (partial) |

**Developer 2 (Shared Module) — 8 SP**:

| Day | Ticket | SP |
|---|---|---|
| Mon–Tue | ERICK-93 — Accelerating Backspace | 3 |
| Wed–Fri | ERICK-89 — Custom Layout Data Model | 5 |

**Note**: ERICK-92 (5 SP) will likely carry into Sprint 5 for Dev 1.

### Sprint 5 Plan (March 23–27, 2026)

**Developer 1 — ~18 SP**:

| Ticket | SP |
|---|---|
| ERICK-92 (carry-over if needed) | 2-3 |
| ERICK-95 — Shift/Caps Lock Indicators | 3 |
| ERICK-94 — Font Selection | 5 |
| ERICK-90 — Custom Layout Creator UI | 5 |
| ERICK-97 — Emoji Keyboard (stretch) | 5 |

**Developer 2 — ~3 SP + carry-over**:

| Ticket | SP |
|---|---|
| ERICK-96 — Symbols Keyboard Shortcut | 3 |
| Sprint 3 carry-overs (ERICK-80–86) | varies |

### Sprint 6 Plan (March 30 – April 3, 2026)

**Developer 1 — ~8 SP**:

| Ticket | SP |
|---|---|
| ERICK-98 — Typing Game | 5 |
| ERICK-100 — Play Store Assets | 3 |
| ERICK-103 — Tutorial & Accessibility | 3 |

**Developer 2 — ~5 SP**:

| Ticket | SP |
|---|---|
| ERICK-99 — GitHub Pages Website | 5 |

### Sprint 7 Plan (April 6–10, 2026)

**Developer 1** — Carry-overs, bug fixes, polish

**Developer 2 — ~13 SP**:

| Ticket | SP |
|---|---|
| ERICK-101 — Translate Comments to English | 3 |
| ERICK-102 — Final Documentation Update | 5 |
| ERICK-104 — Word Prediction (Stretch) | 5 |

---

### Notes & Recommendations

1. **Sprint 3 carry-overs**: Tickets ERICK-80 through ERICK-86 (Custom Keybinds Data Model, Custom Keybind Editor UIs, Controller Support) from Sprint 3 that weren't completed should be prioritized alongside these new tickets. ERICK-89 and ERICK-90 supersede ERICK-80/81/82 with expanded scope. ERICK-83/84/85/86 (Controller Support) should be scheduled into Sprint 5 or 6 based on remaining capacity.

2. **Risk: Dev 1 workload** — Developer 1 has significantly more work (45 SP) than Developer 2 (34 SP). Consider:
   - Dev 2 can help with some platform UI work if they finish shared module tasks early
   - Some UI tickets (ERICK-94 Font Selection, ERICK-97 Emoji) could be split across developers

3. **Testing time**: Reserve at least half a day per sprint for integration testing — both platforms, all layouts, all modes.

4. **Play Store timeline**: If targeting a Play Store release, ERICK-99 (website with privacy policy) and ERICK-100 (listing assets) are critical-path items that should not slip.

5. **Feature freeze suggestion**: Consider Sprint 6 as the feature freeze point, with Sprint 7 dedicated to bug fixes, documentation, and polish.
