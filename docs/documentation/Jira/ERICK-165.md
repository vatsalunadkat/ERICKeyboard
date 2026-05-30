# ERICK-165 - AMOLED True Black Theme

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Low |
| **Story Points** | 3 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, theme, android, ios, accessibility, ui |
| **Dependencies** | None. Extends the existing theme system (System/Light/Dark) |

---

## Objective

Add a dedicated AMOLED True Black theme option that uses pure black (#000000) backgrounds throughout the keyboard UI, optimized for OLED/AMOLED screens to save battery and reduce visual strain in dark environments.

---

## Why This Matters

- OLED and AMOLED displays turn off individual pixels for pure black, saving measurable battery life compared to dark gray backgrounds.
- Many accessibility users prefer true black for reduced eye strain, especially at night or in low-light environments.
- The current Dark theme uses dark gray tones (typical Material Design dark surfaces) which still illuminate OLED pixels.
- True black is a frequently requested feature in keyboard apps and is a low-effort high-satisfaction addition.

---

## Current Surfaces To Build On

### Android
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` — theme application and background rendering
- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` — dial background colors
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt` — theme selection UI
- `android/app/src/main/res/layout/keyboard_simple.xml` — keyboard layout background

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardContainerView.swift` — keyboard background
- `ios/ERICK/ErickKeyBoard/JoystickView.swift` — dial rendering
- `ios/ERICK/ErickKeyBoard/SettingsView.swift` — theme selection

---

## Proposed Scope

### 1. Add AMOLED Theme Option

Extend the existing theme selector (System / Light / Dark) with a fourth option: **AMOLED Black**

- Theme selector becomes: System / Light / Dark / AMOLED Black
- Stored as a new theme value in preferences

### 2. Define AMOLED Color Scheme

| Surface | Color |
|---|---|
| Keyboard background | `#000000` (pure black) |
| Top bar background | `#000000` |
| Dial background (outer) | `#000000` |
| Dial background (inner/ring) | `#0A0A0A` (near-black for subtle contrast) |
| Text and icons | `#FFFFFF` or `#E0E0E0` |
| Suggestion bar text | `#FFFFFF` |
| Dividers/borders | `#1A1A1A` (very subtle) |
| Preview capsule background | `#1A1A1A` |
| Direction colors | Existing palette colors (unchanged) — colorblind palette still applies on top |

### 3. Ensure Color Palette Compatibility

- The direction/section colors from all 7 colorblind palettes must remain readable against the pure black background
- The custom palette editor should still work correctly with AMOLED Black selected
- Floating mode badge and emoji panel should also use the AMOLED scheme

### 4. Platform Parity

- Android: Apply via the existing theme observer in `MyInputMethodService`
- iOS: Apply via the existing theme preference in keyboard extension and host app

---

## Acceptance Criteria

- [ ] New "AMOLED Black" option appears in the theme selector on both platforms
- [ ] All keyboard surfaces use pure `#000000` black when AMOLED theme is active
- [ ] Direction colors and palette colors remain visible and accessible against the black background
- [ ] Emoji panel respects the AMOLED theme
- [ ] Top bar, suggestion strip, and floating badge respect the AMOLED theme
- [ ] Setting persists across keyboard restarts
- [ ] Works with all 7 colorblind palettes plus custom palettes
- [ ] No regressions to existing System/Light/Dark themes

---

## Out of Scope

- Automatic AMOLED detection based on device screen type
- Per-element dark/light mixing (the theme is uniformly true black)
- AMOLED variants of the host app screens (keyboard extension only in first pass; host app can follow up)
