# ERICK — User Guide

**Version**: 0.4.2-alpha  
**Last Updated**: March 22, 2026  

---

## Table of Contents

1. [Getting Started](#1-getting-started)
2. [Basic Typing — Chord Mechanics](#2-basic-typing--chord-mechanics)
3. [Utility Functions](#3-utility-functions)
4. [Layout Options](#4-layout-options)
5. [Word Prediction & Autocorrect](#5-word-prediction--autocorrect)
6. [Custom Layout Creator](#6-custom-layout-creator)
7. [Settings](#7-settings)
8. [Accessibility Features](#8-accessibility-features)
9. [Physical Controller Support](#9-physical-controller-support)
10. [Troubleshooting](#10-troubleshooting)

---

## 1. Getting Started

### What is ERICK?

ERICK (**E**rgonomic **R**adial **I**nclusive **C**horded **K**eyboard) is a novel soft keyboard for Android and iOS that replaces the traditional QWERTY layout with a two-joystick radial interface. Users enter characters by combining directional swipes on two on-screen dials — a technique called **chording**.

ERICK is designed for:
- **Ergonomic comfort** — thumbs stay near the center of the screen
- **Accessibility** — colorblind palettes, left-handed mode, dyslexia-friendly fonts
- **Customization** — create your own chord-to-character layouts

### Installing the Keyboard

#### Android
1. Install the ERICK app from the APK or build from source.
2. Open **Settings → System → Languages & input → On-screen keyboard**.
3. Enable **ERICK Keyboard**.
4. Open any text field and switch to ERICK via the keyboard-switcher notification.

#### iOS
1. Install the ERICK app on your device.
2. Open **Settings → General → Keyboard → Keyboards → Add New Keyboard**.
3. Select **ErickKeyBoard**.
4. Tap on ErickKeyBoard and enable **Allow Full Access** (required for settings persistence and controller support).
5. Switch to ERICK when typing in any app.

### First Look

When ERICK appears, you will see:
- **Two circular dials** (joysticks) side by side — left and right
- A **preview bar** above the dials showing available characters
- A **suggestion bar** displaying word predictions

---

## 2. Basic Typing — Chord Mechanics

### How Chords Work

A **chord** is a two-step directional input using both joysticks:

1. **Swipe one joystick** in one of 8 directions (N, NE, E, SE, S, SW, W, NW). This selects a **character group**.
2. **Swipe the other joystick** in a direction. This selects a **specific character** within the group.
3. **Release both joysticks** — the character is committed to the text field.

The 8 directions are spaced at 45° increments around each dial.

### Character Groups (Logical / A–Z Layout)

| Left Direction | Characters (indexed by Right Direction) |
|---|---|
| **N** | a, b, c, d, e |
| **NE** | f, g, h, i, j |
| **E** | k, l, m, n, o |
| **SE** | p, q, r, s, t |
| **S** | u, v, w, x, y |
| **SW** | z, \\, [, ], \` |
| **W** | 1, 2, 3, 4, 5 |
| **NW** | 6, 7, 8, 9, 0 |

The right joystick direction determines which character within the group is selected:
- N = 1st, NE = 2nd, E = 3rd, SE = 4th, S = 5th, SW = 6th, W = 7th, NW = 8th

**Example:** To type the letter **"e"**, swipe the left joystick **N** and the right joystick **S** (5th position in the N group).

### Preview Bar

As you hold a joystick direction, a **preview bar** appears showing the characters available for that group. The currently targeted character is highlighted and enlarged, giving real-time visual feedback before you commit.

### Shift & Caps Lock States

| Mode | Letters | Numbers/Symbols | How to Activate |
|---|---|---|---|
| **Normal** | lowercase (a–z) | digits (0–9) | Default state |
| **Shifted** | UPPERCASE (A–Z) | symbols (! @ # $ % ^ & * ( )) | Single-swipe SW |
| **Caps Lock** | UPPERCASE (A–Z) | digits (0–9) | Single-swipe NW |

- **Shift** auto-resets after one chord (type one uppercase letter, then returns to normal).
- **Caps Lock** persists until toggled off with another NW single-swipe.

---

## 3. Utility Functions

Utility functions are triggered by **single-swipe** actions — swiping only one joystick while the other remains at center.

| Direction | Function | Shifted Variant |
|---|---|---|
| **N** | Move cursor to line start (Home) | Move Home |
| **NE** | Type `,` (comma) | Type `<` |
| **E** | SPACE | SPACE |
| **SE** | Type `.` (period) | Type `>` |
| **S** | ENTER (new line) | ENTER |
| **SW** | Toggle SHIFT | Toggle SHIFT |
| **W** | BACKSPACE | BACKSPACE |
| **NW** | Toggle CAPS LOCK | Toggle CAPS LOCK |

### Accelerating Backspace

Holding the backspace direction (W) provides progressively faster deletion:

| Hold Duration | Behavior |
|---|---|
| 0 – 300 ms | No deletion (prevents accidental trigger) |
| 300 – 1,500 ms | Delete 1 character every 100 ms |
| 1,500 – 3,000 ms | Delete 1 word every 200 ms |
| 3,000 ms + | Delete 1 word every 100 ms |

Release the joystick at any point to stop deleting.

### Additional Navigation

Some navigation functions are available through extended or controller-specific inputs:

- **Move End** — Jump to end of line/input
- **Cursor Left / Right** — Move cursor by one character
- **Cursor Up / Down** — Move cursor by one line
- **Page Up / Page Down** — Large cursor jumps
- **Delete Word** — Delete the entire previous word

---

## 4. Layout Options

ERICK ships with two built-in layouts and supports unlimited custom layouts.

### Logical Layout (A–Z)

The default layout arranges letters alphabetically. This is recommended for **new users** learning the chord system, since the character positions are predictable.

### Efficiency Layout

An optimized layout that places the most frequently used English letters (e, t, a, o, i, n, s, h, r, d…) on the easiest chord combinations. Recommended for **experienced users** who want faster typing speed.

### Custom Layouts

You can create your own chord-to-character mappings. See [Section 6: Custom Layout Creator](#6-custom-layout-creator).

### Switching Layouts

1. Open the ERICK app (or keyboard settings).
2. Navigate to **Layout** section.
3. Select **Logical**, **Efficiency**, or any saved custom layout.
4. The keyboard updates immediately.

---

## 5. Word Prediction & Autocorrect

ERICK includes a built-in word prediction and autocorrect engine that works across both platforms.

### How Predictions Appear

A **suggestion bar** displays up to 3 word suggestions above the dials. Suggestions update in real-time as you type each character.

### Types of Suggestions

1. **Prefix completions** — Words that start with what you have typed so far (e.g., typing "hel" suggests "hello", "help", "helpful").
2. **Spelling corrections** — If no exact prefix match is found, the engine suggests words within 2 edit-distance of your input (catches common typos).
3. **Next-word predictions** — After completing a word (pressing space), the engine suggests likely next words based on common word pairs (e.g., after "I" → "am", "have", "was").

### Using Suggestions

- **Tap a suggestion** to insert it. The partial word you typed is replaced with the full suggestion.
- **After tapping a suggestion**, the engine automatically shows next-word predictions.
- **Default suggestions** (when starting fresh with no context): "I", "The", "Hello".

### Dictionary

The prediction engine includes approximately 700 words across frequency tiers:
- Ultra-common words (the, be, to, of, and, etc.)
- Common everyday words
- Extended vocabulary

Plus ~70 common word-pair transitions for next-word predictions.

---

## 6. Custom Layout Creator

### Creating a New Layout

1. Open the ERICK app → **Settings** → **Manage Custom Layouts**.
2. Choose one of:
   - **Create Blank** — Starts with the Logical layout as a template.
   - **Duplicate from Built-In** — Clone either Logical or Efficiency as a starting point.
3. **Name your layout** (1–30 characters).

### Editing a Layout

The editor presents a visual 8-direction grid for each left-dial direction:
- Click any cell to assign or change a character.
- Edit both **normal** and **shifted** character mappings.
- The color preview shows assigned characters in their direction colors.

### Validation

The editor enforces these rules:
- Layout name is required (1–30 characters).
- All 8 directions must have character mappings for both normal and shifted modes.
- Each direction must have exactly 8 character entries.
- No duplicate characters within the normal or shifted map.

### Saving & Persistence

Custom layouts are saved locally:
- **Android**: DataStore (persists across app restarts)
- **iOS**: App Group UserDefaults (shared between the main app and keyboard extension)

### Export & Import

Custom layouts use a JSON format and can be shared via text copy-paste. To share a layout:
1. Export generates a JSON string representing the full layout.
2. Share the JSON text with another user.
3. The recipient imports the JSON to add the layout to their device.

---

## 7. Settings

Settings are organized into collapsible accordion sections. Only one section is expanded at a time.

### Layout Section
- **Logical (A–Z)** — Alphabetically ordered character groups
- **Efficiency** — Frequency-optimized character placement
- **Custom Layouts** — User-created layouts (if any exist)
- **Manage Custom Layouts** — Open the layout creation/editing interface

### Appearance Section

**Theme:**
- System Default (follows device light/dark mode)
- Light Mode
- Dark Mode

**Font:**
- System Default
- OpenDyslexic (dyslexia-friendly typeface)
- Verdana
- Georgia
- Atkinson Hyperlegible (iOS only)

### Accessibility Section
- **Colorblind Mode** — Toggle on to reveal color palette options
- **Color Palette Selection** (when colorblind mode is on):
  - Okabe-Ito (Universal) — recommended for all types
  - Deuteranopia (Green-blind)
  - Protanopia (Red-blind)
  - Tritanopia (Blue-blind)
  - Pastel (Soft)
- **Left-Handed Mode** — Mirrors joystick positions

### Privacy & Security Section
- Privacy-related settings (data handling preferences)

---

## 8. Accessibility Features

### Colorblind Mode

ERICK uses color to distinguish the 8 radial directions. For users with color vision deficiency, 5 specialized palettes remap these colors to ensure every direction is visually distinguishable.

| Palette | Best For |
|---|---|
| **Okabe-Ito** | All types of color blindness (universal) |
| **Deuteranopia** | Green-blind users |
| **Protanopia** | Red-blind users |
| **Tritanopia** | Blue-blind users |
| **Pastel** | Users who prefer softer, low-contrast colors |

Text on colored backgrounds automatically switches between black and white for maximum readability.

### Left-Handed Mode

Enabling left-handed mode **mirrors** the joystick layout:
- The left physical joystick becomes the "action" dial (shift, backspace, space, etc.)
- The right physical joystick becomes the "letter" dial (chord character output)
- Direction mappings rotate accordingly

This ensures a comfortable experience for left-handed users without relearning the layout.

### Dyslexia-Friendly Fonts

- **OpenDyslexic** — A typeface with weighted bottoms on letters to reduce visual confusion. Available on both platforms.
- **Atkinson Hyperlegible** — Designed for maximum letter differentiation (iOS only).

### Visual Indicators

- **Shift indicator** (⇧ Shift) — Appears on-screen when shift mode is active.
- **Caps Lock indicator** (⇧⇧ CAPS) — Displayed with a red background when caps lock is engaged.
- **Preview bar** — Color-coded character previews update in real-time as you swipe.
- **Suggestion bar** — Always visible when both dials are idle.

---

## 9. Physical Controller Support

ERICK supports physical game controllers (DualShock 4, Xbox controllers, etc.) as an alternative to touchscreen joysticks.

### Setup

#### Android
1. Pair your controller via Bluetooth or connect via USB.
2. Open any text field and switch to ERICK.
3. The left and right analog sticks map directly to the left and right on-screen joysticks.
4. Controller and touch input can be used simultaneously.

#### iOS
1. Pair your controller via Bluetooth in **Settings → Bluetooth**.
2. Open the ERICK host app — it will detect the controller automatically.
3. The host app bridges controller input to the keyboard extension via a shared App Group.
4. If the keyboard extension detects a directly connected controller, it uses that instead of the bridge.

### How It Works

- **Left analog stick** → Left joystick direction
- **Right analog stick** → Right joystick direction
- Dead zone of 0.25 prevents accidental input from stick drift.
- Input is polled at 60 FPS for responsive chord detection.
- Stale controller data (>200 ms old) is automatically discarded.

### Mixed Input

You can switch freely between touch and controller input. If both are active simultaneously, touch takes priority.

---

## 10. Troubleshooting

### Keyboard does not appear

- **Android**: Verify ERICK is enabled in **Settings → System → Languages & input → On-screen keyboard**. Restart the device if it still does not appear.
- **iOS**: Ensure the keyboard is added in **Settings → General → Keyboard → Keyboards** and that **Allow Full Access** is enabled.

### Characters are wrong or unexpected

- Check which layout is active (Logical, Efficiency, or a custom layout) in Settings → Layout.
- If you recently switched to a custom layout, verify its character mappings are correct.

### Word predictions not appearing

- Ensure you are typing letters (predictions do not activate for symbols or numbers).
- Predictions appear in the suggestion bar above the dials when both joysticks are at rest.

### Controller not detected

- **Android**: Ensure the controller is paired in Bluetooth settings and recognized by the system. Try reconnecting.
- **iOS**: The controller must be connected to the **host app** (not just the keyboard extension). Open the ERICK app and verify the controller shows as connected. The bridge transfers input to the keyboard extension automatically.

### Backspace deletes too much

- Accelerating backspace kicks in after holding for 300 ms. For single-character deletion, perform a quick swipe-and-release in the W direction.

### Left-handed mode feels reversed

- Left-handed mode intentionally swaps the joystick roles. The physical **left** joystick handles actions (space, backspace, shift) and the physical **right** joystick handles letter chords. If this is not what you want, disable left-handed mode in Settings → Accessibility.

### Settings not persisting on iOS

- Ensure **Allow Full Access** is enabled for the keyboard extension. Without it, the keyboard cannot read from shared App Group storage.

---

*For bug reports or feature requests, please open an issue on the [GitHub repository](https://github.com/vatsalunadkat/ERICKeyboard).*
