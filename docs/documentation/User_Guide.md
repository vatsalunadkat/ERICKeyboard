# ERICK User Guide

**Version**: 1.0  
**Last Updated**: April 28, 2026

---

## What Is ERICK?

ERICK stands for **Ergonomic Radial Inclusive Controller Keyboard**.

It is a keyboard for Android and iOS that replaces rows of tiny keys with **two large directional controls** (dials). To type a letter, you move the left and right dials in two directions. That pair of directions creates a character **chord**.

ERICK is designed to make typing:

- easier to learn
- more comfortable on small screens
- more accessible for users who struggle with precise tapping
- possible with a physical game controller as well as touch

## Who Is ERICK For?

ERICK is mainly aimed at:

- people with motor limitations or reduced dexterity
- people who find normal touch keyboards tiring or frustrating
- users who want controller-based typing
- users who care about offline privacy

It can also help everyday users who want larger touch targets, controller support, or a different way to type on phones, TVs, and other screen-based devices.

**Availability:** Android is available now on [Google Play](https://play.google.com/store/apps/details?id=com.vatoo.erick). The iOS App Store release is coming soon.

---

## Getting Started

### Android

1. Install ERICK from [Google Play](https://play.google.com/store/apps/details?id=com.vatoo.erick). If you are testing a local build, you can still build from source.
2. Open **Settings → System → Languages & input → On-screen keyboard**.
3. Enable **ERICK Keyboard**.
4. Open any text field and switch to ERICK via the keyboard-switcher notification.

### iOS

1. The iOS App Store release is coming soon. If you are testing ERICK today, install the app on your device from a local Xcode build or other development distribution.
2. Open **Settings → General → Keyboard → Keyboards → Add New Keyboard**.
3. Select **ErickKeyBoard**.
4. Tap on ErickKeyBoard and enable **Allow Full Access** (required for settings persistence and controller support).
5. Switch to ERICK when typing in any app.

### Quickstart and Practice

- The host app now treats **Quickstart** and **Practice Lessons** as the main learning path, with optional help shown only when you ask for it.
- The host app now offers a **Quickstart** that teaches the two-dial model, utility swipes, assisted typing, and controller typing.
- The quickstart opens automatically only on the first launch of the host app, then stays out of the way on later launches.
- After that first launch, use **How to Type** to open **Replay Quickstart**, which appears above the **Practice Lessons** button.
- The quickstart keeps its action buttons readable on smaller screens and with larger accessibility text sizes.
- The new **Practice Lessons** hub includes drills for 8-section basics, 6-section basics, utility swipes, one-handed assisted typing, controller typing, and quote practice.
- Opening a lesson now applies the recommended keyboard preset for that lesson, including dial mode, layout, and input mode.
- Practice lessons now keep the default screen compact and move the longer explanation behind the question-mark help action.
- Each guided lesson now covers the basics in sequence: letters first, then numbers, then punctuation or symbols.
- Lesson actions now appear only when they are useful, such as when ERICK is not active yet or the lesson preset needs to be reapplied.
- Guided lessons include **Previous Part**, **Next Part**, **Previous Lesson**, and **Next Lesson** actions so you can move through the sequence without returning to the hub.
- Completed lessons are highlighted as complete in the hub and reopen with **Replay Lesson** instead of **Resume Lesson**.
- On Android, the lesson can open the keyboard picker directly. On iOS, the lesson can focus the practice field and open Settings, but you still use the globe key to switch to ERICK because iOS does not allow apps to change the active keyboard directly.
- Quote Practice remains the advanced freeform step and is launched from within the lesson flow after the guided drills.

### Using Practice Lessons

1. Open **Practice Lessons** from the host app.
2. Pick the lesson that matches the skill you want to practice.
3. Check the compact lesson header to see the current part and the preset that was applied.
4. Tap the question-mark help action if you want the full lesson explanation or success hint.
5. Use the contextual lesson actions only if you need to switch back to ERICK, focus the practice field, or reapply the preset.
6. Complete each drill in order. Guided lessons now move from letters to numbers and then to punctuation or symbols.
7. Use **Previous Part** and **Next Part** within a lesson, or **Previous Lesson** and **Next Lesson** to move across the learning path.
8. When a lesson is complete, use **Replay Lesson** to run it again or launch **Quote Practice** when the guided drills feel easy enough for freeform typing.

## What You See on Screen

When ERICK opens, you will see:

- **Two circular dials** (joysticks) side by side - left and right
- A **preview bar** above the dials showing available characters for the current direction, with the targeted character highlighted and enlarged in real time
- A **suggestion bar** displaying up to 3 word predictions

---

## How Typing Works

### Character Chords

Typing is based on a simple pattern:

1. **Swipe one dial** in one of 8 directions (N, NE, E, SE, S, SW, W, NW) to choose a character group. In 6-section mode, 6 directions are used (N, NE, SE, S, SW, NW).
2. **Swipe the other dial** in a direction to choose a specific character inside that group.
3. **Release both dials** - the character is typed.

This means every letter uses the same two-movement interaction instead of asking you to reach for different-sized keys in different places.

### Character Groups (Logical / A–Z Layout)

In the default Logical layout, characters are arranged alphabetically:

| Left Direction | Characters (selected by Right Direction: N, NE, E, SE, S, SW, W, NW) |
|---|---|
| **N** | a, b, c, d, e |
| **NE** | f, g, h, i, j |
| **E** | k, l, m, n, o |
| **SE** | p, q, r, s, t |
| **S** | u, v, w, x, y |
| **SW** | z, \\, [, ], \` |
| **W** | 1, 2, 3, 4, 5 |
| **NW** | 6, 7, 8, 9, 0 |

**Example:** To type the letter **"e"**, swipe the left dial **N** and the right dial **S** (5th position in the N group).

### Shift and Caps Lock (8-Section Default)

| Mode | Letters | Numbers/Symbols | How to Activate |
|---|---|---|---|
| **Normal** | lowercase (a–z) | digits (0–9) | Default state |
| **Shifted** | UPPERCASE (A–Z) | symbols (! @ # $ % ^ & * ( )) | Single-swipe SW |
| **Caps Lock** | UPPERCASE (A–Z) | digits (0–9) | Single-swipe NW |

- **Shift** auto-resets after one chord (type one uppercase letter, then returns to normal).
- **Caps Lock** persists until toggled off with another NW single-swipe.
- In 6-section mode, Caps Lock is not on the single-swipe wheel; see the Dial Mode section below.

### Single-Direction Actions in 8-Section Mode

Some actions use only one dial while the other stays at center:

| Direction | Function | Shifted Variant |
|---|---|---|
| **N** | Move cursor to line start | Move Home |
| **NE** | Type `,` (comma) | Type `<` |
| **E** | SPACE | SPACE |
| **SE** | Type `.` (period) | Type `>` |
| **S** | ENTER (new line) | ENTER |
| **SW** | Toggle SHIFT | Toggle SHIFT |
| **W** | BACKSPACE | BACKSPACE |
| **NW** | Toggle CAPS LOCK | Toggle CAPS LOCK |

### Accelerating Backspace

Holding the backspace direction (W in 8-section, NW in 6-section) provides progressively faster deletion:

| Hold Duration | Behavior |
|---|---|
| 0 – 300 ms | No deletion (prevents accidental trigger) |
| 300 – 1,500 ms | Delete 1 character every 100 ms |
| 1,500 – 3,000 ms | Delete 1 word every 200 ms |
| 3,000 ms + | Delete 1 word every 100 ms |

Release the dial at any point to stop deleting.

---

## Layout Options

ERICK ships with two built-in layouts and supports custom layouts.

### Logical (A–Z)

The default layout. Characters are arranged in a predictable A–Z order so the system is easier to learn and remember. Recommended for new users.

### Efficiency

A layout designed around character frequency so common English letters (e, t, a, o, i, n, s, h, r, d…) are placed on the easiest chord combinations. Intended for users who want more speed after they become comfortable with ERICK.

In 6-section mode, the built-in Efficiency preset now uses the current mixed-shortform research winner rather than the older placeholder arrangement.

### Custom

Your own saved layout. You can create and switch between custom layouts in settings.

### Switching Layouts

1. Open the ERICK app or keyboard settings.
2. Navigate to the **Layout** section.
3. Select **Logical**, **Efficiency**, or any saved custom layout.
4. The keyboard updates immediately.

---

## Dial Mode

ERICK supports two dial geometries. You can switch between them in **Settings → Dial Mode**.

### 8-Section (Default)

The standard mode with **8 directions** (N, NE, E, SE, S, SW, W, NW), each spanning **45°**. This provides 64 chord positions (8×8). All original features use this mode.

### 6-Section

An optional mode with **6 directions** (N, NE, SE, S, SW, NW — no E or W), each spanning **60°**. The wider segments can be easier to target, especially for users with reduced dexterity.

**Key differences in 6-section mode:**

| Feature | 8-Section | 6-Section |
|---|---|---|
| Directions | 8 (45° each) | 6 (60° each) |
| Chord positions | 64 | 36 |
| Normal mode characters | 26 letters + 10 digits + symbols | 26 letters + 10 digits |
| Symbols mode | Not available | N single-swipe toggles Symbols |
| Left dial rings | 3 inner rings | 2 inner rings |
| Shift toggle | SW single-swipe | NE single-swipe |
| Enter | S single-swipe | SW single-swipe |
| Backspace | W single-swipe | NW single-swipe |
| Space | E single-swipe | SE single-swipe |
| Period | SE single-swipe | S single-swipe |
| Caps Lock | NW single-swipe | Not available (use Symbols instead) |

**6-Section Single-Direction Actions:**

| Direction | Function |
|---|---|
| **N** | Toggle SYMBOLS mode |
| **NE** | Toggle SHIFT |
| **SE** | SPACE |
| **S** | Type `.` (period) |
| **SW** | ENTER |
| **NW** | BACKSPACE |

**Symbols Mode** (6-section only): When enabled, the dials show punctuation, brackets, math operators, and other special characters instead of letters. Swipe N again to return to normal typing. Shift works within Symbols mode to access Unicode currency, math, and arrow characters.

---

## Input Modes

ERICK includes three input modes. You can switch between them in **Settings → Input Mode**.

### Quick Type (Default)

The fastest mode. A character is typed as soon as **either dial is released** after forming a chord. Recommended for most users.

### Steady Type

A more deliberate mode. Both dials must **return to center** before the chord fires. This gives you time to verify your selection before committing, reducing accidental inputs. Useful for:

- users who are still learning the chord system
- situations where accuracy matters more than speed

### One-Handed

Designed for single-hand typing:

1. Swipe the left dial to a direction - it **locks** in that direction when you release.
2. Swipe the right dial - the chord is formed using the locked left direction plus the right direction.
3. Characters are typed each time you release the right dial.
4. To change the locked direction, swipe the left dial again.

This allows typing with a single thumb by locking the left direction and repeatedly swiping the right dial.

---

## Word Prediction and Autocorrect

ERICK includes a built-in prediction engine that runs entirely offline.

### How Predictions Appear

A **suggestion bar** displays up to 3 word suggestions above the dials. Suggestions update in real time as you type each character.

### Types of Suggestions

1. **Prefix completions** - Words that start with what you have typed so far (e.g., typing "hel" → "hello", "help", "helpful").
2. **Spelling corrections** - If no exact prefix match is found, the engine suggests words within 2 edit-distance of your input (catches common typos).
3. **Next-word predictions** - After completing a word (pressing space), the engine suggests likely next words based on common word pairs (e.g., after "I" → "am", "have", "was").

### Using Suggestions

- **Tap a suggestion** to insert it. The partial word is replaced with the full suggestion.
- Suggestion acceptance now adds spacing more intelligently around punctuation and next-word predictions.
- Frequently used words rise in rank over time and are stored locally on your device.
- **Default suggestions** (when starting fresh): "I", "The", "Hello".
- All predictions run fully offline with zero data collection.

---

## Custom Layout Creator

### Creating a New Layout

1. Open the ERICK app → **Settings** → **Manage Custom Layouts**.
2. Choose one of:
   - **Create Blank** - starts with the Logical layout as a template.
   - **Duplicate from Built-In** - clone either Logical or Efficiency as a starting point.
3. **Name your layout** (1–30 characters).

### Editing a Layout

The editor presents a visual 8-direction grid for each left-dial direction:

- Click any cell to assign or change a character.
- Edit both **normal** and **shifted** character mappings.
- Each direction must have exactly 8 character entries.
- No duplicate characters within the normal or shifted map.

### Saving and Sharing

Custom layouts are saved locally on the device (Android DataStore / iOS App Group UserDefaults). Layouts can be exported as JSON and shared via text copy-paste.

---

## Settings

Settings are organized into collapsible sections with short summaries so you can scan the page before opening the details.

### Start Here

- The settings screen begins with a short overview that points most users toward **Dial Mode**, **Input Mode**, and **Accessibility** first.

### Dial Mode

- Switch between **8-section** and **6-section** dial geometry.
- Use this first if you need larger directional targets.

### Layout

- Logical (A–Z), Efficiency, or any saved custom layout
- Manage Custom Layouts - open the layout editor

### Appearance

- **Theme**: System Default, Light Mode, Dark Mode
- **Font**: System Default, OpenDyslexic, Verdana, Georgia, Atkinson Hyperlegible (iOS only)

### Accessibility

- **Colorblind Mode** - toggle on to reveal color palette options:

| Palette | Best For |
|---|---|
| **Okabe-Ito** | All types of color blindness (universal) |
| **Deuteranopia** | Green-blind users |
| **Protanopia** | Red-blind users |
| **Tritanopia** | Blue-blind users |
| **Pastel** | Users who prefer softer colors |
| **Custom** | Create your own 8-color palette |

- **Left-Handed Mode** - mirrors the joystick positions

### Feedback

- **Haptic Feedback** - vibration on key input (stronger for utility keys, lighter for letters, and controller rumble on supported hardware)
- **Typing Sounds** - system click sounds when typing

### Input Mode

- Quick Type, Steady Type, or One-Handed (see [Input Modes](#input-modes))

### Controller

- **Android**: Adjust controller dead zone, invert the Y-axis if needed, and open **Controller Diagnostics** for live stick feedback. The diagnostics screen also includes a local **Confusion Drill** that records only aggregate expected-versus-resolved direction buckets for the current session, with no typed text or raw stick traces stored. When **Haptic Feedback** is enabled, supported controllers can also rumble on controller-typed input.
- **iOS**: Controller behavior follows the active system connection and the same lesson flow, but controller calibration is currently documented through the host app workflow rather than a dedicated diagnostics screen. When **Haptic Feedback** is enabled and the keyboard extension can access the controller directly, supported controllers can also rumble on controller-typed input.

### Privacy and Security

- Privacy details are kept in a separate info view so the main settings screen stays easier to scan.

---

## Accessibility Features

ERICK includes:

- **Large touch targets** - two dials instead of dozens of tiny keys
- **Left-handed mode** - mirrors the joystick layout so the character dial sits under the dominant thumb
- **Colorblind-safe palettes** - 6 palettes designed for different types of color vision, plus a custom palette editor
- **Dyslexia-friendly fonts** - OpenDyslexic (both platforms), Atkinson Hyperlegible (iOS)
- **Controller support** - physical game controller input for users who cannot use touchscreens
- **One-handed typing mode** - lock one dial and type entirely with the other
- **Haptic feedback** - optional vibration for sensory confirmation of each input
- **Visual indicators** - on-screen shift (⇧) and caps lock (⇧⇧) indicators, color-coded preview bar

These are not extra add-ons. They are part of the main design of the keyboard.

---

## Physical Controller Support

ERICK supports physical game controllers (DualShock 4, Xbox, 8BitDo, and others) as an alternative to touchscreen input.

### Setup

- **Android**: Pair your controller via Bluetooth or USB. The left and right analog sticks map directly to the on-screen dials. Controller and touch input can be used simultaneously.
- **iOS**: Pair your controller via Bluetooth. Open the ERICK host app - it will detect the controller and bridge input to the keyboard extension via the shared App Group.

### How It Works

- **Left analog stick** → left dial direction
- **Right analog stick** → right dial direction
- A dead zone of 0.25 prevents accidental input from stick drift.
- With **Haptic Feedback** enabled, controller-originated input still uses the normal key vibration and can also rumble supported controllers.
- You can switch freely between touch and controller input.

---

## Privacy

ERICK is designed to work fully offline.

- No internet permission
- No cloud typing service
- No analytics or tracking SDKs
- No keystroke collection

Your typed text stays on your device.

---

## Troubleshooting

### The keyboard does not appear

- **Android**: Verify ERICK is enabled in **Settings → System → Languages & input → On-screen keyboard**. Restart the device if it still does not appear.
- **iOS**: Ensure the keyboard is added in **Settings → General → Keyboard → Keyboards** and that **Allow Full Access** is enabled.

### The wrong characters appear

- Check which layout is active (Logical, Efficiency, or a custom layout) in Settings → Layout.
- If you are using a custom layout, confirm that its character mappings are correct.

### Suggestions are missing

- Suggestions appear while typing letters. They do not activate for symbols or numbers.
- Predictions show in the suggestion bar when both dials are at rest.

### Controller input is not working

- **Android**: Ensure the controller is paired in Bluetooth settings and recognized by the system. If it still feels wrong, open **Controller Diagnostics** to inspect live stick input, dead zone, and Y-axis inversion.
- **iOS**: The controller must be connected to the **host app** (not just the keyboard extension). Open the ERICK app and verify the controller shows as connected. The bridge transfers input to the keyboard extension automatically.

### Controller typing has no rumble

- Ensure **Haptic Feedback** is enabled in Settings → Feedback.
- **Android**: Some controllers do not expose a hardware vibrator. In that case typing still works and the phone or tablet still provides the normal ERICK haptic feedback.
- **iOS**: Controller rumble requires the keyboard extension to have direct access to the controller's GameController haptics. Bridge-only controller input still types normally, but may not expose hardware rumble.

### Backspace deletes too much

- Accelerating backspace kicks in after holding for 300 ms. For single-character deletion, perform a quick swipe-and-release in the W direction.

### One-Handed mode feels unusual

- One-Handed mode intentionally changes how chords are entered. The left dial locks a direction on release, then the right dial completes each chord.
- If this is not what you want, switch back to Quick Type or Steady Type in Settings → Input Mode.

### Settings not persisting on iOS

- Ensure **Allow Full Access** is enabled for the keyboard extension. Without it, the keyboard cannot read from shared App Group storage.

---

## Final Note

ERICK is meant to be learned over time. Most users will feel more comfortable after a short practice period, especially when starting with the Logical layout and then moving to faster layouts later if they want to.

For bugs or feature requests, open an issue on the [GitHub repository](https://github.com/vatsalunadkat/ERICKeyboard).
