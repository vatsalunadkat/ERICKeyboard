# ERICK - Android

This folder contains the Android implementation of ERICK (Ergonomic Radial Inclusive Controller Keyboard).

## Overview

ERICK Android is a custom Input Method Editor (IME) that provides chord-based text input using dual touch joysticks or a physical gaming controller. It features word prediction & autocorrect, multiple accessibility options, and a fully customizable layout — all powered by a Kotlin Multiplatform shared module.

## Setup

### Requirements

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24 or higher (target SDK 36)
- Kotlin 1.9+
- Gradle 8.0+
- JDK 17 or higher

### Getting Started

1. Open this folder (`android/`) in Android Studio
2. Sync Gradle files
3. Build and run on emulator or device (API level 24+)
4. Follow the in-app onboarding to enable ERICK as an input method

## Project Structure

```
android/
├── app/                          # Main application module
│   ├── src/main/java/            # Android-specific code
│   │   ├── MainActivity.kt      # Onboarding and IME setup UI
│   │   ├── MyInputMethodService.kt  # IME service (preview bar, suggestions, controller)
│   │   ├── JoystickView.kt      # Custom Canvas-based touch joystick
│   │   ├── SettingsActivity.kt   # Settings UI
│   │   ├── SettingsScreen.kt     # Compose settings screen
│   │   ├── SettingsViewModel.kt  # Settings state management
│   │   └── LayoutPreferences.kt  # DataStore preferences
│   ├── src/main/res/layout/
│   │   └── keyboard_simple.xml   # Keyboard layout (joysticks + preview + suggestions)
│   └── build.gradle.kts
├── shared/                       # Kotlin Multiplatform module
│   ├── src/commonMain/kotlin/    # Cross-platform keyboard logic
│   │   ├── KeyboardStateMachine.kt   # State machine, word buffer, suggestion orchestration
│   │   ├── KeyboardLogic.kt          # Chord resolution, 4 layout maps
│   │   ├── KeyboardContracts.kt      # Platform interfaces
│   │   ├── WordPredictionEngine.kt   # Trie, bigrams, autocorrect (~700 words)
│   │   └── ColorPalettes.kt          # 6 accessibility color palettes
│   ├── src/androidMain/          # Android platform code
│   └── src/iosMain/              # iOS platform code
├── gradle/
│   └── libs.versions.toml       # Dependency version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Key Components

### Input Method Editor (IME)
- **MyInputMethodService**: Main IME service — handles keyboard lifecycle, preview bar rendering, suggestion bar display, physical controller polling, theme application, and accelerating backspace.
- **JoystickView**: Custom Canvas-based View for touch joystick input with visual knob and return-to-center animation.
- **Preview Bar**: Animated capsule showing color-coded characters as chords form.
- **Suggestion Bar**: 3-suggestion strip displaying word completions, spelling corrections, or next-word predictions. Appears at the same level as the preview capsule.

### Word Prediction & Autocorrect
- **WordPredictionEngine** (shared): Trie-based dictionary with prefix completions, Levenshtein spelling corrections, and bigram next-word predictions.
- Always-on suggestions — predictions shown immediately on keyboard open (defaults like "I", "The", "Hello").
- Smart space insertion when accepting next-word suggestions.

### Physical Controller Support
- Detects connected gamepads via `InputManager`
- Polls analog stick positions to produce 8-directional inputs
- Maps controller axes to the same `KeyboardStateMachine` chord logic

### Settings & Preferences
- **SettingsActivity/Screen**: Jetpack Compose UI for:
  - Layout (Efficient, Accessible, Legacy, Custom)
  - Theme (Light, Dark, System)
  - Font (Default, OpenDyslexic, Atkinson Hyperlegible)
  - Colorblind palette (6 options)
  - Left-handed mode
  - Custom Layout Creator with chord editor and color indicators
- **LayoutPreferences**: DataStore-based persistence for all above settings plus custom layout JSON.

### Shared Module (KMP)
The `shared` module contains platform-agnostic logic compiled for both Android (JVM) and iOS (native):
- Chord state machine and word buffer management
- 4 layout maps (Efficient, Accessible, Legacy, Custom)
- Word prediction engine (trie + bigrams + autocorrect)
- 6 colorblind-safe palettes
- Platform interfaces for settings, input connection, and controller events

## Building

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test

# Run Android instrumentation tests
./gradlew connectedAndroidTest
```

## Testing the Keyboard

1. Build and install the app
2. Open the app and follow the onboarding:
   - Enable ERICK in system settings
   - Select ERICK as current keyboard
3. Open any text input field (e.g., Messages, Notes)
4. Use the touch joysticks (or a connected gamepad) to input text via chord combinations
5. Tap suggestions in the suggestion bar to accept word completions or next-word predictions

## Architecture Notes

**State Management**: The keyboard uses a state machine pattern to track joystick movements, manage a word buffer, and orchestrate suggestion updates.

**Multiplatform Strategy**: Core logic lives in `shared/src/commonMain` and is compiled to JVM bytecode for Android and native code for iOS.

**UI Framework**: Modern Android UI built with Jetpack Compose; the keyboard overlay uses XML with Canvas-based custom views.

**Persistence**: Settings use Jetpack DataStore (Preferences) for type-safe, async storage.

**Word Prediction**: A trie-based dictionary with bigram next-word predictions and Levenshtein-based spelling corrections — all running locally with zero network calls.

## Troubleshooting

**IME not appearing**: Make sure you've enabled ERICK in Settings → System → Languages & Input → On-screen keyboard

**Build errors**: Ensure you're using the correct JDK version (17+) and have synced Gradle files

**Controller not detected**: Verify the gamepad is connected and recognized by Android. ERICK polls `InputManager` for connected input devices.

**Shared module errors**: The KMP shared module requires Kotlin Multiplatform plugin - ensure Android Studio has the latest Kotlin plugin installed
