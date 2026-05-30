# ERICK-170 - Custom Visual Themes with Background Images

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Low |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, theme, ui, android, ios, settings, personalization |
| **Dependencies** | None. Extends the existing theme and custom palette systems |

---

## Objective

Allow users to set custom background images for the keyboard, creating a personalized visual theme. Users can pick images from their device gallery that display behind the dial and top bar areas, with appropriate opacity and overlay controls to maintain readability.

---

## Why This Matters

- Keyboard personalization is one of the highest-engagement features in consumer keyboards. Users spend significant time with their keyboard visible and want it to feel personal.
- Background images are a visual reward that encourages adoption and daily use — important for a new keyboard that needs to overcome the switching cost.
- This goes beyond the existing palette system (which only controls direction colors) to make the entire keyboard surface customizable.
- Personalization builds emotional attachment to the product, which helps with long-term retention — especially important for a novel input method that requires learning investment.

---

## Current Surfaces To Build On

### Android
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` — keyboard background rendering
- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` — dial area background
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt` — theme/appearance section
- `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt` — preference persistence
- `android/app/src/main/java/com/vatoo/erick/CustomPaletteEditorScreen.kt` — existing custom color editing patterns

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardContainerView.swift` — keyboard container background
- `ios/ERICK/ErickKeyBoard/JoystickView.swift` — dial rendering layers
- `ios/ERICK/ErickKeyBoard/SettingsView.swift` — settings UI
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift`

---

## Proposed Scope

### 1. Add Background Image Picker

In the Appearance section of settings, add a **"Background Image"** option:

- **None** (default) — use the current solid theme color
- **Choose from Gallery** — opens the system photo picker
- Selected image is cropped/scaled to fit the keyboard dimensions
- Image is stored locally in app-private storage (not shared externally)

### 2. Add Overlay and Opacity Controls

To maintain text readability over arbitrary images:

- **Overlay opacity slider** (0% to 80%): adds a semi-transparent dark or light overlay on top of the image
- **Overlay color**: Auto-selects based on current theme (dark overlay for light themes, light overlay for dark themes), or manual black/white toggle
- **Blur intensity** (optional, 0 to 20px): applies a background blur to reduce visual noise from detailed images

### 3. Image Rendering Behavior

- Image fills the keyboard background area (behind both dials and the top bar)
- Image is scaled using center-crop (fills the space, crops overflow)
- Image does NOT extend behind the system navigation bar or status bar
- In emoji mode, the same background shows behind the emoji panel
- In numpad mode (if ERICK-168 ships), background shows behind the numpad

### 4. Readability Safeguards

- Direction colors, text labels, preview capsule, and suggestion bar must remain readable over any background
- Add a subtle shadow/outline behind white text elements when a background image is active
- If overlay opacity is below 20%, show a warning that readability may be affected
- The floating mode badge should have a semi-opaque pill background to ensure visibility

### 5. Storage and Performance

- Store the processed/cropped image in app-internal storage (not raw full-resolution photo)
- Resize to a reasonable maximum (e.g., 1080px wide) to prevent memory issues
- Cache the rendered bitmap to avoid re-processing on every keyboard open
- Android: Store in internal files dir accessible to IME service
- iOS: Store in App Group container accessible to keyboard extension

### 6. Settings UI

```
Appearance
├── Theme: [System] [Light] [Dark] [AMOLED]
├── Background Image
│   ├── [None] [Choose Image...]
│   ├── Preview thumbnail of selected image
│   ├── Overlay Opacity: [────●──────] 40%
│   ├── Overlay Style: [Auto] [Dark] [Light]
│   ├── Blur: [────●──────] 5px
│   └── [Remove Image]
├── Colorblind Mode...
├── Font...
```

### 7. Platform Parity

- Android: Use `ActivityResultContracts.PickVisualMedia()` for image selection; render via `BitmapDrawable` or Canvas behind the keyboard layout
- iOS: Use `PhotosUI.PHPickerViewController` for image selection; render via SwiftUI `Image` with `.resizable().scaledToFill()` behind the container

---

## Acceptance Criteria

- [ ] Users can select an image from their device gallery as keyboard background
- [ ] Image displays correctly behind the dial area and top bar
- [ ] Overlay opacity slider (0-80%) ensures text remains readable
- [ ] Dark/light overlay auto-adapts to current theme
- [ ] Optional blur control reduces visual noise from detailed images
- [ ] Direction colors and text labels remain visible with any background
- [ ] Background image persists across keyboard restarts
- [ ] "Remove Image" returns to the default solid theme background
- [ ] Image is stored locally and never transmitted over the network
- [ ] Performance: keyboard open/close time is not noticeably affected
- [ ] Works on both Android and iOS
- [ ] Emoji panel and other overlays (numpad if present) also show the background

---

## Out of Scope

- Animated backgrounds or live wallpapers
- Theme marketplace or sharing themes between users
- Pre-bundled background images (users bring their own)
- Background images for the host app (keyboard extension only)
- Video backgrounds
