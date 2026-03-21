# ERICK — Sprint 1 Tickets

**Sprint**: SCRUM Sprint 1  
**Start Date**: February 23, 2026 (Monday)  
**End Date**: February 27, 2026 (Friday)  
**Project**: ERICK 

**Sprint Goal**: MVP product delivered by Friday

---

## SCRUM-19 — Research: Keyboard and Joystick Libraries on Android

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Vatsal Unadkat |
| **Group** | Group 1 |
| **Labels** | research, android |
| **Dependencies** | None |

### Description

Find keyboard and joystick libraries on GitHub and test them on Android. The goal is to find open-source libraries that accept swipe/joystick input which can then be mapped to keyboard characters. Download and run these libraries, and test whether a particular angle can be mapped to a particular input key (e.g. input in -20 degrees to +20 degrees should type number 2).

### Deliverable

An evaluated list of candidate libraries with a working proof-of-concept or notes on how each can be adapted for the ERICK chord keyboard.

### Acceptance Criteria

- [ ] At least 2–3 open-source joystick/swipe libraries found and evaluated
- [ ] Libraries downloaded, run, and tested on an Android device
- [ ] Angle-to-key mapping tested with at least one candidate library
- [ ] Findings documented and shared with the team

---

## ERICK-20 — UI Design: App Wireframes and Screen Mockups

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Vatsal Unadkat |
| **Group** | Group 2 |
| **Labels** | design, UI |
| **Dependencies** | Group 3 + Group 2 must sync early on visual style so SCRUM-27 assets match the design |

### Description

Design in Figma or other tools what the app should look like. The keyboard (2 joysticks and extra buttons) and the game screens (if there is time). Aim to create something good enough to publish on the Google Play Store.

### Deliverable

Figma (or equivalent) design files covering the keyboard screen and at least one game screen, with a consistent visual style suitable for production.

### Acceptance Criteria

- [ ] Keyboard screen mockup created showing the 2-joystick layout
- [ ] At least one game screen designed
- [ ] Visual style guide defined (colors, typography, spacing)
- [ ] Design files committed to or linked from the repository

---

## ERICK-21 — Documentation: Architecture Diagrams and File Structure

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Assignee** | Vatsal Unadkat |
| **Group** | Group 2 |
| **Labels** | documentation |
| **Dependencies** | None |

### Description

Create and maintain project documentation including architectural diagrams that show how the system components connect, and a file structure overview for the codebase.

### Deliverable

Architecture diagram and file structure documentation committed to the repository.

### Acceptance Criteria

- [ ] Architecture diagram created showing major system components and their relationships
- [ ] File structure overview documented
- [ ] Documentation committed to the repository under `documentation/`

---

## SCRUM-22 — Jetpack Compose / XML: Keyboard Screen Implementation

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Vatsal Unadkat |
| **Group** | Group 3 |
| **Labels** | android, UI, keyboard |
| **Dependencies** | Group 3 + Group 2 must sync early on visual style so SCRUM-27 assets match the design |

### Description

From the Figma designs (SCRUM-20), create the keyboard screen in Android Studio using XML layouts or Jetpack Compose. One team member focuses on the keyboard layout. The design must be dynamic — display properly on all common screen sizes.

### Deliverable

A working keyboard screen that displays the 2-joystick ERICK keyboard layout in Android Studio.

### Acceptance Criteria

- [ ] XML or Compose screen for the keyboard layout created in Android Studio
- [ ] Layout is dynamic and adapts to common screen sizes
- [ ] Design matches Figma mockups from SCRUM-20
- [ ] Keyboard screen renders without errors in the emulator

---

## ERICK-23 — System Integration: InputMethodService Implementation

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Highest |
| **Assignee** | Vatsal Unadkat |
| **Group** | Group 4 |
| **Labels** | android, IME |
| **Dependencies** | None |

### Description

Implement the `InputMethodService` so that the Android system recognizes the ERICK app as a keyboard and can send characters to other apps such as Notepad or WhatsApp.

### Deliverable

A working Android keyboard service that the system can activate and that can inject characters into any text field in any app.

### Acceptance Criteria

- [ ] `InputMethodService` subclass implemented
- [ ] Android system recognizes ERICK as an available keyboard
- [ ] Characters can be injected into external apps (e.g. Notepad, WhatsApp)
- [ ] No crash on keyboard open or character injection

---

## ERICK-25 — System Integration: AndroidManifest Keyboard Configuration

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | High |
| **Assignee** | Vatsal Unadkat |
| **Group** | Group 4 |
| **Labels** | android, IME, infrastructure |
| **Dependencies** | SCRUM-23 |

### Description

Configure `AndroidManifest.xml` to declare the Input Method Editor (IME) service correctly, ensuring all required intent filters, metadata, and permissions are set so the keyboard is selectable by the user in Android Settings → Language & Input.

### Deliverable

A correctly configured `AndroidManifest.xml` with the full IME service declaration.

### Acceptance Criteria

- [ ] `AndroidManifest.xml` declares the IME service with correct intent filters
- [ ] Keyboard is selectable in Android Settings → Language & Input
- [ ] No manifest warnings or build errors

---

## SCRUM-34 — Research: iOS Keyboard Integration via Xcode & Swift

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Medium |
| **Assignee** | Vatsal Unadkat |
| **Group** | Group 4 |
| **Labels** | iOS, research |
| **Sprint** | Sprint 1 (added mid-sprint) |
| **Dependencies** | None |

### Description

Research how to build and pop up a custom keyboard on iOS using Xcode and Swift. This groundwork will inform the iOS keyboard extension implementation planned for Sprint 2.

### Deliverable

Research notes and/or a proof-of-concept Xcode project demonstrating how a custom iOS keyboard extension can be created and activated.

### Acceptance Criteria

- [ ] iOS keyboard extension mechanism (UIInputViewController / App Extension) researched and understood
- [ ] Key findings documented for the Sprint 2 iOS development team
- [ ] Proof-of-concept created or research notes committed to the repository

---

## SCRUM-35 — Showcases: Keyboard Usage Demonstration

| Field | Value |
|---|---|
| **Type** | Task |
| **Priority** | Low |
| **Assignee** | Vatsal Unadkat |
| **Labels** | onboarding, UX |
| **Sprint** | Sprint 1 |
| **Dependencies** | Keyboard design must be complete |

### Description

After completing the keyboard design, build showcases to demonstrate to users how to use the ERICK chord keyboard. Reference: Finger Dance as a model for interactive keyboard learning.

### Deliverable

At least one showcase or demo flow that illustrates how to use the ERICK chord keyboard input system.

### Acceptance Criteria

- [ ] At least one showcase demo created
- [ ] Demo illustrates the chord input method clearly
- [ ] Demo is suitable for first-time users
