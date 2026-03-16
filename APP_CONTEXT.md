# ERICK - Application Context & Architecture

**Version**: 0.2.1-alpha  
**Last Updated**: March 8, 2026  
**Project**: Ergonomic Radial Inclusive Controller Keyboard (ERICK)

## Executive Summary

ERICK is a cross-platform chorded keyboard system that enables text input through dual joystick movements (touch or physical controller). The application uses Kotlin Multiplatform to share core keyboard logic between Android and iOS implementations, with Android currently featuring a fully functional Input Method Editor (IME) service.

## Architecture Overview

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                 Platform Layer (UI/OS)                   │
│  ┌─────────────────────┐      ┌─────────────────────┐  │
│  │   Android IME       │      │   iOS Extension     │  │
│  │  - Activities       │      │  (In Development)   │  │
│  │  - IME Service      │      │                     │  │
│  │  - Compose UI       │      │                     │  │
│  └──────────┬──────────┘      └──────────┬──────────┘  │
└─────────────┼─────────────────────────────┼─────────────┘
              │                             │
              └──────────┬──────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│          Shared Module (Kotlin Multiplatform)            │
│  ┌─────────────────────────────────────────────────┐    │
│  │  KeyboardStateMachine                           │    │
│  │  - State tracking (idle, first stick, complete) │    │
│  │  - Chord validation                             │    │
│  │  - Event processing                             │    │
│  └─────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────┐    │
│  │  KeyboardLogic                                  │    │
│  │  - Direction mapping                            │    │
│  │  - Character resolution                          │    │
│  │  - Chord combinations                           │    │
│  └─────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────┐    │
│  │  KeyboardContracts                              │    │
│  │  - Interfaces and data classes                  │    │
│  │  - Platform abstractions                        │    │
│  └─────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────┘
```

### Technology Stack

**Cross-Platform:**
- Kotlin Multiplatform (KMP) - Shared business logic
- Coroutines - Async operations and state management

**Android:**
- Kotlin - Primary language
- Jetpack Compose - Modern declarative UI
- DataStore - Type-safe preferences storage
- Android IME Framework - Custom keyboard implementation
- Material Design 3 - UI components

**iOS (Planned):**
- Swift/Objective-C interop with KMP
- Custom Keyboard Extension

## Core Components

### 1. Shared Module (Kotlin Multiplatform)

Located in `android/shared/src/commonMain/kotlin/`

#### KeyboardStateMachine
**Purpose**: Manages the state transitions of joystick inputs to form complete chords.

**States**:
- `Idle`: No joystick active
- `FirstStickActive`: One joystick moved (awaiting second)
- `ChordComplete`: Both joysticks registered (ready to output character)

**Key Methods**:
- `processInput(leftDirection, rightDirection)`: Processes joystick positions
- `reset()`: Returns to idle state
- `getCurrentChord()`: Returns the active chord combination

#### KeyboardLogic
**Purpose**: Translates chord combinations into characters.

**Responsibilities**:
- Direction enumeration (UP, DOWN, LEFT, RIGHT, CENTER)
- Chord-to-character mapping
- Special character handling
- Layout switching support

#### KeyboardContracts
**Purpose**: Defines interfaces and data structures shared between platforms.

**Key Interfaces**:
- `KeyboardActionDelegate`: Platform callback for key events
- `ChordDefinition`: Data class for chord configurations
- `KeyboardLayout`: Interface for different layout modes

### 2. Android Implementation

Located in `android/app/src/main/java/com/vatoo/erick/`

#### MyInputMethodService
**Purpose**: Android IME service that integrates the keyboard with the Android OS.

**Responsibilities**:
- IME lifecycle management (onCreate, onDestroy)
- Input view creation and management
- Connection to text fields
- Integration with KeyboardStateMachine
- Coroutine scope management for async operations

**Key Features**:
- Uses custom keyboard layout (XML + programmatic views)
- Handles touch events from JoystickView
- Dispatches characters to active input connections
- Launches settings activity

#### JoystickView
**Purpose**: Custom Android View for rendering and handling touch-based joystick input.

**Features**:
- Circular touch area with visual feedback
- Directional detection (8-way or 4-way)
- Return-to-center animation
- Configurable sensitivity and dead zones
- Real-time position tracking

**Technical Details**:
- Extends `View`
- Custom `onDraw()` for rendering
- `onTouchEvent()` for input handling
- Callback interface for direction changes

#### MainActivity
**Purpose**: Onboarding and IME enablement UI.

**Features**:
- **Guided Setup Flow**:
  1. Check if ERICK is enabled in system settings
  2. Check if ERICK is selected as current keyboard
  3. Provide direct links to system settings
- Helper functions to detect IME status
- Jetpack Compose UI for modern, responsive design
- Navigation to settings

#### SettingsActivity / SettingsScreen
**Purpose**: Configuration UI for keyboard preferences.

**Settings Available**:
- **Layout Mode**: Efficient, Accessible, Legacy
- **Theme**: Light, Dark, System Default
- **Accessibility**:
  - Colorblind mode (adjust colors)
  - Left-handed mode (mirror layout)
- **Future**: Typing speed, haptic feedback, sound effects

**Technical Details**:
- Built with Jetpack Compose
- Material Design 3 components
- Real-time preview of settings
- Persists to DataStore

#### LayoutPreferences (DataStore)
**Purpose**: Type-safe, asynchronous persistence of user settings.

**Stored Preferences**:
- `selectedLayout: String`
- `theme: String`
- `colorblindMode: Boolean`
- `leftHandedMode: Boolean`

**Technical Approach**:
- Uses Preferences DataStore (key-value)
- Coroutine-based async reads/writes
- Flow-based reactive updates
- Migration support from SharedPreferences (if needed)

### 3. Data Flow

#### Input Flow (Touch to Character)

```
User Touch on JoystickView
    ↓
