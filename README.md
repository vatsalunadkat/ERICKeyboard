<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<div align="center">
  <h3 align="center">Ergonomic Radial Inclusive Controller Keyboard (ERICK)</h3>
  <p align="center"><strong>Version 0.4.2-alpha</strong></p>

  <p align="center">
    A cross-platform ergonomic chorded keyboard for Android &amp; iOS — type with two joystick dials using touch or a physical gaming controller, featuring word prediction, accessibility-first design, and fully offline privacy.
    <br />
    <a href="docs/documentation/APP_CONTEXT.md"><strong>📘 View Architecture & App Context »</strong></a>
    <br />
    <br />
    <a href="docs/documentation/User_Guide.md"><strong>📖 User Guide</strong></a>
    ·
    <a href="docs/documentation/Research/README.md"><strong>🔬 Research</strong></a>
    ·
    <a href="CHANGELOG.md"><strong>📋 Changelog</strong></a>
    <br />
    <br />
    <a href="https://github.com/vatsalunadkat/ERICKeyboard/issues">Report Bug</a>
    ·
    <a href="https://github.com/vatsalunadkat/ERICKeyboard/issues">Request Feature</a>
  </p>
</div>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#project-structure">Project Structure</a></li>
        <li><a href="#features">Features</a></li>
        <li><a href="#future-scope">Future Scope</a></li>
      </ul>
    </li>
    <li>
      <a href="#project-artifacts">Project Artifacts</a>
      <ul>
        <li><a href="#swipe-typing">Swipe Typing</a></li>
        <li><a href="#typing-with-controller">Typing with Controller</a></li>
        <li><a href="#keyboard-typing-with-no-fingers-vs-typing-with-controller">Keyboard Typing with No Fingers vs Typing with Controller</a></li>
        <li><a href="#architecture-overview">Architecture Overview</a></li>
      </ul>
    </li>
    <li><a href="docs/documentation/APP_CONTEXT.md">📘 Architecture & App Context (Detailed)</a></li>
    <li><a href="docs/documentation/User_Guide.md">📖 User Guide</a></li>
    <li><a href="docs/documentation/Research/README.md">🔬 Research Documentation</a></li>
    <li><a href="#contributing">Contributing</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
<div align="center">
  <img src="" height="400" />
</div>

ERICK is a cross-platform chorded keyboard that replaces dozens of tiny keys with two intuitive joystick dials. Users swipe on both dials to combine directions into character "chords" — making every letter, number, and symbol equally easy to type. It works with on-screen touch joysticks or physical gaming controllers (DualShock 4, Xbox, 8BitDo, etc.).

The keyboard is designed with accessibility at its core: large touch targets eliminate the need for fine motor skills, six colorblind-safe palettes ensure readability for all forms of color vision, left-handed mode mirrors the layout, and dyslexia-friendly fonts are built in. Characters are arranged in a logical alphabetical order (A–Z), making it intuitive for users on the autism spectrum or anyone learning the system for the first time. An efficiency layout optimized by character frequency is also available for advanced typists.

ERICK includes smart word prediction and autocorrect — a suggestion bar shows up to three completions or next-word predictions at all times, powered by a Trie-based engine with bigram sentence prediction. A live preview bar shows available characters as you hold a dial, with animated highlighting and a capsule design. All logic is shared between Android and iOS via Kotlin Multiplatform (KMP), ensuring identical behavior on both platforms.

The keyboard is 100% open-source, runs fully offline with zero internet permissions, and collects no data whatsoever — every keystroke stays on your device.

**Key use cases:**
- Accessible typing for people with motor disabilities, limited finger dexterity, or repetitive strain injuries
- Controller-based typing on gaming consoles, smart TVs, and set-top boxes
- Eyes-free typing (e.g., taking notes in class without looking at the screen)
- Privacy-preserving alternative to data-collecting commercial keyboards

### Built With

