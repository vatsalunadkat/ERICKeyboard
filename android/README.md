# ERICK - Android

This folder contains the Android implementation of ERICK, the Ergonomic Radial Inclusive Controller Keyboard.

## Overview

ERICK Android is a custom Input Method Editor (IME) built around two large directional controls instead of rows of tiny keys. Users type by combining left and right directional swipes into character chords. The Android app supports touch input, physical game controllers, word prediction, accessibility features, and custom layouts while sharing its core typing logic with iOS through Kotlin Multiplatform.

The current production Android build is available on [Google Play](https://play.google.com/store/apps/details?id=com.vatoo.erick).

In practical terms, this build is for:

- users who need a more accessible typing option
- users who want controller-based typing
- developers working on the Android IME, onboarding flow, and settings UI

## Requirements

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24 or higher
- target SDK 36
- JDK 17 or higher

## Getting Started

1. Open the `android/` folder in Android Studio.
2. Sync Gradle files.
3. Build and run on an emulator or device.
4. Follow the in-app onboarding to enable ERICK as an input method.

## Key Features in the Android Build

- Android IME integration
- Chorded typing with two radial controls
- Three input modes: Quick Type, Steady Type, and One-Handed
- Three layout modes: Logical, Efficiency, and Custom
- Word prediction and autocorrect
- Live preview bar and suggestion bar
- Physical controller support
- Typing practice mini-game
- Colorblind-safe palettes and custom colors
- Left-handed mode
- Dyslexia-friendly font options
- Haptic feedback and typing sounds

## Project Structure

```text
android/
├── app/
│   ├── src/main/java/
│   │   ├── MainActivity.kt
│   │   ├── MyInputMethodService.kt
│   │   ├── JoystickView.kt
│   │   ├── SettingsActivity.kt
│   │   ├── SettingsScreen.kt
│   │   └── PreferencesManager.kt
│   └── build.gradle.kts
├── shared/
│   ├── src/commonMain/kotlin/
│   │   ├── KeyboardStateMachine.kt
│   │   ├── KeyboardLogic.kt
│   │   ├── KeyboardContracts.kt
│   │   ├── WordPredictionEngine.kt
│   │   ├── ColorPalettes.kt
│   │   ├── CustomLayout.kt
│   │   └── CustomLayoutSerializer.kt
│   └── build.gradle.kts
└── README.md
```

## Important Components

### `MyInputMethodService`

The Android IME service. It connects ERICK to Android text fields, forwards touch and controller input to the shared state machine, and renders the preview and suggestion UI.

### `JoystickView`

Custom `View` that draws the radial touch dial with 8 color-coded sectors, 3 concentric rings displaying characters, animated return-to-center behavior, and left-handed mode support.

### Shared Kotlin Multiplatform module

The `shared/` module contains platform-agnostic logic compiled for both Android (JVM) and iOS (XCFramework):

- `KeyboardStateMachine` - chord state tracking, word buffer, suggestion orchestration, accelerating backspace
- `KeyboardLogic` - 8-way direction detection via `atan2`, chord-to-character resolution across 3 layouts
- `WordPredictionEngine` - Trie-based dictionary (~700 words), bigram next-word predictions, Levenshtein autocorrect
- `ColorPalettes` - 7 accessibility palettes including custom palette support
- `KeyboardContracts` - interfaces, enums, and data classes shared across platforms
- `CustomLayout` / `CustomLayoutSerializer` - user layout models, CRUD, validation, JSON serialization

## Build Commands

```bash
./gradlew clean
./gradlew assembleDebug
./gradlew :shared:testAndroidHostTest
./gradlew installDebug
./gradlew connectedAndroidTest
```

## Testing the Keyboard

1. Install the app.
2. Enable ERICK in Android keyboard settings.
3. Open any text field.
4. Switch to ERICK.
5. Test both touch input and controller input if available.

## Notes

- ERICK is source available and fully offline.
- **Build stack**: compileSdk 36, minSdk 24, targetSdk 36, Kotlin 2.0.21, AGP 8.13.2, Jetpack Compose (BOM 2024.09.00).
- **Persistence**: Jetpack DataStore for type-safe async preference storage.
- The Android implementation shares core typing behavior with iOS through the `shared` module.
- For product-level context, read the root [README](../README.md).

## Troubleshooting

- **Keyboard not appearing**: Verify ERICK is enabled in Settings → System → Languages & input → On-screen keyboard. Restart the device if needed.
- **Controller not detected**: Ensure the controller is paired in Bluetooth settings and recognized by the system. Try reconnecting.
- **Build errors with SharedKeyboard**: Run `./gradlew clean` then rebuild. Ensure JDK 17 is configured in Android Studio.