JoystickView detects direction
    ↓
Callback to MyInputMethodService
    ↓
Dispatch to KeyboardStateMachine.processInput()
    ↓
State machine checks current state:
  - Idle → FirstStickActive (store direction)
  - FirstStickActive → ChordComplete (both directions recorded)
    ↓
KeyboardLogic.resolveChord(leftDir, rightDir)
    ↓
Character returned (e.g., "A", "5", "?")
    ↓
MyInputMethodService.getCurrentInputConnection().commitText()
    ↓
Character appears in text field
```

#### Settings Flow

```
User opens Settings (from MainActivity or IME settings button)
    ↓
SettingsActivity/SettingsScreen rendered (Compose)
    ↓
User changes setting (toggle/radio button)
    ↓
SettingsViewModel updates state
    ↓
LayoutPreferences writes to DataStore
    ↓
Flow emits new preference value
    ↓
MyInputMethodService observes Flow
    ↓
Keyboard behavior updates (layout/theme/etc.)
```

## Chord Input System

### Chord Definition

A "chord" is a combination of two joystick directions (left stick + right stick) that maps to a character.

**Example Chords**:
- Left: UP, Right: UP → "A"
- Left: RIGHT, Right: UP → "5"
- Left: CENTER, Right: DOWN → "backspace"

### Layout Modes

1. **Efficient Mode**: Optimized for typing speed, common letters on easy chords
2. **Accessible Mode**: Simplified layout for users with motor impairments
3. **Legacy Mode**: Traditional layout similar to OrbiTouch keyboard

### State Machine Logic

The state machine prevents accidental inputs and ensures proper chord completion:

1. **Idle State**: Waiting for input
   - Both joysticks at center
   - No pending chord

2. **FirstStickActive**: One joystick moved
   - Records which joystick moved and in what direction
   - Waits for second joystick
   - Timeout (if implemented) returns to Idle

3. **ChordComplete**: Both joysticks have moved
   - Chord is resolved to character
   - Character is output
   - State resets to Idle (or waits for joysticks to return to center)

## Configuration and Preferences

### DataStore Schema (Preferences)

```kotlin
// Key definitions
private val LAYOUT_KEY = stringPreferencesKey("selected_layout")
private val THEME_KEY = stringPreferencesKey("theme")
private val COLORBLIND_KEY = booleanPreferencesKey("colorblind_mode")
private val LEFT_HANDED_KEY = booleanPreferencesKey("left_handed_mode")

