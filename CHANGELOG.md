# Changelog

All notable changes to the ERICK (Ergonomic Radial Inclusive Controller Keyboard) project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v1.0] - April 13, 2026

## What's New

### Major Feature: 6-Section Dial Mode
- **Optional 6-Section Dial** - New dial geometry with 6 directions (N, NE, SE, S, SW, NW) at 60-degree intervals instead of 8 at 45 degrees. Larger segments improve targeting accuracy, especially for users with motor limitations. Enabled via a toggle in Settings (off by default).
- **36-Slot Chord Grid** - 6x6 chord layout maps 26 letters + 10 digits in normal mode. Shifted mode provides uppercase letters and common symbols.
- **Symbols Mode** - Dedicated symbols layer toggled by swiping NW on the right dial. Provides punctuation, brackets, math operators, and special characters across a 6x6 grid. Shift works within symbols mode for additional Unicode currency, math, and arrow characters.
- **6-Section Efficiency Layout** - Frequency-optimized chord layout re-generated for the 6x6 grid using the Parallel Tempering optimizer. Most common letters (e, t, a, o, i, n) placed on same-direction (diagonal) chords.
- **Remapped Utility Actions** - 6-section single-swipe actions: N = Shift, NE = Period, SE = Space, S = Enter, SW = Backspace, NW = Symbols toggle.
- **6-Color Palettes** - All 7 color palettes (Default, Okabe-Ito, Deuteranopia, Protanopia, Tritanopia, Pastel, Custom) updated with 6-color variants. Custom palette editor adapts to show 6 or 8 color slots.
- **Preview Bar Support** - Live preview bar fully functional in 6-section mode with correct direction-to-color mapping on both platforms.
- **Inner Ring Text Positioning** - Inner ring character labels shifted slightly outward (58% radial bias) for better readability within the circular geometry.
- **Ring Border** - Solid black ring border separating inner and outer character rings on both platforms.

### Improvements
- **Settings Dropdown Default** - Settings sections now start collapsed on both Android and iOS instead of opening with the Layout section expanded.
- **Fresh Install Sync** - Fixed 6-section dial mode not applying on Android when enabled at fresh install (before keyboard views were created).
- **Haptic & Sound Tuning** - Adjusted haptic feedback intensity and key click sound behavior.

### Platform Parity
All 6-section dial features implemented identically on Android and iOS:
- Android: Canvas-based 6-segment rendering in JoystickView, DataStore persistence, IME preview bar
- iOS: SwiftUI SectorSlice-based 6-segment rendering in JoystickView, App Group persistence, KeyboardViewController preview
- Shared KMP: KeyboardLogic 6-way direction detection, all chord maps, KeyboardStateMachine symbols mode, ColorPalettes 6-color variants, CustomLayout 6x6 support

### Breaking Changes
None - existing 8-section mode is unchanged and remains the default. User preferences are fully compatible.

---

## [v0.7.4-beta] - Mar 31, 2026

## What's New

### Major Features
- **Three Input Modes** - Choose how chords are triggered via a new Input Mode settings section:
  - **Quick Type** (default): Type at full speed - characters appear as soon as you release either dial.
  - **Steady Type**: Take your time - characters appear only after both dials return to center.
  - **One-Handed**: Type with one hand - lock a direction on the left dial, then swipe the right dial to type.
- **Custom Color Palettes** - Create your own 8-color palette with a full color editor. Android uses HSV sliders with hex/RGB input; iOS uses the native ColorPicker with hex/RGB fields. Accessible via a "Create Your Own" option in the Colorblind Mode section.
- **Haptic Feedback & Typing Sounds** - New Feedback settings section with toggleable haptic vibration (strong for utility keys, light for letters) and system typing sounds. Both default to off.

### Improvements
- **Pastel Palette Icon Fix** - Fixed utility icons on the right dial appearing invisible when using Pastel palette colors with low luminance (Lavender, Lilac, Slate). Icons now correctly use black instead of white on light-colored pastel backgrounds.
- **Preview Text Capsule Fix** - Fixed text being cut off at 1 and 3 characters in the preview capsule.
- **Shift/Caps Indicator Redesign** - Redesigned the shift indicator badge for better visibility.
- **How to Type Emoji Update** - Changed the indicator icon/emoji for the How to Type overlay.
- **Website Deployment Fix** - Updated deployment workflow for the documentation website.

### Breaking Changes
None - existing user preferences remain compatible. New preferences (`input_mode`, `custom_palette_colors`, `haptic_feedback`, `typing_sounds`) default to backward-compatible values.

---

## [v0.5.1-alpha] - Mar 28, 2026

## What's New