* [![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)](#) - Input Method Editor (IME) service
* [![iOS](https://img.shields.io/badge/iOS-000000?logo=ios&logoColor=white)](#) - Custom Keyboard Extension
* [![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?logo=kotlin&logoColor=white)](#) - Primary language for Android & shared logic
* [![Swift](https://img.shields.io/badge/Swift-F05138?logo=swift&logoColor=white)](#) - iOS platform implementation
* [![Kotlin Multiplatform](https://img.shields.io/badge/KMP-7F52FF?logo=kotlin&logoColor=white)](#) - Shared keyboard logic across platforms
* [![SwiftUI](https://img.shields.io/badge/SwiftUI-0D96F6?logo=swift&logoColor=white)](#) - iOS keyboard UI
* [![DataStore](https://img.shields.io/badge/DataStore-3DDC84?logo=android&logoColor=white)](#) - Android preferences management
* Deployed on [![Google Play Store](https://img.shields.io/badge/Google_Play-414141?logo=google-play&logoColor=white)](#) (Coming Soon) and [![App Store](https://img.shields.io/badge/App_Store-0D96F6?logo=app-store&logoColor=white)](#) (Coming Soon)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- PROJECT STRUCTURE -->
### Project Structure

This is a multi-platform project supporting both Android and iOS with shared business logic:

```
ERICK/
├── android/              # Android implementation
│   ├── app/             # Android app module (IME service, UI, activities)
│   ├── shared/          # Kotlin Multiplatform shared module
│   │   ├── commonMain/  # Shared keyboard logic
│   │   │   ├── KeyboardStateMachine.kt  # State machine & word buffer
│   │   │   ├── KeyboardLogic.kt         # Chord processing & layout math
│   │   │   ├── KeyboardContracts.kt     # Interfaces, enums & contracts
│   │   │   ├── WordPredictionEngine.kt  # Trie-based prediction & autocorrect
│   │   │   ├── ColorPalettes.kt         # 6 colorblind-safe palettes
│   │   │   ├── CustomLayout.kt          # Custom layout data models & manager
│   │   │   ├── CustomLayoutSerializer.kt# JSON serialization for layouts
│   │   │   ├── SettingsRepository.kt    # Cross-platform settings contracts
│   │   │   ├── Platform.kt             # Platform abstraction
│   │   │   └── KeyboardFactory.kt       # Platform factory
│   │   ├── androidMain/ # Android-specific implementations
│   │   └── iosMain/     # iOS-specific bridge code
│   ├── gradle/          # Gradle configuration
│   └── README.md        # Android setup instructions
├── ios/                 # iOS implementation
│   ├── ERICK/           # Xcode project
│   │   ├── ERICK/       # Main app target (onboarding, settings)
│   │   ├── ErickKeyBoard/ # Keyboard extension target
│   │   │   ├── KeyboardViewController.swift  # IME controller
│   │   │   ├── JoystickView.swift            # SwiftUI radial dial
│   │   │   ├── SettingsView.swift            # In-keyboard settings
│   │   │   └── ControllerBridge.swift        # App Group controller bridge
│   │   └── SharedKeyboard.xcframework/       # KMP shared module binary
│   └── README.md        # iOS setup instructions
├── docs/                # GitHub Pages website & documentation
│   ├── documentation/   # Architecture docs, sprint tickets, research
│   │   ├── APP_CONTEXT.md       # Full architecture & component docs
│   │   ├── User_Guide.md       # End-user guide
│   │   ├── Jira/               # Sprint tickets & retrospectives
│   │   └── Research/           # Optimization research & papers
│   ├── index.html       # Landing page
│   ├── accessibility.html # Accessibility features page
│   └── privacy-policy.html # Privacy policy
├── documentation/       # Research papers, logo assets, demos
├── README.md            # This file
├── CHANGELOG.md         # Version history
└── LICENSE              # Project license
```

**Architecture Highlights:**
- **Shared Module (KMP)**: Core keyboard logic (state machine, chord processing, word prediction, color palettes, contracts) shared between Android & iOS
- **Android IME**: Custom Input Method Editor service with XML layout and Canvas-based JoystickView
- **iOS Keyboard Extension**: UIInputViewController with SwiftUI views (JoystickView, PreviewBar, SuggestionBar)
- **Word Prediction**: Trie-based engine with bigram next-word prediction, autocorrect via Levenshtein edit distance
- **Physical Controller Support**: DualShock 4, Xbox, 8BitDo controllers via Android InputManager / iOS GCController
- **Settings & Preferences**: Android DataStore / iOS App Group UserDefaults

**Getting Started:**
- For Android development, see [android/README.md](android/README.md)
- For iOS development, see [ios/README.md](ios/README.md)
- For detailed architecture and component documentation, see [docs/documentation/APP_CONTEXT.md](docs/documentation/APP_CONTEXT.md)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->

### Features

**Current Implementation (v0.4.2-alpha):**
- [x] **Dual-Platform Support**: Full keyboard on both Android (IME) and iOS (Keyboard Extension)
- [x] **Chorded Input**: Two radial dials combine to form character chords (8 directions × 8 = 64 characters)
- [x] **Three Layout Modes**: Logical (A–Z alphabetical), Efficiency (frequency-optimized), Custom (user-defined)
- [x] **Custom Layout Creator**: Design, save, and switch between personalized chord layouts with color-coded UI
- [x] **Word Prediction & Autocorrect**: Trie-based engine with ~700-word dictionary, bigram next-word prediction, and spelling corrections
- [x] **Suggestion Bar**: Always-visible bar showing up to 3 word completions or next-word predictions; tapping inserts with smart spacing
- [x] **Live Preview Bar**: Animated capsule preview showing available characters when holding a dial direction
- [x] **Physical Controller Support**: DualShock 4, Xbox, 8BitDo and other Bluetooth/USB gamepads via analog sticks
- [x] **Colorblind Mode**: 6 palettes — Default, Okabe-Ito, Deuteranopia, Protanopia, Tritanopia, Pastel
- [x] **Left-Handed Mode**: Mirrors dial layout so the primary selector is under the dominant hand
- [x] **Light & Dark Mode**: Follows system preference or manual override; full theme support across UI
- [x] **Font Selection**: System, Verdana, Georgia, and OpenDyslexic (dyslexia-friendly) fonts
- [x] **Shift & Caps Lock**: Visual indicators (⇧ Shift badge, ⇧⇧ CAPS red badge) on both platforms
- [x] **Accelerating Backspace**: Hold backspace to delete characters, then whole words with increasing speed
- [x] **Collapsible Settings Menu**: Accordion-style settings with animated expandable sections on both Android (Jetpack Compose) and iOS (SwiftUI); only one section open at a time
- [x] **Kotlin Multiplatform Shared Module**: Identical keyboard logic on Android and iOS
- [x] **Guided Onboarding**: Step-by-step IME setup flow on both platforms
- [x] **Privacy Focused**: Zero data collection, no internet permissions, fully offline, 100% open-source

### Future Scope

**Planned Features:**
- [ ] Multi-language support (Spanish, French, German, Mandarin, and more)
- [ ] Mini typing game — learn chord combinations through a relaxing practice mode with curated quotes
- [ ] Complex mode with trigger/button combinations for faster typing on controllers
- [ ] Typing speed analytics and improvement tracking
- [ ] Haptic feedback options
- [ ] Cloud sync for settings and custom layouts across devices
- [ ] Tablet-optimized layout

See the [open issues](https://github.com/vatsalunadkat/ERICKeyboard/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- OTHER ARTIFACTS -->
## Project Artifacts

### Swipe Typing
<img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/main/documentation/swipe.gif" height="400" />

### Typing with Controller
<img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/main/documentation/controller.gif" height="400" />

### Keyboard Typing with No Fingers vs Typing with Controller
<img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/main/documentation/no%20hands.gif" height="400" /> vs <img src="https://github.com/vatsalunadkat/ERICKeyboard/blob/main/documentation/no%20hands%20type.gif" height="400" />

### Architecture Overview

ERICK uses **Kotlin Multiplatform (KMP)** to share all core keyboard logic between Android and iOS. The shared module contains the state machine, chord processing, word prediction engine, color palettes, layout definitions, and settings contracts — platform layers only implement the UI and OS-specific integration.

```
┌───────────────────────────────────────────────────────┐
│                   Platform Layer                      │
│  ┌────────────────────┐    ┌────────────────────────┐ │
│  │   Android (IME)    │    │   iOS (Keyboard Ext.)  │ │
│  │  JoystickView      │    │  KeyboardViewController│ │
│  │  SettingsScreen    │    │  JoystickView (SwiftUI)│ │
│  │  PreferencesManager│    │  ControllerBridge      │ │
│  └────────┬───────────┘    └────────┬───────────────┘ │
│           │      Shared Module      │                 │
│           └──────────┬──────────────┘                 │
│  ┌───────────────────┴──────────────────────────────┐ │
│  │  KeyboardStateMachine · KeyboardLogic            │ │
│  │  WordPredictionEngine · KeyboardContracts        │ │
│  │  ColorPalettes · CustomLayout · SettingsRepo     │ │
│  └──────────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────────┘
```

For detailed architecture diagrams (Mermaid class diagrams, sequence diagrams, data flow), see [APP_CONTEXT.md](docs/documentation/APP_CONTEXT.md).

---

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Make your changes and ensure they build on both platforms.
4. Commit with a descriptive message.
5. Open a Pull Request against `main`.

Please read the platform-specific setup guides before contributing:
- [Android setup](android/README.md)
- [iOS setup](ios/README.md)

---

## Documentation

| Document | Description |
|---|---|
| [APP_CONTEXT.md](docs/documentation/APP_CONTEXT.md) | Architecture, class diagrams, sequence diagrams, component docs |
| [User Guide](docs/documentation/User_Guide.md) | End-user guide: chord mechanics, settings, accessibility, troubleshooting |
| [Research](docs/documentation/Research/README.md) | Layout optimization research, 43 academic papers, optimizer scripts |
| [CHANGELOG.md](CHANGELOG.md) | Version history and release notes |
| [Sprint Retrospectives](docs/documentation/Jira/) | Jira ticket archives and sprint retrospective documents |

<!-- CONTACT -->
## Contact

Vatsal Paresh Unadkat <br />
LinkedIn: [https://www.linkedin.com/in/vatsalunadkat/](https://www.linkedin.com/in/vatsalunadkat/) <br />
Project Link: [https://github.com/vatsalunadkat/ERICKeyboard](https://github.com/vatsalunadkat/ERICKeyboard) <br />



<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS -->
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
