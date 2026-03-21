# Changelog

All notable changes to the ERICK (Ergonomic Radial Inclusive Controller Keyboard) project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v0.4.2-alpha] - Mar 21, 2026

## What's New

### Major Features
- **Physical Gaming Controller Support** — Full DualShock 4, 8BitDo, and other Bluetooth game controller support on both Android and iOS. Left and right analog sticks map to the left and right joystick dials respectively. On iOS, a ControllerBridge in the host app shares controller state with the keyboard extension via App Group, since extensions run in a separate process.
- **Word Prediction & Autocorrect** — Cross-platform word suggestion engine built into the shared Kotlin module. Displays up to 3 real-time suggestions above the keyboard. Tap to accept a suggestion, which replaces the current partial word. Includes next-word prediction after committing text.
- **Accelerating Backspace** — Hold-to-delete-words support on both platforms. A single tap deletes one character; holding backspace accelerates to delete entire words, matching native keyboard behavior.
- **Collapsible Settings Menu (Android)** — Replaced the long scrolling settings screen with an accordion-style layout using animated collapsible sections. Only one section expands at a time (Layout, Appearance, Accessibility, Privacy & Security). Font and theme options now use compact radio buttons instead of large pickers with preview text.
- **Collapsible Settings Menu (iOS Keyboard Extension)** — Redesigned the in-keyboard settings overlay from a flat Form to a ScrollView with collapsible card sections, matching the Android accordion pattern. Includes a custom `CollapsibleSettingsSection` component with animated chevron rotation and compact radio rows.

### Improvements
- **Documentation & Website Overhaul** — Comprehensive updates to README.md, APP_CONTEXT.md, android/README.md, ios/README.md, and the GitHub Pages site (docs/index.html, docs/accessibility.html). All documentation now reflects the full feature set including controller support, word prediction, custom layouts, and accessibility options.
- **Code Comment Localization** — Translated all 62 Simplified Chinese code comments to English across 6 source files: `MyInputMethodService.kt`, `KeyboardStateMachine.kt`, `KeyboardLogic.kt`, `KeyboardContracts.kt`, `ControllerBridge.swift`, and `KeyboardViewController.swift`. Improves readability and onboarding for all contributors.
- **JIRA Sprint Documentation** — Added Sprint 3 ticket tracking documentation.

### Bug Fixes
- Fixed Android UI rendering issues after controller input integration.
- Fixed right-stick UI sync when using physical controllers on iOS.

### Breaking Changes
None — existing user preferences remain compatible.

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
None — existing user preferences remain compatible.

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
<img src="https://github.com/vatsalunadkat/Ergonomic-Inclusive-Controller-Keybord/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/v0.2.1a_user_onboarding.gif" height="400" /> & <img src="https://github.com/vatsalunadkat/Ergonomic-Inclusive-Controller-Keybord/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/v0.2.1a_typing_demo.gif" height="400" /> 

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
<img src="https://github.com/vatsalunadkat/Ergonomic-Inclusive-Controller-Keybord/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/swipe.gif" height="200" /> vs <img src="https://github.com/vatsalunadkat/Ergonomic-Inclusive-Controller-Keybord/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/controller.gif" height="200" />

### Keyboard Typing with No Fingers vs Typing with Controller
<img src="https://github.com/vatsalunadkat/Ergonomic-Inclusive-Controller-Keybord/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/no%20hands.gif" height="200" /> vs <img src="https://github.com/vatsalunadkat/Ergonomic-Inclusive-Controller-Keybord/blob/0cdf578a7a3596009c78906d2e717a7c7de9afdd/documentation/demo%20files/no%20hands%20type.gif" height="200" />

<br>
Worked on by @VatsalUnadkat and @bisensamiksha 

---
---

## Upcoming in Future Releases

### [Planned for 0.5.0]
- Multi-language support (extended dictionaries, character sets)
- Mini typing game for chord learning and speed practice

### [Planned for 1.0.0]
- Production-ready stability
- Cloud settings sync
- Typing speed analytics and personal bests
- App Store and Play Store releases

---

**Note**: Alpha versions are pre-release builds for testing and development. Features and APIs may change without notice.