### Major Features
- **Typing Practice Mini-Game** - In-app typing game on both Android (Jetpack Compose) and iOS (SwiftUI). Presents curated quotes for practice with real-time WPM, accuracy, and streak tracking. Features per-character correctness highlighting, shake animation on errors, invisible input capture, and a live stats bar. Activated by typing "start" in the test field.
- **Website Redesign** - Complete rebuild of the documentation site using React 19, Vite 8, and Tailwind CSS v4. New SPA with Landing, Features, Evolution timeline, and Privacy pages. GitHub Pages deployment via dedicated workflow. Legacy v1 site preserved under `docs/v1/`.
- **Architecture Diagrams** - Added `ERICK_architecture.drawio` and exported PNG documenting the full KMP shared module, platform layers, controller/touch input flows, and build artifacts.

### Improvements
- **Preview Text Readability** - Added outlined/stroked preview text on both platforms for better contrast across light and dark themes. Android uses a custom `OutlinedTextView` with configurable stroke color/width; iOS uses stacked offset `Text` views with `strokeOffsets` helper.
- **Shift Indicator Relocation** - Moved the Android shift indicator into the preview row to prevent overlap with the capsule.
- **UI Contrast Fixes** - Forced preview card content color to black on Android Compose screens for better legibility; iOS cards forced to light color scheme where needed.
- **iOS Settings GitHub Link** - Added a GitHub repository link button in the iOS SettingsView.
- **AD_ID Opt-Out** - Added `com.google.android.gms.permission.AD_ID` with `tools:node="remove"` in the Android manifest to explicitly opt out of advertising ID collection.
- **Demo Media Refresh** - Replaced landscape GIFs with new portrait typing/controller demos, added v0.4.2 screenshots to the Evolution timeline, updated hero banner to `ERICK_feature_graphic_black.png`.
- **GIF Cropping Fix** - Removed forced `aspect-[9/16]` and `object-cover` constraints on website demo media.
- **Build Hygiene** - Added `build/` to root `.gitignore`, cleaned up stale build assets.

### Breaking Changes
None - existing user preferences remain compatible.

---

## Installation

### Android (Available Now)
Download the .apk file and install it.

**Requirements:**
- Android 7.0 (API 24) or higher
- ~20 MB storage space

### iOS
iOS keyboard with native SwiftUI interface and shared Kotlin logic. Build from source via the Xcode project.

---

## Demo
TODO

---
---

## [v0.4.2-alpha] - Mar 21, 2026

## What's New

### Major Features
- **Physical Gaming Controller Support** - Full DualShock 4, 8BitDo, and other Bluetooth game controller support on both Android and iOS. Left and right analog sticks map to the left and right joystick dials respectively. On iOS, a ControllerBridge in the host app shares controller state with the keyboard extension via App Group, since extensions run in a separate process.
- **Word Prediction & Autocorrect** - Cross-platform word suggestion engine built into the shared Kotlin module. Displays up to 3 real-time suggestions above the keyboard. Tap to accept a suggestion, which replaces the current partial word. Includes next-word prediction after committing text.
- **Accelerating Backspace** - Hold-to-delete-words support on both platforms. A single tap deletes one character; holding backspace accelerates to delete entire words, matching native keyboard behavior.
- **Collapsible Settings Menu (Android)** - Replaced the long scrolling settings screen with an accordion-style layout using animated collapsible sections. Only one section expands at a time (Layout, Appearance, Accessibility, Privacy & Security). Font and theme options now use compact radio buttons instead of large pickers with preview text.
- **Collapsible Settings Menu (iOS Keyboard Extension)** - Redesigned the in-keyboard settings overlay from a flat Form to a ScrollView with collapsible card sections, matching the Android accordion pattern. Includes a custom `CollapsibleSettingsSection` component with animated chevron rotation and compact radio rows.

### Improvements
- **Documentation & Website Overhaul** - Comprehensive updates to README.md, APP_CONTEXT.md, android/README.md, ios/README.md, and the GitHub Pages site (docs/index.html, docs/accessibility.html). All documentation now reflects the full feature set including controller support, word prediction, custom layouts, and accessibility options.
- **Code Comment Localization** - Translated all 62 Simplified Chinese code comments to English across 6 source files: `MyInputMethodService.kt`, `KeyboardStateMachine.kt`, `KeyboardLogic.kt`, `KeyboardContracts.kt`, `ControllerBridge.swift`, and `KeyboardViewController.swift`. Improves readability and onboarding for all contributors.
- **JIRA Sprint Documentation** - Added Sprint 3 ticket tracking documentation.

### Bug Fixes
- Fixed Android UI rendering issues after controller input integration.
- Fixed right-stick UI sync when using physical controllers on iOS.

### Breaking Changes
None - existing user preferences remain compatible.

---

## Installation

### Android (Available Now)
Download the .apk file and install it.

**Requirements:**
- Android 7.0 (API 24) or higher
- ~20 MB storage space