// Default values
selectedLayout: "efficient"
theme: "system"
colorblindMode: false
leftHandedMode: false
```

### Build Configuration

**Android App Module** (`android/app/build.gradle.kts`):
- compileSdk: 36
- minSdk: 24
- targetSdk: 36
- Dependencies: Compose, DataStore, Material3, Coroutines

**Shared Module** (`android/shared/build.gradle.kts`):
- Kotlin Multiplatform plugin
- Android library target (minSdk 24, compileSdk 36)
- iOS targets: iosX64, iosArm64, iosSimulatorArm64
- XCFramework output: "SharedKeyboard"

## Development Workflow

### Adding a New Feature

1. **Shared Logic** (if cross-platform):
   - Add to `shared/commonMain/kotlin/`
   - Write platform-agnostic code
   - Define interfaces in KeyboardContracts if platform-specific implementation needed

2. **Android Implementation**:
   - Add UI in `app/src/main/` (Compose or XML)
   - Wire up to shared logic in MyInputMethodService or relevant Activity
   - Add preferences to LayoutPreferences if persistent

3. **Testing**:
   - Unit tests for shared module
   - Android instrumentation tests for UI
   - Manual testing on physical device or emulator

### Version History

- **v0.2.1-alpha** (Current):
  - Kotlin Multiplatform shared module
  - Settings UI with DataStore
  - JoystickView touch input
  - Onboarding flow
  - ERICK logo and branding

- **v0.1.x**:
  - Initial Android IME prototype
  - Basic XML keyboard layout
  - Simple touch input

## Future Architecture Considerations

### iOS Integration
- XCFramework built from shared module
- Swift code will import `SharedKeyboard` framework
- Custom Keyboard Extension will call KMP APIs

### Physical Controller Support
- Detect gamepad connection (Android: InputManager, iOS: GCController)
- Map analog stick inputs to same state machine
- Handle button inputs for additional chords (triggers, bumpers)

### Cloud Sync
- Backend service for settings sync
- Authentication (Google, Apple Sign-In)
- Conflict resolution for multi-device users

### Analytics
- Typing speed tracking
- Common chord patterns
- Error rate analysis
- Privacy-preserving (local-first, opt-in)

## Key Files Reference

### Configuration Files
- `android/gradle/libs.versions.toml` - Dependency versions
- `android/app/build.gradle.kts` - App module build config
- `android/shared/build.gradle.kts` - KMP shared module config
- `android/settings.gradle.kts` - Module declarations

### Source Files
- `android/shared/src/commonMain/kotlin/KeyboardStateMachine.kt` - Core state logic
- `android/shared/src/commonMain/kotlin/KeyboardLogic.kt` - Chord mapping
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` - IME service
- `android/app/src/main/java/com/vatoo/erick/JoystickView.kt` - Touch input view
- `android/app/src/main/java/com/vatoo/erick/MainActivity.kt` - Onboarding UI
- `android/app/src/main/java/com/vatoo/erick/SettingsActivity.kt` - Settings UI
- `android/app/src/main/java/com/vatoo/erick/LayoutPreferences.kt` - DataStore wrapper

### Resource Files
- `android/app/src/main/res/xml/method.xml` - IME metadata
- `android/app/src/main/res/layout/keyboard_simple.xml` - Keyboard layout
- `android/app/src/main/res/drawable/erick_logo.png` - App logo
- `android/app/src/main/res/values/strings.xml` - String resources

## Troubleshooting & Common Issues

### Issue: Shared module not compiling
**Solution**: Ensure Kotlin Multiplatform plugin is installed and Android Studio is updated.

### Issue: IME not showing in system settings
**Solution**: Check AndroidManifest.xml has proper IME service declaration with intent filter.

### Issue: DataStore not persisting
**Solution**: Verify DataStore context is application context, not activity context.

### Issue: JoystickView not responding
**Solution**: Check touch event handling in onTouchEvent() and ensure view is clickable/focusable.

---

**Document Maintained By**: ERICK Development Team  
**For Questions**: See [GitHub Issues](https://github.com/vatsalunadkat/Ergonomic-Radial-Inclusive-Chorded-Keyboard/issues)
