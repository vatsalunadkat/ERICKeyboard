<a name="readme-top"></a>

<div align="center">
  <h3 align="center">Ergonomic Radial Inclusive Controller Keyboard (ERICK)</h3>
  <p align="center"><strong>Version 1.0</strong></p>

  <p align="center">
    An accessibility-first keyboard for Android and iOS that replaces tiny keys with two large directional controls.
    <br />
    Type with touch or a physical game controller, get offline word prediction, and keep every keystroke on your own device.
    <br />
    <br />
    <a href="APP_CONTEXT.md"><strong>View Architecture & App Context</strong></a>
    ·
    <a href="docs/documentation/User_Guide.md"><strong>User Guide</strong></a>
    ·
    <a href="docs/documentation/Research/README.md"><strong>Research</strong></a>
    ·
    <a href="AGENTS.md"><strong>AI Agent Guide</strong></a>
    ·
    <a href="CHANGELOG.md"><strong>Changelog</strong></a>
    <br />
    <br />
    <a href="https://github.com/vatsalunadkat/ERICKeyboard/issues">Report Bug</a>
    ·
    <a href="https://github.com/vatsalunadkat/ERICKeyboard/issues">Request Feature</a>
  </p>
</div>

## About The Project
<div align="center">
  <img src="docs/documentation/logo/ERICK_feature_graphic_black.png" alt="ERICK Feature Graphic" width="700" />
</div>

ERICK is an accessibility-first keyboard. Instead of asking the user to hit rows of small keys, it uses two large directional controls. Moving the left and right controls together creates a character "chord." The same system works with on-screen touch controls or with physical gaming controllers such as DualShock, Xbox, and 8BitDo pads.

ERICK is designed first for people who struggle with standard touch keyboards, especially users with motor limitations, repetitive strain issues, or situations where precise tapping is uncomfortable. At the same time, it can also help many everyday users by making typing more predictable on small screens, enabling controller-based typing on TVs and consoles, and supporting low-visual-demand typing.

The default Logical layout uses a clear A-Z arrangement to make learning easier for new users and for people who prefer a simple, easy-to-follow pattern. An Efficiency layout is also included for users who want faster typing after practice. ERICK also includes word prediction, autocorrect, live previews, left-handed mode, dyslexia-friendly fonts, colorblind-safe palettes, and custom layouts.

Everything runs fully offline. ERICK requests no internet permission, collects no typing data, and keeps every keystroke on the device.

## Why ERICK Matters

ERICK is built around the idea that accessibility should improve the product for everyone, not only for a small group of users.

- Large touch targets can help people with limited dexterity, but they also reduce mistakes on small screens.
- Controller support can help users who cannot rely on touch, but it also makes sense for couches, TVs, and gaming setups.
- Predictive text can reduce effort for users with fatigue or motor limitations, while also speeding up everyday typing.
- Offline privacy protects vulnerable users and gives privacy-conscious users a keyboard they can actually trust.

## Key Use Cases

- Accessible typing for people with motor disabilities, limited finger dexterity, or repetitive strain injuries
- Controller-based typing on gaming consoles, smart TVs, and set-top boxes
- Low-visual-demand or eyes-free typing
- Typing while commuting or multitasking
- Privacy-preserving alternative to data-collecting commercial keyboards

### Built With