### iOS
iOS keyboard with native SwiftUI interface and shared Kotlin logic. Build from source via the Xcode project.

---

## Demo
TODO

---
---

## [v0.3.2-alpha] - Mar 14, 2026

## What's New

### Major Features
- Radial Dial Keyboard UI (Android) - Completely overhauled joystick interface with distinct left/right radial dials, 8-segment radial blocks, direction-based color palettes, and layered concentric blocks. Includes icon support for control keys (backspace, caps lock, space, enter, home, end, shift).
- Live Character Preview - Real-time preview bar on Android showing highlighted, color-coded characters as you swipe, making chord input significantly more intuitive.
- Efficiency Layout - New optimized keyboard layout based on English letter frequency, selectable from Settings on both Android and iOS. Fully wired through shared logic and platform UIs.
- Refined iOS Radial Keyboard UI - iOS joystick views redesigned to closely match the Android radial dial design, with improved color and letter alignment and live preview feedback.
- iOS Onboarding Flow: Full step-by-step onboarding added to the iOS app with color sync matching the Android experience.
- iOS Settings Screen & App Logo - Native SwiftUI settings screen integrated with ERICK branding assets and app icon.
- Shared App-Group Preferences (iOS): Layout and theme preferences are now shared between the iOS host app and the keyboard extension via a shared UserDefaults app group, keeping them in sync.

### Improvements
- Color Palette Centralization - New ColorPalettes shared module object provides consistent direction-based colors across Android and iOS.
- Keyboard State Machine Integration (Android): MyInputMethodService now fully manages the KeyboardStateMachine lifecycle with a coroutine scope, routing all touch input through the shared state machine.
- iOS Swift Interop: Added explicit factory function overloads for iOS/Swift consumers so Kotlin default arguments are reliably exposed via Kotlin/Native.
- Xcode Project Portability: Removed machine-specific absolute paths from Xcode build settings, making the iOS project buildable across different developer machines.

### Bug Fixes
- Removed double swipe-right dial binds from shared Android and iOS logic.
- Fixed coroutine/lifecycle cleanup in IME service on Android.
- Fixed SettingsActivity layout option enabling/disabling logic.

### Breaking Changes
None - existing user preferences remain compatible.

---

## Installation

###Android (Available Now)
Download the .apk file and install it.

**Requirements:**
- Android 7.0 (API 24) or higher
- ~15 MB storage space

### iOS
iOS keyboard with native SwiftUI interface and shared Kotlin logic. Build from source via the Xcode project.

---

## Demo
TODO

---
---

## [v0.2.1-alpha] - Mar 7, 2026

## What's New

### Major Features
- **Kotlin Multiplatform Architecture** - Cross-platform keyboard logic ready for iOS expansion
- **Intelligent Setup Guide** - Step-by-step onboarding with real-time status indicators
- **Redesigned Settings** - Material 3 design with 4 organized sections (Layout, Appearance, Accessibility, Privacy)
- **Professional Branding** - Custom ERICK logo and consistent naming
- **Enhanced Keyboard UI** - Larger joysticks, cleaner layout, optimized touch targets

### Bug Fixes
- Fixed keyboard status not refreshing when selecting input method
- Fixed layout crashes and missing imports
- Improved reactive state management

### Breaking Changes
- None - this is backwards compatible with existing user preferences.

---

## Installation

### Android (Available Now)
Download the `.apk` file and install it

**Requirements:**
- Android 7.0 (API 24) or higher
- 10 MB storage space

### iOS (Coming Q2 2026)
iOS keyboard with native SwiftUI interface and shared Kotlin logic.

---

### User Onboarding & Typing Demo
<img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/v0.2.1a_user_onboarding.gif" height="400" /> & <img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/v0.2.1a_typing_demo.gif" height="400" /> 

---
---

## [v0.1.7-alpha] - Jul 8, 2022

This release includes:
- Support for Android 12
- Added detection for controller i.e. to check if the controller is connected. 
- Disabled touch-based controller. (In-works for further improvement) 
- Bug fixes (3 major issues and various minor issues resolved)

Install using APK file provided.

### Swipe Typing vs Typing with Controller
<img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/swipe.gif" height="200" /> vs <img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/controller.gif" height="200" />

### Keyboard Typing with No Fingers vs Typing with Controller
<img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/no%20hands.gif" height="200" /> vs <img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/no%20hands%20type.gif" height="200" />

<br>
Worked on by @VatsalUnadkat and @bisensamiksha 

---
---

## Upcoming in Future Releases

### [Planned for 1.0.0]
- Multi-language support (extended dictionaries, character sets)
- 6 Section dials mode

---

**Note**: Alpha versions are pre-release builds for testing and development. Features and APIs may change without notice.
