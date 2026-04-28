# ERICK-153 - Settings Setup Wizard And Recommended Presets

| Field | Value |
|---|---|
| **Status** | Backlog |
| **Type** | Story |
| **Priority** | High |
| **Story Points** | 8 |
| **Assignee** | Vatsal Unadkat |
| **Sprint** | Backlog |
| **Parent Epic** | ERICK-42 Keyboard UI & Visual Design |
| **Labels** | feature, onboarding, settings, android, ios, accessibility, research-followup |
| **Dependencies** | Build on ERICK-149 accessibility settings, current settings storage on Android and iOS, and the ERICK-151 Branch 3 and Branch 8 findings about persona bundles and setup guidance |

---

## Objective

Add a rerunnable setup wizard inside Settings that asks a small number of practical questions and applies a recommended ERICK preset bundle automatically.

---

## Why This Ticket Exists

- The current settings surface is flexible, but it assumes the user already understands how dial mode, input mode, handedness, and accessibility settings fit together.
- ERICK-151 Branch 3 identified repeatable persona bundles such as touch-precision-first, low-vision, one-handed, and controller-first users.
- ERICK-151 Branch 8 ranked setup guidance ahead of more layout experimentation because expectation-setting is still a stronger source of early product friction.

---

## Current Surfaces To Build On

### Android
- `android/app/src/main/java/com/vatoo/erick/MainSettingsContent.kt`
- `android/app/src/main/java/com/vatoo/erick/PreferencesManager.kt`
- `android/app/src/main/java/com/vatoo/erick/MyInputMethodService.kt`

### iOS
- `ios/ERICK/ERICK/SettingsView.swift`
- `ios/ERICK/ErickKeyBoard/SettingsView.swift`
- `ios/ERICK/ErickKeyBoard/KeyboardViewController.swift`

---

## Proposed Scope

### 1. Add A Visible Setup Wizard Entry Point

Add a `Setup Wizard` button inside the settings menu on both platforms. The wizard must be re-runnable at any time and should not replace the existing manual controls.

### 2. Ask A Small, Practical Question Set

Keep the wizard short and product-focused. Candidate questions:

- Do you mainly type with touch, a controller, or both?
- Do you want larger targets or the full 8-direction layout?
- Are you typing one-handed or two-handed most of the time?
- Do you want the fastest immediate typing path or a steadier confirmation path?
- Do you need stronger accessibility defaults such as left-handed mode, larger emphasis, or a colorblind-safe palette?

### 3. Translate Answers Into A Preset Bundle

The wizard should apply a recommended combination of existing settings, not invent a second configuration system.

Expected settings touched by the wizard:

- `sixSectionDial`
- `inputMode`
- `leftHandedMode`
- accessibility palette defaults where appropriate
- controller defaults where appropriate

### 4. Show A Summary Before Applying Changes

Before the wizard commits changes, show a short summary such as:

- `6-section + Quick Type + left-handed mode`
- `8-section + Assisted mode + controller-first tuning`

The user should explicitly confirm before the new bundle is written.

### 5. Preserve Manual Overrides

After applying the wizard result, users must still be free to tweak individual settings manually. The wizard is a starting point, not a lock-in path.

---

## Guardrails

- No network account, telemetry, or cloud sync work is required here.
- Do not silently change settings outside the wizard flow.
- Do not hide or remove the existing manual settings panels.

---

## Acceptance Criteria

- [ ] A `Setup Wizard` entry point exists inside Settings on Android and iOS
- [ ] The wizard asks a small focused set of questions instead of dumping the full settings list again
- [ ] The wizard maps answers onto existing persisted settings rather than creating duplicate state
- [ ] The user sees a summary before changes are applied
- [ ] The resulting settings are written to platform storage and picked up by the keyboard normally
- [ ] The wizard can be re-run later from Settings
- [ ] Manual settings remain editable after the wizard runs
- [ ] The initial preset bundles cover at least touch-first, one-handed, controller-first, and accessibility-oriented users
