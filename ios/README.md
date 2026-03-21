# ERICK - iOS

This folder contains the iOS implementation of ERICK (Ergonomic Radial Inclusive Controller Keyboard).

## Overview

ERICK iOS is a Custom Keyboard Extension that provides chord-based text input using dual touch joysticks or a physical gaming controller. It shares its core logic with the Android app via a Kotlin Multiplatform `SharedKeyboard.xcframework` and presents a native SwiftUI interface.

## Setup

### Requirements

- macOS with Xcode 15 or later
- iOS 15.0+ deployment target
- Swift 5.9+
- SharedKeyboard.xcframework (built from the KMP shared module)

### Getting Started

1. Open `ios/ERICK/ERICK.xcodeproj` in Xcode
2. Ensure the `SharedKeyboard.xcframework` is present under `ios/ERICK/SharedKeyboard.xcframework/`
3. Select a simulator or connected device (iOS 15+)
4. Build and run the **ERICK** scheme for the host app
5. To test the keyboard:
   - Go to **Settings → General → Keyboard → Keyboards → Add New Keyboard**
   - Select **ErickKeyBoard**
   - Grant "Allow Full Access" if prompted
   - Open any text field and switch to the ERICK keyboard

### Building the XCFramework

From the `android/` directory, run:

```bash
./gradlew assembleSharedKeyboardXCFramework
```

Then copy the output into `ios/ERICK/SharedKeyboard.xcframework/`.

## Project Structure

```
ios/
├── ERICK/
│   ├── ERICK/                    # Host app
│   │   ├── ERICKApp.swift        # App entry point
│   │   ├── ContentView.swift     # Main content view
│   │   ├── SettingsView.swift    # Host app settings UI
│   │   ├── ERICK.entitlements    # App Group entitlement
│   │   └── Assets.xcassets/      # App icons and assets
│   ├── ErickKeyBoard/            # Keyboard Extension
│   │   ├── KeyboardViewController.swift   # UIInputViewController entry point
│   │   ├── JoystickView.swift             # SwiftUI touch joystick
│   │   ├── SettingsView.swift             # Extension settings UI
│   │   ├── ErickKeyBoard.entitlements     # App Group entitlement
│   │   └── Info.plist                     # Extension configuration
│   ├── SharedKeyboard.xcframework/  # KMP compiled framework
│   │   ├── ios-arm64/               # Device binary
│   │   └── ios-arm64_x86_64-simulator/  # Simulator binary
│   └── ERICK.xcodeproj/            # Xcode project
└── README.md
```

## Key Components

### Keyboard Extension
- **KeyboardViewController**: `UIInputViewController` subclass that hosts the SwiftUI keyboard UI, connects to text fields via `textDocumentProxy`, and manages controller input.
- **JoystickView**: SwiftUI view with circular touch area, 8-directional detection, spring-back animation, and left-handed mode support.
- **KeyboardPreviewBar**: Animated capsule showing color-coded characters as chords form.
- **KeyboardSuggestionBar**: 3-suggestion strip for word completions, spelling corrections, and next-word predictions.

### Word Prediction & Autocorrect
- Uses `WordPredictionEngine` from SharedKeyboard.xcframework
- Always-on suggestions with default sentence starters
- Smart space insertion for next-word predictions

### Physical Controller Support
- Listens for `GCController.didConnect` / `didDisconnect` notifications
- Polls gamepad analog sticks via `CADisplayLink`
- Maps analog axes to 8-directional inputs for the shared state machine

### Settings & Persistence
- Settings stored in **App Group UserDefaults** (`group.com.vatoo.erick`) so both the host app and the keyboard extension share the same preferences
- Supports: layout mode, theme, font, colorblind palette, left-handed mode, custom layout

### Shared Framework
The `SharedKeyboard.xcframework` provides:
- `KeyboardStateMachine` — chord processing, word buffer, suggestion orchestration
- `KeyboardLogic` — chord resolution with 4 layout maps
- `WordPredictionEngine` — trie, bigrams, autocorrect
- `ColorPalettes` — 6 accessibility color schemes
- `KeyboardContracts` — platform interfaces

## Architecture Notes

**UI Framework**: SwiftUI for all keyboard views, hosted inside the `UIInputViewController` via `UIHostingController`.

**Multiplatform Strategy**: The Kotlin Multiplatform shared module is compiled to a native iOS framework (`SharedKeyboard.xcframework`). Swift imports the framework and calls the KMP APIs directly.

**Persistence**: App Group UserDefaults enable the host app and keyboard extension to share settings. Keys mirror the Android DataStore schema.

## Troubleshooting

**Keyboard not appearing**: Ensure you've added ErickKeyBoard in Settings → General → Keyboard → Keyboards.

**"Allow Full Access" prompt**: The extension needs full access to read App Group UserDefaults.

**SharedKeyboard.xcframework missing**: Rebuild from the `android/` directory with `./gradlew assembleSharedKeyboardXCFramework` and copy the output.
