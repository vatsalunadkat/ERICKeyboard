# ERICK — Full Project Context for AI Assistants

## 1. What Is ERICK?

**ERICK** (Ergonomic Radial Inclusive Controller Keyboard) is a cross-platform chorded keyboard app for **Android** and **iOS**. Users type by swiping dual on-screen joysticks (or using a physical game controller's analog sticks). Each letter is produced by a **chord** — a combination of a left-stick direction and a right-stick direction (8 directions each, yielding 64 chord slots). Single-stick swipes handle actions like Space, Enter, Backspace, Shift, Caps Lock, and cursor movement.

**Current version**: 0.4.2-alpha (March 21, 2026)  
**Repository**: `vatsalunadkat/ERICKeyboard` on GitHub  
**License**: Open-source  
**Privacy stance**: 100% offline, zero data collection, no internet permissions, COPPA/GDPR/CCPA compliant.

---

## 2. Core Philosophy & Goals

- **Accessibility-first**: colorblind palettes, dyslexia fonts, left-handed mode, large touch targets, controller support for motor-impaired users.
- **Privacy-by-design**: everything runs locally, no telemetry, no third-party SDKs.
- **Cross-platform parity**: Android and iOS share identical chord logic, word prediction, and accessibility features via Kotlin Multiplatform.
- **Ergonomic innovation**: research-backed layout optimization (Parallel Tempering optimizer, 44.6% improvement over random baseline).

---

## 3. Architecture

### 3.1 Kotlin Multiplatform (KMP) Shared Module

All core logic lives in `android/shared/src/commonMain/kotlin/` and compiles to:
- **Android**: JVM library (`:shared` Gradle module)
- **iOS**: `SharedKeyboard.xcframework` (arm64 device + arm64/x86_64 simulator)

**Shared classes:**

| Class | Responsibility |
|-------|---------------|
| `KeyboardStateMachine` | Chord state tracking, word buffer, suggestion orchestration, accelerating backspace (4 phases), controller dead-zone normalization, left-handed mode (dial swapping) |
| `KeyboardLogic` | 8-way direction detection via `atan2`, chord → character resolution across 3 layouts (Logical, Efficiency, Custom), single-swipe action mapping |
| `WordPredictionEngine` | Trie-based dictionary (~700 words, 4 frequency tiers), Levenshtein autocorrect, ~70 bigram pairs for next-word prediction, default suggestions ("I", "The", "Hello") |
| `KeyboardContracts` | `Direction` enum (NONE + 8 compass), `KeyboardMode` (NORMAL/SHIFTED/CAPS_LOCKED), `LayoutType` (LOGICAL/EFFICIENCY/CUSTOM), `InputAction` enum, `KeyboardActionDelegate` interface |
| `ColorPalettes` | 6 accessibility palettes: Default (Rainbow), Okabe-Ito, Deuteranopia, Protanopia, Tritanopia, Pastel. W3C luminance-based contrast text color. |
| `CustomLayout` / `CustomLayoutManager` | Data class for user layouts (chord maps + single-swipe maps), CRUD + validation, built-in layout cloning, platform `CustomLayoutStorage` interface |
| `KeyboardFactory` | iOS-specific factory (exposes Kotlin default arguments to Swift/ObjC) |

### 3.2 Android Platform Layer

| File | Role |
|------|------|
| `MyInputMethodService.kt` (~500 lines) | Core IME service: implements `KeyboardActionDelegate`, manages `KeyboardStateMachine`, touch event dispatch, preview bar rendering, 3-suggestion strip, physical controller polling, theme/font application |
| `JoystickView.kt` | Custom `View`: canvas-drawn radial dial with 8 color-coded sectors, 3 concentric rings, animated return-to-center, left-handed mode support |
| `MainActivity.kt` | Onboarding: IME enablement flow with status checks |
| `SettingsActivity.kt` / `SettingsScreen.kt` | Jetpack Compose: accordion-style collapsible sections (Layout, Appearance, Accessibility, Privacy), layout editor, theme/font/palette pickers |
| `PreferencesManager.kt` | Jetpack DataStore: type-safe async preference storage |

**Build stack**: compileSdk 36, minSdk 24, targetSdk 36, Kotlin 2.0.21, AGP 8.13.2, Jetpack Compose (BOM 2024.09.00)

### 3.3 iOS Platform Layer

**Host App** (`ios/ERICK/ERICK/`):

| File | Role |
|------|------|
| `ContentView.swift` | Onboarding: keyboard enablement flow, privacy card, controller status, test typing field |
| `ControllerBridge.swift` | Polls `GCController` at 60 FPS via DisplayLink, writes normalized stick data to App Group `UserDefaults` (bridge needed because keyboard extensions run in a separate process) |
| `SettingsView.swift` | Host app Form-based settings |
| `ERICKApp.swift` | SwiftUI `@main` entry point |
| `IOSCustomLayoutStorage.swift` | Custom layout persistence via App Group |

**Keyboard Extension** (`ios/ERICK/ErickKeyBoard/`):

| File | Role |
|------|------|
| `KeyboardViewController.swift` (~900 lines) | `UIInputViewController`: hosts SwiftUI via `UIHostingController`, integrates `KeyboardStateMachine`, handles touch + controller input (direct `GCController` + App Group bridge polling), implements `KeyboardActionDelegate` |
| `JoystickView.swift` (~600 lines) | SwiftUI: left wheel (8 sectors × 3 rings showing characters) + right wheel (8 color-coded sectors with action icons), drag gesture, spring-back animation, custom palette/font support |
| `SettingsView.swift` | In-keyboard settings overlay: collapsible card sections matching Android design, layout/theme/font/palette toggles, custom layout editor |
| `IOSCustomLayoutStorage.swift` | Extension-side custom layout CRUD via App Group |

**iOS requirements**: Xcode 15+, iOS 15+, macOS

### 3.4 Simplified Architecture Diagram

```
┌──────────────────────────────────────────────────────────┐
│              Platform UI Layer (Android / iOS)            │
│                                                          │
│  IME Service / UIInputViewController                     │
│  JoystickView (radial touch input)                       │
│  SettingsScreen / SettingsView (accordion sections)       │
│  PreferencesManager / App Group UserDefaults              │
│  Physical Controller Polling                             │
└─────────────────────┬────────────────────────────────────┘
                      │ implements KeyboardActionDelegate
┌─────────────────────▼────────────────────────────────────┐
│        Shared Kotlin Multiplatform Module                 │
│                                                          │
│  KeyboardStateMachine  ←→  KeyboardLogic                 │
│  WordPredictionEngine  ←→  KeyboardContracts             │
│  ColorPalettes              CustomLayout                 │
└──────────────────────────────────────────────────────────┘
```

---

## 4. How Chord Input Works

1. User swipes the **left joystick** in a direction (N, NE, E, SE, S, SW, W, NW).
2. User swipes the **right joystick** in a direction.
3. The combination (left direction + right direction) maps to a character via `KeyboardLogic.getChordResult()`.
4. If only **one** joystick is swiped, it triggers a single-swipe action (Space, Enter, Backspace, Shift, etc.).
5. The `KeyboardStateMachine` manages the word buffer and feeds partial words to the `WordPredictionEngine` for live suggestions.

### Layout Types

- **Logical (A–Z)**: Alphabetical ordering across the 64 chord slots. Easy to learn.
- **Efficiency**: Frequency-optimized — `e`, `t`, `a`, `o`, `i`, `n`, `s`, `h`, `r`, `d` on the easiest same-direction chords. Researched via Parallel Tempering optimization.
- **Custom**: User-defined chord maps with full editor UI.

---

## 5. Key Features

| Feature | Details |
|---------|---------|
| **Dual-joystick chorded input** | 64 chord combinations for letters, single-swipe for actions |
| **Physical controller support** | DualShock 4, Xbox, 8BitDo, etc. via Bluetooth. Left/right analog sticks map to left/right dials |
| **Word prediction** | Trie + bigrams, 3 suggestion strip, tap to accept |
| **Autocorrect** | Levenshtein edit-distance matching |
| **Accelerating backspace** | 4 phases: initial delay → char delete → word delete → fast word delete |
| **3 keyboard layouts** | Logical, Efficiency, Custom (user-editable) |
| **6 colorblind palettes** | Default, Okabe-Ito, Deuteranopia, Protanopia, Tritanopia, Pastel |
| **Dyslexia fonts** | OpenDyslexic, Atkinson Hyperlegible |
| **Left-handed mode** | Swaps left/right dial functions |
| **Light & dark themes** | System-aware + manual toggle |
| **Fully offline** | No internet, no data collection |
| **Live preview** | Highlighted, color-coded characters as you swipe |
| **Collapsible settings** | Accordion UI: Layout, Appearance, Accessibility, Privacy |

---

## 6. Repository Structure

```
ERICKeyboard/
├── APP_CONTEXT.md              # Detailed architecture doc (Mermaid diagrams, class hierarchies)
├── CHANGELOG.md                # Version history
├── README.md                   # Project overview
├── PROJECT_PROMPT.md           # This file — AI context prompt
│
├── android/
│   ├── app/
│   │   └── src/main/
│   │       ├── java/.../       # MyInputMethodService, JoystickView, MainActivity,
│   │       │                   # SettingsActivity, SettingsScreen, SettingsViewModel,
│   │       │                   # PreferencesManager, LayoutPreferences
│   │       └── res/            # XML layouts, drawables, strings
│   ├── shared/
│   │   └── src/
│   │       ├── commonMain/     # KeyboardStateMachine, KeyboardLogic, WordPredictionEngine,
│   │       │                   # KeyboardContracts, ColorPalettes, CustomLayout
│   │       ├── androidMain/    # Android-specific implementations
│   │       └── iosMain/        # iOS-specific implementations
│   ├── build.gradle.kts        # Top-level Gradle config
│   ├── app/build.gradle.kts    # App module (Compose, DataStore)
│   ├── shared/build.gradle.kts # KMP shared module config
│   └── gradle/libs.versions.toml  # Dependency catalog
│
├── ios/
│   └── ERICK/
│       ├── ERICK/              # Host app (ContentView, ControllerBridge, Settings, etc.)
│       ├── ErickKeyBoard/      # Keyboard extension (KeyboardViewController, JoystickView, Settings)
│       ├── ERICK.xcodeproj/    # Xcode project
│       └── SharedKeyboard.xcframework/  # Compiled KMP framework
│
├── docs/                       # GitHub Pages website
│   ├── index.html              # Landing page
│   ├── accessibility.html      # Accessibility features showcase
│   ├── privacy-policy.html     # Privacy policy
│   ├── css/style.css           # Pastel color palette, responsive design
│   ├── js/main.js              # Theme toggle, font controls, scroll animations
│   └── documentation/
│       ├── APP_CONTEXT.md      # Copy of architecture doc
│       ├── User_Guide.md       # End-user guide
│       ├── Jira/               # Sprint tickets, retrospectives, burndown charts
│       ├── Research/           # Layout optimization research, academic references
│       └── logo/               # Branding assets
│
└── website/                    # Alternate website copy (mirrors docs/)
```

---

## 7. Tech Stack

| Layer | Technology |
|-------|-----------|
| Shared logic | Kotlin Multiplatform (Kotlin 2.0.21) |
| Android UI | Jetpack Compose, Canvas-based custom views, Material Design 3 |
| Android IME | `InputMethodService`, DataStore, CoroutineScope |
| Android build | Gradle 8.13.2, AGP, compileSdk 36, minSdk 24 |
| iOS UI | SwiftUI, UIHostingController |
| iOS keyboard | `UIInputViewController`, GCController, App Group |
| iOS build | Xcode 15+, iOS 15+ |
| Website | Vanilla HTML/CSS/JS, GitHub Pages |
| Research | Python (Parallel Tempering optimizer), React (layout visualizer) |

---

## 8. Version History (Condensed)

| Version | Date | Highlights |
|---------|------|-----------|
| **v0.4.2-alpha** | Mar 21, 2026 | Word prediction, controller support, collapsible settings, accelerating backspace |
| **v0.3.2-alpha** | Mar 14, 2026 | Radial dial UI redesign, efficiency layout, iOS radial UI, live character preview |
| **v0.2.1-alpha** | Mar 7, 2026 | KMP architecture, Material 3 settings, onboarding flow, ERICK branding |
| **v0.1.7-alpha** | Jul 8, 2022 | Initial Android-only prototype with controller detection |

### Planned

| Version | Features |
|---------|----------|
| **v0.5.0** | Multi-language support, typing practice game |
| **v1.0.0** | Production stability, App Store / Play Store main release, typing analytics |

---

## 9. Sprint History (Agile Context)

The project has been developed in 2-week-ish sprints with a team that varied from 2–8 people:

- **Sprint 1** (Feb 23–27, 2026): Project setup, initial task breakdown. 11 tasks, 100% velocity. Team of 8 in 4 groups.
- **Sprint 2** (Mar 2–6, 2026): KMP setup, full iOS implementation, shared logic wiring. 95 SP, 100% velocity.
- **Sprint 3** (Mar 9–13, 2026): Radial UI overhaul, efficiency layout, colorblind palettes. 67 SP, 100% velocity.
- **Sprint 4** (Mar 16–20, 2026): Controller support, word prediction, settings polish, documentation. 29/32 SP (91%). 2-person team.
- **Sprint 5** (Mar 21–22, 2026): Word prediction refinement, comprehensive documentation.

---

## 10. Research Background

The chord layout design was optimized using a **Parallel Tempering** algorithm:
- **44.6% improvement** over random baseline (0.862 final cost vs 1.557 random).
- Most common letters (`e`, `t`, `a`, `o`, `i`, `n`, `s`, `h`, `r`, `d`) placed on the easiest same-direction chords.
- **43 academic papers** referenced covering chorded keyboard design, ergonomics, accessibility, and OrbiTouch.
- Research includes corpus frequency analysis, layout visualization tools, and a React interactive visualizer.

---

## 11. Key Interfaces & Contracts

### `KeyboardActionDelegate` (platform must implement)
```kotlin
interface KeyboardActionDelegate {
    fun commitText(text: String)
    fun sendInputAction(action: InputAction)
    fun onModeChanged(mode: KeyboardMode)
    fun onSuggestionsUpdated(suggestions: List<String>)
    fun getCurrentWordPrefix(): String
}
```

### Direction Enum
```kotlin
enum class Direction { NONE, N, NE, E, SE, S, SW, W, NW }
```

### Input Actions
```kotlin
enum class InputAction {
    SPACE, ENTER, BACKSPACE, DELETE_WORD,
    TOGGLE_SHIFT, TOGGLE_CAPS,
    CURSOR_LEFT, CURSOR_RIGHT, CURSOR_HOME, CURSOR_END
}
```

---

## 12. How to Work with This Codebase

### Building Android
1. Open `android/` in Android Studio.
2. Sync Gradle. The `:shared` module auto-compiles for JVM.
3. Build and run the `:app` module on a device/emulator (API 24+).
4. Enable ERICK in Settings → System → Keyboard → Manage keyboards.

### Building iOS
1. Open `ios/ERICK/ERICK.xcodeproj` in Xcode.
2. The `SharedKeyboard.xcframework` is pre-built and committed.
3. To update it: build the `:shared` KMP module and copy the framework.
4. Build and run on a device/simulator (iOS 15+).
5. Enable in Settings → General → Keyboard → Keyboards → Add New Keyboard → ERICK.

### Editing Shared Logic
- All shared code is in `android/shared/src/commonMain/kotlin/`.
- Changes auto-apply to Android. For iOS, rebuild the xcframework.
- Tests: `android/shared/src/commonTest/`.

### Website
- Static files in `docs/` served via GitHub Pages.
- Edit HTML/CSS/JS directly; no build step.

---

## 13. Common Tasks & Where to Look

| Task | Files to Edit |
|------|--------------|
| Add a new chord mapping | `KeyboardLogic.kt` (chord maps) |
| Change word dictionary | `WordPredictionEngine.kt` (dictionary + bigrams) |
| Add a new color palette | `ColorPalettes.kt` |
| Modify keyboard UI (Android) | `JoystickView.kt`, `MyInputMethodService.kt` |
| Modify keyboard UI (iOS) | `JoystickView.swift`, `KeyboardViewController.swift` |
| Change settings options | `SettingsScreen.kt` (Android), `SettingsView.swift` (iOS) |
| Add a new InputAction | `KeyboardContracts.kt`, then handle in both platform delegates |
| Update onboarding | `MainActivity.kt` (Android), `ContentView.swift` (iOS) |
| Edit website | `docs/index.html`, `docs/css/style.css`, `docs/js/main.js` |
| Build/deploy Android | `android/app/build.gradle.kts` |
| Update iOS framework | Build `:shared` KMP → copy xcframework |

---

## 14. Design Decisions & Conventions

- **Accordion settings**: Only one section expanded at a time on both platforms.
- **Controller bridge (iOS)**: Keyboard extensions can't access controllers directly; the host app polls and writes to App Group `UserDefaults`.
- **Left-handed mode**: Swaps which dial is "left" vs "right" at the `KeyboardStateMachine` level, not the UI level.
- **Accelerating backspace**: 4-phase timing managed in `KeyboardStateMachine` with coroutine delays.
- **Color palettes**: Each maps the 8 directions to 8 colors. Text color is computed via W3C relative luminance formula.
- **KMP factory**: `KeyboardFactory.createEngine()` exists because Kotlin default arguments aren't visible to Swift/ObjC.
- **App Group ID (iOS)**: Used for preference sync between host app and keyboard extension.

---

## 15. How to Use This Prompt

Paste this document (or attach it) at the start of a conversation with any AI assistant. Then you can:

- **Brainstorm**: "What features should we add for v0.5.0?"
- **Debug**: "The accelerating backspace stops working after switching layouts — where should I look?"
- **Code**: "Add a new 'High Contrast' color palette to ColorPalettes.kt"
- **Plan**: "Help me write Sprint 6 tickets for multi-language support"
- **Document**: "Update the User Guide to cover the new word prediction feature"
- **Research**: "What academic papers support using Fitts' Law for joystick target sizing?"

The AI will have full context of the architecture, file locations, tech stack, and design conventions.
