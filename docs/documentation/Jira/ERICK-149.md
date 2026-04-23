# ERICK-149 - Scalable Text and Dial Accessibility

| Field | Value |
|---|---|
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 13 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, accessibility, android, ios, visual-design, ergonomics |
| **Dependencies** | Build on the controller diagnostics, quickstart/practice flows, and current accessibility settings completed in ERICK-146 through ERICK-148 |

---

## Objective

Make ERICK easier to see and target for users who need larger labels, a bigger live preview, or physically larger dial geometry without breaking the keyboard height budget on Android or iOS.

---

## Why This Matters

- Some users need more than color, haptics, or font changes. They need the actual interactive geometry and label sizing to scale up.
- Small-screen devices and keyboard-extension height constraints make naive "scale everything up" solutions risky.
- The current app can teach users how ERICK works, but it still lacks a dedicated accessibility scaling strategy for users with low vision or users who need larger movement targets.

---

## Problem Framing

There are really three separate accessibility needs here, and they should not be solved with one blunt slider:

1. **Label readability**
   Users may need larger text for ring labels, preview text, suggestion chips, and host-app help/practice screens.

2. **Target size / dial geometry**
   Users may need larger radial segments or thicker rings so the dials are easier to hit accurately.

3. **Cognitive simplification**
   Some users may benefit more from emphasis and reduced visual density than from pure scaling, especially inside the fixed-height keyboard extension.

---

## Proposed Solution

### 1. Add an Accessibility Display Settings Group

Introduce a new settings group that separates sizing concerns instead of collapsing them into one global scale:

- **Keyboard Label Size**
  - Default
  - Large
  - Extra Large

- **Preview and Suggestion Size**
  - Default
  - Large
  - Extra Large

- **Dial Geometry Size**
  - Default
  - Large touch targets
  - Maximum touch targets

- **High Emphasis Preview Mode**
  - stronger preview highlight
  - larger current-character capsule
  - slightly dimmer non-selected labels

### 2. Use Presets Instead of Freeform Scaling First

Start with validated presets rather than an unrestricted percentage slider:

- easier to test across devices
- easier to document in the quickstart and user guide
- less likely to create broken layouts inside the keyboard extension

### 3. Split Host-App Scaling From Keyboard Scaling

Do not assume the host app and keyboard extension must scale the same way.

- Host app can support more aggressive text growth because it has more vertical space.
- Keyboard extension needs conservative geometry presets that respect platform height limits.
- Shared settings names should remain aligned, but implementation constraints can differ per platform.

### 4. Add a Live Preview / Calibration Surface

Extend the host app with a simple display preview so users can test:

- dial size preset
- label size preset
- preview bar size
- suggestion chip size

This should be visual-first and should not require enabling the IME to evaluate the change.

### 5. Protect the Keyboard Height Budget

For the actual IME/extension layout:

- keep a hard maximum total height
- prioritize enlarging the preview and active ring labels before enlarging every static label equally
- allow ring label reduction when the active preview is already enlarged
- consider hiding low-priority chrome before shrinking the dials

### 6. Keep the Learning Surface in Sync

Quickstart and practice screens should explain these presets clearly:

- when to use larger label size
- when to use larger dial geometry
- when to combine scaling with 6-section mode for simpler targeting

---

## Candidate Implementation Areas

### Shared / Cross-Platform Concepts

- shared setting names and preset enums
- shared documentation language
- acceptance criteria for visual scaling behavior

### Android

- `PreferencesManager.kt` for new display-scale settings
- `JoystickView.kt` and render helpers for geometry presets
- preview/suggestion bar sizing in `MyInputMethodService.kt`
- host-app preview screen in the Android app

### iOS

- App Group settings storage
- SwiftUI wheel rendering and label scaling in `JoystickView.swift`
- keyboard extension layout budget in `KeyboardContainerView.swift` and `KeyboardViewController.swift`
- host-app preview flow in `ContentView.swift` or a dedicated accessibility preview screen

---

## Validation

- Android debug build passes after adding display presets.
- Shared behavior remains stable where settings names or enums are shared.
- iOS host app and keyboard extension build after the new display presets are added.
- Manual smoke tests confirm the presets remain usable on:
  - a small phone
  - a larger phone
  - large system text / accessibility text sizes

---

## Acceptance Criteria

1. Users can increase label size without necessarily increasing dial geometry.
2. Users can increase dial target size through validated presets.
3. Preview text and suggestion text can scale independently from ring labels.
4. The keyboard extension remains within safe height constraints on both platforms.
5. The host app provides a visual way to preview these accessibility settings before typing live.
6. Quickstart/help text explains how the sizing presets interact with 6-section mode and one-handed/controller workflows.