[![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)](#) [![iOS](https://img.shields.io/badge/iOS-000000?logo=ios&logoColor=white)](#) [![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?logo=kotlin&logoColor=white)](#) [![Swift](https://img.shields.io/badge/Swift-F05138?logo=swift&logoColor=white)](#) [![Kotlin Multiplatform](https://img.shields.io/badge/KMP-7F52FF?logo=kotlin&logoColor=white)](#) [![SwiftUI](https://img.shields.io/badge/SwiftUI-0D96F6?logo=swift&logoColor=white)](#) [![DataStore](https://img.shields.io/badge/DataStore-3DDC84?logo=android&logoColor=white)](#)

Available now on [![Google Play Store](https://img.shields.io/badge/Google_Play-414141?logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.vatoo.erick). The iOS App Store release is coming soon.

## Availability

- Android: Install ERICK from [Google Play](https://play.google.com/store/apps/details?id=com.vatoo.erick).
- iOS: App Store release coming soon. Source builds remain available from the Xcode project under `ios/`.

## Features

### Current Implementation

- [x] Dual-platform keyboard support on Android and iOS
- [x] Chorded input with two radial dials
- [x] Optional 6-section dial mode with larger 60-degree segments and dedicated symbols layer
- [x] Three input modes: Quick Type, Steady Type, and One-Handed
- [x] Three layout modes: Logical, Efficiency, and Custom
- [x] Custom layout creator
- [x] Word prediction, autocorrect, and next-word suggestions
- [x] Live preview bar
- [x] Typing practice mini-game
- [x] Physical controller support
- [x] Seven colorblind-safe palettes plus a custom palette editor
- [x] Haptic feedback and typing sounds
- [x] Left-handed mode
- [x] Light and dark theme support
- [x] Font selection including OpenDyslexic
- [x] Shift and Caps Lock indicators
- [x] Accelerating backspace
- [x] Guided onboarding on both platforms
- [x] Source-available, offline-first privacy model

### Planned Features

- [ ] Multi-language support
- [ ] More advanced controller typing modes
- [ ] Typing analytics and improvement tracking
- [ ] Cloud sync for settings and custom layouts
- [ ] Tablet-optimized layout

## Project Structure

```text
ERICK/
├── android/                     Android implementation
│   ├── app/                     Android app module
│   ├── shared/                  Kotlin Multiplatform shared module
│   └── README.md                Android setup guide
├── ios/                         iOS implementation
│   ├── ERICK/                   Xcode project
│   └── README.md                iOS setup guide
├── docs/                        Website and documentation
│   ├── documentation/
│   │   ├── APP_CONTEXT.md      Mirrored architecture copy for docs navigation
│   │   ├── User_Guide.md
│   │   ├── Jira/
│   │   └── Research/
│   ├── index.html
│   ├── accessibility.html
│   └── privacy-policy.html
├── APP_CONTEXT.md               Canonical architecture doc
├── CHANGELOG.md
├── PROJECT_PROMPT.md
└── README.md
```

## Project Artifacts

### Touch Typing (iOS)
<img src="docs/documentation/demo%20files/v0.4.2-alpha_iOS_typing.gif" height="400" />

### Controller Typing (Android)
<img src="docs/documentation/demo%20files/v0.4.2-alpha_Android_controller_support.gif" height="400" />

### Controller Typing (iOS)
<img src="docs/documentation/demo%20files/v0.4.2-alpha_iOS_controller_support.gif" height="400" />

### Screenshots
<p>
  <img src="docs/documentation/demo%20files/v0.4.2-alpha_Android_normal.jpg" height="400" />
  <img src="docs/documentation/demo%20files/v0.4.2-alpha_Android_dark_mode_whatsapp.jpg" height="400" />
  <img src="docs/documentation/demo%20files/v0.4.2-alpha_Android_pastel_colors_dark_mode_gmail.jpg" height="400" />
  <img src="docs/documentation/demo%20files/v0.4.2-alpha_Android_dyslexia_font_reddit.jpg" height="400" />
  <img src="docs/documentation/demo%20files/v0.4.2-alpha_Android_colorblind_mode_instagram.jpg" height="400" />
</p>

## Documentation

| Document | Description |
| --- | --- |
| [AGENTS.md](AGENTS.md) | Short AI-first workflow, validation commands, and hotspot guide |
| [APP_CONTEXT.md](APP_CONTEXT.md) | Canonical architecture, class diagrams, sequence diagrams, and component docs |
| [User Guide](docs/documentation/User_Guide.md) | End-user guide for typing, settings, accessibility, and troubleshooting |
| [Research](docs/documentation/Research/README.md) | Research notes, layout optimization, and academic references |
| [CHANGELOG.md](CHANGELOG.md) | Version history and release notes |
| [Sprint Archives](docs/documentation/Jira/) | Jira ticket archives and sprint retrospectives |

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make your changes and ensure they build on both platforms.
4. Commit with a descriptive message.
5. Open a pull request against `main`.

Please read the platform-specific setup guides before contributing:

- [Android setup](android/README.md)
- [iOS setup](ios/README.md)

## License

This project is **source available**, not open source. It is free for personal, educational, and accessibility use. Commercial use and modified redistribution require written permission. See [LICENSE](LICENSE) for full terms.

## Contact

Vatsal Paresh Unadkat  
LinkedIn: [https://www.linkedin.com/in/vatsalunadkat/](https://www.linkedin.com/in/vatsalunadkat/)  
Project Link: [https://github.com/vatsalunadkat/ERICKeyboard](https://github.com/vatsalunadkat/ERICKeyboard)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
