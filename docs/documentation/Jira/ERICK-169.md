# ERICK-169 - Haptic Pattern Customization

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | Low |
| **Story Points** | 5 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, accessibility, feedback, android, ios, settings, haptics |
| **Dependencies** | Builds on existing haptic feedback implementation from v0.7.4-beta |

---

## Objective

Allow users to configure different haptic vibration patterns and intensities for different keyboard actions, enabling users to distinguish actions by feel alone. This is especially valuable for visually impaired users and as a non-visual feedback channel.

---

## Why This Matters

- The current haptic system has only two levels: strong (utility keys) and light (letters). This is functional but doesn't distinguish between the many different actions ERICK supports.
- Users with low vision or who type without looking at the screen benefit from distinct tactile feedback for different event types.
- Different haptic patterns for errors vs. successful chords vs. suggestions provide a non-visual communication channel.
- Power users can fine-tune feedback to match their sensitivity preferences.
- Aligns with ERICK's accessibility-first mission — haptics are an inclusion feature, not just polish.

---

## Current Surfaces To Build On

### Android
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt` — current haptic feedback calls (strong/light vibration)
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt` — Feedback settings section

### iOS
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift` — current haptic feedback (UIImpactFeedbackGenerator)
- `ios/ERICK/ErickKeyBoard/SettingsView.swift`

---

## Proposed Scope

### 1. Define Haptic Action Categories

Separate feedback into distinct categories that can be independently configured:

| Category | Trigger Events | Default Pattern |
|---|---|---|
| **Letter Commit** | Chord fires and produces a character | Light tap |
| **Utility Action** | Space, Enter, Backspace, Shift, Symbols toggle | Medium tap |
| **Suggestion Accept** | Tapping a suggestion in the suggestion bar | Light double-tap |
| **Error / Invalid** | Invalid chord, no-op action, blocked word | Sharp buzz |
| **Mode Change** | Caps Lock on/off, Symbols on/off, Emoji open/close | Distinct pattern |
| **Delete (accelerating)** | Each character/word deleted during hold | Rhythmic pulse (speeds up) |

### 2. Add Intensity Controls Per Category

In settings, under the Feedback section:

- Global haptic toggle (existing) stays as master switch
- When enabled, show per-category sliders or segmented controls:
  - **Off** / **Light** / **Medium** / **Strong** for each category
- Users can disable specific categories while keeping others active

### 3. Add Distinct Haptic Patterns

Implement differentiated vibration patterns beyond just intensity:

- **Single tap**: brief single pulse (letters)
- **Double tap**: two quick pulses (suggestions, confirmations)
- **Long pulse**: sustained vibration (errors, invalid actions)
- **Rhythmic**: repeating pattern (accelerating backspace)

Platform capabilities:
- Android: `VibrationEffect.createOneShot()`, `createWaveform()`, and `createPredefined()` on API 26+; fallback to basic vibration on older APIs
- iOS: `UIImpactFeedbackGenerator` (light/medium/heavy), `UINotificationFeedbackGenerator` (success/warning/error), `UISelectionFeedbackGenerator`

### 4. Controller Rumble Alignment

- Controller rumble (where supported) should follow the same category settings
- If letter feedback is set to "Off" but utility is "Strong," controller rumble should respect that split

### 5. Settings UI

Add an expandable sub-section under "Feedback" in settings:

```
Feedback
├── Haptic Feedback: [ON/OFF]
├── Customize Haptics...
│   ├── Letters: [Off] [Light] [Medium] [Strong]
│   ├── Utility Keys: [Off] [Light] [Medium] [Strong]
│   ├── Suggestions: [Off] [Light] [Medium] [Strong]
│   ├── Errors: [Off] [Light] [Medium] [Strong]
│   ├── Mode Changes: [Off] [Light] [Medium] [Strong]
│   └── Reset to Defaults
├── Typing Sounds: [ON/OFF]
```

---

## Acceptance Criteria

- [ ] At least 5 distinct haptic categories can be independently configured
- [ ] Each category supports Off / Light / Medium / Strong intensity
- [ ] Different categories produce distinguishably different tactile patterns
- [ ] Global haptic toggle still works as a master on/off switch
- [ ] Controller rumble respects per-category settings where hardware supports it
- [ ] Settings persist across keyboard restarts
- [ ] "Reset to Defaults" restores the factory haptic configuration
- [ ] Works on Android (API 26+ with graceful fallback) and iOS
- [ ] Error/invalid chord feedback is distinguishable from successful commit feedback by feel alone

---

## Out of Scope

- Custom waveform editor (too complex for first pass)
- Audio pattern customization (separate from haptics, covered by typing sounds)
- Per-language haptic profiles
- Haptic feedback for scrolling or navigation within settings
