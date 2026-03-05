# ERICK - Android

This folder contains the Android implementation of ERICK (Ergonomic Radial Inclusive Controller Keyboard).

## Setup

### Requirements

- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.9+
- Gradle 8.0+

### Getting Started

1. Open this folder (android/) in Android Studio
2. Sync Gradle files
3. Build and run on emulator or device

## Project Structure

```
android/
├── app/                # Main application module
├── gradle/            # Gradle wrapper files
├── build.gradle.kts   # Root build configuration
├── settings.gradle.kts # Project settings
└── README.md          # This file
```

## Building

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```
