# ERICK - Android

This folder contains the Android implementation of ERICK (Ergonomic Radial Inclusive Controller Keyboard).

## Overview

ERICK Android is a custom Input Method Editor (IME) that enables chord-based text input using touch joysticks. The app uses Kotlin Multiplatform to share keyboard logic with the iOS implementation.

## Setup

### Requirements

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24 or higher (target SDK 36)
- Kotlin 1.9+
- Gradle 8.0+
- JDK 17 or higher

### Getting Started

1. Open this folder (android/) in Android Studio
2. Sync Gradle files
3. Build and run on emulator or device (API level 24+)
4. Follow the in-app onboarding to enable ERICK as an input method

## Project Structure

```
android/
├── app/                      # Main application module
│   ├── src/main/java/        # Android-specific code
│   │   ├── MainActivity.kt   # Onboarding and IME setup UI
│   │   ├── MyInputMethodService.kt  # IME service implementation
│   │   ├── JoystickView.kt   # Custom touch joystick view
│   │   ├── SettingsActivity.kt      # Settings UI
│   │   ├── SettingsScreen.kt        # Compose settings screen
│   │   ├── SettingsViewModel.kt     # Settings state management
│   │   └── LayoutPreferences.kt     # DataStore preferences
│   └── build.gradle.kts      # App module configuration
├── shared/                   # Kotlin Multiplatform module
│   ├── commonMain/           # Shared keyboard logic
│   │   ├── KeyboardStateMachine.kt  # State machine for chords
│   │   ├── KeyboardLogic.kt         # Chord processing logic
│   │   └── KeyboardContracts.kt     # Interfaces and contracts
│   ├── androidMain/          # Android platform code
│   └── iosMain/              # iOS platform code (for future)
├── gradle/                   # Gradle wrapper files
├── build.gradle.kts          # Root build configuration
├── settings.gradle.kts       # Project settings
└── README.md                 # This file
```

## Key Components

### Input Method Editor (IME)
- **MyInputMethodService**: Main IME service that handles keyboard lifecycle
- **JoystickView**: Custom view for rendering and handling touch-based joystick input
- **KeyboardStateMachine** (shared): Processes joystick movements into chords and characters

### Settings & Preferences
- **SettingsActivity/Screen**: Jetpack Compose UI for configuration
- **LayoutPreferences**: DataStore-based persistence for:
  - Keyboard layout (Efficient, Accessible, Legacy)
  - Theme (Light, Dark, System)
  - Colorblind mode
  - Left-handed mode

### Shared Module (KMP)
The `shared` module contains platform-agnostic keyboard logic that will be reused in the iOS app:
- Chord state machine
- Input processing algorithms
- Keyboard layout definitions
- Character mapping logic

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
4. Use the touch joysticks to input text via chord combinations

## Architecture Notes

**State Management**: The keyboard uses a state machine pattern to track joystick movements and determine when a complete chord is formed.

**Multiplatform Strategy**: Core logic lives in `shared/commonMain` and is compiled to native code for Android (JVM) and iOS (native).

**UI Framework**: Modern Android UI built with Jetpack Compose; legacy keyboard layouts use XML.

**Persistence**: Settings use Jetpack DataStore (Preferences) for type-safe, async storage.

## Troubleshooting

**IME not appearing**: Make sure you've enabled ERICK in Settings → System → Languages & Input → On-screen keyboard

**Build errors**: Ensure you're using the correct JDK version (17+) and have synced Gradle files

**Shared module errors**: The KMP shared module requires Kotlin Multiplatform plugin - ensure Android Studio has the latest Kotlin plugin installed
