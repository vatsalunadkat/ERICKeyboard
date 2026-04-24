# ERICK - iOS

This folder contains the iOS implementation of ERICK, the Ergonomic Radial Inclusive Controller Keyboard.

## Overview

ERICK iOS is a Custom Keyboard Extension that replaces tiny on-screen keys with two large directional controls. Users type by combining directional swipes into character chords. The iOS build supports touch input, physical game controllers, prediction features, accessibility options, and shared keyboard behavior through the Kotlin Multiplatform `SharedKeyboard.xcframework`.

The iOS App Store release is coming soon. Current iOS distribution is through local or development builds from Xcode.

This build is especially relevant for:

- developers working on the iOS keyboard extension
- developers maintaining the shared iOS host app and extension settings flow
- anyone testing controller input or cross-platform parity with Android

## Requirements

- macOS with Xcode 15 or later
- iOS 15.0 or later
- Swift 5.9 or later
- `SharedKeyboard.xcframework` built from the shared Kotlin module

## Getting Started

1. Open `ios/ERICK/ERICK.xcodeproj` in Xcode.
2. Make sure `SharedKeyboard.xcframework` is present in `ios/ERICK/`.
3. Select a simulator or connected device.
4. Build and run the **ERICK** scheme.
5. Add the keyboard in iOS settings and switch to it in any text field.

## Key Features in the iOS Build

- iOS keyboard extension
- Chorded typing with two radial controls
- Three input modes: Quick Type, Steady Type, and One-Handed
- Three layout modes: Logical, Efficiency, and Custom
- Word prediction and autocorrect
- Live preview bar and suggestion bar
- Physical controller support
- Shared settings between host app and extension
- Accessibility features such as left-handed mode, dyslexia-friendly fonts, and color palettes
- Haptic feedback, supported controller rumble, and typing sounds

## Project Structure

```text
ios/
├── ERICK/
│   ├── ERICK/                         # Host app
│   │   ├── ERICKApp.swift             # SwiftUI @main entry point
│   │   ├── ContentView.swift          # Onboarding, controller status, test typing field
│   │   ├── SettingsView.swift         # Host app settings UI
│   │   ├── HelpView.swift             # User help content
│   │   ├── ControllerBridge.swift     # Polls GCController at 60 FPS via DisplayLink
│   │   ├── IOSCustomLayoutStorage.swift
│   │   ├── TypingGameView.swift       # Typing practice mini-game
│   │   ├── ERICK.entitlements         # App Group entitlement
│   │   └── Assets.xcassets/           # App icons and assets
│   ├── ErickKeyBoard/                 # Keyboard Extension
│   │   ├── KeyboardViewController.swift   # UIInputViewController entry point
│   │   ├── JoystickView.swift             # SwiftUI radial touch input
│   │   ├── SettingsView.swift             # In-keyboard settings overlay
│   │   ├── IOSCustomLayoutStorage.swift   # Extension-side custom layout CRUD
│   │   ├── ErickKeyBoard.entitlements     # App Group entitlement
│   │   └── Info.plist                     # Extension configuration
│   ├── SharedKeyboard.xcframework/    # KMP compiled framework
│   │   ├── ios-arm64/                 # Device binary
│   │   └── ios-arm64_x86_64-simulator/  # Simulator binary
│   └── ERICK.xcodeproj/
└── README.md
```

## Important Components

### `KeyboardViewController`

The main keyboard extension controller (`UIInputViewController` subclass). It hosts the SwiftUI keyboard UI via `UIHostingController`, connects to the active text field via `textDocumentProxy`, manages controller input, and forwards touch and controller events to the shared `KeyboardStateMachine`.

### `JoystickView`

SwiftUI view with a circular touch area implementing 8-directional detection, color-coded sectors with character labels, spring-back animation, and left-handed mode support. Renders the preview bar (animated capsule showing color-coded characters as chords form, with stroked text for readability across themes) and suggestion bar (3-suggestion strip).

### `ControllerBridge`

The host-app bridge that polls `GCController` at 60 FPS via `CADisplayLink` and writes normalized stick data to App Group `UserDefaults`. This bridge is needed because keyboard extensions run in a separate process from the host app.

### `SharedKeyboard.xcframework`

The compiled Kotlin Multiplatform framework that provides:

- `KeyboardStateMachine` - chord processing, word buffer, suggestion orchestration
- `KeyboardLogic` - chord resolution across Logical, Efficiency, and Custom layouts
- `WordPredictionEngine` - Trie-based dictionary, bigrams, Levenshtein autocorrect
- `ColorPalettes` - 7 accessibility color schemes including custom palette support
- `KeyboardContracts` - platform interfaces and data classes

## Building the Shared Framework

From the `android/` directory:

```bash
./gradlew assembleSharedKeyboardXCFramework
```

Then copy the output into `ios/ERICK/SharedKeyboard.xcframework/`.

## Testing the Keyboard

1. Build and run the host app.
2. Add **ErickKeyBoard** in iOS keyboard settings.
3. Enable **Allow Full Access** if prompted for shared settings and controller support.
4. Open any text field and switch to ERICK.
5. Test touch input, prediction, and controller support if available.

## Notes

- ERICK is source available and fully offline.
- **UI Framework**: SwiftUI for all keyboard views, hosted inside the `UIInputViewController` via `UIHostingController`.
- **Multiplatform Strategy**: The Kotlin Multiplatform shared module is compiled to a native iOS framework (`SharedKeyboard.xcframework`). Swift imports the framework and calls the KMP APIs directly.
- **Persistence**: App Group UserDefaults (`group.com.vatoo.erick`) enable the host app and keyboard extension to share settings. Keys mirror the Android DataStore schema.
- The iOS keyboard shares core behavior with Android through the shared Kotlin module.
- For broader product context, read the root [README](../README.md).

## Troubleshooting

- **Keyboard not appearing**: Ensure you have added ErickKeyBoard in Settings → General → Keyboard → Keyboards.
- **"Allow Full Access" prompt**: The extension needs full access to read App Group UserDefaults for shared settings.
- **SharedKeyboard.xcframework missing**: Rebuild from the `android/` directory with `./gradlew assembleSharedKeyboardXCFramework` and copy the output.
- **Controller not detected**: The controller must be connected to the host app first. Open the ERICK app and verify the controller shows as connected.
